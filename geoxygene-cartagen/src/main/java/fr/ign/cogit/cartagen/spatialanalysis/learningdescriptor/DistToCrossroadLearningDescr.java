/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.learningdescriptor;

import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class DistToCrossroadLearningDescr implements LearningDescriptor {

  private IFeatureCollection<IFeature> crossroads;

  public DistToCrossroadLearningDescr(IFeatureCollection<IFeature> crossroads) {
    super();
    this.crossroads = crossroads;
  }

  @Override
  public double getValue(IFeature feature) {
    IFeature nearest = SpatialQuery.selectNearestFeature(feature.getGeom(),
        crossroads, 150.0);
    if (nearest == null)
      return 150.0;
    GeometryProximity proxy = new GeometryProximity(nearest.getGeom(),
        feature.getGeom());
    return proxy.getDistance();
  }

  @Override
  public String getName() {
    return "DistanceToCrossroad";
  }

}
