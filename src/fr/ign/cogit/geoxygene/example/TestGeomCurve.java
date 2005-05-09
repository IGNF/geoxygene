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

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbOracle;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_CurveBoundary;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;


/**
 * Utilisation du package spatial pour les primitives linéaires :  Exemple de code.
 * On suppose qu'il existe une classe persistante "donnees.defaut.Troncon_route" 
 * (sinon changer le nom de la classe dans le code).
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class TestGeomCurve {

    /* Attributs */
    private Geodatabase db;
    private Class tronconClass;	// classe de troncons  
    private String nomClasse = "donnees.defaut.Troncon_route"; // nom de la classe a charger
    int identifiant1 = 50736;    // identifiant de l'objet qu'on va charger
    int identifiant2 = 50717;    // identifiant pour former une composite curve
    int identifiant3 = 50716;    // identifiant pour former une composite curve
    int identifiant4 = 50724;    // identifiant pour former une composite curve
    
    /* Creates new GeomCurve */
    public TestGeomCurve() {
        db = new GeodatabaseOjbOracle();
        try {
            tronconClass = Class.forName(nomClasse);
        } catch (ClassNotFoundException e) {
            System.out.println(nomClasse+" : non trouvee");  
            System.exit(0);     
        }        
    }


    
    /* Methode main */
    public static void main (String args[]) {
        
        TestGeomCurve test = new TestGeomCurve();

        test.testOrientation();
        test.testBoundary();
        test.testComposite();
        
    }
        

    
    /* Amusons nous avec les notions de courbes orientees et de primitive */
    public void testOrientation ()  {
             
        /////////////////////////////////////////////////////////////////////////////////////////////
        // Pour bien comprendre ce qui suit :
        // GM_Curve herite de GM_OrientableCurve, qui herite de GM_OrientablePrimitive (abstrait), qui herite de GM_Primitive (abstrait).
        // Une primitive possede 2 primitives orientees (+1 et -1) auxquelles on accede par 
        // getPositive() (pour avoir celle orientee positivement) et getNegative(pour avoir celle orientee negativement).
        // Soit oriPrim une primitive orientee. On a les proprietes suivantes :
        // oriPrim.getOrientation() = +1 => oriPrim.getPositive() = oriPrim.
        // oriPrim.getOrientation() = -1 => oriPrim.getNegative() = oriPrim.
        // oriPrim.getPositive().getNegative() = oriPrim.
        // Une GM_Curve est PAR DEFINITION la primitive orientee positivement.
        // Une GM_OrientableCurve orientee positivement EST une GM_Curve.
        // Soit curve une GM_Curve. On a la propriete suivante : curve.getPositive() = curve.
        // L'operateur getPrimitive() permet de recuperer la GM_Curve d'une courbe orientee.
        // Soit curve une GM_Curve. On a la propriete suivante : curve.getPrimitive() = curve.
        // Soit oriCurve une courbe orientee negativement construite avec curve.getNegative().
        // Alors oriCurve.getPrimitive() renvoie une GM_Curve renversee par rapport a curve.
        // Donc pour renverser une courbe, il faut faire : curve.getNegative().getPrimitive()
        ///////////////////////////////////////////////////////////////////////////////////////////////
                       
        // Declaration des variables
        FT_Feature tron;
        Integer gid;
        GM_Curve curve;
        GM_Primitive prim;
        GM_OrientableCurve oriCurve;
        
        System.out.println("#### test orientation");
        
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
        
        // On charge un FT_Feature par son identifiant, avec OJB
        // Remarque : l'identifiant doit etre de type Integer et non int
        gid = new Integer(identifiant1);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
                 
        // Examinons la geometrie du FT_Feature
        curve = (GM_Curve)tron.getGeom();
        System.out.println(curve);
        System.out.println("orientation : "+curve.getOrientation());
        // l'orientation vaut +1 : c'est normal, c'est une GM_Curve
                
        // A SAVOIR : le "hashCode" est lie la place en memoire de l'objet. 
        // meme hashCode <==> meme objet
                
        // Examinons la primitive de la geometrie du FT_Feature
        prim = curve.getPrimitive();
        System.out.println("hash code de la courbe : "+curve.hashCode());
        System.out.println("hash code de la primitive : "+prim.hashCode());
        // curve et prim sont le meme objet : normal, car l'orientation est positive
                
        // Examinons la courbe orientee positivement a partir de curve
        oriCurve = curve.getPositive();
        System.out.println("nouvelle orientation : "+oriCurve.getOrientation());
        System.out.println("hash code de la courbe orientee positivement : "+oriCurve.hashCode());
        //curve, prim, et oriCurve sont le meme objet : normal, car l'orientation est positive
                
        // Examinons la courbe orientee negativement a partir de curve
        oriCurve = curve.getNegative();
        System.out.println("nouvelle orientation : "+oriCurve.getOrientation());
        System.out.println("hash code de la courbe orientee negativement : "+oriCurve.hashCode()); 
        // c'est un nouvel objet
                
        // Examinons la primitive de la courbe orientee negativement
        prim = oriCurve.getPrimitive();
        System.out.println("type de la primitive :"+prim.getClass().getName());
        curve = (GM_Curve)prim; // cast - on reutilise le nom de variable curve
        System.out.println(curve);
        // la primitive renvoie une courbe renversee par rapport a tout a l'heure
        System.out.println("hash code de la primitive de la courbe orientee negativement: "+curve.hashCode()); 
        // c'est un nouvel objet
                
        // Jouons avec la courbe orientee negativement oriCurve
        // oriCurve etant deja orientee negativement, son "getNegative" renvoie le meme objet
        System.out.println("orientation de la courbe orientee negativement : "+(oriCurve.getNegative()).getOrientation());
        System.out.println("hash code de la courbe orientee negativement : "+(oriCurve.getNegative()).hashCode());
                
        // Oh miracle, le "getPositive" sur oriCurve renvoie le meme objet que la courbe initiale
        // Donc : curve.getNegative().getPositive() = curve
        System.out.println("orientation de la courbe orientee positivement : "+(oriCurve.getPositive()).getOrientation());
        System.out.println("hash code de la courbe orientee positivement : "+(oriCurve.getPositive()).hashCode());
                
        // Jouons avec la primitive de la courbe orientee negativement (cette primitive s'appelle curve)
        System.out.println("hash code de la primitive : "+curve.hashCode());  // on l'a deja affiche plus haut
        System.out.println("hash code du getPositive() de la  primitive : "+curve.getPositive().hashCode());  // c'est le meme
        System.out.println("hash code du getNegative() de la  primitive : "+curve.getNegative().hashCode());  // c'est un nouvel objet
        System.out.println("hash code de la primitive du getNegative() de la  primitive : "+curve.getNegative().getPrimitive().hashCode());
        // et voila, on retrouve encore la courbe initiale
        // Donc : curve.getNegative().getPrimitive().getNegative().getPrimitive() = curve
        
        // Fin
        System.out.println("Commit");
        db.commit();
             
    }
        

    
    /* Amusons nous avec les frontieres */
    public void testBoundary ()  {
                
        // Declaration des variables
        FT_Feature tron;
        Integer gid;
        GM_Curve curve;
        GM_OrientableCurve oriCurve;
        GM_CurveBoundary curveBdy;

        System.out.println("#### test boundary");
        
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
                
        // On charge un FT_Feature et sa geometrie
        gid = new Integer(identifiant1);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
        curve = (GM_Curve)tron.getGeom();
                
        // Frontiere de la courbe en passant par GM_CurveBoundary
        // l'operation "boundary" recupere un GM_CurveBoundary
        // l'operation getEndPoint() et getStartPoint() recupere des GM_Point.
        // Donc : on connait le CRS
        curveBdy = curve.boundary();
        System.out.println("start point  : "+curveBdy.getStartPoint());
        System.out.println("end point  : "+curveBdy.getEndPoint());  
        System.out.println("CRS du end point : "+curveBdy.getEndPoint().getCRS());
        
        // Frontiere de la courbe en passant par startPoint() et endPoint()
        // on recupere des DirectPosition
        // Il n'y a pas de CRS a priori
        System.out.println("start point  : "+curve.startPoint());
        System.out.println("end point  : "+curve.endPoint());
                
        // Une petite experience...
        // On change la valeur de la coordonnees Y du premier point de la courbe
        double oldY = ((GM_LineString)curve.getSegment(0)).getControlPoint(0).getY();
        ((GM_LineString)curve.getSegment(0)).getControlPoint(0).setY(100000.0);
        // On regarde
        System.out.println("nouveau start point(Y)  : "+curveBdy.getStartPoint().getPosition().getY()); // ca n'a pas change
        System.out.println("nouveau start point(Y)  : "+curve.startPoint().getY()); // ici, ca a change
        // Pourquoi ? avec startPoint() on accede directement aux coordonnees, 
        // alors qu'avec "boundary" on a cree des nouveaux points "independants" des coordonnees de la courbe.
        // Il faut donc rappeler la methode "boundary" pour repercuter le changement de coordonnees.
        curveBdy = curve.boundary();
        System.out.println("nouveau start point(Y)  : "+curveBdy.getStartPoint().getPosition().getY()); // OK maintenant
        
        // on reaffecte l'ancienne valeur ...        
        ((GM_LineString)curve.getSegment(0)).getControlPoint(0).setY(oldY);                
                
        // Rejouons avec les courbes orientees
        oriCurve = curve.getNegative();
        // la methode "boundary()" s'applique sur GM_OrientableCurve (et donc aussi sur GM_Curve)
        curveBdy = oriCurve.boundary();
        System.out.println("start point  : "+curveBdy.getStartPoint());
        System.out.println("end point  : "+curveBdy.getEndPoint());  
        // startPoint() et endPoint() ne marche que sur GM_Curve (et pas GM_OrientableCurve) -> on passe par la primitive
        curve = oriCurve.getPrimitive();
        System.out.println("start point  : "+curve.startPoint());
        System.out.println("end point  : "+curve.endPoint());                
        
        // Commit
        System.out.println("Commit");    
        db.commit();
  
    }
    
    
  
    /* Amusons nous avec les composite curves */
    public void testComposite ()  {
           
        // Declaration des variables
        FT_Feature tron;
        Integer gid;
        GM_Curve curve;
        GM_Primitive prim;
        GM_OrientableCurve oriCurve;
        GM_CompositeCurve compCurve;
        GM_CurveBoundary curveBdy;
        
        System.out.println("#### test composite");        
        
        // on recupere la tolerance dans les metadonnees d'Oracle
        // rigoureusement ici, on recupere la tolerance sur les X, on suppose que c'est la meme sur les Y
        double tolerance = db.getMetadata(tronconClass).getTolerance(0);
        System.out.println("tolerance : "+tolerance);
                
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
                
        // On charge un FT_Feature et sa geometrie
        gid = new Integer(identifiant1);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
        curve = (GM_Curve)tron.getGeom();
        System.out.println(curve);
                
        // On cree une composite curve a partir d'une courbe orientee (positivement ou negativement)
        //compCurve = new GM_CompositeCurve(curve.getNegative());
        compCurve = new GM_CompositeCurve(curve);
                
        // On charge un FT_Feature et sa geometrie
        gid = new Integer(identifiant2);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
        curve = (GM_Curve)tron.getGeom();
        System.out.println(curve);
        
        // On ajoute un element a la composite curve
        //compCurve.appendGenerator(curve);             //  sans filet (pas de parametre "tolerance")
        //compCurve.appendGenerator(curve,tolerance);     // avec verification du chainage, mais sans retourner la courbe  
        // essayer en rompant le chainage pour voir.
        
        // proprement, il faut faire comme ceci :
        // si ca chaine, on ajoute, sinon on ajoute l'oppose, 
        // sinon affiche un message et on continue sans avoir rien fait en affichant une exception
        try {
            compCurve.addGeneratorTry(curve,tolerance); 
        } catch (Exception e1) {
                System.out.println(e1.getMessage());
        }
                
        // On charge un FT_Feature et sa geometrie
        gid = new Integer(identifiant3);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
        curve = (GM_Curve)tron.getGeom();
        System.out.println(curve);
                
        // On ajoute encore
        try {
            compCurve.addGeneratorTry(curve,tolerance); 
        } catch (Exception e1) {
                System.out.println(e1.getMessage());
        }
                
        // On charge un FT_Feature et sa geometrie
        gid = new Integer(identifiant4);
        tron = (FT_Feature)db.load(tronconClass,gid);
        System.out.println("identifiant de l'objet charge : "+tron.getId());
        curve = (GM_Curve)tron.getGeom();
        System.out.println(curve);
        
        
        // On ajoute encore
        try {
            compCurve.addGeneratorTry(curve,tolerance); 
        } catch (Exception e1) {
                System.out.println(e1.getMessage());
        }
           
        // Verifions que le chainage est OK
        System.out.println("validate : "+compCurve.validate(tolerance));
                
        // Examinons la frontiere
        curveBdy = compCurve.boundary();
        System.out.println("start point  : "+curveBdy.getStartPoint());
        System.out.println("end point  : "+curveBdy.getEndPoint());
                
        // Examinons la primitive
        System.out.println("nombre de composants de la composite curve : "+compCurve.sizeGenerator());
        curve = compCurve.getPrimitive();
        System.out.println("hash code de la primitive : "+curve.hashCode());
        System.out.println(curve);
        // on observe qu'il y a des doublon : la courbe est en fait constituee de plusieurs segments 
        //(corresondant aux courbes initiales)
        System.out.println("nombre de segments de la primitive : "+curve.sizeSegment());
        // pour eliminer les doublons : caster en une seule linestring
        GM_LineString theLinestring = curve.asLineString(0.0,0.0,tolerance);
        System.out.println("linestring : ");
        System.out.println(theLinestring);
                        
        // Examinons les courbe orientees  a partir de compCurve
        oriCurve = compCurve.getPositive();
        System.out.println("hash code de la courbe orientee positivement : "+oriCurve.hashCode());  // meme chose que la primitive
        oriCurve = compCurve.getNegative();
        System.out.println("hash code de la courbe orientee negativement : "+oriCurve.hashCode());  // nouvel objet
        System.out.println(oriCurve.getPrimitive()); // courbe renversee par rapport a plus haut
        
        // Utilisons GM_Ring(anneau)
        // constructeur a partir d'une compCurve - sans filet
        // GM_Ring ring = new GM_Ring(compCurve); 
        
        // constructeur a partir d'une compCurve - avec verification du chainage et de la fermeture
        try {
            GM_Ring ring = new GM_Ring(compCurve, tolerance);
            System.out.println("validate ring :"+ring.validate(tolerance));
        } catch (Exception e) {System.out.println(e.getMessage());}
                    
        // Commit
        System.out.println("Commit");
        db.commit();
    }
    
}
