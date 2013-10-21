/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.KMeansCluster;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.KMeansClutering;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate a process that clusters point features by K-Means and then
 * replace them by their centroid.
 * @author GTouya
 * 
 */
public class KMeansClusteringProcess extends ScaleMasterGeneProcess {

  private boolean centroid = false;
  private static KMeansClusteringProcess instance = null;

  /**
   * The number of clusters remaining after KMeans.
   */
  private int k = 1;
  /**
   * The ratio of remaining points.
   */
  private double shrink_ratio = 1.0;

  protected KMeansClusteringProcess() {
    // Exists only to defeat instantiation.
  }

  public static KMeansClusteringProcess getInstance() {
    if (instance == null) {
      instance = new KMeansClusteringProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    // compute k
    this.k = (int) Math.round(features.size() * shrink_ratio);
    // first cluster features
    List<KMeansCluster> clusters = new KMeansClutering(features, k)
        .getClusters();

    // then cover clusters
    for (KMeansCluster cluster : clusters) {
      cluster.computeCenter();
      IGeneObj newCenter = cluster.getCenterNearest();
      for (IGeneObj obj : cluster.getFeatures()) {
        if (centroid || !obj.equals(newCenter))
          obj.eliminateBatch();
      }

      if (centroid) {
        Class<?> classObj = cluster.getFeaturesClass();
        for (Method meth : CartagenApplication.getInstance()
            .getCreationFactory().getClass().getMethods()) {
          if (classObj.equals(meth.getReturnType())) {
            if (meth.getParameterTypes().length == 1
                & (meth.getParameterTypes()[0].equals(IPolygon.class) || meth
                    .getParameterTypes()[0].equals(IGeometry.class))) {
              try {
                IGeneObj newObj = (IGeneObj) meth.invoke(CartagenApplication
                    .getInstance().getCreationFactory(), cluster.getCenter()
                    .toGM_Point());
                // add object to its dataset population
                String ft = (String) classObj.getField("FEAT_TYPE_NAME").get(
                    null);
                @SuppressWarnings("unchecked")
                IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDocOld
                    .getInstance()
                    .getCurrentDataset()
                    .getCartagenPop(
                        CartAGenDocOld.getInstance().getCurrentDataset()
                            .getPopNameFromClass(classObj), ft);
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

  @Override
  public String getProcessName() {
    return "KMeansClustering";
  }

  @Override
  public void parameterise() {
    if (this.hasParameter("shrink_ratio"))
      shrink_ratio = (Double) getParamValueFromName("shrink_ratio");
    if (this.hasParameter("centroid"))
      centroid = (Boolean) getParamValueFromName("centroid");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("centroid", Boolean.class, false));
    params.add(new ProcessParameter("shrink_ratio", Double.class, 1.0));
    return params;
  }

}
