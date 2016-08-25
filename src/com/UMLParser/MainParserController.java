package com.UMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class MainParserController {

	public static void main(String[] args) throws Exception {

		ArrayList<String> javaFileNames = new ArrayList<String>();
		String folderRoot = args[0];
		final File folder = new File(folderRoot);
		javaFileNames = listFilesForFolder(folder);
		CompilationUnit cu;

		ParseJavaToUML umlParser = new ParseJavaToUML();

		ParseJavaToUML.setCurrentObj(0);

		for (String fileName : javaFileNames) {
			FileInputStream in = new FileInputStream(folderRoot + fileName);

			try {
				// parse the file
				cu = JavaParser.parse(in);
			} finally {
				in.close();
			}

			new ParseJavaToUML.ClassVisitor().visit(cu, umlParser);
		}

		for (String fileName : javaFileNames) {
			FileInputStream in = new FileInputStream(folderRoot + fileName);

			try {
				// parse the file
				cu = JavaParser.parse(in);
			} finally {
				in.close();
			}

			new ParseJavaToUML.ClassVisitorForImpAndExtend().visit(cu, umlParser);
			new ParseJavaToUML.ConstructorVisitor().visit(cu, umlParser);
			new ParseJavaToUML.FieldVisitor().visit(cu, umlParser);
			new ParseJavaToUML.MethodVisitor().visit(cu, umlParser);
			new ParseJavaToUML.VariableDeclarationVisitor().visit(cu, umlParser);
			new ParseJavaToUML.MethodVisitorForGettersAndSetters().visit(cu, umlParser);
			umlParser.checkForGettersAndSetters();

			ParseJavaToUML.setCurrentObj(ParseJavaToUML.getCurrentObj() + 1);
		}
		umlParser.generatePlantUMLString(args[1]);
	}

	public static ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> fileNames = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".java") || fileEntry.getName().endsWith(".JAVA"))
					fileNames.add(fileEntry.getName());
			}
		}
		return fileNames;
	}
}
