/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.hydro;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.ign.cogit.geoxygene.api.schemageo.hydro.Regime;
import fr.ign.cogit.geoxygene.api.schemageo.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ArcReseauImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
@Entity
@Table(name = "troncon_hydrographique")
public class TronconHydrographiqueImpl extends ArcReseauImpl implements
    TronconHydrographique {

  /**
   * @param res
   * @param estFictif
   */
  public TronconHydrographiqueImpl(Reseau res, boolean estFictif, ICurve geom) {
    super(res, estFictif, geom);
  }

  public TronconHydrographiqueImpl() {
    super();
  }

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

  private boolean artificiel = false;

  @Override
  public boolean isArtificiel() {
    return this.artificiel;
  }

  @Override
  public void setArtificiel(boolean artificiel) {
    this.artificiel = artificiel;
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

  private Regime regime;

  @Override
  public Regime getRegime() {
    return this.regime;
  }

  @Override
  public void setRegime(Regime regime) {
    this.regime = regime;
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

  private double largeur = 0;

  @Override
  public double getLargeur() {
    return this.largeur;
  }

  @Override
  public void setLargeur(double largeur) {
    this.largeur = largeur;
  }

}
