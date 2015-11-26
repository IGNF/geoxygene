package fr.ign.cogit.mapping.datastructure.hexaTree;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
/*
 * @author Dr Tsatcha D.
 */

public class HexaNode extends Thread implements Runnable  {

    /*
     * @param generation designe la generation à laquelle appartient le noeud en
     * fonction de son parent...Il permet de determiner le rayon, le centre et
     * la nature de l'hexagone
     */
    protected int generation;
    /*
     * @param indiceX designe la valeur de l'indice en abcisse du noeud dans sa
     * generation
     */
    protected int indiceX;
    /*
     * @param indiceY designe la valeur de l'indice en ordonnée du noeud dans sa
     * generation
     */
    protected int indiceY;
    /*
     * @param angle designe l'angle de rotation qui permet de caclculer les
     * sommets
     */
    protected double angle;
    /*
     * @param center designe le centre de l'hexagone
     */
    protected Point center;
    /*
     * @param vectex designe le sommet ass
     */
    protected Point vertex;

    /*
     * @param aperture designe la nature de la couverture 3, 4 ,5
     */

    protected int aperture;

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
     * @param ConnectedSpatialEntities Designe toutes les entités spatiales qui
     * sont contenues dans la cellule hexagonale
     */
    LinkedHashMap<Integer, Record<Geometry>> ConnectedSpatialEntities =
            new LinkedHashMap<Integer, Record<Geometry>>();

    public LinkedHashMap<Integer,  Record<Geometry>> 
    getConnectedSpatialEntities() {
        return ConnectedSpatialEntities;
    }

    public void setConnectedSpatialEntities(
            LinkedHashMap<Integer, 
            Record<Geometry>> connectedSpatialEntities) {
        ConnectedSpatialEntities = connectedSpatialEntities;
    }

    /**
     * param direction designe le sommet auquel nous avons hérité d'un premier
     * parent afin de ne plus etre crée par d'autres frères... Elle peut prendre
     * les valeurs : NE, NO, E, O, SO, SE
     */

    protected String direction = " ";

    /**
     * @param generation
     * @param indiceX
     * @param indiceY
     * @param angle
     * @param center
     */
    public HexaNode(int generation, int indiceX, int indiceY, double angle,
            Point center) {
        super();
                
        this.generation = generation;
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.angle = angle;
        this.center = center;
    }

    public String toString() {
        return "[indiceX" + ":" + indiceX + "]" + " " + "[indiceY" + ":"
                + indiceY + "]" + " " + "[angle" + ":" + angle + "]" + " "
                + "[center" + ":" + center.toText() + "]";
    }

    /*
     * @param return generation
     */
    public int getGeneration() {
        return generation;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /*
     * @aparam set generation
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /*
     * @param return indice de l'hexagone en abcisse
     */
    public int getIndiceX() {
        return indiceX;
    }

    // enregistre l'indice en abscisse de l'hexagone
    public void setIndiceX(int indiceX) {
        this.indiceX = indiceX;
    }

    // return l'indice en ordonné de l'hexagone
    public int getIndiceY() {
        return indiceY;
    }

    // enregistre l'indice en ordonnée de l'hexagone
    public void setIndiceY(int indiceY) {
        this.indiceY = indiceY;
    }

    // retourne l'angle
    public double getAngle() {
        return angle;
    }

    // enregistre l'angle de l'hexagone
    public void setAngle(double angle) {
        this.angle = angle;
    }

    // retourne le centre l'hexagone
    public Point getCenter() {
        return center;
    }

    // enregistre le centre de l'hexagone
    public void setCenter(Point center) {
        this.center = center;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HexaNode))
            return false;
        HexaNode hexNode = (HexaNode) obj;
        if (indiceX == hexNode.getIndiceX() && indiceY == hexNode.getIndiceY()
                && generation == hexNode.getGeneration()
                && aperture == hexNode.getAperture()) {
            return true;
        }

        return false;
    }

    // retourne la valeur de la couverture
    public int getAperture() {
        return aperture;
    }

    /**
     * @param generation
     * @param indiceX
     * @param indiceY
     * @param center
     * @param aperture
     *            à partir de la valeur de la couverture (aperture) on peut
     *            calculer l'angle
     */
    public HexaNode(int generation, int indiceX, int indiceY, Point center,
            int aperture) {
        super();
        this.generation = generation;
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.center = center;
        this.aperture = aperture;
    }

    /**
     * @param generation
     * @param indiceX
     * @param indiceY
     * @param center
     * @param aperture
     * @param direction
     */
    public HexaNode(int generation, int indiceX, int indiceY, Point center,
            int aperture, String direction) {
        super();
        this.generation = generation;
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.center = center;
        this.aperture = aperture;
        this.direction = direction;
    }

    /**
     * @param generation
     * @param indiceX
     * @param indiceY
     * @param center
     * @param vertex
     * @param aperture
     * @param direction
     */
    public HexaNode(int generation, int indiceX, int indiceY, Point center,
            Point vertex, int aperture, String direction) {
        super();
        this.generation = generation;
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.center = center;
        this.vertex = vertex;
        this.aperture = aperture;
        this.direction = direction;
    }

    // enregistre la valeur de l'aperture
    public void setAperture(int aperture) {
        this.aperture = aperture;
    }

    // fabrique le premier sommet d'un hexagone au cours de la generation de ces
    // 6 voisins.
    public Double firstSommentAngle() {
        if (aperture == 4) {
            return generation % 2 * (Math.PI / 2);

        }
        return null;
    }

    public Point getVertex() {
        return vertex;
    }

    public void setVertex(Point vertex) {
        this.vertex = vertex;
    }

    // permet de determiner le rayon associe à l'hexagone
    // en prenant en compte :
    // @param generationParam sa generation
    // @param aperture la nature de la couverture
    // @param rayon son rayon...
    public Double computecurrentRay(int generationParam) {
        if (aperture == 4) {

            return RADIUS * Math.pow(2, generationParam - generation);

        }
        return null;
    }

    /*
     * @return Raduis le rayon de l'hexagon
     */
    public double getRADIUS() {
        return RADIUS;
    }

    /*
     * @param raDuis charge le rayon de l'hexagon..;
     */
    public void setRADIUS(double rADIUS) {
        RADIUS = rADIUS;
    }

    /*
     * generate la premieres celulles du noeuds
     * 
     * @param rayon designe de l'hexagone retourne le premier sommet qui permet
     * de generer les autres somment de l'hexagone
     */

    public Point generateFirstSommet() {
        Point ptImage, pt;
        double abscisse = 0.0;
        double ordinate = 0.0;
        // le point sur la vertical est :
        // on fait -Raduis pour garder le sens 
        // de projection de l'ecran...
        abscisse = this.center.getX() - RADIUS;
        ordinate = this.center.getY();
        // la transformation affine pour generer
        // le decalage en fonction de l'aperture.

        Coordinate c = new Coordinate(abscisse, ordinate, 0);
        pt = new GeometryFactory().createPoint(c);
        Rotation rt = new Rotation(firstSommentAngle(), this.center, RADIUS);

        ptImage = rt.makeRotation(pt);

        return ptImage;

    }
    
    /*
     * Cette methode renvoie l'image de l'hexagone
     * en fait rentourne le centre de l'hexagone image...
     * Cependant on devra rechercher l'ensemble de noeuds 
     * connectés à ce nouveau hexagone...
     */

    public HexaNode imageHexaNode(AffineTransform2D transformation) {

        Point2D pointA = new Point2D.Double(center.getX(), center.getY());

        Point2D pointImgA = transformation.transform(pointA, null);

        Coordinate c1 = new Coordinate(pointImgA.getX(), pointImgA.getY());

        Point newCenter = new GeometryFactory().createPoint(c1);

        center = newCenter;
        HexaGoneUtil utilHex = new HexaGoneUtil();
        utilHex.computeHexaCenterFromPoint(newCenter.getX(), newCenter.getY());
        HexaNode hex = new HexaNode(generation, indiceX, indiceY, angle,
                newCenter);

        return hex;

    }

    /**
     * 
     */
    public HexaNode() {
        super();
        // TODO Auto-generated constructor stub
    }
    
  
    

}