package fr.ign.cogit.cartagen.spatialanalysis.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class KMeansClutering {

  private IFeatureCollection<IGeneObj> features;
  private int k;
  private double movementTol = 2.0;

  public KMeansClutering(Collection<? extends IGeneObj> features, int k) {
    super();
    this.features = new FT_FeatureCollection<IGeneObj>();
    this.features.addAll(features);
    this.k = k;
  }

  public IFeatureCollection<IGeneObj> getFeatures() {
    return features;
  }

  public void setFeatures(IFeatureCollection<IGeneObj> features) {
    this.features = features;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }

  public double getMovementTol() {
    return movementTol;
  }

  public void setMovementTol(double movementTol) {
    this.movementTol = movementTol;
  }

  public List<KMeansCluster> getClusters() {
    // initialisation with random centers
    List<IDirectPosition> centersList = new ArrayList<IDirectPosition>();
    // take k features randomly and use them as initial centers.
    Random random = new Random();
    int n = getFeatures().size() - 1;
    Set<Integer> usedIds = new HashSet<Integer>();
    for (int i = 0; i < k; i++) {
      boolean newId = false;
      int id = 0;
      while (!newId) {
        id = random.nextInt(n);
        if (usedIds.contains(id))
          continue;
        newId = true;
      }
      usedIds.add(id);
      IGeneObj obj = getFeatures().get(id);
      centersList.add(obj.getGeom().centroid());
    }
    List<KMeansCluster> newClusters = null;
    double moveMax = movementTol + 1;
    while (moveMax > movementTol) {
      moveMax = 0;
      newClusters = computeClusters(centersList);
      for (int i = 0; i < k; i++) {
        newClusters.get(i).computeCenter();
        double dist = centersList.get(i).distance2D(
            newClusters.get(i).getCenter());
        if (dist > moveMax)
          moveMax = dist;
        centersList.set(i, newClusters.get(i).getCenter());
      }
    }

    return newClusters;
  }

  private List<KMeansCluster> computeClusters(List<IDirectPosition> centersList) {
    List<KMeansCluster> clusters = buildEmptyClusters();
    for (IGeneObj feat : getFeatures()) {
      int clusterId = 0;
      double minDist = Double.MAX_VALUE;
      for (int i = 0; i < k; i++) {
        double dist = feat.getGeom().centroid().distance2D(centersList.get(i));
        if (dist < minDist) {
          clusterId = i;
          minDist = dist;
        }
      }
      clusters.get(clusterId).getFeatures().add(feat);
    }

    return clusters;
  }

  private List<KMeansCluster> buildEmptyClusters() {
    List<KMeansCluster> clusters = new ArrayList<KMeansCluster>();
    for (int i = 0; i < k; i++) {
      clusters.add(new KMeansCluster(i, new HashSet<IGeneObj>(), null));
    }
    return clusters;
  }
}
