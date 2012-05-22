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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_CurveBoundary;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

/**
 * Complexe ayant toutes les propriétés géométriques d'une courbe. C'est une
 * liste de courbes orientées (GM_OrientableCurve) de telle manière que le noeud
 * final d'une courbe correspond au noeud initial de la courbe suivante dans la
 * liste. Hérite de GM_OrientableCurve, mais le lien n'apparaît pas
 * explicitement (problème de double héritage en java). Les méthodes et
 * attributs ont été reportés.
 * 
 * <P>
 * ATTENTION : normalement, il faudrait remplir le set "element" (contrainte :
 * toutes les primitives du generateur sont dans le complexe). Ceci n'est pas
 * implémenté pour le moment.
 * <P>
 * A FAIRE AUSSI : iterateur sur "generator"
 * 
 * @author Thierry Badard & Arnaud Braun
 * @author julien Gaffuri
 * @version 1.1
 * 
 */
public class GM_CompositeCurve extends GM_OrientableCurve implements
    ICompositeCurve {
  private static Logger logger = Logger.getLogger(GM_CompositeCurve.class
      .getName());
  // Attribut "generator" et méthodes pour le traiter ////////////////////
  /** Les GM_OrientableCurve constituant self. */
  protected List<IOrientableCurve> generator;

  @Override
  public List<IOrientableCurve> getGenerator() {
    return this.generator;
  }

  @Override
  public IOrientableCurve getGenerator(int i) {
    return this.generator.get(i);
  }

  @Override
  public void setGenerator(int i, IOrientableCurve value) {
    this.generator.set(i, value);
  }

  @Override
  public void addGenerator(IOrientableCurve value) {
    this.generator.add(value);
  }

  @Override
  public void addGenerator(IOrientableCurve value, double tolerance)
      throws Exception {
    IDirectPosition pt1;
    IDirectPosition pt2;
    if (this.generator.size() > 0) {
      IOrientableCurve laDerniereCourbe = this.getGenerator(this.generator
          .size() - 1);
      pt1 = laDerniereCourbe.boundary().getEndPoint().getPosition();
      pt2 = value.boundary().getStartPoint().getPosition();
      if (pt1.equals(pt2, tolerance)) {
        this.generator.add(value);
      } else {
        throw new Exception(
            "Rupture de chaînage avec la courbe passée en paramètre."); //$NON-NLS-1$
      }
    } else {
      this.generator.add(value);
    }
  }

  @Override
  public void addGeneratorTry(IOrientableCurve value, double tolerance)
      throws Exception {
    try {
      this.addGenerator(value, tolerance);
    } catch (Exception e1) {
      try {
        this.addGenerator(value.getNegative(), tolerance);
      } catch (Exception e2) {
        throw new Exception(
            "Rupture de chaînage avec la courbe passée en paramètre(après avoir essayé les 2 orientations)"); //$NON-NLS-1$
      }
    }
  }

  @Override
  public void addGenerator(int i, IOrientableCurve value) {
    this.generator.add(i, value);
  }

  @Override
  public void removeGenerator(IOrientableCurve value) throws Exception {
    if (this.generator.size() == 1) {
      throw new Exception("Il n'y a qu'un objet dans l'association."); //$NON-NLS-1$
    }
    this.generator.remove(value);
  }

  @Override
  public void removeGenerator(int i) throws Exception {
    if (this.generator.size() == 1) {
      throw new Exception("Il n'y a qu'un objet dans l'association."); //$NON-NLS-1$
    }
    this.generator.remove(i);
  }

  @Override
  public int sizeGenerator() {
    return this.generator.size();
  }

  /**
   * Constructeur par défaut.
   */
  public GM_CompositeCurve() {
    this.generator = new ArrayList<IOrientableCurve>(0);
    this.primitive = new GM_Curve();
    // this.proxy[0] = this.primitive;
    /*
     * GM_OrientableCurve proxy1 = new GM_OrientableCurve(); proxy1.orientation
     * = -1; proxy1.proxy[0] = this.primitive; proxy1.proxy[1] = proxy1;
     * proxy1.primitive = new GM_Curve(this.primitive); this.proxy[1] = proxy1;
     */
  }

  /**
   * Constructeur à partir d'une et d'une seule GM_OrientableCurve.
   * L'orientation vaut +1.
   */
  public GM_CompositeCurve(IOrientableCurve oCurve) {
    this.generator = new ArrayList<IOrientableCurve>(1);
    this.generator.add(oCurve);
    this.primitive = new GM_Curve();
    this.simplifyPrimitive();
    // this.proxy[0] = this.primitive;
    /*
     * GM_OrientableCurve proxy1 = new GM_OrientableCurve(); proxy1.orientation
     * = -1; proxy1.proxy[0] = this.primitive; proxy1.proxy[1] = proxy1;
     * proxy1.primitive = new GM_Curve(this.primitive); this.proxy[1] = proxy1;
     */
  }

  // //////////////////////////////////////////////////////////////////////
  // Attributs et méthodes héritées de GM_OrientableCurve ////////////////
  // //////////////////////////////////////////////////////////////////////
  // On simule l'heritage du modele en reportant les attributs et methodes
  // de GM_OrientableCurve
  // On n'a pas repris l'attribut "orientation" qui ne sert a rien ici.

  /**
   * Primitive. Elle doit etre recalculée à chaque modification de self : fait
   * dans getPrimitive().
   */
  protected ICurve primitive;

  @Override
  public ICurve getPrimitive() {
    this.simplifyPrimitive();
    return this.primitive;
  }

  /**
   * Attribut stockant les primitives orientées de cette primitive. Proxy[0] est
   * celle orientée positivement. Proxy[1] est celle orientée négativement. On
   * accède aux primitives orientées par getPositive() et getNegative().
   */
  // protected GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];
  @Override
  public GM_OrientableCurve getPositive() {
    return (GM_OrientableCurve) this.getPrimitive(); // equivaut a return
                                                     // this.proxy[0]
  }

  @Override
  public GM_OrientableCurve getNegative() {
    return (GM_OrientableCurve) this.getPrimitive().getNegative();
  }

  @Override
  public GM_CurveBoundary boundary() {
    return (GM_CurveBoundary) this.getPrimitive().boundary();
  }

  /**
   * Vérifie le chaînage des composants. Renvoie TRUE s'ils sont chaînés, FALSE
   * sinon.
   */
  @Override
  public boolean validate(double tolerance) {
    for (int i = 0; i < this.generator.size() - 1; i++) {
      IOrientableCurve oCurve1 = this.generator.get(i);
      ICurve prim1 = oCurve1.getPrimitive();
      IOrientableCurve oCurve2 = this.generator.get(i + 1);
      ICurve prim2 = oCurve2.getPrimitive();
      IDirectPosition pt1 = prim1.endPoint();
      IDirectPosition pt2 = prim2.startPoint();
      if (!pt1.equals(pt2, tolerance)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IDirectPositionList coord() {
    return this.getPrimitive().coord();
  }

  /**
   * Calcule la primitive se self.
   */
  private void simplifyPrimitive() {
    int n = this.generator.size();
    if (n > 0) {
      // vidage de la primitive
      synchronized (this.primitive.getSegment()) {
        this.primitive.clearSegments();
        for (IOrientableCurve oCurve : this.generator) {
          ICurve thePrimitive = oCurve.getPrimitive();
          synchronized (thePrimitive.getSegment()) {
            for (ICurveSegment theSegment : thePrimitive.getSegment()) {
              this.primitive.addSegment(theSegment);
            }
          }
        }
      }
    }
  }

  @Override
  public int getOrientation() {
    GM_CompositeCurve.logger.error("non implemented method");
    return 0;
  }

  @Override
  public Set<IComplex> getComplex() {
    GM_CompositeCurve.logger.error("non implemented method");
    return null;
  }

  @Override
  public int sizeComplex() {
    GM_CompositeCurve.logger.error("non implemented method");
    return 0;
  }

  /** Set de primitives constituant self. */
  // protected Set<IGeometry> element = new HashSet<IGeometry>();
  @Override
  public void addElement(IPrimitive value) {
    this.getElement().add(value);
    value.getComplex().add(this);
  }

  @Override
  public void removeElement(IPrimitive value) {
    this.getElement().remove(value);
    value.getComplex().remove(this);
  }

  @Override
  public Set<IGeometry> getElement() {
    return null; // return this.element;
  }

  @Override
  public int sizeElement() {
    return this.getElement().size();
  }

  /** Les sous-complexes constituant self. */
  protected Set<IComplex> subComplex = new HashSet<IComplex>();

  @Override
  public Set<IComplex> getSubComplex() {
    return this.subComplex;
  }

  @Override
  public int sizeSubComplex() {
    return this.subComplex.size();
  }

  @Override
  public void addSubComplex(IComplex value) {
    this.subComplex.add(value);
    value.getSuperComplex().add(this);
    this.getElement().add(value);
    value.getElement().add(this);
  }

  @Override
  public void removeSubComplex(IComplex value) {
    this.subComplex.remove(value);
    value.getSuperComplex().remove(this);
    this.getElement().remove(value);
    value.getElement().remove(this);
  }

  /** Les super-complexes constituant self. */
  // protected Set<IComplex> superComplex = new HashSet<IComplex>();
  @Override
  public Set<IComplex> getSuperComplex() {
    return null;// this.superComplex;
  }

  @Override
  public void addSuperComplex(IComplex value) {
    this.getSuperComplex().add(value);
  }

  @Override
  public void removeSuperComplex(IComplex value) {
    this.getSuperComplex().remove(value);
  }

  @Override
  public int sizeSuperComplex() {
    return this.getSuperComplex().size();
  }

  @Override
  public boolean isMaximal() {
    return (this.sizeSuperComplex() == 0);
  }
}
