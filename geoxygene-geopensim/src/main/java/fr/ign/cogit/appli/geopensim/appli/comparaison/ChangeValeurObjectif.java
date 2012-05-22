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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.appli.GeOpenSimApplication;
import fr.ign.cogit.appli.geopensim.feature.meso.ClasseUrbaine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.MainFrame;

/**
 * @author Florence Curie
 *
 */
public class ChangeValeurObjectif extends JFrame implements ActionListener, FocusListener{

	private static final long serialVersionUID = 4078652248433963214L;
	private static final Logger logger = Logger.getLogger(ChangeValeurObjectif.class.getName());
	private JButton boutonAppliquer,boutonAnnuler;
	JRadioButton partieCommune,partieSelectionnee,sansDelimitation;
	private JTextField densiteObj;
	private JComboBox comboTypeFonctionnel;
	JFrame parent;
	List<AgentZoneElementaireBatie> listeZESelect = new ArrayList<AgentZoneElementaireBatie>();

	// Constructeur
	public ChangeValeurObjectif(final JFrame parent){

		super();
		this.parent = parent;
		// La fenêtre
		this.setTitle("Choix des valeurs objectif");
		this.setBounds(50, 100, 480, 200);
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

		// La densité
		Box hBoxDensite = Box.createHorizontalBox();
		hBoxDensite.add(new JLabel("Densité objectif : "));
		hBoxDensite.add(Box.createHorizontalStrut(5));
		densiteObj = new JTextField(3);
		densiteObj.setText("");
		densiteObj.setMaximumSize(densiteObj.getPreferredSize());
		densiteObj.addFocusListener(this);
		hBoxDensite.add(densiteObj);
		hBoxDensite.add(Box.createHorizontalGlue());

		// Le type fonctionnel objectif
		Box hBoxTypeFonctionnel = Box.createHorizontalBox();
		hBoxTypeFonctionnel.add(new JLabel("Type fonctionnel objectif : "));
		hBoxTypeFonctionnel.add(Box.createHorizontalGlue());
		Field[] fields = ClasseUrbaine.class.getDeclaredFields();
		String[] typeFonct = new String[fields.length];
		for (int i=0;i<fields.length;i++){
			typeFonct[i] = fields[i].getName();
		}
		comboTypeFonctionnel = new JComboBox(typeFonct);
		comboTypeFonctionnel.setMaximumSize(comboTypeFonctionnel.getPreferredSize());	
		comboTypeFonctionnel.addActionListener(this);
		hBoxTypeFonctionnel.add(comboTypeFonctionnel);
		hBoxTypeFonctionnel.add(Box.createHorizontalGlue());

		// Choix des îlots
		Box hBoxIlot = Box.createHorizontalBox();
		hBoxIlot.add(new JLabel(" Sélectionner les îlots sur la carte puis valider "));
		hBoxIlot.add(Box.createHorizontalGlue());

		// Bouton de validation
		Box hBoxValidation = Box.createHorizontalBox();
		hBoxValidation.add(Box.createHorizontalGlue());
		boutonAppliquer = new JButton("Appliquer");
		boutonAppliquer.addActionListener(this);
		hBoxValidation.add(boutonAppliquer);
		hBoxValidation.add(Box.createHorizontalStrut(10));
		boutonAnnuler = new JButton("Annuler");
		boutonAnnuler.addActionListener(this);
		hBoxValidation.add(boutonAnnuler);

		// L'agencement vertical des boîtes horizontales 
		Box vBox = Box.createVerticalBox();
		vBox.add(hBoxDensite);
		vBox.add(hBoxTypeFonctionnel);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBoxIlot);
		vBox.add(Box.createVerticalStrut(20));
		vBox.add(hBoxValidation);
		vBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		contenu.add(vBox,BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.boutonAppliquer){
			if (!densiteObj.getText().isEmpty()){
				MainFrame mainF = (MainFrame)this.parent;
				Set<IFeature> selectedFeatures=mainF.getSelectedProjectFrame().getLayerViewPanel().getSelectedFeatures();
				listeZESelect = new ArrayList<AgentZoneElementaireBatie>();
				for (IFeature feat:selectedFeatures){
					if(feat instanceof AgentZoneElementaireBatie){
						((AgentZoneElementaireBatie)feat).setClassificationFonctionnelleBut(ClasseUrbaine.getVal((String)(comboTypeFonctionnel.getSelectedItem())));
						((AgentZoneElementaireBatie)feat).setDensiteBut(Double.parseDouble(densiteObj.getText()));
					}
				}
				// Fermeture de la fenêtre
				setVisible(false);	
				dispose();
			}else{
				JOptionPane.showMessageDialog(null, "Entrez une densité valide");
			}
		}else if(e.getSource()==this.boutonAnnuler){
			// Fermeture de la fenêtre
			setVisible(false);	
			dispose();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource()==this.densiteObj){
			verifDensite(this.densiteObj, "La densité entrée n'est pas valide");
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	protected static void verifDensite(JTextField textField,String str1){
		double valeurDouble = -1;
		if (!textField.getText().isEmpty()){
			try {
				valeurDouble = Double.parseDouble(textField.getText());
			} catch (Exception e2) {
				logger.error(str1);
				String valeurString = "";
				textField.setText(valeurString);
			}
			if ((valeurDouble<0)||(valeurDouble>1)){textField.setText("");}
		}
	}

}
