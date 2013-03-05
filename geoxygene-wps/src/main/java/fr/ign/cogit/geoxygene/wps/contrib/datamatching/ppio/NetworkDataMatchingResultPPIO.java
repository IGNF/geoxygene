/**
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL_V2-fr.txt
*        see Licence_CeCILL_V2-en.txt
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
* 
* 
* @copyright IGN
*/
package fr.ign.cogit.geoxygene.wps.contrib.datamatching.ppio;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import com.thoughtworks.xstream.io.xml.SaxWriter;
import com.thoughtworks.xstream.XStream;
import com.vividsolutions.jts.io.gml2.GMLWriter;

import org.geoserver.wps.ppio.XStreamPPIO;
import org.geotools.GML;
import org.geotools.GML.Version;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ResultatAppariement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ResultatStatAppariement;


/**
 * A PPIO to generate good looking xml for the network data mathing process results.
 *    - 
 *    
 * @author M.-D. Van Damme
 */
public class NetworkDataMatchingResultPPIO extends XStreamPPIO {
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(NetworkDataMatchingResultPPIO.class.getName());

  /**
   * Default constructor.
   */
  protected NetworkDataMatchingResultPPIO() {
    super(ResultatAppariement.class);
  }
  
  @Override
  public void encode(Object obj, ContentHandler handler) throws Exception {

    LOGGER.info("------------------------------------------------------------------------");
    LOGGER.info("Start encoding the result for output.");
    
    SaxWriter writer = new SaxWriter();
    GMLWriter gmlWriter = new GMLWriter();
    writer.setContentHandler(handler);
    
    StringBuffer result = new StringBuffer();

    // Start document
    // result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    result.append("<NetworkDataMatchingResult>");
    
    // Stats
    ResultatStatAppariement resultatStatAppariement = ((ResultatAppariement)obj).getResultStat();
    result.append("<NbArcRef>");
    result.append(resultatStatAppariement.getNbArcRef());
    result.append("</NbArcRef>");
    result.append("<NbArcComp>");
    result.append(resultatStatAppariement.getNbArcComp());
    result.append("</NbArcComp>");
    
    // GML Network Matched
    result.append("<NetworkMatched>");
    try {
      System.out.println("------------------------------------------------------------------------");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      GML encode = new GML(Version.WFS1_0);
      encode.setNamespace("geotools", "http://geotools.org");
      encode.encode(output, ((ResultatAppariement)obj).getNetworkMatched());
      result.append(output.toString());
      System.out.println("------------------------------------------------------------------------");
    } catch (Exception e) {
      e.printStackTrace();
    }
    result.append("</NetworkMatched>");
    
    // End document
    result.append("</NetworkDataMatchingResult>");
    
    // write out xml
    XStream xstream = new XStream();
    xstream.marshal(result.toString(), writer);
    System.out.println("------------------------------------------------------------------------");
  }
  
}
