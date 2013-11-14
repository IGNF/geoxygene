package fr.ign.cogit.geoxygene.util.math;

import java.util.Set;

public class Combination {

  private Set<? extends Object> subSet;
  private CombinationSet combinationSet;

  public Combination(Set<? extends Object> subSet, CombinationSet combinationSet) {
    super();
    this.subSet = subSet;
    this.combinationSet = combinationSet;
  }

  public CombinationSet getCombinationSet() {
    return combinationSet;
  }

  public void setCombinationSet(CombinationSet combinationSet) {
    this.combinationSet = combinationSet;
  }

  public Set<? extends Object> getSubSet() {
    return subSet;
  }

  public void setSubSet(Set<? extends Object> subSet) {
    this.subSet = subSet;
  }

  public int getSize() {
    return subSet.size();
  }

  public boolean contains(Object obj) {
    return subSet.contains(obj);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((subSet == null) ? 0 : subSet.hashCode());
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
    Combination other = (Combination) obj;
    if (subSet == null) {
      if (other.subSet != null)
        return false;
    } else if (!subSet.equals(other.subSet))
      return false;
    return true;
  }

  @Override
  public String toString() {
    if (subSet.size() == 0)
      return "{}";
    StringBuffer buff = new StringBuffer("{");
    for (Object obj : subSet)
      buff.append(obj.toString() + ",");
    buff.deleteCharAt(buff.length() - 1);
    buff.append("}");
    return buff.toString();
  }

}
