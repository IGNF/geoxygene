/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.PassePar;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * permet de modeliser les franchissements sans noeud (comme dans la BD carto)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class FranchissementImpl extends ElementDuReseauImpl implements
    Franchissement {

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  };

  /**
   * les objets passage
   */
  Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  @Override
  public Collection<PassePar> getPassePar() {
    return this.passePar;
  }

}
