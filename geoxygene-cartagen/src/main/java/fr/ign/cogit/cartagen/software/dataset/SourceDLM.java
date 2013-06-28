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

public enum SourceDLM {

  SPECIAL_CARTAGEN, BD_TOPO_V2, BD_CARTO, BD_Cete1, BD_OSInitial, BD_OSFinal, VMAP2i, VMAP1, VMAP0, MGCPPlusPlus, VMAP1PlusPlus, OpenStreetMap;

  /**
   * Get the scale related to source DLM. Can be useful to compute a radical
   * law.
   * @return
   */
  public double getRelatedScale() {
    if (this.equals(SPECIAL_CARTAGEN))
      return 15000.0;
    if (this.equals(BD_TOPO_V2))
      return 15000.0;
    if (this.equals(BD_CARTO))
      return 50000.0;
    if (this.equals(VMAP2i) || this.equals(MGCPPlusPlus))
      return 50000.0;
    if (this.equals(VMAP1) || this.equals(VMAP1PlusPlus))
      return 250000.0;
    if (this.equals(VMAP0))
      return 1000000.0;
    return 15000.0;
  }
}
