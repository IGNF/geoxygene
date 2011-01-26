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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Courbe orientée. L'orientation traduit le sens de paramètrisation. Utilisée
 * comme frontière d'une surface, la surface dont la courbe est frontière est à
 * gauche de la courbe. Si l'orientation est +1, alors self est une GM_Curve, de
 * primitive elle-même. Si l'orientation est -1, alors self est une
 * GM_OrientableCurve, de primitive une GM_Curve renversée par rapport à la
 * courbe positive.
 * 
 * <P>
 * Utilisation : on ne construit pas une GM_OrientableCurve directement, mais a
 * partir d'une GM_Curve en utilisant getPositive() et getNegative().
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_OrientableCurve extends GM_OrientablePrimitive {

  /** Primitive */
  public GM_Curve primitive;

  /** Renvoie la primitive de self */
  public GM_Curve getPrimitive() {
    return this.primitive;
  }

  /**
   * Attribut stockant les primitives orientées de cette primitive. Proxy[0] est
   * celle orientée positivement. Proxy[1] est celle orientée négativement. On
   * accède aux primitives orientées par getPositive() et getNegative().
   */
  public GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];

  /** Renvoie la primitive orientée positivement correspondant à self. */
  public GM_OrientableCurve getPositive() {
    return this.proxy[0];
  }

  /** Renvoie la primitive orientée négativement correspondant à self. */
  public GM_OrientableCurve getNegative() {
    GM_Curve proxy1prim = this.proxy[1].primitive;
    proxy1prim.getSegment().clear();
    GM_Curve proxy0 = (GM_Curve) this.proxy[1].proxy[0];
    int n = proxy0.sizeSegment();
    if (n > 1) {
      for (int i = 0; i < n; i++) {
        proxy1prim.addSegment(proxy0.getSegment(n - 1 - i).reverse());
      }
    } else if (n == 1) {
      proxy1prim.segment.add(proxy0.getSegment(0).reverse());
    }
    return this.proxy[1];
  }

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une
   * GM_CurveBoundary, c'est-à-dire deux GM_Point.
   */
  public GM_CurveBoundary boundary() {
    GM_Curve prim = this.getPrimitive();
    GM_CurveBoundary bdy = new GM_CurveBoundary(prim);
    return bdy;
  }

  /** Renvoie les coordonnees de la primitive. */
  @Override
  public DirectPositionList coord() {
    return this.getPrimitive().coord();
  }

}
