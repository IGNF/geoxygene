package fr.ign.cogit.geoxygene.semio.legend.symbol.color;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeatureCollection;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 *
 * @see Contrast
 */
public class HueQualityContrastComparator implements Comparator<SymbolisedFeatureCollection> {

  @Override
  public int compare(SymbolisedFeatureCollection sfc1,
      SymbolisedFeatureCollection sfc2) {
    
    double hueQContrast1 = sfc1.getMeanContrast().getQualiteContrasteTeinte();
    double hueQContrast2 = sfc2.getMeanContrast().getQualiteContrasteTeinte();
    
    if (hueQContrast1 > hueQContrast2) {
      return -1;
    } else if (hueQContrast1 < hueQContrast2) {
      return 1;
    } else {
      return 0;
    }
  }
}
