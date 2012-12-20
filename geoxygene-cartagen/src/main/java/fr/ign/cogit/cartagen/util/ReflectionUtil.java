/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * This class gathers static methods to ease reflection procedures.
 * @author GTouya
 * 
 */
public class ReflectionUtil {

  /**
   * Get all the superclasses of a given class.
   * @param classObj
   * @return
   */
  public static Set<Class<?>> getSuperClasses(Class<?> classObj) {
    Set<Class<?>> classList = new HashSet<Class<?>>();
    Class<?> superclass = classObj.getSuperclass();
    classList.add(superclass);
    while (superclass != null) {
      superclass = superclass.getSuperclass();
      classList.add(superclass);
    }
    return classList;
  }

  /**
   * Get all the superclasses and interfaces of a given class.
   * @param classObj
   * @return
   */
  public static Set<Class<?>> getSuperClassesAndInterfaces(Class<?> classObj) {
    Set<Class<?>> classList = new HashSet<Class<?>>();
    // first get the superclasses
    Class<?> superclass = classObj.getSuperclass();
    if (superclass != null) {
      classList.add(superclass);
    }
    while (superclass != null) {
      superclass = superclass.getSuperclass();
      if (superclass != null) {
        classList.add(superclass);
      }
    }

    // then get the interfaces
    Stack<Class<?>> stack = new Stack<Class<?>>();
    for (Class<?> c : classObj.getInterfaces()) {
      stack.add(c);
    }
    while (!stack.isEmpty()) {
      Class<?> current = stack.pop();
      classList.add(current);
      for (Class<?> c : current.getInterfaces()) {
        if (!classList.contains(c)) {
          stack.add(c);
        }
      }
    }

    return classList;
  }

  /**
   * Get the method of a class declared in the class or in its superclasses.
   * Returns null when method isn't found.
   * @param classObj
   * @param methodName
   * @param parameterTypes
   * @return
   * @throws SecurityException
   */
  public static Method getInheritedMethod(Class<?> classObj, String methodName,
      Class<?>... parameterTypes) throws SecurityException {
    Class<?> currentClass = classObj;
    boolean found = false;
    while (!found) {
      try {
        Method meth = currentClass
            .getDeclaredMethod(methodName, parameterTypes);
        return meth;
      } catch (NoSuchMethodException e) {
        if (currentClass.equals(Object.class)) {
          return null;
        }
        currentClass = currentClass.getSuperclass();
      }
    }
    return null;
  }

  /**
   * Determine the depth of inheritance between a super class (baseClass) and
   * one of its sub classes (subClass). It corresponds to the number
   * inheritances in the OO hierarchy of classes (and interfaces) between both
   * classes (e.g. 1 for direct inheritance).
   * 
   * @param baseClass
   * @param subClass
   * @return the depth of inheritance (-1 if baseClass is not assignable from
   *         subClass)
   */
  public static int getInheritanceDepth(Class<?> baseClass, Class<?> subClass) {
    if (!baseClass.isAssignableFrom(subClass))
      return -1;
    if (baseClass.equals(subClass))
      return 0;
    // direct inheritance case
    if (ReflectionUtil.isDirectBaseClass(baseClass, subClass))
      return 1;

    // general case
    int level = 1;
    boolean direct = false;
    Set<Class<?>> baseClasses = getSuperClassesAndInterfaces(subClass);
    while (direct) {
      Set<Class<?>> loopSet = new HashSet<Class<?>>(baseClasses);
      baseClasses.clear();
      for (Class<?> bClass : loopSet) {
        if (ReflectionUtil.isDirectBaseClass(bClass, baseClass))
          break;
        baseClasses.addAll(getSuperClassesAndInterfaces(bClass));
      }
      level++;
    }
    return level;
  }

  /**
   * Determine if a class object (baseClass) is a direct base class in the OO
   * sense of another class (subClass). In Java, it means that baseClass is the
   * superclass or one of the implemented interfaces of subClass.
   * @param baseClass
   * @param subClass
   * @return
   */
  public static boolean isDirectBaseClass(Class<?> baseClass, Class<?> subClass) {
    if (baseClass.equals(subClass.getSuperclass()))
      return true;
    for (Class<?> interfaceObj : subClass.getInterfaces()) {
      if (interfaceObj.equals(baseClass))
        return true;
    }
    return false;
  }
}
