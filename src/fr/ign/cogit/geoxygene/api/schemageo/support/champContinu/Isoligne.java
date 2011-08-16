/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Isoligne extends IFeature {

  /**
   * @return la valeur de l'isoligne
   */
  public double getValeur();

  /**
   * @param valeur la valeur de l'isoligne
   */
  public void setValeur(double valeur);

  /**
   * @return le champ continu de l'isoligne
   */
  public ChampContinu getChampContinu();

  /**
   * @param champContinu le champ continu de l'isoligne
   */
  public void setChampContinu(ChampContinu champContinu);

  @Override
  public ICurve getGeom();

}
