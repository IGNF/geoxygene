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
package fr.ign.cogit.appli.geopensim.feature.micro;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Classe mère de toutes les représentation d'objets micro Géographiques.
 * @author Julien Perret
 */
public abstract class MicroRepresentation extends ElementRepresentation {
	
	/**
	 * Constructeur de micro-représentations Géographiques.
	 */
	public MicroRepresentation() {super();}
	/**
	 * Constructeur de mirco-représentations Géographiques à partir d'une géométrie
	 * @param geom géométrie de la micro-représentation Géographique
	 */
	public MicroRepresentation(IGeometry geom) {super(geom);}

	/**
	 * Source de la donnée.
	 */
	protected String source = "NA";
	/**
	 * @return Source de la donnée
	 */
	public String getSource() {return this.source; }
	/**
	 * @param Source Source de la donnée
	 */
	public void setSource (String Source) {source = Source; }

	/**
	 * Nature de la représentation
	 */
	protected String nature = "NA";
	/**
	 * @return nature de la représentation
	 */
	public String getNature() {return this.nature; }
	/**
	 * @param Nature nature de la représentation
	 */
	public void setNature (String Nature) {nature = Nature; }

	@Override
	public String toString() {
		return this.getClass().getCanonicalName()+" "+this.getDateSourceSaisie()+" "+this.getNature()+" "+this.getSource();
	}

}
