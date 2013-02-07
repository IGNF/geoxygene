package fr.ign.cogit.cartagen.spatialanalysis.clustering;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * Make clusters with the adjacent features in the given feature collection.
 * @author GTouya
 * 
 */
public class AdjacencyClustering {

  private IFeatureCollection<IGeneObj> features;

  public AdjacencyClustering(Collection<? extends IGeneObj> features) {
    super();
    this.features = new FT_FeatureCollection<IGeneObj>();
    this.features.addAll(features);
  }

  public void setFeatures(IFeatureCollection<IGeneObj> features) {
    this.features = features;
  }

  public IFeatureCollection<IGeneObj> getFeatures() {
    return features;
  }

  public Set<Set<IGeneObj>> getClusters() {
    Set<Set<IGeneObj>> clusters = new HashSet<Set<IGeneObj>>();
    Stack<IGeneObj> stack = new Stack<IGeneObj>();
    stack.addAll(features);
    while (!stack.empty()) {
      Set<IGeneObj> cluster = new HashSet<IGeneObj>();
      IGeneObj feature = stack.pop();
      Stack<IGeneObj> stack2 = new Stack<IGeneObj>();
      stack2.add(feature);
      while (!stack2.empty()) {
        IGeneObj feat = stack2.pop();
        cluster.add(feat);
        // get the features that touch feat
        Collection<IGeneObj> touchColn = features.select(feat.getGeom());
        touchColn.removeAll(stack2);
        touchColn.removeAll(cluster);
        stack2.addAll(touchColn);
      }
      clusters.add(cluster);
      stack.removeAll(cluster);
    }

    return clusters;
  }
}
