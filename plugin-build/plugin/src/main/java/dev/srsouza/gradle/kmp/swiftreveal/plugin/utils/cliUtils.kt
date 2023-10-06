// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import java.io.File

internal object MacUtils {
    val xcodeSelect: File by lazy {
        File("/usr/bin/xcode-select")
    }

    val swift: File by lazy {
        File("/usr/bin/swift")
    }
}

internal object UnixUtils {
    val git: File by lazy {
        File("/usr/bin/git")
    }
}
