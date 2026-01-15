// This is a generated file. Not intended for manual editing.
package com.scriptacus.riderunrealangelscript.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.scriptacus.riderunrealangelscript.lang.psi.AngelScriptTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class AngelScriptParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, EXTENDS_SETS_);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return script(builder_, level_ + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ADDITIVE_EXPR, ASSIGN_EXPR, BITWISE_AND_EXPR, BITWISE_OR_EXPR,
      BITWISE_XOR_EXPR, EQUALITY_EXPR, EXPR, LOGICAL_AND_EXPR,
      LOGICAL_OR_EXPR, MACRO_VALUE_EXPR, MEMBER_ACCESS_EXPR, MULTIPLICATIVE_EXPR,
      POSTFIX_EXPR, PRIMARY_EXPR, RELATIONAL_EXPR, SHIFT_EXPR,
      TERNARY_EXPR, UNARY_EXPR),
  };

  /* ********************************************************** */
  // !('}' | CLASS | STRUCT | INTERFACE | ENUM | NAMESPACE | MIXIN | FUNCDEF | TYPEDEF)
  static boolean ClosedBracketRecover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ClosedBracketRecover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !ClosedBracketRecover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '}' | CLASS | STRUCT | INTERFACE | ENUM | NAMESPACE | MIXIN | FUNCDEF | TYPEDEF
  private static boolean ClosedBracketRecover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ClosedBracketRecover_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, "}");
    if (!result_) result_ = consumeToken(builder_, CLASS);
    if (!result_) result_ = consumeToken(builder_, STRUCT);
    if (!result_) result_ = consumeToken(builder_, INTERFACE);
    if (!result_) result_ = consumeToken(builder_, ENUM);
    if (!result_) result_ = consumeToken(builder_, NAMESPACE);
    if (!result_) result_ = consumeToken(builder_, MIXIN);
    if (!result_) result_ = consumeToken(builder_, FUNCDEF);
    if (!result_) result_ = consumeToken(builder_, TYPEDEF);
    return result_;
  }

  /* ********************************************************** */
  // !('}' | CLASS | STRUCT | INTERFACE | ENUM | NAMESPACE | MIXIN | FUNCDEF | TYPEDEF)
  static boolean UntilBraceRecover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UntilBraceRecover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !UntilBraceRecover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '}' | CLASS | STRUCT | INTERFACE | ENUM | NAMESPACE | MIXIN | FUNCDEF | TYPEDEF
  private static boolean UntilBraceRecover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UntilBraceRecover_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, "}");
    if (!result_) result_ = consumeToken(builder_, CLASS);
    if (!result_) result_ = consumeToken(builder_, STRUCT);
    if (!result_) result_ = consumeToken(builder_, INTERFACE);
    if (!result_) result_ = consumeToken(builder_, ENUM);
    if (!result_) result_ = consumeToken(builder_, NAMESPACE);
    if (!result_) result_ = consumeToken(builder_, MIXIN);
    if (!result_) result_ = consumeToken(builder_, FUNCDEF);
    if (!result_) result_ = consumeToken(builder_, TYPEDEF);
    return result_;
  }

  /* ********************************************************** */
  // !(';')
  static boolean UntilSemicolonRecover(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UntilSemicolonRecover")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !UntilSemicolonRecover_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (';')
  private static boolean UntilSemicolonRecover_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "UntilSemicolonRecover_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ";");
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (IDENTIFIER | STAR | call_expression) access_modifiers?
  public static boolean access_class(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_class")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ACCESS_CLASS, "<access class>");
    result_ = access_class_0(builder_, level_ + 1);
    result_ = result_ && access_class_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // IDENTIFIER | STAR | call_expression
  private static boolean access_class_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_class_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, IDENTIFIER);
    if (!result_) result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = call_expression(builder_, level_ + 1);
    return result_;
  }

  // access_modifiers?
  private static boolean access_class_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_class_1")) return false;
    access_modifiers(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // ACCESS IDENTIFIER ASSIGNMENT access_entry (LIST_SEPARATOR access_entry)* END_STATEMENT
  public static boolean access_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_decl")) return false;
    if (!nextTokenIs(builder_, ACCESS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ACCESS, IDENTIFIER, ASSIGNMENT);
    result_ = result_ && access_entry(builder_, level_ + 1);
    result_ = result_ && access_decl_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, marker_, ACCESS_DECL, result_);
    return result_;
  }

  // (LIST_SEPARATOR access_entry)*
  private static boolean access_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_decl_4")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!access_decl_4_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "access_decl_4", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR access_entry
  private static boolean access_decl_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_decl_4_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && access_entry(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // access_level? access_class
  public static boolean access_entry(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_entry")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ACCESS_ENTRY, "<access entry>");
    result_ = access_entry_0(builder_, level_ + 1);
    result_ = result_ && access_class(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // access_level?
  private static boolean access_entry_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_entry_0")) return false;
    access_level(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // PRIVATE | PROTECTED | PUBLIC
  public static boolean access_level(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_level")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ACCESS_LEVEL, "<access level>");
    result_ = consumeToken(builder_, PRIVATE);
    if (!result_) result_ = consumeToken(builder_, PROTECTED);
    if (!result_) result_ = consumeToken(builder_, PUBLIC);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean access_modifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_modifier")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, marker_, ACCESS_MODIFIER, result_);
    return result_;
  }

  /* ********************************************************** */
  // OPEN_PARENTHESIS access_modifier (LIST_SEPARATOR access_modifier)* CLOSE_PARENTHESIS
  public static boolean access_modifiers(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_modifiers")) return false;
    if (!nextTokenIs(builder_, OPEN_PARENTHESIS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OPEN_PARENTHESIS);
    result_ = result_ && access_modifier(builder_, level_ + 1);
    result_ = result_ && access_modifiers_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, ACCESS_MODIFIERS, result_);
    return result_;
  }

  // (LIST_SEPARATOR access_modifier)*
  private static boolean access_modifiers_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_modifiers_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!access_modifiers_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "access_modifiers_2", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR access_modifier
  private static boolean access_modifiers_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_modifiers_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && access_modifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // PRIVATE
  //   | PROTECTED
  //   | PUBLIC
  //   | ACCESS (COLON IDENTIFIER)?
  public static boolean access_specifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_specifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ACCESS_SPECIFIER, "<access specifier>");
    result_ = consumeToken(builder_, PRIVATE);
    if (!result_) result_ = consumeToken(builder_, PROTECTED);
    if (!result_) result_ = consumeToken(builder_, PUBLIC);
    if (!result_) result_ = access_specifier_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ACCESS (COLON IDENTIFIER)?
  private static boolean access_specifier_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_specifier_3")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ACCESS);
    result_ = result_ && access_specifier_3_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COLON IDENTIFIER)?
  private static boolean access_specifier_3_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_specifier_3_1")) return false;
    access_specifier_3_1_0(builder_, level_ + 1);
    return true;
  }

  // COLON IDENTIFIER
  private static boolean access_specifier_3_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "access_specifier_3_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, COLON, IDENTIFIER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // PLUS | MINUS
  static boolean additive_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "additive_op")) return false;
    if (!nextTokenIs(builder_, "", MINUS, PLUS)) return false;
    boolean result_;
    result_ = consumeToken(builder_, PLUS);
    if (!result_) result_ = consumeToken(builder_, MINUS);
    return result_;
  }

  /* ********************************************************** */
  // named_argument | expr
  public static boolean argument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argument")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ARGUMENT, "<argument>");
    result_ = named_argument(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // argument (LIST_SEPARATOR argument)* LIST_SEPARATOR?
  public static boolean argument_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argument_list")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ARGUMENT_LIST, "<argument list>");
    result_ = argument(builder_, level_ + 1);
    result_ = result_ && argument_list_1(builder_, level_ + 1);
    result_ = result_ && argument_list_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (LIST_SEPARATOR argument)*
  private static boolean argument_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argument_list_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!argument_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "argument_list_1", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR argument
  private static boolean argument_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argument_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && argument(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LIST_SEPARATOR?
  private static boolean argument_list_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argument_list_2")) return false;
    consumeToken(builder_, LIST_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK statement* END_STATEMENT_BLOCK
  public static boolean asset_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asset_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    result_ = result_ && asset_body_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT_BLOCK);
    exit_section_(builder_, marker_, ASSET_BODY, result_);
    return result_;
  }

  // statement*
  private static boolean asset_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asset_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "asset_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ASSET IDENTIFIER OF typename (END_STATEMENT | asset_body)?
  public static boolean asset_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asset_decl")) return false;
    if (!nextTokenIs(builder_, ASSET)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ASSET, IDENTIFIER, OF);
    result_ = result_ && typename(builder_, level_ + 1);
    result_ = result_ && asset_decl_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, ASSET_DECL, result_);
    return result_;
  }

  // (END_STATEMENT | asset_body)?
  private static boolean asset_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asset_decl_4")) return false;
    asset_decl_4_0(builder_, level_ + 1);
    return true;
  }

  // END_STATEMENT | asset_body
  private static boolean asset_decl_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "asset_decl_4_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, END_STATEMENT);
    if (!result_) result_ = asset_body(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // ASSIGNMENT
  //   | ADD_ASSIGN
  //   | SUB_ASSIGN
  //   | MUL_ASSIGN
  //   | DIV_ASSIGN
  //   | MOD_ASSIGN
  //   | AND_ASSIGN
  //   | OR_ASSIGN
  //   | XOR_ASSIGN
  //   | SHIFT_LEFT_ASSIGN
  //   | SHIFT_RIGHT_L_ASSIGN
  //   | SHIFT_RIGHT_A_ASSIGN
  static boolean assignment_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "assignment_op")) return false;
    boolean result_;
    result_ = consumeToken(builder_, ASSIGNMENT);
    if (!result_) result_ = consumeToken(builder_, ADD_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SUB_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MUL_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, DIV_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, MOD_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, AND_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, OR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, XOR_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHIFT_LEFT_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHIFT_RIGHT_L_ASSIGN);
    if (!result_) result_ = consumeToken(builder_, SHIFT_RIGHT_A_ASSIGN);
    return result_;
  }

  /* ********************************************************** */
  // BREAK END_STATEMENT
  public static boolean break_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "break_statement")) return false;
    if (!nextTokenIs(builder_, BREAK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, BREAK, END_STATEMENT);
    exit_section_(builder_, marker_, BREAK_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // (typename | scoped_identifier) OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS
  public static boolean call_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expression")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CALL_EXPRESSION, "<call expression>");
    result_ = call_expression_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, OPEN_PARENTHESIS);
    pinned_ = result_; // pin = 2
    result_ = result_ && report_error_(builder_, call_expression_2(builder_, level_ + 1));
    result_ = pinned_ && consumeToken(builder_, CLOSE_PARENTHESIS) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // typename | scoped_identifier
  private static boolean call_expression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expression_0")) return false;
    boolean result_;
    result_ = typename(builder_, level_ + 1);
    if (!result_) result_ = scoped_identifier(builder_, level_ + 1);
    return result_;
  }

  // argument_list?
  private static boolean call_expression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "call_expression_2")) return false;
    argument_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // case_statement | default_case_statement
  public static boolean case_clause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_clause")) return false;
    if (!nextTokenIs(builder_, "<case clause>", CASE, DEFAULT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CASE_CLAUSE, "<case clause>");
    result_ = case_statement(builder_, level_ + 1);
    if (!result_) result_ = default_case_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // CASE expr COLON statement*
  public static boolean case_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_statement")) return false;
    if (!nextTokenIs(builder_, CASE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CASE);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, COLON);
    result_ = result_ && case_statement_3(builder_, level_ + 1);
    exit_section_(builder_, marker_, CASE_STATEMENT, result_);
    return result_;
  }

  // statement*
  private static boolean case_statement_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "case_statement_3")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "case_statement_3", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // CAST LESS_THAN typename GREATER_THAN OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
  public static boolean cast_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cast_expression")) return false;
    if (!nextTokenIs(builder_, CAST)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CAST, LESS_THAN);
    result_ = result_ && typename(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, GREATER_THAN, OPEN_PARENTHESIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, CAST_EXPRESSION, result_);
    return result_;
  }

  /* ********************************************************** */
  // unfinished_class_body END_STATEMENT_BLOCK?
  public static boolean class_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unfinished_class_body(builder_, level_ + 1);
    result_ = result_ && class_body_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, CLASS_BODY, result_);
    return result_;
  }

  // END_STATEMENT_BLOCK?
  private static boolean class_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_body_1")) return false;
    consumeToken(builder_, END_STATEMENT_BLOCK);
    return true;
  }

  /* ********************************************************** */
  // uclass_macro? CLASS IDENTIFIER class_inheritance? class_body?
  public static boolean class_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_decl")) return false;
    if (!nextTokenIs(builder_, "<class decl>", CLASS, UCLASS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS_DECL, "<class decl>");
    result_ = class_decl_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, CLASS, IDENTIFIER);
    result_ = result_ && class_decl_3(builder_, level_ + 1);
    result_ = result_ && class_decl_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // uclass_macro?
  private static boolean class_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_decl_0")) return false;
    uclass_macro(builder_, level_ + 1);
    return true;
  }

  // class_inheritance?
  private static boolean class_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_decl_3")) return false;
    class_inheritance(builder_, level_ + 1);
    return true;
  }

  // class_body?
  private static boolean class_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_decl_4")) return false;
    class_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // COLON scoped_identifier
  public static boolean class_inheritance(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_inheritance")) return false;
    if (!nextTokenIs(builder_, COLON)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && scoped_identifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, CLASS_INHERITANCE, result_);
    return result_;
  }

  /* ********************************************************** */
  // access_decl                  // Unreal extension
  //   | constructor_decl             // Constructor (must come before method to avoid ambiguity - no return type)
  //   | destructor_decl              // Destructor (implicit in official grammar)
  //   | class_method_decl            // BNF:1 FUNC (must come before property to avoid ambiguity)
  //   | class_property_decl          // BNF:1 VAR (includes access_specifier)
  //   | default_statement
  public static boolean class_member(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_member")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS_MEMBER, "<class member>");
    result_ = access_decl(builder_, level_ + 1);
    if (!result_) result_ = constructor_decl(builder_, level_ + 1);
    if (!result_) result_ = destructor_decl(builder_, level_ + 1);
    if (!result_) result_ = class_method_decl(builder_, level_ + 1);
    if (!result_) result_ = class_property_decl(builder_, level_ + 1);
    if (!result_) result_ = default_statement(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // not_constructor_variable access_specifier? ufunction_macro? access_specifier? (MIXIN | LOCAL)? function_decl
  public static boolean class_method_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS_METHOD_DECL, "<class method decl>");
    result_ = not_constructor_variable(builder_, level_ + 1);
    result_ = result_ && class_method_decl_1(builder_, level_ + 1);
    result_ = result_ && class_method_decl_2(builder_, level_ + 1);
    result_ = result_ && class_method_decl_3(builder_, level_ + 1);
    result_ = result_ && class_method_decl_4(builder_, level_ + 1);
    result_ = result_ && function_decl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // access_specifier?
  private static boolean class_method_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl_1")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  // ufunction_macro?
  private static boolean class_method_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl_2")) return false;
    ufunction_macro(builder_, level_ + 1);
    return true;
  }

  // access_specifier?
  private static boolean class_method_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl_3")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  // (MIXIN | LOCAL)?
  private static boolean class_method_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl_4")) return false;
    class_method_decl_4_0(builder_, level_ + 1);
    return true;
  }

  // MIXIN | LOCAL
  private static boolean class_method_decl_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_method_decl_4_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, MIXIN);
    if (!result_) result_ = consumeToken(builder_, LOCAL);
    return result_;
  }

  /* ********************************************************** */
  // access_specifier? uproperty_macro? access_specifier? variable_decl
  public static boolean class_property_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_property_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS_PROPERTY_DECL, "<class property decl>");
    result_ = class_property_decl_0(builder_, level_ + 1);
    result_ = result_ && class_property_decl_1(builder_, level_ + 1);
    result_ = result_ && class_property_decl_2(builder_, level_ + 1);
    result_ = result_ && variable_decl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // access_specifier?
  private static boolean class_property_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_property_decl_0")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  // uproperty_macro?
  private static boolean class_property_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_property_decl_1")) return false;
    uproperty_macro(builder_, level_ + 1);
    return true;
  }

  // access_specifier?
  private static boolean class_property_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "class_property_decl_2")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER OPEN_PARENTHESIS parameter_list CLOSE_PARENTHESIS function_body?
  public static boolean constructor_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constructor_decl")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, IDENTIFIER, OPEN_PARENTHESIS);
    result_ = result_ && parameter_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && constructor_decl_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, CONSTRUCTOR_DECL, result_);
    return result_;
  }

  // function_body?
  private static boolean constructor_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "constructor_decl_4")) return false;
    function_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // CONTINUE END_STATEMENT
  public static boolean continue_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "continue_statement")) return false;
    if (!nextTokenIs(builder_, CONTINUE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CONTINUE, END_STATEMENT);
    exit_section_(builder_, marker_, CONTINUE_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // DEFAULT COLON statement*
  public static boolean default_case_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_case_statement")) return false;
    if (!nextTokenIs(builder_, DEFAULT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, DEFAULT, COLON);
    result_ = result_ && default_case_statement_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, DEFAULT_CASE_STATEMENT, result_);
    return result_;
  }

  // statement*
  private static boolean default_case_statement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_case_statement_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "default_case_statement_2", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ASSIGNMENT expr
  public static boolean default_parameter_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_parameter_value")) return false;
    if (!nextTokenIs(builder_, ASSIGNMENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ASSIGNMENT);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, DEFAULT_PARAMETER_VALUE, result_);
    return result_;
  }

  /* ********************************************************** */
  // DEFAULT expr END_STATEMENT
  public static boolean default_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "default_statement")) return false;
    if (!nextTokenIs(builder_, DEFAULT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DEFAULT);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, marker_, DEFAULT_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // DELEGATE function_signature END_STATEMENT?
  public static boolean delegate_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "delegate_decl")) return false;
    if (!nextTokenIs(builder_, DELEGATE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, DELEGATE);
    result_ = result_ && function_signature(builder_, level_ + 1);
    result_ = result_ && delegate_decl_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, DELEGATE_DECL, result_);
    return result_;
  }

  // END_STATEMENT?
  private static boolean delegate_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "delegate_decl_2")) return false;
    consumeToken(builder_, END_STATEMENT);
    return true;
  }

  /* ********************************************************** */
  // BIT_NOT IDENTIFIER OPEN_PARENTHESIS CLOSE_PARENTHESIS function_body?
  public static boolean destructor_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "destructor_decl")) return false;
    if (!nextTokenIs(builder_, BIT_NOT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, BIT_NOT, IDENTIFIER, OPEN_PARENTHESIS, CLOSE_PARENTHESIS);
    result_ = result_ && destructor_decl_4(builder_, level_ + 1);
    exit_section_(builder_, marker_, DESTRUCTOR_DECL, result_);
    return result_;
  }

  // function_body?
  private static boolean destructor_decl_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "destructor_decl_4")) return false;
    function_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // ELSE statement
  public static boolean else_clause(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "else_clause")) return false;
    if (!nextTokenIs(builder_, ELSE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ELSE);
    result_ = result_ && statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, ELSE_CLAUSE, result_);
    return result_;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK enum_value (LIST_SEPARATOR enum_value)* LIST_SEPARATOR? END_STATEMENT_BLOCK
  public static boolean enum_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    result_ = result_ && enum_value(builder_, level_ + 1);
    result_ = result_ && enum_body_2(builder_, level_ + 1);
    result_ = result_ && enum_body_3(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT_BLOCK);
    exit_section_(builder_, marker_, ENUM_BODY, result_);
    return result_;
  }

  // (LIST_SEPARATOR enum_value)*
  private static boolean enum_body_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_body_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!enum_body_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "enum_body_2", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR enum_value
  private static boolean enum_body_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_body_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && enum_value(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LIST_SEPARATOR?
  private static boolean enum_body_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_body_3")) return false;
    consumeToken(builder_, LIST_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // uenum_macro? ENUM IDENTIFIER enum_body?
  public static boolean enum_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_decl")) return false;
    if (!nextTokenIs(builder_, "<enum decl>", ENUM, UENUM)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, ENUM_DECL, "<enum decl>");
    result_ = enum_decl_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, ENUM, IDENTIFIER);
    result_ = result_ && enum_decl_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // uenum_macro?
  private static boolean enum_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_decl_0")) return false;
    uenum_macro(builder_, level_ + 1);
    return true;
  }

  // enum_body?
  private static boolean enum_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_decl_3")) return false;
    enum_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER (ASSIGNMENT expr)? umeta_macro?
  public static boolean enum_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_value")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    result_ = result_ && enum_value_1(builder_, level_ + 1);
    result_ = result_ && enum_value_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, ENUM_VALUE, result_);
    return result_;
  }

  // (ASSIGNMENT expr)?
  private static boolean enum_value_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_value_1")) return false;
    enum_value_1_0(builder_, level_ + 1);
    return true;
  }

  // ASSIGNMENT expr
  private static boolean enum_value_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_value_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ASSIGNMENT);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // umeta_macro?
  private static boolean enum_value_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "enum_value_2")) return false;
    umeta_macro(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // EQUAL | NOT_EQUAL
  static boolean equality_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "equality_op")) return false;
    if (!nextTokenIs(builder_, "", EQUAL, NOT_EQUAL)) return false;
    boolean result_;
    result_ = consumeToken(builder_, EQUAL);
    if (!result_) result_ = consumeToken(builder_, NOT_EQUAL);
    return result_;
  }

  /* ********************************************************** */
  // EVENT function_signature END_STATEMENT
  public static boolean event_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "event_decl")) return false;
    if (!nextTokenIs(builder_, EVENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, EVENT);
    result_ = result_ && function_signature(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, marker_, EVENT_DECL, result_);
    return result_;
  }

  /* ********************************************************** */
  // expr END_STATEMENT
  public static boolean expression_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "expression_statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, EXPRESSION_STATEMENT, "<expression statement>");
    result_ = expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // FALLTHROUGH END_STATEMENT
  public static boolean fallthrough_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fallthrough_statement")) return false;
    if (!nextTokenIs(builder_, FALLTHROUGH)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, FALLTHROUGH, END_STATEMENT);
    exit_section_(builder_, marker_, FALLTHROUGH_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // for_variable_decl | expr
  public static boolean for_init(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_init")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOR_INIT, "<for init>");
    result_ = for_variable_decl(builder_, level_ + 1);
    if (!result_) result_ = expr(builder_, level_ + 1, -1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // FOR OPEN_PARENTHESIS for_init? END_STATEMENT expr? END_STATEMENT for_update? CLOSE_PARENTHESIS statement
  public static boolean for_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_statement")) return false;
    if (!nextTokenIs(builder_, FOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, FOR, OPEN_PARENTHESIS);
    result_ = result_ && for_statement_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    result_ = result_ && for_statement_4(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    result_ = result_ && for_statement_6(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, FOR_STATEMENT, result_);
    return result_;
  }

  // for_init?
  private static boolean for_statement_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_statement_2")) return false;
    for_init(builder_, level_ + 1);
    return true;
  }

  // expr?
  private static boolean for_statement_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_statement_4")) return false;
    expr(builder_, level_ + 1, -1);
    return true;
  }

  // for_update?
  private static boolean for_statement_6(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_statement_6")) return false;
    for_update(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // expr (LIST_SEPARATOR expr)*
  public static boolean for_update(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_update")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FOR_UPDATE, "<for update>");
    result_ = expr(builder_, level_ + 1, -1);
    result_ = result_ && for_update_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (LIST_SEPARATOR expr)*
  private static boolean for_update_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_update_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!for_update_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "for_update_1", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR expr
  private static boolean for_update_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_update_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // typename variable_declarator (LIST_SEPARATOR variable_declarator)*
  static boolean for_variable_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_variable_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = typename(builder_, level_ + 1);
    result_ = result_ && variable_declarator(builder_, level_ + 1);
    result_ = result_ && for_variable_decl_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (LIST_SEPARATOR variable_declarator)*
  private static boolean for_variable_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_variable_decl_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!for_variable_decl_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "for_variable_decl_2", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR variable_declarator
  private static boolean for_variable_decl_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "for_variable_decl_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && variable_declarator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // FOR OPEN_PARENTHESIS typename IDENTIFIER COLON expr CLOSE_PARENTHESIS statement
  public static boolean foreach_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "foreach_statement")) return false;
    if (!nextTokenIs(builder_, FOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, FOR, OPEN_PARENTHESIS);
    result_ = result_ && typename(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENTIFIER, COLON);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, FOREACH_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // FSTRING_TEXT
  //   | FSTRING_ESCAPED_LBRACE
  //   | FSTRING_ESCAPED_RBRACE
  //   | fstring_expression
  public static boolean fstring_content(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_content")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FSTRING_CONTENT, "<fstring content>");
    result_ = consumeToken(builder_, FSTRING_TEXT);
    if (!result_) result_ = consumeToken(builder_, FSTRING_ESCAPED_LBRACE);
    if (!result_) result_ = consumeToken(builder_, FSTRING_ESCAPED_RBRACE);
    if (!result_) result_ = fstring_expression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // FSTRING_EXPR_BEGIN expr fstring_format? FSTRING_EXPR_END
  public static boolean fstring_expression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_expression")) return false;
    if (!nextTokenIs(builder_, FSTRING_EXPR_BEGIN)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FSTRING_EXPRESSION, null);
    result_ = consumeToken(builder_, FSTRING_EXPR_BEGIN);
    pinned_ = result_; // pin = 1
    result_ = result_ && report_error_(builder_, expr(builder_, level_ + 1, -1));
    result_ = pinned_ && report_error_(builder_, fstring_expression_2(builder_, level_ + 1)) && result_;
    result_ = pinned_ && consumeToken(builder_, FSTRING_EXPR_END) && result_;
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // fstring_format?
  private static boolean fstring_expression_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_expression_2")) return false;
    fstring_format(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (FSTRING_FORMAT_SEP | FSTRING_DEBUG_EQ) FSTRING_FORMAT_SPEC?
  public static boolean fstring_format(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_format")) return false;
    if (!nextTokenIs(builder_, "<fstring format>", FSTRING_DEBUG_EQ, FSTRING_FORMAT_SEP)) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FSTRING_FORMAT, "<fstring format>");
    result_ = fstring_format_0(builder_, level_ + 1);
    pinned_ = result_; // pin = 1
    result_ = result_ && fstring_format_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, null);
    return result_ || pinned_;
  }

  // FSTRING_FORMAT_SEP | FSTRING_DEBUG_EQ
  private static boolean fstring_format_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_format_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, FSTRING_FORMAT_SEP);
    if (!result_) result_ = consumeToken(builder_, FSTRING_DEBUG_EQ);
    return result_;
  }

  // FSTRING_FORMAT_SPEC?
  private static boolean fstring_format_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_format_1")) return false;
    consumeToken(builder_, FSTRING_FORMAT_SPEC);
    return true;
  }

  /* ********************************************************** */
  // FSTRING_BEGIN fstring_content* FSTRING_END
  public static boolean fstring_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_literal")) return false;
    if (!nextTokenIs(builder_, FSTRING_BEGIN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FSTRING_BEGIN);
    result_ = result_ && fstring_literal_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, FSTRING_END);
    exit_section_(builder_, marker_, FSTRING_LITERAL, result_);
    return result_;
  }

  // fstring_content*
  private static boolean fstring_literal_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "fstring_literal_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!fstring_content(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "fstring_literal_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // unfinished_function_body END_STATEMENT_BLOCK?
  public static boolean function_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unfinished_function_body(builder_, level_ + 1);
    result_ = result_ && function_body_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, FUNCTION_BODY, result_);
    return result_;
  }

  // END_STATEMENT_BLOCK?
  private static boolean function_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_body_1")) return false;
    consumeToken(builder_, END_STATEMENT_BLOCK);
    return true;
  }

  /* ********************************************************** */
  // function_signature function_body?
  public static boolean function_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNCTION_DECL, "<function decl>");
    result_ = function_signature(builder_, level_ + 1);
    result_ = result_ && function_decl_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // function_body?
  private static boolean function_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_decl_1")) return false;
    function_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // CONST | FINAL | OVERRIDE | PROPERTY
  public static boolean function_qualifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_qualifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNCTION_QUALIFIER, "<function qualifier>");
    result_ = consumeToken(builder_, CONST);
    if (!result_) result_ = consumeToken(builder_, FINAL);
    if (!result_) result_ = consumeToken(builder_, OVERRIDE);
    if (!result_) result_ = consumeToken(builder_, PROPERTY);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // function_qualifier+
  public static boolean function_qualifiers(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_qualifiers")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNCTION_QUALIFIERS, "<function qualifiers>");
    result_ = function_qualifier(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!function_qualifier(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "function_qualifiers", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // return_type IDENTIFIER OPEN_PARENTHESIS parameter_list CLOSE_PARENTHESIS function_qualifiers?
  public static boolean function_signature(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_signature")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNCTION_SIGNATURE, "<function signature>");
    result_ = return_type(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENTIFIER, OPEN_PARENTHESIS);
    result_ = result_ && parameter_list(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && function_signature_5(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // function_qualifiers?
  private static boolean function_signature_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "function_signature_5")) return false;
    function_qualifiers(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // ufunction_macro? (MIXIN | LOCAL)? function_decl
  public static boolean global_function_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_function_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, GLOBAL_FUNCTION_DECL, "<global function decl>");
    result_ = global_function_decl_0(builder_, level_ + 1);
    result_ = result_ && global_function_decl_1(builder_, level_ + 1);
    result_ = result_ && function_decl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ufunction_macro?
  private static boolean global_function_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_function_decl_0")) return false;
    ufunction_macro(builder_, level_ + 1);
    return true;
  }

  // (MIXIN | LOCAL)?
  private static boolean global_function_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_function_decl_1")) return false;
    global_function_decl_1_0(builder_, level_ + 1);
    return true;
  }

  // MIXIN | LOCAL
  private static boolean global_function_decl_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "global_function_decl_1_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, MIXIN);
    if (!result_) result_ = consumeToken(builder_, LOCAL);
    return result_;
  }

  /* ********************************************************** */
  // SCOPE
  static boolean global_scope_prefix(PsiBuilder builder_, int level_) {
    return consumeToken(builder_, SCOPE);
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean identifier_reference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "identifier_reference")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, marker_, IDENTIFIER_REFERENCE, result_);
    return result_;
  }

  /* ********************************************************** */
  // IF OPEN_PARENTHESIS expr CLOSE_PARENTHESIS statement else_clause?
  public static boolean if_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_statement")) return false;
    if (!nextTokenIs(builder_, IF)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, IF, OPEN_PARENTHESIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && statement(builder_, level_ + 1);
    result_ = result_ && if_statement_5(builder_, level_ + 1);
    exit_section_(builder_, marker_, IF_STATEMENT, result_);
    return result_;
  }

  // else_clause?
  private static boolean if_statement_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "if_statement_5")) return false;
    else_clause(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // macro_identifier macro_value?
  public static boolean macro_argument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_argument")) return false;
    if (!nextTokenIs(builder_, "<macro argument>", IDENTIFIER, STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_ARGUMENT, "<macro argument>");
    result_ = macro_identifier(builder_, level_ + 1);
    result_ = result_ && macro_argument_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // macro_value?
  private static boolean macro_argument_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_argument_1")) return false;
    macro_value(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // macro_argument (LIST_SEPARATOR macro_argument)* LIST_SEPARATOR?
  public static boolean macro_arguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_arguments")) return false;
    if (!nextTokenIs(builder_, "<macro arguments>", IDENTIFIER, STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_ARGUMENTS, "<macro arguments>");
    result_ = macro_argument(builder_, level_ + 1);
    result_ = result_ && macro_arguments_1(builder_, level_ + 1);
    result_ = result_ && macro_arguments_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (LIST_SEPARATOR macro_argument)*
  private static boolean macro_arguments_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_arguments_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!macro_arguments_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "macro_arguments_1", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR macro_argument
  private static boolean macro_arguments_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_arguments_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && macro_argument(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LIST_SEPARATOR?
  private static boolean macro_arguments_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_arguments_2")) return false;
    consumeToken(builder_, LIST_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER | STRING
  public static boolean macro_identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_identifier")) return false;
    if (!nextTokenIs(builder_, "<macro identifier>", IDENTIFIER, STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_IDENTIFIER, "<macro identifier>");
    result_ = consumeToken(builder_, IDENTIFIER);
    if (!result_) result_ = consumeToken(builder_, STRING);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER (BIT_OR IDENTIFIER)+
  public static boolean macro_pipe_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_pipe_list")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    result_ = result_ && macro_pipe_list_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, MACRO_PIPE_LIST, result_);
    return result_;
  }

  // (BIT_OR IDENTIFIER)+
  private static boolean macro_pipe_list_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_pipe_list_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = macro_pipe_list_1_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!macro_pipe_list_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "macro_pipe_list_1", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // BIT_OR IDENTIFIER
  private static boolean macro_pipe_list_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_pipe_list_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, BIT_OR, IDENTIFIER);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // ASSIGNMENT macro_value_expr
  //   | ASSIGNMENT OPEN_PARENTHESIS macro_arguments CLOSE_PARENTHESIS
  public static boolean macro_value(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_value")) return false;
    if (!nextTokenIs(builder_, ASSIGNMENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = macro_value_0(builder_, level_ + 1);
    if (!result_) result_ = macro_value_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, MACRO_VALUE, result_);
    return result_;
  }

  // ASSIGNMENT macro_value_expr
  private static boolean macro_value_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_value_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ASSIGNMENT);
    result_ = result_ && macro_value_expr(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ASSIGNMENT OPEN_PARENTHESIS macro_arguments CLOSE_PARENTHESIS
  private static boolean macro_value_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_value_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ASSIGNMENT, OPEN_PARENTHESIS);
    result_ = result_ && macro_arguments(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER | NUMBER | STRING | TRUE | FALSE | NULLPTR | macro_pipe_list
  public static boolean macro_value_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "macro_value_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MACRO_VALUE_EXPR, "<macro value expr>");
    result_ = consumeToken(builder_, IDENTIFIER);
    if (!result_) result_ = consumeToken(builder_, NUMBER);
    if (!result_) result_ = consumeToken(builder_, STRING);
    if (!result_) result_ = consumeToken(builder_, TRUE);
    if (!result_) result_ = consumeToken(builder_, FALSE);
    if (!result_) result_ = consumeToken(builder_, NULLPTR);
    if (!result_) result_ = macro_pipe_list(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // STAR | SLASH | PERCENT
  static boolean multiplicative_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "multiplicative_op")) return false;
    boolean result_;
    result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = consumeToken(builder_, SLASH);
    if (!result_) result_ = consumeToken(builder_, PERCENT);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON expr
  public static boolean named_argument(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "named_argument")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, IDENTIFIER, COLON);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, NAMED_ARGUMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // unfinished_namespace_body END_STATEMENT_BLOCK?
  public static boolean namespace_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespace_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unfinished_namespace_body(builder_, level_ + 1);
    result_ = result_ && namespace_body_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, NAMESPACE_BODY, result_);
    return result_;
  }

  // END_STATEMENT_BLOCK?
  private static boolean namespace_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespace_body_1")) return false;
    consumeToken(builder_, END_STATEMENT_BLOCK);
    return true;
  }

  /* ********************************************************** */
  // NAMESPACE scoped_identifier namespace_body?
  public static boolean namespace_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespace_decl")) return false;
    if (!nextTokenIs(builder_, NAMESPACE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NAMESPACE);
    result_ = result_ && scoped_identifier(builder_, level_ + 1);
    result_ = result_ && namespace_decl_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, NAMESPACE_DECL, result_);
    return result_;
  }

  // namespace_body?
  private static boolean namespace_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespace_decl_2")) return false;
    namespace_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // NAMESTRING_BEGIN NAMESTRING_TEXT? NAMESTRING_END
  public static boolean namestring_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namestring_literal")) return false;
    if (!nextTokenIs(builder_, NAMESTRING_BEGIN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, NAMESTRING_BEGIN);
    result_ = result_ && namestring_literal_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, NAMESTRING_END);
    exit_section_(builder_, marker_, NAMESTRING_LITERAL, result_);
    return result_;
  }

  // NAMESTRING_TEXT?
  private static boolean namestring_literal_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namestring_literal_1")) return false;
    consumeToken(builder_, NAMESTRING_TEXT);
    return true;
  }

  /* ********************************************************** */
  // !(typename IDENTIFIER OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS END_STATEMENT)
  static boolean not_constructor_variable(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_constructor_variable")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NOT_);
    result_ = !not_constructor_variable_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // typename IDENTIFIER OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS END_STATEMENT
  private static boolean not_constructor_variable_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_constructor_variable_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = typename(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, IDENTIFIER, OPEN_PARENTHESIS);
    result_ = result_ && not_constructor_variable_0_3(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, CLOSE_PARENTHESIS, END_STATEMENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // argument_list?
  private static boolean not_constructor_variable_0_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "not_constructor_variable_0_3")) return false;
    argument_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // typename ref_qualifier? IDENTIFIER? default_parameter_value?
  public static boolean parameter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAMETER, "<parameter>");
    result_ = typename(builder_, level_ + 1);
    result_ = result_ && parameter_1(builder_, level_ + 1);
    result_ = result_ && parameter_2(builder_, level_ + 1);
    result_ = result_ && parameter_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ref_qualifier?
  private static boolean parameter_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_1")) return false;
    ref_qualifier(builder_, level_ + 1);
    return true;
  }

  // IDENTIFIER?
  private static boolean parameter_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_2")) return false;
    consumeToken(builder_, IDENTIFIER);
    return true;
  }

  // default_parameter_value?
  private static boolean parameter_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_3")) return false;
    default_parameter_value(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // (parameter (LIST_SEPARATOR parameter)* LIST_SEPARATOR?)?
  public static boolean parameter_list(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PARAMETER_LIST, "<parameter list>");
    parameter_list_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  // parameter (LIST_SEPARATOR parameter)* LIST_SEPARATOR?
  private static boolean parameter_list_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = parameter(builder_, level_ + 1);
    result_ = result_ && parameter_list_0_1(builder_, level_ + 1);
    result_ = result_ && parameter_list_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (LIST_SEPARATOR parameter)*
  private static boolean parameter_list_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_0_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!parameter_list_0_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "parameter_list_0_1", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR parameter
  private static boolean parameter_list_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && parameter(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // LIST_SEPARATOR?
  private static boolean parameter_list_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "parameter_list_0_2")) return false;
    consumeToken(builder_, LIST_SEPARATOR);
    return true;
  }

  /* ********************************************************** */
  // OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS  // Function call
  //   | OPEN_BRACKET expr CLOSE_BRACKET                     // Array subscript
  //   | INC                                                 // Post-increment
  //   | DEC
  static boolean postfix_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfix_op")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = postfix_op_0(builder_, level_ + 1);
    if (!result_) result_ = postfix_op_1(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, INC);
    if (!result_) result_ = consumeToken(builder_, DEC);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS
  private static boolean postfix_op_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfix_op_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OPEN_PARENTHESIS);
    result_ = result_ && postfix_op_0_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // argument_list?
  private static boolean postfix_op_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfix_op_0_1")) return false;
    argument_list(builder_, level_ + 1);
    return true;
  }

  // OPEN_BRACKET expr CLOSE_BRACKET
  private static boolean postfix_op_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfix_op_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OPEN_BRACKET);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_BRACKET);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // VOID | BOOL | AUTO
  //   | INT | INT8 | INT16 | INT32 | INT64
  //   | UINT | UINT8 | UINT16 | UINT32 | UINT64
  //   | FLOAT | FLOAT32 | FLOAT64 | DOUBLE
  public static boolean primitive_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primitive_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PRIMITIVE_TYPE, "<primitive type>");
    result_ = consumeToken(builder_, VOID);
    if (!result_) result_ = consumeToken(builder_, BOOL);
    if (!result_) result_ = consumeToken(builder_, AUTO);
    if (!result_) result_ = consumeToken(builder_, INT);
    if (!result_) result_ = consumeToken(builder_, INT8);
    if (!result_) result_ = consumeToken(builder_, INT16);
    if (!result_) result_ = consumeToken(builder_, INT32);
    if (!result_) result_ = consumeToken(builder_, INT64);
    if (!result_) result_ = consumeToken(builder_, UINT);
    if (!result_) result_ = consumeToken(builder_, UINT8);
    if (!result_) result_ = consumeToken(builder_, UINT16);
    if (!result_) result_ = consumeToken(builder_, UINT32);
    if (!result_) result_ = consumeToken(builder_, UINT64);
    if (!result_) result_ = consumeToken(builder_, FLOAT);
    if (!result_) result_ = consumeToken(builder_, FLOAT32);
    if (!result_) result_ = consumeToken(builder_, FLOAT64);
    if (!result_) result_ = consumeToken(builder_, DOUBLE);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // AMP (OUT | IN | INOUT)?
  public static boolean ref_qualifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_qualifier")) return false;
    if (!nextTokenIs(builder_, AMP)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, AMP);
    result_ = result_ && ref_qualifier_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, REF_QUALIFIER, result_);
    return result_;
  }

  // (OUT | IN | INOUT)?
  private static boolean ref_qualifier_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_qualifier_1")) return false;
    ref_qualifier_1_0(builder_, level_ + 1);
    return true;
  }

  // OUT | IN | INOUT
  private static boolean ref_qualifier_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ref_qualifier_1_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, OUT);
    if (!result_) result_ = consumeToken(builder_, IN);
    if (!result_) result_ = consumeToken(builder_, INOUT);
    return result_;
  }

  /* ********************************************************** */
  // LESS_THAN | GREATER_THAN | LESS_THAN_OR_EQUAL | GREATER_THAN_OR_EQUAL
  static boolean relational_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "relational_op")) return false;
    boolean result_;
    result_ = consumeToken(builder_, LESS_THAN);
    if (!result_) result_ = consumeToken(builder_, GREATER_THAN);
    if (!result_) result_ = consumeToken(builder_, LESS_THAN_OR_EQUAL);
    if (!result_) result_ = consumeToken(builder_, GREATER_THAN_OR_EQUAL);
    return result_;
  }

  /* ********************************************************** */
  // RETURN expr? END_STATEMENT
  public static boolean return_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "return_statement")) return false;
    if (!nextTokenIs(builder_, RETURN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, RETURN);
    result_ = result_ && return_statement_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, marker_, RETURN_STATEMENT, result_);
    return result_;
  }

  // expr?
  private static boolean return_statement_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "return_statement_1")) return false;
    expr(builder_, level_ + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // VOID | typename
  public static boolean return_type(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "return_type")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RETURN_TYPE, "<return type>");
    result_ = consumeToken(builder_, VOID);
    if (!result_) result_ = typename(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // identifier_reference scope_segment*
  static boolean scope_name_chain(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scope_name_chain")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = identifier_reference(builder_, level_ + 1);
    result_ = result_ && scope_name_chain_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // scope_segment*
  private static boolean scope_name_chain_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scope_name_chain_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!scope_segment(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "scope_name_chain_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SCOPE identifier_reference
  public static boolean scope_segment(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scope_segment")) return false;
    if (!nextTokenIs(builder_, SCOPE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _LEFT_, SCOPE_RESOLUTION, null);
    result_ = consumeToken(builder_, SCOPE);
    result_ = result_ && identifier_reference(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // global_scope_prefix? scope_name_chain
  public static boolean scoped_identifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scoped_identifier")) return false;
    if (!nextTokenIs(builder_, "<scoped identifier>", IDENTIFIER, SCOPE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SCOPED_IDENTIFIER, "<scoped identifier>");
    result_ = scoped_identifier_0(builder_, level_ + 1);
    result_ = result_ && scope_name_chain(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // global_scope_prefix?
  private static boolean scoped_identifier_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "scoped_identifier_0")) return false;
    global_scope_prefix(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // script_item*
  static boolean script(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "script")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!script_item(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "script", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // namespace_decl        // BNF:1 NAMESPACE
  //   | class_decl            // BNF:1 CLASS
  //   | struct_decl           // BNF:1 STRUCT (variant of CLASS)
  //   | enum_decl             // BNF:1 ENUM
  //   | global_function_decl  // BNF:1 FUNC (with optional UFUNCTION macro)
  //   | variable_decl         // BNF:1 VAR
  //   // Unreal-specific declarations (not in base AngelScript):
  //   | delegate_decl         // Unreal extension
  //   | event_decl            // Unreal extension
  //   | asset_decl            // Unreal extension
  //   | access_decl           // Unreal extension
  //   | END_STATEMENT
  static boolean script_item(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "script_item")) return false;
    boolean result_;
    result_ = namespace_decl(builder_, level_ + 1);
    if (!result_) result_ = class_decl(builder_, level_ + 1);
    if (!result_) result_ = struct_decl(builder_, level_ + 1);
    if (!result_) result_ = enum_decl(builder_, level_ + 1);
    if (!result_) result_ = global_function_decl(builder_, level_ + 1);
    if (!result_) result_ = variable_decl(builder_, level_ + 1);
    if (!result_) result_ = delegate_decl(builder_, level_ + 1);
    if (!result_) result_ = event_decl(builder_, level_ + 1);
    if (!result_) result_ = asset_decl(builder_, level_ + 1);
    if (!result_) result_ = access_decl(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, END_STATEMENT);
    return result_;
  }

  /* ********************************************************** */
  // BIT_SHIFT_LEFT | shift_right_arith | shift_right_logical
  static boolean shift_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "shift_op")) return false;
    if (!nextTokenIs(builder_, "", BIT_SHIFT_LEFT, GREATER_THAN)) return false;
    boolean result_;
    result_ = consumeToken(builder_, BIT_SHIFT_LEFT);
    if (!result_) result_ = shift_right_arith(builder_, level_ + 1);
    if (!result_) result_ = shift_right_logical(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // GREATER_THAN GREATER_THAN GREATER_THAN
  static boolean shift_right_arith(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "shift_right_arith")) return false;
    if (!nextTokenIs(builder_, GREATER_THAN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, GREATER_THAN, GREATER_THAN, GREATER_THAN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // GREATER_THAN GREATER_THAN
  static boolean shift_right_logical(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "shift_right_logical")) return false;
    if (!nextTokenIs(builder_, GREATER_THAN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, GREATER_THAN, GREATER_THAN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // if_statement              // BNF:8 IF
  //   | while_statement           // BNF:8 WHILE
  //   | for_statement             // BNF:8 FOR
  //   | foreach_statement         // Unreal extension (foreach loop)
  //   | switch_statement          // BNF:8 SWITCH
  //   | return_statement          // BNF:8 RETURN
  //   | break_statement           // BNF:8 BREAK
  //   | continue_statement        // BNF:8 CONTINUE
  //   | fallthrough_statement     // Unreal extension (explicit fallthrough)
  //   | statement_block           // BNF:2 STATBLOCK
  //   | variable_decl             // BNF:1 VAR (allowed in statement context)
  //   | expression_statement      // BNF:8 EXPRSTAT
  //   | END_STATEMENT
  public static boolean statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STATEMENT, "<statement>");
    result_ = if_statement(builder_, level_ + 1);
    if (!result_) result_ = while_statement(builder_, level_ + 1);
    if (!result_) result_ = for_statement(builder_, level_ + 1);
    if (!result_) result_ = foreach_statement(builder_, level_ + 1);
    if (!result_) result_ = switch_statement(builder_, level_ + 1);
    if (!result_) result_ = return_statement(builder_, level_ + 1);
    if (!result_) result_ = break_statement(builder_, level_ + 1);
    if (!result_) result_ = continue_statement(builder_, level_ + 1);
    if (!result_) result_ = fallthrough_statement(builder_, level_ + 1);
    if (!result_) result_ = statement_block(builder_, level_ + 1);
    if (!result_) result_ = variable_decl(builder_, level_ + 1);
    if (!result_) result_ = expression_statement(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // unfinished_statement_block END_STATEMENT_BLOCK?
  public static boolean statement_block(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_block")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unfinished_statement_block(builder_, level_ + 1);
    result_ = result_ && statement_block_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, STATEMENT_BLOCK, result_);
    return result_;
  }

  // END_STATEMENT_BLOCK?
  private static boolean statement_block_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "statement_block_1")) return false;
    consumeToken(builder_, END_STATEMENT_BLOCK);
    return true;
  }

  /* ********************************************************** */
  // STRING
  public static boolean string_literal(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "string_literal")) return false;
    if (!nextTokenIs(builder_, STRING)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, STRING);
    exit_section_(builder_, marker_, STRING_LITERAL, result_);
    return result_;
  }

  /* ********************************************************** */
  // unfinished_struct_body END_STATEMENT_BLOCK?
  public static boolean struct_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_body")) return false;
    if (!nextTokenIs(builder_, START_STATEMENT_BLOCK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = unfinished_struct_body(builder_, level_ + 1);
    result_ = result_ && struct_body_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, STRUCT_BODY, result_);
    return result_;
  }

  // END_STATEMENT_BLOCK?
  private static boolean struct_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_body_1")) return false;
    consumeToken(builder_, END_STATEMENT_BLOCK);
    return true;
  }

  /* ********************************************************** */
  // ustruct_macro? STRUCT IDENTIFIER struct_body?
  public static boolean struct_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_decl")) return false;
    if (!nextTokenIs(builder_, "<struct decl>", STRUCT, USTRUCT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_DECL, "<struct decl>");
    result_ = struct_decl_0(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, STRUCT, IDENTIFIER);
    result_ = result_ && struct_decl_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // ustruct_macro?
  private static boolean struct_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_decl_0")) return false;
    ustruct_macro(builder_, level_ + 1);
    return true;
  }

  // struct_body?
  private static boolean struct_decl_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_decl_3")) return false;
    struct_body(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // constructor_decl             // Must come before class_method_decl to avoid ambiguity (no return type)
  //   | destructor_decl              // Destructor (must come before class_method_decl to avoid ambiguity)
  //   | class_method_decl            // Struct methods with optional access specifiers (same as class methods)
  //   | struct_property_decl
  public static boolean struct_member(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_member")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_MEMBER, "<struct member>");
    result_ = constructor_decl(builder_, level_ + 1);
    if (!result_) result_ = destructor_decl(builder_, level_ + 1);
    if (!result_) result_ = class_method_decl(builder_, level_ + 1);
    if (!result_) result_ = struct_property_decl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // access_specifier? uproperty_macro? access_specifier? variable_decl
  public static boolean struct_property_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_property_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, STRUCT_PROPERTY_DECL, "<struct property decl>");
    result_ = struct_property_decl_0(builder_, level_ + 1);
    result_ = result_ && struct_property_decl_1(builder_, level_ + 1);
    result_ = result_ && struct_property_decl_2(builder_, level_ + 1);
    result_ = result_ && variable_decl(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // access_specifier?
  private static boolean struct_property_decl_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_property_decl_0")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  // uproperty_macro?
  private static boolean struct_property_decl_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_property_decl_1")) return false;
    uproperty_macro(builder_, level_ + 1);
    return true;
  }

  // access_specifier?
  private static boolean struct_property_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "struct_property_decl_2")) return false;
    access_specifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // SWITCH OPEN_PARENTHESIS expr CLOSE_PARENTHESIS START_STATEMENT_BLOCK case_clause* END_STATEMENT_BLOCK
  public static boolean switch_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_statement")) return false;
    if (!nextTokenIs(builder_, SWITCH)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, SWITCH, OPEN_PARENTHESIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeTokens(builder_, 0, CLOSE_PARENTHESIS, START_STATEMENT_BLOCK);
    result_ = result_ && switch_statement_5(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT_BLOCK);
    exit_section_(builder_, marker_, SWITCH_STATEMENT, result_);
    return result_;
  }

  // case_clause*
  private static boolean switch_statement_5(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "switch_statement_5")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!case_clause(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "switch_statement_5", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LESS_THAN typename (LIST_SEPARATOR typename)* GREATER_THAN
  public static boolean template_arguments(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "template_arguments")) return false;
    if (!nextTokenIs(builder_, LESS_THAN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LESS_THAN);
    result_ = result_ && typename(builder_, level_ + 1);
    result_ = result_ && template_arguments_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, GREATER_THAN);
    exit_section_(builder_, marker_, TEMPLATE_ARGUMENTS, result_);
    return result_;
  }

  // (LIST_SEPARATOR typename)*
  private static boolean template_arguments_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "template_arguments_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!template_arguments_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "template_arguments_2", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR typename
  private static boolean template_arguments_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "template_arguments_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && typename(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // CONST? (primitive_type | scoped_identifier) template_arguments? ref_qualifier?
  public static boolean typename(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typename")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, TYPENAME, "<typename>");
    result_ = typename_0(builder_, level_ + 1);
    result_ = result_ && typename_1(builder_, level_ + 1);
    result_ = result_ && typename_2(builder_, level_ + 1);
    result_ = result_ && typename_3(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // CONST?
  private static boolean typename_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typename_0")) return false;
    consumeToken(builder_, CONST);
    return true;
  }

  // primitive_type | scoped_identifier
  private static boolean typename_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typename_1")) return false;
    boolean result_;
    result_ = primitive_type(builder_, level_ + 1);
    if (!result_) result_ = scoped_identifier(builder_, level_ + 1);
    return result_;
  }

  // template_arguments?
  private static boolean typename_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typename_2")) return false;
    template_arguments(builder_, level_ + 1);
    return true;
  }

  // ref_qualifier?
  private static boolean typename_3(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "typename_3")) return false;
    ref_qualifier(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // UCLASS OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean uclass_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uclass_macro")) return false;
    if (!nextTokenIs(builder_, UCLASS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, UCLASS, OPEN_PARENTHESIS);
    result_ = result_ && uclass_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, UCLASS_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean uclass_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uclass_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // UENUM OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean uenum_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uenum_macro")) return false;
    if (!nextTokenIs(builder_, UENUM)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, UENUM, OPEN_PARENTHESIS);
    result_ = result_ && uenum_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, UENUM_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean uenum_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uenum_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // UFUNCTION OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean ufunction_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ufunction_macro")) return false;
    if (!nextTokenIs(builder_, UFUNCTION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, UFUNCTION, OPEN_PARENTHESIS);
    result_ = result_ && ufunction_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, UFUNCTION_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean ufunction_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ufunction_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // UMETA OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean umeta_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "umeta_macro")) return false;
    if (!nextTokenIs(builder_, UMETA)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, UMETA, OPEN_PARENTHESIS);
    result_ = result_ && umeta_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, UMETA_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean umeta_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "umeta_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // PLUS | MINUS | NOT | BIT_NOT | INC | DEC
  static boolean unary_op(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_op")) return false;
    boolean result_;
    result_ = consumeToken(builder_, PLUS);
    if (!result_) result_ = consumeToken(builder_, MINUS);
    if (!result_) result_ = consumeToken(builder_, NOT);
    if (!result_) result_ = consumeToken(builder_, BIT_NOT);
    if (!result_) result_ = consumeToken(builder_, INC);
    if (!result_) result_ = consumeToken(builder_, DEC);
    return result_;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK class_member*
  static boolean unfinished_class_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_class_body")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    pinned_ = result_; // pin = 1
    result_ = result_ && unfinished_class_body_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, AngelScriptParser::UntilBraceRecover);
    return result_ || pinned_;
  }

  // class_member*
  private static boolean unfinished_class_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_class_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!class_member(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "unfinished_class_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK statement*
  static boolean unfinished_function_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_function_body")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    pinned_ = result_; // pin = 1
    result_ = result_ && unfinished_function_body_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, AngelScriptParser::UntilBraceRecover);
    return result_ || pinned_;
  }

  // statement*
  private static boolean unfinished_function_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_function_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "unfinished_function_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK script_item*
  static boolean unfinished_namespace_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_namespace_body")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    pinned_ = result_; // pin = 1
    result_ = result_ && unfinished_namespace_body_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, AngelScriptParser::UntilBraceRecover);
    return result_ || pinned_;
  }

  // script_item*
  private static boolean unfinished_namespace_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_namespace_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!script_item(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "unfinished_namespace_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK statement*
  static boolean unfinished_statement_block(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_statement_block")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    pinned_ = result_; // pin = 1
    result_ = result_ && unfinished_statement_block_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, AngelScriptParser::UntilBraceRecover);
    return result_ || pinned_;
  }

  // statement*
  private static boolean unfinished_statement_block_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_statement_block_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!statement(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "unfinished_statement_block_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // START_STATEMENT_BLOCK struct_member*
  static boolean unfinished_struct_body(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_struct_body")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_);
    result_ = consumeToken(builder_, START_STATEMENT_BLOCK);
    pinned_ = result_; // pin = 1
    result_ = result_ && unfinished_struct_body_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, pinned_, AngelScriptParser::UntilBraceRecover);
    return result_ || pinned_;
  }

  // struct_member*
  private static boolean unfinished_struct_body_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unfinished_struct_body_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!struct_member(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "unfinished_struct_body_1", pos_)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMENT | UNKNOWN
  //   | PP_TEXT | PP_IDENTIFIER
  //   // F-string tokens
  //   | FSTRING_ESCAPED_LBRACE | FSTRING_ESCAPED_RBRACE
  //   | FSTRING_TEXT | FSTRING_BEGIN | FSTRING_END
  //   | FSTRING_EXPR_BEGIN | FSTRING_EXPR_END
  //   // Name string tokens
  //   | NAMESTRING_BEGIN | NAMESTRING_END | NAMESTRING_TEXT
  //   // Preprocessor keywords
  //   | PP_IF | PP_ELIF | PP_ELSE | PP_ENDIF | PP_DEFINE | PP_UNDEF
  //   | PP_EDITOR | PP_TEST
  //   // All other keywords and tokens
  //   | HASH | STAR | VARIADIC | XOR | FROM
  static boolean unused_tokens(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unused_tokens")) return false;
    boolean result_;
    result_ = consumeToken(builder_, COMMENT);
    if (!result_) result_ = consumeToken(builder_, UNKNOWN);
    if (!result_) result_ = consumeToken(builder_, PP_TEXT);
    if (!result_) result_ = consumeToken(builder_, PP_IDENTIFIER);
    if (!result_) result_ = consumeToken(builder_, FSTRING_ESCAPED_LBRACE);
    if (!result_) result_ = consumeToken(builder_, FSTRING_ESCAPED_RBRACE);
    if (!result_) result_ = consumeToken(builder_, FSTRING_TEXT);
    if (!result_) result_ = consumeToken(builder_, FSTRING_BEGIN);
    if (!result_) result_ = consumeToken(builder_, FSTRING_END);
    if (!result_) result_ = consumeToken(builder_, FSTRING_EXPR_BEGIN);
    if (!result_) result_ = consumeToken(builder_, FSTRING_EXPR_END);
    if (!result_) result_ = consumeToken(builder_, NAMESTRING_BEGIN);
    if (!result_) result_ = consumeToken(builder_, NAMESTRING_END);
    if (!result_) result_ = consumeToken(builder_, NAMESTRING_TEXT);
    if (!result_) result_ = consumeToken(builder_, PP_IF);
    if (!result_) result_ = consumeToken(builder_, PP_ELIF);
    if (!result_) result_ = consumeToken(builder_, PP_ELSE);
    if (!result_) result_ = consumeToken(builder_, PP_ENDIF);
    if (!result_) result_ = consumeToken(builder_, PP_DEFINE);
    if (!result_) result_ = consumeToken(builder_, PP_UNDEF);
    if (!result_) result_ = consumeToken(builder_, PP_EDITOR);
    if (!result_) result_ = consumeToken(builder_, PP_TEST);
    if (!result_) result_ = consumeToken(builder_, HASH);
    if (!result_) result_ = consumeToken(builder_, STAR);
    if (!result_) result_ = consumeToken(builder_, VARIADIC);
    if (!result_) result_ = consumeToken(builder_, XOR);
    if (!result_) result_ = consumeToken(builder_, FROM);
    return result_;
  }

  /* ********************************************************** */
  // UPROPERTY OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean uproperty_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uproperty_macro")) return false;
    if (!nextTokenIs(builder_, UPROPERTY)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, UPROPERTY, OPEN_PARENTHESIS);
    result_ = result_ && uproperty_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, UPROPERTY_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean uproperty_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "uproperty_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // USTRUCT OPEN_PARENTHESIS macro_arguments? CLOSE_PARENTHESIS
  public static boolean ustruct_macro(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ustruct_macro")) return false;
    if (!nextTokenIs(builder_, USTRUCT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, USTRUCT, OPEN_PARENTHESIS);
    result_ = result_ && ustruct_macro_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, USTRUCT_MACRO, result_);
    return result_;
  }

  // macro_arguments?
  private static boolean ustruct_macro_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ustruct_macro_2")) return false;
    macro_arguments(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // OPEN_PARENTHESIS argument_list? CLOSE_PARENTHESIS
  public static boolean variable_constructor(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_constructor")) return false;
    if (!nextTokenIs(builder_, OPEN_PARENTHESIS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OPEN_PARENTHESIS);
    result_ = result_ && variable_constructor_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, VARIABLE_CONSTRUCTOR, result_);
    return result_;
  }

  // argument_list?
  private static boolean variable_constructor_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_constructor_1")) return false;
    argument_list(builder_, level_ + 1);
    return true;
  }

  /* ********************************************************** */
  // typename variable_declarator (LIST_SEPARATOR variable_declarator)* END_STATEMENT
  public static boolean variable_decl(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_decl")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, VARIABLE_DECL, "<variable decl>");
    result_ = typename(builder_, level_ + 1);
    result_ = result_ && variable_declarator(builder_, level_ + 1);
    result_ = result_ && variable_decl_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, END_STATEMENT);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (LIST_SEPARATOR variable_declarator)*
  private static boolean variable_decl_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_decl_2")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!variable_decl_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "variable_decl_2", pos_)) break;
    }
    return true;
  }

  // LIST_SEPARATOR variable_declarator
  private static boolean variable_decl_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_decl_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LIST_SEPARATOR);
    result_ = result_ && variable_declarator(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER (variable_initializer | variable_constructor)?
  public static boolean variable_declarator(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_declarator")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    result_ = result_ && variable_declarator_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, VARIABLE_DECLARATOR, result_);
    return result_;
  }

  // (variable_initializer | variable_constructor)?
  private static boolean variable_declarator_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_declarator_1")) return false;
    variable_declarator_1_0(builder_, level_ + 1);
    return true;
  }

  // variable_initializer | variable_constructor
  private static boolean variable_declarator_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_declarator_1_0")) return false;
    boolean result_;
    result_ = variable_initializer(builder_, level_ + 1);
    if (!result_) result_ = variable_constructor(builder_, level_ + 1);
    return result_;
  }

  /* ********************************************************** */
  // ASSIGNMENT expr
  public static boolean variable_initializer(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "variable_initializer")) return false;
    if (!nextTokenIs(builder_, ASSIGNMENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, ASSIGNMENT);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, VARIABLE_INITIALIZER, result_);
    return result_;
  }

  /* ********************************************************** */
  // WHILE OPEN_PARENTHESIS expr CLOSE_PARENTHESIS statement
  public static boolean while_statement(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "while_statement")) return false;
    if (!nextTokenIs(builder_, WHILE)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, WHILE, OPEN_PARENTHESIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    result_ = result_ && statement(builder_, level_ + 1);
    exit_section_(builder_, marker_, WHILE_STATEMENT, result_);
    return result_;
  }

  /* ********************************************************** */
  // Expression root: expr
  // Operator priority table:
  // 0: BINARY(assign_expr)
  // 1: BINARY(ternary_expr)
  // 2: BINARY(logical_or_expr)
  // 3: BINARY(logical_and_expr)
  // 4: BINARY(bitwise_or_expr)
  // 5: BINARY(bitwise_xor_expr)
  // 6: BINARY(bitwise_and_expr)
  // 7: BINARY(equality_expr)
  // 8: BINARY(shift_expr)
  // 9: BINARY(relational_expr)
  // 10: BINARY(additive_expr)
  // 11: BINARY(multiplicative_expr)
  // 12: PREFIX(unary_expr)
  // 13: POSTFIX(member_access_expr)
  // 14: POSTFIX(postfix_expr)
  // 15: ATOM(primary_expr)
  public static boolean expr(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr")) return false;
    addVariant(builder_, "<expr>");
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<expr>");
    result_ = unary_expr(builder_, level_ + 1);
    if (!result_) result_ = primary_expr(builder_, level_ + 1);
    pinned_ = result_;
    result_ = result_ && expr_0(builder_, level_ + 1, priority_);
    exit_section_(builder_, level_, marker_, null, result_, pinned_, null);
    return result_ || pinned_;
  }

  public static boolean expr_0(PsiBuilder builder_, int level_, int priority_) {
    if (!recursion_guard_(builder_, level_, "expr_0")) return false;
    boolean result_ = true;
    while (true) {
      Marker marker_ = enter_section_(builder_, level_, _LEFT_, null);
      if (priority_ < 0 && assignment_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, -1);
        exit_section_(builder_, level_, marker_, ASSIGN_EXPR, result_, true, null);
      }
      else if (priority_ < 1 && consumeTokenSmart(builder_, QUESTION)) {
        result_ = report_error_(builder_, expr(builder_, level_, 1));
        result_ = ternary_expr_1(builder_, level_ + 1) && result_;
        exit_section_(builder_, level_, marker_, TERNARY_EXPR, result_, true, null);
      }
      else if (priority_ < 2 && consumeTokenSmart(builder_, OR)) {
        result_ = expr(builder_, level_, 2);
        exit_section_(builder_, level_, marker_, LOGICAL_OR_EXPR, result_, true, null);
      }
      else if (priority_ < 3 && consumeTokenSmart(builder_, AND)) {
        result_ = expr(builder_, level_, 3);
        exit_section_(builder_, level_, marker_, LOGICAL_AND_EXPR, result_, true, null);
      }
      else if (priority_ < 4 && consumeTokenSmart(builder_, BIT_OR)) {
        result_ = expr(builder_, level_, 4);
        exit_section_(builder_, level_, marker_, BITWISE_OR_EXPR, result_, true, null);
      }
      else if (priority_ < 5 && consumeTokenSmart(builder_, BIT_XOR)) {
        result_ = expr(builder_, level_, 5);
        exit_section_(builder_, level_, marker_, BITWISE_XOR_EXPR, result_, true, null);
      }
      else if (priority_ < 6 && consumeTokenSmart(builder_, AMP)) {
        result_ = expr(builder_, level_, 6);
        exit_section_(builder_, level_, marker_, BITWISE_AND_EXPR, result_, true, null);
      }
      else if (priority_ < 7 && equality_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 7);
        exit_section_(builder_, level_, marker_, EQUALITY_EXPR, result_, true, null);
      }
      else if (priority_ < 8 && shift_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 8);
        exit_section_(builder_, level_, marker_, SHIFT_EXPR, result_, true, null);
      }
      else if (priority_ < 9 && relational_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 9);
        exit_section_(builder_, level_, marker_, RELATIONAL_EXPR, result_, true, null);
      }
      else if (priority_ < 10 && additive_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 10);
        exit_section_(builder_, level_, marker_, ADDITIVE_EXPR, result_, true, null);
      }
      else if (priority_ < 11 && multiplicative_op(builder_, level_ + 1)) {
        result_ = expr(builder_, level_, 11);
        exit_section_(builder_, level_, marker_, MULTIPLICATIVE_EXPR, result_, true, null);
      }
      else if (priority_ < 13 && member_access_expr_0(builder_, level_ + 1)) {
        result_ = true;
        exit_section_(builder_, level_, marker_, MEMBER_ACCESS_EXPR, result_, true, null);
      }
      else if (priority_ < 14 && postfix_expr_0(builder_, level_ + 1)) {
        result_ = true;
        exit_section_(builder_, level_, marker_, POSTFIX_EXPR, result_, true, null);
      }
      else {
        exit_section_(builder_, level_, marker_, null, false, false, null);
        break;
      }
    }
    return result_;
  }

  // COLON expr
  private static boolean ternary_expr_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "ternary_expr_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, COLON);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  public static boolean unary_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "unary_expr")) return false;
    boolean result_, pinned_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, null);
    result_ = unary_op(builder_, level_ + 1);
    pinned_ = result_;
    result_ = pinned_ && expr(builder_, level_, 12);
    exit_section_(builder_, level_, marker_, UNARY_EXPR, result_, pinned_, null);
    return result_ || pinned_;
  }

  // DOT identifier_reference
  private static boolean member_access_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "member_access_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, DOT);
    result_ = result_ && identifier_reference(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // postfix_op+
  private static boolean postfix_expr_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "postfix_expr_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = postfix_op(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!postfix_op(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "postfix_expr_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // THIS                                                              // BNF: implicit
  //   | TRUE                                                              // BNF:12 LITERAL
  //   | FALSE                                                             // BNF:12 LITERAL
  //   | NULLPTR                                                           // BNF:12 LITERAL (null)
  //   | NUMBER                                                            // BNF:17 NUMBER
  //   | string_literal                                                    // BNF:17 STRING
  //   | fstring_literal                                                   // Unreal extension
  //   | namestring_literal                                                // Unreal extension
  //   | cast_expression                                                   // BNF:12 CAST
  //   | call_expression                                                   // BNF:11-12 CONSTRUCTCALL/FUNCCALL
  //   | scoped_identifier                                                 // BNF:12 VARACCESS
  //   | OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
  public static boolean primary_expr(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primary_expr")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, PRIMARY_EXPR, "<primary expr>");
    result_ = consumeTokenSmart(builder_, THIS);
    if (!result_) result_ = consumeTokenSmart(builder_, TRUE);
    if (!result_) result_ = consumeTokenSmart(builder_, FALSE);
    if (!result_) result_ = consumeTokenSmart(builder_, NULLPTR);
    if (!result_) result_ = consumeTokenSmart(builder_, NUMBER);
    if (!result_) result_ = string_literal(builder_, level_ + 1);
    if (!result_) result_ = fstring_literal(builder_, level_ + 1);
    if (!result_) result_ = namestring_literal(builder_, level_ + 1);
    if (!result_) result_ = cast_expression(builder_, level_ + 1);
    if (!result_) result_ = call_expression(builder_, level_ + 1);
    if (!result_) result_ = scoped_identifier(builder_, level_ + 1);
    if (!result_) result_ = primary_expr_11(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // OPEN_PARENTHESIS expr CLOSE_PARENTHESIS
  private static boolean primary_expr_11(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "primary_expr_11")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokenSmart(builder_, OPEN_PARENTHESIS);
    result_ = result_ && expr(builder_, level_ + 1, -1);
    result_ = result_ && consumeToken(builder_, CLOSE_PARENTHESIS);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}
