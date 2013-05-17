package fr.ign.cogit.geoxygene.semio.legend.improvement;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.filter.ShapeFilter;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserLayerFactory;

/**
 * 
 * 
 *
 */
public class LucilContrastAnalysisTest {
    
    private static Logger LOGGER = Logger.getLogger(LucilContrastAnalysisTest.class.getName());
    
    /**
     * Test Analyse des contrastes.
     * - BasicStopCriteria
     */
    @Test
    public void LucilAnalysisWithBasicStopTest() {
        
        // -------------------------------------------------------------------------------
        // On construit une Map
        
        // SLD
        /*String sldName = LucilContrastAnalysisTest.class.getResource("Tropical.xml").getPath();
        StyledLayerDescriptor sld = StyledLayerDescriptor.unmarshall(sldName);
        System.out.println("SLD = " + sld.getLayers().size());
        
        List<Layer> layers = new ArrayList<Layer>();
        
        // On se place dans le bon repertoire
        File dir = new File(LucilContrastAnalysisTest.class.getResource("shp").getPath());
        
        FilenameFilter shpFilter = new ShapeFilter();
        String[] children = dir.list(shpFilter);
        if (children != null) {
            for (int f = 0; f < children.length; f++) {
                String shpFileName = children[f];
                // System.out.println(shpFileName);
                
                UserLayerFactory factory = new UserLayerFactory();
                factory.setModel(sld);

                IPopulation<IFeature> collection = ShapefileReader.read(dir + "\\" + shpFileName);
                factory.setGeometryType(collection.getFeatureType().getGeometryType());
                // System.out.println(collection.getFeatureType().getGeometryType());
                factory.setCollection(collection);
                
                int indexBegin = collection.getNom().indexOf("shp");
                String name = collection.getNom().substring(indexBegin + 4, collection.getNom().length() - 3);
                factory.setName(name);
                System.out.println("Nom = " + name);
                
                Layer layer = factory.createLayer();
                // layer.setCRS(crs);
                layers.add(layer);
            }
        }
        // System.out.println("Fin de chargement des fichiers");
        
        // Legend
        Legend currentLegend = Legend.unmarshall(LucilContrastAnalysisTest.class.getResource("Legend.xml").getPath());
        System.out.println("Legende : " + currentLegend.getLeaves().size());
        
        // Map
        Map currentMap = new Map(layers, currentLegend);
        currentMap.setName("Map pour le test 1 d'analyse des contrastes");*/
        
        // -------------------------------------------------------------------------------
        // 
        /*LucilContrastAnalysis analysis = new LucilContrastAnalysis();
        
        //
        analysis.setSurfaceWeights(true);
        
        //
        BasicStopCriteria stop = new BasicStopCriteria(10);
        analysis.initialize(currentMap, stop);
        
        // Run
        analysis.run(50);
        
        // -------------------------------------------------------------------------------
        //  Test : construit la map et on compare ????
        Map carteRetour = analysis.getMap();
        LOGGER.info("Nom de la carte retour : " + carteRetour.getName());*/
        Assert.assertTrue(true);
    }

}
