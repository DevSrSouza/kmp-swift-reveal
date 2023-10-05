plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("dev.srsouza.gradle.kmp-swift-reveal")
}

kotlin {

    targetHierarchy.default()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
//        it.binaries.framework {
//            baseName = "Example"
//        }
    }

    sourceSets {
        targetHierarchy.default()
    }
}

swiftReveal {
    directoryForSwiftGeneratedSourceFromModule.set(layout.projectDirectory.dir("swift-reveal"))
}
