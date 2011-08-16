/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.Isoligne;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "isoligne")
public class IsoligneImpl extends DefaultFeature implements Isoligne {

  /**
   * constructeur
   * 
   * @param champContinu
   * @param valeur
   * @param geom
   */
  public IsoligneImpl(ChampContinu champContinu, double valeur, ICurve geom) {
    this();
    this.setChampContinu(champContinu);
    this.setValeur(valeur);
    this.setGeom(geom);
  }

  public IsoligneImpl() {
    super();
  }

  /**
   * la valeur de l'objet
   */
  private double valeur = 0;

  @Override
  public double getValeur() {
    return this.valeur;
  }

  @Override
  public void setValeur(double valeur) {
    this.valeur = valeur;
  }

  /**
   * le champ continu de l'objet
   */
  private ChampContinu champContinu = null;

  @Override
  @ManyToOne(targetEntity = ChampContinuImpl.class)
  public ChampContinu getChampContinu() {
    return this.champContinu;
  }

  @Override
  public void setChampContinu(ChampContinu champContinu) {
    this.champContinu = champContinu;
  }

  @Override
  @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
  public ICurve getGeom() {
    return (ICurve) super.getGeom();
  }

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return super.getId();
  }

}
