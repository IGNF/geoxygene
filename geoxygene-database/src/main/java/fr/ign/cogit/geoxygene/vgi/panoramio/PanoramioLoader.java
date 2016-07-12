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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class PanoramioLoader {

  private static final Logger logger = Logger.getLogger(PanoramioLoader.class);

  private String proxyHost;
  private int proxyPort;

  public PanoramioLoader(String proxyHost, int proxyPort) {
    super();
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
  }

  /**
   * 
   * @param minLat
   * @param maxLat
   * @param minLong
   * @param maxLong
   * @param photoSize the size of the downloaded photos. Can be "original",
   *          "medium" (default value), "small", "thumbnail", "square", or
   *          "mini_square".
   * 
   * @param mapFilter if true, photos are filtered such that they look better
   *          when they are placed on a map. It takes into account the location
   *          and tries to avoid of returning photos of the same location.
   * @return
   * @throws IOException
   * @throws ParseException
   */
  public List<PanoramioFeature> getPhotosFromExtent(double minLat,
      double maxLat, double minLong, double maxLong, String photoSize,
      boolean mapFilter) throws IOException, ParseException {
    List<PanoramioFeature> features = new ArrayList<>();

    StringBuffer urlString = new StringBuffer(
        "http://www.panoramio.com/map/get_panoramas.php?");
    urlString.append("set=full&from=0&to=100&");
    urlString.append("minx=");
    urlString.append(minLong);
    urlString.append("&miny=");
    urlString.append(minLat);
    urlString.append("&maxx=");
    urlString.append(maxLong);
    urlString.append("&maxy=");
    urlString.append(maxLat);
    urlString.append("&size=");
    urlString.append(photoSize);
    if (mapFilter)
      urlString.append("&mapfilter=true");
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
    JSONResult result = new Gson().fromJson(reader, JSONResult.class);
    for (JSONPhoto photoJSON : result.photos) {
      PanoramioPhoto photo = new PanoramioPhoto(photoJSON.photo_id,
          photoJSON.owner_id, photoJSON.width, photoJSON.height,
          photoJSON.photo_title, photoJSON.photo_url, photoJSON.photo_file_url,
          photoJSON.owner_name, photoJSON.owner_url, photoJSON.upload_date);
      PanoramioFeature feature = new PanoramioFeature(photo,
          Double.parseDouble(photoJSON.longitude),
          Double.parseDouble(photoJSON.latitude));
      features.add(feature);
    }
    return features;
  }

  public class JSONResult {
    @SerializedName("count")
    public int count;
    @SerializedName("photos")
    public Collection<JSONPhoto> photos;
  }

  public class JSONPhoto {
    @SerializedName("photo_id")
    public long photo_id;
    @SerializedName("photo_title")
    public String photo_title;
    @SerializedName("photo_url")
    public String photo_url;
    @SerializedName("photo_file_url")
    public String photo_file_url;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("width")
    public int width;
    @SerializedName("height")
    public int height;
    @SerializedName("upload_date")
    public String upload_date;
    @SerializedName("owner_id")
    public int owner_id;
    @SerializedName("owner_name")
    public String owner_name;
    @SerializedName("owner_url")
    public String owner_url;
  }
}
