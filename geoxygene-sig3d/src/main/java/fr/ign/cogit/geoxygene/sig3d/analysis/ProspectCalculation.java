package fr.ign.cogit.geoxygene.sig3d.analysis;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

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
 * @version 1.7
 * */
public class ProspectCalculation {

  /**
   * Permet de calculer l'emprise d'une parcelle par d = alpha * h sur l'emprise
   * géomModif On calcule la distance a partir de la géométrie geom
   * 
   * @param geomModif
   * @param geom
   * @param pente
   * @param hObj
   * @return
   */
  public static IGeometry calculate(IGeometry geomModif, IGeometry geom,
      double pente, double hObj) {
    DirectPositionList dpl = new DirectPositionList();

    IDirectPositionList dplIni = geomModif.coord();

    ApproximatedPlanEquation eq = new ApproximatedPlanEquation(dplIni);

    if (eq.getNormale().getZ() < 0) {
      dplIni.inverseOrdre();

    }

    int nbPoints = dplIni.size();

    Box3D b = new Box3D(geomModif);

    double zObj = b.getLLDP().getZ();

    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition dp = dplIni.get(i);
      GM_Object geom2 = new GM_Point(dp);
      double distance = geom2.distance(geom);

    
      dpl.add(new DirectPosition(dp.getX(), dp.getY(), distance * pente + zObj + hObj));

    }

    return new GM_Polygon(new GM_LineString(dpl));
  }
  
}
