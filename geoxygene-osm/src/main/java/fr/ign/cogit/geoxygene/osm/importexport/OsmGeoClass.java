package fr.ign.cogit.geoxygene.osm.importexport;

import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class OsmGeoClass implements GeographicClass {

  private String name;
  private String featureTypeName;
  private Class<? extends IGeometry> geometryType;

  public OsmGeoClass(String name, String featureTypeName, GeometryType type) {
    super();
    this.name = name;
    this.featureTypeName = featureTypeName;
    if (type.equals(GeometryType.POINT))
      this.geometryType = IPoint.class;
    else if (type.equals(GeometryType.LINE))
      this.geometryType = ILineString.class;
    else if (type.equals(GeometryType.POLYGON))
      this.geometryType = IPolygon.class;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getFeatureTypeName() {
    return featureTypeName;
  }

  @Override
  public Class<? extends IGeometry> getGeometryType() {
    return geometryType;
  }

  @Override
  public void addCartAGenId() {
    // do nothing
  }

}
