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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;


/**
 * 
 * 
 *
 */
public class EditParamDistancePanel extends JPanel implements ActionListener {
  
  /** Serial version UID. */
  private static final long serialVersionUID = 4791806011051504347L;
  
  private ParamDistanceNetworkDataMatching paramDistance;
  
  private JTextField fieldDistanceNoeudsMax = null;
  private JTextField fieldDistanceNoeudsImpassesMax = null;
  private JTextField fieldDistanceArcsMax = null;
  private JTextField fieldDistanceArcsMin = null;
  private JCheckBox impasseButton = null;
  
  /**
   * Constructor.
   */
  public EditParamDistancePanel(ParamDistanceNetworkDataMatching distanceParam) {
    
    paramDistance = distanceParam;
    
    // Initialize all fields
    initFields();
    
    // Initialize the panel with all fields
    initPanel();
  }
  
  /**
   * Initialize all fields
   */
  private void initFields() {
    
    fieldDistanceNoeudsMax = new JTextField(10);
    fieldDistanceNoeudsMax.setText(Float.toString(paramDistance.getDistanceNoeudsMax()));
    
    fieldDistanceArcsMax = new JTextField(10);
    fieldDistanceArcsMax.setText(Float.toString(paramDistance.getDistanceArcsMax()));
    
    fieldDistanceArcsMin = new JTextField(10);
    fieldDistanceArcsMin.setText(Float.toString(paramDistance.getDistanceArcsMin()));
    
    fieldDistanceNoeudsImpassesMax = new JTextField(10);
    
    impasseButton = new JCheckBox();
    impasseButton.addActionListener(this);
    if (paramDistance.getDistanceNoeudsImpassesMax() == -1) {
      impasseButton.setSelected(false);
      // fieldDistanceNoeudsImpassesMax.setEnabled(false);
      fieldDistanceNoeudsImpassesMax.setEditable(false);
      fieldDistanceNoeudsImpassesMax.setText("");
    } else {
      impasseButton.setSelected(true);
      // fieldDistanceNoeudsImpassesMax.setEnabled(true);
      fieldDistanceNoeudsImpassesMax.setEditable(true);
      fieldDistanceNoeudsImpassesMax.setText(Float.toString(paramDistance.getDistanceNoeudsImpassesMax()));
    }
  }
  
  /**
   * 
   */
  private void initPanel() {
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, 5dlu, pref, pref, pref, 40dlu",  // colonnes
        "20dlu, pref, 10dlu, pref, 10dlu, pref, 10dlu, pref, 40dlu");  // lignes
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    // Ligne 1
    add(new JLabel("Distance maximale entre deux noeuds : "), cc.xy(2, 2));
    add(fieldDistanceNoeudsMax, cc.xy(5, 2));
    add(new JLabel(" m"), cc.xy(6, 2));
    
    // Ligne 2
    add(new JLabel("Distance maximum autorisée entre les arcs des deux réseaux : "), cc.xy(2, 4));
    add(fieldDistanceArcsMax, cc.xy(5, 4));
    add(new JLabel(" m"), cc.xy(6, 4));
    
    // Ligne 3
    add(new JLabel("<html>Distance minimum sous laquelle l'écart de distance<br/>pour divers arcs du "
        + " réseaux 2 (distance vers les arcs du réseau 1)<br/>n'a plus aucun sens : "), cc.xy(2, 6));
    add(fieldDistanceArcsMin, cc.xy(5, 6));
    add(new JLabel(" m"), cc.xy(6, 6));
    
    // Ligne 4
    add(new JLabel("<html>Distance maximale autorisée entre deux noeuds appariés,<br/>quand le noeud du "
        + " réseau 1 est une impasse uniquement :<br/>"
        + "Ce paramètre peut ne pas être pris en compte.</html>"), cc.xy(2, 8));
    add(impasseButton, cc.xy(4, 8));
    add(fieldDistanceNoeudsImpassesMax, cc.xy(5, 8));
    add(new JLabel(" m"), cc.xy(6, 8));
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == impasseButton) {
      if (impasseButton.isSelected()) {
        fieldDistanceNoeudsImpassesMax.setEditable(true);
      } else {
        fieldDistanceNoeudsImpassesMax.setEditable(false);
        fieldDistanceNoeudsImpassesMax.setText("");
      }
    }
  }
  
  /**
   * 
   * @return
   */
  public ParamDistanceNetworkDataMatching valideField() {
    ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
    
    paramDistance.setDistanceNoeudsMax(Float.parseFloat(fieldDistanceNoeudsMax.getText()));
    paramDistance.setDistanceArcsMax(Float.parseFloat(fieldDistanceArcsMax.getText()));
    paramDistance.setDistanceArcsMin(Float.parseFloat(fieldDistanceArcsMin.getText()));
    
    if (impasseButton.isSelected()) {
      paramDistance.setDistanceNoeudsImpassesMax(Float.parseFloat(fieldDistanceNoeudsImpassesMax.getText()));
    } else {
      paramDistance.setDistanceNoeudsImpassesMax(-1);
    }
    
    return paramDistance;
  }

}
