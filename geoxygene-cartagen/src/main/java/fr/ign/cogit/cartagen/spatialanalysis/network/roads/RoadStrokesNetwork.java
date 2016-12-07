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

import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class RoadStrokesNetwork extends StrokesNetwork {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public RoadStrokesNetwork(Set<ArcReseau> features) {
    super(features);
  }

  public RoadStrokesNetwork(IFeatureCollection<TronconDeRoute> features) {
    super();
    this.getFeatures().addAll(features);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public void buildStrokes(Set<String> attributeNames, double deviatAngle,
      double deviatSum, boolean noStop) {

    // loop on the network features
    for (ArcReseau obj : this.getFeatures()) {
      // test if the feature has already been treated
      if (this.getGroupedFeatures().contains(obj)) {
        continue;
      }

      // build a new stroke object
      RoadStroke stroke = new RoadStroke(this, obj);

      // build the stroke on the initial side
      stroke.buildOneSide(true, attributeNames, deviatAngle, deviatSum, noStop);

      // build the stroke on the final side
      stroke
          .buildOneSide(false, attributeNames, deviatAngle, deviatSum, noStop);

      // build the stroke geometry
      stroke.buildGeomStroke();

      // add the stroke to the strokes set
      this.getStrokes().add(stroke);

    }

  }

  /**
   * Build strokes that go through roundabouts instead of being stopped by the
   * continuity gap. At roundabouts, the continuity is computed with the
   * exterior roads of the roundabout.
   * 
   * @param attributeNames
   * @param deviatAngle
   * @param deviatSum
   * @param noStop
   * @author GTouya
   */
  public void buildStrokesThroughRoundabouts(HashSet<String> attributeNames,
      double deviatAngle, double deviatSum, boolean noStop,
      IFeatureCollection<RondPoint> roundabouts) {

    // loop on the network features
    for (ArcReseau obj : this.getFeatures()) {
      // test if the feature has already been treated
      if (this.getGroupedFeatures().contains(obj)) {
        continue;
      }

      // test if the road belongs to a roundabout
      for (RondPoint r : roundabouts) {
        if (r.getRoutesInternes().contains(obj)) {
          continue;
        }
      }

      // build a new stroke object
      RoadStroke stroke = new RoadStroke(this, obj);

      // build the stroke on the initial side
      stroke.buildOneSideRoundabout(true, attributeNames, deviatAngle,
          deviatSum, noStop, roundabouts);

      // build the stroke on the final side
      stroke.buildOneSideRoundabout(false, attributeNames, deviatAngle,
          deviatSum, noStop, roundabouts);

      // build the stroke geometry
      stroke.buildGeomStroke();

      // add the stroke to the strokes set
      this.getStrokes().add(stroke);
    }

  }

  public IFeatureCollection<IRoadStroke> getStrokesFeat() {
    IFeatureCollection<IRoadStroke> featColn = new FT_FeatureCollection<IRoadStroke>();
    for (Stroke str : this.getStrokes()) {
      IRoadStroke geneObj = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB().getGeneObjImpl().getCreationFactory()
          .createRoadStroke(str.getGeomStroke(), (RoadStroke) str);
      featColn.add(geneObj);
    }
    return featColn;
  }
  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
