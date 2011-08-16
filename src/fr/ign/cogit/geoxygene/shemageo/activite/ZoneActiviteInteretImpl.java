/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.activite;

import fr.ign.cogit.geoxygene.api.schemageo.activite.ZoneActiviteInteret;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MicroImpl;

/**
 * 
 * surface activite, terrain de sport (?), cimetiere, piste aerodrome, zone
 * reglementee touristique, enceinte, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class ZoneActiviteInteretImpl extends MicroImpl implements
    ZoneActiviteInteret {

  public ZoneActiviteInteretImpl(IGeometry geom) {
    super(geom);
  }

  /**
   * le nom de l'objet
   */
  private String nom = null;

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

}
