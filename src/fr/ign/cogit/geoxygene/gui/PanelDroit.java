package fr.ign.cogit.geoxygene.gui;

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

/**
 * @author julien Gaffuri
 * 
 */
public final class PanelDroit extends JPanel {
  private static final long serialVersionUID = -3719644987901188303L;

  // la fenetre a laquelle le panel est eventuellement lie
  private InterfaceGeoxygene frame = null;

  InterfaceGeoxygene getFrame() {
    return this.frame;
  }

  public JPanel pSelection = new JPanel(new GridBagLayout());
  public JLabel lNbSelection = new JLabel("Nb=0");
  public JCheckBox cVoirSelection = new JCheckBox("Afficher selection", true);
  public JButton bViderSelection = new JButton("Annuler selection");

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

  public PanelDroit(InterfaceGeoxygene frame) {
    this.frame = frame;

    this.setLayout(new GridBagLayout());
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.insets = new Insets(1, 5, 1, 5);

    // selection
    this.pSelection.setFont(this.getFont());
    this.pSelection.setBorder(BorderFactory.createTitledBorder("Sélection"));

    this.lNbSelection.setFont(this.getFont());
    this.pSelection.add(this.lNbSelection, c);

    this.cVoirSelection.setFont(this.getFont());
    this.pSelection.add(this.cVoirSelection, c);

    this.bViderSelection.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        PanelDroit.this.getFrame().getPanelVisu().objetsSelectionnes.clear();
        PanelDroit.this.lNbSelection.setText("Nb=0");
        PanelDroit.this.getFrame().getPanelVisu().repaint();
      }
    });
    this.bViderSelection.setFont(this.getFont());
    this.pSelection.add(this.bViderSelection, c);

    this.add(this.pSelection, this.getGBC());

  }

}
