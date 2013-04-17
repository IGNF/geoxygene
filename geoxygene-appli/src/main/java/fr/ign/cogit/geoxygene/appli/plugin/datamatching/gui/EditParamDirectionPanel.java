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

/**
 *
 */
public class EditParamDirectionPanel extends JPanel {

  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
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
  public EditParamDirectionPanel() {
    
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
    ButtonGroup groupe2 = new ButtonGroup();
    
    rbDoubleSensArcReseau1 = new JRadioButton("Tous les axes sont en double sens");
    rbDoubleSensArcReseau1.setSelected(true);
    groupe1.add(rbDoubleSensArcReseau1);
    
    rbDoubleSensArcReseau2 = new JRadioButton("Tous les axes sont en double sens");
    rbDoubleSensArcReseau2.setSelected(true);
    groupe2.add(rbDoubleSensArcReseau2);
    
    rbSimpleSensArcReseau1 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    groupe1.add(rbSimpleSensArcReseau1);
    
    rbSimpleSensArcReseau2 = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    groupe2.add(rbSimpleSensArcReseau2);
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
    fieldAttributeReseau1 = new JTextField(30);
    fieldAttributeReseau1.setText("sens_de_circulation");
    add(fieldAttributeReseau1, cc.xy(5, 6));
    fieldAttributeReseau2 = new JTextField(30);
    fieldAttributeReseau2.setText("");
    add(fieldAttributeReseau2, cc.xy(8, 6));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(2, 7));
    fieldValueDirectReseau1 = new JTextField(20);
    fieldValueDirectReseau1.setText("direct");
    add(fieldValueDirectReseau1, cc.xy(5, 7));
    fieldValueDirectReseau2 = new JTextField(20);
    fieldValueDirectReseau2.setText("");
    add(fieldValueDirectReseau2, cc.xy(8, 7));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(2, 8));
    fieldValueInverseReseau1 = new JTextField(20);
    fieldValueInverseReseau1.setText("inverse");
    add(fieldValueInverseReseau1, cc.xy(5, 8));
    fieldValueInverseReseau2 = new JTextField(20);
    fieldValueInverseReseau2.setText("");
    add(fieldValueInverseReseau2, cc.xy(8, 8));
    
    add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(2, 9));
    fieldValueDoubleSensReseau1 = new JTextField(20);
    fieldValueDoubleSensReseau1.setText("double");
    add(fieldValueDoubleSensReseau1, cc.xy(5, 9));
    fieldValueDoubleSensReseau2 = new JTextField(20);
    fieldValueDoubleSensReseau2.setText("");
    add(fieldValueDoubleSensReseau2, cc.xy(8, 9));
    
  }
  
  
  public ParamDirectionNetworkDataMatching valideField() {
    ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
    /*if (rbEdgeTwoWayRef.isSelected()) {
      paramDirection.setPopulationsArcsAvecOrientationDouble(true);
      paramDirection.setAttributOrientation1(null);
      paramDirection.setAttributOrientation2(null);
    } else {
      paramDirection.setPopulationsArcsAvecOrientationDouble(false);
      if (rbEdgeDefineWayRef.isSelected()) {
        paramDirection.setAttributOrientation1(null);
      } else if (rbEdgeDefineWayRef.isSelected()) {
        paramDirection.setAttributOrientation1(fieldAttributeRef.getText());
        Map<Integer, String> orientationMap1 = new HashMap<Integer, String>();
        orientationMap1.put(ParamInterface.SENS_DIRECT, fieldValueDirectRef.getText());
        orientationMap1.put(ParamInterface.SENS_INVERSE, fieldValueInverseRef.getText());
        orientationMap1.put(ParamInterface.DOUBLE_SENS, fieldValueDoubleSensRef.getText());
        paramDirection.setOrientationMap1(orientationMap1);
      }
    }*/
    return paramDirection;
  }

}
