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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleBase;

import org.apache.log4j.Logger;

/**
 * @author Florence Curie
 *
 */
public class EditRulesFramev2 extends JFrame implements ActionListener{

	private static final long serialVersionUID = -3096219058454297601L;
	private static final Logger logger = Logger.getLogger(EditRulesFramev2.class.getName());
	private JButton boutonAjouterUnite, boutonSupprimerUnite, boutonModifierUnite,boutonFermer;
	private JButton boutonAjouterZone, boutonSupprimerZone, boutonModifierZone;
	private DefaultListModel listModelUnite,listModelZone;
	private JList listReglesUnite, listReglesZone;
	private EvolutionRuleBase configuration;
	private List<EvolutionRule> listeRulesZone,listeRulesUnite;
	
	// Constructeur
	public EditRulesFramev2(){
		super();
		// La fenêtre
		this.setTitle("Règles d'évolution");
		this.setBounds(50, 100, 500, 400);
		this.setResizable(true);
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

		// Lecture du fichier xml
		//configuration = new EvolutionRuleBase();
		configuration = EvolutionRuleBase.getInstance();
		List<EvolutionRule> listRules = configuration.getRules();
		// Création de la liste des règles d'évolution
		listModelUnite = new DefaultListModel();
		listModelZone = new DefaultListModel();
		listeRulesUnite = new ArrayList<EvolutionRule>();
		listeRulesZone = new ArrayList<EvolutionRule>();
		for (EvolutionRule rule:listRules){
			if(rule.getType().equals("ZoneElementaireUrbaine")){  
				String str = makeString(rule);
				listModelZone.addElement(str);
				listeRulesZone.add(rule);
			}else if(rule.getType().equals("UniteUrbaine")){
				String str = makeString(rule);
				listModelUnite.addElement(str);
				listeRulesUnite.add(rule);
			}
		}
				
		// Le premier onglet (l'unité urbaine)
		JPanel unitePanel = new JPanel(false);
		unitePanel.setLayout(new GridLayout(0, 1));
		
		// Le label au-dessus de la zone de texte
		JLabel columnLabelUnite = new JLabel(" période : (fréquence) [précondition] expression");
		Box hBoxLabelUnite = Box.createHorizontalBox();
		hBoxLabelUnite.add(columnLabelUnite);
		hBoxLabelUnite.add(Box.createHorizontalGlue());
		
		// Création de la zone de texte pour l'unité urbaine
		listReglesUnite = new JList(listModelUnite);
		if(!listeRulesUnite.isEmpty()){
			int index = listeRulesUnite.size()-1;
			listReglesUnite.setSelectedIndex(index);
			listReglesUnite.ensureIndexIsVisible(index);
		}
		JScrollPane defilUnite = new JScrollPane(listReglesUnite);
		defilUnite.setMinimumSize(new Dimension(500, 300));
		
		// Les 3 boutons pour l'unité urbaine
		boutonAjouterUnite = new JButton("ajouter");
		boutonAjouterUnite.addActionListener(this);
		boutonSupprimerUnite = new JButton("supprimer");
		boutonSupprimerUnite.addActionListener(this);
		boutonModifierUnite = new JButton("modifier");
		boutonModifierUnite.addActionListener(this);
		if(listeRulesUnite.isEmpty()){
			boutonModifierUnite.setEnabled(false);
			boutonSupprimerUnite.setEnabled(false);
		}
		Box hBoxAjoutSupprUnite = Box.createHorizontalBox();
		hBoxAjoutSupprUnite.add(boutonAjouterUnite);
		hBoxAjoutSupprUnite.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprUnite.add(boutonSupprimerUnite);
		hBoxAjoutSupprUnite.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprUnite.add(boutonModifierUnite);
		hBoxAjoutSupprUnite.add(Box.createHorizontalGlue());
		
		// Boite verticale dans le panneau unité urbaine
		Box vBoxUnite = Box.createVerticalBox();
		vBoxUnite.add(hBoxLabelUnite);
		vBoxUnite.add(Box.createVerticalStrut(3));
		vBoxUnite.add(defilUnite);
		vBoxUnite.add(Box.createVerticalStrut(10));
		vBoxUnite.add(hBoxAjoutSupprUnite);
		vBoxUnite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		unitePanel.add(vBoxUnite);

		// Le deuxième onglet (la zone élémentaire urbaine)
		JPanel zonePanel = new JPanel(false);;
		zonePanel.setLayout(new GridLayout(0, 1));
		
		// Le label au-dessus de la zone de texte
		JLabel columnLabelZone = new JLabel(" propriété = nomRegleEvolution (période)");
		Box hBoxLabelZone = Box.createHorizontalBox();
		hBoxLabelZone.add(columnLabelZone);
		hBoxLabelZone.add(Box.createHorizontalGlue());
		
		// Création de la zone de texte pour la zone élémentaire urbaine
		listReglesZone = new JList(listModelZone);
		if(!listeRulesZone.isEmpty()){
			int index = listeRulesZone.size()-1;
			listReglesZone.setSelectedIndex(index);
			listReglesZone.ensureIndexIsVisible(index);
		}
		JScrollPane defilZone = new JScrollPane(listReglesZone);
		defilZone.setMinimumSize(new Dimension(500, 300));
		
		// Les 3 boutons pour la zone élémentaire urbaine
		boutonAjouterZone = new JButton("ajouter");
		boutonAjouterZone.addActionListener(this);
		boutonSupprimerZone = new JButton("supprimer");
		boutonSupprimerZone.addActionListener(this);
		boutonModifierZone = new JButton("modifier");
		boutonModifierZone.addActionListener(this);
		if(listeRulesZone.isEmpty()){
			boutonModifierZone.setEnabled(false);
			boutonSupprimerZone.setEnabled(false);
		}
		Box hBoxAjoutSupprZone = Box.createHorizontalBox();
		hBoxAjoutSupprZone.add(boutonAjouterZone);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonSupprimerZone);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonModifierZone);
		hBoxAjoutSupprZone.add(Box.createHorizontalGlue());
		
		// Boite verticale dans le panneau zone élémentaire urbaine
		Box vBoxZone = Box.createVerticalBox();
		vBoxZone.add(hBoxLabelZone);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(defilZone);
		vBoxZone.add(Box.createVerticalStrut(10));
		vBoxZone.add(hBoxAjoutSupprZone);
		vBoxZone.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		zonePanel.add(vBoxZone);
		
		// Ajout des onglets au panneau
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Unite Urbaine",unitePanel);
		tabbedPane.addTab("Zone Elementaire Urbaine", zonePanel);
		
		// Le bouton appliquer
		boutonFermer = new JButton("Fermer");
		boutonFermer.addActionListener(this);
		Box hBoxBoutons = Box.createHorizontalBox();
		hBoxBoutons.add(Box.createHorizontalGlue());
		hBoxBoutons.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxBoutons.add(boutonFermer);
		
		Box vBoxFinal = Box.createVerticalBox();
		vBoxFinal.add(tabbedPane);
		vBoxFinal.add(Box.createRigidArea(new Dimension(0,10)));
		vBoxFinal.add(hBoxBoutons);
		vBoxFinal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contenu.add(vBoxFinal,BorderLayout.CENTER);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame fenetre1 = new EditRulesFramev2();
		fenetre1.setVisible(true);
	}
	
	public String makeString(EvolutionRule rule){
		String strAff = rule.getPropertyName()+" = "+rule.getNom()+ " ("+rule.getStart()+ " - "+ rule.getEnd()+")";
		return strAff;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonFermer)){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonAjouterUnite)){
			// Ouverture de la fenêtre d'ajout d'une règle
			EvolutionRuleCreationDialog fenetre2 = new EvolutionRuleCreationDialog(this);
			EvolutionRule rule = fenetre2.getRule();
			if (rule!=null){
				rule.setType("UniteUrbaine"); 
				// ajout de la nouvelle règle à la liste
				String strAffich = makeString(rule);
				logger.info(strAffich);
				// Ajout de la règle d'évolution à la liste
				int index = listModelUnite.size();
				listModelUnite.insertElementAt(strAffich,index);
				listeRulesUnite.add(rule);
				//Selection du nouvel item et visibilité
				listReglesUnite.setSelectedIndex(index);
				listReglesUnite.ensureIndexIsVisible(index);
				if (listModelUnite.size()>0){
					boutonSupprimerUnite.setEnabled(true);
					boutonModifierUnite.setEnabled(true);
				}
				// ajout de la règle à la base de règles
				configuration.getRules().add(rule);
				configuration.marshall();
				// réactivation des boutons modifier et supprimer
				boutonModifierUnite.setEnabled(true);
				boutonSupprimerUnite.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonModifierUnite)){
			int index = listReglesUnite.getSelectedIndex();
			EvolutionRuleCreationDialog fenetre2 = new EvolutionRuleCreationDialog(this,listeRulesUnite.get(index));
			EvolutionRule rule = fenetre2.getRule();
			if (rule!=null){
				rule.setType("UniteUrbaine"); 
				listeRulesUnite.set(index, rule);
				String strAffich = makeString(rule);
				// Modification de la liste des règles d'évolution
				listModelUnite.setElementAt(strAffich,index);
				// modification du fichier xml
				configuration.setRules(listeRulesUnite);
				configuration.marshall();
			}
		}else if(e.getSource().equals(boutonSupprimerUnite)){
			int index = listReglesUnite.getSelectedIndex();
			EvolutionRule rule = listeRulesUnite.get(index);
			// Suppresion de l'élément
			listModelUnite.remove(index);
			listeRulesUnite.remove(index);
			// Si la liste est vide on désactive le bouton supprimer
			if (listModelUnite.size() == 0) { 
				boutonSupprimerUnite.setEnabled(false);
				boutonModifierUnite.setEnabled(false);
			}else{// sinon on sélectionne un élément
				if (index == listModelUnite.getSize()) {index--;}
				listReglesUnite.setSelectedIndex(index);
				listReglesUnite.ensureIndexIsVisible(index);
			}
			// modification du fichier xml
			configuration.getRules().remove(rule);
			configuration.marshall();
		}else if(e.getSource().equals(boutonAjouterZone)){
			// Ouverture de la fenêtre d'ajout d'une règle
			EvolutionRuleCreationDialog fenetre2 = new EvolutionRuleCreationDialog(this);
			EvolutionRule rule = fenetre2.getRule();
			if (rule!=null){
				rule.setType("ZoneElementaireUrbaine"); 
				// ajout de la nouvelle règle à la liste
				String strAffich = makeString(rule);
				logger.info(strAffich);
				// Ajout de la règle d'évolution à la liste
				int index = listModelZone.size();
				listModelZone.insertElementAt(strAffich,index);
				listeRulesZone.add(rule);
				//Selection du nouvel item et visibilité
				listReglesZone.setSelectedIndex(index);
				listReglesZone.ensureIndexIsVisible(index);
				if (listModelZone.size()>0){
					boutonSupprimerZone.setEnabled(true);
					boutonModifierZone.setEnabled(true);
				}
				// ajout de la règle à la base de règles
				configuration.getRules().add(rule);
				configuration.marshall();
				// réactivation des boutons modifier et supprimer
				boutonModifierZone.setEnabled(true);
				boutonSupprimerZone.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonModifierZone)){
			int index = listReglesZone.getSelectedIndex();
			EvolutionRuleCreationDialog fenetre2 = new EvolutionRuleCreationDialog(this,listeRulesZone.get(index));
			EvolutionRule rule = fenetre2.getRule();
			if (rule!=null){
				rule.setType("ZoneElementaireUrbaine"); 
				listeRulesZone.set(index, rule);
				String strAffich = makeString(rule);
				// Modification de la liste des règles d'évolution
				listModelZone.setElementAt(strAffich,index);
				// modification du fichier xml
				configuration.setRules(listeRulesZone);
				configuration.marshall();
			}
		}else if(e.getSource().equals(boutonSupprimerZone)){
			int index = listReglesZone.getSelectedIndex();
			EvolutionRule rule = listeRulesZone.get(index);
			// Suppresion de l'élément
			listModelZone.remove(index);
			listeRulesZone.remove(index);
			// Si la liste est vide on désactive le bouton supprimer
			if (listModelZone.size() == 0) { 
				boutonSupprimerZone.setEnabled(false);
				boutonModifierZone.setEnabled(false);
			}else{// sinon on sélectionne un élément
				if (index == listModelZone.getSize()) {index--;}
				listReglesZone.setSelectedIndex(index);
				listReglesZone.ensureIndexIsVisible(index);
			}
			// modification du fichier xml
			configuration.getRules().remove(rule);
			configuration.marshall();
		}
	}
}
