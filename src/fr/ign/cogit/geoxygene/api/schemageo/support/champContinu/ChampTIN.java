/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITin;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ChampTIN extends ChampContinu {

  /**
   * @return le TIN de l'objet
   */
  public ITin getGeom();

  /**
   * @param tin le TIN de l'objet
   */
  public void setGeom(ITin tin);
}
