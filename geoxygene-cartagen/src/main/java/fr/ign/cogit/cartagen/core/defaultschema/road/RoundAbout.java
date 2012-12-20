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

import java.util.Collection;
import java.util.HashSet;
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
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/**
 * ###### IGN / CartAGen ###### Title: RoundAbout Description: Ronds-points
 * Author: J. Renard Date: 21/10/2009 Deprecated, use the RoundAbout class in
 * the networks package rather.
 * 
 */
@Entity
@Access(AccessType.PROPERTY)
public class RoundAbout extends GeneObjSurfDefault implements IRoundAbout {

  /**
   * Associated Geoxygene schema object
   */
  private RondPoint geoxObj;
  private Set<INetworkNode> simples;
  private Set<IRoadLine> internalRoads;
  private Set<IRoadLine> externalRoads;
  private double diameter;
  private int nbLegs;
  private HashSet<IBranchingCrossroad> branchings;

  /**
   * Default constructor, used by Hibernate.
   */
  public RoundAbout() {
    super();
  }

  /**
   * Constructor
   */
  public RoundAbout(RondPoint geoxObj, Collection<IRoadLine> roads,
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
    for (IRoadLine road : roads) {
      if (geoxObj.getRoutesInternes().contains(road.getGeoxObj())) {
        this.internalRoads.add(road);
      }
      if (geoxObj.getRoutesExternes().contains(road.getGeoxObj())) {
        this.externalRoads.add(road);
      }
    }
    this.diameter = geoxObj.getDiameter();
    this.nbLegs = geoxObj.getNbLegs();
    this.branchings = new HashSet<IBranchingCrossroad>();
  }

  /**
   * Constructor from a geometry, sets of internal and external roads, and a set
   * of road nodes. Warning this constructor does not instantiate the reference
   * to adjacent branching crossroads: it is left null and should be
   * instantiated separately later (and the same on the associated Geoxygen
   * RondPoint feature created inside this constructor).
   * @param geom Geometry of the roundabout
   * @param externalRoads Collection of the roads connected to the roundabout
   * @param internalRoads Collection of the roads internal to the roundabout
   * @param initialNodes Collection of the nodes internal to the roundabout
   */
  public RoundAbout(IPolygon geom, Collection<IRoadLine> externalRoads,
      Collection<IRoadLine> internalRoads, Collection<INetworkNode> initialNodes) {

    super();

    // Retrieve the sets of GeOxygen (not CartAGen) internal and external roads
    // from the input sets.
    HashSet<TronconDeRoute> geoxExternalRoads = new HashSet<TronconDeRoute>();
    for (IRoadLine roadLine : externalRoads) {
      geoxExternalRoads.add((TronconDeRoute) roadLine.getGeoxObj());
    }
    HashSet<TronconDeRoute> geoxInternalRoads = new HashSet<TronconDeRoute>();
    for (IRoadLine roadLine : internalRoads) {
      geoxInternalRoads.add((TronconDeRoute) roadLine.getGeoxObj());
    }
    // Same thing for road nodes
    Set<NoeudReseau> geoxNodes = new HashSet<NoeudReseau>();
    for (INetworkNode roadNode : initialNodes) {
      geoxNodes.add((NoeudReseau) roadNode.getGeoxObj());
    }
    // Construct the Geoxygen RondPoint associated to <this>
    RondPoint rondPoint = new RondPoint(geom, geoxNodes, geoxInternalRoads,
        geoxExternalRoads, new HashSet<PatteOie>());

    // Now fill in the attributes
    this.geoxObj = rondPoint;
    this.setInitialGeom(rondPoint.getGeom());
    this.setEliminated(false);
    this.simples = new HashSet<INetworkNode>(initialNodes);
    this.externalRoads = new HashSet<IRoadLine>(externalRoads);
    this.internalRoads = new HashSet<IRoadLine>(internalRoads);
    this.diameter = rondPoint.getDiameter();
    this.nbLegs = this.geoxObj.getNbLegs();
    this.branchings = new HashSet<IBranchingCrossroad>();
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
  public void setExternalRoads(Set<IRoadLine> externalRoads) {
    this.externalRoads = externalRoads;
  }

  @Override
  public void setInternalRoads(Set<IRoadLine> internalRoads) {
    this.internalRoads = internalRoads;
  }

  @Override
  @Transient
  public Set<IBranchingCrossroad> getBranchings() {
    return this.branchings;
  }

  @Override
  public double getDiameter() {
    return this.diameter;
  }

  public void setDiameter(double diameter) {
    this.diameter = diameter;
  }

  public static IRoundAbout getRoundabout(IUrbanBlock block,
      Collection<IRoundAbout> rounds) {
    for (IRoundAbout r : rounds) {
      if (block.getGeom().equals(r.getGeom())) {
        return r;
      }
    }
    return null;
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

  public void setNbLegs(int nbLegs) {
    this.nbLegs = nbLegs;
  }

  public int getNbLegs() {
    return this.nbLegs;
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
  private Set<Integer> branchingsIds = new HashSet<Integer>();
  private Set<Integer> internalRoadsIds = new HashSet<Integer>();
  private Set<Integer> externalRoadsIds = new HashSet<Integer>();

  @ElementCollection
  @CollectionTable(name = "RoundaboutSimpleIds", joinColumns = @JoinColumn(name = "roundabout"))
  @Column(name = "simplesIds")
  @EncodedRelation(targetEntity = RoadNode.class, inverse = false, methodName = "Simples", collectionType = CollectionType.SET)
  public Set<Integer> getSimplesIds() {
    return this.simplesIds;
  }

  public void setSimplesIds(Set<Integer> simplesIds) {
    this.simplesIds = simplesIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoundBranchingIds", joinColumns = @JoinColumn(name = "roundabout"))
  @Column(name = "branchingIds")
  @EncodedRelation(targetEntity = BranchingCrossRoad.class, invClass = RoundAbout.class, methodName = "Branchings", invMethodName = "RoundAbout", nToM = false, collectionType = CollectionType.SET)
  public Set<Integer> getBranchingsIds() {
    return this.branchingsIds;
  }

  public void setBranchingsIds(Set<Integer> branchingsIds) {
    this.branchingsIds = branchingsIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoundInternalRoadsIds", joinColumns = @JoinColumn(name = "roundabout"))
  @Column(name = "internalRoadsIds")
  @EncodedRelation(targetEntity = RoadLine.class, inverse = false, methodName = "InternalRoads", collectionType = CollectionType.SET)
  public Set<Integer> getInternalRoadsIds() {
    return this.internalRoadsIds;
  }

  public void setInternalRoadsIds(Set<Integer> internalRoadsIds) {
    this.internalRoadsIds = internalRoadsIds;
  }

  @ElementCollection
  @CollectionTable(name = "RoundExternalRoadsIds", joinColumns = @JoinColumn(name = "roundabout"))
  @Column(name = "externalRoadsIds")
  @EncodedRelation(targetEntity = RoadLine.class, inverse = false, methodName = "ExternalRoads", collectionType = CollectionType.SET)
  public Set<Integer> getExternalRoadsIds() {
    return this.externalRoadsIds;
  }

  public void setExternalRoadsIds(Set<Integer> externalRoadsIds) {
    this.externalRoadsIds = externalRoadsIds;
  }

}
