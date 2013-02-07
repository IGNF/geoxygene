package fr.ign.cogit.cartagen.pearep.mgcp.aer;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class MGCPRunwayLine extends MGCPFeature implements IRunwayLine {

  private MGCPAirport airport;
  private int z;

  /**
   * @param type
   */
  public MGCPRunwayLine(ILineString geom, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.setAttributeMap(attributes);
  }

  public MGCPRunwayLine(ILineString geom) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.setAttributeMap(new HashMap<String, Object>());
  }

  @Override
  public IAirportArea getAirport() {
    return airport;
  }

  @Override
  public int getZ() {
    return z;
  }

  public void setAirport(MGCPAirport airport) {
    this.airport = airport;
  }

  public void setZ(int z) {
    this.z = z;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) this.geom;
  }

}
