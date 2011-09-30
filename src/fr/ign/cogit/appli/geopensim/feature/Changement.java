/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.feature;

/**
 * @author Julien Perret
 *
 */
public class Changement {
	public static final int Inconnu = 0;
	public static final int Creation = 1;
	public static final int Stabilite = 2;
	public static final int Aggrandissement = 3;
	public static final int Reduction = 4;
	public static final int Modification = 5;
	public static final int Decoupage = 6;
	public static final int Agregation = 7;
	public static String toString(int changement) {
		switch (changement) {
		case 1:return "Création";
		case 2:return "Stabilité";
		case 3:return "Aggrandissement";
		case 4:return "Réduction";
		case 5:return "Modification";
		case 6:return "Découpage";
		case 7:return "agrégation";
		default:return "Inconnu";
		}
	}
}
