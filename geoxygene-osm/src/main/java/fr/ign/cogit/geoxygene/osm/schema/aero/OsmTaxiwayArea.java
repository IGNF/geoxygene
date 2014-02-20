package fr.ign.cogit.geoxygene.osm.schema.aero;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmTaxiwayArea extends OsmGeneObjSurf implements ITaxiwayArea {

  TaxiwayType type = TaxiwayType.APRON;
  private IAirportArea airport;

  public OsmTaxiwayArea(IPolygon geom) {
    super(geom);
    if ("taxiway".equals(getTags().get("aeroway")))
      type = TaxiwayType.TAXIWAY;
  }

  @Override
  public TaxiwayType getType() {
    return type;
  }

  public void setType(TaxiwayType type) {
    this.type = type;
  }

  @Override
  public IAirportArea getAirport() {
    return airport;
  }

  @Override
  public void setAirport(IAirportArea airport) {
    this.airport = airport;
  }

}
