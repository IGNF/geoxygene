/**
 * 
 */
package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author juju
 * 
 */
public class LineDensification {

  public static LineString densification(LineString ls, double pas) {

    // coordonnees de la ligne initiale
    Coordinate[] coords = ls.getCoordinates();

    // table des coordonnees densifiees
    int nbPoints = (int) (ls.getLength() / pas);
    Coordinate[] coordsDens = new Coordinate[nbPoints + 1];

    if (nbPoints + 1 < coords.length)
      return ls;

    // remplissage
    int iDens = 0;
    double dist = 0.0, angle = 0.0, longueur;
    for (int i = 0; i < coords.length - 1; i++) {
      Coordinate coord0 = coords[i], coord1 = coords[i + 1];

      longueur = coord0.distance(coord1);
      if (dist <= longueur) {
        angle = Math.atan2(coord1.y - coord0.y, coord1.x - coord0.x);
      }

      while (dist <= longueur) {

        // ajouter point a ligne densifiee
        coordsDens[iDens] = new Coordinate(coord0.x + dist * Math.cos(angle),
            coord0.y + dist * Math.sin(angle));

        dist += pas;
        iDens++;
      }
      dist -= longueur;
    }

    // le dernier point
    coordsDens[nbPoints] = coords[coords.length - 1];

    return new GeometryFactory().createLineString(coordsDens);
  }

  public static ILineString densification(ILineString ls, double pas) {
    ILineString ls_ = null;
    try {
      ls_ = (ILineString) AdapterFactory.toGM_Object(LineDensification
          .densification((LineString) AdapterFactory.toGeometry(
              new GeometryFactory(), ls), pas));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ls_;
  }

  public static IPolygon densification(IPolygon poly, double pas) {
    IPolygon poly_ = null;
    try {
      ILineString densExt = densification(poly.exteriorLineString(), pas);
      poly_ = new GM_Polygon(densExt);
      for (IRing ring : poly.getInterior()) {
        ILineString densRing = densification((ILineString) ring.getPrimitive(),
            pas);
        poly_.addInterior(new GM_Ring(densRing));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly_;
  }

  public static IGeometry densification(IGeometry geom, double pas) {
    if (geom instanceof IPolygon)
      return densification((IPolygon) geom, pas);
    if (geom instanceof ILineString)
      return densification((ILineString) geom, pas);
    return geom;
  }
}
