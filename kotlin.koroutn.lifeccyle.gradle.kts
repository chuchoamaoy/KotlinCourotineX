import java.io.File

val isVibeModeOn = true

fun runGit(vararg args: String): Pair<Int, String> {
    return try {
        val process = ProcessBuilder(listOf("git", *args))
            .directory(rootDir) // luon chay o repo root
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val code = process.waitFor()
        Pair(code, output.trim())
    } catch (e: Exception) {
        Pair(-1, e.message ?: "system error")
    }
}

if (isVibeModeOn) {
    val autoGitTask = tasks.register("autoGitCommitAndPush") {
        outputs.upToDateWhen { false } // chong task cache
        doLast {
            println("\n=========================================")
            println("[VIBE MODE] Git Auto bat dau...")

            val (statusExit, _) = runGit("status", "--porcelain")
            if (statusExit != 0) {
                println("[VIBE MODE] Khong phai git repo hoac loi git. Dung.")
                return@doLast
            }

            val (_, branch) = runGit("rev-parse", "--abbrev-ref", "HEAD")
            println("[VIBE MODE] Branch: $branch")

            runGit("add", "-A")

            val stamp = java.time.LocalDateTime.now().toString()
            val (commitExit, commitOut) = runGit("commit", "-m", "vibe: auto commit $stamp")
            if (commitExit != 0 && commitOut.contains("nothing to commit")) {
                println("[VIBE MODE] Khong co gi de commit.")
            } else {
                println("[VIBE MODE] Commit:\n$commitOut")
            }

            // Push; neu bi non-fast-forward thi rebase roi push lai
            val (pushExit, pushOut) = runGit("push", "origin", branch)
            when {
                pushExit == 0 -> println("[VIBE MODE] PUSH OK -> origin/$branch")
                pushOut.contains("non-fast-forward") ||
                    pushOut.contains("rejected") ||
                    pushOut.contains("fetch first") -> {
                    println("[VIBE MODE] Remote di truoc, dang rebase...")
                    val (rb, rbOut) = runGit("pull", "--rebase", "origin", branch)
                    if (rb != 0) {
                        runGit("rebase", "--abort")
                        println("[VIBE MODE] Co conflict, can xu ly tay. Bo qua push lan nay:\n$rbOut")
                        println("=========================================\n")
                        return@doLast
                    }
                    val (p2, p2Out) = runGit("push", "origin", branch)
                    println(if (p2 == 0) "[VIBE MODE] PUSH OK sau rebase" else "[VIBE MODE] Van loi:\n$p2Out")
                }
                else -> println("[VIBE MODE] PUSH loi:\n$pushOut")
            }
            println("=========================================\n")
        }
    }

    tasks.configureEach {
        if (name == "assembleDebug") {
            finalizedBy(autoGitTask)
        }
    }
}
