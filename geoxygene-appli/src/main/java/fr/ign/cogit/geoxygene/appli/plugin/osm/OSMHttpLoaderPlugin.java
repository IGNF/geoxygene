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
import java.util.ArrayList;
import java.util.List;

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
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;


public class OSMHttpLoaderPlugin implements GeOxygeneApplicationPlugin, ActionListener {
  
  private GeOxygeneApplication application = null;
  
  private boolean withProxy = true;
  private String dataRequest = "<osm-script>\n"
      + "   <union>\n"
      + "      <query type=\"node\">\n"
      + "         <bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8567\" w=\"1.8783\" />\n"
      + "      </query>\n"
      + "      <query type=\"way\">\n"
      + "         <bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8587\" w=\"1.8783\" />\n"
      + "      </query>\n"
      + "      <query type=\"relation\">\n"
      + "         <bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8587\" w=\"1.8783\" />\n"
      + "      </query>\n"
      + "   </union>\n"
      + "   <print mode=\"meta\"/>\n"
      + "</osm-script>\n";
  
  @Override
  public void initialize(GeOxygeneApplication application) {
    
    this.application = application;
    
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
    EditParamOsmLoaderPanel dialogParamOsmLoader = new EditParamOsmLoaderPanel(this, dataRequest);
    if (dialogParamOsmLoader.getAction().equals("LAUNCH")) {
        doOsmXmlLoader(this.dataRequest);
    }
    
  }
  
  private void doOsmXmlLoader(String dataRequest) {

    try {
      
      /*dataRequest = "<osm-script>"
          + "<query type=\"node\">"
          + "<has-kv k=\"name\" v=\"Gielgen\"/>"
          + "<has-kv k=\"place\" v=\"suburb\"/>"
          + "</query>"
          + "<print mode=\"meta\"/>"
          + "</osm-script>";*/
      
      dataRequest = "<osm-script>"
          + "<union>"
          + "<query type=\"node\">"
          //+ "<has-kv k=\"name\" regv=\"holtorf\" />"
          + "<bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8567\" w=\"1.8783\" />"
          + "</query>"
          + "<query type=\"way\">"
          //+ "<has-kv k=\"name\" regv=\"holtorf\" />"
          + "<bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8587\" w=\"1.8783\" />"
          + "</query>"
          + "<query type=\"relation\">"
          //+ "<has-kv k=\"name\" regv=\"holtorf\" />"
          + "<bbox-query e=\"1.9037\" n=\"47.8681\" s=\"47.8587\" w=\"1.8783\" />"
          + "</query>"
          /*+ "<query type=\"node\">"
          + "<has-kv k=\"highway\" />"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"way\">"
          + "<has-kv k=\"highway\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"relation\">"
          + "<has-kv k=\"highway\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"way\">"
          + "<has-kv k=\"building\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"relation\">"
          + "<has-kv k=\"building\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"*/
          + "</union>"
          + "<print mode=\"meta\"/>"
          + "</osm-script>";
      
      /*dataRequest = "<osm-script output=\"xml\" timeout=\"250\">"
          + "<id-query {{nominatimArea:Flemish Brabant}} into=\"area\"/>"
          + "<union>"
          + "<query type=\"node\">"
          + "<has-kv k=\"highway\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"way\">"
          + "<has-kv k=\"highway\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"relation\">"
          + "<has-kv k=\"highway\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"way\">"
          + "<has-kv k=\"building\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "<query type=\"relation\">"
          + "<has-kv k=\"building\"/>"
          + "<area-query from=\"area\"/>"
          + "</query>"
          + "</union>"
          + "<print mode=\"meta\"/>"
          + "<recurse type=\"down\"/>"
          + "<print mode=\"meta\" order=\"quadtile\"/>"
          + "</osm-script>";*/
      
      String xml = OsmXmlHttpClient.getOsmXML(dataRequest, this.withProxy);
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      
      OsmXmlParser loader = new OsmXmlParser();
      loader.loadOsm(doc);
      
      Population<DefaultFeature> popPoint = loader.getPopPointOSM();
      Population<DefaultFeature> popLigne = loader.getPopLigneOSM();
      Population<DefaultFeature> popRoute = loader.getPopRouteOSM();
      System.out.println("Nb point pop = " + popPoint.size());
      System.out.println("Nb ligne pop = " + popLigne.size());
      
      ProjectFrame p1 = this.application.getMainFrame().newProjectFrame();
      p1.setTitle("Get XML OSM data from Overpass API");
      
      Layer layerPoint = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popPoint, "Point OSM", null);
      PointSymbolizer symbolizerP1 = (PointSymbolizer) layerPoint.getSymbolizer();
      symbolizerP1.setUnitOfMeasurePixel();
      symbolizerP1.getGraphic().setSize(6);
      symbolizerP1.getGraphic().getMarks().get(0).setWellKnownName("square");
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setStrokeWidth(1);
      symbolizerP1.getGraphic().getMarks().get(0).getStroke().setColor(new Color(74, 163, 202));
      symbolizerP1.getGraphic().getMarks().get(0).getFill().setColor(new Color(169, 209, 116));
      
      Layer layerParking = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(loader.getPopParking(), "Parking", null);
      PointSymbolizer symbolizerP2 = (PointSymbolizer) layerParking.getSymbolizer();
      Graphic g2 = symbolizerP2.getGraphic();
      ExternalGraphic eg2 = new ExternalGraphic();
      eg2.setHref("/symbols/osm/parking.p.16.png");
      eg2.setFormat("png");
      List<ExternalGraphic> l2 = new ArrayList<ExternalGraphic>();
      l2.add(eg2);
      g2.setExternalGraphics(l2);
      
      Layer layerPolice = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(loader.getPopPolice(), "Police", null);
      PointSymbolizer symbolizerP3 = (PointSymbolizer) layerPolice.getSymbolizer();
      Graphic g3 = symbolizerP3.getGraphic();
      ExternalGraphic eg3 = new ExternalGraphic();
      eg3.setHref("/symbols/osm/police.p.16.png");
      eg3.setFormat("png");
      List<ExternalGraphic> l3 = new ArrayList<ExternalGraphic>();
      l3.add(eg3);
      g3.setExternalGraphics(l3);
      
      Layer layerSchool = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(loader.getPopSchool(), "School", null);
      PointSymbolizer symbolizerP4 = (PointSymbolizer) layerSchool.getSymbolizer();
      //symbolizerP4.setUnitOfMeasurePixel();
      symbolizerP4.setUnitOfMeasureMetre();
      Graphic g4 = symbolizerP4.getGraphic();
      ExternalGraphic eg4 = new ExternalGraphic();
      eg4.setHref("/symbols/osm/school.png");
      eg4.setFormat("png");
      List<ExternalGraphic> l4 = new ArrayList<ExternalGraphic>();
      l4.add(eg4);
      g4.setExternalGraphics(l4);
      
     
      
      Layer layerLigne = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popLigne, "Ligne OSM", null);
      LineSymbolizer symbolizerL1 = (LineSymbolizer) layerLigne.getSymbolizer();
      symbolizerL1.setUnitOfMeasurePixel();
      symbolizerL1.getStroke().setStrokeWidth(1);
      symbolizerL1.getStroke().setStroke(new Color(74, 163, 202));
      
      Layer layerRoute = this.application.getMainFrame().getSelectedProjectFrame().addUserLayer(popRoute, "Route OSM", null);
      LineSymbolizer symbolizerL2 = (LineSymbolizer) layerRoute.getSymbolizer();
      symbolizerL2.setUnitOfMeasurePixel();
      symbolizerL2.getStroke().setStrokeWidth(1);
      symbolizerL2.getStroke().setStroke(Color.RED);
      
      
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
  private String dataRequest;
  
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
  public EditParamOsmLoaderPanel(OSMHttpLoaderPlugin olp, String dataRequest) {
    this.osmHttpLoaderPlugin = olp;
    this.dataRequest = dataRequest;
    
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
    withProxy.setSelected(false);
    paramPanel.add(withProxy, cc.xy(2, 2));
    paramPanel.add(new JLabel("Proxy IGN"), cc.xy(3, 2));
    
    paramPanel.add(new JLabel("Paramètres de la requête : "), cc.xy(3, 4));
    
    textParam = new JTextArea();
    textParam.setText(this.dataRequest); // "area[name=\"Hoogstade\"];(node(area);<;);out meta qt;"
    textParam.setColumns(40);
    textParam.setRows(8);
    // textParam.setEnabled(false);
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
