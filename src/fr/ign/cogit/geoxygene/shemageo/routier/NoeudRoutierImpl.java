/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.NoeudReseauImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class NoeudRoutierImpl extends NoeudReseauImpl implements NoeudRoutier {

  public NoeudRoutierImpl(Reseau res, IPoint geom) {
    this.setReseau(res);
    this.setGeom(geom);
  }

  public NoeudRoutierImpl() {
    super();
  }
}
