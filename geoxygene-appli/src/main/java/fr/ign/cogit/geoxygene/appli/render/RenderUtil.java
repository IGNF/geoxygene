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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.locationtech.jts.geom.Envelope;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
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
import fr.ign.cogit.geoxygene.appli.render.stroke.TextStroke;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
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
import fr.ign.cogit.geoxygene.style.AnchorPoint;
import fr.ign.cogit.geoxygene.style.Displacement;
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
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;
import fr.ign.cogit.geoxygene.style.gradient.GradientStroke;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;
import fr.ign.cogit.geoxygene.util.ColorUtil;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

/**
 * @author Julien Perret
 * 
 */
public final class RenderUtil {
  /**
   * Private constructor. Should not be used.
   */
  private RenderUtil() {
  }

  private static final Logger logger = Logger.getLogger(RenderUtil.class);

  /**
   * Draw a geometry on the given graphics.
   * 
   * @param geometry the geometry
   * @param viewport the viewport
   * @param graphics the graphics
   */
  @SuppressWarnings("unchecked")
  public static void draw(final IGeometry geometry, final Viewport viewport,
      final Graphics2D graphics, double opacity) {
    if (geometry.isPolygon()) {
      GM_Polygon polygon = (GM_Polygon) geometry;
      try {
        Shape shape = viewport.toShape(polygon.exteriorLineString());
        if (shape != null) {
          graphics.draw(shape);
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < polygon.sizeInterior(); i++) {
        try {
          Shape shape = viewport.toShape(polygon.interiorLineString(i));
          if (shape != null) {
            graphics.draw(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
    } else {
      if (geometry.isMultiSurface() || geometry.isMultiCurve()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderUtil.draw(element, viewport, graphics, opacity);
        }
      } else {
        try {
          Shape shape = viewport.toShape(geometry);
          if (shape != null) {
            graphics.draw(shape);
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
  public static void fill(final IGeometry geometry, final Viewport viewport,
      final Graphics2D graphics, double opacity) {
    if (geometry.isPolygon()) {
      try {
        Shape shape = viewport.toShape(geometry);
        if (shape != null) {
          graphics.fill(shape);
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    } else {
      if (geometry.isMultiSurface()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderUtil.fill(element, viewport, graphics, opacity);
        }
      }

    }
  }

  public static void paint(Symbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity,
      BufferedImage img) {
    if (PointSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((PointSymbolizer) symbolizer, feature, viewport, graphics, opacity);
      return;

    }
    if (LineSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((LineSymbolizer) symbolizer, feature, viewport, graphics, opacity);
      return;
    }
    if (PolygonSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((PolygonSymbolizer) symbolizer, feature, viewport, graphics,
          opacity, img);
      return;
    }
    if (RasterSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((RasterSymbolizer) symbolizer, feature, viewport, graphics,
          opacity);
      return;
    }
    if (TextSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((TextSymbolizer) symbolizer, feature, viewport, graphics, opacity);
      return;
    }
    if (ThematicSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
      paint((ThematicSymbolizer) symbolizer, feature, viewport, graphics,
          opacity);
      return;
    }
    if (feature.getGeom() == null) {
      return;
    }
    if (feature.getGeom().isPolygon() || feature.getGeom().isMultiSurface()) {
      graphics.setColor(new Color(1f, 1f, 0f, 0.5f));
      RenderUtil.fill(feature.getGeom(), viewport, graphics, opacity);
    }
    java.awt.Stroke bs = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER);
    graphics.setColor(new Color(1f, 1f, 0f, 1f));
    graphics.setStroke(bs);
    RenderUtil.draw(feature.getGeom(), viewport, graphics, opacity);

    try {
      graphics.setStroke(
          new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
      for (IDirectPosition position : viewport
          .toViewDirectPositionList(feature.getGeom().coord())) {
        GeneralPath shape = new GeneralPath();
        shape.moveTo(position.getX() - 2, position.getY() - 2);
        shape.lineTo(position.getX() + 2, position.getY() - 2);
        shape.lineTo(position.getX() + 2, position.getY() + 2);
        shape.lineTo(position.getX() - 2, position.getY() + 2);
        shape.lineTo(position.getX() - 2, position.getY() - 2);
        graphics.setColor(new Color(1f, 1f, 0f, 1f));
        graphics.fill(shape);
        graphics.setColor(Color.black);
        graphics.draw(shape);
      }

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

  public static void paint(PointSymbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (symbolizer.getGraphic() == null) {
      return;

    }
    Point2D point;
    IGeometry geometry = getGeometry(symbolizer.getGeometryPropertyName(),
        feature);
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
      AffineTransform at = AffineTransform.getTranslateInstance(point.getX(),
          point.getY());
      at.rotate(-Double.parseDouble(
          symbolizer.getGraphic().getRotation().evaluate(feature).toString())
          * Math.PI / 180.0);
      at.scale(size, size);
      markShape = at.createTransformedShape(markShape);
      if (symbolizer.getColorMap() != null) {
        try {
          graphics.setColor(
              ColorUtil.getColorWithOpacity(new Color(symbolizer.getColorMap()
                  .getColor((Double.parseDouble(feature
                      .getAttribute(symbolizer.getColorMap().getPropertyName())
                      .toString())))),
                  opacity));
        } catch (NumberFormatException e) {
        }
      } else if (symbolizer.getCategorizedMap() != null) {
        Object value = feature
            .getAttribute(symbolizer.getCategorizedMap().getPropertyName());
        int rgb = symbolizer.getCategorizedMap().getColor(value);
        graphics
            .setColor(ColorUtil.getColorWithOpacity(new Color(rgb), opacity));
      } else {
        Color color = (mark.getFill() == null) ? Color.gray
            : mark.getFill().getColor();
        graphics.setColor(ColorUtil.getColorWithOpacity(color, opacity));
      }
      graphics.fill(markShape);
      if (mark.getStroke() != null) {
        graphics.setStroke(mark.getStroke().toAwtStroke((float) scale));
      }
      if (symbolizer.getColorMap() != null
          || symbolizer.getCategorizedMap() != null) {
        Color color = Color.black;
        graphics.setColor(ColorUtil.getColorWithOpacity(color, opacity));
      } else {
        Color color = (mark.getStroke() == null) ? Color.black
            : mark.getStroke().getColor(feature);
        graphics.setColor(ColorUtil.getColorWithOpacity(color, opacity));
      }
      graphics.draw(markShape);

    }

    for (ExternalGraphic theGraphic : symbolizer.getGraphic()
        .getExternalGraphics()) {
      if (theGraphic.getFormat().contains("svg")) { //$NON-NLS-1$
        GraphicsNode node = theGraphic.getGraphicsNode();
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
        AffineTransform at = AffineTransform.getTranslateInstance(point.getX(),
            point.getY());
        // AffineTransform at =
        // AffineTransform.getRotateInstance(-Double
        // .parseDouble(symbolizer.getGraphic().getRotation()
        // .evaluate(feature).toString())
        // * Math.PI / 180.0);
        at.rotate(-Double.parseDouble(
            symbolizer.getGraphic().getRotation().evaluate(feature).toString())
            * Math.PI / 180.0);
        double ratio = node.getBounds().getWidth()
            / node.getBounds().getHeight();
        at.translate(-ratio * size / 2, -size / 2);
        at.scale(size / node.getBounds().getHeight(),
            size / node.getBounds().getHeight());
        node.setTransform(at);
        node.paint(graphics);
      } else if (theGraphic.getFormat().contains("png") //$NON-NLS-1$
          || theGraphic.getFormat().contains("gif")) {
        Image onlineImage = theGraphic.getOnlineResource();
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
        AffineTransform at = AffineTransform
            .getRotateInstance(-Double.parseDouble(symbolizer.getGraphic()
                .getRotation().evaluate(feature).toString()) * Math.PI / 180.0);
        at.scale(size / onlineImage.getHeight(null),
            size / onlineImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(at,
            AffineTransformOp.TYPE_BILINEAR);
        graphics.drawImage((BufferedImage) onlineImage, op,
            (int) (point.getX() - ((((double) onlineImage.getWidth(null))
                / ((double) onlineImage.getHeight(null))) * (size / 2))),
            (int) (point.getY() - size / 2));
      }
    }

  }

  public static IGeometry getGeometry(String propertyName, IFeature feature) {
    IGeometry result = feature.getGeom();
    if (propertyName == null || propertyName.equalsIgnoreCase("geom")
        || propertyName.equalsIgnoreCase("geometrie")
        || propertyName.equalsIgnoreCase("geometry")) {
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
    try {
      return (IGeometry) feature.getAttribute(propertyName);
    } catch (ClassCastException e) {
      try {
        return WktGeOxygene
            .makeGeOxygene(feature.getAttribute(propertyName).toString());
      } catch (Exception e1) {
        e1.printStackTrace();
        return null;
      }
    }
  }

  public static void paint(LineSymbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity) {
    IGeometry geometry = getGeometry(symbolizer.getGeometryPropertyName(),
        feature);
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
    graphics.setStroke(
        symbolizer.getStroke().toAwtStroke((float) scaleUOMToPixels));
    paintShadow(symbolizer, geometry, viewport, graphics, opacity);
    if (symbolizer.getStroke().getColor(feature) != null) {
      List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, false);
      if (shapes != null) {
        // ColorMap
        if (symbolizer.getColorMap() != null) {
          try {
            Color c = new Color(symbolizer.getColorMap()
                .getColor((Double.parseDouble(feature
                    .getAttribute(symbolizer.getColorMap().getPropertyName())
                    .toString()))));
            graphics.setColor(ColorUtil.getColorWithOpacity(c, opacity));
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
          // Categorized Map
        } else if (symbolizer.getCategorizedMap() != null) {
          Object value = feature
              .getAttribute(symbolizer.getCategorizedMap().getPropertyName());
          int rgb = symbolizer.getCategorizedMap().getColor(value);
          graphics
              .setColor(ColorUtil.getColorWithOpacity(new Color(rgb), opacity));
          // Proxy Symbol
        } else if (symbolizer.getProxySymbol() != null) {
          // Color without opacity
          Color adaptedColor = new Color(Integer.parseInt(feature
              .getAttribute(
                  symbolizer.getProxySymbol().getProxyColorPropertyName())
              .toString()));
          // Color with stroke opacity
          Color adaptedColorTransp = new Color(adaptedColor.getRed(),
              adaptedColor.getGreen(), adaptedColor.getBlue(),
              (int) (symbolizer.getStroke().getStrokeOpacity() * 255f));
          // Color with stroke and layer opacity
          graphics.setColor(
              ColorUtil.getColorWithOpacity(adaptedColorTransp, opacity));

        } else {
          graphics.setColor(ColorUtil.getColorWithOpacity(
              symbolizer.getStroke().getColor(feature), opacity));
        }
        for (Shape shape : shapes) {
          graphics.draw(shape);
        }
      }

    }
    if (symbolizer.getStroke().getGraphicType() != null) {
      if (symbolizer.getStroke().getGraphicType().getClass()
          .isAssignableFrom(GraphicFill.class)) {
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, true);
        // GraphicFill
        List<Graphic> graphicList = ((GraphicFill) symbolizer.getStroke()
            .getGraphicType()).getGraphics();
        for (Graphic graphic : graphicList) {
          for (Shape shape : shapes) {
            graphicFillLineString(shape, graphic, viewport, graphics, opacity);
          }
        }
      } else if (symbolizer.getStroke().getGraphicType().getClass()
          .isAssignableFrom(GraphicStroke.class)) {
        // GraphicStroke
        float[] dashArray = symbolizer.getStroke()
            .getStrokeDashArray((float) scaleUOMToPixels);
        double space = 0.0;
        if (dashArray != null) {
          space = dashArray[1];
        }
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport,
            false);
        if (shapes != null) {
          List<Graphic> graphicList = ((GraphicStroke) symbolizer.getStroke()
              .getGraphicType()).getGraphics();
          for (Graphic graphic : graphicList) {
            for (Shape shape : shapes) {
              graphicStrokeLineString(shape, graphic, viewport, graphics,
                  opacity, space);
            }
          }
        }
      } else if (symbolizer.getStroke().getGraphicType().getClass()
          .isAssignableFrom(GradientStroke.class)) {
        // GradientStroke
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport,
            false);
        if (shapes != null) {
          for (Shape shape : shapes) {
            int xMin = (int) shape.getBounds2D().getMinX();
            int xMax = (int) shape.getBounds2D().getMaxX();
            int yMin = (int) shape.getBounds2D().getMinY();
            int yMax = (int) shape.getBounds2D().getMaxY();
            if (feature != null) {
              IDirectPositionList coord = feature.getGeom().coord();
              Point2D point_init = null;
              Point2D point_final = null;
              try {
                point_init = viewport.toViewPoint(coord.get(0));
                point_final = viewport.toViewPoint(coord.get(coord.size() - 1));
              } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
              }
              xMin = (int) point_init.getX();
              xMax = (int) point_final.getX();
              yMin = (int) point_init.getY();
              yMax = (int) point_final.getX();
            }
            GradientStroke gradient = ((GradientStroke) symbolizer.getStroke()
                .getGraphicType());
            Color color1 = gradient.getColor1(feature);
            Color color2 = gradient.getColor2(feature);

            GradientPaint redtowhite = new GradientPaint(xMin, yMin, color1,
                xMax, yMax, color2);
            graphics.setPaint(redtowhite);
            graphics.draw(shape);
          }
        }
      }

    }
  }

  /**
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @param fill true if the stroke width should be used to build the shapes, ie
   *          if they will be used for graphic fill
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  private static List<Shape> getShapeList(LineSymbolizer symbolizer,
      IGeometry geometry, Viewport viewport, boolean fill) {
    double scaleSymbolizerUOMToDataUOM = 1;
    if (symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleSymbolizerUOMToDataUOM = 1
            / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    if (ICurve.class.isAssignableFrom(geometry.getClass())
        || IPolygon.class.isAssignableFrom(geometry.getClass())) {
      ICurve curve = ((ICurve.class.isAssignableFrom(geometry.getClass()))
          ? ((ICurve) geometry)
          : ((IPolygon) geometry).exteriorLineString());
      if (symbolizer.getPerpendicularOffset() != 0) {
        IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(curve,
            symbolizer.getPerpendicularOffset() * scaleSymbolizerUOMToDataUOM);
        List<Shape> shapes = new ArrayList<Shape>();
        for (ILineString l : offsetCurve) {
          shapes.addAll(
              getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(), l,
                  viewport, fill, scaleSymbolizerUOMToDataUOM));
        }
        return shapes;
      }
      return getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(),
          curve, viewport, fill, scaleSymbolizerUOMToDataUOM);
    }
    if (geometry.isMultiCurve()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableCurve line : (IMultiCurve<IOrientableCurve>) geometry) {
        if (symbolizer.getPerpendicularOffset() != 0) {
          IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(
              (ILineString) line, symbolizer.getPerpendicularOffset()
                  * scaleSymbolizerUOMToDataUOM);
          for (ILineString l : offsetCurve) {
            shapes.addAll(
                getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(),
                    l, viewport, fill, scaleSymbolizerUOMToDataUOM));
          }
        } else {
          shapes.addAll(
              getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(),
                  line, viewport, fill, scaleSymbolizerUOMToDataUOM));
        }

      }
      return shapes;
    }
    if (geometry.isMultiSurface()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableSurface surface : ((IMultiSurface<IOrientableSurface>) geometry)
          .getList()) {
        try {
          Shape shape = viewport.toShape(
              fill ? surface.buffer(symbolizer.getStroke().getStrokeWidth() / 2)
                  : surface);
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
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @param fill true if the stroke width should be used to build the shapes, ie
   *          if they will be used for graphic fill
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  private static List<Shape> getShapeList(PolygonSymbolizer symbolizer,
      IGeometry geometry, Viewport viewport, boolean fill) {
    double scaleSymbolizerUOMToDataUOM = 1;
    if (symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleSymbolizerUOMToDataUOM = 1
            / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }

    if (ICurve.class.isAssignableFrom(geometry.getClass())
        || IPolygon.class.isAssignableFrom(geometry.getClass())) {
      ICurve curve = ((ICurve.class.isAssignableFrom(geometry.getClass()))
          ? ((ICurve) geometry)
          : ((IPolygon) geometry).exteriorLineString());

      return getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(),
          curve, viewport, fill, scaleSymbolizerUOMToDataUOM);
    }
    if (geometry.isMultiCurve()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableCurve line : (IMultiCurve<IOrientableCurve>) geometry) {
        // if (symbolizer.getPerpendicularOffset() != 0) {
        // IMultiCurve<ILineString> offsetCurve =
        // JtsAlgorithms.offsetCurve(
        // (ILineString) line, symbolizer.getPerpendicularOffset() *
        // scaleSymbolizerUOMToDataUOM);
        // for (ILineString l : offsetCurve) {
        // shapes.addAll(getLineStringShapeList(symbolizer, l, viewport,
        // fill,
        // scaleSymbolizerUOMToDataUOM));
        // }
        // } else {
        shapes.addAll(
            getLineStringShapeList(symbolizer.getStroke().getStrokeWidth(),
                line, viewport, fill, scaleSymbolizerUOMToDataUOM));
        // }
      }
      return shapes;
    }
    if (geometry.isMultiSurface()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableSurface surface : ((IMultiSurface<IOrientableSurface>) geometry)
          .getList()) {
        try {
          Shape shape = viewport.toShape(
              fill ? surface.buffer(symbolizer.getStroke().getStrokeWidth() / 2)
                  : surface);
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
   * @param data .getSymbolizer() a line symbolizer
   * @param line the geometry of the line
   * @param viewport the viewport used for rendering
   * @param fill true if the stroke width should be used to build the shapes, ie
   *          if they will be used for graphic fill
   * @param scale scale to go from the symbolizer's uom to the data uom
   * @return
   */
  private static List<Shape> getLineStringShapeList(float strokeWidth,
      IOrientableCurve line, Viewport viewport, boolean fill, double scale) {
    List<Shape> shapes = new ArrayList<Shape>();
    try {
      Shape shape = viewport
          .toShape(fill ? line.buffer(strokeWidth * 0.5 * scale) : line);
      if (shape != null) {
        shapes.add(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    return shapes;
  }

  @SuppressWarnings("unchecked")
  private static void paintShadow(LineSymbolizer symbolizer, IGeometry geometry,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (symbolizer.getShadow() != null) {
      Color shadowColor = ColorUtil
          .getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
      double translate_x = -5;
      double translate_y = -5;
      if (symbolizer.getShadow().getDisplacement() != null) {
        translate_x = symbolizer.getShadow().getDisplacement()
            .getDisplacementX();
        translate_y = symbolizer.getShadow().getDisplacement()
            .getDisplacementY();
      }
      graphics.setColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isLineString()) {
        try {
          Shape shape = viewport
              .toShape(geometry.translate(translate_x, translate_y, 0));
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
              Shape shape = viewport
                  .toShape(line.translate(translate_x, translate_y, 0));
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

  private static void graphicFillLineString(Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") //$NON-NLS-1$
          || external.getFormat().contains("gif")) { //$NON-NLS-1$
        Image image = external.getOnlineResource();
        graphicFillLineString(shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicFillLineString(shape, node, size, graphics, opacity);
        }
      }
      return;
    }

    List<Shape> shapes = new ArrayList<Shape>(graphic.getMarks().size());
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      graphics.setColor(
          ColorUtil.getColorWithOpacity(mark.getFill().getColor(), opacity));

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
            (i + 0.5) * size + shape.getBounds2D().getMinX(),
            (j + 0.5) * size + shape.getBounds2D().getMinY());
        transform.concatenate(scaleTransform);
        for (Shape markShape : shapes) {
          Shape tranlatedShape = transform.createTransformedShape(markShape);
          graphics.fill(tranlatedShape);
        }
      }
    }
  }

  private static void graphicFillLineString(Shape shape, GraphicsNode node,
      float size, Graphics2D graphics, double opacity) {
    AffineTransform translate = AffineTransform.getTranslateInstance(
        -node.getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    graphicFillLineString(shape, buff, size, graphics, opacity);
  }

  private static void graphicFillLineString(Shape shape, Image image,
      float size, Graphics2D graphics, double opacity) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight.doubleValue() / image.getHeight(null);
    Double shapeWidth = new Double(image.getWidth(null) * factor);
    AffineTransform transform = AffineTransform.getTranslateInstance(
        shape.getBounds2D().getMinX(), shape.getBounds2D().getMinY());
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

  private static void graphicStrokeLineString(Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity, double space) {
    if (shape == null || viewport == null || graphic == null) {
      return;

    }

    float size = graphic.getSize();
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") //$NON-NLS-1$
          || external.getFormat().contains("gif")) { //$NON-NLS-1$
        Image image = external.getOnlineResource();
        graphicStrokeLineString(shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicStrokeLineString(shape, node, size, graphics, opacity);
        }
      }
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>();
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      graphics.setColor(
          ColorUtil.getColorWithOpacity(mark.getFill().getColor(), opacity));
    }

    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(
        shape, size, 1, 1, space);
    for (AffineTransform t : transforms) {
      for (Shape markShape : shapes) {
        Shape tranlatedShape = t.createTransformedShape(markShape);
        graphics.fill(tranlatedShape);
      }
    }

  }

  private static void graphicStrokeLineString(Shape shape, GraphicsNode node,
      float size, Graphics2D graphics, double opacity) {
    double width = node.getBounds().getWidth();
    double height = node.getBounds().getHeight();
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(
        shape, size, width, height, 0.0);
    for (AffineTransform t : transforms) {
      AffineTransform tr = AffineTransform.getTranslateInstance(
          -node.getBounds().getMinX(), -node.getBounds().getMinY());
      t.concatenate(tr);
      node.setTransform(t);
      node.paint(graphics);
    }
  }

  private static void graphicStrokeLineString(Shape shape, Image image,
      float size, Graphics2D graphics, double opacity) {
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(
        shape, size, image.getWidth(null), image.getHeight(null), 0.0);
    for (AffineTransform t : transforms) {
      graphics.drawImage(image, t, null);
    }

  }

  /**
   * 
   * @param shape the shape being drawn regularly on the stroke
   * @param size the size of the shape
   * @param width the width of the shape
   * @param height the height of the shape
   * @param space the space between two shapes on the stroke
   * @return
   */
  private static List<AffineTransform> getGraphicStrokeLineStringTransforms(
      Shape shape, float size, double width, double height, double space) {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    double shapeHeight = size;
    double factor = shapeHeight / height;
    double shapeWidth = width * factor;
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor,
        factor);
    AffineTransform translation = AffineTransform
        .getTranslateInstance(-(0.5) * width, -(0.5) * height);
    transforms.add(translation);
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
    // case with no space between symbols
    if (space == 0.0) {
      ILineString line = Operateurs.resampling(new GM_LineString(points),
          shapeWidth);
      for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
        IDirectPosition p1 = line.getControlPoint(i);
        IDirectPosition p2 = line.getControlPoint(i + 1);
        IDirectPosition p = new DirectPosition((p1.getX() + p2.getX()) / 2,
            (p1.getY() + p2.getY()) / 2);
        AffineTransform transform = AffineTransform
            .getTranslateInstance(p.getX(), p.getY());
        transform.concatenate(scaleTransform);
        transform.concatenate(
            AffineTransform.getRotateInstance(new Angle(p1, p2).getValeur()));
        transform.concatenate(translation);
        transforms.add(transform);
      }
    } else {
      ILineString line = new GM_LineString(points);
      IDirectPosition previous = line.coord().get(0);
      for (double curv = 0.0; curv < line.length(); curv += space
          + shapeWidth) {
        IDirectPosition p = Operateurs.pointEnAbscisseCurviligne(line, curv);
        AffineTransform transform = AffineTransform
            .getTranslateInstance(p.getX(), p.getY());
        transform.concatenate(scaleTransform);
        transform.concatenate(AffineTransform
            .getRotateInstance(new Angle(previous, p).getValeur()));
        // transform.concatenate(translation);
        transforms.add(transform);
        previous = p;
      }
    }
    return transforms;
  }

  public static void paint(PolygonSymbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity,
      BufferedImage img) {
    IGeometry geometry = getGeometry(symbolizer.getGeometryPropertyName(),
        feature);
    if (geometry == null) {
      return;
    }

    if (geometry == null || viewport == null) {
      return;
    }
    if (symbolizer.getShadow() != null) {
      Color shadowColor = ColorUtil
          .getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
      double translate_x = -5;
      double translate_y = -5;
      if (symbolizer.getShadow().getDisplacement() != null) {
        translate_x = symbolizer.getShadow().getDisplacement()
            .getDisplacementX();
        translate_y = symbolizer.getShadow().getDisplacement()
            .getDisplacementY();
      }
      graphics.setColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isPolygon()) {
        try {
          Shape shape = viewport
              .toShape(geometry.translate(translate_x, translate_y, 0));
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (GM_MultiSurface.class.isAssignableFrom(geometry.getClass())) {
          try {
            Shape shape = viewport
                .toShape(geometry.translate(translate_x, translate_y, 0));
            if (shape != null) {
              shapes.add(shape);
            }
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
      }
      for (Shape shape : shapes) {
        fillShadow(symbolizer, shape, viewport, graphics, opacity);
      }

    }

    Color fillColor = null;
    float fillOpacity = 1f;
    if (symbolizer.getFill() != null && symbolizer.getFill().getColor() != null
        && opacity > 0f) {
      fillColor = ColorUtil.getColorWithOpacity(symbolizer.getFill().getColor(),
          opacity);
      fillOpacity = symbolizer.getFill().getFillOpacity() * (float) opacity;

    }

    if (symbolizer.getColorMap() != null
        && symbolizer.getColorMap().getInterpolate() != null) {
      double value = Double.parseDouble(feature
          .getAttribute(symbolizer.getColorMap().getPropertyName()).toString());
      int rgb = symbolizer.getColorMap().getColor(value);
      fillColor = ColorUtil.getColorWithOpacity(new Color(rgb), opacity);
      symbolizer.getStroke().setStroke(Color.BLACK);

    }
    if (symbolizer.getCategorizedMap() != null) {
      Object value = feature
          .getAttribute(symbolizer.getCategorizedMap().getPropertyName());
      int rgb = symbolizer.getCategorizedMap().getColor(value);
      fillColor = ColorUtil.getColorWithOpacity(new Color(rgb), opacity);
      symbolizer.getStroke().setStroke(Color.BLACK);
    }
    if (fillColor != null && fillOpacity > 0f) {
      graphics.setColor(fillColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isPolygon()) {
        try {
          Shape shape = viewport.toShape(geometry);
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (GM_MultiSurface.class.isAssignableFrom(geometry.getClass())) {
          for (IOrientableSurface surface : ((GM_MultiSurface<?>) geometry)) {
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
        if (symbolizer.getFill() != null) {
          fillPolygon(symbolizer, shape, viewport, graphics, opacity);
        }
        if (symbolizer.getFill().getGraphicFill() != null) {
          List<Graphic> graphicList = symbolizer.getFill().getGraphicFill()
              .getGraphics();
          for (Graphic graphic : graphicList) {
            double rotation = Double.parseDouble(
                graphic.getRotation().evaluate(feature).toString());
            graphicFillPolygon(shape, graphic, viewport, graphics, opacity,
                rotation);
          }
        } else if (symbolizer.getFill().getExpressiveFill() != null) {
          // Is there a texture parameter?
          Texture textureparam = null;
          for (ExpressiveParameter p : symbolizer.getFill().getExpressiveFill()
              .getExpressiveParameters()) {
            if (p.getValue() instanceof Texture) {
              textureparam = (Texture) p.getValue();
              break;
            }
          }
          texturePolygon(textureparam, feature, shape, viewport, graphics, img,
              opacity);
        }
      }
    }
    if (symbolizer.getStroke() != null) {
      if (symbolizer.getStroke()
          .getStrokeLineJoin() == BasicStroke.JOIN_MITER) {
        symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_SQUARE);
      } else if (symbolizer.getStroke()
          .getStrokeLineJoin() == BasicStroke.JOIN_BEVEL) {
        symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_BUTT);
      } else if (symbolizer.getStroke()
          .getStrokeLineJoin() == BasicStroke.JOIN_ROUND) {
        symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_ROUND);
      } else {
        RenderUtil.logger.error("Stroke Line Join undefined."); //$NON-NLS-1$
      }

      float strokeOpacity = symbolizer.getStroke().getStrokeOpacity();
      if (strokeOpacity > 0f) {

        double scale = 1;
        if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
          try {
            scale = viewport.getModelToViewTransform().getScaleX();
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        BasicStroke bs = (BasicStroke) symbolizer.getStroke()
            .toAwtStroke((float) scale);
        // Solid color
        Color color = ColorUtil.getColorWithOpacity(
            symbolizer.getStroke().getColor(feature), opacity);
        graphics.setColor(color);
        if (geometry.isPolygon()) {
          drawPolygon(symbolizer, feature, (IPolygon) geometry, viewport,
              graphics, bs, opacity);
        } else {
          if (GM_MultiSurface.class.isAssignableFrom(geometry.getClass())) {
            for (IOrientableSurface surface : ((GM_MultiSurface<?>) geometry)) {
              drawPolygon(symbolizer, feature, (IPolygon) surface, viewport,
                  graphics, bs, opacity);
            }
          }
        }
      }

      if (symbolizer.getStroke().getGraphicType() != null) {
        if (symbolizer.getStroke().getGraphicType().getClass()
            .isAssignableFrom(GraphicFill.class)) {
          List<Shape> shapes = getShapeList(symbolizer, geometry, viewport,
              true);
          // GraphicFill
          List<Graphic> graphicList = ((GraphicFill) symbolizer.getStroke()
              .getGraphicType()).getGraphics();
          for (Graphic graphic : graphicList) {
            for (Shape shape : shapes) {
              graphicFillLineString(shape, graphic, viewport, graphics,
                  opacity);
            }
          }
        } else {
          // GraphicStroke
          double scaleUOMToPixels = 1;
          if (!symbolizer.getUnitOfMeasure()
              .equalsIgnoreCase(Symbolizer.PIXEL)) {
            try {
              scaleUOMToPixels = viewport.getModelToViewTransform().getScaleX();
            } catch (NoninvertibleTransformException e) {
              e.printStackTrace();
            }
          }
          float[] dashArray = symbolizer.getStroke()
              .getStrokeDashArray((float) scaleUOMToPixels);
          double space = 0.0;
          if (dashArray != null) {
            space = dashArray[1];
          }
          List<Shape> shapes = getShapeList(symbolizer, geometry, viewport,
              false);
          if (shapes != null) {
            List<Graphic> graphicList = ((GraphicStroke) symbolizer.getStroke()
                .getGraphicType()).getGraphics();
            for (Graphic graphic : graphicList) {
              for (Shape shape : shapes) {
                graphicStrokeLineString(shape, graphic, viewport, graphics,
                    opacity, space);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Draw a polygon using the Fill.Texture
   * 
   * @param data .getSymbolizer()
   * @param shape
   * @param graphics
   * @param img
   * @param opacity
   */
  private static void texturePolygon(Texture texture, IFeature feature,
      Shape shape, Viewport viewport, Graphics2D graphics, BufferedImage img,
      double opacity) {
    ;
    GLTexture builtTex = TextureManager.getTexture(texture,
        feature.getFeatureCollection(0), viewport);
    ;
    if (builtTex.getClass().isAssignableFrom(BasicTexture.class)) {
      BasicTexture tex = (BasicTexture) builtTex;
      // draw the texture image into resulting image
      if (builtTex != null && tex.getTextureImage() != null) {
        BufferedImage imgTexture = tex.getTextureImage();
        boolean xRepeat = texture.isRepeatedX();
        boolean yRepeat = texture.isRepeatedY();
        IFeatureCollection<IFeature> featureCollection = feature
            .getFeatureCollection(0);
        // FIXME: what do we do if the feature belongs to multiple
        // feature collections ??? (how to know which one is
        // currently drawn??)
        IEnvelope envelope = featureCollection.getEnvelope();
        double pixelScaleX = envelope.width() / imgTexture.getWidth()
            * viewport.getScale();
        double pixelScaleY = envelope.length() / imgTexture.getHeight()
            * viewport.getScale();
        switch (texture.getTextureDrawingMode()) {
          case VIEWPORTSPACE: {
            try {
              Point2D upper = new Point2D.Double();
              Point2D lower = new Point2D.Double();
              Point2D.Double displacedUpper = new Point2D.Double(
                  envelope.getUpperCorner().getX()
                      + texture.getDisplacement().getX(),
                  envelope.getUpperCorner().getY()
                      + texture.getDisplacement().getY());
              viewport.getModelToViewTransform().transform(displacedUpper,
                  upper);
              Point2D.Double displacedLower = new Point2D.Double(
                  envelope.getLowerCorner().getX()
                      + texture.getDisplacement().getX(),
                  envelope.getLowerCorner().getY()
                      + texture.getDisplacement().getY());
              viewport.getModelToViewTransform().transform(displacedLower,
                  lower);
              graphics.setComposite(AlphaComposite
                  .getInstance(AlphaComposite.SRC_OVER, (float) opacity));
              AffineTransform transform = new AffineTransform();
              transform.translate(lower.getX(), upper.getY());
              transform.scale(pixelScaleX * texture.getScaleFactor().getX(),
                  pixelScaleY * texture.getScaleFactor().getY());
              transform.rotate(texture.getRotation().getAngleInRadians());
              drawTexture(feature, shape, viewport, graphics, opacity,
                  imgTexture, transform, xRepeat, yRepeat);
            } catch (NoninvertibleTransformException e) {
              logger.error(e);
              e.printStackTrace();
            }
          }
            break;
          case SCREENSPACE: {
            logger.warn(
                "Screenspace textures have not been retested after changes. You're lucky if it works...");
            double pixelDisplacementX = texture.getDisplacement().getX();
            double pixelDisplacementY = texture.getDisplacement().getY();
            AffineTransform transform = new AffineTransform();
            transform.translate(pixelDisplacementX, pixelDisplacementY);
            transform.scale(pixelScaleX, pixelScaleY);
            drawTexture(feature, shape, viewport, graphics, opacity, imgTexture,
                transform, xRepeat, yRepeat);
          }
            break;
          default:
            logger.warn("Do not know how to draw texture type "
                + texture.getTextureDrawingMode());
        }
      }
    }

  }

  /**
   * Draw a textured polygon in feature-collection coordinates system (follow
   * the viewport)
   * 
   * @param feature current feature to draw
   * @param shape feature shape in screen space (used as clip shape)
   * @param viewport current viewport
   * @param graphics graphics in which texture has to be drawn
   * @param opacity global opacity
   * @param imgTexture texture to draw
   */
  private static void drawTexture(IFeature feature, Shape shape,
      Viewport viewport, Graphics2D graphics, double opacity,
      BufferedImage imgTexture, AffineTransform localTransform, boolean xRepeat,
      boolean yRepeat) {
    graphics.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
    if (xRepeat || yRepeat) {
      logger.warn("xy texture repeat option is not handled by AWT");
    }

    graphics.setClip(shape);
    // graphics.setTransform(localTransform);
    graphics.drawImage(imgTexture, localTransform, null);

  }

  private static void graphicFillPolygon(Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity, double rotation) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") //$NON-NLS-1$
          || external.getFormat().contains("gif")) { //$NON-NLS-1$
        Image image = external.getOnlineResource();
        graphicFillPolygon(shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicFillPolygon(shape, node, size, graphics, opacity);
        }
      }
      return;
    }
    int markShapeSize = 200;
    for (Mark mark : graphic.getMarks()) {
      Shape markShape = mark.toShape();
      AffineTransform translate = AffineTransform
          .getTranslateInstance(markShapeSize / 2, markShapeSize / 2);
      if (graphic.getRotation() != null) {
        AffineTransform rotate = AffineTransform
            .getRotateInstance(Math.PI * rotation / 180.0);
        translate.concatenate(rotate);
      }
      AffineTransform scaleTransform = AffineTransform
          .getScaleInstance(markShapeSize, markShapeSize);
      translate.concatenate(scaleTransform);
      Shape tranlatedShape = translate.createTransformedShape(markShape);
      BufferedImage buff = new BufferedImage(markShapeSize, markShapeSize,
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) buff.getGraphics();
      g.setColor(
          ColorUtil.getColorWithOpacity(mark.getFill().getColor(), opacity));
      g.fill(tranlatedShape);
      graphicFillPolygon(shape, buff, size, graphics, opacity);
    }

  }

  private static void graphicFillPolygon(Shape shape, Image image, float size,
      Graphics2D graphics, double opacity) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight / image.getHeight(null);
    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
    AffineTransform transform = AffineTransform.getTranslateInstance(
        shape.getBounds2D().getMinX(), shape.getBounds2D().getMinY());
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
    graphics.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
    graphics.drawImage(bufferedImage, transform, null);
    bufferedImage.flush();
    im.dispose();
    scaledImage.flush();
    buff.flush();
  }

  private static void graphicFillPolygon(Shape shape, GraphicsNode node,
      float size, Graphics2D graphics, double opacity) {
    AffineTransform translate = AffineTransform.getTranslateInstance(
        -node.getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    graphicFillPolygon(shape, buff, size, graphics, opacity);
  }

  private static void fillPolygon(PolygonSymbolizer symbolizer, Shape shape,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null) {
      return;
    }
    if (symbolizer.getFill() != null) {
      float[] symbolizerColorComponenents = symbolizer.getFill().getColor()
          .getComponents(null);
      Color color = new Color(symbolizerColorComponenents[0],
          symbolizerColorComponenents[1], symbolizerColorComponenents[2],
          symbolizerColorComponenents[3] * (float) opacity);
      graphics.setColor(color);
    }
    graphics.fill(shape);

  }

  private static void fillShadow(PolygonSymbolizer symbolizer, Shape shape,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null) {
      return;

    }
    graphics.fill(shape);
  }

  private static void drawPolygon(PolygonSymbolizer symbolizer,
      IFeature feature, IPolygon polygon, Viewport viewport,
      Graphics2D graphics, BasicStroke stroke, double opacity) {
    if (polygon == null || viewport == null) {
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>(0);
    try {
      Shape shape = viewport.toShape(polygon.getExterior());
      if (shape != null) {
        shapes.add(shape);
      }
      for (IRing ring : polygon.getInterior()) {
        shape = viewport.toShape(ring);
        if (shape != null) {
          shapes.add(shape);
        }
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    for (Shape shape : shapes) {
      Shape outline = stroke.createStrokedShape(shape);
      graphics.setColor(ColorUtil.getColorWithOpacity(
          symbolizer.getStroke().getColor(feature), opacity));
      graphics.fill(outline);
    }
  }

  /**
   * @param obj The FT_coverage to render This method shall be reworked.
   */
  public static void paint(RasterSymbolizer symbolizer, IFeature obj,
      Viewport viewport, Graphics2D graphics, double opacity) {
    FT_Coverage fcoverage = (FT_Coverage) obj;
    try {
      // Geotools stuff
      StyleFactory sf = CommonFactoryFinder.getStyleFactory();
      FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

      GridCoverage2D coverage = fcoverage.coverage();

      IEnvelope view = viewport.getEnvelopeInModelCoordinates();
      Envelope renderEnvelope = new Envelope(view.minX(), view.maxX(),
          view.minY(), view.maxY());

      GridCoverageRenderer renderer = new GridCoverageRenderer(
          coverage.getCoordinateReferenceSystem(), renderEnvelope,
          viewport.getLayerViewPanels().iterator().next().getVisibleRect(),
          null);
      // For geotools, we create a style
      org.geotools.styling.RasterSymbolizer s = new StyleBuilder()
          .createRasterSymbolizer();

      // style: Opacity
      s.setOpacity(
          (new FilterFactoryImpl()).literal(opacity * symbolizer.getOpacity()));

      // Style: channelSelection
      if (symbolizer.getChannelSelection() != null) {
        int numBands = coverage.getNumSampleDimensions();
        org.geotools.styling.ChannelSelection channelSelection = null;

        if (symbolizer.getChannelSelection().isGrayChannel()) {
          // For Grayscale selection
          // TODO: proper contract enhancement
          ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0),
              ContrastMethod.NONE);
          SelectedChannelType sct = sf.createSelectedChannelType(
              String.valueOf(symbolizer.getChannelSelection().getGrayChannel()
                  .getSourceChannelName()),
              ce);

          channelSelection = sf.channelSelection(sct);
        } else if (symbolizer.getChannelSelection().isRGBChannels()) {
          // For RGB selection
          // TODO: proper contract enhancement
          ContrastEnhancement[] ce = new ContrastEnhancement[3];
          SelectedChannelType[] sct = new SelectedChannelType[3];

          for (int i = 0; i < 3; i++) {
            ce[i] = sf.contrastEnhancement(ff.literal(1.0),
                ContrastMethod.NONE);
          }
          sct[0] = sf.createSelectedChannelType(String.valueOf(symbolizer
              .getChannelSelection().getRedChannel().getSourceChannelName()),
              ce[0]);
          sct[1] = sf.createSelectedChannelType(String.valueOf(symbolizer
              .getChannelSelection().getGreenChannel().getSourceChannelName()),
              ce[1]);
          sct[2] = sf.createSelectedChannelType(String.valueOf(symbolizer
              .getChannelSelection().getBlueChannel().getSourceChannelName()),
              ce[2]);

          channelSelection = sf.channelSelection(sct[0], sct[1], sct[2]);
        } else {
          System.err.println("Error in ChannelSelection");
        }

        s.setChannelSelection(channelSelection);
      }

      // Style: colormap
      // We have to adapt our Colormap (SE standard) to Geotools Colormap
      if (symbolizer.getColorMap() != null) {
        String[] labels = null;
        double[] quantities = null;
        Color[] colors = null;
        int type = 0;

        if (symbolizer.getColorMap().isInterpolate()) {
          // "ramp" mode in geotools
          type = 1;
          labels = new String[symbolizer.getColorMap().getInterpolate()
              .getNbInterpolationPoint()];
          quantities = new double[symbolizer.getColorMap().getInterpolate()
              .getNbInterpolationPoint()];
          colors = new Color[symbolizer.getColorMap().getInterpolate()
              .getNbInterpolationPoint()];

          for (int i = 0; i < symbolizer.getColorMap().getInterpolate()
              .getNbInterpolationPoint(); i++) {
            labels[i] = "";
            quantities[i] = symbolizer.getColorMap().getInterpolate()
                .getInterpolationPoint(i).getData();
            colors[i] = symbolizer.getColorMap().getInterpolate()
                .getInterpolationPoint(i).getColor();
          }
        } else if (symbolizer.getColorMap().isIntervals()) {
          // "intervals" mode in geotools
          type = 2;
          labels = new String[symbolizer.getColorMap().getIntervals()
              .getNbIntervalsPoint()];
          quantities = new double[symbolizer.getColorMap().getIntervals()
              .getNbIntervalsPoint()];
          colors = new Color[symbolizer.getColorMap().getIntervals()
              .getNbIntervalsPoint()];

          for (int i = 0; i < symbolizer.getColorMap().getIntervals()
              .getNbIntervalsPoint(); i++) {
            labels[i] = "";
            quantities[i] = symbolizer.getColorMap().getIntervals()
                .getIntervalsPoint(i).getData();
            colors[i] = symbolizer.getColorMap().getIntervals()
                .getIntervalsPoint(i).getColor();
          }
        } else if (symbolizer.getColorMap().isCategorize()) {
          // "intervals" mode in geotools
          type = 3;
          labels = new String[symbolizer.getColorMap().getCategorize()
              .getNbCategorizePoint()];
          quantities = new double[symbolizer.getColorMap().getCategorize()
              .getNbCategorizePoint()];
          colors = new Color[symbolizer.getColorMap().getCategorize()
              .getNbCategorizePoint()];

          for (int i = 0; i < symbolizer.getColorMap().getCategorize()
              .getNbCategorizePoint(); i++) {
            labels[i] = "";
            quantities[i] = symbolizer.getColorMap().getCategorize()
                .getThreshold(i);
            colors[i] = symbolizer.getColorMap().getCategorize().getColor(i);
          }
        } else {
          System.err.println("Requested ColorMap mode is not yet implemented");
        }

        org.geotools.styling.ColorMap colorMap = new StyleBuilder()
            .createColorMap(labels, quantities, colors, type);
        s.setColorMap(colorMap);
      }

      // Rendering
      renderer.paint(graphics, coverage, s);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return;
  }

  public static void paint(TextSymbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (symbolizer.getLabel() == null) {
      return;
    }
    Object value = feature.getAttribute(symbolizer.getLabel());
    if (value == null)
      value = symbolizer.getLabel();
    if (symbolizer.getLabelPlacement() != null) {
      if (symbolizer.getLabelPlacement().getPlacement() != null && symbolizer
          .getLabelPlacement().getPlacement() instanceof PointPlacement) {
        PointPlacement pl = ((PointPlacement) symbolizer.getLabelPlacement()
            .getPlacement());
        if (pl.getRotation() != null) {
          double rotationval = pl.getRotation().getRotationValue(feature);
          pl.getRotation().setRotationValue(rotationval);
        }

      }
    }
    String text = (value == null) ? null : value.toString();
    if (text != null) {
      paint(symbolizer, text, feature.getGeom(), viewport, graphics, opacity);
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
  public static void paint(TextSymbolizer symbolizer, String text,
      IGeometry geometry, Viewport viewport, Graphics2D graphics,
      double opacity) {
    // Initialize the color with which to actually paint the text
    Color fillColor = ColorUtil.getColorWithOpacity(Color.black, opacity);
    if (symbolizer.getFill() != null) {
      fillColor = ColorUtil.getColorWithOpacity(symbolizer.getFill().getColor(),
          opacity);
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
        scaleSymbolizerUOMToDataUOM = 1
            / viewport.getModelToViewTransform().getScaleX();
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
    graphics.setFont(awtFont);
    // Initialize the color for the halo around the text
    Color haloColor = ColorUtil.getColorWithOpacity(Color.WHITE, opacity);
    float haloRadius = 1.0f;
    if (symbolizer.getHalo() != null) {
      if (symbolizer.getHalo().getFill() != null) {
        haloColor = ColorUtil.getColorWithOpacity(
            symbolizer.getHalo().getFill().getColor(), opacity);
      }
      haloRadius = symbolizer.getHalo().getRadius();
    }
    LabelPlacement labelPlacement = symbolizer.getLabelPlacement();
    if (labelPlacement != null && labelPlacement.getPlacement() != null) {
      Placement placement = labelPlacement.getPlacement();
      if (PointPlacement.class.isAssignableFrom(placement.getClass())) {
        PointPlacement pointPlacement = (PointPlacement) placement;
        try {
          paint(pointPlacement, text, fillColor, haloColor, haloRadius,
              geometry.centroid(), viewport, graphics, scaleUOMToPixels);
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (LinePlacement.class.isAssignableFrom(placement.getClass())) {
          LinePlacement linePlacement = (LinePlacement) placement;
          float offset = linePlacement.getPerpendicularOffset()
              * (float) scaleSymbolizerUOMToDataUOM;
          IGeometry g = geometry;
          if (offset != 0.0f) {
            g = JtsAlgorithms.offsetCurve(geometry, offset);
          }
          if (IMultiCurve.class.isAssignableFrom(g.getClass())) {
            IMultiCurve<IOrientableCurve> multiCurve = (IMultiCurve<IOrientableCurve>) g;
            for (IOrientableCurve curve : multiCurve) {
              try {
                paint(linePlacement, text, fillColor, haloColor, haloRadius,
                    curve, viewport, graphics, scaleUOMToPixels);
              } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
              }
            }
          } else {
            try {
              paint(linePlacement, text, fillColor, haloColor, haloRadius, g,
                  viewport, graphics, scaleUOMToPixels);
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
  private static void paint(LinePlacement linePlacement, String text,
      Color fillColor, Color haloColor, float haloRadius, IGeometry geometry,
      Viewport viewport, Graphics2D graphics, double scale)
      throws NoninvertibleTransformException {
    if (linePlacement.isGeneralizeLine()) {
      // we have to generalize the geometry first
      double sigma = graphics.getFontMetrics().getMaxAdvance() / scale;
      geometry = GaussianFilter
          .gaussianFilter(new GM_LineString(geometry.coord()), sigma, 1.0);
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
    Stroke s = new TextStroke(text, graphics.getFont(), false,
        linePlacement.isRepeated(), false,
        linePlacement.getInitialGap() * (float) scale,
        linePlacement.getGap() * (float) scale);
    Shape textShape = s.createStrokedShape(lineShape);
    // halo
    if (haloColor != null) {
      graphics.setColor(haloColor);
      graphics.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND));
      graphics.draw(textShape);
    }
    graphics.setColor(fillColor);
    graphics.fill(textShape);
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
  private static void paint(PointPlacement pointPlacement, String text,
      Color fillColor, Color haloColor, float haloRadius,
      IDirectPosition position, Viewport viewport, Graphics2D graphics,
      double scale) throws NoninvertibleTransformException {
    FontRenderContext frc = graphics.getFontRenderContext();
    float rotation = 0.0f;
    if (pointPlacement.getRotation() != null) {
      rotation = (float) ((double) pointPlacement.getRotation()
          .getRotationValue() * Math.PI / 180);
    }
    GlyphVector gv = graphics.getFont().createGlyphVector(frc, text);
    Shape textShape = gv.getOutline();
    Rectangle2D bounds = textShape.getBounds2D();
    double width = bounds.getWidth();
    double height = bounds.getHeight();
    Point2D p = viewport.toViewPoint(position);
    AnchorPoint anchorPoint = pointPlacement.getAnchorPoint();
    float anchorPointX = (anchorPoint == null) ? 0.5f
        : anchorPoint.getAnchorPointX();
    float anchorPointY = (anchorPoint == null) ? 0.5f
        : anchorPoint.getAnchorPointY();
    Displacement displacement = pointPlacement.getDisplacement();
    float displacementX = (displacement == null) ? 0.0f
        : displacement.getDisplacementX();
    float displacementY = (displacement == null) ? 0.0f
        : displacement.getDisplacementY();
    float tx = (float) (p.getX() + displacementX * scale);
    float ty = (float) (p.getY() - displacementY * scale);
    AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
    t.rotate(rotation);
    t.translate(-width * anchorPointX, height * anchorPointY);
    textShape = t.createTransformedShape(textShape);
    // halo
    if (haloColor != null) {
      graphics.setColor(haloColor);
      graphics.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND));
      graphics.draw(textShape);

    }
    graphics.setColor(fillColor);
    graphics.fill(textShape);
  }

  /**
   * @param symbolizer
   * @param feature
   * @param viewport
   * @param graphics
   * @param opacity
   */
  @SuppressWarnings({ "unchecked" })
  public static void paint(ThematicSymbolizer symbolizer, IFeature feature,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (feature.getGeom() == null || viewport == null) {
      return;
    }
    for (DiagramSymbolizer s : symbolizer.getSymbolizers()) {

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
          ((IPopulation<IFeature>) DataSet.getInstance()
              .getPopulation("Triangulation")) //$NON-NLS-1$
                  .add(new DefaultFeature(a.getGeometrie()));
        }
        double maxDistance = Double.MIN_VALUE;
        Noeud maxNode = null;
        for (Arc a : t.getPopVoronoiEdges().select(feature.getGeom())) {
          if (!a.getGeometrie().intersectsStrictement(feature.getGeom())) {
            ((Population<DefaultFeature>) DataSet.getInstance()
                .getPopulation("MedialAxis")) //$NON-NLS-1$
                    .add(new DefaultFeature(a.getGeometrie()));
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
          return;
        }
        position = maxNode.getGeometrie().getPosition();
        symbolizer.getPoints().put(feature, position);
        symbolizer.getRadius().put(feature, maxDistance);
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

      Point2D point = null;
      try {
        point = viewport.toViewPoint(position);
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
        return;
      }

      if (s.getDiagramType().equalsIgnoreCase("piechart")) { //$NON-NLS-1$
        double startAngle = 0.0;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue()
              .evaluate(feature)).doubleValue();
          if (value == 0) {
            continue;
          }
          double arcAngle = 3.6 * value;
          graphics.setColor(ColorUtil.getColorWithOpacity(
              thematicClass.getFill().getColor(), opacity));
          graphics.fillArc((int) (point.getX() - size),
              (int) (point.getY() - size), (int) (2 * size), (int) (2 * size),
              (int) startAngle, (int) arcAngle);
          graphics.setColor(Color.BLACK);
          graphics.drawArc((int) (point.getX() - size),
              (int) (point.getY() - size), (int) (2 * size), (int) (2 * size),
              (int) startAngle, (int) arcAngle);
          startAngle += arcAngle;
        }
      } else if (s.getDiagramType().equalsIgnoreCase("barchart")) {
        double part = 0.70;
        graphics.setColor(Color.BLACK);
        drawArrow(graphics, (int) (point.getX() - part * size),
            (int) (point.getY() + part * size),
            (int) (point.getX() - part * size),
            (int) (point.getY() - part * size));
        drawArrow(graphics, (int) (point.getX() - part * size),
            (int) (point.getY() + part * size),
            (int) (point.getX() + part * size),
            (int) (point.getY() + part * size));

        int width = (int) (2 * part * size / (3 * s.getThematicClass().size()));
        int startX = width;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue()
              .evaluate(feature)).doubleValue();
          int hauteur = (int) (value * 2 * part * size / 100);
          graphics.setColor(ColorUtil.getColorWithOpacity(
              thematicClass.getFill().getColor(), opacity));
          graphics.fillRect((int) (point.getX() - part * size) + startX,
              (int) (point.getY() + part * size) - hauteur, 2 * width, hauteur);

          startX += 3 * width;
        }
      } else if (s.getDiagramType().equalsIgnoreCase("rosechart90")
          || s.getDiagramType().equalsIgnoreCase("rosechart180")) {
        int nbPas = s.getThematicClass().size();
        double part = 1.0; // 0.90;
        size *= part;
        double x0 = point.getX() - size;
        double y0 = point.getY() - size;
        // graphics.setColor(Color.WHITE);
        // graphics.fillRect((int)(point.getX() - size),
        // (int)(point.getY() -
        // size), (int)(2 * size.doubleValue()), (int)(2*
        // size.doubleValue()));
        // graphics.setColor(Color.RED);
        // graphics.drawString(".", (int)point.getX(),
        // (int)point.getY());

        // Portions
        double max = 0;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue()
              .evaluate(feature)).doubleValue();
          if (value > max)
            max = value;
        }
        graphics.setColor(Color.ORANGE);
        int pas = 10;
        if (s.getDiagramType().equalsIgnoreCase("rosechart90")) {
          pas = 90 / nbPas;
        } else if (s.getDiagramType().equalsIgnoreCase("rosechart180")) {
          pas = 180 / nbPas;
        }
        double angleDegre = 0;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue()
              .evaluate(feature)).doubleValue();
          if (value <= 0) {
            value = 1;
          }
          int a1 = (int) angleDegre + 1;
          int a2 = pas - 2;
          int l = (int) (size - value * size / max);
          // graphics.setColor(Color.ORANGE);
          graphics.setColor(ColorUtil.getColorWithOpacity(
              thematicClass.getFill().getColor(), opacity));
          graphics.fillArc((int) (x0 + l), (int) (y0 + l),
              (int) (2 * size - 2 * l), (int) (2 * size - 2 * l), a1, a2);
          angleDegre = angleDegre + pas;
        }

        // Axes
        graphics.setColor(Color.LIGHT_GRAY);
        float dash1[] = { 10.0f };
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        graphics.setStroke(dashed);
        graphics.drawArc((int) (x0 + 1 * size / 4), (int) (y0 + 1 * size / 4),
            (int) (2 * size - 2 * size / 4), (int) (2 * size - 2 * size / 4), 0,
            180);
        graphics.drawArc((int) (x0 + 2 * size / 4), (int) (y0 + 2 * size / 4),
            (int) (2 * size - 4 * size / 4), (int) (2 * size - 4 * size / 4), 0,
            180);
        graphics.drawArc((int) (x0 + 3 * size / 4), (int) (y0 + 3 * size / 4),
            (int) (2 * size - 6 * size / 4), (int) (2 * size - 6 * size / 4), 0,
            180);
        // graphics.drawLine((int)(x0 + size), (int)(y0 + size),
        // (int)(x0 +
        // size), (int)(y0));
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(1.0f));
        graphics.drawArc((int) (x0 + 0 * size / 4), (int) (y0 + 0 * size / 4),
            (int) (2 * size - 0 * size / 4), (int) (2 * size - 0 * size / 4), 0,
            180);
        graphics.drawLine((int) (x0), (int) (y0 + size), (int) (x0 + 2 * size),
            (int) (y0 + size));

      } else if (s.getDiagramType().equalsIgnoreCase("linechart")) {

        double part = 0.70;
        graphics.setColor(Color.BLACK);
        drawArrow(graphics, (int) (point.getX() - part * size),
            (int) (point.getY() + part * size),
            (int) (point.getX() - part * size),
            (int) (point.getY() - part * size));
        drawArrow(graphics, (int) (point.getX() - part * size),
            (int) (point.getY() + part * size),
            (int) (point.getX() + part * size),
            (int) (point.getY() + part * size));

        /*
         * int width = (int) (2 * part * size / s.getThematicClass().size());
         * int startX = 0; for (ThematicClass thematicClass :
         * s.getThematicClass()) { double value = ((Number)
         * thematicClass.getClassValue().evaluate(feature)).doubleValue( ); int
         * hauteur = (int) (value * 2 * part * size / 100);
         * graphics.setColor(ColorUtil.getColorWithOpacity(thematicClass
         * .getFill().getColor(), opacity));
         * graphics.drawRect((int)(point.getX() - part*size) + startX,
         * (int)(point.getY() + part*size) - hauteur, 5, 5); //
         * graphics.fillRect((int)(point.getX() - part*size) + startX,
         * (int)(point.getY() + part*size) - hauteur, 2 * width, hauteur);
         * 
         * startX += width; }
         */

      }
    }
  }

  private final static int ARR_SIZE = 2;

  private static void drawArrow(Graphics2D g1, int x1, int y1, int x2, int y2) {
    Graphics2D g = (Graphics2D) g1.create();

    double dx = x2 - x1, dy = y2 - y1;
    double angle = Math.atan2(dy, dx);
    int len = (int) Math.sqrt(dx * dx + dy * dy);
    AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
    at.concatenate(AffineTransform.getRotateInstance(angle));
    g.transform(at);

    // Draw horizontal arrow starting in (0, 0)
    g.drawLine(0, 0, len, 0);
    g.fillPolygon(new int[] { len, len - ARR_SIZE, len - ARR_SIZE, len },
        new int[] { 0, -ARR_SIZE, ARR_SIZE, 0 }, 4);
  }
}
