/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface TronconFerre extends ArcReseau {

  /**
   * @return l'energie
   */
  public String getEnergie();

  public void setEnergie(String energie);

  /**
   * @return le nombre de voie
   */
  public int getNombreVoies();

  public void setNombreVoies(int nombreVoies);

  /**
   * @return la largeur de voie
   */
  public String getLargeurVoie();

  public void setLargeurVoie(String largeurVoie);

  /**
   * @return la position par rapport au sol
   */
  public int getPositionParRapportAuSol();

  public void setPositionParRapportAuSol(int positionParRapportAuSol);

  /**
   * @return le classement de l'objet
   */
  public String getClassement();

  public void setClassement(String classement);

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

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

}
