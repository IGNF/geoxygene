/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminCapital;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ICompositeAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterCourse;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterNode;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.core.genericschema.land.ICompositeLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.ILandUseLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ILandUsePoint;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IBoundedArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.ILabelPoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscArea;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscLine;
import fr.ign.cogit.cartagen.core.genericschema.misc.IMiscPoint;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayRoute;
import fr.ign.cogit.cartagen.core.genericschema.railway.ITriageArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IDEMPixel;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementArea;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefTriangle;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IInterchange;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadArea;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadFacilityPoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadRoute;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IEmptySpace;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.PatteOie;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadLineImpl;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RondPoint;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.CityPartition;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.schemageo.api.activite.PointRepresentatifActiviteInteret;
import fr.ign.cogit.geoxygene.schemageo.api.activite.ZoneActiviteInteret;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.LimiteAdministrative;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.UniteAdministrativeComposite;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.UniteAdministrativeElementaire;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ville;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.AireTriage;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.LigneDeCheminDeFer;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.TronconCable;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.TronconFerre;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.CoursDEau;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.SurfaceDEau;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ElementIsole;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ZoneOccSol;
import fr.ign.cogit.geoxygene.schemageo.api.relief.CourbeDeNiveau;
import fr.ign.cogit.geoxygene.schemageo.api.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.schemageo.api.routier.CarrefourComplexe;
import fr.ign.cogit.geoxygene.schemageo.api.routier.EquipementRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.RouteItineraire;
import fr.ign.cogit.geoxygene.schemageo.api.routier.SurfaceRoute;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.PointCote;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.Micro;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

public abstract class AbstractCreationFactory {

  private static Logger logger = Logger.getLogger(AbstractCreationFactory.class
      .getName());

  // /////////////////
  // URBAN
  // /////////////////

  // Building

  public IBuilding createBuilding() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuilding");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuilding createBuilding(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuilding");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuilding createBuilding(Batiment geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuilding");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuilding createBuilding(IPolygon poly, String nature) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuilding");
    return null;
  }

  // BuildArea

  @SuppressWarnings("unused")
  public IBuildArea createBuildArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuildArea createBuildArea(AutreConstruction geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildLine");
    return null;
  }

  // BuildLine

  @SuppressWarnings("unused")
  public IBuildLine createBuildLine(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuildLine createBuildLine(AutreConstruction geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildLine");
    return null;
  }

  // BuildPoint

  @SuppressWarnings("unused")
  public IBuildPoint createBuildPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IBuildPoint createBuildPoint(AutreConstruction geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBuildPoint");
    return null;
  }

  // UrbanAlignment

  public IUrbanAlignment createUrbanAlignment() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanAlignment");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanAlignment createUrbanAlignment(List<IUrbanElement> urbanElements) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanAlignment");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanAlignment createUrbanAlignment(
      List<IUrbanElement> urbanElements, ILineString shapeLine,
      IUrbanElement initialElement, IUrbanElement finalElement) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanAlignment");
    return null;
  }

  // UrbanBlock

  public IUrbanBlock createUrbanBlock() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanBlock");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanBlock createUrbanBlock(Ilot block, CityPartition partition,
      StreetNetwork net, Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanBlock");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanBlock createUrbanBlock(IPolygon poly, CityPartition partition,
      StreetNetwork net, Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanBlock");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanBlock createUrbanBlock(IPolygon poly, ITown town,
      IFeatureCollection<IUrbanElement> urbanElements,
      IFeatureCollection<INetworkSection> sections, CityPartition partition,
      StreetNetwork net) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanBlock");
    return null;
  }

  @SuppressWarnings("unused")
  public IUrbanBlock createUrbanBlock(Ilot block, CityPartition partition,
      StreetNetwork net, IPolygon geom, Set<IUrbanElement> buildings,
      Set<IRoadLine> surroundRoads) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IUrbanBlock");
    return null;
  }

  // Town

  public ITown createTown() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITown");
    return null;
  }

  @SuppressWarnings("unused")
  public ITown createTown(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITown");
    return null;
  }

  @SuppressWarnings("unused")
  public ITown createTown(Ville geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITown");
    return null;
  }

  // EmptySpace

  @SuppressWarnings("unused")
  public IEmptySpace createEmptySpace(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IEmptySpace");
    return null;
  }

  // SportsField

  @SuppressWarnings("unused")
  public ISportsField createSportsField(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISportsField");
    return null;
  }

  // /////////////////
  // NETWORK
  // /////////////////

  // Network

  public INetwork createNetwork() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for INetwork");
    return null;
  }

  @SuppressWarnings("unused")
  public INetwork createNetwork(Reseau res) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for INetwork");
    return null;
  }

  // NetworkFace

  @SuppressWarnings("unused")
  public INetworkFace createNetworkFace(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for INetworkFace");
    return null;
  }

  @SuppressWarnings("unused")
  public INetworkFace createNetworkFace(Face geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for INetworkFace");
    return null;
  }

  // /////////////////
  // ROAD
  // /////////////////

  // RoadLine

  public IRoadLine createRoadLine() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadLine createRoadLine(ILineString line, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadLine createRoadLine(ILineString line, int importance, int symbolId) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadLine createRoadLine(RoadLineImpl geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadLine createRoadLine(TronconDeRoute geoxObj, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadLine createRoadLine(TronconDeRoute geoxObj, int importance,
      int symbolId) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadLine");
    return null;
  }

  // RoadNode

  public IRoadNode createRoadNode() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadNode createRoadNode(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadNode createRoadNode(Noeud noeud) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadNode createRoadNode(NoeudRoutier geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadNode");
    return null;
  }

  // RoadRoute

  @SuppressWarnings("unused")
  public IRoadRoute createRoadRoute(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadRoute");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadRoute createRoadRoute(RouteItineraire geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadRoute");
    return null;
  }

  // RoadArea

  public IRoadArea createRoadArea() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadArea createRoadArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadArea createRoadArea(SurfaceRoute geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadArea");
    return null;
  }

  // RoadFacilityPoint

  public IRoadFacilityPoint createRoadFacilityPoint() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadFacilityPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadFacilityPoint createRoadFacilityPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadFacilityPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoadFacilityPoint createRoadFacilityPoint(EquipementRoutier geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoadFacilityPoint");
    return null;
  }

  // BranchingCrossroad

  public IBranchingCrossroad createBranchingCrossroad() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBranchingCrossroad");
    return null;
  }

  @SuppressWarnings("unused")
  public IBranchingCrossroad createBranchingCrossroad(PatteOie geoxObj,
      Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBranchingCrossroad");
    return null;
  }

  // RoundAbout

  public IRoundAbout createRoundAbout() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoundAbout");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoundAbout createRoundAbout(RondPoint geoxObj,
      Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoundAbout");
    return null;
  }

  @SuppressWarnings("unused")
  public IRoundAbout createRoundAbout(IPolygon geom,
      Collection<IRoadLine> externalRoads, Collection<IRoadLine> internalRoads,
      Collection<INetworkNode> initialNodes) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRoundAbout");
    return null;
  }

  // Interchange

  @SuppressWarnings("unused")
  public IInterchange createInterchange(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IInterchange");
    return null;
  }

  @SuppressWarnings("unused")
  public IInterchange createInterchange(CarrefourComplexe geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IInterchange");
    return null;
  }

  // Path

  @SuppressWarnings("unused")
  public IPathLine createPath(ILineString line, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IPath");
    return null;
  }

  @SuppressWarnings("unused")
  public IPathLine createPath(ILineString line, int importance, int symbolId) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IPath");
    return null;
  }

  // /////////////////
  // RAILWAY
  // /////////////////

  // RailwayLine

  public IRailwayLine createRailwayLine() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRailwayLine createRailwayLine(TronconFerre geoxObj, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IRailwayLine createRailwayLine(ILineString line, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayLine");
    return null;
  }

  // ElectricityLine

  public IElectricityLine createElectricityLine() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IElectricityLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IElectricityLine createElectricityLine(ArcReseau geoxObj,
      int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IElectricityLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IElectricityLine createElectricityLine(ILineString line, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IElectricityLine");
    return null;
  }

  // Cable

  public ICable createCable() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICable");
    return null;
  }

  @SuppressWarnings("unused")
  public ICable createCable(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICable");
    return null;
  }

  @SuppressWarnings("unused")
  public ICable createCable(TronconCable geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICable");
    return null;
  }

  // RailwayNode

  public IRailwayNode createRailwayNode() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IRailwayNode createRailwayNode(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IRailwayNode createRailwayNode(Noeud noeud) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayNode");
    return null;
  }

  // RailwayRoute

  @SuppressWarnings("unused")
  public IRailwayRoute createRailwayRoute(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayRoute");
    return null;
  }

  @SuppressWarnings("unused")
  public IRailwayRoute createRailwayRoute(LigneDeCheminDeFer geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRailwayRoute");
    return null;
  }

  // TriageArea

  public ITriageArea createTriageArea() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITriageArea");
    return null;
  }

  @SuppressWarnings("unused")
  public ITriageArea createTriageArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITriageArea");
    return null;
  }

  @SuppressWarnings("unused")
  public ITriageArea createTriageArea(AireTriage geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITriageArea");
    return null;
  }

  // /////////////////
  // HYDRO
  // /////////////////

  // WaterLine

  public IWaterLine createWaterLine() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterLine createWaterLine(ILineString line, int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterLine createWaterLine(TronconHydrographique geoxObj,
      int importance) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterLine");
    return null;
  }

  // WaterCourse

  @SuppressWarnings("unused")
  public IWaterCourse createWaterCourse(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterCourse");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterCourse createWaterCourse(CoursDEau geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterCourse");
    return null;
  }

  // WaterNode

  public IWaterNode createWaterNode() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterNode createWaterNode(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterNode");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterNode createWaterNode(Noeud noeud) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterNode");
    return null;
  }

  // WaterPoint

  @SuppressWarnings("unused")
  public IWaterPoint createWaterPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterPoint createWaterPoint(PointDEau geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterPoint");
    return null;
  }

  // WaterArea

  public IWaterArea createWaterArea() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterArea createWaterArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IWaterArea createWaterArea(SurfaceDEau geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IWaterArea");
    return null;
  }

  // /////////////////
  // RELIEF
  // /////////////////

  // ContourLine

  @SuppressWarnings("unused")
  public IContourLine createContourLine(ILineString line, double value) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IContourLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IContourLine createContourLine(CourbeDeNiveau geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IContourLine");
    return null;
  }

  // SpotHeight

  @SuppressWarnings("unused")
  public ISpotHeight createSpotHeight(IPoint point, double value) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISpotHeight");
    return null;
  }

  @SuppressWarnings("unused")
  public ISpotHeight createSpotHeight(PointCote geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISpotHeight");
    return null;
  }

  // DEMPixel

  @SuppressWarnings("unused")
  public IDEMPixel createDEMPixel(double x, double y, double z) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IDEMPixel");
    return null;
  }

  // ReliefField

  @SuppressWarnings("unused")
  public IReliefField createReliefField(ChampContinu champ) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefField");
    return null;
  }

  // ReliefElementPoint

  @SuppressWarnings("unused")
  public IReliefElementPoint createReliefElementPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IReliefElementPoint createReliefElementPoint(
      ElementCaracteristiqueDuRelief geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementPoint");
    return null;
  }

  // ReliefElementLine

  @SuppressWarnings("unused")
  public IReliefElementLine createReliefElementLine(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IReliefElementLine createReliefElementLine(
      ElementCaracteristiqueDuRelief geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementLine");
    return null;
  }

  // ReliefElementArea

  @SuppressWarnings("unused")
  public IReliefElementArea createReliefElementArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IReliefElementArea createReliefElementArea(
      ElementCaracteristiqueDuRelief geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefElementArea");
    return null;
  }

  // ReliefTriangle

  @SuppressWarnings("unused")
  public IReliefTriangle createReliefTriangle(IDirectPosition pt1,
      IDirectPosition pt2, IDirectPosition pt3) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IReliefTriangle");
    return null;
  }

  // /////////////////
  // LAND USE
  // /////////////////

  // SimpleLandUseArea

  @SuppressWarnings("unused")
  public ISimpleLandUseArea createSimpleLandUseArea(IPolygon poly, int type) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISimpleLandUseArea");
    return null;
  }

  @SuppressWarnings("unused")
  public ISimpleLandUseArea createSimpleLandUseArea(ZoneOccSol geoxObj, int type) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISimpleLandUseArea");
    return null;
  }

  // CompositeLandUseArea

  @SuppressWarnings("unused")
  public ICompositeLandUseArea createCompositeLandUseArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICompositeLandUseArea");
    return null;
  }

  @SuppressWarnings("unused")
  public ICompositeLandUseArea createCompositeLandUseArea(ZoneOccSol geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICompositeLandUseArea");
    return null;
  }

  // LandUsePoint

  @SuppressWarnings("unused")
  public ILandUsePoint createLandUsePoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILandUsePoint");
    return null;
  }

  @SuppressWarnings("unused")
  public ILandUsePoint createLandUsePoint(ElementIsole geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILandUsePoint");
    return null;
  }

  // LandUseLine

  @SuppressWarnings("unused")
  public ILandUseLine createLandUseLine(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILandUseLine");
    return null;
  }

  @SuppressWarnings("unused")
  public ILandUseLine createLandUseLine(ElementIsole geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILandUseLine");
    return null;
  }

  // /////////////////
  // ADMIN
  // /////////////////

  // SimpleAdminUnit

  @SuppressWarnings("unused")
  public ISimpleAdminUnit createSimpleAdminUnit(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISimpleAdminUnit");
    return null;
  }

  @SuppressWarnings("unused")
  public ISimpleAdminUnit createSimpleAdminUnit(
      UniteAdministrativeElementaire geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ISimpleAdminUnit");
    return null;
  }

  // CompositeAdminUnit

  @SuppressWarnings("unused")
  public ICompositeAdminUnit createCompositeAdminUnit(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICompositeAdminUnit");
    return null;
  }

  @SuppressWarnings("unused")
  public ICompositeAdminUnit createCompositeAdminUnit(
      UniteAdministrativeComposite geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ICompositeAdminUnit");
    return null;
  }

  // AdminLimit

  @SuppressWarnings("unused")
  public IAdminLimit createAdminLimit(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAdminLimit");
    return null;
  }

  @SuppressWarnings("unused")
  public IAdminLimit createAdminLimit(LimiteAdministrative geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAdminLimit");
    return null;
  }

  // AdminCapital

  public IAdminCapital createAdminCapital() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAdminCapital");
    return null;
  }

  @SuppressWarnings("unused")
  public IAdminCapital createAdminCapital(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAdminCapital");
    return null;
  }

  @SuppressWarnings("unused")
  public IAdminCapital createAdminCapital(ChefLieu geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAdminCapital");
    return null;
  }

  // /////////////////
  // MISC
  // /////////////////

  // LabelPoint

  public ILabelPoint createLabelPoint() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILabelPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public ILabelPoint createLabelPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILabelPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public ILabelPoint createLabelPoint(PointRepresentatifActiviteInteret geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ILabelPoint");
    return null;
  }

  // MiscPoint

  public IMiscPoint createMiscPoint() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscPoint createMiscPoint(IPoint point) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscPoint");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscPoint createMiscPoint(AutreConstruction geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscPoint");
    return null;
  }

  // MiscLine

  public IMiscLine createMiscLine() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscLine createMiscLine(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscLine");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscLine createMiscLine(AutreConstruction geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscLine");
    return null;
  }

  // MiscArea

  public IMiscArea createMiscArea() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscArea createMiscArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IMiscArea createMiscArea(Micro geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMiscArea");
    return null;
  }

  // BoundedArea

  public IBoundedArea createBoundedArea() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBoundedArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IBoundedArea createBoundedArea(IPolygon poly) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBoundedArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IBoundedArea createBoundedArea(ZoneActiviteInteret geoxObj) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IBoundedArea");
    return null;
  }

  // airports
  @SuppressWarnings("unused")
  public IAirportArea createAirportArea(IPolygon geom) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IAirportArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IRunwayArea createRunwayArea(IPolygon geom) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRunwayArea");
    return null;
  }

  @SuppressWarnings("unused")
  public IRunwayLine createRunwayLine(ILineString geom) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IRunwayLine");
    return null;
  }

  @SuppressWarnings("unused")
  public ITaxiwayArea createTaxiwayArea(IPolygon simple, TaxiwayType type) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITaxiwayArea");
    return null;
  }

  @SuppressWarnings("unused")
  public ITaxiwayLine createTaxiwayLine(ILineString geom, TaxiwayType type) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for ITaxiwayLine");
    return null;
  }

  // /////////////////
  // MASK
  // /////////////////

  public IMask createMask() {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMask");
    return null;
  }

  @SuppressWarnings("unused")
  public IMask createMask(ILineString line) {
    AbstractCreationFactory.logger
        .error("Non implemented creation factory method for IMask");
    return null;
  }

}
