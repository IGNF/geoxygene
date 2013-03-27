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
package fr.ign.cogit.geoxygene.client.service;

import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Thread du processus WPS appariement.
 *
 */
public class DemonstrateurServiceAppariementThread extends Thread {

  /** Identifiant du dataset. */
  private String datasetId;
  
  /**
   * The logger used to log the errors or several information.
   * 
   * @see org.apache.log4j.Logger
   */
  protected final transient Logger logger = Logger.getLogger(this.getClass());
  
  /**
   * Constructeur.
   * 
   * @param d : identifiant du dataset
   * @throws Exception
   */
  public DemonstrateurServiceAppariementThread(String d) throws Exception {
    datasetId = d;
  }
  
  /**
   * Launch in thread mode the data matching process.
   */
  public void run() {

    try {
      
      Date startDate = new Date();
      logger.info("Début du process d'appariement " + startDate + ".");

      // 
      DemonstrateurServiceAppariement serviceAppariement = new DemonstrateurServiceAppariement(this);
      serviceAppariement.doCalcul(datasetId);
      
      // Log the end the the request
      Date endDate = new Date();
      logger.info("Process d'appariement terminée correctement en " + (endDate.getTime() - startDate.getTime()) / 1000.00 + " sec.");

      
    } finally {
      
      // Remove itself from the list of running checks
      String key = datasetId;
      ThreadLock.getInstance().releaseProcess(key);
      
      logger.info("Fin du thread pour le datasetId = " + datasetId);
    
    }
    
  }
  
}
