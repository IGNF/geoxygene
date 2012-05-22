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
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Logger;

//import org.apache.log4j.Logger;


import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.appli.tracking.SimulationTreeFrame;
import fr.ign.cogit.geoxygene.appli.MainFrame;

/**
 * @author Florence Curie
 *
 */
public class ChoixEtatSelection extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 80438770163190784L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChoixEtatSelection.class.getName());
	private JButton boutonValidation, boutonAnnulation;
	JRadioButton bREtat1,bREtat2;
	EtatGlobal etatG1,etatG2,etatSelect;
	JFrame parent;

	// Constructeur
	public ChoixEtatSelection(final JFrame parent,EtatGlobal etat1,EtatGlobal etat2){

		this.parent = parent;
		this.etatG1 = etat1;
		this.etatG2 = etat2;
		// La fenêtre
		this.setTitle("Choix de l'état dans lequel aura lieu la sélection");
		this.setBounds(50, 100, 400, 200);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(GeOpenSimApplication.class.getResource("/geopensim-icon.gif")).getImage());
		Container contenu = this.getContentPane();
		
		// Arrêt du programme sur fermeture de la fenêtre
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				parent.setVisible(true);
				setVisible(false);	
				dispose();
			}
		});
		
		// Choix de l'état
		JPanel panneau = new JPanel();
		panneau.setLayout(new javax.swing.BoxLayout(panneau, BoxLayout.Y_AXIS));
		Box hBoxTitre = Box.createHorizontalBox();
		hBoxTitre.add(new JLabel(" Choix de l'état : "));
		hBoxTitre.add(Box.createHorizontalGlue());
		panneau.add(hBoxTitre);
		panneau.add(Box.createVerticalStrut(5));
		Box hBoxAlt1 = Box.createHorizontalBox();
		hBoxAlt1.add(Box.createHorizontalStrut(10));
		bREtat1 = new JRadioButton("etat1 : "+ etat1.getNom());
		bREtat1.addActionListener(this);
		hBoxAlt1.add(bREtat1);
		hBoxAlt1.add(Box.createHorizontalGlue());
		panneau.add(hBoxAlt1);
		Box hBoxAlt2 = Box.createHorizontalBox();
		hBoxAlt2.add(Box.createHorizontalStrut(10));
		bREtat2 = new JRadioButton("etat2 : "+ etat2.getNom());
		bREtat2.addActionListener(this);
		hBoxAlt2.add(bREtat2);
		hBoxAlt2.add(Box.createHorizontalGlue());
		panneau.add(hBoxAlt2);
		ButtonGroup grouperadio = new ButtonGroup();
		grouperadio.add(bREtat1);
		grouperadio.add(bREtat2);
		
		// mise à jour de la carte
		etatSelect = etatG1;
		bREtat1.setSelected(true);
		MainFrame mainF = (MainFrame)((ChoixEtatComparaison)this.parent).getPreviousFrame();
		((GeOpenSimApplication)(mainF.getApplication())).affichageCarte(etatG1);
		
		// Choix des îlots
		Box hBoxIlot = Box.createHorizontalBox();
		hBoxIlot.add(new JLabel(" Sélectionner les îlots sur la carte puis valider "));
		hBoxIlot.add(Box.createHorizontalGlue());
		
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
		vBox.add(panneau);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxIlot);
		vBox.add(Box.createVerticalStrut(20));
		vBox.add(hBoxValidation);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==this.bREtat1){
			etatSelect = etatG1;
			// Appel d'une fonction d'affichage
			((SimulationTreeFrame)((ChoixEtatComparaison)this.parent).getPreviousFrame()).affichageCarteEtArbre(etatG1.getNom());
		}else if(e.getSource()==this.bREtat2){
			etatSelect = etatG2;
			// Appel d'une fonction d'affichage
			((SimulationTreeFrame)((ChoixEtatComparaison)this.parent).getPreviousFrame()).affichageCarteEtArbre(etatG2.getNom());
		}else if(e.getSource()==this.boutonValidation){
			((ChoixEtatComparaison)this.parent).getSelection();
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource()==this.boutonAnnulation){
			parent.setVisible(true);
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}
	}

	public EtatGlobal getEtat(){
		return etatSelect;
	}
	
}
