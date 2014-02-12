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

	boolean encounteredRightBrace = false;
	boolean encounteredLeftBracket = false;
	boolean encounteredRightBracket = false;
	boolean encounteredDot = false;
	boolean encounteredBINOP = false;
	

	boolean comments = false;

	boolean lookedAhead = false;

	public Scanner(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	private void ignoreMultiLineComment(){
		//this method is called by nextChar() in the event that a '/*'
		//has been encountered. The method will simply pull off all characters
		//until the stop sequence of '*/' is encountered.
		
		System.out.println("ignoreMultiLineComment called...");
		int num = 0;
		while(true){
			try {
				num = inputStream.read();
			} catch (IOException e){
				e.printStackTrace();
			}
			char pluckedChar = (char) num;
			if (pluckedChar == '*'){
				char next = peek();
				if (next == '/'){
					lookingForFirstCharOfToken = true;
					previewedChar = ' ';
					lookedAhead = false;
					currentChar = ' ';
					break;
				}
			}
		}//end while loop
	}//end of method

	private void ignoreSingleLineComment() {
		// this method will be called by nextChar if // is read in
		// method will pull of an entire line of input and then return
		// currentChar as ' '
		System.out.println("ignoreSingleLineComment called...");
		int num = 0;
		while (true) {
			try {
				num = inputStream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			char pluckedChar = (char) num;
			if (pluckedChar == '\n') {
				lookingForFirstCharOfToken = true;
				lookedAhead = false;
				previewedChar = ' ';
				currentChar = ' '; // beginning of new line and new token.
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
			//if we encounter a '/' then we should peek at next symbol to see if 
			//its a comment.
			if (currentChar == '/') {
				char next = peek();
				if (next == '/') {
					ignoreSingleLineComment();
				} else if (next == '*'){
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

		// this method will return the tokens
		// uses nextChar() to keep grabbing characters to form tokens.
		// boolean lookingForFirstCharOfToken;

		if (lookedAhead) {
			System.out.println("lookedAhead.");
			lookingForFirstCharOfToken = false;
			// currentToken will begin with the previewed character at index 0.
			System.out.println("currentChar: " + currentChar);
			currentToken = "" + previewedChar;
			// currentToken = "" + previewedChar;
			// now I need to loop through and pull off remaining characters to
			// form token.
			System.out.println("currentToken & previewedChar: " + currentToken);
			//if we peek and see a space then the token we have is ready to be proessed.
			//otherwise we need to complete the token before moving on.
			char next = peek();
			currentToken += previewedChar;
			System.out.println("currentToken: " + currentToken);
			if (next != ' ' && next != '\n' && next!= '\r' && next!= '\t') {
				System.out.println("PEEKED AND SAW THAT THERE'S MORE.");
				//currentToken += previewedChar;
				while (currentChar == ' ') {
					nextChar();
					currentToken += currentChar;
				}
				
				if (previewedChar == ';') {
					System.out.println("semicolon encountered after peeking.....");
					System.out.println("token length: " + currentToken.length());
					// here is another delimiter
					// I'll set a global flag so that next time scanner is called a
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
				
			}//end of peek() if statement

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
		
		if (encounteredDot){
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
		if (encounteredBINOP){
			System.out.println("encounteredBINOP: " + encounteredBINOP);
			encounteredBINOP = false;
			return new Token("BINOP", ""+binop);
		}

		System.out.println("about to enter search mode....");
		while (true) {

			while ((currentChar == ' ' || currentChar == '\t'
					|| currentChar == '\r' || currentChar == '\n')
					&& lookingForFirstCharOfToken) {
				System.out.println("removing white space...");
				// remove leading white space
				nextChar();

			}
			// lookingForFirstCharOfToken = false;
			System.out.println("currentChar after removing white space: "
					+ currentChar);

			if (currentChar == '$') {
				System.out.println("Detected EOT. Returning EOT.");
				return new Token("EOT", "$");
			}

			// once we enter this part of loop the leading spaces have been
			// removed.
			// currentChar should contain 1 valid symbol now.
			// char next = peek();
			/*
			 * if (currentChar == '/' && peek() == '/') {
			 * System.out.println("Single line comments detected..."); while
			 * (true) { // pull those comments out of char stream nextChar(); if
			 * (currentChar == '\n' || currentChar == '\r') { lookedAhead =
			 * false; previewedChar = ' '; currentChar = ' '; currentToken = "";
			 * comments = true; lookingForFirstCharOfToken = true; break; } } }
			 */

			// ///////////////////
			// multiline comment handling
			/*
			 * 
			 * if (comments == false) { // if we had to do a peek to check for
			 * single line comment // above. // but it wasn't a comment we have
			 * a previewedChar hanging in // the balance // if we do another
			 * peek() we'll lose that symbol if (currentChar == '/' &&
			 * previewedChar == '*') { lookedAhead = false;
			 * System.out.println("multiline comment detected..."); // I need a
			 * flag to see if there was a failed peek while (true) { // pull off
			 * the character stream until is detected. if (lookedAhead == false)
			 * { nextChar(); } else { currentChar = previewedChar; } if
			 * (currentChar == '*') { System.out.println("currentChar is *"); if
			 * (peek() == '/') { // end of multiline comments lookedAhead =
			 * false; previewedChar = ' '; currentToken = ""; currentChar = ' ';
			 * comments = true; lookingForFirstCharOfToken = true; break; } else
			 * { lookedAhead = false; } // lookedAhead will now be true since we
			 * peeked. } } } }
			 * 
			 * 
			 * // ///////////////////////////
			 * 
			 * // if we saw any type of comments in the file then we will grab a
			 * // fresh token once // we have exited the comments. if (comments)
			 * {
			 * 
			 * System.out.println("pulling next symbol after comment.");
			 * //comments = false;
			 * 
			 * //here I need to pull of an entire token rather than just a
			 * symbol
			 * 
			 * //pull off any leading spaces while (currentChar == ' ' ||
			 * currentChar == '\t' || currentChar == '\n' || currentChar ==
			 * '\r'){ nextChar(); } //grab a token //I should already have the
			 * first symbol from the above while loop while (currentChar !=
			 * ' '){ nextChar();
			 * 
			 * }
			 * 
			 * 
			 * comments = false; //lookingForFirstCharOfToken = false; }
			 */

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
			if (currentChar == '.'){
				System.out.println("dot encountered.");
				if (currentToken.length() > 1){
					encounteredDot = true;
					currentToken = (currentToken.substring(0, currentToken.length()-1)).trim();
					System.out.println("currentToken: " + currentToken);
					//return new Token(typeOfToken(), currentToken);
				}
				encounteredDot = true;
				return new Token(typeOfToken(), currentToken);
			}
			if (currentChar == '(') {
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
			//recognize BINOPs
			if (currentChar == '+' || currentChar == '-' || currentChar == '/'
					|| currentChar == '*'){
				System.out.println("encountered binary operator");
				if (currentToken.length() > 1){
					encounteredBINOP = true;
					currentToken = (currentToken.substring(0, currentToken.length()-1)).trim();
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
		} else if (isBINOP()) {
			return "BINOP";
		} else if (isSemicolon()) {
			return "SEMICOLON";
		}
		return null;
	}// endTypeOfToken()

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

	private boolean isBINOP() {
		System.out.println("inside BINOP");
		boolean starterIsFine = false;

		if (currentToken.equals("<") || currentToken.equals(">")
				|| currentToken.equals("=") || currentToken.equals("!")
				|| currentToken.equals("&") || currentToken.equals("|")
				|| currentToken.equals("+") || currentToken.equals("-")) {
			starterIsFine = true;
		}

		if (starterIsFine) {
			char next = peek();
			if (currentToken.equals("&") && next == '&') {
				// here we should peek at the next character to see if it is an
				// & sign.
				// if yes, currentToken should be modified to be the
				// currentToken + previewedChar & lookedAhead should be set to
				// false.
				// return true only if there are two ampersands back to back
				currentToken = "&&";
				lookedAhead = false;
				return true;
			} else if (currentToken.equals("<") || currentToken.equals(">")
					|| currentToken.equals("=")) {
				// again this is a situation to peek at the next character to
				// see if it is an equal sign
				// if yes, currentToken should be modified to be the
				// currentToken + previewedChar & lookedAhead should be set to
				// false.
				// return true regardless of whether or not the previewed
				// character was a match.
				if (next == '=') {
					currentToken = currentToken + previewedChar;
					lookedAhead = false;
				}
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

			} else if (currentToken.equals("+") || currentToken.equals("-")) {
				return true;
			}
		}// end outer if
		lookedAhead = false;
		System.out.println("previewedChar: " + previewedChar);
		System.out.println("lookedAhead: " + lookedAhead);
		// System.exit(0);

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
