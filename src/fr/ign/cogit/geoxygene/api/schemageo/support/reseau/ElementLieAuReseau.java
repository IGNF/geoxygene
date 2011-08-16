/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * represente les elements lies thematiquement au reseau mais sans incidence sur
 * la topologie
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementLieAuReseau extends IFeature {

  /**
   * @return le reseau
   */
  public Reseau getReseau();

  public void setReseau(Reseau reseau);

  /**
   * @return elements topologiques du reseau lies a l'objet
   */
  public Collection<ElementDuReseau> getElementsDuReseau();
}
