/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
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

  @XmlElement(name = "ColorMap")
  ColorMap colorMap = null;

  public ColorMap getColorMap() {
    return this.colorMap;
  }

  public void setColorMap(ColorMap colorMap) {
    this.colorMap = colorMap;
  }

  @XmlElement(name = "PerpendicularOffset")
  double perpendicularOffset = 0;

  public double getPerpendicularOffset() {
    return this.perpendicularOffset;
  }

  public void setPerpendicularOffset(double perpendicularOffset) {
    this.perpendicularOffset = perpendicularOffset;
  }

  @Override
  public void paint(FT_Feature feature, Viewport viewport, Graphics2D graphics) {
    GM_Object geometry = feature.getGeom();
    if (this.getGeometryPropertyName() != null
        && !this.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
      geometry = (GM_Object) feature.getAttribute(this
          .getGeometryPropertyName());
    }
    if (geometry == null) {
      return;
    }
    if (this.getStroke() == null) {
      return;
    }
    double scale = 1;
    if (!this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scale = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    graphics.setStroke(this.getStroke().toAwtStroke((float) scale));
    this.paintShadow(geometry, viewport, graphics);
    if (this.getStroke().getGraphicType() == null) {
      List<Shape> shapes = this.getShapeList(geometry, viewport, false);
      if (this.getColorMap() != null) {
        graphics.setColor(new Color(this.getColorMap().getColor(
            ((Double) feature
                .getAttribute(this.getColorMap().getPropertyName()))
                .doubleValue())));
      } else {
        graphics.setColor(this.getStroke().getColor());
      }
      for (Shape shape : shapes) {
        graphics.draw(shape);
      }
    } else {
      if (this.getStroke().getGraphicType().getClass().isAssignableFrom(
          GraphicFill.class)) {
        List<Shape> shapes = this.getShapeList(geometry, viewport, true);
        // GraphicFill
        List<Graphic> graphicList = ((GraphicFill) this.getStroke()
            .getGraphicType()).getGraphics();
        for (Graphic graphic : graphicList) {
          for (Shape shape : shapes) {
            this.graphicFillLineString(shape, graphic, viewport, graphics);
          }
        }
      } else {
        // GraphicStroke
        List<Shape> shapes = this.getShapeList(geometry, viewport, false);
        List<Graphic> graphicList = ((GraphicStroke) this.getStroke()
            .getGraphicType()).getGraphics();
        for (Graphic graphic : graphicList) {
          for (Shape shape : shapes) {
            this.graphicStrokeLineString(shape, graphic, viewport, graphics);
          }
        }
      }
    }
  }

  /**
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  private List<Shape> getShapeList(GM_Object geometry, Viewport viewport,
      boolean fill) {
    if (geometry.isLineString() || geometry.isPolygon()) {
      GM_LineString line = (GM_LineString) ((geometry.isLineString()) ? geometry
          : ((GM_Polygon) geometry).exteriorLineString());
      if (this.getPerpendicularOffset() != 0) {
        GM_MultiCurve<GM_LineString> offsetCurve = JtsAlgorithms.offsetCurve(
            line, this.getPerpendicularOffset());
        List<Shape> shapes = new ArrayList<Shape>();
        for (GM_LineString l : offsetCurve) {
          shapes.addAll(this.getLineStringShapeList(l, viewport, fill));
        }
        return shapes;
      }
      return this.getLineStringShapeList(line, viewport, fill);
    }
    if (geometry.isMultiCurve()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (GM_OrientableCurve line : (GM_MultiCurve<GM_OrientableCurve>) geometry) {
        if (this.getPerpendicularOffset() != 0) {
          GM_MultiCurve<GM_LineString> offsetCurve = JtsAlgorithms.offsetCurve(
              (GM_LineString) line, this.getPerpendicularOffset());
          for (GM_LineString l : offsetCurve) {
            shapes.addAll(this.getLineStringShapeList(l, viewport, fill));
          }
        } else {
          shapes.addAll(this.getLineStringShapeList(line, viewport, fill));
        }
      }
      return shapes;
    }
    if (geometry.isMultiSurface()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (GM_OrientableSurface surface : (GM_MultiSurface<GM_OrientableSurface>) geometry) {
        try {
          Shape shape = viewport.toShape(fill ? surface.buffer(this.getStroke()
              .getStrokeWidth() / 2) : surface);
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
      return shapes;
    }
    return null;
  }

  private List<Shape> getLineStringShapeList(GM_OrientableCurve line,
      Viewport viewport, boolean fill) {
    List<Shape> shapes = new ArrayList<Shape>();
    try {
      Shape shape = viewport.toShape(fill ? line.buffer(this.getStroke()
          .getStrokeWidth() / 2) : line);
      if (shape != null) {
        shapes.add(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    return shapes;
  }

  @SuppressWarnings("unchecked")
  private void paintShadow(GM_Object geometry, Viewport viewport,
      Graphics2D graphics) {
    if (this.getShadow() != null) {
      Color shadowColor = this.getShadow().getColor();
      double translate_x = -5;
      double translate_y = -5;
      if (this.getShadow().getDisplacement() != null) {
        translate_x = this.getShadow().getDisplacement().getDisplacementX();
        translate_y = this.getShadow().getDisplacement().getDisplacementY();
      }
      graphics.setColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isLineString()) {
        try {
          Shape shape = viewport.toShape(geometry.translate(translate_x,
              translate_y, 0));
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (geometry.isMultiCurve()) {
          for (GM_OrientableCurve line : (GM_MultiCurve<GM_OrientableCurve>) geometry) {
            try {
              Shape shape = viewport.toShape(line.translate(translate_x,
                  translate_y, 0));
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
  }

  private void graphicFillLineString(Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
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
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(size,
        size);
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        AffineTransform transform = AffineTransform.getTranslateInstance(
            (i + 0.5) * size + shape.getBounds2D().getMinX(), (j + 0.5) * size
                + shape.getBounds2D().getMinY());
        transform.concatenate(scaleTransform);
        for (Shape markShape : shapes) {
          Shape tranlatedShape = transform.createTransformedShape(markShape);
          graphics.fill(tranlatedShape);
        }
      }
    }
  }

  private void graphicFillLineString(Shape shape, GraphicsNode node,
      float size, Graphics2D graphics) {
    AffineTransform translate = AffineTransform.getTranslateInstance(-node
        .getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    this.graphicFillLineString(shape, buff, size, graphics);
  }

  private void graphicFillLineString(Shape shape, Image image, float size,
      Graphics2D graphics) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight.doubleValue() / image.getHeight(null);
    Double shapeWidth = new Double(image.getWidth(null) * factor);
    AffineTransform transform = AffineTransform.getTranslateInstance(shape
        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
        shapeHeight.intValue(), Image.SCALE_FAST);
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(), shapeHeight
        .intValue(), BufferedImage.TYPE_INT_ARGB);
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
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    // graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
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
    List<AffineTransform> transforms = this
        .getGraphicStrokeLineStringTransforms(shape, size, 1, 1);
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
    List<AffineTransform> transforms = this
        .getGraphicStrokeLineStringTransforms(shape, size, width, height);
    for (AffineTransform t : transforms) {
      AffineTransform tr = AffineTransform.getTranslateInstance(-node
          .getBounds().getMinX(), -node.getBounds().getMinY());
      t.concatenate(tr);
      node.setTransform(t);
      node.paint(graphics);
    }
  }

  private void graphicStrokeLineString(Shape shape, Image image, float size,
      Graphics2D graphics) {
    List<AffineTransform> transforms = this
        .getGraphicStrokeLineStringTransforms(shape, size,
            image.getWidth(null), image.getHeight(null));
    for (AffineTransform t : transforms) {
      graphics.drawImage(image, t, null);
    }
  }

  private List<AffineTransform> getGraphicStrokeLineStringTransforms(
      Shape shape, float size, double width, double height) {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    double shapeHeight = size;
    double factor = shapeHeight / height;
    double shapeWidth = width * factor;
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor,
        factor);
    AffineTransform translation = AffineTransform.getTranslateInstance(-(0.5)
        * width, -(0.5) * height);
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
    GM_LineString line = Operateurs.resamping(new GM_LineString(points),
        shapeWidth);
    for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
      DirectPosition p1 = line.getControlPoint(i);
      DirectPosition p2 = line.getControlPoint(i + 1);
      DirectPosition p = new DirectPosition((p1.getX() + p2.getX()) / 2, (p1
          .getY() + p2.getY()) / 2);
      AffineTransform transform = AffineTransform.getTranslateInstance(
          p.getX(), p.getY());
      transform.concatenate(scaleTransform);
      transform.concatenate(AffineTransform.getRotateInstance(new Angle(p1, p2)
          .getValeur()));
      transform.concatenate(translation);
      transforms.add(transform);
    }
    return transforms;
  }
}
