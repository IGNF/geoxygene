/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import java.util.Date;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ArcReseau;

/**
 * troncon de transport routier (route, chemin, GR, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface TronconDeRoute extends ArcReseau {

  /**
   * @return le nombre de voies
   */
  public int getNombreDeVoies();

  public void setNombreDeVoies(int nombreDeVoies);

  /**
   * @return le date de mise en service
   */
  public Date getDateMiseEnService();

  public void setDateMiseEnService(Date dateMiseEnService);

  /**
   * @return l'etat physique
   */
  public String getEtatPhysique();

  public void setEtatPhysique(String etatPhysique);

  /**
   * @return l'acces
   */
  public String getAcces();

  public void setAcces(String acces);

  /**
   * @return l'altitude initiale
   */
  public double getzIni();

  public void setzIni(double zIni);

  /**
   * @return l'altitude finale
   */
  public double getzFin();

  public void setzFin(double zFin);

  /**
   * @return le nom de l'itineraire eventuel
   */
  public String getNomItineraire();

  public void setNomItineraire(String nomItineraire);
}
