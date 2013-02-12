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

package fr.ign.cogit.appli.geopensim.appli.comparaison;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import fr.ign.cogit.appli.geopensim.ConfigurationComparaison;
import fr.ign.cogit.appli.geopensim.ConfigurationComparaison.ParametresComparaison;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;

import org.apache.log4j.Logger;

/**
 * @author Florence Curie
 *
 */
public class ChoixParametresComparaison extends JFrame implements ActionListener{


	private static final long serialVersionUID = -2410212071568927811L;
	private static final Logger logger = Logger.getLogger(ChoixParametresComparaison.class.getName());
	private JButton boutonValidation, boutonAnnulation, boutonVersDroite, boutonVersGauche;
	private JTextField ponderation,nomParametrage;
	private JComboBox comboTypeOperation;
	private JTable table = null;
	private String[] columnNames = {"Paramètre","Opération","Pondération"};
	private Object[][] data = null;
	private JScrollPane scrollPane = null;
	private DefaultListModel listModel;
	private List<Method> listeMethodes;
	private JList list;
	private String nouveauNom;
	private boolean modifier = false;
	
	// Constructeur
	@SuppressWarnings("serial")
	public ChoixParametresComparaison(){

		super();
		// La fenêtre
		this.setTitle("Choix des paramètres de comparaison");
		this.setBounds(50, 100, 800, 400);
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

		// Panneau de gauche
		// La liste des paramètres possibles
		Box hBoxListe = Box.createHorizontalBox();
		hBoxListe.add(new JLabel("Liste des paramètres : "));
		hBoxListe.add(Box.createHorizontalGlue());
		// Création de la liste des méthodes
		listModel = new DefaultListModel();
		listeMethodes = new ArrayList<Method>();
		Method[] methods = ZoneElementaireUrbaine.class.getMethods();
		for (int i=0;i<methods.length;i++){
			Method methode = methods[i];
			if (methode.getName().startsWith("get")){
				Class<?> returnType = methode.getReturnType();
				String valueType = returnType.getSimpleName();
				if (returnType.isPrimitive()) {
					if ((valueType.equals("int"))||(valueType.equals("double"))) {
						listModel.addElement(methode.getName().replaceFirst("get", ""));
						listeMethodes.add(methode);
						logger.debug(methode.getName());
					}
				}
			}
		}
		list = new JList(listModel);
		JScrollPane defil = new JScrollPane(list);
		defil.setMinimumSize(new Dimension(300, 150));
		Box hBoxListeMethode = Box.createHorizontalBox();
		hBoxListeMethode.add(defil);
		
		// Le type d'opération à effectuer
		Box hBoxTypeOperation1 = Box.createHorizontalBox();
		hBoxTypeOperation1.add(new JLabel("Type d'opération à effectuer "));
		hBoxTypeOperation1.add(Box.createHorizontalGlue());
		String[] typeOp = {"Moyenne","Somme"};
		comboTypeOperation = new JComboBox(typeOp);
		comboTypeOperation.setMaximumSize(comboTypeOperation.getPreferredSize());	
		Box hBoxTypeOperation2 = Box.createHorizontalBox();
		hBoxTypeOperation2.add(comboTypeOperation);
		hBoxTypeOperation2.add(Box.createHorizontalGlue());
		
		// La pondération à appliquer
		Box hBoxPonderation1 = Box.createHorizontalBox();
		hBoxPonderation1.add(new JLabel("Pondération"));
		hBoxPonderation1.add(Box.createHorizontalGlue());
		Box hBoxPonderation2 = Box.createHorizontalBox();
		ponderation = new JTextField(10);
		ponderation.setMaximumSize(ponderation.getPreferredSize());		
		hBoxPonderation2.add(ponderation);
		hBoxPonderation2.add(Box.createHorizontalGlue());
		
		// Panneau de gauche
		Box vBoxGauche = Box.createVerticalBox();
		vBoxGauche.add(hBoxListe);
		vBoxGauche.add(Box.createVerticalStrut(5));
		vBoxGauche.add(hBoxListeMethode);
		vBoxGauche.add(Box.createVerticalStrut(10));
		vBoxGauche.add(hBoxTypeOperation1);
		vBoxGauche.add(Box.createVerticalStrut(5));
		vBoxGauche.add(hBoxTypeOperation2);
		vBoxGauche.add(Box.createVerticalStrut(10));
		vBoxGauche.add(hBoxPonderation1);
		vBoxGauche.add(Box.createVerticalStrut(5));
		vBoxGauche.add(hBoxPonderation2);
		vBoxGauche.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneauGauche = new JPanel();
		panneauGauche.setLayout(new javax.swing.BoxLayout(panneauGauche, BoxLayout.Y_AXIS));
		panneauGauche.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneauGauche.add(vBoxGauche);
		
		// Panneau du milieu
		Box vBoxCentre = Box.createVerticalBox();
		boutonVersDroite = new JButton(">>>");
		boutonVersDroite.addActionListener(this);
		vBoxCentre.add(boutonVersDroite);
		vBoxCentre.add(Box.createVerticalStrut(10));
		boutonVersGauche = new JButton("<<<");
		boutonVersGauche.addActionListener(this);
		vBoxCentre.add(boutonVersGauche);
		
		// La liste des paramètres sélectionnés
		Box hBoxRecap1 = Box.createHorizontalBox();
		hBoxRecap1.add(new JLabel("Paramètres sélectionnés : "));
		hBoxRecap1.add(Box.createHorizontalGlue());
		// Création de la Jtable
		Box hBoxRecap2 = Box.createHorizontalBox();
		data = new Object[0][columnNames.length];
		DefaultTableModel model = new DefaultTableModel(data,columnNames) {
		  @Override
		  public boolean isCellEditable(int row, int column) {return false;}
		};
		table = new JTable(model);
		scrollPane = new JScrollPane();
	    scrollPane.setViewportView(table);
		hBoxRecap2.add(scrollPane);
		
		// Panneau de droite
		Box vBoxDroite = Box.createVerticalBox();
		vBoxDroite.add(hBoxRecap1);
		vBoxDroite.add(Box.createVerticalStrut(10));
		vBoxDroite.add(hBoxRecap2);
		vBoxDroite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new javax.swing.BoxLayout(panneauDroite, BoxLayout.Y_AXIS));
		panneauDroite.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneauDroite.add(vBoxDroite);
		
		// Bouton de validation
		Box hBoxValidation = Box.createHorizontalBox();
		hBoxValidation.add(new JLabel("Nom du nouveau paramétrage : "));
		hBoxValidation.add(Box.createHorizontalStrut(10));
		nomParametrage = new JTextField(20);
		nomParametrage.setMaximumSize(nomParametrage.getPreferredSize());		
		hBoxValidation.add(nomParametrage);
		hBoxValidation.add(Box.createHorizontalGlue());
		boutonValidation = new JButton("Valider");
		boutonValidation.addActionListener(this);
		hBoxValidation.add(boutonValidation);
		hBoxValidation.add(Box.createHorizontalStrut(10));
		boutonAnnulation = new JButton("Annuler");
		boutonAnnulation.addActionListener(this);
		hBoxValidation.add(boutonAnnulation);
		
		// L'agencement des vbox
		Box hBoxHaut = Box.createHorizontalBox();
		hBoxHaut.add(panneauGauche);
		hBoxHaut.add(Box.createHorizontalStrut(10));
		hBoxHaut.add(vBoxCentre);
		hBoxHaut.add(Box.createHorizontalStrut(10));
		hBoxHaut.add(panneauDroite);
		
		// L'agencement final
		Box vBoxFinal = Box.createVerticalBox();
		vBoxFinal.add(hBoxHaut);
		vBoxFinal.add(Box.createVerticalStrut(20));
		vBoxFinal.add(hBoxValidation);
		vBoxFinal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenu.add(vBoxFinal,BorderLayout.CENTER);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame fenetre1 = new ChoixParametresComparaison();
		fenetre1.setVisible(true);
	}

	
	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonValidation)){
			// déserialisation du fichier de configuration 
			ConfigurationComparaison config = new ConfigurationComparaison().getInstance();
			// Le nom de la nouvelle Méthode de peuplement
			boolean verif = true;
			boolean vide = false;
			nouveauNom = this.nomParametrage.getText();
			int reponse=1;
			while(verif){
				// On vérifie que le nom n'est pas vide
				if (this.nomParametrage.getText().isEmpty()){
					vide = true;
					String nom = "";
					while(nom.isEmpty()){
						nom = JOptionPane.showInputDialog(null, "Entrez un nom pour le nouveau paramétrage", "Nom de paramétrage obligatoire", JOptionPane.WARNING_MESSAGE);
						if(nom==null){
							nom = "";
							reponse = JOptionPane.showConfirmDialog(null,"Voulez vous annuler la création d'un nouveau paramétrage ?","Nom de paramétrage obligatoire", JOptionPane.YES_NO_OPTION);
							if (reponse==0){
								reponse=1;
								return;
							}
						}
					}
					this.nomParametrage.setText(nom);	 
				}
				// On modifie le nom
				nouveauNom = this.nomParametrage.getText();
				String[] resultatSplit = nouveauNom.split(" ");
				for (int i=0;i<resultatSplit.length;i++){
					char premierC = resultatSplit[i].charAt(0);
					char data1a[] = {premierC};
					char data2a[] = {Character.toUpperCase(premierC)};
					resultatSplit[i] = resultatSplit[i].replaceFirst(new String(data1a),new String(data2a));
				}
				nouveauNom =resultatSplit[0];
				for (int i=1;i<resultatSplit.length;i++){
					nouveauNom = nouveauNom.concat(resultatSplit[i]);
				}
				verif = false;
				this.nomParametrage.setText(nouveauNom);
				// On vérifie que le nom n'existe pas déjà dans la BD
				if (!modifier){
					if(config.getListType().keySet().contains(this.nomParametrage.getText())){
						// Message d'erreur
						String nom = "";
						while(nom.isEmpty()){
							nom = JOptionPane.showInputDialog(null, "entrez un nom pour le nouveau paramétrage", "Ce nom existe déjà", JOptionPane.QUESTION_MESSAGE);
						}
						this.nomParametrage.setText(nom);
						verif = true;
					}
				}
			}
			if(vide==true)return;
			
			// Sérialisation de la JTable
			List<ParametresComparaison> listeParametresComparaison = new ArrayList<ParametresComparaison>();
			for (int i=0;i<table.getModel().getRowCount();i++){
				String meth = (String) table.getModel().getValueAt(i, 0);
				String typeOp = (String) table.getModel().getValueAt(i, 1);
				double pond = Double.parseDouble((String) table.getModel().getValueAt(i, 2));
				ParametresComparaison param = new ParametresComparaison(meth, typeOp, pond);
				listeParametresComparaison.add(param);
			}
			config.getListType().put(nouveauNom, listeParametresComparaison);
			config.marshall();
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonAnnulation)){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonVersDroite)){
			int index = list.getSelectedIndex();
			if(index!=-1){
				String valPondStr = "0";
				if (!ponderation.getText().isEmpty()){
					try {
						Double.parseDouble(ponderation.getText());
						valPondStr = ponderation.getText();
					}catch (Exception e21) {
						logger.error("la valeur de pondération n'est pas un chiffre");
					}
				}
				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();   
				Object[] obj = new Object[3];
				obj[0] = listeMethodes.get(index).getName();
				obj[1] = comboTypeOperation.getSelectedItem();
				obj[2] = valPondStr;
				((DefaultTableModel) tableModel).addRow(obj);
				table.setModel(tableModel);
				table.repaint();
			}
		}else if(e.getSource().equals(boutonVersGauche)){
			int[] listeIndex = table.getSelectedRows();
			if (listeIndex.length>0){
				for (int i = listeIndex.length-1;i>=0;i--){
					DefaultTableModel tableModel = (DefaultTableModel) table.getModel();   
					tableModel.removeRow(listeIndex[i]);
					table.setModel(tableModel);
				}
				table.repaint();
			}
		}
	}
}


