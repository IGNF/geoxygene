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

package fr.ign.cogit.geoxygene.util.algo;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Appel des methodes GEOS sur des GM_Object. Non finalisé.
 * 
 * @author Thierry Badard, Arnaud Braun & Christophe Pele
 * @version 1.0
 * 
 */
@SuppressWarnings("unused")
public class GeosAlgorithms implements GeomAlgorithms {

  public GeosAlgorithms() {
    System.loadLibrary("GeosAlgorithms");
  }

  private native String intersection(String wkt1, String wkt2);

  @Override
  public IGeometry intersection(IGeometry geom1, IGeometry geom2) {
    try {
      String wkt1 = WktGeOxygene.makeWkt(geom1);
      String wkt2 = WktGeOxygene.makeWkt(geom2);
      String wktResult = this.intersection(wkt1, wkt2);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private native String union(String wkt1, String wkt2);

  @Override
  public IGeometry union(IGeometry geom1, IGeometry geom2) {
    try {
      String wkt1 = WktGeOxygene.makeWkt(geom1);
      String wkt2 = WktGeOxygene.makeWkt(geom2);
      String wktResult = this.union(wkt1, wkt2);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private native String difference(String wkt1, String wkt2);

  @Override
  public IGeometry difference(IGeometry geom1, IGeometry geom2) {
    try {
      String wkt1 = WktGeOxygene.makeWkt(geom1);
      String wkt2 = WktGeOxygene.makeWkt(geom2);
      String wktResult = this.difference(wkt1, wkt2);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private native String symDifference(String wkt1, String wkt2);

  @Override
  public IGeometry symDifference(IGeometry geom1, IGeometry geom2) {
    try {
      String wkt1 = WktGeOxygene.makeWkt(geom1);
      String wkt2 = WktGeOxygene.makeWkt(geom2);
      String wktResult = this.symDifference(wkt1, wkt2);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private native String buffer(String wkt, double distance);

  @Override
  public IGeometry buffer(IGeometry geom, double distance) {
    try {
      String wkt = WktGeOxygene.makeWkt(geom);
      String wktResult = this.buffer(wkt, distance);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public IGeometry buffer10(IGeometry geOxyGeom) {
    return this.buffer(geOxyGeom, 10);
  }

  private native String convexHull(String wkt);

  @Override
  public IGeometry convexHull(IGeometry geOxyGeom) {
    try {
      String wkt = WktGeOxygene.makeWkt(geOxyGeom);
      String wktResult = this.convexHull(wkt);
      return WktGeOxygene.makeGeOxygene(wktResult);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private native boolean contains(String wkt1, String wkt2);

  @Override
  public boolean contains(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    String wkt1 = WktGeOxygene.makeWkt(geOxyGeom1);
    String wkt2 = WktGeOxygene.makeWkt(geOxyGeom2);
    boolean result = this.contains(wkt1, wkt2);
    return result;
  }

  private native boolean intersects(String wkt1, String wkt2);

  @Override
  public boolean intersects(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    String wkt1 = WktGeOxygene.makeWkt(geOxyGeom1);
    String wkt2 = WktGeOxygene.makeWkt(geOxyGeom2);
    boolean result = this.intersects(wkt1, wkt2);
    return result;
  }

  private native double distance(String wkt1, String wkt2);

  @Override
  public double distance(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    String wkt1 = WktGeOxygene.makeWkt(geOxyGeom1);
    String wkt2 = WktGeOxygene.makeWkt(geOxyGeom2);
    double result = this.distance(wkt1, wkt2);
    return result;
  }

  private native double area(String wkt);

  @Override
  public double area(IGeometry geOxyGeom1) {
    String wkt1 = WktGeOxygene.makeWkt(geOxyGeom1);
    double result = this.area(wkt1);
    return result;
  }

  private native String boundary(String wkt);

  public IGeometry boundary(IGeometry geOxyGeom1) {
    return null;
  }

  private native String coordinates(String wkt);

  public DirectPositionList coordinates(IGeometry geOxyGeom1) {
    return null;
  }

  private native String envelope(String wkt);

  public GM_Envelope envelope(IGeometry geOxyGeom) {
    return null;
  }

  private native boolean equals(String wkt1, String wkt2);

  @Override
  public boolean equals(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean equalsExact(String wkt1, String wkt2);

  public boolean equalsExact(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean crosses(String wkt1, String wkt2);

  public boolean crosses(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean disjoint(String wkt1, String wkt2);

  public boolean disjoint(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean within(String wkt1, String wkt2);

  public boolean within(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean overlaps(String wkt1, String wkt2);

  public boolean overlaps(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean touches(String wkt1, String wkt2);

  public boolean touches(IGeometry geOxyGeom1, IGeometry geOxyGeom2) {
    return false;
  }

  private native boolean isEmpty(String wkt);

  public boolean isEmpty(IGeometry geOxyGeom) {
    return false;
  }

  private native boolean isSimple(String wkt);

  public boolean isSimple(IGeometry geOxyGeom) {
    return false;
  }

  private native boolean isValid(String wkt);

  public boolean isValid(IGeometry geOxyGeom) {
    return false;
  }

  public int dimension(IGeometry geOxyGeom) {
    return 0;
  }

  @Override
  public double length(IGeometry geOxyGeom) {
    return 0;
  }

  public int numPoints(IGeometry geOxyGeom) {
    return 0;
  }

  public IGeometry translate(IGeometry geom, double tx, double ty, double tz) {
    return null;
  }

  @Override
  public DirectPosition centroid(IGeometry geom) {
    return null;
  }

}
