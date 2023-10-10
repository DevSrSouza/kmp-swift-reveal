# kmp-swift-reveal 🐘

Gradle plugin that reveal the Swift code that you should expect from a Kotlin iOS module.

:arrow_right: `gradlew swiftReveal`

```kotlin
import kotlin.experimental.ExperimentalObjCName

class ExampleClass {
    fun mySuperFun(): Int = 10
    fun withCallback(myCallback: () -> String) {}
    fun myFunctionReturnMap(): Map<String, String> = mapOf("Example" to "Value")
    
    @OptIn(ExperimentalObjCName::class)
    @ObjCName(swiftName = "mySwiftNameFunction")
    fun withObjcName() {}
}

fun ExampleClass.myExtensionFunction() {}

fun myTopLevelFunction(): String = "Example return string"
```

Generated Swift representation
```swift
class ExampleClass : KotlinBase {
    public init()
    
    open func myFunctionReturnMap() -> [String : String]

    open func mySuperFun() -> Int32

    open func withCallback(myCallback: @escaping () -> String)

    open func mySwiftNameFunction()
}

extension ExampleClass {
    open func myExtensionFunction()
}

class ExampleClassKt : KotlinBase {
    open class func myTopLevelFunction() -> String
}
```

## Motivation

First motivation: When writing a Kotlin Multiplatform module targeting iOS is usually hard to know what to expect when going to Swift, with the time we can get a little better at this but we usually miss things.

Second motivation: When working with iOS developers it usually much harder for then to predict what is the output of a Kotlin Multiplatform module. This tool can be hooked into the CI pipeline for example to show ahead of time what the iOS developer should expect.

This project aims to fill this unknown gap before going to XCode and implement using new Kotlin Multiplatform code by generating a Swift representation of the output OBJC Header of your module.

## How to use

- Add the plugin to your module build script
```kotlin
plugins {
    ...
    id("dev.srsouza.gradle.kmp-swift-reveal") version "TODO"
}
```

- Call: `gradlew :yourModule:swiftReveal`

- The file should be located by default in `build/kmp-swift-reveal/out/module/module.swift`

## Configuring
```kotlin
swiftReveal {
    directoryForSwiftGeneratedSourceFromModule.set(layout.projectDirectory.dir("swift-reveal")) // default: build/kmp-swift-reveal/out/module/
}
```

## Integration example
You can do a bunch of custom integration with the generated file to make it visible to the iOS Developers the changes
that are being taking place in the modules that expose code to Swift.

A simple **CI** example would be this Github Action Jobs

```yaml
  # Put the Swift generated file to the `GITHUB_OUTPUT` of the Step
  - name: Get Swift Reveal Output
    id: build_swift_reveal_comment
    run: |
      FILE_CONTENT=$(cat example/swift-reveal/module.swift)
      delimiter="$(openssl rand -hex 8)"
      echo "content<<${delimiter}" >> "${GITHUB_OUTPUT}"
      echo "$FILE_CONTENT" >> "${GITHUB_OUTPUT}"
      echo "${delimiter}" >> "${GITHUB_OUTPUT}"
    if: success()
  # Comment the Swift file to the Github PR using 'maintain-one-comment' Action
  - name: Swift Reveal Comment
    uses: actions-cool/maintain-one-comment@v3
    if: success()
    with:
      token: ${{ secrets.GITHUB_TOKEN }}
      body: |
        ## Swift Reveal result
        ```swift
        ${{ steps.build_swift_reveal_comment.outputs.content }}
        ```
      body-include: '<!-- Swift Reveal Comment -->'
```

<image width="700" src="https://github.com/DevSrSouza/kmp-swift-reveal/assets/29736164/2e5ed7ca-249b-4dc3-90d4-be1ce404bff6" />

You can get the full example Github Action Workflow [here](https://github.com/DevSrSouza/kmp-swift-reveal/blob/main/.github/workflows/swift-reveal.yaml).

## Roadmap
- [ ] Support generate swift file from `binaries` configuration of the module.
- [ ] More examples
- [X] CI usage examples
- [ ] Filter classes and definitions from module dependencies.
- [ ] Validate and support [SKIE](https://github.com/touchlab/SKIE)

## Thanks

- @cortinico for the amazing [kotlin-gradle-plugin-template](https://github.com/cortinico/kotlin-gradle-plugin-template)
- @SalomonBrys for the [Kotlin Conf talk](https://www.youtube.com/watch?v=j-zEAMcMcjA) that inspired this project
- Source kitten contributors
- Kotlin Gradle Plugin and Compose Multiplatform developers for a bunch of Gradle Extensions.