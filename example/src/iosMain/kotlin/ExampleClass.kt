import kotlin.experimental.ExperimentalObjCName

class ExampleClass {
    val someProperty = "String property"

    fun mySuperFun(): Int = 10
    fun withCallback(myCallback: () -> String) {}
    fun myFunctionReturnMap(): Map<String, String> = mapOf("Example" to "Value")

    @OptIn(ExperimentalObjCName::class)
    @ObjCName(swiftName = "mySwiftNameFunction")
    fun withObjcName() {}

    @OptIn(ExperimentalObjCName::class)
    @ObjCName(swiftName = "swiftDoSomethingReallyCool")
    fun doSomethingReallyCool() {}
}

fun ExampleClass.myExtensionFunction() {}
fun ExampleClass.newExtension() {}

fun myTopLevelFunction(): String = "Example return string"
