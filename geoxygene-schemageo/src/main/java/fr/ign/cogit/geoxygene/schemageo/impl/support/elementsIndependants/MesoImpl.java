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

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.Meso;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class MesoImpl extends ElementIndependantImpl implements Meso {

  /**
   * constructeur par defaut
   * 
   * @param geom
   */
  public MesoImpl(IGeometry geom) {
    this(geom, new FT_FeatureCollection<ElementIndependant>());
  }

  /**
   * @param geom
   * @param composants
   */
  public MesoImpl(IGeometry geom, Collection<ElementIndependant> composants) {
    this.setGeom(geom);
    this.setComposants(composants);
  }

  /**
   * les composants de l'objet
   */
  private Collection<ElementIndependant> composants = null;

  @Override
  public Collection<ElementIndependant> getComposants() {
    return this.composants;
  }

  @Override
  public void setComposants(Collection<ElementIndependant> composants) {
    this.composants = composants;
  }

}
