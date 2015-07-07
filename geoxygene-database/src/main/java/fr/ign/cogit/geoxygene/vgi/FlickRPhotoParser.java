package fr.ign.cogit.geoxygene.vgi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class FlickRPhotoParser extends DefaultHandler {

  private StringBuffer buffer;
  private Map<String, Boolean> elements = new HashMap<String, Boolean>();
  private FlickRPhoto currentPhoto = null;
  private Set<FlickRPhoto> photos = new HashSet<>();

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    elements.put(qName, true);
    if (qName.equals("photo")) {
      String id = attributes.getValue("id");
      String owner = attributes.getValue("owner");
      String secret = attributes.getValue("secret");
      String server = attributes.getValue("server");
      String farm = attributes.getValue("farm");
      String title = attributes.getValue("title");
      String ispublic = attributes.getValue("ispublic");
      String isfriend = attributes.getValue("isfriend");
      String isfamily = attributes.getValue("isfamily");
      this.currentPhoto = new FlickRPhoto(id, owner, secret, server, farm,
          title, ispublic, isfriend, isfamily);
    } else {
      // do nothing for other names
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null)
      buffer.append(lecture);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {

    if (qName.equals("photo")) {
      photos.add(currentPhoto);
    } else {
      // do nothing
    }
    elements.put(qName, false);
  }

  public Set<FlickRPhoto> getPhotos() {
    return photos;
  }

  /**
   * Parse an input stream that is a response to a
   * "flickr.photos.geo.getLocation" request.
   * @param stream
   * @return
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public FlickRLocation parseLocation(InputStream stream)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(stream);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc.getElementsByTagName("rsp").item(0);

    Element locationElem = (Element) root.getElementsByTagName("location")
        .item(0);
    String placeId = locationElem.getAttribute("place_id");
    String woeid = locationElem.getAttribute("woeid");
    int accuracy = Integer.valueOf(locationElem.getAttribute("accuracy"));
    String longitude = locationElem.getAttribute("longitude");
    String latitude = locationElem.getAttribute("latitude");
    IDirectPosition position = new DirectPosition(Double.valueOf(longitude),
        Double.valueOf(latitude));
    FlickRLocation location = new FlickRLocation(position, placeId, woeid,
        accuracy);

    return location;
  }
}
