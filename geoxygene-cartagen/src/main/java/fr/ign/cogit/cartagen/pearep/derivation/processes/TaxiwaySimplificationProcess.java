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
 * Encapsulate the airport generalisation methods related to taxiways.
 * @author GTouya
 * 
 */
public class TaxiwaySimplificationProcess extends ScaleMasterGeneProcess {

  private boolean collapse = false, merge = true, linkToAirport = true;
  /**
   * the taxiway parts that are less wide than minWidth are collapsed into
   * lines.
   */
  private double minWidth;
  private static TaxiwaySimplificationProcess instance = null;

  protected TaxiwaySimplificationProcess() {
    // Exists only to defeat instantiation.
  }

  public static TaxiwaySimplificationProcess getInstance() {
    if (instance == null) {
      instance = new TaxiwaySimplificationProcess();
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
          typif.merg;
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
    return "TaxiwaySimplification";
  }

  @Override
  public void parameterise() {
    this.minWidth = (Double) getParamValueFromName("min-width");
    if (this.hasParameter("fusion"))
      this.merge = (Boolean) getParamValueFromName("fusion");
    if (this.hasParameter("collapse"))
      this.collapse = (Boolean) getParamValueFromName("collapse");
    if (this.hasParameter("link_to_airport"))
      this.linkToAirport = (Boolean) getParamValueFromName("link_to_airport");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("min-width", Double.class, 30.0));
    params.add(new ProcessParameter("fusion", Boolean.class, true));
    params.add(new ProcessParameter("collapse", Boolean.class, false));
    params.add(new ProcessParameter("link_to_airport", Boolean.class, true));
    return params;
  }

}
