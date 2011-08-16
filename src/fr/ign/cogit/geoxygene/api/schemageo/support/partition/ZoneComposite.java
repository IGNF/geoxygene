/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.partition;

import java.util.Collection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ZoneComposite extends Zone {

  /**
   * @return les zones composant l'objet
   */
  public Collection<Zone> getZones();

  /**
   * @param zones les zones composant l'objet
   */
  public void setZones(Collection<Zone> zones);

}
