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

package fr.ign.cogit.geoxygene.appli.example;


import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.SplashScreen;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Displacement;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.GraphicStroke;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Shadow;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.style.texture.Texture;

/**
 * Base class for GeOxygene applications.
 * 
 * @author Julien Perret
 */
public class SLDDemoApplication extends GeOxygeneApplication {

  public static ProjectFrame projectFrame;
  
  /**
   * Main GeOxygene Application.
   * @param args arguments of the application
   */
  public static void main(final String[] args) {
    SplashScreen splashScreen = new SplashScreen(
        GeOxygeneApplication.splashImage(), "GeOxygene"); //$NON-NLS-1$
    splashScreen.setVisible(true);
    SLDDemoApplication application = new SLDDemoApplication();
    projectFrame = application.getFrame().newProjectFrame();
    
    exampleGraphicFill_Fill_Polygon();
//    exampleGraphicFill_Stroke_Polygon();
//    exampleGraphicStroke_Stroke_Polygon();
//    exampleShadow();
//    exampleGraphicStroke_Line();
//    exampleGraphicFill_Line();
//    exampleTexture();
    
    FileWriter fichier;
    try {
      fichier = new FileWriter ("./src/main/resources/sld/GraphicMark.xml");
      projectFrame.getSld().marshall(fichier);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    
    try {
      projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    application.getFrame().setVisible(true);
    splashScreen.setVisible(false);
    splashScreen.dispose();
  }
  
  public static void exampleGraphicFill_Fill_Polygon() {
    Layer layer = projectFrame.getSld().createLayer("GraphicFill_Polygon", //$NON-NLS-1$
        GM_Polygon.class, new Color(0.5f, 1.f, 0.5f), Color.green, 1f, 4);
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();
    GraphicFill graphicFill = new GraphicFill();
    Graphic graphicTree = new Graphic();
    graphicTree.setSize(10);
    
    //Exemples avec des GraphicFill de type image
//    ExternalGraphic tree = new ExternalGraphic();
//    URL url = SLDDemoApplication.class.getResource("/images/herbes.png"); //$NON-NLS-1$
//    tree.setHref(url.toString());
//    tree.setFormat("png"); //$NON-NLS-1$
//    graphicTree.getExternalGraphics().add(tree);
    
//    ExternalGraphic cogitLogo = new ExternalGraphic();
//    cogitLogo.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
//    cogitLogo.setFormat("gif"); //$NON-NLS-1$
//    graphicTree.getExternalGraphics().add(cogitLogo);
    
//    graphicFill.getGraphics().add(graphicTree);

    //Exemple avec un graphic de type Mark
    Graphic graphicStar = new Graphic();
    graphicStar.setSize(8f);
    Mark markStar = new Mark();
    markStar.setWellKnownName("star"); //$NON-NLS-1$
    Fill fillStar = new Fill();
    fillStar.setColor(new Color(1.f,0.4f,0.4f));
    markStar.setFill(fillStar);
    graphicStar.getMarks().add(markStar);
    graphicFill.getGraphics().add(graphicStar);
    
    symbolizer.getFill().setGraphicFill(graphicFill);
    Population<DefaultFeature> pop = new Population<DefaultFeature>(
        "GraphicFill_Polygon"); //$NON-NLS-1$
    pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(230, 330, 120, 220))));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);

  }
  
  /**
   * Le Graphic Stroke permet de répéter une forme le long d'une ligne.
   * Cette forme peut être une image (ExternalGraphic) ou une forme prédéfinie (Mark).
   */
  public static void exampleGraphicFill_Stroke_Polygon() {
    Layer layer = projectFrame.getSld().createLayer("GraphicFill_Stroke_Polygon", //$NON-NLS-1$
        GM_Polygon.class, Color.red, Color.yellow, 1f, 4);
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();

    GraphicFill graphicFill = new GraphicFill();
    Graphic graphicCircle = new Graphic();
    graphicCircle.setSize(8f);
    
    ExternalGraphic circles = new ExternalGraphic();
    URL url = SLDDemoApplication.class.getResource("/images/circles.png"); //$NON-NLS-1$
    circles.setHref(url.toString());
    circles.setFormat("png"); //$NON-NLS-1$
    graphicCircle.getExternalGraphics().add(circles);
    
//    ExternalGraphic cogitLogo = new ExternalGraphic();
//    cogitLogo.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
//    cogitLogo.setFormat("gif"); //$NON-NLS-1$
//    graphicTree.getExternalGraphics().add(cogitLogo);
    
    graphicFill.getGraphics().add(graphicCircle);
    
    symbolizer.getStroke().setGraphicType(graphicFill);

    Population<DefaultFeature> pop = new Population<DefaultFeature>("GraphicFill_Stroke_Polygon"); //$NON-NLS-1$
    pop.add(new DefaultFeature(
        new GM_Polygon(new GM_Envelope(120, 220, 120, 220))
    ));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
  
  /**
   * Le Graphic Stroke permet de répéter une forme le long d'une ligne.
   * Cette forme peut être une image (ExternalGraphic) ou une forme prédéfinie (Mark).
   */
  public static void exampleGraphicStroke_Stroke_Polygon() {
    Layer layer = projectFrame.getSld().createLayer("GraphicStroke_Stroke_Polygon", //$NON-NLS-1$
        GM_Polygon.class, Color.red, Color.yellow, 1f, 1);
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();
    GraphicStroke graphicStroke = new GraphicStroke();
    
    //Exemple avec un graphic de type image png
    Graphic graphicCircle = new Graphic();
    graphicCircle.setSize(20f);
    ExternalGraphic externalGraphicCircle = new ExternalGraphic();
    URL urlCircle = SLDDemoApplication.class.getResource("/images/circle.png"); //$NON-NLS-1$
    externalGraphicCircle.setHref(urlCircle.toString());
    externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
    graphicCircle.getExternalGraphics().add(externalGraphicCircle);
    graphicStroke.getGraphics().add(graphicCircle);
    
    //Exemple avec un graphic de type Mark
//    Graphic graphicStar = new Graphic();
//    graphicStar.setSize(8f);
//    Mark markStar = new Mark();
//    markStar.setWellKnownName("star"); //$NON-NLS-1$
//    Fill fillStar = new Fill();
//    fillStar.setColor(new Color(1.f,0.4f,0.4f));
//    markStar.setFill(fillStar);
//    graphicStar.getMarks().add(markStar);
//    graphicStroke.getGraphics().add(graphicStar);
    
    symbolizer.getStroke().setGraphicType(graphicStroke);

    Population<DefaultFeature> pop = new Population<DefaultFeature>("GraphicStroke_Stroke_Polygon"); //$NON-NLS-1$
    pop.add(new DefaultFeature(
        new GM_Polygon(new GM_Envelope(120, 220, 10, 110))
    ));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
  
  public static void exampleShadow() {
    Layer layer = projectFrame.getSld().createLayer("Shadow", //$NON-NLS-1$
        GM_Polygon.class, Color.blue, Color.yellow, 1f, 2);
    layer.getStyles().get(0).setGroup("default"); //$NON-NLS-1$
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();

    Shadow shadow = new Shadow();
    shadow.setColor(Color.black);
    Displacement d = new Displacement();
    d.setDisplacementX(5);
    d.setDisplacementY(-5);
    shadow.setDisplacement(d);
    symbolizer.setShadow(shadow);
    Style style = projectFrame.getSld().createStyle("Red", //$NON-NLS-1$
        GM_Polygon.class, Color.red, Color.gray, 1f, 2);

    layer.getStyles().add(style);
    layer.setActiveGroup("default"); //$NON-NLS-1$
    Population<DefaultFeature> pop = new Population<DefaultFeature>("Shadow"); //$NON-NLS-1$
    pop.add(new DefaultFeature(
        new GM_Polygon(new GM_Envelope(10, 110, 230, 330))
    ));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
  
  /**
   * Le Graphic Stroke permet de répéter une forme le long d'une ligne.
   * Cette forme peut être une image (ExternalGraphic) ou une forme prédéfinie (Mark).
   */
  public static void exampleGraphicStroke_Line() {
    Layer layer = projectFrame.getSld().createLayer("GraphicStroke_Line", //$NON-NLS-1$
        GM_LineString.class, Color.red, Color.red, 1f, 1);
    LineSymbolizer symbolizer = (LineSymbolizer) layer.getSymbolizer();

    GraphicStroke graphicStroke = new GraphicStroke();

    //Exemple avec un Graphic de type image png
    Graphic graphicCircle = new Graphic();
    graphicCircle.setSize(20f);
    ExternalGraphic externalGraphicCircle = new ExternalGraphic();
    URL urlCircle = SLDDemoApplication.class.getResource("/images/circle.png"); //$NON-NLS-1$
    externalGraphicCircle.setHref(urlCircle.toString());
    externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
    graphicCircle.getExternalGraphics().add(externalGraphicCircle);
    graphicStroke.getGraphics().add(graphicCircle);
    
    //Exemple avec un Graphic de type Mark
//    Graphic graphicStar = new Graphic();
//    graphicStar.setSize(8f);
//    Mark markStar = new Mark();
//    markStar.setWellKnownName("star"); //$NON-NLS-1$
//    Fill fillStar = new Fill();
//    fillStar.setColor(new Color(1.f,0.4f,0.4f));
//    markStar.setFill(fillStar);
//    graphicStar.getMarks().add(markStar);
//    graphicStroke.getGraphics().add(graphicStar);
    
    symbolizer.getStroke().setGraphicType(graphicStroke);

    Population<DefaultFeature> pop = new Population<DefaultFeature>("GraphicStroke_Line"); //$NON-NLS-1$
    pop.add(new DefaultFeature(new GM_LineString(new DirectPositionList(
        new DirectPosition(10, 10), new DirectPosition(10, 60), new DirectPosition(60, 60),
        new DirectPosition(60, 110), new DirectPosition(110, 110)))));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
  
  /**
   * Le Graphic Fill permet de répéter une forme dans le remplissage d'une ligne.
   * Cette forme peut être une image (ExternalGraphic) ou une forme prédéfinie (Mark).
   */
  public static void exampleGraphicFill_Line() {
    Layer layer = projectFrame.getSld().createLayer("GraphicFill_Line", //$NON-NLS-1$
        GM_LineString.class, Color.red, Color.red, 1f, 4);
    LineSymbolizer symbolizer = (LineSymbolizer) layer.getSymbolizer();
    GraphicFill graphicFill = new GraphicFill();
    Graphic graphicCircles = new Graphic();

    //Exemple avec un Graphic de type image png
    graphicCircles.setSize(8f);
    ExternalGraphic externalGraphic = new ExternalGraphic();
    URL urlCircles = SLDDemoApplication.class
        .getResource("/images/circles.png"); //$NON-NLS-1$
    externalGraphic.setHref(urlCircles.toString());
    externalGraphic.setFormat("png"); //$NON-NLS-1$
    graphicCircles.getExternalGraphics().add(externalGraphic);
    graphicFill.getGraphics().add(graphicCircles);
    
    //Exemple avec un Graphic de type Mark
//    Mark mark = new Mark();
//    mark.setWellKnownName("circle"); //$NON-NLS-1$
//    graphicCircles.setSize(5f);
//    Fill fill = new Fill();
//    fill.setColor(new Color(0.8f,0.2f,0.4f));
//    mark.setFill(fill);
//    graphicCircles.getMarks().add(mark);
//    graphicFill.getGraphics().add(graphicCircles);
    
    symbolizer.getStroke().setGraphicType(graphicFill);

    Population<DefaultFeature> pop = new Population<DefaultFeature>("GraphicFill_Line"); //$NON-NLS-1$
    pop.add(new DefaultFeature(new GM_LineString(new DirectPositionList(
        new DirectPosition(10, 120), new DirectPosition(10, 170), new DirectPosition(60, 170),
        new DirectPosition(60, 220), new DirectPosition(110, 220)))));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
  
  public static void exampleTexture() {
    Layer layer = projectFrame.getSld().createLayer("Texture", //$NON-NLS-1$
        GM_Polygon.class, Color.black, Color.red, 1f, 4);
    
    PolygonSymbolizer symbolizer = (PolygonSymbolizer)layer.getSymbolizer();
    Fill fill = new Fill();
    fill.setFill(Color.pink);
    symbolizer.setFill(fill);
    
    Texture texture = new PerlinNoiseTexture();
    symbolizer.getFill().setTexture(texture);
    
    Population<DefaultFeature> pop = new Population<DefaultFeature>("Texture"); //$NON-NLS-1$
    pop.add(new DefaultFeature(
        new GM_Polygon(new GM_Envelope(120, 220, 230, 330))
    ));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }
}
