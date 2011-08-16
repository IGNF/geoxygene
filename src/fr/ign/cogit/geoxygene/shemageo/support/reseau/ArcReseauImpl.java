/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Direction;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.PassePar;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "arc_reseau")
public class ArcReseauImpl extends ElementDuReseauImpl implements ArcReseau {

  /**
   * constructeur par defaut
   * @param res
   * @param fictif
   */
  public ArcReseauImpl(Reseau res, boolean fictif, ICurve geom) {
    this();
    this.setReseau(res);
    this.setFictif(fictif);
    this.setGeom(geom);
  }

  public ArcReseauImpl() {
    super();
  }

  @Override
  @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
  public ICurve getGeom() {
    return (ICurve) super.getGeom();
  }

  /**
   * indique si l'arc est fictif ou non
   */
  private boolean fictif = false;

  public boolean isFictif() {
    return this.fictif;
  }

  public void setFictif(boolean fictif) {
    this.fictif = fictif;
  }

  /**
   * donne la direction de l'arc
   */
  private Direction direction = Direction.INCONNU;

  public Direction getDirection() {
    return this.direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * le noeud final de l'arc
   */
  private NoeudReseau noeudFinal = null;

  public NoeudReseau getNoeudFinal() {
    return this.noeudFinal;
  }

  public void setNoeudFinal(NoeudReseau noeudFinal) {
    this.noeudFinal = noeudFinal;
  }

  /**
   * le noeud initial de l'arc
   */
  private NoeudReseau noeudInitial = null;

  public NoeudReseau getNoeudInitial() {
    return this.noeudInitial;
  }

  public void setNoeudInitial(NoeudReseau noeudInitial) {
    this.noeudInitial = noeudInitial;
  }

  /**
	 * 
	 */
  private Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  public Collection<PassePar> getPassePar() {
    return this.passePar;
  }

  @Override
  @ManyToOne(targetEntity = ReseauImpl.class)
  public Reseau getReseau() {
    return super.getReseau();
  }
}
