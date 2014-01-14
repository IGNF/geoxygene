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

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
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
    "popEdge",
    "popNode",
    "popFace"
})
@XmlRootElement(name = "CarteTopo")
public class CarteTopoData {
  
  // Edge
  @XmlElement(name = "Edge")
  private IPopulation<Arc> popEdge;
  
  // Node
  @XmlElement(name = "Node")
  private IPopulation<Noeud> popNode;
  
  // Face
  @XmlElement(name = "Face")
  private IPopulation<Face> popFace;
  
  
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
  public CarteTopoData(IPopulation<Arc> popEdge, IPopulation<Noeud> popNode, IPopulation<Face> popFace) {
      super();
      this.popEdge = popEdge;
      this.popNode = popNode;
      this.popFace = popFace;
  }
  
  /**
   * @return the population of edges
   */
  public IPopulation<Arc> getPopEdge() {
    return popEdge;
  }
  
  /**
   * @return the population of nodes
   */
  public IPopulation<Noeud> getPopNode() {
    return popNode;
  }
  
  /**
   * @return the population of faces
   */
  public IPopulation<Face> getPopFace() {
    return popFace;
  }
  
  /**
   * @param popEdge
   *            the the population of edges to set
   */
  public void setPopEdge(IPopulation<Arc> popEdge) {
    this.popEdge = popEdge;
  }
  
  /**
   * @param popNode
   *            the population of nodes to set
   */
  public void setPopNode(IPopulation<Noeud> popNode) {
    this.popNode = popNode;
  }
  
  /**
   * @param popFace
   *            the population of faces to set
   */
  public void setPopFace(IPopulation<Face> popFace) {
    this.popFace = popFace;
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
    result.append("<CarteTopo>");
    
    // EDGES
    result.append("<Edges>");
    // Convert
    LOGGER.trace("Pop edges size = " + carteTopo.getPopEdge().size());
    SimpleFeatureCollection edges = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopEdge(), sourceCRS);
    // transform
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ft.transform(edges, output);
    // Delete first line
    String buffer = output.toString();
    int begin = buffer.indexOf("wfs:FeatureCollection");
    buffer = buffer.substring(begin - 1, buffer.length() - 1);
    // Add to the document
    result.append(buffer);
    result.append("</Edges>");
    
    // NODES
    result.append("<Nodes>");
    // Convert
    LOGGER.trace("Pop nodes size = " + carteTopo.getPopNode().size());
    SimpleFeatureCollection nodes = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopNode(), sourceCRS);
    // transform
    output = new ByteArrayOutputStream();
    ft.transform(nodes, output);
    // Delete first line
    buffer = output.toString();
    begin = buffer.indexOf("wfs:FeatureCollection");
    buffer = buffer.substring(begin - 1, buffer.length() - 1);
    // Add to the document
    result.append(buffer);
    result.append("</Nodes>");
    
    // Faces
    result.append("<Faces>");
    // Convert
    LOGGER.trace("Pop faces size = " + carteTopo.getPopFace().size());
    SimpleFeatureCollection faces = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopFace(), sourceCRS);
    // transform
    output = new ByteArrayOutputStream();
    ft.transform(faces, output);
    // Delete first line
    buffer = output.toString();
    begin = buffer.indexOf("wfs:FeatureCollection");
    buffer = buffer.substring(begin - 1, buffer.length() - 1);
    // Add to the document
    result.append(buffer);
    result.append("</Faces>");
    
    // End document
    result.append("</CarteTopo>");
    
    return result.toString();
  }
  
  
}
