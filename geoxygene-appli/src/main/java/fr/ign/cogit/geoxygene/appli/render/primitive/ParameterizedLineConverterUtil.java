package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * Conversion utility between IGeometry and ParameterizedPolyline
 * @author JeT
 *
 */
public class ParameterizedLineConverterUtil {

  private static Logger logger = Logger.getLogger(ParameterizedLineConverterUtil.class.getName()); // logger

  private ParameterizedLineConverterUtil() {
    // Utility class
  }

  /**
   * Convert shapes to drawing primitives
   * @param shapes shapes to convert
   * @param viewport viewport used to generate shapes
   * @return a list of drawing primitives
   */
  public static DrawingPrimitive generateParameterizedPolyline(final List<Shape> shapes, final Viewport viewport, final Parameterizer parameterizer) {
    MultiDrawingPrimitive multiPrimitive = new MultiDrawingPrimitive();
    for (Shape shape : shapes) {
      DrawingPrimitive primitive = generateParameterizedPolyline(shape, parameterizer);
      if (primitive != null) {
        multiPrimitive.addPrimitive(primitive);
      }
    }
    // if there is only one primitive stored, no need to encapsulate it into a multiprimitive 
    if (multiPrimitive.getPrimitives().size() == 1) {
      return multiPrimitive.getPrimitives().get(0);
    }
    return multiPrimitive;
  }

  /**
   * Generate a list of all the points from a shape representing a polyline
   * @param shape shape to extract the collection of points
   * @return point list 
   * TODO: if there is a MOVE_TO in the middle of the shape, we should return a list of polyline instead of a simple polyline...
   */
  public static ParameterizedPolyline generateParameterizedPolyline(final Shape shape, final Parameterizer parameterizer) {
    PathIterator pathIterator = shape.getPathIterator(null);
    ParameterizedPolyline line = new ParameterizedPolyline();
    double distance = 0; // distance from the line start
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

        if (parameterizer != null) {
          distance = parameterizer.getLinearParameter(coords[0], coords[1]);
        }

        line.addPoint(point, distance);
        break;
      case PathIterator.SEG_CUBICTO:
        point.x = coords[4];
        point.y = coords[5];
        if (parameterizer != null) {
          distance = parameterizer.getLinearParameter(coords[0], coords[1]);
        }
        line.addPoint(point, distance);
        break;
      case PathIterator.SEG_QUADTO:
        point.x = coords[2];
        point.y = coords[3];
        if (parameterizer != null) {
          distance = parameterizer.getLinearParameter(coords[0], coords[1]);
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

  /**
   * This method is the exact copy of RenderUtil.getShapeList() method
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @param fill true if the stroke width should be used to build the shapes, ie if they will be used for graphic fill
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  public static List<Shape> getShapeList(final LineSymbolizer symbolizer, final IGeometry geometry, final Viewport viewport, final boolean fill) {
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
   * This method is the exact copy of RenderUtil.getLineStringShapeList() method
   * @param symbolizer a line symbolizer
   * @param line the geometry of the line
   * @param viewport the viewport used for rendering
   * @param fill true if the stroke width should be used to build the shapes, ie if they will be used for graphic fill
   * @param scale scale to go from the symbolizer's uom to the data uom
   * @return
   */
  public static List<Shape> getLineStringShapeList(final LineSymbolizer symbolizer, final IOrientableCurve line, final Viewport viewport,
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

}
