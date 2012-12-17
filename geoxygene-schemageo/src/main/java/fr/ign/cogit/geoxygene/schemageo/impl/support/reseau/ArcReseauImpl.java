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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.PassePar;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
@Entity
@Table(name = "arc_reseau")
public class ArcReseauImpl extends ElementDuReseauImpl implements ArcReseau {

  /**
   * constructeur par defaut
   * @param res
   * @param fictif
   */
  public ArcReseauImpl(Reseau res, boolean fictif, ICurve geom) {
    this();
    this.setReseau(res);
    this.setFictif(fictif);
    this.setGeom(geom);
  }

  public ArcReseauImpl() {
    super();
  }

  @Override
  @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
  public ICurve getGeom() {
    return (ICurve) super.getGeom();
  }

  /**
   * indique si l'arc est fictif ou non
   */
  private boolean fictif = false;

  @Override
  public boolean isFictif() {
    return this.fictif;
  }

  @Override
  public void setFictif(boolean fictif) {
    this.fictif = fictif;
  }

  /**
   * donne la direction de l'arc
   */
  private Direction direction = Direction.INCONNU;

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * le noeud final de l'arc
   */
  private NoeudReseau noeudFinal = null;

  @Override
  public NoeudReseau getNoeudFinal() {
    return this.noeudFinal;
  }

  @Override
  public void setNoeudFinal(NoeudReseau noeudFinal) {
    this.noeudFinal = noeudFinal;
  }

  /**
   * le noeud initial de l'arc
   */
  private NoeudReseau noeudInitial = null;

  @Override
  public NoeudReseau getNoeudInitial() {
    return this.noeudInitial;
  }

  @Override
  public void setNoeudInitial(NoeudReseau noeudInitial) {
    this.noeudInitial = noeudInitial;
  }

  /**
	 * 
	 */
  private Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  @Override
  public Collection<PassePar> getPassePar() {
    return this.passePar;
  }

  @Override
  @ManyToOne(targetEntity = ReseauImpl.class)
  public Reseau getReseau() {
    return super.getReseau();
  }
}
