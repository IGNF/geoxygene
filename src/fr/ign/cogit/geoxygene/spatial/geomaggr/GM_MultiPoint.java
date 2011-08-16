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

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * Agrégation de points. Il n'y a aucune structure interne. La liste "element"
 * est une liste de GM_Point.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_MultiPoint extends GM_MultiPrimitive<IPoint> implements
    IMultiPoint {

  // ///////////////////////////////////////////////////////////////////////////////////////
  // constructeurs
  // ////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////

  /** Constructeur par défaut. */
  public GM_MultiPoint() {
    this.element = new ArrayList<IPoint>();
  }

  /** Constructeur à partir d'un GM_Point. */
  public GM_MultiPoint(IPoint aGM_Point) {
    this.element = new ArrayList<IPoint>();
    this.add(aGM_Point);
  }

  /** Constructeur à partir d'une liste de GM_Point. */
  public GM_MultiPoint(List<IPoint> fromGM_Point) {
    this.element = new ArrayList<IPoint>();
    for (IPoint point : fromGM_Point) {
      this.add(point);
    }
  }

  /** Constructeur à partir d'une liste de DirectPosition. */
  public GM_MultiPoint(IDirectPositionList L) {
    this.element = new ArrayList<IPoint>();
    for (IDirectPosition position : L) {
      this.add(position.toGM_Point());
    }
  }
  
  @Override
  public Object clone() {
    GM_MultiPoint agg = new GM_MultiPoint();
    for (IPoint elt : this.element) {
      agg.add((IPoint) elt.clone());
    }
    return agg;
  }
  
}
