/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.hibernate;

/**
 * @author Julien Perret
 * 
 */
import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

  private static SessionFactory sessionFactory = null;
  private static Configuration configuration = null;
  public static String cheminFichierConfiguration = null;

  /**
   * @return the unique session factory.
   */
  public static SessionFactory getSessionFactory() {
    if (HibernateUtil.sessionFactory == null) {
      if (HibernateUtil.cheminFichierConfiguration == null) {
        HibernateUtil.sessionFactory = HibernateUtil.getConfiguration()
            .configure().buildSessionFactory();
      } else {
        HibernateUtil.sessionFactory = HibernateUtil.getConfiguration()
            .configure(new File(HibernateUtil.cheminFichierConfiguration))
            .buildSessionFactory();
      }
    }
    return HibernateUtil.sessionFactory;
  }

  /**
   * @return the unique configuration.
   */
  public static Configuration getConfiguration() {
    if (HibernateUtil.configuration == null) {
      HibernateUtil.configuration = new AnnotationConfiguration();
    }
    return HibernateUtil.configuration;
  }

}
