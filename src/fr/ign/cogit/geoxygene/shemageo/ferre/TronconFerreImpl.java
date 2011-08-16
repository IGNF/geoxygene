/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.ferre.TronconFerre;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ArcReseauImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class TronconFerreImpl extends ArcReseauImpl implements TronconFerre {

  public TronconFerreImpl(Reseau res, boolean estFictif, ICurve geom) {
    super(res, estFictif, geom);
  }

  private String energie = "";

  public String getEnergie() {
    return this.energie;
  }

  public void setEnergie(String energie) {
    this.energie = energie;
  }

  private int nombreVoies = 0;

  public int getNombreVoies() {
    return this.nombreVoies;
  }

  public void setNombreVoies(int nombreVoies) {
    this.nombreVoies = nombreVoies;
  }

  private String largeurVoie = "";

  public String getLargeurVoie() {
    return this.largeurVoie;
  }

  public void setLargeurVoie(String largeurVoie) {
    this.largeurVoie = largeurVoie;
  }

  private int positionParRapportAuSol = 0;

  public int getPositionParRapportAuSol() {
    return this.positionParRapportAuSol;
  }

  public void setPositionParRapportAuSol(int positionParRapportAuSol) {
    this.positionParRapportAuSol = positionParRapportAuSol;
  }

  private String classement = "";

  public String getClassement() {
    return this.classement;
  }

  public void setClassement(String classement) {
    this.classement = classement;
  }

  private String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  private double zIni = 0;

  public double getZIni() {
    return this.zIni;
  }

  public void setZIni(double zIni) {
    this.zIni = zIni;
  }

  private double zFin = 0;

  public double getZFin() {
    return this.zFin;
  }

  public void setZFin(double zFin) {
    this.zFin = zFin;
  }

}
