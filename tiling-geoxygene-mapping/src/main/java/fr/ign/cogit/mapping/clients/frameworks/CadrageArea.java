package fr.ign.cogit.mapping.clients.frameworks;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;

import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.webentity.spatial.Constants;

/*
 * Cette classe permet de definir la zone de cadrage
 * en fonction de la resolution du support visuel de 
 * l'utilisateur...
 * Ainsi on s'interesse au point d'interet de l'utilisateur
 * et on construit ainsi les huits cadres necessaires...
 * en dehors du cadre centre les autres sont deposées
 * dans un cache...
 * @author Dr Tsatcha
 * 
 * 
 *  ______________
 * |    |    |    |    
 * |_0__|__1_|__2_|
 * |    |    |    | 
 * |_3__|_4__|_5__|
 * |    |    |    | 
 * |_6__|_7__|_8__|
 * Dr Tsatcha D.
 */

public class CadrageArea {

    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);

    /*
     * @param interestPoint designe le point d'interet qui sera le centre du
     * cadrage...
     */

    protected Point InterestPoint;

    /*
     * Les paramètres de resolution du support visuel...
     * 
     * @param height designe la hauteur
     * 
     * @param weight designe la hauteur
     */

    protected double CadreHeight;

    protected double CadreWeight;

    /*
     * @param ptInfCadrageArea Designe la borne inférieure de la zone de cadrage
     */
    protected Point ptInfCadrageArea;

    /*
     * @param ptSupCadrageArea Designe la borne supérieure de la zone de cadrage
     */
    protected Point ptSupCadrageArea;

    /*
     * les differents cadres qui forment la zone de cadrage
     */
    
    /*
     * @param mixCadres
     * designe les cadres qui n'ont pas encore éte 
     * chargé dans le modèle par des hexanodes et cadre... 
     * à l'intialisation il est à -1 à  tous les entrées
     * si l'entrée est chargé il passe à  1...
     */
    
    protected int[] mixCadre=new int [] {-1,-1,-1,-1,-1,-1,-1,-1,-1};
    
    public Point getInterestPoint() {
        return InterestPoint;
    }

    public void setInterestPoint(Point interestPoint) {
        InterestPoint = interestPoint;
    }

    public int[] getIndicesTranfertCadre() {
        return indicesTranfertCadre;
    }

    public void setIndicesTranfertCadre(int[] indicesTranfertCadre) {
        this.indicesTranfertCadre = indicesTranfertCadre;
    }

    /*
     * @param indicesTranfert...
     * Designe les indices de noeuds des cadres qui vont etre
     * mis a jour ou changer dans la base d'indice...
     * le but est est de simplement modifier ce qui est à modifier...
     */
    protected int[] indicesTranfertCadre=new int [] {-1,-1,-1,-1,-1,-1,-1,-1,-1};
    
    LinkedHashMap<Integer, Cadre> cadrageArea = new LinkedHashMap<Integer, Cadre>();

    public LinkedHashMap<Integer, Cadre> getCadrageArea() {
        return cadrageArea;
    }

    public void setCadrageArea(LinkedHashMap<Integer, Cadre> cadrageArea) {
        this.cadrageArea = cadrageArea;
    }

    /**
     * @param interestPoint
     * @param height
     * @param weight
     */

    /*
     * Recherche des coordonnées du cadre de visualition principale du point
     * d'interet (0,0) la borne inférieur de carte sur l'echelle est ramenée à
     * (0,0) dont on effectue une translation de vecteur OA ou A est inférieur
     * de la carte dont est considére comme l'origne de du repère...;
     * --------------x---------------> | | | _____ | | |_____| | | | | y
     */
    public CadrageArea(Point interestPoint, double weightParam, double heightParam) {
        super();
        InterestPoint = interestPoint;
        this.CadreWeight = weightParam;
        this.CadreHeight = heightParam;
      

        double bornInfx = 0.0;
        double bornInfy = 0.0;
        double bornSupx = 0.0;
        double bornSupy = 0.0;
        Coordinate c1;
        Cadre cadre = null;
        Coordinate c2;

        Point ptInf;
        Point ptSup;

        // fabrication des cadres en fonction du point d'interet
        for (int i = 0; i < 9; i++) {

            switch (i) {
            case 0:
                // se calcule en faisant une translation de vecteur
                // (-w,-h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 - CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2 - CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2 - CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2 - CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                ptSupCadrageArea = ptSup;

                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            case 1:
                // se calcule en faisant une translation de vecteur
                // (0,-h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2;
                bornSupy = interestPoint.getY() - CadreHeight / 2 - CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2;
                bornInfy = interestPoint.getY() + CadreHeight / 2 - CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            case 2:

                // se calcule en faisant une translation de vecteur
                // (+w,-h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 + CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2 - CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2 + CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2 - CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            case 3:

                // se calcule en faisant une translation de vecteur
                // (-w,0) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 - CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2;
                bornInfx = interestPoint.getX() + CadreWeight / 2 - CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;
            case 4:
                // defintion du cadre central

                bornSupx = interestPoint.getX() - CadreWeight/2;
                bornSupy = interestPoint.getY() - CadreHeight / 2;
                bornInfx = interestPoint.getX() + CadreWeight / 2;
                bornInfy = interestPoint.getY() + CadreHeight / 2;
                System.out.println("mon supx"+ bornSupx + " "+ CadreWeight);
                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;
            case 5:

                // se calcule en faisant une translation de vecteur
                // (+w,0) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 + CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2;
                bornInfx = interestPoint.getX() + CadreWeight / 2 + CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;
            case 6:
                // se calcule en faisant une translation de vecteur
                // (-w,+h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 - CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2 + CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2 - CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2 + CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            case 7:

                // se calcule en faisant une translation de vecteur
                // (0,+h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2;
                bornSupy = interestPoint.getY() - CadreHeight / 2 + CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2;
                bornInfy = interestPoint.getY() + CadreHeight / 2 + CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            case 8:

                // se calcule en faisant une translation de vecteur
                // (+w,+h) du cadre central
                bornSupx = interestPoint.getX() - CadreWeight / 2 + CadreWeight;
                bornSupy = interestPoint.getY() - CadreHeight / 2 + CadreHeight;
                bornInfx = interestPoint.getX() + CadreWeight / 2 + CadreWeight;
                bornInfy = interestPoint.getY() + CadreHeight / 2 + CadreHeight;

                c1 = new Coordinate(bornInfx, bornInfy, 0);

                c2 = new Coordinate(bornSupx, bornSupy, 0);

                ptInf = new GeometryFactory().createPoint(c1);
                ptSup = new GeometryFactory().createPoint(c2);

                ptInfCadrageArea = ptInf;
                cadre = new Cadre(ptSup, ptInf, "cadre" + String.valueOf(i));
                cadrageArea.put(i, cadre);

                break;

            default:
                break;
            }

        }
        
        indicesTranfertCadre=new int [] {-1,-1,-1,-1,-1,-1,-1,-1,-1};
        mixCadre=new int [] {-1,-1,-1,-1,-1,-1,-1,-1,-1};
    }

    public double getCadreHeight() {
        return CadreHeight;
    }

    public void setCadreHeight(double cadreHeightParam) {
        CadreHeight = cadreHeightParam;
    }

    public double getCadreWeight() {
        return CadreWeight;
    }

    public void setCadreWeight(double cadreWeightParam) {
        CadreWeight = cadreWeightParam;
    }

    /*
     * cette methode permet de considerer un cadrage comme un objet géometrique
     */
    public Polygon buildPolygonOfCadrageArea() {

        Polygon geom = null;
        Coordinate[] coordinates = null;
        Coordinate coord = null;
        coordinates = new Coordinate[] {
                new Coordinate(ptSupCadrageArea.getX(),
                        ptSupCadrageArea.getY()),
                new Coordinate(ptInfCadrageArea.getX(),
                        ptSupCadrageArea.getY()),
                new Coordinate(ptInfCadrageArea.getX(),
                        ptInfCadrageArea.getY()),
                new Coordinate(ptSupCadrageArea.getX(),
                        ptInfCadrageArea.getY()),
                new Coordinate(ptSupCadrageArea.getX(),
                        ptSupCadrageArea.getY())

        };

        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coordinates);
        // notre polygone n'a pas de trous
        LinearRing[] holes = null;
        geom = GEOMETRY_FACTORY.createPolygon(shell, holes);

        return geom;

    }

    /*
     * @param transformation designe la transformation necessaire au cadrage
     */

    public CadrageArea imageCadrageArea(AffineTransform2D transformation) {

        Point2D pointA = new Point2D.Double(ptSupCadrageArea.getX(),
                ptSupCadrageArea.getY());

        Point2D pointB = new Point2D.Double(ptInfCadrageArea.getX(),
                ptInfCadrageArea.getY());
        // on effectue la transformation
        Point2D pointImgA = transformation.transform(pointA, null);
        Point2D pointImgB = transformation.transform(pointB, null);

        Coordinate c1 = new Coordinate(pointImgA.getX(), pointImgA.getY());

        Coordinate c2 = new Coordinate(pointImgB.getX(), pointImgB.getY());

        Point bSup = new GeometryFactory().createPoint(c1);

        Point bInf = new GeometryFactory().createPoint(c2);

        Point2D pointInt = new Point2D.Double(this.InterestPoint.getX(),
                this.InterestPoint.getY());

        Point2D pointInteresPoint = transformation.transform(pointInt, null);

        Coordinate c3 = new Coordinate(pointInteresPoint.getX(),
                pointInteresPoint.getY(), 0);

        Point InteresPoint = new GeometryFactory().createPoint(c3);
        // on fabrique le nouveau cadrage
        CadrageArea cadrageImage = new CadrageArea(InteresPoint,
                Math.abs(pointImgA.getX() - pointImgB.getX()),
                Math.abs(pointImgA.getY() - pointImgB.getY()));

        return cadrageImage;
    }
    
    /*
     * Cette methode permet de voir les cadres qu'il faut charger si deux cadres
     * venaient ou pas se rencontrer...
     * 
     * mixCadre[i] = 1; on a le contenu du cadre i deja present dans l'index
     * indicesTranfert[i]=i // on peut juster le changer par i par le
     * indicesTranfert[i] autre cas le cadre est à rechercher..
     * 
     * Dessigne les cadre à changer.. cependant un cadre se compose d'un
     * ensemble de cellules hexagone qui contient les objets...ce sont ces
     * cellules hexagones qui designeront indiquerons les elements du cadre
     * mettre à jour...
     */
    public void collapseCadrage(CadrageArea cadreOld) {

        int i = 0;

        for (i = 0; i < 9; i++) {
            // on verifie que l' ième element du nouveau cadre
            // est fabriquer par l'ancien...c'est à dire qu'il deja indexé...

            for (int j = 0; j < 9; j++) {

                if (cadreOld.getCadrageArea().get(j).buildPolygonOfCadre()
                        .equalsExact(cadrageArea.get(i).buildPolygonOfCadre())) {
                    // passera à 9 quand le transfert sera effectuer...;
                    // simple transfert d'indices...
                    System.out.println("you"+cadreOld.getCadrageArea().get(j).buildPolygonOfCadre().toText());
                    System.out.println("me"+cadrageArea.get(i).buildPolygonOfCadre().toText());

                    indicesTranfertCadre[i] = j;
                    break;

                }
            }
        }
    }
    
    /*
     * Cette methode designe la tranmission des transfert des
     * Noeuds en fonction de la position des noeuds centraux...
     * c'est à dire le noeud cadre....
     */
  
    /*
     * Cette methode permet de voir lesquelles indices
     * necessitent d'etre transferée en fonction de l'intersection
     * de ce qui existe deja dans la base d'index afin de 
     * garder la structure intiale de l'index donc le cadre visuel
     * est le 4...
     */
    
   public int[] getMixCadre() {
        return mixCadre;
    }

    public void setMixCadre(int[] mixCadre) {
        this.mixCadre = mixCadre;
    }

    /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
    public String toString(){
         return  buildPolygonOfCadrageArea().toText();
    }
    
    // cette methode permet de retourner le 
    // centre du cadre...
    
    /*
     * @return le quatrième cadre qui est le
     * centre du cadrage dans la structure de
     * cadrage.
     */
    
    public Cadre getCenterCadrage(){
        return cadrageArea.get(4);
    }

}
