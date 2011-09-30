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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;

/**
 * @author Florence Curie
 *
 */
public class ListeMethodesPeuplementFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 2382218933353370633L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ListeMethodesPeuplementFrame.class.getName());
	private DefaultListModel listModel;
	private JList list;
	private JButton boutonAjout,boutonSuppression,boutonModification,boutonFermer;
	private List<String> listeMethod = new ArrayList<String>();
	private ConfigurationMethodesPeuplement configuration;
	private String nomCheminFichier = "";
	
	// Constructeur
	public ListeMethodesPeuplementFrame(String nomFichier,Image icone){
		
		nomCheminFichier = nomFichier;
		// La fenêtre
		this.setTitle("Liste des méthodes de peuplement du fichier");
		this.setBounds(50, 100, 420, 500);
		this.setResizable(false);
		this.setIconImage(icone);
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);	
				dispose();
			}
		});

		// Création de la liste des méthodes de peuplement
		configuration = new ConfigurationMethodesPeuplement();
		configuration = ConfigurationMethodesPeuplement.getInstance(nomFichier);
		HashMap<String, ParametresMethodesPeuplement> listeMethodes = configuration.getListType();
		listModel = new DefaultListModel();
		int index = listModel.size();
		for (String nomMethode:listeMethodes.keySet()){
			listModel.insertElementAt(nomMethode,index);
			listeMethod.add(nomMethode);
			index = listModel.size();
		}
		
		// Panneau avec la liste des méthodes de peuplement
		list = new JList(listModel);
		list.setSelectedIndex(index-1);
        list.ensureIndexIsVisible(index-1);
        JScrollPane defil = new JScrollPane(list);
		Box hBoxListeMethodes = Box.createHorizontalBox();
		hBoxListeMethodes.add(defil);
		
		// Ajouter / supprimer une méthode
		boutonAjout = new JButton("ajouter");
		boutonAjout.addActionListener(this);
		boutonSuppression = new JButton("supprimer");
		boutonSuppression.addActionListener(this);
		if (listModel.isEmpty()) boutonSuppression.setEnabled(false);
		boutonModification = new JButton("modifier");
		boutonModification.addActionListener(this);
		if (listModel.isEmpty()) boutonModification.setEnabled(false);
		Box hBoxAjoutSupprMethode = Box.createHorizontalBox();
		hBoxAjoutSupprMethode.add(boutonAjout);
		hBoxAjoutSupprMethode.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprMethode.add(boutonSuppression);
		hBoxAjoutSupprMethode.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprMethode.add(boutonModification);
		hBoxAjoutSupprMethode.add(Box.createHorizontalGlue());
		
		// Fermer
		boutonFermer = new JButton("fermer");
		boutonFermer.addActionListener(this);
		Box hBoxFermer = Box.createHorizontalBox();
		hBoxFermer.add(Box.createHorizontalGlue());
		hBoxFermer.add(boutonFermer);
		
		// L'agencement vertical des boîte horizontales 
		Box vBox = Box.createVerticalBox();
        vBox.add(hBoxListeMethodes);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxAjoutSupprMethode);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxFermer);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenu.add(vBox,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource()==this.boutonAjout){
			// Appel de la fenêtre permettant d'ajouter une méthode
			CreationMethodePeuplementDialog fenetre1 = new CreationMethodePeuplementDialog(this,nomCheminFichier);
			fenetre1.setVisible(true);
			Map<String,ParametresMethodesPeuplement> methode = fenetre1.getMethode();
			String nomMethode = "";
			for (String nomMeth : methode.keySet()){nomMethode = nomMeth;}
			ParametresMethodesPeuplement parametresMethode = methode.get(nomMethode);
			// Ajout de la méthode à la liste
			if (!nomMethode.isEmpty()){
				int index = listModel.size();
				listModel.insertElementAt(nomMethode,index);
				listeMethod.add(nomMethode);
				//Selection du nouvel item et visibilité
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
				if (listModel.size()>0){
					boutonSuppression.setEnabled(true);
					boutonModification.setEnabled(true);
				}
				// On ajoute la méthode au xml
				configuration.getListType().put(nomMethode, parametresMethode);
				configuration.marshall();
			}
		}else if (e.getSource()==this.boutonSuppression){
			 int index = list.getSelectedIndex();
	         // On supprime la méthode des listes
			 String methodeASupprimer = listeMethod.get(index);
			 listModel.remove(index);
	         listeMethod.remove(index);	         
	         if (listModel.size() == 0) {// Si la liste est vide on désactive le bouton supprimer 
	        	 boutonSuppression.setEnabled(false);
	         }else{// On sélectionne un élément
	        	 if (index == listModel.getSize()) {index--;}
	        	 list.setSelectedIndex(index);
	        	 list.ensureIndexIsVisible(index);
	         }
	         // On supprime la méthode du xml
	         configuration.getListType().remove(methodeASupprimer);
			 configuration.marshall();
			
		}else if (e.getSource()==this.boutonModification){
			int index = list.getSelectedIndex();
			String methodeAModifier = listeMethod.get(index);
			ParametresMethodesPeuplement parametrePeuplementAModifier = configuration.getListType().get(methodeAModifier);
			// Appel de la fenêtre permettant de modifier une méthode
			CreationMethodePeuplementDialog fenetre1 = new CreationMethodePeuplementDialog(this,nomCheminFichier,methodeAModifier,parametrePeuplementAModifier);
			fenetre1.setVisible(true);
			Map<String,ParametresMethodesPeuplement> methode = fenetre1.getMethode();
			String nomMethode = "";
			for (String nomMeth : methode.keySet()){nomMethode = nomMeth;}
			ParametresMethodesPeuplement parametresMethode = methode.get(nomMethode);
			// Remplacement de la méthode dans la liste
			if (!nomMethode.isEmpty()){
				listModel.setElementAt(nomMethode,index);
				listeMethod.set(index,nomMethode);
				// On remplace la méthode au xml
		        configuration.getListType().remove(methodeAModifier);
				configuration.getListType().put(nomMethode, parametresMethode);
				configuration.marshall();
			}
			
		}else if (e.getSource()==this.boutonFermer){
			// Fermeture de la fenêtre
			this.setVisible(false);	
			this.dispose();
		}
	


	}

}
