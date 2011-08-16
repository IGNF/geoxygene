/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ArcReseau extends ElementDuReseau {

  /**
   * @return
   */
  public boolean isFictif();

  public void setFictif(boolean fictif);

  @Override
  public ICurve getGeom();

  /**
   * @return la direction du troncon
   */
  public Direction getDirection();

  public void setDirection(Direction direction);

  /**
   * @return le noeud initial de l'arc
   */
  public NoeudReseau getNoeudInitial();

  public void setNoeudInitial(NoeudReseau noeud);

  /**
   * @return le noeud final de l'arc
   */
  public NoeudReseau getNoeudFinal();

  public void setNoeudFinal(NoeudReseau noeud);

  /**
   * @return
   */
  public Collection<PassePar> getPassePar();
}
