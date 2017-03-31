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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.core.persistence.CollectionType;
import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BlockDensityCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BlockSizeCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BuildingHeightCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.BuildingSizeCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.CentroidCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.ChurchCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre.LimitCriterion;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ville;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.VilleImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/*
 * ###### IGN / CartAGen ###### Title: Town Description: Villes Author: J.
 * Renard Date: 04/02/2010
 */
@Entity
@Access(AccessType.PROPERTY)
public class Town extends GeneObjSurfDefault implements ITown {

  private static Logger logger = Logger.getLogger(Town.class.getName());

  /**
   * Associated Geoxygene schema object
   */
  @Transient
  private Ville geoxObj;

  @Transient
  private IFeatureCollection<IUrbanBlock> townBlocks;
  @Transient
  private StreetNetwork streetNetwork;
  @Transient
  private IFeatureCollection<DeadEndGroup> deadEnds;
  @Transient
  private Double meanBuildArea, meanBlockArea;
  private IDirectPosition centre;
  private ILineString outline;

  private DescriptiveStatistics buildAreaStats;

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public Town() {
    super();
    this.townBlocks = new FT_FeatureCollection<IUrbanBlock>();
  }

  /**
   * Constructor
   */
  public Town(Ville geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.townBlocks = new FT_FeatureCollection<IUrbanBlock>();
    this.centre = geoxObj.getGeom().centroid();
    this.outline = ((IPolygon) geoxObj.getGeom()).exteriorLineString();
  }

  public Town(IPolygon poly) {
    super();
    this.geoxObj = new VilleImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.townBlocks = new FT_FeatureCollection<IUrbanBlock>();
    this.centre = this.geoxObj.getGeom().centroid();
    this.outline = ((IPolygon) this.geoxObj.getGeom()).exteriorLineString();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public IFeatureCollection<IUrbanBlock> getTownBlocks() {
    return this.townBlocks;
  }

  @Override
  public void setTownBlocks(IFeatureCollection<IUrbanBlock> townBlocks) {
    this.townBlocks = townBlocks;
    HashSet<IUrbanBlock> coln = new HashSet<IUrbanBlock>();
    coln.addAll(townBlocks);
    for (IUrbanBlock block : coln) {
      block.setTown(this);
    }
  }

  @Override
  @Transient
  public StreetNetwork getStreetNetwork() {
    return this.streetNetwork;
  }

  @Override
  public void setStreetNetwork(StreetNetwork net) {
    this.streetNetwork = net;
  }

  @Override
  @Transient
  public IFeatureCollection<DeadEndGroup> getDeadEnds() {
    return this.deadEnds;
  }

  @Override
  public void setDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds) {
    this.deadEnds = deadEnds;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public IPolygon getGeom() {
    return super.getGeom();
  }

  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getOutline() {
    return this.outline;
  }

  public void setOutline(ILineString outline) {
    this.outline = outline;
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
  /**
   * This implementation of the method is based on the ELECTRE TRI multiple
   * criteria decision method. 7 criteria are used : block size, the average
   * building size, the average building height, the presence of a church, the
   * distance to town centroid, the intersection with town limits and obviously
   * the block density.
   */
  @Transient
  public boolean isTownCentre(IUrbanBlock block) {
    // if the block is already, colored, return true
    if (block.isColored()) {
      return true;
    }
    // compute the statistics on the town blocks
    if (this.meanBlockArea == null) {
      this.computeTownStats();
    }

    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
    Set<Criterion> criteria = new HashSet<Criterion>();
    boolean special = false;
    if (CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
        .getSourceDLM().equals(SourceDLM.SPECIAL_CARTAGEN))
      special = true;
    if (!special)
      criteria.add(new ChurchCriterion("Church"));
    criteria.add(new BlockDensityCriterion("Density"));
    criteria.add(new BlockSizeCriterion("Area"));
    if (!special)
      criteria.add(new BuildingHeightCriterion("Height"));
    criteria.add(new CentroidCriterion("Centroid"));
    criteria.add(new BuildingSizeCriterion("BuildingArea"));
    criteria.add(new LimitCriterion("Limit"));
    ConclusionIntervals conclusion = this.initCentreConclusion(criteria);
    electre.setCriteriaParamsFromCriteria(criteria);

    // make the decision
    // on remplit les valeurs courantes à partir du block courant
    Map<String, Double> valeursCourantes = new HashMap<String, Double>();
    for (Criterion crit : criteria) {
      Map<String, Object> param = this.getCentreParamMap(block, crit);
      valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
    }
    String res = electre.decision(criteria, valeursCourantes, conclusion)
        .getCategory();
    if (res.equals("very good") || res.equals("good")) {
      Town.logger.info("block " + block.getId() + " est grise !!!!");
      return true;
    }
    return false;
  }

  public void computeTownStats() {
    DescriptiveStatistics blockAreas = new DescriptiveStatistics();
    DescriptiveStatistics buildingAreas = new DescriptiveStatistics();
    this.buildAreaStats = new DescriptiveStatistics();
    for (IUrbanBlock b : this.townBlocks) {
      // exclude the non standard and too small blocks from the computation of
      // stats on block and building size
      if (!b.isStandard())
        continue;
      if (b.getGeom().area() < 2000.0)
        continue;
      DescriptiveStatistics blockStats = new DescriptiveStatistics();
      blockAreas.addValue(b.getGeom().area());
      for (IUrbanElement e : b.getUrbanElements()) {
        if (e.isDeleted()) {
          continue;
        }
        if (e.getGeom() == null) {
          continue;
        }
        blockStats.addValue(e.getGeom().area());
      }
      if (b.getUrbanElements().size() != 0) {
        buildingAreas.addValue(blockStats.getPercentile(50));
        buildAreaStats.addValue(blockStats.getMean());
      }
    }
    this.meanBlockArea = new Double(blockAreas.getPercentile(50));
    if (buildingAreas.getMean() == Double.NaN)
      this.meanBuildArea = 0.0;
    else
      this.meanBuildArea = new Double(buildingAreas.getPercentile(50));
  }

  /**
   * Builds a parameters map for the multiple criteria decision making on the
   * centreness of the town blocks.
   * 
   * @param block
   * @param crit
   * @return
   * @author GTouya
   */
  @Transient
  private Map<String, Object> getCentreParamMap(IUrbanBlock block,
      Criterion crit) {
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("block", block);
    param.put("buildingAreaStats", this.buildAreaStats);
    if (crit.getName().equals("BuildingArea")) {
      param.put("meanBuildingArea", this.meanBuildArea);
    } else if (crit.getName().equals("Centroid")) {
      IDirectPosition centrePos = this.centre;
      if (centrePos == null)
        centrePos = this.getGeom().centroid();
      param.put("centroid", centrePos);
      Point pt;
      try {
        pt = CommonAlgorithms.getPointLePlusLoin(
            (Point) JtsGeOxygene.makeJtsGeom(centrePos.toGM_Point()),
            (Polygon) JtsGeOxygene.makeJtsGeom(this.getGeom()));
        IDirectPosition dp = JtsGeOxygene
            .makeDirectPosition(pt.getCoordinateSequence());
        double maxDist = dp.distance2D(centrePos);
        param.put("max_dist", maxDist);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (crit.getName().equals("Area")) {
      param.put("meanBlockArea", this.meanBlockArea);
    } else if (crit.getName().equals("Limit")) {
      param.put("outline", this.outline);
    }
    return param;
  }

  /**
   * Method that defines the intervals that conclude a multiple criteria
   * decision with a method like ELECTRE TRI.
   * 
   * @param criteria
   * @return
   * @author GTouya
   */
  private ConclusionIntervals initCentreConclusion(Set<Criterion> criteria) {
    ConclusionIntervals conclusion = new ConclusionIntervals(criteria);
    Map<String, Double> borneSupTB = new Hashtable<String, Double>();
    Map<String, Double> borneInfTB = new Hashtable<String, Double>();
    Map<String, Double> borneInfB = new Hashtable<String, Double>();
    Map<String, Double> borneInfMy = new Hashtable<String, Double>();
    Map<String, Double> borneInfMv = new Hashtable<String, Double>();
    Map<String, Double> borneInfTMv = new Hashtable<String, Double>();

    Iterator<Criterion> itc = criteria.iterator();
    while (itc.hasNext()) {
      Criterion ct = itc.next();
      borneSupTB.put(ct.getName(), new Double(1));
      borneInfTB.put(ct.getName(), new Double(0.8));
      borneInfB.put(ct.getName(), new Double(0.6));
      borneInfMy.put(ct.getName(), new Double(0.4));
      borneInfMv.put(ct.getName(), new Double(0.2));
      borneInfTMv.put(ct.getName(), new Double(0));
    }
    conclusion.addInterval(borneInfTMv, borneInfMv, "very bad");
    conclusion.addInterval(borneInfMv, borneInfMy, "bad");
    conclusion.addInterval(borneInfMy, borneInfB, "average");
    conclusion.addInterval(borneInfB, borneInfTB, "good");
    conclusion.addInterval(borneInfTB, borneSupTB, "very good");
    return conclusion;
  }

  // ////////////////////////////////////////
  // Persistence methods //
  // ////////////////////////////////////////
  private List<Integer> townBlocksIds = new ArrayList<Integer>();

  public void setTownBlocksIds(List<Integer> townBlocksIds) {
    this.townBlocksIds = townBlocksIds;
  }

  @ElementCollection
  @CollectionTable(name = "TownBlocksIds", joinColumns = @JoinColumn(name = "town"))
  @Column(name = "TownBlocksIds")
  @Access(AccessType.FIELD)
  @EncodedRelation(targetEntity = UrbanBlock.class, invClass = ITown.class, methodName = "TownBlocks", invMethodName = "Town", nToM = false, collectionType = CollectionType.FEATURE_COLLECTION)
  public List<Integer> getTownBlocksIds() {
    return this.townBlocksIds;
  }

  @Override
  public void restoreGeoxObjects() {
    if (this.geoxObj == null)
      this.geoxObj = new VilleImpl(this.getGeom());
  }

}
