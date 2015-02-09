// Skeleton MiniJava Lexical Analyzer Specification

package parse;

%% 

%implements java_cup.runtime.Scanner
%function next_token
%type java_cup.runtime.Symbol
%char

%state COMMENT

%{
private errormsg.ErrorMsg errorMsg;

private java_cup.runtime.Symbol tok(int kind, Object value) {
  return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

Yylex(java.io.InputStream s, errormsg.ErrorMsg e) {
  this(s);
  errorMsg=e;
}

%}

%eofval{
{
  return tok(sym.EOF, null);
}
%eofval}       

%%

<YYINITIAL> class		{return tok(sym.CLASS, null);}
<YYINITIAL> [\ \t\n]+		{ }
<YYINITIAL> .			{errorMsg.error(yychar,
					"unmatched input: " + yytext());}

