package fr.ign.cogit.geoxygene.semio.legend.improvement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Charlotte Hoarau
 * 
 * Basic Stop Criteria.
 * Based on a counter which stops iterations when reaching the given step number.
 * 
 */
public class BasicStopCriteria implements StopCriteria {
  private static final Logger logger = LogManager.getLogger(BasicStopCriteria.class);
  
  /**
   * The counter controlling iterations.
   */
  private int count;
  
  /**
   * Returns the counter controlling iterations.
   * @return The counter controlling iterations.
   */
  public int getCount() {
    return count;
  }

  /**
   * Number of iterations of the Contrast Analysis.
   */
  private int finalNbStep;
  
  public BasicStopCriteria(int finalNbStep) {
    super();
    this.finalNbStep = finalNbStep;
  }
  
  /**
   * Initialize the counter at zero.
   */
  @Override
  public void initialize(){
    this.count = 0;
  }
  
  /**
   * Check if the counter is inferior to the number of iterations.
   * Returns <tt>true</tt> if iterations are finished,
   * and <tt>false</tt> otherwise.
   * @return <tt>true</tt> if iterations are finished, <tt>false</tt> otherwise.
   */
  @Override
  public boolean isChecked() {
    if (this.count < this.finalNbStep) {
      logger.info("Step " + this.getCount());
      this.count ++;
      return false;
    } else {
      logger.info(this.finalNbStep + " improvement steps done");
      return true;
    }
  }

}
