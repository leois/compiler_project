/*
 * JFlex specification for the lexical analyzer for a simple demo language.
 * Change this into the scanner for your implementation of MiniJava.
 * CSE 401/P501 Au11
 */


package Scanner;

import java_cup.runtime.*;
import Parser.sym;

%%

%public
%final
%class scanner
%unicode
%cup
%line
%column

/* Code copied into the generated scanner class.  */
/* Can be referenced in scanner action code. */
%{
  // Return new symbol objects with line and column numbers in the symbol 
  // left and right fields. This abuses the original idea of having left 
  // and right be character positions, but is   // is more useful and 
  // follows an example in the JFlex documentation.
  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }

  // Return a readable representation of symbol s (aka token)
  public String symbolToString(Symbol s) {
    String rep;
    switch (s.sym) {
      case sym.BECOMES: return "BECOMES";
      case sym.SEMICOLON: return "SEMICOLON";
      case sym.PLUS: return "PLUS";
      case sym.MINUS: return "MINUS";
      case sym.MULTIPLIER: return "MULTIPLIER";
      case sym.DIVIDER: return "DIVIDER";
      case sym.AND: return "AND";
      case sym.LESSTHAN: return "LESSTHAN";
      case sym.LPAREN: return "LPAREN";
      case sym.RPAREN: return "RPAREN";
      case sym.LBRACE: return "LBRACE";
      case sym.RBRACE: return "RBRACE";
      case sym.LBRACKET: return "LBRACKET";
      case sym.RBRACKET: return "RBRACKET";
      case sym.DISPLAY: return "DISPLAY";
      case sym.CLASS: return "CLASS";
	  case sym.PUBLIC: return "PUBLIC";
	  case sym.STATIC: return "STATIC";
	  case sym.VOID: return "VOID";
      case sym.MAIN: return "MAIN";
      case sym.EXTENDS: return "EXTENDS";
      case sym.RETURN: return "RETURN";
      case sym.STRING: return "STRING";
 	  case sym.BOOLEAN: return "BOOLEAN";
	  case sym.IF: return "IF";
	  case sym.ELSE: return "ELSE";
	  case sym.WHILE: return "WHILE";
	  case sym.DOT: return "DOT";
	  case sym.COMMA: return "COMMA";
	  case sym.LENGTH: return "LENGTH";
	  case sym.TRUE: return "TRUE";
	  case sym.FALSE: return "FALSE";
	  case sym.THIS: return "THIS";
	  case sym.NEW: return "NEW";
	  case sym.INT: return "INT";
	  case sym.PRINT: return "PRINT";
	  case sym.NOT: return "NOT";
      case sym.NUMBER: return "NUM(" + (Integer)s.value + ")";
      case sym.IDENTIFIER: return "ID(" + (String)s.value + ")";
      case sym.EOF: return "<EOF>";
      case sym.error: return "<ERROR>";
      default: return "<UNEXPECTED TOKEN " + s.toString() + ">";
    }
  }
%}

/* Helper definitions */
letter = [a-zA-Z]
digit = [0-9]
eol = [\r\n]
white = {eol}|[ \t]
numbers = 0 | [1-9][0-9]*
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Comment = {EndOfLineComment}
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
%%

/* Token definitions */

/* reserved words */
/* (put here so that reserved words take precedence over identifiers) */
"display" { return symbol(sym.DISPLAY); }
"class" { return symbol(sym.CLASS); }
"public" { return symbol(sym.PUBLIC); }
"static" { return symbol(sym.STATIC); }
"void" { return symbol(sym.VOID); }
"main" { return symbol(sym.MAIN); }
"extends" { return symbol(sym.EXTENDS); }
"return" { return symbol(sym.RETURN); }
"String" { return symbol(sym.STRING); }
"boolean" { return symbol(sym.BOOLEAN); }
"if" { return symbol(sym.IF); }
"else" { return symbol(sym.ELSE); }
"while" { return symbol(sym.WHILE); }
"length" { return symbol(sym.LENGTH); }
"true" { return symbol(sym.TRUE); }
"false" { return symbol(sym.FALSE); }
"this" { return symbol(sym.THIS); }
"new" { return symbol(sym.NEW); }
"int" { return symbol(sym.INT); }
"System.out.println" { return symbol(sym.PRINT); }


/* operators */
"+" { return symbol(sym.PLUS); }
"-" { return symbol(sym.MINUS); }
"*" { return symbol(sym.MULTIPLIER); }
"/" { return symbol(sym.DIVIDER); }
"=" { return symbol(sym.BECOMES); }

/* comparators */
"&&" { return symbol(sym.AND); }
"<" { return symbol(sym.LESSTHAN); }

/* delimiters */
"(" { return symbol(sym.LPAREN); }
")" { return symbol(sym.RPAREN); }
";" { return symbol(sym.SEMICOLON); }
"{" { return symbol(sym.LBRACE);}
"}" { return symbol(sym.RBRACE);}
"[" { return symbol(sym.LBRACKET);}
"]" { return symbol(sym.RBRACKET);}

/* special symbols */
"!" { return symbol(sym.NOT); }
"." { return symbol(sym.DOT); }
"," { return symbol(sym.COMMA); }

/* identifiers */
{letter} ({letter}|{digit}|_)* { return symbol(sym.IDENTIFIER, yytext()); }
{numbers} { return symbol(sym.NUMBER, Integer.valueOf(yytext())); }
{digit} ({digit})* { return symbol(sym.INT, yytext()); }

/* whitespace */
{white}+ { /* ignore whitespace */ }
{Comment} { /* ignore comment */ }

/* lexical errors (put last so other matches take precedence) */
. { System.err.println(
	"\nunexpected character in input: '" + yytext() + "' at line " +
	(yyline+1) + " column " + (yycolumn+1));
  }
