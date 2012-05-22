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

package fr.ign.cogit.geoxygene.util.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.I18N;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_Complex;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Cette factory sert à transformer les géométries JTS en géométrie GeOxygene et
 * l'inverse. Elle est beaucoup plus efficace que la méthode de la classe
 * JtsGeOxygene puisqu'elle ne passe pas par la représentation des géométries en
 * WKT, mais transpose directement les structures de données. néanmoins, tous
 * les objets sont dupliqués (notamment les points). Une autre méthode
 * consisterait à faire de vrais adaptateurs qui ne dupliquent pas la géométrie
 * mais se font passer pour des objets d'une autre classe. Elle n'est pas
 * implémentée et il reste à voir si c'est possible.
 * 
 * TODO : gérer toutes les géométries, notamment {@link GM_Complex},
 * {@link GM_Solid}, etc.
 * 
 * @author Julien Perret
 */
public class AdapterFactory {
  static Logger logger = Logger.getLogger(AdapterFactory.class.getName());

  /**
   * Transforme une géométrie GeOxygene ({@link GM_Object}) en géométrie JTS (
   * {@link Geometry}). Toutes les géométries ne sont pas gérées. Une géométrie
   * non gérées renvoie une exception. TODO Doit-on renvoyer une exception si la
   * GM_Ring contient 1 ou 2 points ?
   * 
   * @param factory factory JTS pour construire les nouvelles géométries JTS.
   * @param geom géométrie GeOxygene
   * @return géométrie JTS équivalente
   * @throws Exception renvoie une exception si le type de géométrie n'est pas
   *           géré.
   */
  @SuppressWarnings("unchecked")
  public static Geometry toGeometry(GeometryFactory factory, IGeometry geom)
      throws Exception {
    if (geom == null) {
      return null;
    }
    Geometry result = null;
    if (geom instanceof IPoint) {
      result = factory.createPoint(AdapterFactory.toCoordinateSequence(factory,
          geom.coord()));
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IRing) {
      IDirectPositionList coord = geom.coord();
      if (coord.size() <= 3 && !coord.isEmpty()) {
        if (AdapterFactory.logger.isDebugEnabled()) {
          AdapterFactory.logger.debug(geom);
          AdapterFactory.logger.debug(coord);
        }
        throw new Exception(
            I18N.getString("AdapterFactory.RingWithLessThan4Points")); //$NON-NLS-1$
      }
      CoordinateSequence sequence = AdapterFactory.toCoordinateSequence(
          factory, coord);
      if (sequence.size() > 3
          && sequence.getCoordinate(0).equals(
              sequence.getCoordinate(sequence.size() - 1))) {
        result = factory.createLinearRing(sequence);
        result.setSRID(geom.getCRS());
        return result;
      }
//      logger.error(sequence.size() + " " + sequence.getCoordinate(0).equals(
//              sequence.getCoordinate(sequence.size() - 1)));
//      logger.error(sequence.getCoordinate(0));
//      logger.error(sequence.getCoordinate(sequence.size() - 1));
//      System.exit(0);
      throw new Exception(I18N
              .getString("AdapterFactory.RingNotClosed")); //$NON-NLS-1$
    }
    if (geom instanceof ILineString) {
      result = AdapterFactory.toLineString(factory, (GM_LineString) geom);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof ICurve) {
      // other than linestring
      ILineString line = ((ICurve) geom).asLineString(AdapterFactory.getSpacing(), 0);
//      logger.error("ICURVE " + line);
      result = AdapterFactory.toLineString(factory, line);
//      logger.error("ICURVE " + result);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IPolygon) {
      result = factory.createPolygon(
          (LinearRing) AdapterFactory.toGeometry(factory,
              ((IPolygon) geom).getExterior()),
          AdapterFactory.toLinearRingArray(factory,
              ((IPolygon) geom).getInterior()));
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IMultiPoint) {
      IMultiPoint multiPoint = (IMultiPoint) geom;
      Point[] points = new Point[multiPoint.size()];
      for (int index = 0; index < multiPoint.size(); index++) {
        points[index] = (Point) AdapterFactory.toGeometry(factory,
            multiPoint.get(index));
      }
      result = factory.createMultiPoint(points);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IMultiCurve) {
      IMultiCurve<IOrientableCurve> multiCurve = (IMultiCurve<IOrientableCurve>) geom;
      LineString[] lineStrings = new LineString[multiCurve.size()];
      for (int index = 0; index < multiCurve.size(); index++) {
        lineStrings[index] = (LineString) AdapterFactory.toGeometry(factory,
            multiCurve.get(index));
      }
      result = factory.createMultiLineString(lineStrings);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IMultiSurface) {
      IMultiSurface<IOrientableSurface> multiSurface = (IMultiSurface<IOrientableSurface>) geom;
      Polygon[] polygons = new Polygon[multiSurface.size()];
      for (int index = 0; index < multiSurface.size(); index++) {
        polygons[index] = (Polygon) AdapterFactory.toGeometry(factory,
            multiSurface.get(index));
      }
      result = factory.createMultiPolygon(polygons);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof IAggregate) {
      IAggregate<IGeometry> aggregate = (IAggregate<IGeometry>) geom;
      Geometry[] geometries = new Geometry[aggregate.size()];
      for (int index = 0; index < aggregate.size(); index++) {
        geometries[index] = AdapterFactory.toGeometry(factory,
            aggregate.get(index));
      }
      result = factory.createGeometryCollection(geometries);
      result.setSRID(geom.getCRS());
      return result;
    }
    if (geom instanceof ISolid) {
      List<IOrientableSurface> lOS = ((ISolid) geom).getFacesList();
      GM_MultiSurface<IOrientableSurface> multiSurface = new GM_MultiSurface<IOrientableSurface>(
          lOS);
      Polygon[] polygons = new Polygon[multiSurface.size()];
      for (int index = 0; index < multiSurface.size(); index++) {
        polygons[index] = (Polygon) AdapterFactory.toGeometry(factory,
            multiSurface.get(index));
      }
      result = factory.createMultiPolygon(polygons);
      result.setSRID(geom.getCRS());
      return result;
    }
    throw new Exception(
        I18N.getString("AdapterFactory.Type") + geom.getClass() + I18N.getString("AdapterFactory.Unhandled")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private static double SPACING = 1.0;
  public static void setSpacing(double s) {
    AdapterFactory.SPACING = s;
  }
  public static double getSpacing() {
    return AdapterFactory.SPACING;
  }

  public static LineString toLineString(GeometryFactory factory,
      ILineString line) {
    return factory.createLineString(AdapterFactory.toCoordinateSequence(
        factory, line.coord()));
  }

  /**
   * Transforme une position GeOxygene ({@link DirectPosition}) en coordonnée
   * JTS ({@link Coordinate}).
   * 
   * @param directPosition position GeOxygene
   * @return coordonnée JTS équivalente
   */
  public static Coordinate toCoordinate(IDirectPosition directPosition) {
    return new Coordinate(directPosition.getX(), directPosition.getY(),
        directPosition.getZ());
  }

  /**
   * Transforme une liste de positions GeOxygene ({@link DirectPositionList}) en
   * coordonnées JTS ({@link CoordinateSequence}).
   * @param factory factory JTS
   * @param list liste de Positions GeOxygene
   * @return séquence de coordonnées JTS équivalents
   */
  public static CoordinateSequence toCoordinateSequence(
      GeometryFactory factory, IDirectPositionList list) {
    if (list == null) {
      return factory.getCoordinateSequenceFactory().create(new Coordinate[0]);
    }
    Coordinate[] coords = new Coordinate[list.size()];
    for (int i = 0; i < list.size(); i++) {
      coords[i] = AdapterFactory.toCoordinate(list.get(i));
    }
    return factory.getCoordinateSequenceFactory().create(coords);
  }

  /**
   * Transforme une liste de {@link GM_Ring}s GeOxygene en {@link LinearRing}s
   * JTS
   * @param factory factory JTS
   * @param list liste de {@link GM_Ring}s
   * @return tableau de {@link LinearRing}s JTS équivalents
   * @throws Exception renvoie une exception si le type de géométrie n'est pas
   *           géré.
   */
  public static LinearRing[] toLinearRingArray(GeometryFactory factory,
      List<IRing> list) throws Exception {
    // LinearRing[] rings = new LinearRing[list.size()];
    List<LinearRing> rings = new ArrayList<LinearRing>();
    for (int i = 0; i < list.size(); i++) {
      LinearRing ring = (LinearRing) AdapterFactory.toGeometry(factory,
          list.get(i));
      if (ring != null) {
        rings.add(ring);
      } else {
        return null;
      }
    }
    return rings.toArray(new LinearRing[0]);
  }

  /**
   * Transforme une géométrie JTS ({@link Geometry}) en géométrie GeOxygene (
   * {@link IGeometry}). Toutes les géométries ne sont pas gérées. Une géométrie
   * non gérée renvoie une exception.
   * 
   * @param geom géométrie JTS
   * @return géométrie GeOxygene équivalente
   * @throws Exception renvoie une exception si le type de géométrie n'est pas
   *           géré.
   */
  public static IGeometry toGM_Object(Geometry geom) throws Exception {
    if (geom == null) {
      return null;
    }
    GM_Object result = null;
    if (geom instanceof Point) {
      result = new GM_Point(AdapterFactory.toDirectPosition(geom
          .getCoordinate()));
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof LinearRing) {
      result = new GM_Ring(new GM_LineString(
          AdapterFactory.toDirectPositionList(geom.getCoordinates())));
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof LineString) {
      result = new GM_LineString(AdapterFactory.toDirectPositionList(geom
          .getCoordinates()));
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof Polygon) {
      if (geom.isEmpty()) {
        result = new GM_Polygon();
      } else {
        GM_Polygon polygon = new GM_Polygon(new GM_Ring(new GM_LineString(
            AdapterFactory.toDirectPositionList(((Polygon) geom)
                .getExteriorRing().getCoordinates()))));
        for (int index = 0; index < ((Polygon) geom).getNumInteriorRing(); index++) {
          LineString ring = ((Polygon) geom).getInteriorRingN(index);
          polygon.addInterior((GM_Ring) AdapterFactory.toGM_Object(ring));
        }
        result = polygon;
        result.setCRS(geom.getSRID());
        return result;
      }
    }
    if (geom instanceof MultiPoint) {
      MultiPoint mp = (MultiPoint) geom;
      GM_MultiPoint multiPoint = new GM_MultiPoint();
      for (int i = 0; i < mp.getNumGeometries(); i++) {
        Point p = (Point) mp.getGeometryN(i);
        GM_Point point = (GM_Point) AdapterFactory.toGM_Object(p);
        multiPoint.add(point);
      }
      result = multiPoint;
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof MultiLineString) {
      MultiLineString mls = (MultiLineString) geom;
      GM_MultiCurve<GM_OrientableCurve> multiLineString = new GM_MultiCurve<GM_OrientableCurve>();
      for (int i = 0; i < mls.getNumGeometries(); i++) {
        LineString p = (LineString) mls.getGeometryN(i);
        GM_LineString lineString = (GM_LineString) AdapterFactory
            .toGM_Object(p);
        multiLineString.add(lineString);
      }
      result = multiLineString;
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof MultiPolygon) {
      MultiPolygon mp = (MultiPolygon) geom;
      GM_MultiSurface<GM_OrientableSurface> multiPolygon = new GM_MultiSurface<GM_OrientableSurface>();
      for (int i = 0; i < mp.getNumGeometries(); i++) {
        Polygon p = (Polygon) mp.getGeometryN(i);
        GM_Polygon polygon = (GM_Polygon) AdapterFactory.toGM_Object(p);
        multiPolygon.add(polygon);
      }
      result = multiPolygon;
      result.setCRS(geom.getSRID());
      return result;
    }
    if (geom instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geom;
      GM_Aggregate<IGeometry> aggregate = new GM_Aggregate<IGeometry>();
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        aggregate.add(AdapterFactory.toGM_Object(gc.getGeometryN(i)));
      }
      result = aggregate;
      result.setCRS(geom.getSRID());
      return result;
    }
    if (result != null) {
      result.setCRS(geom.getSRID());
      return result;
    }
    throw new Exception(
        I18N.getString("AdapterFactory.Type") + geom.getClass() + I18N.getString("AdapterFactory.Unhandled")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Transforme une coordonnée JTS ({@link Coordinate}) en position GeOxygene (
   * {@link IDirectPosition}).
   * 
   * @param coord coordonnée JTS
   * @return position GeOxygene équivalente
   */
  public static IDirectPosition toDirectPosition(Coordinate coord) {
    return new DirectPosition(coord.x, coord.y, coord.z);
  }

  /**
   * Transforme un tableau de coordonnées JTS ({@link Coordinate}) en liste de
   * positions GeOxygene ({@link IDirectPositionList}).
   * 
   * @param coords tableau de coordonnées JTS
   * @return liste de positions GeOxygene équivalente
   */
  public static IDirectPositionList toDirectPositionList(Coordinate[] coords) {
    DirectPositionList list = new DirectPositionList();
    if (coords.length == 0) {
      return list;
    }
    // verifie si coords est fermee, cad si les deux coordonnees extremes sont a
    // la meme position
    boolean closed = (coords[0].x == coords[coords.length - 1].x && coords[0].y == coords[coords.length - 1].y);
    // copie de toutes les coodonnees sauf la derniere
    for (int i = 0; i < coords.length - 1; i++) {
      list.add(AdapterFactory.toDirectPosition(coords[i]));
    }
    // si ferme, ajout ajout de la premiere coordonnee au debut, sinon ajout de
    // la derniere
    if (closed && !list.isEmpty()) {
      list.add(list.get(0));
    } else {
      list.add(AdapterFactory.toDirectPosition(coords[coords.length - 1]));
    }
    return list;
  }

  /**
   * Transforme la dimension des coordonnées d'un tableau de coordonnées JTS (
   * {@link Coordinate}) en 2D.
   * 
   * @param coords tableau de coordonnées JTS
   * @return séquence de coordonnées JTS en 2D
   */
  public static CoordinateSequence to2DCoordinateSequence(Coordinate[] coords,
      GeometryFactory factory) {
    Coordinate[] newCoords = new Coordinate[coords.length];
    for (int i = 0; i < coords.length; i++) {
      newCoords[i] = AdapterFactory.to2DCoordinate(coords[i]/* , factory */);
    }
    return factory.getCoordinateSequenceFactory().create(newCoords);
  }

  /**
   * Transforme la dimension de coordonnées JTS ({@link Coordinate}) en 2D.
   * 
   * @param coord coordonnées JTS
   * @return coordonnées JTS en 2D
   */
  public static Coordinate to2DCoordinate(Coordinate coord) {
    return new Coordinate(coord.x, coord.y);
  }

  /**
   * Transforme la dimension de {@link IDirectPosition}s en 2D.
   * 
   * @param position position
   * @return psotion en 2D
   */
  public static IDirectPosition to2DDirectPosition(IDirectPosition position) {
    return new DirectPosition(position.getX(), position.getY());
  }

  /**
   * Transforme la dimension d'une liste de positions GeOxygene (
   * {@link IDirectPositionList}).
   * 
   * @param directPositionList liste de positions GeOxygene
   * @return liste de positions GeOxygene équivalente en 2D
   */
  public static IDirectPositionList to2DDirectPositionList(
      IDirectPositionList directPositionList) {
    DirectPositionList list = new DirectPositionList();
    for (IDirectPosition o : directPositionList) {
      list.add(AdapterFactory.to2DDirectPosition(o));
    }
    return list;
  }

  /**
   * Transforme une géométrie GeOxygene en géométrie 2D.
   * @param geom une géométrie GeOxygene
   * @return une géométrie GeOxygene 2D
   * @throws Exception renvoie une exception si le type de géométrie n'est pas
   *           géré
   */
  @SuppressWarnings("unchecked")
  public static IGeometry to2DGM_Object(IGeometry geom) throws Exception {
    if (geom == null) {
      return null;
    }
    if (geom instanceof IPoint) {
      return new GM_Point(AdapterFactory.to2DDirectPosition(((GM_Point) geom)
          .getPosition()));
    }
    if (geom instanceof IRing) {
      return new GM_Ring(new GM_LineString(
          AdapterFactory.to2DDirectPositionList(geom.coord())));
    }
    if (geom instanceof ILineString) {
      return new GM_LineString(AdapterFactory.to2DDirectPositionList(geom
          .coord()));
    }
    if (geom instanceof GM_Polygon) {
      GM_Polygon polygon = new GM_Polygon(new GM_Ring(new GM_LineString(
          AdapterFactory.to2DDirectPositionList(((GM_Polygon) geom)
              .exteriorCoord()))));
      for (int index = 0; index < ((IPolygon) geom).sizeInterior(); index++) {
        ILineString ring = ((IPolygon) geom).interiorLineString(index);
        polygon.addInterior(new GM_Ring((GM_LineString) AdapterFactory
            .to2DGM_Object(ring)));
      }
      return polygon;
    }
    if (geom instanceof IMultiPoint) {
      IMultiPoint mp = (IMultiPoint) geom;
      GM_MultiPoint multiPoint = new GM_MultiPoint();
      for (int i = 0; i < mp.size(); i++) {
        multiPoint.add((IPoint) AdapterFactory.to2DGM_Object(mp.get(i)));
      }
      return multiPoint;
    }
    if (geom instanceof IMultiCurve) {
      IMultiCurve<IOrientableCurve> mls = (IMultiCurve<IOrientableCurve>) geom;
      IMultiCurve<IOrientableCurve> multiLineString = new GM_MultiCurve<IOrientableCurve>();
      for (int i = 0; i < mls.size(); i++) {
        multiLineString.add((IOrientableCurve) AdapterFactory.to2DGM_Object(mls
            .get(i)));
      }
      return multiLineString;
    }
    if (geom instanceof IMultiSurface) {
      IMultiSurface<IOrientableSurface> mp = (IMultiSurface<IOrientableSurface>) geom;
      GM_MultiSurface<GM_OrientableSurface> multiPolygon = new GM_MultiSurface<GM_OrientableSurface>();
      for (int i = 0; i < mp.size(); i++) {
        multiPolygon.add((GM_OrientableSurface) AdapterFactory
            .to2DGM_Object(multiPolygon.get(i)));
      }
      return multiPolygon;
    }
    if (geom instanceof IAggregate) {
      IAggregate<IGeometry> gc = (IAggregate<IGeometry>) geom;
      GM_Aggregate<IGeometry> aggregate = new GM_Aggregate<IGeometry>();
      for (int i = 0; i < gc.size(); i++) {
        aggregate.add(AdapterFactory.to2DGM_Object(gc.get(i)));
      }
      return aggregate;
    }
    throw new Exception(
        I18N.getString("AdapterFactory.Type") + geom.getClass() + I18N.getString("AdapterFactory.Unhandled")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Traduit un type de géométrie JTS {@link Geometry} et renvoie le type de
   * géométrie GeOxygene {@link IGeometry} équivalent. TODO gérer tous les types
   * de géométrie.
   * @param geometryType type de géométrie JTS
   * @return type de géométrie GeOxygene équivalent
   */
  public static Class<? extends IGeometry> toGeometryType(Class<?> geometryType) {
    if (LineString.class.equals(geometryType)) {
      return ILineString.class;
    }
    if (MultiLineString.class.equals(geometryType)) {
      return IMultiCurve.class;
    }
    if (Polygon.class.equals(geometryType)) {
      return IPolygon.class;
    }
    if (MultiPolygon.class.equals(geometryType)) {
      return IMultiSurface.class;
    }
    if (Point.class.equals(geometryType)) {
      return IPoint.class;
    }
    if (MultiPoint.class.equals(geometryType)) {
      return IMultiPoint.class;
    }
    return IGeometry.class;
  }

  /**
   * Traduit un type de géométrie GeOxygene {@link IGeometry} et renvoie le type
   * de géométrie JTS {@link Geometry} équivalent. TODO gérer tous les types de
   * géométrie.
   * @param geometryType type de géométrie GeOxygene
   * @return type de géométrie JTS équivalent
   */
  public static Class<? extends Geometry> toJTSGeometryType(
      Class<?> geometryType) {
    if (ILineString.class.isAssignableFrom(geometryType)) {
      return LineString.class;
    }
    if (IMultiCurve.class.isAssignableFrom(geometryType)) {
      return MultiLineString.class;
    }
    if (IPolygon.class.isAssignableFrom(geometryType)) {
      return Polygon.class;
    }
    if (IMultiSurface.class.isAssignableFrom(geometryType)) {
      return MultiPolygon.class;
    }
    if (IPoint.class.isAssignableFrom(geometryType)) {
      return Point.class;
    }
    if (IMultiPoint.class.isAssignableFrom(geometryType)) {
      return MultiPoint.class;
    }
    if (IAggregate.class.isAssignableFrom(geometryType)) {
      return GeometryCollection.class;
    }
    return Geometry.class;
  }
}
