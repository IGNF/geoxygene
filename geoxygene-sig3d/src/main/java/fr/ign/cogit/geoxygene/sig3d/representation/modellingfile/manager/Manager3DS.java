package fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BranchGroup;

import com.mnstarfire.loaders3d.Loader3DS;
import com.sun.j3d.loaders.Scene;

import fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.IManagerModel;


/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * 
 * Cette classe est un singleton et ne peut donc être implémentée directement
 * 
 * Cette classe permet de gèrer les objets de types .3ds. On ne conserve qu'un
 * objet 3ds par chemin .Cela permet de ne pas avoir à recharger un symbole à
 * chaque fois que l'on souhaite représenter une nouvelle fois un symbole déjà
 * utilisé.
 * 
 * This class manages symbology of .3ds files. The symbol is loaded once for
 * each file path
 * 
 */
public class Manager3DS implements IManagerModel {

  public static boolean TEXTURED = true;
  // Tableaux de BranchGroup (symboles) et de chemins.
  private static List<BranchGroup> bGroups = new ArrayList<BranchGroup>();
  private static List<String> pathModels = new ArrayList<String>();

  private static Manager3DS manager = null;

  /**
   * Le manager est un singleton. Pour pouvoir influer sur le Manager, il faut
   * passer par cette méthode statique
   * 
   * @return renvoie l'instance du manager 3DS
   */
  public static Manager3DS getInstance() {
    if (Manager3DS.manager == null) {

      Manager3DS.manager = new Manager3DS();
    }

    return Manager3DS.manager;

  }

  private Manager3DS() {
    super();
  }

  /**
   * Cette classe permet de récupèrer le BranchGroup (symbole) venant d'un
   * fichier de modélisation.
   */
  @Override
  public BranchGroup loadingFile(String pathShape) {
    int nbTextures = Manager3DS.bGroups.size();

    for (int i = 0; i < nbTextures; i++) {

      if (Manager3DS.pathModels.get(i).equals(pathShape)) {

        return Manager3DS.bGroups.get(i);
      }

    }
    BranchGroup bg = Manager3DS.Loader3D(pathShape);

    Manager3DS.bGroups.add(bg);
    Manager3DS.pathModels.add(pathShape);

    return bg;

  }

  /**
   * Chargement de l'objet Wavefront (masque.obj) ainsi que les materiaux qui
   * lui sont associes
   * 
   * @param filename nom du fichier de l'objet a charger
   * @return BranchGroup branch group contenant l'objet Wavefront
   */
  private static BranchGroup Loader3D(String filename) {

    Loader3DS d = new Loader3DS();

    if (!Manager3DS.TEXTURED) {
      d.noTextures();
    }

    Scene scene = null;

    try {
      String path = filename;

      scene = d.load(path);

      BranchGroup bg = scene.getSceneGroup();

      return bg;

    } catch (Exception e) {
      System.out.println(filename);
      e.printStackTrace();
    }

    return null;

  }

}
