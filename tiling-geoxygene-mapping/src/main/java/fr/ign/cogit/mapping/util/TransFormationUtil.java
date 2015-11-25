package fr.ign.cogit.mapping.util;

import java.awt.geom.AffineTransform;

import org.deegree.io.rtree.HyperBoundingBox;
import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;

/*
 * Cette definir les differentes transformation geéometrique
 */
public class TransFormationUtil {

    /**
     * 
     */
    public TransFormationUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    // cette methode definie la fonction d'homothetie entre
    // deux carte..
    // Permettra de cacluler la coordonnée d'un point sur une carte
    // vers une autre carte..
    /*
     * @param map1Designe la carte à une echelle quelconque
     * 
     * @param map2 Designe egalement la carte à une autre echelle
     */
    public static AffineTransform2D scalabily(HyperBoundingBox map1,
            HyperBoundingBox map2) {

        AffineTransform2D transformation = new AffineTransform2D();

        transformation.scale(
                Math.abs(map1.getPMin().getCoord(0)
                        - map1.getPMax().getCoord(0)
                        / (map2.getPMin().getCoord(0) - map2.getPMax()
                                .getCoord(0))),

                Math.abs(map1.getPMin().getCoord(1)
                        - map1.getPMax().getCoord(1)
                        / (map2.getPMin().getCoord(1) - map2.getPMax()
                                .getCoord(1))));

        return transformation;
    }

    /*
     * une tranlation de deplace OP ou O est le point de depart de la
     * translation P est le pont d'arrivée..; P(Px,Py);
     */

    public static AffineTransform2D translation(Point P) {

        AffineTransform2D transformation = new AffineTransform2D();

        AffineTransform translate = AffineTransform.getTranslateInstance(
                -P.getX(), -P.getY());

        transformation.concatenate(translate);

        return transformation;
    }

    /*
     * Le miroir permet de passer des coordonnées entieres vers de coordonnées
     * entieres naturelles ( toujours positifs) Par exemple l'ecran de
     * visualisation... En changeant aussi les axes par rapport à l'orientation
     * habituelle des repères...;
     */

    public static AffineTransform2D miroirY(Double height) {

        AffineTransform2D transformation = new AffineTransform2D();

        AffineTransform mirror_y = new AffineTransform(1, 0, 0, -1, 0, height);

        transformation.concatenate(mirror_y);

        return transformation;
    }

    /*
     * permet de changer le sens d'un axe... etc...
     */
    public static AffineTransform2D miroirX(Double weight) {

        AffineTransform2D transformation = new AffineTransform2D();

        AffineTransform mirror_x = new AffineTransform(-1, 0, weight, 1, 0, 0);

        transformation.concatenate(mirror_x);

        return transformation;
    }

    /*
     * Cette methode determine les paramètres d'echelle à considerer pour la
     * plus petite echelle correctement vu par le dispositif ....
     */

    public static HyperBoundingBox computeMinTransBox(
            ManageRtreeMultiLevel manager, Converter convertisseur) {

        ScaleInfo scaleMax = null;
        int i = 0;
        for (ScaleInfo s : manager.getRtreemultilevel().getMultiLevelIndex()
                .keySet()) {

            if (i == 0) {

                scaleMax = s;

                i++;
            } else {
                if (s.getIdScale() > scaleMax.getIdScale()) {
                    scaleMax = s;

                }
            }
        }

        if (scaleMax != null) {
            return manager.getRtreemultilevel().getMultiLevelIndex()
                    .get(scaleMax).getMaxRectangle();
        } else {
            return null;
        }

    }

    /*
     * 
     */

    /*
     * Cette methode permet de determiner les hyperBoundingBox associé à la plus
     * petite echelle de transmission
     */
    public static HyperBoundingBox initiateTransmission(
            ManageRtreeMultiLevel manager, Converter convertisseur) {
        HyperBoundingBox map = null;
        // on recherche la petite echelle
        // dans notre representation c'est celle qui a
        // la valeur la plus grande
        // reveille le manager

        return computeMinTransBox(manager, convertisseur);

    }

    /*
     * La transformation associée a une echelle quelconque connaissant l'echelle
     * minimale de transmission precedement calculé...
     */

    public static AffineTransform2D TransmissionFromScale(ScaleInfo scale,
            ManageRtreeMultiLevel manager, Converter convertisseur) {

        // la transmission intiale
        HyperBoundingBox map = initiateTransmission(manager, convertisseur);

        AffineTransform2D reportTransfinale = null;

        // correspondance entre l'echelle courante scale...
        // et l'echelle de transmission correcte...
        // boolean existe=false;
        // permet de verifier que l'information sur l'echelle existe deja...
        // dans
        // le manager...
        // rechecher l'image du convertisseur via cette echelle

        ScaleInfo newScale = ScaleInfoUtil.existeScale(scale, manager);
        
        System.out.println("new scale"+newScale.printScale());

        if (newScale != null && map != null) {

            // rapport de transmission entre les deux echelles...
            AffineTransform2D reportTrans = TransFormationUtil.scalabily(
                    map,
                    manager.getRtreemultilevel().getMultiLevelIndex()
                            .get(newScale).getMaxRectangle());

            // la transmission de cette echelle s'obtient par

            HyperBoundingBox m = HyperBoundingBoxUtil.imageHyperBoundinBox(
                    convertisseur.generateScreenParam(), reportTrans);

            reportTransfinale = TransFormationUtil.scalabily(m, manager
                    .getRtreemultilevel().getMultiLevelIndex().get(newScale)
                    .getMaxRectangle());

            // composition de deux homotheties... r=r1xr2
            // intialeTrans.concatenate(reportTrans);
        } else {

            throw new RuntimeException("l'echelle max n'a pas été intialisée");
        }
        return reportTransfinale;

    }

}
