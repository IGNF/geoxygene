package fr.ign.cogit.geoxygene.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmParkArea extends OsmGeneObjSurf implements ISquareArea {

  @Override
  public IUrbanBlock getBlock() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    // TODO Auto-generated method stub

  }

  public OsmParkArea(IPolygon geom) {
    super(geom);
  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

}
