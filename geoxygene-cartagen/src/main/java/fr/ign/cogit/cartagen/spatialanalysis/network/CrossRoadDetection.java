/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.IlotImpl;

public class CrossRoadDetection {

  /**
   * the maximum angle to consider an angle as flat for crossroads. 12.5° as
   * default value.
   */
  private double flatAngle = 12.5 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle difference to consider a road is the bisector of two
   * other roads at degree 3 crossroad. 20° as default value.
   */
  private double bisAngle = 20.0 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle to consider a crossroad as fork-shaped. 70° as default
   * value.
   */
  private double forkAngle = 70.0 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle difference to consider that two roads as symmetrical in
   * relation to the third one in a degree 3 crossroad. 30° as default value.
   */
  private double symmAngle = 30.0 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle difference to consider that two road as symmetrical in in
   * relation to a common crossroad. 35° as default value.
   */
  private double symmCrossAngle = 35.0 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle difference to consider a crossroad as square. 10° as
   * default value.
   */
  private double squareAngle = 10.0 * java.lang.Math.PI / 180.0;
  /**
   * The maximum angle to consider the crossroad is y-shaped. 40° as default
   * value.
   */
  private double yAngle = 40.0 * java.lang.Math.PI / 180.0;

  private IFeatureCollection<RondPoint> rounds;
  private IFeatureCollection<PatteOie> branchs;

  public CrossRoadDetection() {
    this.rounds = new FT_FeatureCollection<>();
    FeatureType ft = new FeatureType();
    ft.setGeometryType(IPolygon.class);
    this.rounds.setFeatureType(ft);
    this.branchs = new FT_FeatureCollection<>();
    this.branchs.setFeatureType(ft);
  }

  /**
   * Create the Geoxygene roundabouts and branching crossroads then the cartagen
   * ones.
   * @param roads
   * @param blocks
   */
  public void detectRoundaboutsAndBranchingCartagen(CartAGenDataSet dataSet) {

    IFeatureCollection<TronconDeRoute> geoxRoads = new FT_FeatureCollection<TronconDeRoute>();
    IFeatureCollection<Ilot> geoxBlocks = new FT_FeatureCollection<Ilot>();

    // Construction of Geox roads
    for (INetworkSection road : dataSet.getRoads()) {
      if (!(road instanceof IRoadLine)) {
        continue;
      }
      geoxRoads.add((TronconDeRoute) road.getGeoxObj());
    }

    // construction of the topological map based on roads
    CarteTopo carteTopo = new CarteTopo("cartetopo");
    carteTopo.setBuildInfiniteFace(false);
    carteTopo.importClasseGeo(dataSet.getRoads(), true);
    carteTopo.creeNoeudsManquants(1.0);
    carteTopo.fusionNoeuds(1.0);
    carteTopo.filtreDoublons(1.0);
    carteTopo.rendPlanaire(1.0);
    carteTopo.fusionNoeuds(1.0);
    carteTopo.filtreArcsDoublons();
    carteTopo.creeTopologieFaces();

    // Construction of Geox blocks
    for (Face face : carteTopo.getListeFaces()) {
      geoxBlocks.add(new IlotImpl(face.getGeom()));
    }

    // Detection of basic roundabouts
    this.detectRoundaboutsAndBranching(geoxRoads, geoxBlocks);

    // cartagen objects creations
    // CartAGenDataSet dataSet = CartAGenDoc.getInstance().getCurrentDataset();
    IFeatureCollection<IRoadNode> nodes = this
        .getNodesFromRoadsCartAGen(dataSet.getRoads());
    // first the roundabouts
    for (RondPoint round : this.rounds) {
      dataSet.getRoundabouts().add(
          CartagenApplication.getInstance().getCreationFactory()
              .createRoundAbout(round, dataSet.getRoads(), nodes));
    }
    // then the branching crossroads
    Map<PatteOie, IBranchingCrossroad> mapForRel = new HashMap<PatteOie, IBranchingCrossroad>();
    for (PatteOie patte : this.branchs) {
      IBranchingCrossroad branch = CartagenApplication.getInstance()
          .getCreationFactory()
          .createBranchingCrossroad(patte, dataSet.getRoads(), nodes);
      dataSet.getBranchings().add(branch);
      mapForRel.put(patte, branch);
    }
    // then the relation between both
    for (IRoundAbout round : dataSet.getRoundabouts()) {
      RondPoint geox = (RondPoint) round.getGeoxObj();
      if (geox.getBranchings().size() == 0) {
        continue;
      }
      for (PatteOie patte : geox.getBranchings()) {
        IBranchingCrossroad branch = mapForRel.get(patte);
        branch.setRoundAbout(round);
        round.getBranchings().add(branch);
      }
    }

  }

  /**
   * Create the Geoxygene roundabouts and branching crossroads.
   * @param roads
   * @param blocks
   */
  public void detectRoundaboutsAndBranching(
      IFeatureCollection<TronconDeRoute> roads, IFeatureCollection<Ilot> blocks) {

    IFeatureCollection<NoeudRoutier> crossroads = this.getNodesFromRoads(roads);

    // loop on the road blocks (that can be roundabouts or branching crossroads)
    for (Ilot ilot : blocks) {
      // test if the block is a roundabout
      if (RondPoint.isRoundAbout(ilot, 40000.0)) {
        // on construit le rond-point Cartagen
        RondPoint rp = new RondPoint(ilot, roads, crossroads);
        this.rounds.add(rp);
      }
    }

    this.branchs = new FT_FeatureCollection<PatteOie>();
    for (Ilot ilot : blocks) {
      if (RondPoint.isRoundAbout(ilot, 40000.0)) {
        continue;
      }
      if (PatteOie.isBranchingCrossRoad(ilot, 2500.0, 0.5, crossroads,
          this.rounds)) {
        PatteOie br = new PatteOie(ilot, roads, crossroads);
        this.branchs.add(br);
      }
    }
    for (RondPoint rp : this.rounds) {
      rp.addBranchingCrossRoads(this.branchs);
    }

  }

  /**
   * Create the Geoxygene roundabouts and branching crossroads.
   * @param roads
   * @param blocks
   * @param branchings true if you want to compute branching crossroads
   */
  public void detectRoundaboutsAndBranching(
      IFeatureCollection<TronconDeRoute> roads,
      IFeatureCollection<Ilot> blocks, boolean branchings) {

    IFeatureCollection<NoeudRoutier> crossroads = this.getNodesFromRoads(roads);

    // loop on the road blocks (that can be roundabouts or branching crossroads)
    for (Ilot ilot : blocks) {
      // test if the block is a roundabout
      if (RondPoint.isRoundAbout(ilot, 40000.0)) {
        // on construit le rond-point Cartagen
        RondPoint rp = new RondPoint(ilot, roads, crossroads);
        this.rounds.add(rp);
      }
    }

    if (branchings) {
      this.branchs = new FT_FeatureCollection<PatteOie>();
      for (Ilot ilot : blocks) {
        if (RondPoint.isRoundAbout(ilot, 40000.0)) {
          continue;
        }
        if (PatteOie.isBranchingCrossRoad(ilot, 2500.0, 0.5, crossroads,
            this.rounds)) {
          PatteOie br = new PatteOie(ilot, roads, crossroads);
          this.branchs.add(br);
        }
      }
      for (RondPoint rp : this.rounds) {
        rp.addBranchingCrossRoads(this.branchs);
      }
    }
  }

  /**
   * Get a feature collection of the nodes from the roads.
   * @param roads
   * @return
   */
  private IFeatureCollection<NoeudRoutier> getNodesFromRoads(
      IFeatureCollection<TronconDeRoute> roads) {
    IFeatureCollection<NoeudRoutier> crossroads = new FT_FeatureCollection<NoeudRoutier>();
    for (TronconDeRoute road : roads) {
      crossroads.add((NoeudRoutier) road.getNoeudInitial());
      crossroads.add((NoeudRoutier) road.getNoeudFinal());
    }
    return crossroads;
  }

  /**
   * Get a feature collection of the nodes from the roads.
   * @param roads
   * @return
   */
  private IFeatureCollection<IRoadNode> getNodesFromRoadsCartAGen(
      IFeatureCollection<IRoadLine> roads) {
    IFeatureCollection<IRoadNode> crossroads = new FT_FeatureCollection<IRoadNode>();
    for (IRoadLine road : roads) {
      crossroads.add((IRoadNode) road.getInitialNode());
      crossroads.add((IRoadNode) road.getFinalNode());
    }
    return crossroads;
  }

  public double getFlatAngle() {
    return this.flatAngle;
  }

  public void setFlatAngle(double flatAngle) {
    this.flatAngle = flatAngle;
  }

  public double getBisAngle() {
    return this.bisAngle;
  }

  public void setBisAngle(double bisAngle) {
    this.bisAngle = bisAngle;
  }

  public double getForkAngle() {
    return this.forkAngle;
  }

  public void setForkAngle(double forkAngle) {
    this.forkAngle = forkAngle;
  }

  public double getSymmAngle() {
    return this.symmAngle;
  }

  public void setSymmAngle(double symmAngle) {
    this.symmAngle = symmAngle;
  }

  public double getSymmCrossAngle() {
    return this.symmCrossAngle;
  }

  public void setSymmCrossAngle(double symmCrossAngle) {
    this.symmCrossAngle = symmCrossAngle;
  }

  public double getSquareAngle() {
    return this.squareAngle;
  }

  public void setSquareAngle(double squareAngle) {
    this.squareAngle = squareAngle;
  }

  public double getyAngle() {
    return this.yAngle;
  }

  public void setyAngle(double yAngle) {
    this.yAngle = yAngle;
  }

  /**
   * Get the previously computed roundabouts.
   * @return
   */
  public IFeatureCollection<RondPoint> getRoundabouts() {
    return rounds;
  }

  /**
   * Get the previously computed branching crossroads
   * @return
   */
  public IFeatureCollection<PatteOie> getBranchingCrossroads() {
    return branchs;
  }
}
