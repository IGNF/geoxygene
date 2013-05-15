/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3;

import java.util.Comparator;

public class ELECTREIIIComparator implements Comparator<ELECTREIIIAction> {

  private ELECTREIIIPreOrder preOrderZ1, preOrderZ2;

  @Override
  public int compare(ELECTREIIIAction arg0, ELECTREIIIAction arg1) {
    if (preOrderZ1.equality(arg0, arg1) && preOrderZ2.equality(arg0, arg1))
      return 0;
    if (preOrderZ1.outranksOrEquality(arg0, arg1)
        && preOrderZ2.outranksOrEquality(arg0, arg1))
      return 1;
    // return
    // Math.round((preOrderZ1.difference(arg0,arg1)+preOrderZ2.difference(arg0,arg1))/2);
    if (preOrderZ1.outranksOrEquality(arg1, arg0)
        && preOrderZ2.outranksOrEquality(arg1, arg0))
      return -1;
    // return Math.round((preOrderZ1.difference(arg1, arg0) +
    // preOrderZ2.difference(arg1, arg0)) / 2);
    return 0;
  }

  public ELECTREIIIComparator(ELECTREIIIPreOrder preOrderZ1,
      ELECTREIIIPreOrder preOrderZ2) {
    this.preOrderZ1 = preOrderZ1;
    this.preOrderZ2 = preOrderZ2;
  }
}
