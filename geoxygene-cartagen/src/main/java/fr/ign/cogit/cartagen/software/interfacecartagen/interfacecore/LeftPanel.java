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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import fr.ign.cogit.cartagen.software.CartagenApplication;

/**
 * @author julien Gaffuri 6 mars 2007
 * 
 */
public final class LeftPanel extends JPanel {
  private static final long serialVersionUID = -3719644987901188303L;

  // la fenetre a laquelle le panel est eventuellement lie
  private GeoxygeneFrame frame = null;

  GeoxygeneFrame getFrame() {
    return this.frame;
  }

  public JCheckBox cSymbol = new JCheckBox("Display symbols", true);
  public JCheckBox cSymbolInitial = new JCheckBox("Display initial symbols",
      false);

  private JPanel pannel = null;

  public JPanel getPannel() {
    if (this.pannel == null) {
      this.pannel = new JPanel();
    }
    return this.pannel;
  }

  private GridBagConstraints c_ = null;

  public GridBagConstraints getGBC() {
    if (this.c_ == null) {
      this.c_ = new GridBagConstraints();
      this.c_.gridwidth = GridBagConstraints.REMAINDER;
      this.c_.gridheight = 1;
      this.c_.gridy = 0;
      this.c_.anchor = GridBagConstraints.NORTHWEST;
      this.c_.insets = new Insets(20, 5, 1, 5);
    }
    return this.c_;
  }

  public LeftPanel(GeoxygeneFrame frame) {
    this.frame = frame;

    this.setFont(new Font("Arial", Font.PLAIN, 9));
    this.setPreferredSize(new Dimension(160, 800));
    this.setMinimumSize(new Dimension(160, 200));
    this.setMaximumSize(new Dimension(160, 1600));

    this.getPannel().setLayout(new GridBagLayout());
    this.getPannel().setFont(this.getFont());

    // symbolisation
    this.cSymbol.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (LeftPanel.this.cSymbol.isSelected()) {
          CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay = true;
        } else {
          CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay = false;
        }
      }
    });
    this.getGBC();
    this.cSymbol.setFont(this.getFont());
    this.getPannel().add(this.cSymbol, this.c_);

    // symbolisation initiale
    this.cSymbolInitial.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (LeftPanel.this.cSymbolInitial.isSelected()) {
          CartagenApplication.getInstance().getInitialLayerGroup().symbolisationDisplay = true;
        } else {
          CartagenApplication.getInstance().getInitialLayerGroup().symbolisationDisplay = false;
        }
      }
    });
    this.getGBC();
    this.cSymbolInitial.setFont(this.getFont());
    this.c_.insets = new Insets(1, 5, 1, 5);
    this.c_.gridy++;
    this.getPannel().add(this.cSymbolInitial, this.c_);

    JScrollPane scroll = new JScrollPane(this.getPannel(),
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.add(scroll);
  }

}
