// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks

import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.enabledOnCurrentHost
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.frameworkOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.moduleGenerationOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.registerRevealExportDependencies
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.registerRevealLibsDependencies
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.registerTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind
import org.jetbrains.kotlin.gradle.targets.native.tasks.artifact.KotlinNativeLinkArtifactTask

// TODO: what we want are two tasks -> `swiftRevealForModule`, creates a link.
//  `swiftRevealForBinary` that uses the link framework of this module if there is any
//  for binary comes with all configurations in the `framework {}` block, etc

private const val LINK_MODULE_SWIFT_REVEAL_TASK_NAME = "linkIosForModuleSwiftReveal"
private const val MODULE_SWIFT_REVEAL_TASK_NAME = "swiftRevealForModule"

internal fun Project.defaultFrameworkBaseNameForModule(): String {
    return name
}

internal fun Project.registerKotlinLinkTaskForSwiftRevealForModule(): TaskProvider<KotlinNativeLinkArtifactTask> {
    val librariesConfigurationName = registerRevealLibsDependencies(
        revealFrameworkTarget,
        setOf(dependencies.project(mapOf("path" to path)))
    )
    val exportLibrariesConfigurationName = registerRevealExportDependencies(
        revealFrameworkTarget,
        setOf(dependencies.project(mapOf("path" to path)))
    )

    val kind = NativeOutputKind.FRAMEWORK
    return project.registerTask<KotlinNativeLinkArtifactTask>(
        LINK_MODULE_SWIFT_REVEAL_TASK_NAME,
        listOf(revealFrameworkTarget, kind.compilerOutputKind)
    ) { task ->
        task.description = "Assemble a iOS Framework for this module for kmp-swift-reveal"
        task.enabled = revealFrameworkTarget.enabledOnCurrentHost
        task.baseName.set(defaultFrameworkBaseNameForModule())
        task.destinationDir.set(layout.frameworkOutputDir())
        task.optimized.set(false)
        task.debuggable.set(false)
        task.linkerOptions.set(emptyList())
        task.binaryOptions.set(emptyMap())
        task.staticFramework.set(false)
        task.embedBitcode.set(BitcodeEmbeddingMode.DISABLE)
        task.libraries.setFrom(project.configurations.getByName(librariesConfigurationName))
        task.exportLibraries.setFrom(project.configurations.getByName(exportLibrariesConfigurationName))
        // task.kotlinOptions(kotlinOptionsFn) <- TODO
    }
}

internal fun Project.registerSwiftRevealForModule(
    outputModuleSwiftFileDirectory: Property<Directory>
): TaskProvider<SwiftRevealTask> {
    val linkTask = registerKotlinLinkTaskForSwiftRevealForModule()
    return registerTask<SwiftRevealTask>(MODULE_SWIFT_REVEAL_TASK_NAME) { task ->
        task.description = "Generate a Swift file revealing the Kotlin Module"
        task.enabled = revealFrameworkTarget.enabledOnCurrentHost
        task.destinationDir.set(layout.moduleGenerationOutputDir())
        task.outputModuleSwiftRepresentationFile.set(
            outputModuleSwiftFileDirectory.map { it.file("module.swift").asFile }
        )

        task.dependsOn(linkTask.name)
    }
}

internal fun Project.registerSwiftRevealLifecycle(): TaskProvider<Task> {
    return tasks.register(LIFECYLE_SWIFTREVIEAL_TASK_NAME)
}
