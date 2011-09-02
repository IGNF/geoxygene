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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ISurfacePatch;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurfaceBoundary;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SurfaceBoundary;

/**
 * Polygone : morceau de surface plan (les arêtes constituant la frontière sont
 * coplanaires). L'attribut interpolation vaut "planar" par défaut.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Polygon extends GM_SurfacePatch implements IPolygon {

  // ////////////////////////////////////////////////////////////////////////////////////////////
  // // modele original ISO abandonne pour simplification
  // ///////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////////

  /** Stocke la frontière constituant le polygone. */
  // protected GM_SurfaceBoundary boundary;

  /** Renvoie la frontière. */
  // public GM_SurfaceBoundary getBoundary () {return this.boundary;}

  /** Optionnel. */
  // protected GM_Surface spanningSurface;

  /** Renvoie la spanning surface. */
  // public GM_Surface getSpanningSurface () {return this.spanningSurface;}

  // ////////////////////////////////////////////////////////////////////////////////////////////
  // // frontiere du polygone
  // ///////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////////////////

  /** Anneau extérieur. */
  protected IRing exterior;

  @Override
  public IRing getExterior() {
    return this.exterior;
  }

  @Override
  public void setExterior(IRing value) {
    this.exterior = value;
  }

  @Override
  public int sizeExterior() {
    if (this.exterior == null) {
      return 0;
    }
    return 1;
  }

  /** Anneau(x) intérieur(s) en cas de trou(s) : liste de GM_Ring */
  protected List<IRing> interior = new ArrayList<IRing>(0);

  @Override
  public List<IRing> getInterior() {
    return this.interior;
  }

  @Override
  public IRing getInterior(int i) {
    return this.interior.get(i);
  }

  @Override
  public void setInterior(int i, IRing value) {
    this.interior.set(i, value);
  }

  @Override
  public void addInterior(IRing value) {
    this.interior.add(value);
  }

  @Override
  public void addInterior(int i, IRing value) {
    this.interior.add(i, value);
  }

  @Override
  public void removeInterior(IRing value) {
    this.interior.remove(value);
  }

  @Override
  public void removeInterior(int i) {
    this.interior.remove(i);
  }

  @Override
  public int sizeInterior() {
    return this.interior.size();
  }

  // //////////////////////////////////////////////////////////////////////////////////////////////
  // // Constructeurs
  // /////////////////////////////////////////////////////////////////////////////

  /** Constructeur par défaut */
  public GM_Polygon() {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
  }

  /**
   * NON IMPLEMENTE. Constructeur à partir d'une frontière et d'une surface.
   * @param boundary
   * @param spanSurf
   */
  public GM_Polygon(ISurfaceBoundary boundary, ISurface spanSurf) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
  }

  /** Constructeur à partir d'une frontière. */
  public GM_Polygon(ISurfaceBoundary bdy) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    this.exterior = bdy.getExterior();
    this.interior = bdy.getInterior();
  }

  /**
   * Constructeur à partir d'une GM_LineString fermée sans vérifier la
   * fermeture. ATTENTION : ne vérifie pas la fermeture.
   */
  public GM_Polygon(ILineString ls) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    GM_Ring ring = new GM_Ring(ls);
    this.exterior = ring;
  }

  /**
   * Constructeur à partir d'une GM_LineString fermée en vérifiant la fermeture.
   * Vérifie la fermeture (d'où le paramètre tolérance), sinon exception.
   */
  public GM_Polygon(ILineString ls, double tolerance) throws Exception {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    try {
      GM_Ring ring = new GM_Ring(ls, tolerance);
      this.exterior = ring;
    } catch (Exception e) {
      throw new Exception(
          "Tentative de créer un polygone avec une LineString non fermée.");
    }
  }

  /**
   * Constructeur à partir d'une GM_Curve fermée sans vérifier la fermeture.
   * ATTENTION : ne vérifie pas la fermeture.
   */
  public GM_Polygon(ICurve curve) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    GM_Ring ring = new GM_Ring(curve);
    this.exterior = ring;
  }

  /**
   * Constructeur à partir d'une GM_Curve fermée en vérifiant la fermeture.
   * Vérifie la fermeture (d'où le paramètre tolérance), sinon exception.
   */
  public GM_Polygon(ICurve curve, double tolerance) throws Exception {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    try {
      GM_Ring ring = new GM_Ring(curve, tolerance);
      this.exterior = ring;
    } catch (Exception e) {
      throw new Exception(
          "Tentative de créer un polygone avec une GM_Curve non fermée.");
    }
  }

  /** Constructeur à partir d'un GM_Ring. */
  public GM_Polygon(IRing ring) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    this.exterior = ring;
  }

  /** Constructeur à partir d'une enveloppe (2D). */
  public GM_Polygon(IEnvelope env) {
    super();
    this.patch.add(this);
    this.interpolation = "planar"; //$NON-NLS-1$
    GM_LineString ls = new GM_LineString();
    boolean flag3D = true;
    Double D = new Double(env.getLowerCorner().getZ());
    if (D.isNaN()) {
      flag3D = false;
    }
    ls.getControlPoint().add(env.getLowerCorner());
    DirectPosition dp = null;
    if (flag3D) {
      dp = new DirectPosition(env.getUpperCorner().getX(), env.getLowerCorner()
          .getY(), 0.0);
    } else {
      dp = new DirectPosition(env.getUpperCorner().getX(), env.getLowerCorner()
          .getY());
    }
    ls.getControlPoint().add(dp);
    ls.getControlPoint().add(env.getUpperCorner());
    if (flag3D) {
      dp = new DirectPosition(env.getLowerCorner().getX(), env.getUpperCorner()
          .getY(), 0.0);
    } else {
      dp = new DirectPosition(env.getLowerCorner().getX(), env.getUpperCorner()
          .getY());
    }
    ls.getControlPoint().add(dp);
    ls.getControlPoint().add(env.getLowerCorner());
    GM_Ring ring = new GM_Ring(ls);
    this.exterior = ring;
  }

  /**
   * Constructor by copy.
   * @param geom geometry to copy
   */
  public GM_Polygon(GM_Polygon geom) {
    this(new GM_Ring(geom.getExterior()));
    for (IRing ring : geom.getInterior()) {
      this.addInterior(new GM_Ring(ring));
    }
  }

  // //////////////////////////////////////////////////////////////////////////////////////////////
  // // Methode implementant une methode de GM_SurfacePatch
  // ///////////////////////////////////////
  @Override
  public ISurfacePatch reverse() {
    IRing oldRing = this.getExterior();
    GM_Ring newRing = new GM_Ring();
    int n = oldRing.sizeGenerator();
    for (int i = 0; i < n; i++) {
      IOrientableCurve oriCurve = oldRing.getGenerator(n - i - 1);
      if (oriCurve.getOrientation() == +1) {
        newRing.addGenerator(oriCurve.getNegative());
      } else if (oriCurve.getOrientation() == -1) {
        newRing.addGenerator(oriCurve.getPositive());
      }
    }
    GM_SurfaceBoundary newBdy = new GM_SurfaceBoundary(newRing);
    int m = this.sizeInterior();
    if (m > 0) {
      for (int j = 0; j < m; j++) {
        oldRing = this.getInterior(j);
        n = oldRing.sizeGenerator();
        newRing = new GM_Ring();
        for (int i = 0; i < n; i++) {
          IOrientableCurve oriCurve = oldRing.getGenerator(n - i - 1);
          if (oriCurve.getOrientation() == +1) {
            newRing.addGenerator(oriCurve.getNegative());
          } else if (oriCurve.getOrientation() == -1) {
            newRing.addGenerator(oriCurve.getPositive());
          }
        }
        newBdy.addInterior(newRing);
      }
    }
    GM_Polygon result = new GM_Polygon(newBdy);
    return result;
  }

  @Override
  public Object clone() {
    GM_Polygon poly = new GM_Polygon((IRing) this.getExterior().clone());
    for (int i = 0; i < this.sizeInterior(); i++) {
      poly.addInterior((IRing) this.getInterior(i).clone());
    }
    return poly;
  }

  @Override
  public boolean isPolygon() {
    return true;
  }

  /**
   * @return true if the polygon is counter clockwise, false otherwise
   */
  public boolean isCounterClockwise() {
    // computation of the signed area
    IDirectPositionList exteriorCoord = this.exteriorCoord();
    double signedArea = 0;
    for (int i = 0; i < exteriorCoord.size() - 1; i++) {
      signedArea += (exteriorCoord.get(i).getX()
          * exteriorCoord.get(i + 1).getY() - exteriorCoord.get(i + 1).getX()
          * exteriorCoord.get(i).getY()) / 2;
    }
    return signedArea > 0;
  }
}
