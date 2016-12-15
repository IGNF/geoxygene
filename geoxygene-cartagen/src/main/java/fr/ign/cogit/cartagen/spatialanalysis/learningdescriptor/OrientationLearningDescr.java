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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;

public class OrientationLearningDescr implements LearningDescriptor {

  @Override
  public double getValue(IFeature feature) {
    return new OrientationMeasure(feature.getGeom()).getGeneralOrientation();
  }

  @Override
  public String getName() {
    return "Orientation";
  }

}
