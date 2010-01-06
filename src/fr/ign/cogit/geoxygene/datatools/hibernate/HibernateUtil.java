/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.hibernate;

/**
 * @author Julien Perret
 *
 */
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import fr.ign.cogit.geoxygene.example.hibernate.BirdProxyInterceptor;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final Configuration configuration;

    static {
        try {
        	configuration = new AnnotationConfiguration();
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = configuration.configure().setInterceptor(
                    new BirdProxyInterceptor()).buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println(
                    "Initial SessionFactory creation failed." //$NON-NLS-1$
                    + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * @return the unique session factory.
     */
    public static SessionFactory getSessionFactory() { return sessionFactory; }

    /**
     * @return the unique configuration.
     */
    public static Configuration getConfiguration() { return configuration; }
}
