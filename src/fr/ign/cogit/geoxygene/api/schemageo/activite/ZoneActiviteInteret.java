/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.activite;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Micro;

/**
 * 
 * surface activite, terrain de sport (?), cimetiere, piste aerodrome, zone
 * reglementee touristique, enceinte, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface ZoneActiviteInteret extends Micro {

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);
}
