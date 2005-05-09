/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.spatial.geomaggr;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
//import spatial.geomroot.GM_Object;

/**
  * Agrégation de points. Il n'y a aucune structure interne.
  * La liste "element" est une liste de GM_Point.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class GM_MultiPoint extends GM_MultiPrimitive {
     
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    // constructeurs ////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /** Constructeur par défaut. */
    public GM_MultiPoint() {
        element = new ArrayList();
    }
    
    /** Constructeur à partir d'un GM_Point. */
    public GM_MultiPoint (GM_Point aGM_Point) {
        element = new ArrayList();
        this.add(aGM_Point);
    }
        
    /** Constructeur à partir d'une liste de GM_Point. */
    public GM_MultiPoint (List fromGM_Point) {
        element = new ArrayList();
        int n = fromGM_Point.size();
        if (n > 0) 
            for (int i=0; i<n; i++)
                this.add((GM_Point)fromGM_Point.get(i));
    }
    
    /** Constructeur à partir d'une liste de DirectPosition. */
    public GM_MultiPoint (DirectPositionList L) {
        element = new ArrayList();
        int n = L.size();
        if (n > 0) 
            for (int i=0; i<n; i++)
                this.add(L.get(i).toGM_Point());
    }    
    
}
