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

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Conversions entre les GM_Object GeOxygene et les Geometry JTS.
 * 
 * @author Thierry Badard, Arnaud Braun & Christophe Pele & Julien Perret
 * @version 1.2 2008/07/31 (Julien Perret) : ajout de la factory
 *          {@link AdapterFactory}
 * @version 1.3 2008/07/31 (Julien Perret) : commentaires javadoc
 */

public class JtsGeOxygene {
  /*------------------------------------------------------------*/
  /*-- Fields --------------------------------------------------*/
  /*------------------------------------------------------------*/

  // private static int jtsSRID=0;
  private static PrecisionModel jtsPrecision = new PrecisionModel();

  // private static GeometryFactory jtsGeomFactory=new
  // GeometryFactory(JtsGeOxygene.jtsPrecision,JtsGeOxygene.jtsSRID);
  // private static WKTReader jtsWktReader=new
  // WKTReader(JtsGeOxygene.jtsGeomFactory);
  // private static WKTWriter jtsWktWriter=new WKTWriter();

  /*------------------------------------------------------------*/
  /*-- Conversion beetween JTS and GeOxygene objects -----------*/
  /*------------------------------------------------------------*/

  /**
   * Conversion d'une géométrie GeOxygene {@link IGeometry} en géométrie JTS
   * {@link Geometry}.
   * @param geOxyGeom une géométrie GeOxygene
   * @return une géométrie JTS équivalente
   * @throws Exception renvoie une exception si la géométrie en entrée n'est pas
   *           valide
   */
  public static Geometry makeJtsGeom(IGeometry geOxyGeom) throws Exception {
    return JtsGeOxygene.makeJtsGeom(geOxyGeom, true);
  }

  /**
   * Conversion d'une géométrie GeOxygene {@link IGeometry} en géométrie JTS
   * {@link Geometry}.
   * @param geOxyGeom une géométrie GeOxygene
   * @param adapter si adapter est vrai, on utiliser la factory
   *          {@link AdapterFactory}, sinon, on passe par WKT
   * @return une géométrie JTS équivalente
   * @throws Exception Exception renvoie une exception si la géométrie en entrée
   *           n'est pas valide
   */
  public static Geometry makeJtsGeom(IGeometry geOxyGeom, boolean adapter)
      throws Exception {
    if (adapter) {
      return AdapterFactory.toGeometry(new GeometryFactory(
          JtsGeOxygene.jtsPrecision, geOxyGeom.getCRS()), geOxyGeom);
    }

    GeometryFactory jtsGeomFactory = new GeometryFactory(
        JtsGeOxygene.jtsPrecision, geOxyGeom.getCRS());
    WKTReader jtsWktReader = new WKTReader(jtsGeomFactory);
    String wktGeom = WktGeOxygene.makeWkt(geOxyGeom);
    return jtsWktReader.read(wktGeom);
  }

  /**
   * Conversion d'une géométrie JTS {@link Geometry} en géométrie GeOxygene
   * {@link IGeometry}.
   * @param jtsGeom une géométrie JTS
   * @return une géométrie GeOxygene équivalente
   * @throws Exception Exception renvoie une exception si la géométrie en entrée
   *           n'est pas valide
   */
  public static IGeometry makeGeOxygeneGeom(Geometry jtsGeom) throws Exception {
    return JtsGeOxygene.makeGeOxygeneGeom(jtsGeom, true);
  }

  /**
   * Conversion d'une géométrie JTS {@link Geometry} en géométrie GeOxygene
   * {@link IGeometry}.
   * @param adapter si adapter est vrai, on utiliser la factory
   *          {@link AdapterFactory}, sinon, on passe par WKT
   * @param jtsGeom une géométrie JTS
   * @return une géométrie GeOxygene équivalente
   * @throws Exception Exception renvoie une exception si la géométrie en entrée
   *           n'est pas valide
   */
  public static IGeometry makeGeOxygeneGeom(Geometry jtsGeom, boolean adapter)
      throws Exception {
    if (adapter) {
      return AdapterFactory.toGM_Object(jtsGeom);
    }

    WKTWriter jtsWktWriter = new WKTWriter();
    String wktResult = jtsWktWriter.write(jtsGeom);
    return WktGeOxygene.makeGeOxygene(wktResult);
  }

  /**
   * Conversion d'une coordonnée JTS {@link CoordinateSequence} en position
   * GeOxygene {@link IDirectPosition}.
   * @param jtsCoord une coordonnée JTS
   * @return une position GeOxygene équivalente
   * @throws Exception Exception renvoie une exception si la géométrie en entrée
   *           n'est pas valide
   */
  public static IDirectPosition makeDirectPosition(CoordinateSequence jtsCoord)
      throws Exception {
    GeometryFactory jtsGeomFactory = new GeometryFactory(
        JtsGeOxygene.jtsPrecision, 0);
    Geometry jtsPoint = new Point(jtsCoord, jtsGeomFactory);
    IPoint geOxyPoint = (IPoint) JtsGeOxygene.makeGeOxygeneGeom(jtsPoint);
    IDirectPosition geOxyDirectPos = geOxyPoint.getPosition();
    return geOxyDirectPos;
  }

  /**
   * Conversion d'un tableau de coordonnées JTS {@link CoordinateSequence} en
   * liste de positions GeOxygene {@link IDirectPositionList}.
   * @param jtsCoords un tableau de coordonnées JTS
   * @return une list de positions GeOxygene équivalente
   * @throws Exception Exception renvoie une exception si la géométrie en entrée
   *           n'est pas valide
   */
  public static IDirectPositionList makeDirectPositionList(
      CoordinateSequence[] jtsCoords) throws Exception {
    DirectPositionList list = new DirectPositionList();
    for (CoordinateSequence jtsCoord : jtsCoords) {
      list.add(JtsGeOxygene.makeDirectPosition(jtsCoord));
    }
    return list;
  }
}
