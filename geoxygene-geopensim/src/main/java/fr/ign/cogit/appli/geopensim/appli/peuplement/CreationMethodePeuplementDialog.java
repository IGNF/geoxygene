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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Dates;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;


/**
 * @author Florence Curie
 *
 */
public class CreationMethodePeuplementDialog extends JDialog implements ActionListener,FocusListener{
	private static final long serialVersionUID = 7835704040491692178L;
	private JButton boutonValidation, boutonAnnulation, boutonAjout,boutonSuppression,boutonModification;
	private JTextField nomMethode, dateDebut, dateFin;
	private JLabel sommeFrequence;
	private JComboBox comboTypeFonct;
	private JRadioButton boutonORoui,boutonORnon,boutonOBoui,boutonOBnon;
	private String[] typeFonct = {"Quelconque","Public","Industriel","Habitat"};
	private DefaultListModel listModel;
	private JList list;
	private List<ParametresForme> listeForme = new ArrayList<ParametresForme>();
	private int dateD,dateF;
	private double distRMoy,distRET,distBMoy,distBET;
	private static Logger logger=Logger.getLogger(CreationMethodePeuplementDialog.class.getName());
	private String nouveauNom; 
	private String nomCheminFichier="";
	private ParametresMethodesPeuplement methodePeuplement;
	private boolean modifier = false;
	private boolean valider = false;
	private VariabilitePanel panneauDistanceR,panneauDistanceB;
	
	// Constructeur
	public CreationMethodePeuplementDialog(JFrame parent, String nomChemin, String nomMethodePeuplement, ParametresMethodesPeuplement parametresMethode){
		this(parent,nomChemin);
		
		modifier = true;
		// Le nom de la methode
		nomMethode.setText(nomMethodePeuplement);
		// La période
		Dates dates = parametresMethode.getDatesMethode();
		if(dates.getDateDebut()!=-1){dateDebut.setText(((Integer)dates.getDateDebut()).toString());}
		if(dates.getDateFin()!=-1){dateFin.setText(((Integer)dates.getDateFin()).toString());}
		//Le type fonctionnel
		int typeInt = parametresMethode.getTypeFonctionnel();
		String type = "Quelconque";
		if (typeInt==0) type = "Quelconque";
		if (typeInt==1) type = "Public";
		if (typeInt==2) type = "Habitat";
		if (typeInt==3) type = "Industriel";
		comboTypeFonct.setSelectedItem(type);
		
		// Remplissage du panneau distance à la route
		panneauDistanceR.setDistribution(parametresMethode.getDistanceRoute());

		// Remplissage du panneau distance aux autres batiments
		panneauDistanceB.setDistribution(parametresMethode.getDistanceBatiment());

		// L'orientation par rapport à la route
		boolean orientR = parametresMethode.getParalleleRoute();
		if (orientR==true){boutonORoui.setSelected(true);}
		else{boutonORnon.setSelected(true);}
		// L'orientation par rapport aux autres bâtiments
		boolean orientB = parametresMethode.getParalleleBatiment();
		if (orientB==true){boutonOBoui.setSelected(true);}
		else{boutonOBnon.setSelected(true);}
		// les formes de bâtiments
		// Ajout de la forme à la liste
		int index = listModel.size();
		double somme = 0;
		for (ParametresForme param :parametresMethode.getFormeBatiment()){
			String strAffich = creationString(param);
			listModel.addElement(strAffich);
			listeForme.add(param);
			somme += param.getFrequence();
		}
		//Selection du nouvel item et visibilité
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
        if (listModel.size()>0){
        	 boutonSuppression.setEnabled(true);
        	 boutonModification.setEnabled(true);
        }
        // MAJ de la somme des fréquences
		if (somme!=-1){
			sommeFrequence.setText(String.valueOf(somme));
			// Si la somme des fréquences est supérieure à 100% 
			if (somme>100){
				JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
						"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
			}
		}
		
	}

	
	// Constructeur
	public CreationMethodePeuplementDialog(JFrame parent, String nomChemin){

		// La fenêtre
		super (parent,"Ajout d'une Méthode de peuplement",true);
		nomCheminFichier = nomChemin;
		this.setBounds(500, 100, 450, 690);
		this.setResizable(false);
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);	
				dispose();
			}
		});

		// le nom de la Méthode 
		nomMethode = new JTextField(20);
		nomMethode.setMaximumSize(nomMethode.getPreferredSize());		
		nomMethode.addFocusListener(this);
		
		Box hBoxNomMethode = Box.createHorizontalBox();
		hBoxNomMethode.add(new JLabel("Nom de la Méthode : "));
		hBoxNomMethode.add(nomMethode);
		hBoxNomMethode.add(Box.createHorizontalGlue());

		// La période
		dateDebut = new JTextField(5);
		dateDebut.setMaximumSize(dateDebut.getPreferredSize());	
		dateDebut.addFocusListener(this);
		dateFin = new JTextField(5);
		dateFin.setMaximumSize(dateFin.getPreferredSize());		
		dateFin.addFocusListener(this);

		Box hBoxPeriode = Box.createHorizontalBox();
		hBoxPeriode.add(new JLabel("période allant de : "));
		hBoxPeriode.add(dateDebut);
		hBoxPeriode.add(new JLabel(" à "));
		hBoxPeriode.add(dateFin);
		hBoxPeriode.add(Box.createHorizontalGlue());

		// Le type fonctionnel	
		comboTypeFonct = new JComboBox(typeFonct);
		comboTypeFonct.setMaximumSize(comboTypeFonct.getPreferredSize());	
		
		Box hBoxTypeFonctionnel = Box.createHorizontalBox();
		hBoxTypeFonctionnel.add(new JLabel("Type fonctionnel des bâtiments : "));
		hBoxTypeFonctionnel.add(comboTypeFonct);
		hBoxTypeFonctionnel.add(Box.createHorizontalGlue());

		// La distance à la route
		panneauDistanceR = new VariabilitePanel(this," Distance à la route la plus proche ");
		Box hBoxDistanceRoute = Box.createHorizontalBox();
		hBoxDistanceRoute.add(panneauDistanceR);
		
		// La distance aux autres batiments
		panneauDistanceB = new VariabilitePanel(this," Distance inter bâtiment ");
		Box hBoxDistanceBatiment = Box.createHorizontalBox();
		hBoxDistanceBatiment.add(panneauDistanceB);

		// L'orientation du bâtiment
		JPanel panneauOrientation = new JPanel();
		panneauOrientation.setLayout(new javax.swing.BoxLayout(panneauOrientation, BoxLayout.Y_AXIS));
		Border border = BorderFactory.createLineBorder(Color.gray);
		TitledBorder titreOrientation = BorderFactory.createTitledBorder(border, " Orientation du bâtiment ");
		titreOrientation.setTitleJustification(TitledBorder.LEFT);
		panneauOrientation.setBorder(titreOrientation);
		
		// par rapport à la route
		Box hSousBoxOrient1 = Box.createHorizontalBox();
		hSousBoxOrient1.add(Box.createHorizontalStrut(10));
		hSousBoxOrient1.add(new JLabel(" bâtiment parallèle à la route : "));
		ButtonGroup groupeBoutonsOR = new ButtonGroup();
		boutonORoui = new JRadioButton("Oui",true);
		hSousBoxOrient1.add(boutonORoui);
		groupeBoutonsOR.add(boutonORoui);
		hSousBoxOrient1.add(Box.createHorizontalStrut(10));
		boutonORnon = new JRadioButton("Non",false);
		hSousBoxOrient1.add(boutonORnon);
		groupeBoutonsOR.add(boutonORnon);
		hSousBoxOrient1.add(Box.createHorizontalGlue());
		panneauOrientation.add(hSousBoxOrient1);
		// par rapport aux autres bâtiments
		Box hSousBoxOrient2 = Box.createHorizontalBox();
		hSousBoxOrient2.add(Box.createHorizontalStrut(10));
		hSousBoxOrient2.add(new JLabel(" bâtiment parallèle aux autres bâtiments : "));
		ButtonGroup groupeBoutonsOB = new ButtonGroup();
		boutonOBoui = new JRadioButton("Oui",true);
		hSousBoxOrient2.add(boutonOBoui);
		groupeBoutonsOB.add(boutonOBoui);
		hSousBoxOrient2.add(Box.createHorizontalStrut(10));
		boutonOBnon = new JRadioButton("Non",false);
		hSousBoxOrient2.add(boutonOBnon);
		groupeBoutonsOB.add(boutonOBnon);
		hSousBoxOrient2.add(Box.createHorizontalGlue());
		panneauOrientation.add(hSousBoxOrient2);
		
		Box hBoxOrientation = Box.createHorizontalBox();
		hBoxOrientation.add(panneauOrientation);


		// Ajouter / supprimer une forme
		boutonAjout = new JButton("ajouter");
		boutonAjout.addActionListener(this);
		boutonSuppression = new JButton("supprimer");
		boutonSuppression.addActionListener(this);
		boutonSuppression.setEnabled(false);
		boutonModification = new JButton("modifier");
		boutonModification.addActionListener(this);
		boutonModification.setEnabled(false);
		Box hBoxAjoutSupprForme = Box.createHorizontalBox();
		hBoxAjoutSupprForme.add(new JLabel("Forme de bâtiments : "));
		hBoxAjoutSupprForme.add(boutonAjout);
		hBoxAjoutSupprForme.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprForme.add(boutonSuppression);
		hBoxAjoutSupprForme.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprForme.add(boutonModification);
		hBoxAjoutSupprForme.add(Box.createHorizontalGlue());
		
		// Liste des formes
		listModel = new DefaultListModel();
		list = new JList(listModel);
		JScrollPane defil = new JScrollPane(list);
		Box hBoxListeForme = Box.createHorizontalBox();
		hBoxListeForme.add(defil);

		// La somme des fréquences
		Box hBoxSommeFrequence = Box.createHorizontalBox();
		hBoxSommeFrequence.add(new JLabel("Somme des fréquences : "));
		sommeFrequence = new JLabel("0.0");
		hBoxSommeFrequence.add(sommeFrequence);
		hBoxSommeFrequence.add(Box.createHorizontalGlue());
		
		// Deux boutons
		boutonValidation = new JButton("valider");
		boutonValidation.addActionListener(this);
		boutonAnnulation = new JButton("annuler");
		boutonAnnulation.addActionListener(this);
		Box hBoxValidAnnul = Box.createHorizontalBox();
		hBoxValidAnnul.add(Box.createHorizontalGlue());
		hBoxValidAnnul.add(boutonValidation);
		hBoxValidAnnul.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxValidAnnul.add(boutonAnnulation);
		
		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
        vBox.add(hBoxNomMethode);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxPeriode);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxTypeFonctionnel);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxDistanceRoute);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxDistanceBatiment);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxOrientation);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxAjoutSupprForme);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxListeForme);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxSommeFrequence);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxValidAnnul);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource()==this.nomMethode){
			// Le nom de la nouvelle Méthode de peuplement
			boolean verif = true;
			nouveauNom = this.nomMethode.getText();
			ConfigurationMethodesPeuplement config = new ConfigurationMethodesPeuplement();
			if (nomCheminFichier.isEmpty()){
				config = ConfigurationMethodesPeuplement.getInstance();
			}else{
				config = ConfigurationMethodesPeuplement.getInstance(nomCheminFichier);
			}
			while(verif){
				// On vérifie que le nom convient bien si il n'est pas vide
				if (!this.nomMethode.getText().isEmpty()){
					// On modifie le nom
					nouveauNom = this.nomMethode.getText();
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
					this.nomMethode.setText(nouveauNom);
					// On vérifie que le nom n'existe pas déjà dans la BD
					if (!modifier){
						if(config.getListType().keySet().contains(this.nomMethode.getText())){
							// Message d'erreur
							String nom = "";
							while(nom.isEmpty()){
								nom = JOptionPane.showInputDialog(null, "entrez un nom pour la nouvelle Méthode", "Ce nom existe déjà", JOptionPane.QUESTION_MESSAGE);
							}
							this.nomMethode.setText(nom);
							verif = true;
						}
					}
				}else{
					verif=false;
				}
			}
		}else if (e.getSource()==this.dateDebut){
			// La date de départ de cette Méthode
			dateD = -1;
			String strDateD1 = "La date de début entrée n'est pas un entier";
			String strDateD2 = "Voulez vous entrer une autre date de début ?";
			String strDateD3 = "Entrez une nouvelle date de Début pour la méthode";
			dateD = CreationMethodePeuplementDialog.verifInteger(this.dateDebut, strDateD1, strDateD2, strDateD3);
		}else if (e.getSource()==this.dateFin){
			// La date de fin de cette Méthode
			dateF = -1;
			String strDateF1 = "La date de fin entrée n'est pas un entier";
			String strDateF2 = "Voulez vous entrer une autre date de fin ?";
			String strDateF3 = "Entrez une nouvelle date de fin pour la méthode";
			dateF = CreationMethodePeuplementDialog.verifInteger(this.dateFin, strDateF1, strDateF2, strDateF3);
		}
	}
	
	public Map<String,ParametresMethodesPeuplement> getMethode(){
		Map<String,ParametresMethodesPeuplement> methode = new HashMap<String,ParametresMethodesPeuplement>();
		if (valider){
			methode.put(nouveauNom, methodePeuplement);
		}
		return methode;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==this.boutonValidation){
			
			// déserialisation du fichier de configuration des Méthodes de peuplement
			ConfigurationMethodesPeuplement config = new ConfigurationMethodesPeuplement();
			if (nomCheminFichier.isEmpty()){
				config = ConfigurationMethodesPeuplement.getInstance();
			}else{
				config = ConfigurationMethodesPeuplement.getInstance(nomCheminFichier);
			}
			// Le nom de la nouvelle Méthode de peuplement
			boolean verif = true;
			boolean vide = false;
			nouveauNom = this.nomMethode.getText();
			int reponse=1;
			while(verif){
				// On vérifie que le nom n'est pas vide
				if (this.nomMethode.getText().isEmpty()){
					vide = true;
					String nom = "";
					while(nom.isEmpty()){
						nom = JOptionPane.showInputDialog(null, "Entrez un nom pour la nouvelle Méthode", "Nom de Méthode obligatoire", JOptionPane.WARNING_MESSAGE);
						if(nom==null){
							nom = "";
							reponse = JOptionPane.showConfirmDialog(null,"Voulez vous annuler la création de la nouvelle Méthode ?","Nom de Méthode obligatoire", JOptionPane.YES_NO_OPTION);
							if (reponse==0){
								reponse=1;
								return;
							}
						}
					}
					this.nomMethode.setText(nom);	 
				}
				// On modifie le nom
				nouveauNom = this.nomMethode.getText();
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
				this.nomMethode.setText(nouveauNom);
				// On vérifie que le nom n'existe pas déjà dans la BD
				if (!modifier){
					if(config.getListType().keySet().contains(this.nomMethode.getText())){
						// Message d'erreur
						String nom = "";
						while(nom.isEmpty()){
							nom = JOptionPane.showInputDialog(null, "entrez un nom pour la nouvelle Méthode", "Ce nom existe déjà", JOptionPane.QUESTION_MESSAGE);
						}
						this.nomMethode.setText(nom);
						verif = true;
					}
				}
			}
			if(vide==true)return;
			
			// La date de départ de cette Méthode
			if(this.dateDebut.getText().isEmpty()){dateD=-1;}
			else{dateD = Integer.parseInt(this.dateDebut.getText());}
			
			// La date de fin de cette Méthode
			if(this.dateFin.getText().isEmpty()){dateF=-1;}
			else{dateF = Integer.parseInt(this.dateFin.getText());}
			
			// Le type fonctionnel des bâtiments
			String type = this.typeFonct[this.comboTypeFonct.getSelectedIndex()];
			int typeInt = 0;
			if (type.equals("Quelconque")) typeInt = 0;
			if (type.equals("Public")) typeInt = 1;
			if (type.equals("Habitat")) typeInt = 2;
			if (type.equals("Industriel")) typeInt = 3;
			
			// L'orientation par rapport à la route
			boolean orientR = true;
			if (boutonORnon.isSelected()){orientR = false;}
			
			// L'orientation par rapport aux autres bâtiments
			boolean orientB = true;
			if (boutonOBnon.isSelected()){orientB = false;}

			// La distance moyenne à la route
			Distribution distributionDistanceR = panneauDistanceR.getDistribution();
		
			// La distance moyenne aux autres bâtiments
			Distribution distributionDistanceB = panneauDistanceB.getDistribution();

			// vérification de la somme des fréquences des différents types
			double somme = Double.parseDouble(sommeFrequence.getText());
			int nbAvecFreq = 0;
			for (ParametresForme forme:listeForme){
				if(forme.getFrequence()!=-1){
					nbAvecFreq++;
				}
			}
			
			// Tous les types ont une fréquence associée
			if((nbAvecFreq==listeForme.size())&&(listeForme.size()!=0)){
				if (somme>100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
							"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
					return;
				}else if (somme<100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou ajouter un type de bâtiment", 
							"la somme des fréquences est inférieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
					return;
				}
			// Tous les types n'ont pas une fréquence associée
			}else if (nbAvecFreq>0){
				if (somme>=100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
							"la somme des fréquences est supérieure ou égale à 100 % : ", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			
			// Création d'une nouvelle Méthode de peuplement
			methodePeuplement = new ParametresMethodesPeuplement(new Dates(dateD,dateF),typeInt,listeForme,orientR,orientB,distributionDistanceR,distributionDistanceB);
			RecapitulatifMethodeDialog fenetre3 = new RecapitulatifMethodeDialog(this,nouveauNom,methodePeuplement);
			boolean rep = fenetre3.getReponse();
			if (rep==false) return;
			
			// Affichage
			if (logger.isDebugEnabled()){
				logger.debug("Création/modification d'une méthode de peuplement :");
				
				logger.debug("Nom de la méthode : " + nouveauNom);
				logger.debug("Période : " + dateD +" - "+dateF);
				logger.debug("Type fonctionnel : " + type);
				logger.debug("Type de bâtiments : ");
				for (ParametresForme forme:listeForme){
					String strAffich = creationString(forme);
					logger.debug("     - "+strAffich);
				}
				logger.debug("parallèle à la route : " + orientR);
				logger.debug("parallèle aux autres bâtiments : " + orientB);
				logger.debug("Distance moyenne par rapport à la route : " + distRMoy);
				logger.debug("Ecart Type de la distance par rapport à la route : " + distRET);
				logger.debug("Distance moyenne aux autres bâtiments : " + distBMoy);
				logger.debug("Ecart Type de la distance aux autres bâtiments : " + distBET);
			}
			valider = true;
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
			
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonAjout){
			ParametresFormeDialog fenetre2 = new ParametresFormeDialog(this);
			ParametresForme param = fenetre2.getParametres();
			if (param != null){
				// Ajout de la forme à la liste
				int index = listModel.size();
				String strAffich = creationString(param);
			    listModel.insertElementAt(strAffich,index);
				listeForme.add(param);
				//Selection du nouvel item et visibilité
	            list.setSelectedIndex(index);
	            list.ensureIndexIsVisible(index);
	            if (listModel.size()>0){
	            	 boutonSuppression.setEnabled(true);
	            	 boutonModification.setEnabled(true);
	            }
	            // MAJ de la somme des fréquences
				if (param.getFrequence()!=-1){
					double somme = Double.parseDouble(sommeFrequence.getText())+param.getFrequence();
					sommeFrequence.setText(String.valueOf(somme));
					// Si la somme des fréquences est supérieure à 100% 
					if (somme>100){
						JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
								"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}else if (e.getSource()==this.boutonSuppression){
			 int index = list.getSelectedIndex();
			 // MAJ de la somme des fréquences
			 if (listeForme.get(index).getFrequence()!=-1){
				 double somme = Double.parseDouble(sommeFrequence.getText())-listeForme.get(index).getFrequence();
				 sommeFrequence.setText(String.valueOf(somme));
			 }
	         // Suppresion de l'élément
	         listModel.remove(index);
	         listeForme.remove(index);
	         	         
	         if (listModel.size() == 0) {// Si la liste est vide on désactive le bouton supprimer 
	        	 boutonSuppression.setEnabled(false);
	         }else{// On sélectionne un élément
	        	 if (index == listModel.getSize()) {index--;}
	        	 list.setSelectedIndex(index);
	        	 list.ensureIndexIsVisible(index);
	         }
		}else if (e.getSource()==this.boutonModification){
			int index = list.getSelectedIndex();
			ParametresFormeDialog fenetre2 = new ParametresFormeDialog(this,listeForme.get(index));
			ParametresForme param = fenetre2.getParametres();
			if (param!=null){
				listeForme.set(index, param);
				String strAffich = creationString(param);
				// Modification de la liste des formes
				listModel.setElementAt(strAffich,index);
				// MAJ de la somme des fréquences : on recalcule 
				double somme =0;
				for (ParametresForme forme:listeForme){
					if (forme.getFrequence()!=-1){
						somme+=forme.getFrequence();
					}
				}
				sommeFrequence.setText(String.valueOf(somme));
				// Si la somme des fréquences est supérieure à 100% 
				if (somme>100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
							"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

	protected static Double verifDouble(JTextField textField,String str1,String str2,String str3){
		double valeurDouble = -1;
		if (!textField.getText().isEmpty()){
			try {
				valeurDouble = Double.parseDouble(textField.getText());
			} catch (Exception e2) {
				logger.error(str1);
				int rep = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
				String valeurString = "";
				int rep2 = 0;
				while ((rep==0)&&(valeurDouble==-1)&&(rep2==0)){
					valeurString = JOptionPane.showInputDialog(null,str3,str1, JOptionPane.QUESTION_MESSAGE);
					try {
						valeurDouble = Double.parseDouble(valeurString);
					} catch (Exception e21) {
						logger.error(str1);
					}
					if(valeurString==null){
						rep2 = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
					}
				}
				if (valeurDouble==-1){textField.setText("");}
				else {textField.setText(valeurString);}
			}
		}
		return valeurDouble;
	}
	
	protected static int verifInteger(JTextField textField,String str1,String str2,String str3){
		int valeurInt = -1;
		if (!textField.getText().isEmpty()){
			try {
				valeurInt = Integer.parseInt(textField.getText());
			} catch (Exception e2) {
				logger.error(str1);
				int rep = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
				String valeurString = "";
				int rep2 = 0;
				while ((rep==0)&&(valeurInt==-1)&&(rep2==0)){
					valeurString = JOptionPane.showInputDialog(null,str3,str1, JOptionPane.QUESTION_MESSAGE);
					try {
						valeurInt = Integer.parseInt(valeurString);
					} catch (Exception e21) {
						logger.error(str1);
					}
					if(valeurString==null){
						rep2 = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
					}
				}
				if (valeurInt==-1){textField.setText("");}
				else {textField.setText(valeurString);}
			}
		}
		return valeurInt;
	}

	public String creationString (ParametresForme param){
		
		// La forme
		String strForme = "[Batiment "+param.getForme()+"] :";
		// L'aire
		String strAire = " aire = ";
		if ((param.getTailleBatiment().getMinimum()!=-1)||(param.getTailleBatiment().getMaximum()!=-1)){
			strAire = strAire+" de "+param.getTailleBatiment().getMinimum()+" à "+param.getTailleBatiment().getMaximum();
		}else{
			strAire = strAire+param.getTailleBatiment().getMoyenne()+" (+/- "+param.getTailleBatiment().getEcartType()+")";
		}
		// L'élongation
		String strElongation = " - elong = ";
		if ((param.getElongationBatiment().getMinimum()!=-1)||(param.getElongationBatiment().getMaximum()!=-1)){
			strElongation = strElongation+" de "+param.getElongationBatiment().getMinimum()+" à "+param.getElongationBatiment().getMaximum();
		}else{
			strElongation = strElongation+param.getElongationBatiment().getMoyenne()+" (+/- "+param.getElongationBatiment().getEcartType()+")";
		}
		// L'épaisseur
		String strEpaisseur = " - epaiss = ";
		if ((param.getEpaisseurBatiment().getMinimum()!=-1)||(param.getEpaisseurBatiment().getMaximum()!=-1)){
			strEpaisseur = strEpaisseur+" de "+param.getEpaisseurBatiment().getMinimum()+" à "+param.getEpaisseurBatiment().getMaximum();
		}else{
			strEpaisseur = strEpaisseur+param.getEpaisseurBatiment().getMoyenne()+" (+/- "+param.getEpaisseurBatiment().getEcartType()+")";
		}
		// La fréquence
		String strFrequence = " - freq = "+param.getFrequence();
		
		return strForme + strAire + strElongation + strEpaisseur + strFrequence;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
