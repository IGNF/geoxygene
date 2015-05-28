package fr.ign.cogit.cartagen.software.dataset;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class DatabaseView {

  private CartAGenDoc doc;
  /**
   * geographic coordinates of the center of the visualisation panel.
   */
  private IDirectPosition geoCenter = new DirectPosition(0.0, 0.0);

  /**
   * geometry of the display window.
   */
  private IEnvelope displayEnvelope = null;

  /**
   * local SLD of the view
   */
  private StyledLayerDescriptor localSld;

  /**
   * The layers of the view that is toggled to visible
   */
  private Set<String> displayedLayers;

  public DatabaseView(CartAGenDoc doc, IDirectPosition geoCenter,
      IEnvelope displayEnvelope, StyledLayerDescriptor localSld,
      Set<String> displayedLayers) {
    super();
    this.doc = doc;
    this.geoCenter = geoCenter;
    this.displayEnvelope = displayEnvelope;
    this.localSld = localSld;
    this.displayedLayers = displayedLayers;
  }

  public DatabaseView(CartAGenDoc doc, Element xmlElem) {
    this.doc = doc;
    this.readFromXml(xmlElem);
  }

  public CartAGenDoc getDoc() {
    return doc;
  }

  public void setDoc(CartAGenDoc doc) {
    this.doc = doc;
  }

  public IDirectPosition getGeoCenter() {
    return geoCenter;
  }

  public void setGeoCenter(IDirectPosition geoCenter) {
    this.geoCenter = geoCenter;
  }

  public IEnvelope getDisplayEnvelope() {
    return displayEnvelope;
  }

  public void setDisplayEnvelope(IEnvelope displayEnvelope) {
    this.displayEnvelope = displayEnvelope;
  }

  public StyledLayerDescriptor getLocalSld() {
    return localSld;
  }

  public void setLocalSld(StyledLayerDescriptor localSld) {
    this.localSld = localSld;
  }

  public Set<String> getDisplayedLayers() {
    return displayedLayers;
  }

  public void setDisplayedLayers(Set<String> displayedLayers) {
    this.displayedLayers = displayedLayers;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((displayEnvelope == null) ? 0 : displayEnvelope.hashCode());
    result = prime * result
        + ((displayedLayers == null) ? 0 : displayedLayers.hashCode());
    result = prime * result + ((geoCenter == null) ? 0 : geoCenter.hashCode());
    result = prime * result + ((localSld == null) ? 0 : localSld.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DatabaseView other = (DatabaseView) obj;
    if (displayEnvelope == null) {
      if (other.displayEnvelope != null)
        return false;
    } else if (!displayEnvelope.equals(other.displayEnvelope))
      return false;
    if (displayedLayers == null) {
      if (other.displayedLayers != null)
        return false;
    } else if (!displayedLayers.equals(other.displayedLayers))
      return false;
    if (geoCenter == null) {
      if (other.geoCenter != null)
        return false;
    } else if (!geoCenter.equals(other.geoCenter))
      return false;
    if (localSld == null) {
      if (other.localSld != null)
        return false;
    } else if (!localSld.equals(other.localSld))
      return false;
    return true;
  }

  public void saveToXml(Element root, Document xmlDoc, String databaseFileName) {
    Node n = null;
    Element centerElem = xmlDoc.createElement("geo-center");
    root.appendChild(centerElem);
    Element xElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getGeoCenter().getX()));
    xElem.appendChild(n);
    centerElem.appendChild(xElem);
    Element yElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getGeoCenter().getY()));
    yElem.appendChild(n);
    centerElem.appendChild(yElem);
    Element envElem = xmlDoc.createElement("envelope");
    root.appendChild(envElem);
    Element lCornerElem = xmlDoc.createElement("lower-corner");
    envElem.appendChild(lCornerElem);
    Element xlcElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getLowerCorner().getX()));
    xlcElem.appendChild(n);
    lCornerElem.appendChild(xlcElem);
    Element ylcElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getLowerCorner().getY()));
    ylcElem.appendChild(n);
    lCornerElem.appendChild(ylcElem);
    Element uCornerElem = xmlDoc.createElement("upper-corner");
    envElem.appendChild(uCornerElem);
    Element xucElem = xmlDoc.createElement("x");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getUpperCorner().getX()));
    xucElem.appendChild(n);
    uCornerElem.appendChild(xucElem);
    Element yucElem = xmlDoc.createElement("y");
    n = xmlDoc.createTextNode(String.valueOf(this.getDisplayEnvelope()
        .getUpperCorner().getY()));
    yucElem.appendChild(n);
    uCornerElem.appendChild(yucElem);

    // the sld in a separate file
    String fileName = databaseFileName.replace(".xml", "_sld_" + doc.getName()
        + ".xml");
    Element sldElem = xmlDoc.createElement("sld-file");
    n = xmlDoc.createTextNode(fileName);
    sldElem.appendChild(n);
    root.appendChild(sldElem);
    localSld.marshall(fileName);

    // the displayed layers
    for (String layer : displayedLayers) {
      Element layerElem = xmlDoc.createElement("displayed-layer");
      n = xmlDoc.createTextNode(layer);
      layerElem.appendChild(n);
      root.appendChild(layerElem);
    }
  }

  private void readFromXml(Element xmlElem) {

    // the geoCenter
    Element centerElem = (Element) xmlElem.getElementsByTagName("geo-center")
        .item(0);
    Element xCenterElem = (Element) centerElem.getElementsByTagName("x")
        .item(0);
    double xCenter = Double.valueOf(xCenterElem.getChildNodes().item(0)
        .getNodeValue());
    Element yCenterElem = (Element) centerElem.getElementsByTagName("y")
        .item(0);
    double yCenter = Double.valueOf(yCenterElem.getChildNodes().item(0)
        .getNodeValue());
    this.setGeoCenter(new DirectPosition(xCenter, yCenter));

    // the display envelope
    Element envElem = (Element) xmlElem.getElementsByTagName("envelope")
        .item(0);
    Element lCornerElem = (Element) envElem
        .getElementsByTagName("lower-corner").item(0);
    Element xlcElem = (Element) lCornerElem.getElementsByTagName("x").item(0);
    double xlCorner = Double.valueOf(xlcElem.getChildNodes().item(0)
        .getNodeValue());
    Element ylcElem = (Element) lCornerElem.getElementsByTagName("y").item(0);
    double ylCorner = Double.valueOf(ylcElem.getChildNodes().item(0)
        .getNodeValue());
    Element uCornerElem = (Element) envElem
        .getElementsByTagName("upper-corner").item(0);
    Element xucElem = (Element) uCornerElem.getElementsByTagName("x").item(0);
    double xuCorner = Double.valueOf(xucElem.getChildNodes().item(0)
        .getNodeValue());
    Element yucElem = (Element) uCornerElem.getElementsByTagName("y").item(0);
    double yuCorner = Double.valueOf(yucElem.getChildNodes().item(0)
        .getNodeValue());
    IDirectPosition lCorner = new DirectPosition(xlCorner, ylCorner);
    IDirectPosition uCorner = new DirectPosition(xuCorner, yuCorner);
    this.setDisplayEnvelope(new GM_Envelope(uCorner, lCorner));

    // the SLD
    Element sldElem = (Element) xmlElem.getElementsByTagName("sld-file")
        .item(0);
    String fileName = sldElem.getChildNodes().item(0).getNodeValue();
    try {
      this.localSld = StyledLayerDescriptor.unmarshall(fileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    // the displayed layers
    this.displayedLayers = new HashSet<String>();
    for (int i = 0; i < xmlElem.getElementsByTagName("displayed-layer")
        .getLength(); i++) {
      Element layerElem = (Element) xmlElem.getElementsByTagName(
          "displayed-layer").item(i);
      this.displayedLayers
          .add(layerElem.getChildNodes().item(0).getNodeValue());
    }
  }

}
