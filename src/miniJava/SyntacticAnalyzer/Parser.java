package miniJava.SyntacticAnalyzer;

public class Parser {

	// data members
	private Scanner scanner;
	private Token token;
	boolean leftBraceInStatement = true;

	// constructor
	public Parser(Scanner scanner) {
		this.scanner = scanner;
		// System.out.println("Parser constructor called.");
	}

	// grab the first token and then use that to begin parsing program
	public void parse() {
		System.out.println("parse() method called.");
		token = scanner.scan();
		System.out.println("first token type returned: " + token.type);
		parseProgram();
	}

	// parse from the start symbol 'Program ::= (ClassDeclaration)* eot
	private void parseProgram() {
		System.out.println("Parse Program called.");
		parseClassDeclaration();

	}

	private void parseClassDeclaration() {
		System.out.println("Parse Class Declaration Called");

		accept("class");
		parseIdentifier();
		System.out
				.println("next should see left brace from class declaration...");
		accept("LEFTBRACE");
		// /////////////////////////////////////////
		// here is where loop will begin.
		while (true) {
			parseDeclarators();
			System.out.println("expecting to see an identifier next.");
			// System.exit(0);
			parseIdentifier();
			System.out.println("just parsed identifier");
			// System.exit(0);
			System.out.println("Expecting a left paren. or semicolon...");
			// System.exit(0);
			// accept("LEFTPAREN");
			// if current token is ; this is end of fieldDeclaration
			// if current token is '(' this is a method declaration.
			System.out.println("Was it field or method.");
			System.out.println("token.spelling: " + token.spelling);
			System.out.println("token type: " + token.type);
			// System.exit(0);
			// /////////////////////////////////
			// code to handle a field declaration:
			if (token.spelling.equals(";")) {
				System.out.println("field!!!");
				// System.exit(0);

				acceptIt();
				// there may be any number of other field declarations or method
				// declarations from here
				// therefore, I may have to do a peek here to see what's next to
				// make proper branch.
				//
				//
				//
				//
				char next = scanner.peek();
				if (next == '}') {
					break; // closing here means its end of class.
				} else {
					// if the last character was not a closing brace then
					// restart the loop.
					continue;
				}

				// System.out.println("token.spelling: " + token.spelling);
				// System.exit(0);
				// //////////////////////////////////
				// code to handle a method declaration:
			} else if (token.spelling.equals("(")) {
				System.out.println("method!!");
				acceptIt();
				// System.exit(0);
				parseParameterList();
				System.out.println("after parameter list..");
				accept("RIGHTPAREN");
				System.out
						.println("accepting rightparen in class declaration loop.");
				// System.exit(0);
				System.out
						.println("accepting left brace explicitly from method handling.");
				accept("LEFTBRACE");
				// / Method Body Will Follow //
				// System.exit(0);
				// //////////////////////////////////
				// after left brace we may have to parse zero or more statements
				// check if token is a starter of a possible statement.
				while (true) {
					//this loop is for statements*
					if (token.spelling.equals("{")
							|| token.type.equals("IDENTIFIER")
							|| token.type.equals("INT")
							|| token.spelling.equals("boolean")
							|| token.spelling.equals("void")
							|| token.spelling.equals("if")
							|| token.spelling.equals("while")
							|| token.spelling.equals("this")
							|| token.type.equals("REFERENCE")) {
						System.out
								.println("prior to parsestatement in class declaration.");
						System.out.println("token type: " + token.type);
						// System.exit(0);

						parseStatement();
						//parse statement will keep the braces balanced also.
						System.out
								.println("after parseStatement in class declaration.");
						System.out.println("Continuing Loop within same method....");
						continue;
					} else {
						System.out.println("Breaking from main loop in class declaration...");
						break;
					}
				}// end while statement
				//here we will check for a return statement and then close the method declaration
				
				System.out
						.println("check for return statement and accept right brace if no return statement here.");

				// check for a return statement
				if (token.spelling.equals("return")) {
					accept("RETURN");
					parseExpression();
					accept("SEMICOLON");
					//accept("RIGHTBRACE");
				}
				// at this point method declaration has been fully read.
				// good place to set a loop boundary.
				// so here; after the field or method declaration has been
				// parsed we
				// may look ahead at next starter to determine whether to
				// continue loop or break;
				// look for a right brace as indicator that loop needs to
				// end.
				accept("RIGHTBRACE"); //end of method declaration
				/*
				if (token.type.equals("RIGHTBRACE")) {
					System.out.println("Breaking from loop mechanism.");
					acceptIt(); //this is the brace at end of given method declaration
					break;
				} else {
					System.out.println("continuing loop mechanism....");
					// System.exit(0);
					continue;
				}*/

			}// end of code to handle method declaration.
			
			//look if we have an end of class brace or if the is another method/field declaration
			if (token.type.equals("RIGHTBRACE")){
				accept("RIGHTBRACE"); //this is the right brace at end of class declaration
				accept("EOT");
			} else {
				continue;
			}

		}// end of outermost while loop.
		//System.out.println("accepting right brace out of while loop.");
		//look if we have an end of class brace or if the is another method/field declaration
		
		/*if (token.type.equals("RIGHTBRACE")){
			accept("RIGHTBRACE"); //this is the right brace at end of class declaration
			accept("EOT");
		} else {
			continue;
		}*/
		// accept("RIGHTBRACE");
		// this is the last brace in the program.
		// now I need to accept eot.
		//accept("EOT");

	}

	private void parseType() {
		System.out.println("Parsing Type.");
		System.out.println("previewedChar: " + scanner.previewedChar);
		// char next = scanner.peek();
		if (token.spelling.equals("int")) {

			// here there is a decision to be made
			// is the next character a leftbrace or an identifier
			acceptIt();
			/*
			 * if (token.spelling.equals("[")) { accept("LEFTBRACE");
			 * accept("RIGHTBRACE"); } else if (token.type.equals("IDENTIFIER"))
			 * {
			 * System.out.println("calling parse identifier from parseType...");
			 * parseIdentifier(); }
			 */

		} else if (token.spelling.equals("boolean")) {
			acceptIt(); // accept boolean
		} else if (token.spelling.equals("void")) {
			acceptIt(); // accept void
		} else if (token.type.equals("IDENTIFIER")
				&& scanner.encounteredLeftBracket) {
			System.out
					.println("inside parseType seeing identifier followed by bracket.");
			acceptIt(); // accept IDENTIFIER
			accept("LEFTBRACKET");
			accept("RIGHTBRACKET");
			scanner.encounteredLeftBracket = false;
			// System.exit(0);

		} else if (token.type.equals("IDENTIFIER")) {
			System.out.println("inside parseType seeing identifier.");
			parseIdentifier();
		}
		// System.exit(0);
	}

	private void parseParameterList() {
		while (true) {
			System.out.println("Parsing Parameter List");
			parseType();
			System.out.println("About to call Parse Identifier.");
			parseIdentifier();
			// System.exit(0);
			if (token.type.equals("COMMA")) {
				System.out.println("spotted comma in param list...");
				acceptIt();
				// parseType();
				// System.out.println("About to call parseIdentifier()");
				// parseIdentifier();
				continue;
				// next we have the choice of parsing a statement based on what
				// we
				// see from lookahead

			} else {
				// if after parsing type and id we don't see a comma then end
				// this routine.
				System.out.println("breaking from parameter list.");
				// System.exit(0);
				break;
			}
		}
		// System.exit(0);
	}

	private void parseStatement() {
		System.out.println("PARSE STATEMENT CALLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("token.spelling: " + token.spelling);
		System.out.println("token.type: " + token.type);
		if (token.type.equals("LEFTBRACE")) {
			leftBraceInStatement = true;
			parseStatement();
			System.out
					.println("looking for right brace to match left brace in parsestatement.");
			accept("RIGHTBRACE");
		} else if ((token.spelling.equals("this") || token.type.equals("IDENTIFIER")) && 
				!token.type.equals("INT")) {
			System.out.println("Reference detected as starter. accept and parseReference().");
			// this is how parseStatement will handle Reference starters.
			acceptIt();
			parseReference();
			
			if (token.spelling.equals("=")) {
				// System.out.println("Equal sign detected in parseStatement()");
				System.out
						.println("equals BINOP can go here... inside of parseStatement");
				// accept("REFERENCE");
				acceptIt(); // accept '='
				parseExpression();
				accept("SEMICOLON");
			} else if (token.spelling.equals("(")) {
				// accept("REFERENCE");
				acceptIt(); // accept '('
				parseArgumentList();
				accept("RIGHTPAREN");
				accept("SEMICOLON");
			} 
		} else if (token.spelling.equals("if")) {
			acceptIt(); // accept if
			accept("LEFTPAREN");
			parseExpression();
			accept("RIGHTPAREN");
			parseStatement();
			// check to see if next token is 'else'
			if (token.spelling.equals("else")) {
				acceptIt();
				parseStatement();
			}

		} else if (token.spelling.equals("while")) {
			acceptIt(); // accept while
			accept("LEFTPAREN");
			parseExpression();
			accept("RIGHTPAREN");
			parseStatement();
		} else {
			// this is for the Type id = Expression ;
			System.out.println("before calling parseType in parse statement final else...");
			// System.exit(0);
			parseType();
			System.out
					.println("after parse type in parse statement final else...");
			// System.exit(0);
			accept("IDENTIFIER");
			System.out.println("parse statement awaiting EQUALSIGN.");

			if (token.spelling.equals("=")) {
				System.out
						.println("equal sign has been detected in final else of parse statement.");
				acceptIt(); // accept equal sign.
				// System.exit(0);
				System.out.println("about to parse expression.");
				parseExpression();
				accept("SEMICOLON");
				// System.exit(0);
			} else {
				System.out.println("Error in parse statement.");
				// System.exit(0);
			}
			// acceptIt(); //accept equal sign.
			// parseExpression();
			// accept("SEMICOLON");
		}

		if (token.type.equals("RIGHTBRACE") && leftBraceInStatement) {
			System.out.println("looking for right brace after statement.");
			accept("RIGHTBRACE");
		}
		leftBraceInStatement = false;

	}

	private void parseBaseRef() {
		System.out.println("Parse BaseRef called.");
		// look for keyword 'this' token
		// look for identifier followed by '['
		// look for plain identifier
		if (token.spelling.equals("this") || token.type.equals("IDENTIFIER")) {
			acceptIt();
			if (token.spelling.equals("[")){
				acceptIt();
				parseExpression();
				accept("RIGHTBRACKET");
			}
		}	
	}

	private void parseIdentifier() {
		System.out.println("parsing: " + token.type);
		// look at the next token to see if it is a valid identifier.
		System.out.println("Parsing Identifier");
		if (token.type.equals("IDENTIFIER")) {
			accept("IDENTIFIER"); // this method will make sure token is
									// identifier
		}
		// accept() also moves to next token in stream.

	}

	private void parseExpression() {
		System.out.println("Parse Expression called.");
		System.out.println("token.type: " + token.type);

		if (token.spelling.equals("this") || token.type.equals("IDENTIFIER")) {
			// this is a BaseRef or Reference Type
			parseIdentifier();
			if (token.spelling.equals("[")) {
				acceptIt();
				parseExpression();
				accept("RIGHTBRACKET");
			}
			// after we've parsed BaseRef: this|id|id[Expression]
			if (token.spelling.equals(".")) {
				acceptIt();
				parseIdentifier();
				if (token.type.equals("LEFTBRACKET")) {
					acceptIt();
					parseExpression();
					accept("RIGHTBRACKET");
				}
			} // at this point a reference type has been parsed.
				// note that the reference may be followed by an argumentList in
				// parentheses
			if (token.type.equals("LEFTPAREN")) {
				acceptIt();
				// check if there is an argument list to be parsed.
				while (true) {
					if (!token.type.equals("RIGHTPAREN")) {
						// if we're not looking at a right paren then there must
						// be an argument list
						parseArgumentList();
					} else {
						break;
					}
				}// end while loop for argument list.
			}// end left paren if statement
		} else if (token.type.equals("UNOP")) {
			acceptIt();
			parseExpression();
		} else if (token.type.equals("LEFTPAREN")) {
			acceptIt();
			parseExpression();
			accept("RIGHTPAREN");
		} else if (token.type.equals("NUM")) {
			parseNUM();
			if (token.type.equals("BINOP")){
				acceptIt();
				parseExpression();
			}

		} else if (token.spelling.equals("true")) {
			acceptIt();
		} else if (token.spelling.equals("false")) {
			acceptIt();
		} else if (token.spelling.equals("new")) {
			acceptIt();
			if (token.type.equals("INT")) {
				acceptIt();
				accept("LEFTBRACKET");
				parseExpression();
				accept("RIGHTBRACKET");
			} else if (token.type.equals("IDENTIFIER")) {
				acceptIt();
				if (token.type.equals("LEFTPAREN")) {
					acceptIt();
					accept("RIGHTPAREN");
				} else if (token.type.equals("LEFTBRACKET")) {
					acceptIt();
					parseExpression();
					accept("RIGHTBRACKET");
				}
			}

		} else {
			// case for Expression BINOP Expression
			parseExpression();
			accept("BINOP");
			parseExpression();
			
		}

	}// end of parseExpression() method.

	private void parseNUM() {
		accept("NUM");
	}

	private void parseUnderscore() {
		accept("UNDERSCORE");
	}

	private void parseBINOP() {
		accept("BINOP");
	}

	private void parseUNOP() {
		accept("UNOP");
	}

	private void parseArgumentList() {
		// check if lookahead sees a COMMA
		// parse accordingly
		if (scanner.peek() == ',') {
			parseExpression();
			accept("COMMA");
			parseExpression();
		} else {
			parseExpression();
		}

	}

	private void parseReference() {
		System.out.println("Parse Reference called.");
		// parseBaseRef();
		// if next symbol is a '.' then accept it and parse another base ref
		// else we're done
		parseBaseRef();
		if (token.spelling.equals(".")){
			System.out.println("accepting dot...");
			acceptIt();
			parseIdentifier();
			if (token.spelling.equals("[")){
				acceptIt();
				parseExpression();
				accept("RIGHTBRACKET");
			}
		}

	}

	private void parseFieldDeclaration() {
		parseDeclarators();
		parseIdentifier();
		accept("SEMICOLON");
	}

	private void parseMethodDeclaration() {
		parseDeclarators();
		parseIdentifier();
		accept("LEFTPAREN");
		parseParameterList();
		accept("RIGHTPAREN");
		accept("LEFTBRACE");
		// at this point if we are looking at an identifier, leftbrace, boolean,
		// void, if,
		// while, or 'this' then call parseStatement.
		if (token.spelling.equals("{") || token.type.equals("IDENTIFIER")
				|| token.spelling.equals("boolean")
				|| token.spelling.equals("void") || token.spelling.equals("if")
				|| token.spelling.equals("while")
				|| token.spelling.equals("this")) {
			parseStatement();
		}

		if (token.spelling.equals("return")) {
			acceptIt();
			parseExpression();
			accept("SEMICOLON");
			accept("RIGHTBRACE");
		}

	}

	private void parseDeclarators() {
		System.out.println("parsing: " + token.type);
		System.out
				.println("Check to see if the current token is a Declarator...");

		if (token.spelling.equals("public") || token.spelling.equals("private")) {
			System.out.println("token is public or private....acceptIt");
			acceptIt();
		}

		if (token.spelling.equals("static")) {
			System.out.println("static....acceptIt");
			acceptIt();
		}
		System.out.println("calling parseType from parseDeclarator()");
		parseType();
	}

	private void acceptIt() {
		System.out.println("entered acceptIt()");
		token = scanner.scan();
		// System.out.println("next token: " + token.spelling);
	}

	private void accept(String expectedToken) {
		System.out.println("expectedToken: " + expectedToken);
		System.out.println("token.type: " + token.type);
		if (token.type == expectedToken) {
			if (token.type.equals("EOT")){
				System.exit(0);
			}
			System.out.println("accepting token and moving to next");
			token = scanner.scan();

		} else {
			System.out.println("Parse Error.");
			System.exit(4);
		}
	}

}
