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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class RunwaySimplificationProcess extends ScaleMasterGeneProcess {

  private boolean collapse = false, merge = true, linkToAirport = true;
  private static RunwaySimplificationProcess instance = null;

  protected RunwaySimplificationProcess() {
    // Exists only to defeat instantiation.
  }

  public static RunwaySimplificationProcess getInstance() {
    if (instance == null) {
      instance = new RunwaySimplificationProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    Set<IAirportArea> treatedAirports = new HashSet<IAirportArea>();
    if (merge) {
      for (IGeneObj obj : features) {
        IAirportArea airport = ((IRunwayArea) obj).getAirport();
        if (treatedAirports.contains(airport))
          continue;
        if (linkToAirport && airport.isEliminated()) {
          obj.eliminateBatch();
          continue;
        }
        treatedAirports.add(airport);
        AirportTypification typif = new AirportTypification(airport);
        try {
          typif.mergeRunways();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (collapse) {
      for (IAirportArea airport : treatedAirports) {
        AirportTypification typif = new AirportTypification(airport);
        try {
          typif.collapseRunways();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public String getProcessName() {
    return "RunwaySimplification";
  }

  @Override
  public void parameterise() {
    this.merge = (Boolean) getParamValueFromName("fusion");
    this.collapse = (Boolean) getParamValueFromName("collapse");
    if (this.hasParameter("link_to_airport"))
      this.linkToAirport = (Boolean) getParamValueFromName("link_to_airport");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("fusion", Boolean.class, true));
    params.add(new ProcessParameter("collapse", Boolean.class, false));
    params.add(new ProcessParameter("link_to_airport", Boolean.class, true));
    return params;
  }

}
