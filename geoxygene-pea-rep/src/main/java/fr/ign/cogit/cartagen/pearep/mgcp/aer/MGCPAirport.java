package fr.ign.cogit.cartagen.pearep.mgcp.aer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IHelipadArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IHelipadPoint;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class MGCPAirport extends MGCPFeature implements IAirportArea {

  private String name;
  private int z;
  private Set<IRunwayArea> runwayAreas;
  private Set<IRunwayLine> runwayLines;
  private Set<ITaxiwayArea> taxiwayAreas;
  private Set<ITaxiwayLine> taxiwayLines;
  private Set<IHelipadArea> helipadAreas;
  private Set<IHelipadPoint> helipadPoint;

  /**
   * @param type
   */
  public MGCPAirport(IPolygon geom, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.setAttributeMap(attributes);
    runwayAreas = new HashSet<IRunwayArea>();
    runwayLines = new HashSet<IRunwayLine>();
    taxiwayAreas = new HashSet<ITaxiwayArea>();
    taxiwayLines = new HashSet<ITaxiwayLine>();
    helipadAreas = new HashSet<IHelipadArea>();
    helipadPoint = new HashSet<IHelipadPoint>();

  }

  public MGCPAirport(IPolygon geom) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    this.setAttributeMap(new HashMap<String, Object>());
    runwayAreas = new HashSet<IRunwayArea>();
    runwayLines = new HashSet<IRunwayLine>();
    taxiwayAreas = new HashSet<ITaxiwayArea>();
    taxiwayLines = new HashSet<ITaxiwayLine>();
    helipadAreas = new HashSet<IHelipadArea>();
    helipadPoint = new HashSet<IHelipadPoint>();
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) this.geom;
  }

  public double getArea() {
    return this.geom.area();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  public Set<IRunwayLine> getRunwayLines() {
    return runwayLines;
  }

  @Override
  public Set<IRunwayArea> getRunwayAreas() {
    return runwayAreas;
  }

  @Override
  public Set<ITaxiwayArea> getTaxiwayAreas() {
    return taxiwayAreas;
  }

  @Override
  public Set<ITaxiwayLine> getTaxiwayLines() {
    return taxiwayLines;
  }

  @Override
  public Set<IHelipadArea> getHelipadAreas() {
    return helipadAreas;
  }

  @Override
  public Set<IHelipadPoint> getHelipadPoints() {
    return helipadPoint;
  }

  @Override
  public Set<IBuilding> getTerminals() {
    // TODO Auto-generated method stub
    return null;
  }

}
