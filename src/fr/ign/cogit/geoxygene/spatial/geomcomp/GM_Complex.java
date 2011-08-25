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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.IComplex;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IComplexBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPrimitive;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * NON TESTE. Un complexe est un set de primitives géométriques dont les
 * intérieurs sont disjoints. De plus, si une primitive est dans un complexe,
 * alors il existe un set de primitives dans le complexe dont l'union en forme
 * la frontière.
 * <P>
 * ATTENTION : pour le moment, la contrainte qui impose que si un élément est
 * dans un complexe, sa frontière est dans un complexe, n'est pas implémentée.
 * <P>
 * A FAIRE AUSSI : mettre des itérateurs sur les listes
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Complex extends GM_Object implements IComplex {

  /** Set de primitives constituant self. */
  // protected Set<IGeometry> element = new HashSet<IGeometry>();
  @Override
  public Set<IGeometry> getElement() {
    return null;// this.element
  }
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
  public int sizeElement() {
    return this.getElement().size();
  }
  /** Les sous-complexes constituant self. */
  // protected Set<IComplex> subComplex = new HashSet<IComplex>();
  @Override
  public Set<IComplex> getSubComplex() {
    return null;// this.subComplex;
  }
  @Override
  public void addSubComplex(IComplex value) {
    this.getSubComplex().add(value);
    value.getSuperComplex().add(this);
    this.getElement().add(value);
    value.getElement().add(this);
  }
  @Override
  public void removeSubComplex(IComplex value) {
    this.getSubComplex().remove(value);
    value.getSuperComplex().remove(this);
    this.getElement().remove(value);
    value.getElement().remove(this);
  }
  @Override
  public int sizeSubComplex() {
    return this.getSubComplex().size();
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
  @Override
  public IComplexBoundary boundary() {
    return null;
  }
  @Override
  public IDirectPositionList coord() {
    IDirectPositionList dpl = new DirectPositionList();
    for (IGeometry o : this.getElement()) {
      dpl.addAll(o.coord());
    }
    return dpl;
  }
}
