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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * Noeud topologique (orientation positive).
 * <P>
 * L'operation "CoBoundary" redefinie sur TP_Object renvoie ici un set de
 * TP_DirectedEdge, oriente positivement pour les entrants, negativement pour
 * les sortants.
 * <P>
 * L'operation "Boundary" sur TP_Object renvoie null.
 * <P>
 * Dans le modele, cette classe herite directement de TP_Primitive (double
 * heritage TP_Primitive / TP_DirectedNode). Ceci n'a pas ete repris en java,
 * mais l'heritage se retrouve par l'intermediaire de TP_DirectedTopo.
 * 
 * A EXPLIQUER : la structure de graphe
 * 
 * @author Thierry Badard, Arnaud Braun & Audrey Simon
 * @version 1.0
 * 
 */

public class TP_Node extends TP_DirectedNode {

  /** Les 2 primitives orientees de this. */
  // hesitation sur le fait : proxy[0] = this ou proxy[0] = new
  // TP_DirectedNode(id) avec proxy[0].topo = this ?
  protected TP_DirectedNode[] proxy;

  // ///////////////////////////////////////////////////////////////////////////////////
  // constructeur
  // /////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  public TP_Node() {
    this.orientation = +1;
    this.proxy = new TP_DirectedNode[2];
    this.proxy[0] = this;
    this.topo = this;
    this.proxy[1] = new TP_DirectedNode();
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
          .println("TP_Node::setId(id) : L'identifiant doit être positif"); //$NON-NLS-1$
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // asTP_DirectedTopo()
  // //////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie le TP_DirectedNode d'orientation "sign". "sign" doit valoir +1 ou
   * -1, sinon renvoie null.
   */
  public TP_DirectedNode asTP_DirectedTopo(int sign) {
    if (sign == +1) {
      return this.proxy[0];
    } else if (sign == -1) {
      return this.proxy[1];
    } else {
      System.out
          .println("TP_Node::asTP_DirectedTopo(sign) : Passer +1 ou -1 en paramètre."); //$NON-NLS-1$
      return null;
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // container
  // ////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /** Face dans laquelle est incluse this, pour les noeuds isoles. */
  public TP_Face container;

  public TP_Face getContainer() {
    return this.container;
  }

  public void setContainer(TP_Face Container) {
    if (Container == null) {
      this.container = null;
      this.containerID = 0;
    } else if (Container.getId() == -1) {
      this.container = null;
    } else {
      this.container = Container;
      this.containerID = Container.getId();
      if (!Container.getIsolated().contains(this)) {
        Container.addIsolated(this);
      }
    }
  }

  // pour le mapping avec OJB
  // on affecte -1 pour avoir une valeur par defaut non nulle
  // il faut un objet topo avec un id = -1 (TP_Face) dans la table TP_Object;
  // cela accelere le chargement
  public int containerID = -1;

  public int getContainerID() {
    return this.containerID;
  }

  public void setContainerID(int ContainerID) {
    this.containerID = ContainerID;
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // coBoundary
  // ///////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie les TP_DirectedEdge qui ont self pour frontiere, orientes
   * positivement pour les entrants, negativement pour les sortants. La liste
   * est ordonnee dans le sens trigo.
   */
  @Override
  @SuppressWarnings("unchecked")
  public List coBoundary() {
    List result = new ArrayList();
    Iterator i;
    i = this.entrant.iterator();
    while (i.hasNext()) {
      TP_Edge edge = (TP_Edge) i.next();
      result.add(edge.asTP_DirectedTopo(+1));
    }
    i = this.sortant.iterator();
    while (i.hasNext()) {
      TP_Edge edge = (TP_Edge) i.next();
      result.add(edge.asTP_DirectedTopo(-1));
    }
    if (result.size() > 1) {
      this.ordonne(result);
    }

    return result;
  }

  /** Les TP_Edge entrants dans ce noeud. */
  // c'est en collection et pas en liste pour permettre le lazy loading Castor
  public Collection<TP_Edge> entrant = new ArrayList<TP_Edge>();

  public Collection<TP_Edge> getEntrant() {
    return this.entrant;
  }

  public void addEntrant(TP_Edge edge) {
    if (edge != null) {
      this.entrant.add(edge);
      if (edge.getEndnode() != this) {
        edge.setEndnode(this);
      }
    }
  }

  /** Les TP_Edge sortants dans ce noeud. */
  public Collection<TP_Edge> sortant = new ArrayList<TP_Edge>();

  public Collection<TP_Edge> getSortant() {
    return this.sortant;
  }

  public void addSortant(TP_Edge edge) {
    if (edge != null) {
      this.sortant.add(edge);
      if (edge.getStartnode() != this) {
        edge.setStartnode(this);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // boundary
  // /////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /** Renvoie null. */
  @Override
  public TP_Boundary boundary() {
    return null;
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // methodes privees pour ordonner la coboundary
  // /////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /*
   * ordonne les edges dans le sens trigo (ordonne l'angle des 2 premiers points
   * de la geometrie dans le sens croissant)
   */
  private void ordonne(List<TP_DirectedEdge> theDirEdges) {
    double[] listOfAngles = new double[theDirEdges.size()];
    for (int i = 0; i < theDirEdges.size(); i++) {
      TP_DirectedEdge diredge = theDirEdges.get(i);
      // attention a l'orientation de la geometrie si on a un directed edge
      // negatif
      // directed negatif => sortant : OK
      // directed positif => entrant : il faut le retourner
      ILineString geom_ = null;
      if (diredge.getId() <= 0) {
        geom_ = (ILineString) diredge.topo().getGeom();
      } else {
        geom_ = (ILineString) ((ILineString) (diredge.topo().getGeom()))
            .reverse();
      }
      listOfAngles[i] = this.calculeAngle(geom_);
    }

    // on ordonne la liste en fonction des valeurs des angles
    for (int i = 1; i < listOfAngles.length; i++) {
      double angle = listOfAngles[i];
      for (int j = 0; j < i; j++) {
        if (angle < listOfAngles[j]) {
          // on decale dans la liste des dir edges
          theDirEdges.add(j, theDirEdges.get(i));
          theDirEdges.remove(i + 1);
          // on decale dans la liste des angles
          for (int k = i; k > j; k--) {
            listOfAngles[k] = listOfAngles[k - 1];
          }
          listOfAngles[j] = angle;
          break;
        }
      }
    }
  }

  /* calcule l'angle forme par les 2 PREMIERS points de la ligne (dans [0, 2.pi] */
  private double calculeAngle(ILineString line) {
    IDirectPosition pt1 = line.getControlPoint().get(0);
    IDirectPosition pt2 = line.getControlPoint().get(1);
    double deltaX = pt2.getX() - pt1.getX();
    double deltaY = pt2.getY() - pt1.getY();
    if (deltaX > 0 && deltaY >= 0) {
      return Math.atan(deltaY / deltaX);
    } else if (deltaX < 0 && deltaY >= 0) {
      return (Math.atan(deltaY / deltaX) + Math.PI);
    } else if (deltaX < 0 && deltaY <= 0) {
      return (Math.atan(deltaY / deltaX) + Math.PI);
    } else if (deltaX > 0 && deltaY <= 0) {
      return (Math.atan(deltaY / deltaX) + 2. * Math.PI);
    } else if (deltaX == 0 && deltaY >= 0) {
      return (Math.PI / 2.);
    } else {
      /* if (deltaX==0 && deltaY<=0) */return (Math.PI / (-2.));
    }
  }

}
