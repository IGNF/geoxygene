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

package fr.ign.cogit.geoxygene.feature;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Classe mère pour toute classe d'éléments ayant une réalité Géographique. Par
 * défaut, porte une géométrie et une topologie, qui peuvent être nulles.
 * 
 * <P>
 * TODO : ne plus porter de geometrie ni de topologie par defaut, et permettre
 * le choix du nom de l'attribut portant geometrie et topologie.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Sandrine Balley
 * @author Nathalie Abadie
 * @author Julien Perret
 */
public abstract class FT_Feature extends AbstractFeature {

  /**
   * Constructeur par défaut.
   */
  public FT_Feature() {
    super();
  }

  /**
   * Contructeur à partir d'une géométrie.
   * @param geom géométrie du feature
   */
  public FT_Feature(IGeometry geom) {
    super();
    this.geom = geom;
  }

  /**
   * Constructeur par copie
   * @param feature
   */
  public FT_Feature(IFeature feature) {
    super(feature);
    this.geom = feature.getGeom();
  }

  @Override
  public AbstractFeature cloneGeom() throws CloneNotSupportedException {

    FT_Feature result = (FT_Feature) this.clone();
    result.setGeom((IGeometry) this.getGeom().clone());
    return result;

  }

}
