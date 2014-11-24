package fr.ign.cogit.geoxygene.contrib.graph;

import junit.framework.Assert;

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
    
    try {
      IPopulation<IFeature> pointPop = ShapefileReader.read("./data/parking/parking.shp", "Parking", null, true);
      IPopulation<IFeature> lignePop = ShapefileReader.read("./data/parking/troncon_de_route.shp", "Ligne", null, true);
      if (pointPop.size() > 0 && lignePop.size() > 0) {
        Assert.assertEquals("Nombre de points chargés.", 6, pointPop.size());
        Assert.assertEquals("Nombre de lignes chargées.", 72, lignePop.size());
      
        boolean deltaZOption = false;
        double distance = 10;
        Population<DefaultFeature> pointsProjetes = PPV.run(pointPop, lignePop, deltaZOption, distance);
      
        Assert.assertEquals("Nb points projetes = ", 5, pointsProjetes.size());
      }
    } catch (Exception e) {
      System.out.println("Impossible de charger le jeu de données");
    }
    
  }

}
