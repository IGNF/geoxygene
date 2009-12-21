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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JColorChooser;
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
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * @author Julien Perret
 *
 */
public class FrameEditeurLayer extends JFrame implements TreeSelectionListener, ChangeListener {
	private static final long serialVersionUID = -2594439123489828985L;

	static Logger logger=Logger.getLogger(FrameEditeurLayer.class.getName());

	private FrameEditeurSLD frameEditeurSLD;
	private JTree tree;

	private StyledLayerDescriptor sld;
	DataSet dataset;
	
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
	
	Layer layer;
	/**
	 * Renvoie la valeur de l'attribut layer.
	 * @return la valeur de l'attribut layer
	 */
	public Layer getLayer() {return this.layer;}

	/**
	 * Affecte la valeur de l'attribut layer.
	 * @param layer l'attribut layer à affecter
	 */
	public void setLayer(Layer layer) {this.layer = layer;}

	public FrameEditeurLayer(FrameEditeurSLD frame,Layer layer) {
		this.frameEditeurSLD=frame;
		setSld(this.frameEditeurSLD.getSld());
		setLayer(layer);
		this.dataset=frame.dataset;
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setLayout(new BorderLayout());
		setResizable(true);
		setSize(new Dimension(500,500));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setTitle("Editeur de Layers de GeOxygene");
		setIconImage(this.frameEditeurSLD.getIconImage());

	    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Styled Layer Descriptor");
	    this.tree = new JTree(top);
	    createNodes(top);
	    this.tree.setCellRenderer(new LayerRenderer(this.sld));
	    //tree.setEditable(true);
	    this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    this.tree.addTreeSelectionListener(this);
	    this.tree.setShowsRootHandles(false);
	    this.tree.setExpandsSelectedPaths(true);
	    this.tree.expandRow(0);
		//Enable tool tips.
	    ToolTipManager.sharedInstance().registerComponent(this.tree);

		JScrollPane scroll = new JScrollPane(this.tree,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll,BorderLayout.CENTER);
	}

	/**
	 * @param top
	 */
	private void createNodes(DefaultMutableTreeNode top) {
		if (this.layer==null) return;
		int nbStyle = 0;
		for (Style style : this.layer.getStyles()) {
			String name = style.getName();
			if ((name==null)||(name.length()==0)) name=style.getClass().getSimpleName()+" "+nbStyle++; //$NON-NLS-1$
			style.setName(name);
			DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode(style);
			top.add(styleNode);
		}
	}
	class LayerRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 3819934049264771686L;
		private StyledLayerDescriptor sldRenderer;
	    public LayerRenderer(StyledLayerDescriptor sld) {this.sldRenderer=sld;}

	    @Override
		public Component getTreeCellRendererComponent(JTree treeRenderer,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocusRenderer) {

	        super.getTreeCellRendererComponent(treeRenderer, value, sel,expanded, leaf, row,hasFocusRenderer);
	        if (leaf && isStyle(value)) {
	            setToolTipText("Ceci est un style.");
	            Style style = (Style) ((DefaultMutableTreeNode)value).getUserObject();
	            setText(style.getName());
	            setIcon(new StyleIcon(style,this.sldRenderer));
	        } else {
	            setToolTipText(null); //no tool tip
	        } 

	        return this;
	    }

	    protected boolean isStyle(Object value) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
	        return (Style.class.isAssignableFrom(node.getUserObject().getClass()));
	    }
	}
	class StyleIcon implements Icon {
		Style style;
		DessinableGeoxygene d;
		public StyleIcon(Style s, StyledLayerDescriptor sld) {
			this.style = s;
			this.d = new DessinableGeoxygene(sld);
			this.d.setCentreGeo(new DirectPosition(50.0,50.0));
		}
		@Override
		public int getIconHeight() {return 50;}
		@Override
		public int getIconWidth() {return 100;}
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			try {
				this.d.majLimitesAffichage(this.getIconWidth(),this.getIconHeight());
				if (FrameEditeurLayer.this.dataset.getPopulation(FrameEditeurLayer.this.layer.getName())!=null)
					this.d.dessiner((Graphics2D)g,this.style, FrameEditeurLayer.this.dataset.getPopulation(FrameEditeurLayer.this.layer.getName()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		this.tree.getLastSelectedPathComponent();

		//Nothing is selected.	
		if (node == null) return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			Style style = (Style)nodeInfo;
			if (logger.isDebugEnabled()) logger.debug(style.getClass().getSimpleName()+ " sélectionné");
			int nbColor = 0;
			Stroke stroke = null;
			Color strokeColor = null;
			Fill fill = null;
			Color fillColor = null;
			Fill textFill = null;
			Color textColor = null;
			Fill haloFill = null;
			Color haloColor = null;
			UserStyle userStyle = (UserStyle) style;
			for(FeatureTypeStyle fts:userStyle.getFeatureTypeStyles()) {
				for (Rule rule:fts.getRules()) {
					for(Symbolizer symbolizer:rule.getSymbolizers()) {
						if (symbolizer.getStroke()!=null) {
							strokeColor = symbolizer.getStroke().getColor();
							stroke = symbolizer.getStroke();
							nbColor++;
						}
						if (symbolizer.isPolygonSymbolizer()) {
							PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
							if (polygonSymbolizer.getFill()!=null) {
								fillColor = polygonSymbolizer.getFill().getColor();
								fill = polygonSymbolizer.getFill();
								nbColor++;
							}
						} else if (symbolizer.isTextSymbolizer()) {
							TextSymbolizer textSymbolizer = (TextSymbolizer) symbolizer;
							if (textSymbolizer.getFill()!=null) {
								textColor = textSymbolizer.getFill().getColor();
								textFill = textSymbolizer.getFill();
								nbColor++;
							}
							if ((textSymbolizer.getHalo()!=null)&&(textSymbolizer.getHalo().getFill()!=null)) {
								haloColor = textSymbolizer.getHalo().getFill().getColor();
								haloFill = textSymbolizer.getHalo().getFill();
								nbColor++;
							}
						}
					}
				}
			}
			if (nbColor>0) {
				if (logger.isDebugEnabled()) logger.debug(nbColor+" couleurs");
				if (stroke!=null) {
					Color newColor = JColorChooser.showDialog(this, "Choisir une couleur pour le trait", strokeColor);
					if (logger.isDebugEnabled()) logger.debug("couleur pour le trait = "+strokeColor);
					stroke.setColor(newColor);
				}
				if (fill!=null) {
					Color newColor = JColorChooser.showDialog(this, "Choisir une couleur pour le remplissage", fillColor);
					if (logger.isDebugEnabled()) logger.debug("couleur pour le remplissage = "+fillColor);
					fill.setColor(newColor);
				}
				if (textFill!=null) {
					Color newColor = JColorChooser.showDialog(this, "Choisir une couleur pour le texte", textColor);
					if (logger.isDebugEnabled()) logger.debug("couleur pour le texte = "+textColor);
					textFill.setColor(newColor);
				}
				if (haloFill!=null) {
					Color newColor = JColorChooser.showDialog(this, "Choisir une couleur pour le halo du texte", haloColor);
					if (logger.isDebugEnabled()) logger.debug("couleur pour le halo du texte = "+haloColor);
					haloFill.setColor(newColor);
				}
				this.sld.fireActionPerformed(new ChangeEvent(this));
			}
		} else {}
	}
	@Override
	public void stateChanged(ChangeEvent e) {this.repaint();}
}
