
import java.io.File

val isVibeModeOn = true

if (isVibeModeOn) {
    val autoGitTask = tasks.register("autoGitCommitAndPush") {
        outputs.upToDateWhen { false } // Chống task cache
        
        doLast {
            println("\n=========================================")
            println("🔥 [VIBE MODE] Bắt đầu chạy Git Auto...")
            
            // Hàm chạy lệnh Terminal bằng Kotlin thuần (Cam kết 100% không bị lỗi Gradle)
            fun runCommand(vararg command: String): Pair<Int, String> {
                return try {
                    val process = ProcessBuilder(*command)
                        .redirectErrorStream(true)
                        .start()
                    val output = process.inputStream.bufferedReader().readText()
                    val exitCode = process.waitFor()
                    Pair(exitCode, output)
                } catch (e: Exception) {
                    Pair(-1, e.message ?: "Lỗi hệ thống")
                }
            }

            // 1. Kiểm tra Git
            val (statusExit, statusOut) = runCommand("git", "status")
            if (statusExit != 0) {
                println("⚠️ [VIBE MODE] Dừng! Thư mục chưa có Git hoặc lỗi:\n$statusOut")
                return@doLast
            }

            // 2. Chạy Git Add
            runCommand("git", "add", ".")
            println("✅ [VIBE MODE] Đã Add toàn bộ file.")

            // 3. Chạy Git Commit
            val (commitExit, commitOut) = runCommand("git", "commit", "-m", "hâhhaha")
            println("✅ [VIBE MODE] Trạng thái Commit:\n$commitOut")

            // 4. Chạy Git Push
            val (pushExit, pushOut) = runCommand("git", "push")
            if (pushExit == 0) {
                println("🚀 [VIBE MODE] PUSH THÀNH CÔNG LÊN GITHUB!")
            } else {
                println("❌ [VIBE MODE] PUSH LỖI! Nguyên nhân:\n$pushOut")
            }
            
            println("=========================================\n")
        }
    }

    // Móc nối vào assembleDebug
    tasks.configureEach {
        if (name == "assembleDebug") {
            finalizedBy(autoGitTask)
            println("🔥 [VIBE MODE] Đã móc nối thành công vào: $name")
        }
    }
}
