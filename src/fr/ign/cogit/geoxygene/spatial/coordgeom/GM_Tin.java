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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

/**
 * Surface triangulée avec la méthode de Delaunay ou un algorithme similaire, et
 * prenant en considération des stoplines, des breaklines et une longueur
 * maximale pour les arêtes des triangles.
 * 
 * Triangulated surface taking into account stoplines and brealines and maximal
 * edge length
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 * 
 */
public class GM_Tin extends GM_TriangulatedSurface implements ITin {

  protected static Logger logger = Logger.getLogger(GM_Tin.class.getName());

  /**
   * Lignes où la continuité locale ou la régularité de la surface est remise en
   * cause : un triangle intersectant une telle ligne doit être enlevé du TIN en
   * laissant un trou à la place.
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
   * Lignes qui doivent être incluses dans la triangulation, même en violant les
   * critères de Delaunay.
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
   * Longueur maximum de l'arête d'un triangle du TIN. Tout triangle adjacent à
   * une arête dont la longueur est supérieure à maxLength doit être supprimé de
   * la triangulation. (NORME : cet attribut est de type Distance.)
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
   * 
   * @param post
   * @param stopLines not used
   * @param breakLines
   * @param maxLength not used
   */
  public GM_Tin(IDirectPositionList post, List<ILineString> stopLines,
      List<ILineString> breakLines, float maxLength) {

    super();

    this.stopLines = stopLines;
    this.controlPoint = post;
    this.breakLines = breakLines;
    this.maxLength = maxLength;

    List<Noeud> lNodes = new ArrayList<Noeud>();

    if (post != null) {
      int nbPos = post.size();

      for (int i = 0; i < nbPos; i++) {
        lNodes.add(new Noeud(post.get(i)));
      }

    }
    List<Arc> lArcStoplines = new ArrayList<Arc>();

    if (stopLines != null) {
      int nbArc = stopLines.size();

      for (int i = 0; i < nbArc; i++) {

        Arc a = new Arc();
        a.setGeom(stopLines.get(i));
        lArcStoplines.add(a);
      }
    }

    List<Arc> lArcBreakLines = new ArrayList<Arc>();

    if (breakLines != null) {
      int nbBreakLines = breakLines.size();

      for (int i = 0; i < nbBreakLines; i++) {

        Arc a = new Arc();
        a.setGeom(breakLines.get(i));
        lArcBreakLines.add(a);
      }

    }

    TriangulationJTS triJTS = new TriangulationJTS("TriangulationJTS"); //$NON-NLS-1$
    triJTS.getPopNoeuds().addAll(lNodes);
    triJTS.getPopArcs().addAll(lArcBreakLines);

    try {
      triJTS.triangule(""); //$NON-NLS-1$
    } catch (Exception e) {
      e.printStackTrace();

    }

    IPopulation<Face> popFaces = triJTS.getPopFaces();

    int nbFace = popFaces.size();

    // On traite chaque triangle
    for (int i = 0; i < nbFace; i++) {

      GM_OrientableSurface geom = (GM_OrientableSurface) popFaces.get(i)
          .getGeometrie();

      // Is this a Triangle ?
      if (geom.coord().size() == 4 || geom.coord().size() == 3) {

        this.getlTriangles().add(
            new GM_Triangle(geom.coord().get(0), geom.coord().get(1), geom
                .coord().get(2)));

      } else {

        GM_Tin.logger.error(I18N.getString("GMTIN.Error")); //$NON-NLS-1$

      }

    }

  }
}
