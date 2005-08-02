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

package fr.ign.cogit.geoxygene.spatial.toporoot;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedEdge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedFace;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedNode;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Edge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Face;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Node;

/**
  * Classe mere abstraite pour les objets topologiques.
  * Un TP_Object est soit un TP_Primitive, soit un TP_Complex.
  * On appelle id l'identifiant topologique qu'on retrouve dans la table TP_Object du SGBD. 
  * <P> Tout ce qui est relatif aux TP_Complex n'est pas implemente.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */


abstract public class TP_Object implements Cloneable {


    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /// pour le mapping ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructeur necessaire pour affecter le type de la sous-classe concrete. */
    public TP_Object() {
        setClasstype(getClass().getName());
        setOjbConcreteClass(getClass().getName());
    }
       
    // castor
    protected String classtype;
    public String getClasstype() {return classtype; }
    public void setClasstype(String Classtype) {classtype = Classtype; }   
    
    // ojb
    protected String ojbConcreteClass;
    public String getOjbConcreteClass() {return ojbConcreteClass; }
    public void setOjbConcreteClass(String OjbConcreteClass) {ojbConcreteClass = OjbConcreteClass; }   
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// identifiant /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
    /** Identifiant topologique. */
    protected int id;       
    /** Renvoie l'identifiant topologique. */
    public int getId() {return this.id;}    
    /**  Affecte une valeur a l'identifiant topologique.*/
    // assure la coherence avec featureID  
// OBSOLETE    
    public void setId(int Id) {
    /*	this.id=Id;
    	if (feature != null)
    		feature.setTopoID(id);*/
    	}
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    /// lien 1-1 vers FT_Feature - ce lien n'est pas dans la norme //////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
    /** FT_Feature auquel est rattache this. */
    protected FT_Feature feature;
    /** FT_Feature auquel est rattache this. */
    public FT_Feature getFeature() {return feature;}
    /** Affecte un FT_Feature a this. */
// A REVOIR OBSOLETE    
    public void setFeature(FT_Feature Feature) {
/*        if (Feature != null) {
            feature = Feature;
            featureID = Feature.getId();
            if (Feature.getTopo() != this)
                Feature.setTopo(this);
        } else {
            feature = null;
            featureID = 0;
        }*/
    }
    
    // pour ojb
    protected int featureID;
    public int getFeatureID() {return featureID;}
    public void setFeatureID(int ID) {featureID= ID;}
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// lien n-m vers FT_Feature - abandonne pour cause de maivaise perf ////////////////
    /////////////////////////////////////////////////////////////////////////////////////        
    /** FT_Feature auquel est rattache? cette topologie. */
    //protected List feature = new ArrayList();
    //protected Collection feature = new ArrayList();    
    /** Renvoie la liste des FT_Feature auquelle est rattach? cette topologie.*/ 
    /*    public Collection getFeature() {
        return this.feature;            
    }
    
    /** Affecte une liste des FT_Feature.*/ 
    /*    public void setFeature (Collection L) {
        this.feature = L;            
    }
    
    /** Affecte un  FT_Feature. A REVOIR (addFeature ??)*/ 
    /*    public void setFeature (FT_Feature Feature) {
        this.feature.add(Feature);             
    }
    
    /** Renvoie le FT_Feature de rang 0. */
    /*public FT_Feature getFeature() {
        return (FT_Feature)this.feature.get(0);            
    }*/
    
    /** Renvoie le FT_Feature de rang i. */
    /*   public FT_Feature getFeature(int i) {
        if (i==0)
        return (FT_Feature)this.feature.iterator().next();
        else {
            System.out.println("tp_object- getFeature - probleme");
            return null;
        }
    }*/
    
    /** Affecte un FT_Feature.  */
    /*    public void addFeature(FT_Feature Feature) {
        this.feature.add(Feature);        
    }

    /** Renvoie le nombre de FT_Feature. */
    /*    public int sizeFeature() {
        return feature.size();
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    /// dimension ///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
    /** Dimension topologique de l'objet 
      * (0 pour node, 1 pour edge, 2 pour face, 3 pour solid).    */
    public int dimension() {
        if (this instanceof TP_Node || this instanceof TP_DirectedNode) return 0;
        else if (this instanceof TP_Edge || this instanceof TP_DirectedEdge) return 1;
        else if (this instanceof TP_Face || this instanceof TP_DirectedFace) return 2;
      //  else if (this instanceof TP_Solid || this instanceof TP_DirectedSolid) return 3;
        else {
            System.out.println("this n'est pas une primitive topologique. La fonction dimension renvoie -1.");
            return -1;
        }
    }


    
    /////////////////////////////////////////////////////////////////////////////////////
    /// methodes de la norme supprimees pour simplification                           //
    /// ces methodes sont definies dans les sous-classes avec un bon typage en sortie //
    /////////////////////////////////////////////////////////////////////////////////////    
    /** Set de TP_DirectedTopo structures en TP_Boundary, qui representent la frontiere de self.
      * Si le TP_Object est associe a un GM_Object, sa frontirre doit rtre cohrrente en terme d'orientation avec celle du GM_Object.
      * Les TP_Boundary sont des TP_Expression et ont donc une forme polynomiale (exemple : +edge1-edge2+edge3, ...)  */
    //abstract public TP_Boundary boundary() ;
    
    /** Tous les TP_DirectedTopo qui ont self pour fronti?re.  */
    //abstract public List coBoundary() ;
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    /// non implemente, relatif aux TP_Complex //////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////     
    /** Set de TP_Primitive constituant l'intrrieur de self (c'est-a-dire sans la frontiere) dans le complexe maximal de l'objet. 
      * Pour un TP_Primitive, le resultat sera self. 
      * Pour un complexe, le resultat sera les primitives de ce complexe qui ne sont pas sur la frontiere du complexe.
      * Le resultat est equivalent a l'interieur de la realisation geometrique de self. */
    //public List interior() {
    //    return null;
    //}
    
    /** Union de l'interieur et de la frontiere de self.
        closure()=interior().union(boundary()) */
    //public List closure() {
    //    return null;
    //}
    
    /** Set de TP_Primitive constituant exterieur de self dans le complexe maximal de l'objet :
      * toutes les TP_Primitives du complexe maximal qui ne sont pas a l'interieur, ou la frontiere du TP_Object. */
    //public List exterior() {
    //    return null;
    //}
    
    /** Le TP_Complex maximal qui contient self. */
    //public TP_Complex maximalComplex() {
    //    return null;
    //}
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// equals //////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie true si les 2 objets appartiennent a la meme classe et ont le meme identifiant 
     (champs id).
     ATTENTION : si aucun id n'a encore ete affecte aux objets, alors les deux id vaudront 0 et le test
     renverra true.*/
    public boolean equalsID(java.lang.Object obj) {
        if (obj==null || !this.getClass().equals(obj.getClass())) return(false);
        TP_Object o=(TP_Object) obj;
        if ( this.id!=o.id ) return( false );
        return( true );
    }        


    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    /// clone ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////    
    /** Clone l'objet. */
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
