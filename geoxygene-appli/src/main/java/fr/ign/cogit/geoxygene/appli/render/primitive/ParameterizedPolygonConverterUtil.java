package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * Conversion utility between IGeometry and ParameterizedPolygon
 * @author JeT
 *
 */
public class ParameterizedPolygonConverterUtil {

  private static Logger logger = Logger.getLogger(ParameterizedPolygonConverterUtil.class.getName()); // logger

  private ParameterizedPolygonConverterUtil() {
    // Utility class
  }

  public static DrawingPrimitive generateParameterizedPolygon(final PolygonSymbolizer polygonSymbolizer, final IGeometry geom,
      final Viewport viewport, final Parameterizer parameterizer) {
    if (geom.isPolygon()) {
      return generateParameterizedGMPolygon(polygonSymbolizer, (GM_Polygon) geom, viewport, parameterizer);
    } else if (geom.isMultiSurface()) {
      return generateParameterizedMultiSurface(polygonSymbolizer, (GM_MultiSurface) geom, viewport, parameterizer);
    } else {
      logger.error("generateParameterizedPolygon cannot handle geometry type " + geom.getClass().getSimpleName());
      return null;
    }
  }

  public static MultiDrawingPrimitive generateParameterizedMultiSurface(final PolygonSymbolizer polygonSymbolizer,
      final GM_MultiSurface<IOrientableSurface> surface, final Viewport viewport, final Parameterizer parameterizer) {
    MultiDrawingPrimitive multi = new MultiDrawingPrimitive();

    for (IGeometry element : surface.getList()) {
      DrawingPrimitive polygon = generateParameterizedPolygon(polygonSymbolizer, element, viewport, parameterizer);
      if (polygon != null) {
        multi.addPrimitive(polygon);
      }
    }
    return multi;
  }

  public static ParameterizedPolygon generateParameterizedGMPolygon(final PolygonSymbolizer polygonSymbolizer, final GM_Polygon polygon,
      final Viewport viewport, final Parameterizer parameterizer) {
    ParameterizedPolygon newPolygon = new ParameterizedPolygon(polygon, viewport, parameterizer);
    try {
      // put all "holes" in a list 
      for (IRing ring : polygon.getInterior()) {
        Shape innerShape = viewport.toShape(ring);
        if (innerShape != null) {
          newPolygon.addInnerFrontier(innerShape, parameterizer);
        }

      }
      // draw the outer & inner frontier
      Shape outerShape = viewport.toShape(polygon.getExterior());
      newPolygon.setOuterFrontier(outerShape, parameterizer);

    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

    return newPolygon;
  }

  public static ParameterizedPolygon generateParameterizedGMPolygon(final Shape shape, final Parameterizer parameterizer) {
    return new ParameterizedPolygon(shape, parameterizer);
  }

  //  /**
  //   * Draw a filled shape with open GL
  //   * @param outerShape Simple polyline describing the outer limit of the polygon
  //   * @param innerShapes list of simple polylines describing the inner limits of the polygon
  //   */
  //  private static void convertPolygon(final Viewport viewport, final Shape outerShape, final List<Shape> innerShapes, final Integer texIndex) {
  //
  //    if (texIndex != null) {
  //      glEnable(GL_TEXTURE_2D);
  //      glBindTexture(GL_TEXTURE_2D, texIndex);
  //    } else {
  //      GL11.glDisable(GL_TEXTURE_2D);
  //    }
  //    ParameterizedPolygon polygon = new ParameterizedPolygon();
  //    // tesselation
  //    GLUtessellator tesselator = gluNewTess();
  //    // Set callback functions
  //    TessCallback callback = new TessCallback(polygon);
  //    tesselator.gluTessCallback(GLU_TESS_VERTEX, callback);
  //    tesselator.gluTessCallback(GLU_TESS_BEGIN, callback);
  //    tesselator.gluTessCallback(GLU_TESS_END, callback);
  //    tesselator.gluTessCallback(GLU_TESS_COMBINE, callback);
  //
  //    tesselator.gluTessBeginPolygon(null);
  //
  //    tesselator.gluTessBeginContour();
  //    PathIterator pathIterator = outerShape.getPathIterator(null);
  //    float[] pathCoords = new float[6];
  //    while (!pathIterator.isDone()) {
  //      double coords[] = new double[6];
  //      int segmentType = pathIterator.currentSegment(pathCoords);
  //      if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
  //        // point coordinates
  //        coords[0] = pathCoords[0];
  //        coords[1] = pathCoords[1];
  //        coords[2] = 0;
  //        // texture coordinates
  //        coords[3] = pathCoords[0] / viewport.getScale() + viewport.getViewOrigin().getX();
  //        coords[4] = pathCoords[1] / viewport.getScale() - viewport.getViewOrigin().getY();
  //        coords[5] = 0;
  //        tesselator.gluTessVertex(coords, 0, coords);
  //      }
  //      pathIterator.next();
  //    }
  //
  //    tesselator.gluTessEndContour();
  //
  //    if (innerShapes != null && !innerShapes.isEmpty()) {
  //
  //      for (Shape innerShape : innerShapes) {
  //        tesselator.gluTessBeginContour();
  //        pathIterator = innerShape.getPathIterator(null);
  //        while (!pathIterator.isDone()) {
  //          double[] coords = new double[6];
  //          int segmentType = pathIterator.currentSegment(pathCoords);
  //          if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
  //            // point coordinates
  //            coords[0] = pathCoords[0];
  //            coords[1] = pathCoords[1];
  //            coords[2] = 0;
  //            // texture coordinates
  //            coords[3] = pathCoords[0] / viewport.getScale() + viewport.getViewOrigin().getX();
  //            coords[4] = pathCoords[1] / viewport.getScale() - viewport.getViewOrigin().getY();
  //            coords[5] = 0;
  //
  //            tesselator.gluTessVertex(coords, 0, coords);
  //          }
  //          pathIterator.next();
  //        }
  //
  //        tesselator.gluTessEndContour();
  //      }
  //
  //    }
  //
  //    tesselator.gluTessEndPolygon();
  //
  //  }
  //
  //  /**
  //   * Callback class used in gl tesselation process
  //   * @author JeT
  //   *
  //   */
  //  private static class TessCallback extends GLUtessellatorCallbackAdapter {
  //
  //    ParameterizedPolygon polygon = null;
  //
  //    public TessCallback(final ParameterizedPolygon polygon) {
  //      this.polygon = polygon;
  //    }
  //
  //    @Override
  //    public void edgeFlag(final boolean boundaryEdge) {
  //      //      System.err.println("edgeFlag " + boundaryEdge);
  //      super.edgeFlag(boundaryEdge);
  //    }
  //
  //    @Override
  //    public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
  //      //      System.err.println("combine " + coords + " " + data + " " + weight + " " + outData);
  //      //        double[] vertex = new double[6];
  //      //      vertex[0] = coords[0];
  //      //      vertex[1] = coords[1];
  //      //      vertex[2] = coords[2];
  //      //for (int i = 3; i < 6; i++)
  //      //vertex[i] = weight[0] * ((double[]) data[0])[i] + weight[1]
  //      //* ((double[]) data[1])[i] + weight[2] * ((double[]) data[2])[i] + weight[3]
  //      //* ((double[]) data[3])[i];
  //    }
  //
  //    @Override
  //    public void beginData(final int type, final Object polygonData) {
  //      //      System.err.println("beginData " + type + " " + polygonData);
  //      super.beginData(type, polygonData);
  //    }
  //
  //    @Override
  //    public void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {
  //      //      System.err.println("edgeFlagData " + boundaryEdge + " " + polygonData);
  //      super.edgeFlagData(boundaryEdge, polygonData);
  //    }
  //
  //    @Override
  //    public void vertexData(final Object vertexData, final Object polygonData) {
  //      //      System.err.println("vertexData " + vertexData + " " + polygonData);
  //      super.vertexData(vertexData, polygonData);
  //    }
  //
  //    @Override
  //    public void endData(final Object polygonData) {
  //      //      System.err.println("endData " + polygonData);
  //      super.endData(polygonData);
  //    }
  //
  //    @Override
  //    public void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData, final Object polygonData) {
  ////      glTexCoord2d((Double) data[3] * 0.0100, (Double) data[4] * 0.0100);
  ////      glVertex2d(coords[0], coords[1]);
  //      this.polygon.addQuadStripPoint(new Point2d(coords[0], coords[1]));
  //    }
  //
  //    @Override
  //    public void begin(final int type) {
  //      //      System.err.println("begin " + type);
  //      //      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
  //      glBegin(type);
  //    }
  //
  //    @Override
  //    public void end() {
  //      //      System.err.println("end");
  //      glEnd();
  //      //      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
  //    }
  //
  //    @Override
  //    public void vertex(final Object coords) {
  //      this.polygon.addQuadStripPoint(new Point2d(coords[0], coords[1]));
  ////      //      System.err.println("vertex " + coords);
  ////      glTexCoord2d(((double[]) coords)[3] * 0.0100, ((double[]) coords)[4] * 0.0100);
  ////      glVertex2d(((double[]) coords)[0], ((double[]) coords)[1]);
  //    }
  //
  //    @Override
  //    public void error(final int errnum) {
  //      String estring;
  //      estring = GLU.gluErrorString(errnum);
  //      logger.error("Tessellation Error Number: " + errnum);
  //      logger.error("Tessellation Error: " + estring);
  //      super.error(errnum);
  //    }
  //
  //    @Override
  //    public void errorData(final int errnum, final Object polygonData) {
  //      logger.error("Tesselation error : " + errnum + " + " + polygonData.toString());
  //      super.errorData(errnum, polygonData);
  //    }
  //
  //  }

}
