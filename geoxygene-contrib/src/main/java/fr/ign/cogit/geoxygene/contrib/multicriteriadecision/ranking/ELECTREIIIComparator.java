/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.multicriteriadecision.ranking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ELECTREIIIComparator implements Comparator<ELECTREIIIAction> {

  private ELECTREIIIPreOrder preOrderZ1, preOrderZ2;

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

  /**
   * Implementation of the merge_sort algorithm using the preorders to sort the
   * actions.
   * @param list
   * @return
   */
  public List<ELECTREIIIAction> sort(List<ELECTREIIIAction> list) {
    // simple case
    if (list.size() <= 1)
      return new ArrayList<>(list);

    List<ELECTREIIIAction> left = new ArrayList<>();
    List<ELECTREIIIAction> right = new ArrayList<>();

    for (int i = 0; i < list.size(); i++) {
      if (i < list.size() / 2)
        left.add(list.get(i));
      else
        right.add(list.get(i));
    }

    // recursive call
    List<ELECTREIIIAction> sortedLeft = sort(left);
    List<ELECTREIIIAction> sortedRight = sort(right);

    return mergeSort(sortedLeft, sortedRight);
  }

  private List<ELECTREIIIAction> mergeSort(List<ELECTREIIIAction> left,
      List<ELECTREIIIAction> right) {
    List<ELECTREIIIAction> mergedList = new ArrayList<>();

    while (!left.isEmpty() && !right.isEmpty()) {
      if (compare(left.get(0), right.get(0)) > 0) {
        mergedList.add(left.get(0));
        left.remove(0);
      } else {
        mergedList.add(right.get(0));
        right.remove(0);
      }
    }

    // deal with remaining cases (one list is not empty)
    while (!left.isEmpty()) {
      mergedList.add(left.get(0));
      left.remove(0);
    }

    while (!right.isEmpty()) {
      mergedList.add(right.get(0));
      right.remove(0);
    }

    return mergedList;
  }
}
