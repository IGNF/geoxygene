/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementDuReseau extends IFeature {

  /**
   * @return elements non topologiques lies a l'objet
   */
  public Collection<ElementDuReseau> getElementsLies();

  /**
   * @return les agregats auquels l'element appartient
   */
  public Collection<AgregatReseau> getAgregats();

  /**
   * @return le reseau auquel appartient l'element
   */
  public Reseau getReseau();

  public void setReseau(Reseau reseau);

  /**
   * @return elements non topologiques lies a l'objet
   */
  public Collection<ElementLieAuReseau> getElementsLiesAuReseau();

}
