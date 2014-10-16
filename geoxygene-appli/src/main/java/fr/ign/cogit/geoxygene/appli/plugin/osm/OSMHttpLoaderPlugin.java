/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.appli.plugin.osm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.http.OsmXmlHttpClient;
import fr.ign.cogit.geoxygene.osm.importexport.OsmXmlParser;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;


public class OSMHttpLoaderPlugin implements GeOxygeneApplicationPlugin, ActionListener {
  
  private GeOxygeneApplication application = null;
  
  private boolean withProxy = true;
  private String dataRequest = "";
  
  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    
    System.out.println("ici ???");
    
    // Check if the DataMatching menu exists. If not we create it.
    JMenu menu = null;
    String menuName = "OSM";
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
        if (c instanceof JMenu) {
            JMenu aMenu = (JMenu) c;
            if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase(menuName)) {
                menu = aMenu;
            }
        }
    }
    if (menu == null) {
        menu = new JMenu(menuName);
    }

    // Add osm http loader menu item to the menu.
    JMenuItem menuItem = new JMenuItem("Load OSM via HTTP");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Refresh menu of the application
    int menuComponentCount = application.getMainFrame().getMenuBar().getComponentCount();
    application.getMainFrame().getMenuBar().add(menu, menuComponentCount - 2);
    
  }
  
  @Override
  public void actionPerformed(final ActionEvent e) {
    
    // Launch parameter osm loader panel.
    EditParamOsmLoaderPanel dialogParamOsmLoader = new EditParamOsmLoaderPanel(this);
    if (dialogParamOsmLoader.getAction().equals("LAUNCH")) {
        doOsmXmlLoader(this.dataRequest);
    }
    
  }
  
  private void doOsmXmlLoader(String dataRequest) {

    try {
      
      String xml = OsmXmlHttpClient.getOsmXML(dataRequest, this.withProxy);
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      
      OsmXmlParser loader = new OsmXmlParser();
      loader.loadOsm(doc);
      
      Population<DefaultFeature> popPoint = loader.getPopPoint();
      Population<DefaultFeature> popLigne = loader.getPopLigne();
      System.out.println("Nb point pop = " + popPoint.size());
      
      ProjectFrame p1 = this.application.getMainFrame().newProjectFrame();
      p1.setTitle("Get XML OSM data from Overpass API");
      
      /*Layer layerPoint = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popPoint, "Point OSM", null);
      PointSymbolizer symbolizerP1 = (PointSymbolizer) layerPoint.getSymbolizer();
      symbolizerP1.setUnitOfMeasurePixel();
      symbolizerP1.getGraphic().setSize(6);
      symbolizerP1.getGraphic().getMarks().get(0).setWellKnownName("square");
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setStrokeWidth(1);
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setColor(new Color(74, 163, 202));
      symbolizerP1.getGraphic().getMarks().get(0).getFill().setColor(new Color(169, 209, 116));*/
      
      Layer layerLigne = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popLigne, "Ligne OSM", null);
      LineSymbolizer symbolizerL1 = (LineSymbolizer) layerLigne.getSymbolizer();
      symbolizerL1.setUnitOfMeasurePixel();
      symbolizerL1.getStroke().setStrokeWidth(1);
      symbolizerL1.getStroke().setStroke(new Color(74, 163, 202));
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
  }
  
  public void setProxy(boolean b) {
    this.withProxy = b;
  }
  
  public void setData(String s) {
    this.dataRequest = s;
  }

}


class EditParamOsmLoaderPanel extends JDialog implements ActionListener {

  /** Serial version UID. */
  private static final long serialVersionUID = 1L;
  
  /** Origin Frame. */
  private OSMHttpLoaderPlugin osmHttpLoaderPlugin;
  private String action;
  
  /** 2 buttons : launch, cancel. */
  private JButton launchButton = null;
  private JButton cancelButton = null;
  
  /** param */
  private JCheckBox withProxy;
  private JTextArea textParam;
  
  /** Tab Panels. */ 
  JPanel buttonPanel = null;
  JPanel paramPanel = null;
  
  /**
   * Constructor.
   * @param olp
   */
  public EditParamOsmLoaderPanel(OSMHttpLoaderPlugin olp) {
    this.osmHttpLoaderPlugin = olp;
    
    setModal(true);
    setTitle("Initialisation des paramètres");
    setIconImage(new ImageIcon(
        GeOxygeneApplication.class.getResource("/images/icons/wrench.png")).getImage());
    
    initButtonPanel();
    initParamPanel();
    getContentPane().add(paramPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    pack();
    setLocation(250, 250);
    setVisible(true);
  }
  
  /**
   * 
   */
  private void initButtonPanel() {
    
    buttonPanel = new JPanel(); 
    
    launchButton = new JButton(I18N.getString("DataMatchingPlugin.Launch"));
    cancelButton = new JButton(I18N.getString("DataMatchingPlugin.Cancel"));
    
    launchButton.addActionListener(this);
    cancelButton.addActionListener(this);
    
    buttonPanel.setLayout(new FlowLayout (FlowLayout.CENTER)); 
    buttonPanel.add(cancelButton);
    buttonPanel.add(launchButton);
    
  }
  
  private void initParamPanel() {
    
    paramPanel = new JPanel();
    
    FormLayout layout = new FormLayout(
        "40dlu, pref, pref, 40dlu",
        "20dlu, pref, 10dlu, pref, 4dlu, pref, 4dlu, pref, 40dlu");
    paramPanel.setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    withProxy = new JCheckBox();
    withProxy.setSelected(true);
    paramPanel.add(withProxy, cc.xy(2, 2));
    paramPanel.add(new JLabel("Proxy IGN"), cc.xy(3, 2));
    
    paramPanel.add(new JLabel("Paramètres de la requête : "), cc.xy(3, 4));
    
    textParam = new JTextArea();
    textParam.setText("area[name=\"Hoogstade\"];(node(area);<;);out meta qt;");
    textParam.setColumns(40);
    textParam.setRows(8);
    textParam.setEnabled(false);
    paramPanel.add(textParam, cc.xy(3, 6));
    
    JLabel avert = new JLabel(" ! Attention la requête peut être un peu longue ...");
    avert.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 12));
    paramPanel.add(avert, cc.xyw(2, 8, 2));
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == launchButton) {
      // Init action
      action = "LAUNCH";
      // Set parameters
      setParameters();
      dispose();
    } else if (source == cancelButton) {
      // Init action
      action = "CANCEL";
      // do nothing
      dispose();
    } 
  }
  
  protected void setParameters() {
    if (withProxy.isSelected()) {
      osmHttpLoaderPlugin.setProxy(true);
    } else {
      osmHttpLoaderPlugin.setProxy(false);
    }
    osmHttpLoaderPlugin.setData(textParam.getText());
  }
  
  public String getAction() {
    return action;
  }
}
