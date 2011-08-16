/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface TronconHydrographique extends ArcReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return le caractere artificiel de l'objet
   */
  public boolean isArtificiel();

  public void setArtificiel(boolean artificiel);

  /**
   * @return la position par rapport au sol
   */
  public int getPositionParRapportAuSol();

  public void setPositionParRapportAuSol(int positionParRapportAuSol);

  /**
   * @return le regime
   */
  public Regime getRegime();

  public void setRegime(Regime regime);

  /**
   * @return l'altitude initiale de l'objet
   */
  public double getZIni();

  public void setZIni(double zIni);

  /**
   * @return l'altitude finale de l'objet
   */
  public double getZFin();

  public void setZFin(double zFin);

  /**
   * @return la largeur
   */
  public double getLargeur();

  public void setLargeur(double largeur);

}
