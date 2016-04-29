/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.FloatingProjectFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;


public class WktPlugin extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(WktPlugin.class.getName());
  
  private FloatingProjectFrame projectFrame;
  private JButton wktButton;
  
  private String wkt;
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;
    projectFrame = (FloatingProjectFrame) this.application.getMainFrame().getSelectedProjectFrame();
    
    // Blanc
    projectFrame.getMainFrame().getMode().getToolBar().addSeparator();
    projectFrame.getMainFrame().getMode().getToolBar().addSeparator();
    projectFrame.getMainFrame().getMode().getToolBar().addSeparator();
        
    // Cube
    wktButton = new JButton();
    wktButton.setIcon(new ImageIcon(WktPlugin.class.getResource("/images/page_white_text.png")));
    wktButton.setToolTipText("Import WKT");
    wktButton.addActionListener(this);
    projectFrame.getMainFrame().getMode().getToolBar().add(wktButton);
     
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource().equals(wktButton)) {
      @SuppressWarnings("unused")
      GeomWktPanel query = new GeomWktPanel(this);
    }
  }
  
  public void displayWkt(String txt) {
    LOGGER.trace("Try to import WKT : " + txt);
    this.wkt = txt;
    try {
      IGeometry bati = WktGeOxygene.makeGeOxygene(this.wkt);

      FeatureType featureTypeBati = new FeatureType();
      featureTypeBati.setTypeName("Bati");
      if (bati instanceof IMultiCurve) {
        featureTypeBati.setGeometryType(IMultiCurve.class);
      } else if (bati instanceof ILineString) {
        featureTypeBati.setGeometryType(ILineString.class);
      } else {
        featureTypeBati.setGeometryType(IPolygon.class);
      }

      SchemaDefaultFeature schemaBati = new SchemaDefaultFeature();
      schemaBati.setFeatureType(featureTypeBati);
      featureTypeBati.setSchema(schemaBati);
      Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
      schemaBati.setAttLookup(attLookup);
      
      Population<DefaultFeature> popBati = new Population<DefaultFeature>(false, "Bati", DefaultFeature.class, true);
      popBati.setFeatureType(featureTypeBati);
      
      DefaultFeature n = popBati.nouvelElement(bati);
      n.setSchema(schemaBati);
      Object[] attributes = new Object[0];
      n.setAttributes(attributes);
      
      // Affiche
      projectFrame.addUserLayer(popBati, "Bati", null);
      //projectFrame.getLayerLegendPanel().getLayerViewPanel().getViewport().zoomToFullExtent();
      
      /*PolygonSymbolizer polySymbol = (PolygonSymbolizer) layerBati.getSymbolizer();
      polySymbol.setUnitOfMeasurePixel();
      polySymbol.getFill().setColor(new Color(32, 112, 177));
      polySymbol.getFill().setFillOpacity(0.8f);
      polySymbol.getStroke().setColor(new Color(111, 85, 66));
      polySymbol.getStroke().setStrokeOpacity(0.8f);
      polySymbol.getStroke().setStrokeWidth(1.0f);*/
      
      projectFrame.getLayerLegendPanel().getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}


class GeomWktPanel extends JDialog implements ActionListener {
  
  /** Serial version id. */
  private static final long serialVersionUID = 1L;
  
  private JTextArea wktArea;
  private JButton okay;
  private JButton cancel;
  
  private WktPlugin wktPlugin;
  
  public GeomWktPanel(WktPlugin wktPlugin) {
    this.wktPlugin = wktPlugin;
    
    setModal(true);
    setTitle("WKT");
    setLocation(300, 150);
    
    setLayout( new GridLayout(0,1));
    
    wktArea = new JTextArea(20, 80);
    wktArea.setText("POLYGON Z ((588573.6 6724981.9 96.5,588593.6 6724958.6 85.5,588597.2 6724954.3 85.5,588679.5 6725023 85.5,588659.5 6725046.3 85.5,588668.8 6725053.9 85.5,588691.7 6725027.1 85.5,588693.6 6725028.8 85.5,588696 6725029.9 85.5,588699.1 6725030.4 85.5,588702.2 6725030.1 85.5,588704.7 6725028.9 85.5,588707.6 6725026.6 85.5,588709.3 6725024 85.5,588710.1 6725021 85.5,588710 6725018 85.5,588709.1 6725015.3 85.5,588708.2 6725013.8 85.5,588706.5 6725011.6 85.5,588703.6 6725009.9 85.5,588699.3 6725009.2 85.5,588695.3 6725009.9 85.5,588692.2 6725011.8 85.5,588690 6725014.6 85.5,588603.2 6724942.6 85.5,588605.3 6724939.5 85.5,588606.2 6724936.4 85.5,588606.2 6724932.8 85.5,588605.1 6724929.9 85.5,588602.9 6724927.1 85.5,588599.9 6724925.1 85.5,588596.4 6724924.3 85.5,588593.3 6724924.5 85.5,588590.4 6724925.6 85.5,588587.1 6724928.2 85.5,588585.4 6724931.4 85.5,588584.8 6724935 85.5,588585.4 6724938.7 85.5,588587.5 6724942.1 85.5,588589.7 6724944 85.5,588564.1 6724973.9 96.5,588548.3 6724992.4 96.5,588542.5 6724994.4 96.5,588541 6724993.3 96.5,588537.9 6724992.3 96.5,588534.3 6724992.2 96.5,588530.9 6724993.4 96.5,588527.5 6724996.1 96.5,588525.8 6724999.3 96.5,588525.4 6725002.1 96.5,588525.7 6725005.6 96.5,588527 6725008.5 96.5,588529.3 6725011.1 96.5,588532.5 6725012.9 96.5,588534.9 6725013.4 96.5,588539 6725013.1 96.5,588542.3 6725011.4 96.5,588544 6725009.8 96.5,588547.3 6725012.6 96.5,588563.6 6725026.4 96.5,588562 6725029.6 96.5,588561.3 6725032.8 96.5,588561.8 6725036.7 96.5,588563.7 6725040.2 96.5,588566 6725042.5 96.5,588569.1 6725043.6 96.5,588572.8 6725043.9 96.5,588576.6 6725042.8 96.5,588579.7 6725040.4 96.5,588581.3 6725041.8 96.5,588596.4 6725054.9 96.5,588597.8 6725056.1 96.5,588596.8 6725057.1 96.5,588595.6 6725059.7 96.5,588595 6725063 96.5,588595.6 6725066.2 96.5,588597.6 6725069.8 96.5,588600.2 6725071.9 96.5,588603.7 6725073.3 96.5,588607.7 6725073.3 96.5,588611.3 6725072 96.5,588614.3 6725069.5 96.5,588628.1 6725080.6 96.5,588625.2 6725085.4 96.5,588624.4 6725088.8 96.5,588628.6 6725089.2 96.5,588628.8 6725092.5 96.5,588630 6725095.8 96.5,588632.7 6725098.9 96.5,588635.7 6725100.5 96.5,588638.6 6725101 96.5,588642 6725100.7 96.5,588644.8 6725099.5 96.5,588647.5 6725097.3 96.5,588649.2 6725094.6 96.5,588650.2 6725090.7 96.5,588649.6 6725087 96.5,588648.1 6725084.1 96.5,588645.4 6725081.4 96.5,588659.9 6725064.5 96.5,588650.6 6725056.6 96.5,588636.4 6725073.1 96.5,588635.1 6725072.2 96.5,588632.8 6725071.7 96.5,588630.7 6725072.2 96.5,588628.4 6725073.6 96.5,588616.4 6725064 96.5,588616.6 6725062.1 96.5,588616 6725059.3 96.5,588614.6 6725056.4 96.5,588612.5 6725054.5 96.5,588624.6 6725040.6 96.5,588628.4 6725036.3 96.5,588630.6 6725037.8 96.5,588633.9 6725038.8 96.5,588637 6725038.8 96.5,588640.4 6725037.6 96.5,588643.5 6725035.2 96.5,588645.3 6725032.2 96.5,588646.2 6725028.5 96.5,588645.8 6725025 96.5,588644.6 6725022.3 96.5,588642.3 6725019.6 96.5,588639.1 6725017.9 96.5,588636.3 6725017.4 96.5,588632.8 6725017.7 96.5,588630 6725018.9 96.5,588627.2 6725021.5 96.5,588613.4 6725009.8 96.5,588610 6725007 96.5,588612 6725003.1 96.5,588612.4 6725000 96.5,588611.8 6724996.6 96.5,588610.4 6724994.1 96.5,588608.1 6724991.7 96.5,588605 6724990 96.5,588601.8 6724989.6 96.5,588598.2 6724990.1 96.5,588595.1 6724991.8 96.5,588592.8 6724994.1 96.5,588591.3 6724997.1 96.5,588590.9 6725000.3 96.5,588591.3 6725003.1 96.5,588592.5 6725006 96.5,588594.7 6725008.4 96.5,588591.2 6725012.5 96.5,588582.9 6725022 96.5,588579.8 6725025.6 96.5,588578.2 6725024.3 96.5,588576.1 6725023.1 96.5,588573.2 6725022.4 96.5,588570.7 6725022.5 96.5,588568.7 6725022.9 96.5,588555.6 6725011.8 96.5,588556.8 6725009.5 96.5,588556.9 6725007.3 96.5,588556.2 6725005.1 96.5,588554.9 6725003.8 96.5,588559.7 6724998.3 96.5,588573.6 6724981.9 96.5))"); 
    JScrollPane scrollPane = new JScrollPane(wktArea); 
    add(scrollPane);
    
    okay = new JButton("OK");
    cancel = new JButton("Cancel");
    
    okay.addActionListener( this );
    cancel.addActionListener( this );
    
    JPanel buttons = new JPanel();
    add(buttons);         
    buttons.add(okay);
    buttons.add(cancel);
    
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    Dimension preferredSize = getPreferredSize();
    preferredSize.height += 30;
    setSize(preferredSize);
    
    pack();
    setVisible(true);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
      if (evt.getSource() == okay) {
        wktPlugin.displayWkt(wktArea.getText());
      } else if (evt.getSource() == cancel) {
        dispose();
    }
    this.setVisible(false);
  }
}
