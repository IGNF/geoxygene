/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class DualGraph extends Graph {

  public DualGraph(String name, boolean oriented,
      HashSet<ArcReseau> linearFeatures) {
    super(name, oriented, linearFeatures);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param linearFeatures
   */
  public DualGraph(String name, boolean oriented,
      Collection<? extends IGeneObjLin> linearFeatures) {
    super(name, oriented);
    // TODO Auto-generated constructor stub
  }
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  // Getters and setters //

  // Other public methods //

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
