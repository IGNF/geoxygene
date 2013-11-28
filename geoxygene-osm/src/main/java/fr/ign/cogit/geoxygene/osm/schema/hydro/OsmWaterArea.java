package fr.ign.cogit.geoxygene.osm.schema.hydro;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmWaterArea extends OsmGeneObjSurf implements IWaterArea {

  public OsmWaterArea(IPolygon polygon) {
    super(polygon);
  }
}
