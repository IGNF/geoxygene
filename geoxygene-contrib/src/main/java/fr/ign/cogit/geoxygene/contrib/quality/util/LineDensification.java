package fr.ign.cogit.geoxygene.contrib.quality.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * A method to to densify correctly a linestring
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

    // remplissage
    int iDens = 0;
    double dist = 0.0, angle = 0.0, longueur;
    for (int i = 0; i < coords.length - 1; i++) {
      Coordinate coord0 = coords[i], coord1 = coords[i + 1];

      longueur = coord0.distance(coord1);
      if (dist <= longueur)
        angle = Math.atan2(coord1.y - coord0.y, coord1.x - coord0.x);

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

  public static GM_LineString densification(GM_LineString ls, double pas) {
    GM_LineString ls_ = null;
    try {
      ls_ = (GM_LineString) AdapterFactory.toGM_Object(densification(
          (LineString) AdapterFactory.toGeometry(new GeometryFactory(), ls),
          pas));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ls_;
  }

}
