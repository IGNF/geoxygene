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

package fr.ign.cogit.geoxygene.appli.render;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.util.glu.GLU.GLU_TESS_BEGIN;
import static org.lwjgl.util.glu.GLU.GLU_TESS_COMBINE;
import static org.lwjgl.util.glu.GLU.GLU_TESS_END;
import static org.lwjgl.util.glu.GLU.GLU_TESS_VERTEX;
import static org.lwjgl.util.glu.GLU.gluNewTess;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.StyleBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;
import fr.ign.cogit.geoxygene.appli.render.stroke.TextStroke;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.GraphicStroke;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author JeT
 * Utility methods for GL Rendering
 */
public final class RenderGL11Util {

  private static final Logger logger = Logger.getLogger(RenderGL11Util.class.getName()); // logger

  /**
   * Private constructor. Should not be used.
   */
  private RenderGL11Util() {
  }

  /**
   * Draw a geometry on the given graphics.
   * @param geometry the geometry
   * @param viewport the viewport
   * @param graphics the graphics
   */
  @SuppressWarnings("unchecked")
  public static void draw(final IGeometry geometry, final Viewport viewport, final double opacity) {
    logger.debug("public static void draw(final IGeometry geometry, final Viewport viewport, double opacity)");
    if (geometry.isPolygon()) {
      GM_Polygon polygon = (GM_Polygon) geometry;
      try {
        Shape shape = viewport.toShape(polygon.exteriorLineString());
        if (shape != null) {
          throw new IllegalStateException("graphics.draw(shape); should be replaced by a GL function");
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < polygon.sizeInterior(); i++) {
        try {
          Shape shape = viewport.toShape(polygon.interiorLineString(i));
          if (shape != null) {
            throw new IllegalStateException("graphics.draw(shape); should be replaced by a GL function");
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
    } else {
      if (geometry.isMultiSurface() || geometry.isMultiCurve()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderGL11Util.draw(element, viewport, opacity);
        }
      } else {
        try {
          Shape shape = viewport.toShape(geometry);
          if (shape != null) {
            throw new IllegalStateException("graphics.draw(shape); should be replaced by a GL function");
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * @param geometry geometry to fill
   * @param viewport viewport
   * @param graphics graphics to draw into
   */
  @SuppressWarnings("unchecked")
  public static void fill(final IGeometry geometry, final Viewport viewport, final double opacity) {
    if (geometry.isPolygon()) {
      try {
        Shape shape = viewport.toShape(geometry);
        if (shape != null) {
          glFillNearlySimpleShape(shape);
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    } else {
      if (geometry.isMultiSurface()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderGL11Util.fill(element, viewport, opacity);
        }
      }
    }
  }

  /**
   * Entry point for drawing anything. This method dispatches to other paint methods depending on the Symbolizer class type.
   * If the symbolizer type is not known it uses a default paint method.
   */
  public static void paint(final Symbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    if (PointSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((PointSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (LineSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((LineSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (PolygonSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((PolygonSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (RasterSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((RasterSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (TextSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((TextSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (ThematicSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((ThematicSymbolizer) symbolizer, feature, viewport, opacity);
      return;
    }
    if (feature.getGeom() == null) {
      logger.debug("geom " + feature + " has an empty geometry");
      return;
    }
    if (feature.getGeom().isPolygon() || feature.getGeom().isMultiSurface()) {
      glColor4f(1f, 1f, 0f, 0.5f);
      RenderGL11Util.fill(feature.getGeom(), viewport, opacity);
      return;
    }
    defaultPaint(feature, viewport, opacity);

  }

  /**
   * default paint method if no other has been found
   * @param feature
   * @param viewport
   * @param opacity
   */
  private static void defaultPaint(final IFeature feature, final Viewport viewport, final double opacity) {
    // java.awt.Stroke bs = new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    glColor4f(1f, 1f, 0f, 0.5f);
    RenderGL11Util.draw(feature.getGeom(), viewport, opacity);
    try {
      glLineWidth(1.f);
      for (IDirectPosition position : viewport.toViewDirectPositionList(feature.getGeom().coord())) {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(position.getX() - 2, position.getY() - 2);
        shape.lineTo(position.getX() + 2, position.getY() - 2);
        shape.lineTo(position.getX() + 2, position.getY() + 2);
        shape.lineTo(position.getX() - 2, position.getY() + 2);
        shape.lineTo(position.getX() - 2, position.getY() - 2);
        glColor4f(1f, 1f, 0f, 1.f);
        glFillNearlySimpleShape(shape);
        glColor(Color.black);
        glPolyline(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

  public static void paint(final PointSymbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    if (symbolizer.getGraphic() == null) {
      return;
    }
    Point2D point;
    // IGeometry geometry = feature.getGeom();
    // if (symbolizer.getGeometryPropertyName() != null
    //          && !symbolizer.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
    // geometry = (IGeometry) feature.getAttribute(symbolizer
    // .getGeometryPropertyName());
    // }
    IGeometry geometry = getGeometry(symbolizer.getGeometryPropertyName(), feature);
    if (geometry == null) {
      return;
    }
    try {
      point = viewport.toViewPoint(geometry.centroid());
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
      return;
    }
    for (Mark mark : symbolizer.getGraphic().getMarks()) {
      Shape markShape = mark.toShape();
      float size = symbolizer.getGraphic().getSize();
      double scale = 1;
      if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
        try {
          scale = viewport.getModelToViewTransform().getScaleX();
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
      size *= scale;
      AffineTransform at = AffineTransform.getTranslateInstance(point.getX(), point.getY());
      at.rotate(-Double.parseDouble(symbolizer.getGraphic().getRotation().evaluate(feature).toString()) * Math.PI / 180.0);
      at.scale(size, size);
      markShape = at.createTransformedShape(markShape);

      if (symbolizer.getColorMap() != null) {
        try {
          glColor(getColorWithOpacity(new Color(symbolizer.getColorMap().getColor(
              Double.parseDouble(feature.getAttribute(symbolizer.getColorMap().getPropertyName()).toString()))), opacity));
        } catch (NumberFormatException e) {
        }
      } else if (symbolizer.getCategorizedMap() != null) {
        Object value = feature.getAttribute(symbolizer.getCategorizedMap().getPropertyName());
        int rgb = symbolizer.getCategorizedMap().getColor(value);
        glColor(getColorWithOpacity(new Color(rgb), opacity));
      } else {
        Color color = mark.getFill() == null ? Color.gray : mark.getFill().getColor();
        glColor(getColorWithOpacity(color, opacity));
      }
      glFillNearlySimpleShape(markShape);

      glStroke(mark.getStroke().toAwtStroke((float) scale));
      if (symbolizer.getColorMap() != null || symbolizer.getCategorizedMap() != null) {
        Color color = Color.black;
        glColor(getColorWithOpacity(color, opacity));
      } else {
        Color color = mark.getStroke() == null ? Color.black : mark.getStroke().getColor();
        glColor(getColorWithOpacity(color, opacity));
      }
      glPolyline(markShape);
    }
    for (ExternalGraphic theGraphic : symbolizer.getGraphic().getExternalGraphics()) {
      Image onlineImage = theGraphic.getOnlineResource();
      glDrawImage(onlineImage, (int) point.getX() - onlineImage.getWidth(null) / 2, (int) point.getY() - onlineImage.getHeight(null) / 2, null);
    }
  }

  public static IGeometry getGeometry(final String propertyName, final IFeature feature) {
    IGeometry result = feature.getGeom();
    if (propertyName == null || propertyName.equalsIgnoreCase("geom")) {
      return result;
    }
    if (propertyName.equalsIgnoreCase("centroid")) {
      if (result != null) {
        result = result.centroid().toGM_Point();
      }
      return result;
    }
    if (propertyName.equalsIgnoreCase("startPoint")) {
      return result.coord().get(0).toGM_Point();
    }
    if (propertyName.equalsIgnoreCase("endPoint")) {
      return result.coord().get(result.numPoints() - 1).toGM_Point();
    }
    return (IGeometry) feature.getAttribute(propertyName);
  }

  public static void paint(final LineSymbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    // IGeometry geometry = feature.getGeom();
    // if (symbolizer.getGeometryPropertyName() != null
    //        && !symbolizer.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
    // geometry = (IGeometry) feature.getAttribute(symbolizer
    // .getGeometryPropertyName());
    // }
    IGeometry geometry = getGeometry(symbolizer.getGeometryPropertyName(), feature);
    if (geometry == null) {
      return;
    }
    if (symbolizer.getStroke() == null) {
      return;
    }
    double scaleUOMToPixels = 1;
    if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleUOMToPixels = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    // logger.warn("Replace by a GL function");
    // graphics.setStroke(symbolizer.getStroke().toAwtStroke((float) scaleUOMToPixels));
    paintShadow(symbolizer, geometry, viewport, opacity);
    if (symbolizer.getStroke().getGraphicType() == null) {
      List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, false);
      if (shapes != null) {
        if (symbolizer.getColorMap() != null) {
          try {
            Color color = getColorWithOpacity(new Color(symbolizer.getColorMap().getColor(
                Double.parseDouble(feature.getAttribute(symbolizer.getColorMap().getPropertyName()).toString()))), opacity);
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
          } catch (NumberFormatException e) {
          }
        } else if (symbolizer.getCategorizedMap() != null) {
          Object value = feature.getAttribute(symbolizer.getCategorizedMap().getPropertyName());
          int rgb = symbolizer.getCategorizedMap().getColor(value);
          Color color = new Color(rgb);
          GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) opacity);
          // graphics.setColor(getColorWithOpacity(new Color(rgb), opacity));
        } else {
          Color color = symbolizer.getStroke().getColor();
          GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) opacity);
          // graphics.setColor(getColorWithOpacity(symbolizer.getStroke().getColor(), opacity));
        }
        for (Shape shape : shapes) {
          glPolyline(shape);
        }
      }
    } else {
      if (symbolizer.getStroke().getGraphicType().getClass().isAssignableFrom(GraphicFill.class)) {
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, true);
        // GraphicFill
        List<Graphic> graphicList = ((GraphicFill) symbolizer.getStroke().getGraphicType()).getGraphics();
        for (Graphic graphic : graphicList) {
          for (Shape shape : shapes) {
            graphicFillLineString(symbolizer, shape, graphic, viewport, opacity);
          }
        }
      } else {
        // GraphicStroke
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, false);
        if (shapes != null) {
          List<Graphic> graphicList = ((GraphicStroke) symbolizer.getStroke().getGraphicType()).getGraphics();
          for (Graphic graphic : graphicList) {
            for (Shape shape : shapes) {
              graphicStrokeLineString(symbolizer, shape, graphic, viewport, opacity);
            }
          }
        }
      }
    }
  }

  /**
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @param fill true if the stroke width should be used to build the shapes, ie if they will be used for graphic fill
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  private static List<Shape> getShapeList(final LineSymbolizer symbolizer, final IGeometry geometry, final Viewport viewport, final boolean fill) {
    double scaleSymbolizerUOMToDataUOM = 1;
    if (symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleSymbolizerUOMToDataUOM = 1 / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    if (ICurve.class.isAssignableFrom(geometry.getClass()) || IPolygon.class.isAssignableFrom(geometry.getClass())) {
      ICurve curve = ICurve.class.isAssignableFrom(geometry.getClass()) ? (ICurve) geometry : ((IPolygon) geometry).exteriorLineString();
      if (symbolizer.getPerpendicularOffset() != 0) {
        IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(curve, symbolizer.getPerpendicularOffset() * scaleSymbolizerUOMToDataUOM);
        List<Shape> shapes = new ArrayList<Shape>();
        for (ILineString l : offsetCurve) {
          shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
        }
        return shapes;
      }
      return getLineStringShapeList(symbolizer, curve, viewport, fill, scaleSymbolizerUOMToDataUOM);
    }
    if (geometry.isMultiCurve()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableCurve line : (IMultiCurve<IOrientableCurve>) geometry) {
        if (symbolizer.getPerpendicularOffset() != 0) {
          IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve((ILineString) line, symbolizer.getPerpendicularOffset()
              * scaleSymbolizerUOMToDataUOM);
          for (ILineString l : offsetCurve) {
            shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
          }
        } else {
          shapes.addAll(getLineStringShapeList(symbolizer, line, viewport, fill, scaleSymbolizerUOMToDataUOM));
        }
      }
      return shapes;
    }
    if (geometry.isMultiSurface()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableSurface surface : ((IMultiSurface<IOrientableSurface>) geometry).getList()) {
        try {
          Shape shape = viewport.toShape(fill ? surface.buffer(symbolizer.getStroke().getStrokeWidth() / 2) : surface);
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

  /**
   * @param symbolizer a line symbolizer
   * @param line the geometry of the line
   * @param viewport the viewport used for rendering
   * @param fill true if the stroke width should be used to build the shapes, ie if they will be used for graphic fill
   * @param scale scale to go from the symbolizer's uom to the data uom
   * @return
   */
  private static List<Shape> getLineStringShapeList(final LineSymbolizer symbolizer, final IOrientableCurve line, final Viewport viewport,
      final boolean fill, final double scale) {
    List<Shape> shapes = new ArrayList<Shape>();
    try {
      Shape shape = viewport.toShape(fill ? line.buffer(symbolizer.getStroke().getStrokeWidth() * 0.5 * scale) : line);
      if (shape != null) {
        shapes.add(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    return shapes;
  }

  @SuppressWarnings("unchecked")
  private static void paintShadow(final LineSymbolizer symbolizer, final IGeometry geometry, final Viewport viewport, final double opacity) {
    if (symbolizer.getShadow() != null) {
      Color shadowColor = getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
      double translate_x = -5;
      double translate_y = -5;
      if (symbolizer.getShadow().getDisplacement() != null) {
        translate_x = symbolizer.getShadow().getDisplacement().getDisplacementX();
        translate_y = symbolizer.getShadow().getDisplacement().getDisplacementY();
      }
      glColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isLineString()) {
        try {
          Shape shape = viewport.toShape(geometry.translate(translate_x, translate_y, 0));
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
        glPolyline(shape);
      }
    }
  }

  private static void graphicFillLineString(final LineSymbolizer symbolizer, final Shape shape, final Graphic graphic, final Viewport viewport,
      final double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    logger.warn("Replace by a GL function");
    // graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
        Image image = external.getOnlineResource();
        graphicFillLineString(symbolizer, shape, image, size, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicFillLineString(symbolizer, shape, node, size, opacity);
        }
      }
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>(graphic.getMarks().size());
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      glColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
    }
    double width = shape.getBounds2D().getWidth();
    double height = shape.getBounds2D().getHeight();
    int xSize = (int) Math.ceil(width / size);
    int ySize = (int) Math.ceil(height / size);
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(size, size);
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        AffineTransform transform = AffineTransform.getTranslateInstance((i + 0.5) * size + shape.getBounds2D().getMinX(), (j + 0.5) * size
            + shape.getBounds2D().getMinY());
        transform.concatenate(scaleTransform);
        for (Shape markShape : shapes) {
          Shape tranlatedShape = transform.createTransformedShape(markShape);
          glFillNearlySimpleShape(tranlatedShape);
        }
      }
    }
  }

  private static void graphicFillLineString(final LineSymbolizer symbolizer, final Shape shape, final GraphicsNode node, final float size,
      final double opacity) {
    AffineTransform translate = AffineTransform.getTranslateInstance(-node.getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(), (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    graphicFillLineString(symbolizer, shape, buff, size, opacity);
  }

  private static void graphicFillLineString(final LineSymbolizer symbolizer, final Shape shape, final Image image, final float size,
      final double opacity) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight.doubleValue() / image.getHeight(null);
    Double shapeWidth = new Double(image.getWidth(null) * factor);
    AffineTransform transform = AffineTransform.getTranslateInstance(shape.getBounds2D().getMinX(), shape.getBounds2D().getMinY());
    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(), shapeHeight.intValue(), Image.SCALE_FAST);
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(), shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
    ParameterBlock p = new ParameterBlock();
    p.addSource(buff);
    p.add(width.intValue());
    p.add(height.intValue());
    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
    BufferedImage bufferedImage = im.getAsBufferedImage();
    glDrawImage(bufferedImage, transform, null);
    bufferedImage.flush();
    im.dispose();
    scaledImage.flush();
    buff.flush();
  }

  private static void graphicStrokeLineString(final LineSymbolizer symbolizer, final Shape shape, final Graphic graphic, final Viewport viewport,
      final double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    // graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
        Image image = external.getOnlineResource();
        graphicStrokeLineString(symbolizer, shape, image, size, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicStrokeLineString(symbolizer, shape, node, size, opacity);
        }
      }
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>();
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      logger.warn("Replace by a GL function");
      // graphics.setColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
    }
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(symbolizer, shape, size, 1, 1);
    for (AffineTransform t : transforms) {
      for (Shape markShape : shapes) {
        Shape tranlatedShape = t.createTransformedShape(markShape);
        logger.warn("Replace by a GL function");
        // graphics.fill(tranlatedShape);
      }
    }
  }

  private static void graphicStrokeLineString(final LineSymbolizer symbolizer, final Shape shape, final GraphicsNode node, final float size,
      final double opacity) {
    double width = node.getBounds().getWidth();
    double height = node.getBounds().getHeight();
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(symbolizer, shape, size, width, height);
    for (AffineTransform t : transforms) {
      AffineTransform tr = AffineTransform.getTranslateInstance(-node.getBounds().getMinX(), -node.getBounds().getMinY());
      t.concatenate(tr);
      node.setTransform(t);
      logger.warn("Replace by a GL function");
      // node.paint(graphics);
    }
  }

  private static void graphicStrokeLineString(final LineSymbolizer symbolizer, final Shape shape, final Image image, final float size,
      final double opacity) {
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(symbolizer, shape, size, image.getWidth(null), image.getHeight(null));
    for (AffineTransform t : transforms) {
      glDrawImage(image, t, null);
    }
  }

  private static List<AffineTransform> getGraphicStrokeLineStringTransforms(final LineSymbolizer symbolizer, final Shape shape, final float size,
      final double width, final double height) {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    double shapeHeight = size;
    double factor = shapeHeight / height;
    double shapeWidth = width * factor;
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor, factor);
    AffineTransform translation = AffineTransform.getTranslateInstance(-0.5 * width, -0.5 * height);
    GeneralPath path = (GeneralPath) shape;
    PathIterator pathIterator = path.getPathIterator(null);
    IDirectPositionList points = new DirectPositionList();
    while (!pathIterator.isDone()) {
      double[] coords = new double[6];
      int type = pathIterator.currentSegment(coords);
      if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
        points.add(new DirectPosition(coords[0], coords[1]));
      }
      pathIterator.next();
    }
    ILineString line = Operateurs.resampling(new GM_LineString(points), shapeWidth);
    for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
      IDirectPosition p1 = line.getControlPoint(i);
      IDirectPosition p2 = line.getControlPoint(i + 1);
      IDirectPosition p = new DirectPosition((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
      AffineTransform transform = AffineTransform.getTranslateInstance(p.getX(), p.getY());
      transform.concatenate(scaleTransform);
      transform.concatenate(AffineTransform.getRotateInstance(new Angle(p1, p2).getValeur()));
      transform.concatenate(translation);
      transforms.add(transform);
    }
    return transforms;
  }

  public static void paint(final PolygonSymbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    if (feature.getGeom() == null || viewport == null) {
      return;
    }
    //    if (symbolizer.getShadow() != null) {
    //      paintShadow(symbolizer, feature, viewport, opacity);
    //    }

    if (feature.getGeom().isPolygon()) {
      fillPolygon(symbolizer, (GM_Polygon) feature.getGeom(), viewport, opacity);
    } else if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
      glFillGeometry(feature.getGeom(), viewport);
    } else {

      logger.warn("Currently GLRendering knows only how to paint polygons and Multi surfaces, not " + feature.getGeom().getClass().getSimpleName());
    }
    double scale = 1;
    if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scale = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    BasicStroke bs = (BasicStroke) symbolizer.getStroke().toAwtStroke((float) scale);
    // Solid color
    glColor(getColorWithOpacity(symbolizer.getStroke().getColor(), opacity));
    if (feature.getGeom().isPolygon()) {
      outlinePolygon(symbolizer, (GM_Polygon) feature.getGeom(), viewport, opacity);
    } else if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
      outlineMultiSurface(symbolizer, (GM_MultiSurface<?>) feature.getGeom(), viewport, opacity);
    } else {
      outlineGeometry(symbolizer, feature.getGeom(), viewport, opacity);
      logger.warn("Currently GLRendering knows only how to paint polygons and Multi surfaces, not " + feature.getGeom().getClass().getSimpleName());
    }
  }

  //  private static void paintShadow(PolygonSymbolizer symbolizer, IFeature feature, Viewport viewport, double opacity) {
  //    {
  //      Color shadowColor = getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
  //      double translate_x = -5;
  //      double translate_y = -5;
  //      if (symbolizer.getShadow().getDisplacement() != null) {
  //        translate_x = symbolizer.getShadow().getDisplacement().getDisplacementX();
  //        translate_y = symbolizer.getShadow().getDisplacement().getDisplacementY();
  //      }
  //      glColor(shadowColor);
  //      List<Shape> shapes = new ArrayList<Shape>();
  //      if (feature.getGeom().isPolygon()) {
  //        try {
  //          Shape shape = viewport.toShape(feature.getGeom().translate(translate_x, translate_y, 0));
  //          if (shape != null) {
  //            shapes.add(shape);
  //          }
  //        } catch (NoninvertibleTransformException e) {
  //          e.printStackTrace();
  //        }
  //      } else {
  //        if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
  //          try {
  //            Shape shape = viewport.toShape(feature.getGeom().translate(translate_x, translate_y, 0));
  //            if (shape != null) {
  //              shapes.add(shape);
  //            }
  //          } catch (NoninvertibleTransformException e) {
  //            e.printStackTrace();
  //          }
  //        }
  //      }
  //      for (Shape shape : shapes) {
  //        fillPolygon(symbolizer, shape, viewport, opacity);
  //      }
  //    }
  //  }

  //  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, Graphic graphic, Viewport viewport, double opacity,
  //      double rotation) {
  //    if (shape == null || viewport == null || graphic == null) {
  //      return;
  //    }
  //    float size = graphic.getSize();
  //    logger.warn("Replace by a GL function");
  //    // graphics.setClip(shape);
  //    for (ExternalGraphic external : graphic.getExternalGraphics()) {
  //      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
  //        Image image = external.getOnlineResource();
  //        graphicFillPolygon(symbolizer, shape, image, size, opacity);
  //      } else {
  //        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
  //          GraphicsNode node = external.getGraphicsNode();
  //          graphicFillPolygon(symbolizer, shape, node, size, opacity);
  //        }
  //      }
  //      return;
  //    }
  //    int markShapeSize = 200;
  //    for (Mark mark : graphic.getMarks()) {
  //      Shape markShape = mark.toShape();
  //      AffineTransform translate = AffineTransform.getTranslateInstance(markShapeSize / 2, markShapeSize / 2);
  //      if (graphic.getRotation() != null) {
  //        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI * rotation / 180.0);
  //        translate.concatenate(rotate);
  //      }
  //      AffineTransform scaleTransform = AffineTransform.getScaleInstance(markShapeSize, markShapeSize);
  //      translate.concatenate(scaleTransform);
  //      Shape tranlatedShape = translate.createTransformedShape(markShape);
  //      BufferedImage buff = new BufferedImage(markShapeSize, markShapeSize, BufferedImage.TYPE_INT_ARGB);
  //      Graphics2D g = (Graphics2D) buff.getGraphics();
  //      g.setColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
  //      g.fill(tranlatedShape);
  //      graphicFillPolygon(symbolizer, shape, buff, size, opacity);
  //    }
  //  }

  //  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, Image image, float size, double opacity) {
  //    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
  //    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
  //    Double shapeHeight = new Double(size);
  //    double factor = shapeHeight / image.getHeight(null);
  //    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
  //    AffineTransform transform = AffineTransform.getTranslateInstance(shape.getBounds2D().getMinX(), shape.getBounds2D().getMinY());
  //    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(), shapeHeight.intValue(), Image.SCALE_FAST);
  //    BufferedImage buff = new BufferedImage(shapeWidth.intValue(), shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
  //    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
  //    ParameterBlock p = new ParameterBlock();
  //    p.addSource(buff);
  //    p.add(width.intValue());
  //    p.add(height.intValue());
  //    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
  //    BufferedImage bufferedImage = im.getAsBufferedImage();
  //    glDrawImage(bufferedImage, transform, null);
  //    bufferedImage.flush();
  //    im.dispose();
  //    scaledImage.flush();
  //    buff.flush();
  //  }

  //  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, GraphicsNode node, float size, double opacity) {
  //    AffineTransform translate = AffineTransform.getTranslateInstance(-node.getBounds().getMinX(), -node.getBounds().getMinY());
  //    node.setTransform(translate);
  //    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(), (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
  //    node.paint((Graphics2D) buff.getGraphics());
  //    graphicFillPolygon(symbolizer, shape, buff, size, opacity);
  //  }
  //
  //  private static void fillPolygon(PolygonSymbolizer symbolizer, Shape shape, Viewport viewport, double opacity) {
  //    if (shape == null || viewport == null) {
  //      return;
  //    }
  //
  //    if (symbolizer.getFill() != null) {
  //      float[] symbolizerColorComponenents = symbolizer.getFill().getColor().getComponents(null);
  //      Color color = new Color(symbolizerColorComponenents[0], symbolizerColorComponenents[1], symbolizerColorComponenents[2],
  //          symbolizerColorComponenents[3] * (float) opacity);
  //      glColor(color);
  //    }
  //
  //    // fill the shape
  //    glColor(getColorWithOpacity(symbolizer.getStroke().getColor(), opacity));
  //    glFill(shape, polygon.getInterior());
  //
  //  }

  /**
   * Outline a polygone using interior and exterior IRing
   * @param symbolizer
   * @param polygon
   * @param viewport
   * @param stroke
   * @param opacity
   */
  private static void outlinePolygon(final PolygonSymbolizer symbolizer, final GM_Polygon polygon, final Viewport viewport, final double opacity) {
    if (polygon == null || viewport == null) {
      return;
    }

    Shape shape;
    try {
      // draw the "holes" in the surface (inner frontier)
      for (IRing ring : polygon.getInterior()) {
        shape = viewport.toShape(ring);
        if (shape != null) {
          glPolyline(shape);
        }

      }
      // draw the outer frontier
      shape = viewport.toShape(polygon.getExterior());
      if (shape != null) {
        glPolyline(shape);
      }

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

  }

  /**
   * Outline a MultiSurface Object by looping over all Orientable surfaces of the list and outlining them
   * @param symbolizer
   * @param multiSurface
   * @param viewport
   * @param opacity
   */
  private static void outlineMultiSurface(final PolygonSymbolizer symbolizer, final GM_MultiSurface<?> multiSurface, final Viewport viewport,
      final double opacity) {
    for (IOrientableSurface surface : multiSurface.getList()) {
      outlineGeometry(symbolizer, surface, viewport, opacity);
    }

  }

  /**
   * Outline any geometry by transforming it to a Java2D Shape
   * @param symbolizer
   * @param geometry
   * @param viewport
   * @param stroke
   * @param opacity
   */
  private static void outlineGeometry(final PolygonSymbolizer symbolizer, final IGeometry geometry, final Viewport viewport, final double opacity) {
    if (geometry == null || viewport == null) {
      return;
    }

    if (geometry.isPolygon()) {
      outlinePolygon(symbolizer, (GM_Polygon) geometry, viewport, opacity);
      return;
    }
    if (geometry.isMultiSurface()) {
      outlineMultiSurface(symbolizer, (GM_MultiSurface<?>) geometry, viewport, opacity);
      return;
    }

    Shape shape;
    try {
      shape = viewport.toShape(geometry);
      if (shape != null) {
        glPolyline(shape);
      }

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

  }

  private static void fillPolygon(final PolygonSymbolizer symbolizer, final GM_Polygon polygon, final Viewport viewport, final double opacity) {
    if (polygon == null || viewport == null) {
      return;
    }

    List<Shape> innerShapes = new ArrayList<Shape>();
    try {
      // put all "holes" in a list 
      for (IRing ring : polygon.getInterior()) {
        Shape innerShape = viewport.toShape(ring);
        if (innerShape != null) {
          innerShapes.add(innerShape);
        }

      }
      // draw the outer & inner frontier
      Shape outerShape = viewport.toShape(polygon.getExterior());
      if (outerShape != null) {
        glFillComplexPolygon(outerShape, innerShapes);
      }

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

  }

  /**
   * A color with the specified opacity applied to the given color.
   * @param color the input color
   * @param opacity the opacity
   * @return a new color with the specified opacity applied to the given color
   */
  private static Color getColorWithOpacity(final Color color, final double opacity) {
    float[] symbolizerColorComponenents = color.getComponents(null);
    return new Color(symbolizerColorComponenents[0], symbolizerColorComponenents[1], symbolizerColorComponenents[2], symbolizerColorComponenents[3]
        * (float) opacity);
  }

  /** @param obj The FT_coverage to render This method shall be reworked. */
  public static void paint(final RasterSymbolizer symbolizer, final IFeature obj, final Viewport viewport, final double opacity) {
    FT_Coverage fcoverage = (FT_Coverage) obj;
    try {
      GridCoverage2D coverage = fcoverage.coverage();
      IEnvelope view = viewport.getEnvelopeInModelCoordinates();
      Envelope renderEnvelope = new Envelope(view.minX(), view.maxX(), view.minY(), view.maxY());
      GridCoverageRenderer renderer = new GridCoverageRenderer(coverage.getCoordinateReferenceSystem(), renderEnvelope, viewport.getLayerViewPanels()
          .iterator().next().getVisibleRect(), null);
      org.geotools.styling.RasterSymbolizer s = new StyleBuilder().createRasterSymbolizer();
      s.setOpacity(new FilterFactoryImpl().literal(opacity * symbolizer.getOpacity()));
      logger.warn("Replace by a GL function");
      // renderer.paint(coverage, s);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return;
  }

  public static void paint(final TextSymbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    if (symbolizer.getLabel() == null) {
      return;
    }
    Object value = feature.getAttribute(symbolizer.getLabel());
    String text = value == null ? null : value.toString();
    if (text != null) {
      paint(symbolizer, text, feature.getGeom(), viewport, opacity);
    }
  }

  /**
   * @param symbolizer a text symbolizer
   * @param text non null text
   * @param geometry the geometry of the feature
   * @param viewport the viewport to paint in
   * @param graphics the graphics to paint with
   */
  @SuppressWarnings("unchecked")
  public static void paint(final TextSymbolizer symbolizer, final String text, final IGeometry geometry, final Viewport viewport, final double opacity) {
    // Initialize the color with which to actually paint the text
    Color fillColor = getColorWithOpacity(Color.black, opacity);
    if (symbolizer.getFill() != null) {
      fillColor = getColorWithOpacity(symbolizer.getFill().getColor(), opacity);
    }
    // The scale
    double scaleUOMToPixels = 1;
    double scaleSymbolizerUOMToDataUOM = 1;
    if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleUOMToPixels = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    } else {
      try {
        scaleSymbolizerUOMToDataUOM = 1 / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    // Initialize the font
    java.awt.Font awtFont = null;
    if (symbolizer.getFont() != null) {
      awtFont = symbolizer.getFont().toAwfFont((float) scaleUOMToPixels);
    }
    if (awtFont == null) {
      awtFont = new java.awt.Font("Default", java.awt.Font.PLAIN, 10); //$NON-NLS-1$
    }
    glFont(awtFont);
    // Initialize the color for the halo around the text
    Color haloColor = getColorWithOpacity(Color.WHITE, opacity);
    float haloRadius = 1.0f;
    if (symbolizer.getHalo() != null) {
      if (symbolizer.getHalo().getFill() != null) {
        haloColor = getColorWithOpacity(symbolizer.getHalo().getFill().getColor(), opacity);
      }
      haloRadius = symbolizer.getHalo().getRadius();
    }
    LabelPlacement labelPlacement = symbolizer.getLabelPlacement();
    if (labelPlacement != null && labelPlacement.getPlacement() != null) {
      Placement placement = labelPlacement.getPlacement();
      if (PointPlacement.class.isAssignableFrom(placement.getClass())) {
        PointPlacement pointPlacement = (PointPlacement) placement;
        try {
          paint(pointPlacement, text, fillColor, haloColor, haloRadius, geometry.centroid(), viewport, scaleUOMToPixels);
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (LinePlacement.class.isAssignableFrom(placement.getClass())) {
          LinePlacement linePlacement = (LinePlacement) placement;
          float offset = linePlacement.getPerpendicularOffset() * (float) scaleSymbolizerUOMToDataUOM;
          IGeometry g = geometry;
          if (offset != 0.0f) {
            g = JtsAlgorithms.offsetCurve(geometry, offset);
          }
          if (IMultiCurve.class.isAssignableFrom(g.getClass())) {
            IMultiCurve<IOrientableCurve> multiCurve = (IMultiCurve<IOrientableCurve>) g;
            for (IOrientableCurve curve : multiCurve) {
              try {
                paint(linePlacement, text, null, fillColor, haloColor, haloRadius, curve, viewport, scaleUOMToPixels);
              } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
              }
            }
          } else {
            try {
              paint(linePlacement, text, null, fillColor, haloColor, haloRadius, g, viewport, scaleUOMToPixels);
            } catch (NoninvertibleTransformException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /**
   * @param linePlacement
   * @param text
   * @param fillColor
   * @param haloColor
   * @param haloRadius
   * @param geometry
   * @param viewport
   * @param graphics
   * @param scale
   * @throws NoninvertibleTransformException
   */
  private static void paint(final LinePlacement linePlacement, final String text, final Font font, final Color fillColor, final Color haloColor,
      final float haloRadius, IGeometry geometry, final Viewport viewport, final double scale) throws NoninvertibleTransformException {
    if (linePlacement.isGeneralizeLine()) {
      // we have to generalize the geometry first
      // double sigma = 20;
      logger.warn("Replace by a GL function");
      double sigma = 12.;
      // double sigma = graphics.getFontMetrics().getMaxAdvance() / scale;
      geometry = GaussianFilter.gaussianFilter(new GM_LineString(geometry.coord()), sigma, 1.0);
    }
    Shape lineShape = null;
    if (linePlacement.isAligned()) {
      // if the text should be aligned on the geometry
      lineShape = viewport.toShape(geometry);
    } else {
      // the expected behaviour here is not well specified
      // we decided to use the horizontal line cutting the envelope of the
      // geometry as the support line and to treat it as a standard text
      // symbolizer
      IEnvelope envelope = geometry.getEnvelope();
      double y = (envelope.minY() + envelope.maxY()) / 2;
      IDirectPosition p1 = new DirectPosition(envelope.minX(), y);
      IDirectPosition p2 = new DirectPosition(envelope.maxX(), y);
      lineShape = viewport.toShape(new GM_LineString(p1, p2));
    }
    if (lineShape == null) {
      // if there is no geometry, return
      return;
    }
    logger.warn("Replace by a GL function");
    Stroke s = new TextStroke(text, font, false, linePlacement.isRepeated(), false, linePlacement.getInitialGap() * (float) scale, linePlacement
        .getGap()
        * (float) scale);
    Shape textShape = s.createStrokedShape(lineShape);
    // halo
    if (haloColor != null) {
      glColor(haloColor);
      glStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      glPolyline(textShape);
    }
    glColor(fillColor);
    glFillNearlySimpleShape(textShape);
  }

  /**
   * @param pointPlacement
   * @param text
   * @param fillColor
   * @param haloColor
   * @param haloRadius
   * @param position
   * @param viewport
   * @param graphics
   * @param scale
   * @throws NoninvertibleTransformException
   */
  private static void paint(final PointPlacement pointPlacement, final String text, final Color fillColor, final Color haloColor,
      final float haloRadius, final IDirectPosition position, final Viewport viewport, final double scale) throws NoninvertibleTransformException {
    logger.warn("Replace by a GL function");
    // FontRenderContext frc = graphics.getFontRenderContext();
    float rotation = (float) (pointPlacement.getRotation() * Math.PI / 180);
    logger.warn("Replace by a GL function");
    // GlyphVector gv = graphics.getFont().createGlyphVector(frc, text);
    // Shape textShape = gv.getOutline();
    // Rectangle2D bounds = textShape.getBounds2D();
    // double width = bounds.getWidth();
    // double height = bounds.getHeight();
    // Point2D p = viewport.toViewPoint(position);
    // AnchorPoint anchorPoint = pointPlacement.getAnchorPoint();
    // float anchorPointX = (anchorPoint == null) ? 0.5f : anchorPoint.getAnchorPointX();
    // float anchorPointY = (anchorPoint == null) ? 0.5f : anchorPoint.getAnchorPointY();
    // Displacement displacement = pointPlacement.getDisplacement();
    // float displacementX = (displacement == null) ? 0.0f : displacement.getDisplacementX();
    // float displacementY = (displacement == null) ? 0.0f : displacement.getDisplacementY();
    // float tx = (float) (p.getX() + displacementX * scale);
    // float ty = (float) (p.getY() - displacementY * scale);
    // AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
    // t.rotate(rotation);
    // t.translate(-width * anchorPointX, height * anchorPointY);
    // textShape = t.createTransformedShape(textShape);
    // halo
    if (haloColor != null) {
      logger.warn("Replace by a GL function");
      // graphics.setColor(haloColor);
      // graphics.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      // graphics.draw(textShape);
    }
    // glColor(fillColor);
    // glFill(textShape);
  }

  /**
   * @param symbolizer
   * @param feature
   * @param viewport
   * @param graphics
   * @param opacity
   */
  @SuppressWarnings({
    "unchecked"
  })
  public static void paint(final ThematicSymbolizer symbolizer, final IFeature feature, final Viewport viewport, final double opacity) {
    if (feature.getGeom() == null || viewport == null) {
      return;
    }
    for (DiagramSymbolizer s : symbolizer.getSymbolizers()) {
      if (s.getDiagramType().equalsIgnoreCase("piechart")) { //$NON-NLS-1$
        // double size = 1.0;
        // for (DiagramSizeElement element : s.getDiagramSize()) {
        // if (element instanceof DiagramRadius) {
        // size = element.getValue();
        // }
        // }
        IDirectPosition position = symbolizer.getPoints().get(feature);
        Double size = symbolizer.getRadius().get(feature);
        if (position == null || size == null) {
          TriangulationJTS t = new TriangulationJTS("TRIANGLE"); //$NON-NLS-1$
          Chargeur.importAsNodes(feature, t);
          try {
            t.triangule("v"); //$NON-NLS-1$
          } catch (Exception e1) {
            e1.printStackTrace();
          }
          GM_MultiCurve<IOrientableCurve> contour = new GM_MultiCurve<IOrientableCurve>();
          if (feature.getGeom() instanceof IPolygon) {
            contour.add(((IPolygon) feature.getGeom()).exteriorLineString());
          } else {
            for (IPolygon surface : (IMultiSurface<IPolygon>) feature.getGeom()) {
              contour.add(surface.exteriorLineString());
            }
          }
          for (Arc a : t.getPopArcs()) {
            ((IPopulation<IFeature>) DataSet.getInstance().getPopulation("Triangulation")).add(new DefaultFeature(a.getGeometrie())); //$NON-NLS-1$
          }
          double maxDistance = Double.MIN_VALUE;
          Noeud maxNode = null;
          for (Arc a : t.getPopVoronoiEdges().select(feature.getGeom())) {
            if (!a.getGeometrie().intersectsStrictement(feature.getGeom())) {
              ((Population<DefaultFeature>) DataSet.getInstance().getPopulation("MedialAxis")).add(new DefaultFeature(a //$NON-NLS-1$
                  .getGeometrie()));
            }
          }
          for (Noeud n : t.getPopVoronoiVertices().select(feature.getGeom())) {
            double d = n.getGeometrie().distance(contour);
            if (d > maxDistance) {
              maxDistance = d;
              maxNode = n;
            }
          }
          size = maxDistance;
          if (maxNode == null) {
            // AbstractSymbolizer.logger.info(feature.getGeom());
            return;
          }
          position = maxNode.getGeometrie().getPosition();
          symbolizer.getPoints().put(feature, position);
          symbolizer.getRadius().put(feature, maxDistance);
        }
        Point2D point = null;
        try {
          point = viewport.toViewPoint(position);
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
          return;
        }
        double scale = 1;
        if (symbolizer.getUnitOfMeasure() != Symbolizer.PIXEL) {
          try {
            scale = viewport.getModelToViewTransform().getScaleX();
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        size *= scale;
        double startAngle = 0.0;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue().evaluate(feature)).doubleValue();
          // AbstractSymbolizer.logger.info(thematicClass.getClassLabel() + " "
          // + value);
          if (value == 0) {
            continue;
          }
          double arcAngle = 3.6 * value;
          // AbstractSymbolizer.logger.info("\t" + startAngle + " - " +
          // arcAngle);
          logger.warn("Replace by a GL function");
          glColor(getColorWithOpacity(thematicClass.getFill().getColor(), opacity));
          // graphics.fillArc((int) (point.getX() - size), (int) (point.getY() - size), (int) (2 * size), (int) (2 * size), (int) startAngle,
          // (int) arcAngle);
          glColor(Color.BLACK);
          // graphics.drawArc((int) (point.getX() - size), (int) (point.getY() - size), (int) (2 * size), (int) (2 * size), (int) startAngle,
          // (int) arcAngle);
          startAngle += arcAngle;
        }
      }
    }
  }

  /**
   * Draw a filled geometry with open GL
   * @param shape shape to draw
   */
  public static void glFillGeometry(final IGeometry geometry, final Viewport viewport) {
    if (geometry == null) {
      logger.warn("Rendering process tries to fill a null geometry...");
    }
    if (viewport == null) {
      logger.warn("Rendering process tries to fill a geometry with a null viewport...");
    }
    try {
      Shape shape = viewport.toShape(geometry);
      if (shape != null) {
        glFillNearlySimpleShape(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

  /**
   * Draw a filled Java2D shape with open GL. The shape has to be "nearly simple":
   * No edge can cross any other but two edges can be exactly over each other
   * to simulate "holes" in a surface
   * @param shape nearly simple shape to draw
   */
  public static void glFillNearlySimpleShape(final Shape shape) {
    if (shape == null) {
      logger.warn("Rendering process tries to fill a null shape...");
    }
    glColor4f(1f, 0f, 0f, 0.2f);
    glLineWidth(3.f);
    glFillComplexPolygon(shape, null);

    //    glColor4f(1f, 0f, 0f, 0.2f);
    //    glLineWidth(3.f);
    //    glDrawRawShape(shape, false);
  }

  /**
   * Draw a filled shape with open GL
   * @param outerShape Simple polyline describing the outer limit of the polygon
   * @param innerShapes list of simple polylines describing the inner limits of the polygon
   */
  private static void glFillComplexPolygon(final Shape outerShape, final List<Shape> innerShapes) {
    GLUtessellator tesselator = gluNewTess();
    // Set callback functions
    TessCallback callback = new TessCallback();
    tesselator.gluTessCallback(GLU_TESS_VERTEX, callback);
    tesselator.gluTessCallback(GLU_TESS_BEGIN, callback);
    tesselator.gluTessCallback(GLU_TESS_END, callback);
    tesselator.gluTessCallback(GLU_TESS_COMBINE, callback);

    tesselator.gluTessBeginPolygon(null);

    tesselator.gluTessBeginContour();
    PathIterator pathIterator = outerShape.getPathIterator(null);
    float[] pathCoords = new float[6];
    while (!pathIterator.isDone()) {
      double coords[] = new double[3];
      int segmentType = pathIterator.currentSegment(pathCoords);
      if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
        coords[0] = pathCoords[0];
        coords[1] = pathCoords[1];
        coords[2] = 0;
        tesselator.gluTessVertex(coords, 0, coords);
      }
      pathIterator.next();
    }

    tesselator.gluTessEndContour();

    if (innerShapes != null && !innerShapes.isEmpty()) {

      for (Shape innerShape : innerShapes) {
        tesselator.gluTessBeginContour();
        pathIterator = innerShape.getPathIterator(null);
        while (!pathIterator.isDone()) {
          double[] coords = new double[3];
          int segmentType = pathIterator.currentSegment(pathCoords);
          if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
            coords[0] = pathCoords[0];
            coords[1] = pathCoords[1];
            coords[2] = 0;
            tesselator.gluTessVertex(coords, 0, coords);
          }
          pathIterator.next();
        }

        tesselator.gluTessEndContour();
      }

    }

    tesselator.gluTessEndPolygon();

  }

  /**
   * set GL Color from a Java Color
   * @param Color the color to color
   */
  public static void glColor(final Color color) {
    glColor4f(color.getRed() / 255.f, color.getGreen() / 255.f, color.getBlue() / 255.f, color.getAlpha() / 255.f);
  }

  /**
   * set GL Font from a Java Font
   * @param Color the font to set
   */
  public static void glFont(final Font font) {
    logger.warn("no GL equivalent for Font defined...");

  }

  /**
   * Draw an outlined shape with open GL
   * @param shape shape to draw
   */
  private static void glPolyline(final Shape shape) {

    ParameterizedPolyline line = generateParameterizedPolyline(shape);
    glRenderPrimitive(line);

    //    //    ResamplerOperator resampler = new ResamplerOperator(0.5);
    //    CutterOperator cutter = new CutterOperator(10., 5.);
    //    try {
    //      cutter.addInput(line);
    //      List<DrawingPrimitive> primitives = cutter.apply();
    //      for (DrawingPrimitive primitive : primitives) {
    //        glRenderPrimitive(primitive);
    //      }
    //    } catch (Exception e) {
    //      logger.error(e.getMessage());
    //      e.printStackTrace();
    //    }

    //    //    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    //    // simple line
    //    // line with a given width
    //    glDrawLargePolyline(shape, new GeoDisplacementSinFunction(0.1, 4., 0., 0.));
    //    //    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    //    glColor(Color.black);
    //    glDrawRawShape(shape, false, false);
  }

  /**
   * Draw an outlined shape with open GL
   * @param shape shape to draw
   */
  private static void glRenderPrimitive(final DrawingPrimitive primitive) {
    if (primitive instanceof ParameterizedPolyline) {
      glRenderPolyline((ParameterizedPolyline) primitive);
      return;
    }
    throw new UnsupportedOperationException("Don't know how to render primitive " + primitive.getClass().getSimpleName());
  }

  /**
   * Draw a ParameterizedPolyline like a simple line
   * @param shape shape to draw
   */
  private static void glRenderPolyline(final ParameterizedPolyline line) {
    glBegin(GL_LINE_STRIP);
    for (int nPoint = 0; nPoint < line.getPointCount(); nPoint++) {
      glVertex2d(line.getPoint(nPoint).x, line.getPoint(nPoint).y);
    }

    glEnd();
  }

  //  /**
  //   * Draw a line by expanding the line width perpendicular to it's normal
  //   * @param shape shape to draw
  //   * @param width line width
  //   */
  //  private static void glDrawLargePolyline(Shape shape, double width) {
  //    List<Point2d> points = generatePointList(shape);
  //    glBegin(GL_QUAD_STRIP);
  //    Point2d prev = null, next = null, current = null; // prev, current and next point
  //    Vector2d prevNormal, normal = null;
  //    for (int nPoint = 0; nPoint < points.size(); nPoint++) {
  //      prev = nPoint > 0 ? points.get(nPoint - 1) : null;
  //      current = points.get(nPoint);
  //      next = nPoint < points.size() - 1 ? points.get(nPoint + 1) : null;
  //      prevNormal = normal; // store previous normal
  //      normal = computeNormal(prev, current, next, prevNormal);
  //      glVertex2d(current.x + width * normal.x, current.y + width * normal.y);
  //      glVertex2d(current.x - width * normal.x, current.y - width * normal.y);
  //    }
  //
  //    glEnd();
  //  }

  //  /**
  //   * Draw a line by expanding the line width perpendicular to it's normal
  //   * @param shape shape to draw
  //   * @param width line width
  //   */
  //  private static void glDrawLargePolyline(final Shape shape, final GeoDisplacementFunction1D f) {
  //    ParameterizedPolyline line = generateParameterizedPolyline(shape);
  //
  //    //    ResamplerOperator resampler = new ResamplerOperator(0.5);
  //    CutterOperator cutter = new CutterOperator(0.5, 0.3);
  //    try {
  //      cutter.addInput(line);
  //      line = (ParameterizedPolyline) cutter.apply();
  //
  //    } catch (Exception e) {
  //      logger.error(e.getMessage());
  //      e.printStackTrace();
  //    }
  //
  //    glBegin(GL_QUAD_STRIP);
  //    Point2d prev = null, next = null, current = null; // prev, current and next point
  //    Vector2d prevNormal, normal = null;
  //    for (int nPoint = 0; nPoint < line.getPointCount(); nPoint++) {
  //      prev = nPoint > 0 ? line.getPoint(nPoint - 1) : null;
  //      current = line.getPoint(nPoint);
  //      next = nPoint < line.getPointCount() - 1 ? line.getPoint(nPoint + 1) : null;
  //      prevNormal = normal; // store previous normal
  //      normal = computeNormal(prev, current, next, prevNormal);
  //      //      double width = f.displacement(line.getParameter(nPoint));
  //      double width = .2;
  //      glVertex2d(current.x + width * normal.x, current.y + width * normal.y);
  //      glVertex2d(current.x - width * normal.x, current.y - width * normal.y);
  //    }
  //
  //    glEnd();
  //  }

  private static Vector2d computeNormal(final Point2d prev, final Point2d current, final Point2d next, final Vector2d prevNormal) {
    Vector2d normal = new Vector2d();
    if (prev == null) {
      normal.x = current.y - next.y;
      normal.y = next.x - current.x;
    } else if (next == null) {
      normal.x = -current.y + prev.y;
      normal.y = -prev.x + current.x;
    } else {
      normal.x = prev.y - next.y;
      normal.y = next.x - prev.x;
    }
    // normalization if not null (Vector2d.normalize() method returns NaN if norm is 0!
    double norm = Math.sqrt(normal.x * normal.x + normal.y * normal.y);
    if (norm > 1.E-6) {
      normal.x /= norm;
      normal.y /= norm;
    }
    return normal;
  }

  /**
   * Draw a shape polygon using open GL. It can either fill the polygon or display only
   * the edges. No tesselation is done when filling, so the shape has to be simple and convex
   * @param shape Java2D shape to paint
   * @param close automatically close the polygon between start and end point
   * @param fill if true fills the polygon with current color. if false, draw only edges
   */
  private static void glDrawRawShape(final Shape shape, final boolean close, final boolean fill) {

    final int glDrawType = fill ? GL11.GL_POLYGON : close ? GL11.GL_LINE_LOOP : GL_LINE_STRIP;
    //    final int glDrawType = GL11.GL_LINE_LOOP;
    //    glLineWidth(5.f);

    boolean polygonStarted = false;

    PathIterator pathIterator = shape.getPathIterator(null);
    // System.err.print("polygon ");
    while (!pathIterator.isDone()) {
      float[] coords = new float[6];

      int segmentType = pathIterator.currentSegment(coords);
      switch (segmentType) {
      case PathIterator.SEG_CLOSE:
        if (polygonStarted) {
          GL11.glEnd();
          polygonStarted = false;
        }
      case PathIterator.SEG_MOVETO:
        if (polygonStarted) {
          GL11.glEnd(); // close the current polygon
          polygonStarted = false;
        }
        GL11.glBegin(glDrawType); // open a new one
        polygonStarted = true;
        GL11.glVertex2f(coords[0], coords[1]); // starting at the given position
        break;
      case PathIterator.SEG_CUBICTO:

        if (!polygonStarted) {
          GL11.glBegin(glDrawType); // start a new polygon if not already done
          polygonStarted = true;
        }
        GL11.glVertex2f(coords[4], coords[5]);
        break;
      case PathIterator.SEG_QUADTO:

        if (!polygonStarted) {
          GL11.glBegin(glDrawType); // start a new polygon if not already done
          polygonStarted = true;
        }
        GL11.glVertex2f(coords[2], coords[3]);
        break;

      case PathIterator.SEG_LINETO:

        if (!polygonStarted) {
          GL11.glBegin(glDrawType); // start a new polygon if not already done
          polygonStarted = true;
        }
        GL11.glVertex2f(coords[0], coords[1]);
        break;
      default:
        logger.warn("Draw GL shape do not know how to handle segment type " + segmentType);
      }
      //      if (coords[0] != 0. || coords[1] != 0.)
      // System.err.print("  -  " + coords[0] + "x" + coords[1]);
      pathIterator.next();
    }
    if (polygonStarted) {
      GL11.glEnd();
      polygonStarted = false;
    }
  }

  /**
   * Draw a shape polygon using open GL. It displays only the shape edges with colors depending on segment types.
   * This method has been implemented for debug purpose in order to display different colors
   * for the different shape's parts. Colors are not customizable
   * @param shape Java2D shape to paint
   */
  public static void glDrawRawShapeColoredSegment(final Shape shape) {

    Color closeColor = Color.blue;
    Color moveColor = Color.red;
    Color lineColor = Color.black;
    Color quadColor = Color.yellow;
    Color cubicColor = Color.pink;

    boolean polygonStarted = false;
    float startX = 0, startY = 0;
    float prevX = 0, prevY = 0;

    GL11.glBegin(GL_LINES); // start a new polygon if not already done
    PathIterator pathIterator = shape.getPathIterator(null);
    // System.err.print("polygon ");
    int nPoint = 0;
    while (!pathIterator.isDone()) {
      float[] coords = new float[6];

      int segmentType = pathIterator.currentSegment(coords);
      switch (segmentType) {
      case PathIterator.SEG_CLOSE:

        if (polygonStarted) {
          glColor(closeColor);
          GL11.glVertex2f(prevX, prevY);
          GL11.glVertex2f(startX, startY);
          polygonStarted = false;
        } else {
          logger.error("Close a non open segment");
        }
      case PathIterator.SEG_MOVETO:
        if (polygonStarted) {
          glColor(moveColor);
          GL11.glVertex2f(prevX, prevY);
          GL11.glVertex2f(coords[0], coords[1]);
          startX = prevX = coords[0];
          startY = prevY = coords[1];
          //          polygonStarted = true;
        } else {
          polygonStarted = true;
          startX = prevX = coords[0];
          startY = prevY = coords[1];
          //          logger.warn("move to " + startX + "x" + startY + " but polygon not started n = " + nPoint);
        }
        break;
      case PathIterator.SEG_CUBICTO:

        if (polygonStarted) {
          glColor(cubicColor);
          GL11.glVertex2f(prevX, prevY);
          GL11.glVertex2f(coords[4], coords[5]);
          prevX = coords[4];
          prevY = coords[5];
        } else {
          polygonStarted = true;
          startX = prevX = coords[4];
          startY = prevY = coords[5];
        }
        break;
      case PathIterator.SEG_QUADTO:

        if (polygonStarted) {
          glColor(quadColor);
          GL11.glVertex2f(prevX, prevY);
          GL11.glVertex2f(coords[2], coords[3]);
          prevX = coords[2];
          prevY = coords[3];
        } else {
          polygonStarted = true;
          startX = prevX = coords[2];
          startY = prevY = coords[3];
        }
        break;

      case PathIterator.SEG_LINETO:

        if (polygonStarted) {
          glColor(lineColor);
          GL11.glVertex2f(prevX, prevY);
          GL11.glVertex2f(coords[0], coords[1]);
          prevX = coords[0];
          prevY = coords[1];
          if (nPoint == 1) {
            logger.debug("First line point = " + prevX + "x" + prevY);
          }
        } else {
          polygonStarted = true;
          startX = prevX = coords[0];
          startY = prevY = coords[1];
        }
        break;
      default:
        logger.warn("Draw GL shape do not know how to handle segment type " + segmentType);
      }
      //      if (coords[0] != 0. || coords[1] != 0.)
      // System.err.print("  -  " + coords[0] + "x" + coords[1]);
      pathIterator.next();
    }
    GL11.glEnd();
  }

  private static void glDrawImage(final Image bufferedImage, final AffineTransform transform, final Object object) {
    logger.warn("no GL equivalent for DrawImage defined...");

  }

  private static void glDrawImage(final Image bufferedImage, final int x, final int y, final Object object) {
    logger.warn("no GL equivalent for DrawImage defined...");

  }

  private static void glStroke(final Stroke awtStroke) {
    logger.warn("no GL equivalent for Stroke defined...");

  }

  /**
   * Callback class used in gl tesselation process
   * @author JeT
   *
   */
  private static class TessCallback extends GLUtessellatorCallbackAdapter {
    @Override
    public void edgeFlag(final boolean boundaryEdge) {
      //      System.err.println("edgeFlag " + boundaryEdge);
      super.edgeFlag(boundaryEdge);
    }

    @Override
    public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
      //      System.err.println("combine " + coords + " " + data + " " + weight + " " + outData);
      //        double[] vertex = new double[6];
      //      vertex[0] = coords[0];
      //      vertex[1] = coords[1];
      //      vertex[2] = coords[2];
      //for (int i = 3; i < 6; i++)
      //vertex[i] = weight[0] * ((double[]) data[0])[i] + weight[1]
      //* ((double[]) data[1])[i] + weight[2] * ((double[]) data[2])[i] + weight[3]
      //* ((double[]) data[3])[i];
    }

    @Override
    public void beginData(final int type, final Object polygonData) {
      //      System.err.println("beginData " + type + " " + polygonData);
      super.beginData(type, polygonData);
    }

    @Override
    public void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {
      //      System.err.println("edgeFlagData " + boundaryEdge + " " + polygonData);
      super.edgeFlagData(boundaryEdge, polygonData);
    }

    @Override
    public void vertexData(final Object vertexData, final Object polygonData) {
      //      System.err.println("vertexData " + vertexData + " " + polygonData);
      super.vertexData(vertexData, polygonData);
    }

    @Override
    public void endData(final Object polygonData) {
      //      System.err.println("endData " + polygonData);
      super.endData(polygonData);
    }

    @Override
    public void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData, final Object polygonData) {
      glVertex2d(coords[0], coords[1]);
    }

    @Override
    public void begin(final int type) {
      //      System.err.println("begin " + type);
      //      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      glBegin(type);
    }

    @Override
    public void end() {
      //      System.err.println("end");
      glEnd();
      //      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    public void vertex(final Object coords) {
      //      System.err.println("vertex " + coords);
      glVertex2d(((double[]) coords)[0], ((double[]) coords)[1]);
    }

    @Override
    public void error(final int errnum) {
      String estring;
      estring = GLU.gluErrorString(errnum);
      logger.error("Tessellation Error Number: " + errnum);
      logger.error("Tessellation Error: " + estring);
      super.error(errnum);
    }

    @Override
    public void errorData(final int errnum, final Object polygonData) {
      logger.error("Tesselation error : " + errnum + " + " + polygonData.toString());
      super.errorData(errnum, polygonData);
    }

  }

  /**
   * Generate a list of all the points from a shape representing a polyline
   * @param shape shape to extract the collection of points
   * @return point list 
   * TODO: if there is a MOVE_TO in the middle of the shape, we should return a list of list of points instead of a simple list of points...
   */
  private static List<Point2d> generatePointList(final Shape shape) {
    PathIterator pathIterator = shape.getPathIterator(null);
    List<Point2d> points = new ArrayList<Point2d>();
    while (!pathIterator.isDone()) {
      float[] coords = new float[6];

      int segmentType = pathIterator.currentSegment(coords);
      switch (segmentType) {
      case PathIterator.SEG_CLOSE:
        break;
      case PathIterator.SEG_LINETO:
      case PathIterator.SEG_MOVETO:
        points.add(new Point2d(coords[0], coords[1]));
        break;
      case PathIterator.SEG_CUBICTO:
        points.add(new Point2d(coords[4], coords[5]));
        break;
      case PathIterator.SEG_QUADTO:
        points.add(new Point2d(coords[2], coords[3]));
        break;
      default:
        logger.warn("Draw GL shape do not know how to handle segment type " + segmentType);
      }
      pathIterator.next();
    }
    return points;
  }

  /**
   * Generate a list of all the points from a shape representing a polyline
   * @param shape shape to extract the collection of points
   * @return point list 
   * TODO: if there is a MOVE_TO in the middle of the shape, we should return a list of list of points instead of a simple list of points...
   */
  private static ParameterizedPolyline generateParameterizedPolyline(final Shape shape) {
    PathIterator pathIterator = shape.getPathIterator(null);
    ParameterizedPolyline line = new ParameterizedPolyline();
    double distance = 0; // distance from the line start
    Point2d prevPoint = null; // previous point position
    Point2d point = new Point2d(); // current point position
    while (!pathIterator.isDone()) {
      float[] coords = new float[6];

      int segmentType = pathIterator.currentSegment(coords);
      switch (segmentType) {
      case PathIterator.SEG_CLOSE:
        break;
      case PathIterator.SEG_LINETO:
      case PathIterator.SEG_MOVETO:
        point.x = coords[0];
        point.y = coords[1];

        if (prevPoint == null) {
          prevPoint = new Point2d(point);
          distance = 0.;
        } else {
          distance += Point2D.distance(point.x, point.y, prevPoint.x, prevPoint.y);
          prevPoint.x = point.x;
          prevPoint.y = point.y;
        }
        line.addPoint(point, distance);
        break;
      case PathIterator.SEG_CUBICTO:
        point.x = coords[4];
        point.y = coords[5];
        if (prevPoint == null) {
          prevPoint = new Point2d(point);
          distance = 0.;
        } else {
          distance += Point2D.distance(point.x, point.y, prevPoint.x, prevPoint.y);
          prevPoint.x = point.x;
          prevPoint.y = point.y;
        }
        line.addPoint(point, distance);
        break;
      case PathIterator.SEG_QUADTO:
        point.x = coords[2];
        point.y = coords[3];
        if (prevPoint == null) {
          prevPoint = new Point2d(point);
          distance = 0.;
        } else {
          distance += Point2D.distance(point.x, point.y, prevPoint.x, prevPoint.y);
          prevPoint.x = point.x;
          prevPoint.y = point.y;
        }
        line.addPoint(point, distance);
        break;
      default:
        logger.warn("Draw GL shape do not know how to handle segment type " + segmentType);
      }

      pathIterator.next();
    }
    return line;
  }
}
