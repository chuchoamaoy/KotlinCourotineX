val isVibeModeOn = true

if (isVibeModeOn) {
    tasks.register("autoGitCommitAndPush") {
        doLast {
            try {
                val checkGit = exec {
                    isIgnoreExitValue = true
                    commandLine("git", "status")
                }
                
                if (checkGit.exitValue != 0) return@doLast

                exec { commandLine("git", "add", ".") }
                exec {
                    isIgnoreExitValue = true
                    commandLine("git", "commit", "-m", "Auto update / fix bugs")
                }
                exec { 
                    isIgnoreExitValue = true
                    commandLine("git", "push") 
                }
            } catch (e: Exception) {
            }
        }
    }

    tasks.whenTaskAdded {
        if (name == "assembleDebug") {
            finalizedBy("autoGitCommitAndPush")
        }
    }
}
