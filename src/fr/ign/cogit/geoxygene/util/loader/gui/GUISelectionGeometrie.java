package fr.ign.cogit.geoxygene.util.loader.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Choix des paramètres géométriques lors de la création de tables postgis
 * et du fichier de mapping à partir d'une classe java (le problème ne se
 * pose pas avec Oracle)
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 */

public class GUISelectionGeometrie extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7651040118952366197L;
	private static Dialog frame = new JDialog();
	private final GUISelectionGeometrie selectionGeometrie;
	private Color bluegray = new Color(197,197,232);
	private JPanel panneauType, panneauDimension, jPanelBoutton;
	//private JScrollPane scrollChargement, scrollStockage;
	private Box boxType, boxDimension;
	//private int selectionType = 0, selectionDimension = 0;
	private JButton jButtonOK;
	private Box boxe = Box.createVerticalBox();
	private static String[] stringsChoixType = new String[4];
	private static String[] stringsChoixDimension = new String[2];
	private String titreType="";
	private String titreDimension="";


	private int typeGeometrie;
	public int getTypeGeometrie() {return typeGeometrie;}
	public void setTypeGeometrie(int typeGeometrie) {this.typeGeometrie = typeGeometrie;}

	private int dimensionGeometrie;
	public int getDimensionGeometrie() {return dimensionGeometrie;}
	public void setDimensionGeometrie(int dimensionGeometrie) {this.dimensionGeometrie = dimensionGeometrie;}


	/** Constructeur de l'interface **/
	public GUISelectionGeometrie(){
		super(frame,"Choix de la géométrie",true);
		selectionGeometrie = this;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(430,270));
		setSize(430,270);
		setResizable(false);
		setBackground(Color.white);
		setAlwaysOnTop(true);

		Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((tailleEcran.width-this.getWidth())/2,
				(tailleEcran.height-this.getHeight())/2);


		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));

		//initialisation des différents composants et variables
		stringsChoixType[0]="Point";
		stringsChoixType[1]="LineString";
		stringsChoixType[2]="Polygon";
		stringsChoixType[3]="Geometry collection";

		stringsChoixDimension[0]="2D";
		stringsChoixDimension[1]="3D";


		titreType = "Quel est le type de votre géométrie ?";
		titreDimension = "Quel est la dimension de votre géométrie ?";
		initBoxTypeGeom();
		initBoxDimensionGeom();
		initJPanelType();
		initJPanelDimension();
		initJPanelBoutton();

		boxe.setBackground(Color.white);
		boxe.add(panneauType);
		boxe.add(panneauDimension);

		boxe.add(jPanelBoutton);

		//Association des panneaux au conteneur
		this.setContentPane(boxe);

		//Visualisation
		this.pack();
		this.setVisible(true);
	}

	/**initialise le panel principal**/
	private void initJPanelType() {
		panneauType = new JPanel(new java.awt.BorderLayout());
		panneauType.setPreferredSize(new Dimension(820,300));
		panneauType.setMaximumSize(new Dimension(820,300));
		panneauType.setBackground(Color.white);
		panneauType.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(bluegray, bluegray),titreType));
		panneauType.add(boxType);
	}

	/**initialise le panel principal**/
	private void initJPanelDimension() {
		panneauDimension = new JPanel(new java.awt.BorderLayout());
		panneauDimension.setPreferredSize(new Dimension(820,300));
		panneauDimension.setMaximumSize(new Dimension(820,300));
		panneauDimension.setBackground(Color.white);
		panneauDimension.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(bluegray, bluegray),titreDimension));
		panneauDimension.add(boxDimension);
	}

	private void initBoxTypeGeom() {
		boxType = Box.createVerticalBox();
		ButtonGroup choix = new ButtonGroup();
		JCheckBox checkBox;
		final int t = stringsChoixType.length;
		for (int i=0;i<t;i++){
			checkBox = new JCheckBox(stringsChoixType[i]);
			checkBox.setBackground(Color.white);
			if(i==0){
				checkBox.setSelected(true);
				setTypeGeometrie(0);
			}
			checkBox.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == 1){
						Object source = e.getItemSelectable();
						for (int index=0;index<t;index++){
							if (((JCheckBox)source).getText().equals(stringsChoixType[index])){
								setTypeGeometrie(index);
							}
						}
					}
				}
			});
			choix.add(checkBox);
			boxType.add(checkBox);
		}
	}

	private void initBoxDimensionGeom() {
		boxDimension = Box.createVerticalBox();
		ButtonGroup choix = new ButtonGroup();
		JCheckBox checkBox;
		final int t = stringsChoixDimension.length;
		for (int i=0;i<t;i++){
			checkBox = new JCheckBox(stringsChoixDimension[i]);
			checkBox.setBackground(Color.white);
			if(i==0){
				checkBox.setSelected(true);
				setDimensionGeometrie(2);
			}
			checkBox.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == 1){
						Object source = e.getItemSelectable();
						for (int index=0;index<t;index++){
							if (((JCheckBox)source).getText().equals(stringsChoixDimension[index])){
								setDimensionGeometrie(index+2);
							}
						}
					}
				}
			});
			choix.add(checkBox);
			boxDimension.add(checkBox);
		}
	}

	/** initialisation de jPanelBoutton*/
	private void initJPanelBoutton() {
		initJButtonOK();

		jPanelBoutton = new JPanel();
		jPanelBoutton.setLayout(new FlowLayout());
		jPanelBoutton.add(jButtonOK);
		jPanelBoutton.setBackground(Color.white);
	}

	/** This method initializes jButtonChargement */
	private void initJButtonOK() {
		jButtonOK = new JButton();
		jButtonOK.setPreferredSize(new java.awt.Dimension(80,30));
		jButtonOK.setSize(new java.awt.Dimension(80,30));
		jButtonOK.setText("Ok");
		jButtonOK.setVisible(true);

		jButtonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionGeometrie.dispose();
			}
		});
	}
}