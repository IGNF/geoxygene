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

  @Override
  public String getEnergie() {
    return this.energie;
  }

  @Override
  public void setEnergie(String energie) {
    this.energie = energie;
  }

  private int nombreVoies = 0;

  @Override
  public int getNombreVoies() {
    return this.nombreVoies;
  }

  @Override
  public void setNombreVoies(int nombreVoies) {
    this.nombreVoies = nombreVoies;
  }

  private String largeurVoie = "";

  @Override
  public String getLargeurVoie() {
    return this.largeurVoie;
  }

  @Override
  public void setLargeurVoie(String largeurVoie) {
    this.largeurVoie = largeurVoie;
  }

  private int positionParRapportAuSol = 0;

  @Override
  public int getPositionParRapportAuSol() {
    return this.positionParRapportAuSol;
  }

  @Override
  public void setPositionParRapportAuSol(int positionParRapportAuSol) {
    this.positionParRapportAuSol = positionParRapportAuSol;
  }

  private String classement = "";

  @Override
  public String getClassement() {
    return this.classement;
  }

  @Override
  public void setClassement(String classement) {
    this.classement = classement;
  }

  private String nom = "";

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  private double zIni = 0;

  @Override
  public double getZIni() {
    return this.zIni;
  }

  @Override
  public void setZIni(double zIni) {
    this.zIni = zIni;
  }

  private double zFin = 0;

  @Override
  public double getZFin() {
    return this.zFin;
  }

  @Override
  public void setZFin(double zFin) {
    this.zFin = zFin;
  }

}
