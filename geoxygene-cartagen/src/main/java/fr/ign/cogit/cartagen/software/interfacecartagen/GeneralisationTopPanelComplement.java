/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.TopPanel;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationTopPanelComplement {
  static Logger logger = Logger
      .getLogger(GeneralisationTopPanelComplement.class.getName());

  // le panneau complete
  TopPanel ph = null;

  /**
	 */
  private static GeneralisationTopPanelComplement content = null;

  public static GeneralisationTopPanelComplement getInstance() {
    if (GeneralisationTopPanelComplement.content == null) {
      GeneralisationTopPanelComplement.content = new GeneralisationTopPanelComplement();
    }
    return GeneralisationTopPanelComplement.content;
  }

  private GeneralisationTopPanelComplement() {
    this.ph = CartagenApplication.getInstance().getFrame().getTopPanel();
  }

  /**
	 * 
	 */
  public void add() {

    // bouton generalisation complete
    JButton bGeneralisation = new JButton();
    bGeneralisation
        .setIcon(new ImageIcon(GeneralisationTopPanelComplement.class
            .getResource("/images/etoile.jpg").getPath().replaceAll("%20", " ")));
    bGeneralisation.setToolTipText("Launch complete generalisation");
    bGeneralisation.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
    this.ph.add(bGeneralisation);

  }
}
