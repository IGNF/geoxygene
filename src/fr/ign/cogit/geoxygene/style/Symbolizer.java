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

import java.awt.Graphics2D;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * @author Julien Perret
 *
 */
public interface Symbolizer {

	/**
	 * Renvoie la valeur de l'attribut stroke.
	 * @return la valeur de l'attribut stroke
	 */
	public Stroke getStroke();

	/**
	 * Affecte la valeur de l'attribut stroke.
	 * @param stroke l'attribut stroke à affecter
	 */
	public void setStroke(Stroke stroke);
	
	public boolean isTextSymbolizer();
	public boolean isPointSymbolizer();
	public boolean isPolygonSymbolizer();
	public boolean isLineSymbolizer();
	public boolean isRasterSymbolizer();

	/**
	 * Renvoie la valeur de l'attribut geometryPropertyName.
	 * @return la valeur de l'attribut geometryPropertyName
	 */
	public String getGeometryPropertyName();
	/**
	 * Affecte la valeur de l'attribut geometryPropertyName.
	 * @param geometryPropertyName l'attribut geometryPropertyName à affecter
	 */
	public void setGeometryPropertyName(String geometryPropertyName);

	/**
	 * @param feature
	 * @param viewport
	 * @param graphics
	 */
	public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics);
}
