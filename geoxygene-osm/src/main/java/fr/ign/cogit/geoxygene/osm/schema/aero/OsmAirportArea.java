package fr.ign.cogit.geoxygene.osm.schema.aero;

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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IRunwayArea> getRunwayAreas() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<ITaxiwayArea> getTaxiwayAreas() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<ITaxiwayLine> getTaxiwayLines() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IHelipadArea> getHelipadAreas() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IHelipadPoint> getHelipadPoints() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<IBuilding> getTerminals() {
    // TODO Auto-generated method stub
    return null;
  }

}
