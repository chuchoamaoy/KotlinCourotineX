import java.io.ByteArrayOutputStream

val isVibeModeOn = true

if (isVibeModeOn) {
    val autoGitTask = tasks.register("autoGitCommitAndPush") {
        outputs.upToDateWhen { false } // Chống task cache
        
        doLast {
            println("\n=========================================")
            println("🔥 [VIBE MODE] Bắt đầu chạy Git Auto...")
            try {
                // 1. Kiểm tra Git (Thêm project. trước exec)
                val statusOut = ByteArrayOutputStream()
                val checkGit = project.exec {
                    isIgnoreExitValue = true
                    commandLine("git", "status")
                    standardOutput = statusOut
                    errorOutput = statusOut
                }
                
                if (checkGit.exitValue != 0) {
                    println("⚠️ [VIBE MODE] Dừng! Thư mục chưa có Git hoặc lỗi:\n$statusOut")
                    return@doLast
                }

                // 2. Chạy Git Add (Thêm project. trước exec)
                project.exec { commandLine("git", "add", ".") }
                println("✅ [VIBE MODE] Đã Add toàn bộ file.")

                // 3. Chạy Git Commit (Thêm project. trước exec)
                val commitOut = ByteArrayOutputStream()
                project.exec {
                    isIgnoreExitValue = true
                    commandLine("git", "commit", "-m", "Auto update / fix bugs")
                    standardOutput = commitOut
                    errorOutput = commitOut
                }
                println("✅ [VIBE MODE] Trạng thái Commit:\n$commitOut")

                // 4. Chạy Git Push (Thêm project. trước exec)
                val pushOut = ByteArrayOutputStream()
                val pushResult = project.exec { 
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
                println("❌ [VIBE MODE] Lỗi hệ thống Gradle: ${e.message}")
            }
            println("=========================================\n")
        }
    }

    // Móc nối an toàn
    tasks.configureEach {
        if (name == "assembleDebug") {
            finalizedBy(autoGitTask)
            println("🔥 [VIBE MODE] Đã móc nối thành công vào: $name")
        }
    }
}
