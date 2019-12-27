/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.api.spatial;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;

/**
 * Abstract factory Interface for the creation of geometric objects using
 * diverse constructors forms
 * 
 */

public interface AbstractGeomFactory {
  public ILineString createILineString(IDirectPositionList coords);

  public ILineString createILineString(List<IDirectPosition> listePoints);

  public IPolygon createIPolygon(ILineString line);

  public IPolygon createIPolygon(IEnvelope env);

  public IPolygon createIPolygon(IRing ring);

  public IPolygon createIPolygon(IDirectPositionList coords);

  public IPoint createPoint();

  public IPoint createPoint(IDirectPosition position);

  public IMultiSurface<IPolygon> createMultiPolygon();

  public ILineSegment createLineSegment(IDirectPosition pt1,
      IDirectPosition pt2);

  public IMultiPoint createMultiPoint();

  /** Create the envelope from two opposite corners. */
  public IEnvelope createEnvelope(IDirectPosition upperRightCorner,
      IDirectPosition lowerLeftCorner);
}
