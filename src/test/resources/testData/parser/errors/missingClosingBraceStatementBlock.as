// Test missing closing brace recovery in statement blocks
void TestFunction() {
    if (true) {
        int x = 1;
        // Missing } for if block

    // Should recover and continue parsing in function body
    int y = 2;

    while (false) {
        int z = 3;
    }
}
