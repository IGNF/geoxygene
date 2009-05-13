/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.util.loader;

import java.io.File;
import java.io.FileWriter;

/**
 * Usage interne.
 * Générateur de classe java.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class JavaGenerator {


	private static FileWriter fw;
	private static String className;
	private static String javaFilePath;
	private static String extentClassName;
	private static String packageName;


	public JavaGenerator (String path, String ClassName, String ExtentClassName, String PackageName)  {
		className = ClassName.substring(ClassName.lastIndexOf('.')+1); // pour enlever le prefixe du nom de package
		extentClassName = ExtentClassName;
		packageName = PackageName;
		try {
			File thePath = new File(path);
			File dirFile = new File(thePath,PackageName.replace('.','/'));
			dirFile.mkdirs();
			File javaFile = new File(dirFile,className+".java");
			javaFilePath = javaFile.getPath();
		} catch (Exception e) {
			System.out.println(className);
			e.printStackTrace();
		}
	}


	public void writeHeader()  {
		String line1 = "package "+packageName+";\n" ;
		String line2 = "\n";
		String line4 = "\n";
		String line5 = "/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme GeOxygene*/\n";
		String line6 = "\n";
		String line7 = "public class "+className+" extends "+extentClassName;
		String line8 = " {\n\n";
		try {
			fw = new FileWriter(javaFilePath);
			fw.write(line1);
			fw.write(line2);
			fw.write(line4);
			fw.write(line5);
			fw.write(line6);
			fw.write(line7);
			fw.write(line8);
			fw.close();
		} catch (Exception e) {
			System.out.println(className);
			e.printStackTrace();
		}
	}


	public void writeBottom()  {
		String line1 = "}\n" ;
		try {
			fw = new FileWriter(javaFilePath,true);
			fw.write(line1);
			fw.close();
		} catch (Exception e) {
			System.out.println(className);
			e.printStackTrace();
		}
	}


	public void writeField(String type, String name)  {
		String str1 = "     protected "+type+" "+name+";\n";
		String str2 = "     public "+type+" get"+name.substring(0,1).toUpperCase()+name.substring(1)+"() {return this."+name+"; }\n";
		String str3 = "     public void set"+name.substring(0,1).toUpperCase()+name.substring(1)+" ("+type+" ";
		String str4 = name.substring(0,1).toUpperCase()+name.substring(1)+") {"+name+" = "+name.substring(0,1).toUpperCase()+name.substring(1)+"; }\n";
		String str5 = "\n";
		try {
			fw = new FileWriter(javaFilePath,true);
			fw.write(str1);
			fw.write(str2);
			fw.write(str3);
			fw.write(str4);
			fw.write(str5);
			fw.close();
		} catch (Exception e) {
			System.out.println(className);
			e.printStackTrace();
		}
	}

}
