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

package fr.ign.cogit.geoxygene.dico;


/**
 * Contraintes pour assurer l'intégrité des données.
 * Ces contraintes sont portées par les GF_FeatureType ou les GF_PropertyType (relations 0..n unidirectionnelles).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GF_Constraint {


	/** Identifiant. */
	protected int id;
	/** Renvoie l'identifiant. */
	public int getId() {return this.id;}
	/** Affecte un identifiant. */
	public void setId(int Id) {this.id = Id;}


	/** Description. */
	protected String description;
	/** Renvoie la description. */
	public String getDescription () {return this.description;}
	/** Affecte une description. */
	public void setDescription (String Description) {this.description = Description;}


}
