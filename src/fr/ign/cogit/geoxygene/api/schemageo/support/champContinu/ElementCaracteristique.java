/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementCaracteristique extends IFeature {

  /**
   * @return le champ continu auquel l'objet appartient
   */
  public ChampContinu getChampContinu();

  /**
   * @param champContinu le champ continu auquel l'objet appartient
   */
  public void setChampContinu(ChampContinu champContinu);

}
