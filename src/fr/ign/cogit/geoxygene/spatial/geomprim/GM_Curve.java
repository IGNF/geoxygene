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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Courbe. L'orientation vaut nécessairement +1, la primitive est self. Une
 * courbe est composée de un ou plusieurs segments de courbe. Chaque segment à
 * l'intérieur d'une courbe peut être défini avec une interpolation différente.
 * Dans la pratique nous n'utiliserons a priori que des polylignes comme
 * segment(GM_LineString).
 * <P>
 * Modification de la norme suite au retour d'utilisation : on fait hériter
 * GM_CurveSegment de GM_Curve. Du coup, on n'implémente plus l'interface
 * GM_GenericCurve.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public class GM_Curve extends GM_OrientableCurve implements ICurve {
  private static Logger logger = Logger.getLogger(GM_Curve.class.getName());
  /** Liste de tous les segment de courbe (GM_CurveSegment) constituant self. */
  protected List<ICurveSegment> segment;

  @Override
  public List<ICurveSegment> getSegment() {
    return this.segment;
  }

  @Override
  public ICurveSegment getSegment(int i) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Recherche d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme"); //$NON-NLS-1$
        return null;
      }
      return this.segment.get(i);
    }
    return this.segment.get(i);
  }

  @Override
  public void setSegment(int i, ICurveSegment value) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Affection d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.set(i, value);
      }
    } else {
      this.segment.set(i, value);
    }
  }

  @Override
  public void addSegment(ICurveSegment value) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      this.segment.add(value);
    }
  }

  @Override
  public void addSegment(ICurveSegment value, double tolerance)
      throws Exception {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      if (this.sizeSegment() == 0) {
        this.segment.add(value);
      } else {
        int n = this.sizeSegment();
        ICurveSegment lastSegment = this.getSegment(n - 1);
        if (value.startPoint().equals(lastSegment.endPoint(), tolerance)) {
          this.segment.add(value);
        } else {
          throw new Exception(
              "Rupture de chaînage avec le segment passée en paramètre"); //$NON-NLS-1$
        }
      }
    }
  }

  @Override
  public void addSegmentTry(ICurveSegment value, double tolerance)
      throws Exception {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      try {
        this.addSegment(value, tolerance);
      } catch (Exception e1) {
        try {
          this.addSegment(value.reverse(), tolerance);
        } catch (Exception e2) {
          throw new Exception(
              "Rupture de chaînage avec le segment passée en paramètre(après avoir essayé de le retourner)."); //$NON-NLS-1$
        }
      }
    }
  }

  @Override
  public void addSegment(int i, ICurveSegment value) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Ajout d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      this.segment.add(i, value);
    }
  }

  @Override
  public void removeSegment(ICurveSegment value) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      GM_Curve.logger
          .error("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.segment.remove(value);
    }
  }

  @Override
  public void removeSegment(int i) {
    if ((ICurveSegment.class).isAssignableFrom(this.getClass())) {
      GM_Curve.logger
          .error("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.segment.remove(i);
    }
  }

  @Override
  public int sizeSegment() {
    return this.segment.size();
  }

  @Override
  public void clearSegments() {
    this.segment.clear();
  }

  @Override
  public boolean validate(double tolerance) {
    if (this.sizeSegment() <= 1) {
      return true;
    }
    int n = this.sizeSegment();
    for (int i = 0; i < n - 1; i++) {
      ICurveSegment segment1 = this.getSegment(i);
      ICurveSegment segment2 = this.getSegment(i + 1);
      if (!(segment2.startPoint().equals(segment1.endPoint(), tolerance))) {
        return false;
      }
    }
    return true;
  }

  /** Constructeur par défaut */
  public GM_Curve() {
    this.segment = new ArrayList<ICurveSegment>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
     * this.proxy[0] = this; GM_OrientableCurve proxy1 = new
     * GM_OrientableCurve(); proxy1.orientation = -1; proxy1.proxy[0] = this;
     * proxy1.proxy[1] = proxy1; proxy1.primitive = new GM_Curve(this);
     * this.proxy[1] = proxy1;
     */
  }

  /** Constructeur à partir d'un et d'un seul GM_CurveSegment */
  public GM_Curve(ICurveSegment C) {
    this();
    this.segment.add(C);
  }

  /**
   * Usage interne. Utilisé en interne (dans les constructeurs publics) pour
   * construire la courbe opposé, qui est la primitive de proxy[1]. On définit
   * ici les références nécessaires. Le but est de retrouver la propriete :
   * curve.getNegative().getPrimitive().getNegative().getPrimitive() = curve.
   * Les segment de la courbe sont calcule en dynamique lors de l'appel a la
   * methode getNegative().
   */
  public GM_Curve(ICurve curve) {
    this.segment = new ArrayList<ICurveSegment>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
     * this.proxy[0] = this; GM_OrientableCurve proxy1 = new
     * GM_OrientableCurve(); proxy1.orientation = -1; proxy1.proxy[0] = this;
     * proxy1.proxy[1] = proxy1; proxy1.primitive = curve; this.proxy[1] =
     * proxy1;
     */
  }

  @Override
  public IDirectPosition startPoint() {
    return this.getSegment(0).coord().get(0);
  }

  @Override
  public IDirectPosition endPoint() {
    ICurveSegment lastSegment = this.getSegment(this.sizeSegment() - 1);
    IDirectPositionList pointArray = lastSegment.coord();
    return pointArray.get(pointArray.size() - 1);
  }

  @Override
  public ILineString asLineString(double spacing, double offset,
      double tolerance) {
    if ((spacing != 0.0) || (offset != 0.0)) {
      GM_Curve.logger
          .error("GM_Curve::asLineString() : Spacing et Offset ne sont pas implémentés. Passer (0.0, 0.0, tolerance) en paramètres"); //$NON-NLS-1$
      return null;
    }
    GM_LineString theLineString = new GM_LineString();
    synchronized (this.segment) {
      for (ICurveSegment theSegment : this.segment) {
        IDirectPositionList aListOfPoints = theSegment.coord();
        for (IDirectPosition pt1 : aListOfPoints) {
          if (theLineString.sizeControlPoint() > 0) {
            IDirectPosition pt2 = theLineString.getControlPoint().get(
                theLineString.getControlPoint().size() - 1);
            if (!pt1.equals(pt2, tolerance)) {
              theLineString.getControlPoint().add(pt1);
            }
          } else {
            theLineString.getControlPoint().add(pt1);
          }
        }
      }
    }
    return theLineString;
  }

  @Override
  public IDirectPositionList coord() {
    DirectPositionList result = new DirectPositionList();
    if (this.sizeSegment() == 0) {
      return result;
    }
    synchronized (this.segment) {
      for (ICurveSegment theSegment : this.segment) {
        IDirectPositionList theList = theSegment.coord();
        result.addAll(theList);
      }
    }
    return result;
  }

  @Override
  public IDirectPosition constrParam(double cp) {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return null;
  }

  @Override
  public double endConstrParam() {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }

  @Override
  public double endParam() {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }

  @Override
  public double length(IPosition p1, IPosition p2) {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }

  @Override
  public double length(double cparam1, double cparam2) {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }

  @Override
  public IDirectPosition param(double s) {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return null;
  }

  @Override
  public List<?> paramForPoint(IDirectPosition P) {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return null;
  }

  @Override
  public double startConstrParam() {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }

  @Override
  public double startParam() {
    GM_Curve.logger.error("non implemented method"); //$NON-NLS-1$
    return 0;
  }
}
