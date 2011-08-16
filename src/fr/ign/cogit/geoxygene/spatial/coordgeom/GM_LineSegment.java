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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc2;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Segment de droite.
 * 
 * @author Thierry Badard & Arnaud Braun & Julien Gaffuri
 * @version 1.0
 * 
 */
public class GM_LineSegment extends GM_LineString implements ILineSegment {
  // private static Logger logger =
  // Logger.getLogger(GM_LineSegment.class.getName());

  private IDirectPosition startPoint;

  public IDirectPosition getStartPoint() {
    return this.startPoint;
  };

  private IDirectPosition endPoint;

  public IDirectPosition getEndPoint() {
    return this.endPoint;
  };

  public GM_LineSegment(IDirectPosition startPoint, IDirectPosition endPoint) {
    super();
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.coord().add(startPoint);
    this.coord().add(endPoint);
  }

  /**
   * Constructeur. Passer des DirectPosition au lieu de GM_Position.
   * @param points
   */
  public GM_LineSegment(IDirectPositionList points) {
    super(points);
    if (points.size() != 2) {
      System.out
          .println("Impossible to create GM_LineSegment: bad number of points: "
              + points.size());
    }
    this.startPoint = points.get(0);
    this.endPoint = points.get(1);
  }

  @Override
  public IGeometry intersection(IGeometry geom) {
    if (geom instanceof IArc2) {
      return ((IArc2) geom).intersection(this);
    }
    if (geom instanceof IArc) {
      return ((IArc) geom).intersection(this);
    }
    return super.intersection(geom);
  }
}
