/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.software;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;

/**
 * class de l'application cartagen (contient LA methode main)
 * @author JGaffuri
 * 
 */
public class CartagenApplicationSansAgent {
  private static Logger logger = Logger
      .getLogger(CartagenApplicationSansAgent.class.getName());

  public static void main(String[] args) {

    // Objects creation factory
    CartagenApplication.getInstance().setCreationFactory(
        new DefaultCreationFactory());
    CartagenApplication.getInstance().setStandardImplementation(
        GeneObjImplementation.getDefaultImplementation());

    // Application initialisation
    CartagenApplication.getInstance().initApplication();
    CartagenApplication.getInstance().initInterface();
    CartagenApplication.getInstance().initialiserInterfacePourGeneralisation();

    CartagenApplicationSansAgent.logger.info("End: Cartagen launched");
    CartagenApplication.getInstance().getFrameInit().lblEnd
        .setText("CartAGen has been successfully launched !");
    CartagenApplication.getInstance().getFrameInit().isGood(
        CartagenApplication.getInstance().getFrameInit().lblEndIcon);
    CartagenApplication.getInstance().getFrameInit().buttonOK.setEnabled(true);
    CartagenApplication.getInstance().getFrameInit().repaint();
    CartagenApplication.getInstance().getFrameInit().dispose();

    try {
      CartagenApplication.getInstance().getFrame().getVisuPanel().imageUpdate();
    } catch (InterruptedException e1) {
    }
    CartagenApplication.getInstance().getFrame().getMenu().repaint();
    CartagenApplication.getInstance().getFrame().getVisuPanel().repaint();
    CartagenApplication.getInstance().getFrame().getLeftPanel().revalidate();
    CartagenApplication.getInstance().getFrame().getRightPanel().revalidate();
    CartagenApplication.getInstance().getFrame().getTopPanel().revalidate();
    CartagenApplication.getInstance().getFrame().getBottomPanel().revalidate();
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
    CartagenApplication.getInstance().getFrame().getVisuPanel()
        .activateAutomaticRefresh();

    // The PostGIS is cleanly closed
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (CartAGenDoc.getInstance().getPostGisSession() != null) {
          CartAGenDoc.getInstance().getPostGisSession().close();
        }
      }
    });

  }

}
