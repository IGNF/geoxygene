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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationFormesBatimentsV2.ParametresForme;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.feature.micro.FormeBatiment;


/**
 * @author Florence Curie
 *
 */
public class ParametresFormeDialog extends JDialog implements ActionListener,FocusListener{
	private static final long serialVersionUID = 7835704040491692178L;
	private JButton boutonValidation, boutonAnnulation;
	private JTextField frequence;
	private JRadioButton bRadioElongation,bRadioEpaisseur,bRadioElongationEpaisseur;
	private JComboBox comboTypeForme;
	private String[] typeForme = setForme();
	private ParametresForme parametres;
	private boolean ok;
	private double freq;
	private JPanel grandPanneau;
	private VariabilitePanel panneauAire,panneauElongation,panneauEpaisseur;
	
	@SuppressWarnings("unused")
	private static Logger logger=Logger.getLogger(ParametresFormeDialog.class.getName());

	
	public String[] setForme(){
		String[] typeForme2 = new String[FormeBatiment.values().length];
		for (int i = 0;i<FormeBatiment.values().length;i++){
			typeForme2[i] = FormeBatiment.values()[i].toString();
		}
		return typeForme2;
	}
	
	// Constructeur
	public ParametresFormeDialog(JDialog parent, ParametresForme param){
		this(parent);
		
		// le type de forme 
		FormeBatiment forme = param.getForme();
		int index = -1;
		for (int i=0;i<typeForme.length;i++){
			if (typeForme[i].equals(forme.toString())){
				index =i;
				break;
			}
		}
		comboTypeForme.setSelectedIndex(index);
		if (typeForme[index].equalsIgnoreCase("carre")){
			panneauElongation.setEnabledPanneau(false);
			panneauEpaisseur.setEnabledPanneau(false);
			bRadioElongation.setEnabled(false);
			bRadioElongationEpaisseur.setEnabled(false);
			bRadioEpaisseur.setEnabled(false);
		}else if (typeForme[index].equalsIgnoreCase("rectangle")){
			if ((param.getEpaisseurBatiment().getMoyenne()!=-1)||(param.getEpaisseurBatiment().getEcartType()!=-1)){
				panneauElongation.setEnabledPanneau(false);
				panneauEpaisseur.setEnabledPanneau(true);
				bRadioEpaisseur.setSelected(true);
			}else{
				panneauEpaisseur.setEnabledPanneau(false);
				bRadioElongation.setSelected(true);
			}
			bRadioElongationEpaisseur.setEnabled(false);			
		}else{
			bRadioElongationEpaisseur.setSelected(true);
		}
		
		// Remplissage du panneau aire
		panneauAire.setDistribution(param.getTailleBatiment());
		// Remplissage du panneau élongation
		panneauElongation.setDistribution(param.getElongationBatiment());
		// remplissage du panneau épaisseur
		panneauEpaisseur.setDistribution(param.getEpaisseurBatiment());
		
		// La fréquence
		double frequ = param.getFrequence();
		if (frequ!=-1){
			frequence.setText(String.valueOf(frequ));
		}
	}
	
	// Constructeur
	public ParametresFormeDialog(JDialog parent){

		// La fenêtre
		super (parent,"Ajout d'une forme de bâtiment",true);
		this.setBounds(500, 100, 450, 600);
		this.setResizable(false);
		Container contenu = this.getContentPane();

		// le type de forme 
		comboTypeForme = new JComboBox(typeForme);
		comboTypeForme.setMaximumSize(comboTypeForme.getPreferredSize());	
		comboTypeForme.addActionListener(this);
		Box hBoxTypeForme = Box.createHorizontalBox();
		hBoxTypeForme.add(new JLabel("Type de forme : "));
		hBoxTypeForme.add(comboTypeForme);
		hBoxTypeForme.add(Box.createHorizontalGlue());

		// La taille	
		panneauAire = new VariabilitePanel(this," Taille du batiment ");
		Box hBoxTaille = Box.createHorizontalBox();
		hBoxTaille.add(panneauAire);
		
		// Grand panneau élongation et épaisseur
		grandPanneau = new JPanel();
		grandPanneau.setLayout(new javax.swing.BoxLayout(grandPanneau, BoxLayout.Y_AXIS));
		Border border = BorderFactory.createLineBorder(Color.gray);
		TitledBorder title = BorderFactory.createTitledBorder(border, " Elongation et épaisseur du batiment ");
		title.setTitleJustification(TitledBorder.LEFT);
		grandPanneau.setBorder(title);
		Box hBoxGrandPanneau = Box.createHorizontalBox();
		hBoxGrandPanneau.add(grandPanneau);
		
		// Les boutons radio de choix
		ButtonGroup groupeBoutons = new ButtonGroup();
		// Le bouton radio elongation
		Box hBoxBoutonRadio = Box.createHorizontalBox();
		bRadioElongation = new JRadioButton("Elongation",true);
		bRadioElongation.addActionListener(this);
		hBoxBoutonRadio.add(bRadioElongation);
		hBoxBoutonRadio.add(Box.createHorizontalGlue());
		groupeBoutons.add(bRadioElongation);
		// Le bouton radio épaisseur
		bRadioEpaisseur = new JRadioButton("Epaisseur",true);
		bRadioEpaisseur.addActionListener(this);
		hBoxBoutonRadio.add(bRadioEpaisseur);
		hBoxBoutonRadio.add(Box.createHorizontalGlue());
		groupeBoutons.add(bRadioEpaisseur);
		// Le bouton radio épaisseur
		bRadioElongationEpaisseur = new JRadioButton("Elongation et épaisseur",true);
		bRadioElongationEpaisseur.addActionListener(this);
		hBoxBoutonRadio.add(bRadioElongationEpaisseur);
		hBoxBoutonRadio.add(Box.createHorizontalGlue());
		groupeBoutons.add(bRadioElongationEpaisseur);
		grandPanneau.add(hBoxBoutonRadio);
		grandPanneau.add(Box.createVerticalStrut(10));

		// L'élongation
		panneauElongation = new VariabilitePanel(this," Elongation du batiment ");
		Box hBoxElongation = Box.createHorizontalBox();
		hBoxElongation.add(panneauElongation);
		grandPanneau.add(hBoxElongation);
		grandPanneau.add(Box.createVerticalStrut(10));

		// L'épaisseur
		panneauEpaisseur = new VariabilitePanel(this," Epaisseur du batiment ");
		Box hBoxEpaisseur = Box.createHorizontalBox();
		hBoxEpaisseur.add(panneauEpaisseur);
		grandPanneau.add(hBoxEpaisseur);
		
		// La fréquence
		frequence = new JTextField(5);
		frequence.setMaximumSize(frequence.getPreferredSize());	
		frequence.addFocusListener(this);
		Box hBoxFrequence = Box.createHorizontalBox();
		hBoxFrequence.add(new JLabel("fréquence de ce type de bâtiment : "));
		hBoxFrequence.add(frequence);
		hBoxFrequence.add(Box.createHorizontalGlue());

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
		
		// L'agencement vertical des boîte horizontales 
		Box vBox = Box.createVerticalBox();
        vBox.add(hBoxTypeForme);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxTaille);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxGrandPanneau);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxFrequence);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxBoutons);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	public ParametresForme getParametres(){
		ok = false;
		setVisible(true);
		if (ok)	return parametres;
		else return null;
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		// La fréquence du bâtiment
		if (e.getSource()==this.frequence){
			freq = -1;
			String strFreq1 = "La fréquence des bâtiments entrée n'est pas un nombre";
			String strFreq2 = "Voulez vous entrer une autre fréquence des bâtiments ?";
			String strFreq3 = "Entrez une nouvelle fréquence des bâtiments pour la méthode";
			freq = CreationMethodePeuplementDialog.verifDouble(this.frequence, strFreq1, strFreq2, strFreq3);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonValidation){
			
			// La forme des bâtiments
			String forme = this.typeForme[this.comboTypeForme.getSelectedIndex()];
			FormeBatiment formeB = FormeBatiment.valueOf(forme);
			
			// La taille des bâtiments
			Distribution distributionAire = panneauAire.getDistribution();
			
			// L'élongation des bâtiments
			Distribution distributionElongation = panneauElongation.getDistribution();

			// L'épaisseur des bâtiments
			Distribution distributionEpaisseur = panneauEpaisseur.getDistribution();

			// La fréquence du bâtiment
			if(this.frequence.getText().isEmpty()){freq=-1;}
			else{freq = Double.parseDouble(this.frequence.getText());}
			
			// Création de la nouvelle forme
			parametres = new ParametresForme(formeB,distributionAire,distributionElongation,distributionEpaisseur,freq); 
			ok = true;
			setVisible(false);
		}else if (e.getSource()==this.boutonAnnulation){
			parametres = null;
			setVisible(false);			
		}else if(e.getSource()==this.bRadioElongation){
			panneauElongation.setEnabledPanneau(true);
			panneauEpaisseur.setEnabledPanneau(false);
		}else if(e.getSource()==this.bRadioEpaisseur){
			panneauElongation.setEnabledPanneau(false);
			panneauEpaisseur.setEnabledPanneau(true);
		}else if(e.getSource()==this.bRadioElongationEpaisseur){
			panneauElongation.setEnabledPanneau(true);
			panneauEpaisseur.setEnabledPanneau(true);
		}else if (e.getSource()==this.comboTypeForme){
			if (comboTypeForme.getSelectedItem().toString().equalsIgnoreCase("carre")){
				panneauElongation.setEnabledPanneau(false);
				panneauEpaisseur.setEnabledPanneau(false);
				bRadioElongation.setEnabled(false);
				bRadioElongationEpaisseur.setEnabled(false);
				bRadioEpaisseur.setEnabled(false);
			}else if (comboTypeForme.getSelectedItem().toString().equalsIgnoreCase("rectangle")){
				panneauElongation.setEnabledPanneau(true);
				panneauEpaisseur.setEnabledPanneau(false);
				bRadioEpaisseur.setEnabled(true);
				bRadioElongation.setEnabled(true);
				bRadioElongationEpaisseur.setEnabled(false);
				bRadioElongation.setSelected(true);
			}else{
				panneauElongation.setEnabledPanneau(true);
				panneauEpaisseur.setEnabledPanneau(true);
				bRadioEpaisseur.setEnabled(true);
				bRadioElongation.setEnabled(true);
				bRadioElongationEpaisseur.setEnabled(true);
				bRadioElongationEpaisseur.setSelected(true);
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}
	
}
