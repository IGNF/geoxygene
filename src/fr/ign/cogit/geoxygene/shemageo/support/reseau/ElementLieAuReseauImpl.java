/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementDuReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementLieAuReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * represente les elements lies thematiquement au reseau mais sans incidence sur
 * la topologie
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ElementLieAuReseauImpl extends DefaultFeature implements
    ElementLieAuReseau {

  /**
   * le reseau
   */
  Reseau reseau = null;

  @Override
  public Reseau getReseau() {
    return this.reseau;
  }

  @Override
  public void setReseau(Reseau reseau) {
    this.reseau = reseau;
  }

  /**
   * les elements topologiques du reseau auxquel l'objet est lie
   */
  Collection<ElementDuReseau> elementsDuReseau = new FT_FeatureCollection<ElementDuReseau>();

  @Override
  public Collection<ElementDuReseau> getElementsDuReseau() {
    return this.elementsDuReseau;
  }

}
