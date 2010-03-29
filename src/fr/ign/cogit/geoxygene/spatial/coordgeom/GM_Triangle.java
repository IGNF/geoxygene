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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.Arrays;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;

/**
 * 
 * @author Thierry Badard & Arnaud Braun & Brasebin Mickael
 * @version 1.0
 * 
 */

public class GM_Triangle extends GM_Polygon {


	public DirectPosition getCorners(int i) {
		DirectPositionList dpl = this.coord();
		return dpl.get(i);
	}

	public DirectPositionList getCorners() {
		return this.coord();
	}

	@Override
	public double area() {

		DirectPositionList dpl = this.coord();

		Vecteur v1 = new Vecteur(dpl.get(0), dpl.get(1));
		Vecteur v2 = new Vecteur(dpl.get(0), dpl.get(2));

		return Math.abs(0.5 * v1.prodVectoriel(v2).norme());

	}

	public int cardCorners() {
		DirectPositionList dpl = this.coord();
		return dpl.size() - 1;// car les géométries sont fermées
	}

	public GM_Triangle(DirectPosition dp1,DirectPosition dp2, DirectPosition dp3) {
		this(new GM_LineString(new DirectPositionList(Arrays.asList(dp1, dp2, dp3, dp1))));
	}
	public GM_Triangle(GM_LineString ls) {
		super(ls);
	}

}