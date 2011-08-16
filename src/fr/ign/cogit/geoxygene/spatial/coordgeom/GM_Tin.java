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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITin;

/**
 * NON IMPLEMENTE, A FAIRE. Surface triangulée avec la méthode de Delaunay ou un
 * algorithme similaire, et prenant en considération des stoplines, des
 * breaklines et une longueur maximale pour les arêtes des triangles.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public class GM_Tin extends GM_TriangulatedSurface implements ITin {

  /**
   * Lignes où la continuité locale ou la régularité de la surface est remise en
   * cause : un triangle intersectant une telle ligne doit être enlevé du TIN en
   * laissant un trou à la place.
   */
  protected List<ILineString> stopLines;

  public ILineString getStopLines(int i) {
    return this.stopLines.get(i);
  }

  public int cardStopLines() {
    return this.stopLines.size();
  }

  /**
   * Lignes qui doivent être incluses dans la triangulation, même en violant les
   * critères de Delaunay.
   */
  protected List<ILineString> breakLines;

  public ILineString getBreakLines(int i) {
    return this.breakLines.get(i);
  }

  public int cardBreakLines() {
    return this.breakLines.size();
  }

  /**
   * Longueur maximum de l'arête d'un triangle du TIN. Tout triangle adjacent à
   * une arête dont la longueur est supérieure à maxLength doit être supprimé de
   * la triangulation. (NORME : cet attribut est de type Distance.)
   */
  protected double maxLength;

  public double getMaxLength() {
    return this.maxLength;
  }

  /**
   * Points servant à construire la grille.
   */
  protected List<IPosition> controlPoint;

  public IPosition getControlPoint(int i) {
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
  public GM_Tin(final IPosition[] post, final ILineString[] stopLines,
      final ILineString[] breakLines, final float maxLength) {
  }
}
