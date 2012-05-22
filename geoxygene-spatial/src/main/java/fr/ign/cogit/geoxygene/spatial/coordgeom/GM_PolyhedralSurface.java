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
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolyhedralSurface;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ISurfacePatch;
import fr.ign.cogit.geoxygene.spatial.I18N;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;

/**
 * GM_Surface composée de surfaces polygonales (GM_Polygon) connectées.
 * 
 * @author Mickaël Brasebin
 * @version 1.0
 * 
 */
public class GM_PolyhedralSurface extends GM_Surface implements
    IPolyhedralSurface {

  private List<IPolygon> lPolygons = null;

  protected static Logger logger = Logger.getLogger(GM_PolyhedralSurface.class
      .getName());

  public List<IPolygon> getlPolygons() {
    if (this.lPolygons == null) {
      this.lPolygons = new ArrayList<IPolygon>();
    }
    return this.lPolygons;
  }

  /** Constructeur par défaut. */
  public GM_PolyhedralSurface() {
    super();
  }

  /**
   * Construit une Polyhedral surface à partir d'une liste de polygones
   * @param lPolygons
   */
  public GM_PolyhedralSurface(List<IPolygon> lPolygons) {
    super();
    this.lPolygons.addAll(lPolygons);
  }

  @Override
  public List<ISurfacePatch> getPatch() {
    List<ISurfacePatch> lPatch = new ArrayList<ISurfacePatch>();
    lPatch.addAll(this.lPolygons);
    return lPatch;
  }

  @Override
  public void setPatch(int i, ISurfacePatch value) {

    if (!(value instanceof IPolygon)) {

      GM_PolyhedralSurface.logger.warn(I18N
          .getString("GMPolyhedralSurface.NOTPolygon")); //$NON-NLS-1$
      return;

    }

    super.setPatch(i, value);
  }

  @Override
  public void addPatch(ISurfacePatch value) {
    if (!(value instanceof IPolygon)) {

      GM_PolyhedralSurface.logger.warn(I18N
          .getString("GMPolyhedralSurface.NOTPolygon")); //$NON-NLS-1$
      return;

    }

    super.addPatch(value);
  }

  @Override
  public void addPatch(int i, ISurfacePatch value) {
    if (!(value instanceof IPolygon)) {

      GM_PolyhedralSurface.logger.warn(I18N
          .getString("GMPolyhedralSurface.NOTPolygon")); //$NON-NLS-1$
      return;

    }
    super.addPatch(i, value);
  }
  
  @Override
  public IDirectPositionList coord() {
    int nbElem = this.getlPolygons().size();

    IDirectPositionList dpl = new DirectPositionList();
    for (int i = 0; i < nbElem; i++) {
      dpl.addAll(this.getlPolygons().get(i).coord());
    }
    return dpl;

  }

}
