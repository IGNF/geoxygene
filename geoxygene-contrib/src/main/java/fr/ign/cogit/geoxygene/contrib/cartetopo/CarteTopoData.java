package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gml.producer.FeatureTransformer;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;




/**
 * Les données de la carte topo. Objet utilisé pour sérialiser/désérialiser une carte topo.
 * @see CarteTopo
 * 
 * 
 * @author MDVan-Damme
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "popEdge"
})
@XmlRootElement(name = "CarteTopo")
public class CarteTopoData {
  
  // Edge
  @XmlElement(name = "Edge")
  private IPopulation<Arc> popEdge;
  
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(CarteTopoData.class.getName());
  
  /**
   * Constructeur.
   */
  public CarteTopoData() {
      super();
  }
  
  /**
   * Constructeur.
   * 
   * @param popEdge
   *            population of edges
   */
  public CarteTopoData(IPopulation<Arc> popEdge) {
      super();
      this.popEdge = popEdge;
  }
  
  /**
   * @return the population of edges
   */
  public IPopulation<Arc> getPopEdge() {
    return popEdge;
  }
  
  /**
   * @param popEdge
   *            the the population of edges to set
   */
  public void setPopEdge(IPopulation<Arc> popEdge) {
    this.popEdge = popEdge;
  }


  
  public void marshall(OutputStream stream) {
    try {
      String result = CarteTopoData.generateXMLResponse(this);
      stream.write(result.getBytes(Charset.forName("UTF-8")));
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
  
  /**
   * 
   * @param carteTopo
   * @return
   * @throws Exception
   */
  public static String generateXMLResponse(CarteTopoData carteTopo) throws Exception {
    
    StringBuffer result = new StringBuffer();
    result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    result.append("<CarteTopo>");
    
    // Convert
    LOGGER.trace("Pop edges size = " + carteTopo.getPopEdge().size());
    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
    SimpleFeatureCollection edges = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopEdge(), sourceCRS);

    // Convert into valid GML
    FeatureTransformer ft = new FeatureTransformer();
    // set the indentation to 4 spaces
    ft.setIndentation(4);
    // this will allow Features with the FeatureType which has the namespace
    // "http://somewhere.org" to be prefixed with xxx...
    ft.getFeatureNamespaces().declarePrefix("xxx", "http://somewhere.org");
    
    // transform
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ft.transform(edges, output);

    // Delete first line
    String buffer = output.toString();
    int begin = buffer.indexOf("wfs:FeatureCollection");
    buffer = buffer.substring(begin - 1, buffer.length() - 1);
    // Add to the document
    result.append(buffer);
    
    // End document
    result.append("</CarteTopo>");
    
    return result.toString();
  }
  
  
}
