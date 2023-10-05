// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

internal fun String.capitalizeAsciiOnly(): String {
    if (isEmpty()) return this
    val c = this[0]
    return if (c in 'a'..'z') {
        c.uppercaseChar() + substring(1)
    } else {
        this
    }
}

internal fun lowerCamelCaseName(vararg nameParts: String?): String {
    val nonEmptyParts = nameParts.mapNotNull { it?.takeIf(String::isNotEmpty) }
    return nonEmptyParts.drop(1).joinToString(
        separator = "",
        prefix = nonEmptyParts.firstOrNull().orEmpty(),
        transform = String::capitalizeAsciiOnly
    )
}
