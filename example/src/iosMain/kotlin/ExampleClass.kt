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
