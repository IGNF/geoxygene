/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.lodharmonisation.operation;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class AdjustBoundsToComponents {

  private IPolygon bounds;
  private Collection<IPolygon> componentsToInclude, componentsToExclude;

  public AdjustBoundsToComponents(IPolygon bounds,
      Collection<IPolygon> componentsToInclude,
      Collection<IPolygon> componentsToExclude) {
    super();
    this.bounds = bounds;
    this.componentsToInclude = componentsToInclude;
    this.componentsToExclude = componentsToExclude;
  }

  /**
   * Adjust the bounds polygon to follow the exact outline of the components to
   * include or exclude, but smooth the junctions, i.e. the previous
   * intersection vertices are removed from the bounds polygon.
   * @return
   */
  public IPolygon adjustBoundsTight() {
    IDirectPositionList coord = bounds.coord();
    for (IPolygon component : componentsToInclude) {
      IPolygon currentPol = new GM_Polygon(new GM_LineString(coord));
      if (!component.intersects(currentPol))
        continue;
      IGeometry union = currentPol.union(component);
      if (union == null) {
        union = currentPol.buffer(0.1).union(component.buffer(0.1));
        if (union == null)
          union = currentPol;
      }
      coord = ((IPolygon) union).exteriorLineString().coord();
      IGeometry inter = bounds.exteriorLineString().intersection(
          component.exteriorLineString());
      if (inter instanceof IMultiPoint) {
        for (IPoint pt : ((IMultiPoint) inter).getList()) {
          if (pt != null)
            coord.remove(pt.getPosition());
        }
      }
    }
    for (IPolygon component : componentsToExclude) {
      IPolygon currentPol = new GM_Polygon(new GM_LineString(coord));
      IGeometry diff = currentPol.difference(component);
      if (diff == null) {
        diff = currentPol.buffer(0.1).difference(component.buffer(0.1));
        if (diff == null)
          diff = currentPol;
      }
      coord = diff.coord();
      IGeometry inter = bounds.exteriorLineString().intersection(
          component.exteriorLineString());
      if (inter instanceof IMultiPoint) {
        for (IPoint pt : ((IMultiPoint) inter).getList()) {
          if (pt != null)
            coord.remove(pt.getPosition());
        }
      }
    }
    return new GM_Polygon(new GM_LineString(coord));
  }

}
