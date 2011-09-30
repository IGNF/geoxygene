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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import fr.ign.cogit.appli.geopensim.ConfigurationMethodesPeuplement.Distribution;
import fr.ign.cogit.appli.geopensim.algo.TypeDistribution;

/**
 * @author Florence Curie
 *
 */
public class VariabilitePanel extends JPanel implements ActionListener,FocusListener{

	private static final long serialVersionUID = 2030010228825822909L;
	private JTextField moy1,moy2,min,max,ecartT;
	private double valBMoy,valBMin,valBMax,valBET;
	private JRadioButton bMMM,bME;
	private JComboBox comboTypeDistrib;
	private String[] typeDistribution = setDistrib();

	public String[] setDistrib(){
		String[] typeDistrib = new String[TypeDistribution.values().length];
		for (int i = 0;i<TypeDistribution.values().length;i++){
			typeDistrib[i] = TypeDistribution.values()[i].toString();
		}
		return typeDistrib;
	}


	public VariabilitePanel(JDialog parentF,String titre) {

		this.setLayout(new javax.swing.BoxLayout(this, BoxLayout.Y_AXIS));
		Border border = BorderFactory.createLineBorder(Color.gray);
		TitledBorder title = BorderFactory.createTitledBorder(border, titre);
		title.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title);

		// le type de distribution
		Box hSousBox1 = Box.createHorizontalBox();
		hSousBox1.add(Box.createHorizontalStrut(10));
		hSousBox1.add(new JLabel("Type de distribution : "));
		comboTypeDistrib = new JComboBox(typeDistribution);
		comboTypeDistrib.setMaximumSize(comboTypeDistrib.getPreferredSize());	
		comboTypeDistrib.addActionListener(this);
		hSousBox1.add(comboTypeDistrib);
		hSousBox1.add(Box.createHorizontalGlue());
		this.add(hSousBox1);
		this.add(Box.createVerticalStrut(10));

		// La méthode Min Max Moy 
		Box hSousBox2 = Box.createHorizontalBox();
		hSousBox2.add(Box.createHorizontalStrut(10));
		bMMM = new JRadioButton("Minimum : ",false);
		bMMM.addActionListener(this);
		hSousBox2.add(bMMM);
		hSousBox2.add(Box.createHorizontalGlue());
		min = new JTextField(5);
		min.setMaximumSize(min.getPreferredSize());
		min.addFocusListener(this);
		hSousBox2.add(min);
		hSousBox2.add(Box.createHorizontalStrut(10));
		hSousBox2.add(new JLabel("Maximum : "));
		max = new JTextField(5);
		max.setMaximumSize(max.getPreferredSize());
		max.addFocusListener(this);
		hSousBox2.add(max);
		hSousBox2.add(Box.createHorizontalStrut(10));
		hSousBox2.add(new JLabel("Moyenne : "));
		moy1 = new JTextField(5);
		moy1.setMaximumSize(moy1.getPreferredSize());
		moy1.addFocusListener(this);
		hSousBox2.add(moy1);
		hSousBox2.add(Box.createHorizontalGlue());
		this.add(hSousBox2);
		this.add(Box.createVerticalStrut(10));

		// La méthode Moy ET
		Box hSousBox3 = Box.createHorizontalBox();
		hSousBox3.add(Box.createHorizontalStrut(10));
		bME = new JRadioButton("Moyenne : ",false);
		bME.addActionListener(this);
		hSousBox3.add(bME);
		moy2 = new JTextField(5);
		moy2.setMaximumSize(moy2.getPreferredSize());
		moy2.addFocusListener(this);
		hSousBox3.add(moy2);
		hSousBox3.add(Box.createHorizontalStrut(10));
		hSousBox3.add(new JLabel("Ecart type : "));
		ecartT = new JTextField(5);
		ecartT.setMaximumSize(ecartT.getPreferredSize());
		ecartT.addFocusListener(this);
		hSousBox3.add(ecartT);
		hSousBox3.add(Box.createHorizontalGlue());
		this.add(hSousBox3);

		// Les boutons radio
		ButtonGroup groupeBouton = new ButtonGroup();
		groupeBouton.add(bMMM);
		groupeBouton.add(bME);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.bME){
			min.setEnabled(false);
			max.setEnabled(false);
			moy1.setEnabled(false);
			ecartT.setEnabled(true);
			moy2.setEnabled(true);
		}else if (e.getSource()==this.bMMM){
			String distrib = this.typeDistribution[this.comboTypeDistrib.getSelectedIndex()];
			min.setEnabled(true);
			max.setEnabled(true);
			if((distrib.equals("Aleatoire"))||(distrib.equals("Normale"))){
				moy1.setEnabled(false);
			}else{
				moy1.setEnabled(true);
			}
			ecartT.setEnabled(false);
			moy2.setEnabled(false);
		}else if (e.getSource()==this.comboTypeDistrib){
			if(bMMM.isSelected()){
				String distrib = this.typeDistribution[this.comboTypeDistrib.getSelectedIndex()];
				if((distrib.equals("Aleatoire"))||(distrib.equals("Normale"))){
					moy1.setEnabled(false);
				}else{
					moy1.setEnabled(true);
				}
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		if ((e.getSource()==this.moy1)||(e.getSource()==this.moy2)){
			// La moyenne
			valBMoy = -1;
			String strMoy1 = "La valeur moyenne entrée n'est pas un nombre";
			String strMoy2 = "Voulez vous entrer une autre valeur moyenne ?";
			String strMoy3 = "Entrez une nouvelle valeur moyenne";
			if (e.getSource()==this.moy1)
				valBMoy = CreationMethodePeuplementDialog.verifDouble(this.moy1, strMoy1, strMoy2, strMoy3);
			if (e.getSource()==this.moy2)
				valBMoy = CreationMethodePeuplementDialog.verifDouble(this.moy2, strMoy1, strMoy2, strMoy3);
		}else if (e.getSource()==this.ecartT){
			// L'écart type
			valBET = -1;
			String strET1 = "L'écart type de la valeur entré n'est pas un nombre";
			String strET2 = "Voulez vous entrer un autre écart type de la valeur ?";
			String strET3 = "Entrez un nouvel écart type de la valeur";
			valBET = CreationMethodePeuplementDialog.verifDouble(this.ecartT, strET1, strET2, strET3);
		}else if (e.getSource()==this.min){
			// Le minimum
			valBMin = -1;
			String strMin1 = "La valeur minimum entrée n'est pas un nombre";
			String strMin2 = "Voulez vous entrer une autre valeur minimum ?";
			String strMin3 = "Entrez une nouvelle valeur minimum";
			valBMin = CreationMethodePeuplementDialog.verifDouble(this.min, strMin1, strMin2, strMin3);
		}else if (e.getSource()==this.max){
			// Le maximum
			valBMax = -1;
			String strMax1 = "La valeur maximum entrée n'est pas un nombre";
			String strMax2 = "Voulez vous entrer une autre valeur maximum ?";
			String strMax3 = "Entrez une nouvelle valeur maximum";
			valBMax = CreationMethodePeuplementDialog.verifDouble(this.max, strMax1, strMax2, strMax3);
		}
	}


	/**
	 * @param param
	 */
	public void setDistribution(Distribution distribution) {

		// Le type de distribution du paramètre
		TypeDistribution typeD = distribution.getTypeDistribution();
		if (typeD==null){typeD = TypeDistribution.Normale;}
		int indexD = -1;
		for (int i=0;i<typeDistribution.length;i++){
			if (typeDistribution[i].equals(typeD.toString())){
				indexD =i;
				break;
			}
		}
		comboTypeDistrib.setSelectedIndex(indexD);
		String distrib = this.typeDistribution[this.comboTypeDistrib.getSelectedIndex()];
		if((distrib.equals("Aleatoire"))||(distrib.equals("Normale"))){
			moy1.setEnabled(false);
		}
		// Les caractéristiques du paramètre
		double valMoy = distribution.getMoyenne();
		double valMin = distribution.getMinimum();
		double valMax = distribution.getMaximum();
		double valET = distribution.getEcartType();
		if ((valMin!=-1)||(valMax!=-1)){
			bMMM.setSelected(true);
			bME.setSelected(false);
			if (valMin!=-1){min.setText(String.valueOf(valMin));}
			if (valMax!=-1){max.setText(String.valueOf(valMax));}
			if (valMoy!=-1){moy1.setText(String.valueOf(valMoy));}
			this.setEnabledME(false);
			this.setEnabledMMM(true);
		}else {
			bME.setSelected(true);
			bMMM.setSelected(false);
			if (valMoy!=-1){moy2.setText(String.valueOf(valMoy));}
			if (valET!=-1){ecartT.setText(String.valueOf(valET));}
			this.setEnabledME(true);
			this.setEnabledMMM(false);
		}

	}


	/**
	 * @return
	 */
	public Distribution getDistribution() {

		// La distribution du paramètre
		String distrib = this.typeDistribution[this.comboTypeDistrib.getSelectedIndex()];
		TypeDistribution typeDistrib = TypeDistribution.valueOf(distrib);

		// La moyenne du paramètre
		if((!this.moy1.getText().isEmpty())&&(this.moy1.isEnabled())){
			valBMoy = Double.parseDouble(this.moy1.getText());
		}else if((!this.moy2.getText().isEmpty())&&(this.moy2.isEnabled())){
			valBMoy = Double.parseDouble(this.moy2.getText());
		}else{
			valBMoy=-1;
		}

		// L'écart type du paramètre
		if((this.ecartT.getText().isEmpty())||(!this.ecartT.isEnabled())){valBET = -1;}
		else{valBET = Double.parseDouble(this.ecartT.getText());}

		// Le minimum du paramètre
		if((this.min.getText().isEmpty())||(!this.min.isEnabled())){valBMin = -1;}
		else{valBMin = Double.parseDouble(this.min.getText());}

		// Le maximum du paramètre
		if((this.max.getText().isEmpty())||(!this.max.isEnabled())){valBMax = -1;}
		else{valBMax = Double.parseDouble(this.max.getText());}

		Distribution distribution = new Distribution(typeDistrib, valBMin, valBMax, valBMoy, valBET);

		return distribution;
	}
	
	public void setEnabledPanneau(boolean actif){
		comboTypeDistrib.setEnabled(actif);
		bME.setEnabled(actif);
		bMMM.setEnabled(actif);
		if (actif==false){
			setEnabledMMM(actif);
			setEnabledME(actif);
		}else{
			if(bME.isSelected()){
				setEnabledME(true);
				setEnabledMMM(false);
			}else{
				setEnabledME(false);
				setEnabledMMM(true);
			}
		}
	}
	
	public void setEnabledMMM(boolean actif){
		min.setEnabled(actif);
		max.setEnabled(actif);
		moy1.setEnabled(actif);
		if (actif==true){
			String distrib = this.typeDistribution[this.comboTypeDistrib.getSelectedIndex()];
			if((distrib.equals("Aleatoire"))||(distrib.equals("Normale"))){
				moy1.setEnabled(false);
			}
		}
	}
	
	public void setEnabledME(boolean actif){
		ecartT.setEnabled(actif);
		moy2.setEnabled(actif);
	}

}
