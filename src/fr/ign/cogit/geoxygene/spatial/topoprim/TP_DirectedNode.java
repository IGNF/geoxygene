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
  * Noeud topologique orienté. Supporte la classe TP_Node pour les TP_Expression.
  * Dans notre implémentation, l'identifiant d'un TP_DirectedTopo est celui de sa primitive avec le signe de l'orientation.
  * EXPLIQUER QUE C'EST PAS PERSISTANT et que A PRIORI ca n'a pas de GEOMETRIE
  *
  * @author Thierry Badard, Arnaud Braun & Audrey Simon
  * @version 1.0
  * 
  */

public class TP_DirectedNode extends TP_DirectedTopo {
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // constructeur /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////        
    /** Constructeur par défaut. */
    public TP_DirectedNode() {
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // topo /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** Primitive de this. */
    protected TP_Node topo;
    /** Primitive de this. */
    public TP_Node topo () {
        return topo;
    }
    
    

    /////////////////////////////////////////////////////////////////////////////////////
    // negate ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** Renvoie le TP_DirectedNode d'orientation opposée. */
    public TP_DirectedNode negate()  { 
        if (orientation<0) return topo.proxy[0];
        else return topo.proxy[1];
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // boundary /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** Renvoie null. */
    public TP_Boundary boundary()  {
        return null;
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // coBoundary ///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    public List coBoundary()  {
        if (orientation == +1) return this.topo().coBoundary();
        
        // pour un noeud negatif, on retourne tous les brins
        else if (orientation == -1) {
            List coBoundary = this.topo().coBoundary();
            for (int i=0; i<coBoundary.size(); i++) {
                TP_DirectedEdge directedEdge1 = (TP_DirectedEdge)coBoundary.get(i);
                TP_DirectedEdge directedEdge2 = (TP_DirectedEdge)directedEdge1.negate();
                coBoundary.set(i,directedEdge2);
            }
            return coBoundary;
        }
        else return null;
    }
    
}
