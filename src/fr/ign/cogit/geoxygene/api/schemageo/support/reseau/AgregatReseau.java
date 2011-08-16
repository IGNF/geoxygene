/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * agregat d'elements du reseau (ex: itineraire, riviere, echangeur, rond-point,
 * etc.)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface AgregatReseau extends IFeature {

  /**
   * @return les noeuds de l'agregat
   */
  public Collection<NoeudReseau> getNoeuds();

  public void setNoeuds(Collection<NoeudReseau> noeuds);

  /**
   * @return les arcs de l'agregat
   */
  public Collection<ArcReseau> getArcs();

  public void setArcs(Collection<ArcReseau> arcs);

  /**
   * @return les "passe par" de l'agregat
   */
  public Collection<PassePar> getPassepar();

  public void setPassePar(Collection<PassePar> passePar);

  /**
   * @return les franchissements de l'agregat
   */
  public Collection<Franchissement> getFranchissements();

  public void setFranchissements(Collection<Franchissement> franchissement);
}
