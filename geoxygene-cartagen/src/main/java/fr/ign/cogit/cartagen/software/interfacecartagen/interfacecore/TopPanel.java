/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Created on 3 août 2005
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * @author julien Gaffuri
 * 
 */
public final class TopPanel extends JPanel {
  private static final long serialVersionUID = -5887331253092445825L;
  static final Logger logger = Logger.getLogger(TopPanel.class.getName());

  // la fenetre a laquelle le panel est eventuellement lie
  private GeoxygeneFrame frame = null;

  GeoxygeneFrame getFrame() {
    return this.frame;
  }

  public JButton bRefresh = new JButton("Refresh");
  public JCheckBox cRefresh = new JCheckBox("(auto)", true);

  public TopPanel(GeoxygeneFrame frameMirage) {
    this.frame = frameMirage;

    // setBackground(new Color(190,190,255));
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    this.bRefresh.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        TopPanel.this.getFrame().getVisuPanel().activate();
      }
    });
    this.bRefresh.setFont(this.getFont());
    this.add(this.bRefresh);

    this.cRefresh.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (TopPanel.this.cRefresh.isSelected()) {
          TopPanel.this.getFrame().getVisuPanel().automaticRefresh = true;
          // activer le rafraichissement
          TopPanel.this.getFrame().getVisuPanel().desactivateAutomaticRefresh();
          TopPanel.this.getFrame().getVisuPanel().activateAutomaticRefresh();
        } else {
          TopPanel.this.getFrame().getVisuPanel().automaticRefresh = false;
          // desactiver le rafraichissement
          TopPanel.this.getFrame().getVisuPanel().desactivateAutomaticRefresh();
        }
      }
    });
    this.cRefresh.setFont(this.getFont());
    this.add(this.cRefresh);

  }

}
