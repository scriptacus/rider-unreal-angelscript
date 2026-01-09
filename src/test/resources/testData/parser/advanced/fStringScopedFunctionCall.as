void Test()
{
    // Test scope resolution with f-string argument
    mm::Text(f"{PropertyDepth}");

    // Test variations
    Namespace::Function(f"{Value}");
    ::GlobalFunc(f"{Count}");

    // Test nested scope resolution
    mm::Text(f"{ns::Value}");
}
