package testmapping;
/*
 * @author Dr Tsatcha D.
 */

import java.util.concurrent.ConcurrentHashMap;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.ControllerI;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.clients.frameworks.Cadre;
import fr.ign.cogit.mapping.clients.geoxygene.GeoxConverter;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTreeIndex;
import fr.ign.cogit.mapping.datastructure.management.HexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ManageHexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.util.TransFormationUtil;

public class CadrageTest {
    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/clients/spatial";

    @Test
    public void testCadrage() {

        Coordinate c1 = new Coordinate(0, 0, 0);

        Coordinate c2 = new Coordinate(5, 7, 0);

        Point bSup = new GeometryFactory().createPoint(c1);

        Point bInf = new GeometryFactory().createPoint(c2);

        Cadre cadre = new Cadre(bSup, bInf, "toto");
        cadre.initialiseHexaFromCadre();

        System.out.println(" rayon" + cadre.getRADIUS());
        System.out.println(" side" + cadre.getSIDE());
        System.out.println(" hauteurHexagone" + cadre.getHEIGHT());

        HyperPoint bornInfScale = new HyperPoint(new double[] { 0, 0 });
        /*
         * @param bornSupScale designe le coin supérieure gauche
         */

        HyperPoint bornSupScale = new HyperPoint(new double[] { 2, 3 });
        /*
         * Designe le plus grand echelle utilisée pour les elements couramment
         * MaxRectangle intialisée actuellement comme un point.
         */

        HyperBoundingBox MaxRectangle = new HyperBoundingBox(bornInfScale,
                bornSupScale);

        bornInfScale = new HyperPoint(new double[] { 0, 0 });
        /*
         * @param bornSupScale designe le coin supérieure gauche
         */

        bornSupScale = new HyperPoint(new double[] { 4, 6 });
        /*
         * Designe le plus grand echelle utilisée pour les elements couramment
         * MaxRectangle intialisée actuellement comme un point.
         */

        HyperBoundingBox MaxRectangle1 = new HyperBoundingBox(bornInfScale,
                bornSupScale);

        /*
         * test du rapport d'echelle sur une homothetie
         */
        System.out.println(TransFormationUtil.scalabily(MaxRectangle,
                MaxRectangle1).toString());

        Coordinate c3 = new Coordinate(0, 0, 0);

        Point InteresPoint = new GeometryFactory().createPoint(c3);
        CadrageArea cadrageImage = new CadrageArea(InteresPoint,
                Math.abs(bornSupScale.getCoords()[0]
                        - bornInfScale.getCoords()[0]), Math.abs(bornSupScale
                        .getCoords()[1] - bornInfScale.getCoords()[1]));

        System.out.println(cadrageImage.buildPolygonOfCadrageArea().toText());

        CadrageArea cadrageImage1 = cadrageImage
                .imageCadrageArea(TransFormationUtil.scalabily(MaxRectangle,
                        MaxRectangle1));

        System.out.println(cadrageImage1.buildPolygonOfCadrageArea().toText());

        /*
         * Test de chevauchement de deux cadrages
         */
        for (int i = 0; i < cadrageImage1.getMixCadre().length; i++) {
            System.out.println(cadrageImage1.getMixCadre()[i]);
        }

        c3 = new Coordinate(2, 2, 0);

        InteresPoint = new GeometryFactory().createPoint(c3);

        bornInfScale = new HyperPoint(new double[] { 2, 2 });
        /*
         * @param bornSupScale designe le coin supérieure gauche
         */

        bornSupScale = new HyperPoint(new double[] { 6, 8 });

        CadrageArea cadrageImageN = new CadrageArea(InteresPoint,
                Math.abs(bornSupScale.getCoords()[0]
                        - bornInfScale.getCoords()[0]), Math.abs(bornSupScale
                        .getCoords()[1] - bornInfScale.getCoords()[1]));

        // certains elements du cadrage ont deja été mise à jour
        // on s'interesse uniquement de ceux mise à jour
        int[] mixCadre = new int[] { 1, 0, 1, -1, -1, 1, -1, -1, -1 };
        cadrageImage1.setMixCadre(mixCadre);

        // chevauchement des cadres
        cadrageImageN.collapseCadrage(cadrageImage1);

        System.out.println("---------------------------------");
        for (int i = 0; i < cadrageImageN.getMixCadre().length; i++) {
            System.out.println(cadrageImageN.getMixCadre()[i]);
        }

        System.out.println("--info du transfert-------------");
        for (int i = 0; i < cadrageImageN.getIndicesTranfertCadre().length; i++) {
            System.out.println(cadrageImageN.getIndicesTranfertCadre()[i]);
        }

        System.out.println(cadrageImageN.buildPolygonOfCadrageArea().toText());
        ScaleInfo scale = new ScaleInfo(15, 25);
        udapteHexaTreeIndex(scale);
    }

    public void udapteHexaTreeIndex(ScaleInfo scale) {
        Coordinate c1 = new Coordinate(1280.0, 1024.0);
        Point interest = new GeometryFactory().createPoint(c1);
        ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, HexaTreeIndex>();
        HexaTreeMultiLevel hexaTreemultilevel = new HexaTreeMultiLevel(
                multiLevelIndex);
        ControllerI controlI = new ControllerI();

        Converter Convertisseur = new GeoxConverter(interest, 1280.0, 1024.0,
                "hello");

        String textH = Convertisseur.toString();

        int[] mixCadre = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        ManageHexaTreeMultiLevel managerHex = new ManageHexaTreeMultiLevel(
                hexaTreemultilevel, Directory, Convertisseur,
                controlI.getManager());

        c1 = new Coordinate(0.0, 0.0);
        interest = new GeometryFactory().createPoint(c1);
        Convertisseur = new GeoxConverter(interest, 2, 4, "old");
        Convertisseur.setInterestPoint(interest);
        Convertisseur.setWeight(2);
        Convertisseur.setHeight(4);

        // on recupère des informations par cadrage central

        // if (managerHex.existAnLevel(scale.getIdScale(), 4)) {
        //
        //
        // } else {
        // determination de l'ancien cadrage qui a permis l'indexation
        CadrageArea oldCadrageArea = new CadrageArea(
                Convertisseur.getInterestPoint(), Convertisseur.getWeight(),
                Convertisseur.getHeight());

        System.out.println("old cadrage"
                + oldCadrageArea.buildPolygonOfCadrageArea().toText()
                + oldCadrageArea.getInterestPoint());

        // determination du nouveau cadrage à indexer...

        c1 = new Coordinate(2, 4);
        Point interestn = new GeometryFactory().createPoint(c1);
        Converter Convertisseurn = new GeoxConverter(interestn, 2, 4, "new");
        Convertisseurn.setInterestPoint(interestn);
        Convertisseurn.setWeight(2);
        Convertisseurn.setHeight(4);

        CadrageArea newCadrageArea = new CadrageArea(
                Convertisseurn.getInterestPoint(), Convertisseurn.getWeight(),
                Convertisseurn.getHeight());

        System.out
                .println("new cadrage"
                        + newCadrageArea.buildPolygonOfCadrageArea().toText()
                        + Convertisseurn.getInterestPoint() + " "
                        + Convertisseurn.getInterestPoint()
                        + Convertisseurn.getHeight() + " "
                        + Convertisseurn.getWeight());

        for (int i = 0; i < 9; i++) {

            System.out.println(newCadrageArea.getCadrageArea().get(i)
                    .buildPolygonOfCadre().toText()
                    + " num" + i);

        }
        
        // rechercher le cadre tuilé qui contient le point d'interet...
        
         interestn = new GeometryFactory().createPoint(c1);
         
         for (int i=0;  i<9 ; i++){
             
             if(newCadrageArea.getCadrageArea().get(i).buildPolygonOfCadre().contains(interest)){
                 
                 // on retourne le point ...
             }
             
         }
        // verification des cadres contenant des contenus

        //
        // for (int i = 0; i < 9; i++) {
        //
        // if (managerHex.existAnLevel(scale.getIdScale(), i)) {
        //
        // HexaTreeIndex index = managerHex.useScaleCadre(scale, i);
        // index.open();
        // // if (index.doSize() > 0) {
        // // // le cadre a été idealement precedement chargé...
        // // mixCadre[i] = 1;
        // // System.out.println("------ici--" + Convertisseur.toString()+
        // "cadre"+ "nume"+ i +" "+ mixCadre[i]);
        // //
        // // }
        //
        // }
        //
        // }
        // on renseigne les cadres qui ont été precedemment chargé et
        // avec des cadres remplis..;
        // oldCadrageArea.setMixCadre(mixCadre);

        // croisement des deux cadrages...
        newCadrageArea.collapseCadrage(oldCadrageArea);

        int[] transfert = newCadrageArea.getIndicesTranfertCadre();
        //
        for (int i = 0; i < transfert.length; i++) {
            //
            if (transfert[i] == -1) {

                System.out.println("les elements à rechercher" + transfert[i]
                        + "à" + " " + i);
                //
            } else {
                //
                // // if(i==transfert[i]){
                //
                System.out.println("les elements à modifier" + transfert[i]
                        + "à" + " " + i);
                //
                // // on permute les elements de la base d'indexes
                // // et la table des index
                //
            }
            //
        }
        
        
        

        //
        // if (managerHex.existAnLevel(scale.getIdScale(), i)) {
        //
        // HexaTreeIndex index = managerHex.useScaleCadre(scale, i);
        // index.open();
        // // if (index.doSize() > 0) {
        // // // le cadre a été idealement precedement chargé...
        // // mixCadre[i] = 1;
        // // System.out.println("------ici--" + Convertisseur.toString()+
        // "cadre"+ "nume"+ i +" "+ mixCadre[i]);
        // //
        // // }

    }

}
