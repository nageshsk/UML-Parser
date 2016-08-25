package com.UMLParser;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ParseJavaToUML {

	private static List<ObjectDesc> foundObjectDesc = null;
	private static int currentObj = 0;

	public static int getCurrentObj() {
		return currentObj;
	}

	public static void setCurrentObj(int currentObj) {
		ParseJavaToUML.currentObj = currentObj;
	}

	class VariableDesc {
		String name = "";
		String type = "";
		int scope = 0;
		boolean includeInClassUML = true;

		@Override
		public String toString() {
			return name + type + scope;
		}
	}
	
	class ConstructorDesc{		
		String name = "";
		List<String> parameterTypes = null;
		List<String> parameters = null;
		int scope = 0;
		
		public ConstructorDesc() {
			parameterTypes = new ArrayList<String>();
			parameters = new ArrayList<String>();
		}		
	}

	class MethodDesc {
		String name = "";
		List<String> parameterTypes = null;
		List<String> parameters = null;
		int scope = 0;
		String returnType = "";

		public MethodDesc() {
			parameterTypes = new ArrayList<String>();
			parameters = new ArrayList<String>();
		}
	}

	class AssociationRel {
		String first = "";
		String second = "";
		String relation = "";
		String relationFirst = "";
		String relationSecond = "";
	}
	
	class DepedencyRel {
		String first = "";
		String second = "";
		String relation = "";

		@Override
		public int hashCode() {
			int hashcode = first.hashCode() + second.hashCode() + relation.hashCode();
			return hashcode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof DepedencyRel){				
				DepedencyRel dr = (DepedencyRel)obj;
				return (dr.first.equals(this.first) && dr.second.equals(this.second) && dr.relation.equals(this.relation));				
			}else {
				return false;
			}
		}
	}
	
	class GettersAndSetters{
		
		List<String> getters = null;
		List<String> setters = null;
		
		public GettersAndSetters() {			
			getters = new ArrayList<String>();
			setters = new ArrayList<String>();			
		}
		
	}

	class ObjectDesc {
		String name = "";
		Boolean isInterface = false;
		List<VariableDesc> variables = null;
		List<MethodDesc> methods = null;
		List<ConstructorDesc> constructors = null;
		List<AssociationRel> associationRelationships = null;
		List<String> usersOfInterface = null;
		List<String> implementersOfInterface = null;
		List<String> extendersOfClass = null;
		List<DepedencyRel> dependencyRelationships = null;
		GettersAndSetters gns = null;

		public ObjectDesc(String name) {
			this.name = name;
			variables = new ArrayList<VariableDesc>();
			methods = new ArrayList<MethodDesc>();
			constructors = new ArrayList<ConstructorDesc>();
			associationRelationships = new ArrayList<AssociationRel>();
			usersOfInterface = new ArrayList<String>();
			implementersOfInterface = new ArrayList<String>();
			extendersOfClass = new ArrayList<String>();
			dependencyRelationships = new ArrayList<DepedencyRel>();
			gns = new GettersAndSetters();
		}
		
		public MethodDesc findMethodByName(String name){
			for(MethodDesc md : methods){
				if(md.name.equalsIgnoreCase(name)){
					return md;
				}				
			}	
			return null;
		}

		@Override
		public String toString() {
			return name + variables; // + methods;
		}
	}

	public ParseJavaToUML() {
		foundObjectDesc = new ArrayList<ObjectDesc>();
	}
	
	public static class ClassVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if (n.getName() != null) {
				ObjectDesc tempOD = ((ParseJavaToUML) arg).new ObjectDesc(n.getName());
				tempOD.isInterface = n.isInterface();
				foundObjectDesc.add(tempOD);
			}
		}
	}

	public static class ClassVisitorForImpAndExtend extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
			if (n.getName() != null) {				
				for (ClassOrInterfaceType ci : n.getImplements()) {
					for (ObjectDesc od : foundObjectDesc) {
						if (od.name.equalsIgnoreCase(ci.toString())) {
							od.implementersOfInterface.add(n.getName());
						}
					}
				}

				for (ClassOrInterfaceType ci : n.getExtends()) {					
					for (ObjectDesc od : foundObjectDesc) {
						if (od.name.equalsIgnoreCase(ci.toString())) {
							od.extendersOfClass.add(n.getName());
						}
					}
				}
			}
		}
	}
	
	public static class ConstructorVisitor extends VoidVisitorAdapter {		
		@Override
		public void visit(ConstructorDeclaration n, Object arg) {
			ObjectDesc tempOD = foundObjectDesc.get(currentObj);
			ConstructorDesc tempCD = ((ParseJavaToUML) arg).new ConstructorDesc();
			
			tempCD.name = n.getName();
			tempCD.scope = n.getModifiers();
			
			for(Parameter p: n.getParameters()){				
				String pmt = p.toString().substring(0, p.toString().indexOf(" "));
				String pm = p.toString().substring(p.toString().indexOf(" ") + 1, p.toString().length() );
				tempCD.parameterTypes.add(pmt);
				tempCD.parameters.add(pm);
				
				for(ObjectDesc od : foundObjectDesc){	
					if(od.name.equalsIgnoreCase(pmt) && od.isInterface && !tempOD.isInterface){
						if(!od.usersOfInterface.contains(tempOD.name)){
							od.usersOfInterface.add(tempOD.name);
						}
					}					
				}				
			}
			
			tempOD.constructors.add(tempCD);			
		}		
	}
	
	public static class FieldVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(FieldDeclaration n, Object arg) {
			ObjectDesc tempObj = foundObjectDesc.get(currentObj);
			VariableDesc tempVarDesc = ((ParseJavaToUML) arg).new VariableDesc();
			tempVarDesc.name = n.getVariables().get(0).toString();
			tempVarDesc.scope = n.getModifiers();
			tempVarDesc.type = n.getType().toString();
			tempObj.variables.add(tempVarDesc);
		}
	}

	public static class MethodVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration n, Object arg) {
			ObjectDesc tempObj = foundObjectDesc.get(currentObj);
			MethodDesc tempMD = ((ParseJavaToUML)arg).new  MethodDesc();			
			Boolean add = true;
			
			tempMD.name = n.getName();
			tempMD.returnType = n.getType().toString();
			tempMD.scope = n.getModifiers();
			
			for(Parameter p: n.getParameters()){
				String pmt = p.toString().substring(0, p.toString().indexOf(" "));
				String pm = p.toString().substring(p.toString().indexOf(" ") + 1, p.toString().length() );
				tempMD.parameterTypes.add(pmt);
				tempMD.parameters.add(pm);
				
				for(ObjectDesc od : foundObjectDesc){					
					if(od.name.equalsIgnoreCase(pmt) && od.isInterface && !tempObj.isInterface){
						add=false;
						if(!od.usersOfInterface.contains(tempObj.name)){
							od.usersOfInterface.add(tempObj.name);
						}
					}					
				}					
			}
			
			if(add){
				tempObj.methods.add(tempMD);
			}
		}		
	}
	
	public static class VariableDeclarationVisitor extends VoidVisitorAdapter {
		
		@Override
		public void visit(VariableDeclarationExpr n, Object arg) {
			ObjectDesc tempObj = foundObjectDesc.get(currentObj);
			for(ObjectDesc od : foundObjectDesc){
				if(od.name.equalsIgnoreCase(n.getType().toString()) && od.isInterface && !tempObj.isInterface){
					if(!od.usersOfInterface.contains(tempObj.name)){
						od.usersOfInterface.add(tempObj.name);
					}
				}				
			}			
		}		
	}
	
	public static class MethodVisitorForGettersAndSetters extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration n, Object arg) {

			ObjectDesc tempObj = foundObjectDesc.get(currentObj);
			
			String tname = n.getName();
			if(ModifierSet.isPublic(n.getModifiers())){
				if(tname.matches("^get\\w*")){
					tempObj.gns.getters.add(tname.substring(tname.indexOf("get")+3, tname.length()));
				}
				if(tname.matches("^set\\w*")){
					tempObj.gns.setters.add(tname.substring(tname.indexOf("set")+3, tname.length()));
				}	
			}
		}
	}
	
	public boolean checkForGettersAndSetters(){
		
		MethodDesc tempMD = null;
		for(ObjectDesc od : foundObjectDesc){
			for(String gt : od.gns.getters){				
				if(od.gns.setters.contains(gt)){					
					for(VariableDesc vd : od.variables){						
						if(vd.name.equalsIgnoreCase(gt)){
							vd.scope = ModifierSet.PUBLIC;
							tempMD = od.findMethodByName("get"+gt);
							if(tempMD != null){
								od.methods.remove(tempMD);
							}
							tempMD = od.findMethodByName("set"+gt);
							if(tempMD != null){
								od.methods.remove(tempMD);
							}
						}						
					}					
				}				
			}			
		}
		return true;		
	}

	// check if associations are present and add them if not present.
	public boolean checkAndAddIfAssocPresent(AssociationRel tempAR, ObjectDesc currentOD) {

		boolean assocPresent = false;

		for (ObjectDesc od : foundObjectDesc) {
			if (od.name.equalsIgnoreCase(tempAR.second)) {
				for (AssociationRel ar : od.associationRelationships) {
					if (ar.second.equalsIgnoreCase(tempAR.first)) {
						assocPresent = true;
						ar.relationFirst = tempAR.relationSecond;
					}
				}
			}
		}

		if (!assocPresent) {
			currentOD.associationRelationships.add(tempAR);
		}

		return assocPresent;
	}

	public void generatePlantUMLString(String location) {
		String plantUMLString = "";
		String classesDef = "";
		String associationDef = "";
		String dependencyDef = "";
		List<String> classesInPgm = new ArrayList<String>();

		for (ObjectDesc od : foundObjectDesc) {
			classesInPgm.add(od.name);
		}

		// associations and check include in class uml
		for (ObjectDesc od : foundObjectDesc) {
			for (VariableDesc vd : od.variables) {
				if (classesInPgm.contains(vd.type)) {
					vd.includeInClassUML = false;

					AssociationRel tempAR = new AssociationRel();

					tempAR.first = od.name;
					tempAR.second = vd.type;
					tempAR.relation = " -- ";
					tempAR.relationSecond = " \"1\" ";

					checkAndAddIfAssocPresent(tempAR, od);

				} else if (vd.type.toLowerCase().contains("collection")) {
					String innerType = vd.type.substring((vd.type.indexOf("<") + 1), (vd.type.indexOf(">")));
					if (classesInPgm.contains(innerType)) {
						vd.includeInClassUML = false;

						AssociationRel tempAR = new AssociationRel();

						tempAR.first = od.name;
						tempAR.second = innerType;
						tempAR.relation = " -- ";
						tempAR.relationSecond = " \"*\" ";

						checkAndAddIfAssocPresent(tempAR, od);
					}
				}
			}
		}

		// Class and variables
		for (ObjectDesc od : foundObjectDesc) {
			if(!od.isInterface){
				classesDef += "class " + od.name + "{\n";
			} else if(od.isInterface){				
				classesDef += "interface " + od.name + "{\n";							
			}
				for (VariableDesc vd : od.variables) {	
					if (vd.includeInClassUML && ModifierSet.isPublic(vd.scope)) {	
						classesDef += "+" + vd.name + ":" + vd.type + "\n";	
					}
					if (vd.includeInClassUML && ModifierSet.isPrivate(vd.scope)) {	
						classesDef += "-" + vd.name + ":" + vd.type + "\n";	
					}	
				}
				
				for(ConstructorDesc cd : od.constructors){
					if(ModifierSet.isPublic(cd.scope)){
						classesDef += "+" + cd.name + "(" ;
						for (int index = 0; index < cd.parameters.size(); index++){
							classesDef += cd.parameters.get(index) + ":" + cd.parameterTypes.get(index);
						}
						classesDef += ")" + "\n";						
					}					
				}
					
				for(MethodDesc md: od.methods){					
					if(ModifierSet.isPublic(md.scope)){
						classesDef += "+" + md.name + "(" ;
						for (int index = 0; index < md.parameters.size(); index++){
							classesDef += md.parameters.get(index) + ":" + md.parameterTypes.get(index);
						}
						classesDef += ")" + ":" + md.returnType + "\n";
					}					
				}
				
				classesDef += "}\n";
		}

		// for associations
		for (ObjectDesc od : foundObjectDesc) {
			for (AssociationRel ar : od.associationRelationships) {
				associationDef += ar.first + " " + ar.relationFirst + " " + ar.relation + " " + ar.relationSecond + " " + ar.second + "\n";
			}
		}
		
		//for dependency
		for (ObjectDesc od : foundObjectDesc) {			
				if(od.isInterface){					
					for(String s : od.implementersOfInterface){
						dependencyDef += od.name + " <|.. " + s + "\n";
					}
					for(String s: od.usersOfInterface){
						dependencyDef += od.name + " <.. " + s +":uses\n";
					}					
				} else if(!od.isInterface){	
					for(String s : od.extendersOfClass){
						dependencyDef += od.name + " <|-- " + s + "\n";
					}					
				}			
		}

		plantUMLString = classesDef + associationDef + dependencyDef;
		new PlantUMLGenerator().genImage(plantUMLString, location);
	}
}
