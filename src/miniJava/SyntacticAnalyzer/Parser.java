package miniJava.SyntacticAnalyzer;

public class Parser {

	// data members
	private Scanner scanner;
	private Token token;
	boolean leftBraceInStatement = false;
	boolean endOfClass = false;
	boolean equalsInStatement = false;

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
		if (token.spelling.equals("class")) {
			accept("class");
		} else {
			System.out
					.println("If first token is not class token this is ill formed.");
			System.exit(4);
		}
		// accept("class");
		parseIdentifier();
		System.out
				.println("next should see left brace from class declaration...");
		accept("LEFTBRACE");

		// /////////////////////////////////////////
		// here is where loop will begin.
		while (true) {
			// the class body could conceivably be empty so we must allow for
			// that.
			if (token.type.equals("RIGHTBRACE")) {
				endOfClass = true;
				acceptIt();
				System.out.println("after the last brace in empty class.");
				// break and then make sure there aren't any characters outside
				// the last brace.
				break;
			}

			parseDeclarators();

			System.out.println("expecting to see an identifier next.");
			// System.exit(0);
			// parseIdentifier();

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
				
				if (token.spelling.equals("}")){
					break; //end of class
				} else {
					continue; //restart loop if not end of class.
				}
				
				//////////////////////////
				/*
				char next = scanner.peek();
				if (next == '}') {
					break; // closing here means its end of class.
				} else {
					// if the last character was not a closing brace then
					// restart the loop.
					continue;
				}*/

				// System.out.println("token.spelling: " + token.spelling);
				// System.exit(0);
				// //////////////////////////////////
				// code to handle a method declaration:
			} else if (token.spelling.equals("(")) {
				System.out.println("method!!");
				acceptIt();
				// System.exit(0);
				if (token.spelling.equals(")")) {
					acceptIt();
				} else {
					parseParameterList();
					accept("RIGHTPAREN");
				}
				// parseParameterList();
				// System.out.println("after parameter list..");
				// accept("RIGHTPAREN");
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
					// this loop is for statements*
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
						// parse statement will keep the braces balanced also.
						System.out
								.println("after parseStatement in class declaration.");
						System.out
								.println("Continuing Loop within same method....");
						continue;
					} else {
						System.out
								.println("Breaking from main loop in class declaration...");
						break;
					}
				}// end while statement
					// here we will check for a return statement and then close
					// the method declaration
					// System.exit(0);
				System.out
						.println("check for return statement and accept right brace if no return statement here.");

				// check for a return statement
				if (token.spelling.equals("return")) {
					System.out.println("about to accept RETURN.");
					accept("RETURN");
					// make sure that what follows 'return' is a starter for
					// Expression
					if (token.type.equals("IDENTIFIER")
							|| token.spelling.equals("this")
							|| token.type.equals("UNOP")
							|| token.type.equals("LEFTPAREN")
							|| token.type.equals("NUM")
							|| token.spelling.equals("true")
							|| token.spelling.equals("false")
							|| token.spelling.equals("new")
							|| token.spelling.equals("int")) {
						parseExpression();
						// accept("SEMICOLON");
					} else {
						// throw an error.
						System.out
								.println("invalid token after return statement.");
						System.exit(4);
					}
					// parseExpression();
					accept("SEMICOLON");
					// System.exit(0);
					// accept("RIGHTBRACE");
				}
				// at this point method declaration has been fully read.
				// good place to set a loop boundary.
				// so here; after the field or method declaration has been
				// parsed we
				// may look ahead at next starter to determine whether to
				// continue loop or break;
				// look for a right brace as indicator that loop needs to
				// end.
				accept("RIGHTBRACE"); // end of method declaration
				System.out
						.println("location check after right brace was accepted.");

				if (token.type.equals("RIGHTBRACE")) {
					System.out.println("Breaking from loop mechanism.");
					System.out.println("accept brace at end of class");
					// peek ahead before accepting last brace
					// if peek reveals anything other than $ terminate exit code
					// 4
					char next = scanner.peek();
					while (next == ' ') {
						next = scanner.peek();
					}

					if (next != '$') {
						if (next == ' ' || next == '\n' || next == '\r') {
							break;
						}
						System.out
								.println("Terminating since there's characters after last brace.");
						System.exit(4);
					}
					acceptIt(); // this is the brace at end of class
					System.out.println("don't break after last brace parsed.");
					// break;
				} else {
					System.out.println("continuing loop mechanism....");
					// System.exit(0);
					continue;
				}
				// end of code to handle method declaration.
			} else {
				// this block means the input is not field or method
				// declaration.
				System.out
						.println("Encountered code that wasnt field or method!!!!");
				System.exit(4);
			}
			System.out.println("Expecting EOT.");
			if (token.type.equals("EOT")) {
				System.out.println("EOT PROCESSING");
				accept("EOT");
			}

			// look if we have an end of class brace or if the is another
			// method/field declaration
			if (token.type.equals("RIGHTBRACE")) {
				System.out
						.println("FINAL CALL FOR RIGHT BRACE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				// System.exit(0);
				accept("RIGHTBRACE"); // this is the right brace at end of class
										// declaration
				// System.exit(0);
				accept("EOT");
				// System.exit(0);
			} else {
				System.out
						.println("CALLING SYSTEM EXIT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.exit(0);
				continue;
			}

		}// end of outermost while loop.

		// if we see any more symbols outside the class declaration the this is
		// an error
		char next = scanner.peek();
		System.out.println("last next before validation: " + next);
		if (next != '$') {
			if (next == ' ' || next == '\r' || next == '\n') {
				System.out.println("Valid source file");
				System.exit(0);
			}
			System.out.println("Detected symbols outside class. Exiting.");
			System.exit(4);
		} else if (next == '$') {
			System.out.println("valid source file.");
			System.exit(0);
		}

	}

	private void parseType() {
		System.out.println("Parsing Type.");
		System.out.println("previewedChar: " + scanner.previewedChar);
		// char next = scanner.peek();
		if (token.spelling.equals("int")) {

			// here there is a decision to be made
			// is the next character a leftbrace or an identifier
			acceptIt();

			if (token.spelling.equals("[")) {
				accept("LEFTBRACKET");
				accept("RIGHTBRACKET");
				return;
			} else {
				return;
			}

		} else if (token.spelling.equals("boolean")) {
			acceptIt(); // accept boolean
			return;
		} else if (token.spelling.equals("void")) {
			acceptIt(); // accept void
			return;
		} else if (token.type.equals("IDENTIFIER")){
				//&& scanner.encounteredLeftBracket) {
			System.out
					.println("inside parseType seeing identifier followed by bracket.");
			acceptIt(); // accept IDENTIFIER
			if (token.spelling.equals("[")){
			accept("LEFTBRACKET");
			accept("RIGHTBRACKET");
			return;
			}
			//System.out.println("left bracket should be parsed.");
			//accept("RIGHTBRACKET");
			//scanner.encounteredLeftBracket = false;
			return;
			// System.exit(0);

		} else if (token.type.equals("IDENTIFIER")) {
			System.out.println("inside parseType seeing identifier.");
			parseIdentifier();
			if (token.type.equals("LEFTBRACKET")) {
				System.out
						.println("identifier parsed... saw a left bracket... inside parseType going to parse bracket.");
				accept("LEFTBRACKET");
				accept("RIGHTBRACKET");
				scanner.encounteredLeftBracket = false;
				return;
			}
			return;
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
		equalsInStatement = false;
		System.out
				.println("PARSE STATEMENT CALLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("token.spelling: " + token.spelling);
		System.out.println("token.type: " + token.type);

		// since both Type and Reference may start with identifier it creates a
		// problem.

		if (token.spelling.equals("int")) {
			acceptIt();
			if (token.spelling.equals("[")) {
				accept("LEFTBRACKET");
				accept("RIGHTBRACKET");
			}
			parseIdentifier();
			if (token.spelling.equals("=")) {
				equalsInStatement = true;
				acceptIt();
			} else {
				// throw an error.
				System.out.println("equal sign expected.");
				System.exit(4);
			}
			parseExpression();
			accept("SEMICOLON");
			equalsInStatement = false;
			return;
		}

		// start by handling Type id = Expression; statements
		if (token.spelling.equals("boolean") || token.spelling.equals("void")) {
			// parseType();
			acceptIt();
			parseIdentifier();
			if (token.spelling.equals("=")) {
				equalsInStatement = true;
				accept("BINOP"); // accept equalsign
			} else {
				System.out.println("Expecting equal sign in statement!!");
				System.exit(4);
			}
			parseExpression();
			accept("SEMICOLON");
			return;

		}
		// parse if (Expression) Statement (else Statement)?
		if (token.spelling.equals("if")) {
			System.out.println("parsing if statement.");
			acceptIt(); // accept if
			accept("LEFTPAREN");
			if (token.type.equals("IDENTIFIER") || token.type.equals("NUM")
					|| token.spelling.equals("true")
					|| token.spelling.equals("false")
					|| token.type.equals("UNOP")
					|| token.spelling.equals("this")
					|| token.spelling.equals("int")
					|| token.spelling.equals("new")
					|| token.type.equals("LEFTPAREN")) {
				parseExpression();
				if (token.spelling.equals(")")){
					acceptIt();
					System.out.println("end of if (expression) with the right paren.");
					parseStatement();
					if (token.spelling.equals("else")){
						acceptIt();
						parseStatement();
						return;
					}
					return;
					//System.exit(0);
				}
				
				/////////////////////////////
				
				System.out.println("after parseExpression and right paren in if statement....");
				System.out.println("token.spelling: " + token.spelling);
				//System.exit(0);
				if (token.spelling.equals("<") || token.spelling.equals(">")) {
					acceptIt();
					if (token.spelling.equals("=")){
						acceptIt();
					}
					return;
				} else if (token.type.equals("DOUBLEBINOP")) {
					accept("DOUBLEBINOP");
				} else {
					// throw an error
					System.out.println("expecting < > or DOUBLEBINOP.");
					System.exit(4);
				}
				parseExpression();
				accept("RIGHTPAREN");
				parseStatement();
				// check for else statement
				if (token.spelling.equals("else")) {
					acceptIt();
					parseStatement();
				}
				return;
			} // end of code to parse if statements
			/*
			 * // parseExpression(); accept("RIGHTPAREN"); parseStatement(); //
			 * check to see if next token is 'else' if
			 * (token.spelling.equals("else")) { acceptIt(); parseStatement(); }
			 */

			// while (Expression) Statement
		} else if (token.spelling.equals("while")) {
			System.out.println("parsing while statement.");
			acceptIt(); // accept while
			accept("LEFTPAREN");
			if (token.type.equals("IDENTIFIER") || token.type.equals("NUM")
					|| token.spelling.equals("true")
					|| token.spelling.equals("false")
					|| token.type.equals("UNOP")
					|| token.spelling.equals("this")
					|| token.spelling.equals("int")
					|| token.spelling.equals("new")
					|| token.type.equals("LEFTPAREN")) {
				parseExpression();
				if (token.spelling.equals("<") || token.spelling.equals(">")) {
					acceptIt();
				} else {
					accept("DOUBLEBINOP");
				}
				System.out
						.println("about to parse second expression for while statement...");
				parseExpression();
				accept("RIGHTPAREN");
				parseStatement();
				return;
			} else {
				// throw error
				System.out.println("Error in while statement.");
				System.exit(4);
			}

			/*
			 * // parseExpression(); accept("RIGHTPAREN"); parseStatement();
			 */

			// parse case for -- { Statement* }
		} else if (token.type.equals("LEFTBRACE")) {
			System.out.println("leftbrace detected in statement.");
			System.out.println("Going to loop through as many statements as necessary!!!!!!!!!!");
			acceptIt(); // accept left brace.
			while (true){
				parseStatement();
				if (token.spelling.equals("}")){
					System.out.println("RIGHT BRACE TO END STATEMENT LIST!!!");
					acceptIt();
					break;
				}
			}
			leftBraceInStatement = true;
			//parseStatement();
			System.out
					.println("looking for right brace to match left brace in parsestatement.");
			//accept("RIGHTBRACE");
			return;

			// parse Type Id = Expression;
			// Reference = Expression;
			// Reference(ArgumentList?);
			// they are not disjoint starters so they're all handled here.
		} else if ((token.spelling.equals("this") || token.type
				.equals("IDENTIFIER")) && !token.type.equals("INT")) {
			System.out
					.println("Reference detected as starter. accept and parseReference().");
			// if token is identifier how do we know if this is a Type or a
			// Reference??
			// acceptIt();

			if (token.spelling.equals("this")) {
				// Reference = Expression;
				// or Reference (ArgumentList?);
				parseReference();
				if (token.spelling.equals("=")) {
					acceptIt();
					parseExpression();
					accept("SEMICOLON");
					return;
				} else if (token.spelling.equals("(")) {
					acceptIt();
					parseArgumentList();
					accept("RIGHTPAREN");
					accept("SEMICOLON");
					return;
				} else {
					// throw error
					System.out.println("Expected equal sign.");
					System.exit(4);
				}

			}

			char next = ' ';
			if (scanner.encounteredDot) {
				System.out.println("scanner.encounteredDot!!!");
				next = '.';
			} else if (scanner.encounteredLeftBracket) {
				next = '[';
				scanner.lookedAhead = false;
			} else if (scanner.encounteredBINOP) {
				next = scanner.binop;
			}
			System.out.println("next: " + next);

			if (next == '.') {
				// case of Reference.(id | id[Expression]) = Expression;
				// OR Reference.(id | id[Expression]) (ArgumentList?);
				acceptIt();
				accept("DOT");
				parseIdentifier();
				if (token.type.equals("LEFTBRACKET")) {
					acceptIt();
					parseExpression();
					accept("RIGHTBRACKET");
				}
				// at this point the Reference is parsed and we are looking for
				// equal sign
				// OR looking for left paren.
				if (token.spelling.equals("=")) {
					equalsInStatement = true;
					acceptIt();
					parseExpression();
					accept("SEMICOLON");
					return;
				} else if (token.spelling.equals("(")) {
					acceptIt();
					// check for Expression to start ArgumentList
					if (token.type.equals("RIGHTPAREN")) {
						acceptIt();
						accept("SEMICOLON");
						return;
					} else {
						parseArgumentList();
						accept("RIGHTPAREN");
						accept("SEMICOLON");
						return;
					}

				}
				// System.exit(0);
			}
			System.out.println("after checking for dot.");
			// three scenarios to handle
			// Type id = Expression; -- case where Type starter is an identifier
			// Reference = Expression; -- case where Ref starter is identifier
			// Reference(ArgumentList?); -- case where Ref starter is identifier
			// if we look ahead will that help at all?

			// current token is still identifier here.
			// situation where next character was revealed without explicitly
			// calling peek()
			if (next == '[') {
				// could be either Type or Reference still
				// Reference: id[Expression]
				// Type: id[]
				System.out.println("parseStatement sees a leftbracket.");
				accept("IDENTIFIER");
				accept("LEFTBRACKET");
				// if we are looking at a right bracket now then its a Type
				// we're parsing
				// otherwise, its a Reference and Expression needs to be parsed.
				if (token.type.equals("RIGHTBRACKET")) {
					acceptIt();
					parseIdentifier();
					if (token.spelling.equals("=")) {
						equalsInStatement = true;
						acceptIt();
					} else {
						// throw an error.
						System.out
								.println("Equal sign expected in Type id = Expression;");
						System.exit(4);
					}
					parseExpression();
					accept("SEMICOLON");
					return;
				} else {
					// if after left bracket we see an expression this is a
					// Reference
					// Reference = Expression; OR Reference(ArgumentList?);
					parseExpression();
					accept("RIGHTBRACKET");
					if (token.spelling.equals(".")) {
						accept("DOT");
						parseIdentifier();
						if (token.type.equals("LEFTBRACKET")) {
							acceptIt();
							parseExpression();
							accept("RIGHTBRACKET");
						}
					}
					// end of parsing Reference that has brackets in it.
					if (token.spelling.equals("=")) {
						equalsInStatement = true;
						acceptIt();
						parseExpression();
						accept("SEMICOLON");
						return;
					} else if (token.spelling.equals("(")) {
						acceptIt();
						parseArgumentList();
						accept("RIGHTPAREN");
						accept("SEMICOLON");
						return;
					} else {
						// throw error.
						System.out
								.println("after reference the statement is ill formed.");
					}
				}// end final else
			} // end if next == [
			System.out.println("next: " + next);
			System.out.println("********* token spelling ********* is: "
					+ token.spelling);
			//ABOUT TO ACCEPT IDENTIFIER TO SEE WHAT'S NEXT WITHOUT PEEKING
			
			// /////////////////////////////////////////
			/*
			 * if (next == ' '){ while (next == ' '){ next = scanner.peek(); } }
			 * 
			 * if (next == '=' && equalsInStatement == false) { // here we are
			 * parsing Reference = Expression; equalsInStatement = true;
			 * System.out
			 * .println("Reference = Expression; being parsed as statement.");
			 * parseReference(); acceptIt(); parseExpression();
			 * accept("SEMICOLON"); equalsInStatement = false; return; }
			 */
			// ///////////////////////////////

			// handle case where we see identifier but we don't know what's next
			// due to space.
			accept("IDENTIFIER");
			if (token.type.equals("LEFTBRACKET")) {
				acceptIt();
				if (token.type.equals("RIGHTBRACKET")) {
					// this is a Type id = Expression;
					accept("RIGHTBRACKET");
					parseIdentifier();
					if (token.spelling.equals("=")) {
						acceptIt();
						parseExpression();
						accept("SEMICOLON");
						return;
					} else {
						// throw error if we don't see equal sign.
						System.out.println("Expecting to see equal sign.");
						System.exit(4);
					}
				} else {
					// there must be expression inside brackets.
					parseExpression();
					accept("RIGHTBRACKET");
					if (token.spelling.equals("=")) {
						acceptIt();
						parseExpression();
						accept("SEMICOLON");
						return;
					} else {
						// throw error if we dont see equal sign
						System.out.println("Expecting equal sign.");
						System.exit(4);
					}
				}
			} else if (token.spelling.equals("=") && equalsInStatement == false) {
				// Reference = Expression where Reference is a simple
				// identifier.
				acceptIt();
				parseExpression();
				accept("SEMICOLON");
				equalsInStatement = false;
				return;
			} else if (token.spelling.equals("(")){
				System.out.println("left paren detected after identifier parsed in parseStatement...");
				acceptIt();
				if (token.spelling.equals(")")){
					acceptIt();
					accept("SEMICOLON");
					return;
				} else {
					parseArgumentList();
					accept("RIGHTPAREN");
					accept("SEMICOLON");
					return;
				}
				
			}
			// if there wasn't a left bracket after identifier then we'll end up
			// here.

			// next I need to parse Type id = Expression; for the case where
			// Type is a simple identifier.
			// since this is the last possible case then I will just try to
			// parse it
			// without testing any conditions.
			System.out.println("attempting to parse Type id = Expression;");
			//parseType();
			parseIdentifier();
			if (token.spelling.equals("=")) {
				acceptIt();
			} else {
				// throw error
				System.out
						.println("ill formed statement. expecting equal sign");
				System.exit(4);
			}
			parseExpression();
			accept("SEMICOLON");
			return;
		}// end final else if for statements of Starters[Ref] or Starters[Type]
			// //////////////////////////////////
		/*
		 * parseIdentifier(); if (token.spelling.equals("=")) { acceptIt(); }
		 * else { // error
		 * System.out.println("Expecting equal sign in statement....");
		 * System.exit(4); } parseExpression(); accept("SEMICOLON"); return; }
		 * 
		 * System.out.println("about to parse reference.");
		 * 
		 * parseReference();
		 * 
		 * System.out
		 * .println("AFTER PARSING REFERENCE BACK IN PARSESTATEMENT()");
		 * 
		 * if (token.spelling.equals("=")) { //
		 * System.out.println("Equal sign detected in parseStatement()");
		 * System.out
		 * .println("equals BINOP can go here... inside of parseStatement"); //
		 * accept("REFERENCE"); acceptIt(); // accept '=' parseExpression();
		 * accept("SEMICOLON"); } else if (token.spelling.equals("(")) { //
		 * accept("REFERENCE"); acceptIt(); // accept '(' parseArgumentList();
		 * accept("RIGHTPAREN"); accept("SEMICOLON"); } else { // error should
		 * be thrown. System.out
		 * .println("The form of this statement is invalid!!!!!!");
		 * System.exit(4); } } else if (token.spelling.equals("if")) {
		 * acceptIt(); // accept if accept("LEFTPAREN"); parseExpression();
		 * accept("RIGHTPAREN"); parseStatement(); // check to see if next token
		 * is 'else' if (token.spelling.equals("else")) { acceptIt();
		 * parseStatement(); }
		 * 
		 * } else if (token.spelling.equals("while")) { acceptIt(); // accept
		 * while accept("LEFTPAREN"); parseExpression(); accept("RIGHTPAREN");
		 * parseStatement(); } else { // this is for the Type id = Expression ;
		 * System.out
		 * .println("before calling parseType in parse statement final else..."
		 * ); // System.exit(0); parseType(); System.out
		 * .println("after parse type in parse statement final else..."); //
		 * System.exit(0); accept("IDENTIFIER");
		 * System.out.println("parse statement awaiting EQUALSIGN.");
		 * 
		 * if (token.spelling.equals("=")) { System.out
		 * .println("equal sign has been detected in final else of parse statement."
		 * ); acceptIt(); // accept equal sign. // System.exit(0);
		 * System.out.println("about to parse expression."); parseExpression();
		 * accept("SEMICOLON"); // System.exit(0); } else {
		 * System.out.println("Error in parse statement."); System.exit(4); } //
		 * acceptIt(); //accept equal sign. // parseExpression(); //
		 * accept("SEMICOLON");
		 * 
		 * }
		 * 
		 * if (token.type.equals("RIGHTBRACE") && leftBraceInStatement) {
		 * System.out.println("looking for right brace after statement.");
		 * accept("RIGHTBRACE"); } leftBraceInStatement = false;
		 */
	}

	private void parseBaseRef() {
		System.out.println("Parse BaseRef called.");
		System.out.println("token in BaseRef: " + token.spelling);
		// look for keyword 'this' token
		// look for identifier followed by '['
		// look for plain identifier
		if (token.spelling.equals("this") || token.type.equals("IDENTIFIER")) {

			if (token.spelling.equals("this")) {
				acceptIt();
			} else {
				parseIdentifier();
			}
			System.out
					.println("accepted IDENTIFIER or this and back in parseBaseRef.");
			if (token.spelling.equals("[")) {
				System.out
						.println("inside parseBaseRef and accepting leftbracket.");
				acceptIt();
				parseExpression();
				accept("RIGHTBRACKET");
			}
		} else {
			System.out.println("not a proper starter for BaseRef.");
			System.exit(4);
		}
	}

	private void parseIdentifier() {
		System.out.println("parsing: " + token.type);
		// look at the next token to see if it is a valid identifier.
		System.out.println("Parsing Identifier");
		if (token.type == null) {
			System.out.println("inside parseIdentifier() token is null");
			System.exit(4);
		}

		if (token.type.equals("IDENTIFIER")) {
			// check if identifier is also a keyword.
			// if so then it should not be accepted.

			if (token.spelling.equals("public")
					|| token.spelling.equals("private")
					|| token.spelling.equals("static")) {
				// this is a keyword in declarator
				System.out.println("declarator keyword rather than identifier");
				System.exit(4);
			}

			if (token.spelling.equals("int") || token.spelling.equals("void")
					|| token.spelling.equals("boolean")
					|| token.spelling.equals("true")
					|| token.spelling.equals("false")
					|| token.spelling.equals("this")) {
				// this is a keyword which is a type rather than identifier
				System.out
						.println("keyword rather than identifier being used.");
				System.exit(4);
			}

			accept("IDENTIFIER"); // this method will make sure token is
									// identifier
		} else {
			// not an identifier so throw an error.
			System.out.println("Not an identifier. Throwing Error.");
			System.exit(4);
		}

	}

	private void parseExpression() {
		System.out.println("Parse Expression called.");
		System.out.println("token.type: " + token.type);
		System.out.println("token.spelling: " + token.spelling);

		if (token.spelling.equals("this") || token.type.equals("IDENTIFIER")) {
			// this is a BaseRef or Reference Type
			parseIdentifier();
			System.out.println("identifier parsed!! back in parseExpression()!!!");
			System.out.println("token.spelling: " + token.spelling);
			if (token.spelling.equals("[")) {
				acceptIt();
				parseExpression();
				System.out
						.println("AFTER PARSING EXPRESSION ABOUT TO ACCEPT RIGHT BRACKET.");
				accept("RIGHTBRACKET");
			}

			if (token.type.equals("BINOP")) {
				acceptIt();
				if (token.spelling.equals("=")){
					acceptIt();
				} else if (token.spelling.equals("-") || token.spelling.equals("!")) {
					// if the token may be parsed as UNOP then try that
					// if its not a UMOP then an error will be thrown.
					parseUNOP();
					// System.out.println("Two BINOPs together.");
					// System.exit(4);
				}
				parseExpression();
			}

			if (token.spelling.equals("-") || token.spelling.equals("!")) {
				System.out.println("Unacceptable place for UNOP.");
				System.exit(4);
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

			// //////////////////////////////////////////////////
			/*
			 * if (token.type.equals("BINOP")) { // make sure next token isn't
			 * another BINOP char next = scanner.peek(); // pull off any white
			 * space to see next character while (next == ' ') { next =
			 * scanner.peek(); } // throw error if we see a restricted sequence
			 * of binop tokens. if (next == '+' || next == '-' || next == '/' ||
			 * next == '*') { System.out.println("Invalid token sequence.");
			 * System.exit(4); }
			 * System.out.println("BINOP after identifier: Exp BINOP Exp.");
			 * acceptIt(); parseExpression(); }
			 */
			// ////////////////////////////////////////////////////

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
				accept("RIGHTPAREN");
			} else {
				// if we have parsed an identifier but none of the other avenues
				// applied.
				// throw error?
				System.out
						.println("after parsing identifier there weren't any avenues.");
				return;
				// System.exit(4);
			}
		} else if (token.spelling.equals("-") || token.spelling.equals("!")) {
			System.out.println("UNOP beggining the expression...");
			acceptIt();
			parseExpression();
		} else if (token.type.equals("LEFTPAREN")) {
			acceptIt();
			parseExpression();
			accept("RIGHTPAREN");
		} else if (token.type.equals("NUM")) {
			System.out.println("NUM is starting off Expression!!");
			parseNUM();
			if (token.type.equals("BINOP")) {
				parseBINOP();
				parseExpression();
				return;
			} else {
				return;
			}
			/*
			 * if (token.type.equals("BINOP") && !token.spelling.equals("=")) {
			 * System.out.println("BINOP after Expression/Reference"); // we
			 * can't have 2 equals signs in the same // expression/statement.
			 * acceptIt(); parseExpression(); //an expression may just be a
			 * number. }
			 */

		} else if (token.spelling.equals("true")) {
			acceptIt();
			if (token.type.equals("BINOP")) {
				acceptIt();
				if (token.spelling.equals("=")){
					acceptIt();
				} else {
					//throw an error.
					//System.out.println("we must see combo of >= <= !=");
					//System.exit(4);
				}
				parseExpression();
				return;
			} else {
				return;
			}
		} else if (token.spelling.equals("false")) {
			acceptIt();
			if (token.type.equals("BINOP")) {
				acceptIt();
				if (token.spelling.equals("=")){
					acceptIt();
				} else {
					System.exit(4);
				}
				parseExpression();
				return;
			} else {
				return;
			}

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

		} else if (token.type.equals("INT")) {
			System.out.println("Detected INT in parseExpression().");
			acceptIt();
			accept("LEFTPAREN");
			parseExpression();
			accept("RIGHTPAREN");

		}

		/*
		 * { // case for Expression BINOP Expression
		 * System.out.println("checking for Expression BINOP Expression."); if
		 * (token.type.equals("IDENTIFIER") || token.spelling.equals("this")){
		 * //if we see a reference:: case for Expression ::= Reference //or
		 * Expression ::= Reference(ArgumentList?) parseReference(); //check if
		 * we see leftparen after reference if (token.type.equals("LEFTPAREN")){
		 * acceptIt(); parseArgumentList(); accept("RIGHTPAREN"); //that's the
		 * expression now parse BINOP and another Expression. accept("BINOP");
		 * parseExpression(); return; } else { //if the Expression is just a
		 * reference then parse BINOP Expression accept("BINOP");
		 * parseExpression(); return; } }
		 */
		// case for Expression ::= UNOP Expression
		if (token.spelling.equals("-") || token.spelling.equals("!")) {
			acceptIt();
			parseExpression();
			accept("BINOP");
			parseExpression();
		} else if (token.type.equals("LEFTPAREN")) {
			acceptIt();
			parseExpression();
			accept("RIGHTPAREN");
			accept("BINOP");
			parseExpression();

		}
		// after parsing an expression of any of the above types parse BINOP
		// then expression
		/*
		 * if (token.type.equals("BINOP")) {
		 * System.out.println("BINOP after any expression!!"); acceptIt();
		 * parseExpression(); }
		 */

		// }
		// check for a BINOP and then parse another Expression.
		// if there are consecutive BINOPs then throw error.
		if (token.type.equals("BINOP")) {
			// make sure next token isn't another BINOP
			char next = scanner.previewedChar;
			// pull off any white space to see next character
			if (next == ' ') {
				while (next == ' ') {
					next = scanner.peek();
				}
			}
			// throw error if we see another binop token.
			if (next == '+' || next == '-' || next == '/' || next == '*'
					|| next == '<' || next == '>' || next == '!' || next == '=') {
				System.out
						.println("Invalid token sequence. 2 BINOPs in Expression -- not doubleBINOP.");
				System.exit(4);
			}
			if (equalsInStatement && token.spelling.equals("=")) {
				System.out
						.println("This statement already saw an equal sign!!");
				System.exit(4);
			}
			System.out.println("BINOP after identifier: Exp BINOP Exp.");
			acceptIt();
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

		if (token.spelling.equals("-") || token.spelling.equals("!")) {
			acceptIt();
		} else {
			// throw error.
			System.out.println("Invalid UNOP!");
			System.out.println("unop: " + token.spelling);
			System.exit(4);
		}

	}

	private void parseArgumentList() {
		System.out.println("parsing argument list...");
		// check if lookahead sees a COMMA
		// parse accordingly

		parseExpression();
		while (token.spelling.equals(",")) {
			acceptIt();
			parseExpression();
		}

		/*
		 * if (scanner.peek() == ',') { parseExpression(); accept("COMMA");
		 * parseExpression(); } else { parseExpression(); }
		 */

	}

	private void parseReference() {
		System.out.println("Parse Reference called.");
		// parseBaseRef();
		// if next symbol is a '.' then accept it and parse another base ref
		// else we're done
		parseBaseRef();
		if (token.spelling.equals(".")) {
			System.out.println("accepting dot in parseReference()...");
			acceptIt();
			parseIdentifier();
			if (token.spelling.equals("[")) {
				System.out
						.println("accepting [ after identifier in parseReference....");
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

		System.out
				.println("CALLING PARSETYPE FROM PARSEDECLARATOR() !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		parseType();
	}

	private void acceptIt() {
		System.out.println("entered acceptIt()");
		if (endOfClass) {
			char next = ' ';
			while (true) {

				next = scanner.peek();
				// remove any white space
				while (next == ' ' || next == '\n' || next == '\r') {
					next = scanner.peek();
				}
				if (next != '$') {

					if (next == '/') {
						System.out
								.println("forward slash detected after last brace.");
						// System.exit(0);
						next = scanner.peek();
						if (next == '/') {
							System.out
									.println("second forward slash detected after brace.");
							// System.exit(0);
							scanner.ignoreSingleLineComment();
							if (scanner.eot) {
								// if single line comment ends with eot then
								// accept source file.
								System.out
										.println("eot at end of single line comment. Valid Source File.");
								System.exit(0);
							}

						} else if (next == '*') {
							System.out
									.println("asterisk after forward slash detected after class.");
							scanner.ignoreMultiLineComment();
							if (scanner.eot) {
								// if multi-line comment ends with eot then
								// accept source file.
								System.out
										.println("eot at end of mult line comment. Valid Source File.");
								System.exit(0);
							}
						}
					} else if (next == '$') {
						System.out.println("dollar sign detected.");
						break;

					} else {

						// after final brace we should see eot.
						// if that's not the case then there is erroneous data
						// after
						// final brace.
						System.out.println("Erroneous data after final brace.");
						System.exit(4);
					}
				} else if (next == '$') {
					System.out.println("dollar sign up next.");
					break;
				}
			}// end while
		}
		System.out.println("accepting token.");
		token = scanner.scan();
		// System.out.println("next token: " + token.spelling);
	}

	private void accept(String expectedToken) {
		System.out.println("expectedToken: " + expectedToken);
		System.out.println("token.type: " + token.type);
		if (token.type.trim() == expectedToken) {
			if (token.type.equals("EOT")) {
				System.out.println("accepting EOT");
				System.out.println("Valid Source File");
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
