package fr.ign.cogit.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.osm.schema.OsmGeneObjSurf;

public class OsmSportsField extends OsmGeneObjSurf implements ISportsField {

  private IPolygon symbolGeom;

  public OsmSportsField(IPolygon polygon) {
    super();
    this.setGeom(polygon);
    this.symbolGeom = polygon;
  }

  @Override
  public IUrbanBlock getBlock() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    // TODO Auto-generated method stub

  }

  @Override
  public IPolygon getSymbolGeom() {
    return symbolGeom;
  }
}
