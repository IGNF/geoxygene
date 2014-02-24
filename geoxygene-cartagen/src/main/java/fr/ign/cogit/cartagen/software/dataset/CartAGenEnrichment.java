/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

import java.util.Collection;

import fr.ign.cogit.cartagen.software.CartagenApplication;

public enum CartAGenEnrichment {
  NETWORK_FACES, ROAD_NETWORK, BUILDINGS, BUILDINGS_ALIGNMENT, HYDRO_NETWORK, RELIEF, LAND_USE, TOURIST_ROUTE_NETWORK, ROAD_TOURIST_ROUTE_ASSOCIATION;

  /**
   * Set the boolean field of CartAGenApplication in relation to 'this'
   * enrichment to true;
   */
  public void setToggle(boolean toggle) {
    if (this.equals(BUILDINGS))
      CartagenApplication.getInstance().setEnrichissementBati(toggle);
    if (this.equals(ROAD_NETWORK))
      CartagenApplication.getInstance().setEnrichissementRoutier(toggle);
    if (this.equals(NETWORK_FACES))
      CartagenApplication.getInstance().setConstructNetworkFaces(toggle);
    if (this.equals(HYDRO_NETWORK))
      CartagenApplication.getInstance().setEnrichissementHydro(toggle);
    if (this.equals(RELIEF))
      CartagenApplication.getInstance().setEnrichissementRelief(toggle);
    if (this.equals(LAND_USE))
      CartagenApplication.getInstance().setEnrichissementOccSol(toggle);
    if (this.equals(BUILDINGS_ALIGNMENT))
      CartagenApplication.getInstance().setEnrichissementBatiAlign(toggle);
  }

  public static void setOtherToFalse(Collection<CartAGenEnrichment> enrichments) {
    for (CartAGenEnrichment enrich : CartAGenEnrichment.values()) {
      if (!enrichments.contains(enrich))
        enrich.setToggle(false);
      else
        enrich.setToggle(true);
    }

  }
}
