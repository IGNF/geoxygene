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

package fr.ign.cogit.appli.geopensim.appli.peuplement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypeMethode;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypePeuplement;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.feature.meso.ClasseUrbaine;

import org.apache.log4j.Logger;

/**
 * @author Florence Curie
 *
 */
public class LienPeuplementTypeFonctionnel extends JFrame implements ActionListener{

	private static final long serialVersionUID = -2410212071568927811L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LienPeuplementTypeFonctionnel.class.getName());
	private JButton boutonFermeture, boutonAjouter,boutonSupprimer,boutonModifier;
	private JComboBox comboTypeFonctionnel;
	private JTable table = null;
	private String[] columnNames = {"Type ","Pourcentage","Méthodes"};
	private JScrollPane scrollPane = null;
	private List<TypeMethode> listeTM;
	private List<TypePeuplement> listeTP;
	private JLabel sommeFrequence;

	// Constructeur
	@SuppressWarnings("serial")
	public LienPeuplementTypeFonctionnel(){

		super();
		// La fenêtre
		this.setTitle("Lien entre les méthodes de peuplement et les types fonctionnels objectifs");
		this.setBounds(50, 100, 450, 400);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(GeOpenSimApplication.class.getResource("/geopensim-icon.gif")).getImage());
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				 // Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}
		});
		
		// Le type fonctionnel objectif
		Box hBoxTypeFonctionnel1 = Box.createHorizontalBox();
		hBoxTypeFonctionnel1.add(new JLabel("Type fonctionnel objectif "));
		hBoxTypeFonctionnel1.add(Box.createHorizontalGlue());
		Field[] fields = ClasseUrbaine.class.getDeclaredFields();
		String[] typeFonct = new String[fields.length];
		for (int i=0;i<fields.length;i++){
			typeFonct[i] = fields[i].getName();
		}
		comboTypeFonctionnel = new JComboBox(typeFonct);
		comboTypeFonctionnel.setMaximumSize(comboTypeFonctionnel.getPreferredSize());	
		comboTypeFonctionnel.addActionListener(this);
		Box hBoxTypeFonctionnel2 = Box.createHorizontalBox();
		hBoxTypeFonctionnel2.add(comboTypeFonctionnel);
		hBoxTypeFonctionnel2.add(Box.createHorizontalGlue());
		
		// La liste des paramètres sélectionnés
		Box hBoxRecap1 = Box.createHorizontalBox();
		hBoxRecap1.add(new JLabel("Paramètres sélectionnés : "));
		hBoxRecap1.add(Box.createHorizontalGlue());
		
		// Boite de modification
		Box hBoxModification = Box.createHorizontalBox();
		boutonAjouter = new JButton("Ajouter");
		boutonAjouter.addActionListener(this);
		hBoxModification.add(boutonAjouter);
		hBoxModification.add(Box.createHorizontalStrut(10));
		boutonModifier = new JButton("Modifier");
		boutonModifier.addActionListener(this);
		hBoxModification.add(boutonModifier);
		hBoxModification.add(Box.createHorizontalStrut(10));
		boutonSupprimer = new JButton("Supprimer");
		boutonSupprimer.addActionListener(this);
		hBoxModification.add(boutonSupprimer);
		hBoxModification.add(Box.createHorizontalGlue());

		// La somme des fréquences
		Box hBoxSommeFrequence = Box.createHorizontalBox();
		hBoxSommeFrequence.add(new JLabel("Somme des fréquences : "));
		sommeFrequence = new JLabel("0.0");
		hBoxSommeFrequence.add(sommeFrequence);
		hBoxSommeFrequence.add(Box.createHorizontalGlue());
		
		// Création de la JTable
		Object[][] data = null;
		DefaultTableModel dm = new DefaultTableModel(data,columnNames) {
			@SuppressWarnings("unchecked")
			public Class getColumnClass(int columnIndex) {
				return String.class;
			}
		};
		table = new JTable(dm);
	    table.setDefaultRenderer(String.class, new MultiLineCellRenderer());
	    affichageTable();
		Box hBoxRecap2 = Box.createHorizontalBox();
		scrollPane = new JScrollPane();
	    scrollPane.setViewportView(table);
		hBoxRecap2.add(scrollPane);
		
		// Panneau 
		Box vBoxPanneau = Box.createVerticalBox();
		vBoxPanneau.add(hBoxTypeFonctionnel1);
		vBoxPanneau.add(Box.createVerticalStrut(5));
		vBoxPanneau.add(hBoxTypeFonctionnel2);
		vBoxPanneau.add(Box.createVerticalStrut(10));
		vBoxPanneau.add(hBoxRecap2);
		vBoxPanneau.add(Box.createVerticalStrut(10));
		vBoxPanneau.add(hBoxModification);
		vBoxPanneau.add(Box.createVerticalStrut(10));
		vBoxPanneau.add(hBoxSommeFrequence);

		vBoxPanneau.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneau = new JPanel();
		panneau.setLayout(new javax.swing.BoxLayout(panneau, BoxLayout.Y_AXIS));
		panneau.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneau.add(vBoxPanneau);
		
		// Bouton de fermeture
		Box hBoxFermeture = Box.createHorizontalBox();
		hBoxFermeture.add(Box.createHorizontalGlue());
		boutonFermeture = new JButton("Fermer");
		boutonFermeture.addActionListener(this);
		hBoxFermeture.add(boutonFermeture);
		
		// L'agencement final
		Box vBoxFinal = Box.createVerticalBox();
		vBoxFinal.add(panneau);
		vBoxFinal.add(Box.createVerticalStrut(20));
		vBoxFinal.add(hBoxFermeture);
		vBoxFinal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenu.add(vBoxFinal,BorderLayout.CENTER);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame fenetre1 = new LienPeuplementTypeFonctionnel();
		fenetre1.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonFermeture)){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource()==comboTypeFonctionnel){
			affichageTable();
		}else if(e.getSource()==boutonAjouter){
			CreationTypePeuplementDialog fenetre0 = new CreationTypePeuplementDialog(this);
			TypePeuplement typeP = fenetre0.getTypePeuplement();
			if(typeP!=null){
				// On ajoute ce nouveau type de peuplement à la table
				ajouteLigne(typeP, table.getRowCount());			
				// On enregistre dans le xml
				ConfigurationLienTypeFonctionnelMethodePeuplement configuration = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
				if(listeTP==null){
					listeTP = new ArrayList<TypePeuplement>();
				}
				listeTP.add(typeP);
				configuration.getListType().put((String) comboTypeFonctionnel.getSelectedItem(), listeTP);
				configuration.marshall();
				// Boutons de modification et de suppression
				if(listeTP.size()>0){
					if(table.getSelectedRow()==-1){
						table.getSelectionModel().setSelectionInterval(0, 0);
					}
					boutonModifier.setEnabled(true);
					boutonSupprimer.setEnabled(true);
				}
				// MAJ de la somme des fréquences
				if (typeP.getFrequence()!=-1){
					double somme = Double.parseDouble(sommeFrequence.getText())+typeP.getFrequence();
					sommeFrequence.setText(String.valueOf(somme));
					// Si la somme des fréquences est supérieure à 100% 
					if (somme>100){
						JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de peuplement", 
								"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}else if (e.getSource()==boutonModifier){
			int index = table.getSelectedRow();
			if (index!=-1){
				TypePeuplement typeP = listeTP.get(index);
				CreationTypePeuplementDialog fenetre0 = new CreationTypePeuplementDialog(this,typeP);
				TypePeuplement typeP1 = fenetre0.getTypePeuplement();
				// On remplace les valeurs du type de peuplement dans la table
				((DefaultTableModel)table.getModel()).setValueAt(typeP1.getFrequence(), index, 1);
				((DefaultTableModel)table.getModel()).setValueAt(creationChaine(typeP1), index, 2);
				// On enregistre dans le xml
				ConfigurationLienTypeFonctionnelMethodePeuplement configuration = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
				listeTP.set(index, typeP1);
				configuration.getListType().put((String) comboTypeFonctionnel.getSelectedItem(), listeTP);
				configuration.marshall();
				// MAJ de la somme des fréquences : on recalcule 
				double somme =0;
				for (TypePeuplement type:listeTP){
					if (type.getFrequence()!=-1){
						somme+=type.getFrequence();
					}
				}
				sommeFrequence.setText(String.valueOf(somme));
				// Si la somme des fréquences est supérieure à 100% 
				if (somme>100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
							"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
				}
			}
		}else if(e.getSource()==boutonSupprimer){
			int index = table.getSelectedRow();
			// MAJ de la somme des fréquences
			if (listeTP.get(index).getFrequence()!=-1){
				double somme = Double.parseDouble(sommeFrequence.getText())-listeTP.get(index).getFrequence();
				sommeFrequence.setText(String.valueOf(somme));
			}
			// On supprime l'enregistrement de la table
			if (index!=-1){
				((DefaultTableModel) table.getModel()).removeRow(index);
				table.repaint();
				// On enregistre dans le xml
				ConfigurationLienTypeFonctionnelMethodePeuplement configuration = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
				listeTP.remove(index);
				configuration.getListType().put((String) comboTypeFonctionnel.getSelectedItem(), listeTP);
				configuration.marshall();
				// Boutons de modification et de suppression
				if(listeTP.size()==0){
					boutonModifier.setEnabled(false);
					boutonSupprimer.setEnabled(false);
				}
			}
		}
	}


	public void affichageTable(){
		while(table.getRowCount()>0){
			((DefaultTableModel)table.getModel()).removeRow(0);
		}
		// Lecture du fichier xml contenant les liens
		ConfigurationLienTypeFonctionnelMethodePeuplement configuration = ConfigurationLienTypeFonctionnelMethodePeuplement.getInstance();
		HashMap<String,List<TypePeuplement>> listeMethode = configuration.getListType();
		listeTP = null; 
		for (String nom:listeMethode.keySet()){
			if (nom.equals(comboTypeFonctionnel.getSelectedItem())){
				listeTP = listeMethode.get(nom);
				// On remplit le tableau de données
				if(listeTP!=null){
					for (int i = 0;i<listeTP.size();i++){
						TypePeuplement typePeuplement = listeTP.get(i);
						int ligne =i;
						ajouteLigne(typePeuplement, ligne);
					}
				}
			}
		}
		// Réglage de la taille des colonnes
		TableColumn column = null;
		for (int i=0;i<columnNames.length-1;i++){
			column = table.getColumnModel().getColumn(i);
			column.setMaxWidth(55+i*25);
		}
		// Boutons de modification et de suppression
		if(listeTP!=null){
			table.getSelectionModel().setSelectionInterval(0, 0);
			boutonModifier.setEnabled(true);
			boutonSupprimer.setEnabled(true);
		}else{
			boutonModifier.setEnabled(false);
			boutonSupprimer.setEnabled(false);
		}
		// MAJ de la somme des fréquences
		double somme = 0;
		if(listeTP!=null){
			for (TypePeuplement param :listeTP){
				somme += param.getFrequence();
			}
		}
		if (somme!=-1){
			sommeFrequence.setText(String.valueOf(somme));
			// Si la somme des fréquences est supérieure à 100% 
			if (somme>100){
				JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
						"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	private void ajouteLigne(TypePeuplement typePeuplement,int ligne){
		Object[] obj = creationObjetLigne(typePeuplement, ligne);
		((DefaultTableModel)table.getModel()).addRow(obj);
		int lines = listeTM.size();
	    table.setRowHeight(table.getRowCount()-1, table.getRowHeight(table.getRowCount()-1) * lines);
	}
	
	private Object[] creationObjetLigne(TypePeuplement typePeuplement,int ligne){
		Object[] obj = new Object[3];
		obj[0] = "Type "+(ligne+1);
		obj[1] = typePeuplement.getFrequence();
		obj[2] = creationChaine(typePeuplement);
		return obj;
	}
	
	private String creationChaine(TypePeuplement typePeuplement){
		String str = new String("");
		listeTM = typePeuplement.getParametresPeuplement();
		for (int j=0;j<listeTM.size();j++){
			str += listeTM.get(j).getNomMethodePeuplement() +" ("+ listeTM.get(j).getPourcentage()+"%)"+"\n";
		}
		return str;
	}

	public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

		private static final long serialVersionUID = 3588563258011553925L;
		public MultiLineCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
			setOpaque(true);
		}
		public Component getTableCellRendererComponent(JTable table1, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table1.getSelectionForeground());
				setBackground(table1.getSelectionBackground());
			} else {
				setForeground(table1.getForeground());
				setBackground(table1.getBackground());
			}
			setFont(table1.getFont());
			if (hasFocus) {
				setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
				if (table1.isCellEditable(row, column)) {
					setForeground( UIManager.getColor("Table.focusCellForeground") );
					setBackground( UIManager.getColor("Table.focusCellBackground") );
				}
			} else {
				setBorder(new EmptyBorder(1, 2, 1, 2));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

}
