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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.LayerLegendPanel;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.algorithms.SwingingArmNonConvexHull;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.jdbc.postgis.PostgisReader;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * 
 * @author MDVan-Damme
 *
 */
public class AlphaShapePlugin implements GeOxygeneApplicationPlugin, ActionListener {

  @Override
  public void initialize(final GeOxygeneApplication application) {
      // this.application = application;
      
      LayerLegendPanel layerLegendPanel = application.getMainFrame().getSelectedProjectFrame().getLayerLegendPanel();
          
      try {
        
        // On charge les données
        Map<String,String> params = new HashMap<String,String>();
        params.put("dbtype", "postgis");
        params.put("host", "localhost");
        params.put("port", "5437");
        params.put("database", "immo");
        params.put("user", "test");
        params.put("passwd", "test");
        
        IPopulation<IFeature> popNuage = PostgisReader.read(params, "nuage", "nuage", null, false);
        System.out.println("Nb features = " + popNuage.size());
        
        
        
        ArrayList<IDirectPosition> listPoint = new ArrayList<IDirectPosition>();

        // Remplissage du tableau de points
        for (int i = 0; i < popNuage.size(); i++) {
          IFeature f = popNuage.get(i);
          GM_Point p = (GM_Point)f.getGeom();
          listPoint.add(p.getPosition());
        }

        SwingingArmNonConvexHull alphaShapeBuilder = new SwingingArmNonConvexHull(listPoint, 200);
        IGeometry output200 = alphaShapeBuilder.compute();
        
        // System.out.println("geom = " + output.toString());
        
        FeatureType featureType = new FeatureType();
        featureType.setTypeName("AlphaShape");
        featureType.setGeometryType(IPolygon.class);
        
        DefaultFeature f200 = new DefaultFeature(output200);
        f200.setFeatureType(featureType);
        IPopulation<IFeature> popShape200 = new Population<IFeature>(false, "P200", DefaultFeature.class, true);
        popShape200.setFeatureType(featureType);
        popShape200.add(f200);
        
        
        alphaShapeBuilder = new SwingingArmNonConvexHull(listPoint, 500);
        IGeometry output500 = alphaShapeBuilder.compute();
        DefaultFeature f500 = new DefaultFeature(output500);
        f500.setFeatureType(featureType);
        IPopulation<IFeature> popShape500 = new Population<IFeature>(false, "P500", DefaultFeature.class, true);
        popShape500.setFeatureType(featureType);
        popShape500.add(f500);
        
        alphaShapeBuilder = new SwingingArmNonConvexHull(listPoint, 800);
        IGeometry output800 = alphaShapeBuilder.compute();
        DefaultFeature f800 = new DefaultFeature(output800);
        f800.setFeatureType(featureType);
        IPopulation<IFeature> popShape800 = new Population<IFeature>(false, "P800", DefaultFeature.class, true);
        popShape800.setFeatureType(featureType);
        popShape800.add(f800);
        
        
        Layer l1 = layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(popNuage, "nuage", null);
        PointSymbolizer symbolizerP2 = (PointSymbolizer) l1.getSymbolizer();
        symbolizerP2.setUnitOfMeasurePixel();
        symbolizerP2.getGraphic().setSize(6);
        symbolizerP2.getGraphic().getMarks().get(0).setWellKnownName("square");
        symbolizerP2.getGraphic().getMarks().get(0).getStroke().setStrokeWidth(1);
        symbolizerP2.getGraphic().getMarks().get(0).getStroke().setColor(new Color(210, 67, 71));
        symbolizerP2.getGraphic().getMarks().get(0).getFill().setColor(new Color(241, 208, 188));
        
        Layer l2 = layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(popShape200, "P200", null);
        PolygonSymbolizer symbolizerP1 = (PolygonSymbolizer)l2.getSymbolizer();
        symbolizerP1.setUnitOfMeasurePixel();
        symbolizerP1.getFill().setFill(new Color(146, 159, 162));
        
        Layer l3 = layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(popShape500, "P500", null);
        PolygonSymbolizer symbolizerP3 = (PolygonSymbolizer)l3.getSymbolizer();
        symbolizerP3.setUnitOfMeasurePixel();
        symbolizerP3.getFill().setFill(new Color(142, 50, 91));
        
        Layer l4 = layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(popShape800, "P800", null);
        PolygonSymbolizer symbolizerP4 = (PolygonSymbolizer)l4.getSymbolizer();
        symbolizerP4.setUnitOfMeasurePixel();
        symbolizerP4.getFill().setFill(new Color(90, 128, 151));
        
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      
      
      System.out.println("----------------------------------------------------------");
      
      
  }

  @Override
  public void actionPerformed(ActionEvent e) {

  }

  
  
}

