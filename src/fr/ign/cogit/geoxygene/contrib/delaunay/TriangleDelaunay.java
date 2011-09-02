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

package fr.ign.cogit.geoxygene.contrib.delaunay;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Classe des triangles de la triangulation.
 * @author Bonin
 * @version 1.0
 */

public class TriangleDelaunay extends Face {
  public TriangleDelaunay() {
  }

  public TriangleDelaunay(NoeudDelaunay n1, NoeudDelaunay n2, NoeudDelaunay n3) {
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(n1.getCoord());
    dpl.add(n2.getCoord());
    dpl.add(n3.getCoord());
    dpl.add(n1.getCoord());
    this.setCoord(dpl);

    // liaison triangle/arcs eventuels
    this.liaison(n1, n2);
    this.liaison(n2, n3);
    this.liaison(n3, n1);

  }

  /**
   * Lie le triangle aux arcs liant eventuellement deux noeuds.
   * 
   * @param n1
   * @param n2
   */
  private void liaison(NoeudDelaunay n1, NoeudDelaunay n2) {

    for (Arc arc : n1.arcs()) {
      if ((arc.getNoeudIni() == n1) && (arc.getNoeudFin() == n2)) {
        arc.setFaceGauche(this);
        break;
      } else if ((arc.getNoeudIni() == n2) && (arc.getNoeudFin() == n1)) {
        arc.setFaceDroite(this);
        break;
      }
    }
  }
}
