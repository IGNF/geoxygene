/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Gï¿½ographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util;

import java.lang.reflect.Method;
import java.util.Collection;
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

  /**
   * Return all getters of a given class that have their return type contained
   * in the paramater collection of accepted types.
   * @param classObj
   * @param types
   * @return
   */
  public static Collection<Method> getAllGetters(Class<?> classObj,
      Collection<Class<?>> types) {
    Collection<Method> methods = new HashSet<Method>();
    for (Method meth : classObj.getMethods()) {
      if (!meth.getName().startsWith("get"))
        continue;
      if (types.contains(meth.getReturnType()))
        methods.add(meth);
    }
    return methods;
  }

  /**
   * When working with String class names, returns true if a collection contains
   * a given class name or the names of one superclass of the given class name.
   * @param classNames
   * @param className
   * @return
   * @throws ClassNotFoundException
   */
  public static boolean containsClassOrSuper(Collection<String> classNames,
      String className) throws ClassNotFoundException {
    if (classNames.contains(className))
      return true;
    Class<?> classToCompare = Class.forName(className);
    for (String name : classNames) {
      Class<?> currentClass = Class.forName(name);
      if (currentClass.isAssignableFrom(classToCompare))
        return true;
    }
    return false;
  }
}
