package dev.srsouza.gradle.kmp.swiftreveal.plugin

import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.toBooleanProvider
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.valueOrNull
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

internal object SwiftRevealProperties {
    private const val VERBOSE = "kmpSwiftReveal.verbose"

    fun isVerbose(providers: ProviderFactory): Provider<Boolean> =
        providers.valueOrNull(VERBOSE).toBooleanProvider(false)
}
