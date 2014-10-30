/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.schema;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.defaultschema.network.Network;
import fr.ign.cogit.cartagen.core.defaultschema.relief.ReliefField;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefField;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.schema.aero.OsmAirportArea;
import fr.ign.cogit.geoxygene.osm.schema.aero.OsmRunwayArea;
import fr.ign.cogit.geoxygene.osm.schema.aero.OsmRunwayLine;
import fr.ign.cogit.geoxygene.osm.schema.aero.OsmTaxiwayArea;
import fr.ign.cogit.geoxygene.osm.schema.aero.OsmTaxiwayLine;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmHospital;
import fr.ign.cogit.geoxygene.osm.schema.amenity.OsmSchool;
import fr.ign.cogit.geoxygene.osm.schema.hydro.OsmWaterArea;
import fr.ign.cogit.geoxygene.osm.schema.hydro.OsmWaterLine;
import fr.ign.cogit.geoxygene.osm.schema.landuse.OsmSimpleLandUseArea;
import fr.ign.cogit.geoxygene.osm.schema.nature.OsmCoastline;
import fr.ign.cogit.geoxygene.osm.schema.nature.OsmReliefElementPoint;
import fr.ign.cogit.geoxygene.osm.schema.nature.OsmTreePoint;
import fr.ign.cogit.geoxygene.osm.schema.network.OsmNetworkFace;
import fr.ign.cogit.geoxygene.osm.schema.rail.OsmCable;
import fr.ign.cogit.geoxygene.osm.schema.rail.OsmRailwayLine;
import fr.ign.cogit.geoxygene.osm.schema.rail.OsmRailwayNode;
import fr.ign.cogit.geoxygene.osm.schema.roads.OsmCycleWay;
import fr.ign.cogit.geoxygene.osm.schema.roads.OsmPathLine;
import fr.ign.cogit.geoxygene.osm.schema.roads.OsmRoadLine;
import fr.ign.cogit.geoxygene.osm.schema.roads.OsmRoadNode;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmBuildPoint;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmBuilding;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmCemetery;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmParkArea;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmPointOfInterest;
import fr.ign.cogit.geoxygene.osm.schema.urban.OsmSportsField;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

public class OSMSchemaFactory extends AbstractCreationFactory {

  public OsmGeneObj createGeneObj(Class<?> classObj, OSMResource resource,
      Collection<OSMResource> nodes, OsmGeometryConversion convertor)
      throws Exception {
    if (IRoadLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createRoadLine(line, 0);
    }
    if (ICable.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createCable(line);
    }
    if (IBuilding.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly == null)
        return null;
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createBuilding(poly);
    }
    if (ISportsField.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createSportsField(poly);
    }
    if (IWaterLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createWaterLine(line, 0);
    }
    if (IWaterArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createWaterArea(poly);
    }
    if (IBuildPoint.class.isAssignableFrom(classObj)) {
      IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom());
      return (OsmGeneObj) this.createBuildPoint(pt);
    }
    if (IWaterArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createWaterArea(poly);
    }
    if (IAirportArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createAirportArea(poly);
    }
    if (IRunwayArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createRunwayArea(poly);
    }
    if (IRunwayLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createRunwayLine(line);
    }
    if (ITaxiwayArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createTaxiwayArea(poly);
    }
    if (ITaxiwayLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createTaxiwayLine(line, null);
    }
    if (ISimpleLandUseArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly == null || poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createSimpleLandUseArea(poly, 0);
    }
    if (IPointOfInterest.class.isAssignableFrom(classObj)) {
      IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom());
      return new OsmPointOfInterest(pt);
    }
    if (ITreePoint.class.isAssignableFrom(classObj)) {
      IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom());
      return (OsmGeneObj) this.createTreePoint(pt);
    }
    if (ICycleWay.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      return (OsmGeneObj) this.createCycleWay(line);
    }
    if (IRailwayLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createRailwayLine(line, 0);
    }
    if (IPathLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createPath(line, 0);
    }
    if (IReliefElementPoint.class.isAssignableFrom(classObj)) {
      IPoint pt = convertor.convertOsmPoint((OSMNode) resource.getGeom());
      return (OsmGeneObj) this.createReliefElementPoint(pt);
    }
    if (ICoastLine.class.isAssignableFrom(classObj)) {
      ILineString line = convertor.convertOSMLine((OSMWay) resource.getGeom(),
          nodes);
      if (line.coord().size() == 1)
        return null;
      return (OsmGeneObj) this.createCoastLine(line);
    }
    if (ISquareArea.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return (OsmGeneObj) this.createSquareArea(poly);
    }
    if (OsmCemetery.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return new OsmCemetery(poly);
    }
    if (OsmSchool.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return new OsmSchool(poly);
    }
    if (OsmHospital.class.isAssignableFrom(classObj)) {
      IPolygon poly = convertor.convertOSMPolygon((OSMWay) resource.getGeom(),
          nodes);
      if (poly.coord().size() < 4)
        return null;
      return new OsmHospital(poly);
    }
    // TODO
    return null;
  }

  @Override
  public IBuilding createBuilding(IPolygon poly) {
    return new OsmBuilding(poly);
  }

  @Override
  public IBuilding createBuilding(IPolygon poly, String nature) {
    OsmBuilding build = new OsmBuilding(poly);
    build.setNature(nature);
    return build;
  }

  @Override
  public IBuildPoint createBuildPoint(IPoint point) {
    return new OsmBuildPoint(point);
  }

  @Override
  public ISportsField createSportsField(IPolygon poly) {
    return new OsmSportsField(poly);
  }

  @Override
  public IRoadLine createRoadLine(ILineString line, int importance) {
    return new OsmRoadLine(line, -1);
  }

  // /////////////////
  // RAILWAY
  // /////////////////

  // RailwayLine
  @Override
  public IRailwayLine createRailwayLine(ILineString line, int importance) {
    return new OsmRailwayLine(line);
  }

  @Override
  public ICable createCable(ILineString line) {
    return new OsmCable(line);
  }

  @Override
  public IWaterLine createWaterLine(ILineString line, int importance) {
    return new OsmWaterLine(line);
  }

  @Override
  public IWaterArea createWaterArea(IPolygon poly) {
    return new OsmWaterArea(poly);
  }

  @Override
  public ISimpleLandUseArea createSimpleLandUseArea(IPolygon poly, int type) {
    return new OsmSimpleLandUseArea(poly);
  }

  @Override
  public IAirportArea createAirportArea(IPolygon geom) {
    return new OsmAirportArea(geom);
  }

  @Override
  public IRunwayArea createRunwayArea(IPolygon geom) {
    return new OsmRunwayArea(geom);
  }

  @Override
  public IRunwayLine createRunwayLine(ILineString geom) {
    return new OsmRunwayLine(geom);
  }

  @Override
  public IReliefField createReliefField(ChampContinu champ) {
    return new ReliefField(champ);
  }

  @Override
  public INetwork createNetwork() {
    return new Network();
  }

  @Override
  public INetwork createNetwork(Reseau res) {
    return new Network(res);
  }

  @Override
  public IRoadNode createRoadNode() {
    return new OsmRoadNode();
  }

  @Override
  public IRoadNode createRoadNode(IPoint point) {
    return new OsmRoadNode(point);
  }

  @Override
  public IRoadNode createRoadNode(Noeud noeud) {
    return new OsmRoadNode(noeud);
  }

  @Override
  public IRoadNode createRoadNode(NoeudRoutier geoxObj) {
    return new OsmRoadNode(geoxObj);
  }

  @Override
  public INetworkFace createNetworkFace(IPolygon poly) {
    return new OsmNetworkFace(poly);
  }

  @Override
  public INetworkFace createNetworkFace(Face geoxObj) {
    return new OsmNetworkFace(geoxObj);
  }

  public ITreePoint createTreePoint(IPoint geom) {
    return new OsmTreePoint(geom);
  }

  @Override
  public ICycleWay createCycleWay(ILineString line) {
    return new OsmCycleWay(line);
  }

  @Override
  public IPathLine createPath(ILineString line, int importance) {
    return new OsmPathLine(line, importance);
  }

  @Override
  public IReliefElementPoint createReliefElementPoint(IPoint point) {
    return new OsmReliefElementPoint(point);
  }

  @Override
  public ICoastLine createCoastLine(ILineString line) {
    return new OsmCoastline(line);
  }

  @Override
  public ISquareArea createSquareArea(IPolygon poly) {
    return new OsmParkArea(poly);
  }

  @Override
  public ITaxiwayArea createTaxiwayArea(IPolygon simple, TaxiwayType type) {
    return new OsmTaxiwayArea(simple, type);
  }

  public ITaxiwayArea createTaxiwayArea(IPolygon simple) {
    return new OsmTaxiwayArea(simple);
  }

  @Override
  public ITaxiwayLine createTaxiwayLine(ILineString geom, TaxiwayType type) {
    return new OsmTaxiwayLine(geom);
  }

  @Override
  public IRailwayNode createRailwayNode() {
    return new OsmRailwayNode();
  }

  @Override
  public IRailwayNode createRailwayNode(IPoint point) {
    return new OsmRailwayNode(point);
  }

  @Override
  public IRailwayNode createRailwayNode(Noeud noeud) {
    return new OsmRailwayNode(noeud);
  }

}
