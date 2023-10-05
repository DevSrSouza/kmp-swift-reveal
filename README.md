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

## Roadmap
- [ ] Download Source Kitten tool and build if not available.
- [ ] Support generate swift file from `binaries` configuration of the module.
- [ ] More examples
- [ ] CI usage examples

## Thanks

- @cortinico for the amazing [kotlin-gradle-plugin-template](https://github.com/cortinico/kotlin-gradle-plugin-template)
- @SalomonBrys for the [Kotlin Conf talk](https://www.youtube.com/watch?v=j-zEAMcMcjA) that inspired this project
- Source kitten contributors
- Kotlin Gradle Plugin and Compose Multiplatform developers for a bunch of Gradle Extensions.