package fr.ign.cogit.osm.schema.aero;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.osm.schema.OsmGeneObjLin;

public class OsmRunwayLine extends OsmGeneObjLin implements IRunwayLine {

  private IAirportArea airport;
  private int z;

  public OsmRunwayLine(ILineString geom) {
    this.setGeom(geom);
  }

  @Override
  public IAirportArea getAirport() {
    return airport;
  }

  @Override
  public int getZ() {
    return z;
  }

}
