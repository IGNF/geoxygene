package fr.ign.cogit.geoxygene.sig3d.convert.geom;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * 
 * 
 */
public class FromPolygonToTriangle {

  private final static Logger logger = Logger
      .getLogger(FromPolygonToTriangle.class.getName());

  public static List<ITriangle> convertAndTriangle(
      List<IOrientableSurface> polygonList) {
    List<ITriangle> lT = new ArrayList<ITriangle>();

    int nbPoly = polygonList.size();
    for (int i = 0; i < nbPoly; i++) {

      IOrientableSurface pol = polygonList.get(i);

      List<ITriangle> tri = convertAndTriangle(pol);
      if (tri != null) {
        lT.addAll(tri);
      } else {
        return null;
      }

    }
    return lT;
  }

  public static List<ITriangle> convertAndTriangle(IOrientableSurface pol) {

    List<ITriangle> lTri = new ArrayList<ITriangle>();

    IDirectPositionList dpl = pol.coord();
    if (dpl.size() == 4) {
      lTri.add(new GM_Triangle(dpl.get(0), dpl.get(1), dpl.get(2)));
      return lTri;

    } else if (dpl.size() == 5) {
      lTri.add(new GM_Triangle(dpl.get(0), dpl.get(1), dpl.get(2)));
      lTri.add(new GM_Triangle(dpl.get(2), dpl.get(3), dpl.get(0)));

      return lTri;

    } else {

      logger.warn("Conversion to ITriangle impossible. NB Points : "
          + dpl.size());
    }

    return null;

  }

  public static List<Triangle> convertTriangleTopo(
      List<IOrientableSurface> polygonList) {

    List<Triangle> lTri = new ArrayList<Triangle>();

    for (IOrientableSurface os : polygonList) {
      lTri.add((Triangle) os);
    }

    return lTri;

  }

}
