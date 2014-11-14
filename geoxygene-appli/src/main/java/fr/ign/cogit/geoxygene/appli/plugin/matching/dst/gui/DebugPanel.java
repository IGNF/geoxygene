package fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;


/**
 * TODO : adapter les colonnes suivant la population
 * 
 * @author Marie-Dominique Van Damme
 */
public class DebugPanel extends JPanel {
  
  /** Logger. */
  private final static Logger LOGGER = Logger.getLogger(DebugPanel.class);
  
  /** Tableau des liens. */
  private JTable table;
  
  /** Population des liens. */
  private Population<DefaultFeature> popLien;
  private GF_FeatureType featureLien;
  
  /** Default serial ID. */
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructor.
   */
  public DebugPanel() {
    
  }
  
  public void setPopLien(Population<DefaultFeature> popLien) {
    this.popLien = popLien;
    LOGGER.trace("Nb colonne " + popLien.getFeatureType().getFeatureAttributes().size());
  }
    
  public void initPanel() {
      
    FormLayout layout = new FormLayout(
                "50dlu, pref, 20dlu",
                "20dlu, pref, 5dlu, pref, pref, 20dlu");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    JTextField titre = new JTextField("Appariement - liens");
    titre.setFont(new java.awt.Font("Default", java.awt.Font.BOLD, 14));
    titre.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    titre.setBackground(UIManager.getColor("Panel.background"));
    add(titre, cc.xy(2, 2));
        
    Vector<Vector<String>> data = new Vector<Vector<String>>();
    Vector<String> title = new Vector<String>();
        
    featureLien = popLien.getFeatureType();
    for (GF_AttributeType attributeType : featureLien.getFeatureAttributes()) {
      title.add(attributeType.getMemberName());
    }
      
    // data
    /*for (DefaultFeature lien : popLien) {
      Vector<String> ligne = new Vector<String>();
      ligne.add(lien.getAttribute("nature1").toString());
      ligne.add(lien.getAttribute("bdcToponyme").toString());
      data.add(ligne);
    }*/
        
    table = new JTable(data, title);
    table.setPreferredScrollableViewportSize(new Dimension(800, 100));
    table.setFillsViewportHeight(true);
    table.setAutoCreateRowSorter(true);
    
    add(table.getTableHeader(), cc.xy(2, 4));
    // JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane, cc.xy(2, 5));
        
  }
  
  public void refreshLightDebug() {
    
    DefaultTableModel dm = (DefaultTableModel) table.getModel();
    int rowCount = dm.getRowCount();
    // Remove rows one by one from the end of the table
    for (int i = rowCount - 1; i >= 0; i--) {
        dm.removeRow(i);
    }
    
    /*for (DefaultFeature lien : popLien) {
      dm.addRow(new Object[] { lien.getAttribute("nature1").toString(), 
          lien.getAttribute("bdcToponyme").toString()
      });
    }*/
  }

  /**
   * 
   * @param popLien
   */
  public void refreshDebug() {
    
    DefaultTableModel dm = (DefaultTableModel) table.getModel();
    int rowCount = dm.getRowCount();
    // Remove rows one by one from the end of the table
    for (int i = rowCount - 1; i >= 0; i--) {
        dm.removeRow(i);
    }
    
    for (DefaultFeature lien : popLien) {
      
      Object[] ligne = new Object[featureLien.getFeatureAttributes().size()];
      int c = 0;
      for (GF_AttributeType attributeType : featureLien.getFeatureAttributes()) {
        Object col = lien.getAttribute(attributeType.getMemberName());
        if (col != null) {
          ligne[c] = col;
        } else {
          ligne[c] = "";
        }
        c++;
      }
      
      dm.addRow(ligne);
      
      /*String nature2 = "";
      if (lien.getAttribute("NATURE2") != null) {
        nature2 = lien.getAttribute("NATURE2").toString();
      }
      String topo2 = "";
      if (lien.getAttribute("bdnToponyme") != null) {
        topo2 = lien.getAttribute("bdnToponyme").toString();
      }
      String maxPign = "";
      if (lien.getAttribute("MaxPign") != null) {
        maxPign = lien.getAttribute("MaxPign").toString();
      }
      
      dm.addRow(new Object[] { lien.getAttribute("nature1").toString(), 
          lien.getAttribute("bdcToponyme").toString(),
          lien.getAttribute("NbCandidat").toString(),
          nature2,
          topo2, 
          maxPign
      });*/
    }
    
  }

}

