/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

/**
 * Implementation of a basic morphing function that maps with straight lines the
 * points at similar curvilinear coordinates.
 * 
 * @author Guillaume Touya
 * 
 */
public class BasicMorphing implements ContinuousGeneralisationMethod {

  private ILineString geomIni, geomFinal;
  private ILineString densGeomIni;

  @Override
  public IGeometry getGeomIni() {
    return geomIni;
  }

  @Override
  public IGeometry getGeomFinal() {
    return geomFinal;
  }

  @Override
  public IGeometry continuousGeneralisation(double t) {
    // t must be between 0 and 1
    if (t < 0.0)
      return null;
    if (t > 1.0)
      return null;
    // first map the vertices of geomIni densified to points in geomFin
    Map<IDirectPosition, IDirectPosition> mapping = new HashMap<>();
    double dist = 0.0;
    double total = geomIni.length();
    double totalFinal = geomFinal.length();
    IDirectPosition prevPt = null;
    for (IDirectPosition pt : densGeomIni.coord()) {
      if (prevPt == null) {
        prevPt = pt;
        mapping.put(pt, geomFinal.startPoint());
        continue;
      }
      dist += pt.distance2D(prevPt);
      double ratio = dist / total;

      // get the point at the curvilinear coordinate corresponding to
      // ratio
      double curvi = totalFinal * ratio;
      IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(geomFinal,
          curvi);
      mapping.put(pt, finalPt);
      prevPt = pt;
    }

    // then, compute the intermediate position between each correspondant
    IDirectPositionList coord = new DirectPositionList();
    for (IDirectPosition pt1 : densGeomIni.coord()) {
      IDirectPosition pt2 = mapping.get(pt1);
      double newX = pt1.getX() + t * (pt2.getX() - pt1.getX());
      double newY = pt1.getY() + t * (pt2.getY() - pt1.getY());
      IDirectPosition newPt = new DirectPosition(newX, newY);
      coord.add(newPt);
    }
    return GeometryEngine.getFactory().createILineString(coord);
  }

  public BasicMorphing(ILineString geomIni, ILineString geomFinal) {
    super();
    this.geomIni = geomIni;
    this.geomFinal = geomFinal;
    this.densGeomIni = LineDensification.densification2(geomIni, 2.0);

  }

}
