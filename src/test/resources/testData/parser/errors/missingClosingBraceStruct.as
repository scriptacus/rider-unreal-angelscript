// Test missing closing brace recovery in struct
struct TestStruct {
    int x;
    int y;
    void Method() {
        return;
    }
    // Missing } for struct

// Should recover to next top-level declaration
class Recovery {
    int member;
}
