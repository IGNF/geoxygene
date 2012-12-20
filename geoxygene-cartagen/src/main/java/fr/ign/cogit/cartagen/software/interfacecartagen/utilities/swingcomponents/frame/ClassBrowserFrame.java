/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.frame;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.GeneObjClassTree;

public class ClassBrowserFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  GeneObjClassTree tree;
  JTextField texteCourant;

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getActionCommand().equals("OK")) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree
          .getLastSelectedPathComponent();
      if (this.texteCourant.isEditable()) {
        this.texteCourant.setText(((Class<?>) node.getUserObject()).getName());
      } else {
        this.texteCourant.setEditable(true);
        this.texteCourant.setText(((Class<?>) node.getUserObject()).getName());
        this.texteCourant.setEditable(false);
      }
      this.tree.clearSelection();
      this.setVisible(false);

    } else if (e.getActionCommand().equals("Annuler")) {
      this.tree.clearSelection();
      this.setVisible(false);
    }
  }

  public ClassBrowserFrame(JTextField txt, boolean api) {
    super("Pick a concept from the ontology");
    this.texteCourant = txt;
    this.setSize(300, 450);

    this.tree = new GeneObjClassTree(api);

    this.buildFrame();
  }

  private void buildFrame() {
    JPanel panelBtn = new JPanel();
    panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
    JButton bouton0 = new JButton("OK");
    bouton0.addActionListener(this);
    bouton0.setActionCommand("OK");
    JButton bouton1 = new JButton("Annuler");
    bouton1.addActionListener(this);
    bouton1.setActionCommand("Annuler");
    panelBtn.add(bouton0);
    panelBtn.add(bouton1);

    JPanel panelTree = new JPanel();
    panelTree.add(this.tree);

    JPanel panelTotal = new JPanel();
    panelTotal.setLayout(new BoxLayout(panelTotal, BoxLayout.Y_AXIS));
    panelTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
    panelTotal.add(panelTree);
    panelTotal.add(panelBtn);

    this.getContentPane().add(new JScrollPane(panelTotal));
    this.setVisible(true);
  }
}
