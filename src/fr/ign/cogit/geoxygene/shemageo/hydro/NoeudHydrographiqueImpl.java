/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.hydro.NoeudHydrographique;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.NoeudReseauImpl;

/**
 * Noeud du reseau routier + certains points d'eau (sources, pertes, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class NoeudHydrographiqueImpl extends NoeudReseauImpl implements
    NoeudHydrographique {

  /**
   * le nom
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
   * le type
   */
  private FeatureType type;

  public FeatureType getType() {
    return this.type;
  }

  public void setType(FeatureType type) {
    this.type = type;
  }

  /**
   * la cote
   */
  private double cote = 0;

  @Override
  public double getCote() {
    return this.cote;
  }

  @Override
  public void setCote(double cote) {
    this.cote = cote;
  }
}
