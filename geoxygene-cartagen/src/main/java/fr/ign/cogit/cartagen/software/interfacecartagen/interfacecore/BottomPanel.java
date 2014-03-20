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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author julien Gaffuri
 * 
 */
public final class BottomPanel extends JPanel {
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
  public JButton b_ = new JButton("-");
  /**
     */
  public JCheckBox cAffichageEchelle = new JCheckBox("Sacel", false);
  /**
     */
  public JCheckBox cVoirPositionCurseur = new JCheckBox("Display position",
      false);
  /**
     */
  public JLabel lX = new JLabel("X=");
  /**
     */
  public JLabel lY = new JLabel("Y=");
  /**
     */
  public JLabel lZ = new JLabel("Z=");
  /**
     */
  public JLabel lValPente = new JLabel("SlopeVal=");
  /**
     */
  public JLabel lOrPente = new JLabel("SlopeOr=");

  public BottomPanel(GeoxygeneFrame frameMirage) {
    this.frame = frameMirage;

    // setBackground(new Color(190,190,255));
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    this.b_.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        // System.out.println("");
        // System.out.println("=============================================");
        // System.out.println("");
      }
    });
    this.b_.setFont(this.getFont());
    this.b_.setSize(0, 0);
    this.add(this.b_);

    this.cAffichageEchelle.setFont(this.getFont());
    this.cAffichageEchelle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (BottomPanel.this.cAffichageEchelle.isSelected()) {
          BottomPanel.this.getFrame().getVisuPanel().scaleBarDisplay = true;
        } else {
          BottomPanel.this.getFrame().getVisuPanel().scaleBarDisplay = false;
        }
      }
    });
    this.add(this.cAffichageEchelle);

    this.cVoirPositionCurseur.setFont(this.getFont());
    this.cVoirPositionCurseur.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (BottomPanel.this.cVoirPositionCurseur.isSelected()) {
          BottomPanel.this.getFrame().getVisuPanel().CursorPositionDispaly = true;
        } else {
          BottomPanel.this.getFrame().getVisuPanel().CursorPositionDispaly = false;
        }
      }
    });
    this.add(this.cVoirPositionCurseur);

    this.lX.setFont(this.getFont());
    this.add(this.lX);
    this.lY.setFont(this.getFont());
    this.add(this.lY);
    this.lZ.setFont(this.getFont());
    this.add(this.lZ);
    this.lValPente.setFont(this.getFont());
    this.add(this.lValPente);
    this.lOrPente.setFont(this.getFont());
    this.add(this.lOrPente);

  }

}
