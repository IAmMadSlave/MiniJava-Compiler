// Skeleton MiniJava Lexical Analyzer Specification
package parse;


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

private errormsg.ErrorMsg errorMsg;
private java_cup.runtime.Symbol tok(int kind, Object value) {
  return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}
Yylex(java.io.InputStream s, errormsg.ErrorMsg e) {
  this(s);
  errorMsg=e;
}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		71
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NOT_ACCEPT,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NOT_ACCEPT,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NOT_ACCEPT,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NOT_ACCEPT,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"46:9,42,40,46:2,41,46:18,42,16,46:4,15,46,1,2,14,12,7,13,9,39,45:10,46,10,8" +
",11,46:3,43:18,25,43:7,5,46,6,46,44,46,18,35,38,37,21,17,34,33,30,43:2,19,2" +
"7,31,28,29,43,23,20,22,24,36,32,43,26,43,3,46,4,46:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,125,
"0,1:15,2,1,3,4,5,1,6,7,6:2,1,6:5,1,6:9,1,8,9,10,11,1,12,13,14,15,16,17,18,1" +
"9,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,4" +
"4,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,6" +
"9,70,71,72,73,74,75,76,77,78,79,6,80,81,82,83,84,85,86,87")[0];

	private int yy_nxt[][] = unpackFromString(88,47,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,116,117,118,93,94,119,116,120," +
"116,95,116,121,45,77,122,116:2,123,96,116,124,44,19,-1,19,116,47,20,47,-1:6" +
"2,21,-1:48,116,97,116:20,-1:4,116:3,-1:41,19,-1,19,-1:49,20,-1:18,116:22,-1" +
":4,116:3,-1:2,23:13,46,23:24,-1,23:7,-1,43:39,26,43:6,-1:14,23,-1:24,43,-1:" +
"24,22,116:13,48,116:7,-1:4,116:3,-1:40,32,-1:24,116:5,24,116:16,-1:4,116:3," +
"-1:29,51,-1:35,116:15,25,116:6,-1:4,116:3,-1:25,53,-1:39,116:4,27,116:17,-1" +
":4,116:3,-1:23,55,-1:41,116:4,28,116:17,-1:4,116:3,-1:10,57,-1:54,116:3,29," +
"116:18,-1:4,116:3,-1:30,59,-1:34,116:14,30,116:7,-1:4,116:3,-1:24,61,-1:40," +
"116:20,31,116,-1:4,116:3,-1:31,63,-1:33,116:4,33,116:17,-1:4,116:3,-1:32,65" +
",-1:32,116:4,34,116:17,-1:4,116:3,-1:23,67,-1:41,116:3,35,116:18,-1:4,116:3" +
",-1:20,69,-1:44,116:16,36,116:5,-1:4,116:3,-1:32,42,-1:32,116:21,37,-1:4,11" +
"6:3,-1,1,-1:63,116:14,38,116:7,-1:4,116:3,-1:18,116:17,39,116:4,-1:4,116:3," +
"-1:18,116:21,40,-1:4,116:3,-1:10,49,-1:7,116:22,-1:4,116:3,-1:18,116:14,41," +
"116:7,-1:4,116:3,-1:18,116:4,50,116:17,-1:4,116:3,-1:18,116:3,52,116:18,-1:" +
"4,116:3,-1:18,116:7,54,116:14,-1:4,116:3,-1:18,116:13,56,116:8,-1:4,116:3,-" +
"1:18,116:13,58,116:8,-1:4,116:3,-1:18,116:13,60,116:8,-1:4,116:3,-1:18,116:" +
"3,62,116:18,-1:4,116:3,-1:18,116:2,64,116:19,-1:4,116:3,-1:18,116:3,66,116:" +
"18,-1:4,116:3,-1:18,116:5,68,116:16,-1:4,116:3,-1:18,116:13,70,116:8,-1:4,1" +
"16:3,-1:18,116:6,72,116:15,-1:4,116:3,-1:18,116:14,73,116:7,-1:4,116:3,-1:1" +
"8,116:13,74,116:8,-1:4,116:3,-1:18,116:10,75,116:11,-1:4,116:3,-1:18,116,76" +
",116:20,-1:4,116:3,-1:18,116:2,78,116:19,-1:4,116:3,-1:18,116:6,79,116:9,80" +
",116:5,-1:4,116:3,-1:18,116,81,116:20,-1:4,116:3,-1:18,116:11,82,116:10,-1:" +
"4,116:3,-1:18,116:2,83,116:19,-1:4,116:3,-1:18,116:14,107,116:7,-1:4,116:3," +
"-1:18,116,108,116:20,-1:4,116:3,-1:18,116:5,109,116:16,-1:4,116:3,-1:18,116" +
":6,110,116:15,-1:4,116:3,-1:18,116:3,111,116:18,-1:4,116:3,-1:18,116:18,112" +
",116:3,-1:4,116:3,-1:18,116:13,84,116:8,-1:4,116:3,-1:18,116:11,113,116:10," +
"-1:4,116:3,-1:18,116,85,116:20,-1:4,116:3,-1:18,116:17,86,116:4,-1:4,116:3," +
"-1:18,116:5,87,116:16,-1:4,116:3,-1:18,116:7,88,116:14,-1:4,116:3,-1:18,116" +
":13,89,116:8,-1:4,116:3,-1:18,116:5,114,116:16,-1:4,116:3,-1:18,116:2,90,11" +
"6:19,-1:4,116:3,-1:18,116:2,115,116:19,-1:4,116:3,-1:18,116:4,91,116:17,-1:" +
"4,116:3,-1:18,116:4,92,116:17,-1:4,116:3,-1:18,116:4,98,116:17,-1:4,116:3,-" +
"1:18,116:5,99,116:16,-1:4,116:3,-1:18,116:4,100,116:17,-1:4,116:3,-1:18,116" +
":5,101,116:3,102,116:12,-1:4,116:3,-1:18,116:7,103,116:14,-1:4,116:3,-1:18," +
"116:16,104,116:5,-1:4,116:3,-1:18,116:11,105,116:10,-1:4,116:3,-1:18,116:2," +
"106,116:19,-1:4,116:3,-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

{
  return tok(sym.EOF, null);
}
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{return tok(sym.LPAREN, null);}
					case -3:
						break;
					case 3:
						{return tok(sym.RPAREN, null);}
					case -4:
						break;
					case 4:
						{return tok(sym.LBRACE, null);}
					case -5:
						break;
					case 5:
						{return tok(sym.RBRACE, null);}
					case -6:
						break;
					case 6:
						{return tok(sym.LBRACK, null);}
					case -7:
						break;
					case 7:
						{return tok(sym.RBRACK, null);}
					case -8:
						break;
					case 8:
						{return tok(sym.COMMA, null);}
					case -9:
						break;
					case 9:
						{return tok(sym.LT, null);}
					case -10:
						break;
					case 10:
						{return tok(sym.DOT, null);}
					case -11:
						break;
					case 11:
						{return tok(sym.SEMICOLON, null);}
					case -12:
						break;
					case 12:
						{return tok(sym.ASSIGN, null);}
					case -13:
						break;
					case 13:
						{return tok(sym.PLUS, null);}
					case -14:
						break;
					case 14:
						{return tok(sym.MINUS, null);}
					case -15:
						break;
					case 15:
						{return tok(sym.TIMES, null);}
					case -16:
						break;
					case 16:
						{errorMsg.error(yychar,
					            "unmatched input: " + yytext());}
					case -17:
						break;
					case 17:
						{return tok(sym.EXCLAMATION, null);}
					case -18:
						break;
					case 18:
						{return tok(sym.ID, yytext());}
					case -19:
						break;
					case 19:
						{ }
					case -20:
						break;
					case 20:
						{return tok(sym.INTEGER_LITERAL, yytext());}
					case -21:
						break;
					case 21:
						{return tok(sym.AND, null);}
					case -22:
						break;
					case 22:
						{return tok(sym.IF, null);}
					case -23:
						break;
					case 23:
						{errorMsg.error(yychar,
                                "unclosed comment...");}
					case -24:
						break;
					case 24:
						{return tok(sym.INT, null);}
					case -25:
						break;
					case 25:
						{return tok(sym.NEW, null);}
					case -26:
						break;
					case 26:
						{ }
					case -27:
						break;
					case 27:
						{return tok(sym.ELSE, null);}
					case -28:
						break;
					case 28:
						{return tok(sym.TRUE, null);}
					case -29:
						break;
					case 29:
						{return tok(sym.THIS, null);}
					case -30:
						break;
					case 30:
						{return tok(sym.MAIN, null);}
					case -31:
						break;
					case 31:
						{return tok(sym.MAIN, null);}
					case -32:
						break;
					case 32:
						{ }
					case -33:
						break;
					case 33:
						{return tok(sym.FALSE, null);}
					case -34:
						break;
					case 34:
						{return tok(sym.WHILE, null);}
					case -35:
						break;
					case 35:
						{return tok(sym.CLASS, null);}
					case -36:
						break;
					case 36:
						{return tok(sym.LENGTH, null);}
					case -37:
						break;
					case 37:
						{return tok(sym.STATIC, null);}
					case -38:
						break;
					case 38:
						{return tok(sym.RETURN, null);}
					case -39:
						break;
					case 39:
						{return tok(sym.STRING, null);}
					case -40:
						break;
					case 40:
						{return tok(sym.PUBLIC, null);}
					case -41:
						break;
					case 41:
						{return tok(sym.BOOLEAN, null);}
					case -42:
						break;
					case 42:
						{return tok(sym.PRINTLN, null);}
					case -43:
						break;
					case 44:
						{errorMsg.error(yychar,
					            "unmatched input: " + yytext());}
					case -44:
						break;
					case 45:
						{return tok(sym.ID, yytext());}
					case -45:
						break;
					case 47:
						{errorMsg.error(yychar,
					            "unmatched input: " + yytext());}
					case -46:
						break;
					case 48:
						{return tok(sym.ID, yytext());}
					case -47:
						break;
					case 50:
						{return tok(sym.ID, yytext());}
					case -48:
						break;
					case 52:
						{return tok(sym.ID, yytext());}
					case -49:
						break;
					case 54:
						{return tok(sym.ID, yytext());}
					case -50:
						break;
					case 56:
						{return tok(sym.ID, yytext());}
					case -51:
						break;
					case 58:
						{return tok(sym.ID, yytext());}
					case -52:
						break;
					case 60:
						{return tok(sym.ID, yytext());}
					case -53:
						break;
					case 62:
						{return tok(sym.ID, yytext());}
					case -54:
						break;
					case 64:
						{return tok(sym.ID, yytext());}
					case -55:
						break;
					case 66:
						{return tok(sym.ID, yytext());}
					case -56:
						break;
					case 68:
						{return tok(sym.ID, yytext());}
					case -57:
						break;
					case 70:
						{return tok(sym.ID, yytext());}
					case -58:
						break;
					case 72:
						{return tok(sym.ID, yytext());}
					case -59:
						break;
					case 73:
						{return tok(sym.ID, yytext());}
					case -60:
						break;
					case 74:
						{return tok(sym.ID, yytext());}
					case -61:
						break;
					case 75:
						{return tok(sym.ID, yytext());}
					case -62:
						break;
					case 76:
						{return tok(sym.ID, yytext());}
					case -63:
						break;
					case 77:
						{return tok(sym.ID, yytext());}
					case -64:
						break;
					case 78:
						{return tok(sym.ID, yytext());}
					case -65:
						break;
					case 79:
						{return tok(sym.ID, yytext());}
					case -66:
						break;
					case 80:
						{return tok(sym.ID, yytext());}
					case -67:
						break;
					case 81:
						{return tok(sym.ID, yytext());}
					case -68:
						break;
					case 82:
						{return tok(sym.ID, yytext());}
					case -69:
						break;
					case 83:
						{return tok(sym.ID, yytext());}
					case -70:
						break;
					case 84:
						{return tok(sym.ID, yytext());}
					case -71:
						break;
					case 85:
						{return tok(sym.ID, yytext());}
					case -72:
						break;
					case 86:
						{return tok(sym.ID, yytext());}
					case -73:
						break;
					case 87:
						{return tok(sym.ID, yytext());}
					case -74:
						break;
					case 88:
						{return tok(sym.ID, yytext());}
					case -75:
						break;
					case 89:
						{return tok(sym.ID, yytext());}
					case -76:
						break;
					case 90:
						{return tok(sym.ID, yytext());}
					case -77:
						break;
					case 91:
						{return tok(sym.ID, yytext());}
					case -78:
						break;
					case 92:
						{return tok(sym.ID, yytext());}
					case -79:
						break;
					case 93:
						{return tok(sym.ID, yytext());}
					case -80:
						break;
					case 94:
						{return tok(sym.ID, yytext());}
					case -81:
						break;
					case 95:
						{return tok(sym.ID, yytext());}
					case -82:
						break;
					case 96:
						{return tok(sym.ID, yytext());}
					case -83:
						break;
					case 97:
						{return tok(sym.ID, yytext());}
					case -84:
						break;
					case 98:
						{return tok(sym.ID, yytext());}
					case -85:
						break;
					case 99:
						{return tok(sym.ID, yytext());}
					case -86:
						break;
					case 100:
						{return tok(sym.ID, yytext());}
					case -87:
						break;
					case 101:
						{return tok(sym.ID, yytext());}
					case -88:
						break;
					case 102:
						{return tok(sym.ID, yytext());}
					case -89:
						break;
					case 103:
						{return tok(sym.ID, yytext());}
					case -90:
						break;
					case 104:
						{return tok(sym.ID, yytext());}
					case -91:
						break;
					case 105:
						{return tok(sym.ID, yytext());}
					case -92:
						break;
					case 106:
						{return tok(sym.ID, yytext());}
					case -93:
						break;
					case 107:
						{return tok(sym.ID, yytext());}
					case -94:
						break;
					case 108:
						{return tok(sym.ID, yytext());}
					case -95:
						break;
					case 109:
						{return tok(sym.ID, yytext());}
					case -96:
						break;
					case 110:
						{return tok(sym.ID, yytext());}
					case -97:
						break;
					case 111:
						{return tok(sym.ID, yytext());}
					case -98:
						break;
					case 112:
						{return tok(sym.ID, yytext());}
					case -99:
						break;
					case 113:
						{return tok(sym.ID, yytext());}
					case -100:
						break;
					case 114:
						{return tok(sym.ID, yytext());}
					case -101:
						break;
					case 115:
						{return tok(sym.ID, yytext());}
					case -102:
						break;
					case 116:
						{return tok(sym.ID, yytext());}
					case -103:
						break;
					case 117:
						{return tok(sym.ID, yytext());}
					case -104:
						break;
					case 118:
						{return tok(sym.ID, yytext());}
					case -105:
						break;
					case 119:
						{return tok(sym.ID, yytext());}
					case -106:
						break;
					case 120:
						{return tok(sym.ID, yytext());}
					case -107:
						break;
					case 121:
						{return tok(sym.ID, yytext());}
					case -108:
						break;
					case 122:
						{return tok(sym.ID, yytext());}
					case -109:
						break;
					case 123:
						{return tok(sym.ID, yytext());}
					case -110:
						break;
					case 124:
						{return tok(sym.ID, yytext());}
					case -111:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
