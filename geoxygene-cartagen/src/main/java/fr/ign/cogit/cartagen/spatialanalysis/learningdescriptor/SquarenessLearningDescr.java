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

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import fr.ign.cogit.cartagen.spatialanalysis.urban.Squareness;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class SquarenessLearningDescr implements LearningDescriptor {

  @Override
  public double getValue(IFeature feature) {
    Squareness squareness;
    try {
      squareness = new Squareness(feature.getGeom(), 15.0, 0.5);
      List<Double> deviations = squareness.getDeviations();
      DescriptiveStatistics stats = new DescriptiveStatistics();
      for (Double dev : deviations)
        stats.addValue(dev);
      return stats.getMean();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public String getName() {
    return "Squareness";
  }

}
