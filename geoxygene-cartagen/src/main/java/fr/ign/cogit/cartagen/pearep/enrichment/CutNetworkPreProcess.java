/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.enrichment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * This pre-process is dedicated to network with features very long, thus prone
 * to bugs in generalisation processes (e.g. the railroad network).
 * @author GTouya
 * 
 */
public class CutNetworkPreProcess extends ScaleMasterPreProcess {

  private static CutNetworkPreProcess instance = null;
  private double maxLength = 2000.0;

  public CutNetworkPreProcess() {
    // Exists only to defeat instantiation.
  }

  public static CutNetworkPreProcess getInstance() {
    if (instance == null) {
      instance = new CutNetworkPreProcess();
    }
    return instance;
  }

  @Override
  public void execute(CartAGenDB dataset) throws Exception {

    for (Class<? extends IGeneObj> classObj : this.getProcessedClasses()) {
      IPopulation<IGeneObj> pop = dataset.getDataSet().getCartagenPop(
          dataset.getDataSet().getPopNameFromClass(classObj));
      INetwork network = dataset.getDataSet().getNetworkFromClass(classObj);
      IPopulation<IGeneObj> iterable = new Population<IGeneObj>();
      iterable.addAll(pop);
      for (IGeneObj obj : iterable) {
        if (!(obj instanceof INetworkSection)) {
          continue;
        }
        INetworkSection section = (INetworkSection) obj;
        try {
          // test if the section is short enough
          if (section.getGeom().length() <= maxLength)
            continue;

          // cut the section every maxLength meters
          boolean first = true;
          ILineString geom = section.getGeom();
          double currentLength = 0.0;
          IDirectPositionList points = new DirectPositionList(geom.coord().get(
              0));
          for (int j = 1; j < geom.coord().size(); j++) {
            currentLength += geom.coord().get(j)
                .distance2D(geom.coord().get(j - 1));
            points.add(geom.coord().get(j));
            if (currentLength < maxLength)
              continue;

            ILineString newGeom = new GM_LineString(points);
            if (first) {
              section.setGeom(newGeom);
              first = false;
            } else {
              Constructor<? extends IGeneObj> constr = classObj
                  .getConstructor(ILineString.class);
              INetworkSection newObj = (INetworkSection) constr
                  .newInstance(newGeom);
              newObj.setSymbolId(section.getSymbolId());
              newObj.setImportance(section.getImportance());
              // copy attributes
              copyAttributes(newObj, section);
              pop.add(newObj);
              network.addSection(newObj);
            }
            points.clear();
            points.add(geom.coord().get(j));
            currentLength = 0.0;
          }

        } catch (SecurityException e1) {
          e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
          e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
          e1.printStackTrace();
        } catch (InstantiationException e1) {
          e1.printStackTrace();
        } catch (IllegalAccessException e1) {
          e1.printStackTrace();
        } catch (InvocationTargetException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  @Override
  public String getPreProcessName() {
    return "Make Network Planar";
  }

  private void copyAttributes(IGeneObj copie, IGeneObj original)
      throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    Class<?> classObj = original.getClass();
    for (Field field : classObj.getDeclaredFields()) {
      // filter the fields to represent relations between GeneObj features
      if (IGeneObj.class.isAssignableFrom(field.getType()))
        continue;
      if (IGeometry.class.isAssignableFrom(field.getType()))
        continue;
      if (IFeature.class.isAssignableFrom(field.getType()))
        continue;
      // get the getter of this field
      String getterName = "get" + field.getName().substring(0, 1).toUpperCase()
          + field.getName().substring(1);
      if (field.getType().equals(boolean.class))
        getterName = "is" + field.getName().substring(0, 1).toUpperCase()
            + field.getName().substring(1);
      Method getter = classObj.getDeclaredMethod(getterName);
      Object value = getter.invoke(original);
      // get the setter of this field
      String setterName = "set" + field.getName().substring(0, 1).toUpperCase()
          + field.getName().substring(1);
      Method setter = classObj.getDeclaredMethod(setterName, field.getType());
      setter.invoke(copie, value);
    }
  }
}
