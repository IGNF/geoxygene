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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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

  @XmlElement(name = "ColorMap")
  ColorMap colorMap = null;

  public ColorMap getColorMap() {
    return this.colorMap;
  }

  public void setColorMap(ColorMap colorMap) {
    this.colorMap = colorMap;
  }

//  @SuppressWarnings("unchecked")
//  @Override
//  public void paint(IFeature feature, Viewport viewport, Graphics2D graphics) {
//    if (feature.getGeom() == null || viewport == null) {
//      return;
//    }
//    if (this.getShadow() != null) {
//      Color shadowColor = this.getShadow().getColor();
//      double translate_x = -5;
//      double translate_y = -5;
//      if (this.getShadow().getDisplacement() != null) {
//        translate_x = this.getShadow().getDisplacement().getDisplacementX();
//        translate_y = this.getShadow().getDisplacement().getDisplacementY();
//      }
//      graphics.setColor(shadowColor);
//      List<Shape> shapes = new ArrayList<Shape>();
//      if (feature.getGeom().isPolygon()) {
//        try {
//          Shape shape = viewport.toShape(feature.getGeom().translate(
//              translate_x, translate_y, 0));
//          if (shape != null) {
//            shapes.add(shape);
//          }
//        } catch (NoninvertibleTransformException e) {
//          e.printStackTrace();
//        }
//      } else {
//        if (feature.getGeom().isMultiSurface()) {
//          for (GM_OrientableSurface surface : ((GM_MultiSurface<GM_OrientableSurface>) feature
//              .getGeom())) {
//            try {
//              Shape shape = viewport.toShape(surface.translate(translate_x,
//                  translate_y, 0));
//              if (shape != null) {
//                shapes.add(shape);
//              }
//            } catch (NoninvertibleTransformException e) {
//              e.printStackTrace();
//            }
//          }
//        }
//      }
//      for (Shape shape : shapes) {
//        this.fillPolygon(shape, viewport, graphics);
//      }
//    }
//    Color fillColor = null;
//    float fillOpacity = 1f;
//    if (this.getFill() != null) {
//      fillColor = this.getFill().getColor();
//      fillOpacity = this.getFill().getFillOpacity();
//    }
//    if (this.getColorMap() != null
//        && this.getColorMap().getInterpolate() != null) {
//      double value = ((Number) feature.getAttribute(this.getColorMap()
//          .getInterpolate().getLookupvalue())).doubleValue();
//      int rgb = this.getColorMap().getColor(value);
//      fillColor = new Color(rgb);
//    }
//    if (fillColor != null && fillOpacity > 0f) {
//      graphics.setColor(fillColor);
//      List<Shape> shapes = new ArrayList<Shape>();
//      if (feature.getGeom().isPolygon()) {
//        try {
//          Shape shape = viewport.toShape(feature.getGeom());
//          if (shape != null) {
//            shapes.add(shape);
//          }
//        } catch (NoninvertibleTransformException e) {
//          e.printStackTrace();
//        }
//      } else {
//        if (feature.getGeom().isMultiSurface()) {
//          for (GM_OrientableSurface surface : ((GM_MultiSurface<GM_OrientableSurface>) feature
//              .getGeom())) {
//            try {
//              Shape shape = viewport.toShape(surface);
//              if (shape != null) {
//                shapes.add(shape);
//              }
//            } catch (NoninvertibleTransformException e) {
//              e.printStackTrace();
//            }
//          }
//        }
//      }
//      for (Shape shape : shapes) {
//        if (this.getFill() == null || this.getFill().getGraphicFill() == null) {
//          this.fillPolygon(shape, viewport, graphics);
//        } else {
//          List<Graphic> graphicList = this.getFill().getGraphicFill()
//              .getGraphics();
//          for (Graphic graphic : graphicList) {
//            this.graphicFillPolygon(shape, graphic, viewport, graphics);
//          }
//        }
//      }
//    }
//    if (this.getStroke() != null) {
//      if (this.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_MITER) {
//        this.getStroke().setStrokeLineCap(BasicStroke.CAP_SQUARE);
//      } else if (this.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_BEVEL) {
//        this.getStroke().setStrokeLineCap(BasicStroke.CAP_BUTT);
//      } else if (this.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_ROUND) {
//        this.getStroke().setStrokeLineCap(BasicStroke.CAP_ROUND);
//      } else {
//        AbstractSymbolizer.logger.error("Stroke Line Join undefined."); //$NON-NLS-1$
//      }
//      float strokeOpacity = this.getStroke().getStrokeOpacity();
//      if (this.getStroke().getGraphicType() == null && strokeOpacity > 0f) {
//        // Solid color
//        Color color = this.getStroke().getColor();
//        double scale = 1;
//        if (!this.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
//          try {
//            scale = viewport.getModelToViewTransform().getScaleX();
//          } catch (NoninvertibleTransformException e) {
//            e.printStackTrace();
//          }
//        }
//        BasicStroke bs = (BasicStroke) this.getStroke().toAwtStroke(
//            (float) scale);
//        graphics.setColor(color);
//        if (feature.getGeom().isPolygon()) {
//          this.drawPolygon((GM_Polygon) feature.getGeom(), viewport, graphics,
//              bs);
//        } else {
//          if (feature.getGeom().isMultiSurface()) {
//            for (GM_OrientableSurface surface : ((GM_MultiSurface<GM_OrientableSurface>) feature
//                .getGeom())) {
//              this.drawPolygon((GM_Polygon) surface, viewport, graphics, bs);
//            }
//          }
//        }
//      }
//    }
//  }
//
//  private void graphicFillPolygon(Shape shape, Graphic graphic,
//      Viewport viewport, Graphics2D graphics) {
//    if (shape == null || viewport == null || graphic == null) {
//      return;
//    }
//    float size = graphic.getSize();
//    graphics.setClip(shape);
//    for (ExternalGraphic external : graphic.getExternalGraphics()) {
//      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
//        Image image = external.getOnlineResource();
//        this.graphicFillPolygon(shape, image, size, graphics);
//      } else {
//        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
//          GraphicsNode node = external.getGraphicsNode();
//          this.graphicFillPolygon(shape, node, size, graphics);
//        }
//      }
//      return;
//    }
//    int markShapeSize = 200;
//    for (Mark mark : graphic.getMarks()) {
//      Shape markShape = mark.toShape();
//      AffineTransform translate = AffineTransform.getTranslateInstance(
//          markShapeSize / 2, markShapeSize / 2);
//      if (graphic.getRotation() != 0) {
//        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI
//            * graphic.getRotation() / 180.0);
//        translate.concatenate(rotate);
//      }
//      AffineTransform scaleTransform = AffineTransform.getScaleInstance(
//          markShapeSize, markShapeSize);
//      translate.concatenate(scaleTransform);
//      Shape tranlatedShape = translate.createTransformedShape(markShape);
//      BufferedImage buff = new BufferedImage(markShapeSize, markShapeSize,
//          BufferedImage.TYPE_INT_ARGB);
//      Graphics2D g = (Graphics2D) buff.getGraphics();
//      g.setColor(mark.getFill().getColor());
//      g.fill(tranlatedShape);
//      this.graphicFillPolygon(shape, buff, size, graphics);
//    }
//  }
//
//  private void graphicFillPolygon(Shape shape, Image image, float size,
//      Graphics2D graphics) {
//    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
//    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
//    Double shapeHeight = new Double(size);
//    double factor = shapeHeight / image.getHeight(null);
//    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
//    AffineTransform transform = AffineTransform.getTranslateInstance(shape
//        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
//    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
//        shapeHeight.intValue(), Image.SCALE_FAST);
//    BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
//        shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
//    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
//    ParameterBlock p = new ParameterBlock();
//    p.addSource(buff);
//    p.add(width.intValue());
//    p.add(height.intValue());
//    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
//    BufferedImage bufferedImage = im.getAsBufferedImage();
//    graphics.drawImage(bufferedImage, transform, null);
//    bufferedImage.flush();
//    im.dispose();
//    scaledImage.flush();
//    buff.flush();
//  }
//
//  private void graphicFillPolygon(Shape shape, GraphicsNode node, float size,
//      Graphics2D graphics) {
//    AffineTransform translate = AffineTransform.getTranslateInstance(-node
//        .getBounds().getMinX(), -node.getBounds().getMinY());
//    node.setTransform(translate);
//    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
//        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
//    node.paint((Graphics2D) buff.getGraphics());
//    this.graphicFillPolygon(shape, buff, size, graphics);
//  }
//
//  private void fillPolygon(Shape shape, Viewport viewport, Graphics2D graphics) {
//    if (shape == null || viewport == null) {
//      return;
//    }
//    graphics.fill(shape);
//  }
//
//  private void drawPolygon(GM_Polygon polygon, Viewport viewport,
//      Graphics2D graphics, BasicStroke stroke) {
//    if (polygon == null || viewport == null) {
//      return;
//    }
//    List<Shape> shapes = new ArrayList<Shape>(0);
//    try {
//      Shape shape = viewport.toShape(polygon.getExterior());
//      if (shape != null) {
//        shapes.add(shape);
//      } else {
//        if (AbstractSymbolizer.logger.isTraceEnabled()) {
//          AbstractSymbolizer.logger.trace("null shape for " + polygon); //$NON-NLS-1$
//          AbstractSymbolizer.logger
//              .trace("ring = " + polygon.exteriorLineString()); //$NON-NLS-1$
//        }
//      }
//      for (IRing ring : polygon.getInterior()) {
//        shape = viewport.toShape(ring);
//        if (shape != null) {
//          shapes.add(shape);
//        } else {
//          if (AbstractSymbolizer.logger.isTraceEnabled()) {
//            AbstractSymbolizer.logger.trace("null shape for " + polygon); //$NON-NLS-1$
//            AbstractSymbolizer.logger.trace("ring = " + ring); //$NON-NLS-1$
//          }
//        }
//      }
//    } catch (NoninvertibleTransformException e) {
//      e.printStackTrace();
//    }
//    for (Shape shape : shapes) {
//      Shape outline = stroke.createStrokedShape(shape);
//      graphics.draw(shape);
//      graphics.setColor(this.getStroke().getColor());
//
//      graphics.fill(outline);
//    }
//  }
}
