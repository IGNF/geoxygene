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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.filter.BinaryComparisonOpsType;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

import org.apache.log4j.Logger;

/**
 * @author Florence Curie
 *
 */
public class PreconditionUnitaireDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 7861972282318924788L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PreconditionUnitaireDialog.class.getName());
	private JButton boutonValidation, boutonAnnulation;
	private JTextField valeur;
	private JComboBox comboTypeOperateur, comboParametres;
	private List<Method> listeMethodes;
	private boolean ok;
	private BinaryComparisonOpsType comp = null;
	private String[] typeParam;
	private String[] typeOp = {"==",">",">=","<","<=","<>"};

	
	public PreconditionUnitaireDialog(JDialog parent, BinaryComparisonOpsType comparateur){
		this(parent);
		
		// Le paramètre
		String param = comparateur.getPropertyName().toString();
		int index = -1;
		for (int i=0;i<typeParam.length;i++){
			if (typeParam[i].equals(param)){
				index =i;
				break;
			}
		}
		comboParametres.setSelectedIndex(index);
		
		// Le comparateur
		String compar = comparateur.getClass().getSimpleName().toString();
		if (compar.equals("PropertyIsEqualTo")){compar = "==";}
		else if (compar.equals("PropertyIsGreaterThan")){compar = ">";}
		else if (compar.equals("PropertyIsGreaterThanOrEqualTo")){compar = ">=";}
		else if (compar.equals("PropertyIsLessThan")){compar = "<";}
		else if (compar.equals("PropertyIsLessThanOrEqualTo")){compar = "<=";}
		else if (compar.equals("PropertyIsNotEqualTo")){compar = "<>";}
		int index2 = -1;
		for (int i=0;i<typeOp.length;i++){
			if (typeOp[i].equals(compar)){
				index2 =i;
				break;
			}
		}
		comboTypeOperateur.setSelectedIndex(index2);
		
		// La valeur
		String val = comparateur.getLiteral().getValue();
		valeur.setText(val);
	}
	
	// Constructeur
	public PreconditionUnitaireDialog(JDialog parent){

		super (parent,"Création d'une précondition",true);
		// La fenêtre
		this.setBounds(50, 100, 380, 280);
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

		// La liste des paramètres possibles
		Box hBoxListe = Box.createHorizontalBox();
		hBoxListe.add(new JLabel("Liste des paramètres : "));
		hBoxListe.add(Box.createHorizontalGlue());
		// Création de la liste des méthodes
		listeMethodes = new ArrayList<Method>();
		Method[] methods = ZoneElementaireUrbaine.class.getMethods();
		for (int i=0;i<methods.length;i++){
			Method methode = methods[i];
			if (methode.getName().startsWith("get")){
				Class<?> returnType = methode.getReturnType();
				String valueType = returnType.getSimpleName();
				if (returnType.isPrimitive()) {
					if ((valueType.equals("int"))||(valueType.equals("double"))||(valueType.equals("String"))) {
						listeMethodes.add(methode);
					}
				}
			}
		}
		typeParam = new String[listeMethodes.size()];
		for (int i=0;i<listeMethodes.size();i++){
			typeParam[i] = listeMethodes.get(i).getName().replaceFirst("get", "");
		}
		comboParametres = new JComboBox(typeParam);
		comboParametres.setMaximumSize(comboParametres.getPreferredSize());	
		Box hBoxListeMethode = Box.createHorizontalBox();
		hBoxListeMethode.add(comboParametres);
		hBoxListeMethode.add(Box.createHorizontalGlue());

		// Le comparateur
		Box hBoxTypeOperateur1 = Box.createHorizontalBox();
		hBoxTypeOperateur1.add(new JLabel("Type d'opérateur "));
		hBoxTypeOperateur1.add(Box.createHorizontalGlue());
		
		comboTypeOperateur = new JComboBox(typeOp);
		comboTypeOperateur.setMaximumSize(comboTypeOperateur.getPreferredSize());	
		Box hBoxTypeOperateur2 = Box.createHorizontalBox();
		hBoxTypeOperateur2.add(comboTypeOperateur);
		hBoxTypeOperateur2.add(Box.createHorizontalGlue());
		
		// La valeur à comparer
		Box hBoxValeur1 = Box.createHorizontalBox();
		hBoxValeur1.add(new JLabel("Valeur"));
		hBoxValeur1.add(Box.createHorizontalGlue());
		Box hBoxValeur2 = Box.createHorizontalBox();
		valeur = new JTextField(10);
		valeur.setMaximumSize(valeur.getPreferredSize());		
		hBoxValeur2.add(valeur);
		hBoxValeur2.add(Box.createHorizontalGlue());
		
		// Panneau 
		Box vBox = Box.createVerticalBox();
		vBox.add(hBoxListe);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxListeMethode);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxTypeOperateur1);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxTypeOperateur2);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxValeur1);
		vBox.add(Box.createVerticalStrut(5));
		vBox.add(hBoxValeur2);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel panneau = new JPanel();
		panneau.setLayout(new javax.swing.BoxLayout(panneau, BoxLayout.Y_AXIS));
		panneau.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panneau.add(vBox);
		
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
		
		// L'agencement final
		Box vBoxFinal = Box.createVerticalBox();
		vBoxFinal.add(panneau);
		vBoxFinal.add(Box.createVerticalStrut(20));
		vBoxFinal.add(hBoxValidation);
		vBoxFinal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contenu.add(vBoxFinal,BorderLayout.CENTER);
	}

	public BinaryComparisonOpsType getPreconditionUnitaire(){
		ok = false;
		setVisible(true);
		if (ok)	return comp;
		else return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(boutonValidation)){
			// Création d'une propriété
			comp = null;
			if (comboTypeOperateur.getSelectedItem().equals("==")){comp = new PropertyIsEqualTo();}
			else if (comboTypeOperateur.getSelectedItem().equals(">")){comp = new PropertyIsGreaterThan();}
			else if (comboTypeOperateur.getSelectedItem().equals(">=")){comp = new PropertyIsGreaterThanOrEqualTo();}
			else if (comboTypeOperateur.getSelectedItem().equals("<")){comp = new PropertyIsLessThan();}
			else if (comboTypeOperateur.getSelectedItem().equals("<=")){comp = new PropertyIsLessThanOrEqualTo();}
			else if (comboTypeOperateur.getSelectedItem().equals("<>")){comp = new PropertyIsNotEqualTo();}

			if (comp!=null){
				comp.setPropertyName(new PropertyName(comboParametres.getSelectedItem().toString()));
				comp.setLiteral(new Literal(valeur.getText()));
				ok = true;
			}
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}else if(e.getSource().equals(boutonAnnulation)){
			comp=null;
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}
	}
}


