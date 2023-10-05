// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

internal inline fun <reified T : Any> ObjectFactory.notNullProperty(): Property<T> =
    property(T::class.java)

@SuppressWarnings("UNCHECKED_CAST")
internal inline fun <reified T : Any> ObjectFactory.nullableProperty(): Property<T?> =
    property(T::class.java) as Property<T?>

internal fun Provider<String?>.forUseAtConfigurationTimeSafe(): Provider<String?> =
    try {
        forUseAtConfigurationTime()
    } catch (e: NoSuchMethodError) {
        // todo: remove once we drop support for Gradle 6.4
        this
    }

internal fun ProviderFactory.valueOrNull(prop: String): Provider<String?> =
    provider {
        gradleProperty(prop).forUseAtConfigurationTimeSafe().orNull
    }

internal fun Provider<String?>.toBooleanProvider(defaultValue: Boolean): Provider<Boolean> =
    orElse(defaultValue.toString()).map { "true" == it }
