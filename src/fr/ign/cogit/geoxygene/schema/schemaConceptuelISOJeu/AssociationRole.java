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

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;

/**
 * @author Sandrine Balley
 */
public class AssociationRole implements GF_AssociationRole {

  /**
	 * 
	 */
  public AssociationRole() {
    super();
  }

  public AssociationRole(FeatureType ft, AssociationType fa) {
    this.featureType = ft;
    this.associationType = fa;
  }

  public AssociationRole(
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole fc) {
    this.id = fc.getId();
    this.cardMax = fc.getCardMax();
    this.cardMin = fc.getCardMin();
    this.definition = fc.getDefinition();
    this.memberName = fc.getMemberName();
    this.nomFieldAsso = fc.getNomFieldAsso();
    this.nomFieldClasse = fc.getNomFieldClasse();
    this.isComponent = fc.getIsComponent();
    this.isComposite = fc.getIsComposite();
  }

  /** Identifiant d'un objet */
  protected int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /** nom du role */
  protected String memberName;

  public String getMemberName() {
    return this.memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  /** definition du role */
  protected String definition;

  public String getDefinition() {
    return this.definition;
  }

  public void setDefinition(String Definition) {
    this.definition = Definition;
  }

  /** lien vers le featureType concerné */
  protected FeatureType featureType;

  public GF_FeatureType getFeatureType() {
    return this.featureType;
  }

  public void setFeatureType(GF_FeatureType featureType) {
    this.featureType = (FeatureType) featureType;
    if (this.featureType != null) {
      if (!this.featureType.getRoles().contains(this)) {
        this.featureType.addRole(this);
      }
    }
  }

  /** lien vers le featureAssociation concerné */
  protected AssociationType associationType;

  public GF_AssociationType getAssociationType() {
    return this.associationType;
  }

  public void setAssociationType(GF_AssociationType associationType) {
    this.associationType = (AssociationType) associationType;
  }

  protected String cardMin;

  public String getCardMin() {
    return this.cardMin;
  }

  public void setCardMin(String CardMin) {
    this.cardMin = CardMin;
  }

  protected String cardMax;

  public String getCardMax() {
    return this.cardMax;
  }

  public void setCardMax(String CardMax) {
    this.cardMax = CardMax;
  }

  /**
   * boolean egal a 1 si le role a valeur de role "composant" dans une
   * agregation
   */
  protected boolean isComponent;

  public boolean getIsComponent() {
    return this.isComponent;
  }

  public void setIsComponent(boolean value) {
    this.isComponent = value;
  }

  /**
   * boolean egal a 1 si le role a valeur de role "composite" dans une
   * agregation
   */
  protected boolean isComposite;

  public boolean getIsComposite() {
    return this.isComposite;
  }

  public void setIsComposite(boolean value) {
    this.isComposite = value;
  }

  /** nom du champ Java representant le role dans la classe Java correspondante */
  protected String nomFieldClasse;

  public String getNomFieldClasse() {
    return this.nomFieldClasse;
  }

  public void setNomFieldClasse(String value) {
    this.nomFieldClasse = value;
  }

  /**
   * nom du champ Java representant le role dans l'eventuelle classe-association
   * Java correspondante
   */
  protected String nomFieldAsso;

  public String getNomFieldAsso() {
    return this.nomFieldAsso;
  }

  public void setNomFieldAsso(String value) {
    this.nomFieldAsso = value;
  }

  /**
   * Non standard Utils aux applications de transformation de schéma caractère
   * implicite ou explicite de l'élément : cf AssociationType.isExplicite
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

  /**
   * commodité pour retrouver à quel schéma appartient ce role (en passant par
   * le featureType auquel il se rapporte)
   * @return SchemaConceptuelJeu
   */
  public SchemaConceptuelJeu getSchemaConceptuel() {
    if (this.getFeatureType() != null) {
      return ((FeatureType) this.getFeatureType()).getSchema();
    }
    return null;
  }

  /*** methodes heritees de l'interface pas encore précisées ***/

  public List<GF_Constraint> getConstraint() {
    return null;
  }

  public void setConstraint(List<GF_Constraint> L) {
  }

  public void addConstraint(GF_Constraint value) {
  }

  public int sizeConstraint() {
    return 0;
  }

}
