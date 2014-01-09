/**
*
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL_V2-fr.txt
*        see Licence_CeCILL_V2-en.txt
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
* 
* 
* @copyright IGN
*
*/
package fr.ign.cogit.process.geoxygene.netmatching.xml;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.geotools.GML;
import org.geotools.GML.Version;

// import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;


/**
 * Parser for the ResultatAppariement object.
 * 
 * @version 1.6
 */
public class ResultatAppariementParser {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ResultatAppariementParser.class.getName());
    
    /**
     * Génère une réponse XML à partir du résultat de l'appariement de réseau.
     * 
     * @param resultatAppariement
     *            L'objet résultat de l'appariement
     * @return Le XML correspondant
     */
    /* public String generateXMLResponse(ResultNetworkDataMatching resultatAppariement) throws Exception {
      
      LOGGER.info("------------------------------------------------------------------------");
      LOGGER.info("Start generating resultat appariement xml response.");
      
      StringBuffer result = new StringBuffer();
      
      // TODO Links data set
      // coming soon ....

      // Start document
      result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      result.append("<NetworkDataMatchingResult>");
      
      // GML Network Matched
      result.append("<NetworkMatched>");
      try {
        
        *//*ByteArrayOutputStream output = new ByteArrayOutputStream();
        GML encode = new GML(Version.WFS1_0);
        encode.setNamespace("geotools", "http://geotools.org");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(resultatAppariement.getNetworkMatched().size());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        encode.encode(output, resultatAppariement.getNetworkMatched());
        
        String buffer = output.toString();
        int begin = buffer.indexOf("wfs:FeatureCollection");
        buffer = buffer.substring(begin - 1, buffer.length() - 1);
            
        // Add to the document
        result.append(buffer);*//*
      
      } catch (Exception e) {
        e.printStackTrace();
      }
      result.append("</NetworkMatched>");
      
      // End document
      result.append("</NetworkDataMatchingResult>");
      
      LOGGER.info("End generating resultat appariement xml response.");
      LOGGER.info("------------------------------------------------------------------------");
      
      // Return the document
      return result.toString();
    
    } */

}
