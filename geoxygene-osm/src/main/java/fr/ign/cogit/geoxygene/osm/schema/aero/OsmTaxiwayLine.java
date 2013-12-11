package fr.ign.cogit.geoxygene.osm.schema.aero;

import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjLin;

public class OsmTaxiwayLine extends OsmGeneObjLin implements ITaxiwayLine {

  TaxiwayType type = TaxiwayType.TAXIWAY;

  public OsmTaxiwayLine(ILineString geom) {
    super(geom);
    if ("apron".equals(getTags().get("aeroway")))
      type = TaxiwayType.APRON;
  }

  @Override
  public TaxiwayType getType() {
    return type;
  }

}
