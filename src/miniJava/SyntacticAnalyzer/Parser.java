package miniJava.SyntacticAnalyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassDeclList;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.FieldDeclList;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IndexedRef;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.Literal;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MemberDecl;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.MethodDeclList;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.ParameterDeclList;
import miniJava.AbstractSyntaxTrees.QualifiedRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.StatementList;
import miniJava.AbstractSyntaxTrees.Terminal;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.Type;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.WhileStmt;

public class Parser {

	// data members
	private Scanner scanner;
	private Token token;
	boolean leftBraceInStatement = false;
	boolean endOfClass = false;
	boolean equalsInStatement = false;
	private FieldDeclList fdl = new FieldDeclList();
	private MethodDeclList mdl = new MethodDeclList();
	private ClassDeclList cdl = new ClassDeclList();
	private StatementList sl = new StatementList();
	private ParameterDeclList pdl = new ParameterDeclList();
	private ExprList el = new ExprList();
	private char binop = 0;

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
		System.exit(0);
	}

	// parse from the start symbol 'Program ::= (ClassDeclaration)* eot
	private Package parseProgram() {

		System.out.println("Parse Program called.");
		// here is where I think the Package object should be created
		// which is the top level AST for the entire program.
		// this method will return a package AST
		Package pkg = new Package(cdl, null);
		
		while (!token.spelling.equals("$")) {
			Declaration cd = parseClassDeclaration();
			cdl.add((ClassDecl) cd);
		}
		//cdl.add((ClassDecl) cd);
		ASTDisplay display = new ASTDisplay();
		// display.visitClassDecl(cd, ".  ");
		display.showTree(pkg);

		return pkg;

	}

	private Declaration parseClassDeclaration() {
		System.out.println("Parse Class Declaration Called");

		if (token.spelling.equals("class")) {
			accept("class");
		} else {
			System.out
					.println("If first token is not class token this is ill formed.");
			System.exit(4);
		}

		Identifier identifierAST = parseIdentifier(); // identifier has token
														// spelling baked in.

		System.out
				.println("next should see left brace from class declaration...");
		accept("LEFTBRACE");

		// /////////////////////////////////////////
		// here is where loop will begin.
		while (true) {
			// the class body could conceivably be empty so we must allow for
			// that.
			if (token.type.equals("RIGHTBRACE")) {
				endOfClass = true; // this is a flag for acceptIt() method to
									// make sure there's nothing after the last
									// brace.
				acceptIt();
				System.out.println("after the last brace in empty class.");
				if (token.type.equals("EOT")) {
					System.out.println("Valid Source File (Empty class).");
					ClassDecl cd = new ClassDecl(identifierAST.spelling, fdl, mdl, null);
					System.out.println("identifier spelling: " + token.spelling);
					return cd;
				} else {
					// throw error if there is something other than EOT or
					// comment after closing brace
					System.out.println("Error after last brace.");
					System.exit(4);
				}
				// break and then make sure there aren't any characters outside
				// the last brace.
				break;
			}

			// if the class is not empty we will start by parsing Declarators
			// note that Declarators are at the beginning of both field
			// declarations & method declarations

			Declaration decl = parseDeclarators(); // will also parse the next
													// identifier after the
													// Declarator

			// the parseDeclarators() method will also call the necessary method
			// or field declaration parse methods before returning back to here.

			// create a Declaration or ClassDecl object which will later be
			// returned containing all field and method declarations from within
			// the class. Note that there are fdl and mdl lists which are
			// arraylists holding all the declarations of the class.

			// fdl.add((FieldDecl) decl);
			ClassDecl cd = new ClassDecl(identifierAST.spelling, fdl, mdl, null);

			// System.exit(0);
			// //////////////////////////////////////////////////
			// check for last brace to end class.
			System.out.println("last token: " + token.spelling);
			if (token.type.equals("RIGHTBRACE")) {
				acceptIt();

				// after this accept, any whitespace or newlines are consumed
				// also any valid comments are consumed
				// check whether the token we are looking at is valid

				if (token.type.equals("EOT")) {
					System.out.println("Valid Source File.");
					return cd;

				} else if (token.type.equals("class")) {
					return cd;
				} else {
					// throw an error.
					System.out.println("Expecting EOT.");
					System.exit(4);
				}

			} else {
				continue;
			}

		}// end while loop.

		return null;

	}// end of parse class declaration method

	private Type parseType() {
		String typeSpelling = token.spelling;
		System.out.println("Parsing Type.");
		System.out.println("previewedChar: " + scanner.previewedChar);
		// char next = scanner.peek();
		if (token.spelling.equals("int")) {
			// either a PrimType or ArrayType based on what follows

			// here there is a decision to be made
			// is the next character a leftbrace or an identifier
			acceptIt();

			if (token.spelling.equals("[")) {
				// ArrType
				typeSpelling = "ArrType";
				accept("LEFTBRACKET");
				accept("RIGHTBRACKET");
				// cannot make Type object since Type is abstract class.
				// how do we create an ArrayType object??
				BaseType bt = new BaseType(TypeKind.INT, null);
				ArrayType at = new ArrayType(bt, null); // array type is of the
														// form int[]

				// Type at = parseType();
				return at;
			} else {
				// PrimType
				typeSpelling = "BaseType";
				BaseType bt = new BaseType(TypeKind.INT, null);
				return bt;
			}

		} else if (token.spelling.equals("boolean")) {
			acceptIt(); // accept boolean
			typeSpelling = "BaseType";
			BaseType bt = new BaseType(TypeKind.BOOLEAN, null);
			return bt;
		} else if (token.spelling.equals("void")) {
			acceptIt(); // accept void
			typeSpelling = "BaseType";
			BaseType bt = new BaseType(TypeKind.VOID, null);
			return bt;
		} else if (token.type.equals("IDENTIFIER")) {

			String spelling = token.spelling;

			System.out
					.println("inside parseType seeing identifier followed by bracket.");
			acceptIt(); // accept IDENTIFIER -- may need to parseIdentifier()
			if (token.spelling.equals("[")) {
				accept("LEFTBRACKET");
				accept("RIGHTBRACKET");
				typeSpelling = "ArrType";
				Identifier id = new Identifier(spelling, null);
				ClassType ct = new ClassType(id, null);
				ArrayType at = new ArrayType(ct, null); // array type is of the
														// form id[]
				return at;
			}

			typeSpelling = "ClassType";
			Identifier id = new Identifier(spelling, null);
			ClassType ct = new ClassType(id, null);
			return ct;
			// System.exit(0);

		} else {
			System.out.println("Error in ParseType.");
			System.exit(4);
		}

		/*
		 * else if (token.type.equals("IDENTIFIER")) {
		 * System.out.println("inside parseType seeing identifier.");
		 * parseIdentifier(); if (token.type.equals("LEFTBRACKET")) { System.out
		 * .println(
		 * "identifier parsed... saw a left bracket... inside parseType going to parse bracket."
		 * ); accept("LEFTBRACKET"); accept("RIGHTBRACKET");
		 * scanner.encounteredLeftBracket = false; return; } return;
		 * 
		 * }
		 */
		// System.exit(0);
		System.out.println("Error occured in ParseType");
		// System.exit(4);
		return null;
	}

	private Declaration parseParameterList() {
		// create a parameter Declaration to return
		ParameterDecl pd = null;
		// create a list to store all parameter declarations in.
		// ParameterDeclList pdl = new ParameterDeclList();
		Identifier id = null;
		while (true) {
			System.out.println("Parsing Parameter List");
			Type type = parseType();
			System.out.println("About to call Parse Identifier.");
			id = parseIdentifier();
			// use type and id to create a ParameterDecl object (which is
			// descendant of Declaration class)
			// then add that object to pdl
			pd = new ParameterDecl(type, id.spelling, null);
			pdl.add(pd);

			// we have to handle case where there are multiple declarations as
			// part of ParameterList.
			if (token.type.equals("COMMA")) {
				System.out.println("spotted comma in param list...");
				acceptIt();
				// parseType();
				// System.out.println("About to call parseIdentifier()");
				// parseIdentifier();
				continue;
				// next we have the choice of parsing a statement based on what
				// we see from lookahead

			} else {
				// if after parsing type and id we don't see a comma then end
				// this routine.
				// we should see a right paren to end the parameter list here.
				if (token.type.equals("RIGHTPAREN")) {
					// we know we're done with reading all the parameters once
					// we get here.
					// break from while statement to return
					break;
				} else {
					// throw error.
					System.out
							.println("expecting right paren after parameters.");
					System.exit(4);
				}

			}// end outer else statement
		}// end while statement

		System.out.println("breaking from parameter list.");
		// note that we can only return 1 Declaration
		System.out.println("Parameter Declaration: " + pd.name);
		// System.exit(0);
		return pd;

	}// end of method parseParameterList()

	private Statement parseStatement() {

		System.out
				.println("PARSE STATEMENT CALLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// Statement class is abstract
		Statement statement = null;
		Statement elseStatement = null;
		Type type = null;
		ArrayType at = null;
		Identifier id = null;
		Expression expr = null;
		VarDecl vd = null;
		Reference ref = null;

		// handle the cases of Type id = Expression;
		// Reference = Expression; & Reference(ArgumentList?)
		// they all have very similar starters if we're looking at an initial
		// identifier.

		if (token.spelling.equals("int") || token.spelling.equals("boolean")
				|| token.spelling.equals("void")) {
			// handle Type id = Expression; PrimTypes
			// could also be int[] in which case its an ArrType
			type = parseType();
			// now we have either a primtype or arraytype.
			id = parseIdentifier();
			if (token.spelling.equals("=")) {
				acceptIt();
			} else {
				System.out
						.println("Error. Expecting equal sign after Type id...");
				System.exit(4);
			}
			System.out
					.println("about to call parseExpression from parseStatement!!");
			// System.exit(0);
			expr = parseExpression();
			accept("SEMICOLON");

			vd = new VarDecl(type, id.spelling, null);
			statement = new VarDeclStmt(vd, expr, null);
			// sl.add(statement);
			System.out
					.println("about to return the Type id = Expression statement from parseStatement()");

			return statement;
		}

		if (token.type.equals("IDENTIFIER") && !token.spelling.equals("if")
				&& !token.spelling.equals("while")) {
			System.out.println("id detected in parseStatement()!!");
			// accept the identifier so we can look further down the token
			// stream.
			
			//id = parseIdentifier();
			
			//instead of parsing identifier, since the only thing starting with an identifier is
			//a reference I'll call parseReference.
			System.out.println("In parse statement: about to call parse reference!!");
			ref = parseReference();
			//the next thing we see will be either equal sign or left paren
			if (token.spelling.equals("=")){
				acceptIt();
				expr = parseExpression();
				accept("SEMICOLON");
				statement = new AssignStmt(ref, expr, null);
				return statement;
			} else if (token.type.equals("LEFTPAREN")){
				acceptIt();
				ExprList exprList = parseArgumentList();
				accept("RIGHTPAREN");
				accept("SEMICOLON");
				statement = new CallStmt(ref, exprList, null);
				return statement;
			}
			
			
			
			

		} else if (token.spelling.equals("int")
				|| token.spelling.equals("boolean")
				|| token.spelling.equals("void")) {
			// handle Type id = Expression; PrimTypes
			// could also be int[] in which case its an ArrType
			type = parseType();
			// now we have either a primtype or arraytype.
			id = parseIdentifier();
			if (token.spelling.equals("=")) {
				acceptIt();
			} else {
				System.out
						.println("Error. Expecting equal sign after Type id...");
				System.exit(4);
			}
			expr = parseExpression();
			accept("SEMICOLON");

			vd = new VarDecl(type, id.spelling, null);
			statement = new VarDeclStmt(vd, expr, null);
			return statement;
		} else if (token.spelling.equals("this")) {
			ref = parseReference();
			// 2 cases: Reference = Expression; OR Reference (ArgumentList?);
			if (token.spelling.equals("=")) {
				// ::AssignStmt
				acceptIt();
				expr = parseExpression();
				accept("SEMICOLON");
				statement = new AssignStmt(ref, expr, null);
				return statement;
			} else if (token.spelling.equals("(")) {
				// ::CallStmt
				acceptIt();
				el = parseArgumentList();
				accept("RIGHTPAREN");
				accept("SEMICOLON");
				statement = new CallStmt(ref, el, null);
				return statement;
			}
		} else if (token.spelling.equals("if")) {
			System.out.println("IF STATEMENT DETECTED!!!!");
			
			acceptIt();
			accept("LEFTPAREN");
			expr = parseExpression();
			accept("RIGHTPAREN");
			System.out.println("token spelling: " + token.spelling);
			
			Statement statement1 = parseStatement();
			Statement statement2 = null;
			if (token.spelling.equals("else")) {
				System.out.println("ELSE STATEMENT DETECTED!!");
				System.out.println("token spelling: " + token.spelling);
				//System.exit(0);
				acceptIt();
				statement2 = parseStatement();
			}
			IfStmt ifStmt = new IfStmt(expr, statement1, statement2, null);
			return ifStmt;
		} else if (token.spelling.equals("while")) {
			acceptIt();
			accept("LEFTPAREN");
			expr = parseExpression();
			accept("RIGHTPAREN");
			statement = parseStatement();

			WhileStmt ws = new WhileStmt(expr, statement, null);
			return ws;
		} else if (token.spelling.equals("{")) {
			acceptIt();
			while (true) {
				// loop to parse multiple statements
				statement = parseStatement();
				// sl.add(statement);
				if (token.spelling.equals("}")) {
					break;
				}
			}// end while loop

			statement = new BlockStmt(sl, null);
			return statement;
		} else {
			return null;
		}

		return statement;
	}

	private Reference parseBaseRef() {
		Reference ref = null;
		Identifier id = null;

		System.out.println("Parse BaseRef called.");
		System.out.println("token in BaseRef: " + token.spelling);
		// look for keyword 'this' token
		// look for identifier followed by '['
		// look for plain identifier
		if (token.spelling.equals("this") || token.type.equals("IDENTIFIER")) {

			Reference identifierRef = null;
			// id = parseIdentifier();

			if (token.spelling.equals("this")) {
				// create AST for this ::ThisRef
				ref = new ThisRef(null);
				acceptIt();
				return ref;
			} else {
				id = parseIdentifier();
				identifierRef = new IdRef(id, null);
			}

			System.out
					.println("accepted IDENTIFIER or this and back in parseBaseRef.");
			if (token.spelling.equals("[")) {
				// create AST for id[Expression] ::IndexedRef

				System.out
						.println("inside parseBaseRef and accepting leftbracket.");
				acceptIt();
				Expression expr = parseExpression();
				accept("RIGHTBRACKET");
				ref = new IndexedRef(identifierRef, expr, null);
				return ref;
			}

			// create AST for id ::IdRef
			ref = new IdRef(id, null);
			return ref;
		} else {
			System.out.println("not a proper starter for BaseRef.");
			System.exit(4);
		}
		return null;
	}

	private Identifier parseIdentifier() {
		String spelling = token.spelling;
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

			accept("IDENTIFIER");
			return new Identifier(spelling, null);
		} else {
			// not an identifier so throw an error.
			System.out.println("Not an identifier. Throwing Error.");
			System.exit(4);
		}
		return null;
	}

	private Expression parseExpression() {
		System.out.println("PARSE EXPRESSION CALLED!!!!");

		Expression expr = parseE();
		//el.add(expr);
		return expr;

	}// end of parseExpression() method.

	private Expression parseE() {
		Expression expr = parseL();
		while (token.spelling.equals("||")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseL();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseL() {
		Expression expr = parseC();
		while (token.spelling.equals("&&")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseC();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseC() {
		Expression expr = parseQ();
		
		while (token.spelling.equals("==") || token.spelling.equals("!=")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseQ();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseQ() {
		Expression expr = parseR();
		while (token.spelling.equals("<=") || token.spelling.equals(">=")
				|| token.spelling.equals("<") || token.spelling.equals(">")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseR();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseR() {
		Expression expr = parseT();
		while (token.spelling.equals("+") || token.spelling.equals("-")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseT();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseT() {
		Expression expr = parseD();
		while (token.spelling.equals("*") || token.spelling.equals("/")) {
			Token t = new Token(token.type, token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr2 = parseD();
			expr = new BinaryExpr(op, expr, expr2, null);
		}
		return expr;
	}

	private Expression parseF() {
		return null;
	}

	private Expression parseD() {

		// check for true or false at the bottom of production rules
		switch (token.spelling) {
		case "true":
		case "false":
			BooleanLiteral bl = new BooleanLiteral(token.spelling, null);
			Expression expr = new LiteralExpr(bl, null);
			acceptIt();
			return expr;

		}
		// check for a number at bottom of production rules
		switch (token.type) {
		case "NUM":
			IntLiteral il = new IntLiteral(token.spelling, null);
			Expression expr = new LiteralExpr(il, null);
			acceptIt();
			return expr;
		}

		// check for case of parenthesized expression
		switch (token.type) {
		case "LEFTPAREN":
			acceptIt();
			Expression expr = parseE();
			accept("RIGHTPAREN");
			return expr;
		}

		// if we call parseReference it will parse the expression all the way
		// through
		// all of the special cases for references will be handled and returned
		// in bundle
		if (token.type.equals("IDENTIFIER")) {
			// if we see an identifier then we know this is going to be
			// either Expression ::= Reference | Reference (ArgumentList?)
			System.out
					.println("Identifier located so parseReference being called.");

			Reference ref = parseReference();

			Expression expr = new RefExpr(ref, null);
			return expr;

		}

		if (token.spelling.equals("-") || token.spelling.equals("!")) {

			Token t = new Token("UNOP", token.spelling);
			Operator op = new Operator(t, null);
			acceptIt();
			Expression expr = parseExpression();
			Expression ue = new UnaryExpr(op, expr, null);
			return ue;
		}

		// check for new id(); new int[Expression]; or new id [Expression]
		if (token.spelling.equals("new")) {
			acceptIt();

			if (token.spelling.equals("int")) {
				acceptIt();
				BaseType bt = new BaseType(TypeKind.INT, null);
				accept("LEFTBRACKET");
				Expression expr = parseExpression();
				accept("RIGHTBRAKCET");

				Expression ne = new NewArrayExpr(bt, expr, null);
				return ne;
			}

			Identifier id = parseIdentifier();
			if (token.type.equals("LEFTPAREN")) {
				acceptIt();
				ClassType ct = new ClassType(id, null);
				Expression ne = new NewObjectExpr(ct, null);
				accept("RIGHTPAREN");
				return ne;
			} else if (token.type.equals("LEFTBRACKET")) {
				acceptIt();
				Expression expr = parseExpression();
				accept("RIGHTBRACKET");
				ClassType ct = new ClassType(id, null);
				Expression na = new NewArrayExpr(ct, expr, null);
				return na;
			}

		}

		return null;
	}

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

	private ExprList parseArgumentList() {
		System.out.println("parsing argument list...");
		// check if lookahead sees a COMMA
		// parse accordingly

		Expression arg = parseExpression();
		el.add(arg);
		
		while (token.spelling.equals(",")) {
			acceptIt();
			arg = parseExpression();
			el.add(arg);
		}

		return el;

	}

	private Reference parseReference() {
		Reference ref = null;
		System.out.println("Parse Reference called.");
		// parseBaseRef();
		// if next symbol is a '.' then accept it and parse another base ref
		// else we're done
		Reference baseRef = parseBaseRef();
		System.out.println("token name before accepting: " + token.spelling);
		if (token.spelling.equals(".")) {
			System.out.println("accepting dot in parseReference()...");
			accept("DOT");
			System.out.println("testing token name: " + token.spelling);
			//System.exit(0);
			Identifier id = parseIdentifier();
			if (token.spelling.equals("[")) {
				System.out
						.println("accepting [ after identifier in parseReference....");
				
				acceptIt();
				Expression expr = parseExpression();
				accept("RIGHTBRACKET");
				// create AST for BaseRef.id[Expression] ::IndexedRef
				ref = new IndexedRef(baseRef, expr, null);
				return ref;
			}

			// create AST for BaseRef.id ::QualifiedRef
			ref = new QualifiedRef(baseRef, id, null);
			return ref;

		}// end if statement

		// handle case for BaseRef as the Reference
		return baseRef;

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

	private Declaration parseDeclarators() {

		// MemberDecl fd = null;
		FieldDecl fd = null;
		MethodDecl md = null;
		// MemberDecl memberDecl = null;
		Declaration pl = null;
		Expression expr = null;
		Statement statement = null;
		boolean isPrivate = false;
		boolean isStatic = false;
		ASTDisplay display = new ASTDisplay();

		// memberDecl = fd;

		System.out.println("parsing: " + token.type);
		System.out
				.println("Check to see if the current token is a Declarator...");

		if (token.spelling.equals("public") || token.spelling.equals("private")) {
			System.out.println("token is public or private....acceptIt");
			acceptIt();
			if (token.spelling.equals("private"))
				isPrivate = true;
		}

		if (token.spelling.equals("static")) {
			isStatic = true;
			System.out.println("static....acceptIt");
			acceptIt();
		}

		System.out
				.println("CALLING PARSETYPE FROM PARSEDECLARATOR() !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Type type = parseType();
		System.out.println("TYPE::::::::: " + type.typeKind);

		// end of Declarators
		// parseIdentifier
		Identifier id = parseIdentifier();

		fd = new FieldDecl(isPrivate, isStatic, type, id.spelling, null);
		// now we should see either a SEMICOLON or LEFTPAREN
		if (token.type.equals("SEMICOLON")) {
			acceptIt();
			// fieldDeclaration detected
			// should I call parseFieldDeclaration or build the field
			// Declaration right here?
			// fd = new FieldDecl(isPrivate, isStatic, type, id.spelling, null);
			display.visitFieldDecl((FieldDecl) fd, ".  ");
			fdl.add((FieldDecl) fd);
			// memberDecl = fd;
			// System.exit(0);
			return fd;
		} else if (token.type.equals("LEFTPAREN")) {
			acceptIt();
			// methodDeclaration detected
			if (!token.type.equals("RIGHTPAREN")) {
				pl = parseParameterList();
			}
			// pl = parseParameterList();
			// we should see a right paren after parsing parameter list
			accept("RIGHTPAREN");
			accept("LEFTBRACE");
			// there may or may not be a statement
			// therefore parse statement may come back null
			// statement = null;
			while (true) {

				statement = parseStatement();
				sl.add(statement);
				if (token.spelling.equals("return")
						|| token.spelling.equals(";")
						|| token.spelling.equals("}")) {
					break;
				}
			}

			System.out
					.println("just returned to parseDeclarator() after parsing statement...");
			// check for a return statement after parsing statement
			if (token.spelling.equals("return")) {
				acceptIt();
				expr = parseExpression();
				accept("SEMICOLON");
			}
			// ////////////////////////////////////////////
			// accept the last brace for the method declaration

			accept("RIGHTBRACE");

			// we need to form a statement list and also get ahold of the
			// expression following any return statement
			// statement list & return expression are needed to create Method
			// Declaration object.
			md = new MethodDecl(fd, pdl, sl, expr, null);
			mdl.add(md);
			// System.exit(0);
			return md;
			// System.exit(0);
		}
		// if we make it here then something was wrong with the declarator.
		System.out.println("throwing an error from parseDeclarator()....");
		System.exit(4);
		return pl;
	}// end parseDeclarators()

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
								System.out.println("token spelling: " + token.spelling);
								
								//System.exit(0);
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
								//System.exit(0);
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
