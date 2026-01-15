package com.scriptacus.riderunrealangelscript.lang.lexer;

import com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

%%

%public
%class AngelScriptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

%{
  // Track brace depth inside f-string expressions
  // This allows us to correctly handle nested braces like {obj[{key: value}]}
  // We only treat } as FSTRING_EXPR_END when braceDepth == 0
  private int fstringBraceDepth = 0;

  // Track parenthesis depth inside f-string expressions
  // This distinguishes format separators from ternary operators:
  // - {Val:.2f} → parenDepth=0, colon is format separator
  // - {(a?b:c)} → parenDepth=1, colon is ternary operator
  private int fstringParenDepth = 0;
%}

// ─────────────── Lexer States ───────────────
%state IN_FSTRING
%state IN_FSTRING_EXPR
%state IN_FSTRING_FORMAT
%state IN_NAMESTRING

// ─────────────── Regex Macros ───────────────
CRLF=\R
WHITE_SPACE = [ \t\r\n]+
LINE_COMMENT = "//"[^\n\r]*
BLOCK_COMMENT = "/*" ~"*/"
COMMENT = {LINE_COMMENT}|{BLOCK_COMMENT}

IDENTIFIER = [A-Za-z_][A-Za-z0-9_]*

// Number literals (comprehensive support)
HEX_LITERAL = 0[xX][0-9a-fA-F]+
BINARY_LITERAL = 0[bB][01]+
OCTAL_LITERAL = 0[oO][0-7]+
DECIMAL_INTEGER = [0-9]+
EXPONENT = [eE][+-]?[0-9]+
FLOAT_WITH_DECIMAL = ([0-9]+\.[0-9]*|\.[0-9]+){EXPONENT}?[fF]?
FLOAT_WITH_EXPONENT = [0-9]+{EXPONENT}[fF]?
DECIMAL_FLOAT = {FLOAT_WITH_DECIMAL}|{FLOAT_WITH_EXPONENT}
NUMBER = {HEX_LITERAL}|{BINARY_LITERAL}|{OCTAL_LITERAL}|{DECIMAL_FLOAT}|{DECIMAL_INTEGER}

// Regular strings
DQ_STRING  = \"([^\"\\]|\\.)*\"
SQ_STRING = \'([^\'\\\r\n]|\\.)*\'
TRIPLE_QUOTE_STRING= \"\"\"([^\"]|\"[^\"]|\"\"[^\"]|\\.)*\"\"\"
STRING = {DQ_STRING}|{SQ_STRING}|{TRIPLE_QUOTE_STRING}

%%

// ─────────────── Main State Rules ──────────────────────

// Whitespace and comments
<YYINITIAL> {CRLF} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {COMMENT} { return AngelScriptTypes.COMMENT; }

// Preprocessor directives (treat as comments, including indented ones)
<YYINITIAL> "#"[^\n\r]* { return AngelScriptTypes.COMMENT; }

// F-strings (format strings)
<YYINITIAL> f\" { fstringBraceDepth = 0; yybegin(IN_FSTRING); return AngelScriptTypes.FSTRING_BEGIN; }

// Name strings (FName literals)
<YYINITIAL> n\" { yybegin(IN_NAMESTRING); return AngelScriptTypes.NAMESTRING_BEGIN; }

// ─── Unreal Macros (must come before keywords) ───
<YYINITIAL> "UPROPERTY" { return AngelScriptTypes.UPROPERTY; }
<YYINITIAL> "UFUNCTION" { return AngelScriptTypes.UFUNCTION; }
<YYINITIAL> "UCLASS" { return AngelScriptTypes.UCLASS; }
<YYINITIAL> "USTRUCT" { return AngelScriptTypes.USTRUCT; }
<YYINITIAL> "UENUM" { return AngelScriptTypes.UENUM; }
<YYINITIAL> "UMETA" { return AngelScriptTypes.UMETA; }

// ─── Declaration Keywords ───
<YYINITIAL> "class" { return AngelScriptTypes.CLASS; }
<YYINITIAL> "struct" { return AngelScriptTypes.STRUCT; }
<YYINITIAL> "enum" { return AngelScriptTypes.ENUM; }
<YYINITIAL> "delegate" { return AngelScriptTypes.DELEGATE; }
<YYINITIAL> "event" { return AngelScriptTypes.EVENT; }
<YYINITIAL> "namespace" { return AngelScriptTypes.NAMESPACE; }

// ─── Control Flow Keywords ───
<YYINITIAL> "if" { return AngelScriptTypes.IF; }
<YYINITIAL> "else" { return AngelScriptTypes.ELSE; }
<YYINITIAL> "for" { return AngelScriptTypes.FOR; }
<YYINITIAL> "while" { return AngelScriptTypes.WHILE; }
<YYINITIAL> "switch" { return AngelScriptTypes.SWITCH; }
<YYINITIAL> "case" { return AngelScriptTypes.CASE; }
<YYINITIAL> "default" { return AngelScriptTypes.DEFAULT; }
<YYINITIAL> "break" { return AngelScriptTypes.BREAK; }
<YYINITIAL> "continue" { return AngelScriptTypes.CONTINUE; }
<YYINITIAL> "return" { return AngelScriptTypes.RETURN; }
<YYINITIAL> "fallthrough" { return AngelScriptTypes.FALLTHROUGH; }

// ─── Modifier Keywords ───
<YYINITIAL> "const" { return AngelScriptTypes.CONST; }
<YYINITIAL> "final" { return AngelScriptTypes.FINAL; }
<YYINITIAL> "override" { return AngelScriptTypes.OVERRIDE; }
<YYINITIAL> "property" { return AngelScriptTypes.PROPERTY; }
<YYINITIAL> "mixin" { return AngelScriptTypes.MIXIN; }
<YYINITIAL> "local" { return AngelScriptTypes.LOCAL; }

// ─── Access Modifiers ───
<YYINITIAL> "private" { return AngelScriptTypes.PRIVATE; }
<YYINITIAL> "protected" { return AngelScriptTypes.PROTECTED; }
<YYINITIAL> "public" { return AngelScriptTypes.PUBLIC; }
<YYINITIAL> "access" { return AngelScriptTypes.ACCESS; }

// ─── Special Keywords ───
<YYINITIAL> "this" { return AngelScriptTypes.THIS; }
<YYINITIAL> "Cast" { return AngelScriptTypes.CAST; }
<YYINITIAL> "asset" { return AngelScriptTypes.ASSET; }
<YYINITIAL> "of" { return AngelScriptTypes.OF; }
<YYINITIAL> "from" { return AngelScriptTypes.FROM; }
<YYINITIAL> "in" { return AngelScriptTypes.IN; }
<YYINITIAL> "out" { return AngelScriptTypes.OUT; }
<YYINITIAL> "inout" { return AngelScriptTypes.INOUT; }

// ─── Primitive Type Keywords ───
<YYINITIAL> "void" { return AngelScriptTypes.VOID; }
<YYINITIAL> "bool" { return AngelScriptTypes.BOOL; }
<YYINITIAL> "int" { return AngelScriptTypes.INT; }
<YYINITIAL> "int8" { return AngelScriptTypes.INT8; }
<YYINITIAL> "int16" { return AngelScriptTypes.INT16; }
<YYINITIAL> "int32" { return AngelScriptTypes.INT32; }
<YYINITIAL> "int64" { return AngelScriptTypes.INT64; }
<YYINITIAL> "uint" { return AngelScriptTypes.UINT; }
<YYINITIAL> "uint8" { return AngelScriptTypes.UINT8; }
<YYINITIAL> "uint16" { return AngelScriptTypes.UINT16; }
<YYINITIAL> "uint32" { return AngelScriptTypes.UINT32; }
<YYINITIAL> "uint64" { return AngelScriptTypes.UINT64; }
<YYINITIAL> "float" { return AngelScriptTypes.FLOAT; }
<YYINITIAL> "float32" { return AngelScriptTypes.FLOAT32; }
<YYINITIAL> "float64" { return AngelScriptTypes.FLOAT64; }
<YYINITIAL> "double" { return AngelScriptTypes.DOUBLE; }

// ─── Boolean and Null Literals ───
<YYINITIAL> "true" { return AngelScriptTypes.TRUE; }
<YYINITIAL> "false" { return AngelScriptTypes.FALSE; }
<YYINITIAL> "nullptr" { return AngelScriptTypes.NULLPTR; }

// ─── Auto Keyword (limited support in Unreal AS) ───
<YYINITIAL> "auto" { return AngelScriptTypes.AUTO; }

// ─── Operators (Order matters! Longer operators first) ───
// Scope and member access
<YYINITIAL> "::" { return AngelScriptTypes.SCOPE; }
<YYINITIAL> "." { return AngelScriptTypes.DOT; }
<YYINITIAL> "..." { return AngelScriptTypes.VARIADIC; }

// Compound assignment operators (before simple operators)
<YYINITIAL> ">>>=" { return AngelScriptTypes.SHIFT_RIGHT_A_ASSIGN; }
<YYINITIAL> ">>=" { return AngelScriptTypes.SHIFT_RIGHT_L_ASSIGN; }
<YYINITIAL> "<<=" { return AngelScriptTypes.SHIFT_LEFT_ASSIGN; }
<YYINITIAL> "+=" { return AngelScriptTypes.ADD_ASSIGN; }
<YYINITIAL> "-=" { return AngelScriptTypes.SUB_ASSIGN; }
<YYINITIAL> "*=" { return AngelScriptTypes.MUL_ASSIGN; }
<YYINITIAL> "/=" { return AngelScriptTypes.DIV_ASSIGN; }
<YYINITIAL> "%=" { return AngelScriptTypes.MOD_ASSIGN; }
<YYINITIAL> "&=" { return AngelScriptTypes.AND_ASSIGN; }
<YYINITIAL> "|=" { return AngelScriptTypes.OR_ASSIGN; }
<YYINITIAL> "^=" { return AngelScriptTypes.XOR_ASSIGN; }

// Shift and comparison operators (before < and >)
// Note: >> and >>> are commented out to allow nested templates like TArray<TArray<int>>
// Individual > tokens will be parsed, and shift operations will be handled in the parser
//<YYINITIAL> ">>>" { return AngelScriptTypes.BIT_SHIFT_RIGHT_ARITH; }
//<YYINITIAL> ">>" { return AngelScriptTypes.BIT_SHIFT_RIGHT; }
<YYINITIAL> "<<" { return AngelScriptTypes.BIT_SHIFT_LEFT; }
<YYINITIAL> ">=" { return AngelScriptTypes.GREATER_THAN_OR_EQUAL; }
<YYINITIAL> "<=" { return AngelScriptTypes.LESS_THAN_OR_EQUAL; }
<YYINITIAL> "==" { return AngelScriptTypes.EQUAL; }
<YYINITIAL> "!=" { return AngelScriptTypes.NOT_EQUAL; }

// Logical operators
<YYINITIAL> "&&" { return AngelScriptTypes.AND; }
<YYINITIAL> "||" { return AngelScriptTypes.OR; }
<YYINITIAL> "^^" { return AngelScriptTypes.XOR; }

// Increment/decrement
<YYINITIAL> "++" { return AngelScriptTypes.INC; }
<YYINITIAL> "--" { return AngelScriptTypes.DEC; }

// Single-character operators
<YYINITIAL> "+" { return AngelScriptTypes.PLUS; }
<YYINITIAL> "-" { return AngelScriptTypes.MINUS; }
<YYINITIAL> "*" { return AngelScriptTypes.STAR; }
<YYINITIAL> "/" { return AngelScriptTypes.SLASH; }
<YYINITIAL> "%" { return AngelScriptTypes.PERCENT; }
<YYINITIAL> "=" { return AngelScriptTypes.ASSIGNMENT; }
<YYINITIAL> "<" { return AngelScriptTypes.LESS_THAN; }
<YYINITIAL> ">" { return AngelScriptTypes.GREATER_THAN; }
<YYINITIAL> "&" { return AngelScriptTypes.AMP; }
<YYINITIAL> "|" { return AngelScriptTypes.BIT_OR; }
<YYINITIAL> "^" { return AngelScriptTypes.BIT_XOR; }
<YYINITIAL> "~" { return AngelScriptTypes.BIT_NOT; }
<YYINITIAL> "!" { return AngelScriptTypes.NOT; }

// Delimiters
<YYINITIAL> ";" { return AngelScriptTypes.END_STATEMENT; }
<YYINITIAL> "," { return AngelScriptTypes.LIST_SEPARATOR; }
<YYINITIAL> "{" { return AngelScriptTypes.START_STATEMENT_BLOCK; }
<YYINITIAL> "}" { return AngelScriptTypes.END_STATEMENT_BLOCK; }
<YYINITIAL> "(" { return AngelScriptTypes.OPEN_PARENTHESIS; }
<YYINITIAL> ")" { return AngelScriptTypes.CLOSE_PARENTHESIS; }
<YYINITIAL> "[" { return AngelScriptTypes.OPEN_BRACKET; }
<YYINITIAL> "]" { return AngelScriptTypes.CLOSE_BRACKET; }
<YYINITIAL> "?" { return AngelScriptTypes.QUESTION; }
<YYINITIAL> ":" { return AngelScriptTypes.COLON; }

// ─── Identifiers, Numbers, Strings ───
<YYINITIAL> {IDENTIFIER} { return AngelScriptTypes.IDENTIFIER; }
<YYINITIAL> {NUMBER} { return AngelScriptTypes.NUMBER; }
<YYINITIAL> {STRING} { return AngelScriptTypes.STRING; }

// Unknown tokens
<YYINITIAL> . { return AngelScriptTypes.UNKNOWN; }

// ─────────────── F-String State (Format Strings) ──────────────────────

<IN_FSTRING> {
    "{{" { return AngelScriptTypes.FSTRING_ESCAPED_LBRACE; }
    "}}" { return AngelScriptTypes.FSTRING_ESCAPED_RBRACE; }
    "{" { fstringBraceDepth = 1; yybegin(IN_FSTRING_EXPR); return AngelScriptTypes.FSTRING_EXPR_BEGIN; }
    "\"" { fstringBraceDepth = 0; yybegin(YYINITIAL); return AngelScriptTypes.FSTRING_END; }
    [^{}\"]+ { return AngelScriptTypes.FSTRING_TEXT; }
    [^] { return TokenType.BAD_CHARACTER; }
}

<IN_FSTRING_EXPR> {
    // Format specifier begins with : or = (only at brace depth 1 AND paren depth 0)
    // This ensures {Val:.2f} has colon as separator, but {(a?b:c)} has colon as ternary
    ":" {
        if (fstringBraceDepth == 1 && fstringParenDepth == 0) {
            yybegin(IN_FSTRING_FORMAT);
            return AngelScriptTypes.FSTRING_FORMAT_SEP;
        } else {
            return AngelScriptTypes.COLON;
        }
    }

    // Debug format specifier {val=}
    "=" {
        if (fstringBraceDepth == 1 && fstringParenDepth == 0) {
            yybegin(IN_FSTRING_FORMAT);
            return AngelScriptTypes.FSTRING_DEBUG_EQ;
        } else {
            return AngelScriptTypes.ASSIGNMENT;
        }
    }

    // Track brace depth for nested expressions (object literals, blocks, etc.)
    "{" { fstringBraceDepth++; return AngelScriptTypes.START_STATEMENT_BLOCK; }

    // End of expression - only when brace depth returns to 0 (matching the opening brace)
    "}" {
        fstringBraceDepth--;
        if (fstringBraceDepth == 0) {
            fstringParenDepth = 0;  // Reset paren depth when exiting
            yybegin(IN_FSTRING);
            return AngelScriptTypes.FSTRING_EXPR_END;
        } else {
            return AngelScriptTypes.END_STATEMENT_BLOCK;
        }
    }

    // Parentheses and brackets - track parens for ternary detection
    "(" { fstringParenDepth++; return AngelScriptTypes.OPEN_PARENTHESIS; }
    ")" { fstringParenDepth--; return AngelScriptTypes.CLOSE_PARENTHESIS; }
    "[" { return AngelScriptTypes.OPEN_BRACKET; }
    "]" { return AngelScriptTypes.CLOSE_BRACKET; }

    // Inside expression, use normal tokenization
    {WHITE_SPACE} { return TokenType.WHITE_SPACE; }
    {COMMENT} { return AngelScriptTypes.COMMENT; }

    // Scope and member access
    "::" { return AngelScriptTypes.SCOPE; }
    "." { return AngelScriptTypes.DOT; }
    "," { return AngelScriptTypes.LIST_SEPARATOR; }

    // Compound assignment operators (must come before simple operators)
    "+=" { return AngelScriptTypes.ADD_ASSIGN; }
    "-=" { return AngelScriptTypes.SUB_ASSIGN; }
    "*=" { return AngelScriptTypes.MUL_ASSIGN; }
    "/=" { return AngelScriptTypes.DIV_ASSIGN; }
    "%=" { return AngelScriptTypes.MOD_ASSIGN; }
    "&=" { return AngelScriptTypes.AND_ASSIGN; }
    "|=" { return AngelScriptTypes.OR_ASSIGN; }
    "^=" { return AngelScriptTypes.XOR_ASSIGN; }
    "<<=" { return AngelScriptTypes.SHIFT_LEFT_ASSIGN; }
    ">>=" { return AngelScriptTypes.SHIFT_RIGHT_L_ASSIGN; }
    ">>>=" { return AngelScriptTypes.SHIFT_RIGHT_A_ASSIGN; }

    // Comparison and shift operators (must come before < and >)
    "<<" { return AngelScriptTypes.BIT_SHIFT_LEFT; }
    ">=" { return AngelScriptTypes.GREATER_THAN_OR_EQUAL; }
    "<=" { return AngelScriptTypes.LESS_THAN_OR_EQUAL; }
    "==" { return AngelScriptTypes.EQUAL; }
    "!=" { return AngelScriptTypes.NOT_EQUAL; }

    // Logical operators
    "&&" { return AngelScriptTypes.AND; }
    "||" { return AngelScriptTypes.OR; }
    "^^" { return AngelScriptTypes.XOR; }

    // Increment/decrement
    "++" { return AngelScriptTypes.INC; }
    "--" { return AngelScriptTypes.DEC; }

    // Single-character operators
    "+" { return AngelScriptTypes.PLUS; }
    "-" { return AngelScriptTypes.MINUS; }
    "*" { return AngelScriptTypes.STAR; }
    "/" { return AngelScriptTypes.SLASH; }
    "%" { return AngelScriptTypes.PERCENT; }
    "=" { return AngelScriptTypes.ASSIGNMENT; }
    "<" { return AngelScriptTypes.LESS_THAN; }
    ">" { return AngelScriptTypes.GREATER_THAN; }
    "&" { return AngelScriptTypes.AMP; }
    "|" { return AngelScriptTypes.BIT_OR; }
    "^" { return AngelScriptTypes.BIT_XOR; }
    "~" { return AngelScriptTypes.BIT_NOT; }
    "!" { return AngelScriptTypes.NOT; }
    "?" { return AngelScriptTypes.QUESTION; }

    // Identifiers, numbers, strings
    {IDENTIFIER} { return AngelScriptTypes.IDENTIFIER; }
    {NUMBER} { return AngelScriptTypes.NUMBER; }
    {STRING} { return AngelScriptTypes.STRING; }

    // Catch-all for any other character
    [^] { return TokenType.BAD_CHARACTER; }
}

// ─────────────── Format Specifier State ──────────────────────

<IN_FSTRING_FORMAT> {
    // Format specification characters (everything after : or = until })
    "}" { fstringParenDepth = 0; yybegin(IN_FSTRING); return AngelScriptTypes.FSTRING_EXPR_END; }
    [^}]+ { return AngelScriptTypes.FSTRING_FORMAT_SPEC; }
}

// ─────────────── Name String State (FName Literals) ──────────────────────

<IN_NAMESTRING> {
    "\"" { yybegin(YYINITIAL); return AngelScriptTypes.NAMESTRING_END; }
    [^\"]+ { return AngelScriptTypes.NAMESTRING_TEXT; }
}

