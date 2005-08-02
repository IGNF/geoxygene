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

import fr.ign.cogit.geoxygene.util.browser.ObjectBrowser;

/**
  *  Classe de test pour le navigateur d'objets graphique de GeOxygene (ObjectBrowser), permettant de 
  *  montrer comment on peut construire, à moindre frais, une interface graphique de lancement pour 
  *  une application comportant plusieurs traitements enchainables.
  * 
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  *
  */
public class TestBrowserApplicationLauncher {

	protected String status;

	/**
	 * Constructeur par défaut de TestLanceurAppli.
	 */
	public TestBrowserApplicationLauncher() {
		status = "";
	}

	protected String lanceProgramme1() {
		status = "Programme 1 executé.";
		return "Programme 1 lancé !!!";
	}

	protected String lanceProgramme2() {
		status = "Programme 2 executé.";
		return "Programme 2 lancé !!!";
	}

	protected String lanceProgramme3() {
		status = "Programme 3 executé.";
		return "Programme 3 lancé !!!";
	}

	public static void main(String[] args) {
		TestBrowserApplicationLauncher obj = new TestBrowserApplicationLauncher();
		ObjectBrowser.browse(obj, true, false, true, false, true);
	}
}
