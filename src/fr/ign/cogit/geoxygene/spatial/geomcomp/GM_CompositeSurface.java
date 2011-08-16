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

import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurfaceBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;

/**
 * NON UTILISE POUR LE MOMENT. A TERMINER ET TESTER. Complexe ayant toutes les
 * propriétés géométriques d'une surface. C'est une liste de surfaces orientées
 * (GM_OrientableSurfaces) contigues. Hérite de GM_OrientableCurve, mais le lien
 * n'apparaît pas explicitement (problème de double héritage en java). Les
 * méthodes et attributs ont été reportés.
 * <P>
 * ATTENTION : normalement, il faudrait remplir le set "element" (contrainte :
 * toutes les primitives du generateur sont dans le complexe). Ceci n'est pas
 * implémenté pour le moment.
 * <P>
 * A FAIRE AUSSI : iterateur sur "generator"
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */
public class GM_CompositeSurface extends GM_OrientableSurface implements
    ICompositeSurface {
  private static Logger logger = Logger.getLogger(GM_CompositeSurface.class
      .getName());

  // Attribut "generator" et méthodes pour le traiter ////////////////////
  /** Les GM_OrientableSurface constituant self. */
  protected List<IOrientableSurface> generator;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#getGenerator()
   */
  @Override
  public List<IOrientableSurface> getGenerator() {
    return this.generator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#getGenerator
   * (int)
   */
  @Override
  public IOrientableSurface getGenerator(int i) {
    return this.generator.get(i);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#setGenerator
   * (int, fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface)
   */
  @Override
  public void setGenerator(int i, IOrientableSurface value) {
    this.generator.set(i, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#addGenerator
   * (fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface)
   */
  @Override
  public void addGenerator(IOrientableSurface value) {
    this.generator.add(value);
  }

  /**
   * A FAIRE. Ajoute une GM_OrientableSurface en fin de liste avec un contrôle
   * de continuité avec la tolérance passée en paramètre. Envoie une exception
   * en cas de problème.
   * @param value
   * @param tolerance
   * @throws Exception
   */
  @Override
  public void addGenerator(IOrientableSurface value, double tolerance)
      throws Exception {
  }

  /**
   * A FAIRE. Ajoute une GM_OrientableSurface en fin de liste avec un contrôle
   * de continuité avec la tolérance passée en paramètre. Eventuellement change
   * le sens d'orientation de la surface pour assurer la continuite. Envoie une
   * exception en cas de problème.
   * @param value
   * @param tolerance
   * @throws Exception
   */
  @Override
  public void addGeneratorTry(IOrientableSurface value, double tolerance)
      throws Exception {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#addGenerator
   * (int, fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface)
   */
  @Override
  public void addGenerator(int i, IOrientableSurface value) {
    this.generator.add(i, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#removeGenerator
   * (fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface)
   */
  @Override
  public void removeGenerator(IOrientableSurface value) throws Exception {
    if (this.generator.size() == 1) {
      throw new Exception("Il n'y a qu'un objet dans l'association.");
    }
    this.generator.remove(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#removeGenerator
   * (int)
   */
  @Override
  public void removeGenerator(int i) throws Exception {
    if (this.generator.size() == 1) {
      throw new Exception("Il n'y a qu'un objet dans l'association.");
    }
    this.generator.remove(i);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#sizeGenerator()
   */
  @Override
  public int sizeGenerator() {
    return this.generator.size();
  }

  // //////////////////////////////////////////////////////////////////////
  // Constructeurs ///////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // les constructeurs sont calques sur ceux de GM_Surface
  /** Constructeur par défaut */
  public GM_CompositeSurface() {
    this.generator = new ArrayList<IOrientableSurface>();
    this.primitive = new GM_Surface();
    this.proxy[0] = this.primitive;
    GM_OrientableSurface proxy1 = new GM_OrientableSurface();
    proxy1.orientation = -1;
    proxy1.proxy[0] = this.primitive;
    proxy1.proxy[1] = proxy1;
    proxy1.primitive = new GM_Surface(this.primitive);
    this.proxy[1] = proxy1;
  }

  /**
   * Constructeur à partir d'une et d'une seule GM_OrientableSurface.
   * L'orientation vaut +1.
   */
  public GM_CompositeSurface(IOrientableSurface oCurve) {
    this.generator = new ArrayList<IOrientableSurface>();
    this.generator.add(oCurve);
    this.primitive = new GM_Surface();
    // this.simplifyPrimitive(); -> creer la primitive
    this.proxy[0] = this.primitive;
    GM_OrientableSurface proxy1 = new GM_OrientableSurface();
    proxy1.orientation = -1;
    proxy1.proxy[0] = this.primitive;
    proxy1.proxy[1] = proxy1;
    proxy1.primitive = new GM_Surface(this.primitive);
    this.proxy[1] = proxy1;
  }

  // //////////////////////////////////////////////////////////////////////
  // Attributs et méthodes héritées de GM_OrientableSurface ////////////////
  // //////////////////////////////////////////////////////////////////////
  // On simule l'heritage du modele en reportant les attributs et methodes
  // de GM_OrientableSurface
  // On n'a pas repris l'attribut "orientation" qui ne sert a rien ici.

  /**
   * Primitive. Elle doit etre recalculée à chaque modification de self : fait
   * dans getPrimitive().
   */
  protected ISurface primitive;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#getPrimitive
   * (fr.ign.cogit.geoxygene.datatools.Geodatabase)
   */
  // le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
  @Override
  public ISurface getPrimitive() {
    this.simplifyPrimitive();
    return this.primitive;
  }

  /**
   * Attribut stockant les primitives orientées de cette primitive. Proxy[0] est
   * celle orientée positivement. Proxy[1] est celle orientée négativement. On
   * accède aux primitives orientées par getPositive() et getNegative().
   */
  protected IOrientableSurface[] proxy = new IOrientableSurface[2];

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#getPositive
   * (fr.ign.cogit.geoxygene.datatools.Geodatabase)
   */
  @Override
  public IOrientableSurface getPositive() {
    this.simplifyPrimitive();
    return this.primitive; // equivaut a return this.proxy[0]
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#getNegative
   * (fr.ign.cogit.geoxygene.datatools.Geodatabase)
   */
  @Override
  public IOrientableSurface getNegative() {
    this.simplifyPrimitive();
    return this.primitive.getNegative();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#boundary(fr
   * .ign.cogit.geoxygene.datatools.Geodatabase)
   */
  @Override
  public ISurfaceBoundary boundary() {
    this.simplifyPrimitive();
    return this.primitive.boundary();
  }

  // //////////////////////////////////////////////////////////////////////
  // Méthodes "validate" /////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.spatial.geomcomp.ICompositeSurface#validate(double )
   */
  @Override
  public boolean validate(double tolerance) {
    /*
     * for (int i=0; i<generator.size()-1; i++) { GM_OrientableCurve oCurve1 =
     * (GM_OrientableCurve)generator.get(i); GM_Curve prim1 =
     * (GM_Curve)oCurve1.getPrimitive(); GM_OrientableCurve oCurve2 =
     * (GM_OrientableCurve)generator.get(i+1); GM_Curve prim2 =
     * (GM_Curve)oCurve2.getPrimitive(); DirectPosition pt1 = prim1.endPoint();
     * DirectPosition pt2 = prim2.startPoint(); if (!pt1.equals(pt2,tolerance))
     * return false; }
     */
    return true;
  }

  // //////////////////////////////////////////////////////////////////////
  // Méthodes privées pour usage interne /////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  /**
   * Calcule la primitive se self. MARCHE PAS
   * 
   * @param data
   */
  private void simplifyPrimitive() {
    /*
     * int n = generator.size(); if (n > 1) { GM_Surface prim =
     * (GM_Surface)this.primitive; prim = this.getGenerator(0); // clonage de la
     * primitive GM_Surface union = new GM_Surface(); for (int i=0;
     * i<prim.cardPatch(); i++) union.appendPatch(prim.getPatch(i)); union =
     * prim; for (int i=1; i<n; i++) { GM_Surface surf = new
     * GM_Surface(((GM_Surface)this.getGenerator(i)).getPrimitive()); union =
     * (GM_Surface)union.union(data,0.0000000001,surf); } }
     */
  }

  public double volume() {
    GM_CompositeSurface.logger.error("non implemented method");
    return 0;
  }

  @Override
  public Set<IComplex> getComplex() {
    GM_CompositeSurface.logger.error("non implemented method");
    return null;
  }

  @Override
  public int sizeComplex() {
    GM_CompositeSurface.logger.error("non implemented method");
    return 0;
  }

  /** Set de primitives constituant self. */
  protected Set<IGeometry> element = new HashSet<IGeometry>();

  @Override
  public void addElement(IPrimitive value) {
    this.element.add(value);
    value.getComplex().add(this);
  }

  @Override
  public void removeElement(IPrimitive value) {
    this.element.remove(value);
    value.getComplex().remove(this);
  }

  @Override
  public Set<IGeometry> getElement() {
    return this.element;
  }

  @Override
  public int sizeElement() {
    return this.element.size();
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
    this.element.remove(value);
    value.getElement().remove(this);
  }

  /** Les super-complexes constituant self. */
  protected Set<IComplex> superComplex = new HashSet<IComplex>();

  @Override
  public Set<IComplex> getSuperComplex() {
    return this.superComplex;
  }

  @Override
  public void addSuperComplex(IComplex value) {
    this.superComplex.add(value);
  }

  @Override
  public void removeSuperComplex(IComplex value) {
    this.superComplex.remove(value);
  }

  @Override
  public int sizeSuperComplex() {
    return this.superComplex.size();
  }

  @Override
  public boolean isMaximal() {
    return (this.sizeSuperComplex() == 0);
  }
}
