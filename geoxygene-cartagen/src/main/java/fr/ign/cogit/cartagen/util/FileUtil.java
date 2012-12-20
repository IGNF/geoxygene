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

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

/**
 * This class contains static methods useful to handle files and directories.
 * @author gtouya
 * 
 */
public class FileUtil {

  /**
   * Visits all files and directories in a given directory and gets all files
   * found.
   * @param dir
   * @return
   */
  public static List<File> getAllFilesInDir(File dir) {
    ArrayList<File> files = new ArrayList<File>();
    if (!dir.isDirectory()) {
      files.add(dir);
      return files;
    }
    for (File file : dir.listFiles()) {
      if (file.isDirectory())
        files.addAll(getAllFilesInDir(file));
      else
        files.add(file);
    }
    return files;
  }

  /**
   * Visits all files and directories in a given directory and gets all files
   * found, except the ones in the directories named in exceptColn.
   * @param dir
   * @return
   */
  public static List<File> getAllFilesInDir(File dir,
      Collection<String> exceptColn) {
    ArrayList<File> files = new ArrayList<File>();
    if (!dir.isDirectory()) {
      files.add(dir);
      return files;
    }
    for (File file : dir.listFiles()) {
      if (file.isDirectory() && !exceptColn.contains(file.getName()))
        files.addAll(getAllFilesInDir(file));
      else
        files.add(file);
    }
    return files;
  }

  /**
   * Visits all files and directories in a given directory and gets the name of
   * all files found.
   * @param dir
   * @return
   */
  public static List<String> getAllFileNamesInDir(File dir) {
    ArrayList<String> files = new ArrayList<String>();
    if (!dir.isDirectory()) {
      files.add(dir.getName());
      return files;
    }
    for (File file : dir.listFiles()) {
      if (file.isDirectory())
        files.addAll(getAllFileNamesInDir(file));
      else
        files.add(file.getName());
    }
    return files;
  }

  /**
   * Change the passed file name to its corresponding class name.The file path
   * has to be limited to the package part (i.e. no "C:" for instance)
   * 
   * @param filePath Class name to be changed. If this does not represent a Java
   *          class then <TT>null</TT> is returned.
   * 
   * @throws IllegalArgumentException If a null <TT>name</TT> passed.
   */
  public static String changeFileNameToClassName(String filePath) {
    if (filePath == null) {
      throw new IllegalArgumentException("File Name == null");
    }
    String className = null;
    if (filePath.toLowerCase().endsWith(".class")) {
      className = filePath.replace('/', '.');
      className = className.replace('\\', '.');
      className = className.substring(0, className.length() - 6);
    }
    return className;
  }

  /**
   * Get the path of the folder of a given CartAGen resource (e.g. "ontologies",
   * "loaded_data" or "XML").
   * @param resource
   * @return
   */
  public static String getResourceFolderPath(String resource) {
    String pathTarget = FileUtil.class.getResource(resource).getPath()
        .replaceAll("%20", " ");
    File target = new File(pathTarget);
    while (!target.getName().equals("target"))
      target = target.getParentFile();
    File root = target.getParentFile();
    String resourcePath = root.getPath().concat(
        "\\src\\main\\resources\\" + resource);
    return resourcePath;
  }

  /**
   * Get a file inside a given directory from its name.
   * @param directory the directory to search in
   * @param filename
   * @return
   */
  public static File getNamedFileInDir(File directory, String filename) {
    if (!directory.isDirectory())
      return null;
    for (File file : directory.listFiles()) {
      if (file.getName().equals(filename))
        return file;
    }
    return null;
  }

  /**
   * Get all the classes (or interfaces) in a package (and its subpackages) that
   * have superClass has a super class.
   * @param pack
   * @param superClass
   * @param isInterface
   * @return
   */
  public static Set<Class<?>> findClassesInPackage(Package pack,
      Class<?> superClass, boolean isInterface) {
    Set<Class<?>> classes = new HashSet<Class<?>>();

    // get the directory of the package of this class
    String name = pack.getName();
    name = name.replace('.', '/');
    name.replaceAll("%20", " ");
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    URL pathName = FileUtil.class.getResource(name);
    File directory = new File(pathName.getFile());
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
        if (isInterface) {
          if (!classObj.isInterface())
            continue;
          if (superClass.isAssignableFrom(classObj)) {
            classes.add(classObj);
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
          if (superClass.isAssignableFrom(classObj)) {
            classes.add(classObj);
          } else if (classObj.isAnnotationPresent(Entity.class)) {
            classes.add(classObj);
          }
        }
      } catch (ClassNotFoundException cnfex) {
        continue;
        // cnfex.printStackTrace();
      } catch (NoClassDefFoundError e) {
        continue;
      }
    }

    return classes;
  }

}
