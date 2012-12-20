/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ELECTREIIIMethod {
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private Set<ELECTREIIICriterion> criteria = new HashSet<ELECTREIIICriterion>();
  private List<ELECTREIIIAction> actions = new ArrayList<ELECTREIIIAction>();
  private double credibilityThreshold = 0.5;
  
  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Public methods               //
  ////////////////////////////////////////////

  // Public constructors //
  public ELECTREIIIMethod(Collection<ELECTREIIICriterion> criteria,
      List<ELECTREIIIAction> actions, double credibilityThreshold){
    this.criteria.addAll(criteria);
    this.actions.addAll(actions);
    this.credibilityThreshold = credibilityThreshold;
  }

  // Getters and setters //
  public Set<ELECTREIIICriterion> getCriteria() {
    return criteria;
  }

  // Other public methods //
  /**
   * The ELECTREIII decision in the decreasing order of the outranking relation (the
   * action that outranks all others is the first in the resulting list).
   */
  public List<ELECTREIIIAction> decision(){
    // (see Figueira et al, 2005, p.146)
    // first compute pre-order Z1
    ELECTREIIIPreOrder preOrderZ1 = computeZ1();
    // then compute pre-order Z2
    ELECTREIIIPreOrder preOrderZ2 = computeZ2();
    // build the comparator
    ELECTREIIIComparator c = new ELECTREIIIComparator(preOrderZ1, preOrderZ2);
    // then sort the actions according to the comparator
    Collections.sort(actions, c);
    Collections.reverse(actions);
    return actions;
  }

  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////
  private double getConcordanceIndex(ELECTREIIIAction a, ELECTREIIIAction b){
    double weightSum = 0.0;
    double phiSum = 0.0;
    for(ELECTREIIICriterion criterion:criteria){
      double valueA = criterion.value(a.getParameters());
      double valueB = criterion.value(b.getParameters());
      if(criterion.isInScoalition(valueA, valueB)) weightSum += criterion.getWeight();
      if(criterion.isInQcoalition(valueA, valueB)){
        double phi = (valueA + criterion.getPreference() - valueB) / 
          (criterion.getPreference()-criterion.getIndifference());
        phiSum += criterion.getWeight() * phi;
      }
    }
    
    return weightSum + phiSum;
  }


  /**
   * Compute the credibility index of the assertion "a outranks b".
   * When there is no discordant criterion, the credibility of the outranking
   * relation is equal to the comprehensive concordance index.
   * When a discordant criterion activates its veto power, the assertion is not
   * credible at all, thus the index is 0.0.
   * For the remaining situations in which the comprehensive concordance index 
   * is strictly lower than the discordance index on the discordant criterion, 
   * the credibility index becomes lower than the comprehensive concordance index, 
   * because of the opposition effect on this criterion.
   *  
   * @param a
   * @param b
   * @return
   * @author GTouya
   */
  private double getCredibilityIndex(ELECTREIIIAction a, ELECTREIIIAction b){
    double concord = this.getConcordanceIndex(a, b);
    double credibility = concord;
    for(ELECTREIIICriterion c:criteria){
      double discord = c.getDiscordanceIndex(c.value(a.getParameters()), 
          c.value(b.getParameters()));
      if(discord > concord){
        double factor = (1-discord) / (1 - concord);
        credibility *= factor;
      }
    }
    
    return credibility;
  }
  
  
  public boolean isPreferredTo(ELECTREIIIAction a, ELECTREIIIAction b){
    if(getCredibilityIndex(a, b) > this.credibilityThreshold) return true;
    return false;
  }
  
  /**
   * Compute a pre-order of the actions according to the credibility index. 
   * A pre-order is an order where ex-aequo are possible (all ex-aequo actions
   * are put in same set). The Z1 pre-order is descending as set number i elements
   * are "preferred to" set number i+1 elements. 
   * At each iteration, the set is filled with actions that are not preferred to any other.
   *  
   * @return
   * @author GTouya
   */
  private ELECTREIIIPreOrder computeZ1(){
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> actionsLeft = new HashSet<ELECTREIIIAction>();
    actionsLeft.addAll(actions);
    while(!actionsLeft.isEmpty()){
      HashSet<ELECTREIIIAction> bSetH = new HashSet<ELECTREIIIAction>();
      HashSet<ELECTREIIIAction> loopSet = new HashSet<ELECTREIIIAction>();
      loopSet.addAll(actionsLeft);
      for(ELECTREIIIAction action:loopSet){
        // check if no other action is preferred to action
        HashSet<ELECTREIIIAction> loopSet2 = new HashSet<ELECTREIIIAction>();
        loopSet2.addAll(actionsLeft);
        loopSet2.remove(action);
        boolean add = true;
        for(ELECTREIIIAction otherAction:loopSet){
          if(this.isPreferredTo(otherAction, action)){
            add = false;
            break;
          }
        }
        if(add) bSetH.add(action);
      }
      
      // update the actionsLeft
      actionsLeft.removeAll(bSetH);
      // add a pre-order set
      preOrder.add(bSetH);
    }
    return new ELECTREIIIPreOrder(preOrder);
  }
  
  /**
   * Compute a pre-order of the actions according to the credibility index. 
   * A pre-order is an order where ex-aequo are possible (all ex-aequo actions
   * are put in same set). The Z2 pre-order is ascending as set number i elements
   * are "preferred to" set number i-1 elements. 
   * At each iteration, the set is filled with actions that no other action is preferred to them.
   *  
   * @return
   * @author GTouya
   */
  private ELECTREIIIPreOrder computeZ2(){
    ArrayList<HashSet<ELECTREIIIAction>> preOrder = new ArrayList<HashSet<ELECTREIIIAction>>();
    HashSet<ELECTREIIIAction> actionsLeft = new HashSet<ELECTREIIIAction>();
    actionsLeft.addAll(actions);
    while(!actionsLeft.isEmpty()){
      HashSet<ELECTREIIIAction> bSetH = new HashSet<ELECTREIIIAction>();
      HashSet<ELECTREIIIAction> loopSet = new HashSet<ELECTREIIIAction>();
      loopSet.addAll(actionsLeft);
      for(ELECTREIIIAction action:loopSet){
        // check if no other action is preferred to action
        HashSet<ELECTREIIIAction> loopSet2 = new HashSet<ELECTREIIIAction>();
        loopSet2.addAll(actionsLeft);
        loopSet2.remove(action);
        boolean add = true;
        for(ELECTREIIIAction otherAction:loopSet){
          if(this.isPreferredTo(action, otherAction)){
            add = false;
            break;
          }
        }
        if(add) bSetH.add(action);
      }
      
      // update the actionsLeft
      actionsLeft.removeAll(bSetH);
      // add a pre-order set
      preOrder.add(bSetH);
    }
    return new ELECTREIIIPreOrder(preOrder);
  }
}

