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

package fr.ign.cogit.geoxygene.example;

/**
 * Exemple de classe permettant de stocker des tableaux.
 * La table correspondante est definie dans le script "tableaux.sql".
 * Le mapping correspondant est defini le fichier "repository_tableaux.xml".
 * 
 * ATTENTION : pour rendre persistants des tableaux de double, int, boolean, ou String,
 * ils doivent avoir un type equivalent dans Oracle de type VARRAY avac la MEME cardinalite.
 * Ces types DOIVENT s'appeler VARRAY_OF_DOUBLE, ou VARRAY_OF_INTEGER ou VARRAY_OF_BOOLEAN, ou .
 * Cf. script " tableaux.sql " pour la syntaxe de creation de ces types.
 * 
 * ATTENTION : pour etre rendue persistants, les tableaux ne doivent pas etre NULL en Java,
 * sinon plantage.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class ClassWithTable  {

	public int id;
	public int getId() {return id; }
	public void setId(int Id) {id = Id;}

	public double[] doubles = new double[10];
	public double[] getDoubles() {return doubles ;}
	public void setDoubles(double[] tab) {doubles = tab;}

	public int[] ints = new int[15];
	public int[] getInts() {return ints ;}
	public void setInts(int[] tab) {ints = tab;}

	public boolean[] booleans = new boolean[5];
	public boolean[] getBooleans() {return booleans ;}
	public void setBooleans(boolean[] tab) {booleans = tab;}

	public String[] strings = new String[10];
	public String[] getStrings() {return strings ;}
	public void setStrings(String[] tab) {strings = tab;}

}
