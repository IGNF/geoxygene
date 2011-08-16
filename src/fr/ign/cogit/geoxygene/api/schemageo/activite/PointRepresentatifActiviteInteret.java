/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.activite;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Micro;

/**
 * 
 * point d'activite ou d'interet, lieu dit habite ou non, toponyme divers, zone
 * d'habitat, d'activite, etablissement
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface PointRepresentatifActiviteInteret extends Micro {

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);
}
