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

package fr.ign.cogit.geoxygene.feature;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Zone d'extraction pour pouvoir lancer des traitement sur une partie seulement
 * d'un jeu de données.
 * 
 * @author Sébastien Mustière
 *
 */
public class Extraction {

	/** Identifiant de la zone d'extraction */
	protected int id;
	/** Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que pour les objets persistants */
	public int getId() {return id;}
	/** Affecte un identifiant (ne pas utiliser si l'objet est persistant car cela est automatique) */
	public void setId (int Id) {id = Id;}

	/** Géometrie définissant la zone d'extraction */
	protected GM_Polygon geom = null;
	/** Renvoie une geometrie. */
	public GM_Polygon getGeom() {return geom;}
	/** Affecte une geometrie. */
	public void setGeom (GM_Polygon g) {geom = g;}

	/** Nom de la zone d'extraction */
	protected String nom;
	public String getNom() {return nom; }
	public void setNom (String S) {nom = S; }

	/** DataSet auquel appartient la zone d'extraction.
	 * Utile uniquement pour OJB: ne pas utiliser directement
	 */
	private int dataSetID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setDataSetID(int I) {dataSetID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getDataSetID() {return dataSetID;}

	/** renvoie une extension avec une géométrie nulle et un nom par défaut: "Zone complète" */
	public static Extraction zoneComplete () {
		Extraction ex = new Extraction();
		ex.setNom("Zone complète");
		return ex;
	}
}
