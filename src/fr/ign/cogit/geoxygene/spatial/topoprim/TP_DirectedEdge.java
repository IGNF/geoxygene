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

import java.util.List;


/**
 * Brin topologique orienté. Supporte la classe TP_Edge pour les TP_Expression.
 * Dans notre implémentation, l'identifiant d'un TP_DirectedTopo est celui de sa primitive avec le signe de l'orientation.
 * EXPLIQUER QUE C'EST PAS PERSISTANT et que A PRIORI ca n'a pas de GEOMETRIE
 *
 * @author Thierry Badard, Arnaud Braun & Audrey Simon
 * @version 1.0
 * 
 */

public class TP_DirectedEdge extends TP_DirectedTopo {


	/////////////////////////////////////////////////////////////////////////////////////
	// constructeur /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur par défaut. */
	public TP_DirectedEdge() {
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// topo /////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Primitive de this. */
	protected TP_Edge topo;
	/** Primitive de this. */
	public TP_Edge topo () {
		return topo;
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// negate ///////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le TP_DirectedEdge d'orientation opposée. */
	public TP_DirectedEdge negate()  {
		if (orientation<0) return topo.proxy[0];
		return topo.proxy[1];
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// boundary /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie les TP_DirectedNode frontière du TP_DirectedEdge, structurés en TP_EdgeBoundary.
	 * Un peut lourd à utiliser, mieux d'utiliser directement getEndnode() et getStartnode().*/
	public TP_EdgeBoundary boundary()  {
		if (orientation == +1) return this.topo().boundary();
		// si le brin est négatif on retourne tout
		else if (orientation == -1) {
			TP_EdgeBoundary boundary = this.topo().boundary();
			TP_DirectedNode startNode = boundary.getStartnode().negate();
			TP_DirectedNode endNode = boundary.getEndnode().negate();
			TP_EdgeBoundary result = new TP_EdgeBoundary(endNode,startNode);
			return result;
		}
		else return null;
	}


	/** Renvoie directement le endNode (qui est orienté positivement). */
	public TP_DirectedNode endNode()  {
		TP_EdgeBoundary bdy = this.boundary();
		TP_DirectedNode theNode = bdy.getEndnode();
		return theNode;
	}


	/** Renvoie directement le startNode (qui est orienté négativemnt). */
	public TP_DirectedNode startNode()  {
		TP_EdgeBoundary bdy = this.boundary();
		TP_DirectedNode theNode = bdy.getStartnode();
		return theNode;
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// coboundary ///////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie les TP_DirectedFace associés au TP_DirectedEdge. */
	@SuppressWarnings("unchecked")
	public List coBoundary()  {
		if (orientation == +1) return this.topo().coBoundary();
		// si le brin est négatif on retourne tout
		else if (orientation == -1) {
			List coBoundary = this.topo().coBoundary();
			for (int i=0; i<coBoundary.size(); i++) {
				TP_DirectedFace directedFace1 = (TP_DirectedFace)coBoundary.get(i);
				TP_DirectedFace directedFace2 = directedFace1.negate();
				coBoundary.set(i,directedFace2);
			}
			return coBoundary;
		}
		else return null;
	}


	/** Renvoie la face gauche (orientée du même signe que le TP_DirectedEdge). */
	public TP_DirectedFace leftFace() {
		List<?> cobdy = this.coBoundary();
		if (cobdy.size() >0) {
			TP_DirectedFace dface1 = (TP_DirectedFace)cobdy.get(0);
			TP_DirectedFace dface2 = (TP_DirectedFace)cobdy.get(1);
			if (dface1.getOrientation() == this.getOrientation()) return dface1;
			return dface2;
		}
		return null;
	}


	/** Renvoie la face droite (orientée du signe opposé au TP_DirectedEdge). */
	public TP_DirectedFace rightFace() {
		List<?> cobdy = this.coBoundary();
		if (cobdy.size() >0) {
			TP_DirectedFace dface1 = (TP_DirectedFace)cobdy.get(0);
			TP_DirectedFace dface2 = (TP_DirectedFace)cobdy.get(1);
			if (dface1.getOrientation() == -this.getOrientation()) return dface1;
			return dface2;
		}
		return null;
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// brin suivant /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** La liste ordonnee dans le sens trigo des brins suivants. Les entrants sont orientés négativement,
       les sortants positivement. */
	@SuppressWarnings("unchecked")
	public List nextEdgesList()  {
		TP_DirectedNode endNode = this.endNode();
		List coBdy = endNode.coBoundary();

		// il faut les retourner car coBoundary renvoie des entrants + et des sortant - (on veut le contraire)
		for (int i=0; i<coBdy.size(); i++) {
			TP_DirectedEdge dirEdge = (TP_DirectedEdge)coBdy.get(i);
			TP_DirectedEdge dirEdgeNeg = dirEdge.negate();
			coBdy.set(i,dirEdgeNeg);
		}

		// on enleve this de la liste
		for (int i=0; i<coBdy.size(); i++) {
			TP_DirectedEdge dirEdge = (TP_DirectedEdge)coBdy.get(i);
			if (dirEdge.getId() == -1*this.getId()) {
				coBdy.remove(i);
				break;
			}

		}
		return coBdy;
	}


	/** Le brin orienté suivant (= celui partageant la même face gauche). */
	public TP_DirectedEdge nextEdge()  {
		TP_DirectedNode endNode = this.endNode();
		List<?> coBdy = endNode.coBoundary();
		// dans coBdy, comme ils sont orientes dans le sens trigo, c'est le brin juste avant this
		TP_DirectedEdge result = null;
		// cas particulier : pas de brin suivant reel. On renvoie l'oppose de this
		if (coBdy.size() == 0)result = this.negate();

		else {
			TP_DirectedEdge dirEdge = (TP_DirectedEdge)coBdy.get(0);
			if (dirEdge.equalsID(this)) result = (TP_DirectedEdge)coBdy.get(coBdy.size()-1);
			else
				for (int i=1; i<coBdy.size(); i++) {
					dirEdge = (TP_DirectedEdge)coBdy.get(i);
					if (dirEdge.equalsID(this)) {
						result = (TP_DirectedEdge)coBdy.get(i-1);
						break;
					}
				}
		}
		// il faut le retourner car coBoundary renvoie des entrants + et des sortant -
		if (result!=null) result = result.negate();
		return result;
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// brin precedent ///////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** La liste ordonnee dans le sens trigo des brins précédents. Les entrants sont orientés négativement,
       les sortants positivement. This n'appartient pas à la liste. */
	@SuppressWarnings("unchecked")
	public List previousEdgesList() {
		TP_DirectedNode startNode = this.startNode();
		List<TP_DirectedEdge> coBdy = startNode.coBoundary();

		// il faut les retourner car coBoundary renvoie des entrants - et des sortant +
		// car startNode est négatif
		for (int i=0; i<coBdy.size(); i++) {
			TP_DirectedEdge dirEdge = coBdy.get(i);
			TP_DirectedEdge dirEdgeNeg = dirEdge.negate();
			coBdy.set(i,dirEdgeNeg);
		}

		// on enleve this de la liste
		for (int i=0; i<coBdy.size(); i++) {
			TP_DirectedEdge dirEdge = coBdy.get(i);
			if (dirEdge.getId() == -1*this.getId()) {
				coBdy.remove(i);
				break;
			}
		}
		return coBdy;
	}


	/** Le brin orienté précédent (= celui partageant la même face gauche). */
	public TP_DirectedEdge previousEdge() {
		TP_DirectedNode startNode = this.startNode();
		List<?> coBdy = startNode.coBoundary();
		// dans coBdy, comme ils sont orientes dans le sens trigo, c'est le brin juste apres this
		TP_DirectedEdge result = null;

		// cas particulier : pas de brin precedent reel. On renvoie l'oppose de this
		if (coBdy.size() == 0)result = this.negate();

		else {
			TP_DirectedEdge dirEdge = (TP_DirectedEdge)coBdy.get(coBdy.size()-1);
			if (dirEdge.equalsID(this)) result = (TP_DirectedEdge)coBdy.get(0);
			else
				for (int i=0; i<coBdy.size()-1; i++) {
					dirEdge = (TP_DirectedEdge)coBdy.get(i);
					if (dirEdge.equalsID(this)) {
						result = (TP_DirectedEdge)coBdy.get(i+1);
						break;
					}
				}
		}
		// il faut le retourner car coBoundary renvoie des entrants - et des sortant +
		// (car startNode est negatif)
		if (result!=null) result = result.negate();
		return result;
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// cycle ////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Tous les TP_DirectedEdge partageant la même face gauche. */
	public TP_Ring cycle () {
		TP_Ring ring = new TP_Ring();
		TP_DirectedEdge suivant1 = (TP_DirectedEdge)this.clone();
		TP_DirectedEdge suivant2 = new TP_DirectedEdge(); // suivant2 a un identifiant 0 est une orientation 0
		while (!suivant2.equalsID(this)) {
			suivant2 = suivant1.nextEdge();
			suivant1 = suivant2;
			ring.addTerm(suivant1);
		}
		return ring;
	}


}
