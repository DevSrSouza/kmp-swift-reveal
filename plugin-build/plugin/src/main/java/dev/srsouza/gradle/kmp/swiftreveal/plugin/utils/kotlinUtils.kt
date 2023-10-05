// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinUsages
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

internal val KonanTarget.enabledOnCurrentHost
    get() = HostManager().isEnabled(this)

internal fun Project.registerRevealLibsDependencies(target: KonanTarget, deps: Set<Any>): String {
    val librariesConfigurationName = "linkLibraryIosSwiftReveal"
    configurations.maybeCreate(librariesConfigurationName).apply {
        isVisible = false
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = true
        attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
        attributes.attribute(KotlinNativeTarget.konanTargetAttribute, target.name)
        attributes.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, KotlinUsages.KOTLIN_API))
    }
    deps.forEach { dependencies.add(librariesConfigurationName, it) }
    return librariesConfigurationName
}

internal fun Project.registerRevealExportDependencies(target: KonanTarget, deps: Set<Any>): String {
    val exportConfigurationName = "linkExportIosSwiftReveal"
    configurations.maybeCreate(exportConfigurationName).apply {
        isVisible = false
        isCanBeConsumed = false
        isCanBeResolved = true
        isTransitive = false
        attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
        attributes.attribute(KotlinNativeTarget.konanTargetAttribute, target.name)
        attributes.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, KotlinUsages.KOTLIN_API))
    }
    deps.forEach { dependencies.add(exportConfigurationName, it) }
    return exportConfigurationName
}
