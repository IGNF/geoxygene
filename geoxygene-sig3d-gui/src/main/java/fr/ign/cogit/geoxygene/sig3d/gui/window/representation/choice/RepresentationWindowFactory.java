package fr.ign.cogit.geoxygene.sig3d.gui.window.representation.choice;

import javax.swing.JDialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.OneColoredLayerWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

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
 * Cette classe sert à afficher un menu de représentation adapté à l'objet This
 * class enable to give a menu adapted to a colletion to generate their
 * representation
 * 
 */
public class RepresentationWindowFactory {

  private final static Logger logger = LogManager
      .getLogger(RepresentationWindowFactory.class.getName());

  /**
   * Cette classe permet de générer la fenêtre adaptée à une collection pour une
   * carte3D
   * 
   * @param iMap3D la carte dans laquelle sera affichée la collection
   * @param featColl la collection à laquelle on veut appliquer un style
   * @return la fenêtre adaptée à la dimension du premier objet de la liste
   */
  public static RepresentationWindow generateDialog(InterfaceMap3D iMap3D,
      IFeatureCollection<IFeature> featColl) {

    if (featColl == null || featColl.size() == 0) {
      RepresentationWindowFactory.logger.error(Messages
          .getString("FenetreChoix.CoucheNulle"));
      return null;
    }

    int dimension = featColl.get(0).getGeom().dimension();

    switch (dimension) {

      case 0:

        return new Representation0DWindow(iMap3D, featColl);

        // Pas de style encore définis pour le 1D
      case 1:
        // return new Representation1DWindow(iMap3D, featColl);
        return new OneColoredLayerWindow(iMap3D, featColl);

      case 2:

        return new Representation2DWindow(iMap3D, featColl);

      case 3:

        return new Representation3DWindow(iMap3D, featColl);

    }
    
    return new OneColoredLayerWindow(iMap3D, featColl);

  }

  /**
   * Génère la fenêtre correspondant le mieux pour la modification d'une couche
   * vectorielle ayant déjà une représentation
   * 
   * @param vl la couche dont on souhaite modifier la représentation
   * @return la fenêtre adaptée à la dimension du premier objet de la liste
   */
  public static JDialog generateDialog(VectorLayer vl) {

    if (vl == null || vl.size() == 0) {
      RepresentationWindowFactory.logger.error(Messages
          .getString("FenetreChoix.CoucheNulle"));
      return null;
    }

    int dimension = vl.get(0).getGeom().dimension();

    switch (dimension) {

      case 0:

        return new Representation0DWindow(vl);

      case 1:

        // return new Representation1DWindow(iMap3D, featColl);
        return new OneColoredLayerWindow(vl);

      case 2:

        return new Representation2DWindow(vl);

      case 3:

        return new Representation3DWindow(vl);

    }

    return new OneColoredLayerWindow(vl);
  }

}
