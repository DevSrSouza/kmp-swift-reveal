package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FilterOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class MultiOutputStream(
    mainStream: OutputStream,
    private val secondaryStream: OutputStream
) : FilterOutputStream(mainStream) {

    override fun write(b: Int) {
        super.write(b)
        secondaryStream.write(b)
    }

    override fun flush() {
        super.flush()
        secondaryStream.flush()
    }

    override fun close() {
        try {
            super.close()
        } finally {
            secondaryStream.close()
        }
    }
}

internal fun OutputStream.alsoOutputTo(secondaryStream: OutputStream): OutputStream =
    MultiOutputStream(this, secondaryStream)

internal class ExternalToolRunner(
    private val verbose: Property<Boolean>,
    private val logsDir: Provider<Directory>,
    private val execOperations: ExecOperations
) {
    enum class LogToConsole {
        Always,
        Never,
        OnlyWhenVerbose
    }

    operator fun invoke(
        tool: File,
        args: Collection<String>,
        environment: Map<String, Any> = emptyMap(),
        workingDir: File? = null,
        checkExitCodeIsNormal: Boolean = true,
        processStdout: Function1<String, Unit>? = null,
        logToConsole: LogToConsole = LogToConsole.OnlyWhenVerbose,
        stdinStr: String? = null
    ): ExecResult {
        val logsDir = logsDir.get().asFile
        logsDir.mkdirs()

        val toolName = tool.nameWithoutExtension
        val outFile = logsDir.resolve("$toolName-${currentTimeStamp()}-out.txt")
        val errFile = logsDir.resolve("$toolName-${currentTimeStamp()}-err.txt")

        val result = outFile.outputStream().buffered().use { outFileStream ->
            errFile.outputStream().buffered().use { errFileStream ->
                execOperations.exec { spec ->
                    spec.executable = tool.absolutePath
                    spec.args(*args.toTypedArray())
                    workingDir?.let { wd -> spec.workingDir(wd) }
                    spec.environment(environment)
                    // check exit value later
                    spec.isIgnoreExitValue = true

                    if (stdinStr != null) {
                        spec.standardInput = ByteArrayInputStream(stdinStr.toByteArray())
                    }

                    @Suppress("NAME_SHADOWING")
                    val logToConsole = when (logToConsole) {
                        LogToConsole.Always -> true
                        LogToConsole.Never -> false
                        LogToConsole.OnlyWhenVerbose -> verbose.get()
                    }
                    if (logToConsole) {
                        spec.standardOutput = spec.standardOutput.alsoOutputTo(outFileStream)
                        spec.errorOutput = spec.errorOutput.alsoOutputTo(errFileStream)
                    } else {
                        spec.standardOutput = outFileStream
                        spec.errorOutput = errFileStream
                    }
                }
            }
        }

        if (checkExitCodeIsNormal && result.exitValue != 0) {
            val errMsg = buildString {
                appendLine("External tool execution failed:")
                val cmd = (listOf(tool.absolutePath) + args).joinToString(", ")
                appendLine("* Command: [$cmd]")
                appendLine("* Working dir: [${workingDir?.absolutePath.orEmpty()}]")
                appendLine("* Exit code: ${result.exitValue}")
                appendLine("* Standard output log: ${outFile.absolutePath}")
                appendLine("* Error log: ${errFile.absolutePath}")
            }

            error(errMsg)
        }

        if (processStdout != null) {
            processStdout(outFile.readText())
        }

        if (result.exitValue == 0) {
            outFile.delete()
            errFile.delete()
        }

        return result
    }

    private fun currentTimeStamp() =
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
}
