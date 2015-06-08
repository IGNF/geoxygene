package fr.ign.cogit.geoxygene.appli.example;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.FloatingProjectFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.thematic.DiagramRadius;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSizeElement;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Utilisation des ThematicSymbolizer. 
 * Exemple des communes de paris
 * 
 * @author MDVan-Damme
 */
public class ChartExample extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  static final Logger LOGGER = Logger.getLogger(SLDDemoApplication.class.getName());
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;

    JMenu menuAwt = addMenu("Example", "Diagram maps");
    application.getMainFrame().getMenuBar()
      .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    
    try {
      
      URL urlContourCommune75 = new URL("file", "", "./data/GEOFLA_COMMUNE_PARIS_LAMB93_2013/ContourCommune75.shp");
      IPopulation<IFeature> commune75Pop = ShapefileReader.read(urlContourCommune75.getFile());
      
      FeatureType newFeatureType = new FeatureType();
      newFeatureType.setTypeName("arc");
      newFeatureType.setGeometryType(ILineString.class);
      
      Population<DefaultFeature> triangulationPop = new Population<DefaultFeature>(false, "Triangulation", DefaultFeature.class, true);
      triangulationPop.setFeatureType(newFeatureType);
      DataSet.getInstance().addPopulation(triangulationPop);
      
      Population<DefaultFeature> medialAxisPop = new Population<DefaultFeature>(false, "MedialAxis", DefaultFeature.class, true);
      medialAxisPop.setFeatureType(newFeatureType);
      DataSet.getInstance().addPopulation(medialAxisPop);
      
      // Thematic class
      List<ThematicClass> themList = new ArrayList<ThematicClass>();
      
      ThematicClass bi = new ThematicClass();
      bi.setClassLabel("batiment activité");
      Fill fillBlue = new Fill();
      fillBlue.setColor(Color.WHITE);
      bi.setFill(fillBlue);
      Expression classValueBI = new PropertyName("NB_BATI_A"); 
      bi.setClassValue(classValueBI);
      themList.add(bi);
      
      ThematicClass ba = new ThematicClass();
      ba.setClassLabel("batiment habitat");
      Fill fillGreen = new Fill();
      fillGreen.setColor(new Color(0, 144, 135));
      ba.setFill(fillGreen);
      Expression classValueBA = new PropertyName("NB_BATI_H");
      ba.setClassValue(classValueBA);
      themList.add(ba);
      
      ThematicClass bd = new ThematicClass();
      bd.setClassLabel("batiment divers");
      Fill fillOrange = new Fill();
      fillOrange.setColor(Color.ORANGE);
      bd.setFill(fillOrange);
      Expression classValueBD = new PropertyName("NB_BATI_D");
      bd.setClassValue(classValueBD);
      themList.add(bd);
      
      // ===============================================================================================
      //    PIE CHART
      // ===============================================================================================
      FloatingProjectFrame pieProjectFrame = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      // pieProjectFrame.getInternalFrame().setMaximum(true);
      
      Layer pieLayerCommune = pieProjectFrame.addUserLayer(commune75Pop, "PieChart", null);
      
      PolygonSymbolizer polyPieSymbolizer = (PolygonSymbolizer) pieLayerCommune.getSymbolizer();
      polyPieSymbolizer.setUnitOfMeasurePixel();
      polyPieSymbolizer.setStroke(new Stroke());
      polyPieSymbolizer.setFill(new Fill());
      polyPieSymbolizer.getStroke().setStrokeWidth(1);
      polyPieSymbolizer.getStroke().setStroke(new Color(0, 144, 135));
      polyPieSymbolizer.getFill().setColor(new Color(227, 222, 219));
      
      ThematicSymbolizer piets = new ThematicSymbolizer();
      piets.setUnitOfMeasureMetre();
      List<DiagramSymbolizer> piesymbolizers = new ArrayList<DiagramSymbolizer>();
      
      DiagramSymbolizer symbolPie = new DiagramSymbolizer();
      symbolPie.setDiagramType("piechart");
      
      List<DiagramSizeElement> piels = new ArrayList<DiagramSizeElement>();
      DiagramRadius dr = new DiagramRadius();
      dr.setValue(0.5);
      piels.add(dr);
      symbolPie.setDiagramSize(piels);
      
      symbolPie.setThematicClass(themList);
      
      piesymbolizers.add(symbolPie);
      piets.setSymbolizers(piesymbolizers);
      pieLayerCommune.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(piets);
      
      // ===============================================================================================
      //    BAR CHART
      // ===============================================================================================
      FloatingProjectFrame barProjectFrame = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      
      Layer barLayerCommune = barProjectFrame.addUserLayer(commune75Pop, "BarChart", null);
      PolygonSymbolizer polyBarSymbolizer = (PolygonSymbolizer) barLayerCommune.getSymbolizer();
      polyBarSymbolizer.setUnitOfMeasurePixel();
      polyBarSymbolizer.setStroke(new Stroke());
      polyBarSymbolizer.setFill(new Fill());
      polyBarSymbolizer.getStroke().setStrokeWidth(1);
      polyBarSymbolizer.getStroke().setStroke(new Color(0, 144, 135));
      polyBarSymbolizer.getFill().setColor(new Color(227, 222, 219));
      
      ThematicSymbolizer barts = new ThematicSymbolizer();
      barts.setUnitOfMeasureMetre();
      List<DiagramSymbolizer> barsymbolizers = new ArrayList<DiagramSymbolizer>();
      
      DiagramSymbolizer symbolBar = new DiagramSymbolizer();
      symbolBar.setDiagramType("barchart");
      
      List<DiagramSizeElement> barls = new ArrayList<DiagramSizeElement>();
      DiagramSizeElement dse = new DiagramSizeElement();
      dse.setValue(0.5);
      barls.add(dse);
      symbolBar.setDiagramSize(barls);
      
      symbolBar.setThematicClass(themList);
      
      barsymbolizers.add(symbolBar);
      barts.setSymbolizers(barsymbolizers);
      barLayerCommune.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(barts);
      
      // ===============================================================================================
      //    Line CHART
      // ===============================================================================================
      /*FloatingProjectFrame lineProjectFrame = (FloatingProjectFrame) this.application.getMainFrame().newProjectFrame();
      
      Layer lineLayerCommune = lineProjectFrame.addUserLayer(commune75Pop, "LineChart", null);
      PolygonSymbolizer lineBarSymbolizer = (PolygonSymbolizer) lineLayerCommune.getSymbolizer();
      lineBarSymbolizer.setUnitOfMeasurePixel();
      lineBarSymbolizer.setStroke(new Stroke());
      lineBarSymbolizer.setFill(new Fill());
      lineBarSymbolizer.getStroke().setStrokeWidth(1);
      lineBarSymbolizer.getStroke().setStroke(new Color(0, 144, 135));
      lineBarSymbolizer.getFill().setColor(new Color(227, 222, 219));
      
      ThematicSymbolizer linesymb = new ThematicSymbolizer();
      linesymb.setUnitOfMeasureMetre();
      List<DiagramSymbolizer> linesymbolizers = new ArrayList<DiagramSymbolizer>();
      
      DiagramSymbolizer symbolLine = new DiagramSymbolizer();
      symbolLine.setDiagramType("linechart");
      
      List<DiagramSizeElement> linels = new ArrayList<DiagramSizeElement>();
      // DiagramSizeElement dse = new DiagramSizeElement();
      // dse.setValue(0.5);
      linels.add(dse);
      symbolBar.setDiagramSize(linels);
      
      symbolLine.setThematicClass(themList);
      
      linesymbolizers.add(symbolLine);
      linesymb.setSymbolizers(linesymbolizers);
      lineLayerCommune.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(linesymb);*/
      
      // ===============================================================================================
      // Organize
      this.application.getMainFrame().organizeCurrentDesktop();
      pieProjectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
      pieProjectFrame.getLayerViewPanel().getViewport().zoomOut();
      barProjectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
      barProjectFrame.getLayerViewPanel().getViewport().zoomOut();
      // lineProjectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
      // lineProjectFrame.getLayerViewPanel().getViewport().zoomOut();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

}
