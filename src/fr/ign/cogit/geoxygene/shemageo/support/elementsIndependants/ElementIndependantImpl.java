/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants;

import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants.Meso;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public abstract class ElementIndependantImpl extends DefaultFeature implements
    ElementIndependant {

  /**
   * le meso (eventuel) dont l'objet est composant
   */
  private Meso meso = null;

  public Meso getMeso() {
    return this.meso;
  }

  public void setMeso(Meso meso) {
    this.meso = meso;
  }

}
