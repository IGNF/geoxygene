package fr.ign.cogit.mapping.util;

import java.awt.geom.Point2D;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import fr.ign.cogit.mapping.webentity.spatial.Constants;

/*
 * @author Dr Tsatcha D.
 */

public class HyperBoundingBoxUtil {

    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(
            PrecisionModel.FLOATING);
    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            PRECISION_MODEL, Constants.WGS84_SRID);

    // cette methode permet de retourner HyperBounding
    // à partir d'un texte....
    public static HyperBoundingBox obtainHuperFromText(String textH) {
        // String textH =
        // "HyperBoundingBox:BOX: P-Min (0.0, 0.0, ), P-Max (0.0, 0.0, )";

        String[] val = textH.split("\\(");
        String[] val2 = null;
        String pointText = null;
        double a, b;
        HyperPoint bornSupScale = null;
        HyperPoint bornInfScale = null;
       if (textH!=null){
        for (int i = 0; i < val.length; i++) {

            switch (i) {
            case 1:
                val2 = val[1].split("\\)");
                pointText = val2[0].substring(0, val2[0].length() - 2);

                a = Double.parseDouble(pointText.split(",")[0]);
                b = Double.parseDouble(pointText.split(",")[1]);
                bornInfScale = new HyperPoint(new double[] { a, b });
                break;

            case 2:
                val2 = val[2].split("\\)");
                pointText = val2[0].substring(0, val2[0].length() - 2);

                a = Double.parseDouble(pointText.split(",")[0]);
                b = Double.parseDouble(pointText.split(",")[1]);

                bornSupScale = new HyperPoint(new double[] { a, b });

                break;

            default:
                break;
            }

        }

        if (bornSupScale == null || bornInfScale == null) {
            return null;
        } else {
            HyperBoundingBox MaxRectangle = new HyperBoundingBox(bornInfScale,
                    bornSupScale);
            return MaxRectangle;
        }
       }
            return null;
// System.out.println("MaxRectangle" + MaxRectangle.toString());
    }
    
    
    /*
     * l'image d'un hyperBoundingBox
     */
    public static HyperBoundingBox imageHyperBoundinBox(HyperBoundingBox hyper,
            AffineTransform2D transformation) {

        Point2D pointA = new Point2D.Double(hyper.getPMax().getCoord(0), hyper
                .getPMax().getCoord(1));

        Point2D pointB = new Point2D.Double(hyper.getPMin().getCoord(0), hyper
                .getPMin().getCoord(1));

        Point2D pointImgA = transformation.transform(pointA, null);
        Point2D pointImgB = transformation.transform(pointB, null);

        HyperPoint bornSupScale = new HyperPoint(new double[] {
                pointImgA.getX(), pointImgA.getY() });
        ;

        HyperPoint bornInfScale = new HyperPoint(new double[] {
                pointImgB.getX(), pointImgB.getY() });
        HyperBoundingBox MaxRectangle = new HyperBoundingBox(bornInfScale,
                bornSupScale);
        
    return MaxRectangle;
    }
}
