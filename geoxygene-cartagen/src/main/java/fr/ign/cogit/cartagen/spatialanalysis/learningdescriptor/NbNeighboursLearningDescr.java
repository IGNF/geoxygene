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

public class NbNeighboursLearningDescr implements LearningDescriptor {

  private double searchRadius;
  private IFeatureCollection<IFeature> neighbours;

  public NbNeighboursLearningDescr(double searchRadius,
      IFeatureCollection<IFeature> neighbours) {
    super();
    this.searchRadius = searchRadius;
    this.neighbours = neighbours;
  }

  @Override
  public double getValue(IFeature feature) {
    Collection<IFeature> inter = this.neighbours
        .select(feature.getGeom().buffer(searchRadius));
    return inter.size();
  }

  @Override
  public String getName() {
    return "NbOfNeighbours";
  }

}
