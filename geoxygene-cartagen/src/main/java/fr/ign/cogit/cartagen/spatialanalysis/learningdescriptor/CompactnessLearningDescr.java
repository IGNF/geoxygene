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

import fr.ign.cogit.cartagen.spatialanalysis.measures.Compactness;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class CompactnessLearningDescr implements LearningDescriptor {

  public enum CompactnessMethod {
    MILLER, SCHUMM, EHRENBERG, MCEACHRAN, SALLE_LEE, BOYCE_CLARK
  };

  private CompactnessMethod method = CompactnessMethod.MILLER;

  public CompactnessLearningDescr(CompactnessMethod method) {
    super();
    this.method = method;
  }

  @Override
  public double getValue(IFeature feature) {
    Compactness compactness = new Compactness((IPolygon) feature.getGeom());
    if (method.equals(CompactnessMethod.MILLER))
      return compactness.getMillerIndex();
    if (method.equals(CompactnessMethod.MCEACHRAN))
      return compactness.getMcEachrenIndex1();
    if (method.equals(CompactnessMethod.SCHUMM))
      return compactness.getSchummIndex();
    if (method.equals(CompactnessMethod.EHRENBERG))
      return compactness.getEhrenbergIndex();
    if (method.equals(CompactnessMethod.SALLE_LEE))
      return compactness.compaciteSalleLee();
    if (method.equals(CompactnessMethod.BOYCE_CLARK))
      return compactness.compaciteBoyceClark(20);
    return 0;
  }

  @Override
  public String getName() {
    return "Compactness";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompactnessLearningDescr other = (CompactnessLearningDescr) obj;
    if (method != other.method)
      return false;
    return true;
  }

}
