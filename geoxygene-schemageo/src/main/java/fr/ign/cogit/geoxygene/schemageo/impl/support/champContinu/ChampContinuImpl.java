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
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ElementCaracteristique;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.Isoligne;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.PointCote;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "champ_continu")
public class ChampContinuImpl implements ChampContinu {

  /**
   * l'id de l'objet
   */
  private int id = 0;

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * le nom de l'objet
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
   * les elements caracteristiques de l'objet
   */
  @Transient
  private Collection<ElementCaracteristique> elementsCaracteristiques = new FT_FeatureCollection<ElementCaracteristique>();

  // @Override
  @Override
  @Transient
  public Collection<ElementCaracteristique> getElementsCaracteristiques() {
    return this.elementsCaracteristiques;
  }

  @Override
  public void setElementsCaracteristiques(
      Collection<ElementCaracteristique> elementsCaracteristiques) {
    this.elementsCaracteristiques = elementsCaracteristiques;
  }

  /**
   * les isolignes de l'objet
   */
  private Collection<Isoligne> isolignes = new FT_FeatureCollection<Isoligne>();

  @Override
  @OneToMany(mappedBy = "champContinu", targetEntity = IsoligneImpl.class)
  public Collection<Isoligne> getIsolignes() {
    return this.isolignes;
  }

  @Override
  public void setIsolignes(Collection<Isoligne> isolignes) {
    this.isolignes = isolignes;
  }

  /**
   * les points cotes de l'objet
   */
  private Collection<PointCote> pointsCotes = new FT_FeatureCollection<PointCote>();

  @Override
  public Collection<PointCote> getPointsCotes() {
    return this.pointsCotes;
  }

  @Override
  public void setPointsCotes(Collection<PointCote> pointsCotes) {
    this.pointsCotes = pointsCotes;
  }

}
