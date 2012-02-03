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
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;

/**
 * Agrégation de courbes orientées.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_MultiCurve<CurveType extends IOrientableCurve> extends
    GM_MultiPrimitive<CurveType> implements IMultiCurve<CurveType> {

  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length et non double
  @Override
  public double perimeter() {
    return this.length();
  }

  /** Constructeur par défaut. */
  public GM_MultiCurve() {
    this.element = new ArrayList<CurveType>(0);
  }

  /** Constructeur à partir d'un GM_CompositeCurve. */
  @SuppressWarnings("unchecked")
  public GM_MultiCurve(ICompositeCurve compCurve) {
    this.element = new ArrayList<CurveType>(compCurve.getGenerator().size());
    this.addAll((List<CurveType>) compCurve.getGenerator());
  }

  /** Constructeur à partir d'une liste de GM_Curve. */
  public GM_MultiCurve(List<CurveType> lCurve) {
    this.element = new ArrayList<CurveType>(lCurve.size());
    this.element.addAll(lCurve);

  }

  /**
   * Constructeur par copie.
   * @param geom geometrie à copier
   */
  public GM_MultiCurve(GM_MultiCurve<CurveType> geom) {
    this(geom.getList());
  }

  @Override
  public boolean isMultiCurve() {
    return true;
  }

  /** Longueur totale. */
  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Length.
  // code dans GM_Object
  /*
   * public double length() { return SpatialQuery.length(this); }
   */

  @SuppressWarnings("unchecked")
  @Override
  public Object clone() {
    GM_MultiCurve<CurveType> agg = new GM_MultiCurve<CurveType>();
    for (CurveType elt : this.element) {
      agg.add((CurveType) elt.clone());
    }
    return agg;
  }
}
