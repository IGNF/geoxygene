/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.routier.CarrefourComplexe;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.AgregatReseauImpl;

/**
 * carrefour complexe du reseau routier (rond-point, echangeur, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class CarrefourComplexeImpl extends AgregatReseauImpl implements
    CarrefourComplexe {

  /**
   * le nom
   */
  private String nom = "";

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

}
