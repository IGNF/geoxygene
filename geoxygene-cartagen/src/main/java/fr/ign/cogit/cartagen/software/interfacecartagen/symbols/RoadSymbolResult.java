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

public class RoadSymbolResult {
  public int symbolId;
  public int importance;

  public RoadSymbolResult(int symbolId, int importance) {
    this.symbolId = symbolId;
    this.importance = importance;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "importance: " + importance + " & symbolId: " + symbolId;
  }

}
