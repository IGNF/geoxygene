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

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;

/**
 * Agrégation de solides.
 * 
 * @author Thierry Badard & Arnaud Braun & Mickael Brasebin
 * @version 1.0
 * 
 */
public class GM_MultiSolid<SolidType extends ISolid> extends
    GM_MultiPrimitive<SolidType> implements IMultiSolid<SolidType> {
  static Logger logger = Logger.getLogger(GM_MultiSolid.class.getName());

  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Volume et non double
  @Override
  public double volume() {
    GM_MultiSolid.logger
        .error("Non implémentée, utiliser : return CalculSansJava3D.CalculVolume(this); (renvoie 0.0)");
    return 0.0;
  }

  // Dans la norme, ceci est un attribut et non une méthode.
  // Dans la norme, cet attribut est de type Area et non double
  @Override
  public double area() {
    GM_MultiSolid.logger
        .error("Non implémentée, utiliser : return CalculSansJava3D.CalculAire(this); (renvoie 0.0)"); //$NON-NLS-1$
    return 0.0;
  }

  /** Constructeur par défaut. */
  public GM_MultiSolid() {
    this.element = new ArrayList<SolidType>(0);
  }

  /** Constructeur à partir d'une liste de GM_Solid. */
  public GM_MultiSolid(List<SolidType> lOS) {
    this.element = new ArrayList<SolidType>(lOS.size());
    this.element.addAll(lOS);
  }

  @Override
  public Object clone() {
    GM_MultiSolid<ISolid> agg = new GM_MultiSolid<ISolid>();
    for (ISolid elt : this.element) {
      agg.add((ISolid) elt.clone());
    }
    return agg;
  }
}
