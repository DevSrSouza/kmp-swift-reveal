// ktlint-disable filename
package dev.srsouza.gradle.kmp.swiftreveal.plugin.utils

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal inline fun <reified T : Task> Project.registerTask(
    name: String,
    args: List<Any> = emptyList(),
    noinline body: ((T) -> (Unit))? = null
): TaskProvider<T> =
    this@registerTask.registerTask(name, T::class.java, args, body)

@Suppress("SpreadOperator")
internal fun <T : Task> Project.registerTask(
    name: String,
    type: Class<T>,
    constructorArgs: List<Any> = emptyList(),
    body: ((T) -> (Unit))? = null
): TaskProvider<T> {
    val resultProvider = project.tasks.register(name, type, *constructorArgs.toTypedArray())
    if (body != null) {
        resultProvider.configure(body)
    }
    return resultProvider
}
