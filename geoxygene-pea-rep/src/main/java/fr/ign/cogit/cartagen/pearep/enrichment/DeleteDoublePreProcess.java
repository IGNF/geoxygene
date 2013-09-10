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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * This pre-process is dedicated to network with features very long, thus prone
 * to bugs in generalisation processes (e.g. the railroad network).
 * @author GTouya
 * 
 */
public class DeleteDoublePreProcess extends ScaleMasterPreProcess {

  private static DeleteDoublePreProcess instance = null;

  public DeleteDoublePreProcess() {
    // Exists only to defeat instantiation.
  }

  public static DeleteDoublePreProcess getInstance() {
    if (instance == null) {
      instance = new DeleteDoublePreProcess();
    }
    return instance;
  }

  @Override
  public void execute(CartAGenDB dataset) throws Exception {

    for (Class<? extends IGeneObj> classObj : this.getProcessedClasses()) {
      IPopulation<IGeneObj> pop = dataset.getDataSet().getCartagenPop(
          dataset.getDataSet().getPopNameFromClass(classObj));
      INetwork net = null;
      if (IRoadLine.class.isAssignableFrom(classObj))
        net = dataset.getDataSet().getRoadNetwork();
      if (IWaterLine.class.isAssignableFrom(classObj))
        net = dataset.getDataSet().getHydroNetwork();
      IPopulation<IGeneObj> iterable = new Population<IGeneObj>();
      iterable.addAll(pop);
      IPopulation<IGeneObj> iterable2 = new Population<IGeneObj>();
      iterable2.addAll(pop);
      for (IGeneObj obj : iterable) {
        if (obj.isEliminated())
          continue;

        // search for a double feature of obj
        for (IGeneObj other : iterable2) {
          if (other.equals(obj))
            continue;
          if (other.getGeom().equals(obj.getGeom())) {
            other.eliminateBatch();
            pop.remove(other);
            if (net != null)
              net.getSections().remove(other);
          }
        }
      }
    }
  }

  @Override
  public String getPreProcessName() {
    return "Delete double features";
  }

}
