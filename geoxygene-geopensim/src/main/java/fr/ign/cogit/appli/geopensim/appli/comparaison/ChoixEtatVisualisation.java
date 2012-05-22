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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.appli.tracking.SimulationTreeFrame;

/**
 * @author Florence Curie
 *
 */
public class ChoixEtatVisualisation extends JDialog implements ActionListener{


	private static final long serialVersionUID = -2687762356288261250L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChoixEtatVisualisation.class.getName());
	private JButton boutonValidation, boutonAnnulation;
	private JTextField nomEtat;
	private String nomEtatSelect;
	JFrame parent;
	private boolean ok;

	// Constructeur
	public ChoixEtatVisualisation(final JFrame parent){

		super(parent);
		this.parent = parent;
		// La fenêtre
		this.setTitle("Choix d'un état");
		this.setBounds(50, 100, 400, 150);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(GeOpenSimApplication.class.getResource("/geopensim-icon.gif")).getImage());
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				((SimulationTreeFrame)parent).setEnabledBouton(true);
				 // Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}
		});

		// Le premier bouton
		Box hBoxLabel = Box.createHorizontalBox();
		hBoxLabel.add(new JLabel("Etat sélectionné : "));
		hBoxLabel.add(Box.createHorizontalGlue());
		
		// Choix d'un état
		Box hBoxChoixEtat = Box.createHorizontalBox();
		hBoxChoixEtat.add(Box.createHorizontalStrut(20));
		String nomFich = "";
		nomEtat = new JTextField(50);
		nomEtat.setText(nomFich);
		nomEtat.setMaximumSize(nomEtat.getPreferredSize());
		hBoxChoixEtat.add(nomEtat);
		hBoxChoixEtat.add(Box.createHorizontalStrut(10));
		
		// Bouton de validation
		Box hBoxValidation = Box.createHorizontalBox();
		hBoxValidation.add(Box.createHorizontalGlue());
		boutonValidation = new JButton("Valider");
		boutonValidation.addActionListener(this);
		hBoxValidation.add(boutonValidation);
		hBoxValidation.add(Box.createHorizontalStrut(10));
		boutonAnnulation = new JButton("Annuler");
		boutonAnnulation.addActionListener(this);
		hBoxValidation.add(boutonAnnulation);
		
		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
		vBox.add(hBoxLabel);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxChoixEtat);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(Box.createVerticalStrut(25));
		vBox.add(hBoxValidation);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getSource()==this.boutonValidation){
			nomEtatSelect = nomEtat.getText();
			((SimulationTreeFrame)parent).setEtatSelect(nomEtatSelect);
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		 }else if(e.getSource()==this.boutonAnnulation){
			((SimulationTreeFrame)parent).setEnabledBouton(true);
			 // Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}
	}

	/**
	 * @param nomEtatSelect le nom de l'état sélectionné
	 */
	public void setEtatSelect(String nomEtat) {
		this.nomEtatSelect = nomEtat;
		this.nomEtat.setText(nomEtat);
	}
	
	/**
	 * @param nomEtatSelect le nom de l'état sélectionné
	 */
	public String getEtatSelect() {
		ok = false;
		setVisible(true);
		if (ok)	return nomEtatSelect;
		else return null;
	}

}
