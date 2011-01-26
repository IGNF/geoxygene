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

package fr.ign.cogit.geoxygene.dico;

import java.util.ArrayList;
import java.util.List;

/**
 * métaclasse instanciée par les classes correspondant aux thèmes Géographiques.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GF_FeatureType {

  /** Identifiant. */
  protected int id;

  /** Renvoie l'identifiant. */
  public int getId() {
    return this.id;
  }

  /** Affecte un identifiant. */
  public void setId(int Id) {
    this.id = Id;
  }

  /** Nom de la classe Géographique. Doit être unique dans un schéma applicatif. */
  protected String typeName;

  /** Renvoie le nom de la classe Géographique. */
  public String getTypeName() {
    return this.typeName;
  }

  /** Affecte un nom. */
  public void setTypeName(String TypeName) {
    this.typeName = TypeName;
  }

  /** Description de la classe Géographique. */
  protected String definition;

  /** Renvoie la description de la classe geographique. */
  public String getDefinition() {
    return this.definition;
  }

  /** Affecte une définition. */
  public void setDefinition(String Definition) {
    this.definition = Definition;
  }

  /** TRUE si la classe est abstraite. Vaut FALSE par defaut. */
  protected boolean isAbstract = false;

  /** Renvoie l'attribut isAbtract. */
  public boolean getIsAbstract() {
    return this.isAbstract;
  }

  /** Affecte l'attribut isAbstract. */
  public void setIsAbstract(boolean IsAbstract) {
    this.isAbstract = IsAbstract;
  }

  /** Les relations de généralisation dans lesquelles est impliquée la classe. */
  protected List<GF_InheritanceRelation> generalization = new ArrayList<GF_InheritanceRelation>();

  /**
   * Renvoie les relations de généralisation dans lesquelles est impliquée la
   * classe.
   */
  public List<GF_InheritanceRelation> getGeneralization() {
    return this.generalization;
  }

  /** Affecte une liste de generalisations */
  public void setGeneralization(List<GF_InheritanceRelation> L) {
    this.generalization = L;
  }

  /**
   * Renvoie le nombre de relation de généralisation dans lesquelles est
   * impliquée la classe.
   */
  public int sizeGeneralization() {
    return this.generalization.size();
  }

  /**
   * Ajoute une relation de généralisation. Affecte automatiquement le sous-type
   * de cette relation.
   */
  public void addGeneralization(GF_InheritanceRelation value) {
    this.generalization.add(value);
    if (value.getSubType() != this) {
      value.setSubType(this); // gestion de la bi-direction
    }
  }

  /** Les relations de spécialisation dans lesquelles est impliquée la classe. */
  protected List<GF_InheritanceRelation> specialization = new ArrayList<GF_InheritanceRelation>();

  /**
   * Renvoie la liste des relations de spécialisation dans lesquelles est
   * impliquée la classe.
   */
  public List<GF_InheritanceRelation> getSpecialization() {
    return this.specialization;
  }

  /** Affecte une liste de specialisations */
  public void setSpecialization(List<GF_InheritanceRelation> L) {
    this.specialization = L;
  }

  /**
   * Renvoie le nombre de relation de spécialisation dans lesquelles est
   * impliquée la classe.
   */
  public int sizeSpecialization() {
    return this.specialization.size();
  }

  /**
   * Ajoute une relation de spécialisation. Affecte automatiquement le
   * super-type de cette relation.
   */
  public void addSpecialization(GF_InheritanceRelation value) {
    this.specialization.add(value);
    if (!value.getSuperType().contains(this)) {
      value.addSuperType(this); // gestion de la bi-direction
    }
  }

  /** Les propriétés (attributs, opérations, roles). */
  protected List<GF_PropertyType> properties = new ArrayList<GF_PropertyType>();

  /** Renvoie la liste des propriétés. */
  public List<GF_PropertyType> getProperties() {
    return this.properties;
  }

  /** Affecte une liste de proprietes */
  public void setProperties(List<GF_PropertyType> L) {
    this.properties = L;
  }

  /** Renvoie le nombre de propriétés. */
  public int sizeProperties() {
    return this.properties.size();
  }

  /**
   * Ajoute une propriété. Affecte automatiquement le feature type de cette
   * propriété.
   */
  public void addProperty(GF_PropertyType value) {
    this.properties.add(value);
    if (value.getFeatureType() != this) {
      value.setFeatureType(this);
    }
  }

  /** Les associations dans lesquelles est impliquée cette classe. */
  protected List<GF_AssociationType> memberOf = new ArrayList<GF_AssociationType>();

  /** Renvoie les associations dans lesquelles est impliquée cette classe. */
  public List<GF_AssociationType> getMemberOf() {
    return this.memberOf;
  }

  /** Affecte une liste d'associations */
  public void setMemberOf(List<GF_AssociationType> L) {
    this.memberOf = L;
  }

  /** Le nombre d'associations dans lesquelles est impliquée cette classe. */
  public int sizeMemberOf() {
    return this.memberOf.size();
  }

  /** Ajoute une association. */
  public void addMemberOf(GF_AssociationType value) {
    this.memberOf.add(value);
    if (!value.getLinkBetween().contains(this)) {
      value.addLinkBetween(this);
    }
  }

  /** Les contraintes. */
  protected List<GF_Constraint> constraint = new ArrayList<GF_Constraint>();

  /** Renvoie la liste des contraintes. */
  public List<GF_Constraint> getConstraint() {
    return this.constraint;
  }

  /** Affecte une liste de contraintes */
  public void setConstraint(List<GF_Constraint> L) {
    this.constraint = L;
  }

  /** Renvoie le nombre de contraintes. */
  public int sizeConstraint() {
    return this.constraint.size();
  }

  /** Ajoute une contrainte. */
  public void addConstraint(GF_Constraint value) {
    this.constraint.add(value);
  }

}
