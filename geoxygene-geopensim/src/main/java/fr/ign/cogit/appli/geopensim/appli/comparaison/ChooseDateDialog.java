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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.MainFrame;

/**
 * @author Florence Curie
 *
 */
public class ChooseDateDialog extends JDialog  implements ActionListener {

	private static final long serialVersionUID = -647198137389566427L;
	static final Logger logger=Logger.getLogger(ChooseDateDialog.class.getName());
	private MainFrame mainFrame = null;
	private JTextField dateSimul;
	private JButton boutonValidation, boutonAnnulation;
	private boolean ok;
	private int dateSimulation;
	
	// Constructeur
	public ChooseDateDialog(MainFrame mainFrame,Integer dateS){

		super (mainFrame,"Choix de la date de simulation",true);
		this.mainFrame = mainFrame;
		this.dateSimulation = dateS;
		
		// La fenêtre
		setIconImage(this.mainFrame.getIconImage());
		this.setBounds(50, 100, 280, 120);
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
		
		// La date simulée
		dateSimul = new JTextField(5);
		dateSimul.setMaximumSize(dateSimul.getPreferredSize());
		dateSimul.setText(((Integer)dateSimulation).toString());
		Box hBoxDateSimulee = Box.createHorizontalBox();
		hBoxDateSimulee.add(new JLabel("Date simulée : "));
		hBoxDateSimulee.add(dateSimul);
		hBoxDateSimulee.add(Box.createHorizontalGlue());
		
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
        vBox.add(hBoxDateSimulee);
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(hBoxBoutons);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	public int getDateSimulee(){
		if (ok)	return dateSimulation;
		else return 0;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonValidation){
			// On affecte la date simulée
			int dateSim = Integer.parseInt(this.dateSimul.getText());
			dateSimulation = dateSim;
			ok = true;
			setVisible(false);
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);			
		}
	}
}
