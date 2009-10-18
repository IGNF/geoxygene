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

package fr.ign.cogit.geoxygene.style;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

/**
 * @author Julien Perret
 *
 */
public class LineSymbolizer extends AbstractSymbolizer {
    @Override
    public boolean isLineSymbolizer() {return true;}

    @SuppressWarnings("unchecked")
    @Override
    public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
	//if (logger.isDebugEnabled()) logger.debug("line");
	if (feature.getGeom()==null) return;
	if (this.getStroke()!=null) {
	    if (this.getStroke().getGraphicType()==null) {
		//if (logger.isDebugEnabled()) logger.debug("stroke "+shape.getBounds2D());
		graphics.setStroke(this.getStroke().toAwtStroke());
		graphics.setColor(this.getStroke().getColor());
		if (feature.getGeom().isLineString()) {
		    try {
			Shape shape = viewport.toShape(feature.getGeom());
			if (shape!=null) graphics.draw(shape);
		    } catch (NoninvertibleTransformException e) {e.printStackTrace();}
		} else if (feature.getGeom().isMultiCurve()) {
		    for(GM_OrientableCurve line:(GM_MultiCurve<GM_OrientableCurve>)feature.getGeom()) {
			try {
			    Shape shape = viewport.toShape(line);
			    if (shape!=null) graphics.draw(shape);					
			} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		    }
		} else {

		}
	    } else {
	    }
	}		
    }

}
