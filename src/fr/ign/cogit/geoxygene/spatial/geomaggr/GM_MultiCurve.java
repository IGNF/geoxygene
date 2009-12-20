/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

/**
 * Agrégation de courbes orientées.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_MultiCurve<CurveType extends GM_OrientableCurve> extends GM_MultiPrimitive<CurveType> {
	/** Périmètre totale. */
	// Dans la norme, ceci est un attribut et non une méthode.
	// Dans la norme, cet attribut est de type Length et non double
	public double perimeter() {return this.length();}
	/** Constructeur par défaut. */
	public GM_MultiCurve() {this.element = new ArrayList<CurveType>();}
	/** Constructeur à partir d'un GM_CompositeCurve. */
	@SuppressWarnings("unchecked")
	public GM_MultiCurve(GM_CompositeCurve compCurve) {
		this.element = new ArrayList<CurveType>();
		this.addAll((List<CurveType>) compCurve.getGenerator());
	}
	/** Constructeur à partir d'une liste de GM_Curve. */
	@SuppressWarnings("unchecked")
	public GM_MultiCurve(ArrayList<GM_Curve> lCurve) {
		this.element = new ArrayList<CurveType>();
		this.element.addAll((Collection<? extends CurveType>) lCurve);

	}
	@Override
	public boolean isMultiCurve() {return true;}
	/** Longueur totale. */
	// Dans la norme, ceci est un attribut et non une méthode.
	// Dans la norme, cet attribut est de type Length.
	// code dans GM_Object
	/*   public double length()  {
        return SpatialQuery.length(this);
    }*/
}
