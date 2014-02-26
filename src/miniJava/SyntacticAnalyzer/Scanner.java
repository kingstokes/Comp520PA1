package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

	InputStream inputStream = null;
	char currentChar;
	char previewedChar;
	char binop;
	String currentToken = "";
	boolean lookingForFirstCharOfToken;
	boolean lastWasSemicolon = false;
	boolean encounteredSemicolon = false;
	boolean encounteredLeftParen = false;
	boolean encounteredLeftBrace = false;
	boolean encounteredComma = false;
	boolean encounteredRightParen = false;
	boolean spaceDelimiter = false;
	boolean eot = false;

	boolean encounteredRightBrace = false;
	boolean encounteredLeftBracket = false;
	boolean encounteredRightBracket = false;
	boolean encounteredDot = false;
	boolean encounteredBINOP = false;
	boolean encounteredUNOP = false;

	boolean comments = false;

	boolean lookedAhead = false;

	public Scanner(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void ignoreMultiLineComment() {
		// this method is called by nextChar() in the event that a '/*'
		// has been encountered. The method will simply pull off all characters
		// until the stop sequence of '*/' is encountered.
		lookedAhead = false;
		boolean removingComments = true;
		char pluckedChar = ' ';
		System.out.println("ignoreMultiLineComment called...");
		int num = 0;
		while (removingComments) {
			try {
				num = inputStream.read();
				if (num < 0) {
					num = '$';
					eot = true;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			pluckedChar = (char) num;
			System.out.println("pluckedChar: " + pluckedChar);
			System.out.println("lookedAhead: " + lookedAhead);

			// System.out.println("");
			if (pluckedChar == '*') {
				char next = ' ';
				if (lookedAhead) {
					next = previewedChar;
					lookedAhead = false;
				} else {
					next = peek();
					lookedAhead = true;
				}
				System.out.println("next: " + next);
				if (next == '/') {
					lookingForFirstCharOfToken = true;
					previewedChar = ' ';
					lookedAhead = false;
					currentChar = ' ';
					break;
				} else if (next == '*') {
					System.out.println("next is a star!!");
					while (next == '*') {
						next = peek();
						if (next == '/') {
							System.out
									.println("peeked and found slash after star.");
							removingComments = false;
							lookingForFirstCharOfToken = true;
							previewedChar = ' ';
							lookedAhead = false;
							currentChar = ' ';
							break;
						}

					}

				}
			}
		}// end while loop
	}// end of method

	public void ignoreSingleLineComment() {
		// this method will be called by nextChar if // is read in
		// method will pull of an entire line of input and then return
		// currentChar as ' '
		System.out.println("ignoreSingleLineComment called...");
		int num = 0;
		while (true) {
			try {
				num = inputStream.read();
				System.out.println("read off a character.");
				if (num < 0) {
					// end of file
					System.out
							.println("eof is at end of line with comment on it.");
					num = '$';
					eot = true;
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			char pluckedChar = (char) num;
			String plucked = "" + pluckedChar;
			System.out.println("pluckedChar: " + pluckedChar);
			// System.exit(0);
			if (pluckedChar == '\n' || plucked == "\r\n") {
				System.out.println("pluckedChar from ignore line comment: "
						+ pluckedChar);
				lookingForFirstCharOfToken = true;
				lookedAhead = false;
				previewedChar = ' ';
				currentChar = ' '; // beginning of new line and new token.
				// System.exit(0);
				break;
			}

		}

	}

	public void nextChar() {
		// this method will grab the next character
		// called by scan() method repeatedly until a token is formed.
		// token is delimited by space or ;
		// method reads right off input stream so can't go backwards.
		int num = 0;

		try {
			num = inputStream.read();
			if (num < 0) {
				num = '$';
				// System.out.println("terminating...");
				// System.exit(0);
			}

			currentChar = (char) num;
			// if we encounter a '/' then we should peek at next symbol to see
			// if
			// its a comment.
			if (currentChar == '/') {
				char next = peek();
				if (next == '/') {
					ignoreSingleLineComment();
				} else if (next == '*') {
					ignoreMultiLineComment();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("currentChar from nextChar(): " + currentChar);

	}// end nextChar()

	public Token scan() {
		System.out.println("Scanning...");
		spaceDelimiter = false;
		// this method will return the tokens
		// uses nextChar() to keep grabbing characters to form tokens.
		// boolean lookingForFirstCharOfToken;

		if (lookedAhead) {
			System.out.println("lookedAhead.");

			if (previewedChar == ' ') {
				lookingForFirstCharOfToken = true;
			} else {
				lookingForFirstCharOfToken = false;
			}

			// currentChar = ' ';

			// currentToken will begin with the previewed character at index 0.
			System.out.println("currentChar: " + currentChar);

			currentToken = "" + previewedChar;
			// currentToken = "" + previewedChar;
			// now I need to loop through and pull off remaining characters to
			// form token.
			System.out.println("currentToken & previewedChar: " + currentToken);

			// if we peek and see a space then the token we have is ready to be
			// processed.
			// otherwise we need to complete the token before moving on.
			char next = peek();

			if (currentToken.equals("!")) {
				System.out.println("scanner returning a UNOP Token.");
				if (currentToken.length() > 1) {
					currentToken = currentToken.substring(0,
							currentToken.length() - 1);
				}
				encounteredUNOP = true;
				return new Token("UNOP", "!");
			}

			if (next == '+' || next == '-' || next == '/' || next == '='
					|| next == '<' || next == '>' || next == '&' || next == '*'
					|| next == '!') {
				System.out
						.println("Breaking off the token since there's a BINOP attached.");
				System.out.println("currentToken: " + currentToken);
				// currentToken = currentToken.substring(0,
				// currentToken.length()-1);
				binop = next;
				encounteredBINOP = true;
				lookedAhead = true;
				return new Token(typeOfToken(), currentToken);
			}

			currentToken += previewedChar;

			System.out.println("currentToken: " + currentToken);

			if (next != ' ' && next != '\n' && next != '\r' && next != '\t') {
				System.out.println("PEEKED AND SAW THAT THERE'S MORE.");
				// currentToken += previewedChar;
				while (currentChar == ' ') {
					nextChar();
					currentToken += currentChar;
				}

				if (previewedChar == ';') {
					System.out
							.println("semicolon encountered after peeking.....");
					System.out
							.println("token length: " + currentToken.length());
					// here is another delimiter
					// I'll set a global flag so that next time scanner is
					// called a
					// SEMICOLON Token
					// is returned before looking at any other tokens.
					// return the token without adding the semicolon to it.
					currentToken = currentToken.trim();
					if (currentToken.length() > 1) {
						encounteredSemicolon = true;
						currentToken = (currentToken.substring(0,
								currentToken.length() - 1)).trim();
						System.out.println("currentToken: " + currentToken);
						// System.exit(0);
						return new Token(typeOfToken(), currentToken);
					}
					encounteredSemicolon = false;
					return new Token(typeOfToken(), currentToken);
				}

			}// end of peek() if statement

			currentToken = currentToken.trim();
			System.out.println("updated currentToken: " + currentToken);
			lookedAhead = false;
			System.out.println("lookedAhead: " + lookedAhead);
			// currentChar = ' ';
		} else {
			System.out.println("didn't look ahead so resetting currentToken.");
			lookingForFirstCharOfToken = true;
			currentToken = "";
			currentChar = ' ';
			System.out.println("encounteredDot: " + encounteredDot);
		}

		if (encounteredDot) {
			encounteredDot = false;
			System.out.println("encountered dot resolution");
			return new Token("DOT", ".");
		}

		if (encounteredSemicolon) {
			System.out.println("encountered semicolon resolution.");
			// System.exit(0);
			encounteredSemicolon = false;
			// currentToken = "";
			return new Token("SEMICOLON", ";");
		}
		if (encounteredLeftParen) {
			encounteredLeftParen = false;
			// currentToken = "";
			return new Token("LEFTPAREN", "(");
		}
		if (encounteredComma) {
			encounteredComma = false;
			return new Token("COMMA", ",");
		}
		if (encounteredRightParen) {
			encounteredRightParen = false;
			return new Token("RIGHTPAREN", ")");
		}
		if (encounteredLeftBrace) {
			System.out.println("encounteredLeftBrace");
			encounteredLeftBrace = false;
			return new Token("LEFTBRACE", "{");
		}
		if (encounteredRightBrace) {
			System.out.println("encounteredRightBrace");
			encounteredRightBrace = false;
			return new Token("RIGHTBRACE", "}");
		}
		if (encounteredLeftBracket) {
			System.out.println("encounteredLeftBracket");
			encounteredLeftBracket = false;
			return new Token("LEFTBRACKET", "[");
		}
		if (encounteredRightBracket) {
			System.out.println("encounteredRightBracket");
			encounteredRightBracket = false;
			return new Token("RIGHTBRACKET", "]");
		}
		if (encounteredBINOP) {
			System.out.println("encounteredBINOP: " + encounteredBINOP);
			
			encounteredBINOP = false;
			return new Token("BINOP", "" + binop);
		}

		System.out.println("about to enter search mode....");
		while (true) {
			System.out.println("lookingForFirstCharOfToken: "
					+ lookingForFirstCharOfToken);
			while ((currentChar == ' ' || currentChar == '\t'
					|| currentChar == '\r' || currentChar == '\n')
					&& lookingForFirstCharOfToken) {
				System.out.println("removing white space...");
				// remove leading white space
				nextChar();

			}
			spaceDelimiter = true;
			// lookingForFirstCharOfToken = false;
			System.out.println("currentChar after removing white space: "
					+ currentChar);

			if (currentChar == '$') {
				System.out.println("Detected EOT. Returning EOT.");
				return new Token("EOT", "$");
			}

			System.out.println("currentChar after comments block: "
					+ currentChar);

			if (lookingForFirstCharOfToken) {
				// place that one valid symbol at front of currentToken
				System.out.println("looking for first char of token...");
				// nextChar();
				currentToken = (currentToken + currentChar).trim();
				System.out.println("currentToken: " + currentToken);
				lookingForFirstCharOfToken = false;
			}
			// nextChar();

			if (currentChar == '&') {
				// if this is not followed by another ampersand then is will be
				// rejected by
				// isBINOP
				return new Token(typeOfToken(), currentToken);
			}

			if (currentChar == ' ') {

				// space delimiter located
				// we have a token to return
				// this is where we can go through and check
				System.out.println("found space delimiter.");
				String type = typeOfToken();
				System.out.println("type:  " + type);
				return new Token(type, currentToken);
			}

			if (currentChar == ';') {
				System.out.println("semicolon encountered.");
				System.out.println("token length: " + currentToken.length());
				// here is another delimiter
				// I'll set a global flag so that next time scanner is called a
				// SEMICOLON Token
				// is returned before looking at any other tokens.
				// return the token without adding the semicolon to it.
				if (currentToken.length() > 1) {
					encounteredSemicolon = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
					System.out.println("currentToken: " + currentToken);
					// System.exit(0);
					return new Token(typeOfToken(), currentToken);
				}
				encounteredSemicolon = false;
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '.') {
				System.out.println("dot encountered.");
				if (currentToken.length() > 1) {
					encounteredDot = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
					System.out.println("currentToken: " + currentToken);
					// return new Token(typeOfToken(), currentToken);
				}
				encounteredDot = true;
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '(') {
				System.out.println("encountered left paren.");
				if (currentToken.length() > 1) {
					encounteredLeftParen = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();

				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == ',') {
				if (currentToken.length() > 1) {
					encounteredComma = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == ')') {
				System.out.println("encoutered right paren.");
				if (currentToken.length() > 1) {
					encounteredRightParen = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '{') {
				System.out.println("encountered left brace.");
				if (currentToken.length() > 1) {
					encounteredLeftBrace = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '}') {
				System.out.println("encountered right brace.");
				if (currentToken.length() > 1) {
					encounteredRightBrace = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '[') {
				System.out.println("encountered left bracket");
				if (currentToken.length() > 1) {
					encounteredLeftBracket = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == ']') {
				System.out.println("encountered right bracket");
				if (currentToken.length() > 1) {
					encounteredRightBracket = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}
			// recognize BINOPs
			if (currentChar == '+' || currentChar == '-' || currentChar == '/'
					|| currentChar == '*' || currentChar == '='
					|| currentChar == '<' || currentChar == '>') {
				System.out.println("encountered binary operator");
				System.out.println("currentToken: " + currentToken);
				
				
				// check if this is a valid doubleBINOP. If so then return
				// doubleBINOP from here.
				char next = ' ';

				if (currentToken.length() > 1) {
					encounteredBINOP = true;
					binop = currentChar;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token("BINOP", currentToken);
			}
			// recognize UNOP
			if (currentChar == '!' || currentChar == '-') {
				if (currentToken.length() > 1) {
					encounteredUNOP = true;
					currentToken = (currentToken.substring(0,
							currentToken.length() - 1)).trim();
				}
				return new Token(typeOfToken(), currentToken);
			}

			nextChar();
			// /////////////

			if (currentChar == '$') {
				System.out.println("terminating...");
				// return new Token("EOT", "$");
				System.exit(0);
			}

			currentToken = (currentToken + currentChar).trim();
			System.out.println("currentToken: " + currentToken);
			// return null;

		}// while loop

	}// end of scan()

	public char peek() {
		System.out.println("Taking a peek.");
		// this method will look ahead at the next symbol from the inputStream
		// it will then store that value so that it will be encountered next
		// time scan()
		// is called.

		int num = 0;
		try {
			num = inputStream.read();
			if (num < 0) {
				previewedChar = '$';
				System.out
						.println("peeking. I see this next: " + previewedChar);
				return previewedChar;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		previewedChar = (char) num;
		System.out.println("peeking. I see this next: " + previewedChar);
		lookedAhead = true;
		return previewedChar;

	}

	private String typeOfToken() {
		// this method will examine the currentToken to find it's type
		System.out
				.println("inside typeOfToken.. currentToken: " + currentToken);
		if (isReturn()) {
			return "RETURN";
		} else if (isClass()) {
			return "class";
		} else if (isNew()) {
			return "NEW";
		} else if (isTrue()) {
			return "TRUE";
		} else if (isFalse()) {
			return "FALSE";
		} else if (isInt()) {
			return "INT";
		} else if (isIdentifier()) {
			System.out.println("returning IDENTIFIER from typeOfToken()");
			return "IDENTIFIER";
		} else if (isNUM()) {
			return "NUM";
		} else if (isLeftParen()) {
			return "LEFTPAREN";
		} else if (isRightParen()) {
			return "RIGHTPAREN";
		} else if (isLeftBrace()) {
			return "LEFTBRACE";
		} else if (isRightBrace()) {
			System.out.println("isRightBrace will return RIGHTBRACE");
			return "RIGHTBRACE";
		} else if (isLeftBracket()) {
			return "LEFTBRACKET";
		} else if (isRightBracket()) {
			return "RIGHTBRACKET";
		} else if (isUNOP()) {
			return "UNOP";
		} else if (isDoubleBINOP()) {
			return ("DOUBLEBINOP");
		} else if (isBINOP()) {
			return "BINOP";
		} else if (isSemicolon()) {
			return "SEMICOLON";
		} else if (isDOT()) {
			return "DOT";
		} else if (isEOT()) {
			return "EOT";
		}
		// if the sequence of characters is null then we should exit the program
		// because it means the token is not well formed.
		System.out.println("No type found for token.");
		System.exit(4);
		return null;
	}// endTypeOfToken()

	private boolean isEOT() {
		if (currentToken.equals("$")) {
			return true;
		}
		return false;
	}

	private boolean isClass() {
		System.out.println("isClass() called...");
		if (currentToken.equals("class")) {
			return true;
		}
		return false;
	}

	private boolean isSemicolon() {
		System.out.println("is semicolon entered...");
		if (currentToken.equals(";")) {
			return true;
		}
		return false;
	}

	private boolean isDoubleBINOP() {
		System.out.println("checking for double BINOP.");
		System.out.println("lookedAhead: " + lookedAhead);

		char next;
		// if there was a space separating the two binops then its malformed.
		/*
		 * if (currentToken.length() == 1 && spaceDelimiter == true){ return
		 * false; }
		 */
		/*
		 * if (lookedAhead) { next = previewedChar;
		 * 
		 * } else { next = peek(); }
		 */

		System.out.println("currentToken: " + currentToken);
		// System.out.println("next: " + next);

		if (currentToken.equals("=")) {

			if (lookedAhead) {
				next = previewedChar;

			} else {
				next = peek();
			}

			if (next == '=') {
				currentToken += next;
				lookedAhead = false;
				return true;
			} else
				return false;

		} else if (currentToken.equals("<")) {

			if (lookedAhead) {
				next = previewedChar;

			} else {
				next = peek();
			}

			if (next == '=') {
				currentToken += next;
				lookedAhead = false;
				return true;
			} else
				return false;

		} else if (currentToken.equals(">")) {

			if (lookedAhead) {
				next = previewedChar;

			} else {
				next = peek();
			}

			if (next == '=') {
				currentToken += next;
				lookedAhead = false;
				return true;
			} else
				return false;

		} else if (currentToken.equals("!")) {

			if (lookedAhead) {
				next = previewedChar;

			} else {
				next = peek();
			}

			if (next == '=') {
				System.out.println("DOUBLE BINOP DETECTED.");
				currentToken += next;
				System.out.println("currentToken: " + currentToken);
				lookedAhead = false;
				return true;
			} else
				return false;

		}
		return false;
	}

	private boolean isUNOP() {
		System.out.println("inside UNOP");
		if (currentToken.equals("!") || currentToken.equals("-")) {
			return true;
		}
		return false;
	}

	private boolean isDOT() {
		if (currentToken.equals(".")) {
			encounteredDot = false;
			return true;
		}
		return false;
	}

	private boolean isBINOP() {
		System.out.println("inside BINOP");
		System.out.println("lookedAhead: " + lookedAhead);
		System.out.println("currentToken: " + currentToken);
		boolean starterIsFine = false;
		
		
		if (currentToken.equals("<") || currentToken.equals(">")
				|| currentToken.equals("=") || currentToken.equals("!")
				|| currentToken.equals("&") || currentToken.equals("|")
				|| currentToken.equals("+") || currentToken.equals("-")
				|| currentToken.equals("*") || currentToken.equals("||")) {
			starterIsFine = true;
			System.out.println("starter is fine.");
		}

		if (starterIsFine) {

			char next;
			if (lookedAhead) {
				next = previewedChar;
			} else {
				System.out.println("about to take a peek in else of BINOP.");
				next = peek();
			}

			System.out.println("starter is fine. next: " + next);

			if (currentToken.equals("||")) {
				return true;
			}
			if (currentToken.equals("&") && next != '&') {
				// throw an error here since this char is only valid when it
				// comes in pair.
				System.out.println("Scanner Error. Malformed BINOP");
				System.exit(4);
			}
			if (currentToken.equals("&") && next == '&') {
				// here we should peek at the next character to see if it is an
				// & sign.
				// if yes, currentToken should be modified to be the
				// currentToken + previewedChar & lookedAhead should be set to
				// false.
				// return true only if there are two ampersands back to back
				currentToken = "&&";
				System.out.println("currentToken from BINOP: " + currentToken);
				lookedAhead = false;
				return true;
			} else if (currentToken.equals("<") || currentToken.equals(">")
					|| currentToken.equals("=")) {

				System.out.println("handling currentToken is: " + currentToken);
				// again this is a situation to peek at the next character to
				// see if it is an equal sign
				// if yes, currentToken should be modified to be the
				// currentToken + previewedChar & lookedAhead should be set to
				// false.
				// return true regardless of whether or not the previewed
				// character was a match.

				/*if (next == '=') {
					currentToken = currentToken + previewedChar;
					lookedAhead = false;
				}*/

				return true;

			} else if (currentToken.equals("|") && next == '|') {
				// peek at the next character to see if it is an vertical bar
				// sign
				// if yes, currentToken should be modified to be the
				// currentToken + previewedChar & lookedAhead should be set to
				// false.
				// return true only if there are two vertical bars back to back
				currentToken = "||";
				lookedAhead = false;
				return true;
			} else if (currentToken.equals("!")) {
				if (next == '=') {
					currentToken = currentToken + next;
					lookedAhead = false;
					return true;
				}
				return false;

			} else if (currentToken.equals("+") || currentToken.equals("-")
					|| currentToken.equals("/") || currentToken.equals("*")) {
				System.out.println("first token is " + currentToken);
				System.out.println("Second token is " + next);
				// I don't think there is any binop that can come after any of
				// these in minijava
				if (next == '!' || next == '+' || next == '-' || next == '*'
						|| next == '/' || next == '&' || next == '>'
						|| next == '<') {
					System.out
							.println("ill formed token sequence. terminating.");
					System.exit(4);
				}

				return true;
			}
		}// end outer if
		lookedAhead = false;
		System.out.println("previewedChar: " + previewedChar);
		System.out.println("lookedAhead: " + lookedAhead);
		// System.exit(0);
		System.out.println("end of isBINOP.");
		return false;
	}

	private boolean isReturn() {
		if (currentToken.equals("return")) {
			return true;
		}
		return false;
	}

	private boolean isNew() {
		if (currentToken.equals("new")) {
			return true;
		}
		return false;
	}

	private boolean isTrue() {
		if (currentToken.equals("true")) {

			return true;
		}

		return false;
	}

	private boolean isFalse() {
		if (currentToken.equals("false")) {
			return true;
		}
		return false;
	}

	private boolean isInt() {
		if (currentToken.equals("int")) {
			return true;
		}
		return false;
	}

	private boolean isIdentifier() {
		// add regex code for identifier
		Pattern p = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9|_]*");
		Matcher m = p.matcher(currentToken);
		boolean b = m.matches();
		System.out.println("entered isIdentifier()");
		// if (currentToken.equals("a")) System.exit(0);

		return b;
	}

	private boolean isNUM() {
		// add regex code for NUM
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(currentToken);
		boolean b = m.matches();
		System.out.println("isNum() called. returning " + b);
		return b;
	}

	private boolean isLeftParen() {
		if (currentToken.equals("(")) {
			return true;
		}
		return false;
	}

	private boolean isRightParen() {
		if (currentToken.equals(")")) {
			return true;
		}
		return false;
	}

	private boolean isLeftBrace() {
		if (currentToken.equals("{")) {
			return true;
		}
		return false;
	}

	private boolean isRightBrace() {
		System.out.println("entered isRightBrace()");
		if (currentToken.equals("}")) {
			return true;
		}
		return false;
	}

	private boolean isLeftBracket() {

		if (currentToken.equals("[")) {
			System.out.println("inside isLeftBracket returning true.");
			return true;
		}
		return false;
	}

	private boolean isRightBracket() {
		if (currentToken.equals("]")) {
			return true;
		}
		return false;
	}

}
