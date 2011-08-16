/**
 * 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.bati;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Meso;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;

/**
 * @author JGaffuri 20 juil. 2009
 * 
 */
public interface Ilot extends Meso {

  /**
   * @return les arcs de reseau limitrophes ou contenus dans l'ilot
   */
  public Collection<ArcReseau> getArcsReseaux();

}
