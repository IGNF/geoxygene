/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampGrille;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IGriddedSurface;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ChampGrilleImpl extends ChampContinuImpl implements ChampGrille {

  /**
   * la geometrie grille de l'objet
   */
  private IGriddedSurface griddedSurface = null;

  @Override
  public IGriddedSurface getGeom() {
    return this.griddedSurface;
  }

  @Override
  public void setGeom(IGriddedSurface griddedSurface) {
    this.griddedSurface = griddedSurface;
  }

}
