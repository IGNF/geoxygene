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

import org.geoserver.wps.ppio.XStreamPPIO;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ResultatAppariement;
import fr.ign.cogit.geoxygene.wps.contrib.datamatching.xml.ResultatAppariementParser;


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
    
    // Get XML format for resultatAppariement
    ResultatAppariementParser resultatAppariementParser = new ResultatAppariementParser();
    String result = resultatAppariementParser.generateXMLResponse(((ResultatAppariement)obj));
    
    // Write out xml
    SaxWriter writer = new SaxWriter();
    writer.setContentHandler(handler);
    XStream xstream = new XStream();
    xstream.marshal(result, writer);
    
    LOGGER.info("End encoding the result for output.");
    LOGGER.info("------------------------------------------------------------------------");
    
  }
  
}
