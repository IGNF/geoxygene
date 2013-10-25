package fr.ign.cogit.osm.schema.rail;

import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.osm.schema.OsmGeneObjLin;

public class OsmCable extends OsmGeneObjLin implements ICable {

  public OsmCable(ILineString line) {
    this.setGeom(line);
  }
}
