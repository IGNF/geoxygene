package fr.ign.cogit.process.geoxygene;

import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.text.Text;

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

}