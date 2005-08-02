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

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;


/**
  * Classe mère abstraite pour les primitives topologiques.
  * Par définition, une primitive est orientée positivement.
  * Une primitive possède 2 TP_DirectedTopo orientés positivement et négativement.
  * Le TP_DirectedTopo orienté positivement est sa propre primitive.
  *
  * @author Thierry Badard, Arnaud Braun & Audrey Simon
  * @version 1.0
  * 
  */



abstract public class TP_Primitive extends TP_Object {
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// lien vers les complexes, non implemente /////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    // Quand un complexe géométrique est la réalisation d'un complexe topologique, 
    // alors les primitives doivent être en correspondance 1-1.
        
    // mettre une liste (une primitive peut appartenir a plusieurs complexes)
    //protected long TP_ComplexID;
    
    // accesseurs
    // long getTP_ComplexID(){return TP_ComplexID;}
    // void setTP_ComplexID(int id){TP_ComplexID=id;}
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// lien vers la primitive geometrique - ce lien n'est pas dans la norme ////////////
    ///////////////////////////////////////////////////////////////////////////////////// 
    /** Lien vers la géométrie correspondant à self. */
    protected GM_Primitive geom;
    
    /** Renvoie la géométrie de self. */
    public GM_Primitive getGeom () {return this.geom;} 
    
    /** Affecte une géométrie à self. */
    public void setGeom (GM_Primitive value) {this.geom = value;}
    
    /** Renvoie 1 si self possède une géométrie, 0 sinon. */
    public int sizeGeom () {
        if ( this.geom == null ) return 0;
        else return 1;
    }
    
    

    /////////////////////////////////////////////////////////////////////////////////////
    /// methode de la norme supprimee pour simplification                              //
    /// cette methodes est definie dans les sous-classes avec un bon typage en sortie ///
    /////////////////////////////////////////////////////////////////////////////////////
    // Conversion en TP_DirectedTopo.
    // public TP_DirectedTopo asTP_DirectedTopo(DataSource data, int orientation) throws Exception {
    //     return null;
    // }
    
    
}
