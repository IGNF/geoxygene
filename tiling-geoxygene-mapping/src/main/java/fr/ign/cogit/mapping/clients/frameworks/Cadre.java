package fr.ign.cogit.mapping.clients.frameworks;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
import fr.ign.cogit.mapping.webentity.spatial.Constants;

/*
 * Un cadre est une zone suivant laquelle
 * le support visuelle peut voir les données sur 
 * la carte dont les echelles sont proposées....
 * Dr Tsatcha D.
 */
public class Cadre {

    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);

    /*
     * @param bornInf designe la borne inférieure du cadre
     */

    protected Point bornInf;

    /*
     * @param designe la borne supérieure du cadre
     */

    protected Point bornSup;

    /*
     * @param name designe le nom du cadre il se compose du terme "cadre"+ la
     * binaire du cadre example: cadre_0, (0..7);
     */

    protected String nameCadre;

    /*
     * @param HEIGHT designe la hauteur de l'hexagone
     */

    protected double HEIGHT;

    /*
     * @param SIDE designe la largeur de l'hexagone
     */

    protected double SIDE;

    /*
     * @param RADUIS Designe le rayon de l'hexagone
     */

    protected double RADIUS;

    /*
     * @param WIDTH designe la largeur des deux extremités de l'hexagone
     */

    protected double WIDTH;
    
    /*
     * Liste des noeud contenus dans le cadre
     */
    protected   HexaNode[] hexaCadre;
    
    protected int[] mixHexaNode=new int [] {-1,-1,-1,-1};
    
    /*
     * @param indicesTranfert...
     * Designe les indices de noeuds des cadres qui vont etre
     * mis a jour ou changer dans la base d'indice...
     * le but est est de simplement modifier ce qui est à modifier...
     */
    protected int[] transfertNode=new int [] {-1,-1,-1,-1};

    public Point getBornInf() {
        return bornInf;
    }

    public void setBornInf(Point bornInf) {
        this.bornInf = bornInf;
    }

    /**
     * @param bornInf
     * @param bornSup
     * @param nameCadre
     */
    public Cadre(Point bornSupParam, Point bornInfParam, String nameCadre) {
        super();
        this.bornInf = bornInfParam;
        this.bornSup = bornSupParam;
        this.nameCadre = nameCadre;
        HexaNode hex =new HexaNode();
        // les cadre hexagone du cadre
        hexaCadre=new HexaNode[]{hex,hex,hex,hex};
    }

    public Point getBornSup() {
        return bornSup;
    }

    public void setBornSup(Point bornSup) {
        this.bornSup = bornSup;
    }

    public String getNameCadre() {
        return nameCadre;
    }

    public void setNameCadre(String nameCadre) {
        this.nameCadre = nameCadre;
    }

    /*
     * cette methode permet de considerer un cadre comme un objet géometrique
     */
    public Polygon buildPolygonOfCadre() {

        Polygon geom = null;
        Coordinate[] coordinates = null;
        Coordinate coord = null;
        coordinates = new Coordinate[] {
                new Coordinate(bornSup.getX(), bornSup.getY(), 0),
                new Coordinate(bornInf.getX(), bornSup.getY(), 0),
                new Coordinate(bornInf.getX(), bornInf.getY(), 0),
                new Coordinate(bornSup.getX(), bornInf.getY(), 0),
                new Coordinate(bornSup.getX(), bornSup.getY(), 0)

        };

        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coordinates);
        // notre polygone n'a pas de trous
        LinearRing[] holes = null;
        geom = GEOMETRY_FACTORY.createPolygon(shell, holes);

        return geom;

    }

    /*
     * Initialisation des paramètres de l'hexagone connaissant le cadre de
     * visualisation car chaque cadre comprenb 4 hexagones de stockage des
     * cellules
     */

    public void initialiseHexaFromCadre() {
        double WeightCadre = Math.abs(bornSup.getX() - bornInf.getX());
        double HeightCadre = Math.abs(bornSup.getY() - bornInf.getY());
        this.HEIGHT = HeightCadre * 2 / 3;
        this.RADIUS= WeightCadre / 2;
         this.SIDE = 3*RADIUS / 2;
        this.WIDTH = this.RADIUS * 2;

    }

    /*
     * Un cadre comprends 4 hexagones... Le premier hexagone du cadre a pour
     * centre : bornSupX+S/2 bornSupY+H/2..
     * 
     * Les cellules retenues sont celles qui ont une intersection avec le cadre
     * supérieure au 1/3 de la surface de l'hexagone
     * 
     * @return les cadre hexgones contenus dans le cadre...
     */
//    public List<HexaNode> HexaForFrame() {
//        // on intialise les paramètres de l'hexagone
//        HexaGoneUtil hexaUtil = new HexaGoneUtil();
//        // initialisation du cadre et les parametres de l'hexagone
//        initialiseHexaFromCadre();
//        // on fabrique le polygone a appliqué au cadre
//        Polygon polyCadre = buildPolygonOfCadre();
//
//        // System.out.println("cadre poly"+ polyCadre.toText());
//        List<HexaNode> selectedNodes = new ArrayList<HexaNode>();
//        // on construit le premier polygone contenu dans le cadre
//        Coordinate c = new Coordinate(bornSup.getX() + SIDE / 2, bornSup.getY()
//                + HEIGHT / 2, 0);
//        Point center = new GeometryFactory().createPoint(c);
//        HexaNode hex = new HexaNode(0, 0, 0, center, 4);
//        hex.setRADIUS(RADIUS);
//
//        // les paramètres de calcul des hexagones
//        // representer en fonction du cadre
//        hexaUtil = new HexaGoneUtil();
//        hexaUtil.setSIDE(SIDE);
//        hexaUtil.setRADIUS(RADIUS);
//        hexaUtil.setWIDTH(WIDTH);
//        hexaUtil.setHEIGHT(HEIGHT);
//
//        hexaUtil.computeHexaCenterFromPoint(hex.getCenter().getX(), hex
//                .getCenter().getY());
//        // on met à jour les centres indexes
//        // des cadres.
//        hex.setIndiceX(hexaUtil.getCenterI());
//        hex.setIndiceY(hexaUtil.getCenterJ());
//        // on renseigne sont rayon
//        hex.setRADIUS(RADIUS);
//        // ajout de l'hexagone ...
//        selectedNodes.add(hex);
//
//        System.out.println("RADIUS" + RADIUS);
//        System.out.println("hexIndexX" + hex.getIndiceX());
//
//        System.out.println("hexIndexY" + hex.getIndiceY());
//
//        // on recherche ses soeurs
//        HexaNeighbor neighbor = new HexaNeighbor(hex);
//        // on fabrique toutes ces soeurs.
//        neighbor.generatePossibleSister();
//
//        // regarde par ses soeurs celles qui appartiennent au Cadre...
//        for (HexaNode nodeH : neighbor.getSisters()) {
//            // on determine le centre du en indices
//            // des hexagones en question
//
//            hexaUtil.computeHexaCenterFromPoint(nodeH.getCenter().getX(), nodeH
//                    .getCenter().getY());
//            // on met à jour les centres indexes
//            // des cadres.
//            nodeH.setRADIUS(RADIUS);
//            nodeH.setIndiceX(hexaUtil.getCenterI());
//            nodeH.setIndiceY(hexaUtil.getCenterJ());
//            // on fabrique le polygone associé à l'hexagone
//
//            // System.out.println(nodeH.toString());
//
//            // en partant des ces soeurs
//            HexaNeighbor neighborSister = new HexaNeighbor(nodeH);
//            // on fabrique toutes ces soeurs.
//            neighborSister.generatePossibleSister();
//
//            // (ArrayList)Arrays.asList(neighborSister.getSisters());
//            // conversion d'un tableau en liste...
//            // on prendre la liste des soeurs pour fabriquer
//            // le polygone parce qu'elle est ordonnée
//            Polygon Hexpoly = HexaGoneUtil.builHexagone(Arrays
//                    .asList(neighborSister.getSisters()));
//
//            // System.out.println("hexaPoly"+poly.toText());
//
//            // on determine l'intersection..;
//
//            if (polyCadre.intersects(Hexpoly)) {
//                if (hex.getIndiceX() == nodeH.getIndiceX()
//                        || hex.getIndiceX() + 1 == nodeH.getIndiceX()) {
//                    // Geometry geo = polyCadre.intersection(poly);
//                    // on prend uniquement celles qui ont une intersection
//                    // dont la valeur est superieur au tiers de l'hexagone
//                    // if (geo.getArea() > poly.getArea() / 3) {
//                    hexaCadre.add(nodeH);
//                }
//            }
//            // }
//        }
//        //
//
//        return selectedNodes;
//    }
    
    
    
    public HexaNode[] HexaForFrameNumberised() {
        // on intialise les paramètres de l'hexagone
        HexaGoneUtil hexaUtil = new HexaGoneUtil();
        HexaNode[] setHexNodes=new HexaNode[4];
        // initialisation du cadre et les parametres de l'hexagone
        initialiseHexaFromCadre();
        // on fabrique le polygone a appliqué au cadre
        Polygon polyCadre = buildPolygonOfCadre();

        // System.out.println("cadre poly"+ polyCadre.toText());
        List<HexaNode> selectedNodes = new ArrayList<HexaNode>();
        // on construit le premier polygone contenu dans le cadre
        Coordinate c = new Coordinate(bornSup.getX() + RADIUS/4, bornSup.getY()
                + HEIGHT / 2, 0);
        Point center = new GeometryFactory().createPoint(c);
        HexaNode hex = new HexaNode(0, 0, 0, center, 4);
        hex.setRADIUS(RADIUS);

        // les paramètres de calcul des hexagones
        // representer en fonction du cadre
        hexaUtil = new HexaGoneUtil();
        hexaUtil.setSIDE(SIDE);
        hexaUtil.setRADIUS(RADIUS);
        hexaUtil.setWIDTH(WIDTH);
        hexaUtil.setHEIGHT(HEIGHT);

        hexaUtil.computeHexaCenterFromPoint(hex.getCenter().getX(), hex
                .getCenter().getY());
        // on met à jour les centres indexes
        // des cadres.
        hex.setIndiceX(hexaUtil.getCenterI());
        hex.setIndiceY(hexaUtil.getCenterJ());
        // on renseigne sont rayon
        hex.setRADIUS(RADIUS);
        // ajout de l'hexagone ...
        
        hexaCadre[0]=hex;
        selectedNodes.add(hex);

        System.out.println("RADIUS" + RADIUS);
        System.out.println("hexIndexX" + hex.getIndiceX());

        System.out.println("hexIndexY" + hex.getIndiceY());

        // on recherche ses soeurs
        HexaNeighbor neighbor = new HexaNeighbor(hex);
        // on fabrique toutes ces soeurs.
        neighbor.generatePossibleSister();

        // regarde par ses soeurs celles qui appartiennent au Cadre...
        
        int i=0;
        int k=1;
        
        for (i=0; i< neighbor.getSisters().length; i++ ){
       // for (HexaNode nodeH : neighbor.getSisters()) {
            // on determine le centre du en indices
            // des hexagones en question

            hexaUtil.computeHexaCenterFromPoint(neighbor.getSisters()[i].getCenter().getX(), 
                    neighbor.getSisters()[i].
                    getCenter().getY());
            // on met à jour les centres indexes
            // des cadres.
            neighbor.getSisters()[i].setRADIUS(RADIUS);
            neighbor.getSisters()[i].setIndiceX(hexaUtil.getCenterI());
            neighbor.getSisters()[i].setIndiceY(hexaUtil.getCenterJ());
            // on fabrique le polygone associé à l'hexagone

            // System.out.println(nodeH.toString());

            // en partant des ces soeurs
            HexaNeighbor neighborSister = new HexaNeighbor(neighbor.getSisters()[i]);
            // on fabrique toutes ces soeurs.
            neighborSister.generatePossibleSister();

            // (ArrayList)Arrays.asList(neighborSister.getSisters());
            // conversion d'un tableau en liste...
            // on prendre la liste des soeurs pour fabriquer
            // le polygone parce qu'elle est ordonnée
            Polygon Hexpoly = HexaGoneUtil.builHexagone(Arrays
                    .asList(neighborSister.getSisters()));

            // System.out.println("hexaPoly"+poly.toText());

            // on determine l'intersection..;

            if (polyCadre.intersects(Hexpoly)) {

                // Geometry geo = polyCadre.intersection(poly);
                // on prend uniquement celles qui ont une intersection
                // dont la valeur est superieur au tiers de l'hexagone
                // if (geo.getArea() > poly.getArea() / 3) {

                if (k < 4) {
                    hexaCadre[k] = neighbor.getSisters()[i];
                    k++;
                }

            }
        }
        //

        return hexaCadre;
    }

    
    /*
     * permet de mettre à jour la liste des cellules hexagonales à rechercher
     * des geometries...
     */
    public void collapseCadre(Cadre cadreOld) {

        int i = 0;

        for (i = 0; i < hexaCadre.length; i++) {
            // on verifie que l' ième element du nouveau cadre
            // est fabriquer par l'ancien...c'est à dire qu'il deja indexé...
            if (cadreOld.buildPolygonOfCadre().contains(
                    HexaGoneUtil.builHexagone(Arrays.asList(new
                            HexaNeighbor(
                            hexaCadre[i]).getSisters())))
                    && cadreOld.getMixHexaNode()[i] != -1) {

                 transfertNode[i] = i;
                
                // on devra changer les elements de l'entre i de transfertNode
                // par i.

            }
        }

    }

  
    public HexaNode[] getHexaCadre() {
        return hexaCadre;
    }

    public void setHexaCadre(HexaNode[] hexaCadre) {
        this.hexaCadre = hexaCadre;
    }

    public int[] getMixHexaNode() {
        return mixHexaNode;
    }

    public void setMixHexaNode(int[] mixHexaNode) {
        this.mixHexaNode = mixHexaNode;
    }

    public Cadre imageCadre(AffineTransform2D transformation) {

        Point2D pointA = new Point2D.Double(this.bornSup.getX(),
                this.bornSup.getY());

        Point2D pointB = new Point2D.Double(this.bornInf.getX(),
                this.bornInf.getY());

        Point2D pointImgA = transformation.transform(pointA, null);
        Point2D pointImgB = transformation.transform(pointB, null);

        Coordinate c1 = new Coordinate(pointImgA.getX(), pointImgA.getY(), 0);

        Coordinate c2 = new Coordinate(pointImgB.getX(), pointImgB.getY(), 0);

        Point bSup = new GeometryFactory().createPoint(c1);

        Point bInf = new GeometryFactory().createPoint(c2);

        Cadre cadreImage = new Cadre(bSup, bInf, "image" + this.nameCadre);

        return cadreImage;
    }

    public double getHEIGHT() {
        return HEIGHT;
    }

    public void setHEIGHT(double hEIGHT) {
        HEIGHT = hEIGHT;
    }

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

    public double getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(double wIDTH) {
        WIDTH = wIDTH;
    }

    /*
     * 
     */

}
