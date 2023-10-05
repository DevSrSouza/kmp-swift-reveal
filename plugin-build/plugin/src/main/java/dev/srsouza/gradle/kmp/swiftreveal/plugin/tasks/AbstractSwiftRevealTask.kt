package dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks

import dev.srsouza.gradle.kmp.swiftreveal.plugin.SwiftRevealProperties
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.ExternalToolRunner
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.logOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.notNullProperty
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.LocalState
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class AbstractSwiftRevealTask @Inject constructor(
    private val objectFactory: ObjectFactory,
    private val execOperations: ExecOperations,
    private val projectLayout: ProjectLayout,
    private val providers: ProviderFactory
) : DefaultTask() {
    @get:LocalState
    protected val logsDir: Provider<Directory> = projectLayout.logOutputDir(name)
    
    @get:Internal
    val verbose: Property<Boolean> = objectFactory.notNullProperty<Boolean>().apply {
        set(
            providers.provider {
                logger.isDebugEnabled || SwiftRevealProperties.isVerbose(providers).get()
            }
        )
    }
    
    @get:Internal
    internal val runExternalTool: ExternalToolRunner
        get() = ExternalToolRunner(verbose, logsDir, execOperations)
}