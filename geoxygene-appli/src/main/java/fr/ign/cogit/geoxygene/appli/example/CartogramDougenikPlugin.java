package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.FloatingProjectFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.algorithms.CartogramDougenik;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * CartogramDougenikPlugin show Dougenik algorithm
 * The plugin use it with Paris arrondissements and recensement attribute.
 * 
 */
public class CartogramDougenikPlugin extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  static final Logger LOGGER = Logger.getLogger(CartogramDougenikPlugin.class.getName());
  
  // private static final String NOM_FICHIER = "ContourCommune75.shp";
  // private static final String NOM_ATTRIBUT = "POP";
  // private static final String NOM_FICHIER = "Cantons.shp";
  // private static final String NOM_ATTRIBUT = "Pop2006";
  private static final String NOM_FICHIER = "prov_4326_simple.shp";
  private static final String NOM_ATTRIBUT = "lonnom";
  // private static final String NOM_ATTRIBUT = "nb";
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;

    JMenu menuAwt = addMenu("Example", "Cartogram by Dougenik");
    application.getMainFrame().getMenuBar()
      .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    
    try {
      
      URL urlContourCommune75 = new URL("file", "", "./data/cartogram/" + NOM_FICHIER);
      IPopulation<IFeature> popCommune = ShapefileReader.read(urlContourCommune75.getFile());
      
      // IPopulation<IFeature> popCommune = createPop();
      
      // Pop début
      FloatingProjectFrame popCommuneDebutProjectFrame = (FloatingProjectFrame) this.application.getMainFrame().getSelectedProjectFrame();
      Layer layer1 = popCommuneDebutProjectFrame.addUserLayer(popCommune, "Original", null);
      PolygonSymbolizer ps1 = (PolygonSymbolizer) layer1.getSymbolizer();
      ps1.setUnitOfMeasurePixel();
      ps1.getStroke().setStrokeWidth(1);
      ps1.getStroke().setStrokeOpacity(0.8f);
      ps1.getStroke().setStroke(new Color(0, 144, 135));
      ps1.getFill().setColor(new Color(163, 217, 90));
      ps1.getFill().setFillOpacity(0.8f);
      
      // Pop fin
      int nbIteration = 8;
      Population<DefaultFeature> entrees1 = CartogramDougenik.doCartogram(popCommune, NOM_ATTRIBUT, nbIteration);
      FloatingProjectFrame popCommuneFinProjectFrame1 = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      Layer layer2 = popCommuneFinProjectFrame1.addUserLayer(entrees1, "Iteration 8", null);
      PolygonSymbolizer ps2 = (PolygonSymbolizer) layer2.getSymbolizer();
      ps2.setUnitOfMeasurePixel();
      ps2.getStroke().setStrokeWidth(1);
      ps2.getStroke().setStrokeOpacity(0.8f);
      ps2.getStroke().setStroke(new Color(0, 144, 135));
      ps2.getFill().setColor(new Color(255, 247, 125));
      ps2.getFill().setFillOpacity(0.8f);
      
      // Pop fin
      nbIteration = 12;
      Population<DefaultFeature> entrees2 = CartogramDougenik.doCartogram(popCommune, NOM_ATTRIBUT, nbIteration);
      FloatingProjectFrame popCommuneFinProjectFrame2 = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      Layer layer3 = popCommuneFinProjectFrame2.addUserLayer(entrees2, "Iteration 12", null);
      PolygonSymbolizer ps3 = (PolygonSymbolizer) layer3.getSymbolizer();
      ps3.setUnitOfMeasurePixel();
      ps3.getStroke().setStrokeWidth(1);
      ps3.getStroke().setStrokeOpacity(0.8f);
      ps3.getStroke().setStroke(new Color(0, 144, 135));
      ps3.getFill().setColor(new Color(255, 247, 125));
      ps3.getFill().setFillOpacity(0.8f);
      
      nbIteration = 20;
      Population<DefaultFeature> entrees3 = CartogramDougenik.doCartogram(popCommune, NOM_ATTRIBUT, nbIteration);
      FloatingProjectFrame popCommuneFinProjectFrame3 = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      Layer layer4 = popCommuneFinProjectFrame3.addUserLayer(entrees3, "Iteration 20", null);
      PolygonSymbolizer ps4 = (PolygonSymbolizer) layer4.getSymbolizer();
      ps4.setUnitOfMeasurePixel();
      ps4.getStroke().setStrokeWidth(1);
      ps4.getStroke().setStrokeOpacity(0.8f);
      ps4.getStroke().setStroke(new Color(0, 144, 135));
      ps4.getFill().setColor(new Color(255, 247, 125));
      ps4.getFill().setFillOpacity(0.8f);
      
      // Organize
      this.application.getMainFrame().organizeCurrentDesktop();
      popCommuneDebutProjectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
      popCommuneDebutProjectFrame.getLayerViewPanel().getViewport().zoomOut();
      popCommuneFinProjectFrame1.getLayerViewPanel().getViewport().zoomToFullExtent();
      popCommuneFinProjectFrame1.getLayerViewPanel().getViewport().zoomOut();
      popCommuneFinProjectFrame2.getLayerViewPanel().getViewport().zoomToFullExtent();
      popCommuneFinProjectFrame2.getLayerViewPanel().getViewport().zoomOut();
      popCommuneFinProjectFrame3.getLayerViewPanel().getViewport().zoomToFullExtent();
      popCommuneFinProjectFrame3.getLayerViewPanel().getViewport().zoomOut();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    LOGGER.trace("End rendering cartogram with Dougenik algorithm.");
      
  }
  
  /*private IPopulation<IFeature> createPop() {
    IPopulation<IFeature> popCarre = new Population<IFeature>("Carre");
    
    DefaultFeature carre1 = new DefaultFeature(new GM_Polygon(new GM_Envelope(10, 20, 10, 40)));
    DefaultFeature carre2 = new DefaultFeature(new GM_Polygon(new GM_Envelope(30, 40, 10, 20)));
    DefaultFeature carre3 = new DefaultFeature(new GM_Polygon(new GM_Envelope(30, 40, 30, 40)));
    
    FeatureType carreFeatureType = new FeatureType();
    carreFeatureType.setTypeName("Metadonnee");
    carreFeatureType.setGeometryType(IPolygon.class);
    
    AttributeType idTextNature = new AttributeType("nb", "double");
    carreFeatureType.addFeatureAttribute(idTextNature);
    
    // Création d'un schéma associé au featureType
    SchemaDefaultFeature schema = new SchemaDefaultFeature();
    schema.setFeatureType(carreFeatureType);
    carreFeatureType.setSchema(schema);
    
    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
    attLookup.put(new Integer(0), new String[] { "nb", "nb" });
    schema.setAttLookup(attLookup);
    
    popCarre.setFeatureType(carreFeatureType);
    
    Object[] attributes = new Object[] { "10" };
    carre1.setSchema(schema);
    carre1.setAttributes(attributes);
    attributes = new Object[] { "40" };
    carre2.setSchema(schema);
    carre2.setAttributes(attributes);
    attributes = new Object[] { "20" };
    carre3.setSchema(schema);
    carre3.setAttributes(attributes);
    
    popCarre.add(carre1);
    popCarre.add(carre2);
    popCarre.add(carre3);
    
    return popCarre;
  }*/

}
