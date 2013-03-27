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
package fr.ign.cogit.geoxygene.client;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.client.service.DemonstrateurServiceAppariementThread;
import fr.ign.cogit.geoxygene.client.service.ThreadLock;

/**
 *
 */
public class TestAppelServiceHttpThread {
  
  protected final static Logger logger = Logger.getLogger(TestAppelServiceHttpThread.class.getName());
  
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    String datasetId = "504";
    DemonstrateurServiceAppariementThread process = (DemonstrateurServiceAppariementThread) ThreadLock.getInstance().getProcess(datasetId);
    
    if (process != null) {
      
      logger.error("Un process est déjà en cours pour ce dataset.");
    
    } else {
      
      try {
        // Lancement du thread d'appariement
        process = new DemonstrateurServiceAppariementThread(datasetId);
        process.start();
  
        // Enregistrement du thread en cours
        ThreadLock.getInstance().lockProcess(datasetId, process);
        
        logger.info("Le thread est lancé, vérouillé et on a récupéré la main !");
      
      } catch (Exception e) {
        logger.error("Erreur dans le process d'appariement.");
        e.printStackTrace();
      }
    
    }
    
  }

}
