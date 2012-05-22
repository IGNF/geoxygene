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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.BinaryComparisonOpsType;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.Not;
import fr.ign.cogit.geoxygene.filter.Or;
import fr.ign.cogit.geoxygene.filter.UnaryLogicOpsType;

import org.apache.log4j.Logger;

/**
 * @author Florence Curie
 *
 */
public class PreconditionDialog extends JDialog implements ActionListener,ListSelectionListener{

	private static final long serialVersionUID = 7861972282318924788L;
	private static final Logger logger = Logger.getLogger(PreconditionDialog.class.getName());
	private JButton boutonValidation, boutonAnnulation,boutonEffacer;
	private JButton boutonDefinirNouvelle,boutonAnd,boutonOr,boutonNot,boutonAjout;
	private JButton boutonAjouter, boutonSupprimer, boutonModifier;
	private DefaultListModel listModel;
	private List<Filter> listePrecondUnit = new ArrayList<Filter>();
	private JList list;
	private boolean valider = false;
	private JTextArea textArea;
	private Filter operateur = null;
	private List<Filter> listePrecondOp = new ArrayList<Filter>();
	private Filter[] filter;

	public PreconditionDialog(JDialog parent, Filter[] filt){
		this(parent);
		if(filt[0]!=null){
			logger.info(filt[0].toString());
			// Création du filtre
			listePrecondOp = new ArrayList<Filter>();
			if(filt[0] instanceof BinaryLogicOpsType){
				listePrecondOp.addAll(((BinaryLogicOpsType)filt[0]).getOps());
				if(filt[0] instanceof And){
					operateur = new And();
					((BinaryLogicOpsType)operateur).getOps().addAll(listePrecondOp);
				}else if(filt[0] instanceof Or){
					operateur = new Or();
					((BinaryLogicOpsType)operateur).getOps().addAll(listePrecondOp);
				}
			}else if(filt[0] instanceof UnaryLogicOpsType){
				listePrecondOp.add(((UnaryLogicOpsType)filt[0]).getOp());
				operateur = new Not();
				((UnaryLogicOpsType)operateur).setOp(listePrecondOp.get(0));
			}else if(filt[0] instanceof BinaryComparisonOpsType){
				listePrecondOp.add(filt[0]);
			}
			filter = new Filter[1];
			filter[0] = operateur;
			listePrecondUnit = new ArrayList<Filter>();
			listePrecondUnit.addAll(listePrecondOp);
			// Remplissage du champs contenant les PU et de la zone de texte
			for (Filter fil:listePrecondUnit){
				listModel.addElement(fil.toString());
			}
			list.setSelectedIndex(0);
			String strAff = makeString();
			textArea.setText(strAff);
			// Gestion des boutons
			if(listePrecondUnit.size()>0){
				boutonModifier.setEnabled(true);
				boutonSupprimer.setEnabled(true);
			}
			boutonValidation.setEnabled(true);
			boutonDefinirNouvelle.setEnabled(true);
			boutonAnd.setEnabled(false);
			boutonNot.setEnabled(false);
			boutonOr.setEnabled(false);
		}
	}
	
	// Constructeur
	public PreconditionDialog(JDialog parent){

		super (parent,"Création d'une précondition",true);
		// La fenêtre
		this.setBounds(570, 100, 750, 400);
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

		// Panneau de gauche
		// La liste des paramètres possibles
		Box hBoxListe = Box.createHorizontalBox();
		hBoxListe.add(new JLabel("Liste des préconditions unitaires : "));
		hBoxListe.add(Box.createHorizontalGlue());
		// Création de la liste des méthodes
		listModel = new DefaultListModel();
		listePrecondUnit = new ArrayList<Filter>();
		list = new JList(listModel);
		list.addListSelectionListener(this);
		JScrollPane defil = new JScrollPane(list);
		defil.setPreferredSize(new Dimension(260, 220));
		
		// Les 3 boutons
		boutonAjouter = new JButton("ajouter");
		boutonAjouter.addActionListener(this);
		boutonSupprimer = new JButton("supprimer");
		boutonSupprimer.addActionListener(this);
		boutonSupprimer.setEnabled(false);
		boutonModifier = new JButton("modifier");
		boutonModifier.addActionListener(this);
		boutonModifier.setEnabled(false);
		Box hBoxAjoutSuppr = Box.createHorizontalBox();
		hBoxAjoutSuppr.add(boutonAjouter);
		hBoxAjoutSuppr.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSuppr.add(boutonSupprimer);
		hBoxAjoutSuppr.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSuppr.add(boutonModifier);
		hBoxAjoutSuppr.add(Box.createHorizontalGlue());
		
		// Panneau de gauche
		Box vBoxGauche = Box.createVerticalBox();
		vBoxGauche.add(hBoxListe);
		vBoxGauche.add(Box.createVerticalStrut(5));
		vBoxGauche.add(defil);
		vBoxGauche.add(Box.createVerticalStrut(10));
		vBoxGauche.add(hBoxAjoutSuppr);
		vBoxGauche.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneauGauche = new JPanel();
		panneauGauche.setLayout(new FlowLayout());
		panneauGauche.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneauGauche.setMaximumSize(new Dimension(0,600));

		panneauGauche.add(vBoxGauche);
		
		// Panneau du milieu
		Box hboxAjout = Box.createHorizontalBox();
		boutonAjout = new JButton("Ajout >>");
		boutonAjout.setEnabled(false);
		boutonAjout.addActionListener(this);
		hboxAjout.add(boutonAjout);
		
		Box hboxLogique = Box.createHorizontalBox();
		boutonAnd = new JButton("And");
		boutonAnd.setEnabled(false);
		boutonAnd.addActionListener(this);
		hboxLogique.add(boutonAnd);
		hboxLogique.add(Box.createRigidArea(new Dimension(5,0)));
		boutonOr = new JButton("Or");
		boutonOr.setEnabled(false);
		boutonOr.addActionListener(this);
		hboxLogique.add(boutonOr);
		hboxLogique.add(Box.createRigidArea(new Dimension(5,0)));
		boutonNot = new JButton("Not");
		boutonNot.setEnabled(false);
		boutonNot.addActionListener(this);
		hboxLogique.add(boutonNot);
		
		Box hboxNouvelle = Box.createHorizontalBox();
		boutonDefinirNouvelle = new JButton("<< Nouvelle PU");
		boutonDefinirNouvelle.setEnabled(false);
		boutonDefinirNouvelle.addActionListener(this);
		hboxNouvelle.add(boutonDefinirNouvelle);
		
		
		Box vBoxCentre = Box.createVerticalBox();
		vBoxCentre.add(hboxLogique);
		vBoxCentre.add(Box.createVerticalStrut(10));
		vBoxCentre.add(hboxAjout);
		
		vBoxCentre.add(Box.createVerticalStrut(25));
		vBoxCentre.add(hboxNouvelle);
		vBoxCentre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel panneauCentre = new JPanel(new FlowLayout());
		panneauCentre.setMaximumSize(new Dimension(0,300));
		panneauCentre.add(vBoxCentre);
		
		// La liste des paramètres sélectionnés
		Box hBoxRecap1 = Box.createHorizontalBox();
		hBoxRecap1.add(new JLabel("Précondition : "));
		hBoxRecap1.add(Box.createHorizontalGlue());
		boutonEffacer = new JButton("Effacer");
		boutonEffacer.addActionListener(this);
		hBoxRecap1.add(boutonEffacer);
		
		// Création de la zone de texte
		textArea = new JTextArea();
		textArea.setLineWrap(true); 
		textArea.setEditable(false);
		textArea.setText("");
		JScrollPane defil2 = new JScrollPane(textArea);
		defil2.setPreferredSize(new Dimension(260, 220));
		
		// Panneau de droite
		Box vBoxDroite = Box.createVerticalBox();
		vBoxDroite.add(hBoxRecap1);
		vBoxDroite.add(Box.createVerticalStrut(10));
		vBoxDroite.add(defil2);
		vBoxDroite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneauDroite = new JPanel();
		panneauDroite.setLayout(new javax.swing.BoxLayout(panneauDroite, BoxLayout.Y_AXIS));
		panneauDroite.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneauDroite.add(vBoxDroite);
		
		// Bouton de validation
		Box hBoxValidation = Box.createHorizontalBox();
		hBoxValidation.add(Box.createHorizontalGlue());
		boutonValidation = new JButton("Valider");
		boutonValidation.setEnabled(false);
		boutonValidation.addActionListener(this);
		hBoxValidation.add(boutonValidation);
		hBoxValidation.add(Box.createRigidArea(new Dimension(10,0)));
		boutonAnnulation = new JButton("Annuler");
		boutonAnnulation.addActionListener(this);
		hBoxValidation.add(boutonAnnulation);
		
		// L'agencement des vbox
		Box hBoxHaut = Box.createHorizontalBox();
		hBoxHaut.add(panneauGauche);
		hBoxHaut.add(panneauCentre);
		hBoxHaut.add(panneauDroite);
		
		// L'agencement final
		Box vBoxFinal = Box.createVerticalBox();
		vBoxFinal.add(hBoxHaut);
		vBoxFinal.add(Box.createVerticalStrut(10));
		vBoxFinal.add(hBoxValidation);
		vBoxFinal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenu.add(vBoxFinal,BorderLayout.CENTER);
	}

	public Filter[] getPrecondition(){
		valider = false;
		setVisible(true);
		if (valider) return filter;
		else return null;
	}
	
	public String makeString(){
		String strAff = "";
		if (operateur!=null){
			if(operateur instanceof UnaryLogicOpsType){
				strAff = strAff + " "+operateur.getClass().getSimpleName()+" ";
				if(listePrecondOp.size()>0){
					strAff += "("+listePrecondOp.get(0)+")";
				}
			}
			if(operateur instanceof BinaryLogicOpsType){
				if(listePrecondOp.size()>0){
					strAff += "("+listePrecondOp.get(0)+")";
				}
				strAff += " "+operateur.getClass().getSimpleName()+" ";
				if(listePrecondOp.size()>1){
					strAff += "("+listePrecondOp.get(1)+")";
				}
			}
		}else if((listePrecondUnit!=null)&&(listePrecondUnit.size()>0)){
			int ind = list.getSelectedIndex();
			strAff += listePrecondUnit.get(ind);
		}
		return strAff;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonValidation)){
			// Création du filtre
			filter = new Filter[1];
			if(operateur instanceof BinaryLogicOpsType){
				if(listePrecondOp.size()==2){
					((BinaryLogicOpsType)operateur).getOps().addAll(listePrecondOp);
					filter[0] = operateur;
				}
			}else if(operateur instanceof UnaryLogicOpsType){
				if(listePrecondOp.size()==1){
					((UnaryLogicOpsType)operateur).setOp(listePrecondOp.get(0));
					filter[0] = operateur;
				}
			}else{
				filter[0] = listePrecondOp.get(0);
			}
			valider = true;
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonAnnulation)){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonAjout)){
			if(operateur instanceof BinaryLogicOpsType){
				if(listePrecondOp.size()<2){
					listePrecondOp.add(listePrecondUnit.get(list.getSelectedIndex()));
				}
				if(listePrecondOp.size()==2){
					boutonAjout.setEnabled(false);
					boutonDefinirNouvelle.setEnabled(true);
					boutonValidation.setEnabled(true);
				}
			}else if(operateur instanceof UnaryLogicOpsType){
				if(listePrecondOp.size()<1){
					listePrecondOp.add(listePrecondUnit.get(list.getSelectedIndex()));
				}
				if(listePrecondOp.size()==1){
					boutonAjout.setEnabled(false);
					boutonDefinirNouvelle.setEnabled(true);
					boutonValidation.setEnabled(true);
				}
			}else {
				listePrecondOp.add(listePrecondUnit.get(list.getSelectedIndex()));
				boutonAjout.setEnabled(false);
				boutonValidation.setEnabled(true);
				boutonAnd.setEnabled(false);
				boutonNot.setEnabled(false);
				boutonOr.setEnabled(false);
			}
			String strAff = makeString();
			textArea.setText(strAff);
		}else if(e.getSource().equals(boutonDefinirNouvelle)){
			if(operateur instanceof BinaryLogicOpsType){
				((BinaryLogicOpsType)operateur).getOps().addAll(listePrecondOp);
			}else if(operateur instanceof UnaryLogicOpsType){
				((UnaryLogicOpsType)operateur).setOp(listePrecondOp.get(0));
			}
			// Ajout de la précondition unitaire à la liste
			int index = listModel.size();
			String strAffich = operateur.toString();
			listModel.insertElementAt(strAffich,index);
			listePrecondUnit.add(operateur);
			//Selection du nouvel item et visibilité
			list.setSelectedIndex(index);
			list.ensureIndexIsVisible(index);
			if (listModel.size()>0){
				boutonSupprimer.setEnabled(true);
				boutonModifier.setEnabled(true);
			}
			// On efface la précondition
			operateur = null;
			listePrecondOp = new ArrayList<Filter>();
			textArea.setText("");
			// Gestion des boutons
			boutonDefinirNouvelle.setEnabled(false);
			boutonValidation.setEnabled(false);
			if((listModel.size()==1)){
				boutonAjout.setEnabled(true);
				boutonNot.setEnabled(true);
				boutonAnd.setEnabled(false);
				boutonOr.setEnabled(false);
			}else if(listModel.size()>1){
				boutonAjout.setEnabled(true);
				boutonNot.setEnabled(true);
				boutonAnd.setEnabled(true);
				boutonOr.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonAnd)){
			operateur = new And();
			String strAff = makeString();
			textArea.setText(strAff);
			// Gestion des boutons
			boutonAnd.setEnabled(false);
			boutonOr.setEnabled(false);
			boutonNot.setEnabled(false);
			if(listePrecondUnit.size()>0){
				boutonAjout.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonOr)){
			operateur = new Or();
			String strAff = makeString();
			textArea.setText(strAff);
			// Gestion des boutons
			boutonAnd.setEnabled(false);
			boutonOr.setEnabled(false);
			boutonNot.setEnabled(false);
			if(listePrecondUnit.size()>0){
				boutonAjout.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonNot)){
			operateur = new Not();
			String strAff = makeString();
			textArea.setText(strAff);
			// Gestion des boutons
			boutonAnd.setEnabled(false);
			boutonOr.setEnabled(false);
			boutonNot.setEnabled(false);
			if(listePrecondUnit.size()>0){
				boutonAjout.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonEffacer)){
			operateur = null;
			listePrecondOp = new ArrayList<Filter>();
			// Gestion des boutons
			boutonDefinirNouvelle.setEnabled(false);
			boutonValidation.setEnabled(false);
			textArea.setText("");
			// Boutons opérateurs
			if((listModel.size()==1)){
				boutonAjout.setEnabled(true);
				boutonNot.setEnabled(true);
			}else if(listModel.size()>1){
				boutonAjout.setEnabled(true);
				boutonNot.setEnabled(true);
				boutonAnd.setEnabled(true);
				boutonOr.setEnabled(true);
			}
		}else if(e.getSource().equals(boutonAjouter)){
			PreconditionUnitaireDialog fenetre2 = new PreconditionUnitaireDialog(this);
			BinaryComparisonOpsType compar = fenetre2.getPreconditionUnitaire();
			// ajout de la nouvelle précondition à la liste
			if (compar!=null){
				logger.info(compar.toString());
				// Ajout de la précondition unitaire à la liste
				int index = listModel.size();
				String strAffich = compar.toString();
				listModel.insertElementAt(strAffich,index);
				listePrecondUnit.add(compar);
				//Selection du nouvel item et visibilité
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
				// Boutons des préconditions unitaires
				if (listModel.size()>0){
					boutonSupprimer.setEnabled(true);
					boutonModifier.setEnabled(true);
				}
				// Boutons opérateurs
				if((listModel.size()==1)&&((textArea.getText()=="")||(textArea.getText().isEmpty()))){
					boutonAjout.setEnabled(true);
					boutonNot.setEnabled(true);
				}else if(listModel.size()>1){
					if((textArea.getText()=="")||(textArea.getText().isEmpty())){
						boutonAjout.setEnabled(true);
						boutonNot.setEnabled(true);
						boutonAnd.setEnabled(true);
						boutonOr.setEnabled(true);
					}else if(operateur!=null){
						if(operateur instanceof BinaryLogicOpsType){
							if(((BinaryLogicOpsType)operateur).getOps().size()<2){
								boutonAjout.setEnabled(true);
							}
						}else if(operateur instanceof UnaryLogicOpsType){
							if(((UnaryLogicOpsType)operateur).getOp()==null){
								boutonAjout.setEnabled(true);
							}
						}
					}
				}
			}
		}else if(e.getSource().equals(boutonModifier)){
			int index = list.getSelectedIndex();
			PreconditionUnitaireDialog fenetre2 = new PreconditionUnitaireDialog(this,(BinaryComparisonOpsType)listePrecondUnit.get(index));
			BinaryComparisonOpsType compar = fenetre2.getPreconditionUnitaire();
			if (compar!=null){
				listePrecondUnit.set(index, compar);
				String strAffich = compar.toString();
				// Modification de la liste des préconditions unitaires
				listModel.setElementAt(strAffich,index);
			}
		}else if(e.getSource().equals(boutonSupprimer)){
			int index = list.getSelectedIndex();
			// Suppresion de l'élément
			listModel.remove(index);
			listePrecondUnit.remove(index);
			// Si la liste est vide on désactive le bouton supprimer
			if (listModel.size() == 0) { 
				boutonSupprimer.setEnabled(false);
				boutonModifier.setEnabled(false);
			}else{// sinon on sélectionne un élément
				if (index == listModel.getSize()) {index--;}
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()){
			int index = list.getSelectedIndex();
			if(index!=-1){
				Filter prec = listePrecondUnit.get(index);
				if(prec instanceof BinaryComparisonOpsType){boutonModifier.setEnabled(true);}
				else{boutonModifier.setEnabled(false);}
			}
		}
	}
	
}


