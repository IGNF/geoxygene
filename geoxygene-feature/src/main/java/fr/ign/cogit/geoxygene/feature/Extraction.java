/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.feature;

import fr.ign.cogit.geoxygene.api.feature.IExtraction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * Zone d'extraction pour pouvoir lancer des traitement sur une partie seulement
 * d'un jeu de données.
 * 
 * @author Sébastien Mustière
 * 
 */
public class Extraction implements IExtraction {

  /** Identifiant de la zone d'extraction */
  protected int id;

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int Id) {
    this.id = Id;
  }

  /** géometrie définissant la zone d'extraction */
  protected IPolygon geom = null;

  @Override
  public IPolygon getGeom() {
    return this.geom;
  }

  @Override
  public void setGeom(IPolygon g) {
    this.geom = g;
  }

  /** Nom de la zone d'extraction */
  protected String nom;

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String S) {
    this.nom = S;
  }

  /**
   * DataSet auquel appartient la zone d'extraction. Utile uniquement pour OJB:
   * ne pas utiliser directement
   */
  private int dataSetID;

  @Override
  public void setDataSetID(int I) {
    this.dataSetID = I;
  }

  @Override
  public int getDataSetID() {
    return this.dataSetID;
  }

  /**
   * renvoie une extension avec une géométrie nulle et un nom par défaut:
   * "Zone complète"
   */
  public static Extraction zoneComplete() {
    Extraction ex = new Extraction();
    ex.setNom("Zone complète"); //$NON-NLS-1$
    return ex;
  }
}
