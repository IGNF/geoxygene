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

package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;

/**
 * @author Sandrine Balley
 */
public class FeatureAttributeValue implements FC_FeatureAttributeValue {

  /**
   * Valeur d'attribut s'appliquant à un attribut de type énuméré
   */
  public FeatureAttributeValue() {
    super();
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * Non standard Utile aux applications de transformation de schéma caractère
   * implicite ou explicite de l'élément : un featureAttributeValue implicite
   * n'est jamais affecté à priori mais il peut l'être par le biais de calculs à
   * partir d'éléments explicites
   **/
  protected boolean isExplicite;

  /** Renvoie le caractere explicite ou implicite */
  public boolean getIsExplicite() {
    return this.isExplicite;
  }

  /** Affecte le caractère implicite ou explicite */
  public void setIsExplicite(boolean value) {
    this.isExplicite = value;
  }

  // ///////////////////////////////////////////////////////////////////
  // Modifications Nathalie
  // ///////////////////////////////////////////////////////////////////
  /** attribut auquel s'applique cette valeur **/
  protected AttributeType featureAttribute;

  /** Renvoie le feature attribute auquel s'applique cette valeur. */
  public GF_AttributeType getFeatureAttribute() {
    return this.featureAttribute;
  }

  /** Affecte un feature attribute auquel s'applique cette valeur. */
  public void setFeatureAttribute(GF_AttributeType FeatureAttribute) {
    AttributeType old = this.featureAttribute;
    this.featureAttribute = (AttributeType) FeatureAttribute;
    if (old != null) {
      old.getValuesDomain().remove(this);
    }
    if (FeatureAttribute != null) {
      if (!FeatureAttribute.getValuesDomain().contains(this)) {
        FeatureAttribute.getValuesDomain().add(this);
      }
    }
  }

  // /////////////////////////////////////////////////////////////////////

  /** label pour une valeur d'attribut **/
  protected String label;

  /** Renvoie la valeur d'attribut */
  public String getLabel() {
    return this.label;
  }

  /** Affecte la valeur d'attribut */
  public void setLabel(String Label) {
    this.label = Label;
  }

  /** code pour une valeur d'attribut **/
  protected int code;

  /** Renvoie la valeur d'attribut */
  public int getcode() {
    return this.code;
  }

  /** Affecte la valeur d'attribut */
  public void setCode(int value) {
    this.code = value;
  }

  /** definition de la valeur. */
  protected String definition;

  /** Renvoie la définition. */
  public String getDefinition() {
    return this.definition;
  }

  /** Affecte une définition. */
  public void setDefinition(String Definition) {
    this.definition = Definition;
  }

  /**
   * la définition semantique du featureType (sous la forme d'un String ou d'une
   * classe d'ontologie) ou un pointeur vers cette définition (sous la forme
   * d'une URI)
   */
  protected Object definitionReference;

  /**
   * @return the definitionReference
   */
  public Object getDefinitionReference() {
    return this.definitionReference;
  }

  /**
   * @param definitionReference the definitionReference to set
   */
  public void setDefinitionReference(Object definitionReference) {
    this.definitionReference = definitionReference;
  }

}
