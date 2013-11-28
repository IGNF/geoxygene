package fr.ign.cogit.geoxygene.osm.schema.nature;

import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjPoint;

public class OsmTreePoint extends OsmGeneObjPoint implements ITreePoint {

  private String name, type;

  public OsmTreePoint(IPoint geom) {
    super(geom);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

}
