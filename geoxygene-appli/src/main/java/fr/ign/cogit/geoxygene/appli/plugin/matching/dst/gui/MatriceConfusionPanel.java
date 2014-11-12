package fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

/**
 * 
 * @author Marie-Dominique Van Damme
 *
 */
public class MatriceConfusionPanel extends JDialog implements ActionListener {
  
  /** Default serial ID. */
  private static final long serialVersionUID = 1L;
  
  /** Population des liens. */
  Population<DefaultFeature> popLien;
  FeatureType featureTypeLien;
  
  /** Attributes. */
  String attribute1;
  String attribute2;
  
  /** button : close. */
  private JButton closeButton = null;
  
  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  JPanel hypothesisPanel = null;
  
  public MatriceConfusionPanel(Population<DefaultFeature> popLien, FeatureType featureTypeLien, String attribute1, String attribute2) {
    
    this.popLien = popLien;
    this.featureTypeLien = featureTypeLien;
    this.attribute1 = attribute1;
    this.attribute2 = attribute2;
    
    setModal(true);
    setTitle("Matrice de confusion.");
    setIconImage(new ImageIcon(
        MatriceConfusionPanel.class.getResource("/images/icons/table.png")).getImage());
    
    initHypothesisPanel();
    initButtonPanel();
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(hypothesisPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(250, 250);
    this.setSize(800, 500);
    setVisible(true);
  }
  
  
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == closeButton) {
      // close
      dispose();
    }
  }
  
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    buttonPanel.setLayout(new FlowLayout (FlowLayout.CENTER)); 
    
    closeButton = new JButton("Close");
    closeButton.addActionListener(this);
    buttonPanel.add(closeButton);
  
  }
  
  private void initHypothesisPanel() {
    
    hypothesisPanel = new JPanel();
    hypothesisPanel.setLayout(new BorderLayout()); 
    
    Vector<Vector<String>> data = new Vector<Vector<String>>();
    List<String> att1Value = new ArrayList<String>();
    Vector<String> att2Value = new Vector<String>();
    att2Value.add(""); // pour la colonne des libellés
    
    for (DefaultFeature point : popLien) {
      Object att1O = point.getAttribute(this.attribute1 + "1");
      Object att2O = point.getAttribute(this.attribute2 + "2");
      
      int j = 0;
      if (att2O != null) {
        String att2 = att2O.toString();
        if (!att2Value.contains(att2)) {
          att2Value.add(att2);
          j = att2Value.size();
          
          //
          for (int k = 0; k < data.size(); k++) {
            Vector<String> ligne = data.get(k);
            ligne.add("0");
          }
          
        }
      }
      if (att1O != null) {
        String att1 = att1O.toString();
        if (!att1Value.contains(att1)) {
          att1Value.add(att1);
          Vector<String> ligne = new Vector<String>();
          ligne.add(att1);
          for (int k = 1; k < j;k++) {
            ligne.add("0");
          }
          data.add(ligne);
        } 
      }
      
      // Incrementation de la confusion
      for (int l = 0; l < data.size(); l++) {
        Vector<String> ligne = data.get(l);
        for (int c = 0; c < ligne.size(); c++) {
          if (att1O != null && att2O != null) {
            if (att1O.toString().equals(att1Value.get(l)) && att2O.toString().equals(att2Value.get(c))) {
              data.get(l).set(c, Integer.toString(Integer.parseInt(ligne.get(c)) + 1));
            }
          }
        } 
      }
    }
    
    JTable tableau = new JTable(data, att2Value);
    tableau.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    for (int l = 1; l < data.get(0).size(); l++) {
      tableau.getColumnModel().getColumn(l).setCellRenderer(new ColorRedRenderer());
    }
    tableau.getColumnModel().getColumn(0).setCellRenderer(new TitreRenderer());
    
    hypothesisPanel.add(tableau.getTableHeader(), BorderLayout.NORTH);
    hypothesisPanel.add(new JScrollPane(tableau), BorderLayout.CENTER);
  }

}

/**
 * 
 *
 */
class ColorRedRenderer extends DefaultTableCellRenderer {
  
  /** Default serial id. */
  private static final long serialVersionUID = 1L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (Integer.parseInt(value.toString()) > 0) {
        setBackground(Color.ORANGE);
        // setForeground(Color.RED);
      } else {
        setBackground(Color.WHITE);
        // setForeground(Color.BLACK);
      }

      return this;
  }
}

class TitreRenderer extends DefaultTableCellRenderer {
  
  /** Default serial id. */
  private static final long serialVersionUID = 1L;
  
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      setBackground(UIManager.getColor("Panel.background"));
      setForeground(Color.BLACK);
      
      return this;
  }

}


