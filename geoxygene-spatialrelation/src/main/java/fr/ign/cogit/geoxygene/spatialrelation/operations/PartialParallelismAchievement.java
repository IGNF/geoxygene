/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.operations;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ConvergingPoint;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ParallelSection;
import fr.ign.cogit.geoxygene.spatialrelation.relation.PartialParallelism2Lines;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class PartialParallelismAchievement implements
    AchievementMeasureOperation {

  private IFeature feature1, feature2;
  /**
   * List of converging points on feature1 sorted on the feature1 geometry
   * order.
   */
  private List<IDirectPosition> convergingPts;
  /**
   * List of diverging points on feature1 sorted on the feature1 geometry order.
   */
  private List<IDirectPosition> divergingPts;

  private PartialParallelism2Lines relation;

  public PartialParallelismAchievement(IFeature feature1, IFeature feature2,
      PartialParallelism2Lines relation) {
    super();
    this.feature1 = feature1;
    this.feature2 = feature2;
    this.relation = relation;
    this.convergingPts = new ArrayList<IDirectPosition>();
    this.divergingPts = new ArrayList<IDirectPosition>();
  }

  @Override
  public void compute() {
    boolean parallel = false;
    ILineString line2 = null;
    List<IDirectPosition> pts = new ArrayList<IDirectPosition>();
    if (feature2.getGeom() instanceof ILineString)
      line2 = (ILineString) feature2.getGeom();
    else if (feature2.getGeom() instanceof IPolygon)
      line2 = ((IPolygon) feature2.getGeom()).exteriorLineString();
    for (IDirectPosition pt : feature1.getGeom().coord()) {
      IDirectPosition closest = JtsAlgorithms.getClosestPoint(pt, line2);
      double dist = closest.distance2D(pt);
      // if it is a parallel section, check if it's a diverging point
      if (parallel) {
        if (!relation.getConditionOfAchievement().validate(new Double(dist))) {
          divergingPts.add(pt);
          pts.add(pt);
          parallel = false;
        }
      } else {
        // else, check if this point is a converging point
        if (relation.getConditionOfAchievement().validate(new Double(dist))) {
          convergingPts.add(pt);
          pts.add(pt);
          parallel = true;
        }
      }
    }
    // if the lines ended parallel, add a diverging point at the end
    IDirectPosition finalPt = feature1.getGeom().coord()
        .get(feature1.getGeom().coord().size() - 1);
    divergingPts.add(finalPt);
    pts.add(finalPt);

    // fill the relation fields if it is achieved
    if (convergingPts.size() + divergingPts.size() > 0) {
      // the relation is achieved
      // fill the properties of the relation, i.e. converging points and
      // parallel sections
      ILineString line1 = null;
      if (feature1.getGeom() instanceof ILineString)
        line1 = (ILineString) feature1.getGeom();
      else if (feature1.getGeom() instanceof IPolygon)
        line1 = ((IPolygon) feature1.getGeom()).exteriorLineString();
      IDirectPosition lastConverging = null;
      for (IDirectPosition pt : pts) {
        boolean converging = true;
        if (divergingPts.contains(pt))
          converging = false;
        ConvergingPoint convPt = new ConvergingPoint(converging, pt,
            getRelation());
        this.getRelation().getConvergencePts().add(convPt);
        // create parallel sections
        if (!converging) {
          ILineString member1 = CommonAlgorithmsFromCartAGen.getSubLine(line1,
              lastConverging, pt);
          IDirectPosition closest1 = JtsAlgorithms.getClosestPoint(
              lastConverging, line2);
          IDirectPosition closest2 = JtsAlgorithms.getClosestPoint(pt, line2);
          ILineString member2 = CommonAlgorithmsFromCartAGen.getSubLine(line2,
              closest1, closest2);
          ParallelSection section = new ParallelSection(member1, member2,
              getRelation());
          this.getRelation().getParallelSections().add(section);
        } else {
          lastConverging = pt;
        }
      }
    }

  }

  @Override
  public boolean measureAchievement() {
    if (convergingPts.size() != 0 || divergingPts.size() != 0)
      return true;
    return false;
  }

  public IFeature getFeature1() {
    return feature1;
  }

  public void setFeature1(IFeature feature1) {
    this.feature1 = feature1;
  }

  public IFeature getFeature2() {
    return feature2;
  }

  public void setFeature2(IFeature feature2) {
    this.feature2 = feature2;
  }

  public List<IDirectPosition> getConvergingPts() {
    return convergingPts;
  }

  public void setConvergingPts(List<IDirectPosition> convergingPts) {
    this.convergingPts = convergingPts;
  }

  public List<IDirectPosition> getDivergingPts() {
    return divergingPts;
  }

  public void setDivergingPts(List<IDirectPosition> divergingPts) {
    this.divergingPts = divergingPts;
  }

  public PartialParallelism2Lines getRelation() {
    return relation;
  }

  public void setRelation(PartialParallelism2Lines relation) {
    this.relation = relation;
  }

}
