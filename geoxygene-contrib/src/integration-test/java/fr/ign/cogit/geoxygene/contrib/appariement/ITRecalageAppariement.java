package fr.ign.cogit.geoxygene.contrib.appariement;

import java.io.ByteArrayOutputStream;

import org.custommonkey.xmlunit.XMLTestCase;
import org.geotools.GML;
import org.geotools.GML.Version;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;

/**
 * Tests d'intégration sur le recalage.
 * 
 */
public class ITRecalageAppariement extends XMLTestCase {
    
    /**
     * 
     * @throws Exception
     */
   /* @Test
    public void testAppariementDefautAvecRecalage() throws Exception {
        
        URL url = ITRecalageAppariement.class.getResource("/data/");
        
        // Réseaux
        IPopulation<IFeature> reseau1 = ShapefileReader.read(url.getPath() + "reseau1.shp");
        IPopulation<IFeature> reseau2 = ShapefileReader.read(url.getPath() + "reseau2.shp");
        
        // Résultats de l'appariement avec l'ancienne structure des objets
        ParametresApp paramApp = new ParametresApp();
        paramApp.attributOrientation1 = null;
        paramApp.attributOrientation2 = null;
        paramApp.debugBilanSurObjetsGeo = false;
        paramApp.populationsArcs1.add(reseau1);
        paramApp.populationsArcs2.add(reseau2);
        paramApp.debugBilanSurObjetsGeo = true;
        List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
        EnsembleDeLiens edl1 = AppariementIO.appariementDeJeuxGeo(paramApp, cartesTopo);
        CarteTopo reseauRecale1 = Recalage.recalage(cartesTopo.get(0), cartesTopo.get(1), edl1);
        
        // Résultats de l'appariement avec la nouvelle structure des objets
        ParamNetworkDataMatching param = new ParamNetworkDataMatching();
        DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
        datasetNetwork1.addPopulationsArcs(reseau1);
        DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
        datasetNetwork2.addPopulationsArcs(reseau2);
        NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2, true);
        ResultNetworkDataMatching resultatAppariement2 = networkDataMatchingProcess.networkDataMatching();
        CarteTopo reseauRecale2 = Recalage.recalage(resultatAppariement2.getReseau1(), resultatAppariement2.getReseau2(), resultatAppariement2.getLiensGeneriques());
        
        // -----------------------------------------------------------------------------------------------------------------------
        // On compare les GML des résultats du recalage
        compareCarteRecale(reseauRecale1, reseauRecale2);
        
    }*/
    
    
    /**
     * 
     * @param reseauRecale1
     * @param reseauRecale2
     * @throws Exception
     */
    private void compareCarteRecale(CarteTopo reseauRecale1,  CarteTopo reseauRecale2) throws Exception {

        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        
        GML encode = new GML(Version.WFS1_0);
        encode.setNamespace("geotools", "http://geotools.org");
        
        // Arcs recales du reseau 1
        SimpleFeatureCollection arcs1 = GeOxygeneGeoToolsTypes.convert2FeatureCollection(reseauRecale1.getPopArcs(), sourceCRS);
        ByteArrayOutputStream output1 = new ByteArrayOutputStream();
        encode.encode(output1, arcs1);
        
        // Arcs recales du reseau 2
        SimpleFeatureCollection arcs2 = GeOxygeneGeoToolsTypes.convert2FeatureCollection(reseauRecale2.getPopArcs(), sourceCRS);
        ByteArrayOutputStream output2 = new ByteArrayOutputStream();
        encode.encode(output2, arcs2);
        
        // On compare : est-ce que le XML est comparable ????
        assertXMLEqual(output1.toString(), output2.toString());
    }
    
    
    public void test2() throws Exception {
        
        String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><root><a></a><b></b></root>";
        
        String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><root><b></b><a></a></root>";
        
        assertXMLEqual(xml1, xml2);
    }

}
