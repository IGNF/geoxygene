/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.champContinu;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ElementCaracteristique;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.Isoligne;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.PointCote;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "champ_continu")
public class ChampContinuImpl implements ChampContinu {

  /**
   * l'id de l'objet
   */
  private int id = 0;

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * le nom de l'objet
   */
  private String nom = "";

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * les elements caracteristiques de l'objet
   */
  @Transient
  private Collection<ElementCaracteristique> elementsCaracteristiques = new FT_FeatureCollection<ElementCaracteristique>();

  // @Override
  @Override
  @Transient
  public Collection<ElementCaracteristique> getElementsCaracteristiques() {
    return this.elementsCaracteristiques;
  }

  @Override
  public void setElementsCaracteristiques(
      Collection<ElementCaracteristique> elementsCaracteristiques) {
    this.elementsCaracteristiques = elementsCaracteristiques;
  }

  /**
   * les isolignes de l'objet
   */
  private Collection<Isoligne> isolignes = new FT_FeatureCollection<Isoligne>();

  @Override
  @OneToMany(mappedBy = "champContinu", targetEntity = IsoligneImpl.class)
  public Collection<Isoligne> getIsolignes() {
    return this.isolignes;
  }

  @Override
  public void setIsolignes(Collection<Isoligne> isolignes) {
    this.isolignes = isolignes;
  }

  /**
   * les points cotes de l'objet
   */
  private Collection<PointCote> pointsCotes = new FT_FeatureCollection<PointCote>();

  @Override
  public Collection<PointCote> getPointsCotes() {
    return this.pointsCotes;
  }

  @Override
  public void setPointsCotes(Collection<PointCote> pointsCotes) {
    this.pointsCotes = pointsCotes;
  }

}
