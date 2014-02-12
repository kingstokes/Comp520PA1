package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {

	public static void main(String[] args) {

		InputStream inputStream = null;
		// from the command line a source file should be read as FileStream
		// that source file should then be sent to the Scanner as an InputStream

		if (args[0].length() > 0) {
			// System.out.println("Analyzing Syntax...");

			// file named at command line will be fed as argument to
			// FileInputStream
			// the handling of that FileStream will be delegated to inputStream
			// object.
			// InputStream is parent class of FileInputStream

			try {
				inputStream = new FileInputStream(args[0]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// inputStream has the entire source file now.
			// send the file over to the scanner [my miniJava scanner class]

			// print the file's contents to make sure it's being read properly.
			/*
			 * while (true){ try {
			 * 
			 * int num = inputStream.read(); if (num < 0) break; char testFile =
			 * (char) num; System.out.print(testFile);
			 * 
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 * 
			 * }
			 */

			// feed input stream to scanner object
			/*Scanner scanner = new Scanner(inputStream);
			while (true) {
				scanner.scan();
			}

		} else {
			// if no argument is specified give an alert.
			System.out.println("File must be specified via Command Line.");
		}
		*/
		//////////////////////////////////////////////////
		}
			Scanner scanner = new Scanner(inputStream);
			Parser parser = new Parser(scanner);
			parser.parse();

	}// end main

}// end class
