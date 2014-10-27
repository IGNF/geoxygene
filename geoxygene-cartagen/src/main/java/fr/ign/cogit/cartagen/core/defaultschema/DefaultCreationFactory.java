/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.defaultschema.admin.AdminCapital;
import fr.ign.cogit.cartagen.core.defaultschema.admin.AdminLimit;
import fr.ign.cogit.cartagen.core.defaultschema.admin.CompositeAdminUnit;
import fr.ign.cogit.cartagen.core.defaultschema.admin.SimpleAdminUnit;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.CoastLine;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterArea;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterCourse;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterLine;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterNode;
import fr.ign.cogit.cartagen.core.defaultschema.hydro.WaterPoint;
import fr.ign.cogit.cartagen.core.defaultschema.land.CompositeLandUseArea;
import fr.ign.cogit.cartagen.core.defaultschema.land.LandUseLine;
import fr.ign.cogit.cartagen.core.defaultschema.land.LandUsePoint;
import fr.ign.cogit.cartagen.core.defaultschema.land.SimpleLandUseArea;
import fr.ign.cogit.cartagen.core.defaultschema.misc.BoundedArea;
import fr.ign.cogit.cartagen.core.defaultschema.misc.LabelPoint;
import fr.ign.cogit.cartagen.core.defaultschema.misc.MiscArea;
import fr.ign.cogit.cartagen.core.defaultschema.misc.MiscLine;
import fr.ign.cogit.cartagen.core.defaultschema.misc.MiscPoint;
import fr.ign.cogit.cartagen.core.defaultschema.network.Network;
import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkFace;
import fr.ign.cogit.cartagen.core.defaultschema.partition.Mask;
import fr.ign.cogit.cartagen.core.defaultschema.railway.Cable;
import fr.ign.cogit.cartagen.core.defaultschema.railway.ElectricityLine;
import fr.ign.cogit.cartagen.core.defaultschema.railway.RailwayLine;
import fr.ign.cogit.cartagen.core.defaultschema.railway.RailwayNode;
import fr.ign.cogit.cartagen.core.defaultschema.railway.RailwayRoute;
import fr.ign.cogit.cartagen.core.defaultschema.railway.TriageArea;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ContourLine;
import fr.ign.cogit.cartagen.core.defaultschema.relief.DEMPixel;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefElementArea;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefElementLine;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefElementPoint;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefField;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefTriangle;
import fr.ign.cogit.cartagen.core.defaultschema.relief.SpotHeight;
import fr.ign.cogit.cartagen.core.defaultschema.road.BranchingCrossRoad;
import fr.ign.cogit.cartagen.core.defaultschema.road.BridgePoint;
import fr.ign.cogit.cartagen.core.defaultschema.road.Interchange;
import fr.ign.cogit.cartagen.core.defaultschema.road.Path;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadArea;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadFacilityPoint;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadLine;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadNode;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoadRoute;
import fr.ign.cogit.cartagen.core.defaultschema.road.RoundAbout;
import fr.ign.cogit.cartagen.core.defaultschema.urban.BuildArea;
import fr.ign.cogit.cartagen.core.defaultschema.urban.BuildLine;
import fr.ign.cogit.cartagen.core.defaultschema.urban.BuildPoint;
import fr.ign.cogit.cartagen.core.defaultschema.urban.Building;
import fr.ign.cogit.cartagen.core.defaultschema.urban.EmptySpace;
import fr.ign.cogit.cartagen.core.defaultschema.urban.Town;
import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanAlignment;
import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminCapital;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ICompositeAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.cartagen.core.genericschema.energy.IElectricityLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
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
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
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

public class DefaultCreationFactory extends AbstractCreationFactory {

  private static Logger logger = Logger.getLogger(DefaultCreationFactory.class
      .getName());

  // /////////////////
  // URBAN
  // /////////////////

  // Building

  @Override
  public IBuilding createBuilding() {
    return new Building();
  }

  @Override
  public IBuilding createBuilding(IPolygon poly) {
    return new Building(poly);
  }

  @Override
  public IBuilding createBuilding(Batiment geoxObj) {
    return new Building(geoxObj);
  }

  @Override
  public IBuilding createBuilding(IPolygon poly, String nature) {
    return new Building(poly, nature);
  }

  // BuildArea

  @Override
  public IBuildArea createBuildArea(IPolygon poly) {
    return new BuildArea(poly);
  }

  @Override
  public IBuildArea createBuildArea(AutreConstruction geoxObj) {
    return new BuildArea(geoxObj);
  }

  // BuildLine

  @Override
  public IBuildLine createBuildLine(ILineString line) {
    return new BuildLine(line);
  }

  @Override
  public IBuildLine createBuildLine(AutreConstruction geoxObj) {
    return new BuildLine(geoxObj);
  }

  // BuildPoint

  @Override
  public IBuildPoint createBuildPoint(IPoint point) {
    return new BuildPoint(point);
  }

  @Override
  public IBuildPoint createBuildPoint(AutreConstruction geoxObj) {
    return new BuildPoint(geoxObj);
  }

  // UrbanAlignment

  @Override
  public IUrbanAlignment createUrbanAlignment() {
    return new UrbanAlignment();
  }

  @Override
  public IUrbanAlignment createUrbanAlignment(List<IUrbanElement> urbanElements) {
    return new UrbanAlignment(urbanElements);
  }

  @Override
  public IUrbanAlignment createUrbanAlignment(
      List<IUrbanElement> urbanElements, ILineString shapeLine,
      IUrbanElement initialElement, IUrbanElement finalElement) {
    return new UrbanAlignment(urbanElements, shapeLine, initialElement,
        finalElement);
  }

  // UrbanBlock

  @Override
  public IUrbanBlock createUrbanBlock() {
    return new UrbanBlock();
  }

  @Override
  public IUrbanBlock createUrbanBlock(Ilot block, CityPartition partition,
      StreetNetwork net, Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    return new UrbanBlock(block, partition, net, buildings, surroundRoads);
  }

  @Override
  public IUrbanBlock createUrbanBlock(IPolygon poly, CityPartition partition,
      StreetNetwork net, Collection<IUrbanElement> buildings,
      Collection<IRoadLine> surroundRoads) {
    return new UrbanBlock(poly, partition, net, buildings, surroundRoads);
  }

  @Override
  public IUrbanBlock createUrbanBlock(IPolygon poly, ITown town,
      IFeatureCollection<IUrbanElement> urbanElements,
      IFeatureCollection<INetworkSection> sections, CityPartition partition,
      StreetNetwork net) {
    return new UrbanBlock(poly, town, urbanElements, sections, partition, net);
  }

  @Override
  public IUrbanBlock createUrbanBlock(Ilot block, CityPartition partition,
      StreetNetwork net, IPolygon geom, Set<IUrbanElement> buildings,
      Set<IRoadLine> surroundRoads) {
    return new UrbanBlock(block, partition, net, geom, buildings, surroundRoads);
  }

  // Town

  @Override
  public ITown createTown() {
    return new Town();
  }

  @Override
  public ITown createTown(IPolygon poly) {
    return new Town(poly);
  }

  @Override
  public ITown createTown(Ville geoxObj) {
    return new Town(geoxObj);
  }

  // EmptySpace

  @Override
  public IEmptySpace createEmptySpace(IPolygon poly) {
    return new EmptySpace(poly);
  }

  // /////////////////
  // NETWORK
  // /////////////////

  // Network

  @Override
  public INetwork createNetwork() {
    return new Network();
  }

  @Override
  public INetwork createNetwork(Reseau res) {
    return new Network(res);
  }

  // NetworkFace

  @Override
  public INetworkFace createNetworkFace(IPolygon poly) {
    return new NetworkFace(poly);
  }

  @Override
  public INetworkFace createNetworkFace(Face geoxObj) {
    return new NetworkFace(geoxObj);
  }

  // /////////////////
  // ROAD
  // /////////////////

  // RoadLine

  @Override
  public IRoadLine createRoadLine() {
    return new RoadLine();
  }

  @Override
  public IRoadLine createRoadLine(ILineString line, int importance) {
    return new RoadLine(line, importance);
  }

  @Override
  public IRoadLine createRoadLine(ILineString line, int importance, int symbolId) {
    return new RoadLine(line, importance, symbolId);
  }

  @Override
  public IRoadLine createRoadLine(RoadLineImpl geoxObj) {
    return new RoadLine(geoxObj);
  }

  @Override
  public IRoadLine createRoadLine(TronconDeRoute geoxObj, int importance) {
    return new RoadLine(geoxObj, importance);
  }

  @Override
  public IRoadLine createRoadLine(TronconDeRoute geoxObj, int importance,
      int symbolId) {
    return new RoadLine(geoxObj, importance, symbolId);
  }

  // RoadNode

  @Override
  public IRoadNode createRoadNode() {
    return new RoadNode();
  }

  @Override
  public IRoadNode createRoadNode(IPoint point) {
    return new RoadNode(point);
  }

  @Override
  public IRoadNode createRoadNode(Noeud noeud) {
    return new RoadNode(noeud);
  }

  @Override
  public IRoadNode createRoadNode(NoeudRoutier geoxObj) {
    return new RoadNode(geoxObj);
  }

  // RoadRoute

  @Override
  public IRoadRoute createRoadRoute(ILineString line) {
    return new RoadRoute(line);
  }

  @Override
  public IRoadRoute createRoadRoute(RouteItineraire geoxObj) {
    return new RoadRoute(geoxObj);
  }

  // RoadArea

  @Override
  public IRoadArea createRoadArea() {
    return new RoadArea();
  }

  @Override
  public IRoadArea createRoadArea(IPolygon poly) {
    return new RoadArea(poly);
  }

  @Override
  public IRoadArea createRoadArea(SurfaceRoute geoxObj) {
    return new RoadArea(geoxObj);
  }

  // RoadFacilityPoint

  @Override
  public IRoadFacilityPoint createRoadFacilityPoint() {
    return new RoadFacilityPoint();
  }

  @Override
  public IRoadFacilityPoint createRoadFacilityPoint(IPoint point) {
    return new RoadFacilityPoint(point);
  }

  @Override
  public IRoadFacilityPoint createRoadFacilityPoint(EquipementRoutier geoxObj) {
    return new RoadFacilityPoint(geoxObj);
  }

  // BranchingCrossroad

  @Override
  public IBranchingCrossroad createBranchingCrossroad() {
    return new BranchingCrossRoad();
  }

  @Override
  public IBranchingCrossroad createBranchingCrossroad(PatteOie geoxObj,
      Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
    return new BranchingCrossRoad(geoxObj, roads, nodes);
  }

  // RoundAbout

  @Override
  public IRoundAbout createRoundAbout() {
    return new RoundAbout();
  }

  @Override
  public IRoundAbout createRoundAbout(RondPoint geoxObj,
      Collection<IRoadLine> roads, Collection<IRoadNode> nodes) {
    return new RoundAbout(geoxObj, roads, nodes);
  }

  @Override
  public IRoundAbout createRoundAbout(IPolygon geom,
      Collection<IRoadLine> externalRoads, Collection<IRoadLine> internalRoads,
      Collection<INetworkNode> initialNodes) {
    return new RoundAbout(geom, externalRoads, internalRoads, initialNodes);
  }

  // Interchange

  @Override
  public IInterchange createInterchange(IPolygon poly) {
    return new Interchange(poly);
  }

  @Override
  public IInterchange createInterchange(CarrefourComplexe geoxObj) {
    return new Interchange(geoxObj);
  }

  // Path

  @Override
  public IPathLine createPath(ILineString line, int importance, int symbolId) {
    return new Path(line, importance, symbolId);
  }

  @Override
  public IPathLine createPath(ILineString line, int importance) {
    return new Path(line, importance);
  }

  // Bridges
  @Override
  public IBridgePoint createBridgePoint(IPoint point) {
    return new BridgePoint(point);
  }

  // /////////////////
  // RAILWAY
  // /////////////////

  // RailwayLine

  @Override
  public IRailwayLine createRailwayLine() {
    return new RailwayLine();
  }

  @Override
  public IRailwayLine createRailwayLine(TronconFerre geoxObj, int importance) {
    return new RailwayLine(geoxObj, importance);
  }

  @Override
  public IRailwayLine createRailwayLine(ILineString line, int importance) {
    return new RailwayLine(line, importance);
  }

  // ElectricityLine

  @Override
  public IElectricityLine createElectricityLine() {
    return new ElectricityLine();
  }

  @Override
  public IElectricityLine createElectricityLine(ArcReseau geoxObj,
      int importance) {
    return new ElectricityLine(geoxObj, importance);
  }

  @Override
  public IElectricityLine createElectricityLine(ILineString line, int importance) {
    return new ElectricityLine(line, importance);
  }

  // Cable

  @Override
  public ICable createCable() {
    return new Cable();
  }

  @Override
  public ICable createCable(ILineString line) {
    return new Cable(line);
  }

  @Override
  public ICable createCable(TronconCable geoxObj) {
    return new Cable(geoxObj);
  }

  // RailwayNode

  @Override
  public IRailwayNode createRailwayNode() {
    return new RailwayNode();
  }

  @Override
  public IRailwayNode createRailwayNode(IPoint point) {
    return new RailwayNode(point);
  }

  @Override
  public IRailwayNode createRailwayNode(Noeud noeud) {
    return new RailwayNode(noeud);
  }

  // RailwayRoute

  @Override
  public IRailwayRoute createRailwayRoute(ILineString line) {
    return new RailwayRoute(line);
  }

  @Override
  public IRailwayRoute createRailwayRoute(LigneDeCheminDeFer geoxObj) {
    return new RailwayRoute(geoxObj);
  }

  // TriageArea

  @Override
  public ITriageArea createTriageArea() {
    return new TriageArea();
  }

  @Override
  public ITriageArea createTriageArea(IPolygon poly) {
    return new TriageArea(poly);
  }

  @Override
  public ITriageArea createTriageArea(AireTriage geoxObj) {
    return new TriageArea(geoxObj);
  }

  // /////////////////
  // HYDRO
  // /////////////////

  // WaterLine

  @Override
  public IWaterLine createWaterLine() {
    return new WaterLine();
  }

  @Override
  public IWaterLine createWaterLine(ILineString line, int importance) {
    return new WaterLine(line, importance);
  }

  @Override
  public IWaterLine createWaterLine(TronconHydrographique geoxObj,
      int importance) {
    return new WaterLine(geoxObj, importance);
  }

  // WaterCourse

  @Override
  public IWaterCourse createWaterCourse(ILineString line) {
    return new WaterCourse(line);
  }

  @Override
  public IWaterCourse createWaterCourse(CoursDEau geoxObj) {
    return new WaterCourse(geoxObj);
  }

  // WaterNode

  @Override
  public IWaterNode createWaterNode() {
    return new WaterNode();
  }

  @Override
  public IWaterNode createWaterNode(IPoint point) {
    return new WaterNode(point);
  }

  @Override
  public IWaterNode createWaterNode(Noeud noeud) {
    return new WaterNode(noeud);
  }

  // WaterPoint

  @Override
  public IWaterPoint createWaterPoint(IPoint point) {
    return new WaterPoint(point);
  }

  @Override
  public IWaterPoint createWaterPoint(PointDEau geoxObj) {
    return new WaterPoint(geoxObj);
  }

  // WaterArea

  @Override
  public IWaterArea createWaterArea() {
    return new WaterArea();
  }

  @Override
  public IWaterArea createWaterArea(IPolygon poly) {
    return new WaterArea(poly);
  }

  @Override
  public IWaterArea createWaterArea(SurfaceDEau geoxObj) {
    return new WaterArea(geoxObj);
  }

  // /////////////////
  // RELIEF
  // /////////////////

  // ContourLine

  @Override
  public IContourLine createContourLine(ILineString line, double value) {
    return new ContourLine(line, value);
  }

  @Override
  public IContourLine createContourLine(CourbeDeNiveau geoxObj) {
    return new ContourLine(geoxObj);
  }

  // SpotHeight

  @Override
  public ISpotHeight createSpotHeight(IPoint point, double value) {
    return new SpotHeight(point, value);
  }

  @Override
  public ISpotHeight createSpotHeight(PointCote geoxObj) {
    return new SpotHeight(geoxObj);
  }

  // DEMPixel

  @Override
  public IDEMPixel createDEMPixel(double x, double y, double z) {
    return new DEMPixel(x, y, z);
  }

  // ReliefField

  @Override
  public IReliefField createReliefField(ChampContinu champ) {
    return new ReliefField(champ);
  }

  // ReliefElementPoint

  @Override
  public IReliefElementPoint createReliefElementPoint(IPoint point) {
    return new ReliefElementPoint(point);
  }

  @Override
  public IReliefElementPoint createReliefElementPoint(
      ElementCaracteristiqueDuRelief geoxObj) {
    return new ReliefElementPoint(geoxObj);
  }

  // ReliefElementLine

  @Override
  public IReliefElementLine createReliefElementLine(ILineString line) {
    return new ReliefElementLine(line);
  }

  @Override
  public IReliefElementLine createReliefElementLine(
      ElementCaracteristiqueDuRelief geoxObj) {
    return new ReliefElementLine(geoxObj);
  }

  // ReliefElementArea

  @Override
  public IReliefElementArea createReliefElementArea(IPolygon poly) {
    return new ReliefElementArea(poly);
  }

  @Override
  public IReliefElementArea createReliefElementArea(
      ElementCaracteristiqueDuRelief geoxObj) {
    return new ReliefElementArea(geoxObj);
  }

  // ReliefTriangle

  @Override
  public IReliefTriangle createReliefTriangle(IDirectPosition pt1,
      IDirectPosition pt2, IDirectPosition pt3) {
    return new ReliefTriangle(pt1, pt2, pt3);
  }

  // /////////////////
  // LAND USE
  // /////////////////

  // SimpleLandUseArea

  @Override
  public ISimpleLandUseArea createSimpleLandUseArea(IPolygon poly, int type) {
    return new SimpleLandUseArea(poly, type);
  }

  @Override
  public ISimpleLandUseArea createSimpleLandUseArea(ZoneOccSol geoxObj, int type) {
    return new SimpleLandUseArea(geoxObj, type);
  }

  // CompositeLandUseArea

  @Override
  public ICompositeLandUseArea createCompositeLandUseArea(IPolygon poly) {
    return new CompositeLandUseArea(poly);
  }

  @Override
  public ICompositeLandUseArea createCompositeLandUseArea(ZoneOccSol geoxObj) {
    return new CompositeLandUseArea(geoxObj);
  }

  // LandUsePoint

  @Override
  public ILandUsePoint createLandUsePoint(IPoint point) {
    return new LandUsePoint(point);
  }

  @Override
  public ILandUsePoint createLandUsePoint(ElementIsole geoxObj) {
    return new LandUsePoint(geoxObj);
  }

  // LandUseLine

  @Override
  public ILandUseLine createLandUseLine(ILineString line) {
    return new LandUseLine(line);
  }

  @Override
  public ILandUseLine createLandUseLine(ElementIsole geoxObj) {
    return new LandUseLine(geoxObj);
  }

  // /////////////////
  // ADMIN
  // /////////////////

  // SimpleAdminUnit

  @Override
  public ISimpleAdminUnit createSimpleAdminUnit(IPolygon poly) {
    return new SimpleAdminUnit(poly);
  }

  @Override
  public ISimpleAdminUnit createSimpleAdminUnit(
      UniteAdministrativeElementaire geoxObj) {
    return new SimpleAdminUnit(geoxObj);
  }

  // CompositeAdminUnit

  @Override
  public ICompositeAdminUnit createCompositeAdminUnit(IPolygon poly) {
    return new CompositeAdminUnit(poly);
  }

  @Override
  public ICompositeAdminUnit createCompositeAdminUnit(
      UniteAdministrativeComposite geoxObj) {
    return new CompositeAdminUnit(geoxObj);
  }

  // AdminLimit

  @Override
  public IAdminLimit createAdminLimit(ILineString line) {
    return new AdminLimit(line);
  }

  @Override
  public IAdminLimit createAdminLimit(LimiteAdministrative geoxObj) {
    return new AdminLimit(geoxObj);
  }

  // AdminCapital

  @Override
  public IAdminCapital createAdminCapital() {
    return new AdminCapital();
  }

  @Override
  public IAdminCapital createAdminCapital(IPoint point) {
    return new AdminCapital(point);
  }

  @Override
  public IAdminCapital createAdminCapital(ChefLieu geoxObj) {
    return new AdminCapital(geoxObj);
  }

  // /////////////////
  // MISC
  // /////////////////

  // LabelPoint

  @Override
  public ILabelPoint createLabelPoint() {
    return new LabelPoint();
  }

  @Override
  public ILabelPoint createLabelPoint(IPoint point) {
    return new LabelPoint(point);
  }

  @Override
  public ILabelPoint createLabelPoint(PointRepresentatifActiviteInteret geoxObj) {
    return new LabelPoint(geoxObj);
  }

  // MiscPoint

  @Override
  public IMiscPoint createMiscPoint() {
    return new MiscPoint();
  }

  @Override
  public IMiscPoint createMiscPoint(IPoint point) {
    DefaultCreationFactory.logger
        .error("Non implemented creation factory method for IMiscPoint");
    return null;
  }

  @Override
  public IMiscPoint createMiscPoint(AutreConstruction geoxObj) {
    return new MiscPoint(geoxObj);
  }

  // MiscLine

  @Override
  public IMiscLine createMiscLine() {
    return new MiscLine();
  }

  @Override
  public IMiscLine createMiscLine(ILineString line) {
    return new MiscLine(line);
  }

  @Override
  public IMiscLine createMiscLine(AutreConstruction geoxObj) {
    return new MiscLine(geoxObj);
  }

  // MiscArea

  @Override
  public IMiscArea createMiscArea() {
    return new MiscArea();
  }

  @Override
  public IMiscArea createMiscArea(IPolygon poly) {
    return new MiscArea(poly);
  }

  @Override
  public IMiscArea createMiscArea(Micro geoxObj) {
    return new MiscArea(geoxObj);
  }

  // BoundedArea

  @Override
  public IBoundedArea createBoundedArea() {
    return new BoundedArea();
  }

  @Override
  public IBoundedArea createBoundedArea(IPolygon poly) {
    return new BoundedArea(poly);
  }

  @Override
  public IBoundedArea createBoundedArea(ZoneActiviteInteret geoxObj) {
    return new BoundedArea(geoxObj);
  }

  // /////////////////
  // MASK
  // /////////////////

  @Override
  public IMask createMask() {
    return new Mask();
  }

  @Override
  public IMask createMask(ILineString line) {
    return new Mask(line);
  }

  @Override
  public ICoastLine createCoastline(ILineString line) {
    ICoastLine coastLine = new CoastLine();
    coastLine.setGeom(line);
    return coastLine;
  }

}
