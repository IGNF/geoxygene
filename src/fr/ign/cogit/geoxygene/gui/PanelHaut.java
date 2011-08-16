package fr.ign.cogit.geoxygene.gui;

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
public final class PanelHaut extends JPanel {
  private static final long serialVersionUID = -5887331253092445825L;
  static final Logger logger = Logger.getLogger(PanelHaut.class.getName());

  // la fenetre a laquelle le panel est eventuellement lie
  private InterfaceGeoxygene frameMirage = null;

  InterfaceGeoxygene getFrameMirage() {
    return this.frameMirage;
  }

  public JButton bRafraichir = new JButton("Rafraichir");
  public JCheckBox cRafraichir = new JCheckBox("(auto)", false);

  public PanelHaut(InterfaceGeoxygene frameMirage) {
    this.frameMirage = frameMirage;

    // setBackground(new Color(190,190,255));
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setFont(new Font("Arial", Font.PLAIN, 9));

    this.bRafraichir.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) { /*
                                                       * getFrameMirage().getPanelVisu
                                                       * ().activer();
                                                       */
        PanelHaut.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.bRafraichir.setFont(this.getFont());
    this.add(this.bRafraichir);

    this.cRafraichir.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (PanelHaut.this.cRafraichir.isSelected()) {
          // activer le rafraichissement
          // getFrameMirage().getPanelVisu().desactiverRafraichissementAuto();
          PanelHaut.this.getFrameMirage().getPanelVisu()
              .activerRafraichissementAuto();
        } else {
          // desactiver le rafraichissement
          PanelHaut.this.getFrameMirage().getPanelVisu()
              .desactiverRafraichissementAuto();
        }
      }
    });
    this.cRafraichir.setFont(this.getFont());
    this.add(this.cRafraichir);

  }

}
