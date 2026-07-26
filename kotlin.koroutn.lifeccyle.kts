import java.io.ByteArrayOutputStream

val isVibeModeOn = true

if (isVibeModeOn) {
    tasks.register("autoGitCommitAndPush") {
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
                    println("❌ [VIBE MODE] PUSH LỖI! Hãy đọc nguyên nhân bên dưới:\n$pushOut")
                }
            } catch (e: Exception) {
                println("❌ [VIBE MODE] Lỗi hệ thống Gradle: ${e.message}")
            }
            println("=========================================\n")
        }
    }

    tasks.whenTaskAdded {
        if (name == "assembleDebug") {
            println("🔥 [VIBE MODE] Đã móc nối thành công vào task: assembleDebug")
            finalizedBy("autoGitCommitAndPush")
        }
    }
}
