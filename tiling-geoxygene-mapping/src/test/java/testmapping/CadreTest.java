package testmapping;

import java.awt.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.frameworks.Cadre;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;

public class CadreTest {

    @Test
    public void testCadre() {

        Coordinate c1 = new Coordinate(0, 0, 0);

        Coordinate c2 = new Coordinate(5, 7, 0);

        Point bSup = new GeometryFactory().createPoint(c1);

        Point bInf = new GeometryFactory().createPoint(c2);

//        Cadre cadre = new Cadre(bSup, bInf, "toto");
//        cadre.initialiseHexaFromCadre();
//
//        System.out.println(" rayon" + cadre.getRADIUS());
//        System.out.println(" side" + cadre.getSIDE());
//        System.out.println(" hauteurHexagone" + cadre.getHEIGHT());
//
//        for (HexaNode nodeH : cadre.HexaForFrameNumberised()) {
//
//            System.out.println("cadre cellules du cadre"+ nodeH.toString());
//
//        }
//        
        
        

    }

}
