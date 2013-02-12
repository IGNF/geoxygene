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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypeMethode;
import fr.ign.cogit.appli.geopensim.ConfigurationLienTypeFonctionnelMethodePeuplement.TypePeuplement;
import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.ParametresMethodesPeuplement;


/**
 * @author Florence Curie
 *
 */
public class CreationTypePeuplementDialog extends JDialog implements ActionListener,FocusListener{
	
	private static final long serialVersionUID = 8095560128112909507L;
	private JButton boutonValidation, boutonAnnulation, boutonAjout,boutonSuppression,boutonModification;
	private JTextField pourcentage;
	private JLabel sommeFrequence;
	private DefaultListModel listModel;
	private JList list;
	private List<TypeMethode> listeMethodesPourc = new ArrayList<TypeMethode>();
	private static Logger logger=Logger.getLogger(CreationTypePeuplementDialog.class.getName());
	private String nouveauNom; 
	private ParametresMethodesPeuplement methodePeuplement;
	private boolean valider = false;
	private double pourc;
	private TypePeuplement typeP;
	
	// Constructeur
	public CreationTypePeuplementDialog(JFrame parent, TypePeuplement typePeuplement){
		this(parent);
		typeP = typePeuplement;
	
		// La fréquence
		pourcentage.setText(String.valueOf(typeP.getFrequence()));
		
		// La liste des types de méthodes
		List<TypeMethode> listeTM = typeP.getParametresPeuplement();
		int index = listModel.size();
		double somme = 0;
		for (TypeMethode param :listeTM){
			String strAffich = creationString(param.getNomMethodePeuplement(),param.getPourcentage());
			listModel.addElement(strAffich);
			listeMethodesPourc.add(param);
			somme += param.getPourcentage();
		}
		// Sélection du nouvel item et visibilité
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
        if (listModel.size()>0){
        	 boutonSuppression.setEnabled(true);
        	 boutonModification.setEnabled(true);
        }
        
        // MAJ de la somme des fréquences
		if (somme!=-1){
			sommeFrequence.setText(String.valueOf(somme));
			// Si la somme des fréquences est supérieure à 100% 
			if (somme>100){
				JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
						"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	
	// Constructeur
	public CreationTypePeuplementDialog(JFrame parent){

		// La fenêtre
		super (parent,"Ajout d'un type de peuplement",true);
		this.setBounds(500, 100, 420, 400);
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

		// La fréquence
		pourcentage = new JTextField(5);
		pourcentage.setMaximumSize(pourcentage.getPreferredSize());	
		pourcentage.addFocusListener(this);
		Box hBoxPourcentage = Box.createHorizontalBox();
		hBoxPourcentage.add(new JLabel("Pourcentage : "));
		hBoxPourcentage.add(pourcentage);
		hBoxPourcentage.add(Box.createHorizontalGlue());
		
		// Le titre de la liste
		Box hBoxTitre = Box.createHorizontalBox();
		hBoxTitre.add(new JLabel("Liste des méthodes et pourcentages : "));
		hBoxTitre.add(Box.createHorizontalGlue());
		
		// Liste des méthodes et pourcentages
		listModel = new DefaultListModel();
		list = new JList(listModel);
		JScrollPane defil = new JScrollPane(list);
		Box hBoxListeForme = Box.createHorizontalBox();
		hBoxListeForme.add(defil);
		
		// Ajouter / supprimer / modifier une méthode
		boutonAjout = new JButton("ajouter");
		boutonAjout.addActionListener(this);
		boutonSuppression = new JButton("supprimer");
		boutonSuppression.addActionListener(this);
		boutonSuppression.setEnabled(false);
		boutonModification = new JButton("modifier");
		boutonModification.addActionListener(this);
		boutonModification.setEnabled(false);
		Box hBoxAjoutSupprForme = Box.createHorizontalBox();
		hBoxAjoutSupprForme.add(boutonAjout);
		hBoxAjoutSupprForme.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprForme.add(boutonSuppression);
		hBoxAjoutSupprForme.add(Box.createRigidArea(new Dimension(10,0)));
		hBoxAjoutSupprForme.add(boutonModification);
		hBoxAjoutSupprForme.add(Box.createHorizontalGlue());
		
		// La somme des fréquences
		Box hBoxSommeFrequence = Box.createHorizontalBox();
		hBoxSommeFrequence.add(new JLabel("Somme des fréquences : "));
		sommeFrequence = new JLabel("0.0");
		hBoxSommeFrequence.add(sommeFrequence);
		hBoxSommeFrequence.add(Box.createHorizontalGlue());
		
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
		
		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxPourcentage);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxTitre);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxListeForme);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxAjoutSupprForme);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBoxSommeFrequence);
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBoxValidAnnul);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
  
        contenu.add(vBox,BorderLayout.CENTER);
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		 if (e.getSource()==this.pourcentage){
			// Le pourcentage associé à ce nouveau type
			pourc = -1;
			String strPourc1 = "Le pourcentage entré n'est pas un double";
			String strPourc2 = "Voulez vous entrer un autre pourcentage ?";
			String strPourc3 = "Entrez un nouveau pourcentage pour la type";
			pourc = CreationTypePeuplementDialog.verifDouble(this.pourcentage, strPourc1, strPourc2, strPourc3);
		}
	}
	
	public Map<String,ParametresMethodesPeuplement> getMethode(){
		Map<String,ParametresMethodesPeuplement> methode = new HashMap<String,ParametresMethodesPeuplement>();
		if (valider){
			methode.put(nouveauNom, methodePeuplement);
		}
		return methode;
	}
	
	public TypePeuplement getTypePeuplement(){
		valider = false;
		setVisible(true);
		if (valider)	return typeP;
		else return typeP;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonValidation){	
			// Le pourcentage associé à ce type de peuplement
			if(this.pourcentage.getText().isEmpty()){pourc=-1;}
			else{pourc = Double.parseDouble(this.pourcentage.getText());}
			// Si la somme des fréquences est inférieure à 100% 
			double somme = Double.parseDouble(sommeFrequence.getText());
			if (somme<100){
				JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou ajouter une méthode de peuplement", 
						"la somme des fréquences est inférieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
			}else{
				// Création d'un nouveauType de peuplement 
				typeP = new TypePeuplement(pourc, listeMethodesPourc);
				valider = true;
				// Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}
		}else if (e.getSource()==this.boutonAnnulation){
			setVisible(false);	
			dispose();
		}else if (e.getSource()==this.boutonAjout){
			ParametresPeuplementDialog fenetre2 = new ParametresPeuplementDialog(this);
			TypeMethode typeM = fenetre2.getTypeMethode();
			if (typeM != null){
				String nomM = typeM.getNomMethodePeuplement();
				double pourcent = typeM.getPourcentage();
				// Ajout de la méthode à la liste
				int position = -1;
				for (int i = 0;i<listeMethodesPourc.size();i++){
					String nom = listeMethodesPourc.get(i).getNomMethodePeuplement();
					if(nomM.equals(nom)){
						position = i;
					}
				}
				int index = listModel.size();
				if(position==-1){
					String strAffich = creationString(nomM,pourcent);
					listModel.insertElementAt(strAffich,index);
					listeMethodesPourc.add(typeM);
				}else{
					index = position;
					double fr = listeMethodesPourc.get(position).getPourcentage();
					typeM.setPourcentage(pourcent+fr);
					String strAffich = creationString(nomM,pourcent+fr);
					listModel.set(index,strAffich);
					listeMethodesPourc.set(index,typeM);
				}
				// Sélection du nouvel item et visibilité
	            list.setSelectedIndex(index);
	            list.ensureIndexIsVisible(index);
	            if (listModel.size()>0){
	            	 boutonSuppression.setEnabled(true);
	            	 boutonModification.setEnabled(true);
	            }
	            // MAJ de la somme des fréquences
				if (pourcent!=-1){
					double somme = Double.parseDouble(sommeFrequence.getText())+pourcent;
					sommeFrequence.setText(String.valueOf(somme));
					// Si la somme des fréquences est supérieure à 100% 
					if (somme>100){
						JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
								"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
			
		}else if (e.getSource()==this.boutonSuppression){
			 int index = list.getSelectedIndex();
			 // MAJ de la somme des fréquences
			 if (listeMethodesPourc.get(index).getPourcentage()!=-1){
				 double somme = Double.parseDouble(sommeFrequence.getText())-listeMethodesPourc.get(index).getPourcentage();
				 sommeFrequence.setText(String.valueOf(somme));
			 }
	         // Suppression de l'élément
	         listModel.remove(index);
	         listeMethodesPourc.remove(index);
	         	         
	         if (listModel.size() == 0) {// Si la liste est vide on désactive le bouton supprimer 
	        	 boutonSuppression.setEnabled(false);
	         }else{// On sélectionne un élément
	        	 if (index == listModel.getSize()) {index--;}
	        	 list.setSelectedIndex(index);
	        	 list.ensureIndexIsVisible(index);
	         }
		}else if (e.getSource()==this.boutonModification){
			int index = list.getSelectedIndex();
			ParametresPeuplementDialog fenetre2 = new ParametresPeuplementDialog(this,listeMethodesPourc.get(index));
			TypeMethode typeM = fenetre2.getTypeMethode();
			if (typeM!=null){
				String nomM = typeM.getNomMethodePeuplement();
				double pourcent = typeM.getPourcentage();
				int position = -1;
				for (int i = 0;i<listeMethodesPourc.size();i++){
					String nom = listeMethodesPourc.get(i).getNomMethodePeuplement();
					if(nomM.equals(nom)){
						position = i;
					}
				}
				if((position==-1)||(position==index)){
					String strAffich = creationString(nomM,pourcent);
					// Modification de la liste des méthodes de peuplement
					listeMethodesPourc.set(index, typeM);
					listModel.setElementAt(strAffich,index);
				}else{
					double fr = listeMethodesPourc.get(position).getPourcentage();
					typeM.setPourcentage(pourcent+fr);
					String strAffich = creationString(nomM,pourcent+fr);
					listModel.set(position,strAffich);
					listeMethodesPourc.set(position,typeM);
					listModel.remove(index);
					listeMethodesPourc.remove(index);
				}
				// MAJ de la somme des fréquences : on recalcule 
				double somme =0;
				for (TypeMethode meth:listeMethodesPourc){
					if (meth.getPourcentage()!=-1){
						somme+=meth.getPourcentage();
					}
				}
				sommeFrequence.setText(String.valueOf(somme));
				// Si la somme des fréquences est supérieure à 100% 
				if (somme>100){
					JOptionPane.showMessageDialog(null, "Vous devez modifier la fréquence ou supprimer un type de bâtiment", 
							"la somme des fréquences est supérieure à 100 % : ", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

	protected static Double verifDouble(JTextField textField,String str1,String str2,String str3){
		double valeurDouble = -1;
		if (!textField.getText().isEmpty()){
			try {
				valeurDouble = Double.parseDouble(textField.getText());
			} catch (Exception e2) {
				logger.error(str1);
				int rep = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
				String valeurString = "";
				int rep2 = 0;
				while ((rep==0)&&(valeurDouble==-1)&&(rep2==0)){
					valeurString = JOptionPane.showInputDialog(null,str3,str1, JOptionPane.QUESTION_MESSAGE);
					try {
						valeurDouble = Double.parseDouble(valeurString);
					} catch (Exception e21) {
						logger.error(str1);
					}
					if(valeurString==null){
						rep2 = JOptionPane.showConfirmDialog(null, str2, str1, JOptionPane.YES_NO_OPTION);
					}
				}
				if (valeurDouble==-1){textField.setText("");}
				else {textField.setText(valeurString);}
			}
		}
		return valeurDouble;
	}
	
	public String creationString (String nomMethode, double pourcent){
		
		String str1 =nomMethode+" : ";
		String str2 = String.valueOf(pourcent);
		return str1 + str2;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
