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


/**
 * Brin topologique (orientation positive).
 * <P> L'operation "CoBoundary" redefinie sur TP_Object renvoie ici une liste de TP_DirectedFace, indiquant quelles faces ont self pour frontiere.
 * Cette liste n'est pas ordonnee.
 * <P> L'operation "Boundary" redefinie sur TP_Object renvoie le point initial du brin (TP_DirectedNode negatif) et le point final (TP_DirectedNode positif) ;
 * Ces points sont structures en TP_EdgeBoundary.
 *
 * EXPLIQUER la structure de graphe
 * 
 * @author Thierry Badard, Arnaud Braun & Audrey Simon
 * @version 1.0
 * 
 */


public class TP_Edge extends TP_DirectedEdge {


	/** Les 2 primitives orientees de this. */
	// hesitation sur le fait : proxy[0] = this ou proxy[0] = new TP_DirectedEdge(id) + proxy[0].topo = this
	protected TP_DirectedEdge[] proxy;



	/////////////////////////////////////////////////////////////////////////////////////
	// constructeur /////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	public TP_Edge () {
		orientation = +1;
		proxy = new TP_DirectedEdge[2];
		proxy[0] = this;
		topo = this;
		proxy[1] = new TP_DirectedEdge();
		proxy[1].topo = this;
		proxy[1].orientation = -1;
	}

	// redefinition pour affecter un bon id au proxy negatif
	@Override
	public void setId(int Id) {
		super.setId(Id);
		proxy[1].setId(-Id);
		if (Id<0) System.out.println("TP_Edge::setId(id) : L'identifiant doit être positif");
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// asTP_DirectedTopo() //////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le TP_DirectedEdge d'orientation "sign". "sign" doit valoir +1 ou -1, sinon renvoie null. */
	public TP_DirectedEdge asTP_DirectedTopo(int sign)  {
		if (sign == +1) return proxy[0];
		else if (sign == -1) return proxy[1];
		else {
			System.out.println("TP_Edge::asTP_DirectedTopo(sign) : Passer +1 ou -1 en paramètre.");
			return null;
		}
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// container ////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Solide dans lequel est inclus this, pour les brins isoles. */
	public TP_Solid container;
	public TP_Solid getContainer() {return container;}
	public void setContainer(TP_Solid Container) {container= Container;}



	/////////////////////////////////////////////////////////////////////////////////////
	// boundary /////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Noeud initial. */
	public TP_Node startnode;
	public TP_Node getStartnode() {return startnode;}
	public void setStartnode(TP_Node StartNode) {
		if (StartNode != null) {
			this.startnode = StartNode;
			this.startnodeID = StartNode.getId();
			if (!StartNode.getSortant().contains(this))
				StartNode.addSortant(this);
		} else {
			startnode = null;
			startnodeID = 0;
		}
	}

	// pour le mapping avec OJB
	public int startnodeID;
	public int getStartnodeID() {return startnodeID; }
	public void setStartnodeID(int StartnodeID) {startnodeID = StartnodeID;}

	/** Noeud final. */
	public TP_Node endnode;
	public TP_Node getEndnode() {return endnode;}
	public void setEndnode(TP_Node EndNode) {
		if (EndNode != null) {
			this.endnode = EndNode;
			this.endnodeID = EndNode.getId();
			if (!EndNode.getEntrant().contains(this))
				EndNode.addEntrant(this);
		} else {
			endnode = null;
			endnodeID = 0;
		}
	}

	// pour le mapping avec OJB
	public int endnodeID;
	public int getEndnodeID() {return endnodeID; }
	public void setEndnodeID(int EndnodeID) {endnodeID = EndnodeID;}

	/** Renvoie les TP_DirectedNode frontiere du TP_Edge, structure en TP_EdgeBoundary.
	 * Un peut lourd a utiliser, mieux d'utiliser endNode() et startNode().*/
	@Override
	public TP_EdgeBoundary boundary() {
		return new TP_EdgeBoundary(this.startnode.asTP_DirectedTopo(-1),this.endnode.asTP_DirectedTopo(+1));
	}



	/////////////////////////////////////////////////////////////////////////////////////
	// coBoundary ///////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	/** Les TP_DirectedFace associes au TP_Edge. La face gauche est orientee positivement,
       la face droite est orientee negativement. */
	@Override
	public List<?> coBoundary()  {
		List<TP_DirectedFace> result = new ArrayList<TP_DirectedFace>();
		if (leftface != null) result.add(this.leftface.asTP_DirectedTopo(+1));
		if (rightface != null) result.add(this.rightface.asTP_DirectedTopo(-1));
		return result;
	}

	/** Face gauche. */
	public TP_Face leftface;
	public TP_Face getLeftface() {return leftface;}
	public void setLeftface(TP_Face Leftface) {
		if (Leftface==null) {
			leftface = null;
			leftfaceID = 0;
		} else if (Leftface.getId() == -1) leftface = null;
		else {
			this.leftface = Leftface;
			this.leftfaceID = Leftface.getId();
			if (!Leftface.getLeft().contains(this))
				Leftface.addLeft(this);
		}
	}

	// pour le mapping avec OJB
	// on affecte  -1 pour avoir une valeur par defaut non nulle
	// il faut un objet topo avec un id = -1 (TP_Face) dans la table TP_Object;
	// cela accelere le chargement
	public int leftfaceID = -1;
	public int getLeftfaceID() {return leftfaceID;}
	public void setLeftfaceID(int LeftfaceID) {leftfaceID = LeftfaceID;}

	/** Face droite. */
	public TP_Face rightface;
	public TP_Face getRightface() {return rightface;}
	public void setRightface(TP_Face Rightface) {
		if (Rightface == null) {
			rightface = null;
			rightfaceID = 0;
		} else if (Rightface.getId() == -1) rightface = null;
		else {
			this.rightface = Rightface;
			this.rightfaceID = Rightface.getId();
			if (!Rightface.getRight().contains(this))
				Rightface.addRight(this);
		}
	}

	// pour le mapping avec OJB
	// on affecte  -1 pour avoir une valeur par defaut non nulle
	// il faut un objet topo avec un id = -1 (TP_Face) dans la table TP_Object;
	// cela accelere le chargement
	public int rightfaceID = -1;
	public int getRightfaceID() {return rightfaceID;}
	public void setRightfaceID(int RightfaceID) {rightfaceID = RightfaceID;}

}
