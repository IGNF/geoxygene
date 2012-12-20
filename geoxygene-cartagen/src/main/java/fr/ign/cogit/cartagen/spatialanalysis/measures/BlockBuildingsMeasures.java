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

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.DeletionCost;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class BlockBuildingsMeasures {

  // ///////////////////////
  // MEASURES ON BLOCKS
  // ///////////////////////

  /**
   * Returns the number of non deleted buildings in the block
   */
  public static int getBlockNonDeletedBuildingsNumber(IUrbanBlock block) {
    int nbBuild = 0;
    for (IUrbanElement build : block.getUrbanElements()) {
      if (!(build instanceof IBuilding)) {
        continue;
      }
      if (!build.isDeleted()) {
        nbBuild++;
      }
    }
    return nbBuild;
  }

  /**
   * Cleans the decomposition of a block by clearing all proximity segments of
   * its buildings
   */
  public static void cleanBlockDecomposition(IUrbanBlock block) {
    for (IUrbanElement build : block.getUrbanElements()) {
      if (build.getProximitySegments() != null) {
        build.getProximitySegments().clear();
      }
    }
  }

  /**
   * 
   * renvoit le meilleur batiment a supprimer c'est celui dont le cout de
   * suppression est le plus grand suppose que la triangulation de l'ilot a ete
   * effectuee
   * 
   * @return le meilleur batiment a supprimer
   */
  public static IBuilding getNextBuildingToRemoveInBlock(IUrbanBlock block,
      double distanceMax) {

    // initialisation
    double coutMax = Double.MIN_VALUE;
    IBuilding abMax = null;

    // recupere aire du plus grand batiment de l'ilot
    double surfaceMaxBatimentIlot = BlockBuildingsMeasures
        .getBlockBiggestBuildingArea(block);

    for (IUrbanElement ab : block.getUrbanElements()) {
      if (ab.isDeleted()) {
        continue;
      }
      if (!(ab instanceof IBuilding)) {
        continue;
      }

      double cout = DeletionCost.getCoutSuppression(ab, surfaceMaxBatimentIlot,
          distanceMax);
      if (cout > coutMax) {
        coutMax = cout;
        abMax = (IBuilding) ab;
      }
    }
    return abMax;
  }

  /**
   * returns the biggest building of a block
   * @param block
   * @return
   */
  public static double getBlockBiggestBuildingArea(IUrbanBlock block) {
    double maxArea = 0.0, area;
    for (IUrbanElement ag : block.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      if (!(ag instanceof IBuilding)) {
        continue;
      }
      area = ag.getGeom().area();
      if (area > maxArea) {
        maxArea = area;
      }
    }
    return maxArea;
  }

  /**
   * returns the smallest building of a block
   * @param block
   * @return
   */
  public static IBuilding getBlockSmallestBuilding(IUrbanBlock block) {
    double minAires = Double.MAX_VALUE;
    IBuilding abMin = null;
    for (IUrbanElement ab : block.getUrbanElements()) {
      if (ab.isDeleted()) {
        continue;
      }
      if (!(ab instanceof IBuilding)) {
        continue;
      }
      double aire = ab.getGeom().area();
      if (aire < minAires) {
        minAires = aire;
        abMin = (IBuilding) ab;
      }
    }
    return abMin;
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  public static double getBuildingsOverlappingRateMean(IUrbanBlock block) {
    if (block.isDeleted()) {
      return 0.0;
    }
    double mean = 0.0;
    int nb = 0;
    for (IUrbanElement ag : block.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      if (!(ag instanceof IBuilding)) {
        continue;
      }
      mean += BlockBuildingsMeasures.getBuildingOverlappingRate(ag, block);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }
    return mean;
  }

  /**
   * @return the most overlapped building in a block
   */
  public static IBuilding getBlockMaxOverlapBuilding(IUrbanBlock block) {
    IBuilding abMax = null;
    double tauxConflitMax = 0.0;
    for (IUrbanElement ab : block.getUrbanElements()) {
      if (ab.isDeleted()) {
        continue;
      }
      if (!(ab instanceof IBuilding)) {
        continue;
      }
      double tauxConflit = BlockBuildingsMeasures.getBuildingOverlappingRate(
          ab, block);
      if (tauxConflit > tauxConflitMax) {
        tauxConflitMax = tauxConflit;
        abMax = (IBuilding) ab;
      }
    }
    return abMax;
  }

  // ///////////////////////
  // MEASURES ON BUILDINGS
  // ///////////////////////

  /**
   * returns the goal area of an urban element after enlargment
   * @param build
   * @return
   */
  public static double getBuildingGoalArea(IUrbanElement build) {
    if (build.isDeleted()) {
      return 0.0;
    }
    double area = build.getSymbolArea();

    // en deca de la valeur SpecCarto.aireSeuilSuppressionBatiment, le batiment
    // est supprime
    if (area < GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT) {
      return 0.0;
    }

    // au dela, le batiment est grossi ou bien conserve son aire
    double aireMini = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() * Legend.getSYMBOLISATI0N_SCALE()
        / 1000000;
    if (area > aireMini) {
      return area;
    }
    return aireMini;
  }

  /**
   * @return the part of the urban element which is overlapping with the other
   *         objects buildings and roads of the block - takes into account a
   *         separation distance.
   */
  public static IGeometry getBuildingOverlappingGeometry(IUrbanElement build,
      IUrbanBlock block) {
    if (build.isDeleted()) {
      return null;
    }
    if (!block.getUrbanElements().contains(build)) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    // superposition avec les autres batiments de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IUrbanElement ag : block.getUrbanElements()) {
      if (ag == build) {
        continue;
      }
      if (ag.getFeature().isDeleted()) {
        continue;
      }
      if (ag.getSymbolGeom().distance(build.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms.buffer(ag.getSymbolGeom(),
          distSep).intersection(build.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }

    // superposition avec les troncons de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (INetworkSection tr : block.getSurroundingNetwork()) {
      if (tr.isDeleted()) {
        continue;
      }
      IGeometry emp = SectionSymbol.getSymbolExtent(tr);
      if (emp.distance(build.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = emp.buffer(distSep).intersection(
          build.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    return geomSuperposition;
  }

  /**
   * @return the urban element overlapping rates, within [0,1]
   */
  public static double getBuildingOverlappingRate(IUrbanElement build,
      IUrbanBlock block) {
    IGeometry overGeom = BlockBuildingsMeasures.getBuildingOverlappingGeometry(
        build, block);
    if (overGeom == null || overGeom.isEmpty()) {
      return 0.0;
    }
    return overGeom.area() / build.getSymbolGeom().area();
  }

  /**
   * @return la partie de l'element urbain qui se superpose avec les autres
   *         batiments de l'ilot
   */
  public static IGeometry getBuildingOverlappingGeometryWithOtherBuildings(
      IUrbanElement build, IUrbanBlock block) {
    if (build.isDeleted()) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    // superposition avec les autres batiments de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IUrbanElement ag : block.getUrbanElements()) {
      if (ag == build) {
        continue;
      }
      if (ag.isDeleted()) {
        continue;
      }
      if (ag.getSymbolGeom().distance(build.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms.buffer(ag.getSymbolGeom(),
          distSep).intersection(build.getSymbolGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    return geomSuperposition;
  }

  /**
   * @return the urban element overlapping rates with the other buildings of a
   *         block, within [0,1]
   */
  public static double getBuildingOverlappingRateWithOtherBuildings(
      IUrbanElement build, IUrbanBlock block) {
    IGeometry geomSuperposition = BlockBuildingsMeasures
        .getBuildingOverlappingGeometryWithOtherBuildings(build, block);
    if (geomSuperposition == null) {
      return 0.0;
    }
    return geomSuperposition.area() / build.getSymbolArea();
  }

  /**
   * returns true if a building is overlapping its block
   * @return
   */
  public static boolean isBuildingOverlappingBlock(IUrbanElement build,
      IUrbanBlock block) {
    for (INetworkSection tr : block.getSurroundingNetwork()) {
      if (tr.isDeleted()) {
        continue;
      }
      // test if the section belongs to meso limit
      IGeometry outline = block.getGeom().getExterior();
      if (!outline.contains(tr.getGeom())) {
        continue;
      }
      if (tr.getGeom().buffer(
          tr.getWidth() / 2 * Legend.getSYMBOLISATI0N_SCALE() / 1000)
          .intersects(build.getSymbolGeom())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public static IGeometry getBuildingDirectOverlapGeometryWithOtherBuildings(
      IUrbanElement build, IUrbanBlock block) {
    if (build.isDeleted()) {
      return null;
    }
    IGeometry geomSuperposition = null;

    // superposition avec les autres batiments de l'ilot
    for (IUrbanElement ag : block.getUrbanElements()) {
      if (ag == build) {
        continue;
      }
      if (ag.isDeleted()) {
        continue;
      }
      IGeometry intersection = ag.getSymbolGeom().intersection(
          build.getSymbolGeom());
      if (intersection == null) {
        continue;
      } else if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    return geomSuperposition;
  }

  /**
   * 
   * @return
   */
  public static double getBuildingDirectOverlapRateWithOtherBuildings(
      IUrbanElement build, IUrbanBlock block) {
    IGeometry geomSuperposition = BlockBuildingsMeasures
        .getBuildingDirectOverlapGeometryWithOtherBuildings(build, block);
    if (geomSuperposition == null) {
      return 0.0;
    }
    return geomSuperposition.area() / build.getSymbolArea();
  }

}
