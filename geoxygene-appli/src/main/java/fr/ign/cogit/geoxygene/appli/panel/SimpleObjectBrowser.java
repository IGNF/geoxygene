/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.panel;

import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.appli.I18N;

public class SimpleObjectBrowser extends JFrame {

  /****/
  private static final long serialVersionUID = -5360827653377606459L;

  /**
   * The list of selected objects under the mouse click
   */
  private List<IFeature> selectedObjs;

  /**
   * The tree that browse the selected objects attributes
   */
  private JTree tree;

  private String title;

  public SimpleObjectBrowser(Point mouseClick, List<IFeature> selectedObjs)
      throws IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    super();
    internationalisation();
    this.setTitle(title);
    this.setPreferredSize(new Dimension(200, 300));
    this.selectedObjs = selectedObjs;
    this.setLocation(mouseClick);
    this.setAlwaysOnTop(true);

    // build the Tree model
    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("selection");
    DefaultTreeModel model = new DefaultTreeModel(treeRoot);
    // add a tree root for each selected object
    for (IFeature obj : this.selectedObjs) {
      String rootName = obj.getClass().getSimpleName() + " - " + obj.getId();
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);
      // a leaf for area and length
      String areaName = "area = " + obj.getGeom().area();
      String lengthName = "length = " + obj.getGeom().length();
      DefaultMutableTreeNode areaLeaf = new DefaultMutableTreeNode(areaName);
      DefaultMutableTreeNode lengthLeaf = new DefaultMutableTreeNode(lengthName);
      root.add(areaLeaf);
      root.add(lengthLeaf);
      // leaves for vertices number and centroid coordinates
      String verticesName = "vertices number = " + obj.getGeom().coord().size();
      String centroidName = "centroid = null";
      if (obj.getGeom().centroid() != null)
        centroidName = "centroid = (" + obj.getGeom().centroid().getX() + ", "
            + obj.getGeom().centroid().getY() + ")";
      DefaultMutableTreeNode verticesLeaf = new DefaultMutableTreeNode(
          verticesName);
      DefaultMutableTreeNode centroidLeaf = new DefaultMutableTreeNode(
          centroidName);
      root.add(verticesLeaf);
      root.add(centroidLeaf);
      // a leaf for each attribute
      for (Method meth : obj.getClass().getDeclaredMethods()) {
        // keep only simple getters
        if (!meth.getName().startsWith("get")
            && !meth.getName().startsWith("is"))
          continue;
        // do not display geometries
        if (meth.getName().startsWith("getGeom"))
          continue;
        // do not display geox objects
        if (meth.getName().startsWith("getGeoxObj"))
          continue;
        // do not display methods with parameters
        if (meth.getParameterTypes().length != 0)
          continue;
        Object value = meth.invoke(obj);
        // TODO manage complex types like collections
        String attribute = meth.getName().substring(3);
        if (meth.getName().startsWith("is"))
          attribute = meth.getName().substring(2);
        String attributeValue = attribute + " = null";
        if (value != null)
          attributeValue = attribute + " = " + value.toString();
        DefaultMutableTreeNode attrLeaf = new DefaultMutableTreeNode(
            attributeValue);
        root.add(attrLeaf);
      }

      // add featureType attributes if exist
      GF_FeatureType ft = obj.getFeatureType();
      if (ft != null) {
        for (GF_AttributeType attr : ft.getFeatureAttributes()) {
          Object value = obj.getAttribute(attr.getMemberName());
          String attribute = attr.getMemberName() + " = null";
          if (value != null)
            attribute = attr.getMemberName() + " = " + value.toString();
          DefaultMutableTreeNode attrLeaf = new DefaultMutableTreeNode(
              attribute);
          root.add(attrLeaf);
        }
      }

      // add root to the model root
      treeRoot.add(root);
    }
    this.tree = new JTree(model);
    this.tree.setPreferredSize(new Dimension(400, 350 * this.selectedObjs
        .size()));
    this.tree
        .setMinimumSize(new Dimension(400, 350 * this.selectedObjs.size()));
    this.tree
        .setMaximumSize(new Dimension(400, 350 * this.selectedObjs.size()));
    this.tree.expandRow(1);

    this.getContentPane().add(new JScrollPane(tree));
    this.pack();
  }

  private void internationalisation() {
    title = I18N.getString("SimpleObjectBrowser.title");
  }
}
