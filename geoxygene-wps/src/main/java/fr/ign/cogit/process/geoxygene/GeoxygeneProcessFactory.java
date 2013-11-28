package fr.ign.cogit.process.geoxygene;


import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.text.Text;

import fr.ign.cogit.process.geoxygene.cartetopo.CreateCarteTopoProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.CreateFacesProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.ImportCarteTopoProcess;
import fr.ign.cogit.process.geoxygene.netmatching.LinearFeatureMatcherProcess;
import fr.ign.cogit.process.geoxygene.netmatching.NetworkDataMatchingProcess;
import fr.ign.cogit.process.geoxygene.netmatching.NetworkDataMatchingWithParamProcess;

/**
 * 
 * 
 * @author MDVan-Damme
 */
public class GeoxygeneProcessFactory extends AnnotatedBeanProcessFactory {

  public GeoxygeneProcessFactory() {
    
    super(Text.text("Geoxygene processes"), "cogit",
        CreateFacesProcess.class,
        CreateCarteTopoProcess.class,
        // ImportCarteTopoProcess.class,
        NetworkDataMatchingProcess.class,
        NetworkDataMatchingWithParamProcess.class,
        LinearFeatureMatcherProcess.class);
  }

}