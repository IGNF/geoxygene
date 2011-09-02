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

import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositePoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * Complexe contenant un et un seul GM_Point. Cette classe ne sert pas a grand
 * chose mais elle a été mise pour homogénéiser avec les GM_Composite des autres
 * types de primitives.
 * <P>
 * Utilisation : un GM_CompositePoint se construit exclusivement à partir d'un
 * GM_Point.
 * 
 * <P>
 * ATTENTION : normalement, il faudrait remplir le set "element" (contrainte :
 * toutes les primitives du generateur sont dans le complexe). Ceci n'est pas
 * implémenté pour le moment.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @author julien Gaffuri
 * @version 1.1
 * 
 */

public class GM_CompositePoint extends GM_Composite implements ICompositePoint {
  /**
   * Le GM_Point constituant self. C'est une référence (un nouvel objet n'est
   * pas construit).
   */
  protected IPoint generator;

  @Override
  public IPoint getGenerator() {
    return this.generator;
  }

  @Override
  public void setGenerator(IPoint value) {
    this.generator = value;
    this.position = new DirectPosition(value.getPosition().getCoordinate());
  }

  @Override
  public int sizeGenerator() {
    if (this.generator == null) {
      return 0;
    }
    return 1;
  }

  /**
   * DirectPosition du point (DirectPosition étant la classe stockant les
   * coordonnées).
   */
  protected IDirectPosition position;

  @Override
  public IDirectPosition getPosition() {
    this.position = this.generator.getPosition();
    return this.position;
  }

  /** Constructeur par défaut. */
  public GM_CompositePoint() {
    this.position = new DirectPosition();
  }

  /** Constructeur à partir d'un GM_Point. */
  public GM_CompositePoint(IPoint thePoint) {
    this.generator = thePoint;
    this.position = new DirectPosition(thePoint.getPosition().getCoordinate());
  }

  @Override
  public void setPosition(IDirectPosition pos) {
    this.getGenerator().setPosition(pos);
  }

  @Override
  public Set<IComplex> getComplex() {
    return this.getGenerator().getComplex();
  }

  @Override
  public int sizeComplex() {
    return this.getGenerator().sizeComplex();
  }
}
