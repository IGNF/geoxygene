package fr.ign.cogit.process.geoxygene;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.factory.FactoryRegistry;
import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.text.Text;

import fr.ign.cogit.process.geoxygene.cartetopo.PlanarGraphCreationProcess;
import fr.ign.cogit.process.geoxygene.cartetopo.BuildingBlocksCreationProcess;
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

  /*public GeoxygeneProcessFactory() {
    super(Text.text("Geoxygene processes"), "cogit",
        CreateFacesProcess.class,
        CreateCarteTopoProcess.class,
        ImportCarteTopoProcess.class,
        NetworkDataMatchingProcess.class,
        NetworkDataMatchingWithParamProcess.class,
        LinearFeatureMatcherProcess.class);
  }*/
  
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
  
  /**
   * Subclass of FactoryRegistry meant for convenience of looking up all the classes
   * that implement a specific bean interface.
   */
  public static class BeanFactoryRegistry<T> extends FactoryRegistry {

      public BeanFactoryRegistry(Class<T> clazz) {
          super(clazz);
      }

      public Class<T> getBeanClass() {
          return (Class<T>) getCategories().next();
      }

      public Class<? extends T>[] lookupBeanClasses() {
          Iterator<T> it = getServiceProviders(getBeanClass(), null, null);
          List<Class> list = new ArrayList();
          while(it.hasNext()) {
              list.add((Class<? extends T>) it.next().getClass());
          }
          return list.toArray(new Class[list.size()]);
      }
  }

}