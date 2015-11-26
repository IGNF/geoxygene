package fr.ign.cogit.mapping.util;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.geoxygene.GeoxConverter;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.webentity.spatial.Constants;
/*
 * @author Dr Tsatcha D.
 */

public class ConverterUtil {
    
    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);

    // cette methode permet de retourner HyperBounding
    // à partir d'un texte....
    /*
     * [interestPoint:POINT (0 0)] [weight:20.0] [height:10.0] 
     */
    
    protected Point interestPoint;
    
    public Point getInterestPoint() {
        return interestPoint;
    }

    /**
     * 
     */
    public ConverterUtil() {
        super();
        // TODO Auto-generated constructor stub
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

    protected  double height=0;
    
    protected  double weight=0;
    
    public  Converter obtainConverterFromtext(String textH) {

        String use = null;
        Node node = null;
        Point pt = null;
        double heightP = 0;
        double weightP = 0;
        if (textH == null) {
            throw new RuntimeException(textH + "  " + "est vide");
        } else {

            String[] containt = textH.split("\\(");

            String str2 = containt[1];

            containt = str2.split("\\)");

            String contain1 = containt[0];
            String contain2 = containt[1];

            double a = Double.parseDouble(contain1.split(" ")[0]);
            double b = Double.parseDouble(contain1.split(" ")[1]);

            Coordinate c1 = new Coordinate(a, b, 0);
            pt = new GeometryFactory().createPoint(c1);

            if (contain2.split(" ").length != 3) {
                throw new RuntimeException(textH + " a une taille de "
                        + containt.length + " " + "au lieu de 3");
            } else {

                int taillec = contain2.split(" ").length;
                for (int i = 0; i < taillec; i++) {
                    String myvalue = contain2.split(" ")[i];

                    // on exploite le troisieme element du noeud
                    // on ne regarde pas l'indice 0 car elle ne
                    // contient aucune valeur....

                    if (i == 1) {
                        String[] term = myvalue.split(":");
                        int taille = term[1].length();
                        use = term[1].substring(0, taille - 1);
                        weightP = Double.parseDouble(use);
                    }

                    if (i == 2) {
                        String[] term = myvalue.split(":");
                        int taille = term[1].length();
                        use = term[1].substring(0, taille - 1);
                        heightP = Double.parseDouble(use);
                    }
                }
            }
        }

        if (pt != null) {
            interestPoint = pt;
            weight = weightP;
            height = heightP;

        }

        return null;
    }

}
