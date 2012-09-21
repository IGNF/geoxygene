package fr.ign.cogit.geoxygene.sig3d.representation.modellingfile.manager;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BranchGroup;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

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
 * Cette classe est un singleton et ne peut donc être implémentée directement
 * 
 * Cette classe permet de gèrer les objets de types .Obj. On ne conserve qu'un
 * objet .Obj par chemin Cela permet de ne pas avoir à recharger un symbole à
 * chaque fois que l'on souhaite représenter une nouvelle fois un symbole déjà
 * utilisé.
 * 
 * This class manages symbology of .obj files. The symbol is loaded once for
 * each file path
 * 
 */
public class ManagerObj implements IManagerModel {

  // Tableaux de BranchGroup (symboles) et de chemins.
  private static List<BranchGroup> bGroups = new ArrayList<BranchGroup>();
  private static List<String> pathModels = new ArrayList<String>();

  private static ManagerObj manager = null;

  /**
   * Le manager est un singleton. Pour pouvoir influer sur le Manager, il faut
   * passer par cette méthode statique
   * 
   * @return renvoie l'instance du managerObj
   */
  public static ManagerObj getInstance() {
    if (ManagerObj.manager == null) {

      ManagerObj.manager = new ManagerObj();
    }

    return ManagerObj.manager;

  }

  /**
   * Cette classe permet de récupèrer le BranchGroup (symbole) venant d'un
   * fichier de modélisation
   */
  @Override
  public BranchGroup loadingFile(String pathShape) {
    int nbTextures = ManagerObj.bGroups.size();

    for (int i = 0; i < nbTextures; i++) {

      if (ManagerObj.pathModels.get(i).equals(pathShape)) {

        return ManagerObj.bGroups.get(i);
      }

    }
    BranchGroup bg = ManagerObj.loadWavefrontObject(pathShape);

    ManagerObj.bGroups.add(bg);
    ManagerObj.pathModels.add(pathShape);

    return bg;

  }

  private ManagerObj() {
    super();
  }

  /**
   * Chargement de l'objet Wavefront (masque.obj) ainsi que les materiaux qui
   * lui sont associes
   * 
   * @param filename nom du fichier de l'objet a charger
   * @return BranchGroup branch group contenant l'objet Wavefront
   */
  private static BranchGroup loadWavefrontObject(String filename) {
    ObjectFile waveFrontObject = new ObjectFile();

    Scene scene = null;

    try {

      scene = waveFrontObject.load(filename);

      BranchGroup bg = scene.getSceneGroup();

      return bg;

    } catch (Exception e) {

      e.printStackTrace();
    }

    return null;

  }

}
