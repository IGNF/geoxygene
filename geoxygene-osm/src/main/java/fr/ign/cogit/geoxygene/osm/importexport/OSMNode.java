package fr.ign.cogit.geoxygene.osm.importexport;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class OSMNode extends PrimitiveGeomOSM {
  private double latitude, longitude;

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public OSMNode(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public IDirectPosition getPosition() {
    return new DirectPosition(latitude, longitude);
  }
}
