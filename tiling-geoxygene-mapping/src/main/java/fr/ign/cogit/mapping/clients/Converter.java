package fr.ign.cogit.mapping.clients;

import java.util.LinkedHashMap;
import java.util.List;

import org.deegree.io.rtree.HyperBoundingBox;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTree;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;

public abstract class Converter {

    /*
     * @param interestPoint Designe le point d'interet
     */
    protected Point interestPoint;

    /*
     * Les paramètres de resolutions du client fournis par le convertisseur...
     */
    /*
     * La hauteur
     */
    protected double height;

    /*
     * La largeur
     */

    protected double weight;

    /**
     * @param interestPoint
     * @param height
     * @param weight
     */
    protected String table;

    /*
     * @param signature designe la signature qui peut etre : geometrie ou
     * emprunte
     */

    protected String signature;

    /*
     * @param hexatree designe l'hexatree associé au au paramètres de cadrage
     * envoyé et le point d'interet... toutes les modifications du client doit
     * s'appliquer sur hexatree...
     */

    protected HexaTree hexatree;
    
  
    /**
     * @param interestPoint
     * @param height
     * @param weight
     * @param table
     * @param signature
     */
    public Converter(Point interestPoint, double height, double weight,
            String table, String signature) {
        super();
        this.interestPoint = interestPoint;
        this.height = height;
        this.weight = weight;
        this.table = table;
        this.signature = signature;
      
    }
    
    
    public Converter(Point interestPoint,
            String table, String signature) {
        super();
        this.interestPoint = interestPoint;
         this.table = table;
        this.signature = signature;
      

    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @param interestPoint
     * @param height
     * @param weight
     * @param table
     */
    public Converter(Point interestPoint, double height, double weight,
            String table) {
        super();
        this.interestPoint = interestPoint;
        this.height = height;
        this.weight = weight;
        this.table = table;
     

    }

    public Converter() {
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Converter(Point interestPoint, double height, double weight) {
        super();
        this.interestPoint = interestPoint;
        this.height = height;
        this.weight = weight;
      

    }

    /**
     * Ce constructeur doit etre utiliser pour gestion des contenus au niveau du
     * support de visualisation..
     * 
     * @param interestPoint
     * @param height
     * @param weight
     * @param hexatree
     */
   

    /*
     * Ce contructeur est appellé pour permettre le chargement des données dans
     * la base de données... et creation des indexes rtree multi-echelles..
     */
    public Converter(String tableParam, String signatureParam) {
        // TODO Auto-generated constructor stub

        this.table = tableParam;
        this.signature = signatureParam;
    
    }

    public Point getInterestPoint() {
        return interestPoint;
    }

    public void setInterestPoint(Point interestPoint) {
        this.interestPoint = interestPoint;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // permet de supprimer une echelle
    public abstract void deleteScale(int value);

    // permet de supprimer une table
    public abstract void deleteTable(String table);

    // permet d'effectuer un translation du cadre
    // change les coordonnées du cadre
    public abstract void translateCadrage(Point interestPoint1,
            Point interestPoint2);

    // permet d'effectuer un zoom du cadre suivant le point
    // d'interet
    // change l'echelle de recherche next echelle
    public abstract void zoomingeCadrage(Point interestPoint);

    // permet de generer dezooming
    public abstract void offZoomingeCadrage(Point interestPoint);

    // permet de gerer la rotation du cadre

    public abstract void rotateCadrage(Point interet, double angle);

    // permet de generer une transformation globale ...
    // homothetie°translation°Rotation

    public abstract void affineTransformation(Point interestPoint1,
            Point interestPoint2);

    public abstract HyperBoundingBox generateScreenParam();

    public abstract List<Record<Geometry>> sendGeomReferTO(Point pt,
            ScaleInfo scale);

    /*
     * fabrique l'hexatree associé au paramètre du convertisseur pour toutes les
     * echelles
     */
    public abstract HexaTree buildHexaTree();

    /*
     * fabrique l'hexatree associé au paramètre du convertisseur correspondant à
     * l'echelle
     * 
     * @param scale
     */
    public abstract HexaTree buildHexaTree(ScaleInfo scale);

    public HexaTree getHexatree() {
        return hexatree;
    }

    public void setHexatree(HexaTree hexatree) {
        this.hexatree = hexatree;
    }
    
    public  String toString(){
        
        return "[interestPoint" + ":" + interestPoint + "]" + " " +
           "[weight" + ":" + weight + "]" + " " +
           "[height" + ":" + height + "]" + " " ; 
        
    }

}
