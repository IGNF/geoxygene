/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.plugin.semio.toolbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


import fr.ign.cogit.geoxygene.semio.legend.legendContent.*;
import fr.ign.cogit.geoxygene.semio.legend.metadata.*;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * @author Charlotte Hoarau
 * 
 * Specifications toolbar.
 * Used to described the semantic relations in the legend
 * and the organization of the legend tree.
 *
 */
public class SpecificationToolBar
						extends JToolBar
						implements ActionListener, ListSelectionListener {
	
	private static final long serialVersionUID = 1L;
    
	private List<SemanticRelation> relationList 
		= new ArrayList<SemanticRelation>();
	private String[] layerNames;
	
	private Legend currentLegend;
	
	public Legend getCurrentLegend() {
		return currentLegend;
	}
	
	public void setCurrentLegend(Legend currentLegend) {
		this.currentLegend = currentLegend;
	}

	private SemanticRelationDescriptor currentSemanticRelations;
	
	public SemanticRelationDescriptor getCurrentSemanticRelations() {
		return currentSemanticRelations;
	}
	
	public void setCurrentSemanticRelations(
			SemanticRelationDescriptor currentSemanticRelations) {
		this.currentSemanticRelations = currentSemanticRelations;
	}
	
	/**Components of the first tab (RelationShip Selection)*/
	private JComboBox comboLayer1;
	private JComboBox comboLayer2;
	private JComboBox comboLayer3;
	private JComboBox comboLayer4;
	private JComboBox comboLayer5;
	private JComboBox comboLayer6;
	private JComboBox comboLayer7;
	
	private JComboBox comboRelation;
	private JButton btnValidOneRelation;
	
	private JPanel comboPanel;
	
	private DefaultListModel listRelation;
	private JList list;
	
	private JButton btnValidAllRelations;
	private JButton btnValidLegendTree;

	private JButton btnRemoveRelation;
	
	/**Components of the second tab (Legend Tree Build)*/
	private JTree graphicLegendTree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	private JMenuItem eraseMenu;
	private JMenuItem addThemeMenu;
	private JMenuItem addLayerMenu;
	private JMenuItem addAllLayerMenu;

	private DefaultMutableTreeNode selectedNode;
	
	private JPanel panelLegendTree;
	private JPanel panelRelationship;
	
	/**Common components*/
	private JTabbedPane tabbedPane;
	
	private ProjectFrame frame;
	
	/**
	 * Constructor. Building the ToolBar.
	 * @param frame The Project Frame of the ToolBar
	 */
	public SpecificationToolBar(final ProjectFrame frame) {
		super("Legend pecifications", SwingConstants.VERTICAL);
		
		tabbedPane = new JTabbedPane();
		
		//Initialization of the projectFrame components
		this.frame = frame;
		
		List<Layer> layers = this.frame.getLayers();
		layerNames = new String[layers.size()+1];
		for (int i = 0; i < layerNames.length-1; i++) {
			layerNames[i] = layers.get(i).getName();
		}
		///////////////////////////////////////////////////////////////////////
		//          LEGEND TREE PANEL
		root = new DefaultMutableTreeNode("Legend");
		
		graphicLegendTree = new JTree(root);
		this.model = new DefaultTreeModel(root);
		graphicLegendTree.setModel(model);
		graphicLegendTree.addMouseListener(new MouseAdapter(){

			@Override
      public void mousePressed(MouseEvent event) {
				//Right click in the JTree
				if(event.getButton() == MouseEvent.BUTTON3){
					//Getting the selecting Node
					graphicLegendTree.setSelectionPath(graphicLegendTree
							.getPathForLocation(event.getX(), event.getY()));
					selectedNode = (DefaultMutableTreeNode)graphicLegendTree
											.getLastSelectedPathComponent();
					
					//Popup Build
					eraseMenu = new JMenuItem("Delete");
					eraseMenu.addActionListener(SpecificationToolBar.this);
					
					addThemeMenu = new JMenuItem("Add a theme");
					addThemeMenu.addActionListener(SpecificationToolBar.this);

					addLayerMenu = new JMenuItem("Add layers");
					addLayerMenu.addActionListener(SpecificationToolBar.this);

					addAllLayerMenu = new JMenuItem("Add all the layers");
					addAllLayerMenu.addActionListener(SpecificationToolBar.this);
					
					JPopupMenu jpm = new JPopupMenu();
					jpm.add(addLayerMenu);
					jpm.add(addAllLayerMenu);
					jpm.add(addThemeMenu);
					jpm.addSeparator();
					jpm.add(eraseMenu);
					jpm.show(graphicLegendTree, event.getX(), event.getY());
				}
			}
		});
		
		btnValidLegendTree = new JButton("Validate the Legend Tree");
		btnValidLegendTree.addActionListener(this);
		
		panelLegendTree = new JPanel();
		panelLegendTree.setLayout(new BorderLayout());
		panelLegendTree.add(btnValidLegendTree, BorderLayout.NORTH);
		panelLegendTree.add(graphicLegendTree, BorderLayout.CENTER);
		
		tabbedPane.addTab("Legend Tree", null, panelLegendTree, "Legend Tree");
		
		///////////////////////////////////////////////////////////////////////
		//          SEMANTIC RELATION PANEL
		panelRelationship = new JPanel();
		tabbedPane.addTab("Semantic RelationShip", null, panelRelationship,
		        "Semantic specifications of the legend");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setEnabledAt(1, false);
		
		///////////////////////////////////////////////////////////////////////
		//          CONTRASTS PANEL
//		JPanel panelContrast = new JPanel();
//		tabbedPane.addTab("Color Contrasts", icon, panelContrast,
//		        "Selection of the Contrasts");
//		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		///////////////////////////////////////////////////////////////////////
		//          HELP PANEL
		JTextPane txtHelp = createTextHelp();
		JScrollPane paneHelp = new JScrollPane(txtHelp);
		paneHelp.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneHelp.setPreferredSize(new Dimension(280, 380));
		paneHelp.setMinimumSize(new Dimension(10, 10));

		JPanel panelHelp = new JPanel();
		panelHelp.add(paneHelp);
		
		tabbedPane.addTab("Help", null, panelHelp, "Concept Glossary");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		///////////////////////////////////////////////////////////////////////
		//Add the panel to this frame.
		tabbedPane.setPreferredSize(new Dimension(300, 800));
		add(tabbedPane);
		setVisible(true);
	}
	
	/**
	 * This method create and styles the text of the Help tab.
	 * @return textPaneHelp The helping text.
	 */
	private JTextPane createTextHelp(){
		String[] initString ={
				" Semantic Relations\n",					//bold large
				"\n",										//small
				"   Association\n",							//large
				"   Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication\n",	//regular
				"\n",										//small
				"   Differenciation\n",						//large
				"   Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication\n",	//regular
				"\n",										//small
				"   Order\n",								//large
				"   Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication\n",	//regular
				"\n",										//small
				"   Quantity\n",							//large
				"   Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication\n",	//regular
				"\n",										//small
				" Contrasts\n",								//bold large
				"\n",										//small
				"   Hue Contrast\n",						//large
				"   Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication\n",	//regular
				"\n",										//small
				"   Saturation Contrast\n",					//large
				"     Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication" +
				" Explication Explication Explication"		//regular
		};
		
		String[] initStyles = { 
				"boldlarge", "small",
				"bolditalic", "regular", "small",
				"bolditalic", "regular", "small",
				"bolditalic", "regular", "small",
				"bolditalic", "regular", "small",
				"boldlarge", "small",
				"bolditalic", "regular", "small",
				"bolditalic", "regular", "small"
		};

		JTextPane textPaneHelp = new JTextPane();
		StyledDocument doc = textPaneHelp.getStyledDocument();
		addStylesToDocument(doc);
		try {
			for (int i=0; i < initString.length; i++) {
				doc.insertString(doc.getLength(), initString[i],
						doc.getStyle(initStyles[i]));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text into text pane.");
		}
		return textPaneHelp;
	}
	
	/**
	 * This method creates the styles needed to layout the helping text.
	 * @param doc The Styled Document to layout.
	 */
	protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 6);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
        
        s = doc.addStyle("boldlarge", regular);
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setBold(s, true);
        
        s = doc.addStyle("bolditalic", regular);
        StyleConstants.setItalic(s, true);
        StyleConstants.setBold(s, true);
    }
	
	public void buildLegendTree(
			DefaultMutableTreeNode node, LegendComposite container){

		for (int i = 0; i < node.getChildCount(); i++) {
			if (node.getChildAt(i).isLeaf()) {
				LegendLeaf leaf = new LegendLeaf();
				container.getComponents().add(leaf);
				
				leaf.setSymbol(new GraphicSymbol());
				leaf.setName(node.getChildAt(i).toString());
			} else {
				LegendComposite theme = new LegendComposite();
				theme.setName(node.getChildAt(i).toString());
				
				container.getComponents().add(theme);
				buildLegendTree((DefaultMutableTreeNode)node.getChildAt(i), theme);
			}
		}
	}
	@Override
  public void actionPerformed(ActionEvent e) {
		//When the user validate the legend tree
		if (e.getSource() == this.btnValidLegendTree) {
			if (layerNames.length - 1 > root.getLeafCount()){
				JOptionPane.showMessageDialog(this, "All the layers must appear in the legend tree !", "Legent Tree not finished", JOptionPane.ERROR_MESSAGE);
			} else {
				LegendComposite rootLegend = new LegendComposite();
				rootLegend.setName("Root");
				
				currentLegend = new Legend();
//				String legendName = JOptionPane.showInputDialog(
//						SpecificationToolBar.this, 
//				"Name of the new tree legend");
				currentLegend.setName("Legend");
				currentLegend.setLegendRoot(rootLegend);
				buildLegendTree(root, rootLegend);
				System.out.println(currentLegend.toString());
				
				CharArrayWriter writer = new CharArrayWriter();
				currentLegend.marshall(writer);
				System.out.println(writer.toString());
			}
			tabbedPane.setEnabledAt(1, true);
			//The relationships specification panel is created after the legend
			//   --> Themes of the legend can be integrated in the interface
			createRelationsPanelComponents(SpecificationToolBar.this.panelRelationship);
			SpecificationToolBar.this.tabbedPane.setSelectedIndex(1);
			
			
		}
		//When the user erase a node
		if (e.getSource() == this.eraseMenu){
			model.removeNodeFromParent(selectedNode);
		}
		//When the user add layers in the legend tree
		if (e.getSource() == this.addLayerMenu){	        
			JDialog dialog = new JDialog();
			
			final JButton okBtn = new JButton("Ok");
			okBtn.setActionCommand("Set");
			okBtn.addActionListener(new java.awt.event.ActionListener() {
				//When the user validate the selection of layers to be added
                @Override
                public void actionPerformed(final ActionEvent e) {
                	JList list = (JList)((JViewport)((JScrollPane)((JButton)
                			e.getSource()).getParent().getComponent(0))
                			.getComponent(0)).getView();
                	for (int i = 0; i < list.getSelectedValues().length; i++) {
	                	DefaultMutableTreeNode parentNode =
	        				(DefaultMutableTreeNode)graphicLegendTree.getLastSelectedPathComponent();
	        			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(list.getSelectedValues()[i]);
	        			parentNode.add(childNode);
	        			model.insertNodeInto(childNode, parentNode, parentNode.getChildCount()-1);
	        			model.nodeChanged(parentNode);
	        			graphicLegendTree.expandPath(new TreePath(model.getPathToRoot(parentNode)));
                	}
                	((JDialog)((JButton)e.getSource()).getParent().getParent().getParent().getParent()).dispose();
                }
            });
			dialog.add(okBtn);
			dialog.getRootPane().setDefaultButton(okBtn);
			
			JList list = new JList(layerNames);
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL);
			
			JScrollPane listScroller = new JScrollPane(list);
			listScroller.setPreferredSize(new Dimension(250, 200));
			
			dialog.setLayout(new BorderLayout());
			dialog.getContentPane().add(listScroller, BorderLayout.CENTER);
			dialog.getContentPane().add(okBtn, BorderLayout.SOUTH);
			dialog.pack();
			dialog.setLocation(500,400);
			dialog.setVisible(true);
			dialog.setAlwaysOnTop(true);
		}
		//When the user add a theme in the legend tree
		if (e.getSource() == this.addThemeMenu){
			String themeName = JOptionPane.showInputDialog(
					SpecificationToolBar.this, 
    				"Name of the new theme");
			DefaultMutableTreeNode parentNode =
				(DefaultMutableTreeNode)graphicLegendTree.getLastSelectedPathComponent();
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(themeName);
			parentNode.add(childNode);
			model.insertNodeInto(childNode, parentNode, parentNode.getChildCount()-1);
			model.nodeChanged(parentNode);
			graphicLegendTree.expandPath(new TreePath(model.getPathToRoot(parentNode)));
		}
		//When the user add a layer in the legend tree
		if (e.getSource() == this.addAllLayerMenu){
			DefaultMutableTreeNode parentNode =
					(DefaultMutableTreeNode)graphicLegendTree.getLastSelectedPathComponent();
			for (String layerName : layerNames) {
				if (layerName != null) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(layerName);
					parentNode.add(childNode);
					model.insertNodeInto(childNode, parentNode, parentNode.getChildCount()-1);
					model.nodeChanged(parentNode);
				}
			}
			graphicLegendTree.expandPath(new TreePath(model.getPathToRoot(parentNode)));
		}
		
		//When the user choose a relation type
		if (e.getSource() == this.comboRelation){
			if (comboRelation.getSelectedIndex() == SemanticRelation.ORDER){
				comboLayer3.setVisible(true);
				comboLayer3.setEnabled(true);
				comboLayer4.setVisible(true);
				comboLayer4.setEnabled(true);
				comboLayer5.setVisible(true);
				comboLayer5.setEnabled(true);
				comboLayer6.setVisible(true);
				comboLayer6.setEnabled(true);
				comboLayer7.setVisible(true);
				comboLayer7.setEnabled(true);
				
				this.comboPanel.setPreferredSize(new Dimension(300,300));
			}else{
				comboLayer3.setVisible(false);
				comboLayer3.setEnabled(false);
				comboLayer4.setVisible(false);
				comboLayer4.setEnabled(false);
				comboLayer5.setVisible(false);
				comboLayer5.setEnabled(false);
				comboLayer6.setVisible(false);
				comboLayer6.setEnabled(false);
				comboLayer7.setVisible(false);
				comboLayer7.setEnabled(false);
				
				this.comboPanel.setPreferredSize(new Dimension(300,300));
			}
		}
		//When the user validate a relation
		if (e.getSource()== this.btnValidOneRelation) {
			String nameComponent1 = ((String)comboLayer1.getSelectedItem()).substring(8);
			String nameComponent2 = ((String)comboLayer2.getSelectedItem()).substring(8);

			if (comboLayer1.getSelectedIndex() == comboLayer2.getSelectedIndex()){
				JOptionPane.showMessageDialog(
						tabbedPane, 
						"You must select two differents layers !",
						"Relation between two layers",
						JOptionPane.WARNING_MESSAGE);
			} else {
				//Adding a relation between layers
				if (LegendLeaf.class.isAssignableFrom(
						currentLegend.getComponent(nameComponent1).getClass()) &&
					LegendLeaf.class.isAssignableFrom(
						currentLegend.getComponent(nameComponent2).getClass())){
					//Displaying the relation in the interface
					String textRelation = null;
					int indexRelation = comboRelation.getSelectedIndex();
					switch (indexRelation){
						case 1:
							textRelation = 
								"A - " + nameComponent1 + " / " + nameComponent2;
							break;
						case 2:
							textRelation =
								"D - " + nameComponent1 + " / " + nameComponent2;
							break;
						case 3:
							if(this.comboRelation.getSelectedIndex() == SemanticRelation.ORDER){
								textRelation = nameComponent1 + " >> " + nameComponent2;
								
								if(comboLayer3.getSelectedItem()!= null){
									textRelation = textRelation + " >> " +
													comboLayer3.getSelectedItem();
								}
								if(comboLayer4.getSelectedItem()!= null){
									textRelation = textRelation + " >> " +
													comboLayer4.getSelectedItem();
								}
								if(comboLayer5.getSelectedItem()!= null){
									textRelation = textRelation + " >> " +
													comboLayer5.getSelectedItem();
								}
								if(comboLayer6.getSelectedItem()!= null){
									textRelation = textRelation + " >> " +
													comboLayer6.getSelectedItem();
								}
								if(comboLayer7.getSelectedItem()!= null){
									textRelation = textRelation + " >> " +
													comboLayer7.getSelectedItem();
								}
							}
							break;
						case 4:
							break;
					}
					listRelation.addElement(textRelation);
					
					//Adding the Relation to the RelationList (Java Object)
					List<LegendComponent> layerIndex = new ArrayList<LegendComponent>();
					layerIndex.add(currentLegend.getLeaf(nameComponent1));
					layerIndex.add(currentLegend.getLeaf(nameComponent2));
					if(this.comboRelation.getSelectedIndex() == SemanticRelation.ORDER){
						if(comboLayer3.getSelectedItem()!= null){
							String nameComponent3 =
								((String)comboLayer3.getSelectedItem()).substring(8);
							layerIndex.add(currentLegend.getLeaf(nameComponent3));
						}
						if(comboLayer4.getSelectedItem()!= null){
							String nameComponent4 =
								((String)comboLayer4.getSelectedItem()).substring(8);
							layerIndex.add(currentLegend.getLeaf(nameComponent4));
						}
						if(comboLayer5.getSelectedItem()!= null){
							String nameComponent5 =
								((String)comboLayer5.getSelectedItem()).substring(8);
							layerIndex.add(currentLegend.getLeaf(nameComponent5));
						}
						if(comboLayer6.getSelectedItem()!= null){
							String nameComponent6 =
								((String)comboLayer6.getSelectedItem()).substring(8);
							layerIndex.add(currentLegend.getLeaf(nameComponent6));
						}
						if(comboLayer7.getSelectedItem()!= null){
							String nameComponent7 =
								((String)comboLayer7.getSelectedItem()).substring(8);
							layerIndex.add(currentLegend.getLeaf(nameComponent7));
						}
					}
					
					SemanticRelation relation = new SemanticRelation(
							layerIndex,
							comboRelation.getSelectedIndex());
					System.out.println(relation.toString());
					relationList.add(relation);
				} else if ( //Adding a relation between themes
						LegendComposite.class.isAssignableFrom(
								currentLegend.getComponent(nameComponent1).getClass()) &&
						LegendComposite.class.isAssignableFrom(
								currentLegend.getComponent(nameComponent2).getClass())){
					
					LegendComposite theme1 = currentLegend.getTheme(nameComponent1);
					LegendComposite theme2 = currentLegend.getTheme(nameComponent2);
					
					//Themes can not be ordered
					if (comboRelation.getSelectedIndex() == 3) {
						JOptionPane.showMessageDialog(
								tabbedPane, 
								"Themes can not be ordered.",
								"Relation between two layers",
								JOptionPane.WARNING_MESSAGE);
						
					/*Themes can be associated
						-> each layer of the first theme will be associated
							with each layer of the second one.
					Themes can be differentiated
						-> each layer of the first theme will be differentiated
							with each layer of the second one.*/
					} else {
						for (LegendLeaf	leaf1 : theme1.allLeaves()) {
							for (LegendLeaf leaf2 : theme2.allLeaves()) {
								//Displaying the relation in the interface
								String textRelation = null;
								int indexRelation = comboRelation.getSelectedIndex();
								switch (indexRelation){
									case 1:
										textRelation = 
											"A - " + leaf1.getName() + " / " + leaf2.getName();
										break;
									case 2:
										textRelation =
											"D - " + leaf1.getName() + " / " + leaf2.getName();
										break;
								}
								listRelation.addElement(textRelation);
								
								//Adding the Relation to the RelationList (Java Object)
								List<LegendComponent> layerIndex = new ArrayList<LegendComponent>();
								layerIndex.add(leaf1);
								layerIndex.add(leaf2);
								
								SemanticRelation relation = new SemanticRelation(
										layerIndex,
										comboRelation.getSelectedIndex());
								System.out.println(relation.toString());
								relationList.add(relation);
							}
						}
					}
					
				//Theme and layers can not be linked by a semantic relationship
				} else {
					JOptionPane.showMessageDialog(
							tabbedPane, 
							"It is not possible to define a semantic relationship between a theme and a layer.",
							"Relation between two layers",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		
		//When the user remove a relation
		if (e.getSource()== this.btnRemoveRelation) {
			//Removing the relation on the list to save
			relationList.remove(list.getSelectedIndex());
			
			//Removing the relation on the displayed list
			listRelation.remove(list.getSelectedIndex());
		}
		
		//When the user validate the semantic relation description
		if (e.getSource()==this.btnValidAllRelations){
			
			currentSemanticRelations = new SemanticRelationDescriptor(
					this.relationList,
					relationList.size());
			System.out.println("= " + currentSemanticRelations.getNbRelations() + " relations saved" + System.getProperty("line.separator"));
				
			CharArrayWriter writer = new CharArrayWriter();
			currentSemanticRelations.marshall(writer);
			System.out.println(writer.toString());
		}
	}

	@Override
  public void valueChanged(ListSelectionEvent e) {		
		if (e.getValueIsAdjusting() == false) {
			if (list.getSelectedIndex() == -1) {
				//No selection, disable fire button.
				btnRemoveRelation.setEnabled(false);
			} else {
				//Selection, enable the fire button.
				btnRemoveRelation.setEnabled(true);
			}
		}
	}
	
	/**
	 * Dynamic creation of the relationships panel.
	 * Its elements depend on the legend tree that have been created before.
	 * Themes are included to be available to build semantic relationships.
	 * Layers contained in the same theme are associated by default.
	 * 
	 * @param panelRelationship The panel to be created.
	 */
	public void createRelationsPanelComponents(JPanel panelRelationship){
		JLabel lblTitleRelation = new JLabel("  Add a semantic Relationship");
		lblTitleRelation.setFont(lblTitleRelation.getFont().deriveFont(16f));
		lblTitleRelation.setHorizontalAlignment(JLabel.LEFT);
		lblTitleRelation.setVerticalAlignment(JLabel.BOTTOM);
		JPanel panelTitleRelation = new JPanel();
		panelTitleRelation.setPreferredSize(new Dimension(300,40));
		panelTitleRelation.setLayout(new GridLayout(1, 1));
		panelTitleRelation.add(lblTitleRelation);
		
		String[] legendElements = new String[layerNames.length + this.currentLegend.getThemes().size()];
		for (int i = 0; i < this.currentLegend.getThemes().size(); i++) {
			legendElements[i] = "Theme : " + this.currentLegend.getThemes().get(i).getName();
		}
		for (int i = this.currentLegend.getThemes().size(); i < legendElements.length; i++) {
			legendElements[i] = "Layer : " + layerNames[i-this.currentLegend.getThemes().size()];
		}
		
		comboLayer1 = new JComboBox(legendElements);
		comboLayer1.setPreferredSize(new Dimension(200,25));
		
		comboLayer2 = new JComboBox(legendElements);
		comboLayer2.setPreferredSize(new Dimension(200,25));
		
		comboLayer3 = new JComboBox(legendElements);
		comboLayer3.setPreferredSize(new Dimension(200,25));
		comboLayer3.setVisible(false);
		comboLayer3.setEnabled(false);
		
		comboLayer4 = new JComboBox(legendElements);
		comboLayer4.setPreferredSize(new Dimension(200,25));
		comboLayer4.setVisible(false);
		comboLayer4.setEnabled(false);
		
		comboLayer5 = new JComboBox(legendElements);
		comboLayer5.setPreferredSize(new Dimension(200,25));
		comboLayer5.setVisible(false);
		comboLayer5.setEnabled(false);
		
		comboLayer6 = new JComboBox(legendElements);
		comboLayer6.setPreferredSize(new Dimension(200,25));
		comboLayer6.setVisible(false);
		comboLayer6.setEnabled(false);
		
		comboLayer7 = new JComboBox(legendElements);
		comboLayer7.setPreferredSize(new Dimension(200,25));
		comboLayer7.setVisible(false);
		comboLayer7.setEnabled(false);
		
		String[] relations = new String[4];
		relations[0] = null;
		relations[1] = "Association";
		relations[2] = "Differenciation";
		relations[3] = "Order";
//		relations[4] = "Quantity";
		comboRelation = new JComboBox(relations);
		comboRelation.setPreferredSize(new Dimension(200,25));
		comboRelation.addActionListener(this);
		
		comboPanel = new JPanel();
		comboPanel.add(comboRelation);
		comboPanel.add(new Label("Layers :                                 " +
				"                                           "));
		comboPanel.add(comboLayer1);
		comboPanel.add(comboLayer2);
		comboPanel.add(comboLayer3);
		comboPanel.add(comboLayer4);
		comboPanel.add(comboLayer5);
		comboPanel.add(comboLayer6);
		comboPanel.add(comboLayer7);
		comboPanel.setPreferredSize(new Dimension(300,300));

		btnValidOneRelation = new JButton("Add");
		btnValidOneRelation.addActionListener(this);
		JPanel pValidRelation = new JPanel();
		pValidRelation.add(btnValidOneRelation);
		pValidRelation.setPreferredSize(new Dimension(300,50));
		
		JLabel lblRecord = new JLabel("  Relationships");
		lblRecord.setFont(lblTitleRelation.getFont().deriveFont(16f));
		lblRecord.setHorizontalAlignment(JLabel.LEFT);
		lblRecord.setVerticalAlignment(JLabel.BOTTOM);
		JPanel pRecord = new JPanel();
		pRecord.setPreferredSize(new Dimension(300,40));
		pRecord.setLayout(new GridLayout(1, 1));
		pRecord.add(lblRecord);
		
		listRelation = new DefaultListModel();
		//Creating association relationships between leaves contained directly in the same theme
		for (LegendComposite theme : this.currentLegend.getThemes()) {
			System.out.println("theme directLeaves() : " + theme.directLeaves().size());
			System.out.println("theme allLeaves() : " + theme.allLeaves().size());
			if (theme.directLeaves().size() == theme.allLeaves().size()) {
				List<LegendLeaf> leaves = theme.directLeaves();
				for (int i = 0; i < leaves.size(); i++) {
					for (int j = i+1; j < leaves.size(); j++) {
						String textRelation =
							"A - " + leaves.get(i).getName()
							+ " / " + leaves.get(j).getName();
						listRelation.addElement(textRelation);
						
						List<LegendComponent> layerIndex = new ArrayList<LegendComponent>();
						layerIndex.add(leaves.get(i));
						layerIndex.add(leaves.get(j));
						SemanticRelation relation = new SemanticRelation(
								layerIndex,
								1);
						System.out.println(relation.toString());
						relationList.add(relation);
					}
				}
			}
		}
		list = new JList(listRelation);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		JScrollPane jsp = new JScrollPane(list);
		jsp.setPreferredSize(new Dimension(260,150));
		
		btnRemoveRelation = new JButton("Remove the selected relationship");
		btnRemoveRelation.setEnabled(false);
		btnRemoveRelation.addActionListener(this);
		btnValidAllRelations = new JButton("Validate all the relationships");
		btnValidAllRelations.addActionListener(this);
		JPanel pValid = new JPanel();
		pValid.add(btnRemoveRelation);
		pValid.add(btnValidAllRelations);
		pValid.setPreferredSize(new Dimension(300,100));
		
		panelRelationship.add(panelTitleRelation);
		panelRelationship.add(comboPanel);
		panelRelationship.add(pValidRelation);
		panelRelationship.add(pRecord);
		panelRelationship.add(jsp);
		panelRelationship.add(pValid);
	}
}
