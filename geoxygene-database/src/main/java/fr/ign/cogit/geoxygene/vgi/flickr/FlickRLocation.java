/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.flickr;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class FlickRLocation {

  private IDirectPosition position;
  private String placeId, woeid;
  private int accuracy;

  public FlickRLocation(IDirectPosition position, String placeId, String woeid,
      int accuracy) {
    super();
    this.position = position;
    this.placeId = placeId;
    this.woeid = woeid;
    this.accuracy = accuracy;
  }

  public IDirectPosition getPosition() {
    return position;
  }

  public void setPosition(IDirectPosition position) {
    this.position = position;
  }

  public String getPlaceId() {
    return placeId;
  }

  public void setPlaceId(String placeId) {
    this.placeId = placeId;
  }

  public String getWoeid() {
    return woeid;
  }

  public void setWoeid(String woeid) {
    this.woeid = woeid;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

}
