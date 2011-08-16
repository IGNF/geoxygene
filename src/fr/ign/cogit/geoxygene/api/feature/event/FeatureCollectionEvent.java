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

package fr.ign.cogit.geoxygene.api.feature.event;

import java.util.EventObject;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Events on feature collections.
 * @author Julien Perret
 * 
 */
public class FeatureCollectionEvent extends EventObject {
  /**
   * Serial uid.
   */
  private static final long serialVersionUID = 6977794480279289305L;

  /**
   * Type of Event.
   * @author Julien Perret
   */
  public enum Type {
    ADDED, CHANGED, REMOVED
  }

  /**
   * Type of Event.
   */
  private Type type;

  /**
   * @return the type of event
   */
  public final Type getType() {
    return this.type;
  }

  /**
   * Feature on which the event happens.
   */
  private IFeature feature;

  /**
   * @return the feature on which the event happens
   */
  public final IFeature getFeature() {
    return this.feature;
  }

  /**
   * Geometry of the feature before the event.
   */
  private IGeometry geometry;

  /**
   * @return the Geometry of the feature before the event
   */
  public final IGeometry getGeometry() {
    return this.geometry;
  }

  /**
   * @param source Source of the event
   * @param feature Feature on which the event happens
   * @param type Type of event
   * @param geometry Geometry of the feature before the event
   */
  public FeatureCollectionEvent(Object source, IFeature feature, Type type,
      IGeometry geometry) {
    super(source);
    this.feature = feature;
    this.type = type;
    this.geometry = geometry;
  }
}
