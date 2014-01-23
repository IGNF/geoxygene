/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for the development and
 * deployment of geographic (GIS) applications. It is a open source contribution of the COGIT laboratory at the Institut
 * Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library (see file LICENSE if
 * present); if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITin;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.spatial.I18N;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Surface triangulée avec la méthode de Delaunay ou un algorithme similaire, et prenant en considération des stoplines,
 * des breaklines et une longueur maximale pour les arêtes des triangles.
 * 
 * Triangulated surface taking into account stoplines and brealines and maximal edge length
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 * 
 */
public class GM_Tin extends GM_TriangulatedSurface implements ITin {

  protected static Logger logger = Logger.getLogger(GM_Tin.class.getName());

  /**
   * Lignes où la continuité locale ou la régularité de la surface est remise en cause : un triangle intersectant une
   * telle ligne doit être enlevé du TIN en laissant un trou à la place.
   */
  protected List<ILineString> stopLines;

  @Override
  public ILineString getStopLines(int i) {
    return this.stopLines.get(i);
  }

  @Override
  public int cardStopLines() {
    return this.stopLines.size();
  }

  /**
   * Lignes qui doivent être incluses dans la triangulation, même en violant les critères de Delaunay.
   */
  protected List<ILineString> breakLines;

  @Override
  public ILineString getBreakLines(int i) {
    return this.breakLines.get(i);
  }

  @Override
  public int cardBreakLines() {
    return this.breakLines.size();
  }

  /**
   * Longueur maximum de l'arête d'un triangle du TIN. Tout triangle adjacent à une arête dont la longueur est
   * supérieure à maxLength doit être supprimé de la triangulation. (NORME : cet attribut est de type Distance.)
   */
  protected double maxLength;

  @Override
  public double getMaxLength() {
    return this.maxLength;
  }

  protected IDirectPositionList controlPoint;

  @Override
  public IDirectPosition getControlPoint(int i) {
    return this.controlPoint.get(i);
  }

  @Override
  public int sizeControlPoint() {
    return this.controlPoint.size();
  }

  /**
   * Constructor with only used parameters
   * @param post
   * @param breakLines
   */
  public GM_Tin(IDirectPositionList post, List<ILineString> breakLines) {
    this(post, null, breakLines, 0);

  }

  /**
   * Constructor using a set of points, a set of stop lines and break lines.
   * @param post points
   * @param stopLines stop lines
   * @param breakLines break lines
   * @param maxLength maximum edge length
   */
  public GM_Tin(IDirectPositionList post, List<ILineString> stopLines, List<ILineString> breakLines, float maxLength) {
    super();
    this.stopLines = stopLines;
    this.controlPoint = post;
    this.breakLines = breakLines;
    this.maxLength = maxLength;

    ConformingDelaunayTriangulationBuilder tb = new ConformingDelaunayTriangulationBuilder();
    GM_MultiPoint sites = new GM_MultiPoint();
    for (IDirectPosition n : post) {
      sites.add(n.toGM_Point());
    }
    IMultiCurve<ILineString> linesConstraints = new GM_MultiCurve<ILineString>();
    if (breakLines != null) {
      for (ILineString l : breakLines) {
        linesConstraints.add(l);
      }
    }
    IMultiCurve<ILineString> stopLinesAggregate = new GM_MultiCurve<ILineString>();
    if (stopLines != null) {
      for (ILineString l : stopLines) {
        stopLinesAggregate.add(l);
      }
    }
    GeometryFactory geomFact = new GeometryFactory();
    try {
      Geometry geomSites = AdapterFactory.toGeometry(geomFact, sites);
      Geometry lineConstraints = AdapterFactory.toGeometry(geomFact, linesConstraints);
      tb.setTolerance(0.01); // FIXME added tolerance to prevent from having ghost triangles
      tb.setSites(geomSites);
      tb.setConstraints(lineConstraints);
      GeometryCollection triangles = (GeometryCollection) tb.getTriangles(geomFact);
      for (int i = 0; i < triangles.getNumGeometries(); i++) {
        Polygon triangle = (Polygon) triangles.getGeometryN(i);
        IDirectPositionList list = AdapterFactory.toDirectPositionList(triangle.getCoordinates());
        double dmax = list.get(0).distance(list.get(1));
        dmax = Math.max(dmax, list.get(1).distance(list.get(2)));
        dmax = Math.max(dmax, list.get(2).distance(list.get(0)));
        if (dmax <= maxLength) {
          GM_Triangle t = new GM_Triangle(list);
          // go through stop lines to prevent from adding triangles that intersect them
          if (!t.intersects(stopLinesAggregate)) {
            this.getlTriangles().add(t);
          }
        }
      }
    } catch (Exception e) {
      GM_Tin.logger.error(I18N.getString("GMTIN.Error")); //$NON-NLS-1$
      e.printStackTrace();
    }
  }
}
