/**
 * 
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * @author julien Gaffuri 21 octobre 2009
 * 
 */
public interface PointCote extends IFeature {

  /**
   * @return la valeur du point cote
   */
  public double getValeur();

  public void setValeur(double valeur);

  @Override
  public IPoint getGeom();

  /**
   * @return le champ continu du point cote
   */
  public ChampContinu getChampContinu();

  public void setChampContinu(ChampContinu champContinu);

}
