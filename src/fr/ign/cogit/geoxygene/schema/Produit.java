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
 */

package fr.ign.cogit.geoxygene.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

/**
 * Un produit qui a un producteur, des métadonnées minimales et notamment un
 * schéma conceptuel (de type SchemaISOProduit) D'autres métadonnées sont à
 * venir, notamment des MD ISO 19115 et des spécifications Cogit complètes
 * @author Sandrine Balley
 */

public class Produit {

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  protected String nom;

  /**
   * @return the nom
   */
  public String getNom() {
    return this.nom;
  }

  /**
   * @param nom the nom to set
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * 1 = BD 2 = série de cartes
   */
  protected int type;

  /**
   * @return the type
   */
  public int getType() {
    return this.type;
  }

  /**
   * @param type the type to set 1 = BD 2 = série de cartes
   */
  public void setType(int type) {
    this.type = type;
  }

  protected String producteur;

  /**
   * @return the producteur
   */
  public String getProducteur() {
    return this.producteur;
  }

  /**
   * @param producteur the producteur to set
   */
  public void setProducteur(String producteur) {
    this.producteur = producteur;
  }

  protected double echelleMin;

  /**
   * @return the echelleMin
   */
  public double getEchelleMin() {
    return this.echelleMin;
  }

  /**
   * @param echelleMin the echelleMin to set
   */
  public void setEchelleMin(double echelleMin) {
    this.echelleMin = echelleMin;
  }

  protected double echelleMax;

  /**
   * @return the echelleMax
   */
  public double getEchelleMax() {
    return this.echelleMax;
  }

  /**
   * @param echelleMax the echelleMax to set
   */
  public void setEchelleMax(double echelleMax) {
    this.echelleMax = echelleMax;
  }

  protected SchemaConceptuelProduit schemaConceptuel;

  /**
   * @return the schemaConceptuel
   */

  public SchemaConceptuelProduit getSchemaConceptuel() {
    return this.schemaConceptuel;
  }

  /**
   * @param schemaConceptuel the schemaConceptuel to set
   */

  public void setSchemaConceptuel(SchemaConceptuelProduit schemaConceptuel) {
    this.schemaConceptuel = schemaConceptuel;
  }

  /**
   * la liste des jeux de données dont on dispose pour ce produit
   */
  protected List<IDataSet> jeuxDisponibles;

  /**
   * @return the jeuxDisponibles
   */
  public List<IDataSet> getJeuxDisponibles() {
    return this.jeuxDisponibles;
  }

  /**
   * @param jeuxDisponibles the jeuxDisponibles to set
   */
  public void setJeuxDisponibles(List<IDataSet> jeuxDisponibles) {
    this.jeuxDisponibles = jeuxDisponibles;
  }

}
