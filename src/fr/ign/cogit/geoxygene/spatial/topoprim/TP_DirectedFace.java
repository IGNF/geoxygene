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

package fr.ign.cogit.geoxygene.spatial.topoprim;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;


/**
 * Face topologique orientée. Supporte la classe TP_Face pour les TP_Expression.
 * Dans notre implémentation, l'identifiant d'un TP_DirectedTopo est celui 
 * de sa primitive avec le signe de l'orientation.
 * EXPLIQUER QUE C'EST PAS PERISTANT et que A PRIORI ca n'a pas de GEOMETRIE *
 *
 * @author Thierry Badard, Arnaud Braun & Audrey Simon
 * @version 1.0
 * 
 */

public class TP_DirectedFace extends TP_DirectedTopo {

	/////////////////////////////////////////////////////////////////////////////////////
	// constructeur /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur par défaut. */
	public TP_DirectedFace() {}

	/////////////////////////////////////////////////////////////////////////////////////
	// topo() ///////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Primitive de this. */
	protected TP_Face topo;
	/** Primitive de this. */
	public TP_Face topo () {return this.topo;}

	/////////////////////////////////////////////////////////////////////////////////////
	// negate() /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le TP_DirectedFace d'orientation opposée. */
	public TP_DirectedFace negate()  {
		if (this.orientation<0) return this.topo.proxy[0];
		return this.topo.proxy[1];
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// boundary() ///////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie les TP_DirectedEdge associés au TP_DirectedFace, structurés en TP_FaceBoundary. */
	public TP_FaceBoundary boundary()  {
		TP_FaceBoundary result = null;

		if (this.orientation == +1) return this.topo().boundary();

		TP_FaceBoundary boundary = this.topo().boundary();
		TP_Ring ring = boundary.getExterior();
		int n = ring.sizeTerm();
		List<TP_DirectedEdge> theEdges = new ArrayList<TP_DirectedEdge>();
		for (int i=0; i<n; i++) {
			TP_DirectedEdge edge = (TP_DirectedEdge)ring.getTerm(n-i-1);
			edge = edge.negate();
			theEdges.add(edge);
		}
		try {
			TP_Ring ext = new TP_Ring(theEdges);
			result = new TP_FaceBoundary(ext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (boundary.sizeInterior() > 0)
			for (int j=0; j<boundary.sizeInterior(); j++) {
				ring = boundary.getInterior(j);
				n = ring.sizeTerm();
				theEdges = new ArrayList<TP_DirectedEdge>();
				for (int i=0; i<n; i++) {
					TP_DirectedEdge edge = (TP_DirectedEdge)ring.getTerm(n-i-1);
					edge = edge.negate();
					theEdges.add(edge);
				}
				try {
					TP_Ring inte = new TP_Ring(theEdges);
					if (result!=null) result.addInterior(inte);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		return result;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// coBoundary() /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** non implémenté (renvoie null). */
	public List<TP_Object> coBoundary()  {return null;}

}
