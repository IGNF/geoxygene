/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class DensityMeasures {

  /**
   * Measure the building density (with current building sizes) of a block. The
   * road symbols are taken into account.
   */
  public static double getBlockBuildingsDensity(IPolygon block,
      Collection<IUrbanElement> components,
      IFeatureCollection<INetworkSection> blockRoads) {
    if (components == null) {
      return 0.0;
    }
    // computes the sum of the areas of the non deleted buildings
    double buildingArea = 0.0;
    for (IUrbanElement elem : components) {
      if (elem.isDeleted()) {
        continue;
      }
      if (!block.intersects(elem.getSymbolGeom())) {
        continue;
      }
      buildingArea += elem.getSymbolArea();
    }

    // computes the area of the roads inside the block
    IGeometry symbols = null;
    for (INetworkSection road : blockRoads) {
      IGeometry geom = SectionSymbol.getSymbolExtent(road);
      if (symbols == null) {
        symbols = geom;
      } else {
        symbols.union(geom);
      }
    }
    if (symbols == null) {
      return 0.0;
    }

    // Computes the density ratio
    symbols = symbols.intersection(block);
    double symbolArea = symbols.area();
    return (buildingArea + symbolArea) / block.area();

  }

  /**
   * Measure the building density (with current building sizes) of a block. The
   * road symbols are taken into account.
   */
  public static double getBlockBuildingsDensity(IUrbanBlock block) {
    return DensityMeasures.getBlockBuildingsDensity(block.getGeom(), block
        .getUrbanElements(), block.getSurroundingNetwork());
  }

  /**
   * Measure the initial building density (with initial building sizes) of a
   * block. The road symbols are taken into account.
   */
  public static double getBlockBuildingsInitialDensity(IPolygon block,
      Collection<IUrbanElement> components,
      IFeatureCollection<INetworkSection> blockRoads) {
    if (components == null) {
      return 0.0;
    }
    // computes the sum of the areas of the non deleted buildings
    double buildingArea = 0.0;
    for (IUrbanElement elem : components) {
      if (!block.intersects(elem.getInitialGeom())) {
        continue;
      }
      buildingArea += elem.getInitialGeom().area();
    }

    // computes the area of the roads inside the block
    IGeometry symbols = null;
    for (INetworkSection road : blockRoads) {
      IGeometry geom = SectionSymbol.getSymbolExtent(road);
      if (symbols == null) {
        symbols = geom;
      } else {
        symbols.union(geom);
      }
    }
    if (symbols == null) {
      return 0.0;
    }

    // Computes the density ratio
    symbols = symbols.intersection(block);
    double symbolArea = symbols.area();
    return (buildingArea + symbolArea) / block.area();

  }

  /**
   * Measure the building density (with current building sizes) of a block. The
   * road symbols are taken into account.
   */
  public static double getBlockBuildingsInitialDensity(IUrbanBlock block) {
    return DensityMeasures.getBlockBuildingsInitialDensity(block.getGeom(),
        block.getUrbanElements(), block.getSurroundingNetwork());
  }

  /**
   * Measure the building simulated density (with minimum building size after
   * enlargment) of a block. The road symbols are taken into account.
   */
  public static double getBlockBuildingsSimulatedDensity(IPolygon block,
      Collection<IUrbanElement> components,
      IFeatureCollection<INetworkSection> blockRoads, double buildingMinSize) {

    // computes the sum of the areas of the non deleted buildings
    if (components == null) {
      return 0.0;
    }
    double buildingArea = 0.0;
    for (IUrbanElement elem : components) {
      if (elem.isDeleted()) {
        continue;
      }
      if (!block.intersects(elem.getSymbolGeom())) {
        continue;
      }
      buildingArea += Math.min(buildingMinSize, BlockBuildingsMeasures
          .getBuildingGoalArea(elem));
    }

    // computes the area of the roads inside the block
    IGeometry symbols = null;
    for (INetworkSection road : blockRoads) {
      IGeometry geom = SectionSymbol.getSymbolExtent(road);
      if (symbols == null) {
        symbols = geom;
      } else {
        symbols.union(geom);
      }
    }
    if (symbols == null) {
      return 0.0;
    }

    // Computes the density ratio
    symbols = symbols.intersection(block);
    double symbolArea = symbols.area();
    return (buildingArea + symbolArea) / block.area();

  }

  /**
   * Measure the simulated density (with minimum building size after enlargment)
   * of a block. The road symbols are taken into account.
   */
  public static double getBlockBuildingsSimulatedDensity(IUrbanBlock block,
      double buildingMinSize) {
    return DensityMeasures.getBlockBuildingsSimulatedDensity(block.getGeom(),
        block.getUrbanElements(), block.getSurroundingNetwork(),
        buildingMinSize);
  }

  /**
   * Measure the simulated density of a block. The road symbols are taken into
   * account.
   */
  public static double getBlockBuildingsSimulatedDensity(IUrbanBlock block) {
    double buildingMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE()
        * Legend.getSYMBOLISATI0N_SCALE()
        / 1000000;
    return DensityMeasures.getBlockBuildingsSimulatedDensity(block.getGeom(),
        block.getUrbanElements(), block.getSurroundingNetwork(),
        buildingMinSize);
  }

  /**
   * Measure the building simulated density (with minimum building size after
   * enlargment) of a block taking into account all buildings even deleted. The
   * road symbols are taken into account.
   */
  public static double getBlockBuildingsInitialSimulatedDensity(IPolygon block,
      Collection<IUrbanElement> components,
      IFeatureCollection<INetworkSection> blockRoads, double buildingMinSize) {

    if (components == null) {
      return 0.0;
    }
    // computes the sum of the areas of the non deleted buildings
    double buildingArea = 0.0;
    for (IUrbanElement elem : components) {
      if (!block.intersects(elem.getSymbolGeom())) {
        continue;
      }
      buildingArea += Math.min(buildingMinSize, BlockBuildingsMeasures
          .getBuildingGoalArea(elem));
    }

    // computes the area of the roads inside the block
    IGeometry symbols = null;
    for (INetworkSection road : blockRoads) {
      IGeometry geom = SectionSymbol.getSymbolExtent(road);
      if (symbols == null) {
        symbols = geom;
      } else {
        symbols.union(geom);
      }
    }
    if (symbols == null) {
      return 0.0;
    }

    // Computes the density ratio
    symbols = symbols.intersection(block);
    double symbolArea = symbols.area();
    return (buildingArea + symbolArea) / block.area();

  }

  /**
   * Measure the building simulated density (with minimum building size after
   * enlargment) of a block taking into account all buildings even deleted. The
   * road symbols are taken into account.
   */
  public static double getBlockBuildingsInitialSimulatedDensity(
      IUrbanBlock block) {
    double buildingMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE()
        * Legend.getSYMBOLISATI0N_SCALE()
        / 1000000;
    return DensityMeasures.getBlockBuildingsInitialSimulatedDensity(block
        .getGeom(), block.getUrbanElements(), block.getSurroundingNetwork(),
        buildingMinSize);
  }

}
