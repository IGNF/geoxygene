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
package fr.ign.cogit.geoxygene.schemageo.impl.support.elementsIndependants;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.Meso;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public abstract class ElementIndependantImpl extends DefaultFeature implements
    ElementIndependant {

  /**
   * le meso (eventuel) dont l'objet est composant
   */
  private Meso meso = null;

  @Override
  public Meso getMeso() {
    return this.meso;
  }

  @Override
  public void setMeso(Meso meso) {
    this.meso = meso;
  }

}
