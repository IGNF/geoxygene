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

package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author juju
 * 
 */
public class LineDensification {

  /**
   * Puts x vertices in the line where x is the length divided by the step
   * parameter. Be careful, this method does not preserve the initial vertices!
   * @param ls
   * @param step
   * @return
   */
  public static LineString densification(LineString ls, double step) {

    // coordonnees de la ligne initiale
    Coordinate[] coords = ls.getCoordinates();

    // table des coordonnees densifiees
    int nbPoints = (int) (ls.getLength() / step);
    Coordinate[] coordsDens = new Coordinate[nbPoints + 1];

    if (nbPoints + 1 < coords.length)
      return ls;

    // remplissage
    int iDens = 0;
    double dist = 0.0, angle = 0.0, longueur;
    for (int i = 0; i < coords.length - 1; i++) {
      Coordinate coord0 = coords[i], coord1 = coords[i + 1];

      longueur = coord0.distance(coord1);
      if (dist <= longueur) {
        angle = Math.atan2(coord1.y - coord0.y, coord1.x - coord0.x);
      }

      while (dist <= longueur) {

        // ajouter point a ligne densifiee
        coordsDens[iDens] = new Coordinate(coord0.x + dist * Math.cos(angle),
            coord0.y + dist * Math.sin(angle));

        dist += step;
        iDens++;
      }
      dist -= longueur;
    }

    // le dernier point
    coordsDens[nbPoints] = coords[coords.length - 1];

    return new GeometryFactory().createLineString(coordsDens);
  }

  public static ILineString densification(ILineString ls, double pas) {
    ILineString ls_ = null;
    try {
      ls_ = (ILineString) AdapterFactory
          .toGM_Object(LineDensification.densification(
              (LineString) AdapterFactory.toGeometry(new GeometryFactory(), ls),
              pas));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ls_;
  }

  public static IPolygon densification(IPolygon poly, double pas) {
    IPolygon poly_ = null;
    try {
      ILineString densExt = densification(poly.exteriorLineString(), pas);
      poly_ = new GM_Polygon(densExt);
      for (IRing ring : poly.getInterior()) {
        ILineString densRing = densification((ILineString) ring.getPrimitive(),
            pas);
        poly_.addInterior(new GM_Ring(densRing));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly_;
  }

  public static IGeometry densification(IGeometry geom, double pas) {
    if (geom instanceof IPolygon)
      return densification((IPolygon) geom, pas);
    if (geom instanceof ILineString)
      return densification((ILineString) geom, pas);
    return geom;
  }

  /**
   * The line is densified with at least a vertex every step. When original
   * segments are shorter than step, they are kept as they are. All initial
   * vertices are preserved.
   * @param ls
   * @param step
   * @return
   */
  public static ILineString densification2(ILineString ls, double step) {
    IDirectPositionList pts = new DirectPositionList();
    IDirectPosition previous = ls.startPoint();
    pts.add(ls.startPoint());
    for (int i = 1; i < ls.coord().size(); i++) {
      IDirectPosition current = ls.coord().get(i);
      double dist = previous.distance2D(current);
      if (dist <= step) {
        pts.add(current);
        previous = current;
        continue;
      }
      // arrived here, vertices have to be added
      int nbAdded = new Double(Math.floor(dist / step)).intValue();
      for (int j = 0; j < nbAdded; j++) {
        // compute the coordinates of the new point
        double length1 = (j + 1) * step;
        double length2 = dist - ((j + 1) * step);
        double k = length1 / length2;
        double x = (previous.getX() + k * current.getX()) / (k + 1.0);
        double y = (previous.getY() + k * current.getY()) / (k + 1.0);
        pts.add(new DirectPosition(x, y));
      }
      // add the current vertex to the new vertices list
      pts.add(current);
      previous = current;
    }

    return new GM_LineString(pts);
  }

  /**
   * The polygon ring is densified with at least a vertex every step. When
   * original segments are shorter than step, they are kept as they are. All
   * initial vertices are preserved.
   * @param ls
   * @param step
   * @return
   */
  public static IPolygon densification2(IPolygon poly, double pas) {
    IPolygon poly_ = null;
    try {
      ILineString densExt = densification2(poly.exteriorLineString(), pas);
      poly_ = new GM_Polygon(densExt);
      for (IRing ring : poly.getInterior()) {
        ILineString densRing = densification2(
            (ILineString) ring.getPrimitive(), pas);
        poly_.addInterior(new GM_Ring(densRing));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly_;
  }

  /**
   * The geometry is densified with at least a vertex every step. When original
   * segments are shorter than step, they are kept as they are. All initial
   * vertices are preserved.
   * @param ls
   * @param step
   * @return
   */
  public static IGeometry densification2(IGeometry geom, double pas) {
    if (geom instanceof IPolygon)
      return densification2((IPolygon) geom, pas);
    if (geom instanceof ILineString)
      return densification2((ILineString) geom, pas);
    return geom;
  }

}
