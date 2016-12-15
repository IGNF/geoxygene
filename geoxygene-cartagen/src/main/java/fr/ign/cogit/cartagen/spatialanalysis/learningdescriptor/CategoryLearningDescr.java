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

public class CategoryLearningDescr implements LearningDescriptor {

  @Override
  public double getValue(IFeature feature) {
    return (int) feature.getAttribute("category_value");
  }

  @Override
  public String getName() {
    return "CategoryAttribute";
  }

}
