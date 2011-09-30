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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;

/**
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Brasebin Mickael
 */

public class GM_Triangle extends GM_Polygon implements ITriangle {

  protected GM_Position[] corners = new GM_Position[3];

  @Override
  public IPosition[] getCorners() {
    return this.corners;
  }

  @Override
  public IPosition getCorners(int i) {
    return this.corners[i];
  }

  @Override
  public double area() {

    IDirectPositionList dpl = this.coord();

    Vecteur v1 = new Vecteur(dpl.get(0), dpl.get(1));
    Vecteur v2 = new Vecteur(dpl.get(0), dpl.get(2));

    return Math.abs(0.5 * v1.prodVectoriel(v2).norme());

  }

  public GM_Triangle(IRing gmRing) {
    super(gmRing);
  }

  public GM_Triangle(IDirectPosition dp1, IDirectPosition dp2,
      IDirectPosition dp3) {
    this(new GM_LineString(new DirectPositionList(dp1, dp2, dp3, dp1)));
  }

  public GM_Triangle(IDirectPositionList list) {
    this(list.get(0), list.get(1), list.get(2));
  }

  public GM_Triangle(GM_LineString ls) {
    super(ls);
  }

  public GM_Triangle() {
    super();
  }

  @Override
  public GM_SurfacePatch reverse() {
    return new GM_Triangle(this.getCorners(0).getDirect(), this.getCorners(2)
        .getDirect(), this.getCorners(1).getDirect());
  }
}
