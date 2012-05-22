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
package fr.ign.cogit.appli.geopensim.feature.bdtopo1_2;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;

/**
 * Classe pour les bâtiments de la BDTopo version 1.2.
 * @author Julien Perret
 */
public class Batiment1_2 extends Batiment {

	/**
	 * Identifiant de l'objet issu du shapefile.
	 */
	protected int gid;
	/**
	 * @return identifiant de l'objet issu du shapefile
	 */
	public int getGid() {return this.gid; }
	/**
	 * @param Gid identifiant de l'objet issu du shapefile
	 */
	public void setGid (int Gid) {gid = Gid; }

	/**
	 * Catégorie du bâtiment.
	 */
	protected String categorie;
	/**
	 * @return Catégorie du bâtiment
	 */
	public String getCategorie() {return this.categorie; }
	/**
	 * @param Categorie Catégorie du bâtiment
	 */
	public void setCategorie (String Categorie) {categorie = Categorie; }

	/* (non-Javadoc)
	 * @see geoxygene.geodata.feature.micro.Batiment#qualifier()
	 */
	@Override
	public void qualifier() {
		super.qualifier();
	}
}
