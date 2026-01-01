// Test missing closing brace recovery in function body
void IncompleteFunction() {
    int x = 1;
    int y = 2;
    // Missing } for function

// Should recover to next top-level declaration
void NextFunction() {
    return;
}
