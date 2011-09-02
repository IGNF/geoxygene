package fr.ign.cogit.geoxygene.gui;

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

/**
 * @author julien Gaffuri 6 mars 2007
 * 
 */
public final class PanelGauche extends JPanel {
  private static final long serialVersionUID = -3719644987901188303L;

  // la fenetre a laquelle le panel est eventuellement lie
  private InterfaceGeoxygene frame = null;

  InterfaceGeoxygene getFrame() {
    return this.frame;
  }

  //
  public JCheckBox cSymbole = new JCheckBox("Afficher symboles", true);

  private JPanel panneau = null;

  public JPanel getPanneau() {
    if (this.panneau == null) {
      this.panneau = new JPanel();
    }
    return this.panneau;
  }

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

  public PanelGauche(InterfaceGeoxygene frame) {
    this.frame = frame;

    this.setFont(new Font("Arial", Font.PLAIN, 9));

    this.getPanneau().setLayout(new GridBagLayout());
    this.getPanneau().setFont(this.getFont());

    // symbolisation
    this.cSymbole.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (PanelGauche.this.cSymbole.isSelected()) {
          PanelGauche.this.getFrame().getPanelVisu().affichageSymbolisation = true;
        } else {
          PanelGauche.this.getFrame().getPanelVisu().affichageSymbolisation = false;
        }
      }
    });
    this.cSymbole.setFont(this.getFont());
    this.getPanneau().add(this.cSymbole);

    JScrollPane scroll = new JScrollPane(this.getPanneau(),
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.add(scroll);
  }

}
