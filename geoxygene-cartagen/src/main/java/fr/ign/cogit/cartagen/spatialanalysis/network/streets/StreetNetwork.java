/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.streets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.jfree.data.statistics.Statistics;

import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.graph.DualGraph;
import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.Compactness;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;

public class StreetNetwork extends AbstractFeature {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  public static String ROAD_TRAFFIC_ATTRIBUTE = "";
  private static Logger logger = Logger
      .getLogger(StreetNetwork.class.getName());
  private static double sMinT = 3250.0;// 1 mm * 1.3 mm map at 1:50 000.
  private static AtomicInteger counter = new AtomicInteger();
  private static final double CITY_MEDIUM_SIZE = 5000000.0;
  private static final double CITY_LARGE_SIZE = 10000000.0;

  // Public fields //
  public enum CitySize {
    SMALL, MEDIUM, LARGE
  }

  // Protected fields //

  // Package visible fields //
  // criteria used for block aggregation
  StreetNetworkCriterionSet criteria = new StreetNetworkCriterionSet();

  // Private fields //
  private int id;
  private IPolygon geom;
  private CitySize size;
  private Graph dualGraph;
  private double maxArea, maxCost, meanStroke, cityDensity, meanTraffic;
  private double meanDegree, meanProxi, meanBetween, maxBuildingDensity,
      deadEndLength;
  private double buildingMinSize, sMinD, meanArea, medArea;
  private int importanceThreshold;

  // objects contained in the city
  private Set<IUrbanBlock> cityBlocks;
  private Set<CityPartition> cityParts;
  private Set<CityAxis> cityAxes;
  private Collection<IRoundAbout> roundabouts;
  private Collection<IBranchingCrossroad> branchings;
  private Set<IRoadStroke> strokes;
  private Set<IRoadLine> roads;
  private Set<Ilot> geoxBlocks;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  /**
   * Constructor from the outline of the city being the street network, the
   * criteria for aggregation, some parameters of the algorithm and all the
   * feature collections needed (roads, strokes, roundabouts and branching
   * crossroads and blocks).
   * 
   * @param geom the outline of the street network (of the city)
   * @param roads all the roads of the data being generalised
   * @param strokes all the strokes of the data being generalised
   * @param roundabouts all the roundabouts of the data being generalised
   * @param branchings all the branchings of the data being generalised
   * @param blocks all the blocks of the data being generalised
   * @param criteria an object containing the criteria used (each being true or
   *          false)
   */
  public StreetNetwork(IPolygon geom, IFeatureCollection<IRoadLine> roads,
      IFeatureCollection<IRoadStroke> strokes,
      IFeatureCollection<IRoundAbout> roundabouts,
      IFeatureCollection<IBranchingCrossroad> branchings,
      IFeatureCollection<Ilot> blocks, StreetNetworkCriterionSet criteria) {
    this.id = StreetNetwork.counter.getAndIncrement();
    this.geom = geom;
    this.cityBlocks = new HashSet<IUrbanBlock>();
    this.cityParts = new HashSet<CityPartition>();
    this.cityAxes = new HashSet<CityAxis>();
    this.strokes = new HashSet<IRoadStroke>();
    this.strokes.addAll(strokes.select(geom));
    this.roads = new HashSet<IRoadLine>();
    this.roads.addAll(roads.select(geom));
    this.geoxBlocks = new HashSet<Ilot>();
    this.geoxBlocks.addAll(blocks.select(geom));
    this.roundabouts = roundabouts.select(geom);
    this.branchings = branchings.select(geom);
    this.importanceThreshold = StreetNetworkParameters.importanceThreshold;
    this.buildingMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Math.pow(Legend.getSYMBOLISATI0N_SCALE(), 2.0) / 1000000.0;
    // compute city size
    if (geom.area() > StreetNetwork.CITY_LARGE_SIZE) {
      this.size = CitySize.LARGE;
    } else if (geom.area() > StreetNetwork.CITY_MEDIUM_SIZE) {
      this.size = CitySize.MEDIUM;
    } else {
      this.size = CitySize.SMALL;
    }

    this.criteria = criteria;

    // if the city is small, build its dual graph
    if (this.size.equals(CitySize.SMALL) && criteria.centrCrit) {
      this.dualGraph = new DualGraph("city_" + this.id, false,
          this.getNotDeadEndStrokes());
      this.dualGraph.computeCentralities();
    }

    // build the city blocks
    this.buildCityBlocks(this.geoxBlocks);

    // compute the external attributes of the street network
    this.computeExternalAttributes(StreetNetworkParameters.costLarge,
        StreetNetworkParameters.costSmall, StreetNetworkParameters.costMed,
        StreetNetworkParameters.surfLarge, StreetNetworkParameters.surfSmall,
        StreetNetworkParameters.surfMed);

    // then build the city axes
    this.buildCityAxes();

    // then build the city partitions
    this.buildCityPartitions();

    // checks that JTS did succeed in partitioning
    for (IUrbanBlock b : this.cityBlocks) {
      if (b.getPartition() != null) {
        continue;
      }
      IPolygon poly = b.getGeom();
      CityPartition nearest = null;
      double distMin = 100.0;
      for (CityPartition p : this.cityParts) {
        double dist = poly.distance(p.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearest = p;
        }
      }
      b.setPartition(nearest);
    }

    // cut the blocks at the city limit
    this.cutBlocksAtCityLimit();

    // build the blocks neighbourhood
    this.buildBlockNeighbourhood();
    this.computeHoleBlocks();

    if (criteria.centrCrit && !this.size.equals(CitySize.SMALL)) {
      this.buildGraphInPartitions();
    }

    // finally compute the internal attributes of the street network
    this.computeAttributes();
  }

  /**
   * Constructor from the outline of the city being the street network, the
   * criteria for aggregation, some parameters of the algorithm and all the
   * feature collections needed (roads, strokes, roundabouts and branching
   * crossroads and blocks).
   * 
   * @param geom the outline of the street network (of the city)
   * @param roads all the roads of the data being generalised
   * @param strokes all the strokes of the data being generalised
   * @param roundabouts all the roundabouts of the data being generalised
   * @param branchings all the branchings of the data being generalised
   * @param blocks all the blocks of the data being generalised
   * @param criteria an object containing the criteria used (each being true or
   *          false)
   */
  public StreetNetwork(IPolygon geom, IFeatureCollection<IRoadLine> roads,
      IFeatureCollection<IRoadStroke> strokes,
      IFeatureCollection<IRoundAbout> roundabouts,
      IFeatureCollection<IBranchingCrossroad> branchings,
      IFeatureCollection<IUrbanBlock> blocks) {
    this.id = StreetNetwork.counter.getAndIncrement();
    this.geom = geom;
    this.cityBlocks = new HashSet<IUrbanBlock>();
    this.cityParts = new HashSet<CityPartition>();
    this.cityAxes = new HashSet<CityAxis>();
    this.strokes = new HashSet<IRoadStroke>();
    this.strokes.addAll(strokes.select(geom));
    this.roads = new HashSet<IRoadLine>();
    this.roads.addAll(roads.select(geom));
    this.geoxBlocks = new HashSet<Ilot>();
    for (IUrbanBlock b : blocks.select(geom)) {
      this.geoxBlocks.add((Ilot) b.getGeoxObj());
    }
    this.roundabouts = roundabouts.select(geom);
    this.branchings = branchings.select(geom);
    this.importanceThreshold = StreetNetworkParameters.importanceThreshold;
    this.buildingMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Math.pow(Legend.getSYMBOLISATI0N_SCALE(), 2.0) / 1000000.0;
    // compute city size
    if (geom.area() > StreetNetwork.CITY_LARGE_SIZE) {
      this.size = CitySize.LARGE;
    } else if (geom.area() > StreetNetwork.CITY_MEDIUM_SIZE) {
      this.size = CitySize.MEDIUM;
    } else {
      this.size = CitySize.SMALL;
    }

    this.criteria = new StreetNetworkCriterionSet();

    // if the city is small, build its dual graph
    if (this.size.equals(CitySize.SMALL) && this.criteria.centrCrit) {
      this.dualGraph = new DualGraph("city_" + this.id, false,
          this.getNotDeadEndStrokes());
      this.dualGraph.computeCentralities();
    }

    // build the city blocks
    this.buildCityBlocks(this.geoxBlocks);

    // compute the external attributes of the street network
    this.computeExternalAttributes(StreetNetworkParameters.costLarge,
        StreetNetworkParameters.costSmall, StreetNetworkParameters.costMed,
        StreetNetworkParameters.surfLarge, StreetNetworkParameters.surfSmall,
        StreetNetworkParameters.surfMed);

    // then build the city axes
    this.buildCityAxes();

    // then build the city partitions
    this.buildCityPartitions();

    // checks that JTS did succeed in partitioning
    for (IUrbanBlock b : this.cityBlocks) {
      if (b.getPartition() != null) {
        continue;
      }
      IPolygon poly = b.getGeom();
      CityPartition nearest = null;
      double distMin = 100.0;
      for (CityPartition p : this.cityParts) {
        double dist = poly.distance(p.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearest = p;
        }
      }
      b.setPartition(nearest);
    }

    // cut the blocks at the city limit
    this.cutBlocksAtCityLimit();

    // build the blocks neighbourhood
    this.buildBlockNeighbourhood();
    this.computeHoleBlocks();

    if (this.criteria.centrCrit && !this.size.equals(CitySize.SMALL)) {
      this.buildGraphInPartitions();
    }

    // finally compute the internal attributes of the street network
    this.computeAttributes();
  }

  public StreetNetwork(IPolygon geom, IPopulation<IRoadLine> roads,
      IPopulation<IRoadStroke> strokes, IPopulation<IRoundAbout> rounds,
      IPopulation<IBranchingCrossroad> branchings, Population<Ilot> blocks,
      StreetNetworkCriterionSet criteria) {
    this.id = StreetNetwork.counter.getAndIncrement();
    this.geom = geom;
    this.cityBlocks = new HashSet<IUrbanBlock>();
    this.cityParts = new HashSet<CityPartition>();
    this.cityAxes = new HashSet<CityAxis>();
    this.strokes = new HashSet<IRoadStroke>();
    this.strokes.addAll(strokes.select(geom));
    this.roads = new HashSet<IRoadLine>();
    this.roads.addAll(roads.select(geom));
    this.geoxBlocks = new HashSet<Ilot>();
    this.geoxBlocks.addAll(blocks.select(geom));
    this.roundabouts = rounds.select(geom);
    this.branchings = branchings.select(geom);
    this.importanceThreshold = StreetNetworkParameters.importanceThreshold;
    this.buildingMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Math.pow(Legend.getSYMBOLISATI0N_SCALE(), 2.0) / 1000000.0;
    // compute city size
    if (geom.area() > StreetNetwork.CITY_LARGE_SIZE) {
      this.size = CitySize.LARGE;
    } else if (geom.area() > StreetNetwork.CITY_MEDIUM_SIZE) {
      this.size = CitySize.MEDIUM;
    } else {
      this.size = CitySize.SMALL;
    }

    this.criteria = criteria;

    // if the city is small, build its dual graph
    if (this.size.equals(CitySize.SMALL) && criteria.centrCrit) {
      this.dualGraph = new DualGraph("city_" + this.id, false,
          this.getNotDeadEndStrokes());
      this.dualGraph.computeCentralities();
    }

    // build the city blocks
    this.buildCityBlocks(this.geoxBlocks);

    // compute the external attributes of the street network
    this.computeExternalAttributes(StreetNetworkParameters.costLarge,
        StreetNetworkParameters.costSmall, StreetNetworkParameters.costMed,
        StreetNetworkParameters.surfLarge, StreetNetworkParameters.surfSmall,
        StreetNetworkParameters.surfMed);

    // then build the city axes
    this.buildCityAxes();

    // then build the city partitions
    this.buildCityPartitions();

    // checks that JTS did succeed in partitioning
    for (IUrbanBlock b : this.cityBlocks) {
      if (b.getPartition() != null) {
        continue;
      }
      IPolygon poly = b.getGeom();
      CityPartition nearest = null;
      double distMin = 100.0;
      for (CityPartition p : this.cityParts) {
        double dist = poly.distance(p.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearest = p;
        }
      }
      b.setPartition(nearest);
    }

    // cut the blocks at the city limit
    this.cutBlocksAtCityLimit();

    // build the blocks neighbourhood
    this.buildBlockNeighbourhood();
    this.computeHoleBlocks();

    if (criteria.centrCrit && !this.size.equals(CitySize.SMALL)) {
      this.buildGraphInPartitions();
    }

    // finally compute the internal attributes of the street network
    this.computeAttributes();
  }

  // Getters and setters //
  public CitySize getSize() {
    return this.size;
  }

  public void setSize(CitySize size) {
    this.size = size;
  }

  public double getMaxArea() {
    return this.maxArea;
  }

  public void setMaxArea(double maxArea) {
    this.maxArea = maxArea;
  }

  public double getMaxCost() {
    return this.maxCost;
  }

  public void setMaxCost(double maxCost) {
    this.maxCost = maxCost;
  }

  public double getMeanStroke() {
    return this.meanStroke;
  }

  public void setMeanStroke(double meanStroke) {
    this.meanStroke = meanStroke;
  }

  public double getCityDensity() {
    return this.cityDensity;
  }

  public void setCityDensity(double cityDensity) {
    this.cityDensity = cityDensity;
  }

  public double getMeanTraffic() {
    return this.meanTraffic;
  }

  public void setMeanTraffic(double meanTraffic) {
    this.meanTraffic = meanTraffic;
  }

  public double getMeanDegree() {
    return this.meanDegree;
  }

  public void setMeanDegree(double meanDegree) {
    this.meanDegree = meanDegree;
  }

  public double getMeanProxi() {
    return this.meanProxi;
  }

  public void setMeanProxi(double meanProxi) {
    this.meanProxi = meanProxi;
  }

  public double getMeanBetween() {
    return this.meanBetween;
  }

  public void setMeanBetween(double meanBetween) {
    this.meanBetween = meanBetween;
  }

  public double getMaxBuildingDensity() {
    return this.maxBuildingDensity;
  }

  public void setMaxBuildingDensity(double maxBuildingDensity) {
    this.maxBuildingDensity = maxBuildingDensity;
  }

  public double getDeadEndLength() {
    return this.deadEndLength;
  }

  public void setDeadEndLength(double deadEndLength) {
    this.deadEndLength = deadEndLength;
  }

  public double getBuildingMinSize() {
    return this.buildingMinSize;
  }

  public void setBuildingMinSize(double buildingMinSize) {
    this.buildingMinSize = buildingMinSize;
  }

  public double getSMinD() {
    return this.sMinD;
  }

  public void setSMinD(double minD) {
    this.sMinD = minD;
  }

  public Set<IUrbanBlock> getCityBlocks() {
    return this.cityBlocks;
  }

  public void setCityBlocks(Set<IUrbanBlock> blocks) {
    this.cityBlocks = blocks;
  }

  public Set<CityPartition> getCityParts() {
    return this.cityParts;
  }

  public void setCityParts(HashSet<CityPartition> cityParts) {
    this.cityParts = cityParts;
  }

  public Set<CityAxis> getCityAxes() {
    return this.cityAxes;
  }

  public void setCityAxes(HashSet<CityAxis> cityAxes) {
    this.cityAxes = cityAxes;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public IPolygon getGeom() {
    return this.geom;
  }

  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }

  public void setDualGraph(Graph dualGraph) {
    this.dualGraph = dualGraph;
  }

  public Graph getDualGraph() {
    return this.dualGraph;
  }

  public void setRoundabouts(Collection<IRoundAbout> roundabouts) {
    this.roundabouts = roundabouts;
  }

  public Collection<IRoundAbout> getRoundabouts() {
    return this.roundabouts;
  }

  public void setBranchings(Collection<IBranchingCrossroad> branchings) {
    this.branchings = branchings;
  }

  public Collection<IBranchingCrossroad> getBranchings() {
    return this.branchings;
  }

  public void setMeanArea(double meanArea) {
    this.meanArea = meanArea;
  }

  public double getMeanArea() {
    return this.meanArea;
  }

  public void setMedArea(double medArea) {
    this.medArea = medArea;
  }

  public double getMedArea() {
    return this.medArea;
  }

  // Other public methods //
  public IFeatureCollection<IRoadLine> getRoads() {
    IFeatureCollection<IRoadLine> ftroads = new FT_FeatureCollection<IRoadLine>();
    ftroads.addAll(this.roads);
    return ftroads;
  }

  public IFeatureCollection<IRoadStroke> getStrokes() {
    IFeatureCollection<IRoadStroke> ftstrokes = new FT_FeatureCollection<IRoadStroke>();
    ftstrokes.addAll(this.strokes);
    return ftstrokes;
  }

  public Set<IRoadStroke> getStrokesSet() {
    return this.strokes;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    StreetNetwork other = (StreetNetwork) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.geom == null) {
      if (other.geom != null) {
        return false;
      }
    } else if (!this.geom.equals(other.geom)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "StreetNetwork (" + this.id + ")";
  }

  /**
   * Get the city strokes that are not completely dead ends (at least one stroke
   * feature is not a deadEnd).
   * 
   * @return
   * @author GTouya
   */
  public HashSet<IRoadStroke> getNotDeadEndStrokes() {
    HashSet<IRoadStroke> ndeStrokes = new HashSet<IRoadStroke>();
    for (IRoadStroke s : this.strokes) {
      // test if all the strokes roads are dead ends
      boolean deadEnd = true;
      for (ArcReseau feat : s.getFeatures()) {
        // get the RoadLine related to this GeOx object
        IRoadLine road = null;
        for (IRoadLine r : this.getRoads()) {
          if (r.getGeoxObj().equals(feat)) {
            road = r;
            if (!road.isDeadEnd()) {
              deadEnd = false;
            }
            break;
          }
        }
      }
      if (deadEnd) {
        continue;
      }
      ndeStrokes.add(s);
    }
    return ndeStrokes;
  }

  /**
   * Set the criteria used in the aggregation cost function.
   * 
   * @param compact true to use the block compactness criterion
   * @param area true to use the block size criterion
   * @param strokes true to use the strokes length criterion
   * @param traffic true to use the traffic estimation criterion
   * @param centrality true to use the stroke dual centrality criterion
   * @param building true to use the building density criterion
   * @param density true to use the block density criterion
   * @param crossing true to use the partition crossing criterion
   * @author GTouya
   */
  public void setAggregationCriteria(boolean compact, boolean area,
      boolean strokes, boolean traffic, boolean centrality, boolean building,
      boolean density, boolean crossing) {
    this.criteria = new StreetNetworkCriterionSet(compact, area, strokes,
        traffic, centrality, building, density, crossing);
  }

  /**
   * <p>
   * Core of the street selection algorithm by block aggregation : the city
   * block are aggregated according to t-Gap principle thanks to cost function.
   * This version of the algorithm uses the dynamics for Anne Ruas' PhD: only
   * one aggregation is allowed per city block.
   * 
   */
  public void limitedAggregationAlgorithm() {
    StreetNetwork.logger
        .config("Limited block aggregation in the street network");
    // first, get the city blocks to treat in the algorithm
    HashSet<IUrbanBlock> blocksToTreat;
    if (this.criteria.densBuildCrit) {
      blocksToTreat = this.getBlocksToTreatBuilding();
    } else {
      blocksToTreat = this.getBlocksToTreat();
    }
    HashSet<IUrbanBlock> disolvableBlocks = this.getDisolvableBlocks();
    StreetNetwork.logger.fine(blocksToTreat.size()
        + " blocks to treat for aggregation");

    // while blocks remain to treat, continue
    while (blocksToTreat.size() > 0) {
      // first, get the best city block to treat
      IUrbanBlock block = this.getBestBlockToTreat(blocksToTreat);
      StreetNetwork.logger.fine(block + " is the best block to treat");
      // if there is none, the algorithm is over, return.
      if (block == null) {
        return;
      }

      // get the best neighbour for aggregation
      BestNeighbourResult result = this.chooseBestNeighbour(block,
          disolvableBlocks);
      IUrbanBlock neigh = result.bestNeighbour;
      double neighCost = result.bestCost;
      StreetNetwork.logger.fine(neigh + " is the best neighbour with cost "
          + neighCost);

      // if cost is > than city maxCost, then the block cannot be aggregated
      // to its neighbour. Remove it from the blocks to treat.
      if (neighCost > this.maxCost) {
        blocksToTreat.remove(block);
        continue;
      }
      // Then, the two blocks can be aggregated
      // remove neigbour from the blocks to treat
      blocksToTreat.remove(neigh);
      disolvableBlocks.remove(neigh);

      // aggregate blocks
      IUrbanBlock newBlock = block.aggregateWithBlock(neigh);
      this.cityBlocks.remove(neigh);
      this.cityBlocks.remove(block);
      this.cityBlocks.add(newBlock);
      // the aggregation is limited, the new block is removed
      // from the blocks to treat
      blocksToTreat.remove(block);
      StreetNetwork.logger.info(block + " has been aggregated to " + neigh);
    }// while(blocksToTreat.size()>0)
  }

  /**
   * <p>
   * Core of the street selection algorithm by block aggregation : the city
   * block are aggregated according to t-Gap principle thanks to cost function.
   * This version of the algorithm uses the dynamics proposed by (Touya, 2007)
   * road selection algorithm: multiple aggregations are allowed as long as the
   * cost is less than max cost for the city.
   * 
   */
  public void aggregationAlgorithm() {

    // first, get the city blocks to treat in the algorithm
    HashSet<IUrbanBlock> blocksToTreat;
    if (this.criteria.densBuildCrit) {
      blocksToTreat = this.getBlocksToTreatBuilding();
    } else {
      blocksToTreat = this.getBlocksToTreat();
    }

    // while blocks remain to treat, continue
    while (blocksToTreat.size() > 0) {
      // first, get the best city block to treat
      IUrbanBlock block = this.getBestBlockToTreat(blocksToTreat);

      // if there is none, the algorithm is over, return.
      if (block == null) {
        return;
      }

      // get the best neighbour for aggregation
      BestNeighbourResult result = this.chooseBestNeighbour(block,
          blocksToTreat);
      IUrbanBlock neigh = result.bestNeighbour;
      double neighCost = result.bestCost;

      // if cost is > than city maxCost, then the block cannot be aggregated
      // to its neighbour. Remove it from the blocks to treat.
      if (neighCost > this.maxCost) {
        blocksToTreat.remove(block);
        continue;
      }

      // Then, the two blocks can be aggregated
      // remove neigbour from the blocks to treat
      blocksToTreat.remove(neigh);

      // aggregate blocks
      IUrbanBlock newBlock = block.aggregateWithBlock(neigh);
      this.cityBlocks.remove(neigh);
      this.cityBlocks.remove(block);
      this.cityBlocks.add(newBlock);
      StreetNetwork.logger.info(block + " has been aggregated to " + neigh);
    }// while(blocksToTreat.size()>0)
  }// agregationSituations()

  /**
   * Build the street network from city blocks in an empty StreetNetwork object.
   * @param blocks
   */
  public void buildNetworkFromCityBlocks(Set<IUrbanBlock> blocks) {
    // build the city blocks
    this.setCityBlocks(blocks);

    // compute the external attributes of the street network
    this.computeExternalAttributes(StreetNetworkParameters.costLarge,
        StreetNetworkParameters.costSmall, StreetNetworkParameters.costMed,
        StreetNetworkParameters.surfLarge, StreetNetworkParameters.surfSmall,
        StreetNetworkParameters.surfMed);

    // then build the city axes
    this.buildCityAxes();

    // then build the city partitions
    this.buildCityPartitions();

    // checks that JTS did succeed in partitioning
    for (IUrbanBlock b : this.cityBlocks) {
      if (b.getPartition() != null) {
        continue;
      }
      IPolygon poly = b.getGeom();
      CityPartition nearest = null;
      double distMin = 100.0;
      for (CityPartition p : this.cityParts) {
        double dist = poly.distance(p.getGeom());
        if (dist < distMin) {
          distMin = dist;
          nearest = p;
        }
      }
      b.setPartition(nearest);
    }

    // cut the blocks at the city limit
    this.cutBlocksAtCityLimit();

    // build the blocks neighbourhood
    this.buildBlockNeighbourhood();
    this.computeHoleBlocks();

    if (this.criteria.centrCrit && !this.size.equals(CitySize.SMALL)) {
      this.buildGraphInPartitions();
    }

    // finally compute the internal attributes of the street network
    this.computeAttributes();
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

  /**
   * <p>
   * Build the cityBlocks of the street network from the road blocks inside the
   * network area.
   * 
   */
  private void buildCityBlocks(Set<Ilot> roadBlocks) {
    this.cityBlocks = new HashSet<IUrbanBlock>();
    CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    // loop on the road blocks
    for (Ilot block : roadBlocks) {
      // get the urban elements inside the block
      Set<IUrbanElement> buildings = new HashSet<IUrbanElement>();
      buildings.addAll(dataSet.getBuildings().select(block.getGeom()));
      // get the roads surrounding the block
      Set<IRoadLine> blockRoads = new HashSet<IRoadLine>();
      for (IFeature r : dataSet.getPopulation(CartAGenDataSet.ROADS_POP)) {
        if (!(r instanceof IRoadLine)) {
          continue;
        }
        if (JTSAlgorithms.coversPredicate(this.geom, r.getGeom())) {
          blockRoads.add((IRoadLine) r);
        }
      }
      // builds the new city block without partition for now on
      this.cityBlocks.add(new UrbanBlock(block, null, this, buildings,
          blockRoads));
    }// while boucle sur setIlots
  }

  /**
   * Build the neighbourhood information of the city blocks thanks to a
   * topological map of the blocks.
   * 
   * @author GTouya
   */
  private void buildBlockNeighbourhood() {
    // build a topological map of the blocks
    CarteTopo topoBlock = new CarteTopo("city_blocks");
    topoBlock.importClasseGeo(new FT_FeatureCollection<IUrbanBlock>(
        this.cityBlocks));
    topoBlock.ajouteArcsEtNoeudsAuxFaces(true);
    // loop on the blocks
    for (IUrbanBlock block : this.cityBlocks) {
      // get the topological primitives of the block
      for (IFeature prim : block.getCorrespondants(topoBlock.getPopFaces())) {
        Face face = (Face) prim;
        for (Face voisin : face.voisins()) {
          block.getNeighbours().add((IUrbanBlock) voisin.getCorrespondant(0));
        }
      }
    }// while, boucle sur les situations du réseau

  }

  /**
   * Compute the attributes of the city that depend on the blocks.
   * 
   * @author Guillaume
   */
  private void computeAttributes() {
    // *****************************************
    // compute the density of blocks
    // *****************************************
    this.cityDensity = this.getCityBlocks().size() / this.getGeom().area();

    // *********************************************
    // compute the maximum building density of city blocks
    // *********************************************
    this.maxBuildingDensity = 0.0;
    double totalArea = 0.0;
    for (IUrbanBlock block : this.getCityBlocks()) {
      if (block.getDensity() > this.maxBuildingDensity) {
        this.maxBuildingDensity = block.getDensity();
      }
      totalArea += block.getGeom().area();
    }
    this.meanArea = totalArea / this.getCityBlocks().size();

    // ***********************************************************
    // compute the building density distribution
    // ***********************************************************
    ArrayList<IUrbanBlock> list = new ArrayList<IUrbanBlock>();
    for (IUrbanBlock block : this.getStandardBlocks()) {
      list.add(block);
    }
    DensityComparator compDens = new DensityComparator();
    AreaComparator compSurf = new AreaComparator();
    Collections.sort(list, compDens);
    int index = 0;
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        IUrbanBlock block = list.get(i);
        if (block.getDensity() > 0.8) {
          index = i;
          break;
        }
      }
      int nbSitDens = list.size() - index;
      if (index == 0) {
        return;
      }
      Collections.sort(list, compSurf);
      this.sMinD = list.get(nbSitDens).getGeom().area();
    }
  }

  /**
   * <p>
   * Instanciate the parameters of street selection on this street network.
   * 
   */
  private void computeExternalAttributes(double costLarge, double costSmall,
      double costMed, double surfLarge, double surfSmall, double surfMed) {
    // *********************************************************
    // first compute strokes mean length
    // *********************************************************
    Set<IRoadStroke> localStrokes = this.getStrokesSet();

    // get a list of strokes lengths
    ArrayList<Double> lengths = new ArrayList<Double>();
    for (IRoadStroke str : localStrokes) {
      lengths.add(new Double(str.getGeomStroke().length()));
    }

    // compute list mean
    this.meanStroke = Statistics.calculateMean(lengths);

    // ********************************************************
    // now computes mean centralities
    // ********************************************************
    // test if centralities are to be used
    if (this.criteria.centrCrit && (this.size.equals(CitySize.SMALL))) {
      double sommeDegre = 0.0;
      double sommeProxi = 0.0;
      double sommeInter = 0.0;
      HashSet<IRoadStroke> strokesNotDead = this.getNotDeadEndStrokes();
      for (IRoadStroke str : strokesNotDead) {
        // get the associated dual node
        INode node = this.getDualGraph().getNodeFromGeoObj(str);

        sommeDegre = sommeDegre + node.getDegree();
        sommeProxi = sommeProxi + node.getProximityCentrality();
        sommeInter = sommeInter + node.getBetweenCentrality();
      }
      // compute the means
      this.meanDegree = sommeDegre
          / (new Double(localStrokes.size())).doubleValue();
      this.meanProxi = sommeProxi
          / (new Double(localStrokes.size())).doubleValue();
      this.meanBetween = sommeInter
          / (new Double(localStrokes.size())).doubleValue();
    }

    // *******************************************************
    // now computes road traffic estimation
    // *******************************************************
    IFeatureCollection<IRoadLine> localRoads = this.getRoads();
    if (this.criteria.traffCrit) {
      ArrayList<Integer> traffics = new ArrayList<Integer>();
      for (IRoadLine road : localRoads) {
        traffics.add((Integer) road
            .getAttribute(StreetNetwork.ROAD_TRAFFIC_ATTRIBUTE));
      }
      // compute list mean
      this.meanTraffic = Statistics.calculateMean(traffics);
    } else {
      this.meanTraffic = 1.0;
    }

    // *******************************************************
    // then compute the maximum area and cost
    // *******************************************************
    if (this.size.equals(CitySize.LARGE)) {
      this.maxArea = surfLarge;
      this.maxCost = costLarge;
    } else if (this.size.equals(CitySize.MEDIUM)) {
      this.maxArea = surfMed;
      this.maxCost = costMed;
    } else {
      this.maxArea = surfSmall;
      this.maxCost = costSmall;
    }
    ArrayList<IUrbanBlock> list = new ArrayList<IUrbanBlock>();
    for (IUrbanBlock block : this.getStandardBlocks()) {
      list.add(block);
    }
    AreaComparator compSurf = new AreaComparator();
    Collections.sort(list, compSurf);
    if (list.size() == 0) {
      this.medArea = 0;
    } else if (list.size() == 1) {
      this.medArea = list.get(0).getGeom().area();
    } else {
      this.medArea = list.get((int) Math.round(list.size() / 2.0)).getGeom()
          .area();
    }
  }

  private Set<IUrbanBlock> getStandardBlocks() {
    HashSet<IUrbanBlock> set = new HashSet<IUrbanBlock>();
    for (IUrbanBlock block : this.getCityBlocks()) {
      if (block.isStandard()) {
        set.add(block);
      }
    }
    return set;
  }

  /**
   * <p>
   * Dans une ville, récupère les situations à traiter par agrégation. Celles
   * représentant une structure (un rond-point, une patte d'oie ou une voie à
   * chaussées séparées) ne sont pas traitées.
   * 
   */
  private HashSet<IUrbanBlock> getBlocksToTreat() {
    // initialisation
    HashSet<IUrbanBlock> treated = new HashSet<IUrbanBlock>();
    double threshold = this.maxArea;

    // loop on the city blocks
    for (IUrbanBlock block : this.cityBlocks) {
      // if area is bigger than threshold, the block is not kept
      if (block.getGeom().area() > threshold) {
        continue;
      }

      // if the block is already the aggregation of several small blocks, add it
      if (block.getInitialGeoxBlocks().size() > 1) {
        treated.add(block);
        continue;
        // else if there is no initial geox block, the city block has been
        // created by cutting the with the city limits: add it.
      } else if (block.getInitialGeoxBlocks().size() == 0) {
        treated.add(block);
        continue;
      }

      // test if the block is a road structure
      if (!block.isStandard()) {
        continue;
      }

      // arrived here, the block is treated
      treated.add(block);
    }

    return treated;
  }

  /**
   * <p>
   * Même chose que recupSitATraiter() mais prend aussi en compte la densité
   * simulée en bâtiment pour déterminer les situations à traiter. Le critère de
   * densité doit donc avoir été choisi. Utilise les seuils sMinT et sMinD issus
   * de la thèse d'Anne Ruas.
   * 
   */
  private HashSet<IUrbanBlock> getBlocksToTreatBuilding() {
    // initialisation
    HashSet<IUrbanBlock> treated = new HashSet<IUrbanBlock>();
    double seuil = Math.max(StreetNetwork.sMinT, this.sMinD);

    // loop on the city blocks
    for (IUrbanBlock block : this.cityBlocks) {

      // if the block is an edge block, continue
      if (block.isEdge()) {
        continue;
      }
      // the big blocks are not chosen for treatment
      if (block.getGeom().area() > seuil) {
        continue;
      }

      // test if the block is a road structure
      if (!block.isStandard()) {
        continue;
      }

      // if the block is very small, add it
      if (block.getGeom().area() < StreetNetwork.sMinT) {
        treated.add(block);
        continue;
      }
      // if the density is small, the block is not treated
      if ((block.getGeom().area() > StreetNetwork.sMinT)
          && (block.getSimulatedDensity() < 0.5)) {
        continue;
      }
      // more complex case
      if ((block.getSimulatedDensity() < 1)
          && (block.getGeom().area() > (2
              * Math.abs(seuil - StreetNetwork.sMinT)
              * block.getSimulatedDensity() + 2 * StreetNetwork.sMinT - seuil))) {
        continue;
      }

      // if the block is already the aggregation of several small blocks, add it
      if (block.getInitialGeoxBlocks().size() > 1) {
        treated.add(block);
        continue;
        // else if there is no initial geox block, the city block has been
        // created by cutting the with the city limits: add it.
      } else if (block.getInitialGeoxBlocks().size() == 0) {
        treated.add(block);
        continue;
      }

      // arrived here, the block is treated
      treated.add(block);
    }

    return treated;
  }

  /**
   * <p>
   * Dans une ville, récupère les situations auxquelles on peut agréger une
   * petite situation urbaine. Ce sont toutes les situations qui ne sont pas des
   * structures.
   * 
   */
  private HashSet<IUrbanBlock> getDisolvableBlocks() {
    // initialise
    HashSet<IUrbanBlock> treated = new HashSet<IUrbanBlock>();

    // loop on the city blocks
    for (IUrbanBlock block : this.cityBlocks) {

      // if the block is already the aggregation of several small blocks, add it
      if (block.getInitialGeoxBlocks() != null) {
        if (block.getInitialGeoxBlocks().size() > 1) {
          treated.add(block);
          continue;
          // else if there is no initial geox block, the city block has been
          // created by cutting the with the city limits: add it.
        } else if (block.getInitialGeoxBlocks().size() == 0) {
          treated.add(block);
          continue;
        }
      }

      // test if the block is a road structure
      if (!block.isStandard()) {
        continue;
      }

      // arrived here, the block is treated
      treated.add(block);
    }

    return treated;
  }

  /**
   * <p>
   * Pour une situation urbaine, détermine quel situation voisine (parmi celles
   * à traiter) a le meilleur coût d'agrégation. Renvoie un VR contenant dans la
   * valeur "meilleurVoisin" le GothicObject et dans la valeur "meilleurCout" le
   * coût de son agrégation avec situation.
   * 
   * @param block
   * @param disolvableBlocks
   * @return a BestNeighbourResult containing the best neighbour and its
   *         aggregation cost
   */
  private BestNeighbourResult chooseBestNeighbour(IUrbanBlock block,
      HashSet<IUrbanBlock> disolvableBlocks) {
    // initialise
    double minCost = 10000.0;
    IUrbanBlock bestNeighbour = null;

    // get the neighbour city blocks of block
    HashSet<IUrbanBlock> neighbourSet = new HashSet<IUrbanBlock>(
        block.getNeighbours());
    StreetNetwork.logger.finer(block + " has " + neighbourSet.size()
        + " neighbours");
    // filter to keep only the disolvable neighbours
    neighbourSet.retainAll(disolvableBlocks);
    StreetNetwork.logger.finer(neighbourSet.size()
        + " neighbours remain for aggregation");
    // get the City axes surrounding block
    HashSet<CityAxis> setAxes = new HashSet<CityAxis>(block.getAxes());

    // loop on the neighbour to compute each aggregation cost
    for (IUrbanBlock neigh : neighbourSet) {
      // check that the city partition is the same
      if (!neigh.getPartition().equals(block.getPartition())) {
        continue;
      }
      StreetNetwork.logger.finer("This neighbour " + neigh
          + " is in the same partition");
      // get the city axes of neighbour
      HashSet<CityAxis> neighAxes = new HashSet<CityAxis>(neigh.getAxes());
      // retain only common axes
      neighAxes.retainAll(setAxes);

      // check if some axes remain
      if (neighAxes.size() > 0) {
        // in this case, check if an axis is between the 2 blocks
        if (this.isAxisBetweenBlocks(block, neigh, neighAxes)) {
          continue;
        }
      }

      // compute the aggregation cost
      double cost = this.aggregationCostFunction(block, neigh);
      StreetNetwork.logger.finer("Aggregation cost: " + cost);
      if (cost <= minCost) {
        minCost = cost;
        bestNeighbour = neigh;
      }
    }

    BestNeighbourResult result = new BestNeighbourResult(bestNeighbour, minCost);
    return result;
  }

  /**
   * <p>
   * Compute the aggregation cost between 2 blocks. It takes into account the
   * criteria defined at the city level: they deal with the aggregated block
   * shape, and the saliency of the road that separates the two blocks.
   * 
   * @param block the block to be aggregated
   * @param neigh the neighbour block being aggregated
   * @return the cost to aggregate the two blocks according to the current
   *         criteria
   * @author GTouya
   */
  @SuppressWarnings("unchecked")
  private double aggregationCostFunction(IUrbanBlock block, IUrbanBlock neigh) {
    // initialise cost
    double cost = 0.0;

    // compute density coefficient for cost function
    double densCoef = 1.0;
    double areaThreshold = this.maxArea;
    if (this.criteria.densDiffCrit) {
      // relate maxAera to its partition density compared to city density
      areaThreshold = this.maxArea * this.cityDensity
          / block.getPartition().getDensity();
      densCoef = Math
          .sqrt(block.getPartition().getDensity() / this.cityDensity);
    }

    double densBatiments = this.maxBuildingDensity;
    if (this.criteria.densBuildCrit) {
      densBatiments = neigh.getDensity();
    }

    // the mean centralities are computed at partition level or
    // town level for small cities.
    double between = 0.0;
    double proximity = 0.0;
    double degree = 0.0;
    if (this.size.equals(CitySize.SMALL)) {
      between = this.meanBetween;
      proximity = this.meanProxi;
      degree = this.meanDegree;
    } else {
      between = block.getPartition().getMeanBetween();
      proximity = block.getPartition().getMeanProxi();
      degree = block.getPartition().getMeanDegree();
    }
    IGeometry newGeom = block.getGeom().union(neigh.getGeom());
    // compute the aggregated block geometry
    IPolygon newPoly = null;
    if (newGeom instanceof IPolygon) {
      newPoly = (IPolygon) newGeom;
    } else if (newGeom.isMultiSurface()) {
      // TODO not satisfying. Must never occurs, but occurs when we get very
      // small geometry in the union (bug jts ?)
      newPoly = ((IMultiSurface<IPolygon>) newGeom).get(0);
    }
    double newArea = newPoly.area();

    // compute the new block compactness (Miller's Index)
    double compactness = 0.0001;// mais le coefficient à 1
    if (this.criteria.compactCrit) {
      compactness = new Compactness(newPoly).getMillerIndex();
    }

    // Now get the common strokes and roads between the blocks
    // pour cela on combine les deux géométries
    HashSet<IRoadStroke> strokesSet = new HashSet<IRoadStroke>();
    HashSet<IRoadLine> localRoads = new HashSet<IRoadLine>();
    IGeometry geomLine = null;
    if (this.criteria.strokeCrit || this.criteria.centrCrit
        || this.criteria.traffCrit) {
      geomLine = block.getGeom().intersection(neigh.getGeom());
      // test if intersection worked
      if (geomLine instanceof IAggregate<?>) {
        for (Object g : ((IAggregate<?>) geomLine).getList()) {
          // among the strokes and roads, keep the ones that cross geomLine
          strokesSet.addAll(this.getStrokes().select((IGeometry) g));
          localRoads.addAll(this.getRoads().select((IGeometry) g));
        }
      } else {
        // among the strokes and roads, keep the ones that cross geomLine
        strokesSet.addAll(this.getStrokes().select(geomLine));
        localRoads.addAll(this.getRoads().select(geomLine));
      }
    }// if(strokeCrit||centrCrit||traffCrit)

    boolean crosses = false;
    ArrayList<Integer> degList = new ArrayList<Integer>();
    ArrayList<Double> proxiList = new ArrayList<Double>();
    ArrayList<Double> betwList = new ArrayList<Double>();
    if (this.criteria.centrCrit || this.criteria.strokeCrit
        || this.criteria.crossCrit) {

      // loop on the strokes to keep the ones between the 2 blocks
      for (IRoadStroke s : new HashSet<IRoadStroke>(strokesSet)) {
        // test the number of common vertices
        if (CommonAlgorithmsFromCartAGen.getNbCommonVertices(geomLine,
            s.getGeom()) <= 1) {
          strokesSet.remove(s);
          continue;
        }
        // now test if the stroke crosses the partition
        if (block.getPartition().isCrossedByStroke(s)) {
          crosses = true;
        }
        if (this.criteria.centrCrit) {
          // get the centrality values
          // first get the graph of the partition or city
          Graph graph = this.getDualGraph();
          if (!this.size.equals(CitySize.SMALL)) {
            graph = block.getPartition().getGraph();
          }

          // then get the dual node related to the stroke
          INode node = graph.getNodeFromGeoObj(s);
          // case of a stroke outside the city
          if (node == null) {
            degList.add(new Integer(1));
            proxiList.add(new Double(1.0));
            betwList.add(new Double(1.0));
          } else {
            degList.add(new Integer(node.getDegree()));
            proxiList.add(new Double(node.getProximityCentrality()));
            betwList.add(new Double(node.getBetweenCentrality()));
          }
        }
      }
    }// if(centrCrit||strokeCrit||crossCrit)

    // compute the mean of strokes lengths
    double strokesCoef = 1.0;
    if (this.criteria.strokeCrit) {
      // put the strokes lengths in a list and divide by the mean length
      ArrayList<Double> lengthList = new ArrayList<Double>();
      boolean touchLimit = false;
      for (IRoadStroke s : strokesSet) {
        lengthList
            .add(new Double(s.getGeomStroke().length() / this.meanStroke));
        if (s.getGeomStroke().crosses(this.getGeom().getExterior())) {
          touchLimit = true;
        }
      }
      // calculate list mean
      strokesCoef = Statistics.calculateMean(lengthList);
      // if one of the common strokes crosses the network limits, double the
      // coefficient
      if (touchLimit) {
        strokesCoef *= 2.0;
      }
    }// if(strokeCrit)

    // if one stroke crosses the partition and if criterion is selected,
    // update the coefficient.
    if (crosses && this.criteria.crossCrit) {
      strokesCoef *= 1.4;
    }

    double centrDeg = 0.0, centrProxi = 0.0, centrInter = 0.0;
    if (this.criteria.centrCrit) {
      // compute degree centrality mean
      centrDeg = Statistics.calculateMean(degList);
      // compute proximity centrality mean
      centrProxi = Statistics.calculateMean(proxiList);
      // compute betweenness centrality mean
      centrInter = Statistics.calculateMean(betwList);
    }

    // loop on the roads
    double traffic = this.meanTraffic - 1.0;// the coeff is 1
    if (this.criteria.traffCrit) {
      HashSet<IRoadLine> copySet = new HashSet<IRoadLine>(localRoads);
      for (IRoadLine r : copySet) {
        // test geomLine intersection type with r.getGeom()
        if (CommonAlgorithmsFromCartAGen.getNbCommonVertices(geomLine,
            r.getGeom()) <= 1) {
          localRoads.remove(r);
        }
      }

      // now compute the mean traffic estimation on the remaining roads
      ArrayList<Integer> list = new ArrayList<Integer>();
      for (IRoadLine r : localRoads) {
        list.add((Integer) r.getAttribute(StreetNetwork.ROAD_TRAFFIC_ATTRIBUTE));
      }
      traffic = Statistics.calculateMean(list);
    }

    // compute a compactness penalty : if aggregated block compactness is
    // less than both initial blocks, the cost is multiplied by 2.5
    // now compare the compactnesses
    double coefComp = 1.0;
    if (this.criteria.compactCrit) {
      double comp1 = new Compactness(block.getGeom()).getMillerIndex();
      double comp2 = new Compactness(neigh.getGeom()).getMillerIndex();

      if ((comp1 > compactness) && (comp2 > compactness)) {
        coefComp = 2.5;
      }
      if ((comp1 < compactness) && (comp2 < compactness)) {
        coefComp = 0.8;
      }
    }

    // ********************************
    // COMPUTE THE AGGREGATION COST
    // ********************************
    // the area is taken into account only if bigger than maxArea
    double areaCoef = 1.0;
    if (this.criteria.areaCrit) {
      if (newArea >= areaThreshold) {
        areaCoef = Math.sqrt(neigh.getGeom().area() / areaThreshold);
      }
    }

    // if block is very small, a 0.8 factor is applied to make aggregation
    // easier
    if (this.criteria.areaCrit) {
      if (areaThreshold / block.getGeom().area() > 20.0) {
        areaCoef *= 0.8;
      }
    }

    double coefDegree = 1.0;
    double coefProxi = 1.0;
    double coefInter = 1.0;
    if (this.criteria.centrCrit) {
      if (centrDeg != 0.0) {
        coefDegree = degree / centrDeg;
      }
      if (centrProxi != 0.0) {
        coefProxi = proximity / centrProxi;
      }
      if (centrInter != 0.0) {
        coefInter = Math.sqrt(between / centrInter);
      }
    }

    // the final formula to compute cost
    StreetNetwork.logger.finest("compactness: " + compactness);
    StreetNetwork.logger.finest("coefComp: " + coefComp);
    StreetNetwork.logger.finest("areaCoef: " + areaCoef);
    StreetNetwork.logger.finest("coefDegree: " + coefDegree);
    StreetNetwork.logger.finest("coefProxi: " + coefProxi);
    StreetNetwork.logger.finest("coefInter: " + coefInter);
    StreetNetwork.logger.finest("traffic: " + traffic);
    StreetNetwork.logger.finest("strokesCoef: " + strokesCoef);
    StreetNetwork.logger.finest("densCoef: " + densCoef);
    StreetNetwork.logger.finest("densBatiments: " + densBatiments);
    cost = (1.0001 - compactness) * (1.0001 - compactness)
        * (1.0001 - compactness) * coefComp * areaCoef
        * Math.sqrt((coefDegree + coefProxi + coefInter) / 3.0) * (traffic + 1)
        / this.meanTraffic * strokesCoef * densCoef * (0.1 + densBatiments);

    return cost;
  }

  /**
   * <p>
   * Determine for 2 neighbour blocks if one of the city axes really goes
   * between the blocks.
   * 
   */
  private boolean isAxisBetweenBlocks(IUrbanBlock block, IUrbanBlock neigh,
      HashSet<CityAxis> neighAxes) {
    IGeometry line = block.getGeom().intersection(neigh.getGeom());
    // loop on the axes
    for (CityAxis axis : neighAxes) {
      // count the points in common in line and axis geometry
      int n = CommonAlgorithmsFromCartAGen.getNbCommonVertices(line,
          axis.getGeom());
      if (line.coord().size() == 2 && n == 2) {
        return true;
      } else if (n < 3) {
        continue;
      }
    }
    return false;
  }

  /**
   * <p>
   * Choose the best block to try aggregation first among the blocks remaining
   * for aggregation. The best choice is the smallest block, weighted by
   * partition density in blocks related to city density in blocks.
   * 
   * @param blocksToTreat the set of remaining blocks for aggregation
   * @return the best city block for aggregation
   * @author GTouya
   */
  private IUrbanBlock getBestBlockToTreat(HashSet<IUrbanBlock> blocksToTreat) {
    // initialise
    double minArea = this.maxArea;
    IUrbanBlock best = null;

    // loop on the blocks to treat
    for (IUrbanBlock b : blocksToTreat) {
      // get the partition density
      double density = b.getPartition().getDensity();
      // weight the area by the density ratio
      double area = b.getGeom().area() * this.cityDensity / density;
      if (area <= minArea) {
        minArea = area;
        best = b;
      }
    }
    return best;
  }

  /**
   * Build a dual graph of the strokes contained in each partition of the street
   * network. The centralities are computed in each graph.
   * 
   * @author GTouya
   */
  private void buildGraphInPartitions() {
    for (CityPartition part : this.cityParts) {
      HashSet<IRoadStroke> strokesPart = new HashSet<IRoadStroke>();
      for (IRoadStroke stroke : this.getNotDeadEndStrokes()) {
        if (stroke.getGeomStroke().intersects(part.getGeom())) {
          strokesPart.add(stroke);
        }
      }
      part.setGraph(new DualGraph("city_" + this.id + "_part_" + part.getId(),
          false, strokesPart));
      part.getGraph().computeCentralities();
    }
  }

  /**
   * Cut blocks at the street network limit to keep only internal blocks.
   * 
   * @author GTouya
   */
  @SuppressWarnings("unchecked")
  private void cutBlocksAtCityLimit() {

    // loop on the city blocks
    for (IUrbanBlock block : new HashSet<IUrbanBlock>(this.cityBlocks)) {
      // test if the block intersects the street network exterior
      if (!block.getGeom().intersects(this.getGeom().exteriorLineString())) {
        continue;
      }

      // test if block area is bigger than network median
      if (block.getGeom().area() < this.medArea) {
        continue;
      }

      // the geometry is cut with the network polygon
      IGeometry cutGeom = block.getGeom().intersection(this.getGeom());
      block.setEdge(true);

      if (cutGeom instanceof GM_Aggregate) {
        continue;
      } else if (cutGeom instanceof IPolygon) { // test if it is a simple
                                                // geometry
        // update the block geometry
        block.updateGeom((IPolygon) cutGeom);
      } else {
        // it is a multisurface geometry
        IMultiSurface<IOrientableSurface> multiSurface = (IMultiSurface<IOrientableSurface>) cutGeom;
        // we build new blocks for each surface
        boolean first = true;
        for (int index = 0; index < multiSurface.size(); index++) {
          IPolygon newGeom = (IPolygon) multiSurface.get(index);
          if (first) {
            first = false;
            block.updateGeom(newGeom);

            // update partition
            if (!block.getPartition().getGeom().contains(newGeom)) {
              for (CityPartition part : this.cityParts) {
                if (part.getGeom().contains(newGeom)) {
                  block.getPartition().getBlocks().remove(block);
                  part.getBlocks().add(block);
                  block.setPartition(part);
                  break;
                }
              }
            }
            continue;
          }
          // build a new CityBlock
          CityPartition partition = block.getPartition();
          if (!partition.getGeom().contains(newGeom)) {
            for (CityPartition part : this.cityParts) {
              if (part.getGeom().contains(newGeom)) {
                partition = part;
                break;
              }
            }
          }
          // update block buildings
          Set<IUrbanElement> newBuilds = new HashSet<IUrbanElement>();
          for (IUrbanElement b : block.getUrbanElements()) {
            if (newGeom.contains(b.getGeom())) {
              newBuilds.add(b);
            }
          }
          // update block roads
          Set<IRoadLine> newRoads = new HashSet<IRoadLine>();
          for (INetworkSection r : block.getSurroundingNetwork()) {
            if (!(r instanceof IRoadLine)) {
              continue;
            }
            if (newGeom.intersects(r.getGeom())) {
              newRoads.add((IRoadLine) r);
            }
          }
          IUrbanBlock newBlock = new UrbanBlock(newGeom, partition, this,
              newBuilds, newRoads);
          newBlock.setEdge(true);
          this.cityBlocks.add(newBlock);
        }// for (int index = 0 ; index < multiSurface.size() ; index++)
      }// else of if(cutGeom instanceof IPolygon)
    }
  }

  /**
   * <p>
   * Build the city partitions of the street network. They are the city bounds
   * divided by the city axes. Each partition is characterised.
   * 
   */
  private void buildCityPartitions() {
    // first, build a topology graph with the city limits and the axes
    CarteTopo carteTopo = new CarteTopo("CityPartitions");
    carteTopo
        .importClasseGeo(new FT_FeatureCollection<CityAxis>(this.cityAxes));
    // create a new ArcReseau feature collection with a single feature made
    // with the exterior of the network geometry
    IFeatureCollection<ArcReseau> net = new FT_FeatureCollection<ArcReseau>();
    net.add(new ArcReseauImpl(null, true, this.getGeom().exteriorLineString()));
    carteTopo.importClasseGeo(net);
    carteTopo.creeNoeudsManquants(0.001);
    carteTopo.rendPlanaire(0.001);
    carteTopo.creeTopologieFaces();

    // loop on the faces of the topological graph
    for (Face face : carteTopo.getPopFaces()) {
      // test if the face is inside the city
      IPoint pt = new GM_Point(JTSAlgorithms.getInteriorPoint(face
          .getGeometrie()));
      if (!this.getGeom().contains(pt)) {
        continue;
      }

      // get the city blocks contained inside the partition
      IFeatureCollection<IUrbanBlock> blocks = new FT_FeatureCollection<IUrbanBlock>(
          this.cityBlocks);
      Collection<IUrbanBlock> interBlocks = blocks.select(face.getGeometrie());
      // remove the blocks that only touch the partition
      HashSet<IUrbanBlock> partBlocks = new HashSet<IUrbanBlock>();
      for (IUrbanBlock block : interBlocks) {
        IGeometry inter = block.getGeom().intersection(face.getGeometrie());
        if (inter instanceof IPolygon || inter instanceof IMultiSurface<?>) {
          partBlocks.add(block);
        }
      }

      // build the new CityPartition object
      CityPartition partition = new CityPartition(face.getGeometrie(), this,
          partBlocks);

      // update the link with the city blocks
      for (IUrbanBlock block : partBlocks) {
        block.setPartition(partition);
      }

      this.cityParts.add(partition);
    }

    // build the partitions neighbourhood
    IFeatureCollection<CityPartition> featColn = new FT_FeatureCollection<CityPartition>(
        this.cityParts);
    for (CityPartition part : this.cityParts) {
      // get the partitions that intersect this partition
      HashSet<CityPartition> neighs = new HashSet<CityPartition>();
      neighs.addAll(featColn.select(part.getGeom()));
      neighs.remove(part);
      part.setNeighbours(neighs);
    }
    // loop once again on the partition to disolve the too small ones into
    // bigger neighbours
    HashSet<CityPartition> disolved = new HashSet<CityPartition>();
    for (CityPartition part : this.cityParts) {
      // count the number of blocks in the partition: if the nb is < 3
      // and the partition is small, it has to be disolved
      if ((part.getBlocks().size() < 3) && (part.getArea() < this.maxArea)) {
        // look for the best neighbour to disolve
        for (CityPartition neigh : part.getNeighbours()) {
          if (disolved.contains(neigh)) {
            continue;
          }
          // test if this disolve is geometrically possible
          IGeometry inter = neigh.getGeom().union(part.getGeom());
          if (!(inter instanceof IPolygon)) {
            continue;
          }
          neigh.aggregateWithPartition(part);
          for (IUrbanBlock block : part.getBlocks()) {
            block.setPartition(neigh);
          }
          // add part to the disolved partitions
          disolved.add(part);
          break;
        }
      }// if((nbSit.intValue()<=2)&&(surf<surfMax))
    }

    // remove the aggregated partitions
    this.cityParts.removeAll(disolved);
    for (CityPartition part : disolved) {
      part.cleanPartition();
    }
  }

  /**
   * <p>
   * Marque les cas où des situations urbaines seraient des trous. Ces trous
   * seront généralisés différemment en tenant compte de la grappe d'impasses à
   * laquelle ils appartiennent.
   * 
   */
  private void computeHoleBlocks() {
    // loop on the city blocks
    for (IUrbanBlock block : new HashSet<IUrbanBlock>(this.cityBlocks)) {

      if (block.getNeighbours().size() != 1) {
        block.setHoleBlock(false);
        continue;
      }
      // now test if geom is included in neighbour geom
      IPolygon neighGeom = block.getNeighbours().iterator().next()
          .getCityBlockGeom();
      IPolygon neighNoHole = new GM_Polygon(neighGeom.exteriorLineString());
      if (block.getCityBlockGeom().within(neighNoHole)) {
        block.setHoleBlock(true);
        continue;
      }
      block.setHoleBlock(false);
    }
  }

  /**
   * Build the City Axes of the street network. A city axis is either a long
   * stroke (twice longer than network mean) or a RoadLine object whose
   * importance is bigger than the network threshold.
   * 
   * @author GTouya
   */
  private void buildCityAxes() {
    HashSet<IGeneObjLin> setObjLin = new HashSet<IGeneObjLin>();

    // add to this set the long strokes
    for (IRoadStroke stroke : this.getStrokes()) {
      if (stroke.getGeomStroke().length() > this.getMeanStroke() * 4) {
        setObjLin.add(stroke);
      }
    }

    // now add the important roads
    for (IRoadLine road : this.getRoads()) {
      if (road.getImportance() > this.importanceThreshold) {
        setObjLin.add(road);
      }
    }

    IFeatureCollection<IUrbanBlock> blocks = new FT_FeatureCollection<IUrbanBlock>();
    blocks.addAll(this.cityBlocks);

    // now we have all the features that will be city axes. We have to create
    // a CityAxis object for each.
    for (IGeneObjLin obj : setObjLin) {
      // get the city blocks that intersect the object
      HashSet<IUrbanBlock> alongBlocks = new HashSet<IUrbanBlock>();
      alongBlocks.addAll(blocks.select(obj.getGeom()));

      // build the new city axis from the linear object
      CityAxis axis = new CityAxis(this, obj);
      axis.setAlongBlocks(alongBlocks);
      // add it to the city collection
      this.cityAxes.add(axis);
      // update the link with the blocks
      for (IUrbanBlock block : alongBlocks) {
        block.getAxes().add(axis);
      }
    }
  }

  // //////////////////////////////////////////
  // INTERNAL CLASSES //
  // //////////////////////////////////////////
  /**
   * The complex result of the chooseBestNeighbour(...) method: it is composed
   * of a CityBlock object that is the best neighbour for aggregation at some
   * time in the algorithm, and the cost of the aggregation with the best
   * neighbour.
   */
  private class BestNeighbourResult {
    IUrbanBlock bestNeighbour;
    double bestCost;

    public BestNeighbourResult(IUrbanBlock bestNeighbour, double bestCost) {
      this.bestNeighbour = bestNeighbour;
      this.bestCost = bestCost;
    }
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

}
