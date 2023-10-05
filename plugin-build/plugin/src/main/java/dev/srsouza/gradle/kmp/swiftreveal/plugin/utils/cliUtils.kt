// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import java.io.File

internal object MacUtils {
    val xcodeSelect: File by lazy {
        File("/usr/bin/xcode-select")
    }
}
