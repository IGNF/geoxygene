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

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ElementPonctuelReseauImpl;

/**
 * point d'eau, reservoir (?), point d'eau isole, toponyme de surface hydro (?)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class PointDEauImpl extends ElementPonctuelReseauImpl implements
    PointDEau {

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

  /**
   * le type
   */
  private FeatureType type;

  public FeatureType getType() {
    return this.type;
  }

  public void setType(FeatureType type) {
    this.type = type;
  }

  /**
   * l'altitude maximale de l'objet
   */
  private double zMax;

  @Override
  public double getZMax() {
    return this.zMax;
  }

  @Override
  public void setZMax(double zMax) {
    this.zMax = zMax;
  }

  /**
   * l'altitude minimale de l'objet
   */
  private double zMin;

  @Override
  public double getZMin() {
    return this.zMin;
  }

  @Override
  public void setZMin(double zMin) {
    this.zMin = zMin;
  }

  /**
   * la hauteur de l'objet
   */
  private double hauteur;

  @Override
  public double getHauteur() {
    return this.hauteur;
  }

  @Override
  public void setHauteur(double hauteur) {
    this.hauteur = hauteur;
  }
}
