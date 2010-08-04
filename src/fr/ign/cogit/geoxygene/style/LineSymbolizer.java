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
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.batik.gvt.GraphicsNode;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
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

            if (this.getShadow() != null) {
                Color shadowColor = this.getShadow().getColor();
                double translate_x = this.getShadow().getDisplacement().getDisplacementX();
                double translate_y = this.getShadow().getDisplacement().getDisplacementY();
                graphics.setColor(shadowColor);
                List<Shape> shapes = new ArrayList<Shape>();
                if (feature.getGeom().isLineString()) {
                    try {
                        Shape shape = viewport.toShape(feature.getGeom().translate(translate_x, translate_y, 0));
                        if (shape != null) {
                            shapes.add(shape);
                        }
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (feature.getGeom().isMultiCurve()) {
                        for (GM_OrientableCurve line :
                            (GM_MultiCurve<GM_OrientableCurve>) feature
                            .getGeom()) {
                            try {
                                Shape shape = viewport.toShape(line.translate(translate_x, translate_y, 0));
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
                    graphics.draw(shape);
                }
            }
            if (this.getStroke().getGraphicType() == null) {

                List<Shape> shapes = new ArrayList<Shape>();
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
                                        shapes.add(shape);
                                    }
                                } catch (NoninvertibleTransformException e) {
                                    e.printStackTrace();
                                }
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
                                            shapes.add(shape);
                                        }
                                    } catch (NoninvertibleTransformException e) {
                                        e.printStackTrace();
                                    }
                    }
                }
                if (feature.getGeom().isMultiSurface()) {
                    for (GM_OrientableSurface surface :
                        (GM_MultiSurface<GM_OrientableSurface>) feature
                        .getGeom()) {
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

                graphics.setColor(this.getStroke().getColor());
                for (Shape shape : shapes) {
                    graphics.draw(shape);
                }
            } else {
                if (this.getStroke().getGraphicType().getClass().isAssignableFrom(GraphicFill.class)) {                    
                    List<Shape> shapes = new ArrayList<Shape>();
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
                                        Shape shape = viewport.toShape(newLine.buffer(this.getStroke().getStrokeWidth()/2));
                                        if (shape != null) {
                                            shapes.add(shape);
                                        }
                                    } catch (NoninvertibleTransformException e) {
                                        e.printStackTrace();
                                    }
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
                                            Shape shape = viewport.toShape(newLine.buffer(this.getStroke().getStrokeWidth()/2));
                                            if (shape != null) {
                                                shapes.add(shape);
                                            }
                                        } catch (NoninvertibleTransformException e) {
                                            e.printStackTrace();
                                        }
                        }
                    }
                    if (feature.getGeom().isMultiSurface()) {
                        for (GM_OrientableSurface surface :
                            (GM_MultiSurface<GM_OrientableSurface>) feature
                            .getGeom()) {
                            try {
                                Shape shape = viewport.toShape(surface.buffer(this.getStroke().getStrokeWidth()/2));
                                if (shape != null) {
                                    shapes.add(shape);
                                }
                            } catch (NoninvertibleTransformException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    
                    // GraphicFill
                    List<Graphic> graphicList = ((GraphicFill) this.getStroke().getGraphicType()).getGraphics();
                    for (Graphic graphic : graphicList) {
                        for (Shape shape : shapes) {
                            this.graphicFillLineString(shape, graphic, viewport, graphics);
                        }
                    }
                } else {
                    // GraphicStroke
                    List<Shape> shapes = new ArrayList<Shape>();
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
                                            shapes.add(shape);
                                        }
                                    } catch (NoninvertibleTransformException e) {
                                        e.printStackTrace();
                                    }
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
                                                shapes.add(shape);
                                            }
                                        } catch (NoninvertibleTransformException e) {
                                            e.printStackTrace();
                                        }
                        }
                    }
                    if (feature.getGeom().isMultiSurface()) {
                        for (GM_OrientableSurface surface :
                            (GM_MultiSurface<GM_OrientableSurface>) feature
                            .getGeom()) {
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

                    List<Graphic> graphicList = ((GraphicStroke) this.getStroke().getGraphicType()).getGraphics();
                    for (Graphic graphic : graphicList) {
                        for (Shape shape : shapes) {
                            this.graphicStrokeLineString(shape, graphic, viewport, graphics);
                        }
                    }                                    
                }
            }
        }
    }

    private void graphicFillLineString(Shape shape, Graphic graphic,
                Viewport viewport, Graphics2D graphics) {
        if (shape == null || viewport == null || graphic == null) { return; }
        float size = graphic.getSize();
        graphics.setClip(shape);
        for (ExternalGraphic external : graphic.getExternalGraphics()) {
            if (external.getFormat().contains("png")||external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
                Image image = external.getOnlineResource();
                this.graphicFillLineString(shape, image, size, graphics);
            } else {
                if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                    GraphicsNode node = external.getGraphicsNode();
                    this.graphicFillLineString(shape, node, size, graphics);
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

    private void graphicFillLineString(Shape shape, GraphicsNode node,
                float size, Graphics2D graphics) {
        AffineTransform translate = AffineTransform.
        getTranslateInstance(
                    -node.getBounds().getMinX(),
                    -node.getBounds().getMinY());
        node.setTransform(translate);
        BufferedImage buff = new BufferedImage((int) node.getBounds()
                    .getWidth(), (int) node.getBounds().getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
        node.paint((Graphics2D) buff.getGraphics());
        this.graphicFillLineString(shape, buff, size, graphics);
    }

    private void graphicFillLineString(Shape shape, Image image, float size,
                Graphics2D graphics) {
        Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
        Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
        Double shapeHeight = new Double(size);
        double factor = shapeHeight / image.getHeight(null);
        Double shapeWidth = new Double(image.getWidth(null) * factor);
        AffineTransform transform = AffineTransform.
        getTranslateInstance(
                    shape.getBounds2D().getMinX(),
                    shape.getBounds2D().getMinY());
        Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
                    shapeHeight.intValue(), Image.SCALE_FAST);
        BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
                    shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
        buff.getGraphics().drawImage(scaledImage, 0, 0, null);
        ParameterBlock p = new ParameterBlock();
        p.addSource(buff);
        p.add(width.intValue());
        p.add(height.intValue());
        RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
        BufferedImage bufferedImage = im.getAsBufferedImage();
        graphics.drawImage(bufferedImage, transform, null);
        bufferedImage.flush();
        im.dispose();
        scaledImage.flush();
        buff.flush();
    }

    private void graphicStrokeLineString(Shape shape, Graphic graphic,
                Viewport viewport, Graphics2D graphics) {
        if (shape == null || viewport == null || graphic == null) { return; }
        float size = graphic.getSize();
        //graphics.setClip(shape);
        for (ExternalGraphic external : graphic.getExternalGraphics()) {
            if (external.getFormat().contains("png")||external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
                Image image = external.getOnlineResource();
                this.graphicStrokeLineString(shape, image, size, graphics);
            } else {
                if (external.getFormat().contains("svg")) { //$NON-NLS-1$
                    GraphicsNode node = external.getGraphicsNode();
                    this.graphicStrokeLineString(shape, node, size, graphics);
                }
            }
            return;
        }
        List<Shape> shapes = new ArrayList<Shape>();
        for (Mark mark : graphic.getMarks()) {
            shapes.add(mark.toShape());
            graphics.setColor(mark.getFill().getColor());
        }
        List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(shape, size, 1, 1);
        for (AffineTransform t : transforms) {
            for (Shape markShape : shapes) {
                Shape tranlatedShape = t.createTransformedShape(markShape);
                graphics.fill(tranlatedShape);
            }
        }
    }

    private void graphicStrokeLineString(Shape shape, GraphicsNode node,
                float size, Graphics2D graphics) {
        double width = node.getBounds().getWidth();
        double height = node.getBounds().getHeight();
        List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(shape, size, width, height);
        for (AffineTransform t : transforms) {
            AffineTransform tr = AffineTransform.getTranslateInstance(-node.getBounds().getMinX(), -node.getBounds().getMinY());
            t.concatenate(tr);
            node.setTransform(t);
            node.paint(graphics);
         }
    }

    private void graphicStrokeLineString(Shape shape, Image image, float size,
                Graphics2D graphics) {
        List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(shape, size, image.getWidth(null), image.getHeight(null));
        for (AffineTransform t : transforms) {
            graphics.drawImage(image, t, null);
        }
    }
    private List<AffineTransform> getGraphicStrokeLineStringTransforms(Shape shape, float size, double width, double height) {
        List<AffineTransform> transforms = new ArrayList<AffineTransform>();
        double shapeHeight = size;
        double factor = shapeHeight / height;
        double shapeWidth = width * factor;
        AffineTransform scaleTransform = AffineTransform.
        getScaleInstance(factor, factor);
        AffineTransform translation = AffineTransform.
        getTranslateInstance( -(0.5) * width, -(0.5) * height);
        GeneralPath path = (GeneralPath) shape;
        PathIterator pathIterator = path.getPathIterator(null);
        DirectPositionList points = new DirectPositionList();
        while (!pathIterator.isDone()) {
            double[] coords = new double[6];
            int type = pathIterator.currentSegment(coords);
            if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
                points.add(new DirectPosition(coords[0], coords[1]));
            }
            pathIterator.next();
        }
        GM_LineString line = Operateurs.resamping(new GM_LineString(points), shapeWidth);
        for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
            DirectPosition p1 = line.getControlPoint(i);
            DirectPosition p2 = line.getControlPoint(i+1);
            DirectPosition p = new DirectPosition((p1.getX() + p2.getX()) / 2,
                        (p1.getY() + p2.getY()) / 2);
            AffineTransform transform = AffineTransform.
            getTranslateInstance(p.getX(), p.getY());
            transform.concatenate(scaleTransform);
            transform.concatenate(AffineTransform.
                        getRotateInstance(new Angle(p1,p2).getValeur()));
            transform.concatenate(translation);
            transforms.add(transform);
        }
        return transforms;
    }
}
