package com.UMLParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLGenerator {

	void genImage(String plString, String location) {
		OutputStream png = null;
		try {
			png = new FileOutputStream(location);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String source = "@startuml\n";
		source += "skinparam classAttributeIconSize 0\nskinparam dpi 150\n";
		source += plString;
		source += "@enduml\n";

		System.out.println(source);
		
		SourceStringReader reader = new SourceStringReader(source);
		try {
			reader.generateImage(png);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
