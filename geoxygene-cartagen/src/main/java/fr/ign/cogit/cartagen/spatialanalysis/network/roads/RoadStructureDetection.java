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
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.jgrapht.GraphFactory;
import fr.ign.cogit.cartagen.graph.jgrapht.MetricalGraphWeighter;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

/**
 * A class that compiles algorithm for the detection of road structures bigger
 * than complex crossroads, like interchanges, rest areas, or dual carriageways.
 * @author GTouya
 * 
 */
public class RoadStructureDetection {

  private static Logger logger = Logger.getLogger(RoadStructureDetection.class);

  private CarteTopo topoMap;

  // Parameters on geometric properties for dual carriageways
  private final double concLimit;
  private final double elongLimit;
  private final double compLimit;
  private final double areaLimit;
  // parameters for interchanges
  private final double distMaxClustering;
  private final double euclMaxDist;
  private final int clusterMinSize;

  public RoadStructureDetection() {
    this.concLimit = 0.8;
    this.elongLimit = 5.0;
    this.compLimit = 0.1;
    this.areaLimit = 80000.0;
    this.distMaxClustering = 600.0;
    this.euclMaxDist = 50.0;
    this.clusterMinSize = 6;
  }

  // ///////////////////////////////////
  // DETECTION OF THE DUAL CARRIAGEWAYS
  // ///////////////////////////////////

  /**
   * Main detection method, which fills the collection of motorway separators
   * @param importance the importance of the roads to use (-1 to use all roads)
   */
  public List<Face> detectDualCarriageways(int importance) {

    // builds the topological map based on motorway sections
    List<Face> allFaces = this.buildMajorRoadsTopoMap(importance);

    // detects the primary separators based on geometry
    List<Face> separators = this.detectLongFaces(allFaces);
    separators.removeAll(this.detectSharpAngleFaces(separators));
    separators.removeAll(this.detectBadFaces(separators));

    // detects the remaining little separators based on continuity
    separators.addAll(this
        .detectNeighbourLittleSeparators(separators, allFaces));

    return separators;

  }

  /**
   * Main detection method, which fills the collection of motorway separators.
   * @param popName the name of the population (and layer) of dual carriageways
   * @param importance the importance of the roads to use (-1 to use all roads)
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IDualCarriageWay> detectAndBuildDualCarriageways(
      String popName, int importance) {

    // builds the topological map based on motorway sections
    List<Face> allFaces = this.buildMajorRoadsTopoMap(importance);

    // detects the primary separators based on geometry
    List<Face> separators = this.detectLongFaces(allFaces);
    separators.removeAll(this.detectSharpAngleFaces(separators));
    separators.removeAll(this.detectBadFaces(separators));

    // detects the remaining little separators based on continuity
    separators.addAll(this
        .detectNeighbourLittleSeparators(separators, allFaces));

    // build the dual carriageways from the separators
    IPopulation<IDualCarriageWay> duals = new Population<>(popName);
    AbstractCreationFactory factory = CartAGenDoc.getInstance()
        .getCurrentDataset().getCartAGenDB().getGeneObjImpl()
        .getCreationFactory();
    for (Face separator : separators) {
      // get inner roads from the topology map
      Set<IRoadLine> innerRoads = new HashSet<>();
      for (Arc arc : separator.getArcsDirects()) {
        for (IFeature feat : arc.getCorrespondants())
          innerRoads.add((IRoadLine) feat);
      }
      for (Arc arc : separator.getArcsIndirects()) {
        for (IFeature feat : arc.getCorrespondants())
          innerRoads.add((IRoadLine) feat);
      }
      Set<IRoadLine> outerRoads = new HashSet<>();
      for (Arc arc : (List<Arc>) separator.arcsExterieursClasses().get(0)) {
        for (IFeature feat : arc.getCorrespondants())
          outerRoads.add((IRoadLine) feat);
      }
      int mainImportance = innerRoads.iterator().next().getImportance();
      duals.add(factory.createDualCarriageways(separator.getGeometrie(),
          mainImportance, innerRoads, outerRoads));
    }

    return duals;

  }

  /**
   * Method that builds a topological map from motorway sections - the faces
   * will be used to detect separators
   * @return
   */

  private List<Face> buildMajorRoadsTopoMap(int importance) {
    if (importance == -1) {
      return this.buildTopoMap(CartAGenDoc.getInstance().getCurrentDataset()
          .getRoads());
    }
    IPopulation<IRoadLine> roadSections = new Population<IRoadLine>();
    for (IRoadLine road : CartAGenDoc.getInstance().getCurrentDataset()
        .getRoads()) {
      if (road.getImportance() == importance) {
        roadSections.add(road);
      }
    }
    return this.buildTopoMap(roadSections);
  }

  /**
   * Method that builds a topological map from determined road sections - the
   * faces will be used to detect separators
   * @return
   */

  private List<Face> buildTopoMap(IPopulation<IRoadLine> roadSections) {
    return this.buildTopoMap(roadSections, true);
  }

  /**
   * Method that builds a topological map from determined road sections - the
   * faces will be used to detect separators
   * @return
   */

  private List<Face> buildTopoMap(IPopulation<IRoadLine> roadSections,
      boolean useMask) {

    // fills the topological map with motorway sections
    topoMap = new CarteTopo("cartetopo");
    topoMap.setBuildInfiniteFace(false);
    topoMap.importClasseGeo(roadSections, true);

    // adds the mask to the topological map if needed
    if (useMask) {
      IPopulation<IMask> contours = new Population<IMask>();
      for (IMask mask : CartAGenDoc.getInstance().getCurrentDataset()
          .getMasks()) {
        contours.add(mask);
      }
      topoMap.importClasseGeo(contours, true);
    }
    // computes the topology
    topoMap.creeNoeudsManquants(1.0);
    topoMap.fusionNoeuds(1.0);
    topoMap.filtreDoublons(1.0);
    topoMap.rendPlanaire(1.0);
    topoMap.fusionNoeuds(1.0);
    topoMap.filtreArcsDoublons();

    if (!useMask) {

      // close future faces abstract features on the limits of the topo map
      IPopulation<Noeud> lonelyNodes = new Population<Noeud>();
      // Initial node
      for (Arc arc : topoMap.getListeArcs()) {
        if (arc.getNoeudIni().getEntrants().size()
            + arc.getNoeudIni().getSortants().size() == 1) {
          lonelyNodes.add(arc.getNoeudIni());
        }
        // Final node
        if (arc.getNoeudFin().getEntrants().size()
            + arc.getNoeudFin().getSortants().size() == 1) {
          lonelyNodes.add(arc.getNoeudFin());
        }
      }
      // Addition of the abstract limits to the topo map
      IPopulation<IFeature> abstractLimits = new Population<IFeature>();
      for (Noeud node : lonelyNodes) {
        IPoint geom = (IPoint) node.getGeom();
        Noeud closestNode = node;
        double closestDistance = Double.MAX_VALUE;
        for (Noeud node2 : lonelyNodes) {
          if (node2.equals(node)) {
            continue;
          }
          double distance = geom.distance(node2.getGeom());
          if (distance < closestDistance) {
            closestNode = node2;
            closestDistance = distance;
          }
        }
        if (closestDistance < 100.0) {
          ILineSegment segment = new GM_LineSegment(geom.getPosition(),
              ((IPoint) closestNode.getGeom()).getPosition());
          IFeature abstractLimit = new DefaultFeature(segment);
          abstractLimits.add(abstractLimit);
        }
      }
      topoMap.importClasseGeo(abstractLimits, true);

      // re-computes the topology with additional arcs
      topoMap.creeNoeudsManquants(1.0);
      topoMap.fusionNoeuds(1.0);
      topoMap.filtreDoublons(1.0);
      topoMap.rendPlanaire(1.0);
      topoMap.fusionNoeuds(1.0);
      topoMap.filtreArcsDoublons();

    }

    // computes the faces topology
    try {
      topoMap.creeTopologieFaces();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("Impossible to compute faces topology on defined roads");
    }

    return topoMap.getListeFaces();

  }

  /**
   * Method that calculates the geometric properties of the potential
   * separators, in order to know if the can be separators or not
   * @return a table of 4 doubles: area, compactness, concavity and elongation
   *         of the face
   */

  private double[] calculeFaceGeomProperties(Face face) {

    double[] geomProp = new double[4];

    // Area and perimeter
    double area = face.getGeom().area();
    double perim = ((IPolygon) face.getGeom()).perimeter();
    geomProp[0] = area;

    // Compactness
    double compactness = 4 * Math.PI * area / (perim * perim);
    geomProp[1] = compactness;

    // Concavity
    IGeometry convexHull = face.getGeom().convexHull();
    double surfaceHull = convexHull.area();
    double concavity = area / surfaceHull;
    geomProp[2] = concavity;

    // Elongation
    IPolygon rectEngl = SmallestSurroundingRectangleComputation.getSSR(face
        .getGeom());
    double X0 = rectEngl.coord().get(0).getX();
    double X1 = rectEngl.coord().get(1).getX();
    double X2 = rectEngl.coord().get(2).getX();
    double Y0 = rectEngl.coord().get(0).getY();
    double Y1 = rectEngl.coord().get(1).getY();
    double Y2 = rectEngl.coord().get(2).getY();
    double length = Math.sqrt((X1 - X0) * (X1 - X0) + (Y1 - Y0) * (Y1 - Y0));
    double width = Math.sqrt((X2 - X1) * (X2 - X1) + (Y2 - Y1) * (Y2 - Y1));
    if (length < width) {
      double temp = width;
      width = length;
      length = temp;
    }
    double elongation = length / width;
    geomProp[3] = elongation;

    return geomProp;

  }

  /**
   * Method that detects the separators based on their geometric properties
   * @return the faces whose geometric properties fit those needed to be a
   *         separator
   */

  private List<Face> detectLongFaces(List<Face> allFaces) {

    List<Face> longFaces = new ArrayList<Face>();

    for (Face face : allFaces) {

      // Geometric properties of the face
      double[] geomProp = this.calculeFaceGeomProperties(face);
      double area = geomProp[0];
      double compactness = geomProp[1];
      double concavity = geomProp[2];
      double elongation = geomProp[3];

      // if the face is convex, we consider the elongation
      if (concavity > this.concLimit) {
        if ((elongation > this.elongLimit)
            || ((compactness < this.compLimit) && (elongation > this.elongLimit / 2))) {
          longFaces.add(face);
          continue;
        }
      }

      // if the face is not convex, we consider the compactness
      else {
        if (compactness < this.compLimit) {
          longFaces.add(face);
          continue;
        }
      }

      // special case : long motorways
      if ((compactness < this.compLimit / 4) && (area < 10 * this.areaLimit)) {
        longFaces.add(face);
      }

    }

    return longFaces;

  }

  /**
   * Method that detects the faces that have sharp angles (supposed to concern
   * slip roads so not needed)
   * @param faces : the faces on which the detection is performed
   * @return the detected faces
   */

  private List<Face> detectSharpAngleFaces(List<Face> faces) {

    List<Face> sharpAngleFaces = new ArrayList<Face>();
    Angle alpha = new Angle();

    for (Face face : faces) {

      List<Arc> arcs = new ArrayList<Arc>();
      arcs.addAll(face.getArcsDirects());
      arcs.addAll(face.getArcsIndirects());

      // loop on each section
      for (Arc arc : arcs) {
        IFeature obj = arc.getCorrespondant(0);
        if (obj instanceof IRoadLine) {
          IRoadLine sect = (IRoadLine) obj;

          // calculation of points
          int i = sect.getGeom().numPoints();
          IDirectPosition p1 = sect.getGeom().coord().get(0);
          IDirectPosition p2 = sect.getGeom().coord().get(1);
          IDirectPosition p3 = sect.getGeom().coord().get(i - 2);
          IDirectPosition p4 = sect.getGeom().coord().get(i - 1);

          // second loop on each section
          for (Arc arc2 : arcs) {
            IFeature obj2 = arc2.getCorrespondant(0);
            if (obj2 instanceof IRoadLine) {
              IRoadLine sect2 = (IRoadLine) obj2;

              // comparison of the section
              // do not continue if the two sections are the same object
              if (sect.equals(sect2) == false) {

                // calculation of the points of the second section
                if (sect.getGeom().buffer(0.1)
                    .intersection(sect2.getGeom().buffer(0.1)).isEmpty() == false) {
                  IDirectPosition pDebut = sect2.getGeom().startPoint();
                  IDirectPosition pFin = sect2.getGeom().endPoint();
                  IDirectPosition pDebutSuite = sect2.getGeom().coord().get(1);
                  IDirectPosition pFinSuite = sect2.getGeom().coord()
                      .get(sect2.getGeom().numPoints() - 2);
                  IDirectPosition centroid = sect.getGeom().buffer(0.1)
                      .intersection(sect2.getGeom().buffer(0.1)).centroid();

                  // 4 cases
                  if (((pDebut.distance(centroid)) < (pFin.distance(centroid)))
                      && (pDebut.distance(centroid) < 2)) {
                    if ((p1.distance(centroid)) < (p4.distance(centroid))) {
                      alpha = Angle.angleTroisPoints(p2, centroid, pDebutSuite);
                    } else {
                      alpha = Angle.angleTroisPoints(p3, centroid, pDebutSuite);
                    }
                    if (Math.abs(alpha.getValeur()) < Math.PI / 9) {
                      sharpAngleFaces.add(face);
                      break;
                    }
                  }

                  if (((pFin.distance(centroid)) < (pDebut.distance(centroid)))
                      && (pFin.distance(centroid) < 2)) {
                    if ((p1.distance(centroid)) < (p4.distance(centroid))) {
                      alpha = Angle.angleTroisPoints(p2, centroid, pFinSuite);
                    } else {
                      alpha = Angle.angleTroisPoints(p3, centroid, pFinSuite);
                    }
                    if (Math.abs(alpha.getValeur()) < Math.PI / 9) {
                      sharpAngleFaces.add(face);
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return sharpAngleFaces;

  }

  /**
   * Detection of faces that cannot be separators
   * @param faces : the faces on which the detection is performed
   * @return the detected faces
   */

  private List<Face> detectBadFaces(List<Face> faces) {

    List<Face> badFaces = new ArrayList<Face>();

    for (Face face : faces) {

      // a separator should have at least 4 delineating sections
      List<Arc> arcs = new ArrayList<Arc>();
      arcs.addAll(face.getArcsDirects());
      arcs.addAll(face.getArcsIndirects());
      if (arcs.size() < 4) {
        badFaces.add(face);
        continue;
      }
      if (arcs.size() > 8) {
        badFaces.add(face);
        continue;
      }

      // a separator shouldn't have buildings inside its geometry
      for (IBuilding building : CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings()) {
        if (face.getGeom().contains(building.getGeom())) {
          badFaces.add(face);
          break;
        }
      }

    }

    return badFaces;

  }

  /**
   * Method that detects the remaining little separators based on continuity
   * with already detected separators
   */

  private List<Face> detectNeighbourLittleSeparators(List<Face> separators,
      List<Face> allFaces) {

    List<Face> neigbourLittleFaces = new ArrayList<Face>();
    int i = 0;
    int nbSect;
    List<Face> sep = new ArrayList<Face>();
    ArrayList<IRoadLine> sectCommunes = new ArrayList<IRoadLine>();

    for (i = 0; i < 2; i++) {
      // boucle sur les faces des separateurs
      for (Face face : separators) {

        List<Arc> arcsSep = new ArrayList<Arc>();
        arcsSep.addAll(face.getArcsDirects());
        arcsSep.addAll(face.getArcsIndirects());

        // boucle sur les faces non selectionnees
        for (Face myFace : allFaces) {
          nbSect = 0;
          // s'il s'agit d'une petite face qui intersecte une face déjà détectée
          if (myFace.getGeom().area() < 2500
              && separators.contains(myFace) == false
              && sep.contains(myFace) == false
              && face.getGeom().buffer(0.1)
                  .intersects(myFace.getGeom().buffer(0.1)) == true) {
            List<Arc> arcs = new ArrayList<Arc>();
            arcs.addAll(myFace.getArcsDirects());
            arcs.addAll(myFace.getArcsIndirects());
            sectCommunes.clear();

            // on parcourt les sections de ces petites faces
            for (Arc arc : arcs) {

              IFeature obj = arc.getCorrespondant(0);
              if (obj instanceof IRoadLine) {
                IRoadLine sect = (IRoadLine) obj;

                // s'il s'agit d'un troncon d'autoroute qui intersecte le
                // separateur(Face)
                if (sect.getImportance() == 4
                    && sect.getGeom().buffer(0.1)
                        .intersects(face.getGeom().buffer(0.1))) {
                  for (Arc arcSep : arcsSep) {
                    IFeature objSep = arcSep.getCorrespondant(0);
                    if (objSep instanceof IRoadLine) {
                      IRoadLine sectSep = (IRoadLine) objSep;
                      if (sectSep.getGeom().equals(sect.getGeom()) == true) {
                        sectCommunes.add(sect);
                        nbSect++;
                      }
                    }
                  }
                }
              }
            }
            // si les deux tronçons en commun ne s'intersectent pas
            if (nbSect == 2
                && sectCommunes.get(0).getGeom().buffer(0.1)
                    .intersects(sectCommunes.get(1).getGeom().buffer(0.1)) == false) {
              sep.add(myFace);
            }
          }
        }
      }

      neigbourLittleFaces.addAll(sep);
      sep.clear();
    }

    return neigbourLittleFaces;

  }

  // ///////////////////////////////////
  // DETECTION OF THE INTERCHANGES
  // ///////////////////////////////////
  public Collection<IPolygon> detectInterchanges() {
    // initialisation
    Collection<IPolygon> interchangeExtents = new HashSet<>();
    CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();

    // enrich the network if necessary
    NetworkEnrichment.buildTopology(dataset, dataset.getRoadNetwork(), false);
    Set<TronconDeRoute> roads = new HashSet<TronconDeRoute>();
    for (IRoadLine feat : dataset.getRoads()) {
      roads.add((TronconDeRoute) feat.getGeoxObj());
    }

    // map the NoeudReseau instances to the IRoadNode instances of the network
    Map<NoeudReseau, INetworkNode> nodesMap = new HashMap<>();
    for (INetworkNode node : dataset.getRoadNetwork().getNodes()) {
      nodesMap.put((NoeudReseau) node.getGeoxObj(), node);
    }

    // classify the simple crossroads
    CrossRoadDetection algo = new CrossRoadDetection();
    Set<SimpleCrossRoad> simples = algo.classifyCrossRoads(roads);
    // filter to keep only Y and Fork nodes
    IFeatureCollection<SimpleCrossRoad> crossroads = new FT_FeatureCollection<>();
    for (SimpleCrossRoad simple : simples) {
      if (simple instanceof ForkCrossRoad)
        crossroads.add(simple);
      if (simple instanceof YCrossRoad)
        crossroads.add(simple);
    }

    // build a graph from the network
    WeightedPseudograph<INetworkNode, DefaultWeightedEdge> graph = GraphFactory
        .buildGraphFromNetwork(dataset.getRoadNetwork(),
            new MetricalGraphWeighter());

    // cluster the simple crossroads based on network distance
    Set<Set<SimpleCrossRoad>> clusters = new HashSet<Set<SimpleCrossRoad>>();
    Stack<SimpleCrossRoad> stack = new Stack<>();
    stack.addAll(crossroads);
    while (!stack.empty()) {
      Set<SimpleCrossRoad> cluster = new HashSet<>();
      SimpleCrossRoad feature = stack.pop();
      Stack<SimpleCrossRoad> stack2 = new Stack<>();
      stack2.add(feature);
      while (!stack2.empty()) {
        SimpleCrossRoad feat = stack2.pop();
        cluster.add(feat);
        // get the features closer than a distance
        Collection<SimpleCrossRoad> closeColn = crossroads.select(feat
            .getGeom().centroid(), distMaxClustering);

        // now filter closeColn by network distance
        // the shortest path should also be less than distMaxClustering
        Collection<SimpleCrossRoad> toAdd = new HashSet<>();
        for (SimpleCrossRoad simple : closeColn) {
          if (cluster.contains(simple))
            continue;
          if (simple.getCoord().distance2D(feature.getCoord()) < euclMaxDist) {
            toAdd.add(simple);
            continue;
          }
          // compute the shortest path between feature and simple
          DijkstraShortestPath<INetworkNode, DefaultWeightedEdge> shortest = new DijkstraShortestPath<>(
              graph, nodesMap.get(feature.getNode()), nodesMap.get(simple
                  .getNode()));

          if (shortest.getPathLength() < distMaxClustering)
            toAdd.add(simple);
        }
        closeColn.removeAll(stack2);
        closeColn.removeAll(cluster);
        stack2.addAll(toAdd);
      }
      if (cluster.size() >= this.clusterMinSize)
        clusters.add(cluster);
      stack.removeAll(cluster);
    }

    // filter the clusters to only keep the interchanges
    clusters = filterInterchangeClusters(clusters);

    // reshape the clusters to exclude the crossroads that do not belong to the
    // interchange
    clusters = reshapeInterchangeClusters(clusters);

    // compute the extent of each cluster
    for (Set<SimpleCrossRoad> cluster : clusters) {
      IMultiPoint multi = GeometryEngine.getFactory().createMultiPoint();
      for (SimpleCrossRoad simple : cluster)
        multi.add((IPoint) simple.getGeom());
      interchangeExtents.add((IPolygon) multi.convexHull().buffer(5.0));
    }

    return interchangeExtents;
  }

  private Set<Set<SimpleCrossRoad>> filterInterchangeClusters(
      Set<Set<SimpleCrossRoad>> clusters) {
    Set<Set<SimpleCrossRoad>> realClusters = new HashSet<>();
    // TODO
    return realClusters;
  }

  private Set<Set<SimpleCrossRoad>> reshapeInterchangeClusters(
      Set<Set<SimpleCrossRoad>> clusters) {
    Set<Set<SimpleCrossRoad>> reshapedClusters = new HashSet<>();
    // TODO
    return reshapedClusters;
  }

  // ///////////////////////////////////
  // DETECTION OF THE REST AREAS
  // ///////////////////////////////////
}
