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

import java.util.Collection;
import java.util.List;


/**
  * Solide topologique (orientation positive).
  * <P> L'opération "CoBoundary" redéfinie sur TP_Object renvoie NULL.
  * <P> L'opération "Boundary" redéfinie sur TP_Object renvoie un set de TP_DirectedFace avec les orientations adéquates.  Cette opération est aussi une association.
  * Ceci n'est pas implémenté.
  *
  * @author Thierry Badard, Arnaud Braun & Audrey Simon
  * @version 1.0
  * 
  */

class TP_Solid extends TP_DirectedSolid {
    

    /** Les 2 primitives orientées de this. */    
    // hesitation sur le fait : proxy[0] = this ou proxy[0] = new TP_DirectedSolid(id) + proxy[0].topo = this        
    protected TP_DirectedSolid[] proxy;
         
         
    /////////////////////////////////////////////////////////////////////////////////////
    // constructeur /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
    public TP_Solid () {
       orientation = +1;
       proxy = new TP_DirectedSolid[2];
       proxy[0] = this;
       topo = this;
       proxy[1] = new TP_DirectedSolid();
       proxy[1].topo = this;
       proxy[1].orientation = -1;       
    }
    
    // redefinition pour affecter un bon id au proxy negatif       
    public void setId(int Id) {
        super.setId(Id);
        proxy[1].setId(-Id);
        if (Id<0) System.out.println("TP_Solid::setId(id) : L'identifiant doit être positif");
    }    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // asTP_DirectedTopo() //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
   /** Renvoie le TP_DirectedSolid d'orientation "sign". "sign" doit valoir +1 ou -1, sinon renvoie null. */
   public TP_DirectedSolid asTP_DirectedTopo(int sign)  {
        if (sign == +1) return proxy[0];
        else if (sign == -1) return proxy[1];
        else {
            System.out.println("TP_Solid::asTP_DirectedTopo(sign) : Passer +1 ou -1 en paramètre.");
            return null;
        }
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // isolated in (relation inverse de container) //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////       
    /** Relation inverse de container sur TP_Edge. */
    public Collection isolated;
    public Collection getIsolated() {return isolated;};
    public void setIsolated (Collection c) {isolated = c;}
    public void addIsolated(TP_Edge edge) {isolated.add(edge);}        
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // boundary() ///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** non implémenté (renvoie null). Renvoie la frontière de self.*/
    public TP_SolidBoundary boundary() {
        return null;
    }

    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // coBounfdary() ////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////      
    /** Renvoie null. */
    public List coBoundary()  {
        return null;
    }
        
}
