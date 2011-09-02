/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 * 
 */
public class FrameEditeurSLD extends JFrame implements TreeSelectionListener,
    ChangeListener {
  private static final long serialVersionUID = -3425463042901557851L;
  static Logger logger = Logger.getLogger(FrameEditeurSLD.class.getName());

  public DataSet dataset = new DataSet();

  public static GM_Point point = new GM_Point(new DirectPosition(50, 50));

  public static GM_LineString lineString = new GM_LineString(
      new DirectPositionList(new ArrayList<IDirectPosition>(Arrays.asList(
          new DirectPosition(20, 50), new DirectPosition(70, 50)))));

  public static GM_Polygon polygon = new GM_Polygon(new GM_LineString(
      new DirectPositionList(new ArrayList<IDirectPosition>(Arrays.asList(
          new DirectPosition(20, 20), new DirectPosition(70, 20),
          new DirectPosition(70, 70), new DirectPosition(20, 70),
          new DirectPosition(20, 20))))));

  private InterfaceGeoxygene frameGeoxygene;
  private JTree tree;
  private StyledLayerDescriptor sld;

  /**
   * Renvoie la valeur de l'attribut sld.
   * @return la valeur de l'attribut sld
   */
  public StyledLayerDescriptor getSld() {
    return this.sld;
  }

  /**
   * Affecte la valeur de l'attribut sld.
   * @param sld l'attribut sld à affecter
   */
  public void setSld(StyledLayerDescriptor sld) {
    this.sld = sld;
    sld.addChangeListener(this);
  }

  /**
   * Constructeur
   * @param frame Frame GeOxygene
   */
  public FrameEditeurSLD(InterfaceGeoxygene frame) {
    this.frameGeoxygene = frame;
    this.setSld(this.frameGeoxygene.getPanelVisu().getSld());

    this.createDataSetFromSld(this.getSld());

    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setLayout(new BorderLayout());
    this.setResizable(true);
    this.setSize(new Dimension(500, 500));
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.setTitle("Editeur de SLD de GéOxygène");
    this.setIconImage(InterfaceGeoxygene.getIcone());

    DefaultMutableTreeNode top = new DefaultMutableTreeNode(
        "Styled Layer Descriptor");
    this.tree = new JTree(top);
    this.createNodes(top);
    this.tree.setCellRenderer(new SLDRenderer(this.sld));
    // tree.setEditable(true);
    this.tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.tree.addTreeSelectionListener(this);
    this.tree.setShowsRootHandles(false);
    this.tree.setExpandsSelectedPaths(true);
    this.tree.expandPath(this.tree.getLeadSelectionPath());
    this.tree.expandRow(0);
    // Enable tool tips.
    ToolTipManager.sharedInstance().registerComponent(this.tree);

    JScrollPane scroll = new JScrollPane(this.tree,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.add(scroll, BorderLayout.CENTER);
  }

  /**
   * Crée un dataset pour afficher le SLD.
   * @param sld SLD à utiliser
   */
  private void createDataSetFromSld(StyledLayerDescriptor sld) {
    for (Layer layer : sld.getLayers()) {
      if ((layer.getFeatureCollection() != null)
          && (layer.getFeatureCollection().size() > 0)) {
        Population<IFeature> population = new Population<IFeature>();
        population.setNom(layer.getName());
        DefaultFeature feature = new DefaultFeature();
        // FIXME ce n'est pas très joli, mais les featuresCollection peuvent ne
        // pas avoir de featuretype
        Class<? extends IGeometry> geometryType = (layer.getFeatureCollection()
            .getFeatureType() != null) ? layer.getFeatureCollection()
            .getFeatureType().getGeometryType() : layer.getFeatureCollection()
            .get(0).getGeom().getClass();
        if ((geometryType.equals(IMultiCurve.class))
            || (geometryType.equals(ILineString.class))) {
          feature.setGeom(FrameEditeurSLD.lineString);
        } else if ((geometryType.equals(IMultiSurface.class))
            || (geometryType.equals(IPolygon.class))) {
          feature.setGeom(FrameEditeurSLD.polygon);
        } else if ((geometryType.equals(IMultiPoint.class))
            || (geometryType.equals(IPoint.class))) {
          feature.setGeom(FrameEditeurSLD.point);
        } else {
          FrameEditeurSLD.logger.error("Aucune géométrie n'a été affectée !!!");
        }
        if (layer.getFeatureCollection().get(0).getFeatureType() != null) {
          feature.setFeatureType(layer.getFeatureCollection().get(0)
              .getFeatureType());
        }
        if (layer.getFeatureCollection().get(0) instanceof DefaultFeature) {
          // FIXME arriver à faire ça si ce n'est pas un FeafautFeature
          feature.setSchema(((DefaultFeature) layer.getFeatureCollection().get(
              0)).getSchema());
          /**
           * on crée un tableau d'attributs suffisamment grand pour recevoir la
           * clé la plus grande
           */
          Integer[] keys = feature.getSchema().getAttLookup().keySet()
              .toArray(new Integer[0]);
          Arrays.sort(keys);
          feature.setAttributes(new Object[keys[keys.length - 1] + 1]);
          /**
           * On parcours le schéma et on affecte à tous les attributs de type
           * texte une valeur "texte"
           */
          for (AttributeType attribute : ((FeatureType) feature
              .getFeatureType()).getSchema().getFeatureAttributes()) {
            if (attribute.getValueType().equalsIgnoreCase("String")) {
              if (FrameEditeurSLD.logger.isTraceEnabled()) {
                FrameEditeurSLD.logger.trace("affecte la valeur de l'attribut "
                    + attribute);
              }
              feature.setAttribute(attribute.getMemberName(), "Texte");
            }
          }
        }
        population.setElements(new ArrayList<IFeature>(Arrays.asList(feature)));
        population.initSpatialIndex(Tiling.class, false);
        this.dataset.addPopulation(population);
      }
    }
  }

  /**
   * Crée les noeuds de l'arbre à partir du sld
   * @param top racine de l'arbre à remplir
   */
  private void createNodes(DefaultMutableTreeNode top) {
    if ((this.frameGeoxygene.getPanelVisu() == null)
        || (this.frameGeoxygene.getPanelVisu().getSld() == null)) {
      return;
    }
    for (Layer layer : this.frameGeoxygene.getPanelVisu().getSld().getLayers()) {
      DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(
          layer.getName());
      top.add(layerNode);
      layerNode.setUserObject(layer);
    }
  }

  /**
   * Classe utilisée pour le rendu des cellules de l'arbre du SLD.
   * @author Julien Perret
   */
  class SLDRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 2130271540227696439L;

    private StyledLayerDescriptor sld;

    public SLDRenderer(StyledLayerDescriptor sld) {
      this.sld = sld;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
          hasFocus);
      if (leaf && this.isLayer(value)) {
        this.setToolTipText("Ceci est un layer.");
        Layer layer = (Layer) ((DefaultMutableTreeNode) value).getUserObject();
        this.setText(layer.getClass().getSimpleName() + " - " + layer.getName());
        if (FrameEditeurSLD.this.dataset.getPopulation(layer.getName()) != null) {
          this.setIcon(new LayerIcon(layer, this.sld));
        }
      } else {
        this.setToolTipText(null); // no tool tip
      }

      return this;
    }

    protected boolean isLayer(Object value) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      return (Layer.class.isAssignableFrom(node.getUserObject().getClass()));
    }
  }

  /**
   * Classe représentant l'icone d'une couche d'un SLD
   * @author Julien Perret
   */
  class LayerIcon implements Icon {
    Layer layer;
    DessinableGeoxygene d;

    /**
     * Constructeur
     * @param l couche représentée par l'icone
     * @param sld sld auquel appartient la couche
     */
    public LayerIcon(Layer l, StyledLayerDescriptor sld) {
      this.layer = l;
      this.d = new DessinableGeoxygene(sld);
      this.d.setCentreGeo(new DirectPosition(50.0, 50.0));
    }

    @Override
    public int getIconHeight() {
      return 50;
    }

    @Override
    public int getIconWidth() {
      return 100;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      try {
        this.d.majLimitesAffichage(this.getIconWidth(), this.getIconHeight());
        if (FrameEditeurSLD.this.dataset.getPopulation(this.layer.getName()) != null) {
          this.d.dessiner((Graphics2D) g, this.layer,
              FrameEditeurSLD.this.dataset.getPopulation(this.layer.getName()));
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree
        .getLastSelectedPathComponent();
    // Nothing is selected.
    if (node == null) {
      return;
    }
    Object nodeInfo = node.getUserObject();
    if (node.isLeaf()) {
      Layer layer = (Layer) nodeInfo;
      if (FrameEditeurSLD.logger.isDebugEnabled()) {
        FrameEditeurSLD.logger.debug("Layer " + layer.getName()
            + " séléctionné");
      }
      FrameEditeurLayer editeur = new FrameEditeurLayer(this, layer);
      editeur.setVisible(true);
    } else {
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    this.repaint();
  }
}
