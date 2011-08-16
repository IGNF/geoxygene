/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampTIN;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITin;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ChampTINImpl extends ChampContinuImpl implements ChampTIN {

  /**
   * la geometrie TIN de l'objet
   */
  private ITin tin = null;

  @Override
  public ITin getGeom() {
    return this.tin;
  }

  @Override
  public void setGeom(ITin tin) {
    this.tin = tin;
  }

}
