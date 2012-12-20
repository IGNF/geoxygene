/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component;

import java.awt.Component;
import java.util.Collection;
import java.util.Set;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * An OntologyBrowser is a JTree extension that displays the classes of an OWL
 * ontology as a tree.
 * @author GTouya
 * 
 */
public class OntologyBrowser extends JTree {

  /**
   * 
   */
  private static final long serialVersionUID = -8730796467777814732L;

  private OWLOntology ontology;
  private DefaultTreeModel dtm;
  private String rootConcept = "Thing";

  public OntologyBrowser(OWLOntology ontology) {
    super();
    this.ontology = ontology;
    this.buildTreeModel();
    this.setModel(this.dtm);
    // set the tree selection model
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(dtsm);
    // set the tree renderer
    OntologyRenderer myRenderer = new OntologyRenderer();
    this.setCellRenderer(myRenderer);
    this.setRootVisible(false);
  }

  public OntologyBrowser(OWLOntology ontology, String rootConcept) {
    super();
    this.ontology = ontology;
    this.rootConcept = rootConcept;
    this.buildTreeModel();
    this.setModel(this.dtm);
    // set the tree selection model
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(dtsm);
    // set the tree renderer
    OntologyRenderer myRenderer = new OntologyRenderer();
    this.setCellRenderer(myRenderer);
    this.setRootVisible(false);
  }

  public OntologyBrowser(OWLOntology ontology, Set<String> rootsToRemove) {
    super();
    this.ontology = ontology;
    this.rootConcept = "Thing";
    this.buildTreeModel();
    for (String concept : rootsToRemove) {
      DefaultMutableTreeNode node = getNodeFromName(concept);
      this.dtm.removeNodeFromParent(node);
    }

    this.setModel(this.dtm);
    // set the tree selection model
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(dtsm);
    // set the tree renderer
    OntologyRenderer myRenderer = new OntologyRenderer();
    this.setCellRenderer(myRenderer);
    this.setRootVisible(false);
  }

  public OntologyBrowser(OWLOntology ontology, ImageIcon closedIcon,
      ImageIcon openedIcon) {
    super();
    this.ontology = ontology;
    this.buildTreeModel();
    this.setModel(this.dtm);
    // set the tree selection model
    DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel();
    dtsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.setSelectionModel(dtsm);
    this.setRootVisible(false);
    // set the tree renderer
    OntologyRenderer myRenderer = new OntologyRenderer();
    // Change the icon for closed nodes.
    myRenderer.setClosedIcon(closedIcon);
    // Change the icon for opened nodes.
    myRenderer.setOpenIcon(openedIcon);
    this.setCellRenderer(myRenderer);
  }

  private void buildTreeModel() {
    // first get all classes of the ontology
    Collection<OWLClass> classes = this.ontology.getClassesInSignature();
    // get the root of the tree model (the "Thing" OWL Class)
    DefaultMutableTreeNode root = null;
    for (OWLClass owlClass : classes) {
      if (owlClass.getIRI().getFragment().equals(rootConcept)) {
        root = new DefaultMutableTreeNode(owlClass);
        break;
      }
    }
    if (root == null) {
      return;
    }
    classes.remove(root.getUserObject());

    // now, add all remaining classes to the root by specialisation
    this.addSubClasses(root);
  }

  private void addSubClasses(DefaultMutableTreeNode node) {
    OWLClass owlClass = (OWLClass) node.getUserObject();
    for (OWLClassExpression c : owlClass.getSubClasses(this.ontology)) {
      if (!OWLClass.class.isInstance(c)) {
        continue;
      }
      OWLClass subClass = (OWLClass) c;
      DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subClass);
      node.add(subNode);
      if (subClass.getSubClasses(this.ontology).size() != 0) {
        this.addSubClasses(subNode);
      }
    }
  }

  private DefaultMutableTreeNode getNodeFromName(String nom) {
    DefaultMutableTreeNode racine = (DefaultMutableTreeNode) dtm.getRoot();
    if (racine.getUserObject().toString().equals(nom))
      return racine;
    Stack<DefaultMutableTreeNode> enfants = new Stack<DefaultMutableTreeNode>();
    for (int i = 0; i < racine.getChildCount(); i++)
      enfants.add((DefaultMutableTreeNode) racine.getChildAt(i));
    while (!enfants.empty()) {
      DefaultMutableTreeNode n = enfants.pop();
      if (n.getUserObject().toString().equals(nom))
        return n;
      for (int i = 0; i < n.getChildCount(); i++)
        enfants.add((DefaultMutableTreeNode) n.getChildAt(i));
    }
    return null;
  }

  class OntologyRenderer extends DefaultTreeCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
          hasFocus1);
      if (value instanceof OWLNamedObject) {
        this.setText(((OWLNamedObject) value).getIRI().getFragment());
      }

      return this;
    }
  }
}
