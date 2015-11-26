package fr.ign.cogit.mapping.util;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.hexaTree.Rotation;
import fr.ign.cogit.mapping.webentity.spatial.Constants;

/*
 * Cette classe permet d'apporter les méthodes supplémentaires
 * à la gestion des hexagones..
 * Inspirée de l'article IJGIS 
 * 
 * [Tsatcha et al., 2014] Dieudonné Tsatcha, Eric Saux, Christophe 
 * Claramunt, A Bidirectional Path-Finding Algorithm and Data Structure
 * for Maritime Routing, International Journal of Geographical
 *  Information Science(IJGIS),24 pp., 2013,
 * Taylor \& Francis (2013 Impact Factor: 1.479)
 * Dr Tsatcha D.
 */
/*
 * Cette classe permet de calculer les coordonnées d'un hexagone qui recouvre
 * un  point données...
 * Les coordonnées peuvent etre : 
 * son centre 
 * les indices de son centre
 * 
 * En fonction du cadre il faut intialiser 
 * la hauteur
 * la largeur
 * et le SIDE de l'hexagone 
 * @see  Cadre.
 * @author Dr Tsatcha D.
 */
public class HexaGoneUtil {

    /**
     * 
     */
    public HexaGoneUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);

    /*
     * @param SIDE de l'hexagone
     */

    protected double SIDE;
    /*
     * @param RADIUS
     */
    protected double RADIUS;
    /**
     * Cell height
     */
    protected double HEIGHT;
    /**
     * Cell width
     */
    protected double WIDTH;
    /*
     * indice du centre en abscisse
     */
    protected int CenterI;

    /*
     * indice du centre en ordonnée
     */
    protected int CenterJ;

    /*
     * coordonnée du centre en abscisse
     */
    protected double CenterX;
    /*
     * coordonnée du centre en ordonnée
     */
    protected double CenterY;

    /*
     * @param setHexa Designe une liste de noeuds voisins à un hexagone
     * 
     * @return le polygon hexagone associé
     */

    public static Polygon builHexagone(List<HexaNode> setHexa) {
        Polygon geom = null;
        Coordinate[] coordinates = generateRingFromNodes(setHexa);

        if (coordinates == null || coordinates.length < 3) {

            throw new RuntimeException("la liste des noeuds en entrée"
                    + " ne permettent pas de former un polynome");
        } else {

            LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coordinates);
            // notre polygone n'a pas de trous
            LinearRing[] holes = null;
            geom = GEOMETRY_FACTORY.createPolygon(shell, holes);

        }

        return geom;

    }

    // Cette methode permet de rechercher
    // le ring du polygone d'un ensemble des celules voisines
    /*
     * @param setHexa Designe la liste des polygones voisins
     * 
     * @@param une liste de coordonnées qui forment un ring...
     */
    public static Coordinate[] generateRingFromNodes(List<HexaNode> setHexa) {

        Coordinate[] coordinates = null;
        Coordinate coord = null;
        if (setHexa.size() > 0 && setHexa.size() ==6) {

            // coord=new Coordinate(lst.get(i).getSom().getPt().getLong(),
            // lst.get(i).getSom().getPt().getLat(), 0);

            coordinates = new Coordinate[] {
                    new Coordinate(setHexa.get(0).getVertex().getX(), setHexa
                            .get(0).getVertex().getY()),

                    new Coordinate(setHexa.get(1).getVertex().getX(), setHexa
                            .get(1).getVertex().getY()),

                    new Coordinate(setHexa.get(2).getVertex().getX(), setHexa
                            .get(2).getVertex().getY()),

                    new Coordinate(setHexa.get(3).getVertex().getX(), setHexa
                            .get(3).getVertex().getY()),

                    new Coordinate(setHexa.get(4).getVertex().getX(), setHexa
                            .get(4).getVertex().getY()),

                    new Coordinate(setHexa.get(5).getVertex().getX(), setHexa
                            .get(5).getVertex().getY()),

                    new Coordinate(setHexa.get(0).getVertex().getX(), setHexa
                            .get(0).getVertex().getY()),

            };

        } else {

            throw new RuntimeException("la taille" + "  " + setHexa.size()
                    + "  n'est pas suffisant pour representer" + "un hexagone");
        }

        return coordinates;
    }

    /*
     * Cette methode permet de generer les coordonnées d'un hexagone pour une
     * coordonnées abcisse donnée et ordonnée donnée...
     */

    // cette methode permet de recuperer les
    // coordonnées de l'hexagone qui recouvre un point donnée

    public void computeHexaCenterFromPoint(double x, double y) {
//        RADIUS = Math.abs(radius);
//        WIDTH = Math.abs(radius) * 2;
//        HEIGHT = (((float) radius) * Math.sqrt(3));
//        SIDE = radius * 3 / 2;

        setCellByPoint(x, y);

    }
    
    
    public void computeHexaCenterFromPoint(double radius, double x, double y) {
      RADIUS = Math.abs(radius);
      WIDTH = Math.abs(radius) * 2;
      HEIGHT = (((float) radius) * Math.sqrt(3));
      SIDE = radius * 3 / 2;

      setCellByPoint(x, y);

  }

    // cette methode permet d'initialiser les coordonnées de l'hexagone
    // en fonction du cadre de visualisation de l'outil...
    // permet ainsi de synchroniser le dispositif de visualisation et
    // le maillage hexagonal
    /*
     * @param height Designe la hauteur du dispositif de visualisation qui se
     * compose de 4 hexagone
     * 
     * @param weight Designe la largeur du dispositif Un cadre dans notre model
     * comprend 4 hexagones Donc les propriétés sont ainsi definies
     */
    
  
    public double getSIDE() {
        return SIDE;
    }

    public void setSIDE(double sIDE) {
        SIDE = sIDE;
    }

    public double getRADIUS() {
        return RADIUS;
    }

    public void setRADIUS(double rADIUS) {
        RADIUS = rADIUS;
    }

    public double getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(double hEIGHT) {
        HEIGHT = hEIGHT;
    }

    public double getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(double wIDTH) {
        WIDTH = wIDTH;
    }

    public int getCenterI() {
        return CenterI;
    }

    public void setCenterI(int centerI) {
        CenterI = centerI;
    }

    public int getCenterJ() {
        return CenterJ;
    }

    public void setCenterJ(int centerJ) {
        CenterJ = centerJ;
    }

    public double getCenterX() {
        return CenterX;
    }

    public void setCenterX(double centerX) {
        CenterX = centerX;
    }

    public double getCenterY() {
        return CenterY;
    }

    public void setCenterY(double centerY) {
        CenterY = centerY;
    }

    /*
     * @param x l'abscisse
     * 
     * @param y l'ordonnée
     * 
     * permet de calculer les indices de l'hexagone qui recouvre le point en
     * question...
     */
    public void setCellByPoint(double x, double y) {
        int ci = (int) Math.floor(x / (SIDE));
        int cx = (int) (x - SIDE * ci);

        double ty = y - (ci % 2) * HEIGHT / 2;
        int cj = (int) Math.floor(ty / HEIGHT);
        int cy = (int) (ty - HEIGHT * cj);

        if (cx > Math.abs(RADIUS / 2 - RADIUS * cy / HEIGHT)) {
            setCellIndex(ci, cj);
        } else {
            setCellIndex(ci - 1, cj + (ci % 2) - ((cy < HEIGHT / 2) ? 1 : 0));
        }
    }

    /*
     * 
     */

    public void setCellIndex(int i, int j) {
        CenterI = i;
        CenterJ = j;
        CenterX = i * SIDE;
        CenterY = HEIGHT * (2 * j + (i % 2)) / 2;
        // System.out.println("mX est egale a" + CenterX + "my est egale à" +
        // CenterY);
    }

    /*
     * Generer la liste des hexagones voisines à un hexagone ... connaissant
     * deja la liste de ces voisins construits...
     */

}
