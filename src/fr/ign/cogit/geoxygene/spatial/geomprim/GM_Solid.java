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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;


/** NON UTILISE.
  * Object géométrique de base en 3D.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GM_Solid extends GM_Primitive {

    /** NON IMPLEMENTE (renvoie 0.0).
      * Aire. */
   // Dans la norme, le résultat est de type Area.
    public double area() {return 0.0;}

    
    /** NON IMPLEMETE (renvoie 0.0).
      * Volume. */
     // Dans la norme, le résultat est de type Volume.
    public double volume() {return 0.0;}

    
    /**  Constructeur par défaut.  */
    public GM_Solid () {
    }
    
    
    /** NON IMPLEMETE.
      * Constructeur à partir de la frontière. */
    public  GM_Solid(GM_SolidBoundary bdy) {
    }
    
    /** NON IMPLEMENTE. 
     * Constructeur à partir d'une enveloppe .*/
    public  GM_Solid(GM_Envelope env) {
    }
    

    
    /** NON IMPLEMENTE (Renvoie null).
      * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une GM_SolidBoundary, 
      * c'est-à-dire un shell extérieur et éventuellement un (des) shell(s) intérieur(s). */
    public GM_SolidBoundary boundary() {return null;}
      
        
    
    /** Marche pas. Renvoie null; */
    public DirectPositionList coord() {
        System.out.println("coord() : marche pas pour un solide. Renvoie null");
        return null;
    }
}
