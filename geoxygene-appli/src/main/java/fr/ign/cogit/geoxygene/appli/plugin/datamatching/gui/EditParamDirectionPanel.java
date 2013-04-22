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
package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

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

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;

/**
 *
 */
public class EditParamDirectionPanel extends JPanel implements ActionListener {

  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  private ParamDirectionNetworkDataMatching paramDirection1 = null;
  private ParamDirectionNetworkDataMatching paramDirection2 = null;
  
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
  public EditParamDirectionPanel(ParamDirectionNetworkDataMatching paramDirection1, ParamDirectionNetworkDataMatching paramDirection2) {
    
    // Initialize directions parameters 
    this.paramDirection1 = paramDirection1;
    this.paramDirection2 = paramDirection2;
    
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
    if (paramDirection1.getOrientationDouble()) {
      rbDoubleSensArcReseau1.setSelected(true);
    }
    groupe1.add(rbDoubleSensArcReseau1);
    
    // Sens definit par un attribut
    rbSimpleSensArcReseau1 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    rbSimpleSensArcReseau1.addActionListener(this);
    if (!paramDirection1.getOrientationDouble()) {
      rbSimpleSensArcReseau1.setSelected(true);
    }
    groupe1.add(rbSimpleSensArcReseau1);
    
    // Attribut
    fieldAttributeReseau1 = new JTextField(30);
    if (paramDirection1.getAttributOrientation() != null) {
      fieldAttributeReseau1.setText(paramDirection1.getAttributOrientation());
    } else {
      fieldAttributeReseau1.setText("sens_de_circulation");
    }
    
    // Valeur des attributs
    fieldValueDirectReseau1 = new JTextField(20);
    fieldValueInverseReseau1 = new JTextField(20);
    fieldValueDoubleSensReseau1 = new JTextField(20);
    if (paramDirection1.getOrientationMap() != null) {
      
      // Set map of values 
      Map<Integer, String> orientationMap1 = paramDirection1.getOrientationMap();
      
      // Sens direct
      if (orientationMap1.get(OrientationInterface.SENS_DIRECT) != null) {
        fieldValueDirectReseau1.setText(orientationMap1.get(OrientationInterface.SENS_DIRECT));
      } else {
        fieldValueDirectReseau1.setText("direct");
      }
      
      // Sens inverse
      if (orientationMap1.get(OrientationInterface.SENS_INVERSE) != null) {
        fieldValueInverseReseau1.setText(orientationMap1.get(OrientationInterface.SENS_INVERSE));
      } else {
        fieldValueInverseReseau1.setText("inverse");
      }
      
      // Double sens
      if (orientationMap1.get(OrientationInterface.DOUBLE_SENS) != null) {
        fieldValueDoubleSensReseau1.setText(orientationMap1.get(OrientationInterface.DOUBLE_SENS));
      } else {
        fieldValueDoubleSensReseau1.setText("double");
      }
    
    }
    
    // -----------------------------------------------------------------------------------------------------
    
    ButtonGroup groupe2 = new ButtonGroup();
    
    rbDoubleSensArcReseau2 = new JRadioButton("Tous les axes sont en double sens");
    rbDoubleSensArcReseau2.addActionListener(this);
    if (paramDirection2.getOrientationDouble()) {
      rbDoubleSensArcReseau2.setSelected(true);
    }
    groupe2.add(rbDoubleSensArcReseau2);
    
    rbSimpleSensArcReseau2 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    rbSimpleSensArcReseau2.addActionListener(this);
    if (!paramDirection2.getOrientationDouble()) {
      rbSimpleSensArcReseau2.setSelected(true);
    }
    groupe2.add(rbSimpleSensArcReseau2);
    
    // Attribut
    fieldAttributeReseau2 = new JTextField(30);
    if (paramDirection2.getAttributOrientation() != null) {
      fieldAttributeReseau2.setText(paramDirection2.getAttributOrientation());
    } else {
      fieldAttributeReseau2.setText("sens_de_circulation");
    }
    
    fieldValueDirectReseau2 = new JTextField(20);
    fieldValueInverseReseau2 = new JTextField(20);
    fieldValueDoubleSensReseau2 = new JTextField(20);
    if (paramDirection2.getOrientationMap() != null) {
      
      // Set map of values 
      Map<Integer, String> orientationMap2 = paramDirection2.getOrientationMap();
      
      // Sens direct
      if (orientationMap2.get(OrientationInterface.SENS_DIRECT) != null) {
        fieldValueDirectReseau2.setText(orientationMap2.get(OrientationInterface.SENS_DIRECT));
      } else {
        fieldValueDirectReseau2.setText("direct");
      }
      
      // Sens inverse
      if (orientationMap2.get(OrientationInterface.SENS_INVERSE) != null) {
        fieldValueInverseReseau2.setText(orientationMap2.get(OrientationInterface.SENS_INVERSE));
      } else {
        fieldValueInverseReseau2.setText("inverse");
      }
      
      // Double sens
      if (orientationMap2.get(OrientationInterface.DOUBLE_SENS) != null) {
        fieldValueDoubleSensReseau2.setText(orientationMap2.get(OrientationInterface.DOUBLE_SENS));
      } else {
        fieldValueDoubleSensReseau2.setText("double");
      }
    
    }
    
    // -----------------------------------------------------------------------------------------------------
    
    if (paramDirection1.getOrientationDouble()) {
      fieldAttributeReseau1.setEnabled(false);
      fieldValueDirectReseau1.setEnabled(false);
      fieldValueInverseReseau1.setEnabled(false);
      fieldValueDoubleSensReseau1.setEnabled(false);
    }
    
    if (paramDirection2.getOrientationDouble()) {
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
  public ParamDirectionNetworkDataMatching[] valideField() {
    
    ParamDirectionNetworkDataMatching[] tabParamDirection = new ParamDirectionNetworkDataMatching[2];
    
    // param direction 1
    ParamDirectionNetworkDataMatching paramDirection1 = new ParamDirectionNetworkDataMatching();
    if (rbDoubleSensArcReseau1.isSelected()) {
      paramDirection1.setOrientationDouble(true);
      paramDirection1.setAttributOrientation(null);
      paramDirection1.setOrientationMap(null);
    } else if (rbSimpleSensArcReseau1.isSelected()) {
      paramDirection1.setOrientationDouble(false);
      paramDirection1.setAttributOrientation(fieldAttributeReseau1.getText());
      
      Map<Integer, String> orientationMap1 = new HashMap<Integer, String>();
      orientationMap1.put(OrientationInterface.SENS_DIRECT, fieldValueDirectReseau1.getText());
      orientationMap1.put(OrientationInterface.SENS_INVERSE, fieldValueInverseReseau1.getText());
      orientationMap1.put(OrientationInterface.DOUBLE_SENS, fieldValueDoubleSensReseau1.getText());
      paramDirection1.setOrientationMap(orientationMap1);
    }
    tabParamDirection[0] = paramDirection1;
    
    // param direction 2
    ParamDirectionNetworkDataMatching paramDirection2 = new ParamDirectionNetworkDataMatching();
    if (rbDoubleSensArcReseau2.isSelected()) {
      paramDirection2.setOrientationDouble(true);
      paramDirection2.setAttributOrientation(null);
      paramDirection2.setOrientationMap(null);
    } else if (rbSimpleSensArcReseau2.isSelected()) {
      paramDirection2.setOrientationDouble(false);
      paramDirection2.setAttributOrientation(fieldAttributeReseau2.getText());
      
      Map<Integer, String> orientationMap2 = new HashMap<Integer, String>();
      orientationMap2.put(OrientationInterface.SENS_DIRECT, fieldValueDirectReseau2.getText());
      orientationMap2.put(OrientationInterface.SENS_INVERSE, fieldValueInverseReseau2.getText());
      orientationMap2.put(OrientationInterface.DOUBLE_SENS, fieldValueDoubleSensReseau2.getText());
      paramDirection2.setOrientationMap(orientationMap2);
    }
    tabParamDirection[1] = paramDirection2;
    
    return tabParamDirection;
  
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
