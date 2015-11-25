package fr.ign.cogit.mapping.datastructure.hexaTree;

import java.text.DecimalFormat;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

//cette classe doit realiser la rotatatioàn d'angle x et centre  y 
public class Rotation {
    double rayon;

    public double getRayon() {
        return rayon;
    }

    public void setRayon(double rayon) {
        this.rayon = rayon;
    }

    double angle;
    Point centre;

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angleParam) {
        this.angle = angleParam;
    }

    public Point getCentre() {
        return centre;
    }

    public void setCentre(Point centreParam) {
        this.centre = centreParam;
    }

    public Rotation(double angleParam, Point centreParam, Double rayonParam) {
        super();
        this.angle = angleParam;
        this.centre = centreParam;
        this.rayon = rayonParam;
    }

    public Rotation(double angleParam, Double rayonParam) {
        super();
        this.angle = angleParam;
        this.rayon = rayonParam;
    }

    public Rotation(Double rayonParam) {
        this.rayon = rayonParam;
    }

    // une rotation de centre
    // pour obtenir l'image d'un point sommet , .
    // on fera une rotation de centre w centre de l'hexagone
    // et pour obtenir le centre du futur hexagone,
    // on fera: une rotation d'angle 180 degr� du milieu de chaque segment.
    public Point makeRotation(Point pt) {
        DecimalFormat df = new DecimalFormat("#######0.00");
        Point ptImage = null;
        // new Point(0.0,0.0);
        // Point pt=new Point(0.0,0.0);
        double abscisse = 0.0;
        double ordinate = 0.0;

        abscisse = Math.cos(angle) * (pt.getX() - centre.getX())
                - Math.sin(angle) * (pt.getY() - centre.getY()) + centre.getX();

        ordinate = Math.sin(angle) * (pt.getX() - centre.getX())
                + Math.cos(angle) * (pt.getY() - centre.getY()) + centre.getY();

        Coordinate c = new Coordinate(abscisse, ordinate, 0);
        ptImage = new GeometryFactory().createPoint(c);

        return ptImage;
    }

    // public static void main(String[] args) {
    // DecimalFormat df = new DecimalFormat("#######0.00");
    // Double r=2.0;
    // Point centre= new Point(0.0,0.0);
    // Point p= new Point(1.0,1.0);
    // Rotation rt =new Rotation(Math.PI/3, centre);
    // Point im=rt.makeRotation(p);
    // System.out.println("lat  "+df.format(im.getLat())+
    // "  "+"Long  "+df.format(im.getLong()));
    //
    // }

}
