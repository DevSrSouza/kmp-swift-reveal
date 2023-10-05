package dev.srsouza.gradle.kmp.swiftreveal.plugin

import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.moduleGenerationOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.notNullProperty
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class SwiftRevealExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project
) : ExtensionAware {
    /**
     * Where to output the `module.swift` file that represents the
     * kotlin ios source output as a swift module.
     *
     * By default: "build/kmp-swift-reveal/out/module/"
     */
    val directoryForSwiftGeneratedSourceFromModule: Property<Directory> = objects.directoryProperty().apply {
        convention(project.layout.moduleGenerationOutputDir())
    }
}
