package fr.ign.cogit.cartagen.pearep.mgcp.aer;

import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea.TaxiwayType;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class MGCPTaxiwayLine extends MGCPFeature implements ITaxiwayLine {

  private TaxiwayType type;

  /**
   * @param type
   */
  public MGCPTaxiwayLine(ILineString geom, TaxiwayType type) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.type = type;
  }

  @Override
  public TaxiwayType getType() {
    return type;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) this.geom;
  }

}
