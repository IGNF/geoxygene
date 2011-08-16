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

package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu;

import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;

/**
 * @author Sandrine Balley
 */
public class InheritanceRelation implements GF_InheritanceRelation {

  public InheritanceRelation() {
    super();
  }

  public InheritanceRelation(
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.InheritanceRelation ori) {
    this.id = ori.getId();
    this.description = ori.getDescription();
    this.name = ori.getName();
    this.uniqueInstance = ori.getUniqueInstance();
    this.boolExtends = ori.getBoolExtends();
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /** Nom de la généralisation ou de la spécialisation. */
  protected String name;

  /** Renvoie le nom. */
  public String getName() {
    return this.name;
  }

  /** Affecte un nom. */
  public void setName(String Name) {
    this.name = Name;
  }

  /** Classe mère de la relation d'heritage. */
  protected FeatureType superType;

  /** Renvoie la classe mère de la relation d'héritage. */
  public GF_FeatureType getSuperType() {
    return this.superType;
  }

  /** Affecte une classe mère à la relation d'héritage. */
  public void setSuperType(GF_FeatureType SuperType) {
    FeatureType old = this.superType;
    this.superType = (FeatureType) SuperType;
    if (old != null) {
      old.getSpecialization().remove(this);
    }
    if (SuperType != null) {
      if (!SuperType.getSpecialization().contains(this)) {
        SuperType.addSpecialization(this);
      }
    }
  }

  /** Classe fille de la relation d'heritage. */
  protected FeatureType subType;

  /** Renvoie la classe fille de la relation d'héritage. */
  public GF_FeatureType getSubType() {
    return this.subType;
  }

  /** Affecte une classe fille à la relation d'héritage. */
  public void setSubType(GF_FeatureType SubType) {
    FeatureType old = this.subType;
    this.subType = (FeatureType) SubType;
    if (old != null) {
      old.getGeneralization().remove(this);
    }
    if (SubType != null) {
      if (!SubType.getGeneralization().contains(this)) {
        SubType.addGeneralization(this);
      }
    }
  }

  /** Description. */
  protected String description;

  /** Renvoie la description. */
  public String getDescription() {
    return this.description;
  }

  /** Affecte une description. */
  public void setDescription(String Description) {
    this.description = Description;
  }

  /**
   * TRUE si une instance de l'hyperclasse doit être au plus dans une
   * sous-classe, FALSE sinon.
   */
  protected boolean uniqueInstance;

  /** Renvoie l'attribut uniqueInstance. */
  public boolean getUniqueInstance() {
    return this.uniqueInstance;
  }

  /** Affecte l'attribut uniqueInstance.. */
  public void setUniqueInstance(boolean UniqueInstance) {
    this.uniqueInstance = UniqueInstance;
  }

  /**
   * booleen indiquant si l'heritage est represente par un "extends" en Java(1)
   * ou s'il n'est pas explicitement represente(0)
   */
  protected boolean boolExtends;

  public boolean getBoolExtends() {
    return this.boolExtends;
  }

  public void setBoolExtends(boolean value) {
    this.boolExtends = value;
  }

  /**
   * commodité pour retrouver à quel schéma conceptuel appartient cette relation
   * (en passant par les featureType qu'elle relie). S'il y a incohérence entre
   * les membres de l'héritage, la méthode renvoie null.
   *
   */
  public SchemaConceptuelJeu getSchemaConceptuel() {

    SchemaConceptuelJeu schema = null;
    if ((this.getSubType() != null) & (this.getSuperType() != null)) {
      if (((FeatureType) this.getSubType()).getSchema().equals(
          ((FeatureType) this.getSuperType()).getSchema())) {
        schema = ((FeatureType) this.getSubType()).getSchema();
      }
    }
    return schema;
  }

}
