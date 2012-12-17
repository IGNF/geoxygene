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
package fr.ign.cogit.geoxygene.schemageo.api.ferre;

import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

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
