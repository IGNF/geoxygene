/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.impl.hydro;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.Regime;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;

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
  private String nom = ""; //$NON-NLS-1$

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
