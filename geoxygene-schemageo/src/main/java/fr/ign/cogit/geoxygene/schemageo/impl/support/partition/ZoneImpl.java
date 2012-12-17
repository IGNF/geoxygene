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
package fr.ign.cogit.geoxygene.schemageo.impl.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schemageo.api.support.partition.Limite;
import fr.ign.cogit.geoxygene.schemageo.api.support.partition.Partition;
import fr.ign.cogit.geoxygene.schemageo.api.support.partition.Zone;
import fr.ign.cogit.geoxygene.schemageo.api.support.partition.ZoneComposite;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public abstract class ZoneImpl extends DefaultFeature implements Zone {

  /**
   * la partition a laquelle l'objet appartient
   */
  private Partition partition = null;

  @Override
  public Partition getPartition() {
    return this.partition;
  }

  @Override
  public void setPartition(Partition partition) {
    this.partition = partition;
  }

  /**
   * les limites de la zone
   */
  Collection<Limite> limites = new FT_FeatureCollection<Limite>();

  @Override
  public Collection<Limite> getLimites() {
    return this.limites;
  }

  @Override
  public void setLimites(Collection<Limite> limites) {
    this.limites = limites;
  }

  /**
   * les zones composees auxquelles la zone appartient eventuellement
   */
  private Collection<ZoneComposite> zonesComposees = new FT_FeatureCollection<ZoneComposite>();

  @Override
  public Collection<ZoneComposite> getZonesComposees() {
    return this.zonesComposees;
  }

  @Override
  public void setZonesComposees(Collection<ZoneComposite> zonesComposees) {
    this.zonesComposees = zonesComposees;
  }

  @Override
  public IMultiSurface<?> getGeom() {
    return (IMultiSurface<?>) super.getGeom();
  };

}
