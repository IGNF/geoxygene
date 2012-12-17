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

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Franchissement;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.PassePar;

/**
 * permet de modeliser les franchissements sans noeud (comme dans la BD carto)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class FranchissementImpl extends ElementDuReseauImpl implements
    Franchissement {

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  };

  /**
   * les objets passage
   */
  Collection<PassePar> passePar = new FT_FeatureCollection<PassePar>();

  @Override
  public Collection<PassePar> getPassePar() {
    return this.passePar;
  }

}
