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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.Regime;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.SurfaceDEau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ElementZonalReseauImpl;

/**
 * lacs, etendues d'eau diverses representees sous forme surfacique
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class SurfaceDEauImpl extends ElementZonalReseauImpl implements
    SurfaceDEau {

  public SurfaceDEauImpl() {
    super();
  }

  public SurfaceDEauImpl(Reseau res, IPolygon geom) {
    this();
    this.setReseau(res);
    this.setGeom(geom);
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

  /**
   * l'altitude moyenne de l'objet
   */
  private double zMoy;

  @Override
  public double getZMoy() {
    return this.zMoy;
  }

  @Override
  public void setZMoy(double zMoy) {
    this.zMoy = zMoy;
  }

  /**
   * le regime
   */
  private Regime regime;

  @Override
  public Regime getRegime() {
    return this.regime;
  }

  @Override
  public void setRegime(Regime regime) {
    this.regime = regime;
  }

}
