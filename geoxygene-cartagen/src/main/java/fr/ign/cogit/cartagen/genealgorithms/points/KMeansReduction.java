/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.points;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.KMeansCluster;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.KMeansClutering;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class provides an algorithm to simplify a set of points by KMeans
 * clustering. The algorithm corresponds to a reduction according to the
 * classification of point-set operators from Bereuter & Weibel (CaGIS 2013),
 * i.e. there is still a set of points as output of the algorithm, but fewer
 * points. Two options are possible: either keeping one of the initial points to
 * replace a cluster or replace the cluster by its centroid.
 * @author gtouya
 *
 */
public class KMeansReduction {

  private IFeatureCollection<? extends IGeneObj> features;
  /**
   * The number of clusters remaining after KMeans.
   */
  private int k = 0;
  /**
   * The ratio of remaining points.
   */
  private double shrinkRatio = 1.0;

  /**
   * true if the clusters are replaced by their centroid, false if the clusters
   * are replaced by one of their points.
   */
  private boolean centroid = false;

  public KMeansReduction(IFeatureCollection<? extends IGeneObj> features,
      boolean centroid, double shrinkRatio) {
    super();
    this.features = features;
    this.centroid = centroid;
    this.shrinkRatio = shrinkRatio;
  }

  public KMeansReduction(IFeatureCollection<? extends IGeneObj> features,
      boolean centroid, int k) {
    super();
    this.features = features;
    this.k = k;
    this.centroid = centroid;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }

  public double getShrinkRatio() {
    return shrinkRatio;
  }

  public void setShrinkRatio(double shrinkRatio) {
    this.shrinkRatio = shrinkRatio;
  }

  public boolean isCentroid() {
    return centroid;
  }

  public void setCentroid(boolean centroid) {
    this.centroid = centroid;
  }

  public void reducePointSet() {
    // compute k
    if (this.k == 0)
      this.k = (int) Math.round(features.size() * shrinkRatio);
    // first cluster features
    List<KMeansCluster> clusters = new KMeansClutering(features, k)
        .getClusters();

    // then cover clusters
    for (KMeansCluster cluster : clusters) {
      cluster.computeCenter();
      IGeneObj newCenter = cluster.getCenterNearest();
      for (IGeneObj obj : cluster.getFeatures()) {
        if (centroid || !obj.equals(newCenter))
          obj.eliminate();
      }

      if (centroid) {
        Class<?> classObj = cluster.getFeaturesClass();
        for (Method meth : CartAGenDoc.getInstance().getCurrentDataset()
            .getCartAGenDB().getGeneObjImpl().getCreationFactory().getClass()
            .getMethods()) {
          if (classObj.equals(meth.getReturnType())) {
            if (meth.getParameterTypes().length == 1
                & (meth.getParameterTypes()[0].equals(IPolygon.class)
                    || meth.getParameterTypes()[0].equals(IGeometry.class))) {
              try {
                IGeneObj newObj = (IGeneObj) meth.invoke(
                    CartAGenDoc.getInstance().getCurrentDataset()
                        .getCartAGenDB().getGeneObjImpl().getCreationFactory(),
                    cluster.getCenter().toGM_Point());
                // add object to its dataset population
                String ft = (String) classObj.getField("FEAT_TYPE_NAME")
                    .get(null);
                @SuppressWarnings("unchecked")
                IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDoc
                    .getInstance().getCurrentDataset()
                    .getCartagenPop(CartAGenDoc.getInstance()
                        .getCurrentDataset().getPopNameFromClass(classObj), ft);
                pop.add(newObj);

              } catch (IllegalArgumentException e) {
                e.printStackTrace();
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              } catch (InvocationTargetException e) {
                e.printStackTrace();
              } catch (SecurityException e) {
                e.printStackTrace();
              } catch (NoSuchFieldException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }
  }
}
