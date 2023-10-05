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