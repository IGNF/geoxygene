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

/**
 * métaclasse pour la définition des attributs des Feature Types. Dans la norme,
 * il existe des sous-classes pour distinguer les attributs temporels,
 * thématiques, spatiaux, sur la qualité, etc.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GF_AttributeType extends GF_PropertyType {

  /** Type de l'attribut. */
  protected String valueType;

  /** Renvoie le type de l'attribut. */
  public String getValueType() {
    return this.valueType;
  }

  /** Affecte un type à l'attribut. */
  public void setValueType(String ValueType) {
    this.valueType = ValueType;
  }

  /** Definition du domaine de valeur. A COMPLETER POUR LES ENUMERES. */
  protected String domainOfValues;

  /** Renvoie le domaine de valeur. */
  public String getDomainOfValues() {
    return this.domainOfValues;
  }

  /** Affecte un domaine de valeur. */
  public void setDomainOfValues(String DomainOfValues) {
    this.domainOfValues = DomainOfValues;
  }

  /** Nombre de valeurs minimal de l'attribut */
  protected int cardMin;

  /** Renvoie le nombre de valeurs minimal de l'attribut. */
  public int getCardMin() {
    return this.cardMin;
  }

  /** Affecte un nombre de valeurs minimal à l'attribut. */
  public void setCardMin(int CardMin) {
    this.cardMin = CardMin;
  }

  /** Nombre de valeurs maximal de l'attribut */
  protected int cardMax;

  /** Renvoie le nombre de valeurs maximal de l'attribut. */
  public int getCardMax() {
    return this.cardMax;
  }

  /** Affecte un nombre de valeurs maximal à l'attribut. */
  public void setCardMax(int CardMax) {
    this.cardMax = CardMax;
  }

  /**
   * Attribut en caractérisant un autre. La relation inverse n'est pas
   * implémentée.
   */
  protected GF_AttributeType characterize;

  /** Renvoie l'attribut que self caractérise. */
  public GF_AttributeType getCharacterize() {
    return this.characterize;
  }

  /** Affecte un attribut que self caractérise. */
  public void setCharacterize(GF_AttributeType Characterize) {
    this.characterize = Characterize;
  }

}
