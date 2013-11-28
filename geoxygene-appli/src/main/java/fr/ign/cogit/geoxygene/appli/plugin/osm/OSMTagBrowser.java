package fr.ign.cogit.geoxygene.appli.plugin.osm;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

/**
 * A Browser that displays the tags of {@link OsmGeneObj} features rather than
 * the attributes.
 * @author GTouya
 * 
 */
public class OSMTagBrowser extends JFrame {

  /****/
  private static final long serialVersionUID = 1L;

  /**
   * The list of selected objects under the mouse click
   */
  private List<OsmGeneObj> selectedObjs;

  /**
   * The tree that browse the selected objects attributes
   */
  private JTree tree;

  private String title, contributeur, changeSet, captureTool;

  public OSMTagBrowser(Point mouseClick, List<OsmGeneObj> selectedObjs)
      throws IllegalArgumentException {
    super();
    this.internationalisation();
    this.setTitle(this.title);
    this.setPreferredSize(new Dimension(200, 300));
    this.selectedObjs = selectedObjs;
    this.setLocation(mouseClick);
    this.setAlwaysOnTop(true);

    // build the Tree model
    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("selection");
    DefaultTreeModel model = new DefaultTreeModel(treeRoot);
    // add a tree root for each selected object
    for (OsmGeneObj obj : this.selectedObjs) {
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
      String centroidName = "centroid = (" + obj.getGeom().centroid().getX()
          + ", " + obj.getGeom().centroid().getY() + ")";
      DefaultMutableTreeNode verticesLeaf = new DefaultMutableTreeNode(
          verticesName);
      DefaultMutableTreeNode centroidLeaf = new DefaultMutableTreeNode(
          centroidName);
      root.add(verticesLeaf);
      root.add(centroidLeaf);
      // leaves for the generic tags (not stored in the tag collection)
      DefaultMutableTreeNode contribLeaf = new DefaultMutableTreeNode(
          this.contributeur + " = " + obj.getContributor());
      DefaultMutableTreeNode uidLeaf = new DefaultMutableTreeNode("uid = "
          + obj.getUid());
      DefaultMutableTreeNode captureToolLeaf = new DefaultMutableTreeNode(
          this.captureTool + " = " + obj.getCaptureTool().name());
      DefaultMutableTreeNode sourceLeaf = new DefaultMutableTreeNode(
          "source = " + obj.getSource());
      DefaultMutableTreeNode idLeaf = new DefaultMutableTreeNode("OSM id = "
          + obj.getOsmId());
      DefaultMutableTreeNode changeSetLeaf = new DefaultMutableTreeNode(
          this.changeSet + " = " + obj.getChangeSet());
      DefaultMutableTreeNode versionLeaf = new DefaultMutableTreeNode(
          "version = " + obj.getVersion());
      DefaultMutableTreeNode dateLeaf = new DefaultMutableTreeNode("date = "
          + obj.getDate());
      root.add(contribLeaf);
      root.add(uidLeaf);
      root.add(captureToolLeaf);
      root.add(sourceLeaf);
      root.add(idLeaf);
      root.add(changeSetLeaf);
      root.add(versionLeaf);
      root.add(dateLeaf);
      // a leaf for each attribute
      for (String tag : obj.getTags().keySet()) {
        String value = obj.getTags().get(tag);
        String attribute = tag + " = " + value;
        DefaultMutableTreeNode attrLeaf = new DefaultMutableTreeNode(attribute);
        root.add(attrLeaf);
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

    this.getContentPane().add(new JScrollPane(this.tree));
    this.pack();
  }

  private void internationalisation() {
    this.title = I18N.getString("OSMTagBrowser.title");
    this.contributeur = I18N.getString("OSMTagBrowser.contributeur");
    this.captureTool = I18N.getString("OSMTagBrowser.captureTool");
    this.changeSet = I18N.getString("OSMTagBrowser.changeSet");
  }
}
