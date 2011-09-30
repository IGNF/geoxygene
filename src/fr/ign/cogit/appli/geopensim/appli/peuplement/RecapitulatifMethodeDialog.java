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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;


/**
 * @author Florence Curie
 *
 */
public class RecapitulatifMethodeDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = -4248831294124498093L;
	private JButton boutonValidation, boutonAnnulation;
	private boolean ok;
	
	@SuppressWarnings("unused")
	private static Logger logger=Logger.getLogger(RecapitulatifMethodeDialog.class.getName());
	
	// Constructeur
	public RecapitulatifMethodeDialog(JDialog parent,String nom,ParametresMethodesPeuplement methodePeuplement){

		// La fenêtre
		super (parent,"Récapitulatif de la Méthode de peuplement",true);
		this.setBounds(500, 100, 400, 400);
		this.setResizable(false);
		Container contenu = this.getContentPane();

		// Le nom de la Méthode
		Box hBoxNom = Box.createHorizontalBox();
		hBoxNom.add(new JLabel("Nom de la Méthode : "+nom));
		hBoxNom.add(Box.createHorizontalGlue());
		
		// la période 
		Box hBoxPeriode = Box.createHorizontalBox();
		hBoxPeriode.add(new JLabel("période allant de "+methodePeuplement.getDatesMethode().getDateDebut()+
				" à "+methodePeuplement.getDatesMethode().getDateFin()));
		hBoxPeriode.add(Box.createHorizontalGlue());

		// Le type fonctionnel
		Box hBoxTypeF = Box.createHorizontalBox();
		hBoxTypeF.add(new JLabel("Type fonctionnel des bâtiments : "+methodePeuplement.getTypeFonctionnel()));
		hBoxTypeF.add(Box.createHorizontalGlue());
		
		// l'orientation à la route
		Box hBoxOrientation = Box.createHorizontalBox();
		hBoxOrientation.add(new JLabel("parallèle à la route : "+methodePeuplement.getParalleleRoute()));
		hBoxOrientation.add(Box.createHorizontalGlue());
		
		// l'orientation aux autres bâtiments
		Box hBoxOrientation2 = Box.createHorizontalBox();
		hBoxOrientation2.add(new JLabel("parallèle aux autres bâtiments : "+methodePeuplement.getParalleleBatiment()));
		hBoxOrientation2.add(Box.createHorizontalGlue());
		
		// La distance à la route
		Box hBoxDistanceR = Box.createHorizontalBox();
		Distribution distribdistanceR = methodePeuplement.getDistanceRoute();
		hBoxDistanceR.add(new JLabel("Distance à la route : " + affichDistrib(distribdistanceR)));
		hBoxDistanceR.add(Box.createHorizontalGlue());
		
		// La distance aux autres bâtiments
		Box hBoxDistanceB = Box.createHorizontalBox();
		Distribution distribdistanceB = methodePeuplement.getDistanceBatiment();
		hBoxDistanceB.add(new JLabel("Distance aux autres bâtiments : " + affichDistrib(distribdistanceB)));
		hBoxDistanceB.add(Box.createHorizontalGlue());
		
		// Les différents types de bâtiments
		Box hBoxTypeBatiments = Box.createHorizontalBox();
		Box vBoxTypeBatiments = Box.createVerticalBox();
		int i=0;
		for (ParametresForme forme:methodePeuplement.getFormeBatiment()){
			vBoxTypeBatiments.add(new JLabel("Type de bâtiment numéro "+ ++i));
			vBoxTypeBatiments.add(new JLabel("      - Forme : " + forme.getForme()));
			Distribution distribAire = forme.getTailleBatiment();
			vBoxTypeBatiments.add(new JLabel("      - Taille : " + affichDistrib(distribAire)));
			Distribution distribElongation = forme.getElongationBatiment();
			vBoxTypeBatiments.add(new JLabel("      - Elongation : " + affichDistrib(distribElongation)));
			Distribution distribEpaisseur = forme.getEpaisseurBatiment();
			vBoxTypeBatiments.add(new JLabel("      - Epaisseur : " + affichDistrib(distribEpaisseur)));
			vBoxTypeBatiments.add(new JLabel("      - Frequence : " + forme.getFrequence()));
		}
		vBoxTypeBatiments.add(Box.createHorizontalGlue());
		hBoxTypeBatiments.add(vBoxTypeBatiments);
		
		
		// Deux boutons
		boutonValidation = new JButton("valider");
		boutonValidation.addActionListener(this);
		boutonAnnulation = new JButton("annuler");
		boutonAnnulation.addActionListener(this);
		Box hBoxBoutons = Box.createHorizontalBox();
		hBoxBoutons.add(Box.createHorizontalGlue());
		hBoxBoutons.add(boutonValidation);
		hBoxBoutons.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxBoutons.add(boutonAnnulation);
		
		// L'agencement vertical des boîte horizontales de caractéristiques
		Box vBoxCaracteristiques = Box.createVerticalBox();
		vBoxCaracteristiques.add(hBoxNom);
        vBoxCaracteristiques.add(Box.createVerticalStrut(10));
		vBoxCaracteristiques.add(hBoxPeriode);
		vBoxCaracteristiques.add(hBoxTypeF);
        vBoxCaracteristiques.add(hBoxOrientation);
        vBoxCaracteristiques.add(hBoxOrientation2);
        vBoxCaracteristiques.add(hBoxDistanceR);
        vBoxCaracteristiques.add(hBoxDistanceB);
        vBoxCaracteristiques.add(Box.createVerticalStrut(10));
        vBoxCaracteristiques.add(hBoxTypeBatiments);
		vBoxCaracteristiques.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane defil = new JScrollPane(vBoxCaracteristiques);
		
		// Vbox final
		Box vBox = Box.createVerticalBox();
		vBox.add(defil);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxBoutons);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	public boolean getReponse(){
		setVisible(true);
		return ok;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonValidation){
			ok = true;
			setVisible(false);
		}else if (e.getSource()==this.boutonAnnulation){
			ok = false;
			setVisible(false);			
		}
		
	}
	
	public String affichDistrib(Distribution distrib){
		
		String str = "";
		if ((distrib.getMinimum()!=-1)||(distrib.getMaximum()!=-1)){
			str = 	" distribution "+distrib.getTypeDistribution()+ " de "+ distrib.getMinimum()+
					" à "+distrib.getMaximum();
		}else{
			str = 	" distribution "+distrib.getTypeDistribution()+ " "+ distrib.getMoyenne()+
					" (+/- "+distrib.getEcartType()+")";
		}
		
		return str;
	}
	
}
