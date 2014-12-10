package fr.ign.cogit.geoxygene.matching.geopoint;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class PositionNameTest {
  
  @Test
  public void testAppariementPPVEvalTop() {
    IPopulation<IFeature> popComp = ShapefileReader.read("./data/ign-point/LIEU_DIT_HABITE.shp", "oronyme", null, true);
    IPopulation<IFeature> popRef = ShapefileReader.read("./data/ign-point/ZONE_HABITAT.shp", "Relief", null, true);
    Assert.assertEquals("Pop lieu dit habite mal chargee : ", 445, popComp.size());
    Assert.assertEquals("Pop zone habitat mal chargée : ", 65, popRef.size());
    
    double seuilEcart = 20.0;
    double seuilDistanceMax = 50.0;
    EnsembleDeLiens edl = SequentiallyPositionName.appariementPPVEvalTop(popRef, popComp, seuilEcart, seuilDistanceMax, "TOPONYME", "NOM");
    Assert.assertEquals("Nombre de liens trouvés = ", 33, edl.size());
  }

}
