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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIShapefileChoice;

/**
 * Fenetre utilisée pour le chargement de fichiers shape.
 * @author Julien Perret
 *
 */
public class FrameChargement extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 217165713748917999L;

	static Logger logger=Logger.getLogger(FrameChargement.class.getName());
	
	/**
	 * Liste des fichiers shapefiles chargés.
	 */
	protected List<File> shapeFiles = new ArrayList<File>();
	protected List<Integer> shapeFileLayers = new ArrayList<Integer>();

	/**
	 * Modèle de données pour la table.
	 */
	private TableModel dataModel;
	/**
	 * vrai si le chargement a été valid� (grâce au bouton "Ok").
	 */
	protected boolean validated = false;
	/**
	 * ComboBox utilisée pour �diter les valeurs des types associés aux shapefiles.
	 */
	protected JComboBox comboBox;

	protected List<String> layerNames = new ArrayList<String>();
	
	String title = "Chargeur de GeOxygene";
	/**
	 * @param sld
	 * @throws HeadlessException
	 */
	public FrameChargement(StyledLayerDescriptor sld) throws HeadlessException {
		super();

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setLayout(new BorderLayout());
		setResizable(true);
		setSize(new Dimension(500,500));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setTitle(title);
		setIconImage(InterfaceGeoxygene.getIcone());

		layerNames.add("Nouvelle Couche");
		for (Layer layer:sld.getLayers()) {layerNames.add(layer.getName());}
		this.comboBox = new JComboBox(new Vector<String>(layerNames));
				
		dataModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;
			public int getColumnCount() {return 2;}
			public int getRowCount() {return shapeFiles.size();}
			public Object getValueAt(int row, int col) {
				return (col == 0)?shapeFiles.get(row).getName():shapeFileLayers.get(row);
			}
			@Override
			public boolean isCellEditable(int row, int col) {return (col==1);}
			@Override
			public String getColumnName(int column) {
				return (column == 0)?"Fichier shape":"Couche utilisée";
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
					for (index = 0; index < layerNames.size()&&!found;index++) {
						if (layerNames.get(index).equals(value)) {
							found = true;
							index--;
						}
					}
					shapeFileLayers.set(row, index);
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
		if ((files == null)||(files.length == 0)) {
			return;
		}
		updateLayout(files);
	}

	/**
	 * Ajout de r�pertoires contenant des fichiers shapefiles.
	 * Les r�pertoires sont tous parcourus récursivement et
	 * tous les shapefiles qu'ils contiennent sont ajoutés.
	 */
	private void addDirectories() {
		GUIShapefileChoice sfc = new GUIShapefileChoice(true);
		File[] files = sfc.getSelectedDirectories();
		if ((files == null)||(files.length == 0)) {
			return;
		}
		updateLayoutDirectories(files);
	}

	private void updateLayoutDirectories(File[] files) {
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if (files[i].getName().endsWith(".shp")||files[i].getName().endsWith(".SHP"))
					updateLayout(files[i]);
			} else {
				updateLayoutDirectories(files[i].listFiles());
			}
		}
	}

	/**
	 * Mise à jour de la table avec les fichiers en paramètre.
	 * @param files fichiers à ajouter à la table des fichiers à charger.
	 */
	private void updateLayout(File[] files) {
		for (int i = 0; i < files.length; i++) {
			updateLayout(files[i]);
		}
	}

	/**
	 * Mise à jour de la table avec le shapefile en paramètre.
	 * @param shapefile fichier shapefile à ajouter à la table des fichiers à charger.
	 */
	private void updateLayout(File shapefile) {
		String shapefileName = shapefile.getName();
		String layerName = getLayerNameFromFileName(shapefileName);

		int index = 0;

		if (layerName!=null){
			boolean found = false;
			for (index = 0; index < layerNames.size()&&!found;index++) {
				if (layerNames.get(index).equals(layerName)) {
					if (logger.isTraceEnabled()) logger.trace(shapefileName+" - layer = "+layerName);
					shapeFiles.add(shapefile);
					shapeFileLayers.add(index);
					((AbstractTableModel) dataModel).fireTableDataChanged();
					return;
				}
			}
		}
		shapeFiles.add(shapefile);
		shapeFileLayers.add(0);
		((AbstractTableModel) dataModel).fireTableDataChanged();
	}

	/**
	 * création du JDialog de s�lection des fichiers.
	 * @param parent Frame parent du JDialog à créer.
	 * @return JDialog de s�lection des fichiers shapefiles à charger.
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
		col.setCellRenderer(new MyComboBoxRenderer(layerNames.toArray(new String[0])));
		table.setFillsViewportHeight(true);
		contentPane.add(scrollpane,BorderLayout.LINE_START);

		table.setRowHeight(30);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

		JButton okButton = new JButton("Ok");
		okButton.setActionCommand("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				validated = true;
				dialog.dispose();
			}
		});

		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		Icon iconAddButton = new ImageIcon("images/Plus.png");
		JButton addButton = new JButton("Ajouter des Shapefiles",iconAddButton);
		addButton.setToolTipText("Ajouter des Shapefiles");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addShapeFiles();
			}
		});
		Icon iconAddDirectoryButton = new ImageIcon("images/AjoutRepertoire.png");
		JButton addDirectoryButton = new JButton("Ajouter des r�pertoires contenant des Shapefiles",iconAddDirectoryButton);
		addDirectoryButton.setToolTipText("Ajouter des r�pertoires contenant des Shapefiles");
		addDirectoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDirectories();
			}
		});

		controlPanel.add(addButton);
		controlPanel.add(addDirectoryButton);

		controlPanel.add(okButton);
		controlPanel.add(cancelButton);
		contentPane.add(controlPanel,BorderLayout.PAGE_START);
		
		dialog.pack();
		dialog.setLocationRelativeTo(parent);

		return dialog;
	}

	/**
	 * création du JDialog de s�lection des fichiers.
	 * @return vrai si le JDialog a été valid� (grâce au bouton "Ok"), faux sinon.
	 */
	public boolean showDialog() {
		final JDialog dialog = createDialog(this);
		dialog.setVisible(true);
		dialog.dispose();
		return validated;
	}
	
	/**
	 * TableCellRenderer utilisé pour la colonne contenant les noms des fichiers chargés.
	 */
	class MappingTableCellRenderer extends JLabel implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur.
		 */
		public MappingTableCellRenderer() {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int col) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				if (row%2==0)
					setBackground(new Color(230,230,230));
				else
					setBackground(table.getBackground());
			}
			if (col == 0) {
				setText((String)value);
			}
			return this;
		}
	}

	/**
	 * TableCellRenderer utilisé pour la colonne contenant le type des objets chargés.
	 * Cette colonne est �ditable grace à une comboBox.
	 */
	public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructeur.
		 * @param items liste des valeurs possibles pour la comboBox.
		 */
		public MyComboBoxRenderer(String[] items) {super(items);}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				if (value.equals(new Integer(0))) {
					setBackground(new Color(230,30,30));
				} else {
					if (row%2==0)
						setBackground(new Color(230,230,230));
					else
						setBackground(table.getBackground());
				}
			}
			// Select the current value
			int selectedIndex = ((Integer)value).intValue();
			//if ((selectedIndex<0) || (selectedIndex>=sld.getLayers().size()) || (sld.getLayers().get(selectedIndex)==null)) return this;
			//String layerName = sld.getLayers().get(selectedIndex).getName();
			String layerName = layerNames.get(selectedIndex);
			setSelectedItem(layerName);
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
		public MyComboBoxEditor(String[] items) {
			super(new JComboBox(items));
		}
	}
	/**
	 * Récupère le type d'objets contenus dans le fichier grâce au nom du fichier.
	 * Ce n'est qu'un heuristique simple : on consid�re que le fichier
	 * contient des b�timents si son nom contient "batiment", etc.
	 * @param shapefileName nom d'un fichier shapefile
	 * @return le type java supposé correspondant aux features de ce fichier
	 */
	private String getLayerNameFromFileName(String shapefileName) {
		String fileName = shapefileName.toLowerCase();
		if (fileName.contains("batiment")&&!fileName.contains("lin")&&!fileName.contains("pct")) {
			return "Batiment";
		} else if (fileName.contains("cimetiere")) {
			return "Cimetiere";
		} else if (fileName.contains("parking")) {
			return "Parking";
		} else if (fileName.contains("surface_route")) {
			return "SurfaceRoute";
		} else if (fileName.contains("surface_eau")||fileName.contains("res_hydro_surf")) {
			return "SurfaceEau";
		} else if (fileName.contains("terrain_sport")||fileName.contains("eq_sport_surf")) {
			return "TerrainSport";
		} else if (fileName.contains("troncon_chemin")||fileName.contains("autre_voie_com")) {
			return "TronconChemin";
		} else if (fileName.contains("troncon_cours_eau")||fileName.contains("res_hydro_lin")) {
			return "TronconCoursEau";
		} else if (fileName.contains("point_eau")) {
			return "PointEau";
		} else if (fileName.contains("troncon_route")) {
			return "TronconRoute";
		} else if (fileName.contains("troncon_voie_ferree")) {
			return "TronconVoieFerree";
		} else if (fileName.contains("vegetation")||fileName.contains("occ_sol_surf")||fileName.contains("zone_arboree")) {
			return "Vegetation";
		} else if (fileName.contains("aire_triage")) {
			return "AireTriage";
		} else if (fileName.contains("piste_aerodrome")) {
			return "PisteAerodrome";
		} else if (fileName.contains("ligne_orographique")) {
			return "LigneOrographique";
		} else if (fileName.contains("commune")) {
			return "Commune";
		} else if (fileName.contains("pylone")) {
			return "Pylone";
		} else {
			return null;
		}
	}
}
