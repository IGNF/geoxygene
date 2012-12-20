/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols;

public enum SymbolGroup {
  BD_TOPO_25,
  BD_TOPO_50,
  BD_CARTO_100,
  BD_CARTO_250,
  SPECIAL_Cartagen,
  Simple;
  
  public int getScale(){
    if(this.equals(BD_CARTO_100)) return 100000;
    else if(this.equals(BD_TOPO_50)) return 50000;
    else if(this.equals(BD_CARTO_250)) return 250000;
    return 25000;
  }
}
