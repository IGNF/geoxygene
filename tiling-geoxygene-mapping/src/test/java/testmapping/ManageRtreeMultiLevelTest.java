package testmapping;

import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.RtreeMultiLevelIndex;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.PostgresExtractor;
import fr.ign.cogit.mapping.util.ScaleInfoUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryConverter;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndexException;

/*
 * Le lancement des methodes du manager doit
 * se faire sequentiellement sinon certains processus
 * peut bloquer cette ressource et compromettre les resultats 
 */
public class ManageRtreeMultiLevelTest {

    ConcurrentHashMap<ScaleInfo, RTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, RTreeIndex>();
    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/webentity/spatial";

    RtreeMultiLevelIndex multiLevel = new RtreeMultiLevelIndex(multiLevelIndex);
    ManageRtreeMultiLevel manager = new ManageRtreeMultiLevel(multiLevel,
            Directory);
    // private RTreeIndex defaultGraphIndex;
    // intanciation des classes
    protected RTreeIndex scale0 = new RTreeIndex(Directory, "scale0");
    protected RTreeIndex scale15 = new RTreeIndex(Directory, "scale15");
    protected RTreeIndex scale25 = new RTreeIndex(Directory, "scale25");
    protected RTreeIndex scale50 = new RTreeIndex(Directory, "scale50");

    @Test
    public void intialisation() {

        ScaleInfo scale = new ScaleInfo(15, 25);
        Node node = new Node("4589", scale, "geometrie", "myName");

        // defaultGraphIndex =new
        // RTreeIndex("C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mappingwebentity\spatial");
        // System.out.println("hello" + scale0.getIndexDir());
        // System.out.println("hello" + scale15.getIndexDir());
        // System.out.println("hello" + scale25.getIndexDir());
        // Assert.assertEquals("Hello World", "Hello World");
        //
        // // chargement des differentes echelles
        // ScaleInfo scale = new ScaleInfo(0,15);
        // multiLevelIndex.put(scale, scale0);
        //
        // testAddGeometry(scale0);
        //
        // scale = new ScaleInfo(15,25);
        // multiLevelIndex.put(scale, scale15);
        //
        // testAddGeometry(scale15);
        //
        // scale = new ScaleInfo(25,50);
        // multiLevelIndex.put(scale, scale25);
        //
        // testAddGeometry(scale25);
        //
        //
        // scale = new ScaleInfo(50,100);
        // multiLevelIndex.put(scale, scale50);
        //
        // testAddGeometry(scale50);
        // test bon

        // manager.createOneLevel(0, 15);
        // RTreeIndex index0 = manager.useAnLevel(0);
        // if (index0!=null){
        // testAddGeometry(index0);
        // }
        //
        // // test bon
        // manager.createOneLevel(15, 25);
        // RTreeIndex index15 = manager.useAnLevel(15);
        // if (index15!=null){
        // testAddGeometry(index15);
        // }
        //

        // test bon
        // manager.createOneLevel(25, 50);
        // RTreeIndex index25 = manager.useAnLevel(25);
        // if (index25!=null){
        // testAddGeometry(index25);
        // }

        // test bon
        // manager.createOneLevel(50, 75);
        // RTreeIndex index50 = manager.useAnLevel(50);
        // if (index50!=null){
        // testAddGeometry(index50);
        // }
        //

        // test bon
//        manager.createOneLevel(75, 100);
//        RTreeIndex index75 = manager.useAnLevel(75);
//        if (index75 != null) {
//            // testAddGeometry(index75, node);
//        }
        // manager.deleteAllLevel();

        //
        // manager.deleteOneLevel(15);

        // manager.ChargeLevels();

        // testAddGeometry(namedGraphIndex);

        // nouvelle version de test de l'index manager multi-level...
        /*
         * Ce test s'effectue sur une base de donnéées postgis.. dont les
         * informations de connexion sont renseignées dans la @see Constants.
         */

        // table de test dans la base de données
        String tableName = "ref_patte_d_oie";

        // on obient les informations sur l'echelle en fonction
        // du nom de la table qui permettrons de fabriquer la cle
        // d

        scale = ScaleInfoUtil.generateScale(tableName);

        // on fabrique une connexion à la base de donnée
        PostgresExtractor myextractor = new PostgresExtractor();

        // on fabrique les interateurs de cette table qui seront
        // renseignés dans l'index..;

//        Iterator<Record<Geometry>> it = myextractor.generateIteratorTable(
//                tableName, "geometrie", scale);

        // utiliser le test du manager en multi-niveauxs

        // manager.createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
        // System.out.println(scale.getMinScaleValue());
        // //manager.deleteAllLevel();
        // RTreeIndex index =manager.useAnLevel(scale.getMinScaleValue()) ;
        // loadValues(index, it);

        // chargement des batiments
        /***
         * tableName = "batiment_fonctionnel_25"; scale=
         * ScaleInfoUtil.generateScale(tableName); it=
         * myextractor.generateIteratorTable(tableName,"geometrie",scale);
         * manager
         * .createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
         * index =manager.useAnLevel(scale.getMinScaleValue()) ;
         * loadValues(index, it);
         ***/

        // chargement des points d'eaux dans l'index..
        /***
         * tableName = "point_d_eau_25"; scale=
         * ScaleInfoUtil.generateScale(tableName); it=
         * myextractor.generateIteratorTable(tableName,"geometrie",scale);
         * manager
         * .createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
         * index =manager.useAnLevel(scale.getMinScaleValue()) ;
         * loadValues(index, it);
         ***/
        /**
         * tableName = "route_numerotee_ou_nommee"; scale=
         * ScaleInfoUtil.generateScale(tableName); it=
         * myextractor.generateIteratorTable(tableName,"geometrie",scale);
         * manager
         * .createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
         * index =manager.useAnLevel(scale.getMinScaleValue()) ;
         * loadValues(index, it);
         **/
        /**
         * tableName = "troncon_de_route"; scale=
         * ScaleInfoUtil.generateScale(tableName); it=
         * myextractor.generateIteratorTable(tableName,"geometrie",scale);
         * manager
         * .createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
         * index =manager.useAnLevel(scale.getMinScaleValue()) ;
         * loadValues(index, it);
         **/

        /*
         * Decoupage de tous les elements de la base de données
         */

        // Map<Integer, Map<String, String>>test= myextractor.readAllTables();
        //
        // for( Integer table : test.keySet()){
        //
        // Map<String, String> entry = test.get(table);
        // //
        // // Map<Integer, String>
        // //
        // mcontent=myextractor.readTableGeometry(entry.get("table"),entry.get("geometry_colum"));
        // //
        // // for(Integer key : mcontent.keySet()) {
        // // System.out.println(mcontent.get(key));
        // // }
        //
        // tableName = entry.get("table");
        // scale= ScaleInfoUtil.generateScale(tableName);
        // it=
        // myextractor.generateIteratorTable(tableName,entry.get("geometry_colum"),scale);
        // manager.createOneLevel(scale.getMinScaleValue(),scale.getMaxScaleValue());
        // index =manager.useAnLevel(scale.getMinScaleValue()) ;
        // loadValues(index, it);
        // }

        // suppression de tous les indexes...

         manager.ChargeLevels();
         System.out.println(manager.toString());

    }

    /*
     * Ce test fonctionne uniquemet lorsque l'index est supposé comme simple
     * string actuellement l'index est enrichi avec d'autres information
     * provenant de la base de données
     */

    public void testAddGeometry(SpatialIndex index, Node node) {
        index.open();
        // Node n = Node.createURI("http://test.org#test");
        Geometry extent = GeometryConverter
                .createPoint(new double[] { 1.0, 1.0 });
        // test add node
        try {
            index.add(Record.create(node, extent));
        } catch (SpatialIndexException e) {
            e.printStackTrace();
            fail();
        }
        // assertEquals(1, index.size());

        // test add another node
        // n = Node.createURI("http://test.org#test2");
        extent = GeometryConverter.createPoint(new double[] { 1.0, 2.0 });
        try {
            index.add(Record.create(node, extent));
        } catch (SpatialIndexException e) {
            e.printStackTrace();
            fail();
        }
        // assertEquals(2, index.size());

        double[] array = { 20, 10, 30, 0, 40, 10, 30, 20 };
        extent = GeometryConverter.createLineString(array);

        try {
            index.add(Record.create(node, extent));
        } catch (SpatialIndexException e) {
            e.printStackTrace();
            fail();
        }
        // assertEquals(3, index.size());

        double[] array1 = { 20, 10, 30, 0, 40, 10, 30, 20, 20, 10 };
        extent = GeometryConverter.createPolygon(array1);

        try {
            index.add(Record.create(node, extent));
        } catch (SpatialIndexException e) {
            e.printStackTrace();
            fail();
        }
        // assertEquals(4, index.size());

        System.out.println("look my size " + index.size());
        Record r = Record.create(node, extent);
        index.add(r);
        System.out.println("look my size " + index.size());

    }

    public void loadValues(SpatialIndex index, Iterator<Record<Geometry>> it) {
        if (it != null) {
            index.open();

            // // System.out.println(index.size());
            // //
            // // index.doOpen();
            // // System.out.println(index.size());
            // index.add(it);
            // //
            while (it.hasNext()) {
                index.add(it.next());
                // ////
                // ////
                // ////
            }
            // //
            // //
            index.close();
            //
        }

    }

}
