/**
 * 
 */
package fr.ign.cogit.geoxygene.example.hibernate;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;

/**
 * @author Julien Perret
 * 
 */
public class BirdProxyInterceptor extends EmptyInterceptor {
  /**
	 * 
	 */
  private static final long serialVersionUID = 6222736026797506449L;

  @Override
  public String getEntityName(Object object) {
    System.out
        .println("getEntityName " + object.getClass() + " ( " + Proxy.isProxyClass(object.getClass()) + " )"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    if (Proxy.isProxyClass(object.getClass())
        && object instanceof BirdInterface) {
      System.out.println("BirdInterface"); //$NON-NLS-1$
      return BirdInterface.class.getName();
    }
    return super.getEntityName(object);
  }

  @Override
  public Object instantiate(String entityName, EntityMode entityMode,
      Serializable id) {
    if (entityName.equals(BirdInterface.class.getName())) {
      BirdImpl newBird = new BirdImpl();
      newBird.setId(((Integer) id).intValue());
      BirdInterface foo = (BirdInterface) BirdProxy.newInstance(newBird,
          new Class[] { BirdInterface.class });
      return foo;
    }
    return super.instantiate(entityName, entityMode, id);
  }
}
