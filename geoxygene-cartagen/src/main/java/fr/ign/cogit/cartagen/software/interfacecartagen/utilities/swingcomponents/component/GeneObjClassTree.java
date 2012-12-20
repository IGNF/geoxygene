/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.renderer.ClassSimpleNameTreeRenderer;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.ReflectionUtil;

/**
 * This swing component is a JTree that displays either all classes or all
 * interfaces of the GeneObj schema. Its renderer only displays the simple name
 * of the classes (or interfaces).
 * @author GTouya
 * 
 */
public class GeneObjClassTree extends JTree {

  /****/
  private static final long serialVersionUID = 1L;

  private boolean api;
  private Set<Class<?>> classes;
  private DefaultTreeModel dtm;

  public GeneObjClassTree(boolean api) {
    super();
    this.api = api;
    this.classes = new HashSet<Class<?>>();
    findClasses();
    this.setCellRenderer(new ClassSimpleNameTreeRenderer());
    this.setEditable(false);
    this.setRootVisible(false);
    // set the tree selection model
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(dtsm);
    makeTreeFromClasses();
    this.setModel(dtm);
  }

  private void makeTreeFromClasses() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(IGeneObj.class);
    classes.remove(IGeneObj.class);
    // recursively add subclasses as root children
    addSubClasses(root);
    dtm = new DefaultTreeModel(root);
  }

  private void addSubClasses(DefaultMutableTreeNode node) {
    Class<?> classObj = (Class<?>) node.getUserObject();
    for (Class<?> subClass : getAllSubClasses(classObj)) {
      if (!ReflectionUtil.isDirectBaseClass(classObj, subClass))
        continue;
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(subClass);
      node.add(child);
      addSubClasses(child);
    }
  }

  private Set<Class<?>> getAllSubClasses(Class<?> classObj) {
    Set<Class<?>> set = new HashSet<Class<?>>();
    for (Class<?> c : this.classes) {
      if (c.equals(classObj))
        continue;
      if (classObj.isAssignableFrom(classObj))
        set.add(c);
    }
    return set;
  }

  /**
   * Get all the {@link Class} objects to be displayed in the component
   * according to api and implementation.
   */
  private void findClasses() {
    // get the directory of the package of this class
    Package pack = this.getClass().getPackage();
    String name = pack.getName();
    name = name.replace('.', '/');
    name.replaceAll("%20", " ");
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    URL pathName = this.getClass().getResource(name);
    File directory = new File(pathName.getFile());
    // get the parent directories to get fr.ign.cogit.cartagen package
    while (!directory.getName().equals("cartagen")) {
      directory = directory.getParentFile();
    }
    String cleanDir = directory.toString().replaceAll("%20", " ");

    directory = new File(cleanDir);
    List<File> files = FileUtil.getAllFilesInDir(directory);
    for (File file : files) {
      if (!file.getName().endsWith(".class")) {
        continue;
      }
      if (file.getName().substring(0, file.getName().length() - 6).equals(
          "GothicObjectDiffusion")) {
        continue;
      }

      String path = file.getPath().substring(file.getPath().indexOf("fr"));
      String classname = FileUtil.changeFileNameToClassName(path);
      try {
        // Try to create an instance of the object
        Class<?> classObj = Class.forName(classname);
        if (api) {
          if (!classObj.isInterface())
            continue;
          if (IGeneObj.class.isAssignableFrom(classObj)) {
            this.classes.add(classObj);
          }
        } else {
          // verify that its an implementation class
          if (classObj.isInterface())
            continue;
          if (classObj.isLocalClass())
            continue;
          if (classObj.isMemberClass())
            continue;
          if (classObj.isEnum())
            continue;
          if (Modifier.isAbstract(classObj.getModifiers()))
            continue;

          // test if the class inherits from IGeneObj
          if (GeneObjDefault.class.isAssignableFrom(classObj)) {
            this.classes.add(classObj);
          } else if (classObj.isAnnotationPresent(Entity.class)) {
            this.classes.add(classObj);
          }
        }
      } catch (ClassNotFoundException cnfex) {
        continue;
        // cnfex.printStackTrace();
      } catch (NoClassDefFoundError e) {
        continue;
      }
    }
  }

  /**
   * Get the last selected class in the component.
   * @return
   */
  public Class<?> getSelectedClass() {
    return (Class<?>) ((DefaultMutableTreeNode) this.getSelectionPath()
        .getLastPathComponent()).getUserObject();
  }

  /**
   * Get the last selected class in the component.
   * @return
   */
  public Class<?>[] getSelectedClasses() {
    Class<?>[] selClasses = new Class<?>[this.getSelectionPaths().length];
    int i = 0;
    for (TreePath path : this.getSelectionPaths()) {
      selClasses[i] = (Class<?>) ((DefaultMutableTreeNode) path
          .getLastPathComponent()).getUserObject();
      i++;
    }
    return selClasses;
  }

  /**
   * Use the TreeSelectionModel static fields to define the mode parameter.
   * @param mode
   */
  public void setSelectionMode(int mode) {
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(mode);
    this.setSelectionModel(dtsm);
  }

  /**
   * Filter the tree classes to the ones that are sub classes of classObj.
   * @param classObj
   */
  public void filterClasses(Class<?> classObj) {
    HashSet<Class<?>> loopSet = new HashSet<Class<?>>(classes);
    for (Class<?> c : loopSet) {
      if (!classObj.isAssignableFrom(c))
        classes.remove(c);
    }
  }

}
