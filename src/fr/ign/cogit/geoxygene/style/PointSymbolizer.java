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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;


/**
 * @author Julien Perret
 *
 */
public class PointSymbolizer extends AbstractSymbolizer {
	@Override
	public boolean isPointSymbolizer() {return true;}
	
	private Graphic graphic=null;
	public Graphic getGraphic() {return this.graphic;}
	public void setGraphic(Graphic graphic) {this.graphic=graphic;}
	@Override
	public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
		if (this.getGraphic()==null) return;
		Point2D point;
		try {point = viewport.toViewPoint(feature.getGeom().centroid());}
		catch (NoninvertibleTransformException e) {e.printStackTrace();return;}
		for(Mark mark:this.getGraphic().getMarks()) {
			Shape markShape = mark.toShape();
			float size = this.getGraphic().getSize();
			AffineTransform at = AffineTransform.getTranslateInstance(point.getX(),point.getY());
			at.rotate(this.getGraphic().getRotation());
			at.scale(size,size);
			markShape = at.createTransformedShape(markShape);
			graphics.setColor((mark.getFill()==null)?Color.gray:mark.getFill().getColor());
			graphics.fill(markShape);
			graphics.setColor((mark.getStroke()==null)?Color.black:mark.getStroke().getColor());
			graphics.draw(markShape);
		}
		for(ExternalGraphic theGraphic:this.getGraphic().getExternalGraphics()) {
			Image onlineImage = theGraphic.getOnlineResource();
			graphics.drawImage(onlineImage, (int)point.getX()-onlineImage.getWidth(null)/2, (int)point.getY()-onlineImage.getHeight(null)/2, null);
		}		
	}
}
