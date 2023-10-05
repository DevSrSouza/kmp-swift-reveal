plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("dev.srsouza.gradle.kmp-swift-reveal")
}

kotlin {

    listOf(
        ios(),
        iosSimulatorArm64()
    ).forEach {
//        it.binaries.framework {
//            baseName = "Example"
//        }
    }

    sourceSets {
        val iosMain by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

swiftReveal {
    directoryForSwiftGeneratedSourceFromModule.set(layout.projectDirectory.dir("swift-reveal"))
}
