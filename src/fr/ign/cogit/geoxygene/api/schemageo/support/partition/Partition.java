/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.partition;

import java.util.Collection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Partition {

  /**
   * @return l'identifiant de l'objet
   */
  public int getId();

  public void setId(int id);

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return les zones de la partition
   */
  public Collection<Zone> getZones();
}
