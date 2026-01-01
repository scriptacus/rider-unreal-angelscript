// Test missing closing brace recovery
class TestClass {
    int member;
    void Method() {
        return;
    }
    // Missing } for class

// Should recover to next top-level declaration
namespace Recovery {
    int global;
}