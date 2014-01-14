package fr.ign.cogit.geoxygene.contrib.data;

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

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;

/**
 * 
 * 
 * @author MDVan-Damme
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "links"
})
@XmlRootElement(name = "EnsembleDeLiens")
public class EnsembleDeLiensData {
  
  // Links
  @XmlElement(name = "Links")
  EnsembleDeLiens links;
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(EnsembleDeLiensData.class.getName());
  
  /**
   * Constructeur.
   */
  public EnsembleDeLiensData() {
      super();
  }
  
  /**
   * Constructeur.
   * 
   * @param popEdge
   *            population of edges
   */
  public EnsembleDeLiensData(EnsembleDeLiens links) {
      super();
      this.links = links;
  }
  
  /**
   * @return the population of edges
   */
  public EnsembleDeLiens getLinks() {
    return links;
  }
  
  /**
   * @param liens
   *            the links to set
   */
  public void setLinks(EnsembleDeLiens links) {
    this.links = links;
  }
  
  public void marshall(OutputStream stream) {
    try {
      String result = EnsembleDeLiensData.generateXMLResponse(this);
      stream.write(result.getBytes(Charset.forName("UTF-8")));
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
  
  /**
   * 
   * @param ensembleDeLiens
   * @return
   * @throws Exception
   */
  public static String generateXMLResponse(EnsembleDeLiensData ensembleDeLiens) throws Exception {
    
    StringBuffer result = new StringBuffer();
    
    // TODO : CRS
    CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:2154");
    
    // Convert into valid GML
    FeatureTransformer ft = new FeatureTransformer();
    // set the indentation to 4 spaces
    ft.setIndentation(4);
    // this will allow Features with the FeatureType which has the namespace
    // "http://somewhere.org" to be prefixed with xxx...
    ft.getFeatureNamespaces().declarePrefix("xxx", "http://somewhere.org");
    
    result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    result.append("<EnsembleDeLiens>");
    
    // LINKS
    
    // Convert
    LOGGER.trace("Pop links size = " + ensembleDeLiens.getLinks().size());
    SimpleFeatureCollection liens = GeOxygeneGeoToolsTypes.convert2FeatureCollection(ensembleDeLiens.getLinks(), sourceCRS);
    // transform
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ft.transform(liens, output);
    // Delete first line
    String buffer = output.toString();
    int begin = buffer.indexOf("wfs:FeatureCollection");
    buffer = buffer.substring(begin - 1, buffer.length() - 1);
    // Add to the document
    result.append(buffer);
    
    
    // End document
    result.append("</EnsembleDeLiens>");
    
    return result.toString();
  }

}
