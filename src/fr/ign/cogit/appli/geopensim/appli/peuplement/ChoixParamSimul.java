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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleBase;
import fr.ign.cogit.appli.geopensim.evolution.Scenario;
import fr.ign.cogit.appli.geopensim.evolution.ScenariosBase;
import fr.ign.cogit.geoxygene.appli.MainFrame;


/**
 * @author Florence Curie
 *
 */
public class ChoixParamSimul extends JDialog implements ActionListener, FocusListener{

	private static final long serialVersionUID = -2410212071568927811L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChoixParamSimul.class.getName());
	private JTextField dateSimul;
	String[] listeEtatsString = new String[10];
	String[] listeScenariosString = new String[10];
	private JComboBox comboEtatInitial,comboScenarios;
	private JButton boutonValidation, boutonAnnulation;
	private ScenariosBase configuration;
	private List<EtatGlobal> listeEtats = new ArrayList<EtatGlobal>();
	JFrame parent;
	List<Scenario> listeScenarios = new ArrayList<Scenario>();
	List<EvolutionRule> listeReglesEvol = new ArrayList<EvolutionRule>();
	private boolean valid = false;
	
	// Constructeur
	public ChoixParamSimul(final JFrame parent,List<EtatGlobal> listeEtats,EtatGlobal etatVisu,Integer dateS){

		super(parent,"Choix des paramètres de la simulation",true);
		this.listeEtats = listeEtats;
		this.parent = parent;

		// La fenêtre
//		this.setTitle("Choix des paramètres de la simulation");
		this.setBounds(50, 100, 450, 200);
		this.setResizable(false);
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
		
		// L'état initial
		int index=0;
		listeEtatsString = new String[listeEtats.size()];
		for (int i = 0;i<listeEtats.size();i++){
			listeEtatsString[i] = listeEtats.get(i).getNom();
			if(listeEtats.get(i)==etatVisu){
				index = i;
			}
		}
		comboEtatInitial = new JComboBox(listeEtatsString);
		comboEtatInitial.setMaximumSize(comboEtatInitial.getPreferredSize());	
		comboEtatInitial.setSelectedIndex(index);
		comboEtatInitial.addActionListener(this);
		Box hBoxEtatInitial = Box.createHorizontalBox();
		hBoxEtatInitial.add(new JLabel("Etat initial : "));
		hBoxEtatInitial.add(comboEtatInitial);
		hBoxEtatInitial.add(Box.createHorizontalGlue());

		// La date simulée
		Box hBoxDateSimulee = Box.createHorizontalBox();
		hBoxDateSimulee.add(new JLabel("Date simulée : "));
		hBoxDateSimulee.add(Box.createHorizontalStrut(10));
		dateSimul = new JTextField(5);
		dateSimul.setMaximumSize(dateSimul.getPreferredSize());
		dateSimul.setText(((Integer)dateS).toString());
		dateSimul.addFocusListener(this);
		hBoxDateSimulee.add(dateSimul);
		hBoxDateSimulee.add(Box.createHorizontalGlue());
		
		// Le scenario
		
		// Création de la liste des scénarios disponibles
		configuration = new ScenariosBase();
		configuration = ScenariosBase.getInstance();
		listeScenarios = configuration.getScenarios();
		listeScenariosString = new String[listeScenarios.size()];
		for (int i = 0;i<listeScenarios.size();i++){
			listeScenariosString[i] = listeScenarios.get(i).getNom();
		}
		
		comboScenarios = new JComboBox(listeScenariosString);
		comboScenarios.setMaximumSize(comboScenarios.getPreferredSize());	
		comboScenarios.setSelectedIndex(0);
		comboScenarios.addActionListener(this);
		Box hBoxScenarios = Box.createHorizontalBox();
		hBoxScenarios.add(new JLabel("Scenario : "));
		hBoxScenarios.add(comboScenarios);
		hBoxScenarios.add(Box.createHorizontalGlue());
		
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
		
		// Panneau 
		Box vBoxPanneau = Box.createVerticalBox();
		vBoxPanneau.add(hBoxEtatInitial);
		vBoxPanneau.add(Box.createVerticalStrut(5));
		vBoxPanneau.add(hBoxDateSimulee);
		vBoxPanneau.add(Box.createVerticalStrut(5));
		vBoxPanneau.add(hBoxScenarios);
		vBoxPanneau.add(Box.createVerticalStrut(5));
		vBoxPanneau.add(hBoxValidAnnul);
		vBoxPanneau.add(Box.createVerticalGlue());
				
		vBoxPanneau.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneau = new JPanel();
		panneau.setLayout(new javax.swing.BoxLayout(panneau, BoxLayout.Y_AXIS));
		panneau.add(vBoxPanneau);

        contenu.add(vBoxPanneau,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonAnnulation)){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource()==comboEtatInitial){
			String nomEtatInitial = this.comboEtatInitial.getSelectedItem().toString();
			EtatGlobal etat = null;
			for (EtatGlobal et:listeEtats){
				if (et.getNom().equals(nomEtatInitial))etat = et;
			}
			// on met à jour la carte
			((GeOpenSimApplication)((MainFrame)this.parent).getApplication()).affichageCarte(etat);
		}else if(e.getSource()==boutonValidation){
			int ind = this.comboScenarios.getSelectedIndex();
			Scenario scenario =  listeScenarios.get(ind);
			listeReglesEvol.clear();
            for (String rule : scenario.getRules()) {
                listeReglesEvol.add(EvolutionRuleBase.getInstance().getRule(rule));
            }
			valid = true;
			// Fermeture de la fenêtre
			setVisible(false);	
		}
	}

	public List<EvolutionRule> getRegles(){
		if (valid)	return listeReglesEvol;
		else return null;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(dateSimul)){
			int dateSim = Integer.parseInt(this.dateSimul.getText());
			// Mise à jour de la date de simulation
			((GeOpenSimApplication)((MainFrame)this.parent).getApplication()).setDateSimul(dateSim);
		}
	}

}
