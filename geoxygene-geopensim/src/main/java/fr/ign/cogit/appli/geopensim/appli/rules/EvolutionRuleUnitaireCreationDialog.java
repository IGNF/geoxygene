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
import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.geoxygene.filter.Filter;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire.Possibilites;


/**
 * @author Florence Curie
 *
 */
public class EvolutionRuleUnitaireCreationDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 7835704040491692178L;
	private JButton boutonValidation, boutonAnnulation;
	private JButton boutonCreerPrecond,boutonSupprimerPrecond;
	private JTextField precondition;
	private static Logger logger=Logger.getLogger(EvolutionRuleUnitaireCreationDialog.class.getName());
	private boolean valider = false;
	private EvolutionRuleUnitaire evolutionRuleUnit;
	private Filter[] precond=new Filter[1];;
	private JButton boutonAjouterPossib, boutonSupprimerPossib, boutonModifierPossib;
	private JList list;
	private List<Possibilites> listePossib = new ArrayList<Possibilites>();
	private DefaultListModel listModel;

	
	// Constructeur
	public EvolutionRuleUnitaireCreationDialog(JDialog parent, EvolutionRuleUnitaire evolRuleUnit){
		
		this(parent);
		this.evolutionRuleUnit = evolRuleUnit;
		// La précondition
		if(evolRuleUnit.getFilter()!=null){
			if(evolRuleUnit.getFilter().length>0){
				precond = evolRuleUnit.getFilter();
				if(precond[0]!=null){
					precondition.setText(precond[0].toString());
				}
			}
		}
		
		// Ajout des possibilités à la liste
		int index = listModel.size();
		for (Possibilites param : evolRuleUnit.getPossib()){
			String strAffich = makeString(param);
			listModel.addElement(strAffich);
			listePossib.add(param);
		}
		//Selection du nouvel item et visibilité
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
        if(listePossib.isEmpty()){
			boutonModifierPossib.setEnabled(false);
			boutonSupprimerPossib.setEnabled(false);
		}
	}

	// Constructeur
	public EvolutionRuleUnitaireCreationDialog(JDialog parent){

		// La fenêtre
		super (parent,"Ajout d'une règle d'évolution unitaire",true);
		this.setBounds(570, 100, 450, 450);
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

		// Panel de précondition
		JPanel preconditionPanel = new JPanel();
		preconditionPanel.setLayout(new GridLayout(0, 1));
		Border border = BorderFactory.createLineBorder(Color.gray);
		TitledBorder titlePrecond = BorderFactory.createTitledBorder(border, " Precondition ");
		titlePrecond.setTitleJustification(TitledBorder.LEFT);
		preconditionPanel.setBorder(titlePrecond);
		Box hBoxPreconditionPanel = Box.createHorizontalBox();
		hBoxPreconditionPanel.add(preconditionPanel);
		
		// La précondition
		Box hBoxPrecondition = Box.createHorizontalBox();
		precondition = new JTextField(50);
		precondition.setMaximumSize(precondition.getPreferredSize());	
		precondition.setEditable(false);
		hBoxPrecondition.add(precondition);
		hBoxPrecondition.add(Box.createHorizontalGlue());

		// les boutons de précondition
		boutonCreerPrecond = new JButton("Créer / modifier");
		boutonCreerPrecond.addActionListener(this);
		boutonSupprimerPrecond = new JButton("Supprimer");
		boutonSupprimerPrecond.addActionListener(this);
		Box hBoxBoutonsPrecondition = Box.createHorizontalBox();
		hBoxBoutonsPrecondition.add(Box.createHorizontalGlue());
		hBoxBoutonsPrecondition.add(boutonCreerPrecond);
		hBoxBoutonsPrecondition.add(Box.createHorizontalStrut(10));
		hBoxBoutonsPrecondition.add(boutonSupprimerPrecond);
		
		// Agencement dans le panel de precondition
		Box vBoxPrecondition = Box.createVerticalBox();
		vBoxPrecondition.add(hBoxPrecondition);
		vBoxPrecondition.add(Box.createVerticalStrut(10));
		vBoxPrecondition.add(hBoxBoutonsPrecondition);
		vBoxPrecondition.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		preconditionPanel.add(vBoxPrecondition);

		// Panel des possibilités
		JPanel expressionPanel = new JPanel();
		expressionPanel.setLayout(new GridLayout(0, 1));
		TitledBorder titleExpression = BorderFactory.createTitledBorder(border, " Possibilités ");
		titleExpression.setTitleJustification(TitledBorder.LEFT);
		expressionPanel.setBorder(titleExpression);
		Box hBoxExpressionPanel = Box.createHorizontalBox();
		hBoxExpressionPanel.add(expressionPanel);
		
		// Liste des possibités
		listModel = new DefaultListModel();
		list = new JList(listModel);
		JScrollPane defil = new JScrollPane(list);
		Box hBoxPossibilite = Box.createHorizontalBox();
		hBoxPossibilite.add(defil);

		// les boutons des possibilités
		boutonAjouterPossib = new JButton("ajouter");
		boutonAjouterPossib.addActionListener(this);
		boutonSupprimerPossib = new JButton("supprimer");
		boutonSupprimerPossib.addActionListener(this);
		boutonModifierPossib = new JButton("modifier");
		boutonModifierPossib.addActionListener(this);
		
		Box hBoxAjoutSupprZone = Box.createHorizontalBox();
		hBoxAjoutSupprZone.add(boutonAjouterPossib);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonSupprimerPossib);
		hBoxAjoutSupprZone.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprZone.add(boutonModifierPossib);
		hBoxAjoutSupprZone.add(Box.createHorizontalGlue());
		
		// Agencement dans le panel des possibilités
		Box vBoxExpression = Box.createVerticalBox();
		vBoxExpression.add(hBoxPossibilite);
		vBoxExpression.add(Box.createVerticalStrut(10));
		vBoxExpression.add(hBoxAjoutSupprZone);
		vBoxExpression.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		expressionPanel.add(vBoxExpression);

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
		
		// Boite verticale dans le panneau 
		Box vBoxZone = Box.createVerticalBox();
		vBoxZone.add(preconditionPanel);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(expressionPanel);
		vBoxZone.add(Box.createVerticalStrut(3));
		vBoxZone.add(hBoxValidAnnul);
		vBoxZone.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenu.add(vBoxZone,BorderLayout.CENTER);
	}
	
	public String makeString(Possibilites possib){
		String strAff = possib.getPropertyName()+" = "+possib.getExpression().toString()+" ("+possib.getProbability()+")";
		
		return strAff;
	}
	
	
	public EvolutionRuleUnitaire getRuleUnit(){
		valider = false;
		setVisible(true);
		if (valider) return evolutionRuleUnit;
		else return null;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonCreerPrecond){
			if(precond==null){
				PreconditionDialog fenetrePrecond = new PreconditionDialog(this);
				precond = fenetrePrecond.getPrecondition();
				if(precond!=null){
					precondition.setText(precond[0].toString());
				}
			}else{
				PreconditionDialog fenetrePrecond = new PreconditionDialog(this,precond);
				precond = fenetrePrecond.getPrecondition();
				if(precond!=null){
					precondition.setText(precond[0].toString());
				}
			}
		}else if (e.getSource()==this.boutonAjouterPossib){
			PossibiliteCreationDialog fenetrePossib = new PossibiliteCreationDialog(this);
			Possibilites possib = fenetrePossib.getPossib();
			if (possib!=null){
				// ajout de la nouvelle possibilité à la liste
				String strAffich = makeString(possib);
				logger.info(strAffich);
				// Ajout de la possibilité à la liste
				int index = listModel.size();
				listModel.insertElementAt(strAffich,index);
				listePossib.add(possib);
				//Selection du nouvel item et visibilité
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
				if (listModel.size()>0){
					boutonSupprimerPossib.setEnabled(true);
					boutonModifierPossib.setEnabled(true);
				}
				// réactivation des boutons modifier et supprimer
				boutonModifierPossib.setEnabled(true);
				boutonSupprimerPossib.setEnabled(true);
			}
			
		}else if (e.getSource()==this.boutonModifierPossib){
			int index = list.getSelectedIndex();
			PossibiliteCreationDialog fenetre2 = new PossibiliteCreationDialog(this,listePossib.get(index));
			Possibilites poss = fenetre2.getPossib();
			if (poss!=null){
				listePossib.set(index, poss);
				String strAffich = makeString(poss);
				// Modification de la liste des possibilités
				listModel.setElementAt(strAffich,index);
			}
		}else if (e.getSource()==this.boutonSupprimerPossib){
			int index = list.getSelectedIndex();
			// Suppresion de l'élément
			listModel.remove(index);
			listePossib.remove(index);
			// Si la liste est vide on désactive le bouton supprimer
			if (listModel.size() == 0) { 
				boutonSupprimerPossib.setEnabled(false);
				boutonModifierPossib.setEnabled(false);
			}else{// sinon on sélectionne un élément
				if (index == listModel.getSize()) {index--;}
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
		}else if(e.getSource()==this.boutonValidation){
			evolutionRuleUnit.setFilter(precond);
			evolutionRuleUnit.setPossib(listePossib);
			// Fermeture de la fenêtre
			valider = true;
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);	
			dispose();
		}
	}

}
