package fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice;

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
 * Une interface permettant de manipuler de manière générique les objets des
 * classes RepresentationXDWindow
 */
public interface RepresentationWindow {
  /**
   * Indique si une fenêtre est validée ou annulée
   * 
   * @return indique si l'on a annulé la fenêtre
   */
  public boolean isCanceled();

}
