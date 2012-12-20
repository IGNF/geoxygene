/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;

/**
 * This is the abstract class of the enrichments triggered in a scale master
 * process. For instance, a sub class made for roundabout enrichment has only
 * IRoundAbout.class in its enrichmentClasses and the method execute() triggers
 * the roundabout detection algorithm.
 * @author GTouya
 * 
 */
public abstract class ScaleMasterEnrichment {

  private Set<Class<? extends IGeneObj>> enrichmentClasses;
  private boolean done = false;

  /**
   * Execute the enrichment process in a given dataset.
   */
  public abstract void execute(CartAGenDB dataset) throws Exception;

  public abstract String getEnrichmentName();

  public void setEnrichmentClasses(
      Set<Class<? extends IGeneObj>> enrichmentClasses) {
    this.enrichmentClasses = enrichmentClasses;
  }

  public Set<Class<? extends IGeneObj>> getEnrichmentClasses() {
    return enrichmentClasses;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public boolean isDone() {
    return done;
  }
}
