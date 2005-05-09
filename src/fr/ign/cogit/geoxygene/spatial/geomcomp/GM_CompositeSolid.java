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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.List;
//import spatial.geomprim.GM_Solid;
//import spatial.geomprim.GM_SolidBoundary;


/** NON UTILISE.
  * Complexe ayant toutes les propriétes géométriques d'un solide. C'est un set de solides (GM_Solid) partageant des surfaces communes.
  * Hérite de GM_Solid, mais le lien n'apparaît pas explicitement (problème de double héritage en java). Les méthodes et attributs ont été reportés.
  *
  * <P> ATTENTION : normalement, il faudrait remplir le set "element" (contrainte : toutes les primitives du generateur
  * sont dans le complexe). Ceci n'est pas implémenté pour le moment.
  * <P> A FAIRE AUSSI : iterateur sur "generator"
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GM_CompositeSolid extends GM_Composite {

    ////////////////////////////////////////////////////////////////////////
    // Attribut "generator" et méthodes pour le traiter ////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    /** Les GM_Solid constituant self. */
    protected List generator;
    
    /** Renvoie la liste des GM_Solid */
    public List getGenerator() {return generator;}
    
    /** Renvoie le GM_Solid de rang i */
    //public GM_Solid getGenerator (int i) {return (GM_Solid)this.generator.get(i);}
    
    /** Affecte un GM_Solid au rang i. Attention : aucun contrôle de cohérence n'est effectué. */
    //protected void setGenerator (int i, GM_Solid value) {this.generator.set(i, value);}
    
    /** Ajoute un GM_Solid en fin de liste. Attention : aucun contrôle de cohérence n'est effectué. */
    //protected void addGenerator (GM_Solid value) {this.generator.add(value);}

    /** Ajoute un GM_Solid au rang i. Attention : aucun contrôle de cohérence n'est effectué. */
    //protected void addGenerator (int i, GM_Solid value) {this.generator.add(i, value);}
 
    /** Efface le (ou les) GM_Solid passé en paramètre. Attention : aucun contrôle de cohérence n'est effectué. */
    /*protected void removeGenerator (GM_Solid value) throws Exception {
        if (this.generator.size() == 1)
            throw new Exception ( "Dr Cogit - error 4.001" );
        else
            this.generator.remove(value);
    }*/
    
    /** Efface le GM_Solid de rang i. Attention : aucun contrôle de cohérence n'est effectué. */
    protected void removeGenerator (int i) throws Exception {
        if (this.generator.size() == 1)
            throw new Exception ( "Il n'y a qu'un objet dans l'association." );
        else
            this.generator.remove(i);
    }
    
     /** Nombre de GM_Solid constituant self */
    public int sizeGenerator () {return this.generator.size();}

    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    // Méthodes héritées de GM_Solid (héritage simulé) //////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////    
    
    /** NON IMPLEMETE (renvoie 0.0).
      * Aire. */
   // Dans la norme, le résultat est de type Area.
    public double area() {return 0.0;}
 
    /** NON IMPLEMETE (renvoie 0.0).
      * Volume. */
     // Dans la norme, le résultat est de type Volume.
    public double volume() {return 0.0;}
   
    /** NON IMPLEMENTE (Renvoie null).
      * Redéfinition de l'opérateur "boundary" sur GM_Object. Renvoie une GM_SolidBoundary, 
      * c'est-à-dire un shell extérieur et éventuellement un (des) shell(s) intérieur(s). */
    //public GM_SolidBoundary boundary() {return null;}
     

    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    // Constructeurs ////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////   
      
    /**  Constructeur par défaut.  */
    public GM_CompositeSolid () {
        generator = new ArrayList();
    }
    
    /** Constructeur à partir d'un GM_Solid. */
   /* public  GM_CompositeSolid (GM_Solid theSolid) {
        generator = new ArrayList();
        generator.add(theSolid);
    }*/
}
