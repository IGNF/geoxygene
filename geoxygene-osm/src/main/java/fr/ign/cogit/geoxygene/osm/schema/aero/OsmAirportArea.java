package fr.ign.cogit.geoxygene.osm.schema.aero;

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
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjSurf;

public class OsmAirportArea extends OsmGeneObjSurf implements IAirportArea {

  private int z;
  private Set<IRunwayArea> runwayAreas = new HashSet<IRunwayArea>();
  private Set<IRunwayLine> runwayLines = new HashSet<IRunwayLine>();
  private Set<ITaxiwayArea> taxiwayAreas = new HashSet<ITaxiwayArea>();
  private Set<ITaxiwayLine> taxiwayLines = new HashSet<ITaxiwayLine>();
  private Set<IHelipadArea> helipadAreas = new HashSet<IHelipadArea>();
  private Set<IHelipadPoint> helipadPoint = new HashSet<IHelipadPoint>();
  private Set<IBuilding> terminals = new HashSet<IBuilding>();

  public OsmAirportArea(IPolygon geom) {
    super(geom);
  }

  @Override
  public String getName() {
    if (this.getTags().containsKey("name"))
      return this.getTags().get("name");
    return "";
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
    return terminals;
  }

}
