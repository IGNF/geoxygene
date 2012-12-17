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
package fr.ign.cogit.geoxygene.schemageo.impl.ferre;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.TronconFerre;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;

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
