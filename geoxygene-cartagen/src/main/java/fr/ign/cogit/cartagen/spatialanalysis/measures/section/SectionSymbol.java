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

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.carringrelation.ICarrierNetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class SectionSymbol {
  private static Logger logger = Logger
      .getLogger(SectionSymbol.class.getName());

  /**
   * The used symbol width, taking into account half of the border, on the map
   * in mm
   * @return
   */
  public static double getUsedSymbolWidth(INetworkSection section) {
    return (section.getInternWidth()
        + (section.getWidth() - section.getInternWidth()) / 2);
  }

  /**
   * The geometry of the symbol on the field
   * @return
   */
  @SuppressWarnings("unchecked")
  public static IGeometry getSymbolExtent(INetworkSection section) {
    IGeometry g = section.getGeom().buffer(
        section.getWidth() / 2 * Legend.getSYMBOLISATI0N_SCALE() / 1000, 10,
        BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND);
    if (!(g instanceof IPolygon)) {
      SectionSymbol.logger.warn(String.valueOf(section.getWidth()));
      SectionSymbol.logger.warn(String.valueOf(
          section.getWidth() / 2 * Legend.getSYMBOLISATI0N_SCALE() / 1000));
      SectionSymbol.logger.warn(String.valueOf(section.getGeom().length()));
      SectionSymbol.logger
          .warn("Warning lors du calcul de l'emprise du troncon " + section
              + ". geometrie resultat non polygone: " + g + ". geom initiale: "
              + section.getGeom());
      return CommonAlgorithmsFromCartAGen
          .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) g);
    }
    return g;
  }

  /**
   * The geometry of the symbol on the field
   * @return
   */
  @SuppressWarnings("unchecked")
  public static IGeometry getSymbolExtent(IFeature section) {
    double width = SLDUtilCartagen.getSymbolMaxWidth(section);
    IGeometry g = section.getGeom().buffer(width / 2, 10,
        BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND);
    if (!(g instanceof IPolygon)) {
      SectionSymbol.logger.warn(String.valueOf(width));
      SectionSymbol.logger.warn(
          String.valueOf(width / 2 * Legend.getSYMBOLISATI0N_SCALE() / 1000));
      SectionSymbol.logger.warn(String.valueOf(section.getGeom().length()));
      SectionSymbol.logger
          .warn("Warning lors du calcul de l'emprise du troncon " + section
              + ". geometrie resultat non polygone: " + g + ". geom initiale: "
              + section.getGeom());
      return CommonAlgorithmsFromCartAGen
          .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) g);
    }
    return g;
  }

  /**
   * The geometry of the symbol on the field
   * @return
   */
  public static IGeometry getSymbolExtentAtScale(INetworkSection section,
      double scale) {
    IGeometry g = section.getGeom().buffer(
        section.getWidth() / 2 * scale / 1000, 10, BufferParameters.CAP_FLAT,
        BufferParameters.JOIN_ROUND);
    if (!(g instanceof IPolygon)) {
      SectionSymbol.logger.warn(String.valueOf(section.getWidth()));
      SectionSymbol.logger
          .warn(String.valueOf(section.getWidth() / 2 * scale / 1000));
      SectionSymbol.logger.warn(String.valueOf(section.getGeom().length()));
      SectionSymbol.logger
          .warn("Warning lors du calcul de l'emprise du troncon " + section
              + ". geometrie resultat non polygone: " + g + ". geom initiale: "
              + section.getGeom());
    }
    return g;
  }

  public static IGeometry getSymbolExtentWithCarriedObjects(
      ICarrierNetworkSection section) {
    // Compute the size of the buffer
    double distanceLeft = section.distance(true);

    ILineString l = JtsAlgorithms
        .offsetCurve(section.getGeom(), (section.getWidth() / 2 + distanceLeft)
            * Legend.getSYMBOLISATI0N_SCALE() / 1000)
        .get(0);

    // Compute the size of the buffer
    double distanceRight = section.distance(false);

    ILineString r = JtsAlgorithms.offsetCurve(section.getGeom(),
        -(section.getWidth() / 2 + distanceRight)
            * Legend.getSYMBOLISATI0N_SCALE() / 1000)
        .get(0);

    if (l.coord().get(0).distance(r.coord().get(0)) < l.coord().get(0)
        .distance(r.coord().get(r.coord().size() - 1))) {
      l.coord().inverseOrdre();
    }

    for (IDirectPosition position : r.coord()) {
      l.coord().add(position);
    }
    l.coord().add(l.coord().get(0));

    return new GM_Polygon(l);

  }

  public static ILineString getSymbolExtentAsAnOffsetWithCarriedObjects(
      ICarrierNetworkSection section, boolean left, double offsetAdjust) {
    double distance = section.distance(left);
    distance *= left ? 1 : -1;
    offsetAdjust *= left ? 1 : -1;
    // System.out.println("distance : " + distance);
    ILineString g = JtsAlgorithms.offsetCurve(section.getGeom(),
        offsetAdjust + ((left ? 1 : -1) * section.getWidth() / 2 + distance)
            * Legend.getSYMBOLISATI0N_SCALE() / 1000)
        .get(0);

    if (g.coord().get(0).distance(section.getGeom().coord().get(0)) > g.coord()
        .get(0).distance(section.getGeom().coord()
            .get(section.getGeom().coord().size() - 1))) {
      g.coord().inverseOrdre();
    }
    return g;
  }

  public static ILineString getSymbolExtentAsAnOffsetWithCarriedObjects(
      ICarrierNetworkSection section, boolean left) {
    return getSymbolExtentAsAnOffsetWithCarriedObjects(section, left, 0.0);
  }

  public static IGeometry getSymbolExtentWithCarriedObjects(
      ICarrierNetworkSection section, boolean left) {

    ILineString g = getSymbolExtentAsAnOffsetWithCarriedObjects(section, left);
    for (IDirectPosition position : section.getGeom().coord()) {
      g.coord().add(position);
    }
    g.coord().add(g.coord().get(0));
    return new GM_Polygon(g);

  }

  public static IGeometry getMaxSymbolExtentWithCarriedObjects(
      ICarrierNetworkSection section) {
    return getMaxSymbolExtentWithCarriedObjects(section, 0);
  }

  public static IGeometry getMaxSymbolExtentWithCarriedObjects(
      ICarrierNetworkSection section, double marge) {
    // Compute the size of the buffer
    double distance = section.maxWidth() + marge;

    ILineString g = JtsAlgorithms
        .offsetCurve(section.getGeom(), (section.getWidth() / 2 + distance) / 2
            * Legend.getSYMBOLISATI0N_SCALE() / 1000)
        .get(0);

    if (g.coord().get(0).distance(section.getGeom().coord().get(0)) < g.coord()
        .get(0).distance(
            section.getGeom().coord().get(section.getGeom().coord().size()))) {
      g.coord().inverseOrdre();
    }

    for (IDirectPosition position : section.getGeom().coord()) {
      g.coord().add(position);
    }
    g.coord().add(g.coord().get(0));
    return new GM_Polygon(g);

  }

  /**
   * The geometry of the used symbol (taking into account half of the border)
   * @return
   */
  public static IGeometry getUsedSymbolExtent(INetworkSection section) {
    IGeometry g = section.getGeom().buffer(
        SectionSymbol.getUsedSymbolWidth(section) / 2
            * Legend.getSYMBOLISATI0N_SCALE() / 1000,
        10, BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND);
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
