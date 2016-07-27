/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.foursquare;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FoursquareLoader {

  private static final Logger logger = Logger.getLogger(FoursquareLoader.class);

  private String proxyHost;
  private int proxyPort;
  private String clientId, clientSecret;

  /**
   * A loader for Foursquare venues into GeOxygene features.
   * @param proxyHost
   * @param proxyPort
   * @param clientId
   * @param clientSecret
   */
  public FoursquareLoader(String proxyHost, int proxyPort, String clientId,
      String clientSecret) {
    super();
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  /**
   * 
   * @param longitude
   * @param latitude
   * @param radius in meters
   * @return
   * @throws IOException
   * @throws FoursquareApiException
   */
  public List<FoursquareFeature> exploreVenuesInArea(double longitude,
      double latitude, int radius) throws IOException {
    List<FoursquareFeature> features = new ArrayList<FoursquareFeature>();

    StringBuffer urlString = new StringBuffer(
        "https://api.foursquare.com/v2/venues/search?");
    urlString.append("client_id=" + clientId);
    urlString.append("&client_secret=" + clientSecret);
    urlString.append("&ll=" + latitude + "," + longitude);
    urlString.append("&radius=" + radius);
    // urlString.append("&section=food");
    urlString.append("&v=20160101");
    urlString.append("&m=foursquare");
    URL url = new URL(urlString.toString());
    if (logger.isTraceEnabled())
      logger.trace(url);

    // Connection
    URLConnection urlConn;
    Proxy proxy = new Proxy(Proxy.Type.HTTP,
        new InetSocketAddress(proxyHost, proxyPort));
    urlConn = (URLConnection) url.openConnection(proxy);

    // Get connection inputstream
    InputStream is = urlConn.getInputStream();
    Reader reader = new InputStreamReader(is, "UTF-8");
    // System.out.println(IOUtils.toString(is));
    JSONResult result = new Gson().fromJson(reader, JSONResult.class);
    for (FoursquareVenue venue : result.response.venues) {
      logger.trace(venue.name);
      features.add(new FoursquareFeature(venue));
    }

    return features;
  }

  class JSONResult {
    @SerializedName("meta")
    public ResultMeta meta;
    @SerializedName("response")
    public Response response;
  }

  class ResultMeta {
    @SerializedName("code")
    public int code;
    @SerializedName("requestId")
    public String requestId;
  }

  class Response {
    @SerializedName("venues")
    public FoursquareVenue[] venues;
    @SerializedName("confident")
    public Boolean confident;
  }
}
