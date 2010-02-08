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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * @author Julien Perret
 *
 */
public class RasterSymbolizer extends AbstractSymbolizer {
	@Override
	public boolean isRasterSymbolizer() { return true; }

	@Override
	public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
        BufferedImage image = viewport.getLayerViewPanel().getProjectFrame().getImage(feature);
        if (image == null) {
            return;
        }
        GM_Envelope envelope = feature.getGeom().envelope();
        try {
            Shape shape = viewport.toShape(envelope.getGeom());
            double minX = shape.getBounds().getMinX();
            double minY = shape.getBounds().getMinY();
            double maxX = shape.getBounds().getMaxX();
            double maxY = shape.getBounds().getMaxY();
            graphics.drawImage(image, (int) minX, (int) minY,
                    (int) (maxX - minX), (int) (maxY - minY), null);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return;
        }
	}
}
