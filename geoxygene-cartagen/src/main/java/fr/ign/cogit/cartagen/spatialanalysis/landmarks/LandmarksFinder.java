/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.landmarks;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.spatialanalysis.learningdescriptor.LearningDescriptor;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import smile.classification.DecisionTree;
import smile.classification.DecisionTree.SplitRule;

/**
 * Decides for a building if it is a landmark or not. Based on a decision tree
 * learning technique, as proposed by (Elias 2003).
 * @author GTouya
 *
 */
public class LandmarksFinder {

  private Set<LearningDescriptor> descriptors;

  private DecisionTree tree;
  private int maxNodes = 10;
  private double searchRadius = 50.0;

  public LandmarksFinder(int maxNodes) {
    this.descriptors = new HashSet<>();
    this.maxNodes = maxNodes;
  }

  public void addDescriptor(LearningDescriptor descriptor) {
    this.descriptors.add(descriptor);
  }

  public void train(LandmarksFinderTrainer trainer) {
    tree = new DecisionTree(trainer.getExamples(), trainer.getResponses(),
        maxNodes, SplitRule.ENTROPY);
  }

  public boolean predictLandmark(IFeature feature) {
    double[] x = this.describeFeature(feature);
    int result = tree.predict(x);
    if (result == 0)
      return false;
    else
      return true;
  }

  public double[] describeFeature(IFeature feature) {
    double[] values = new double[descriptors.size()];
    int i = 0;
    for (LearningDescriptor descriptor : descriptors) {
      values[i] = descriptor.getValue(feature);
      i++;
    }
    return values;
  }

  public double getSearchRadius() {
    return searchRadius;
  }

  public void setSearchRadius(double searchRadius) {
    this.searchRadius = searchRadius;
  }

  public Set<LearningDescriptor> getDescriptors() {
    return descriptors;
  }
}
