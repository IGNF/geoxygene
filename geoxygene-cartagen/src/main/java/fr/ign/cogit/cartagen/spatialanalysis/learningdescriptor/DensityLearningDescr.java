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
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class DensityLearningDescr implements LearningDescriptor {

  private double searchRadius;
  private IFeatureCollection<IFeature> neighbours;

  public DensityLearningDescr(double searchRadius,
      IFeatureCollection<IFeature> neighbours) {
    super();
    this.searchRadius = searchRadius;
    this.neighbours = neighbours;
  }

  @Override
  public double getValue(IFeature feature) {
    IGeometry buffer = feature.getGeom().buffer(searchRadius);
    Collection<IFeature> inter = this.neighbours.select(buffer);
    double totalArea = 0.0;
    for (IFeature interB : inter) {
      totalArea += buffer.intersection(interB.getGeom()).area();
    }
    return totalArea / buffer.area();
  }

  @Override
  public String getName() {
    return "DensityOfNeighbours";
  }

}
