/**
 * @author julien Gaffuri 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementLieAuReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 20 juil. 2009
 * 
 */
@Entity
@Table(name = "reseau")
public class ReseauImpl extends AgregatReseauImpl implements Reseau {

  /**
   * le nom de l'objet
   */
  String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * elements non topologiques lies au reseau
   */
  Collection<ElementLieAuReseau> elementsLiesAuReseau = new FT_FeatureCollection<ElementLieAuReseau>();

  @Override
  public Collection<ElementLieAuReseau> getElementsLiesAuReseau() {
    return this.elementsLiesAuReseau;
  }
}
