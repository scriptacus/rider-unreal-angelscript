void Test()
{
    float Val = 1.0f;
    bool bBool = true;

    // Case 1: Top-level ternary (INVALID - should produce error)
    // The colon ':' at top-level is treated as format separator
    Print(f"Val: {bBool? -1.0f : 1.0f}");

    // Case 2: Format specifier (VALID)
    Print(f"Val: {Val:.2f}");

    // Case 3: Parenthesized ternary (VALID)
    Print(f"Val: {(bBool? -1.0f : 1.0f)}");

    // Case 4: Ternary in subexpression (VALID - this was broken in old implementation)
    Print(f"Val: {Val * (bBool?-1.0f:1.0f)}");

    // Additional valid cases
    Print(f"{GetValue():.2f}");
    Print(f"{val=}");
    Print(f"{(bBool ? -1.0f : 1.0f):.3f}");
}