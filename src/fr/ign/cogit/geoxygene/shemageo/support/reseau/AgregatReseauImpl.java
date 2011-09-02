/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.PassePar;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * agregat d'elements du reseau (ex: itineraire, riviere, echangeur, rond-point,
 * etc.)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class AgregatReseauImpl extends DefaultFeature implements AgregatReseau {

  /**
   * les noeuds de l'agregat
   */
  private Collection<NoeudReseau> noeuds = new FT_FeatureCollection<NoeudReseau>();

  @Override
  public Collection<NoeudReseau> getNoeuds() {
    return this.noeuds;
  }

  @Override
  public void setNoeuds(Collection<NoeudReseau> noeuds) {
    this.noeuds = noeuds;
  }

  /**
   * les arcs de l'agregat
   */
  private Collection<ArcReseau> arcs = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcs() {
    return this.arcs;
  }

  @Override
  public void setArcs(Collection<ArcReseau> arcs) {
    this.arcs = arcs;
  }

  /**
   * les franchissements de l'agregat
   */
  private Collection<Franchissement> franchissements = new FT_FeatureCollection<Franchissement>();

  @Override
  public Collection<Franchissement> getFranchissements() {
    return this.franchissements;
  }

  @Override
  public void setFranchissements(Collection<Franchissement> franchissements) {
    this.franchissements = franchissements;
  }

  /**
   * les passages de l'agregat
   */
  private Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  @Override
  public Collection<PassePar> getPassepar() {
    return this.passePar;
  }

  @Override
  public void setPassePar(Collection<PassePar> passePar) {
    this.passePar = passePar;
  }

}
