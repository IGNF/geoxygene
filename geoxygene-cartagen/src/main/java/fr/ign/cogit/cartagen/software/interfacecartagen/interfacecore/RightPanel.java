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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author julien Gaffuri
 * 
 */
public final class RightPanel extends JPanel {
  private static final long serialVersionUID = -3719644987901188303L;

  // la fenetre a laquelle le panel est eventuellement lie
  /**
     */
  private GeoxygeneFrame frame = null;

  /**
   * @return
   */
  GeoxygeneFrame getFrame() {
    return this.frame;
  }

  /**
     */
  public JPanel pSelection = new JPanel(new GridBagLayout());
  /**
     */
  public JLabel lNbSelection = new JLabel("Nb=0");
  /**
     */
  public JCheckBox cDisplaySelection = new JCheckBox("Display", true);
  /**
     */
  public JButton bSelectionCenterAndZoom = new JButton("Center and zoom");
  /**
   */
  public JButton bSelectionSuppression = new JButton("Eiminate objects");
  /**
     */
  public JButton bEmptySelection = new JButton("Clear selection");

  /**
     */
  private GridBagConstraints c_ = null;

  public GridBagConstraints getGBC() {
    if (this.c_ == null) {
      this.c_ = new GridBagConstraints();
      this.c_.gridwidth = GridBagConstraints.REMAINDER;
      this.c_.anchor = GridBagConstraints.NORTHWEST;
      this.c_.insets = new Insets(1, 5, 1, 5);
    }
    return this.c_;
  }

  public RightPanel(GeoxygeneFrame frame) {
    this.frame = frame;

    this.setPreferredSize(new Dimension(200, 800));
    this.setMinimumSize(new Dimension(200, 200));
    this.setMaximumSize(new Dimension(200, 1600));

    this.setLayout(new GridBagLayout());
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.insets = new Insets(1, 5, 1, 5);

    // selection
    this.pSelection.setFont(this.getFont());
    this.pSelection.setBorder(BorderFactory.createTitledBorder("Selection"));

    this.lNbSelection.setFont(this.getFont());
    this.pSelection.add(this.lNbSelection, c);

    this.cDisplaySelection.setFont(this.getFont());
    this.pSelection.add(this.cDisplaySelection, c);

    this.bSelectionCenterAndZoom.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        RightPanel.this.getFrame().getVisuPanel().centerAndZoom(
            RightPanel.this.getFrame().getVisuPanel().selectedObjects);
        RightPanel.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.bSelectionCenterAndZoom.setFont(this.getFont());
    this.pSelection.add(this.bSelectionCenterAndZoom, c);

    this.bSelectionSuppression.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        for (IFeature sel : CartagenApplication.getInstance().getFrame()
            .getVisuPanel().selectedObjects) {
          if (!(sel instanceof IGeneObj)) {
            continue;
          }
          ((IGeneObj) sel).eliminate();
        }
      }
    });
    this.bSelectionSuppression.setFont(this.getFont());
    this.pSelection.add(this.bSelectionSuppression, c);

    this.bEmptySelection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        RightPanel.this.getFrame().getVisuPanel().selectedObjects.clear();
        RightPanel.this.lNbSelection.setText("Nb=0");
        RightPanel.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.bEmptySelection.setFont(this.getFont());
    this.pSelection.add(this.bEmptySelection, c);

    this.add(this.pSelection, this.getGBC());

  }

}
