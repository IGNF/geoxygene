package fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

public class FlexibilityGraphDetection {

  private Set<MinimumSeparation> minSeps;

  /**
   * conflictNodePropagationCoef : propagation coeff for the diffusion of the
   * nodes adviced value : 0.6
   */
  private double conflictNodePropagationCoef = 0.6;
  /**
   * conflictNodeMinCost : minimal cost of a node conflict adviced value : 15.0
   */
  private double conflictNodeMinCost = 15.0;
  /**
   * conflictOverlapMinCost : minimal cost of an overlap conflict (between 0 and
   * 1, 0 = all conflicts) adviced value : 0.15
   */
  private double conflictOverlapMinCost = 0.05;
  /**
   * conflictOverlapSearchThresh
   */
  private double conflictOverlapSearchThresh = 0.08;
  /**
   * conflictNodeMinFlexibility : minimal flexibility of a node conflict adviced
   * value : 0.001
   */
  private double conflictNodeMinFlexibility = 0.001;
  /**
   * conflictLineMinFlexibility : minimal flexibility of a line conflict adviced
   * value : 0.001
   */
  private double conflictLineMinFlexibility = 0.001;
  /**
   * coalescenceMin : minimal coalescence of a conflict (adviced 0.015) adviced
   * value : 0.015
   */
  private double coalescenceMin = 0.015;
  /**
   * bendsShorteningMin : minimal bends shortening of a conflict adviced value :
   * 0.1 map mm
   */
  private double bendsShorteningMin = 5.0; // 0.1 mm
  /**
   * bendsShorteningMax : maximum bends shortening of a conflict adviced value :
   * 0.3 map mm
   */
  private double bendsShorteningMax = 15.0; // 0.3 mm
  /**
   * searchDist : the searching distance of conflicted objects around one object
   * adviced value : 1 map cm
   */
  private double searchDist = 500.0; // 1 cm
  /**
   * addPointDist : the distance to enriched the line around nodes adviced value
   * : 0.1 map mm
   */
  private double addPointDist = 5.0; // 0.1 mm
  /**
   * iterationMax : number of time beams must be apply by the node filtering
   * adviced value : 2
   */
  private int iterationMax = 2;
  /**
   * treatShortLine : true to apply special treatement on short lines
   */
  private boolean treatShortLine = true;
  /**
   * networkRes : the network resolution, it's a scale dependant parameter.
   * default: 2.5
   */
  private double networkRes = 2.5;
  /**
   * The sections on which the flexibility graph are built
   */
  private Set<INetworkSection> sections;

  /**
   * The set of flexibility graphs created by this algorithm.
   */
  private Set<FlexibilityGraph> graphs;

  /**
   * The set of overlap conflicts identified by this algorithm.
   */
  private Set<OverlapConflict> overlapConflicts;

  /**
   * The set of overlap conflicts identified by this algorithm.
   */
  private Set<NodeConflict> nodeConflicts;

  /**
   * The set of overlap conflicts identified by this algorithm.
   */
  private Set<HoleConflict> holeConflicts;

  /**
   * The set of overlap conflicts identified by this algorithm.
   */
  private Set<CoalescenceConflict> coalConflicts;

  /**
   * Default constructor: all parameters have default values.
   */
  public FlexibilityGraphDetection(Set<INetworkSection> sections,
      Set<MinimumSeparation> minSeps) {
    this.sections = sections;
    this.minSeps = minSeps;
    graphs = new HashSet<FlexibilityGraph>();
  }

  public double getConflictNodePropagationCoef() {
    return conflictNodePropagationCoef;
  }

  public void setConflictNodePropagationCoef(double conflictNodePropagationCoef) {
    this.conflictNodePropagationCoef = conflictNodePropagationCoef;
  }

  public double getConflictNodeMinCost() {
    return conflictNodeMinCost;
  }

  public void setConflictNodeMinCost(double conflictNodeMinCost) {
    this.conflictNodeMinCost = conflictNodeMinCost;
  }

  public double getConflictOverlapMinCost() {
    return conflictOverlapMinCost;
  }

  public void setConflictOverlapMinCost(double conflictOverlapMinCost) {
    this.conflictOverlapMinCost = conflictOverlapMinCost;
  }

  public double getConflictNodeMinFlexibility() {
    return conflictNodeMinFlexibility;
  }

  public void setConflictNodeMinFlexibility(double conflictNodeMinFlexibility) {
    this.conflictNodeMinFlexibility = conflictNodeMinFlexibility;
  }

  public double getConflictLineMinFlexibility() {
    return conflictLineMinFlexibility;
  }

  public void setConflictLineMinFlexibility(double conflictLineMinFlexibility) {
    this.conflictLineMinFlexibility = conflictLineMinFlexibility;
  }

  public double getCoalescenceMin() {
    return coalescenceMin;
  }

  public void setCoalescenceMin(double coalescenceMin) {
    this.coalescenceMin = coalescenceMin;
  }

  public double getBendsShorteningMin() {
    return bendsShorteningMin;
  }

  public void setBendsShorteningMin(double bendsShorteningMin) {
    this.bendsShorteningMin = bendsShorteningMin;
  }

  public double getBendsShorteningMax() {
    return bendsShorteningMax;
  }

  public void setBendsShorteningMax(double bendsShorteningMax) {
    this.bendsShorteningMax = bendsShorteningMax;
  }

  public double getSearchDist() {
    return searchDist;
  }

  public void setSearchDist(double searchDist) {
    this.searchDist = searchDist;
  }

  public double getAddPointDist() {
    return addPointDist;
  }

  public void setAddPointDist(double addPointDist) {
    this.addPointDist = addPointDist;
  }

  public int getIterationMax() {
    return iterationMax;
  }

  public void setIterationMax(int iterationMax) {
    this.iterationMax = iterationMax;
  }

  public boolean isTreatShortLine() {
    return treatShortLine;
  }

  public void setTreatShortLine(boolean treatShortLine) {
    this.treatShortLine = treatShortLine;
  }

  public Set<FlexibilityGraph> getGraphs() {
    return graphs;
  }

  public void setGraphs(Set<FlexibilityGraph> graphs) {
    this.graphs = graphs;
  }

  public Set<OverlapConflict> getOverlapConflicts() {
    return overlapConflicts;
  }

  public void setOverlapConflicts(Set<OverlapConflict> conflicts) {
    this.overlapConflicts = conflicts;
  }

  public Set<NodeConflict> getNodeConflicts() {
    return nodeConflicts;
  }

  public void setNodeConflicts(Set<NodeConflict> nodeConflicts) {
    this.nodeConflicts = nodeConflicts;
  }

  public Set<HoleConflict> getHoleConflicts() {
    return holeConflicts;
  }

  public void setHoleConflicts(Set<HoleConflict> holeConflicts) {
    this.holeConflicts = holeConflicts;
  }

  public Set<CoalescenceConflict> getCoalConflicts() {
    return coalConflicts;
  }

  public void setCoalConflicts(Set<CoalescenceConflict> coalConflicts) {
    this.coalConflicts = coalConflicts;
  }

  public double getConflictOverlapSearchThresh() {
    return conflictOverlapSearchThresh;
  }

  public void setConflictOverlapSearchThresh(double conflictOverlapSearchThresh) {
    this.conflictOverlapSearchThresh = conflictOverlapSearchThresh;
  }

  public void createFlexiGraphs() {
    // TODO
  }

  /**
   * Search the conflicts between the network sections of {@code this}.
   * @param overlap
   * @param node
   * @param hole
   * @param coal
   */
  public void searchConflicts(boolean overlap, boolean node, boolean hole,
      boolean coal) {
    // first search node conflicts if necessary
    if (node)
      this.searchNodeConflicts();
    // then, loop on the sections
    for (INetworkSection section : sections) {
      if (overlap)
        this.searchOverlapConflicts(section);
      if (hole)
        this.searchHoleConflicts(section);
      if (coal)
        this.searchCoalConflicts(section);
    }
  }

  private void searchOverlapConflicts(INetworkSection section) {
    IFeatureCollection<INetworkSection> fc = new FT_FeatureCollection<INetworkSection>();
    fc.addAll(sections);
    fc.remove(section);
    // first get the section envelope
    IEnvelope env = section.getGeom().envelope();
    // expand it a little bit
    env = new GM_Envelope(env.minX() - conflictOverlapSearchThresh, env.maxX()
        + conflictOverlapSearchThresh,
        env.minY() - conflictOverlapSearchThresh, env.maxY()
            + conflictOverlapSearchThresh);

    Collection<INetworkSection> neighbours = fc.select(env);
    OverlapConflict conflict = OverlapConflict.computeConflictCost(section,
        neighbours, minSeps, networkRes);
    if (conflict != null)
      if (conflict.getConflictCost() > this.conflictOverlapMinCost)
        this.overlapConflicts.add(conflict);
  }

  private void searchNodeConflicts() {
    // TODO Auto-generated method stub

  }

  private void searchHoleConflicts(INetworkSection section) {
    // TODO Auto-generated method stub

  }

  private void searchCoalConflicts(INetworkSection section) {
    // TODO Auto-generated method stub

  }

  public Set<MinimumSeparation> getMinSeps() {
    return minSeps;
  }

  public void setMinSeps(Set<MinimumSeparation> minSeps) {
    this.minSeps = minSeps;
  }

  public double getNetworkRes() {
    return networkRes;
  }

  public void setNetworkRes(double networkRes) {
    this.networkRes = networkRes;
  }
}
