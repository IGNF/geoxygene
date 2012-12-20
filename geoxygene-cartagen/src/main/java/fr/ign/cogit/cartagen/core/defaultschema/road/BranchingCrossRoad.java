/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.persistence.CollectionType;
import fr.ign.cogit.cartagen.core.persistence.Encoded1To1Relation;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.SimpleCrossRoad;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/*
 * ###### IGN / CartAGen ###### Title: BranchingCrossRoad Description:
 * Carrefours aménagés Author: J. Renard Date: 21/10/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class BranchingCrossRoad extends GeneObjSurfDefault implements
    IBranchingCrossroad {

  /**
   * Associated Geoxygene schema object
   */
  private PatteOie geoxObj;
  private Set<INetworkNode> simples;
  private Set<IRoadLine> internalRoads;
  private Set<IRoadLine> externalRoads;
  private Set<IRoadLine> mainRoadIntern;
  private IRoadLine minorRoadExtern;
  private IRoundAbout roundAbout;

  /**
   * Default constructor, used by Hibernate.
   */
  public BranchingCrossRoad() {
    super();
  }

  /**
   * Constructor
   */
  public BranchingCrossRoad(PatteOie geoxObj, Collection<IRoadLine> roads,
      Collection<IRoadNode> nodes) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.simples = new HashSet<INetworkNode>();
    for (IRoadNode node : nodes) {
      if (geoxObj.getNoeuds().contains(node.getGeoxObj())) {
        this.simples.add(node);
      }
    }
    this.internalRoads = new HashSet<IRoadLine>();
    this.externalRoads = new HashSet<IRoadLine>();
    this.mainRoadIntern = new HashSet<IRoadLine>();
    for (IRoadLine road : roads) {
      if (geoxObj.getRoutesInternes().contains(road.getGeoxObj())) {
        this.internalRoads.add(road);
      }
      if (geoxObj.getRoutesExternes().contains(road.getGeoxObj())) {
        this.externalRoads.add(road);
      }
      if (geoxObj.getMainRoadIntern().contains(road.getGeoxObj())) {
        this.mainRoadIntern.add(road);
      }
      if (road.getGeoxObj().equals(geoxObj.getMinorRoadExtern())) {
        this.minorRoadExtern = road;
      }
    }
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public Set<IRoadLine> getExternalRoads() {
    return this.externalRoads;
  }

  @Override
  @Transient
  public Set<IRoadLine> getInternalRoads() {
    return this.internalRoads;
  }

  @Override
  @Transient
  public Set<IRoadLine> getMainRoadIntern() {
    return this.mainRoadIntern;
  }

  @Override
  @Transient
  public IRoadLine getMinorRoadExtern() {
    return this.minorRoadExtern;
  }

  @Override
  @Transient
  public IRoundAbout getRoundAbout() {
    return this.roundAbout;
  }

  @Override
  public void setExternalRoads(Set<IRoadLine> externalRoads) {
    this.externalRoads = externalRoads;
  }

  @Override
  public void setInternalRoads(Set<IRoadLine> internalRoads) {
    this.internalRoads = internalRoads;
  }

  @Override
  public void setMainRoadIntern(Set<IRoadLine> mainRoadIntern) {
    this.mainRoadIntern = mainRoadIntern;
  }

  @Override
  public void setMinorRoadExtern(IRoadLine minorRoadExtern) {
    this.minorRoadExtern = minorRoadExtern;
  }

  @Override
  public void setRoundAbout(IRoundAbout roundAbout) {
    this.roundAbout = roundAbout;
  }

  public static IBranchingCrossroad getBranchingCrossRoad(IUrbanBlock block,
      Collection<IBranchingCrossroad> branchs) {
    for (IBranchingCrossroad b : branchs) {
      if (block.getGeom().equals(b.getGeom())) {
        return b;
      }
    }
    return null;
  }

  /**
   * <p>
   * Determine if a road block object is a branching crossroad or not. Uses
   * graph measures (3 nodes with degree 3) and shape measures. A branching
   * crossroad should be triangle shaped.
   * 
   * @param block : the tested road block
   * @param sizeThreshold : the maximum area of a branching crossroad (50000 m²
   *          advised)
   * @param surfDistThreshold : the maximum surfacic distance between the block
   *          and the triangle formed by the three nodes of the block. (0.5
   *          advised)
   * @return true if the block is a branching crossroad.
   * 
   */
  public static boolean isBranchingCrossRoad(Ilot block, double sizeThreshold,
      double surfDistThreshold,
      IFeatureCollection<SimpleCrossRoad> simpleCrossroads) {
    // get the block geometry
    GM_Polygon geom = (GM_Polygon) block.getGeom();
    // get the area of the geometry
    double area = geom.area();

    // if area is > to threshold, return false
    if (area > sizeThreshold) {
      return false;
    }

    // now check if the block has 3 degree 3 nodes
    List<SimpleCrossRoad> crossroads = new ArrayList<SimpleCrossRoad>(
        simpleCrossroads.select(geom));

    // if there is no 3 crossroads, it is not a branching crossroad
    if (crossroads.size() != 3) {
      return false;
    }

    int nbDeg4 = 0;
    // loop on the crossroads to check their degree
    for (SimpleCrossRoad cross : crossroads) {
      // if the degree is not 3 or 4, it is not a branching crossroad
      if (cross.getDegree() != 3 && (cross.getDegree() != 4)) {
        return false;
      }
      // only one degree 4 node is accepted
      if (cross.getDegree() == 4) {
        nbDeg4 += 1;
        if (nbDeg4 > 1) {
          return false;
        }
      }
    }

    // check if the block is connected to a roundabout
    Collection<IRoundAbout> rounds = CartAGenDoc.getInstance()
        .getCurrentDataset().getRoundabouts().select(
            geom.getExterior().getPrimitive());

    if (rounds.size() >= 0) {
      // get the area of the roundabout
      RondPoint round = (RondPoint) rounds.iterator().next();
      double areaRound = round.getArea();
      // if the block area is twice bigger than the roundabout, it is not a
      // branching crossroad
      if (area > 2 * areaRound) {
        return false;
      }

      // there, the block is a branching crossroad

    } else {
      // here, test the shape of the block by comparing it to a triangle
      // first, build the triangle
      DirectPositionList points = new DirectPositionList();
      for (int i = 0; i < 3; i++) {
        points.add(crossroads.get(i).getGeom().centroid());
      }
      points.add(crossroads.get(0).getGeom().centroid());
      ILineString ring = new GM_LineString(points);
      GM_Polygon triangle = new GM_Polygon(ring);

      // then, compare the triangle to the block geometry
      double surfDist = Distances.distanceSurfacique(triangle, geom);

      if (surfDist > surfDistThreshold) {
        return false;
      }

      // there, the block is a branching crossroad
    }

    return true;
  }

  @Override
  @Transient
  public Set<INetworkNode> getSimples() {
    return this.simples;
  }

  @Override
  public void setSimples(Set<INetworkNode> simples) {
    this.simples = simples;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.software.interfaceCartagen.hibernate.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return super.getGeom();
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

  // ///////////////////////////////////////
  // HIBERNATE RELATED FIELDS & METHODS //
  // ///////////////////////////////////////
  private Set<Integer> simplesIds = new HashSet<Integer>();
  private Set<Integer> internalRoadsIds = new HashSet<Integer>();
  private Set<Integer> externalRoadsIds = new HashSet<Integer>();
  private Set<Integer> mainRoadInternIds = new HashSet<Integer>();
  private int minorRoadExternId = 0;

  @ElementCollection
  @CollectionTable(name = "BranchingSimpleIds", joinColumns = @JoinColumn(name = "branching"))
  @Column(name = "simplesIds")
  @EncodedRelation(targetEntity = RoadNode.class, inverse = false, methodName = "Simples", collectionType = CollectionType.SET)
  public Set<Integer> getSimplesIds() {
    return this.simplesIds;
  }

  public void setSimplesIds(Set<Integer> simplesIds) {
    this.simplesIds = simplesIds;
  }

  @ElementCollection
  @CollectionTable(name = "BranchInternalRoadsIds", joinColumns = @JoinColumn(name = "branching"))
  @Column(name = "internalRoadsIds")
  @EncodedRelation(targetEntity = RoadLine.class, inverse = false, methodName = "InternalRoads", collectionType = CollectionType.SET)
  public Set<Integer> getInternalRoadsIds() {
    return this.internalRoadsIds;
  }

  public void setInternalRoadsIds(Set<Integer> internalRoadsIds) {
    this.internalRoadsIds = internalRoadsIds;
  }

  @ElementCollection
  @CollectionTable(name = "BranchExternalRoadsIds", joinColumns = @JoinColumn(name = "branching"))
  @Column(name = "externalRoadsIds")
  @EncodedRelation(targetEntity = RoadLine.class, inverse = false, methodName = "ExternalRoads", collectionType = CollectionType.SET)
  public Set<Integer> getExternalRoadsIds() {
    return this.externalRoadsIds;
  }

  public void setExternalRoadsIds(Set<Integer> externalRoadsIds) {
    this.externalRoadsIds = externalRoadsIds;
  }

  public void setMainRoadInternIds(Set<Integer> mainRoadInternIds) {
    this.mainRoadInternIds = mainRoadInternIds;
  }

  @ElementCollection
  @CollectionTable(name = "BranchMainRoadInternIds", joinColumns = @JoinColumn(name = "branching"))
  @Column(name = "mainRoadInternIds")
  @EncodedRelation(targetEntity = RoadLine.class, inverse = false, methodName = "MainRoadIntern", collectionType = CollectionType.SET)
  public Set<Integer> getMainRoadInternIds() {
    return this.mainRoadInternIds;
  }

  public void setMinorRoadExternId(int minorRoadExternId) {
    this.minorRoadExternId = minorRoadExternId;
  }

  @Encoded1To1Relation(targetEntity = RoadLine.class, inverse = false, methodName = "MinorRoadExtern")
  public int getMinorRoadExternId() {
    return this.minorRoadExternId;
  }

}
