package fr.ign.cogit.geoxygene.contrib.graph;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.graphe.PPV;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class PPPVTest {
  
  @Test
  public void testParking() {
    
    IPopulation<IFeature> pointPop = ShapefileReader.read("./data/parking/parking.shp", "Parking", null, true);
    System.out.println("Nb points = " + pointPop.size());
    IPopulation<IFeature> lignePop = ShapefileReader.read("./data/parking/troncon_de_route.shp", "Ligne", null, true);
    
    boolean deltaZOption = false;
    double distance = 10;
    Population<DefaultFeature> pointsProjetes = PPV.run(pointPop, lignePop, deltaZOption, distance);
    
    System.out.println("Nb points projetes = " + pointsProjetes.size());
    
  }

}
