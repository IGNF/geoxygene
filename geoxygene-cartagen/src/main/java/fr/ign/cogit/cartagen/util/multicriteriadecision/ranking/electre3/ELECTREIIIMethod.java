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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class ELECTREIIIMethod {

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(ELECTREIIIMethod.class
      .getName());

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Set<ELECTREIIICriterion> criteria = new HashSet<ELECTREIIICriterion>();
  private List<ELECTREIIIAction> actions = new ArrayList<ELECTREIIIAction>();
  private double credibilityThreshold = 0.5;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ELECTREIIIMethod(Collection<ELECTREIIICriterion> criteria,
      List<ELECTREIIIAction> actions, double credibilityThreshold) {
    this.criteria.addAll(criteria);
    this.actions.addAll(actions);
    this.credibilityThreshold = credibilityThreshold;
  }

  // Getters and setters //
  public Set<ELECTREIIICriterion> getCriteria() {
    return this.criteria;
  }

  // Other public methods //
  /**
   * The ELECTREIII decision in the decreasing order of the outranking relation
   * (the action that outranks all others is the first in the resulting list).
   */
  public List<ELECTREIIIAction> decision() {
    // (see Figueira et al, 2005, p.146)
    // first compute pre-order Z1
    ELECTREIIIPreOrder preOrderZ1 = this.computeZ1_bis();
    // then compute pre-order Z2
    ELECTREIIIPreOrder preOrderZ2 = this.computeZ2_bis();
    // build the comparator
    ELECTREIIIComparator c = new ELECTREIIIComparator(preOrderZ1, preOrderZ2);
    // then sort the actions according to the comparator
    Collections.sort(this.actions, c);
    Collections.reverse(this.actions);
    return this.actions;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  private double getConcordanceIndex(ELECTREIIIAction a, ELECTREIIIAction b) {
    double weightSum = 0.0;
    double sum = 0.0;
    for (ELECTREIIICriterion criterion : this.criteria) {
      double valueA = criterion.value(a.getParameters());
      double valueB = criterion.value(b.getParameters());
      if (criterion.isInScoalition(valueA, valueB)) {
        sum += criterion.getWeight();
        weightSum += criterion.getWeight();
      }
      if (criterion.isInQcoalition(valueA, valueB)) {
        double phi = (valueA + criterion.getPreference() - valueB)
            / (criterion.getPreference() - criterion.getIndifference());
        sum += criterion.getWeight() * phi;
        weightSum += criterion.getWeight();
      }
    }
    return sum / weightSum;
  }

  /**
   * Compute the credibility index of the assertion "a outranks b". When there
   * is no discordant criterion, the credibility of the outranking relation is
   * equal to the comprehensive concordance index. When a discordant criterion
   * activates its veto power, the assertion is not credible at all, thus the
   * index is 0.0. For the remaining situations in which the comprehensive
   * concordance index is strictly lower than the discordance index on the
   * discordant criterion, the credibility index becomes lower than the
   * comprehensive concordance index, because of the opposition effect on this
   * criterion.
   * 
   * @param a
   * @param b
   * @return
   * @author GTouya
   */
  private double getCredibilityIndex(ELECTREIIIAction a, ELECTREIIIAction b) {
    double concord = this.getConcordanceIndex(a, b);

    // logger.debug("Action " + a + " outranks " + b + " (concordance = "
    // + concord);
    double credibility = concord;
    boolean isDiscordanceLesser = true;
    for (ELECTREIIICriterion c : this.criteria) {
      double discord = c.getDiscordanceIndex(c.value(a.getParameters()),
          c.value(b.getParameters()));
      // logger.debug("Action " + a + " outranks " + b
      // + " (discordance for criterion " + c + " = " + discord);
      if (discord > concord) {
        double factor = (1 - discord) / (1 - concord);
        credibility *= factor;
        isDiscordanceLesser = false;
      }
    }
    if (isDiscordanceLesser) {
      return concord;
    }
    return credibility;
  }

  public boolean isPreferredTo(ELECTREIIIAction a, ELECTREIIIAction b) {
    if (this.getCredibilityIndex(a, b) > this.credibilityThreshold) {
      return true;
    }
    return false;
  }

  /**
   * Compute a pre-order of the actions according to the credibility index. A
   * pre-order is an order where ex-aequo are possible (all ex-aequo actions are
   * put in same set). The Z1 pre-order is descending as set number i elements
   * are "preferred to" set number i+1 elements. At each iteration, the set is
   * filled with actions that are not preferred to any other.
   * 
   * @return
   * @author GTouya
   */
  @SuppressWarnings("unused")
  private ELECTREIIIPreOrder computeZ1() {
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> actionsLeft = new HashSet<ELECTREIIIAction>();
    actionsLeft.addAll(this.actions);
    while (!actionsLeft.isEmpty()) {
      HashSet<ELECTREIIIAction> bSetH = new HashSet<ELECTREIIIAction>();
      HashSet<ELECTREIIIAction> loopSet = new HashSet<ELECTREIIIAction>();
      loopSet.addAll(actionsLeft);
      for (ELECTREIIIAction action : loopSet) {
        // check if no other action is preferred to action
        HashSet<ELECTREIIIAction> loopSet2 = new HashSet<ELECTREIIIAction>();
        loopSet2.addAll(actionsLeft);
        loopSet2.remove(action);
        boolean add = true;
        for (ELECTREIIIAction otherAction : loopSet2) {
          if (this.isPreferredTo(otherAction, action)) {
            add = false;
            break;
          }
        }
        if (add) {
          bSetH.add(action);
        }
      }

      // update the actionsLeft
      actionsLeft.removeAll(bSetH);
      // add a pre-order set
      preOrder.add(bSetH);
    }
    return new ELECTREIIIPreOrder(preOrder);
  }

  /**
   * Compute a pre-order of the actions according to the credibility index. A
   * pre-order is an order where ex-aequo are possible (all ex-aequo actions are
   * put in same set). The Z2 pre-order is ascending as set number i elements
   * are "preferred to" set number i-1 elements. At each iteration, the set is
   * filled with actions that no other action is preferred to them.
   * 
   * @return
   * @author GTouya
   */
  @SuppressWarnings("unused")
  private ELECTREIIIPreOrder computeZ2() {
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> actionsLeft = new HashSet<ELECTREIIIAction>();
    actionsLeft.addAll(this.actions);
    while (!actionsLeft.isEmpty()) {
      HashSet<ELECTREIIIAction> bSetH = new HashSet<ELECTREIIIAction>();
      HashSet<ELECTREIIIAction> loopSet = new HashSet<ELECTREIIIAction>();
      loopSet.addAll(actionsLeft);
      for (ELECTREIIIAction action : loopSet) {
        // check if no other action is preferred to action
        HashSet<ELECTREIIIAction> loopSet2 = new HashSet<ELECTREIIIAction>();
        loopSet2.addAll(actionsLeft);
        loopSet2.remove(action);
        boolean add = true;
        for (ELECTREIIIAction otherAction : loopSet2) {
          if (this.isPreferredTo(action, otherAction)) {
            add = false;
            break;
          }
        }
        if (add) {
          bSetH.add(action);
        }
      }

      // update the actionsLeft
      actionsLeft.removeAll(bSetH);
      // add a pre-order set
      preOrder.add(bSetH);
    }
    return new ELECTREIIIPreOrder(preOrder);
  }

  private ELECTREIIIPreOrder computeZ1_bis() {
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> a = new HashSet<ELECTREIIIAction>();
    a.addAll(this.actions);

    this.classified(a, preOrder, true);

    return new ELECTREIIIPreOrder(preOrder);
  }

  private ELECTREIIIPreOrder computeZ2_bis() {
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> a = new HashSet<ELECTREIIIAction>();
    a.addAll(this.actions);

    this.classified(a, preOrder, false);

    Collections.reverse(preOrder);

    return new ELECTREIIIPreOrder(preOrder);
  }

  private void classified(HashSet<ELECTREIIIAction> d,
      ArrayList<HashSet<ELECTREIIIAction>> preOrder, boolean descending) {

    int qMax = Integer.MIN_VALUE;
    int qMin = Integer.MAX_VALUE;
    HashSet<ELECTREIIIAction> dN = new HashSet<ELECTREIIIAction>();

    for (ELECTREIIIAction actionA : d) {
      int q = 0;
      for (ELECTREIIIAction actionB : d) {
        if (actionB != actionA) {
          q += this.isPreferredTo(actionA, actionB) ? 1 : -1;
        }
      }
      // logger.debug("Action " + actionA + " coefficient : " + q + " qOpt "
      // + qMin);
      if (descending) {
        if (q > qMax) {
          qMax = q;
          dN.clear();
        }
        if (q >= qMax) {
          dN.add(actionA);
        }
      } else {
        if (q < qMin) {
          qMin = q;
          dN.clear();
        }
        if (q <= qMin) {
          dN.add(actionA);
        }
      }
    }

    if (dN.size() == 1 || dN.size() == d.size()) {
      preOrder.add(0, dN);
    } else {
      this.classified(dN, preOrder, descending);
    }

    if (dN.size() != d.size()) {
      HashSet<ELECTREIIIAction> dRemain = new HashSet<ELECTREIIIAction>();
      dRemain.addAll(d);
      dRemain.removeAll(dN);
      ArrayList<HashSet<ELECTREIIIAction>> preOrderBis = new ArrayList<HashSet<ELECTREIIIAction>>();
      this.classified(dRemain, preOrderBis, descending);
      preOrder.addAll(preOrderBis);
    }
  }
}
