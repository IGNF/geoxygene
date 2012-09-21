package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * Classe permettant de construire un toit plat C'est à dire de créer une
 * surface l'aide d'un z et d'un anneau
 * 
 * 
 * Class to create a flat roof from a z and a polygon
 * 
 * 
 */
public class RoofConstruction {

  ArrayList<GM_OrientableSurface> lRoof = new ArrayList<GM_OrientableSurface>();

  /**
   * 
   * On construit le toit comme une couverture à partir des sommets de murs
   * 
   * @param lSSA3
   * @param z
   */
  public RoofConstruction(List<GM_LineString> lSSA3, double z) {

    DirectPosition dp1;
    GM_LineString lS;

    DirectPositionList lPoints = new DirectPositionList();

    int nbElem = lSSA3.size();
    for (int i = 0; i < nbElem; i++) {

      lS = lSSA3.get(i);
      dp1 = new DirectPosition(lS.coord().get(0).getX(), lS.coord().get(0)
          .getY(), z);

      lPoints.add(dp1);
    }

    lS = lSSA3.get(0);
    dp1 = new DirectPosition(lS.coord().get(0).getX(),
        lS.coord().get(0).getY(), z);

    // On ferme le polygone
    lPoints.add(dp1);

    GM_LineString ls = new GM_LineString(lPoints);
    GM_OrientableSurface f = new GM_Polygon(ls);

    this.lRoof.add(f);

  }

  /**
   * 
   * @return les faces composant le toit
   */
  public ArrayList<GM_OrientableSurface> getRoof() {
    return this.lRoof;
  }
}
