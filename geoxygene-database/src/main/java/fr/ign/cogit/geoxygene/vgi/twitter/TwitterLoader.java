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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A class to load georeferenced tweets into GeOxygene as {@link TwitterFeature}
 * instances.
 * @author GTouya
 * 
 */
public class TwitterLoader {

  private static final Logger logger = Logger.getLogger(TwitterLoader.class);

  private boolean proxy = false;
  private String proxyHost;
  private int proxyPort;
  private String apiKey;
  private String apiSecret;
  private String accessToken;
  private String tokenSecret;

  public TwitterLoader() {
    super();
  }

  /**
   * Get the tweets within a radius around a location, using the location given
   * in the users profile, or the geotag when available. Be careful, the API
   * only allows to retrieve 7 days old tweets.
   * 
   * @param latitude
   * @param longitude
   * @param radius in kilometers
   * @param since the date from which tweets are retrieved (set to null if not
   *          applicable)
   * @param until the date until which tweets are retrieved (set to null if not
   *          applicable)
   * @return
   * @throws TwitterException
   */
  public List<TwitterFeature> getTweetsFromLocation(double latitude,
      double longitude, double radius, String since, String until)
      throws TwitterException {
    Query query = new Query().geoCode(new GeoLocation(latitude, longitude),
        radius, "km");
    if (since != null)
      query.setSince(since);
    if (until != null)
      query.setUntil(until);

    return getTweetsFromQuery(query);
  }

  private List<TwitterFeature> getTweetsFromQuery(Query query)
      throws TwitterException {
    List<TwitterFeature> features = new ArrayList<>();
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setOAuthConsumerKey(apiKey);
    cb.setOAuthConsumerSecret(apiSecret);
    cb.setOAuthAccessToken(accessToken);
    cb.setOAuthAccessTokenSecret(tokenSecret);
    if (proxy) {
      cb.setHttpProxyHost(proxyHost);
      cb.setHttpProxyPort(proxyPort);
    }
    Twitter twitter = new TwitterFactory(cb.build()).getInstance();
    QueryResult result = twitter.search(query);
    for (Status status : result.getTweets()) {
      TwitterFeature feature = new TwitterFeature(status);
      if (feature.getGeom() == null)
        continue;
      features.add(feature);
    }

    if (logger.isDebugEnabled())
      logger.debug(features.size() + " tweets retrieved");
    return features;
  }

  /**
   * Set proxy information.
   * @param host
   * @param port
   */
  public void setProxy(String host, int port) {
    this.proxy = true;
    this.proxyHost = host;
    this.proxyPort = port;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiSecret() {
    return apiSecret;
  }

  public void setApiSecret(String apiSecret) {
    this.apiSecret = apiSecret;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenSecret() {
    return tokenSecret;
  }

  public void setTokenSecret(String tokenSecret) {
    this.tokenSecret = tokenSecret;
  }

}
