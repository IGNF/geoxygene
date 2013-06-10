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
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.DistanceClustering;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.hulls.PointsConvexHull;

/**
 * Encapsulate a process that clusters point features by K-Means and then
 * replace them by their centroid.
 * @author GTouya
 * 
 */
public class KMeansClusteringProcess extends ScaleMasterGeneProcess {

  private Class<?> classObj;
  private static KMeansClusteringProcess instance = null;

  /**
   * Threshold on clustering distance: two features below this threshold are
   * grouped in a same cluster.
   */
  private double clusteringDistance = 50.0;

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

    // first cluster features
    Set<Set<IGeneObj>> clusters = new DistanceClustering(features,
        clusteringDistance).getClusters();

    // then cover clusters
    for (Set<IGeneObj> cluster : clusters) {
      IDirectPositionList points = new DirectPositionList();
      for (IGeneObj obj : cluster)
        points.add(obj.getGeom().centroid());
      IPolygon geom = PointsConvexHull.compute(points);

      for (Method meth : CartagenApplication.getInstance().getCreationFactory()
          .getClass().getMethods()) {
        if (classObj.equals(meth.getReturnType())) {
          if (meth.getParameterTypes().length == 1
              & (meth.getParameterTypes()[0].equals(IPolygon.class) || meth
                  .getParameterTypes()[0].equals(IGeometry.class))) {
            try {
              IGeneObj newObj = (IGeneObj) meth.invoke(CartagenApplication
                  .getInstance().getCreationFactory(), geom);
              // add object to its dataset population
              String ft = (String) classObj.getField("FEAT_TYPE_NAME")
                  .get(null);
              @SuppressWarnings("unchecked")
              IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDoc
                  .getInstance()
                  .getCurrentDataset()
                  .getCartagenPop(
                      CartAGenDoc.getInstance().getCurrentDataset()
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

  @Override
  public String getProcessName() {
    return "CoverByConvexHull";
  }

  @Override
  public void parameterise() {
    if (this.hasParameter("clustering_distance"))
      clusteringDistance = (Double) getParamValueFromName("clustering_distance");
    String themeName = (String) getParamValueFromName("theme");
    ScaleMasterTheme theme = this.getScaleMaster().getThemeFromName(themeName);
    CartAGenDB db = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartAGenDB();
    Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.addAll(theme.getRelatedClasses());
    this.classObj = db.getGeneObjImpl().filterClasses(classes).iterator()
        .next();
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("theme", String.class, ""));
    params.add(new ProcessParameter("clustering_distance", Double.class, -1.0));
    return params;
  }

}
