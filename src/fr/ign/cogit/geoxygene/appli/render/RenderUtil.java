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

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Julien Perret
 *
 */
public class RenderUtil {
	@SuppressWarnings("unchecked")
	public static void draw(GM_Object geometry, Viewport viewport, Graphics2D graphics) {
		if (geometry.isPolygon()) {
			GM_Polygon polygon = (GM_Polygon) geometry;
			try {
				Shape shape = viewport.toShape(polygon.exteriorLineString());
				if (shape!=null) graphics.draw(shape);
			} catch (NoninvertibleTransformException e) {e.printStackTrace();}
			for(int i = 0 ; i < polygon.sizeInterior() ; i++)
				try {
					Shape shape = viewport.toShape(polygon.interiorLineString(i));
					if (shape!=null) graphics.draw(shape);
				} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		} else if (geometry.isMultiSurface()||geometry.isMultiCurve()) {
			GM_Aggregate<GM_Object> aggregate = (GM_Aggregate<GM_Object>) geometry;
			for(GM_Object element:aggregate) draw(element, viewport, graphics);
		} else {
			try {
				Shape shape = viewport.toShape(geometry);
				if (shape!=null) graphics.draw(shape);
			} catch (NoninvertibleTransformException e) {e.printStackTrace();}

		}
	}

	/**
	 * @param geometry
	 * @param viewport
	 * @param graphics
	 */
	@SuppressWarnings("unchecked")
	public static void fill(GM_Object geometry, Viewport viewport, Graphics2D graphics) {
		if (geometry.isPolygon()) {
			try {
				Shape shape = viewport.toShape(geometry);
				if (shape!=null) graphics.fill(shape);
			} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		} else if (geometry.isMultiSurface()) {
			GM_Aggregate<GM_Object> aggregate = (GM_Aggregate<GM_Object>) geometry;
			for(GM_Object element:aggregate) fill(element, viewport, graphics);
		}
	}
}
