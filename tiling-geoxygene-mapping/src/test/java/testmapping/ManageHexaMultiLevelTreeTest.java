package testmapping;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.ControllerI;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.geoxygene.GeoxConverter;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTreeIndex;
import fr.ign.cogit.mapping.datastructure.management.HexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ManageHexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.util.ConverterUtil;

public class ManageHexaMultiLevelTreeTest {
    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/clients/spatial";

    @Test
    public void testConverter() {

        ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndex = 
                new ConcurrentHashMap<ScaleInfo, HexaTreeIndex>();
        /*
         * intialisation des paramètre du manager hexaTree
         */
        HexaTreeMultiLevel hexaTreemultilevel = new HexaTreeMultiLevel(
                multiLevelIndex);
        Coordinate c1 = new Coordinate(0, 0, 0);
        Point interest = new GeometryFactory().createPoint(c1);
        
        
        ControllerI  controlI = new ControllerI();

        Converter conv = new GeoxConverter(interest, 10, 20, "hello");
        
        ManageHexaTreeMultiLevel managerHex = new ManageHexaTreeMultiLevel(hexaTreemultilevel,
                Directory, conv, controlI.getManager());

        String textH = conv.toString();
        System.out.println("------1-" + conv.toString());
     //   Converter con2 = ConverterUtil.obtainConverterFromtext(textH);

     //   System.out.println("---2----" + con2.toString());
        
      //   managerHex.createOneLevel(15,20);
        // HexaTreeIndex  hexIndex= managerHex.useAnLevel(15);
          
         managerHex.saveLevelsInFile();
         
         
    }

}
