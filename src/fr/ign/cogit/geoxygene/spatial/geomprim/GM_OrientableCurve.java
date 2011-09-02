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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurveBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;

/**
 * Courbe orientée. L'orientation traduit le sens de paramétrisation. Utilisée
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

public class GM_OrientableCurve extends GM_OrientablePrimitive implements
    IOrientableCurve {
  /** Primitive */
  public ICurve primitive;

  @Override
  public ICurve getPrimitive() {
    return this.primitive;
  }

  /**
   * Attribut stockant les primitives orientées de cette primitive. Proxy[0] est
   * celle orientée positivement. Proxy[1] est celle orientée négativement. On
   * accède aux primitives orientées par getPositive() et getNegative().
   */
  // public GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];
  @Override
  public IOrientableCurve getPositive() {
    return this.primitive;
  }// proxy[0];}

  @Override
  public IOrientableCurve getNegative() {
    return null;
    /*
     * ICurve proxy1prim = this.proxy[1].getPrimitive();
     * proxy1prim.getSegment().clear(); ICurve proxy0 = (ICurve)
     * this.proxy[1].getPositive(); int n = proxy0.sizeSegment(); if (n > 1) {
     * for (int i = 0; i < n; i++) { proxy1prim.addSegment(proxy0.getSegment(n -
     * 1 - i).reverse()); } } else if (n == 1) {
     * proxy1prim.getSegment().add(proxy0.getSegment(0).reverse()); } return
     * this.proxy[1];
     */
  }

  @Override
  public ICurveBoundary boundary() {
    ICurve prim = this.getPrimitive();
    GM_CurveBoundary bdy = new GM_CurveBoundary(prim);
    return bdy;
  }

  @Override
  public IDirectPositionList coord() {
    return this.getPrimitive().coord();
  }

}
