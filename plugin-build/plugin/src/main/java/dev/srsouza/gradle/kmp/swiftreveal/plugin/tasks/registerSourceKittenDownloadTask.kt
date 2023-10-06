// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks

import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.MacUtils
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.UnixUtils
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.registerTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

internal val Project.sourceKittenExecutable
    get() = sourceKittenSrc.map { it.file(".build/arm64-apple-macosx/debug/sourcekitten") }

private const val SOURCE_KITTEN_GIT = "https://github.com/jpsim/SourceKitten.git"
private const val SOURCE_KITTEN_TAG = "0.34.1"
private val Project.sourceKittenSrc get() = rootProject.layout.buildDirectory.dir("sourcekitten-$SOURCE_KITTEN_TAG-src")

internal fun Project.registerDownloadAndBuildSourceKitten(): TaskProvider<AbstractSwiftRevealTask> {
    return registerTask<AbstractSwiftRevealTask>("downloadAndBuildSourceKitten") { task ->
        task.onlyIf { !sourceKittenExecutable.get().asFile.exists() }
        task.doLast {
            val sourceKittenSrcFile = sourceKittenSrc.get().asFile
            sourceKittenSrcFile.deleteRecursively()
            task.runExternalTool(
                UnixUtils.git,
                listOf(
                    "clone",
                    "--depth",
                    "1",
                    "--branch",
                    SOURCE_KITTEN_TAG,
                    SOURCE_KITTEN_GIT,
                    sourceKittenSrcFile.absolutePath
                )
            )
            task.runExternalTool(
                MacUtils.swift,
                listOf("build"),
                workingDir = sourceKittenSrcFile
            )
        }
    }
}
