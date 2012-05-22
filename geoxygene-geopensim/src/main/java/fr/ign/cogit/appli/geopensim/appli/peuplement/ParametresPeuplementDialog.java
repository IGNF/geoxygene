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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypeMethode;

/**
 * @author Florence Curie
 *
 */
public class ParametresPeuplementDialog extends JDialog implements ActionListener,FocusListener{

	private static final long serialVersionUID = -373148965571342366L;
	private JComboBox comboMethodePeuplement;
	private JTextField frequence;
	private JButton boutonValidation, boutonAnnulation;
	private String[] listeMethodePeuplement;
	private String methodeP;
	private TypeMethode typeM = null;
	private boolean ok;
	private double freq;
	
	@SuppressWarnings("unused")
	private static Logger logger=Logger.getLogger(ParametresPeuplementDialog.class.getName());

	// Constructeur
	public ParametresPeuplementDialog(JDialog parent, TypeMethode typeMeth){
		this(parent);
		
		// la méthode de peuplement 
		String nomMethode = typeMeth.getNomMethodePeuplement(); 
		int index = -1;
		for (int i=0;i<listeMethodePeuplement.length;i++){
			if (listeMethodePeuplement[i].equals(nomMethode)){
				index =i;
				break;
			}
		}
		comboMethodePeuplement.setSelectedIndex(index);
		
		// La fréquence
		double frequ = typeMeth.getPourcentage();
		if (frequ!=-1){
			frequence.setText(String.valueOf(frequ));
		}
	}
	
	// Constructeur
	public ParametresPeuplementDialog(JDialog parent){

		// La fenêtre
		super (parent,"Ajout d'une méthode de peuplement",true);
		this.setBounds(500, 100, 300, 190);
		this.setResizable(false);
		Container contenu = this.getContentPane();

		// La liste des méthodes de peuplement
		ConfigurationMethodesPeuplement configMP = ConfigurationMethodesPeuplement.getInstance();
		listeMethodePeuplement = new String[configMP.getListType().size()];
		int compt = 0;
		for(String nomMP : configMP.getListType().keySet()){
			listeMethodePeuplement[compt++] = nomMP;
		}
		comboMethodePeuplement = new JComboBox(listeMethodePeuplement);
		comboMethodePeuplement.setMaximumSize(comboMethodePeuplement.getPreferredSize());
		
		Box hBoxMethodePeuplement1 = Box.createHorizontalBox();
		hBoxMethodePeuplement1.add(new JLabel("Methode de peuplement : "));
		hBoxMethodePeuplement1.add(Box.createHorizontalGlue());
		Box hBoxMethodePeuplement2 = Box.createHorizontalBox();
		hBoxMethodePeuplement2.add(comboMethodePeuplement);
		hBoxMethodePeuplement2.add(Box.createHorizontalGlue());
		
		// La fréquence
		frequence = new JTextField(5);
		frequence.setMaximumSize(frequence.getPreferredSize());	
		frequence.addFocusListener(this);
		Box hBoxFrequence = Box.createHorizontalBox();
		hBoxFrequence.add(new JLabel("fréquence de cette méthode : "));
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
		
		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
		vBox.add(hBoxMethodePeuplement1);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxMethodePeuplement2);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxFrequence);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxBoutons);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	public TypeMethode getTypeMethode(){
		ok = false;
		setVisible(true);
		if (ok)	return typeM;
		else return typeM;
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		// La fréquence de la méthode de peuplement
		if (e.getSource()==this.frequence){
			freq = -1;
			String strFreq1 = "La fréquence entrée n'est pas un nombre";
			String strFreq2 = "Voulez vous entrer une autre fréquence ?";
			String strFreq3 = "Entrez une nouvelle fréquence pour la méthode";
			freq = CreationMethodePeuplementDialog.verifDouble(this.frequence, strFreq1, strFreq2, strFreq3);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonValidation){
			// La methode de peuplement
			methodeP = (String) this.comboMethodePeuplement.getSelectedItem();
			// La fréquence de la méthode de peuplement
			if(this.frequence.getText().isEmpty()){freq=-1;}
			else{freq = Double.parseDouble(this.frequence.getText());}
			// Création du nouveau type de peuplement
			typeM = new TypeMethode(freq, methodeP);
			ok = true;
			// Fermeture de la fenêtre
			setVisible(false);
			dispose();
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);		
			dispose();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}
	
}
