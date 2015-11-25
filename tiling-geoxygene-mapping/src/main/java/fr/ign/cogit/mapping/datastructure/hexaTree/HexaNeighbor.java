package fr.ign.cogit.mapping.datastructure.hexaTree;

import java.util.ArrayList;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.util.HexaGoneUtil;

/*
 * Cette classe permet de construire les cellules voisines
 * d'une hexagone avec leurs coordonnées...
 * On doit copnnaitre le rayon et l'aperture de l'hexagon en question
 * afin de calculer le premier sommet.
 */

public class HexaNeighbor {

    // protected Vector<Node>sisters;
    protected HexaNode[] sisters = null;
    protected HexaNode hex;

    public HexaNeighbor(HexaNode hexParam) {
        // lorsque certaines cellules voisines
        // sont deja connues
        hexParam.setDirection("inconnu");
        HexaNode node1 = hexParam;
        // realise un ensemble de direction innconnu à l'initialisation
        this.sisters = new HexaNode[] { node1, node1, node1, node1, node1,
                node1 };
        hex = hexParam;
    }

    /*
     * Indique la liste des cellules voisines qui n'ont pas encore été
     * identifiées ou calculées...-
     */

    public int sizeVosins() {
        int taille = 0;
        if (sisters != null) {
            for (int i = 0; i < sisters.length; i++) {
                if (sisters[i].getDirection() != "inconnu") {
                    taille++;
                }

            }
        }
        return taille;
    }

    /*
     * @return sisters
     */
    public HexaNode[] getSisters() {
        return sisters;
    }

    /*
     * @param sisters permet de charger la listes des soeurs.
     */
    public void setSisters(HexaNode[] sisters) {
        this.sisters = sisters;
    }

    //
    /*
     * Ajouter un nouveau noeud frère au node courant dans la direction indiquée
     */
    public void addnewsister(HexaNode node) {
        int taille = 0;
        if (node != null) {
            // for (int i=0; i<sisters.length;i++ ){
            if (node.getDirection().equals("NE")
                    && sisters[0].getDirection().equals("inconnu")) {
                this.sisters[0] = node;

                return;
            }
            if (node.getDirection().equals("E")
                    && sisters[1].getDirection().equals("inconnu")) {
                this.sisters[1] = node;

                return;
            }
            if (node.getDirection().equals("SE")
                    && sisters[2].getDirection().equals("inconnu")) {
                this.sisters[2] = node;

                return;
            }
            if (node.getDirection().equals("SO")
                    && sisters[3].getDirection().equals("inconnu")) {
                this.sisters[3] = node;

                return;
            }
            if (node.getDirection().equals("O")
                    && sisters[4].getDirection().equals("inconnu")) {
                this.sisters[4] = node;

                return;
            }
            if (node.getDirection().equals("NO")
                    && sisters[5].getDirection().equals("inconnu")) {
                this.sisters[5] = node;

                return;
                // }
            }
            // }

        }

    }

    // permet d'identifier les noeuds qui n'ont pas encore
    // été calculer dans le voisinage
    public String[] ListStringNoUse() {
        List<HexaNode> lst = new ArrayList<HexaNode>();

        String[] direction = new String[] { "NE", "E", "SE", "SO", "O", "NO" };
        String[] directionsortie = new String[] { "NE", "E", "SE", "SO", "O",
                "NO" };

        if (sisters != null) {
            if (sisters.length > 0) {
                for (int i = 0; i < sisters.length; i++) {
                    // la taille de ldsortie est à chaque fois reduite:
                    for (int j = 0; j < directionsortie.length; j++) {
                        if (sisters[i].getDirection()
                                .equals(directionsortie[j])) {
                            directionsortie[j] = "inconnu";

                        }
                    }
                }
            }
        }
        return directionsortie;
    }

    // cette methode determine la valeur de l'angle associée
    // à un sommet donné de l'hexagone, c'est à dire une direction
    /*
     * @param directValue designe la valeur de la direction
     * 
     * @return angle
     */

    public Double valueOfAngle(String directValue) {
        List<HexaNode> lst = new ArrayList<HexaNode>();
        Double angle = 0.0;
        List<String> lstSortie = new ArrayList<String>();
        String[] direction = new String[] { "NE", "E", "SE", "SO", "O", "NO" };
        // lst= ListActivateDirection(pt);
        // for ( Node node : lst){
        if (directValue != null) {
            for (int i = 0; i < direction.length; i++) {

                if (direction[i].equals(directValue)) {
                    angle = (i + 1) * Math.PI / 3;
                    break;
                }
            }

        }

        return angle;
    }

    /*
     * determine les dirferents angles qui n'ont pas encore été calculés parmi
     * les sisters.
     */

    public List<Double> angleNotuse() {
        List<Double> lstAngle = new ArrayList<Double>();
        String[] directionsortie = null;
        String[] direction = new String[] { "NE", "E", "SE", "SO", "O", "NO" };

        if (sisters != null) {
            for (int i = 0; i < direction.length; i++) {
                for (int j = 0; j < sisters.length; j++) {
                    if (sisters[j].getDirection().equals(direction[i])) {
                        lstAngle.add(valueOfAngle(direction[i]));
                        break;
                    }
                    if (j == direction.length - 1) {

                        lstAngle.add(valueOfAngle(direction[i]));

                    }

                }

            }

        } else {
            // dans le cas qu'au voisin du noeud n'a pas été
            // construit alors on construit tous les susceptibles
            // sisters...;
            for (int i = 0; i < direction.length; i++) {
                lstAngle.add(valueOfAngle(direction[i]));
            }

        }

        return lstAngle;

    }

    // construction du premier noeud

    // public HexaNode buildRacine(Point centreE) {
    //
    // HexaNode node = new HexaNode(0, 0, 0, centreE, 4);
    //
    // return node;
    // }

    // public void generatePossibleSister(double HeightCadre, double
    // WeightCadre) {
    // String[] direction = new String[] { "NE", "E", "SE", "SO", "O", "NO" };
    // List<HexaNode> nodeList = new ArrayList();
    // List<Double> listAngles = null;
    // String orientation = null;
    // Rotation rt = null;
    //
    // Point firstSommet = null, ptNear, pt;
    // // on recherche les angles pas encore calculées
    // // if (vs != null) {
    // listAngles = angleNotuse();
    // // }
    //
    // if (sisters != null) {
    // // for (int i = 0; i < 6; i++) {
    // if (listAngles != null) {
    //
    //
    // if (listAngles.size() > 0) {
    //
    // for (Double angle : listAngles) {
    //
    // // if (i == 0) {
    //
    // // System.out.println("bonjour Ce point position"+ i
    // // +" "+
    // // " noeud entrée"+ nodeE.getIndexNode());
    // // System.out.println(nodeE.toString());
    // // nous calculons le voisin de proche
    // // afin de deduire le centre du prochain côté.
    // rt = new Rotation(angle - Math.PI / 3, hex.getCenter(),
    // hex.getRADIUS());
    // ptNear = rt.makeRotation(hex.generateFirstSommet());
    //
    // rt = new Rotation(angle, hex.getCenter(),
    // hex.getRADIUS());
    // pt = rt.makeRotation(hex.generateFirstSommet());
    //
    // int i = (int) (angle / (Math.PI / 3));
    // // System.out.print("la valeur de i est :" + i);
    // i = i - 1;
    // if (i < direction.length) {
    // orientation = direction[i];
    // }
    //
    //
    //
    // // nodeS.setSom(s);
    // // rt = new Rotation(Math.PI,
    // // getMiddle(firstSommet.getPt(),
    // // pt),rayon*Math.sqrt(3)/2);
    // HexaGoneUtil hexaUtil = new HexaGoneUtil();
    //
    // hexaUtil.computeHexaCenterFromPoint(HeightCadre,
    // WeightCadre, hex.getCenter().getX(), hex.getCenter().getY());
    // // on met à jour le rayon de l'hexagon en fonction du cadre
    // // fournir en entrée...
    //
    // hex.setRADIUS(hexaUtil.getRADIUS());
    //
    // rt = new Rotation(Math.PI, getMiddle(ptNear, pt),
    // hex.getRADIUS() * Math.sqrt(3) / 2);
    // // from the middle of new line, we compute the associate
    // // middle.
    // Point ptmiddle = rt.makeRotation(hex.getCenter());
    //
    // hexaUtil.computeHexaCenterFromPoint(HeightCadre,
    // WeightCadre, ptmiddle.getX(), ptmiddle.getY());
    // // changer les valeur des indexes...
    // HexaNode hx = new HexaNode(hex.getGeneration(),
    // hexaUtil.getCenterI(), hexaUtil.getCenterJ(),
    // ptmiddle, hex.getAperture(), orientation);
    //
    // // double cost= nodeE.getCost()+Math.random();
    //
    // // node.setCost(Math.random());
    // addnewsister(hx);
    //
    //
    // // nodeList.add(hx);
    // // addNode(nodeE,nodeS);// put the new node indexPosto
    // // tree
    // // System.out.println("end 0");
    //
    // }
    // }
    // }
    // }
    //
    // }
    //
    //
    public List<HexaNode> generatePossibleSister() {
        String[] direction = new String[] { "NE", "E", "SE", "SO", "O", "NO" };
        List<HexaNode> nodeList = new ArrayList();
        List<Double> listAngles = null;
        String orientation = null;
        Rotation rt = null;

        Point firstSommet = null, ptNear, pt;
        // on recherche les angles pas encore calculées
        // if (vs != null) {
        listAngles = angleNotuse();
        // }

        if (sisters != null) {
            // for (int i = 0; i < 6; i++) {
            if (listAngles != null) {

                if (listAngles.size() > 0) {

                    for (Double angle : listAngles) {

                        // if (i == 0) {

                        // System.out.println("bonjour Ce point position"+ i
                        // +" "+
                        // " noeud entrée"+ nodeE.getIndexNode());
                        // System.out.println(nodeE.toString());
                        // nous calculons le voisin de proche
                        // afin de deduire le centre du prochain côté.
                        rt = new Rotation(angle - Math.PI / 3, hex.getCenter(),
                                hex.getRADIUS());
                        ptNear = rt.makeRotation(hex.generateFirstSommet());

                        rt = new Rotation(angle, hex.getCenter(),
                                hex.getRADIUS());
                        pt = rt.makeRotation(hex.generateFirstSommet());

                        int i = (int) (angle / (Math.PI / 3));
                        // System.out.print("la valeur de i est :" + i);
                        i = i - 1;
                        if (i < direction.length) {
                            orientation = direction[i];
                        }

                        // nodeS.setSom(s);
                        // rt = new Rotation(Math.PI,
                        // getMiddle(firstSommet.getPt(),
                        // pt),rayon*Math.sqrt(3)/2);
                        // HexaGoneUtil hexaUtil = new HexaGoneUtil();

                        // on met à jour le rayon de l'hexagon en fonction du
                        // cadre
                        // fournir en entrée...

                        rt = new Rotation(Math.PI, getMiddle(ptNear, pt),
                                hex.getRADIUS() * Math.sqrt(3) / 2);
                        // from the middle of new line, we compute the associate
                        // middle.
                        Point ptmiddle = rt.makeRotation(hex.getCenter());

                        // changer les valeur des indexes...
                        HexaNode hx = new HexaNode(hex.getGeneration(), 0, 0,
                                ptmiddle, hex.getAperture(), orientation);
                        hx.setVertex(ptNear);

                        // double cost= nodeE.getCost()+Math.random();

                        // node.setCost(Math.random());
                        addnewsister(hx);

                        nodeList.add(hx);
                        // addNode(nodeE,nodeS);// put the new node indexPosto
                        // tree
                        // System.out.println("end 0");

                    }
                }
            }
        }
        return nodeList;
    }

    /*
     * @param pt1, pt2 sont deux point quelconque ... on retourne alors leur
     * milieu
     * 
     * @ return pt
     */
    public static Point getMiddle(Point pt1, Point pt2) {
        Point pt = null;

        Coordinate c = new Coordinate(pt1.getX() + pt2.getX() / 2, pt1.getX()
                + pt2.getY() / 2, 0);

        pt = new GeometryFactory().createPoint(c);

        return pt;

    }

    // affiche les informations sur les differents
    // sisters noeud courant
    public String toString() {
        String lst = " Liste des directions";
        if (sisters != null) {
            for (int i = 0; i < sisters.length; i++) {
                lst = lst + " " + sisters[i].getDirection();
            }
        }
        return lst;
    }

}
