/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.twitter;

import java.util.Date;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

public class TwitterFeature extends AbstractFeature {

  private Date createdAt;
  private User user;
  private String text;
  private long tweetId;
  private int retweetCount, favoriteCount;
  private Place place;

  public TwitterFeature(Status tweet) {
    super();
    this.setTweetId(tweet.getId());
    this.setText(tweet.getText());
    this.setUser(tweet.getUser());
    this.setCreatedAt(tweet.getCreatedAt());
    this.setFavoriteCount(tweet.getFavoriteCount());
    this.setRetweetCount(tweet.getRetweetCount());
    this.setPlace(tweet.getPlace());
    // compute the tweet geometry
    // FIXME handle SRIDs
    GeoLocation geoLocation = tweet.getGeoLocation();
    if (geoLocation != null) {
      this.setGeom(GeometryEngine.getFactory().createPoint(
          new DirectPosition(geoLocation.getLongitude(), geoLocation
              .getLatitude())));
    }
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public long getTweetId() {
    return tweetId;
  }

  public void setTweetId(long tweetId) {
    this.tweetId = tweetId;
  }

  public int getRetweetCount() {
    return retweetCount;
  }

  public void setRetweetCount(int retweetCount) {
    this.retweetCount = retweetCount;
  }

  public int getFavoriteCount() {
    return favoriteCount;
  }

  public void setFavoriteCount(int favoriteCount) {
    this.favoriteCount = favoriteCount;
  }

  public Place getPlace() {
    return place;
  }

  public void setPlace(Place place) {
    this.place = place;
  }

}
