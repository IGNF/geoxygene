/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.enrichment;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;

/**
 * This is the abstract class of the preprocesses triggered in a
 * "PEA REP kind of" scale master process. For instance, a sub class made for
 * turning the road network into a planar graph has IRoadLine as processed
 * class, and the method execute() triggers the algorithm that cuts roads at
 * intersections.
 * @author GTouya
 * 
 */
public abstract class ScaleMasterPreProcess {

  private Set<Class<? extends IGeneObj>> processedClasses;
  private boolean done = false;

  /**
   * Execute the enrichment process in a given dataset.
   */
  public abstract void execute(CartAGenDB dataset) throws Exception;

  public abstract String getPreProcessName();

  public void setDone(boolean done) {
    this.done = done;
  }

  public boolean isDone() {
    return done;
  }

  public Set<Class<? extends IGeneObj>> getProcessedClasses() {
    return processedClasses;
  }

  public void setProcessedClasses(
      Set<Class<? extends IGeneObj>> processedClasses) {
    this.processedClasses = processedClasses;
  }
}
