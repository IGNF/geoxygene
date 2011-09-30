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

package fr.ign.cogit.appli.geopensim.appli.rules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.csv.CSVParser;
import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire.Possibilites;
import fr.ign.cogit.appli.geopensim.feature.meso.ClasseUrbaine;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;


/**
 * @author Florence Curie
 *
 */
public class EvolutionRuleCreationDialog extends JDialog implements ActionListener,FocusListener{
	private static final long serialVersionUID = 7835704040491692178L;
	private JButton boutonValidation, boutonAnnulation;
	private JTextField dateDebut,dateFin,nomRegle;
	private int dateD,dateF;
	private static Logger logger=Logger.getLogger(EvolutionRuleCreationDialog.class.getName());
	private boolean valider = false;
	private EvolutionRule evolutionRule;
	private JComboBox comboProprieteObj = new JComboBox();
	private String[] propObj = {"densiteBut","classificationFonctionnelleBut"};
	private JRadioButton boutonManuel,boutonStatistiques;
	private JTextField nomFichier;
	private JButton boutonChoix;
	private JButton boutonAjouterERU, boutonSupprimerERU, boutonModifierERU;
	private JList list;
	private List<EvolutionRuleUnitaire> listeERU = new ArrayList<EvolutionRuleUnitaire>();
	private DefaultListModel listModel;
	
	// Constructeur
	public EvolutionRuleCreationDialog(JFrame parent, EvolutionRule evolRule){
		this(parent);
		
		// La période
		if(evolRule.getStart()!=-1){dateDebut.setText(((Integer)evolRule.getStart()).toString());}
		if(evolRule.getEnd()!=-1){dateFin.setText(((Integer)evolRule.getEnd()).toString());}
		
		// Le nom de la règle
		if(evolRule.getNom()!=null)nomRegle.setText(evolRule.getNom());
		
		// La propriété objectif
		String propO = evolRule.getPropertyName();
		comboProprieteObj.setSelectedItem(propO);
		
		// Le mode d'entrée
		boutonManuel.setSelected(true);
		boutonChoix.setEnabled(false);
		nomFichier.setEnabled(false);

		// Ajout de la règle d'évolution unitaire à la liste
		int index = listModel.size();
		for (EvolutionRuleUnitaire param : evolRule.getListeEvolutionRuleUnitaire()){
			String strAffich = makeString(param);
			listModel.addElement(strAffich);
			listeERU.add(param);
		}
		//Selection du nouvel item et visibilité
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
        if(listeERU.isEmpty()){
			boutonModifierERU.setEnabled(false);
			boutonSupprimerERU.setEnabled(false);
		}
	}

	// Constructeur
	public EvolutionRuleCreationDialog(JFrame parent){

		// La fenêtre
		super (parent,"Ajout d'une règle d'évolution",true);
		this.setBounds(570, 100, 450, 450);
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

		// Le nom de la règle
		nomRegle = new JTextField(15);
		nomRegle.setMaximumSize(nomRegle.getPreferredSize());
		nomRegle.addFocusListener(this);
		
		Box hBoxNomRegle = Box.createHorizontalBox();
		hBoxNomRegle.add(new JLabel("Nom de la règle : "));
		hBoxNomRegle.add(nomRegle);
		hBoxNomRegle.add(Box.createHorizontalGlue());
		
		// La propriété objectif
		comboProprieteObj = new JComboBox(propObj);
		comboProprieteObj.setMaximumSize(comboProprieteObj.getPreferredSize());	
		
		Box hBoxProprieteObj = Box.createHorizontalBox();
		hBoxProprieteObj.add(new JLabel("Propriété objectif : "));
		hBoxProprieteObj.add(comboProprieteObj);
		hBoxProprieteObj.add(Box.createHorizontalGlue());
		
		// Le mode d'entrée
		JPanel panneauEntree = new JPanel();
		panneauEntree.setLayout(new GridLayout(0, 1));
		Border borderPanneau = BorderFactory.createLineBorder(Color.gray);
		TitledBorder titrePanneau = BorderFactory.createTitledBorder(borderPanneau, " Mode d'entrée ");
		titrePanneau.setTitleJustification(TitledBorder.LEFT);
		panneauEntree.setBorder(titrePanneau);
		ButtonGroup groupeBoutonsEntree = new ButtonGroup();
		
		// statistiques
		Box hSousBoxStatistiques = Box.createHorizontalBox();
		boutonStatistiques = new JRadioButton("statistiques");
		boutonStatistiques.addActionListener(this);
		hSousBoxStatistiques.add(boutonStatistiques);
		groupeBoutonsEntree.add(boutonStatistiques);
		hSousBoxStatistiques.add(Box.createHorizontalGlue());
		
		// Choix d'un fichier
		Box hBoxChoixFichier = Box.createHorizontalBox();
		String nomFich = "";
		nomFichier = new JTextField(50);
		nomFichier.setText(nomFich);
		nomFichier.setEnabled(false);
		nomFichier.setMaximumSize(nomFichier.getPreferredSize());
		hBoxChoixFichier.add(nomFichier);
		hBoxChoixFichier.add(Box.createHorizontalStrut(10));
		boutonChoix = new JButton("Choisir un fichier");
		boutonChoix.setEnabled(false);
		boutonChoix.addActionListener(this);
		hBoxChoixFichier.add(boutonChoix);
		
		// Manuel
		Box hSousBoxVoisinageDens = Box.createHorizontalBox();
		boutonManuel = new JRadioButton("manuelle",true);
		boutonManuel.setSelected(true);
		boutonManuel.addActionListener(this);
		hSousBoxVoisinageDens.add(boutonManuel);
		groupeBoutonsEntree.add(boutonManuel);
		hSousBoxVoisinageDens.add(Box.createHorizontalGlue());
		
		// Liste des régles d'évolution unitaire
		listModel = new DefaultListModel();
		list = new JList(listModel);
		JScrollPane defil = new JScrollPane(list);
		Box hBoxListeERU = Box.createHorizontalBox();
		hBoxListeERU.add(defil);
		
		// Les 3 boutons pour les règles d'évolution unitaire
		boutonAjouterERU = new JButton("ajouter");
		boutonAjouterERU.addActionListener(this);
		boutonSupprimerERU = new JButton("supprimer");
		boutonSupprimerERU.addActionListener(this);
		boutonModifierERU = new JButton("modifier");
		boutonModifierERU.addActionListener(this);
		
		Box hBoxAjoutSupprZone = Box.createHorizontalBox();
		hBoxAjoutSupprZone.add(boutonAjouterERU);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonSupprimerERU);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonModifierERU);
		hBoxAjoutSupprZone.add(Box.createHorizontalGlue());
		
		// Boite verticale dans le panneau 
		Box vBoxZone = Box.createVerticalBox();
		vBoxZone.add(hSousBoxStatistiques);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(hBoxChoixFichier);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(hSousBoxVoisinageDens);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(hBoxListeERU);
		vBoxZone.add(Box.createVerticalStrut(10));
		vBoxZone.add(hBoxAjoutSupprZone);
		vBoxZone.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panneauEntree.add(vBoxZone);
		
		Box hBoxEntree = Box.createHorizontalBox();
		hBoxEntree.add(panneauEntree);
		
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
        vBox.add(hBoxPeriode);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxNomRegle);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxProprieteObj);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxEntree);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxValidAnnul);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	public String makeString(EvolutionRuleUnitaire rule){
		String strAff = "";
		String precond1 = "";
		if(rule.getFilter()!=null && rule.getFilter().length>0){
			if (rule.getFilter()[0]!=null){
				precond1 += " ["+rule.getFilter()[0].toString()+"] ";
			}
		}else{
			precond1 += "pas de précondition";
		}
		strAff += precond1;
		int nbPossib = rule.getPossib().size();
		if(nbPossib>0){
			strAff += " : "+nbPossib+" sous-règle(s)";
		}
		return strAff;
	}
	
	public void focusLost(FocusEvent e) {
		if (e.getSource()==this.dateDebut){
			// La date de départ de cette Méthode
			dateD = -1;
			String strDateD1 = "La date de début entrée n'est pas un entier";
			String strDateD2 = "Voulez vous entrer une autre date de début ?";
			String strDateD3 = "Entrez une nouvelle date de Début pour la méthode";
			dateD = EvolutionRuleCreationDialog.verifInteger(this.dateDebut, strDateD1, strDateD2, strDateD3);
		}else if (e.getSource()==this.dateFin){
			// La date de fin de cette Méthode
			dateF = -1;
			String strDateF1 = "La date de fin entrée n'est pas un entier";
			String strDateF2 = "Voulez vous entrer une autre date de fin ?";
			String strDateF3 = "Entrez une nouvelle date de fin pour la méthode";
			dateF = EvolutionRuleCreationDialog.verifInteger(this.dateFin, strDateF1, strDateF2, strDateF3);
		}
	}
	
	public EvolutionRule getRule(){
		valider = false;
		setVisible(true);
		if (valider) return evolutionRule;
		else return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.boutonChoix){// Choix d'un fichier à compléter
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("fichier CSV", "csv");
			chooser.setFileFilter(filter);
			Properties prop = System.getProperties();
			String currentDirectory = prop.getProperty("user.dir");
			File repCourant = new File(currentDirectory);
			chooser.setCurrentDirectory(repCourant);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                nomFichier.setText(file.toString());
            } 
		}else if(e.getSource()==this.boutonManuel){
			if (boutonManuel.isSelected()){
				// On désactive les composants qui correspondent à l'autre bouton
				boutonChoix.setEnabled(false);
				nomFichier.setEnabled(false);
				// On active les composants qui correspondent à ce bouton 
				list.setEnabled(true);
				boutonAjouterERU.setEnabled(true);
				if(listeERU.isEmpty()){
					boutonSupprimerERU.setEnabled(false);
					boutonModifierERU.setEnabled(false);
				}else{
					boutonSupprimerERU.setEnabled(true);
					boutonModifierERU.setEnabled(true);
				}
			}
		}else if(e.getSource()==this.boutonStatistiques){
			if (boutonStatistiques.isSelected()){
				// On désactive les composants qui correspondent à l'autre bouton
				list.setEnabled(false);
				boutonAjouterERU.setEnabled(false);
				boutonSupprimerERU.setEnabled(false);
				boutonModifierERU.setEnabled(false);
				// On active les composants qui correspondent à ce bouton 
				boutonChoix.setEnabled(true);
				nomFichier.setEnabled(true);
			}
		}else if(e.getSource()==this.boutonValidation){
			
			// Création de la règle d'évolution
			evolutionRule = new EvolutionRule();
			
			// La date de départ de cette règle d'évolution
			if(this.dateDebut.getText().isEmpty()){dateD=-1;}
			else{dateD = Integer.parseInt(this.dateDebut.getText());}
			evolutionRule.setStart(dateD);

			// La date de fin de cette règle d'évolution
			if(this.dateFin.getText().isEmpty()){dateF=-1;}
			else{dateF = Integer.parseInt(this.dateFin.getText());}
			evolutionRule.setEnd(dateF);
			
			// Le nom de la nouvelle règle d'évolution
			boolean verif = true;
			boolean vide = false;
			String nouveauNom = this.nomRegle.getText();
			int reponse=1;
			while(verif){
				// On vérifie que le nom n'est pas vide
				if (this.nomRegle.getText().isEmpty()){
					vide = true;
					String nom = "";
					while(nom.isEmpty()){
						nom = JOptionPane.showInputDialog(null, "Entrez un nom pour la nouvelle règle d'évolution", "Nom de la règle d'évolution obligatoire", JOptionPane.WARNING_MESSAGE);
						if(nom==null){
							nom = "";
							reponse = JOptionPane.showConfirmDialog(null,"Voulez vous annuler la création de la nouvelle règle d'évolution ?","Nom de la règle d'évolution", JOptionPane.YES_NO_OPTION);
							if (reponse==0){
								reponse=1;
								return;
							}
						}
					}
					this.nomRegle.setText(nom);	 
				}
				// On modifie le nom
				nouveauNom = this.nomRegle.getText();
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
				this.nomRegle.setText(nouveauNom);
			}
			if(vide==true)return;
			evolutionRule.setNom(this.nomRegle.getText());
			
			// La propriété objectif
			evolutionRule.setPropertyName(this.comboProprieteObj.getSelectedItem().toString());
			
			// La liste des ERU
			if (boutonManuel.isSelected()){
				evolutionRule.setListeEvolutionRuleUnitaire(listeERU);
			}else if (boutonStatistiques.isSelected()){
				// Lecture d'un fichier csv
				String input = this.nomFichier.getText();
				CSVParser p;
				try {
					p = new CSVParser(new FileReader(input),',');
					String[][]values = p.getAllValues();
					// Création d'une liste de règles unitaires
					PropertyName propertyName = new PropertyName(this.comboProprieteObj.getSelectedItem().toString().replaceAll("But", ""));
                    logger.trace(propertyName);
					List<EvolutionRuleUnitaire> listesub = new ArrayList<EvolutionRuleUnitaire>(0);
					for (int i = 0 ; i<values.length;i++) {
						String valInit=values[i][0];
						logger.trace(valInit);
						EvolutionRuleUnitaire ERU = new EvolutionRuleUnitaire();
						// Création d'un filtre
						Filter[] filter = new Filter[1];
						if (propertyName.getPropertyName().equals("densite")) {
						    PropertyIsGreaterThanOrEqualTo greater = new PropertyIsGreaterThanOrEqualTo();
                            greater.setPropertyName(propertyName);
                            greater.setLiteral(new Literal(valInit));
                            PropertyIsLessThan less = new PropertyIsLessThan();
                            less.setPropertyName(propertyName);
                            less.setLiteral(new Literal("" + (Double.parseDouble(valInit) + 0.05)));
                            And and = new And();
                            and.setOps(new ArrayList<Filter>(2));
                            and.getOps().add(greater);
                            and.getOps().add(less);
                            filter[0] = and;
						} else { // classe
	                        PropertyIsEqualTo equalTo = new PropertyIsEqualTo();
	                        equalTo.setPropertyName(propertyName);
	                        equalTo.setLiteral(new Literal(""+ClasseUrbaine.getValFromSimpleName(valInit)));
	                        filter[0] = equalTo;
						}
                        logger.trace(filter[0]);
						ERU.setFilter(filter);
						List<Possibilites> listePoss = new ArrayList<Possibilites>();
						int sum = 0;
                        for (int j = 1 ; j<values[i].length;j++) {
                            int proba = Integer.parseInt(values[i][j]);
                            sum += proba;
                        }
                        logger.trace("sum = " + sum);
						for (int j = 1 ; j<values[i].length;j++) {
							String valFin=values[j - 1][0];
	                        if (!propertyName.getPropertyName().equals("densite")) {
	                            valFin = "" + ClasseUrbaine.getValFromSimpleName(valFin);
	                        }
                            logger.trace("valFin = " + valFin);
							int proba = Integer.parseInt(values[i][j]);
							if(proba >0){
								Possibilites possib= new Possibilites();
								possib.setPropertyName(this.comboProprieteObj.getSelectedItem().toString());
								possib.setProbability((float) proba / (float) sum);
								Literal literal = new Literal();
								literal.setValue(valFin);
								possib.setExpression(literal);	
								listePoss.add(possib);
	                            logger.trace("new possibility = " + possib.getPropertyName() + " " + possib.getProbability() + " " + possib.getExpression());
							}
						}
						ERU.setPossib(listePoss);
						listesub.add(ERU);
					}
					evolutionRule.setListeEvolutionRuleUnitaire(listesub);
					logger.trace(evolutionRule);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			// Fermeture de la fenêtre
			valider = true;
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonAjouterERU){
			EvolutionRuleUnitaireCreationDialog fenetre2 = new EvolutionRuleUnitaireCreationDialog(this);
			EvolutionRuleUnitaire ERU = fenetre2.getRuleUnit();
			if (ERU!=null){
				// ajout de la nouvelle règle unitaire à la liste
				String strAffich = makeString(ERU);
				logger.info(strAffich);
				// Ajout de la règle d'évolution unitaire à la liste
				int index = listModel.size();
				listModel.insertElementAt(strAffich,index);
				listeERU.add(ERU);
				//Selection du nouvel item et visibilité
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
				if (listModel.size()>0){
					boutonSupprimerERU.setEnabled(true);
					boutonModifierERU.setEnabled(true);
				}
				// réactivation des boutons modifier et supprimer
				boutonModifierERU.setEnabled(true);
				boutonSupprimerERU.setEnabled(true);
			}
		}else if (e.getSource()==this.boutonSupprimerERU){
			int index = list.getSelectedIndex();
			// Suppresion de l'élément
			listModel.remove(index);
			listeERU.remove(index);
			// Si la liste est vide on désactive le bouton supprimer
			if (listModel.size() == 0) { 
				boutonSupprimerERU.setEnabled(false);
				boutonModifierERU.setEnabled(false);
			}else{// sinon on sélectionne un élément
				if (index == listModel.getSize()) {index--;}
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
		}else if (e.getSource()==this.boutonModifierERU){
			int index = list.getSelectedIndex();
			EvolutionRuleUnitaireCreationDialog fenetre2 = new EvolutionRuleUnitaireCreationDialog(this,listeERU.get(index));
			EvolutionRuleUnitaire ERU = fenetre2.getRuleUnit();
			if (ERU!=null){
				listeERU.set(index, ERU);
				String strAffich = makeString(ERU);
				// Modification de la liste des règles d'évolution unitaire
				listModel.setElementAt(strAffich,index);
			}

		}
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

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
