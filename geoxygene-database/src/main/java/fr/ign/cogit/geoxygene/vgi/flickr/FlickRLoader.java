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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.scribe.model.ParameterList;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.util.StringUtilities;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class FlickRLoader {

    private static final Logger logger = Logger.getLogger(FlickRLoader.class);

    private String apiKey;
    private String sharedSecret;
    private Set<String> extras;
    private REST transport;
    private String proxyHost;
    private int proxyPort;

    public FlickRLoader(String apiKey, String sharedSecret, String proxyHost, int proxyPort) {
        super();
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.extras = new HashSet<>();
        this.extras.add("geo");
        this.extras.add("tags");
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.transport = new REST();
        this.transport.setProxy(proxyHost, proxyPort);
    }

    public FlickRLoader(String apiKey, String sharedSecret) {
        super();
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
        this.extras = new HashSet<>();
        this.extras.add("geo");
        this.extras.add("tags");
        this.transport = new REST();
    }

    public List<FlickRFeature> getPhotosFromExtent(double minLat, double maxLat, double minLong, double maxLong,
            int accuracyLevel) throws FlickrException, IOException, ParserConfigurationException, SAXException {

        // Set the wanted search parameters
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setAccuracy(accuracyLevel);
        searchParameters.setBBox(String.valueOf(minLong), String.valueOf(minLat), String.valueOf(maxLong),
                String.valueOf(maxLat));

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("method", "flickr.photos.search");
        if (accuracyLevel > 0)
            parameters.put("accuracy", String.valueOf(accuracyLevel));
        String[] bbox = searchParameters.getBBox();
        System.out.println(bbox[0]);
        System.out.println(bbox[1]);
        System.out.println(bbox[2]);
        System.out.println(bbox[3]);
        System.out.println(StringUtilities.join(bbox, ","));
        parameters.put("bbox", StringUtilities.join(bbox, ","));
        parameters.put(Flickr.API_KEY, apiKey);
        if (proxyHost != null)
            transport.setProxy(proxyHost, proxyPort);
        String requestUrl = transport.getScheme() + "://" + transport.getHost() + transport.getPath();

        ParameterList paramList = new ParameterList();
        for (String param : parameters.keySet()) {
            paramList.add(param, parameters.get(param));
            System.out.println(param);
            System.out.println(parameters.get(param));
        }
        int pageNb = 1;
        String urlString = paramList.appendTo(requestUrl);
        String finalURL = urlString + "&per_page=500&format=rest&page=" + pageNb;
        URL url = new URL(finalURL);
        System.out.println(finalURL);
        logger.info(url);

        // Connection
        /*
         * URLConnection urlConn; Proxy proxy = new Proxy(Proxy.Type.HTTP, new
         * InetSocketAddress(proxyHost, proxyPort));
         * System.setProperty("https.proxyHost", proxyHost);
         * System.setProperty("https.proxyPort", String.valueOf(proxyPort));
         * urlConn = (URLConnection) url.openConnection(proxy);
         * 
         * // Get connection inputstream InputStream is =
         * urlConn.getInputStream();
         */
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", String.valueOf(proxyPort));

        HttpGet method = new HttpGet(finalURL);
        // HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        // DefaultProxyRoutePlanner routePlanner = new
        // DefaultProxyRoutePlanner(proxy);
        HttpRoutePlanner routePlanner = new HttpRoutePlanner() {

            public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context)
                    throws HttpException {
                return new HttpRoute(target, null, new HttpHost(proxyHost, proxyPort),
                        "https".equalsIgnoreCase(target.getSchemeName()));
            }

        };
        CloseableHttpClient httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        CloseableHttpResponse response = httpclient.execute(method);
        HttpEntity entity = response.getEntity();

        // parse the stream
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        FlickRPhotoParser photoParser = new FlickRPhotoParser();
        parser.parse(entity.getContent(), photoParser);
        boolean continueParsing = photoParser.isThereNextPage();
        method.releaseConnection();

        while (continueParsing) {
            pageNb++;
            finalURL = urlString + "&per_page=500&format=rest&page=" + pageNb;
            url = new URL(finalURL);
            logger.info(url);
            URLConnection urlConn;
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            urlConn = (URLConnection) url.openConnection(proxy);
            InputStream is = urlConn.getInputStream();
            parser.parse(is, photoParser);
            continueParsing = photoParser.isThereNextPage();
        }

        // now create the Features from the photos
        List<FlickRFeature> features = new ArrayList<>();
        for (FlickRPhoto photo : photoParser.getPhotos()) {
            IDirectPosition position = getPhotoLocation(photo.getFlickRId());
            FlickRFeature feat = new FlickRFeature(photo, position.getX(), position.getY());
            features.add(feat);
        }

        return features;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
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
        String requestUrl = transport.getScheme() + "://" + transport.getHost() + transport.getPath();
        String urlString = paramList.appendTo(requestUrl);
        URL url = new URL(urlString);

        // Connection
        URLConnection urlConn;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        urlConn = (URLConnection) url.openConnection(proxy);

        // Get connection inputstream
        InputStream is = urlConn.getInputStream();

        FlickRPhotoParser parser = new FlickRPhotoParser();
        FlickRLocation location = parser.parseLocation(is);

        return location.getPosition();
    }
}
