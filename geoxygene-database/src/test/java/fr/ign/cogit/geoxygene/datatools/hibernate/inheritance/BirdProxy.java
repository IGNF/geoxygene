/**
 * 
 */
package fr.ign.cogit.geoxygene.datatools.hibernate.inheritance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Julien Perret
 * 
 */
public class BirdProxy implements InvocationHandler {
  Object obj;

  public BirdProxy(Object obj) {
    this.obj = obj;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {
    try {// Do something on real object
      return method.invoke(this.obj, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    } catch (Exception e) {
      throw e;
    }
  }

  public static Object newInstance(Object obj, Class<?>[] interfaces) {
    return Proxy.newProxyInstance(obj.getClass().getClassLoader(), interfaces,
        new BirdProxy(obj));
  }
}
