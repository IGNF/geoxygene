/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;

/**
 *
 */
public class EditParamDirectionPanel extends JPanel implements ActionListener {

  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  /** Parameters. */
  private ParametresApp param = null;
  
  /** */
  private JRadioButton rbDoubleSensArcReseau1 = null;
  private JRadioButton rbSimpleSensArcReseau1 = null;
  
  private JRadioButton rbDoubleSensArcReseau2 = null;
  private JRadioButton rbSimpleSensArcReseau2 = null;
  
  private JTextField fieldAttributeReseau1 = null;
  private JTextField fieldValueDirectReseau1 = null;
  private JTextField fieldValueInverseReseau1 = null;
  private JTextField fieldValueDoubleSensReseau1 = null;
  
  private JTextField fieldAttributeReseau2 = null;
  private JTextField fieldValueDirectReseau2 = null;
  private JTextField fieldValueInverseReseau2 = null;
  private JTextField fieldValueDoubleSensReseau2 = null;
  
  /**
   * Constructor.
   */
  public EditParamDirectionPanel(ParametresApp param) {
    
    // Initialize parameters 
    this.param = param;
    
    // Initialize all fields
    initFields();
    
    // Initialize the panel with all fields
    initPanel();
  
  }
  
  /**
   * 
   */
  private void initFields() {
    
    ButtonGroup groupe1 = new ButtonGroup();
    
    // Double sens
    rbDoubleSensArcReseau1 = new JRadioButton("Tous les axes sont en double sens");
    rbDoubleSensArcReseau1.addActionListener(this);
    if (param.populationsArcsAvecOrientationDouble1) {
      rbDoubleSensArcReseau1.setSelected(true);
    }
    groupe1.add(rbDoubleSensArcReseau1);
    
    // Sens definit par un attribut
    rbSimpleSensArcReseau1 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    rbSimpleSensArcReseau1.addActionListener(this);
    if (!param.populationsArcsAvecOrientationDouble1) {
      rbSimpleSensArcReseau1.setSelected(true);
    }
    groupe1.add(rbSimpleSensArcReseau1);
    
    // Attribut
    fieldAttributeReseau1 = new JTextField(30);
    if (param.attributOrientation1 != null) {
      fieldAttributeReseau1.setText(param.attributOrientation1);
    } else {
      fieldAttributeReseau1.setText("sens_de_circulation");
    }
    
    // Valeur des attributs
    fieldValueDirectReseau1 = new JTextField(20);
    fieldValueInverseReseau1 = new JTextField(20);
    fieldValueDoubleSensReseau1 = new JTextField(20);
    if (param.orientationMap1 != null) {
      
      // Set map of values 
      Map<Object, Integer> orientationMap1 = param.orientationMap1;
      
      // Sens direct
      fieldValueDirectReseau1.setText("direct");
      for (Object valSens : orientationMap1.keySet()) {
          if (orientationMap1.get(valSens).equals(OrientationInterface.SENS_DIRECT)) {
            fieldValueDirectReseau1.setText(valSens.toString());
          }
      }
      
      
      // Sens inverse
      fieldValueInverseReseau1.setText("inverse");
        for (Object valSens : orientationMap1.keySet()) {
          if (orientationMap1.get(valSens).equals(OrientationInterface.SENS_INVERSE)) {
            fieldValueInverseReseau1.setText(valSens.toString());
          }
        }
      
      
      // Double sens
        fieldValueDoubleSensReseau1.setText("double");
        for (Object valSens : orientationMap1.keySet()) {
          if (orientationMap1.get(valSens).equals(OrientationInterface.DOUBLE_SENS)) {
            fieldValueDoubleSensReseau1.setText(valSens.toString());
          }
        }
      
    
    }
    
    // -----------------------------------------------------------------------------------------------------
    
    ButtonGroup groupe2 = new ButtonGroup();
    
    rbDoubleSensArcReseau2 = new JRadioButton("Tous les axes sont en double sens");
    rbDoubleSensArcReseau2.addActionListener(this);
    if (param.populationsArcsAvecOrientationDouble2) {
      rbDoubleSensArcReseau2.setSelected(true);
    }
    groupe2.add(rbDoubleSensArcReseau2);
    
    rbSimpleSensArcReseau2 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    rbSimpleSensArcReseau2.addActionListener(this);
    if (!param.populationsArcsAvecOrientationDouble2) {
      rbSimpleSensArcReseau2.setSelected(true);
    }
    groupe2.add(rbSimpleSensArcReseau2);
    
    // Attribut
    fieldAttributeReseau2 = new JTextField(30);
    if (param.attributOrientation2 != null) {
      fieldAttributeReseau2.setText(param.attributOrientation2);
    } else {
      fieldAttributeReseau2.setText("sens_de_circulation");
    }
    
    fieldValueDirectReseau2 = new JTextField(20);
    fieldValueInverseReseau2 = new JTextField(20);
    fieldValueDoubleSensReseau2 = new JTextField(20);
    if (param.orientationMap2 != null) {
      
      // Set map of values 
      Map<Object, Integer> orientationMap2 = param.orientationMap2;
      
      // Sens direct
      fieldValueDirectReseau2.setText("direct");
        for (Object valSens : orientationMap2.keySet()) {
          if (orientationMap2.get(valSens).equals(OrientationInterface.SENS_DIRECT)) {
            fieldValueDirectReseau2.setText(valSens.toString());
          }
        }
      
      // Sens inverse
        fieldValueInverseReseau2.setText("inverse");
        for (Object valSens : orientationMap2.keySet()) {
          if (orientationMap2.get(valSens).equals(OrientationInterface.SENS_INVERSE)) {
            fieldValueInverseReseau2.setText(valSens.toString());
          }
        }
      
      
      // Double sens
        fieldValueDoubleSensReseau2.setText("double");
        for (Object valSens : orientationMap2.keySet()) {
          if (orientationMap2.get(valSens).equals(OrientationInterface.DOUBLE_SENS)) {
            fieldValueDoubleSensReseau2.setText(valSens.toString());
          }
        }
      
    
    }
    
    // -----------------------------------------------------------------------------------------------------
    
    if (param.populationsArcsAvecOrientationDouble1) {
      fieldAttributeReseau1.setEnabled(false);
      fieldValueDirectReseau1.setEnabled(false);
      fieldValueInverseReseau1.setEnabled(false);
      fieldValueDoubleSensReseau1.setEnabled(false);
    }
    
    if (param.populationsArcsAvecOrientationDouble2) {
      fieldAttributeReseau2.setEnabled(false);
      fieldValueDirectReseau2.setEnabled(false);
      fieldValueInverseReseau2.setEnabled(false);
      fieldValueDoubleSensReseau2.setEnabled(false);
    }
  
  }
  
  /**
   * 
   */
  private void initPanel() {
    
    // "Définir le sens de l'axe suivant la valeur d'un attribut"
    // "Tous les axes sont en double sens"
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, 5dlu, pref, pref, 20dlu, pref, pref, 20dlu", // colonnes
        "10dlu, pref, 10dlu, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    setLayout(layout);
    
    // Line 1 : titles
    add(new JLabel("Reseau 1"), cc.xy(5, 2));
    add(new JLabel("Reseau 2"), cc.xy(8, 2));
    
    // Line 2 : double sens
    add(rbDoubleSensArcReseau1, cc.xyw(4, 4, 3));
    add(rbDoubleSensArcReseau2, cc.xyw(7, 4, 3));
    
    // Line 3
    add(rbSimpleSensArcReseau1, cc.xyw(4, 5, 3));
    add(rbSimpleSensArcReseau2, cc.xyw(7, 5, 3));
    
    // Line 4 : simple sens
    add(new JLabel("Nom de l'attribut : "), cc.xy(2, 6));
    add(fieldAttributeReseau1, cc.xy(5, 6));
    add(fieldAttributeReseau2, cc.xy(8, 6));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(2, 7));
    add(fieldValueDirectReseau1, cc.xy(5, 7));
    add(fieldValueDirectReseau2, cc.xy(8, 7));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(2, 8));
    add(fieldValueInverseReseau1, cc.xy(5, 8));
    add(fieldValueInverseReseau2, cc.xy(8, 8));
    
    add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(2, 9));
    add(fieldValueDoubleSensReseau1, cc.xy(5, 9));
    add(fieldValueDoubleSensReseau2, cc.xy(8, 9));
    
  }
  
  /**
   * 
   * @return
   */
  public ParametresApp valideField() {
    
    // param direction 1
    if (rbDoubleSensArcReseau1.isSelected()) {
      param.populationsArcsAvecOrientationDouble1 = true;
      param.attributOrientation1 = null;
      param.orientationMap1 = null;
    } else if (rbSimpleSensArcReseau1.isSelected()) {
      param.populationsArcsAvecOrientationDouble1 = false;
      param.attributOrientation1 = fieldAttributeReseau1.getText();
      
      Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
      orientationMap1.put(fieldValueDirectReseau1.getText(), OrientationInterface.SENS_DIRECT);
      orientationMap1.put(fieldValueInverseReseau1.getText(), OrientationInterface.SENS_INVERSE);
      orientationMap1.put(fieldValueDoubleSensReseau1.getText(), OrientationInterface.DOUBLE_SENS);
      param.orientationMap1 = orientationMap1;
    }
    
    // param direction 2
    if (rbDoubleSensArcReseau2.isSelected()) {
      param.populationsArcsAvecOrientationDouble2 = true;
      param.attributOrientation2 = null;
      param.orientationMap2 = null;
    } else if (rbSimpleSensArcReseau2.isSelected()) {
      param.populationsArcsAvecOrientationDouble2 = false;
      param.attributOrientation2 = fieldAttributeReseau2.getText();
      
      Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>();
      orientationMap2.put(fieldValueDirectReseau2.getText(), OrientationInterface.SENS_DIRECT);
      orientationMap2.put(fieldValueInverseReseau2.getText(), OrientationInterface.SENS_INVERSE);
      orientationMap2.put(fieldValueDoubleSensReseau2.getText(), OrientationInterface.DOUBLE_SENS);
      param.orientationMap2 = orientationMap2;
    }

    // Return all parameters
    return param;
  
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == rbDoubleSensArcReseau1) {
      // double sens pour le reseau 1
      fieldAttributeReseau1.setEnabled(false);
      fieldValueDirectReseau1.setEnabled(false);
      fieldValueInverseReseau1.setEnabled(false);
      fieldValueDoubleSensReseau1.setEnabled(false);
    } else if (source == rbSimpleSensArcReseau1) {
      // sens defini par un attribut pour le reseau 1
      fieldAttributeReseau1.setEnabled(true);
      fieldValueDirectReseau1.setEnabled(true);
      fieldValueInverseReseau1.setEnabled(true);
      fieldValueDoubleSensReseau1.setEnabled(true);
    } else if (source == rbDoubleSensArcReseau2) {
      // double sens pour le reseau 2
      fieldAttributeReseau2.setEnabled(false);
      fieldValueDirectReseau2.setEnabled(false);
      fieldValueInverseReseau2.setEnabled(false);
      fieldValueDoubleSensReseau2.setEnabled(false);
    } else if (source == rbSimpleSensArcReseau2) {
      // sens defini par un attribut pour le reseau 2
      fieldAttributeReseau2.setEnabled(true);
      fieldValueDirectReseau2.setEnabled(true);
      fieldValueInverseReseau2.setEnabled(true);
      fieldValueDoubleSensReseau2.setEnabled(true);
    }
  }

}
