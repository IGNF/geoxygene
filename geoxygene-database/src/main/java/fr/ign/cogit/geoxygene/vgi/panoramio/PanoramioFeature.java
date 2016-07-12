/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.panoramio;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

public class PanoramioFeature extends AbstractFeature {

  private PanoramioPhoto photo;
  private double longitude, latitude;
  private String stringId;

  public PanoramioFeature(PanoramioPhoto photo, double longitude,
      double latitude) {
    super();
    this.photo = photo;
    this.setId(Long.valueOf(photo.getPhotoId()).intValue());
    this.stringId = String.valueOf(photo.getPhotoId());
    this.longitude = longitude;
    this.latitude = latitude;
    this.setGeom(GeometryEngine.getFactory()
        .createPoint(new DirectPosition(longitude, latitude)));
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  public PanoramioPhoto getPhoto() {
    return photo;
  }

  public void setPhoto(PanoramioPhoto photo) {
    this.photo = photo;
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

  public String getStringId() {
    return stringId;
  }

  public void setStringId(String stringId) {
    this.stringId = stringId;
  }

  public String getPhotoUrlSmall() {
    return "http://static.panoramio.com/photos/small/" + photo.getPhotoId()
        + ".jpg";
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    if ("photoId".equals(nomAttribut)) {
      return this.getStringId();
    }
    return super.getAttribute(nomAttribut);
  }

}
