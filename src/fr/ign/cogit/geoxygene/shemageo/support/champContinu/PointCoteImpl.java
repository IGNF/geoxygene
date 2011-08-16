/**
 * 
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.PointCote;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * @author julien Gaffuri 21 octobre 2009
 * 
 */
public class PointCoteImpl extends DefaultFeature implements PointCote {

  public PointCoteImpl(ChampContinu champContinu, double valeur, IPoint geom) {
    this();
    this.setChampContinu(champContinu);
    this.setValeur(valeur);
    this.setGeom(geom);
  }

  public PointCoteImpl() {
    super();
  }

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  /**
   * le champ continu de l'objet
   */
  private ChampContinu champContinu = null;

  public ChampContinu getChampContinu() {
    return this.champContinu;
  }

  public void setChampContinu(ChampContinu champContinu) {
    this.champContinu = champContinu;
  }

  /**
   * la valeur de l'objet
   */
  private double valeur = 0;

  public double getValeur() {
    return this.valeur;
  }

  public void setValeur(double valeur) {
    this.valeur = valeur;
  }
}
