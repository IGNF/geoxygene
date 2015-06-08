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
import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
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
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTextureDescriptor;

/**
 * Test .
 * 
 * 
 */
public class SLDDemoApplication extends AbstractGeOxygeneApplicationPlugin {

    /** Logger. */
    static final Logger LOGGER = Logger.getLogger(SLDDemoApplication.class.getName());

    /** Titles. */
    static final String MENU_TITLE = "Example";
    static final String SUBMENU_TITLE = "Style";
    static final String AWT_TITLE = "motifs & poncifs avec AWT";
    static final String OGL_TITLE = "motifs & poncifs avec OpenGL";
    static final String DERAIN_TITLE = "style Derain (OpenGL)";
    static final String AQUARELLE_TITLE = "style Aquarelle (OpenGL)";
    
    private ProjectFrame projectFrame = null;

    @Override
    public void initialize(final GeOxygeneApplication application) {
      this.application = application;

      JMenu menuAwt = addSubMenu(MENU_TITLE, SUBMENU_TITLE, AWT_TITLE);
      application.getMainFrame().getMenuBar()
        .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
      
      JMenu menuOgl = addSubMenu(MENU_TITLE, SUBMENU_TITLE, OGL_TITLE);
      application.getMainFrame().getMenuBar()
        .add(menuOgl, application.getMainFrame().getMenuBar().getMenuCount() - 2);
      
      JMenu menuDerain = addSubMenu(MENU_TITLE, SUBMENU_TITLE, DERAIN_TITLE);
      application.getMainFrame().getMenuBar()
        .add(menuDerain, application.getMainFrame().getMenuBar().getMenuCount() - 2);
      
      JMenu menuAquarelle = addSubMenu(MENU_TITLE, SUBMENU_TITLE, AQUARELLE_TITLE);
      application.getMainFrame().getMenuBar()
        .add(menuAquarelle, application.getMainFrame().getMenuBar().getMenuCount() - 2);
      
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (source instanceof JMenuItem && ((JMenuItem)source).getText().equals(AWT_TITLE)) {
        this.projectFrame = application
            .getMainFrame().newProjectFrame(
                    LayerViewPanelFactory.newLayerViewAwtPanel());
        this.displayCercles();
      } else if (source instanceof JMenuItem && ((JMenuItem)source).getText().equals(OGL_TITLE)) {
        this.projectFrame = application
            .getMainFrame().newProjectFrame(
                    LayerViewPanelFactory.newLayerViewGLPanel());
        this.displayCercles();  
      } else if (source instanceof JMenuItem && ((JMenuItem)source).getText().equals(DERAIN_TITLE)) {
        this.projectFrame = application
            .getMainFrame().newProjectFrame(
                    LayerViewPanelFactory.newLayerViewGLPanel());
        this.displayDerain();
      } else if (source instanceof JMenuItem && ((JMenuItem)source).getText().equals(AQUARELLE_TITLE)) {
        this.projectFrame = application
            .getMainFrame().newProjectFrame(
                    LayerViewPanelFactory.newLayerViewGLPanel());
        this.displayAquarelle();
      } 
    }

    public void displayCercles() {
        // ProjectFrame projectFrame =
        // application.getMainFrame().newProjectFrame();

        this.exampleGraphicFill_Fill_Polygon();
        this.exampleGraphicFill_Stroke_Polygon();
        this.exampleGraphicStroke_Stroke_Polygon();
        this.exampleShadow();
        this.exampleGraphicStroke_Line();
        this.exampleGraphicFill_Line();
        this.exampleTexture();

        FileWriter fichier;
        try {
            fichier = new FileWriter("./src/main/resources/sld/GraphicMark.xml");
            this.projectFrame.getSld().marshall(fichier);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            this.projectFrame.getLayerViewPanel().getViewport()
                    .zoomToFullExtent();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    public void exampleGraphicFill_Fill_Polygon() {

        Layer layer = this.projectFrame.getSld().createLayer(
                "GraphicFill_Polygon", //$NON-NLS-1$
                GM_Polygon.class, new Color(0.5f, 1.f, 0.5f), Color.green,
                0.5f, 4);
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer
                .getSymbolizer();
        GraphicFill graphicFill = new GraphicFill();

        // --------------- Exemples avec des Graphic de type image
        // ----------------------------------
        // Graphic graphic = new Graphic();
        // graphic.setSize(10);
        // ----- Image au format svg
        // ExternalGraphic circle = new ExternalGraphic();
        //    URL url = SLDDemoApplication.class.getResource("/images/circle.svg"); //$NON-NLS-1$
        // circle.setHref(url.toString());
        //    circle.setFormat("svg"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(circle);

        // ----- Image au format png
        // ExternalGraphic tree = new ExternalGraphic();
        //    URL url = SLDDemoApplication.class.getResource("/images/herbes.png"); //$NON-NLS-1$
        // tree.setHref(url.toString());
        //    tree.setFormat("png"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(tree);

        // ----- Image au format gif
        // ExternalGraphic cogitLogo = new ExternalGraphic();
        //     cogitLogo.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
        //     cogitLogo.setFormat("gif"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(cogitLogo);

        // --------------- Exemple avec un Graphic de type Mark
        // ----------------------------------
        Graphic graphic = new Graphic();
        graphic.setSize(8f);
        Mark markStar = new Mark();
        markStar.setWellKnownName("star"); //$NON-NLS-1$
        Fill fillStar = new Fill();
        fillStar.setColor(Color.YELLOW);
        markStar.setFill(fillStar);
        graphic.getMarks().add(markStar);

        graphicFill.getGraphics().add(graphic);
        symbolizer.getFill().setColor(Color.GREEN);
        symbolizer.getFill().setGraphicFill(graphicFill);
        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "GraphicFill_Polygon"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(230, 330,
                120, 220))));
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(250, 350,
                100, 200))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);

    }

    /**
     * Le Graphic Stroke permet de répéter une forme le long d'une ligne. Cette
     * forme peut être une image (ExternalGraphic) ou une forme prédéfinie
     * (Mark).
     */
    public void exampleGraphicFill_Stroke_Polygon() {
        Layer layer = this.projectFrame.getSld().createLayer(
                "GraphicFill_Stroke_Polygon", //$NON-NLS-1$
                GM_Polygon.class, Color.red, Color.yellow, 1f, 4);
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer
                .getSymbolizer();
        GraphicFill graphicFill = new GraphicFill();

        // --------------- Exemples avec des Graphic de type image
        // ----------------------------------
        // Graphic graphic = new Graphic();
        // graphic.setSize(10);
        // ----- Image au format svg
        // ExternalGraphic circle = new ExternalGraphic();
        //    URL url = SLDDemoApplication.class.getResource("/images/circle.svg"); //$NON-NLS-1$
        // circle.setHref(url.toString());
        //    circle.setFormat("svg"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(circle);

        // ----- Image au format png
        // ExternalGraphic tree = new ExternalGraphic();
        //    URL url = SLDDemoApplication.class.getResource("/images/herbes.png"); //$NON-NLS-1$
        // tree.setHref(url.toString());
        //    tree.setFormat("png"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(tree);

        // ----- Image au format gif
        // ExternalGraphic cogitLogo = new ExternalGraphic();
        //     cogitLogo.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
        //     cogitLogo.setFormat("gif"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(cogitLogo);

        // --------------- Exemple avec un Graphic de type Mark
        // ----------------------------------
        Graphic graphic = new Graphic();
        graphic.setSize(8f);
        Mark markStar = new Mark();
        markStar.setWellKnownName("star"); //$NON-NLS-1$
        Fill fillStar = new Fill();
        fillStar.setColor(Color.YELLOW);
        markStar.setFill(fillStar);
        graphic.getMarks().add(markStar);

        graphicFill.getGraphics().add(graphic);
        symbolizer.getStroke().setColor(Color.GREEN);
        symbolizer.getStroke().setGraphicType(graphicFill);
        symbolizer.getStroke().setStrokeWidth(4);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "GraphicFill_Stroke_Polygon"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(120, 220,
                120, 220))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }

    /**
     * Le Graphic Stroke permet de répéter une forme le long d'une ligne. Cette
     * forme peut être une image (ExternalGraphic) ou une forme prédéfinie
     * (Mark).
     */
    public void exampleGraphicStroke_Stroke_Polygon() {
        Layer layer = this.projectFrame.getSld().createLayer(
                "GraphicStroke_Stroke_Polygon", //$NON-NLS-1$
                GM_Polygon.class, Color.red, Color.yellow, 1f, 1);
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer
                .getSymbolizer();
        GraphicStroke graphicStroke = new GraphicStroke();

        // --------------- Exemple avec un graphic de type image
        // ----------------------------------
        Graphic graphicCircle = new Graphic();
        graphicCircle.setSize(20f);
        ExternalGraphic externalGraphicCircle = new ExternalGraphic();
        URL urlCircle = SLDDemoApplication.class
                .getResource("/images/circle.png"); //$NON-NLS-1$
        externalGraphicCircle.setHref(urlCircle.toString());
        externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
        graphicCircle.getExternalGraphics().add(externalGraphicCircle);
        graphicStroke.getGraphics().add(graphicCircle);

        // --------------- Exemple avec un graphic de type Mark
        // ----------------------------------
        // Graphic graphicStar = new Graphic();
        // graphicStar.setSize(8f);
        // Mark markStar = new Mark();
        //        markStar.setWellKnownName("star"); //$NON-NLS-1$
        // Fill fillStar = new Fill();
        // fillStar.setColor(new Color(1.f,0.4f,0.4f));
        // markStar.setFill(fillStar);
        // graphicStar.getMarks().add(markStar);
        // graphicStroke.getGraphics().add(graphicStar);

        symbolizer.getStroke().setGraphicType(graphicStroke);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "GraphicStroke_Stroke_Polygon"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(120, 220, 10,
                110))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }

    public void exampleShadow() {
        Layer layer = this.projectFrame.getSld().createLayer("Shadow", //$NON-NLS-1$
                GM_Polygon.class, Color.blue, Color.yellow, 1f, 2);
        layer.getStyles().get(0).setGroup("default"); //$NON-NLS-1$
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer
                .getSymbolizer();

        Shadow shadow = new Shadow();
        shadow.setColor(Color.black);
        Displacement d = new Displacement();
        d.setDisplacementX(5);
        d.setDisplacementY(-5);
        shadow.setDisplacement(d);
        symbolizer.setShadow(shadow);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "Shadow"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(10, 110, 230,
                330))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }

    /**
     * Le Graphic Stroke permet de répéter une forme le long d'une ligne. Cette
     * forme peut être une image (ExternalGraphic) ou une forme prédéfinie
     * (Mark).
     */
    public void exampleGraphicStroke_Line() {
        Layer layer = this.projectFrame.getSld().createLayer(
                "GraphicStroke_Line", //$NON-NLS-1$
                GM_LineString.class, Color.red, Color.red, 1f, 1);
        LineSymbolizer symbolizer = (LineSymbolizer) layer.getSymbolizer();

        GraphicStroke graphicStroke = new GraphicStroke();

        // --------------- Exemple avec un Graphic de type image
        // ----------------------------------
        // Graphic graphic = new Graphic();
        // graphic.setSize(20f);
        // ExternalGraphic externalGraphicCircle = new ExternalGraphic();
        //    URL urlCircle = SLDDemoApplication.class.getResource("/images/circle.png"); //$NON-NLS-1$
        // externalGraphicCircle.setHref(urlCircle.toString());
        //    externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(externalGraphicCircle);

        // --------------- Exemple avec un Graphic de type Mark
        // ----------------------------------
        // Graphic graphicStar = new Graphic();
        // graphicStar.setSize(8f);
        // Mark markStar = new Mark();
        //        markStar.setWellKnownName("star"); //$NON-NLS-1$
        // Fill fillStar = new Fill();
        // fillStar.setColor(new Color(1.f,0.4f,0.4f));
        // markStar.setFill(fillStar);
        // graphicStar.getMarks().add(markStar);
        // graphicStroke.getGraphics().add(graphicStar);

        // --------------- Exemple avec un Graphic de type svg
        // ----------------------------------
        Graphic graphic = new Graphic();
        graphic.setSize(10);

        ExternalGraphic circle = new ExternalGraphic();
        URL url = SLDDemoApplication.class.getResource("/images/circle.svg"); //$NON-NLS-1$
        System.out.println(url);
        circle.setHref(url.toString());
        circle.setFormat("svg"); //$NON-NLS-1$
        graphic.getExternalGraphics().add(circle);

        graphicStroke.getGraphics().add(graphic);
        symbolizer.getStroke().setGraphicType(graphicStroke);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "GraphicStroke_Line"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_LineString(new DirectPositionList(
                new DirectPosition(10, 10), new DirectPosition(10, 60),
                new DirectPosition(60, 60), new DirectPosition(60, 110),
                new DirectPosition(110, 110)))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }

    /**
     * Le Graphic Fill permet de répéter une forme dans le remplissage d'une
     * ligne. Cette forme peut être une image (ExternalGraphic) ou une forme
     * prédéfinie (Mark).
     */
    public void exampleGraphicFill_Line() {
        Layer layer = this.projectFrame.getSld().createLayer(
                "GraphicFill_Line", //$NON-NLS-1$
                GM_LineString.class, Color.green, Color.red, 1f, 4);
        LineSymbolizer symbolizer = (LineSymbolizer) layer.getSymbolizer();
        GraphicFill graphicFill = new GraphicFill();
        Graphic graphic = new Graphic();
        graphic.setSize(5f);
        // FIXME Avec cette image svg (herbes.svg), la taille doit être un
        // multiple
        // de 5 ...

        // --------------- Exemple avec un Graphic de type image
        // ----------------------------------
        // ----- Image au format svg
        ExternalGraphic circle = new ExternalGraphic();
        URL url = SLDDemoApplication.class.getResource("/images/circles.svg"); //$NON-NLS-1$
        circle.setHref(url.toString());
        circle.setFormat("svg"); //$NON-NLS-1$
        graphic.getExternalGraphics().add(circle);

        // ----- Image au format png
        // ExternalGraphic externalGraphic = new ExternalGraphic();
        // URL urlCircles = SLDDemoApplication.class
        //        .getResource("/images/circles.png"); //$NON-NLS-1$
        // externalGraphic.setHref(urlCircles.toString());
        //    externalGraphic.setFormat("png"); //$NON-NLS-1$
        // graphic.getExternalGraphics().add(externalGraphic);

        // --------------- Exemple avec un Graphic de type Mark
        // ----------------------------------
        // Mark mark = new Mark();
        //    mark.setWellKnownName("circle"); //$NON-NLS-1$
        // graphic.setSize(20f);
        // Fill fill = new Fill();
        // fill.setColor(Color.YELLOW);
        // mark.setFill(fill);
        // graphic.getMarks().add(mark);

        graphicFill.getGraphics().add(graphic);
        symbolizer.getStroke().setGraphicType(graphicFill);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "GraphicFill_Line"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_LineString(new DirectPositionList(
                new DirectPosition(10, 120), new DirectPosition(10, 170),
                new DirectPosition(60, 170), new DirectPosition(60, 220),
                new DirectPosition(110, 220)))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }

    public void exampleTexture() {
        Layer layer = this.projectFrame.getSld().createLayer("Texture", //$NON-NLS-1$
                GM_Polygon.class, Color.black, Color.blue, 0.8f, 4);
        layer.setOpacity(0.6f);
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer
                .getSymbolizer();

        PerlinNoiseTextureDescriptor texture = new PerlinNoiseTextureDescriptor();
        texture.setTextureResolution(6000);
        symbolizer.getFill().setFill2DDescriptor(texture);

        Population<DefaultFeature> pop = new Population<DefaultFeature>(
                "Texture"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(120, 220,
                230, 330))));
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(150, 250,
                250, 350))));
        this.projectFrame.getDataSet().addPopulation(pop);
        this.projectFrame.getSld().add(layer);
    }
    
    
    public void displayAquarelle() {
      String sldFilename = "./samples/aquarelle/aquarelle1.xml";
      
      /*String lieuDitHabiteFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/LIEU_DIT_HABITE.shp";
      File lieuDitHabitefile = new File(lieuDitHabiteFilename);
      this.projectFrame.addLayer(lieuDitHabitefile);*/
      
      
      displayExtrait1WithSld(sldFilename, "mer_OCS_100");
    }
    
    public void displayDerain() {
      String sldFilename = "./samples/derain/derain-collioure/SLD/100K_StJean_de_Luz-derain-collioure.sld.xml";
      displayExtrait1WithSld(sldFilename, "surface_eau_OCS_100");
    }
    
    
    private void displayExtrait1WithSld(String sldFilename, String styleEau) {
      
      String vegetationFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/ZONE_VEGETATION.shp";
      File vegetationfile = new File(vegetationFilename);
      this.projectFrame.addLayer(vegetationfile);
      
      String surfaceEauFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/SURFACE_EAU.shp";
      File surfaceEaufile = new File(surfaceEauFilename);
      this.projectFrame.addLayer(surfaceEaufile);
      
      String routeFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/ROUTE.shp";
      File routefile = new File(routeFilename);
      this.projectFrame.addLayer(routefile);
      
      String tronconEauFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/TRONCON_COURS_EAU.shp";
      File tronconEaufile = new File(tronconEauFilename);
      this.projectFrame.addLayer(tronconEaufile);
      
      String empriseFilename = "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/petite_emprise.shp";
      File emprisefile = new File(empriseFilename);
      this.projectFrame.addLayer(emprisefile);
      
      // 
      File sldFile = new File(sldFilename);
      
      try {
          
        StyledLayerDescriptor new_sld = StyledLayerDescriptor.unmarshall(
            sldFile.getAbsolutePath(), this.projectFrame.getDataSet());
          
        this.projectFrame.getLayerViewPanel().setViewBackground(new_sld.getBackground());
        this.projectFrame.getSld().setBackground(new_sld.getBackground());
        for (int i = 0; i < this.projectFrame.getLayers().size(); i++) {
          String name = this.projectFrame.getLayers().get(i).getName();
          LOGGER.debug(name);
            
          
          if (name.equals("ZONE_VEGETATION")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer("vegetation_OCS_100").getStyles());
          }
          if (name.equals("SURFACE_EAU")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer(styleEau).getStyles());
          }
          if (name.equals("ROUTE")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer("routes_100").getStyles());
          }
          if (name.equals("TRONCON_COURS_EAU")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer("hydro_100").getStyles());
          }
          if (name.equals("LIEU_DIT_HABITE")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer("toponyme_droit_25_emprise_100K").getStyles());
          }
          if (name.equals("petite_emprise")) {
            this.projectFrame.getLayers().get(i).setStyles(new_sld.getLayer("petite_emprise").getStyles());
          }
        }
        
        this.projectFrame.getLayerViewPanel().repaint();
      } catch (FileNotFoundException e) {
        LOGGER.error("SLD filename '" + sldFilename
              + "' does not exist or is unreadable");
      } catch (JAXBException e) {
        LOGGER.error("Malformed SLD file '" + sldFilename + "'");
        e.printStackTrace();
      }
      
      
    }
    
    
}
