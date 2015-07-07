/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.AttractionPointDetection.AttractionPointNature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;

/**
 * An attraction point in the sense of (Touya, 2010,
 * "A Road Network Selection Process Based on Data Enrichment and Structure Detection"
 * ), i.e. points attached to a road node that attract road traffic, e.g.
 * residential places, airports, train stations, malls, etc.
 * @author GTouya
 * 
 */
public class AttractionPoint extends AbstractFeature {

  private IDirectPosition position;
  private Set<IFeature> attractiveFeatures;
  private IRoadNode roadNode;
  private AttractionPointNature nature;

  public AttractionPoint(IDirectPosition position,
      Set<IFeature> attractiveFeatures, IRoadNode roadNode,
      AttractionPointNature nature) {
    super();
    this.setGeom(roadNode.getGeom());
    this.position = position;
    this.attractiveFeatures = attractiveFeatures;
    this.roadNode = roadNode;
    this.nature = nature;
  }

  public AttractionPoint(IDirectPosition position, IRoadNode roadNode) {
    super();
    this.setGeom(roadNode.getGeom());
    this.position = position;
    this.attractiveFeatures = new HashSet<>();
    this.roadNode = roadNode;
  }

  public IDirectPosition getPosition() {
    return position;
  }

  public void setPosition(IDirectPosition position) {
    this.position = position;
  }

  public Set<IFeature> getAttractiveFeatures() {
    return attractiveFeatures;
  }

  public void setAttractiveFeature(Set<IFeature> attractiveFeatures) {
    this.attractiveFeatures = attractiveFeatures;
  }

  public IRoadNode getRoadNode() {
    return roadNode;
  }

  public void setRoadNode(IRoadNode roadNode) {
    this.roadNode = roadNode;
  }

  public AttractionPointNature getNature() {
    return nature;
  }

  public void setNature(AttractionPointNature nature) {
    this.nature = nature;
  }

  public void addAttractiveFeature(IFeature feature,
      AttractionPointNature nature) {
    this.attractiveFeatures.add(feature);
    if (this.nature == null)
      this.nature = nature;
    else if (!this.nature.equals(nature))
      this.nature = AttractionPointNature.MULTIPLE;
  }

  /**
   * Compute the weight of the attraction point (shortest paths from or to high
   * weight points are also weighted more). The weight is computed using the
   * number of attractive features related to the point, and the importance of
   * these attractive feature related to their impact on road traffic.
   * @return
   */
  public int getWeight() {
    // TODO
    return 1;
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return new AttractionPoint(position, attractiveFeatures, roadNode, nature);
  }
}
