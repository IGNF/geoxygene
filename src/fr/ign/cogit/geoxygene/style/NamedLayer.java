/**
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

package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
/*
@XmlType(name = "", propOrder = {
    "name",
    //"description",
    "layerFeatureConstraints",
    "styles"
})
*/
@XmlRootElement(name = "NamedLayer")
public class NamedLayer extends AbstractLayer {
	/**
	 */
	public NamedLayer() {super();}
	/**
	 * @param layerName
	 */
	public NamedLayer(String layerName) {
		super();
		this.setName(layerName);
	}

	@Override
	public FT_FeatureCollection<? extends FT_Feature> getFeatureCollection() {
		/*
		 *  TODO Récupèrer la population à partir d'un vrai DataSet
		 *  Pour l'instant, on utilise un singleton de DataSet qu'il faut donc avoir remplit au préalable...
		 */
		return DataSet.getInstance().getPopulation(this.getName());
	}
	@Override
	public String toString() {
		String result="NamedLayer "+this.getName()+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
		for(Style style:this.getStyles())
			result+="\tStyle "+style+"\n";  //$NON-NLS-1$//$NON-NLS-2$
		return result;
	}
}
