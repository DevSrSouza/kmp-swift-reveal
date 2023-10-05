package dev.srsouza.gradle.kmp.swiftreveal.plugin.tasks

import dev.srsouza.gradle.kmp.swiftreveal.plugin.SwiftRevealProperties
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.ExternalToolRunner
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.MacUtils
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.defaultGenerationOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.frameworkOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.logOutputDir
import dev.srsouza.gradle.kmp.swiftreveal.plugin.utils.notNullProperty
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.LocalState
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import javax.inject.Inject

internal const val LIFECYLE_SWIFTREVIEAL_TASK_NAME = "swiftReveal"
internal const val FILE_SWIFT_MODULE = "module.swift"

internal val revealFrameworkTarget = KonanTarget.IOS_ARM64

abstract class SwiftRevealTask @Inject constructor(
    private val objectFactory: ObjectFactory,
    private val execOperations: ExecOperations,
    private val projectLayout: ProjectLayout,
    private val providers: ProviderFactory
) : DefaultTask() {
    @get:OutputDirectory
    abstract val destinationDir: DirectoryProperty

    @get:InputDirectory
    abstract val frameworkFolder: DirectoryProperty

    @get:Input
    abstract val frameworkFileName: Property<String>

    @get:Input
    abstract val sourceKittenExecutablePath: Property<String>

    @get:Input
    abstract val outputModuleSwiftRepresentationFile: Property<File>

    @get:Internal
    val outputSourceKittenRequestFile: Provider<File> = project.provider {
        val outFileName = "source-kitten-request.yml"
        destinationDir.asFile.get().resolve(outFileName)
    }

    @get:Internal
    val outputSourceKittenResultFile: Provider<File> = project.provider {
        val outFileName = "result.json"
        destinationDir.asFile.get().resolve(outFileName)
    }

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

    init {
        destinationDir.convention(projectLayout.defaultGenerationOutputDir())
        frameworkFolder.convention(projectLayout.frameworkOutputDir())
        frameworkFileName.convention(project.defaultFrameworkBaseNameForModule())
        outputModuleSwiftRepresentationFile.convention(
            project.provider {
                val outFileName = FILE_SWIFT_MODULE
                destinationDir.asFile.get().resolve(outFileName)
            }
        )
    }

    @TaskAction
    fun generateSwiftRepresentation() {
        val sourceKittenRequestFile = outputSourceKittenRequestFile.get().apply { ensureParentDirsCreated() }
        val sourceKittenResultFile = outputSourceKittenResultFile.get().apply { ensureParentDirsCreated() }
        val moduleSwiftRepresentationFile = outputModuleSwiftRepresentationFile.get().apply { ensureParentDirsCreated() }

        val frameworkFolder = frameworkFolder.get()

        val frameworkAbsolutePath = frameworkFolder.asFile.absolutePath
        val xcodePath = retrieveXcodePath(runExternalTool)
        val frameworkName = frameworkFileName.get()

        // Generating Source Kitten Request File
        val sourceKittenRequestSource = buildSourceKittenRequestFileSource(xcodePath, frameworkAbsolutePath, frameworkName)
        sourceKittenRequestFile.writeText(sourceKittenRequestSource)

        // Requesting Source Kitten
        requestSourceKitten(sourceKittenExecutablePath, runExternalTool, sourceKittenRequestFile, sourceKittenResultFile)

        // getting and writing Swift Source
        val swiftModuleSource = retrieveModuleSwiftSource(sourceKittenResultFile)
        moduleSwiftRepresentationFile.writeText(swiftModuleSource)
    }
}

fun buildSourceKittenRequestFileSource(xcodePath: String, absoluteFrameworkDir: String, frameworkName: String): String {
    return """
        key.request: source.request.editor.open.interface
        key.name: "5F63C5B8-6D92-44FF-8012-DCA7D787D243"
        key.compilerargs:
            - "-target"
            - "arm64-apple-ios12.0"
            - "-sdk"
            - "$xcodePath/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk"
            - "-I"
            - "$xcodePath/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk/usr/include"
            - "-F"
            - "$xcodePath/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk/System/Library/Frameworks"
            - "-F"
            - "$absoluteFrameworkDir"
            - ""

        key.modulename: "$frameworkName"
        key.toolchains:
            - "com.apple.dt.toolchain.XcodeDefault"
        key.synthesizedextensions: 1
    """.trimIndent()
}

internal fun requestSourceKitten(
    sourceKittenExecutablePath: Property<String>,
    runExternalTool: ExternalToolRunner,
    requestFile: File,
    outputJsonFile: File
) {
    val executable = sourceKittenExecutablePath.get()
    runExternalTool(
        File(executable), // TODO: inject the installed source kitten
        listOf("request", "--yaml", requestFile.absolutePath),
        processStdout = { result ->
            outputJsonFile.writeText(result)
        }
    )
}

internal fun retrieveXcodePath(runExternalTool: ExternalToolRunner): String {
    var xcodePath = "/Applications/Xcode.app/Contents/Developer"
    runExternalTool(
        MacUtils.xcodeSelect,
        listOf("--print-path"),
        processStdout = { xcodePathRetrieved ->
            val path = xcodePathRetrieved.lines().firstOrNull()?.takeIf { it.isNotBlank() }
            if (path != null) {
                xcodePath = path
            }
        }
    )
    return xcodePath
}

internal fun retrieveModuleSwiftSource(requestResultJson: File): String {
    val jsonRaw = requestResultJson.readText()
    val json = Json.Default.decodeFromString<JsonObject>(jsonRaw)

    val swiftSourceCode = json["key.sourcetext"]?.jsonPrimitive?.content

    val swiftModuleDeclarations = json["key.substructure"]?.jsonArray
        ?.filter { substructure ->
            val swiftDeclarationName = substructure.jsonObject.get("key.name")?.jsonPrimitive?.content

            // TODO: make filter customizable
            // filter swift class that starts with 'Kotlin'
            swiftDeclarationName?.startsWith("Kotlin")?.not() == true &&
                swiftDeclarationName?.startsWith("NS")?.not() == true // TODO: make it clean
        }
        ?.map { substructure ->
            val start = substructure.jsonObject.get("key.offset")?.jsonPrimitive?.int!!
            val length = substructure.jsonObject.get("key.length")?.jsonPrimitive?.int!!

            val source = swiftSourceCode?.substring(start, start + length)
            source
        }

    val sourceCodeFiltered = swiftModuleDeclarations?.joinToString("\n\n") ?: ""

    return sourceCodeFiltered
}
