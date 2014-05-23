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
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class CollapseToPointProcess extends ScaleMasterGeneProcess {

  private Class<?> classObj;
  private static CollapseToPointProcess instance = null;

  /**
   * Threshold on feature area: only features below this threshold are
   * collapsed. If the value is -1, all features are collapsed.
   */
  private double areaThreshold = -1;

  protected CollapseToPointProcess() {
    // Exists only to defeat instantiation.
  }

  public static CollapseToPointProcess getInstance() {
    if (instance == null) {
      instance = new CollapseToPointProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    parameterise();
    // get the population of points
    String ft = null;
    try {
      ft = (String) classObj.getField("FEAT_TYPE_NAME").get(null);
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
    } catch (SecurityException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (NoSuchFieldException e1) {
      e1.printStackTrace();
    }
    @SuppressWarnings("unchecked")
    IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) CartAGenDocOld
        .getInstance()
        .getCurrentDataset()
        .getCartagenPop(
            CartAGenDocOld.getInstance().getCurrentDataset()
                .getPopNameFromClass(classObj), ft);

    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      obj.eliminateBatch();
      IGeometry geom = obj.getGeom();
      if (areaThreshold > -1 && geom.area() > areaThreshold)
        continue;
      IPoint centroid = geom.centroid().toGM_Point();

      for (Method meth : CartagenApplication.getInstance().getCreationFactory()
          .getClass().getMethods()) {
        if (classObj.equals(meth.getReturnType())) {
          if (meth.getParameterTypes().length == 1
              & (meth.getParameterTypes()[0].equals(IPoint.class) || meth
                  .getParameterTypes()[0].equals(IGeometry.class))) {
            try {
              IGeneObj newObj = (IGeneObj) meth.invoke(CartagenApplication
                  .getInstance().getCreationFactory(), centroid);
              // add object to its dataset population
              pop.add(newObj);

            } catch (IllegalArgumentException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            } catch (SecurityException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  @Override
  public String getProcessName() {
    return "CollapseToPoint";
  }

  @Override
  public void parameterise() {
    if (this.hasParameter("area_min"))
      areaThreshold = (Double) getParamValueFromName("area_min");
    String themeName = (String) getParamValueFromName("theme");
    ScaleMasterTheme theme = this.getScaleMaster().getThemeFromName(themeName);
    CartAGenDB db = CartAGenDocOld.getInstance().getCurrentDataset()
        .getCartAGenDB();
    Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.addAll(theme.getRelatedClasses());
    this.classObj = db.getGeneObjImpl().filterClasses(classes).iterator()
        .next();
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("theme", String.class, "waterl"));
    params.add(new ProcessParameter("area_min", Double.class, -1.0));
    return params;
  }

}
