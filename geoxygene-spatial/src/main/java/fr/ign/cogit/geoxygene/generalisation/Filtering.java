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

package fr.ign.cogit.geoxygene.generalisation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * Methodes statiques de generalisation par filtrage (Douglas-Peucker).
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */
public class Filtering {
  static Logger logger = LogManager.getLogger(Filtering.class.getName());

  public Filtering() {
  }

  // ///////////////////////////////////////////////////////////////////
  // /// DouglasPeucker
  // ///////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////
  /** Filtrage de Douglas-Peucker sur une polyligne. */
  // On constitue une liste de points, et appelle la méthode
  // "DouglasPeuckerListe" sur cette liste
  public static ILineString DouglasPeuckerLineString(ICurve G0, double seuil) {

    // linéarise la courbe
    ILineString theLineString = null;
    theLineString = G0.asLineString(0.0, 0.0, 0.0);
    if (theLineString == null) {
      Filtering.logger.error("La création de la ligne a échouée");
      return null;
    }

    // constitue une liste de points avec la polyligne
    IDirectPositionList initList = theLineString.coord();

    // appelle la méthode qui agit sur la liste - on Récupère une liste de
    // points
    IDirectPositionList resultList = Filtering.DouglasPeuckerList(initList,
        seuil);

    // crée une polyligne avec cette liste de points
    ILineString theResult = new GM_LineString(resultList);
    return theResult;
  }

  /**
   * Filtrage de Douglas-Peucker sur un polygone. Les trous dont la géométrie
   * n'est pas valide sont supprimés.
   * @param polygone polygone à simplifier
   * @param seuil seuil à utiliser pour le filtrage
   * @return résultat du filtrage de Douglas-Peucker sur un polygone
   */
  public static IPolygon DouglasPeuckerPoly(IPolygon polygone, double seuil) {

    // filtre la frontiere exterieure
    ICurve ext = Filtering.DouglasPeuckerLineString(polygone.getExterior()
        .getPrimitive(), seuil);
    IPolygon poly = new GM_Polygon(ext);

    // filtre les anneaux
    if (polygone.sizeInterior() != 0) {
      for (int i = 0; i < polygone.sizeInterior(); i++) {
        ICurve inte = Filtering.DouglasPeuckerLineString(polygone
            .getInterior(i).getPrimitive(), seuil);
        if (inte.coord().size() > 4) {
          poly.addInterior(new GM_Ring(inte));
        } else if (Filtering.logger.isDebugEnabled()) {
          Filtering.logger.debug("Trou non ajouté " + inte);
        }
      }
    }

    return poly;
  }

  /**
   * Filtrage de DouglasPeucker sur un GM_Object. Supportés : Aggrégat, Courbe,
   * Polyligne, Polygon.
   */
  @SuppressWarnings("unchecked")
  public static IGeometry DouglasPeucker(IGeometry geom, double seuil) {

    if ((geom instanceof ICurve) || (geom instanceof ILineString)) {
      return Filtering.DouglasPeuckerLineString((ICurve) geom, seuil);
    }
    if (geom instanceof IPolygon) {
      return Filtering.DouglasPeuckerPoly((IPolygon) geom, seuil);
    }

    if (geom instanceof IMultiCurve) {
      IMultiCurve<IOrientableCurve> aggr = (IMultiCurve<IOrientableCurve>) geom;
      IMultiCurve<IOrientableCurve> result = new GM_MultiCurve<IOrientableCurve>();
      for (int i = 0; i < aggr.size(); i++) {
        ICurve elt = (ICurve) aggr.get(i);
        result.add(Filtering.DouglasPeuckerLineString(elt, seuil));
      }
      return result;
    }

    if (geom instanceof IMultiSurface) {
      IMultiSurface<IOrientableSurface> aggr = (IMultiSurface<IOrientableSurface>) geom;
      IMultiSurface<IOrientableSurface> result = new GM_MultiSurface<IOrientableSurface>();
      for (int i = 0; i < aggr.size(); i++) {
        IPolygon elt = (IPolygon) aggr.get(i);
        result.add(Filtering.DouglasPeuckerPoly(elt, seuil));
      }
      return result;
    }

    // GM_Aggregat
    IAggregate<IGeometry> aggr = (IAggregate<IGeometry>) geom;
    IAggregate<IGeometry> result = new GM_Aggregate<IGeometry>();
    for (int i = 0; i < aggr.size(); i++) {
      IGeometry elt = aggr.get(i);
      result.add(Filtering.DouglasPeucker(elt, seuil));
    }
    return result;
  }

  /**
   * Douglas-Peucker sur une liste de points.
   * <p>
   * On applique l'algo en utilisant la récursivité.
   * @param PtList liste de points
   * @param seuil seuil utilisé
   * @return la liste de points filtrée
   */
  public static IDirectPositionList DouglasPeuckerList(
      IDirectPositionList PtList, double seuil) {
    return new DirectPositionList(Filtering.DouglasPeuckerList(
        PtList.getList(), seuil));
  }

  /**
   * Douglas-Peucker sur une liste de points.
   * <p>
   * On applique l'algo en utilisant la récursivité.
   * @param PtList liste de points
   * @param seuil seuil utilisé
   * @return la liste de points filtrée
   */
  public static List<IDirectPosition> DouglasPeuckerList(
      List<IDirectPosition> PtList, double seuil) {

    List<IDirectPosition> douglasVector = new ArrayList<IDirectPosition>();
    if (PtList.isEmpty()) {
      return douglasVector;
    }
    List<IDirectPosition> filtreVector = new ArrayList<IDirectPosition>();

    int i = 0;
    int k = 0;
    double dist = 0.0;
    double max = 0.0;
    IDirectPosition Pt;

    int nbpts = PtList.size();
    IDirectPosition PtIni = PtList.get(0);
    IDirectPosition PtFin = PtList.get(nbpts - 1);
    if (nbpts > 2) {
      for (k = 1; k < nbpts - 1; k++) {
        Pt = PtList.get(k);
        dist = Filtering.distLigne(Pt, PtIni, PtFin);
        if ((dist >= seuil) && (dist > max)) {
          i = k;
          max = dist;
        }
      }
    }

    if (i != 0) {
      douglasVector.addAll(Filtering.DouglasPeuckerList(new DirectPositionList(
          PtList.subList(0, i + 1)), seuil));
      douglasVector.addAll(Filtering.DouglasPeuckerList(new DirectPositionList(
          PtList.subList(i, nbpts)), seuil));
    } else {
      douglasVector.add(PtIni);
      douglasVector.add(PtFin);
    }
    /* elimination des doublons */
    IDirectPosition ptmul = douglasVector.get(0);
    filtreVector.add(ptmul);
    for (k = 0; k < douglasVector.size(); k++) {
      Pt = douglasVector.get(k);
      if (!Pt.equals2D(ptmul, 0.)) {
        filtreVector.add(Pt);
        ptmul = Pt;
      }
    }
    return filtreVector;
  }

  /** distance du point P a la droite [AB] */
  private static double distLigne(IDirectPosition P, IDirectPosition A,
      IDirectPosition B) {
    // equation de (AB): y = m1x + p1
    // on appelle H le projete de P sur [AB]
    // equation de (PH) : y = m2x + p2
    // on a (PH) perpendicualire a (AB) ce qui permet de trouver les
    // coordonnees de H
    // on en deduit la longueur [PH]
    double Ax = A.getX();
    double Ay = A.getY();
    double Bx = B.getX();
    double By = B.getY();
    double Px = P.getX();
    double Py = P.getY();
    double Hx = 0.0;
    double Hy = 0.0;
    double m1 = 0.0;
    double m2 = 0.0;
    double p1 = 0.0;
    double p2 = 0.0;

    double deltaX = (Bx - Ax);
    double deltaY = (By - Ay);
    if ((deltaX != 0) && (deltaY != 0)) {
      m1 = deltaY / deltaX;
      p1 = Ay - m1 * Ax;
      m2 = -deltaX / deltaY;
      p2 = Py - m2 * Px;
      Hy = (p1 * m2 - p2 * m1) / (m2 - m1);
      Hx = (p1 - p2) / (m2 - m1);
    } else if (deltaY != 0) {
      Hx = Ax;
      Hy = Py;
    } else {
      Hx = Ax;
      Hy = Ay;
    }
    return Math.sqrt((Py - Hy) * (Py - Hy) + (Px - Hx) * (Px - Hx));
  }
}
