import java.io.ByteArrayOutputStream

val isVibeModeOn = true

if (isVibeModeOn) {
    val autoGitTask = tasks.register("autoGitCommitAndPush") {
        // Lệnh này cực kỳ quan trọng: Ép task luôn chạy, không bao giờ bị Cache (UP-TO-DATE) chặn lại
        outputs.upToDateWhen { false }
        
        doLast {
            println("\n=========================================")
            println("🔥 [VIBE MODE] Bắt đầu chạy Git Auto...")
            try {
                // 1. Kiểm tra Git
                val statusOut = ByteArrayOutputStream()
                val checkGit = exec {
                    isIgnoreExitValue = true
                    commandLine("git", "status")
                    standardOutput = statusOut
                    errorOutput = statusOut
                }
                
                if (checkGit.exitValue != 0) {
                    println("⚠️ [VIBE MODE] Dừng! Thư mục chưa có Git hoặc lỗi:\n$statusOut")
                    return@doLast
                }

                // 2. Chạy Git Add
                exec { commandLine("git", "add", ".") }
                println("✅ [VIBE MODE] Đã Add toàn bộ file.")

                // 3. Chạy Git Commit
                val commitOut = ByteArrayOutputStream()
                exec {
                    isIgnoreExitValue = true
                    commandLine("git", "commit", "-m", "Auto update / fix bugs")
                    standardOutput = commitOut
                    errorOutput = commitOut
                }
                println("✅ [VIBE MODE] Trạng thái Commit:\n$commitOut")

                // 4. Chạy Git Push
                val pushOut = ByteArrayOutputStream()
                val pushResult = exec { 
                    isIgnoreExitValue = true
                    commandLine("git", "push") 
                    standardOutput = pushOut
                    errorOutput = pushOut
                }
                
                if (pushResult.exitValue == 0) {
                    println("🚀 [VIBE MODE] PUSH THÀNH CÔNG LÊN GITHUB!")
                } else {
                    println("❌ [VIBE MODE] PUSH LỖI! Nguyên nhân:\n$pushOut")
                }
            } catch (e: Exception) {
                println("❌ [VIBE MODE] Lỗi Gradle: ${e.message}")
            }
            println("=========================================\n")
        }
    }

    // Đợi project khởi tạo xong hết mới móc nối để không bị trượt task
    project.afterEvaluate {
        try {
            tasks.named("assembleDebug") {
                finalizedBy(autoGitTask)
            }
            println("🔥 [VIBE MODE] Đã móc nối THÀNH CÔNG vào assembleDebug!")
        } catch (e: Exception) {
            println("⚠️ [VIBE MODE] Không tìm thấy assembleDebug để móc nối.")
        }
    }
}
