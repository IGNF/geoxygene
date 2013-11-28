package fr.ign.cogit.geoxygene.osm.schema.nature;

import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjPoint;

public class OsmReliefElementPoint extends OsmGeneObjPoint implements
    IReliefElementPoint {

  private String name, type;

  public OsmReliefElementPoint(IPoint geom) {
    super(geom);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
