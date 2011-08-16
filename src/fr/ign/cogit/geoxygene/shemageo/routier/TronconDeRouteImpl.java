/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import java.util.Date;

import fr.ign.cogit.geoxygene.api.schemageo.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ArcReseauImpl;

/**
 * troncon de transport routier (route, chemin, GR, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class TronconDeRouteImpl extends ArcReseauImpl implements TronconDeRoute {

  public TronconDeRouteImpl(Reseau res, boolean estFictif, ICurve geom) {
    super(res, estFictif, geom);
  }

  private int nombreDeVoies = 0;

  public int getNombreDeVoies() {
    return this.nombreDeVoies;
  }

  public void setNombreDeVoies(int nombreDeVoies) {
    this.nombreDeVoies = nombreDeVoies;
  }

  private Date dateMiseEnService = null;

  public Date getDateMiseEnService() {
    return this.dateMiseEnService;
  }

  public void setDateMiseEnService(Date dateMiseEnService) {
    this.dateMiseEnService = dateMiseEnService;
  }

  private String etatPhysique = "";

  public String getEtatPhysique() {
    return this.etatPhysique;
  }

  public void setEtatPhysique(String etatPhysique) {
    this.etatPhysique = etatPhysique;
  }

  private String acces = "";

  public String getAcces() {
    return this.acces;
  }

  public void setAcces(String acces) {
    this.acces = acces;
  }

  private double zIni = 0;

  public double getzIni() {
    return this.zIni;
  }

  public void setzIni(double zIni) {
    this.zIni = zIni;
  }

  private double zFin = 0;

  public double getzFin() {
    return this.zFin;
  }

  public void setzFin(double zFin) {
    this.zFin = zFin;
  }

  private String nomItineraire = "";

  public String getNomItineraire() {
    return this.nomItineraire;
  }

  public void setNomItineraire(String nomItineraire) {
    this.nomItineraire = nomItineraire;
  }

}
