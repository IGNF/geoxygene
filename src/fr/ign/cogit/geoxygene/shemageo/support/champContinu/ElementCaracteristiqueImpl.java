/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ElementCaracteristique;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ElementCaracteristiqueImpl extends DefaultFeature implements
    ElementCaracteristique {

  /**
   * le constructeur par defaut
   * 
   * @param champContinu
   * @param geom
   */
  public ElementCaracteristiqueImpl(ChampContinu champContinu, IGeometry geom) {
    this.setChampContinu(champContinu);
    this.setGeom(geom);
  }

  /**
   * le champ continu auquel l'objet appartient
   */
  private ChampContinu champContinu = null;

  public ChampContinu getChampContinu() {
    return this.champContinu;
  }

  public void setChampContinu(ChampContinu champContinu) {
    this.champContinu = champContinu;
  }

}
