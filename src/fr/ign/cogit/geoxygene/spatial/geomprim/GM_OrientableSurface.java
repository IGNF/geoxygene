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
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurfaceBoundary;

/**
 * Surface orientée. A de l'intérêt pour traiter les trous : un trou est une
 * surface orientée négativement. A aussi de l'intérêt en 3D. Une surface
 * orientée positivement à sa frontière dans le sens direct (la surface est à
 * gauche de la frontière). Une surface orientée négativement à sa frontière
 * dans le sens des aiguilles d'une montre (la surface est à droite de la
 * frontière). En 3D, on peut représenter un vecteur normal à la surface avec la
 * règle du tire-bouchon. Si l'orientation est +1, alors self est une
 * GM_Surface, de primitive elle-même. Si l'orientation est -1, alors self est
 * une GM_OrientableSurface, de primitive une GM_Surface renversée par rapport à
 * la surface positive.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_OrientableSurface extends GM_OrientablePrimitive implements
    IOrientableSurface {
  /** Primitive */
  public ISurface primitive;

  @Override
  public ISurface getPrimitive() {
    return this.primitive;
  }

  /**
   * Attribut stockant les primitives orientées de cette primitive. Proxy[0] est
   * celle orientée positivement. Proxy[1] est celle orientée négativement. On
   * accède aux primitives orientées par getPositive() et getNegative().
   */
  // public IOrientableSurface[] proxy = new IOrientableSurface[2];
  @Override
  public IOrientableSurface getPositive() {
    return this.primitive;
  }

  // on recalcule en dynamique la primitive de la primitive orientee
  // negativement, qui est "renversee"
  // par rapport a la primitive orientee positivement.

  /*
   * public GM_OrientableSurface getNegative() { GM_Surface proxy1prim =
   * this.proxy[1].primitive; proxy1prim.getPatch().clear(); GM_Surface proxy0 =
   * (GM_Surface)this.proxy[1].proxy[0]; int n = proxy0.sizePatch(); if (n>0)
   * for (int i=0; i<n; i++)
   * proxy1prim.addPatch(proxy0.getPatch(n-1-i).reverse()); return
   * this.proxy[1]; }
   */
  @Override
  public IOrientableSurface getNegative() {
    try {
      IOrientableSurface clone = this.getClass().newInstance();
      int n = this.getPrimitive().sizePatch();
      if (n > 0) {
        for (int i = 0; i < n; i++) {
          clone.getPrimitive().addPatch(
              this.getPrimitive().getPatch(n - 1 - i).reverse());
        }
      }
      return clone;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ISurfaceBoundary boundary() {
    ISurface s = this.getPrimitive();
    int n = s.sizePatch();
    if (n == 1) {
      IPolygon poly = (IPolygon) s.getPatch(0);
      GM_SurfaceBoundary bdy = new GM_SurfaceBoundary();
      bdy.exterior = poly.getExterior();
      bdy.interior = poly.getInterior();
      return bdy;
    }
    System.out
        .println("GM_OrientableSurface::boundary() : cette méthode ne fonctionne que pour les surfaces composées d'un et d'un seul patch."); //$NON-NLS-1$
    return null;
  }

  @Override
  public IDirectPositionList coord() {
    return this.getPrimitive().coord();
  }

}
