package fr.ign.cogit.geoxygene.osm.schema.rail;

import fr.ign.cogit.cartagen.core.genericschema.railway.ICable;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjLin;

public class OsmCable extends OsmGeneObjLin implements ICable {

  public OsmCable(ILineString line) {
    super(line);
  }
}
