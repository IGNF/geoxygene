/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementIndependant extends IFeature {

  /**
   * @return le meso (eventuel) dont l'objet est composant
   */
  public Meso getMeso();

  /**
   * @param meso le meso dont l'objet est composant
   */
  public void setMeso(Meso meso);

}
