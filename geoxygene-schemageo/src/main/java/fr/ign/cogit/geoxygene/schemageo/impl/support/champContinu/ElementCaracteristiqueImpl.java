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

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.ElementCaracteristique;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ElementCaracteristiqueImpl extends DefaultFeature implements
    ElementCaracteristique {

  /**
   * le constructeur par defaut
   * 
   * @param champContinu
   * @param geom
   */
  public ElementCaracteristiqueImpl(ChampContinu champContinu, IGeometry geom) {
    this.setChampContinu(champContinu);
    this.setGeom(geom);
  }

  /**
   * le champ continu auquel l'objet appartient
   */
  private ChampContinu champContinu = null;

  @Override
  public ChampContinu getChampContinu() {
    return this.champContinu;
  }

  @Override
  public void setChampContinu(ChampContinu champContinu) {
    this.champContinu = champContinu;
  }

}
