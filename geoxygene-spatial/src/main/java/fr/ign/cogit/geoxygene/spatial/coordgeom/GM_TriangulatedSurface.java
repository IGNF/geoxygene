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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ISurfacePatch;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangulatedSurface;
import fr.ign.cogit.geoxygene.spatial.I18N;

/**
 * Same as PolyhedralSurface with Triangles
 * 
 * 
 * @author Mickaël Brasebin
 * 
 * @version 1.0
 * 
 */

public class GM_TriangulatedSurface extends GM_PolyhedralSurface implements
    ITriangulatedSurface {

  private List<ITriangle> lTriangles = null;
  protected static Logger logger = Logger.getLogger(ITriangulatedSurface.class
      .getName());

  /**
   * Constructeur par défaut
   */
  public GM_TriangulatedSurface() {
    super();
  }

  /**
   * Crée une GM_TriangulatedSurface à partir d'une liste de triangles
   * @param lTriangles
   */
  public GM_TriangulatedSurface(List<ITriangle> lTriangles) {
    super();
    this.getlTriangles().addAll(lTriangles);

  }

  /**
   * Retourne la liste des triangles utilisés
   * @return
   */
  public List<ITriangle> getlTriangles() {
    if (this.lTriangles == null) {
      this.lTriangles = new ArrayList<ITriangle>();
    }
    return this.lTriangles;
  }

  @Override
  public List<IPolygon> getlPolygons() {

    List<IPolygon> lPolygons = new ArrayList<IPolygon>();
    lPolygons.addAll(this.lTriangles);

    return lPolygons;
  }

  @Override
  public List<ISurfacePatch> getPatch() {
    List<ISurfacePatch> lPatch = new ArrayList<ISurfacePatch>();
    lPatch.addAll(this.lTriangles);
    return lPatch;
  }

  @Override
  public void setPatch(int i, ISurfacePatch value) {

    if (!(value instanceof ITriangle)) {

      GM_TriangulatedSurface.logger.warn(I18N
          .getString("GMTriangulatedSurface.NOTtriangle")); //$NON-NLS-1$
      return;

    }

    super.setPatch(i, value);
  }

  @Override
  public void addPatch(ISurfacePatch value) {
    if (!(value instanceof ITriangle)) {

      GM_TriangulatedSurface.logger.warn(I18N
          .getString("GMTriangulatedSurface.NOTtriangle")); //$NON-NLS-1$
      return;

    }

    super.addPatch(value);
  }

  @Override
  public void addPatch(int i, ISurfacePatch value) {
    if (!(value instanceof ITriangle)) {

      GM_TriangulatedSurface.logger.warn(I18N
          .getString("GMTriangulatedSurface.NOTtriangle")); //$NON-NLS-1$
      return;

    }
    super.addPatch(i, value);
  }
  
  @Override
  public IDirectPositionList coord() {
      int nbElem = this.getlTriangles().size();
    
      IDirectPositionList dpl = new DirectPositionList();
      for (int i = 0; i < nbElem; i++) {
        dpl.addAll(this.getlTriangles().get(i).coord());
      }
      return dpl;
  
  }

}
