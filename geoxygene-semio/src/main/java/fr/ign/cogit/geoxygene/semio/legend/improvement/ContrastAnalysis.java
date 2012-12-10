package fr.ign.cogit.geoxygene.semio.legend.improvement;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;

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
 */
interface ContrastAnalysis {
  
  void initialize(Map map, StopCriteria stop);
  
  void improvementStep();
  
  /**
   * @param neighborsDistance The neighborsDistance
   */
  void run(double neighborsDistance);
}
