// Constructor-style variable declarations
// Tests disambiguation of Type var(args); vs Type method(args) {}

class TestClass
{
    // Variables with constructor-style initialization
    TInstigated<EMovementType> MovementType(EMovementType::Walk);
    TArray<int> Numbers(10);
    Vector Position(1.0f, 2.0f, 3.0f);

    // Variables with explicit types
    MyClass Obj(arg1, arg2);
    TMap<FString, int> Dictionary(InitialCapacity);

    // Methods (not variables) - have body
    void Method() {}
    int Calculate(int x) { return x * 2; }

    // Methods with qualifiers
    void ConstMethod() const {}
    void PropertyMethod() property {}
}
