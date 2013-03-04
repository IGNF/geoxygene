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

import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import com.thoughtworks.xstream.io.xml.SaxWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.geoserver.wps.ppio.XStreamPPIO;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Encoder;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ResultatAppariement;
import fr.ign.cogit.geoxygene.wps.contrib.datamatching.NetworkDataMatchingProcess;

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
    
    LOGGER.info("Start encoding the result for output.");
    
    StringBuffer result = new StringBuffer();

    // Start
    // result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    result.append("<NetworkDataMatchingResult>");
    
    // Stats
    result.append("<NbArcRef>");
    result.append(((ResultatAppariement)obj).getNbArcRef());
    result.append("</NbArcRef>");
    result.append("<NbArcComp>");
    result.append(((ResultatAppariement)obj).getNbArcComp());
    result.append("</NbArcComp>");
    
    // GML Network Matched
    result.append("<NetworkMatched>");
    /*Configuration xml = new GMLConfiguration();
    Encoder e = new Encoder(xml);
    e.encode(((ResultatAppariement)obj).getNetworkMatched(), element, handler);*/
    result.append("</NetworkMatched>");
    
    // End 
    result.append("</NetworkDataMatchingResult>");
    
    SaxWriter writer = new SaxWriter();
    writer.setContentHandler(handler);
    // write out xml
    XStream xstream = new XStream();
    xstream.marshal(result, writer);
      
    /*    
      Encode de XStreamPPIO
      
      // prepare xml encoding
      XStream xstream = buildXStream();

      // bind with the content handler
      SaxWriter writer = new SaxWriter();
      writer.setContentHandler(handler);

      // write out xml
      xstream.marshal(object, writer);
      */
    
  }
  
  /*@Override
  protected XStream buildXStream() {
    
    System.out.println("******   buildXStream ********");
    XStream xstream = new XStream() {
      protected MapperWrapper wrapMapper(MapperWrapper next) {
        return new UppercaseTagMapper(next);
      };
    };
    xstream.alias("NetworkDataMatchingResult", ResultatAppariement.class);
    return xstream;
  }*/
  
  
}
