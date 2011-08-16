/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import java.util.Collection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ChampContinu {

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return l'identifiant de l'objet
   */
  public int getId();

  public void setId(int id);

  /**
   * @return les isolignes de l'objet
   */
  public Collection<Isoligne> getIsolignes();

  public void setIsolignes(Collection<Isoligne> isolignes);

  /**
   * @return les points cotes de l'objet
   */
  public Collection<PointCote> getPointsCotes();

  public void setPointsCotes(Collection<PointCote> pointsCotes);

  /**
   * @return les elements caracteristiques de l'objet
   */
  public Collection<ElementCaracteristique> getElementsCaracteristiques();

  public void setElementsCaracteristiques(
      Collection<ElementCaracteristique> elementsCaracteristiques);

}
