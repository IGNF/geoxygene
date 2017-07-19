/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Extra menu that contains utility functions of CartAGen.
 * @author GTouya
 * 
 */
public class ConfigMenuComponent extends JMenu {

  static Logger logger = Logger.getLogger(ConfigMenuComponent.class.getName());

  /**
   */
  private JMenuItem mRechargerConfigurationLogger;

  private JCheckBoxMenuItem mTransparentSelection;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ConfigMenuComponent(String title) {
    super(title);
    this.mRechargerConfigurationLogger = new JMenuItem(
        new ReloadLoggerAction());
    this.mRechargerConfigurationLogger.setFont(this.getFont());
    this.add(mRechargerConfigurationLogger);
    this.addSeparator();
    this.mTransparentSelection.setSelected(true);
    this.add(mTransparentSelection);
  }

  /**
   * @author GTouya
   * 
   */
  class ReloadLoggerAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      logger.info("Rechargement configuration log");
      PropertyConfigurator.configure("log4j.properties");
    }

    public ReloadLoggerAction() {
      putValue(Action.NAME, "Reload logger configuration");
    }
  }

}
