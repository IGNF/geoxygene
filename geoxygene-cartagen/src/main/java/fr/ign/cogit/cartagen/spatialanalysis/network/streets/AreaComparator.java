/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.streets;

import java.util.Comparator;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;

public class AreaComparator implements Comparator<IUrbanBlock> {

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  // Getters and setters //

  // Other public methods //
  @Override
  public int compare(IUrbanBlock arg0, IUrbanBlock arg1) {
    if (arg0.getGeom().area() > arg1.getGeom().area()) {
      return 1;
    }
    if (arg0.getGeom().area() < arg1.getGeom().area()) {
      return -1;
    }
    return 0;
  }

}
