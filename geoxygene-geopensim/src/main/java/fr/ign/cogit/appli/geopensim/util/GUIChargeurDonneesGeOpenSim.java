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
/**
 * 
 */
package fr.ign.cogit.appli.geopensim.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import fr.ign.cogit.geoxygene.util.loader.gui.GUIShapefileChoice;

/**
 * Interface de chargement des données Géographiques venant du
 * Laboratoire Image et Ville dans le cadre du projet ANR GeOpenSim.
 * @author Julien Perret
 *
 */
public class GUIChargeurDonneesGeOpenSim extends JFrame {
	private static final long serialVersionUID = 1L;
	private String title;
	/**
	 * TableCellRenderer utilisé pour la colonne contenant les noms des fichiers chargés.
	 */
	class MappingTableCellRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		/**
		 * Constructeur.
		 */
		public MappingTableCellRenderer() {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int col) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				if (row%2==0) setBackground(new Color(230,230,230));
				else setBackground(table.getBackground());
			}
			if (col == 0) setText((String)value);
			return this;
		}
	}

	/**
	 * TableCellRenderer utilisé pour la colonne contenant le type des objets chargés.
	 * Cette colonne est éditable grace à une comboBox.
	 */
	public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		/**
		 * Constructeur.
		 * @param items liste des valeurs possibles pour la comboBox.
		 */
		public MyComboBoxRenderer(String[] items) {super(items);}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				if (value.equals(new Integer(0))) setBackground(new Color(230,30,30));
				else {
					if (row%2==0) setBackground(new Color(230,230,230));
					else setBackground(table.getBackground());
				}
			}
			// Select the current value
			int selectedIndex = ((Integer)value).intValue();
			String classe = getJavaClassStrings()[selectedIndex];
			setSelectedItem(classe);
			return this;
		}
	}
	/**
	 * Editeur utilisé par la colonne contenant les types de objets chargés.
	 */
	public class MyComboBoxEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		/**
		 * Constructeur.
		 * @param items liste des valeurs possibles pour la comboBox.
		 */
		public MyComboBoxEditor(String[] items) {super(new JComboBox(items));}
	}

	/**
	 * Liste des fichiers shapefiles chargés.
	 */
	protected List<File> shapeFiles = new ArrayList<File>();
	/**
	 * Renvoie la valeur de l'attribut shapeFiles.
	 * @return la valeur de l'attribut shapeFiles
	 */
	public List<File> getShapeFiles() {return this.shapeFiles;}
	/**
	 * Liste des types associés aux shapefiles chargés.
	 */
	private List<Integer> shapeFilesClasses = new ArrayList<Integer>();
	/**
	 * Liste des valeurs possibles pour les types associés aux shapefiles chargés.
	 */
	private String[] javaClassStrings = {
			"Inconnue",
			"Batiment",
			"Cimetiere",
			"Parking",
			"SurfaceEau",
			"SurfaceRoute",
			"TerrainSport",
			"TronconChemin",
			"TronconCoursEau",
			"TronconRoute",
			"TronconVoieFerree",
			"AireTriage",
			"PisteAerodrome",
			"Vegetation"
	};

	/**
	 * Modèle de données pour la table.
	 */
	private TableModel dataModel;
	/**
	 * vrai si le chargement a été validé (grâce au bouton "Ok").
	 */
	protected boolean validated = false;
	/**
	 * ComboBox utilisée pour éditer les valeurs des types associés aux shapefiles.
	 */
	protected JComboBox comboBox = new JComboBox(getJavaClassStrings());
	/**
	 * vrai s'il faut afficher la date à affecter aux données, faux sinon.
	 */
	protected boolean afficheDate = false;	
	private SpinnerModel model = null;
	/**
	 * Checkbox pour Déterminer si les données chargée sont une extraction pour pas.
	 */
	private JCheckBox extractionCheckBox = new JCheckBox("extraction",false);
	private SpinnerModel sridModel = null;
	
	/**
	 * Constructeur.
	 * @param title titre de la fenetre
	 * @param afficheDate vrai si la date est affichée, faux sinon 
	 */
	public GUIChargeurDonneesGeOpenSim(String title, boolean afficheDate) {
		this.title = title;
		this.afficheDate=afficheDate;
		dataModel = new AbstractTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
			  return 2;
			}

			@Override
			public int getRowCount() {
			  return shapeFiles.size();
			}

			@Override
			public Object getValueAt(int row, int col) {
			  return (col == 0)?shapeFiles.get(row).getName():getShapeFilesClasses().get(row);
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return (col==1);
			}

			@Override
			public String getColumnName(int column) {
				return (column == 0)?"fichier shapefile":"classe java";
			}

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0,column).getClass();
			}

			@Override
			public void setValueAt(Object value, int row, int col) {
				if (col == 0) {
					shapeFiles.set(row,(File) value);
					fireTableCellUpdated(row,col);
				}
				if (col == 1) {
					boolean found = false;
					int index = 0;
					for (index = 0; index < getJavaClassStrings().length&&!found;index++) {
						if (getJavaClassStrings()[index].equals(value)) {
							found = true;
							index--;
						}
					}
					getShapeFilesClasses().set(row, index);
					fireTableCellUpdated(row,col);
				}
			}
		};
	}
	/**
	 * Ajout de fichiers shapefiles.
	 */
	private void addShapeFiles() {
		GUIShapefileChoice sfc = new GUIShapefileChoice(true);
		File[] files = sfc.getSelectedFiles();
		if ((files == null)||(files.length == 0)) return;
		updateLayout(files);
	}
	/**
	 * Ajout de répertoires contenant des fichiers shapefiles.
	 * Les répertoires sont tous parcourus récursivement et
	 * tous les shapefiles qu'ils contiennent sont ajoutés.
	 */
	private void addDirectories() {
		GUIShapefileChoice sfc = new GUIShapefileChoice(true);
		File[] files = sfc.getSelectedDirectories();
		if ((files == null)||(files.length == 0)) return;
		updateLayoutDirectories(files);
	}
	/**
	 * Mise à jour de la table avec les répertoires en paramètre.
	 * @param files répertoires
	 */
	private void updateLayoutDirectories(File[] files) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()&&(files[i].getName().endsWith(".shp")||files[i].getName().endsWith(".SHP")))
				updateLayout(files[i]);
			else if (files[i].isDirectory()) updateLayoutDirectories(files[i].listFiles());
		}
	}
	/**
	 * Mise à jour de la table avec les fichiers en paramètre.
	 * @param files fichiers à ajouter à la table des fichiers à charger.
	 */
	private void updateLayout(File[] files) {for(int i=0;i<files.length;i++) updateLayout(files[i]);}
	/**
	 * Mise à jour de la table avec le shapefile en paramètre.
	 * @param shapefile fichier shapefile à ajouter à la table des fichiers à charger.
	 */
	private void updateLayout(File shapefile) {
		String shapefileName = shapefile.getName();
		String javaClassName = getJavaClassNameFromFileName(shapefileName);
		int index = 0;
		if (javaClassName!=null){
			boolean found = false;
			for (index = 0; index < getJavaClassStrings().length&&!found;index++) {
				if (getJavaClassStrings()[index].equals(javaClassName)) {
					found = true;
					index--;
				}
			}
		}
		shapeFiles.add(shapefile);
		getShapeFilesClasses().add(index);
		((AbstractTableModel) dataModel).fireTableDataChanged();
	}
	/**
	 * Création du JDialog de sélection des fichiers.
	 * @param parent Frame parent du JDialog à créer.
	 * @return JDialog de sélection des fichiers shapefiles à charger.
	 */
	private JDialog createDialog(Frame parent) {
		final JDialog dialog = new JDialog(parent, title, true);

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());

		final JTable table = new JTable(dataModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollpane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		TableColumn col = table.getColumnModel().getColumn(0);
		col.setCellRenderer(new MappingTableCellRenderer());
		col = table.getColumnModel().getColumn(1);
		col.setCellEditor(new DefaultCellEditor(comboBox));
		col.setCellRenderer(new MyComboBoxRenderer(getJavaClassStrings()));
		table.setFillsViewportHeight(true);
		contentPane.add(scrollpane,BorderLayout.CENTER);

		table.setRowHeight(30);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

		Icon iconAddButton = new ImageIcon("images/Plus.png");
		JButton addButton = new JButton("Ajouter des Shapefiles",iconAddButton);
		addButton.setToolTipText("Ajouter des Shapefiles");
		addButton.addActionListener(new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
		    addShapeFiles();
		  }
		});
		Icon iconAddDirectoryButton = new ImageIcon("images/AjoutRepertoire.png");
		JButton addDirectoryButton = new JButton("Ajouter des répertoires contenant des Shapefiles",iconAddDirectoryButton);
		addDirectoryButton.setToolTipText("Ajouter des répertoires contenant des Shapefiles");
		addDirectoryButton.addActionListener(new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
		    addDirectories();
			}
		});

		//TODO : utiliser le Booléen afficheDate au moment de l'appel depuis chargeurDonneesBDTopo et ChargeurDonneesGeOpenSim
		controlPanel.add(addButton);
		controlPanel.add(addDirectoryButton);
		if (afficheDate) {
			int anneeCourante = Calendar.getInstance().get(Calendar.YEAR);
			model = new SpinnerNumberModel(anneeCourante,anneeCourante-100,anneeCourante+100,1);
			JSpinner spinner = new JSpinner(model);
			controlPanel.add(spinner);
		}
		controlPanel.add(extractionCheckBox);

		sridModel = new SpinnerNumberModel(-1,-1,32766,1);
		JSpinner spinner = new JSpinner(sridModel);
		controlPanel.add(spinner);

		contentPane.add(controlPanel,BorderLayout.NORTH);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		JButton okButton = new JButton("Ok");
		okButton.setActionCommand("Ok");
		okButton.addActionListener(new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
		    validated = true;
		    dialog.dispose();
		  }
		});

		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
		    dialog.dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		contentPane.add(buttonPanel,BorderLayout.SOUTH);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		return dialog;
	}

	/**
	 * Création du JDialog de sélection des fichiers.
	 * @return vrai si le JDialog a été validé (grâce au bouton "Ok"), faux sinon.
	 */
	public boolean showDialog() {
		final JDialog dialog = createDialog(this);
		dialog.setVisible(true);
		dialog.dispose();
		return validated;
	}

	/**
	 * Renvoie la date à affecter aux données chargées.
	 * @return la date à affecter aux données chargées.
	 */
	public int getDateValue() {return (Integer)model.getValue();}
	/**
	 * Renvoie vrai les données chargées sont extraites d'une base plus grande, faux sinon.
	 * @return vrai les données chargées sont extraites d'une base plus grande, faux sinon.
	 */
	public boolean isExtraction() {return extractionCheckBox.isSelected();}
	/**
	 * Renvoie le SRID à affecter aux données chargées.
	 * @return le SRID à affecter aux données chargées.
	 */
	public int getSRIDValue() {return (Integer)sridModel.getValue();}
	/**
	 * récupère le type d'objets contenus dans le fichier grâce au nom du fichier.
	 * Ce n'est qu'un heuristique simple : on considére que le fichier
	 * contient des bâtiments si son nom contient "batiment", etc.
	 * @param shapefileName nom d'un fichier shapefile
	 * @return le type java supposé correspondant aux features de ce fichier
	 */
	private String getJavaClassNameFromFileName(String shapefileName) {
		String fileName = shapefileName.toLowerCase();
		if (fileName.contains("batiment")&&!fileName.contains("lin")&&!fileName.contains("pct")) {return "Batiment";}
		else if (fileName.contains("cimetiere")) {return "Cimetiere";}
		else if (fileName.contains("parking")) {return "Parking";}
		else if (fileName.contains("surface_route")) {return "SurfaceRoute";}
		else if (fileName.contains("surface_eau")||fileName.contains("res_hydro_surf")) {return "SurfaceEau";}
		else if (fileName.contains("terrain_sport")||fileName.contains("eq_sport_surf")) {return "TerrainSport";}
		else if (fileName.contains("troncon_chemin")||fileName.contains("autre_voie_com")) {return "TronconChemin";}
		else if (fileName.contains("troncon_cours_eau")||fileName.contains("troncon_hydro")||fileName.contains("res_hydro_lin")) {return "TronconCoursEau";}
		else if (fileName.contains("troncon_route") || fileName.equals("route")) {return "TronconRoute";}
		else if (fileName.contains("troncon_voie_ferree")) {return "TronconVoieFerree";}
		else if (fileName.contains("vegetation")||fileName.contains("occ_sol_surf")||fileName.contains("zone_arboree")) {return "Vegetation";}
		else if (fileName.contains("aire_triage")) {return "AireTriage";}
		else if (fileName.contains("piste_aerodrome")) {return "PisteAerodrome";}
		else {return null;}
	}
    /**
     * Affecte la valeur de l'attribut shapeFilesClasses.
     * @param shapeFilesClasses l'attribut shapeFilesClasses à affecter
     */
    public void setShapeFilesClasses(List<Integer> shapeFilesClasses) {
        this.shapeFilesClasses = shapeFilesClasses;
    }
    /**
     * Renvoie la valeur de l'attribut shapeFilesClasses.
     * @return la valeur de l'attribut shapeFilesClasses
     */
    public List<Integer> getShapeFilesClasses() {
        return shapeFilesClasses;
    }
    /**
     * Affecte la valeur de l'attribut javaClassStrings.
     * @param javaClassStrings l'attribut javaClassStrings à affecter
     */
    public void setJavaClassStrings(String[] javaClassStrings) {
        this.javaClassStrings = javaClassStrings;
    }
    /**
     * Renvoie la valeur de l'attribut javaClassStrings.
     * @return la valeur de l'attribut javaClassStrings
     */
    public String[] getJavaClassStrings() {
        return javaClassStrings;
    }
}
