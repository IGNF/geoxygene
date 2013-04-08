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
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.geotools.GML;
import org.geotools.GML.Version;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElement;


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
  public String generateXMLResponse(ResultNetworkDataMatching resultatAppariement) throws Exception {
    
    LOGGER.info("------------------------------------------------------------------------");
    LOGGER.info("Start generating resultat appariement xml response.");
    
    StringBuffer result = new StringBuffer();
    
    // TODO Links data set
    // coming soon ....

    // Start document
    result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    result.append("<NetworkDataMatchingResult>");
    
    // Stats results.
    ResultNetworkStat resultatStatAppariement = resultatAppariement.getResultStat();
    
    // Edges evaluation of the less detailed network
    result.append("<EdgesEvaluationRef>");
    ResultNetworkStatElement edgesEvaluationRef = resultatStatAppariement.getStatsEdgesOfNetwork1();
    result.append("<TotalNumber>" + edgesEvaluationRef.getTotalNetworkElementNumber() + "</TotalNumber>");
    result.append("<OkNumber>" + edgesEvaluationRef.getCorrectMatchingNetworkElementNumber() + "</OkNumber>");
    result.append("<KoNumber>" + edgesEvaluationRef.getNoMatchingNetworkElementNumber() + "</KoNumber>");
    result.append("<DoubtfulNumber>" + edgesEvaluationRef.getDoubtfulNetworkElementNumber() + "</DoubtfulNumber>");
    result.append("</EdgesEvaluationRef>");
    
    // Nodes evaluation of the less detailed network
    result.append("<NodesEvaluationRef>");
    ResultNetworkStatElement nodesEvaluationRef = resultatStatAppariement.getStatsNodesOfNetwork1();
    result.append("<TotalNumber>" + nodesEvaluationRef.getTotalNetworkElementNumber() + "</TotalNumber>");
    result.append("<OkNumber>" + nodesEvaluationRef.getCorrectMatchingNetworkElementNumber() + "</OkNumber>");
    result.append("<KoNumber>" + nodesEvaluationRef.getNoMatchingNetworkElementNumber() + "</KoNumber>");
    result.append("<DoubtfulNumber>" + nodesEvaluationRef.getDoubtfulNetworkElementNumber() + "</DoubtfulNumber>");
    result.append("</NodesEvaluationRef>");
    
    // Edges evaluation of the less detailed network
    result.append("<EdgesEvaluationComp>");
    ResultNetworkStatElement edgesEvaluationComp = resultatStatAppariement.getStatsEdgesOfNetwork2();
    result.append("<TotalNumber>" + edgesEvaluationComp.getTotalNetworkElementNumber() + "</TotalNumber>");
    result.append("<OkNumber>" + edgesEvaluationComp.getCorrectMatchingNetworkElementNumber() + "</OkNumber>");
    result.append("<KoNumber>" + edgesEvaluationComp.getNoMatchingNetworkElementNumber() + "</KoNumber>");
    result.append("<DoubtfulNumber>" + edgesEvaluationComp.getDoubtfulNetworkElementNumber() + "</DoubtfulNumber>");
    result.append("</EdgesEvaluationComp>");
    
    // Nodes evaluation of the less detailed network
    result.append("<NodesEvaluationComp>");
    ResultNetworkStatElement nodesEvaluationComp = resultatStatAppariement.getStatsNodesOfNetwork2();
    result.append("<TotalNumber>" + nodesEvaluationComp.getTotalNetworkElementNumber() + "</TotalNumber>");
    result.append("<OkNumber>" + nodesEvaluationComp.getCorrectMatchingNetworkElementNumber() + "</OkNumber>");
    result.append("<KoNumber>" + nodesEvaluationComp.getNoMatchingNetworkElementNumber() + "</KoNumber>");
    result.append("<DoubtfulNumber>" + nodesEvaluationComp.getDoubtfulNetworkElementNumber() + "</DoubtfulNumber>");
    result.append("</NodesEvaluationComp>");
    
    // GML Network Matched
    result.append("<NetworkMatched>");
    try {
      
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      GML encode = new GML(Version.WFS1_0);
      encode.setNamespace("geotools", "http://geotools.org");
      encode.encode(output, resultatAppariement.getNetworkMatched());
      
      String buffer = output.toString();
      int begin = buffer.indexOf("wfs:FeatureCollection");
      buffer = buffer.substring(begin - 1, buffer.length() - 1);
          
      // Add to the document
      result.append(buffer);
    
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
  }

}
