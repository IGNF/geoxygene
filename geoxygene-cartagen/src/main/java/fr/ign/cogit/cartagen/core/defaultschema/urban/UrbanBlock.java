/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.urban;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterLine;
import fr.ign.cogit.cartagen.core.defaultschema.road.BranchingCrossRoad;
import fr.ign.cogit.cartagen.core.defaultschema.road.DualCarriageway;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IDualCarriageWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IEmptySpace;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.core.persistence.CollectionType;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.DensityMeasures;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.CityAxis;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.CityPartition;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.IlotImpl;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

@Entity
@Access(AccessType.PROPERTY)
public class UrbanBlock extends GeneObjSurfDefault implements IUrbanBlock {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(UrbanBlock.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  @Transient
  private Ilot geoxObj;
  @Transient
  private ITown town;
  @Transient
  private IFeatureCollection<IUrbanElement> urbanElements;
  @Transient
  private IFeatureCollection<INetworkSection> surroundingNetwork;
  @Transient
  private IFeatureCollection<IUrbanAlignment> alignments;
  @Transient
  private Collection<IEmptySpace> emptySpaces;
  private boolean isColored;
  private HashSet<IUrbanBlock> neighbours;
  private HashSet<CityAxis> axes;
  private CityPartition partition;
  private StreetNetwork net;
  private int aggregLevel;
  private HashSet<IUrbanBlock> insideBlocks;
  private HashSet<IUrbanBlock> initialGeoxBlocks;
  private boolean edge = false;
  /**
   * true if the block is a hole inside another block. It means that the block
   * is a part of a dead end group.
   */
  private boolean holeBlock;
  /**
   * The City Blocks have their own geometry as several can share the same
   * geoxygene object that is supposed to manage the geometry.
   */
  private IPolygon cityBlockGeom;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public UrbanBlock() {
    super();
  }

  public UrbanBlock(Ilot block, CityPartition partition, StreetNetwork net,
      Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    super();
    this.geoxObj = block;
    this.setInitialGeom(this.geoxObj.getGeom());
    this.setEliminated(false);
    this.emptySpaces = new HashSet<IEmptySpace>();
    this.urbanElements = new FT_FeatureCollection<IUrbanElement>();
    this.surroundingNetwork = new FT_FeatureCollection<INetworkSection>();
    this.isColored = false;
    this.setGeom(block.getGeom());
    this.cityBlockGeom = (IPolygon) block.getGeom();
    this.partition = partition;
    this.net = net;
    this.aggregLevel = 0;
    this.insideBlocks = new HashSet<IUrbanBlock>();
    this.axes = new HashSet<CityAxis>();
    this.neighbours = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks.add(this);
    this.getUrbanElements().addAll(buildings);
    this.getSurroundingNetwork().addAll(surroundRoads);
  }

  public UrbanBlock(IPolygon poly, CityPartition partition, StreetNetwork net,
      Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    super();
    this.geoxObj = new IlotImpl(poly);
    this.setInitialGeom(this.geoxObj.getGeom());
    this.setEliminated(false);
    this.emptySpaces = new HashSet<IEmptySpace>();
    this.urbanElements = new FT_FeatureCollection<IUrbanElement>();
    this.surroundingNetwork = new FT_FeatureCollection<INetworkSection>();
    this.isColored = false;
    this.setGeom(poly);
    this.cityBlockGeom = poly;
    this.partition = partition;
    this.net = net;
    this.aggregLevel = 0;
    this.insideBlocks = new HashSet<IUrbanBlock>();
    this.axes = new HashSet<CityAxis>();
    this.neighbours = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks.add(this);
    this.getUrbanElements().addAll(buildings);
    this.getSurroundingNetwork().addAll(surroundRoads);
  }

  public UrbanBlock(IPolygon poly, ITown town,
      IFeatureCollection<IUrbanElement> urbanElements,
      IFeatureCollection<INetworkSection> sections, CityPartition partition,
      StreetNetwork net) {
    this.geoxObj = new IlotImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.emptySpaces = new HashSet<IEmptySpace>();
    this.urbanElements = urbanElements;
    this.surroundingNetwork = new FT_FeatureCollection<INetworkSection>();
    this.isColored = false;
    this.setTown(town);
    this.surroundingNetwork = sections;
    this.setGeom(poly);
    this.cityBlockGeom = poly;
    this.partition = partition;
    this.net = net;
    this.aggregLevel = 0;
    this.insideBlocks = new HashSet<IUrbanBlock>();
    this.axes = new HashSet<CityAxis>();
    this.neighbours = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks.add(this);
  }

  public UrbanBlock(IPolygon poly,
      IFeatureCollection<IUrbanElement> urbanElements,
      IFeatureCollection<INetworkSection> sections) {
    this.geoxObj = new IlotImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.emptySpaces = new HashSet<IEmptySpace>();
    this.urbanElements = urbanElements;
    this.surroundingNetwork = new FT_FeatureCollection<INetworkSection>();
    this.isColored = false;
    this.surroundingNetwork = sections;
    this.setGeom(poly);
    this.cityBlockGeom = poly;
    this.aggregLevel = 0;
    this.insideBlocks = new HashSet<IUrbanBlock>();
    this.axes = new HashSet<CityAxis>();
    this.neighbours = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks.add(this);
  }

  /**
   * Constructor with an additional geometry for the cityblock in case this
   * geometry is not the same as the block geometry (cityBlocks cut at city
   * limit)
   * @param block
   * @param partition
   * @param net
   * @param geom
   * @author GTouya
   */
  public UrbanBlock(Ilot block, CityPartition partition, StreetNetwork net,
      IPolygon geom, Set<IUrbanElement> buildings,
      Set<IRoadLine> surroundRoads) {
    this.geoxObj = block;
    this.setInitialGeom(this.geoxObj.getGeom());
    this.setEliminated(false);
    this.emptySpaces = new HashSet<IEmptySpace>();
    this.urbanElements = new FT_FeatureCollection<IUrbanElement>();
    this.surroundingNetwork = new FT_FeatureCollection<INetworkSection>();
    this.isColored = false;
    this.setGeom(geom);
    this.cityBlockGeom = geom;
    this.partition = partition;
    this.net = net;
    this.aggregLevel = 0;
    this.insideBlocks = new HashSet<IUrbanBlock>();
    this.axes = new HashSet<CityAxis>();
    this.neighbours = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks = new HashSet<IUrbanBlock>();
    this.initialGeoxBlocks.add(this);
    this.getUrbanElements().addAll(buildings);
    this.getSurroundingNetwork().addAll(surroundRoads);
  }

  // Getters and setters //

  @Override
  @Transient
  public Ilot getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public ITown getTown() {
    return this.town;
  }

  @Override
  public void setTown(ITown town) {
    this.town = town;
    this.town.getTownBlocks().add(this);
  }

  @Override
  @Transient
  public IFeatureCollection<IUrbanElement> getUrbanElements() {
    return this.urbanElements;
  }

  @Override
  public void setUrbanElements(
      IFeatureCollection<IUrbanElement> urbanElements) {
    this.urbanElements = urbanElements;
  }

  @Override
  @Transient
  public IFeatureCollection<IUrbanAlignment> getAlignments() {
    return this.alignments;
  }

  @Override
  public void setAlignments(IFeatureCollection<IUrbanAlignment> alignments) {
    this.alignments = alignments;
  }

  @Override
  @Transient
  public IFeatureCollection<INetworkSection> getSurroundingNetwork() {
    return this.surroundingNetwork;
  }

  @Override
  public void setSurroundingNetwork(
      IFeatureCollection<INetworkSection> surroundingNetwork) {
    this.surroundingNetwork = surroundingNetwork;
  }

  @Override
  @Transient
  public Collection<IEmptySpace> getEmptySpaces() {
    return this.emptySpaces;
  }

  @Override
  public boolean isColored() {
    return this.isColored;
  }

  @Override
  public void setColored(boolean bool) {
    this.isColored = bool;
  }

  @Override
  @Column(name = "CartAGenDB_name")
  public String getDbName() {
    return super.getDbName();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  @Override
  @Transient
  public HashSet<IUrbanBlock> getNeighbours() {
    return this.neighbours;
  }

  public void setNeighbours(HashSet<IUrbanBlock> neighbours) {
    this.neighbours = neighbours;
  }

  @Override
  @Transient
  public HashSet<CityAxis> getAxes() {
    return this.axes;
  }

  public void setAxes(HashSet<CityAxis> axes) {
    this.axes = axes;
  }

  @Override
  @Transient
  public CityPartition getPartition() {
    return this.partition;
  }

  @Override
  public void setPartition(CityPartition partition) {
    this.partition = partition;
  }

  @Transient
  public StreetNetwork getNet() {
    return this.net;
  }

  public void setNet(StreetNetwork net) {
    this.net = net;
  }

  @Override
  public int getAggregLevel() {
    return this.aggregLevel;
  }

  @Override
  public void setAggregLevel(int aggregLevel) {
    this.aggregLevel = aggregLevel;
  }

  @Override
  @Transient
  public HashSet<IUrbanBlock> getInsideBlocks() {
    return this.insideBlocks;
  }

  public void setInsideBlocks(HashSet<IUrbanBlock> insideBlocks) {
    this.insideBlocks = insideBlocks;
  }

  @Override
  public void setEdge(boolean edge) {
    this.edge = edge;
  }

  @Override
  public boolean isEdge() {
    return this.edge;
  }

  public void setInitialGeoxBlocks(HashSet<IUrbanBlock> initialGeoxBlocks) {
    this.initialGeoxBlocks = initialGeoxBlocks;
  }

  @Override
  @Transient
  public HashSet<IUrbanBlock> getInitialGeoxBlocks() {
    return this.initialGeoxBlocks;
  }

  // Other public methods //
  @Override
  public boolean equals(Object arg0) {
    if (!(arg0 instanceof UrbanBlock)) {
      return false;
    }
    UrbanBlock cityBlock = (UrbanBlock) arg0;
    if (this.net != null) {
      if (!this.net.equals(cityBlock.net)) {
        return false;
      }
    }
    if (this.partition != null && cityBlock.partition != null) {
      if (!this.partition.equals(cityBlock.partition)) {
        return false;
      }
    }
    if (!this.getCityBlockGeom().equals(cityBlock.getCityBlockGeom())) {
      return false;
    }
    return super.equals(arg0);
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public String toString() {
    String netName = "null";
    if (this.net != null) {
      netName = this.net.toString();
    }
    return "City block " + this.getId() + " from " + netName;
  }

  public void printInfo() {
    System.out.println(this.toString());
    System.out.println(this.getUrbanElements().size() + " buildings");
    System.out.println(this.getSurroundingNetwork().size() + " roads");
  }

  /**
   * Determines if a city block is standard, that is to say, if it can be
   * aggregated during the selection process. Road structures like roundabouts
   * are not standard blocks.
   * 
   * @return
   * @author Guillaume
   */
  @Override
  @Transient
  public boolean isStandard() {
    IRoundAbout r = RoundAbout.getRoundabout(this, this.net.getRoundabouts());
    if (r != null) {
      return false;
    }
    IBranchingCrossroad b = BranchingCrossRoad.getBranchingCrossRoad(this,
        this.net.getBranchings());
    if (b != null) {
      return false;
    }
    // handle dual carriageways
    IDualCarriageWay d = DualCarriageway.getDualCarriageWay(this,
        this.net.getDualCarriageways());
    if (d != null) {
      return false;
    }
    // TODO handle escape crossroads
    return true;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////
  @Override
  public IUrbanBlock aggregateWithBlock(IUrbanBlock neighbour) {
    UrbanBlock.logger
        .fine(this.toString() + " is aggregated to " + neighbour.toString());

    // create the new geometry of the block
    IPolygon newGeom = (IPolygon) this.getCityBlockGeom()
        .union(neighbour.getCityBlockGeom());

    Set<IRoadLine> blockRoads = new HashSet<IRoadLine>();

    // mark the between roads as eliminated
    IGeometry line = this.getCityBlockGeom()
        .intersection(neighbour.getCityBlockGeom());
    HashSet<IRoadLine> roads = new HashSet<IRoadLine>();
    // test if the intersection worked
    if (line instanceof IAggregate<?>) {
      for (Object g : ((IAggregate<?>) line).getList()) {
        roads.addAll(this.net.getRoads().select((IGeometry) g));
      }
    } else {
      roads.addAll(this.net.getRoads().select(line));
    }
    for (IRoadLine r : roads) {
      // counts the number of common vertices
      int nbVertCom = CommonAlgorithmsFromCartAGen.getNbCommonVertices(line,
          r.getGeom());
      if (nbVertCom > 2 || (nbVertCom == r.getGeom().coord().size())) {
        r.eliminate();
        r.setDeleted(true);
        blockRoads.remove(r);
      }
      // removes unconnected roads
      if (!newGeom.exteriorLineString().intersects(r.getGeom())) {
        r.eliminate();
        r.setDeleted(true);
        blockRoads.remove(r);
      }
    }

    // now compute the new density considering the eliminated roads
    Set<IUrbanElement> builds = new HashSet<IUrbanElement>();
    builds.addAll(this.getUrbanElements());
    builds.addAll(neighbour.getUrbanElements());

    // build a new GeOxygene object for the new aggregated block
    IUrbanBlock aggrBlock = new UrbanBlock(new IlotImpl(newGeom),
        this.getPartition(), this.getNet(), builds, blockRoads);

    // fill the new block fields
    aggrBlock.getNeighbours().addAll(this.neighbours);
    aggrBlock.getNeighbours().addAll(neighbour.getNeighbours());
    aggrBlock.getNeighbours().remove(this);
    aggrBlock.getNeighbours().remove(neighbour);
    aggrBlock.getInsideBlocks().add(this);
    aggrBlock.getInsideBlocks().add(neighbour);
    aggrBlock.getInitialGeoxBlocks().addAll(this.initialGeoxBlocks);
    aggrBlock.getInitialGeoxBlocks().addAll(neighbour.getInitialGeoxBlocks());
    aggrBlock.getAxes().addAll(this.axes);
    aggrBlock.getAxes().addAll(neighbour.getAxes());
    aggrBlock
        .setAggregLevel(this.getAggregLevel() + neighbour.getAggregLevel() + 1);

    // update the neighbour link the other way round
    for (IUrbanBlock b : neighbour.getNeighbours()) {
      b.getNeighbours().remove(neighbour);
      if (!b.equals(this)) {
        b.getNeighbours().add(aggrBlock);
      }
    }
    for (IUrbanBlock b : this.neighbours) {
      b.getNeighbours().remove(this);
      if (!b.equals(neighbour)) {
        b.getNeighbours().add(aggrBlock);
      }
    }

    // update the evolution of the objects
    this.getResultingObjects().add(aggrBlock);
    neighbour.getResultingObjects().add(aggrBlock);
    aggrBlock.getAntecedents().add(neighbour);
    aggrBlock.getAntecedents().add(this);
    aggrBlock.setStemmingFromN1Transfo(true);

    // update the relation town-blocks
    Set<IUrbanBlock> cityBlocks = this.net.getCityBlocks();
    cityBlocks.remove(this);
    cityBlocks.remove(neighbour);
    cityBlocks.add(aggrBlock);
    this.net.setCityBlocks(cityBlocks);

    // eliminate the old blocks
    this.eliminate();
    neighbour.eliminate();

    return aggrBlock;
  }

  @Override
  @Transient
  public boolean isHoleBlock() {
    return holeBlock;
  }

  @Override
  public void setHoleBlock(boolean holeBlock) {
    this.holeBlock = holeBlock;
  }

  @Override
  public void updateGeom(IPolygon poly) {
    this.setGeom(poly);
    // update block buildings
    IFeatureCollection<IUrbanElement> newBuilds = new FT_FeatureCollection<IUrbanElement>();
    for (IUrbanElement b : this.getUrbanElements()) {
      if (poly.contains(b.getGeom())) {
        newBuilds.add(b);
      }
    }
    // update block roads
    IFeatureCollection<INetworkSection> newRoads = new FT_FeatureCollection<INetworkSection>();
    for (INetworkSection r : this.getSurroundingNetwork()) {
      if (!(r instanceof IRoadLine)) {
        continue;
      }
      if (poly.intersects(r.getGeom())) {
        newRoads.add(r);
      }
    }
    this.setUrbanElements(newBuilds);
    this.setSurroundingNetwork(newRoads);
  }

  // modif Guillaume pour décorréler des agents
  @Override
  @Transient
  public double getDensity() {
    /*
     * IBlockAgent blockAgent = (IBlockAgent) AgentUtil
     * .getAgentAgentFromGeneObj(this); return blockAgent.getInitialDensity();
     */
    return DensityMeasures.getBlockBuildingsDensity(this);
  }

  @Transient
  public String getDensityString() {
    String masque = new String("#0.##");
    DecimalFormat form = new DecimalFormat(masque);
    return form.format(this.getDensity());
  }

  // modif Guillaume pour décorréler des agents
  @Override
  @Transient
  public double getSimulatedDensity() {
    /*
     * IBlockAgent blockAgent = (IBlockAgent) AgentUtil
     * .getAgentAgentFromGeneObj(this); return blockAgent.getSimulatedDensity();
     */
    double buildMinSize = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Math.pow(Legend.getSYMBOLISATI0N_SCALE(), 2.0) / 1000000.0;
    if (this.net != null) {
      buildMinSize = this.net.getBuildingMinSize();
    }
    return DensityMeasures.getBlockBuildingsSimulatedDensity(this,
        buildMinSize);
  }

  @Override
  @Transient
  public IPolygon getGeom() {
    return this.cityBlockGeom;
  }

  public void setCityBlockGeom(IPolygon cityBlockGeom) {
    this.cityBlockGeom = cityBlockGeom;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public IPolygon getCityBlockGeom() {
    return this.cityBlockGeom;
  }

  // ////////////////////////////////////////
  // Persistence methods //
  // ////////////////////////////////////////

  private List<Integer> urbanElementsIds = new ArrayList<Integer>();

  public void setUrbanElementsIds(List<Integer> urbanElementsIds) {
    this.urbanElementsIds = urbanElementsIds;
  }

  @ElementCollection
  @CollectionTable(name = "UrbanElementsIds", joinColumns = @JoinColumn(name = "block"))
  @Column(name = "UrbanElementsIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = Building.class, invClass = IUrbanBlock.class, methodName = "UrbanElements", invMethodName = "Block", nToM = false, collectionType = CollectionType.FEATURE_COLLECTION)
  public List<Integer> getUrbanElementsIds() {
    return this.urbanElementsIds;
  }

  private List<Integer> surroundingNetworkIds = new ArrayList<Integer>();

  public void setSurroundingNetworkIds(List<Integer> surroundingNetworkIds) {
    this.surroundingNetworkIds = surroundingNetworkIds;
  }

  @ElementCollection
  @CollectionTable(name = "SurroundingNetworkIds", joinColumns = @JoinColumn(name = "block"))
  @Column(name = "SurroundingNetworkIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntities = { RoadLine.class,
      WaterLine.class }, inverse = false, methodName = "SurroundingNetwork", nToM = false, collectionType = CollectionType.FEATURE_COLLECTION)
  public List<Integer> getSurroundingNetworkIds() {
    return this.surroundingNetworkIds;
  }

  private List<Integer> alignmentsIds = new ArrayList<Integer>();

  public void setAlignmentsIds(List<Integer> alignmentsIds) {
    this.alignmentsIds = alignmentsIds;
  }

  @ElementCollection
  @CollectionTable(name = "AlignmentsIds", joinColumns = @JoinColumn(name = "block"))
  @Column(name = "AlignmentsIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = UrbanAlignment.class, invClass = IUrbanBlock.class, methodName = "Alignments", invMethodName = "Block", nToM = false, collectionType = CollectionType.FEATURE_COLLECTION)
  public List<Integer> getAlignmentsIds() {
    return this.alignmentsIds;
  }

  @Override
  public void restoreGeoxObjects() {
    if (this.geoxObj == null) {
      this.geoxObj = new IlotImpl(this.getGeom());
    }
  }

  @Override
  public void restoreGeoxRelations() {
    Ilot ilot = this.getGeoxObj();
    System.out.println(this);
    for (INetworkSection sect : this.getSurroundingNetwork()) {
      ilot.getArcsReseaux().add((ArcReseau) sect.getGeoxObj());
    }
  }

  @Override
  public void addUrbanElement(IUrbanElement urbanElement) {
    this.urbanElements.add(urbanElement);
    this.urbanElementsIds.add(urbanElement.getId());
    this.getGeoxObj().getComposants()
        .add((ElementIndependant) urbanElement.getGeoxObj());
  }

}
