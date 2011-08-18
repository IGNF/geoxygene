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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolidBoundary;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * NON UTILISE. Object géométrique de base en 3D.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Solid extends GM_Primitive implements ISolid {
  static Logger logger = Logger.getLogger(GM_Solid.class.getName());

  /**
   * NON IMPLEMENTE (renvoie 0.0). Aire. Dans la norme, le résultat est de type
   * Area.
   * */
  @Override
  public double area() {
    GM_Solid.logger
        .error("Non implémentée, utiliser : return CalculSansJava3D.CalculAire(this); (renvoi 0.0)"); //$NON-NLS-1$
    return 0.0;
  }

  @Override
  public IDirectPositionList coord() {
    List<IOrientableSurface> lFaces = this.getFacesList();
    int n = lFaces.size();
    DirectPositionList dPL = new DirectPositionList();

    for (int i = 0; i < n; i++) {
      IOrientableSurface os = lFaces.get(i);
      dPL.addAll(os.coord());
    }

    int nbInt = this.boundary().getInterior().size();
    for (int i = 0; i < nbInt; i++) {
      lFaces = this.boundary().getInterior().get(i).getlisteFaces();
      n = lFaces.size();

      for (int j = 0; j < n; j++) {
        IOrientableSurface os = lFaces.get(j);
        dPL.addAll(os.coord());
      }
    }
    return dPL;
  }

  /**
   * NON IMPLEMETE (renvoie 0.0). Volume. Dans la norme, le résultat est de type
   * Volume.
   */
  @Override
  public double volume() {
    GM_Solid.logger
        .error("Non implémentée, utiliser : return CalculSansJava3D.CalculVolume(this);"); //$NON-NLS-1$
    return 0.0;
  }

  /** Constructeur par défaut. */
  public GM_Solid() {
  }

  /**
   * Constructeur à partir de la frontière.
   */
  public GM_Solid(ISolidBoundary bdy) {
    this.boundary = bdy;
  }

  /**
   * NON IMPLEMENTE. Constructeur à partir d'une enveloppe .
   * @param env une enveloppe
   */
  public GM_Solid(IEnvelope env) {
    GM_Solid.logger.error("NON IMPLEMENTE"); //$NON-NLS-1$
  }

  @Override
  public ISolidBoundary boundary() {
    return this.boundary;
  }

  /**
   * Boundary auquel est lié le solide
   */
  protected ISolidBoundary boundary = null;

  /**
   * Constructeur à partir d'une liste de faces extérieures
   * @param lOS une liste de faces extérieures
   */
  public GM_Solid(List<IOrientableSurface> lOS) {
    this.boundary = new GM_SolidBoundary(lOS);
  }

  /**
   * Constructeur
   * @param multiSurf multisurface
   */
  public GM_Solid(IMultiSurface<? extends IOrientableSurface> multiSurf) {
    ArrayList<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
    List<? extends IOrientableSurface> lGMObj = multiSurf.getList();
    int nbElements = lGMObj.size();
    for (int i = 0; i < nbElements; i++) {
      lOS.add(lGMObj.get(i));
    }
    this.boundary = new GM_SolidBoundary(lOS);
  }

  @Override
  public List<IOrientableSurface> getFacesList() {
    return this.boundary().getExterior().getlisteFaces();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    List<IOrientableSurface> lOS = this.getFacesList();
    int nbElement = lOS.size();
    sb.append("Solid("); //$NON-NLS-1$
    for (int i = 0; i < nbElement; i++) {
      sb.append(lOS.get(i).toString());
      sb.append("\n"); //$NON-NLS-1$
    }
    sb.append(");"); //$NON-NLS-1$
    return sb.toString();
  }
}
