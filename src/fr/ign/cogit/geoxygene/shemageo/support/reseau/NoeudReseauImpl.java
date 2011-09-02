/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class NoeudReseauImpl extends ElementDuReseauImpl implements NoeudReseau {

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  /**
   * les arcs entrants du noeud
   */
  private Collection<ArcReseau> arcsEntrants = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcsEntrants() {
    return this.arcsEntrants;
  }

  /**
   * les arcs sortants du noeud
   */
  private Collection<ArcReseau> arcsSortants = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcsSortants() {
    return this.arcsSortants;
  }

}
