/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementZonalReseau;

/**
 * lacs, etendues d'eau diverses representees sous forme surfacique
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface SurfaceDEau extends ElementZonalReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return l'altitude moyenne de l'objet
   */
  public double getZMoy();

  public void setZMoy(double zMoy);

  /**
   * @return le regime
   */
  public Regime getRegime();

  public void setRegime(Regime regime);

}
