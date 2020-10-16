/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.datatools;

import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * This class contains static tools to change the CRS of points and geometries.
 * @author GTouya
 * 
 */
public class CRSConversion {

  public static IGeometry changeCRS(IGeometry geom,
      CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2)
      throws Exception {
    // first convert geometry to JTS
    Geometry jtsGeom = JtsGeOxygene.makeJtsGeom(geom);
    GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();
    transformer.setCoordinateReferenceSystem(crs2);
    transformer.setMathTransform(CRS.findMathTransform(crs1, crs2));
    Geometry jtsGeom2 = transformer.transform(jtsGeom);
    return JtsGeOxygene.makeGeOxygeneGeom(jtsGeom2);
  }

  public static IGeometry changeCRS(Geometry geom,
      CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2)
      throws Exception {
    // first convert geometry to JTS
    GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();
    transformer.setCoordinateReferenceSystem(crs2);
    transformer.setMathTransform(CRS.findMathTransform(crs1, crs2));
    Geometry jtsGeom = transformer.transform(geom);
    return JtsGeOxygene.makeGeOxygeneGeom(jtsGeom);
  }

  public static IGeometry changeCRS(IGeometry geom, String epsg1, String epsg2,
      boolean projected1, boolean projected2, boolean reverse)
      throws Exception {
    CoordinateReferenceSystem crs1 = CRS.decode("EPSG:" + epsg1, projected2);
    CoordinateReferenceSystem crs2 = CRS.decode("EPSG:" + epsg2, projected1);
    IGeometry newGeom = changeCRS(geom, crs1, crs2);
    if (reverse) {
      // change x to y and conversely
      IDirectPositionList coords = new DirectPositionList();
      for (int i = 0; i < newGeom.numPoints(); i++) {
        IDirectPosition point = newGeom.coord().get(i);
        coords.add(new DirectPosition(point.getY(), point.getX()));
      }
      if (newGeom.isPoint())
        newGeom = GeometryEngine.getFactory().createPoint(coords.get(0));
      else if (newGeom.isLineString())
        newGeom = GeometryEngine.getFactory().createILineString(coords);
      else if (newGeom.isMultiCurve())
        newGeom = GeometryEngine.getFactory().createILineString(coords);
      else if (newGeom.isPolygon())
        newGeom = GeometryEngine.getFactory().createIPolygon(coords);
      else if (newGeom.isMultiSurface())
        newGeom = GeometryEngine.getFactory().createIPolygon(coords);
    }
    return newGeom;
  }

  public static IGeometry changeCRS(IGeometry geom, String epsg1, String epsg2,
      boolean projected1, boolean projected2) throws Exception {
    CoordinateReferenceSystem crs1 = CRS.decode("EPSG:" + epsg1, projected2);
    CoordinateReferenceSystem crs2 = CRS.decode("EPSG:" + epsg2, projected1);
    return changeCRS(geom, crs1, crs2);
  }

  public static IGeometry changeCRS(Geometry geom, String epsg1, String epsg2,
      boolean projected1, boolean projected2) throws Exception {
    CoordinateReferenceSystem crs1 = CRS.decode("EPSG:" + epsg1, projected2);
    CoordinateReferenceSystem crs2 = CRS.decode("EPSG:" + epsg2, projected1);
    return changeCRS(geom, crs1, crs2);
  }

  public static IGeometry changeCRSToUTM31N(Geometry geom) throws Exception {
    return changeCRS(geom, "4326", "32631", false, true);
  }

  /**
   * Get the EPSG code for the WGS84/UTM zone projection given in a String
   * format like '25S' or '6N'.
   * @param zone
   * @return
   */
  public static String getEPSGFromUTMZone(String zone) {
    StringBuffer epsg = new StringBuffer("32");
    // first check if it's north or south UTM zone
    String last = zone.substring(zone.length() - 1);
    if (last.equals("N"))
      epsg.append("6");
    else
      epsg.append("7");
    // then, get the zone number
    String number = zone.substring(0, zone.length() - 1);
    if (number.length() == 1)
      number = "0" + number;
    epsg.append(number);
    return epsg.toString();
  }

  /**
   * Converts a position in WGS84 geographic coordinates into a Lambert 93
   * position.
   * @param latitude
   * @param longitude
   * @return
   */
  public static IDirectPosition wgs84ToLambert93(double latitude,
      double longitude) {
    // variables:

    // systme WGS84
    double a = 6378137; // demi grand axe de l'ellipsoide (m)
    double e = 0.08181919106; // premire excentricit de l'ellipsoide

    // paramtres de projections
    double lc = deg2rad(3); // longitude de rfrence
    double phi0 = deg2rad(46.5); // latitude d'origine en radian
    double phi1 = deg2rad(44); // 1er parallele automcoque
    double phi2 = deg2rad(49); // 2eme parallele automcoque

    double x0 = 700000; // coordonnes l'origine
    double y0 = 6600000; // coordonnes l'origine

    // coordonnes du point traduire
    double phi = deg2rad(latitude);
    double l = deg2rad(longitude);

    // calcul des grandes normales
    double gN1 = a / Math.sqrt(1 - e * e * Math.sin(phi1) * Math.sin(phi1));
    double gN2 = a / Math.sqrt(1 - e * e * Math.sin(phi2) * Math.sin(phi2));

    // calculs de slatitudes isomtriques
    double gl1 = Math.log(Math.tan(Math.PI / 4 + phi1 / 2)
        * Math.pow((1 - e * Math.sin(phi1)) / (1 + e * Math.sin(phi1)), e / 2));

    double gl2 = Math.log(Math.tan(Math.PI / 4 + phi2 / 2)
        * Math.pow((1 - e * Math.sin(phi2)) / (1 + e * Math.sin(phi2)), e / 2));

    double gl0 = Math.log(Math.tan(Math.PI / 4 + phi0 / 2)
        * Math.pow((1 - e * Math.sin(phi0)) / (1 + e * Math.sin(phi0)), e / 2));

    double gl = Math.log(Math.tan(Math.PI / 4 + phi / 2)
        * Math.pow((1 - e * Math.sin(phi)) / (1 + e * Math.sin(phi)), e / 2));

    // calcul de l'exposant de la projection
    double n = (Math.log((gN2 * Math.cos(phi2)) / (gN1 * Math.cos(phi1))))
        / (gl1 - gl2);// ok

    // calcul de la constante de projection
    double c = ((gN1 * Math.cos(phi1)) / n) * Math.exp(n * gl1);// ok

    // calcul des coordonnes
    double ys = y0 + c * Math.exp(-1 * n * gl0);

    // calcul des coordonnes lambert
    double x93 = x0 + c * Math.exp(-1 * n * gl) * Math.sin(n * (l - lc));
    double y93 = ys - c * Math.exp(-1 * n * gl) * Math.cos(n * (l - lc));

    return new DirectPosition(x93, y93);
  } // wgs84_to_lambert93

  /**
   * Convert an angle in degrees into radians.
   * @param d
   * @return
   */
  private static double deg2rad(double d) {
    return d * Math.PI / 180.0;
  }

}
