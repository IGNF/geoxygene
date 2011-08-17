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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolidBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * NON UTILISE. Complexe ayant toutes les propriétes géométriques d'un solide.
 * C'est un set de solides (GM_Solid) partageant des surfaces communes. Hérite
 * de GM_Solid, mais le lien n'apparaît pas explicitement (problème de double
 * héritage en java). Les méthodes et attributs ont été reportés.
 * 
 * <P>
 * ATTENTION : normalement, il faudrait remplir le set "element" (contrainte :
 * toutes les primitives du generateur sont dans le complexe). Ceci n'est pas
 * implémenté pour le moment.
 * <P>
 * A FAIRE AUSSI : iterateur sur "generator"
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
public class GM_CompositeSolid extends GM_Solid implements ICompositeSolid {
  private static Logger logger = Logger.getLogger(GM_CompositeSolid.class
      .getName());
 
  // Attribut "generator" et méthodes pour le traiter ////////////////////

  /** Les GM_Solid constituant self. */
  protected List<ISolid> generator;

  /** Renvoie la liste des GM_Solid */
  @Override
  public List<ISolid> getGenerator() {
    return this.generator;
  }

  /** Renvoie le GM_Solid de rang i */
  // public GM_Solid getGenerator (int i) {return
  // (GM_Solid)this.generator.get(i);}

  /**
   * Affecte un GM_Solid au rang i. Attention : aucun contrôle de cohérence
   * n'est effectué.
   */
  // protected void setGenerator (int i, GM_Solid value) {this.generator.set(i,
  // value);}

  /**
   * Ajoute un GM_Solid en fin de liste. Attention : aucun contrôle de cohérence
   * n'est effectué.
   */
  // protected void addGenerator (GM_Solid value) {this.generator.add(value);}

  /**
   * Ajoute un GM_Solid au rang i. Attention : aucun contrôle de cohérence n'est
   * effectué.
   */
  // protected void addGenerator (int i, GM_Solid value) {this.generator.add(i,
  // value);}

  /**
   * Efface le (ou les) GM_Solid passé en paramètre. Attention : aucun contrôle
   * de cohérence n'est effectué.
   */
  /*
   * protected void removeGenerator (GM_Solid value) throws Exception { if
   * (this.generator.size() == 1) throw new Exception ( "Dr Cogit - error 4.001"
   * ); else this.generator.remove(value); }
   */

  /**
   * Efface le GM_Solid de rang i. Attention : aucun contrôle de cohérence n'est
   * effectué.
   */
  protected void removeGenerator(int i) throws Exception {
    if (this.getGenerator().size() == 1) {
      throw new Exception("Il n'y a qu'un objet dans l'association.");
    }
    this.generator.remove(i);
  }

  @Override
  public int sizeGenerator() {
    return this.getGenerator().size();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////
  // Méthodes héritées de GM_Solid (héritage simulé)
  // //////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////

  // Dans la norme, le résultat est de type Area.
  @Override
  public double area() {
    return 0.0;
  }

  // Dans la norme, le résultat est de type Volume.
  @Override
  public double volume() {
    return 0.0;
  }

  /**
   * NON IMPLEMENTE (Renvoie null). Redéfinition de l'opérateur "boundary" sur
   * GM_Object. Renvoie une GM_SolidBoundary, c'est-à-dire un shell extérieur et
   * éventuellement un (des) shell(s) intérieur(s).
   */
  // public GM_SolidBoundary boundary() {return null;}

  // ///////////////////////////////////////////////////////////////////////////////////////
  // Constructeurs
  // ////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////////

  /** Constructeur par défaut. */
  public GM_CompositeSolid() {
    this.generator = new ArrayList<ISolid>();
  }

  @Override
  public ISolidBoundary boundary() {
    GM_CompositeSolid.logger.error("non implemented method");
    return null;
  }

  @Override
  public ArrayList<IOrientableSurface> getFacesList() {
    GM_CompositeSolid.logger.error("non implemented method");
    return null;
  }

  @Override
  public Set<IComplex> getComplex() {
    GM_CompositeSolid.logger.error("non implemented method");
    return null;
  }

  @Override
  public int sizeComplex() {
    GM_CompositeSolid.logger.error("non implemented method");
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

  /** Constructeur à partir d'un GM_Solid. */
  /*
   * public GM_CompositeSolid (GM_Solid theSolid) { generator = new ArrayList();
   * generator.add(theSolid); }
   */
}
