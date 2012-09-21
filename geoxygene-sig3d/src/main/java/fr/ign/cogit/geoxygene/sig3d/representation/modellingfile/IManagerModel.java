package fr.ign.cogit.geoxygene.sig3d.representation.modellingfile;

import javax.media.j3d.BranchGroup;

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
 * Interface permettant de gèrer les fichiers de modélisation qui serviront de
 * symboles aux objets ponctuels. Le but du manager est d'éviter d'avoir à
 * charger un objet.
 * 
 * Interface for the management of the loading of 3D modelling object in order
 * to avoid loading a symbol for each points
 * 
 */
public interface IManagerModel {

  /**
   * Il s'agit d'une fonction permettant de renvoyer le symbole sous forme d'un
   * BranchGroup
   */
  public BranchGroup loadingFile(String pathShape);
}
