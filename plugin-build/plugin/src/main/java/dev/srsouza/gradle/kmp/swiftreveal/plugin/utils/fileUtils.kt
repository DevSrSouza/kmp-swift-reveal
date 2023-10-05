// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import dev.srsouza.gradle.kmp.swiftreveal.plugin.PLUGIN_BUILD_DIR
import org.gradle.api.file.ProjectLayout

internal fun ProjectLayout.frameworkOutputDir() = buildDirectory.dir("$PLUGIN_BUILD_DIR/framework")

internal fun ProjectLayout.logOutputDir(taskName: String) = buildDirectory.dir("$PLUGIN_BUILD_DIR/logs/$taskName")

internal fun ProjectLayout.defaultGenerationOutputDir() = buildDirectory.dir("$PLUGIN_BUILD_DIR/out")

internal fun ProjectLayout.moduleGenerationOutputDir() = defaultGenerationOutputDir().map { it.dir("module") }
internal fun ProjectLayout.binaryGenerationOutputDir() = defaultGenerationOutputDir().map { it.dir("binary") }
