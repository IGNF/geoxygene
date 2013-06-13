package fr.ign.cogit.cartagen.software.dataset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;

/**
 * A {@link GeneObjImplementation} instance informs on a particular
 * implementation of the IGeneObj schema classes. A {@link CartAGenDB} only
 * contains features from one of these implementations.
 * @author GTouya
 * 
 */
public class GeneObjImplementation {

  private String name;
  private Package rootPackage;
  private Class<?> rootClass;
  private AbstractCreationFactory creationFactory;

  public Class<?> getRootClass() {
    return rootClass;
  }

  public void setRootClass(Class<?> rootClass) {
    this.rootClass = rootClass;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Package getRootPackage() {
    return rootPackage;
  }

  public void setRootPackage(Package rootPackage) {
    this.rootPackage = rootPackage;
  }

  public GeneObjImplementation(String name, Package rootPackage,
      Class<?> rootClass, AbstractCreationFactory creationFactory) {
    super();
    this.name = name;
    this.rootPackage = rootPackage;
    this.rootClass = rootClass;
    this.creationFactory = creationFactory;
  }

  public static GeneObjImplementation getDefaultImplementation() {
    return new GeneObjImplementation("default",
        GeneObjDefault.class.getPackage(), GeneObjDefault.class,
        new DefaultCreationFactory());
  }

  /**
   * Filter a {@link Class} array to keep only those that are part of
   * {@code this} implementation.
   * @param classes
   * @return
   */
  public Class<?>[] filterClasses(Class<?>[] classes) {
    List<Class<?>> finalClasses = new ArrayList<Class<?>>();
    for (Class<?> classObj : classes) {
      if (rootClass.isAssignableFrom(classObj))
        finalClasses.add(classObj);
    }
    return finalClasses.toArray(new Class<?>[finalClasses.size()]);
  }

  /**
   * Filter a {@link Class} collection to keep only those that are part of
   * {@code this} implementation.
   * @param classes
   * @return
   */
  public Set<Class<?>> filterClasses(Set<Class<?>> classes) {
    Set<Class<?>> finalClasses = new HashSet<Class<?>>();
    for (Class<?> classObj : classes) {
      if (this.containsClass(classObj)) {
        finalClasses.add(classObj);
      }
    }
    return finalClasses;
  }

  @Override
  public String toString() {
    return name + " (" + rootPackage.getName() + ", "
        + rootClass.getSimpleName() + ")";
  }

  /**
   * Check if {@code this} implementation contains a given class by rootClass
   * inheritance and package compatibility (classObj package has to be the same
   * or a subpackage of rootPackage).
   * @param classObj
   * @return
   */
  public boolean containsClass(Class<?> classObj) {
    if (!rootClass.isAssignableFrom(classObj))
      return false;
    // now check package compatibility by String comparison
    if (classObj.getPackage().getName().contains(rootPackage.getName()))
      return true;
    return false;
  }

  public AbstractCreationFactory getCreationFactory() {
    return creationFactory;
  }

  public void setCreationFactory(AbstractCreationFactory creationFactory) {
    this.creationFactory = creationFactory;
  }
}
