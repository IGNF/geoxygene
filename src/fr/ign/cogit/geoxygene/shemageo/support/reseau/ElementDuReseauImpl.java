/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementDuReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementLieAuReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "element_reseau")
public abstract class ElementDuReseauImpl extends DefaultFeature implements
    ElementDuReseau {

  /**
   * les elements de reseau lies a l'objet
   */
  private Collection<ElementDuReseau> elementsLies = new FT_FeatureCollection<ElementDuReseau>();

  public Collection<ElementDuReseau> getElementsLies() {
    return this.elementsLies;
  }

  /**
   * les agregats que composent l'objet
   */
  private Collection<AgregatReseau> agregats = new ArrayList<AgregatReseau>();

  public Collection<AgregatReseau> getAgregats() {
    return this.agregats;
  }

  /**
   * le reseau auquel appartient l'objet
   */
  private Reseau reseau;

  public Reseau getReseau() {
    return this.reseau;
  }

  public void setReseau(Reseau reseau) {
    this.reseau = reseau;
  }

  /**
   * elements non topologiques lies a l'objet
   */
  private Collection<ElementLieAuReseau> elementLieAuReseau;

  public Collection<ElementLieAuReseau> getElementsLiesAuReseau() {
    return this.elementLieAuReseau;
  }
}
