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
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import org.semanticweb.owlapi.model.OWLOntology;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.OntologyBrowser;

public class OntologyBrowserFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  OntologyBrowser tree;
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
        this.texteCourant.setText(node.getUserObject().toString());
      } else {
        this.texteCourant.setEditable(true);
        this.texteCourant.setText(node.getUserObject().toString());
        this.texteCourant.setEditable(false);
      }
      this.tree.clearSelection();
      this.setVisible(false);

    } else if (e.getActionCommand().equals("Annuler")) {
      this.tree.clearSelection();
      this.setVisible(false);
    }
  }

  public OntologyBrowserFrame(JTextField txt, OWLOntology ontology,
      String rootConcept) {
    super("Pick a concept from the ontology");
    this.texteCourant = txt;
    this.setSize(300, 450);
    String rootConc = "";
    if (rootConcept == null) {
      rootConc = "Thing";
    } else if (rootConcept.equals("")) {
      rootConc = "Thing";
    }
    this.tree = new OntologyBrowser(ontology, rootConc);

    this.buildFrame();
  }

  public OntologyBrowserFrame(JTextField txt, OWLOntology ontology,
      Set<String> rootsToRemove) {
    super("Pick a concept from the ontology");
    this.texteCourant = txt;
    this.setSize(300, 450);

    this.tree = new OntologyBrowser(ontology, rootsToRemove);

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

    this.getContentPane().add(panelTotal);
    this.setVisible(true);
  }
}
