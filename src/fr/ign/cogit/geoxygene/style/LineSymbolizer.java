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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LineSymbolizer extends AbstractSymbolizer {
    @Override
    public boolean isLineSymbolizer() {
        return true;
    }
    @XmlElement(name = "PerpendicularOffset")
    double perpendicularOffset = 0;

    public double getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    public void setPerpendicularOffset(double perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void paint(FT_Feature feature, Viewport viewport,
            Graphics2D graphics) {
        if (feature.getGeom() == null) { return; }
        if (this.getStroke() != null) {
            if (this.getStroke().getGraphicType() == null) {
                double scale = 1;
                if (this.getUnitOfMeasure() != PIXEL) {
                    try {
                        scale = viewport.getModelToViewTransform().getScaleX();
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                    }
                }
                graphics.setStroke(this.getStroke().
                        toAwtStroke((float) scale));
                graphics.setColor(this.getStroke().getColor());
                if (feature.getGeom().isLineString()
                        || feature.getGeom().isPolygon()) {
                    GM_LineString line = (GM_LineString) ((feature.getGeom()
                            .isLineString()) ? feature.getGeom()
                            : ((GM_Polygon) feature.getGeom())
                                    .exteriorLineString());
                    GM_LineString newLine = null;
                    if (this.getPerpendicularOffset() != 0) {
                        newLine = JtsAlgorithms.offsetCurve(
                                line, this.getPerpendicularOffset());
                    }
                    DirectPositionList list = (newLine == null) ?
                            line.coord() : newLine.coord();
                    newLine = new GM_LineString(list);
                    try {
                        Shape shape = viewport.toShape(newLine);
                        if (shape != null) {
                            graphics.draw(shape);
                        }
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (feature.getGeom().isMultiCurve()) {
                    for (GM_OrientableCurve line :
                        (GM_MultiCurve<GM_OrientableCurve>) feature
                        .getGeom()) {
                        GM_LineString newLine = null;
                        if (this.getPerpendicularOffset() != 0) {
                            newLine = JtsAlgorithms.offsetCurve(
                                    (GM_LineString) line,
                                    this.getPerpendicularOffset());
                        }
                        DirectPositionList list = (newLine == null) ?
                                line.coord() : newLine.coord();
                                /*
                        DirectPosition p0 = list.get(list.size() - 2);
                        DirectPosition p2 = list.get(list.size() - 1);
                        double dx = p2.getX() - p0.getX();
                        double dy = p2.getY() - p0.getY();
                        double length = Math.sqrt(dx * dx + dy * dy);
                        DirectPosition p1 = new DirectPosition(
                                p2.getX() - 2 * dx / length,
                                p2.getY() - 2 * dy / length);
                        DirectPosition p3 = new DirectPosition(
                                p2.getX() - 2 * dy / length - 2 * dx / length,
                                p2.getY() + 2 * dx / length - 2 * dy / length);
                        list.add(p3);
                        list.add(p1);
                        */
                        newLine = new GM_LineString(list);
                        try {
                            Shape shape = viewport.toShape(newLine);
                            if (shape != null) {
                                graphics.draw(shape);
                            }
                        } catch (NoninvertibleTransformException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                if (feature.getGeom().isMultiSurface()) {
                    for (GM_OrientableSurface surface :
                        (GM_MultiSurface<GM_OrientableSurface>) feature
                        .getGeom()) {
                        try {
                            Shape shape = viewport.toShape(surface);
                            if (shape != null) graphics.draw(shape);
                        } catch (NoninvertibleTransformException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            } else {
            }
        }
    }
}
