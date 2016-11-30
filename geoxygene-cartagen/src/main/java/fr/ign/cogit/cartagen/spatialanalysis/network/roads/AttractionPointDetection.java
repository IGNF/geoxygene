/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint.LabelCategory;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * A class that handles the detection of attraction points in a road network
 * from a complete dataset, following the principles of (Touya, 2010,
 * "A Road Network Selection Process Based on Data Enrichment and Structure Detection"
 * ).
 * @author GTouya
 * 
 */
public class AttractionPointDetection {

  /**
   * The attraction points detected.
   */
  private Collection<AttractionPoint> attractionPoints;
  private INetwork network;
  private CartAGenDataSet dataset;
  private boolean includeResidential = true, includeIndustrial = true;
  private boolean includeAdministrative = true, includeCommercial = true;
  private boolean includeTrainStation = true, includeLeisure = true;
  private boolean includeAirport = true, includeOtherTransport = true;
  private double maxDist = 150.0;
  private int maxImportance = 7;
  private Map<IRoadNode, AttractionPoint> createdPts = new HashMap<>();

  public AttractionPointDetection(CartAGenDataSet dataset, INetwork network) {
    super();
    this.attractionPoints = new HashSet<>();
    this.dataset = dataset;
    this.network = network;
  }

  public enum AttractionPointNature {
    TRAIN_STATION, AIRPORT, OTHER_TRANSPORT, RESIDENTIAL, COMMERCIAL, INDUSTRIAL, ADMINISTRATIVE, LEISURE, MULTIPLE, UNKNOWN;
  }

  public Collection<AttractionPoint> getAttractionPoints() {
    return attractionPoints;
  }

  public void setAttractionPoints(Collection<AttractionPoint> attractionPoints) {
    this.attractionPoints = attractionPoints;
  }

  /**
   * Find in the dataset, the attraction points and attach them to the network.
   * Fills the attractionPoints collection.
   */
  public void findAttractionPoints() {
    // first loop on the buildings to extract some attraction points
    for (IBuilding building : dataset.getBuildings()) {
      if (includeTrainStation & building.getNature().equals("Gare")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(building);
        if (nearest == null)
          continue;
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.TRAIN_STATION);
        } else {
          AttractionPoint pt = new AttractionPoint(building.getGeom()
              .centroid(), nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.TRAIN_STATION);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }

      // town halls
      if (includeAdministrative & building.getNature().equals("Mairie")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(building);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(building,
              AttractionPointNature.ADMINISTRATIVE);
        } else {
          AttractionPoint pt = new AttractionPoint(building.getGeom()
              .centroid(), nearest);
          pt.addAttractiveFeature(building,
              AttractionPointNature.ADMINISTRATIVE);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      if (includeAdministrative
          & (building.getNature().equals("Préfecture") || building.getNature()
              .equals("Sous-préfecture"))) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(building);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(building,
              AttractionPointNature.ADMINISTRATIVE);
        } else {
          AttractionPoint pt = new AttractionPoint(building.getGeom()
              .centroid(), nearest);
          pt.addAttractiveFeature(building,
              AttractionPointNature.ADMINISTRATIVE);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }

      // commercial buildings
      if (includeCommercial & building.getNature().contains("commer")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(building);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.COMMERCIAL);
        } else {
          AttractionPoint pt = new AttractionPoint(building.getGeom()
              .centroid(), nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.COMMERCIAL);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      // sports buildings
      if (includeLeisure & building.getNature().contains("sport")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(building);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.LEISURE);
        } else {
          AttractionPoint pt = new AttractionPoint(building.getGeom()
              .centroid(), nearest);
          pt.addAttractiveFeature(building, AttractionPointNature.LEISURE);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }

      // TODO
    }

    // then, loop on label points
    for (ILabelPoint poi : dataset.getLabelPoints()) {
      // residential living places
      if (includeResidential
          & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.LIVING_PLACE)
          & (poi.getNature().equals("Commune")
              || poi.getNature().equals("Canton") || poi.getNature().equals(
              "Ecart"))) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.RESIDENTIAL);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.RESIDENTIAL);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      // administrative living places
      if (includeAdministrative
          & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.LIVING_PLACE)
          & (poi.getNature().equals("Enseignement")
              || poi.getNature().equals("Santé") || poi.getNature().equals(
              "Science"))) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.ADMINISTRATIVE);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.ADMINISTRATIVE);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }

      // transport labels
      if (includeAirport & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.COMMUNICATION)
          & poi.getNature().equals("Aéroport")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.AIRPORT);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.AIRPORT);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      if (includeOtherTransport & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.COMMUNICATION)
          & poi.getNature().equals("Port")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.OTHER_TRANSPORT);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.OTHER_TRANSPORT);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }

      // OTHER category labels
      if (includeIndustrial & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.OTHER)
          & poi.getNature().equals("Zone d'activité")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.INDUSTRIAL);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.INDUSTRIAL);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      if (includeLeisure & poi.getImportance() <= maxImportance
          & poi.getLabelCategory().equals(LabelCategory.OTHER)
          & poi.getNature().equals("Zone de loisirs")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.LEISURE);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.LEISURE);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      // TODO
    }

    // then, loop on points of interest
    for (IPointOfInterest poi : dataset.getPOIs()) {
      if (includeAirport & poi.getNature().equals("Aérodrome")) {
        // first get the node near this attraction point
        IRoadNode nearest = getRoadNode(poi);
        // then, create or update the attraction point
        if (createdPts.containsKey(nearest)) {
          AttractionPoint pt = createdPts.get(nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.AIRPORT);
        } else {
          AttractionPoint pt = new AttractionPoint(poi.getGeom().centroid(),
              nearest);
          pt.addAttractiveFeature(poi, AttractionPointNature.AIRPORT);
          createdPts.put(nearest, pt);
          attractionPoints.add(pt);
        }
      }
      // TODO
    }
    // TODO
  }

  public void createRandomAttractionPoints(double ratio) {
    List<INetworkNode> nodes = new ArrayList<INetworkNode>();
    nodes.addAll(network.getNodes());
    int nbPoints = Math.round((float) ratio * nodes.size());
    Random random = new Random();
    for (int i = 0; i < nbPoints; i++) {
      int index = random.nextInt(nbPoints);
      INetworkNode node = nodes.get(index);
      nodes.remove(index);
      AttractionPoint pt = new AttractionPoint(node.getGeom().centroid(),
          (IRoadNode) node);
      pt.addAttractiveFeature(new DefaultFeature(node.getGeom()),
          AttractionPointNature.UNKNOWN);
      createdPts.put((IRoadNode) node, pt);
      attractionPoints.add(pt);
    }
  }

  private IRoadNode getRoadNode(IFeature feat) {
    IRoadNode node = (IRoadNode) SpatialQuery.selectNearest(feat.getGeom(),
        network.getNodes(), maxDist);
    return node;
  }
}
