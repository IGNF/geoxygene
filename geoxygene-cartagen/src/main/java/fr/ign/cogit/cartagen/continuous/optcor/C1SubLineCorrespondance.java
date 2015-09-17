/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

/**
 * A C1 subline correspondances matches a subline of the initial line to morph
 * with a single vertex of the final line to morph.
 * @author GTouya
 * 
 */
public class C1SubLineCorrespondance implements SubLineCorrespondance {

  private ILineString subLine;
  private IDirectPosition characPt, prevPt, nextPt;

  public C1SubLineCorrespondance(ILineString subLine, IDirectPosition characPt,
      IDirectPosition prevPt, IDirectPosition nextPt) {
    super();
    this.subLine = subLine;
    this.characPt = characPt;
    this.prevPt = prevPt;
    this.nextPt = nextPt;
  }

  @Override
  public List<Object> getMatchedFeaturesInitial() {
    List<Object> matchedFeatures = new ArrayList<Object>();
    matchedFeatures.add(subLine);
    return matchedFeatures;
  }

  @Override
  public List<Object> getMatchedFeaturesFinal() {
    List<Object> matchedFeatures = new ArrayList<Object>();
    matchedFeatures.add(characPt);
    return matchedFeatures;
  }

  @Override
  public CorrespondanceType getType() {
    return CorrespondanceType.C1;
  }

  @Override
  public IDirectPositionList morphCorrespondance(double t) {
    // then, compute the intermediate position between each correspondant
    IDirectPositionList coord = new DirectPositionList();
    for (IDirectPosition pt1 : subLine.coord()) {
      double newX = pt1.getX() + t * (characPt.getX() - pt1.getX());
      double newY = pt1.getY() + t * (characPt.getY() - pt1.getY());
      IDirectPosition newPt = new DirectPosition(newX, newY);
      coord.add(newPt);
    }

    return coord;
  }

  @Override
  public void matchVertices(IDirectPositionList initialCoord,
      IDirectPositionList finalCoord) {
    double dist = 0.0;
    double total = subLine.length();
    IDirectPositionList listSubLine2 = new DirectPositionList();
    if (prevPt != null)
      listSubLine2.add(new Segment(prevPt, characPt).getMiddlePoint());
    listSubLine2.add(characPt);
    if (nextPt != null)
      listSubLine2.add(new Segment(characPt, nextPt).getMiddlePoint());
    ILineString subLine2 = GeometryEngine.getFactory().createILineString(
        listSubLine2);
    double totalFinal = subLine2.length();
    IDirectPosition prevPt = null;
    for (IDirectPosition pt : subLine.coord()) {
      if (prevPt == null) {
        prevPt = pt;
        if (!initialCoord.contains(pt)) {
          initialCoord.add(pt);
          finalCoord.add(subLine2.startPoint());
        }
        continue;
      }
      dist += pt.distance2D(prevPt);
      double ratio = dist / total;

      // get the point at the curvilinear coordinate corresponding to
      // ratio
      double curvi = totalFinal * ratio;
      IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(subLine2,
          curvi);
      initialCoord.add(pt);
      finalCoord.add(finalPt);
      prevPt = pt;
    }
  }

}
