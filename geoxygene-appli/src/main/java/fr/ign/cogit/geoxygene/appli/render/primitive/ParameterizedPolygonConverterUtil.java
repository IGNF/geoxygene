package fr.ign.cogit.geoxygene.appli.render.primitive;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * Conversion utility between IGeometry and ParameterizedPolygon
 * 
 * @author JeT
 * 
 */
public class ParameterizedPolygonConverterUtil {

    private static Logger logger = Logger.getLogger(ParameterizedPolygonConverterUtil.class.getName()); // logger

    private ParameterizedPolygonConverterUtil() {
        // Utility class
    }

    public static DrawingPrimitive generateParameterizedPolygon(final PolygonSymbolizer polygonSymbolizer, final IGeometry geom, final Viewport viewport) {
        if (geom.isPolygon()) {
            return generateParameterizedGMPolygon(polygonSymbolizer, (GM_Polygon) geom, viewport);
        } else if (geom.isMultiSurface()) {
            return generateParameterizedMultiSurface(polygonSymbolizer, (GM_MultiSurface) geom, viewport);
        } else {
            logger.error("generateParameterizedPolygon cannot handle geometry type " + geom.getClass().getSimpleName());
            return null;
        }
    }

    public static MultiDrawingPrimitive generateParameterizedMultiSurface(final PolygonSymbolizer polygonSymbolizer,
            final GM_MultiSurface<IOrientableSurface> surface, final Viewport viewport) {
        MultiDrawingPrimitive multi = new MultiDrawingPrimitive();

        for (IGeometry element : surface.getList()) {
            DrawingPrimitive polygon = generateParameterizedPolygon(polygonSymbolizer, element, viewport);
            if (polygon != null) {
                multi.addPrimitive(polygon);
            }
        }
        return multi;
    }

    public static ParameterizedPolygon generateParameterizedGMPolygon(final PolygonSymbolizer polygonSymbolizer, final GM_Polygon polygon,
            final Viewport viewport) {
        ParameterizedPolygon newPolygon = new ParameterizedPolygon(polygon, viewport);
        // everything is already done in the ParameterizedPolygon constructor
        //    try {
        //      // put all "holes" in a list 
        //      for (IRing ring : polygon.getInterior()) {
        //        Shape innerShape = viewport.toShape(ring);
        //        if (innerShape != null) {
        //          newPolygon.addInnerFrontier(innerShape, parameterizer);
        //        }
        //
        //      }
        //      // draw the outer & inner frontier
        //      Shape outerShape = viewport.toShape(polygon.getExterior());
        //      newPolygon.setOuterFrontier(outerShape, parameterizer);
        //
        //    } catch (NoninvertibleTransformException e) {
        //      e.printStackTrace();
        //    }

        return newPolygon;
    }

}
