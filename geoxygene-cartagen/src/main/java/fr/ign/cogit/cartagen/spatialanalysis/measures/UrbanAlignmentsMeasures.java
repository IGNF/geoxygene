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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;

public class UrbanAlignmentsMeasures {

  /**
   * @return la partie du batiment qui se superpose avec les autres batiments
   *         d'un alignement
   */
  public static IGeometry getOverlappingGeometryInAlignment(
      IUrbanElement build, IUrbanAlignment align) {
    if (build.isDeleted()) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    // superposition avec les autres batiments de l'ilot
    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (IUrbanElement ag : align.getUrbanElements()) {
      if (ag == build) {
        continue;
      }
      if (ag.isDeleted()) {
        continue;
      }
      if (ag.getGeom().distance(build.getGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms.buffer(
          ag.getFeature().getGeom(), distSep).intersection(build.getGeom());
      if (geomSuperposition == null) {
        geomSuperposition = intersection;
      } else {
        geomSuperposition = intersection.union(geomSuperposition);
      }
    }
    return geomSuperposition;
  }

  /**
   * @return the urban element overlapping rates in an alignment, within [0,1]
   */
  public static double getBuildingOverlappingRateInAlignment(
      IUrbanElement build, IUrbanAlignment align) {
    IGeometry geomSuperposition = UrbanAlignmentsMeasures
        .getOverlappingGeometryInAlignment(build, align);
    if (geomSuperposition == null) {
      return 0.0;
    }
    return geomSuperposition.area() / build.getGeom().area();
  }

  /**
   * @return the buildings overlapping rates mean, within [0,1]
   */
  public static double getBuildingsOverlappingRateMean(IUrbanAlignment align) {
    if (align.isDeleted()) {
      return 0.0;
    }
    double mean = 0.0;
    int nb = 0;
    for (IUrbanElement ag : align.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      mean += UrbanAlignmentsMeasures.getBuildingOverlappingRateInAlignment(ag,
          align);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }
    return mean;
  }

  /**
   * @return the buildings overlapping rates sigma, within [0,1]
   */
  public static double getBuildingsOverlappingRateSigma(IUrbanAlignment align) {
    if (align.isDeleted()) {
      return 0.0;
    }
    double sigma = 0.0;
    double mean = UrbanAlignmentsMeasures
        .getBuildingsOverlappingRateMean(align);
    int nb = 0;
    for (IUrbanElement ag : align.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      double overlappingRate = UrbanAlignmentsMeasures
          .getBuildingOverlappingRateInAlignment(ag, align);
      sigma += Math.pow(overlappingRate - mean, 2.0);
      nb++;
    }
    if (nb != 0) {
      sigma = Math.sqrt(sigma);
    }
    return sigma;
  }

  /**
   * @return the buildings overlapping rates mean taking into account their
   *         enlargement, within [0,1]
   */
  public static double getEnlargedBuildingsOverlappingRateMean(
      IUrbanAlignment align) {

    if (align.isDeleted()) {
      return 0.0;
    }

    // Initialisation
    HashMap<IUrbanElement, IPolygon> initialGeoms = new HashMap<IUrbanElement, IPolygon>();
    double mean = 0.0;
    int nb = 0;

    // Enlargment of buildings
    for (IUrbanElement ag : align.getUrbanElements()) {
      initialGeoms.put(ag, new GM_Polygon(new GM_LineString(ag.getGeom()
          .coord())));
      double goalArea = BlockBuildingsMeasures.getBuildingGoalArea(ag);
      ag.setGeom(CommonAlgorithms.homothetie((IPolygon) ag.getGeom(), Math
          .sqrt(goalArea / ag.getGeom().area())));
    }

    // Computation of superposition
    for (IUrbanElement ag : align.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      mean += UrbanAlignmentsMeasures.getBuildingOverlappingRateInAlignment(ag,
          align);
      nb++;
    }
    if (nb != 0) {
      mean /= nb;
    }

    // Cancel building enlargement
    for (IUrbanElement agent : initialGeoms.keySet()) {
      agent.setGeom(initialGeoms.get(agent));
    }

    return mean;
  }

  /**
   * @return the buildings overlapping rates sigma taking into account their
   *         enlargement, within [0,1]
   */
  public static double getEnlargedBuildingsOverlappingRateSigma(
      IUrbanAlignment align) {

    if (align.isDeleted()) {
      return 0.0;
    }

    // Initialisation
    HashMap<IUrbanElement, IPolygon> initialGeoms = new HashMap<IUrbanElement, IPolygon>();
    double sigma = 0.0;
    double mean = UrbanAlignmentsMeasures
        .getEnlargedBuildingsOverlappingRateMean(align);
    int nb = 0;

    // Enlargment of buildings
    for (IUrbanElement ag : align.getUrbanElements()) {
      initialGeoms.put(ag, new GM_Polygon(new GM_LineString(ag.getGeom()
          .coord())));
      double goalArea = BlockBuildingsMeasures.getBuildingGoalArea(ag);
      ag.setGeom(CommonAlgorithms.homothetie((IPolygon) ag.getGeom(), Math
          .sqrt(goalArea / ag.getGeom().area())));
    }

    // Computation of superposition
    for (IUrbanElement ag : align.getUrbanElements()) {
      if (ag.isDeleted()) {
        continue;
      }
      double overlappingRate = UrbanAlignmentsMeasures
          .getBuildingOverlappingRateInAlignment(ag, align);
      sigma += Math.pow(overlappingRate - mean, 2.0);
      nb++;
    }
    if (nb != 0) {
      sigma = Math.sqrt(sigma);
    }

    // Cancel building enlargement
    for (IUrbanElement agent : initialGeoms.keySet()) {
      agent.setGeom(initialGeoms.get(agent));
    }

    return sigma;
  }

  /**
   * @return the buildings centroid distances sigma
   */
  public static double getBuildingsCentroidsDistanceSigma(IUrbanAlignment align) {

    if (align.isDeleted()) {
      return 0.0;
    }

    HashMap<IUrbanElement, Boolean> urbanElements = new HashMap<IUrbanElement, Boolean>();
    for (IUrbanElement build : align.getUrbanElements()) {
      if (!build.isDeleted()) {
        urbanElements.put(build, Boolean.valueOf(true));
      }
    }

    // Initialisation of the shape line
    ILineString centroidLine = new GM_LineString();
    centroidLine.coord().add(align.getInitialElement().getGeom().centroid());
    urbanElements.put(align.getInitialElement(), Boolean.valueOf(false));
    IUrbanElement currentUrbanElement = align.getInitialElement();

    // Addition of the centroid coordinates one by one
    for (int i = 0; i < align.getUrbanElements().size() - 2; i++) {
      double distMin = Double.MAX_VALUE;
      IUrbanElement nearestBuilding = currentUrbanElement;
      for (IUrbanElement build : urbanElements.keySet()) {
        if (urbanElements.get(build) == Boolean.valueOf(false)) {
          continue;
        }
        double dist = build.getGeom().distance(currentUrbanElement.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearestBuilding = build;
        }
      }
      centroidLine.coord().add(nearestBuilding.getGeom().centroid());
      urbanElements.put(nearestBuilding, Boolean.valueOf(false));
      currentUrbanElement = nearestBuilding;
      if (nearestBuilding.equals(align.getFinalElement())) {
        break;
      }
    }

    // End of the shape line
    centroidLine.coord().add(align.getFinalElement().getGeom().centroid());

    // Computation of mean centroid distance
    double mean = centroidLine.length() / (urbanElements.size() - 1);

    // Computation of sigma
    double sigma = 0.0;
    for (int i = 0; i < centroidLine.coord().size() - 1; i++) {
      sigma += Math.pow(centroidLine.coord().get(i + 1).distance(
          centroidLine.coord().get(i))
          - mean, 2.0);
    }
    sigma = Math.sqrt(sigma);
    return sigma;

  }

  /**
   * @return the mean orientation factor of buildings compared to their ideal
   *         orientation in the alignment
   */
  public static double getBuildingsMeanOrientationFactor(IUrbanAlignment align) {

    if (align.isDeleted()) {
      return 0.0;
    }

    // Initialisation of the shape line and the non deleted components of the
    // alignment
    List<IUrbanElement> nonDeletedComponents = new ArrayList<IUrbanElement>();
    for (IUrbanElement urbanElement : align.getUrbanElements()) {
      if (!urbanElement.isDeleted()) {
        nonDeletedComponents.add(urbanElement);
      }
    }
    double mean = 0.0;

    for (int i = 1; i < nonDeletedComponents.size(); i++) {
      // current orientation
      double orientation = new OrientationMeasure(nonDeletedComponents.get(i)
          .getGeom()).getGeneralOrientation();
      // ideal orientation
      Angle idealAngle = new Angle();
      if (i == 0) {
        idealAngle = new Angle(
            nonDeletedComponents.get(0).getGeom().centroid(),
            nonDeletedComponents.get(0).getGeom().centroid());
      } else if (i == nonDeletedComponents.size() - 1) {
        idealAngle = new Angle(nonDeletedComponents.get(i - 1).getGeom()
            .centroid(), nonDeletedComponents.get(i).getGeom().centroid());
      } else {
        idealAngle = new Angle(nonDeletedComponents.get(i - 1).getGeom()
            .centroid(), nonDeletedComponents.get(i + 1).getGeom().centroid());
      }
      double idealOrientation = idealAngle.getValeur();
      // Difference between the two orientations
      mean += 180 * Math.abs(idealOrientation - orientation) / Math.PI;
    }

    return mean / nonDeletedComponents.size();

  }

  /**
   * @return the part of the urban alignment which is overlapping with the roads
   *         of the block
   */
  public static IGeometry getRoadOverlappingGeometry(IUrbanAlignment align,
      IUrbanBlock block) {
    if (align.isDeleted()) {
      return null;
    }
    IGeometry geomSuperposition = null;
    double distSep;

    distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    for (INetworkSection tr : block.getSurroundingNetwork()) {
      if (tr.isDeleted()) {
        continue;
      }
      IGeometry emp = SectionSymbol.getSymbolExtent(tr);
      if (emp.distance(align.getSymbolGeom()) > distSep) {
        continue;
      }
      IGeometry intersection = emp.buffer(distSep).intersection(
          align.getSymbolGeom());
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
  public static double getRoadOverlappingRate(IUrbanAlignment align,
      IUrbanBlock block) {
    IGeometry overGeom = UrbanAlignmentsMeasures.getRoadOverlappingGeometry(
        align, block);
    if (overGeom == null || overGeom.isEmpty()) {
      return 0.0;
    }
    return overGeom.area() / align.getSymbolGeom().area();
  }

  /**
   * @return the ratio of overlapped alignments inside the block
   */
  public static double getBlockOverlappedAlignmentsRatio(IUrbanBlock block) {

    int overlappedAlignments = 0;
    int alignmentsNumber = 0;

    // Test of alignments one by one
    for (IUrbanAlignment align : block.getAlignments()) {
      alignmentsNumber++;
      double overlappingRatio = 0.0;

      // Comparison to other alignments to compute overlapping
      for (IUrbanAlignment alignBis : block.getAlignments()) {
        if (alignBis.equals(align)) {
          continue;
        }
        // No overlapping
        if (!alignBis.getGeom().overlaps(align.getGeom())) {
          continue;
        }
        // Else, computation of overlapping
        overlappingRatio += (alignBis.getGeom().intersection(align.getGeom())
            .area())
            / align.getGeom().area();
      }

      if (overlappingRatio > 0.5) {
        overlappedAlignments++;
      }

    }
    return overlappedAlignments / alignmentsNumber;
  }

}
