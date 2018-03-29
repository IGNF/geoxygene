package fr.ign.cogit.geoxygene.vgi.flickr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.scribe.model.ParameterList;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class FlickRXmlLoader {

  private String apiKey;
  private String sharedSecret;
  private REST transport;
  private String proxyHost;
  private int proxyPort;

  public FlickRXmlLoader(String apiKey, String sharedSecret, String proxyHost,
      int proxyPort) {
    super();
    this.apiKey = apiKey;
    this.sharedSecret = sharedSecret;
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
    this.transport = new REST();
    this.transport.setProxy(proxyHost, proxyPort);
  }

  public List<FlickRFeature> getPhotosFromXmlFiles(File[] files)
      throws FileNotFoundException, SAXException, IOException,
      ParserConfigurationException {
    List<FlickRFeature> features = new ArrayList<FlickRFeature>();

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();

    for (File file : files) {
      FlickRPhotoParser photoParser = new FlickRPhotoParser();
      // parse the xml file
      parser.parse(new FileInputStream(file), photoParser);

      // create features from the parsed information
      for (FlickRPhoto photo : photoParser.getPhotos()) {
        IDirectPosition position = getPhotoLocation(photo.getFlickRId());
        FlickRFeature feat = new FlickRFeature(photo, position.getX(),
            position.getY());
        features.add(feat);
      }
    }

    return features;
  }

  public IDirectPosition getPhotoLocation(String photoId)
      throws IOException, ParserConfigurationException, SAXException {
    // make the query URL
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("method", "flickr.photos.geo.getLocation");
    parameters.put("photo_id", photoId);
    parameters.put(Flickr.API_KEY, apiKey);
    ParameterList paramList = new ParameterList();
    for (String param : parameters.keySet()) {
      paramList.add(param, parameters.get(param));
    }
    String requestUrl = transport.getScheme() + "://" + transport.getHost()
        + transport.getPath();
    String urlString = paramList.appendTo(requestUrl);
    URL url = new URL(urlString);

    // Connection
    URLConnection urlConn;
    Proxy proxy = new Proxy(Proxy.Type.HTTP,
        new InetSocketAddress(proxyHost, proxyPort));
    urlConn = (URLConnection) url.openConnection(proxy);

    // Get connection inputstream
    InputStream is = urlConn.getInputStream();

    FlickRPhotoParser parser = new FlickRPhotoParser();
    FlickRLocation location = parser.parseLocation(is);

    return location.getPosition();
  }
}
