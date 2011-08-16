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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurveBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;

/**
 * Représente un composant d'une GM_SurfaceBoundary. Un GM_Ring est une
 * GM_CompositeCurve fermée, c'est-à-dire des références vers des
 * GM_OrientableCurve connectées en un cycle.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Ring extends GM_CompositeCurve implements IRing {

  /** Constructeur par défaut */
  public GM_Ring() {
    super();
  }

  /**
   * Constructeur à partir d'une et d'une seule GM_OrientableCurve. Ne vérifie
   * pas la fermeture.
   */
  public GM_Ring(IOrientableCurve oriCurve) {
    super(oriCurve);
  }

  /**
   * Constructeur à partir d'une et d'une seule GM_OrientableCurve. Vérifie la
   * fermeture, d'où le paramètre tolérance. Exception si ca ne ferme pas. TODO
   * : un nouveau type d'exception
   * @param oriCurve
   * @param tolerance
   * @throws Exception
   */
  public GM_Ring(IOrientableCurve oriCurve, double tolerance) throws Exception {
    super(oriCurve);
    ICurve c = oriCurve.getPrimitive();
    IDirectPosition pt1 = c.startPoint();
    IDirectPosition pt2 = c.endPoint();
    if (!pt1.equals(pt2, tolerance)) {
      throw new Exception(
          "tentative de créer un GM_Ring avec une courbe non fermée");
    }
  }

  /**
   * Constructeur à partir d'une courbe composée (cast). Ne vérifie ni la
   * fermeture, ni le chainage.
   * @param compCurve
   */
  public GM_Ring(ICompositeCurve compCurve) {
    super();
    this.generator = compCurve.getGenerator();
    this.primitive = compCurve.getPrimitive();
    this.proxy[0] = compCurve.getPositive();
    this.proxy[1] = compCurve.getNegative();
  }

  /**
   * Constructeur à partir d'une courbe composée (cast). Vérifie la fermeture et
   * le chainage sinon exception.
   */
  public GM_Ring(ICompositeCurve compCurve, double tolerance) throws Exception {
    super();
    this.generator = compCurve.getGenerator();
    this.primitive = compCurve.getPrimitive();
    this.proxy[0] = compCurve.getPositive();
    this.proxy[1] = compCurve.getNegative();
    if (!super.validate(tolerance)) {
      throw new Exception(
          "new GM_Ring(): La courbe composée passée en paramètre n'est pas chaînée");
    }
    if (!this.validate(tolerance)) {
      throw new Exception(
          "new GM_Ring(): La courbe composée passée en paramètre ne ferme pas.");
    }
  }

  /**
   * Méthode pour vérifier qu'on a un chainage, et que le point initial est bien
   * égal au point final. Surcharge de la méthode validate sur
   * GM_CompositeCurve. Renvoie TRUE si c'est le cas, FALSE sinon.
   */
  @Override
  public boolean validate(double tolerance) {
    if (!super.validate(tolerance)) {
      return false;
    }
    ICurveBoundary bdy = this.boundary();
    if (bdy.getStartPoint().getPosition().equals(
        bdy.getEndPoint().getPosition(), tolerance)) {
      return true;
    }
    return false;
  }

  @Override
  public Object clone() {
    return new GM_Ring(new GM_LineString((IDirectPositionList) this.coord()
        .clone()));
  }

}
