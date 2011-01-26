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

package fr.ign.cogit.geoxygene.spatial.topoprim;

import java.util.ArrayList;
import java.util.List;

/**
 * Brin topologique (orientation positive).
 * <P>
 * L'operation "CoBoundary" redefinie sur TP_Object renvoie ici une liste de
 * TP_DirectedFace, indiquant quelles faces ont self pour frontiere. Cette liste
 * n'est pas ordonnee.
 * <P>
 * L'operation "Boundary" redefinie sur TP_Object renvoie le point initial du
 * brin (TP_DirectedNode negatif) et le point final (TP_DirectedNode positif) ;
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
  // hesitation sur le fait : proxy[0] = this ou proxy[0] = new
  // TP_DirectedEdge(id) + proxy[0].topo = this
  protected TP_DirectedEdge[] proxy;

  // ///////////////////////////////////////////////////////////////////////////////////
  // constructeur
  // /////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  public TP_Edge() {
    this.orientation = +1;
    this.proxy = new TP_DirectedEdge[2];
    this.proxy[0] = this;
    this.topo = this;
    this.proxy[1] = new TP_DirectedEdge();
    this.proxy[1].topo = this;
    this.proxy[1].orientation = -1;
  }

  // redefinition pour affecter un bon id au proxy negatif
  @Override
  public void setId(int Id) {
    super.setId(Id);
    this.proxy[1].setId(-Id);
    if (Id < 0) {
      System.out
          .println("TP_Edge::setId(id) : L'identifiant doit être positif"); //$NON-NLS-1$
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // asTP_DirectedTopo()
  // //////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie le TP_DirectedEdge d'orientation "sign". "sign" doit valoir +1 ou
   * -1, sinon renvoie null.
   */
  public TP_DirectedEdge asTP_DirectedTopo(int sign) {
    if (sign == +1) {
      return this.proxy[0];
    } else if (sign == -1) {
      return this.proxy[1];
    } else {
      System.out
          .println("TP_Edge::asTP_DirectedTopo(sign) : Passer +1 ou -1 en paramètre."); //$NON-NLS-1$
      return null;
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // container
  // ////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /** Solide dans lequel est inclus this, pour les brins isoles. */
  public TP_Solid container;

  public TP_Solid getContainer() {
    return this.container;
  }

  public void setContainer(TP_Solid Container) {
    this.container = Container;
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // boundary
  // /////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /** Noeud initial. */
  public TP_Node startnode;

  public TP_Node getStartnode() {
    return this.startnode;
  }

  public void setStartnode(TP_Node StartNode) {
    if (StartNode != null) {
      this.startnode = StartNode;
      this.startnodeID = StartNode.getId();
      if (!StartNode.getSortant().contains(this)) {
        StartNode.addSortant(this);
      }
    } else {
      this.startnode = null;
      this.startnodeID = 0;
    }
  }

  // pour le mapping avec OJB
  public int startnodeID;

  public int getStartnodeID() {
    return this.startnodeID;
  }

  public void setStartnodeID(int StartnodeID) {
    this.startnodeID = StartnodeID;
  }

  /** Noeud final. */
  public TP_Node endnode;

  public TP_Node getEndnode() {
    return this.endnode;
  }

  public void setEndnode(TP_Node EndNode) {
    if (EndNode != null) {
      this.endnode = EndNode;
      this.endnodeID = EndNode.getId();
      if (!EndNode.getEntrant().contains(this)) {
        EndNode.addEntrant(this);
      }
    } else {
      this.endnode = null;
      this.endnodeID = 0;
    }
  }

  // pour le mapping avec OJB
  public int endnodeID;

  public int getEndnodeID() {
    return this.endnodeID;
  }

  public void setEndnodeID(int EndnodeID) {
    this.endnodeID = EndnodeID;
  }

  /**
   * Renvoie les TP_DirectedNode frontiere du TP_Edge, structure en
   * TP_EdgeBoundary. Un peut lourd a utiliser, mieux d'utiliser endNode() et
   * startNode().
   */
  @Override
  public TP_EdgeBoundary boundary() {
    return new TP_EdgeBoundary(this.startnode.asTP_DirectedTopo(-1),
        this.endnode.asTP_DirectedTopo(+1));
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // coBoundary
  // ///////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /**
   * Les TP_DirectedFace associes au TP_Edge. La face gauche est orientee
   * positivement, la face droite est orientee negativement.
   */
  @Override
  public List<?> coBoundary() {
    List<TP_DirectedFace> result = new ArrayList<TP_DirectedFace>();
    if (this.leftface != null) {
      result.add(this.leftface.asTP_DirectedTopo(+1));
    }
    if (this.rightface != null) {
      result.add(this.rightface.asTP_DirectedTopo(-1));
    }
    return result;
  }

  /** Face gauche. */
  public TP_Face leftface;

  public TP_Face getLeftface() {
    return this.leftface;
  }

  public void setLeftface(TP_Face Leftface) {
    if (Leftface == null) {
      this.leftface = null;
      this.leftfaceID = 0;
    } else if (Leftface.getId() == -1) {
      this.leftface = null;
    } else {
      this.leftface = Leftface;
      this.leftfaceID = Leftface.getId();
      if (!Leftface.getLeft().contains(this)) {
        Leftface.addLeft(this);
      }
    }
  }

  // pour le mapping avec OJB
  // on affecte -1 pour avoir une valeur par defaut non nulle
  // il faut un objet topo avec un id = -1 (TP_Face) dans la table TP_Object;
  // cela accelere le chargement
  public int leftfaceID = -1;

  public int getLeftfaceID() {
    return this.leftfaceID;
  }

  public void setLeftfaceID(int LeftfaceID) {
    this.leftfaceID = LeftfaceID;
  }

  /** Face droite. */
  public TP_Face rightface;

  public TP_Face getRightface() {
    return this.rightface;
  }

  public void setRightface(TP_Face Rightface) {
    if (Rightface == null) {
      this.rightface = null;
      this.rightfaceID = 0;
    } else if (Rightface.getId() == -1) {
      this.rightface = null;
    } else {
      this.rightface = Rightface;
      this.rightfaceID = Rightface.getId();
      if (!Rightface.getRight().contains(this)) {
        Rightface.addRight(this);
      }
    }
  }

  // pour le mapping avec OJB
  // on affecte -1 pour avoir une valeur par defaut non nulle
  // il faut un objet topo avec un id = -1 (TP_Face) dans la table TP_Object;
  // cela accelere le chargement
  public int rightfaceID = -1;

  public int getRightfaceID() {
    return this.rightfaceID;
  }

  public void setRightfaceID(int RightfaceID) {
    this.rightfaceID = RightfaceID;
  }

}
