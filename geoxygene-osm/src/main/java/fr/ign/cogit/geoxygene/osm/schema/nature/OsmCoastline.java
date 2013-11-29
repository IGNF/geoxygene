package fr.ign.cogit.geoxygene.osm.schema.nature;

import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjLin;

public class OsmCoastline extends OsmGeneObjLin implements ICoastLine {

  public OsmCoastline(ILineString line) {
    super(line);
  }
}
