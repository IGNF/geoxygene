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
package fr.ign.cogit.geoxygene.appli.plugin.datamatching.network;

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
  private JRadioButton rbEdgeTwoWayRef = null;
  private JRadioButton rbEdgeOneWayRef = null;
  private JRadioButton rbEdgeDefineWayRef = null;
  private JTextField fieldAttributeRef = null;
  private JTextField fieldValueDirectRef = null;
  private JTextField fieldValueInverseRef = null;
  private JTextField fieldValueDoubleSensRef = null;
  
  private JTextField fieldAttributeComp = null;
  private JTextField fieldValueDirectComp = null;
  private JTextField fieldValueInverseComp = null;
  private JTextField fieldValueDoubleSensComp = null;
  
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
    
    ButtonGroup groupe = new ButtonGroup();
    
    rbEdgeTwoWayRef = new JRadioButton("Tous les axes sont en double sens");
    rbEdgeTwoWayRef.setSelected(true);
    groupe.add(rbEdgeTwoWayRef);
    
    rbEdgeOneWayRef = new JRadioButton("Tous les axes sont en simple sens");
    groupe.add(rbEdgeOneWayRef);
    
    rbEdgeDefineWayRef = new JRadioButton("Définir le sens de l'axe suivant la valeur d'un attribut");
    groupe.add(rbEdgeDefineWayRef);
  }
  
  /**
   * 
   */
  private void initPanel() {
    FormLayout layout = new FormLayout(
        "20dlu, pref, 20dlu, pref, pref, pref, pref, 20dlu", // colonnes
        "10dlu, pref, pref, pref, pref, pref, pref, pref, pref, 40dlu");  // lignes
    CellConstraints cc = new CellConstraints();
    setLayout(layout);
    
    // JLabel labelParam1 = new JLabel("Avec orientation double");
    add(rbEdgeTwoWayRef, cc.xyw(2, 2, 4));
    add(rbEdgeOneWayRef, cc.xyw(2, 3, 4));
    add(rbEdgeDefineWayRef, cc.xyw(2, 4, 4));
    
    add(new JLabel("Reseau 1"), cc.xy(5, 5));
    add(new JLabel("Reseau 2"), cc.xy(6, 5));
    
    add(new JLabel("Nom de l'attribut : "), cc.xy(4, 6));
    fieldAttributeRef = new JTextField(20);
    fieldAttributeRef.setText("sens_de_circulation");
    add(fieldAttributeRef, cc.xy(5, 6));
    fieldAttributeComp = new JTextField(20);
    fieldAttributeComp.setText("");
    add(fieldAttributeComp, cc.xy(6, 6));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens direct' : "), cc.xy(4, 7));
    fieldValueDirectRef = new JTextField(20);
    fieldValueDirectRef.setText("direct");
    add(fieldValueDirectRef, cc.xy(5, 7));
    fieldValueDirectComp = new JTextField(20);
    fieldValueDirectComp.setText("");
    add(fieldValueDirectComp, cc.xy(6, 7));
    
    add(new JLabel("Valeur de l'attribut pour 'Sens inverse' : "), cc.xy(4, 8));
    fieldValueInverseRef = new JTextField(20);
    fieldValueInverseRef.setText("inverse");
    add(fieldValueInverseRef, cc.xy(5, 8));
    fieldValueInverseComp = new JTextField(20);
    fieldValueInverseComp.setText("");
    add(fieldValueInverseComp, cc.xy(6, 8));
    
    add(new JLabel("Valeur de l'attribut pour 'Double sens' : "), cc.xy(4, 9));
    fieldValueDoubleSensRef = new JTextField(20);
    fieldValueDoubleSensRef.setText("double");
    add(fieldValueDoubleSensRef, cc.xy(5, 9));
    fieldValueDoubleSensComp = new JTextField(20);
    fieldValueDoubleSensComp.setText("");
    add(fieldValueDoubleSensComp, cc.xy(6, 9));
    
  }
  
  public ParamDirectionNetworkDataMatching valideField() {
    ParamDirectionNetworkDataMatching paramDirection = new ParamDirectionNetworkDataMatching();
    if (rbEdgeTwoWayRef.isSelected()) {
      paramDirection.setPopulationsArcsAvecOrientationDouble(true);
      paramDirection.setAttributOrientation1(null);
      paramDirection.setAttributOrientation2(null);
    } else {
      paramDirection.setPopulationsArcsAvecOrientationDouble(false);
      if (rbEdgeDefineWayRef.isSelected()) {
        paramDirection.setAttributOrientation1(null);
      } else if (rbEdgeDefineWayRef.isSelected()) {
        paramDirection.setAttributOrientation1(fieldAttributeRef.getText());
        Map<Object, Integer> orientationMap1 = new HashMap<Object, Integer>();
        orientationMap1.put(fieldValueDirectRef, 1);
        orientationMap1.put(fieldValueInverseRef, -1);
        orientationMap1.put(fieldValueDoubleSensRef, 2);
        paramDirection.setOrientationMap1(orientationMap1);
      }
    }
    return paramDirection;
  }
}
