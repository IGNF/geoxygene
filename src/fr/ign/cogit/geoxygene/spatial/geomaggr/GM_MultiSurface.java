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

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;

/**
 * Agrégation de surfaces orientées.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_MultiSurface<GeomType extends IOrientableSurface> extends
    GM_MultiPrimitive<GeomType> implements IMultiSurface<GeomType> {

  /** Aire totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Area et non double
  // code dans GM_Object
  @Override
  public double area() {
    double area = 0.0;
    for (GeomType geom : this) {
      area += geom.area();
    }
    return area;
  }

  /** Périmètre totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length et non double
  @Override
  public double perimeter() {
    return this.length();
  }

  @Override
  @SuppressWarnings("unchecked")
  public IMultiSurface<GeomType> homogeneise() {
    return (IMultiSurface<GeomType>) this.buffer(0);
  }

  /** Constructeur par défaut. */
  public GM_MultiSurface() {
    this.element = new ArrayList<GeomType>();
  }

  /** Constructeur à partir d'un GM_CompositeSurface. */
  @SuppressWarnings("unchecked")
  public GM_MultiSurface(ICompositeSurface compSurf) {
    this.element = new ArrayList<GeomType>();
    this.addAll((List<GeomType>) compSurf.getGenerator());
  }

  /** Constructeur à partir d'une liste de GM_OrientableSurface. */
  @SuppressWarnings("unchecked")
  public GM_MultiSurface(List<IOrientableSurface> lOS) {
    this.element = new ArrayList<GeomType>();
    this.element.addAll((Collection<? extends GeomType>) lOS);
  }

  /** Constructeur par copie. */
  public GM_MultiSurface(GM_MultiSurface<GeomType> multiSurface) {
    this((ICompositeSurface) multiSurface.getList());
  }

  @Override
  public boolean isMultiSurface() {
    return true;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Object clone() {
    GM_MultiSurface<GeomType> agg = new GM_MultiSurface<GeomType>();
    for (GeomType elt : this.element) {
      agg.add((GeomType) elt.clone());
    }
    return agg;
  }
  
}
