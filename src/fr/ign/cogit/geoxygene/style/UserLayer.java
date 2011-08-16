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
 */

package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "UserLayer")
public class UserLayer extends AbstractLayer {

  /*
   * TODO revoir cette implémentation des userLayers...
   */
  @XmlTransient
  FT_FeatureCollection<? extends FT_Feature> features = null;

  /**
   * Affecte la valeur de l'attribut features.
   * @param features l'attribut features à affecter
   */
  public void setFeatures(FT_FeatureCollection<? extends FT_Feature> features) {
    this.features = features;
  }

  @Override
  public FT_FeatureCollection<? extends FT_Feature> getFeatureCollection() {
    return this.features;
  }

  public UserLayer() {
    this.setFeatures(new FT_FeatureCollection<FT_Feature>());
  }

  public UserLayer(Layer layer) {
    this.setName(layer.getName());
    this.setFeatures(new FT_FeatureCollection<FT_Feature>());
    this.setStyles(layer.getStyles());
  }
}
