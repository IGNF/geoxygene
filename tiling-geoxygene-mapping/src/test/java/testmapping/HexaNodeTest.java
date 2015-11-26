package testmapping;

import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
/*
 * @author Dr Tsatcha D.
 */
public class HexaNodeTest {

    @Test
    public void testHexaNode() {

        Coordinate c = new Coordinate(0, 0, 0);
        Point center = new GeometryFactory().createPoint(c);

        HexaNode hex = new HexaNode(2, 0, 0, center, 4);
        hex.setRADIUS(8);
        /*
         * test d'affichage de l'hexagone..;
         */
        System.out.println(hex.toString());

        /*
         * Test du calcul de l'angle du premier sommet
         */

        System.out.println("l'angle du premier sommet est"
                + hex.firstSommentAngle());
        /*
         * Test du rayon d'une generation ..;
         */

        /*
         * Test sur le calcul du rayon d'une generation connaissant le rayon de
         * la generation courant ..; Le celules qui ont une valeur de generation
         * plus grande que moi ont un rayon au dessus de moi...
         */

        System.out.println("calcul du rayon " + hex.computecurrentRay(1));

        /*
         * 
         * Test du calcul du premier sommet qui permet de generer les autres
         * sommets ... Cette test montre aussi que la rotation fonctionne bien
         */

        System.out.println("calcul du rayon "
                + hex.generateFirstSommet().toText());

        /*
         * Test des fonctions utiles à l'hexagone ..;
         */

        HexaGoneUtil hexaUtil = new HexaGoneUtil();
        
        /*
         * Recherche de l'hexagone (0,0) axe du repère
         * ______________>
         * |
         * |
         * |
         * |
         * |
         */

        hexaUtil.computeHexaCenterFromPoint(hex.getRADIUS(), 2.5, 2.5);

        System.out.println("calcul de l'hexagone connaissant le cadre  "
                + hexaUtil.getRADIUS());
        System.out.println("du centreX  " + hexaUtil.getCenterX());

        System.out.println("du centreY  " + hexaUtil.getCenterY());

        System.out.println("du indiceI  " + hexaUtil.getCenterI());

        System.out.println("du indiceJ  " + hexaUtil.getCenterJ());
        
        
        hexaUtil.computeHexaCenterFromPoint(hex.getRADIUS(), 4, 5);

        System.out.println("calcul de l'hexagone connaissant le cadre  "
                + hexaUtil.getRADIUS());
        System.out.println("du centreX  " + hexaUtil.getCenterX());

        System.out.println("du centreY  " + hexaUtil.getCenterY());

        System.out.println("du indiceI  " + hexaUtil.getCenterI());

        System.out.println("du indiceJ  " + hexaUtil.getCenterJ());
        
        
        
        hexaUtil.computeHexaCenterFromPoint(hex.getRADIUS(), 4, 15);

        System.out.println("calcul de l'hexagone connaissant le cadre  "
                + hexaUtil.getRADIUS());
        System.out.println("du centreX  " + hexaUtil.getCenterX());

        System.out.println("du centreY  " + hexaUtil.getCenterY());

        System.out.println("du indiceI  " + hexaUtil.getCenterI());

        System.out.println("du indiceJ  " + hexaUtil.getCenterJ());
        
        System.out.println("Les differents noeuds en impression");
        
        
        
        hex = new HexaNode(1, 0, 0, center, 4);
        hex.setRADIUS(8);
        
        HexaNeighbor neighbor= new HexaNeighbor(hex);
        
 
        
        // fabrique les hexagones sans leur
        // centre globale par contre avec leur rayon et 
        
        neighbor.generatePossibleSister();

        for (HexaNode nodeH : neighbor.getSisters()) {
            // on determine le centre du en indices
            // des hexagones en question
             hexaUtil = new HexaGoneUtil();
             hexaUtil.computeHexaCenterFromPoint(hex.getRADIUS(),
                    nodeH.getCenter().getX(),  nodeH.getCenter().getX());
            nodeH.setIndiceX(hexaUtil.getCenterI());
            nodeH.setIndiceY(hexaUtil.getCenterJ());
            System.out.println(nodeH.toString());
        }


    }

}
