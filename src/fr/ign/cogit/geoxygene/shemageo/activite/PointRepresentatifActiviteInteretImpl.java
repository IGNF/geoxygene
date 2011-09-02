/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.activite;

import fr.ign.cogit.geoxygene.api.schemageo.activite.PointRepresentatifActiviteInteret;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.elementsIndependants.MicroImpl;

/**
 * 
 * point d'activite ou d'interet, lieu dit habite ou non, toponyme divers, zone
 * d'habitat, d'activite, etablissement
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class PointRepresentatifActiviteInteretImpl extends MicroImpl implements
    PointRepresentatifActiviteInteret {

  public PointRepresentatifActiviteInteretImpl(IGeometry geom) {
    super(geom);
  }

  /**
   * le nom de l'objet
   */
  private String nom = null;

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

}
