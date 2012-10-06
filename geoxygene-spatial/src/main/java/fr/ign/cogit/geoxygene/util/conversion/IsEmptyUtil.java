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

package fr.ign.cogit.geoxygene.util.conversion;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class IsEmptyUtil {

  @SuppressWarnings("unchecked")
  public static boolean isEmpty(IGeometry geom) {
    if (geom == null) {
      return true;
    }
    if (geom instanceof IPoint) {
      return IsEmptyUtil.isEmpty((IPoint) geom);
    }
    if (geom instanceof IPolygon) {
      return IsEmptyUtil.isEmpty((IPolygon) geom);
    }
    if (geom instanceof ILineString) {
      return IsEmptyUtil.isEmpty((ILineString) geom);
    }
    if (geom instanceof IAggregate) {
      return IsEmptyUtil.isEmpty((IAggregate) geom);
    }
    return false;
  }

  public static boolean isEmpty(IPoint point) {
    IDirectPosition position = point.getPosition();
    double x = position.getX();
    double y = position.getY();
    double z = position.getZ();
    return (x == Double.NaN || y == Double.NaN || z == Double.NaN);
  }

  public static boolean isEmpty(IPolygon poly) {
    return poly.coord() == null || poly.coord().size() == 0;
  }

  public static boolean isEmpty(ILineString lineString) {
    return lineString.sizeControlPoint() == 0;
  }

  static boolean isEmpty(IAggregate<IGeometry> aggr) {
    for (IGeometry geom : aggr) {
      if (!IsEmptyUtil.isEmpty(geom)) {
        return false;
      }
    }
    return true;
  }
}
