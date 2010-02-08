/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PolygonSymbolizer extends AbstractSymbolizer {
    @Override
    public boolean isPolygonSymbolizer() {
        return true;
    }

    @XmlElement(name = "Fill")
    private Fill fill = null;

    /**
     * @return the Fill properties to be used for drawing this Polygon
     */
    public Fill getFill() {
        return this.fill;
    }

    /**
     * @param fill
     */
    public void setFill(Fill fill) {
        this.fill = fill;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void paint(FT_Feature feature, Viewport viewport,
            Graphics2D graphics) {
        if (feature.getGeom() == null || viewport == null) { return; }
        Color fillColor = null;
        float fillOpacity = 1f;
        if (this.getFill() != null) {
            fillColor = this.getFill().getColor();
            fillOpacity = this.getFill().getFillOpacity();
        }
        if (fillColor != null && fillOpacity > 0f) {
            graphics.setColor(fillColor);
            if (feature.getGeom().isPolygon()) this.fillPolygon(
                    (GM_Polygon) feature.getGeom(), viewport, graphics);
            else if (feature.getGeom().isMultiSurface())
                for (GM_OrientableSurface surface :
                    ((GM_MultiSurface<GM_OrientableSurface>) feature
                            .getGeom())) {
                    this.fillPolygon((GM_Polygon) surface, viewport, graphics);
                }
        }
        if (this.getStroke() != null) {
            float strokeOpacity = this.getStroke().getStrokeOpacity();
            if (this.getStroke().getGraphicType() == null
                    && strokeOpacity > 0f) {
                // Solid color
                Color color = this.getStroke().getColor();
                java.awt.Stroke bs = this.getStroke().toAwtStroke();
                graphics.setColor(color);
                graphics.setStroke(bs);
                if (feature.getGeom().isPolygon()) this.drawPolygon(
                        (GM_Polygon) feature.getGeom(), viewport, graphics);
                else if (feature.getGeom().isMultiSurface())
                    for (GM_OrientableSurface surface :
                        ((GM_MultiSurface<GM_OrientableSurface>) feature
                                .getGeom())) {
                        this.drawPolygon((GM_Polygon) surface, viewport,
                                graphics);
                    }
            }
        }
    }

    private void fillPolygon(GM_Polygon polygon, Viewport viewport,
            Graphics2D graphics) {
        if (polygon == null || viewport == null) { return; }
        try {
            Shape shape = viewport.toShape(polygon);
            if (shape != null) {
                graphics.fill(shape);
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    private void drawPolygon(GM_Polygon polygon, Viewport viewport,
            Graphics2D graphics) {
        if (polygon == null || viewport == null) { return; }
        try {
            Shape shape = viewport.toShape(polygon.exteriorLineString());
            if (shape != null) {
                graphics.draw(shape);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("null shape for " + polygon); //$NON-NLS-1$
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < polygon.sizeInterior(); i++) {
            try {
                Shape shape = viewport.toShape(polygon.interiorLineString(i));
                if (shape != null) { graphics.draw(shape); }
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
        }
    }
}
