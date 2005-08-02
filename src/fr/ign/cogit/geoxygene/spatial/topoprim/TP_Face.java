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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
  * Face topologique (orientation positive).
  * <P> L'operation "CoBoundary" redefinie sur TP_Object renvoie ici une sequence de TP_DirectedSolid, indiquant quels solides ont self pour frontiere. 
  * Cette operation est aussi une association. Ceci n'est pas implemente.
  * <P> L'operation "Boundary" redefinie sur TP_Object renvoie une liste de TP_DirectedEdge avec les orientations adequates. Cette liste est structuree en TP_FaceBoundary.
  *
  * EXPLIQUER LA STRUCTURE DE GRAPHE
  * A REVOIR POUR LES TROUS (ne pas utliser le container)
  *
  * @author Thierry Badard, Arnaud Braun & Audrey Simon
  * @version 1.0
  * 
  */

public class TP_Face extends TP_DirectedFace {
   
    
     /** Les 2 primitives orientees de this. */
    // hesitation sur le fait : proxy[0] = this ou proxy[0] = new TP_DirectedFace(id) + proxy[0].topo = this        
    protected TP_DirectedFace[] proxy;
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // constructeur /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////            
    public TP_Face () {
       orientation = +1;
       proxy = new TP_DirectedFace[2];
       proxy[0] = this;
       topo = this;
       proxy[1] = new TP_DirectedFace();
       proxy[1].topo = this;
       proxy[1].orientation = -1;       
    }

    // redefinition pour affecter un bon id au proxy negatif       
    public void setId(int Id) {
        super.setId(Id);
        proxy[1].setId(-Id);
        //if (Id<0) System.out.println("TP_Face::setId(id) : L'identifiant doit être positif");
    }    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // asTP_DirectedTopo() //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////        
    /** Renvoie le TP_DirectedFace d'orientation "sign". "sign" doit valoir +1 ou -1, sinon renvoie null. */
    public TP_DirectedFace asTP_DirectedTopo(int sign) {
        if (sign == +1) return proxy[0];
        else if (sign == -1) return proxy[1];
        else {
            System.out.println("TP_Face::asTP_DirectedTopo(sign) : Passer +1 ou -1 en paramètre.");
            return null;
        }
    }
    
    

    /////////////////////////////////////////////////////////////////////////////////////
    // isolated in (relation inverse de container) //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////     
    /** Relation inverse de container sur TP_Node. */
    public Collection isolated = new ArrayList();
    public Collection getIsolated() {return isolated;};
    public void setIsolated (Collection c) {isolated = c;}
    public void addIsolated(TP_Node node) {
        if (node != null) {
            isolated.add(node);            
	    if (node.getContainer() != this)
                node.setContainer(this);
        }
    }    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // boundary /////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////// 
    /** Les TP_Edge qui ont this pour face gauche. */
    public Collection left = new ArrayList();
    public Collection getLeft() {return left;};
    public void addLeft(TP_Edge edge) {
        if (edge != null) {
            left.add(edge);
            if (edge.getLeftface() != this)
                edge.setLeftface(this);
        }     
    }    

    /** Les TP_Edge qui ont this pour face droite. */
    public Collection right = new ArrayList() ;
    public Collection getRight() {return right;}
    public void addRight(TP_Edge edge) {        
        if (edge != null) {
            right.add(edge);
            if (edge.getRightface() != this)
                edge.setRightface(this);              
        }
    }   
    
    /** Renvoie les TP_DirectedEdge associes au TP_Face, structures en TP_FaceBoundary. */
    public TP_FaceBoundary boundary()  {
        TP_FaceBoundary result = null;
        Iterator it;        
        try {
            // liste des DirectedEdge qui ont this pour frontiere gauche
            List theEdges = new ArrayList();
            it = left.iterator();
            while (it.hasNext()) {
               TP_Edge edge = (TP_Edge)it.next();
               theEdges.add(edge.asTP_DirectedTopo(+1));
            }
            it = right.iterator();
            while (it.hasNext()) {
               TP_Edge edge = (TP_Edge)it.next();
               theEdges.add(edge.asTP_DirectedTopo(-1));
            }
            
            // on cherche l'anneau exterieur
            // on cherche les brins dont les noeud n'ont pas this comme container
            // A REVOIR SANS UTILISER LE CONTAINER
           List extEdges = new ArrayList();
           for (int i=0; i<theEdges.size(); i++) {
               TP_DirectedEdge edge = (TP_DirectedEdge)theEdges.get(i);
               extEdges.add(edge);
           }
               
           try {
            TP_Ring extRing = new TP_Ring(extEdges);
            result = new TP_FaceBoundary(extRing);
           } catch (Exception e) {
               e.printStackTrace(); // normalement il n'y a pas d'exception ici
           }
 
           // il reste eventuellement des anneaux interieurs
           // A REVOIR SANS UTILISER LE CONTAINER
            /*  if (theEdges.size() > 0) {

               // necessite de cloner la liste car on risque de la modifier dans le try
               List theEdgesBis = new ArrayList();
               for (int i=0; i<theEdges.size(); i++) theEdgesBis.add(theEdges.get(i));
               // on essaie de faire un TP_Ring avec le reste : si Exception, c'est qu'il y en a plusieurs
               try {
                    TP_Ring intRing = new TP_Ring(theEdgesBis);
                    result.appendInterior(intRing);
               } catch (Exception e) {

                   // on cherche les cycles
                   while (theEdges.size() > 0) {

                       List aCycle = new ArrayList();
                       TP_DirectedEdge dt0 = (TP_DirectedEdge)theEdges.get(0);
                       aCycle.add(dt0);
                       theEdges.remove(dt0);
                       if (theEdges.size() > 0) {
                           int IDEndNode = dt0.endNode().topo().getId();
                           int theIDStartNode = dt0.startNode().topo().getId();
                           int i = 0;
                           while (theIDStartNode != IDEndNode) {
                                TP_DirectedEdge dt = (TP_DirectedEdge)theEdges.get(i);
                                int IDStartNode = dt.startNode().topo().getId();
                                if (IDEndNode == IDStartNode) {
                                aCycle.add(dt);
                                IDEndNode = dt.endNode().topo().getId();
                                theEdges.remove(i);
                                i = 0;
                                continue;
                                }
                                i++;
                                if (i == theEdges.size()) 
                                    throw new Exception("DrCogit - erreur 8.011");
                           }
                           TP_Ring intRing = new TP_Ring(aCycle);
                           result.appendInterior(intRing);
                       }
                   }
               }                
           }*/
           return result;
           
       } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
             
        
    
    /////////////////////////////////////////////////////////////////////////////////////
    // coBoundary ///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** non implemente (renvoie null). Les TP_DirectedSolid associes au TP_Face. */
    public List coBoundary()  {   
        return null;
    }
       
}
