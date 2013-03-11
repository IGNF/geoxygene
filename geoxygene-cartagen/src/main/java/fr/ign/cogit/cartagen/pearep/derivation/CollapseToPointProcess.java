/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class CollapseToPointProcess extends ScaleMasterGeneProcess {

  private String className;
  private static CollapseToPointProcess instance = null;

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
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      obj.eliminateBatch();
      IGeometry geom = obj.getGeom();
      IPoint centroid = geom.centroid().toGM_Point();
      Class<?> classObj = null;
      try {
        classObj = Class.forName(className);
      } catch (ClassNotFoundException e1) {
        e1.printStackTrace();
      }
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
              CartAGenDoc
                  .getInstance()
                  .getCurrentDataset()
                  .getCartagenPop(
                      CartAGenDoc.getInstance().getCurrentDataset()
                          .getPopNameFromClass(classObj)).add(newObj);
            } catch (IllegalArgumentException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
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
    this.className = (String) getParamValueFromName("class_name");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("class_name", String.class, ""));

    return params;
  }

}
