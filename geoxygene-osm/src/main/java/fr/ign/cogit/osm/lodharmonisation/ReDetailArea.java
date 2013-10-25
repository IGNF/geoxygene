package fr.ign.cogit.osm.lodharmonisation;

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
      newArea = (IPolygon) newArea.union(buffer);
    }
    newArea = (IPolygon) CommonAlgorithms.filtreDouglasPeucker(newArea, doug);
    return newArea;
  }
}
