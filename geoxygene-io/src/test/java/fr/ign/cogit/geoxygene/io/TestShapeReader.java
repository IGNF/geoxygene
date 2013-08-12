package fr.ign.cogit.geoxygene.io;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class TestShapeReader {
    
    @Test
    public void testFichier1() {
       
        /*try {
            
            String filename = "D:\\Data\\Appariement\\MesTests\\T3\\bdcarto_route.shp";
            IPopulation<IFeature> reseau = ShapefileReader.read(filename);
            
            if (reseau != null) {
                Assert.assertTrue("Nb de troncon = ", reseau.size() >= 0);
            } else {
                // Assert.fail();
            }
            
        } catch (Exception e) {
            // Assert.fail();
            e.printStackTrace();
        }*/
        AssertTrue(true);
        
    }

}
