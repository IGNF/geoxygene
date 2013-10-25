package fr.ign.cogit.osm.schema.aero;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.osm.schema.OsmGeneObjSurf;

public class OsmRunwayArea extends OsmGeneObjSurf implements IRunwayArea {

  private IAirportArea airport;
  private int z;

  public OsmRunwayArea(IPolygon geom) {
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

  @Override
  public void setAirport(IAirportArea airport) {
    this.airport = airport;
  }

}
