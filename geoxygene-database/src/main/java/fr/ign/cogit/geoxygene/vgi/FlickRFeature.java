package fr.ign.cogit.geoxygene.vgi;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class FlickRFeature extends AbstractFeature {

  private FlickRPhoto photo;
  private double longitude, latitude;

  public FlickRFeature(FlickRPhoto photo, double longitude, double latitude) {
    super();
    this.setPhoto(photo);
    this.longitude = longitude;
    this.latitude = latitude;
    this.setId(Long.valueOf(photo.getFlickRId()).intValue());
    // FIXME
    this.setGeom(new GM_Point(new DirectPosition(longitude, latitude)));
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public FlickRPhoto getPhoto() {
    return photo;
  }

  public void setPhoto(FlickRPhoto photo) {
    this.photo = photo;
  }

  public String getPhotoUrlSmall() {
    return "https://farm" + photo.getFarm() + ".staticflickr.com/"
        + photo.getServer() + "/" + photo.getFlickRId() + "_"
        + photo.getSecret() + "_s.jpg";
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    if ("flickRId".equals(nomAttribut)) {
      return this.getPhoto().getFlickRId();
    }
    return super.getAttribute(nomAttribut);
  }

}
