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
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.batik.gvt.GraphicsNode;

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
            List<Shape> shapes = new ArrayList<Shape>();
            if (feature.getGeom().isPolygon()) {
                try {
                    Shape shape = viewport.toShape(feature.getGeom());
                    if (shape != null) {
                        shapes.add(shape);
                    }
                } catch (NoninvertibleTransformException e) {
                    e.printStackTrace();
                }
            } else {
                if (feature.getGeom().isMultiSurface()) {
                    for (GM_OrientableSurface surface :
                        ((GM_MultiSurface<GM_OrientableSurface>) feature
                                .getGeom())) {
                        try {
                            Shape shape = viewport.toShape(surface);
                            if (shape != null) {
                                shapes.add(shape);
                            }
                        } catch (NoninvertibleTransformException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            for (Shape shape : shapes) {
                if (this.getFill().getGraphicFill() == null) {
                    this.fillPolygon(shape, viewport, graphics);
                } else {
                    List<Graphic> graphicList = this.getFill().getGraphicFill().getGraphics();
                    for (Graphic graphic : graphicList) {
                        this.graphicFillPolygon(shape, graphic, viewport, graphics);
                    }
                }
            }
        }
        if (this.getStroke() != null) {
            float strokeOpacity = this.getStroke().getStrokeOpacity();
            if (this.getStroke().getGraphicType() == null
                    && strokeOpacity > 0f) {
                // Solid color
                Color color = this.getStroke().getColor();
                double scale = 1;
                if (this.getUnitOfMeasure() != PIXEL) {
                    try {
                        scale = viewport.getModelToViewTransform().getScaleX();
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                    }
                }
                java.awt.Stroke bs = this.getStroke().toAwtStroke((float) scale);
                graphics.setColor(color);
                graphics.setStroke(bs);
                if (feature.getGeom().isPolygon()) {
                    this.drawPolygon((GM_Polygon) feature.getGeom(),
                            viewport, graphics);
                } else {
                    if (feature.getGeom().isMultiSurface()) {
                        for (GM_OrientableSurface surface :
                            ((GM_MultiSurface<GM_OrientableSurface>) feature
                                    .getGeom())) {
                            this.drawPolygon((GM_Polygon) surface,
                                    viewport, graphics);
                        }
                    }
                }
            }
        }
    }

    private void graphicFillPolygon(Shape shape, Graphic graphic,
            Viewport viewport, Graphics2D graphics) {
        if (shape == null || viewport == null || graphic == null) { return; }
        float size = graphic.getSize();
        graphics.setClip(shape);
        for (ExternalGraphic external : graphic.getExternalGraphics()) {
            if (external.getFormat().contains("png")) { //$NON-NLS-1$
                Image image = external.getOnlineResource();
                this.graphicFillPolygon(shape, image, size, graphics);
            } else {
                if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                    GraphicsNode node = external.getGraphicsNode();
                    this.graphicFillPolygon(shape, node, size, graphics);
                }
            }
            return;
        }
        List<Shape> shapes = new ArrayList<Shape>();
        for (Mark mark : graphic.getMarks()) {
            shapes.add(mark.toShape());
            graphics.setColor(mark.getFill().getColor());
        }
        double width = shape.getBounds2D().getWidth();
        double height = shape.getBounds2D().getHeight();
        int xSize = (int) Math.ceil(width / size);
        int ySize = (int) Math.ceil(height / size);
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(size, size);
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                AffineTransform transform = AffineTransform.
                getTranslateInstance(
                        (i + 0.5) * size + shape.getBounds2D().getMinX(),
                        (j + 0.5) * size + shape.getBounds2D().getMinY());
                transform.concatenate(scaleTransform);
                for (Shape markShape : shapes) {
                    Shape tranlatedShape = transform.
                    createTransformedShape(markShape);
                    graphics.fill(tranlatedShape);
                }
            }
        }
    }

    private void graphicFillPolygon(Shape shape, Image image,
            float size, Graphics2D graphics) {
        double width = shape.getBounds2D().getWidth();
        double height = shape.getBounds2D().getHeight();
        double shapeHeight = size;
        double factor = shapeHeight / image.getHeight(null);
        double shapeWidth = image.getWidth(null) * factor;
        int xSize = (int) Math.ceil(width / shapeWidth);
        int ySize = (int) Math.ceil(height / shapeHeight);
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor, factor);
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                AffineTransform transform = AffineTransform.
                getTranslateInstance(
                        i * size + shape.getBounds2D().getMinX(),
                        j * size + shape.getBounds2D().getMinY());
                transform.concatenate(scaleTransform);
                graphics.drawImage(image, transform, null);
            }
        }
    }

    private void graphicFillPolygon(Shape shape, GraphicsNode node,
            float size, Graphics2D graphics) {
        double width = shape.getBounds2D().getWidth();
        double height = shape.getBounds2D().getHeight();
        double shapeHeight = size;
        double factor = shapeHeight / node.getBounds().getHeight();
        double shapeWidth = node.getBounds().getWidth() * factor;
        int xSize = (int) Math.ceil(width / shapeWidth);
        int ySize = (int) Math.ceil(height / shapeHeight);
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor, factor);
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                AffineTransform transform = AffineTransform.
                getTranslateInstance(
                        i * size + shape.getBounds2D().getMinX(),
                        j * size + shape.getBounds2D().getMinY());
                transform.concatenate(scaleTransform);
                node.setTransform(transform);
                node.paint(graphics);
            }
        }
    }

    private void fillPolygon(Shape shape, Viewport viewport,
            Graphics2D graphics) {
        if (shape == null || viewport == null) { return; }
        graphics.fill(shape);
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
                    logger.trace("ring = " + polygon.exteriorLineString());
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
