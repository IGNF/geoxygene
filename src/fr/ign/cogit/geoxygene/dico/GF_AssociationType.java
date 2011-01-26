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
 * métaclasse pour décrire les associations entre Feature Types. L'héritage de
 * GF_FeatureType permet à l'association de porter des propriétés.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GF_AssociationType extends GF_FeatureType {

  /** Les feature types impliqués dans cette association. */
  protected List<GF_FeatureType> linkBetween = new ArrayList<GF_FeatureType>();

  /** Renvoie les feature types impliqués dans cette association. */
  public List<GF_FeatureType> getLinkBetween() {
    return this.linkBetween;
  }

  /** Affecte une liste de feature types */
  public void setLinkBetween(List<GF_FeatureType> L) {
    this.linkBetween = L;
  }

  /** Renvoie le nombre de feature types impliqués dans cette association. */
  public int sizeLinkBetween() {
    return this.linkBetween.size();
  }

  /** Ajoute un feature type. Execute un "addMemberOf" sur GF_FeatureType. */
  public void addLinkBetween(GF_FeatureType value) {
    this.linkBetween.add(value);
    if (!value.getMemberOf().contains(this)) {
      value.addMemberOf(this);
    }
  }

  /** Les roles de cette association. */
  protected List<GF_AssociationRole> roles = new ArrayList<GF_AssociationRole>();

  /** Renvoie les roles de cette association. */
  public List<GF_AssociationRole> getRoles() {
    return this.roles;
  }

  /** Affecte une liste de roles */
  public void setRoles(List<GF_AssociationRole> L) {
    this.roles = L;
  }

  /** Renvoie le nombre de roles. */
  public int sizeRoles() {
    return this.roles.size();
  }

  /** Ajoute un role. */
  public void addRole(GF_AssociationRole Role) {
    this.roles.add(Role);
    if (Role.getAssociationType() != this) {
      Role.setAssociationType(this);
    }
  }

}
