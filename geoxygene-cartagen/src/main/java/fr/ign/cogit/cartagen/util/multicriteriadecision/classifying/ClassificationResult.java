package fr.ign.cogit.cartagen.util.multicriteriadecision.classifying;

import java.util.Set;

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;

public class ClassificationResult {
  private String category;
  private double robustness;
  private Set<Criterion> criteria;

  public ClassificationResult(String category, double robustness,
      Set<Criterion> criteria) {
    super();
    this.category = category;
    this.robustness = robustness;
    this.criteria = criteria;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public double getRobustness() {
    return robustness;
  }

  public void setRobustness(double robustness) {
    this.robustness = robustness;
  }

  public Set<Criterion> getCriteria() {
    return criteria;
  }

  public void setCriteria(Set<Criterion> criteria) {
    this.criteria = criteria;
  }

  @Override
  public String toString() {
    return category;
  }

}
