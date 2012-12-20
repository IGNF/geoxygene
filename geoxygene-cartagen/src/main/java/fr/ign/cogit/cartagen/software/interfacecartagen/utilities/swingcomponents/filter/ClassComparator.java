/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for Class<?> objects that follows the lexicographic order.
 * @author GTouya
 * 
 */
public class ClassComparator implements Comparator<Class<?>>, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public int compare(Class<?> o1, Class<?> o2) {
    return o1.getName().compareTo(o2.getName());
  }

}
