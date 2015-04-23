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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class ReDetailArea {
  private IPolygon areaToDetail;

  private Collection<IGeometry> detailedComponents;

  public ReDetailArea(IPolygon areaToDetail,
      Collection<IGeometry> detailedComponents) {
    super();
    this.areaToDetail = areaToDetail;
    this.detailedComponents = detailedComponents;
  }

  /**
   * Make an union between the areaToDetail and the buffers of the component
   * that initially intersect the areaToDetail.
   * @param radius
   * @return
   */
  public IPolygon reDetailByBuffer(double radius, double doug) {
    IPolygon newArea = new GM_Polygon(new GM_LineString(areaToDetail.coord()));
    for (IGeometry geom : detailedComponents) {
      if (areaToDetail.contains(geom))
        continue;
      IGeometry buffer = geom.buffer(radius);
      if (!buffer.isValid())
        continue;
      IGeometry union = newArea.union(buffer);
      if (union == null)
        union = newArea.union(buffer.buffer(1.0));
      if (union == null)
        continue;
      if (union instanceof IPolygon)
        newArea = (IPolygon) union;
      else
        continue;
    }
    newArea = (IPolygon) CommonAlgorithms.filtreDouglasPeucker(newArea, doug);
    return newArea;
  }

  public IPolygon getAreaToDetail() {
    return areaToDetail;
  }

  public void setAreaToDetail(IPolygon areaToDetail) {
    this.areaToDetail = areaToDetail;
  }

}
