package testmapping;

// Parliament is licensed under the BSD License from the Open Source
// Initiative, http://www.opensource.org/licenses/bsd-license.php
//
// Copyright (c) 2001-2009, BBN Technologies, Inc.
// All rights reserved.

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.Node;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.webentity.spatial.GeometryConverter;
import fr.ign.cogit.mapping.webentity.spatial.Operation;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndexException;

/**
 * @author Dtsatcha
 */

public class RtreeIndexTest {

    private RTreeIndex defaultGraphIndex;
    protected RTreeIndex namedGraphIndex = new RTreeIndex(
            "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/webentity/spatial");

    // protected Record<Geometry> createRecord(int seed) {
    // Geometry extent = GeometryConverter.createPoint(new double[] {
    // 53.2 + seed, 23.1 });
    // // Node n =new Node (1);
    //
    // return GeometryRecord.create("hello", extent);
    // }
    //
    //

//    @Test
//    public void testAddGeometry() {
//        // defaultGraphIndex =new
//        // RTreeIndex("C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mappingwebentity\spatial");
//        System.out.println("hello" + namedGraphIndex.getIndexDir());
//        Assert.assertEquals("Hello World", "Hello World");
//        testAddGeometry(namedGraphIndex);
//        // testAddGeometry(namedGraphIndex);
//    }
//
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
    //
    // private void testAddSameNode(SpatialIndex index) {
    // // test add another node
    // Node n = Node.createURI("http://test.org#test2");
    // Geometry extent = GeometryConverter
    // .createPoint(new double[] { 1.0, 2.0 });
    //
    // // test add same node twice
    // try {
    // index.add(Record.create("me2", extent));
    // } catch (SpatialIndexException e) {
    // e.printStackTrace();
    // fail();
    // }
    // assertEquals(1, index.size());
    //
    // // test add same node twice
    // try {
    // index.add(Record.create("me3", extent));
    // } catch (SpatialIndexException e) {
    // e.printStackTrace();
    // fail();
    // }
    // assertEquals(1, index.size());
    //
    // // test add same node, different extent
    // Geometry extent1 = GeometryConverter.createPoint(new double[] { 1.0,
    // 3.0 });
    // try {
    // index.add(Record.create("m3", extent1));
    // } catch (SpatialIndexException e) {
    // e.printStackTrace();
    // fail();
    // }
    // assertEquals(1, index.size());
    //
    // }

}
