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

import java.util.List;

/**
 * NON IMPLEMENTE, A FAIRE. Surface triangulee avec la methode de Delaunay ou un
 * algorithme similaire, et prenant en consideration des stoplines, des
 * breaklines et une longueur maximale pour les aretes des triangles.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class GM_Tin extends GM_TriangulatedSurface {

  /**
   * Lignes ou la continuite locale ou la regularite de la surface est remise en
   * cause : un triangle intersectant une telle ligne doit etre enleve du TIN en
   * laissant un trou à la place.
   */
  protected List<GM_LineString> stopLines;

  public GM_LineString getStopLines(int i) {
    return this.stopLines.get(i);
  }

  public int cardStopLines() {
    return this.stopLines.size();
  }

  /**
   * Lignes qui doivent etre incluses dans la triangulation, meme en violant les
   * criteres de Delaunay.
   */
  protected List<GM_LineString> breakLines;

  public GM_LineString getBreakLines(int i) {
    return this.breakLines.get(i);
  }

  public int cardBreakLines() {
    return this.breakLines.size();
  }

  /**
   * Longueur maximum de l'arete d'un triangle du TIN. Tout triangle adjacent à
   * une arete dont la longueur est superieure à maxLength doit etre supprime de
   * la triangulation. (NORME : cet attribut est de type Distance.)
   */
  protected double maxLength;

  public double getMaxLength() {
    return this.maxLength;
  }

  /**
   * Points servant à construire la grille.
   */
  protected List<GM_Position> controlPoint;

  public GM_Position getControlPoint(int i) {
    return this.controlPoint.get(i);
  }

  public int sizeControlPoint() {
    return this.controlPoint.size();
  }

  /**
   * Constructeur.
   * @param post
   * @param stopLines
   * @param breakLines
   * @param maxLength
   */
  public GM_Tin(final GM_Position[] post, final GM_LineString[] stopLines,
      final GM_LineString[] breakLines, final float maxLength) {

  }
}
