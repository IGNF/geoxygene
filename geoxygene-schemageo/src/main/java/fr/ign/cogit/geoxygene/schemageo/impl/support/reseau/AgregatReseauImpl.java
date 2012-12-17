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
package fr.ign.cogit.geoxygene.schemageo.impl.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.AgregatReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.PassePar;

/**
 * agregat d'elements du reseau (ex: itineraire, riviere, echangeur, rond-point,
 * etc.)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class AgregatReseauImpl extends DefaultFeature implements AgregatReseau {

  /**
   * les noeuds de l'agregat
   */
  private Collection<NoeudReseau> noeuds = new FT_FeatureCollection<NoeudReseau>();

  @Override
  public Collection<NoeudReseau> getNoeuds() {
    return this.noeuds;
  }

  @Override
  public void setNoeuds(Collection<NoeudReseau> noeuds) {
    this.noeuds = noeuds;
  }

  /**
   * les arcs de l'agregat
   */
  private Collection<ArcReseau> arcs = new FT_FeatureCollection<ArcReseau>();

  @Override
  public Collection<ArcReseau> getArcs() {
    return this.arcs;
  }

  @Override
  public void setArcs(Collection<ArcReseau> arcs) {
    this.arcs = arcs;
  }

  /**
   * les franchissements de l'agregat
   */
  private Collection<Franchissement> franchissements = new FT_FeatureCollection<Franchissement>();

  @Override
  public Collection<Franchissement> getFranchissements() {
    return this.franchissements;
  }

  @Override
  public void setFranchissements(Collection<Franchissement> franchissements) {
    this.franchissements = franchissements;
  }

  /**
   * les passages de l'agregat
   */
  private Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  @Override
  public Collection<PassePar> getPassepar() {
    return this.passePar;
  }

  @Override
  public void setPassePar(Collection<PassePar> passePar) {
    this.passePar = passePar;
  }

}
