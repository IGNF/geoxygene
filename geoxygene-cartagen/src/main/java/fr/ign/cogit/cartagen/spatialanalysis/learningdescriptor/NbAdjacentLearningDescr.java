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

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

public class NbAdjacentLearningDescr implements LearningDescriptor {

  private IFeatureCollection<IFeature> neighbours;

  public NbAdjacentLearningDescr(IFeatureCollection<IFeature> neighbours) {
    super();
    this.neighbours = neighbours;
  }

  @Override
  public double getValue(IFeature feature) {
    Collection<IFeature> adjacent = neighbours.select(feature.getGeom());
    if (adjacent != null) {
      adjacent.remove(feature);
      return adjacent.size();
    }
    return 0;
  }

  @Override
  public String getName() {
    return "NbOfAdjacentFeatures";
  }

  public IFeatureCollection<IFeature> getNeighbours() {
    return neighbours;
  }

  public void setNeighbours(IFeatureCollection<IFeature> neighbours) {
    this.neighbours = neighbours;
  }

}
