/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class StrokesNetwork {

  private Set<Stroke> strokes;
  private Set<ArcReseau> features;
  private Set<ArcReseau> groupedFeatures;// set used to build the strokes
  // (contains features already grouped in a stroke)
  /**
   * True if the attributes are looked for with declared getters on the class,
   * and false if the {@link FeatureType} instance is used.
   */
  private boolean attributesDeclared = true;

  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public Set<Stroke> getStrokes() {
    return this.strokes;
  }

  public void setStrokes(Set<Stroke> strokes) {
    this.strokes = strokes;
  }

  public Set<ArcReseau> getFeatures() {
    return this.features;
  }

  public void setFeatures(Set<ArcReseau> features) {
    this.features = features;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Set<ArcReseau> getGroupedFeatures() {
    return this.groupedFeatures;
  }

  public void setGroupedFeatures(Set<ArcReseau> groupedFeatures) {
    this.groupedFeatures = groupedFeatures;
  }

  public StrokesNetwork() {
    super();
    this.features = new HashSet<ArcReseau>();
    this.groupedFeatures = new HashSet<ArcReseau>();
    this.id = StrokesNetwork.counter.getAndIncrement();
    this.strokes = new HashSet<Stroke>();
  }

  public StrokesNetwork(Collection<ArcReseau> features) {
    super();
    this.features = new HashSet<ArcReseau>();
    this.features.addAll(features);
    this.groupedFeatures = new HashSet<ArcReseau>();
    this.id = StrokesNetwork.counter.getAndIncrement();
    this.strokes = new HashSet<Stroke>();
  }

  /**
   * <p>
   * Build the 'Strokes' of a network. Strokes are groups of network segments
   * that follow the perceptual grouping principle of Good Continuity (Gestalt).
   * 
   * @param attributeNames : the set of attribute names used for attribute
   *          continuity.
   * @param deviatAngle : the limit incident angle for continuity
   * @param deviatSum : the limit sum incident angle differences between
   *          consecutive pts.
   * @param noStop : true if the stroke always has to continue when there is a
   *          unique follower.
   * 
   * @return void : build the strokes in this network
   * 
   */
  public void buildStrokes(Set<String> attributeNames, double deviatAngle,
      double deviatSum, boolean noStop) {

    // loop on the network features
    for (ArcReseau obj : this.features) {
      // test if the feature has already been treated
      if (this.groupedFeatures.contains(obj)) {
        continue;
      }

      // build a new stroke object
      Stroke stroke = new Stroke(this, obj);

      // build the stroke on the initial side
      stroke.buildOneSide(true, attributeNames, deviatAngle, deviatSum, noStop);

      // build the stroke on the final side
      stroke
          .buildOneSide(false, attributeNames, deviatAngle, deviatSum, noStop);

      // build the stroke geometry
      stroke.buildGeomStroke();

      // add the stroke to the strokes set
      this.strokes.add(stroke);
    }
  }

  public void setAttributesDeclared(boolean attributesDeclared) {
    this.attributesDeclared = attributesDeclared;
  }

  public boolean isAttributesDeclared() {
    return attributesDeclared;
  }
}
