package fr.ign.cogit.osm.schema.landuse;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.osm.schema.OsmGeneObjSurf;

public class OsmSimpleLandUseArea extends OsmGeneObjSurf implements
    ISimpleLandUseArea {

  private OsmLandUseTypology type;

  public OsmSimpleLandUseArea(IPolygon geom) {
    this.setGeom(geom);
  }

  @Override
  public int getType() {
    return type.ordinal();
  }

  @Override
  public void setType(int type) {
    this.type = OsmLandUseTypology.values()[type];
  }

}
