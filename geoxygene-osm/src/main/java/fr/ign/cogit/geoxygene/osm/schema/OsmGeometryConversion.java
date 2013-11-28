package fr.ign.cogit.geoxygene.osm.schema;

import java.util.Collection;

import fr.ign.cogit.cartagen.util.CRSConversion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class OsmGeometryConversion {

  /**
   * Converts a way into a {@link ILineString} using a collection of the nodes,
   * as ways only describe the node id of the vertices. The coordinates are
   * projected in Lambert93 so it doesn't work on non-French data.
   * @param way
   * @param nodes
   * @return
   */
  public static ILineString convertOSMLine(OSMWay way,
      Collection<OSMResource> nodes) {
    IDirectPositionList coord = new DirectPositionList();
    for (long index : way.getVertices()) {
      OSMNode vertex = null;
      for (OSMResource node : nodes) {
        if (node.getId() == index) {
          vertex = (OSMNode) node.getGeom();
          break;
        }
      }
      if (vertex != null) {
        IDirectPosition pt = CRSConversion.wgs84ToLambert93(
            vertex.getLatitude(), vertex.getLongitude());
        coord.add(pt);
      }
    }
    return new GM_LineString(coord);
  }

  /**
   * Converts a way into a {@link IPolygon} using a collection of the nodes, as
   * ways only describe the node id of the vertices. If the way does not
   * represent a polygon, returns null. The coordinates are projected in
   * Lambert93 so it doesn't work on non-French data.
   * @param way
   * @param nodes
   * @return
   */
  public static IPolygon convertOSMPolygon(OSMWay way,
      Collection<OSMResource> nodes) {
    if (!way.isPolygon()) {
      return null;
    }
    IDirectPositionList coord = new DirectPositionList();
    for (long index : way.getVertices()) {
      OSMNode vertex = null;
      for (OSMResource node : nodes) {
        if (node.getId() == index) {
          vertex = (OSMNode) node.getGeom();
          break;
        }
      }
      if (vertex != null) {
        IDirectPosition pt = CRSConversion.wgs84ToLambert93(
            vertex.getLatitude(), vertex.getLongitude());
        coord.add(pt);
      }
    }
    return new GM_Polygon(new GM_LineString(coord));
  }

  public static IPoint convertOsmPoint(OSMNode node) {
    IDirectPosition pt = CRSConversion.wgs84ToLambert93(node.getLatitude(),
        node.getLongitude());
    return pt.toGM_Point();
  }
}
