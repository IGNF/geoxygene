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

package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Classe des triangles de la triangulation.
 * @author Bonin
 * @version 1.0
 */

public class TriangleDelaunay extends Face {
	public TriangleDelaunay() {
	}

	public TriangleDelaunay(NoeudDelaunay n1, NoeudDelaunay n2, NoeudDelaunay n3) {
		DirectPositionList dpl = new DirectPositionList();
		dpl.add(n1.getCoord());
		dpl.add(n2.getCoord());
		dpl.add(n3.getCoord());
		dpl.add(n1.getCoord());
		setCoord(dpl);
		Arc arc;
		List<?> arcs;
		Iterator<?> iterarcs;
		arcs = n1.arcs();
		iterarcs = arcs.iterator();
		while (iterarcs.hasNext()) {
			arc = (Arc) iterarcs.next();
			if ((arc.getNoeudIni() == n1) && (arc.getNoeudFin() == n2)) {
				arc.setFaceGauche(this);
				break;
			} else if ((arc.getNoeudIni() == n2) && (arc.getNoeudFin() == n1)) {
				arc.setFaceDroite(this);
				break;
			}
		}
		arcs = n2.arcs();
		iterarcs = arcs.iterator();
		while (iterarcs.hasNext()) {
			arc = (Arc) iterarcs.next();
			if ((arc.getNoeudIni() == n2) && (arc.getNoeudFin() == n3)) {
				arc.setFaceGauche(this);
				break;
			} else if ((arc.getNoeudIni() == n3) && (arc.getNoeudFin() == n2)) {
				arc.setFaceDroite(this);
				break;
			}
		}
		arcs = n3.arcs();
		iterarcs = arcs.iterator();
		while (iterarcs.hasNext()) {
			arc = (Arc) iterarcs.next();
			if ((arc.getNoeudIni() == n3) && (arc.getNoeudFin() == n1)) {
				arc.setFaceGauche(this);
				break;
			} else if ((arc.getNoeudIni() == n1) && (arc.getNoeudFin() == n3)) {
				arc.setFaceDroite(this);
				break;
			}
		}
	}

}