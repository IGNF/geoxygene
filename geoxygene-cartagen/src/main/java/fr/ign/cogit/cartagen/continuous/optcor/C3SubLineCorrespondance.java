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

public class C3SubLineCorrespondance implements SubLineCorrespondance {

  private List<Object> finalFeatures;
  private ILineString subLine;
  private List<ILineString> finalLines;

  public C3SubLineCorrespondance(ILineString subLine,
      List<ILineString> finalLines) {
    super();
    this.subLine = subLine;
    this.finalLines = finalLines;
    finalFeatures = new ArrayList<>();
    finalFeatures.add(finalLines.get(0).startPoint());
    for (ILineString finalSubLine : finalLines) {
      finalFeatures.add(finalSubLine);
      finalFeatures.add(finalSubLine.endPoint());
    }
  }

  @Override
  public List<Object> getMatchedFeaturesInitial() {
    List<Object> matchedFeatures = new ArrayList<Object>();
    matchedFeatures.add(subLine);
    return matchedFeatures;
  }

  @Override
  public List<Object> getMatchedFeaturesFinal() {
    return finalFeatures;
  }

  @Override
  public CorrespondanceType getType() {
    return CorrespondanceType.C3;
  }

  @Override
  public IDirectPositionList morphCorrespondance(double t) {
    IDirectPositionList initialPts = new DirectPositionList();
    IDirectPositionList finalPts = new DirectPositionList();

    matchVertices(initialPts, finalPts);

    // then, compute the intermediate position between each correspondant
    IDirectPositionList coord = new DirectPositionList();
    for (int i = 0; i < initialPts.size(); i++) {
      IDirectPosition pt1 = initialPts.get(i);
      IDirectPosition pt2 = finalPts.get(i);
      double newX = pt1.getX() + t * (pt2.getX() - pt1.getX());
      double newY = pt1.getY() + t * (pt2.getY() - pt1.getY());
      IDirectPosition newPt = new DirectPosition(newX, newY);
      coord.add(newPt);
    }

    return coord;
  }

  @Override
  public void matchVertices(IDirectPositionList initialCoord,
      IDirectPositionList finalCoord) {
    double dist = 0.0;
    // first merge the final lines
    ILineString merged = Operateurs.compileArcs(finalLines);

    // decides which is subLine1 (the one with the most vertices)
    boolean initial = true;
    ILineString subLine1 = this.subLine;
    ILineString subLine2 = merged;
    if (merged.numPoints() > subLine.numPoints()) {
      initial = false;
      subLine1 = merged;
      subLine2 = this.subLine;
    }

    double total = subLine1.length();
    double totalFinal = subLine2.length();
    IDirectPosition prevPt = null;
    for (IDirectPosition pt : subLine1.coord()) {
      if (prevPt == null) {
        prevPt = pt;
        if (initial) {
          if (!initialCoord.contains(pt)) {
            initialCoord.add(pt);
            finalCoord.add(subLine2.startPoint());
          }
        } else {
          if (!finalCoord.contains(pt)) {
            finalCoord.add(pt);
            initialCoord.add(subLine2.startPoint());
          }
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
      if (initial) {
        initialCoord.add(pt);
        finalCoord.add(finalPt);
      } else {
        initialCoord.add(finalPt);
        finalCoord.add(pt);
      }
      prevPt = pt;
    }
  }

  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("C3 matching");
    buff.append(System.getProperty("line.separator"));
    buff.append(subLine);
    buff.append(System.getProperty("line.separator"));
    buff.append("is matched to:");
    buff.append(System.getProperty("line.separator"));
    buff.append(finalLines);
    return buff.toString();
  }

  @Override
  public boolean containsSubLine(ILineString subLine) {
    if (this.finalLines.contains(subLine))
      return true;
    if (subLine.equals(this.subLine))
      return true;
    return false;
  }
}
