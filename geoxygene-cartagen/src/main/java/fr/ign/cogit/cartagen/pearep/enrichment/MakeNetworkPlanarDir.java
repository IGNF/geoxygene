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
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * Same as {@link MakeNetworkPlanar} but keeps the initial direction of lines
 * when they are cut.
 * @author GTouya
 * 
 */
public class MakeNetworkPlanarDir extends ScaleMasterPreProcess {

  private static MakeNetworkPlanarDir instance = null;

  public MakeNetworkPlanarDir() {
    // Exists only to defeat instantiation.
  }

  public static MakeNetworkPlanarDir getInstance() {
    if (instance == null) {
      instance = new MakeNetworkPlanarDir();
    }
    return instance;
  }

  @Override
  public void execute(CartAGenDB dataset) throws Exception {

    for (Class<? extends IGeneObj> classObj : this.getProcessedClasses()) {
      IPopulation<IGeneObj> pop = dataset.getDataSet().getCartagenPop(
          dataset.getDataSet().getPopNameFromClass(classObj));
      IPopulation<IGeneObj> popIter = new Population<IGeneObj>();
      for (IGeneObj obj : pop) {
        if (obj.isEliminated())
          continue;
        popIter.add(obj);
      }
      INetwork network = dataset.getDataSet().getNetworkFromClass(classObj);
      CarteTopo carteTopo = new CarteTopo("make planar");
      carteTopo.importClasseGeo(popIter, true);
      carteTopo.rendPlanaire(1.0);
      for (IGeneObj obj : popIter) {
        if (!(obj instanceof INetworkSection)) {
          continue;
        }
        INetworkSection section = (INetworkSection) obj;
        ILineString geomIni = section.getGeom();
        try {
          // test if the section has been cut by topological map
          if (section.getCorrespondants().size() == 1) {
            continue;
          }

          // update the section geometry with the first edge of the
          // topological
          // map
          ILineString geom = (ILineString) section.getCorrespondants().get(0)
              .getGeom();
          if (!CommonAlgorithmsFromCartAGen
              .areLinesSameDirection(geomIni, geom))
            geom = geom.reverse();
          section.setGeom(geom);

          // loop on the other edges to make new instances
          Constructor<? extends IGeneObj> constr;

          constr = classObj.getConstructor(ILineString.class);
          for (int i = 1; i < section.getCorrespondants().size(); i++) {
            ILineString newLine = (ILineString) section.getCorrespondants()
                .get(i).getGeom();
            if (!CommonAlgorithmsFromCartAGen.areLinesSameDirection(geomIni,
                newLine))
              newLine = newLine.reverse();
            INetworkSection newObj = (INetworkSection) constr
                .newInstance(newLine);
            newObj.setSymbolId(section.getSymbolId());
            newObj.setImportance(section.getImportance());
            // copy attributes
            copyAttributes(newObj, section);
            pop.add(newObj);
            network.addSection(newObj);
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

      // build the network topology
      /*
       * popIter = new Population<IGeneObj>(); for (IGeneObj obj : pop) { if
       * (obj.isEliminated()) continue; popIter.add(obj); } carteTopo = new
       * CarteTopo("make topology"); carteTopo.importClasseGeo(popIter, true);
       * carteTopo.rendPlanaire(1.0); IPopulation<IWaterNode> popWater =
       * CartAGenDoc.getInstance() .getCurrentDataset().getWaterNodes(); for
       * (Noeud n : carteTopo.getPopNoeuds()) { IWaterNode waterNode =
       * CartagenApplication.getInstance()
       * .getCreationFactory().createWaterNode(n); popWater.add(waterNode);
       * CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork()
       * .addNode(waterNode); }
       */
      NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
          .getCurrentDataset().getHydroNetwork(), false);
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
