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
/**
 * 
 */
package fr.ign.cogit.appli.geopensim.feature;

/**
 * Factory pour créer des représentations topographiques.
 * @author Julien Perret
 *
 */
public abstract class AbstractRepresentationFactory {

	/**
	 * @return
	 */
	public abstract ElementRepresentation creerBatiment();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerCimetiere();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerCommune();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerParking();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerCarrefour();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerSurfaceEau();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerTronconChemin();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerTronconCoursEau();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerTronconRoute();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerTerrainSport();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerTronconVoieFerree();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerVegetation();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerIlot();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerGroupe();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerQuartier();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerVille();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerGroupeBatiments();
	/**
	 * @return
	 */
	public abstract ElementRepresentation creerAlignement();
	/**
	 * @param nom
	 * @return
	 */
	public abstract ElementRepresentation creerElementRepresentation(String nom);
	/**
	 * @param representation
	 * @return
	 */
	public abstract ElementRepresentation creerElementRepresentation(ElementRepresentation representation);

}
