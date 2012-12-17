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
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.impl.administratif;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.UniteAdministrative;

/**
 * 
 * chef lieu d'unite administrative
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class ChefLieuImpl extends DefaultFeature implements ChefLieu {

  /**
   * le nom de l'objet
   */
  private String nom = null;

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * l'uniteAdministrative de l'objet
   */
  private UniteAdministrative uniteAdministrative = null;

  @Override
  public UniteAdministrative getUniteAdministrative() {
    return this.uniteAdministrative;
  }

  @Override
  public void setUniteAdministrative(UniteAdministrative uniteAdministrative) {
    this.uniteAdministrative = uniteAdministrative;
  }

}
