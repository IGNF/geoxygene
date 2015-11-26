package testmapping;

/*
 * @author Dr Tsatcha D.
 */

import static org.junit.Assert.fail;

import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.webentity.spatial.GeometryConverter;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndexException;

public class RtreeMutiLevelIndexTest {
    
    
    ConcurrentHashMap<String,RTreeIndex> multiLevelIndex = new ConcurrentHashMap<String, RTreeIndex>();
    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/webentity/spatial";

   // private RTreeIndex defaultGraphIndex;
    // intanciation des classes
    protected RTreeIndex scale0 =  new RTreeIndex(Directory , "scale0");
    protected RTreeIndex scale1 =  new RTreeIndex(Directory , "scale1");
    protected RTreeIndex scale2 =  new RTreeIndex(Directory , "scale2"); 

    

  //  @Test
    public void testAddGeometry() {
        // defaultGraphIndex =new
        // RTreeIndex("C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mappingwebentity\spatial");
//        System.out.println("hello" + scale0.getIndexDir());
//        System.out.println("hello" + scale1.getIndexDir());
//        System.out.println("hello" + scale2.getIndexDir());
//        Assert.assertEquals("Hello World", "Hello World");
//        testAddGeometry(scale0);
//        testAddGeometry(scale1);
//        testAddGeometry(scale2);
//        multiLevelIndex.put("scale0", scale0);
//        multiLevelIndex.put("scale1", scale1);
//        multiLevelIndex.put("scale2", scale2);
        
        // testAddGeometry(namedGraphIndex);
    }
    /*
     *  ce test fonctionnait uniquement pour des noeuds string et non node
     *  il faut changer l'information "me" en node.
     */
    
//    public void testAddGeometry(SpatialIndex index) {
//        index.open();
//        // Node n = Node.createURI("http://test.org#test");
//        Geometry extent = GeometryConverter
//                .createPoint(new double[] { 1.0, 1.0 });
//        // test add node
//        try {
//            index.add(Record.create("me", extent));
//        } catch (SpatialIndexException e) {
//            e.printStackTrace();
//            fail();
//        }
//        // assertEquals(1, index.size());
//
//        // test add another node
//        // n = Node.createURI("http://test.org#test2");
//        extent = GeometryConverter.createPoint(new double[] { 1.0, 2.0 });
//        try {
//            index.add(Record.create("me1", extent));
//        } catch (SpatialIndexException e) {
//            e.printStackTrace();
//            fail();
//        }
//        // assertEquals(2, index.size());
//
//        double[] array = { 20, 10, 30, 0, 40, 10, 30, 20 };
//        extent = GeometryConverter.createLineString(array);
//
//        try {
//            index.add(Record.create("me2", extent));
//        } catch (SpatialIndexException e) {
//            e.printStackTrace();
//            fail();
//        }
//        // assertEquals(3, index.size());
//
//        double[] array1 = { 20, 10, 30, 0, 40, 10, 30, 20, 20, 10 };
//        extent = GeometryConverter.createPolygon(array1);
//
//        try {
//            index.add(Record.create("me3", extent));
//        } catch (SpatialIndexException e) {
//            e.printStackTrace();
//            fail();
//        }
//        // assertEquals(4, index.size());
//
//        System.out.println("look my size " + index.size());
//        Record r = Record.create("me3", extent);
//        index.add(r);
//        System.out.println("look my size " + index.size());
//
//    }

}
