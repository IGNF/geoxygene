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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationComparaison;
import fr.ign.cogit.appli.geopensim.ConfigurationComparaison.ParametresComparaison;

/**
 * @author Florence Curie
 *
 */
public class ListeParamComparaisonFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 2382218933353370633L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ListeParamComparaisonFrame.class.getName());
	private DefaultListModel listModel;
	private JList list;
	private JButton boutonAjout,boutonSuppression,boutonModification,boutonFermer;
	private List<String> listeParametrage = new ArrayList<String>();
	private ConfigurationComparaison configuration;

	
	// Constructeur
	public ListeParamComparaisonFrame(){
		
//		nomCheminFichier = nomFichier;
		// La fenêtre
		this.setTitle("Liste des paramétrages de comparaison");
		this.setBounds(50, 100, 420, 500);
		this.setResizable(false);
//		this.setIconImage(icone);
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);	
				dispose();
			}
		});

		// Création de la liste des paramétrages de comparaison
		configuration = new ConfigurationComparaison();
		configuration = ConfigurationComparaison.getInstance();
		HashMap<String, List<ParametresComparaison>> listeParametrages = configuration.getListType();
		listModel = new DefaultListModel();
		int index = listModel.size();
		for (String nomParametrage:listeParametrages.keySet()){
			listModel.insertElementAt(nomParametrage,index);
			listeParametrage.add(nomParametrage);
			index = listModel.size();
		}
		
		// Panneau avec la liste des paramétrages de comparaison
		list = new JList(listModel);
		list.setSelectedIndex(index-1);
        list.ensureIndexIsVisible(index-1);
        JScrollPane defil = new JScrollPane(list);
		Box hBoxListeParametrages = Box.createHorizontalBox();
		hBoxListeParametrages.add(defil);
		
		// Ajouter / supprimer un paramétrage
		boutonAjout = new JButton("ajouter");
		boutonAjout.addActionListener(this);
		boutonSuppression = new JButton("supprimer");
		boutonSuppression.addActionListener(this);
		if (listModel.isEmpty()) boutonSuppression.setEnabled(false);
		boutonModification = new JButton("modifier");
		boutonModification.addActionListener(this);
		if (listModel.isEmpty()) boutonModification.setEnabled(false);
		Box hBoxAjoutSupprParametrage = Box.createHorizontalBox();
		hBoxAjoutSupprParametrage.add(boutonAjout);
		hBoxAjoutSupprParametrage.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprParametrage.add(boutonSuppression);
		hBoxAjoutSupprParametrage.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprParametrage.add(boutonModification);
		hBoxAjoutSupprParametrage.add(Box.createHorizontalGlue());
		
		// Fermer
		boutonFermer = new JButton("fermer");
		boutonFermer.addActionListener(this);
		Box hBoxFermer = Box.createHorizontalBox();
		hBoxFermer.add(Box.createHorizontalGlue());
		hBoxFermer.add(boutonFermer);
		
		// L'agencement vertical des boîte horizontales 
		Box vBox = Box.createVerticalBox();
        vBox.add(hBoxListeParametrages);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxAjoutSupprParametrage);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxFermer);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenu.add(vBox,BorderLayout.CENTER);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource()==this.boutonAjout){
			// Appel de la fenêtre permettant d'ajouter un paramétrage
			CreationParametrageComparaison fenetre1 = new CreationParametrageComparaison(this);
			fenetre1.setVisible(true);
			HashMap<String, List<ParametresComparaison>> parametrage = fenetre1.getParametrage();
			if(parametrage!=null){
				String nomParametrage = "";
				for (String nomParam : parametrage.keySet()){nomParametrage = nomParam;}
				List<ParametresComparaison> parametresComparaison = parametrage.get(nomParametrage);
				// Ajout du paramétrage à la liste
				if (!nomParametrage.isEmpty()){
					int index = listModel.size();
					listModel.insertElementAt(nomParametrage,index);
					listeParametrage.add(nomParametrage);
					//Selection du nouvel item et visibilité
					list.setSelectedIndex(index);
					list.ensureIndexIsVisible(index);
					if (listModel.size()>0){
						boutonSuppression.setEnabled(true);
						boutonModification.setEnabled(true);
					}
					// On ajoute le paramétrage au xml
					configuration.getListType().put(nomParametrage, parametresComparaison);
					configuration.marshall();
				}
			}
		}else if (e.getSource()==this.boutonSuppression){
			 int index = list.getSelectedIndex();
	         // On supprime le paramétrage des listes
			 String parametrageASupprimer = listeParametrage.get(index);
			 listModel.remove(index);
	         listeParametrage.remove(index);	         
	         if (listModel.size() == 0) {// Si la liste est vide on désactive le bouton supprimer 
	        	 boutonSuppression.setEnabled(false);
	         }else{// On sélectionne un élément
	        	 if (index == listModel.getSize()) {index--;}
	        	 list.setSelectedIndex(index);
	        	 list.ensureIndexIsVisible(index);
	         }
	         // On supprime le paramétrage du xml
	         configuration.getListType().remove(parametrageASupprimer);
			 configuration.marshall();
			
		}else if (e.getSource()==this.boutonModification){
			int index = list.getSelectedIndex();
			String paramAModifier = listeParametrage.get(index);
			List<ParametresComparaison> parametreComparaisonAModifier = configuration.getListType().get(paramAModifier);
			// Appel de la fenêtre permettant de modifier une méthode
			CreationParametrageComparaison fenetre1 = new CreationParametrageComparaison(this,paramAModifier,parametreComparaisonAModifier);
			fenetre1.setVisible(true);
			HashMap<String, List<ParametresComparaison>> parametrage = fenetre1.getParametrage();
			if(parametrage!=null){
				String nomMethode = "";
				for (String nomMeth : parametrage.keySet()){nomMethode = nomMeth;}
				List<ParametresComparaison>  parametresComparaison = parametrage.get(nomMethode);
				// Remplacement de la méthode dans la liste
				if (!nomMethode.isEmpty()){
					listModel.setElementAt(nomMethode,index);
					listeParametrage.set(index,nomMethode);
					// On remplace la méthode au xml
					configuration.getListType().remove(paramAModifier);
					configuration.getListType().put(nomMethode, parametresComparaison);
					configuration.marshall();
				}
			}
		}else if (e.getSource()==this.boutonFermer){
			// Fermeture de la fenêtre
			this.setVisible(false);	
			this.dispose();
		}
	


	}

}
