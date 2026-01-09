void Test()
{
    // This reproduces the incremental lexing bug:
    // 1. Start with: auto Foo = f"";
    // 2. Type { or } inside the f-string
    // 3. Lexer state corruption occurs during incremental update

    auto Foo = f"{PropertyDepth}";
    auto Bar = f"}";  // Edge case: closing brace in f-string
    auto Baz = f"{";  // Edge case: opening brace in f-string (malformed)
}
