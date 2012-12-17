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
package fr.ign.cogit.geoxygene.schemageo.api.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Zone extends IFeature {

  /**
   * @return la partition a laquelle la zone appartient
   */
  public Partition getPartition();

  /**
   * @param partition la partition a laquelle la zone appartient
   */
  public void setPartition(Partition partition);

  /**
   * @return les limites sur lesquelles s'appuie la zone
   */
  public Collection<Limite> getLimites();

  /**
   * @param limites les limites sur lesquelles s'appuie la zone
   */
  public void setLimites(Collection<Limite> limites);

  /**
   * @return les zones composees auxquelles la zone appartient eventuellement
   */
  public Collection<ZoneComposite> getZonesComposees();

  /**
   * @param zonesComposees les zones composees aquelles la zone appartient
   *          eventuellement
   */
  public void setZonesComposees(Collection<ZoneComposite> zonesComposees);

  @Override
  public IMultiSurface<?> getGeom();

}
