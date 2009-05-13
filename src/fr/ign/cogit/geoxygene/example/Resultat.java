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

import fr.ign.cogit.geoxygene.feature.FT_Feature;


/**
 * Exemple de classe permettant de stocker une geometrie, et differents attributs.
 * La geometrie est heritee de FT_Feature.
 * La table correspondante est definie dans le script "resultat.sql".
 * Le mapping correspondant est defini le fichier "repository_resultat.xml".
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class Resultat extends FT_Feature {

	protected double double1;
	public double getDouble1() {return double1; }
	public void  setDouble1(double theDouble) {double1 = theDouble; }

	protected int int1;
	public int getInt1() {return int1; }
	public void  setInt1(int theInt) {int1 = theInt; }

	protected String string1;
	public String getString1() {return string1; }
	public void  setString1(String theString) {string1 = theString; }

	protected boolean boolean1;
	public boolean getBoolean1() {return boolean1; }
	public void  setBoolean1(boolean theBoolean) {boolean1 = theBoolean; }

}
