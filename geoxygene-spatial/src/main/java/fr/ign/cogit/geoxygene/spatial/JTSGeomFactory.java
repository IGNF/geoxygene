/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.geoxygene.spatial;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Class implementing AbstractGeomFactory using the native GeOxygene geometric
 * objects, essentially based on JTS for the geometric computations
 * 
 */

public class JTSGeomFactory implements AbstractGeomFactory {

  /*
   * Constructeurs pour les LineString
   */
  @Override
  public ILineString createILineString(IDirectPositionList coords) {
    ILineString line = new GM_LineString(coords);
    return line;
  }

  @Override
  public ILineString createILineString(List<IDirectPosition> listePoints) {
    return new GM_LineString(listePoints);
  }

  /*
   * Constructeurs pour les Polygon
   */
  @Override
  public IPolygon createIPolygon(ILineString line) {
    return new GM_Polygon(line);
  }

  @Override
  public IPolygon createIPolygon(IEnvelope env) {
    return new GM_Polygon(env);
  }

  @Override
  public IPolygon createIPolygon(IRing ring) {
    return new GM_Polygon(ring);
  }

  /*
   * Constructeurs pour les Point
   */
  @Override
  public IPoint createPoint() {
    return new GM_Point();
  }

  @Override
  public IPoint createPoint(IDirectPosition position) {
    return new GM_Point(position);
  }

  @Override
  public IPolygon createIPolygon(IDirectPositionList coords) {
    return new GM_Polygon(new GM_LineString(coords));
  }

}
