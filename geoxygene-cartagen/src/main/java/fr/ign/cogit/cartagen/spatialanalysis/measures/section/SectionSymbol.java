/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class SectionSymbol {
  private static Logger logger = Logger
      .getLogger(SectionSymbol.class.getName());

  /**
   * The used symbol width, taking into account half of the border, on the map
   * in mm
   * @return
   */
  public static double getUsedSymbolWidth(INetworkSection section) {
    return (section.getInternWidth() + (section.getWidth() - section
        .getInternWidth()) / 2);
  }

  /**
   * The geometry of the symbol on the field
   * @return
   */
  public static IGeometry getSymbolExtent(INetworkSection section) {
    IGeometry g = section.getGeom().buffer(
        section.getWidth() / 2 * Legend.getSYMBOLISATI0N_SCALE() / 1000, 10,
        BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND);
    if (!(g instanceof IPolygon)) {
      SectionSymbol.logger.warn(String.valueOf(section.getWidth()));
      SectionSymbol.logger.warn(String.valueOf(section.getWidth() / 2
          * Legend.getSYMBOLISATI0N_SCALE() / 1000));
      SectionSymbol.logger.warn(String.valueOf(section.getGeom().length()));
      SectionSymbol.logger
          .warn("Warning lors du calcul de l'emprise du troncon " + section
              + ". geometrie resultat non polygone: " + g + ". geom initiale: "
              + section.getGeom());
    }
    return g;
  }

  /**
   * The geometry of the used symbol (taking into account half of the border)
   * @return
   */
  public static IGeometry getUsedSymbolExtent(INetworkSection section) {
    IGeometry g = section.getGeom().buffer(
        SectionSymbol.getUsedSymbolWidth(section) / 2
            * Legend.getSYMBOLISATI0N_SCALE() / 1000, 10,
        BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND);
    if (!(g instanceof IPolygon)) {
      SectionSymbol.logger
          .warn("Warning lors du calcul de l'emprise du troncon " + section
              + ". geometrie resultat non polygone: " + g + ". geom initiale: "
              + section.getGeom());
    }
    return g;
  }

  /**
   * The symbolised section coalescence
   * @return
   */
  public static double getCoalescence(INetworkSection section) {
    double aire = section.getGeom().length()
        * SectionSymbol.getUsedSymbolWidth(section)
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double aireEmprise = SectionSymbol.getUsedSymbolExtent(section).area();
    return (aire - aireEmprise) / aire;
  }

}
