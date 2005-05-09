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

package fr.ign.cogit.geoxygene.example;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbOracle;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedEdge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedFace;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedNode;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedTopo;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Edge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_EdgeBoundary;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Expression;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Face;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_FaceBoundary;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Node;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Ring;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;



// A REVOIR APRES REFLEXION SUR LA TOPOLOGIE - N'EST PLUS A JOUR

/**
 * Utilisation du package spatial pour la topologie : exemple de code.
 * Cet exemple montre comment charger un objet et sa topologie,
 * et l'utilisation des différentes méthodes des classes du package "topoprim".
 * On suppose qu'il existe une classe persistante "donnees.defaut.Troncon_route" pour laquelle 
 * une topologie de face a ete calculee.
 * (sinon changer le nom de la classe dans le code).
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


// RAJOUTER EXEMPLES APRES MODIFS


public class TestTopo {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Attributs */
    private static Geodatabase db;                        // source de données
    private static Class tronconClass;                  // classe de troncons   
    private String nomClasse = "donnees.defaut.Troncon_route"; // nom de la classe a charger
    private static int identifiant = 664000;            // id du troncon a charger

    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /** constructeur : initialisation dses attributs */
    public TestTopo() {
        db = new GeodatabaseOjbOracle();   // pour ne pas avoir les messages Castor        
        try {
            tronconClass = Class.forName(nomClasse);
        } catch (ClassNotFoundException e) {
            System.out.println(nomClasse+" : non trouvee");  
            System.exit(0);     
        }        
    }



    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /** chargement d'un objet et appel des méthodes sur noeud, brin et face*/
    public static void main (String args[]) {
        
        TestTopo test = new TestTopo();     // appel du constructeur
        
        try {
            
            // ouverture transaction
            db.begin();
            
            // chargement d'un reseau
            // necessaire pour charger toutes les relations
            FT_FeatureCollection tronconList = db.loadAllFeatures(tronconClass);

            // chargement d'un objet particulier avec son identifiant. 
            FT_Feature tron = (FT_Feature)db.load(tronconClass, new Integer(identifiant));
            
            // on recupere la topologie
            TP_Edge edge = (TP_Edge)tron.getTopo();            
            System.out.println("identifiant du brin : "+edge.getId());
            System.out.println("    identifiant du troncon : "+tron.getId()); 
            
            // exemples de traitements sur un brin...
            edgeMethod(edge);
            
            // exemples de traitements sur unn noeud...
            TP_Node node = edge.endNode().topo();            
                // on applique topo() car endNode() renvoie un TP_DirectedNode et on veut un TP_Node
            nodeMethod(node);

            // exemples de traitements sur une face...
            if (edge.leftFace() != null) {                
                TP_Face face = edge.leftFace().topo();
                    // on applique topo() car leftFace() renvoie un TP_DirectedFace et on veut un TP_Face            
                faceMethod(face);            
                face = edge.rightFace().topo();
                faceMethod(face);

                // exemple d'utilisation des TP_Expression...
                 tp_expression(edge);
            }
            
            // fermeture transaction
            db.commit();
            System.out.println();
            System.out.println("OK");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**  exemple de traitement sur un noeud */
    public static void nodeMethod (TP_Node node)  {

        System.out.println();
        System.out.println("methodes sur un noeud ...");
        System.out.println();
       
        System.out.println("identifiant du noeud : "+node.getId());
                   
        // container
        if (node.getContainer() != null)
            System.out.println("identifiant du container : "+node.getContainer().getId());
        else System.out.println("pas de container");
       
        // boundary (renvoie null)
        System.out.println("boundary : "+node.boundary());
        
        // coBoundary
        System.out.println("coBoundary : ");
        List node_coBdy = node.coBoundary();
        for (int i=0; i<node_coBdy.size(); i++) {
            TP_DirectedEdge diredge = (TP_DirectedEdge)node_coBdy.get(i);
            System.out.println("    identifiant du brin oriente : "+diredge.getId());
            System.out.println("        identifiant du feature : "+diredge.topo().getFeature().getId());
            // remarque : getFeature() s'applique sur un TP_Edge et non pas un TP_DirectedEdge (sinon renvoie null)
            // c'est pour cela qu'on applique l'operation topo()
        }
                
        // asTPDirectedTopo
        TP_DirectedNode d_node= node.asTP_DirectedTopo(+1);
        System.out.println("identifiant du TP_DirectedNode(+1) : "+d_node.getId());
        d_node= (TP_DirectedNode)node.asTP_DirectedTopo(-1);
        System.out.println("identifiant du TP_Directednode(-1) : "+d_node.getId());
        
        // topo
        System.out.println("identifiant du topo du  TP_DirectedNode(-1): "+d_node.topo().getId());
        
        // negate
        System.out.println("identifiant du negate : "+node.negate().getId());      
        
        // coboundary du negate
        System.out.println("coBoundary du negatif : ");
        node_coBdy = node.negate().coBoundary();
        for (int i=0; i<node_coBdy.size(); i++) {
            TP_DirectedEdge diredge = (TP_DirectedEdge)node_coBdy.get(i);
            System.out.println("    identifiant du brin oriente : "+diredge.getId());
            System.out.println("        identifiant du feature : "+diredge.topo().getFeature().getId());
            // meme remarque que plus haut
        }
                
    }
    
        
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**  exemple de traitement sur un brin */
    public static void edgeMethod (TP_Edge edge)  {
        
        System.out.println();
        System.out.println("methodes sur un brin ...");
        System.out.println();
       
        System.out.println("identifiant du brin : "+edge.getId());
        System.out.println("    identifiant du feature du brin : "+edge.getFeature().getId());
                
        // boundary
        // l'operateur "boundary" renvoie un  TP_EdgeBoundary
        System.out.println("boundary : ");
        TP_EdgeBoundary edge_bdy = edge.boundary();   
        System.out.println("identifiant du startNode : "+edge_bdy.getStartnode().getId());
        System.out.println("identifiant du endNode : "+edge_bdy.getEndnode().getId());
        // sans passer par TP_EdgeBoundary
        System.out.println("identifiant du startNode : "+edge.startNode().getId());
        System.out.println("identifiant du endNode : "+edge.endNode().getId());
        
        // coBoundary
        System.out.println("coBoundary : ");
        List edge_coBdy = edge.coBoundary();
        for (int i=0; i<edge_coBdy.size(); i++) {
            TP_DirectedFace theFace = (TP_DirectedFace)edge_coBdy.get(i);
            System.out.println("    identifiant de la face : "+theFace.getId());
        }
        
        // face gauche
        if (edge.leftFace() != null)
            System.out.println ("identifiant de la face gauche : "+edge.leftFace().getId());
        
        // face droite
        if (edge.rightFace() != null)
            System.out.println ("identifiant de la face droite : "+edge.rightFace().getId());        
        
        // asTPDirectedTopo
        TP_DirectedEdge d_edge= edge.asTP_DirectedTopo(+1);
        System.out.println("identifiant du TP_DirectedEdge(+1) : "+d_edge.getId());
        d_edge= edge.asTP_DirectedTopo(-1);
        System.out.println("identifiant du TP_DirectedEdge(-1) : "+d_edge.getId());
        
        // topo
        System.out.println("identifiant du topo du TP_DirectedEdge(-1) : "+d_edge.topo().getId());
        
        // negate
        System.out.println("identifiant du negate : "+edge.negate().getId());
        
        // boundary du negate
        System.out.println("boundary du brin oppose : ");        
        edge_bdy = edge.negate().boundary();   
        System.out.println("identifiant du startNode : "+edge_bdy.getStartnode().getId());
        System.out.println("identifiant du endNode : "+edge_bdy.getEndnode().getId());
        // sans passer par TP_EdgeBoundary
        System.out.println("identifiant du startNode : : "+edge.negate().startNode().getId());
        System.out.println("identifiant du endNode : : "+edge.negate().endNode().getId());
        
        // coboundary du negate
        System.out.println("coBoundary du brin oppose : ");
        edge_coBdy = edge.negate().coBoundary();
        for (int i=0; i<edge_coBdy.size(); i++) {
            TP_DirectedFace theFace = (TP_DirectedFace)edge_coBdy.get(i);
            System.out.println("    identifiant de la face : "+theFace.getId());
        }

        // face gauche du negate
        if (edge.leftFace() != null)
            System.out.println ("identifiant de la face gauche : "+edge.negate().leftFace().getId());
        
        // face droite du negate
        if (edge.rightFace() != null)
            System.out.println ("identifiant de la face droite : "+edge.negate().rightFace().getId());        
        
        // brin suivant
        System.out.println("identifiant du brin suivant : "+edge.nextEdge().getId());
        System.out.println("    identifiant du feature du brin suivant : "+edge.nextEdge().topo().getFeature().getId());
        // remarque : getFeature() s'applique sur un TP_Edge et non pas un TP_DirectedEdge (sinon renvoie null)
        // c'est pour cela qu'on applique l'operation topo()
        
        // brin suivant du negate()
        System.out.println("identifiant du brin suivant du brin oppose : "+edge.negate().nextEdge().getId());
        System.out.println("    identifiant du feature du brin suivant du brin oppose : "+edge.negate().nextEdge().topo().getFeature().getId());
        // meme remarque que ci-dessus
        
        // brin precedent
        System.out.println("identifiant du brin precedent : "+edge.previousEdge().getId());
        System.out.println("    identifiant du feature du brin precedent : "+edge.previousEdge().topo().getFeature().getId());
       // meme remarque que ci-dessus
        
        // brin precedent du negate()
        System.out.println("identifiant du brin precedent du brin oppose : "+edge.negate().previousEdge().getId());
        System.out.println("    identifiant du feature du brin precedent du brin oppose : "+edge.negate().previousEdge().topo().getFeature().getId());
        // meme remarque que ci-dessus
        
        
        // recherche d'un cycle
        System.out.println("cycle ...");
        TP_Ring cycle = edge.cycle();
        for (int i=0; i<cycle.sizeTerm(); i++) {
            TP_DirectedEdge dedge = (TP_DirectedEdge)cycle.getTerm(i);
            System.out.println("    identifiant du brin : "+dedge.getId());
            System.out.println("        identifiant du feature du brin : "+dedge.topo().getFeature().getId());
        }
                        
    }

        

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**  exemple de traitement sur une face */
    public static void faceMethod (TP_Face face)  {
        
        System.out.println();
        System.out.println("methodes sur une face ...");
        System.out.println();
       
        System.out.println("identifiant de la face : "+face.getId());
        
        // boundary
        TP_FaceBoundary face_bdy = face.boundary();   
        System.out.println("boundary exterior: ");
        TP_Ring ext = face_bdy.getExterior();
        for (int i=0; i<ext.sizeTerm(); i++) {
            TP_DirectedEdge theEdge = (TP_DirectedEdge)ext.getTerm(i);
            System.out.println("    identifiant du brin : "+theEdge.getId());
            System.out.println("     identifiant du feature : "+theEdge.topo().getFeature().getId());
        }
        for (int j=0; j<face_bdy.sizeInterior(); j++) {
            System.out.println("boundary interior: ");
            TP_Ring inte = face_bdy.getInterior(j);
            for (int i=0; i<inte.sizeTerm(); i++) {
                TP_DirectedEdge theEdge = (TP_DirectedEdge)inte.getTerm(i);
                System.out.println("    identifiant du brin : "+theEdge.getId());
                System.out.println("     identifiant du feature : "+theEdge.topo().getFeature().getId());                
            }
        }
        
        // asTPDirectedTopo
        TP_DirectedFace d_face= face.asTP_DirectedTopo(+1);
        System.out.println("identifiant du TP_DirectedTopo(+1) : "+d_face.getId());
        d_face= (TP_DirectedFace)face.asTP_DirectedTopo(-1);
        System.out.println("identifiant du TP_DirectedTopo(-1) : "+d_face.getId());
        
        // topo
        System.out.println("identifiant du topo du TP_DirectedTopo(-1) : "+d_face.topo().getId());
        
        // negate
        System.out.println("identifiant du negate : "+face.negate().getId());
        
        // boundary du negate
        face_bdy = face.negate().boundary();   
        System.out.println("boundary exterior: ");
        ext = face_bdy.getExterior();
        for (int i=0; i<ext.sizeTerm(); i++) {
            TP_DirectedEdge theEdge = (TP_DirectedEdge)ext.getTerm(i);
            System.out.println("    identifiant du brin : "+theEdge.getId());
            System.out.println("     identifiant du feature : "+theEdge.topo().getFeature().getId());            
        }
        for (int j=0; j<face_bdy.sizeInterior(); j++) {
            System.out.println("boundary interior: ");
            TP_Ring inte = face_bdy.getInterior(j);
            for (int i=0; i<inte.sizeTerm(); i++) {
                TP_DirectedEdge theEdge = (TP_DirectedEdge)inte.getTerm(i);
                System.out.println("    identifiant du brin : "+theEdge.getId());
                System.out.println("     identifiant du feature : "+theEdge.topo().getFeature().getId());                
            }
        }
        
    }


    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**  exemple de traitement sur les TP_Expression */
    public static void tp_expression (TP_Edge edge1)  {
        
        System.out.println();
        System.out.println("TP_Expression...");
        System.out.println();        
        
        System.out.println("identifiant du brin1 : "+edge1.getId());
        
        // objets qui serviront dans l'exemple
        // essayer avec differents objets, differentes orientations
        TP_DirectedEdge edge2 = edge1.nextEdge();
        //TP_DirectedEdge edge2 = edge1.nextEdge().negate();
        System.out.println("identifiant du brin2 : "+edge2.getId());
        TP_DirectedEdge edge3 = edge2.nextEdge();
        System.out.println("identifiant du brin3 : "+edge3.getId());
        
        // construction d'un nouveau TP_Expression
        TP_Expression exp = new TP_Expression(edge1);
        exp.addTerm(edge2);
        exp.addTerm(edge3);
                
        // boundary ( ac omparer avec boundary de chacun des brins)
        System.out.println("boundary...");
        TP_Expression bdy = exp.boundary();
        for (int i=0; i<bdy.sizeTerm(); i++) {
            TP_DirectedTopo dt = bdy.getTerm(i);
            System.out.println(dt.getClass().getName()+"    "+dt.getId());
        }
        
        // coboundary
        System.out.println("coBoundary...");
        TP_Expression cobdy = exp.coBoundary();
        for (int i=0; i<cobdy.sizeTerm(); i++) {
            TP_DirectedTopo dt = cobdy.getTerm(i);
            System.out.println(dt.getClass().getName()+"    "+dt.getId());
        }        
        
        // isCycle
        System.out.println("isCycle...");
        System.out.println(exp.isCycle());        
   
        // asSet
        System.out.println("asSet...");
        List theList = exp.asSet();
        for (int i=0; i<theList.size(); i++) {
            TP_Object tpo = (TP_Object)theList.get(i);
            System.out.println(tpo.getClass().getName()+"    "+tpo.getId());
        }        
        
        // support
        System.out.println("support...");
        theList = exp.support();
        for (int i=0; i<theList.size(); i++) {
            TP_Object tpo = (TP_Object)theList.get(i);
            System.out.println(tpo.getClass().getName()+"    "+tpo.getId());
        }           
        
        // objets qui serviront dans l'exemple
        // essayer avec differents objets, differentes orientations
        TP_DirectedFace face1 = edge1.leftFace().topo();
        System.out.println("identifiant de la face1 : "+face1.getId());
        TP_DirectedFace face2 = edge1.rightFace().topo();
        System.out.println("identifiant de la face2 : "+face2.getId());        

        // autre maniere de construire un TP_Expression
        exp = face1.asTP_Expression();
        exp = exp.plus(face2.asTP_Expression());
        
        // boundary (a comparer avec le boundary de chacune des faces)
        System.out.println("boundary...");
        bdy = exp.boundary();
        for (int i=0; i<bdy.sizeTerm(); i++) {
            TP_DirectedTopo dt = bdy.getTerm(i);
            System.out.println(dt.getClass().getName()+"    "+dt.getId());
            System.out.println(" feature : "+((TP_DirectedEdge)dt).topo().getFeature().getId());
        }
        

    }
            
}
