/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.selection;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public class SelectionUtil {

  /**
   * Shortcut to get all the selected objects by code.
   * @return
   */
  public static IFeatureCollection<IFeature> getSelectedObjects() {
    return CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects;
  }

  /**
   * Shortcut to get all the selected objects by code.
   * @return
   */
  public static Collection<IGeneObj> getWindowObjects(String popName) {
    IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(popName);
    return pop.select(CartagenApplication.getInstance().getFrame()
        .getVisuPanel().getDisplayEnvelope());
  }

}
