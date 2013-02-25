package fr.ign.cogit.cartagen.pearep.mgcp.aer;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class MGCPRunwayArea extends MGCPFeature implements IRunwayArea {

  private MGCPAirport airport;
  private int z;

  /**
   * @param type
   */
  public MGCPRunwayArea(IPolygon geom, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.setAttributeMap(attributes);
  }

  public MGCPRunwayArea(IPolygon geom) {
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

  @Override
  public IPolygon getGeom() {
    return (IPolygon) this.geom;
  }

  @Override
  public void setAirport(IAirportArea airport) {
    this.airport = (MGCPAirport) airport;
  }

}
