package dev.srsouza.gradle.kmp.swiftreveal.plugin

import dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks.registerSwiftRevealForModule
import dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks.registerSwiftRevealLifecycle
import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "swiftReveal"
internal const val PLUGIN_BUILD_DIR = "kmp-swift-reveal"

abstract class SwiftRevealPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, SwiftRevealExtension::class.java, project)

        // Add a task that uses configuration from the extension object
        val lifecycle = project.registerSwiftRevealLifecycle()
        val swiftRevealModule = project.registerSwiftRevealForModule(
            outputModuleSwiftFileDirectory = extension.directoryForSwiftGeneratedSourceFromModule,
            sourceKittenExecutablePath = extension.sourceKittenExecutablePath
        )

        lifecycle.configure {
            it.dependsOn(swiftRevealModule.name)
        }
    }
}
