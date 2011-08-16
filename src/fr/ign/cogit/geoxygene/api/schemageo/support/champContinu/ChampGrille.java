/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IGriddedSurface;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ChampGrille {

  /**
   * @return la geometrie grille de l'objet
   */
  public IGriddedSurface getGeom();

  /**
   * @param griddedSurface la geometrie grille de l'objet
   */
  public void setGeom(IGriddedSurface griddedSurface);

}
