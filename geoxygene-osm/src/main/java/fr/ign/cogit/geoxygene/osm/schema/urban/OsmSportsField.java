package fr.ign.cogit.geoxygene.osm.schema.urban;

import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmSportsField extends OsmGeneObjSurf implements ISportsField {

  private IPolygon symbolGeom;
  private SportsFieldType type = SportsFieldType.UNKNOWN;

  public OsmSportsField(IPolygon polygon) {
    super(polygon);
    this.symbolGeom = polygon;
    // FIXME set type from tags
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

  @Override
  public SportsFieldType getType() {
    return type;
  }

  @Override
  public String getTypeSymbol() {
    return type.name();
  }

  @Override
  public ILineString getMedianGeom() {
    return null;
  }
}
