package fr.ign.cogit.process.geoxygene;

import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.text.Text;

import fr.ign.cogit.process.geoxygene.cartetopo.CreateCarteTopoProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.CreateFacesProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.ImportCarteTopoProcess;
import fr.ign.cogit.process.geoxygene.netmatching.NetworkDataMatchingProcess;
import fr.ign.cogit.process.geoxygene.netmatching.NetworkDataMatchingWithParamProcess;

/**
 * 
 * 
 * @author MDVan-Damme
 */
public class GeoxygeneProcessFactory extends AnnotatedBeanProcessFactory {

    static volatile BeanFactoryRegistry<GeoxygeneProcess> registry;

    public static BeanFactoryRegistry<GeoxygeneProcess> getRegistry() {
        if (registry == null) {
            synchronized (GeoxygeneProcessFactory.class) {
                if (registry == null) {
                    registry = new BeanFactoryRegistry<GeoxygeneProcess>(GeoxygeneProcess.class);
                }
            }
        }
        return registry;
    }

    public GeoxygeneProcessFactory() {
        super(Text.text("Geoxygene processes"), "cogit", getRegistry().lookupBeanClasses());
    }
  
  /*public GeoxygeneProcessFactory() {
    super(Text.text("Geoxygene processes"), "cogit",
        CreateFacesProcess.class,
        CreateCarteTopoProcess.class,
        ImportCarteTopoProcess.class,
        NetworkDataMatchingProcess.class,
        NetworkDataMatchingWithParamProcess.class);
  }*/

}