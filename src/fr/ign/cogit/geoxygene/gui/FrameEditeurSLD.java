/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 *
 */
public class FrameEditeurSLD extends JFrame implements TreeSelectionListener, ChangeListener {
	private static final long serialVersionUID = -3425463042901557851L;
	static Logger logger=Logger.getLogger(FrameEditeurSLD.class.getName());

	public DataSet dataset = new DataSet();
	
	public static GM_Point point = new GM_Point(new DirectPosition(50,50));

	public static GM_LineString lineString = new GM_LineString(new DirectPositionList(
			new ArrayList<DirectPosition>(Arrays.asList(new DirectPosition(20,50),new DirectPosition(70,50)))));

	public static GM_Polygon polygon = new GM_Polygon(new GM_LineString(new DirectPositionList(
			new ArrayList<DirectPosition>(Arrays.asList(
					new DirectPosition(20,20),new DirectPosition(70,20),
					new DirectPosition(70,70),new DirectPosition(20,70),new DirectPosition(20,20))))));

	private InterfaceGeoxygene frameGeoxygene;
	private JTree tree;
	private StyledLayerDescriptor sld;
	/**
	 * Renvoie la valeur de l'attribut sld.
	 * @return la valeur de l'attribut sld
	 */
	public StyledLayerDescriptor getSld() {return this.sld;}
	/**
	 * Affecte la valeur de l'attribut sld.
	 * @param sld l'attribut sld à affecter
	 */
	public void setSld(StyledLayerDescriptor sld) {this.sld = sld;sld.addChangeListener(this);}
	/**
	 * Constructeur
	 * @param frame Frame GeOxygene
	 */
	public FrameEditeurSLD(InterfaceGeoxygene frame) {
		this.frameGeoxygene=frame;
		setSld(frameGeoxygene.getPanelVisu().getSld());
		
		createDataSetFromSld(getSld());
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setLayout(new BorderLayout());
		setResizable(true);
		setSize(new Dimension(500,500));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setTitle("Editeur de SLD de GeOxygene");
		setIconImage(InterfaceGeoxygene.getIcone());

	    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Styled Layer Descriptor");
	    tree = new JTree(top);
	    createNodes(top);
	    tree.setCellRenderer(new SLDRenderer(sld));
	    //tree.setEditable(true);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.addTreeSelectionListener(this);
	    tree.setShowsRootHandles(false);
	    tree.setExpandsSelectedPaths(true);
	    tree.expandPath(tree.getLeadSelectionPath());
	    tree.expandRow(0);
		//Enable tool tips.
	    ToolTipManager.sharedInstance().registerComponent(tree);

		JScrollPane scroll = new JScrollPane(tree,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll,BorderLayout.CENTER);
	}

	/**
	 * crée un dataset pour afficher le SLD.
	 * @param newSld SLD à utiliser
	 */
	private void createDataSetFromSld(StyledLayerDescriptor newSld) {
		for (Layer layer:newSld.getLayers()) {
			if ((layer.getFeatureCollection()!=null)&&(layer.getFeatureCollection().size()>0)) {
				Population<FT_Feature> population = new Population<FT_Feature>();
				population.setNom(layer.getName());
				DefaultFeature feature = new DefaultFeature();
				// FIXME ce n'est pas très joli, mais les featuresCollection peuvent ne pas avoir de featuretype
				Class<? extends GM_Object> geometryType = (layer.getFeatureCollection().getFeatureType()!=null)?
						layer.getFeatureCollection().getFeatureType().getGeometryType():
							layer.getFeatureCollection().get(0).getGeom().getClass();
				if ((geometryType.equals(GM_MultiCurve.class))||(geometryType.equals(GM_LineString.class))) {
					feature.setGeom(lineString);
				} else if ((geometryType.equals(GM_MultiSurface.class))||(geometryType.equals(GM_Polygon.class))) {
					feature.setGeom(polygon);
				} else if ((geometryType.equals(GM_MultiPoint.class))||(geometryType.equals(GM_Point.class))) {
					feature.setGeom(point);
				} else {logger.error("Aucune géométrie n'a été affectée !!!");}
				if (layer.getFeatureCollection().get(0).getFeatureType()!=null) {
					feature.setFeatureType(layer.getFeatureCollection().get(0).getFeatureType());
				}
				if (layer.getFeatureCollection().get(0) instanceof DefaultFeature) {
					// FIXME arriver à faire ça si ce n'est pas un FeafautFeature
					feature.setSchema(((DefaultFeature) layer.getFeatureCollection().get(0)).getSchema());
					/** on crée un tableau d'attributs suffisamment grand pour recevoir la clé la plus grande */
					Integer[] keys = feature.getSchema().getAttLookup().keySet().toArray(new Integer[0]);
					Arrays.sort(keys);
					feature.setAttributes(new Object[keys[keys.length-1]+1]);
					/** On parcours le schéma et on affecte à tous les attributs de type texte une valeur "texte" */
					for (AttributeType attribute:feature.getFeatureType().getSchema().getFeatureAttributes()) {
						if (attribute.getValueType().equalsIgnoreCase("String")) {
							if (logger.isTraceEnabled()) logger.trace("affecte la valeur de l'attribut "+attribute);
							feature.setAttribute(attribute.getMemberName(), "Texte");
						}
					}
				}
				population.setElements(new ArrayList<FT_Feature>(Arrays.asList(feature)));
				population.initSpatialIndex(Tiling.class,false);
				dataset.addPopulation(population);
			}
		}
	}

	/**
	 * crée les noeuds de l'arbre à partir du sld
	 * @param top racine de l'arbre à remplir 
	 */
	private void createNodes(DefaultMutableTreeNode top) {
		if ((frameGeoxygene.getPanelVisu()==null) || (frameGeoxygene.getPanelVisu().getSld()==null)) return; 
		for (Layer layer : frameGeoxygene.getPanelVisu().getSld().getLayers()) {
			DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer.getName());
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

		private StyledLayerDescriptor sldRenderer;
	    public SLDRenderer(StyledLayerDescriptor sld) {this.sldRenderer=sld;}

	    @Override
		public Component getTreeCellRendererComponent(
	                        JTree treeRenderer,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocusRenderer) {

	        super.getTreeCellRendererComponent(
	                        treeRenderer, value, sel,
	                        expanded, leaf, row,
	                        hasFocusRenderer);
	        if (leaf && isLayer(value)) {
	            setToolTipText("Ceci est un layer.");
	            Layer layer = (Layer) ((DefaultMutableTreeNode)value).getUserObject();
	            setText(layer.getClass().getSimpleName()+" - "+layer.getName());
				if (dataset.getPopulation(layer.getName())!=null)
					setIcon(new LayerIcon(layer,sldRenderer));
	        } else {
	            setToolTipText(null); //no tool tip
	        } 

	        return this;
	    }

	    protected boolean isLayer(Object value) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
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
			layer=l;
			d = new DessinableGeoxygene(sld);
			d.setCentreGeo(new DirectPosition(50.0,50.0));
		}		
		@Override
		public int getIconHeight() {return 50;}
		@Override
		public int getIconWidth() {return 100;}
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			try {
				d.majLimitesAffichage(this.getIconWidth(),this.getIconHeight());
				if (dataset.getPopulation(layer.getName())!=null)
					d.dessiner((Graphics2D)g, layer, dataset.getPopulation(layer.getName()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		tree.getLastSelectedPathComponent();
		//Nothing is selected.	
		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			Layer layer = (Layer)nodeInfo;
			if (logger.isDebugEnabled()) logger.debug("Layer "+layer.getName()+ " sélectionné");
			FrameEditeurLayer editeur = new FrameEditeurLayer(this,layer);
			editeur.setVisible(true);
		} else {}
	}
	@Override
	public void stateChanged(ChangeEvent e) {this.repaint();}
}
