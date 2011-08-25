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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;
import java.net.URL;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Displacement;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.GraphicStroke;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Shadow;
import fr.ign.cogit.geoxygene.style.Style;

/**
 * Base class for GeOxygene applications.
 * 
 * @author Julien Perret
 */
public class SLDDemoApplication extends GeOxygeneApplication {
  /**
   * Main GeOxygene Application.
   * @param args arguments of the application
   */
  public static void main(final String[] args) {
    SplashScreen splashScreen = new SplashScreen(GeOxygeneApplication
        .splashImage(), "GeOxygene"); //$NON-NLS-1$
    splashScreen.setVisible(true);
    SLDDemoApplication application = new SLDDemoApplication();
    ProjectFrame projectFrame = application.getFrame().newProjectFrame();

    Layer layer = projectFrame.getSld().createLayer("Vegetation", //$NON-NLS-1$
        GM_Polygon.class, new Color(0.5f, 1.f, 0.5f), Color.green, 1f, 4);
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();
    GraphicFill graphicFill = new GraphicFill();
    Graphic graphicTree = new Graphic();
    graphicTree.setSize(100);
    ExternalGraphic tree = new ExternalGraphic();
    //tree.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
    //tree.setFormat("gif"); //$NON-NLS-1$
    URL url = SLDDemoApplication.class.getResource("/images/herbes.png"); //$NON-NLS-1$
    System.out.println(url);
    tree.setHref(url.toString());
    tree.setFormat("png"); //$NON-NLS-1$
    graphicTree.getExternalGraphics().add(tree);
    graphicFill.getGraphics().add(graphicTree);
    symbolizer.getFill().setGraphicFill(graphicFill);
    Population<DefaultFeature> pop = new Population<DefaultFeature>(
        "Vegetation"); //$NON-NLS-1$
    pop
        .add(new DefaultFeature(new GM_Polygon(new GM_Envelope(0, 100, 0, 100))));
    DataSet.getInstance().addPopulation(pop);
    projectFrame.addLayer(layer);

    Layer layer2 = projectFrame.getSld().createLayer("Batiment", //$NON-NLS-1$
        GM_Polygon.class, Color.blue, Color.gray, 1f, 2);
    layer2.getStyles().get(0).setGroup("default"); //$NON-NLS-1$
    PolygonSymbolizer symbolizer2 = (PolygonSymbolizer) layer2.getSymbolizer();
    Shadow shadow = new Shadow();
    shadow.setColor(Color.black);
    Displacement d = new Displacement();
    d.setDisplacementX(5);
    d.setDisplacementY(-5);
    shadow.setDisplacement(d);
    symbolizer2.setShadow(shadow);
    Style style2 = projectFrame.getSld().createStyle("Red", //$NON-NLS-1$
        GM_Polygon.class, Color.red, Color.gray, 1f, 2);
    layer2.getStyles().add(style2);
    layer2.setActiveGroup("default"); //$NON-NLS-1$
    Population<DefaultFeature> pop2 = new Population<DefaultFeature>("Batiment"); //$NON-NLS-1$
    pop2.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(110, 200, 110,
        200))));
    DataSet.getInstance().addPopulation(pop2);
    projectFrame.addLayer(layer2);

    Layer layer3 = projectFrame.getSld().createLayer("Route", //$NON-NLS-1$
        GM_LineString.class, Color.red, Color.red, 1f, 4);
    LineSymbolizer symbolizer3 = (LineSymbolizer) layer3.getSymbolizer();

    GraphicStroke graphicStroke = new GraphicStroke();
    Graphic graphicCircle = new Graphic();
    graphicCircle.setSize(10);
    ExternalGraphic externalGraphicCircle = new ExternalGraphic();
    URL urlCircle = SLDDemoApplication.class.getResource("/images/circle.png"); //$NON-NLS-1$
    externalGraphicCircle.setHref(urlCircle.toString());
    externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
    graphicCircle.getExternalGraphics().add(externalGraphicCircle);
    graphicStroke.getGraphics().add(graphicCircle);

    /*
     * Graphic graphicStar = new Graphic(); Mark markStar = new Mark();
     * markStar.setWellKnownName("star"); //$NON-NLS-1$
     * graphicStar.setSize(20f); Fill fillStar = new Fill();
     * fillStar.setColor(new Color(1.f,0.4f,0.4f)); markStar.setFill(fillStar);
     * graphicStar.getMarks().add(markStar);
     * graphicStroke.getGraphics().add(graphicStar);
     */
    symbolizer3.getStroke().setGraphicType(graphicStroke);

    Population<DefaultFeature> pop3 = new Population<DefaultFeature>("Route"); //$NON-NLS-1$
    pop3.add(new DefaultFeature(new GM_LineString(new DirectPositionList(new DirectPosition(110, 0), new DirectPosition(110, 100),
            new DirectPosition(200, 100)))));
    DataSet.getInstance().addPopulation(pop3);
    projectFrame.addLayer(layer3);

    Layer layer4 = projectFrame.getSld().createLayer("Chemin", //$NON-NLS-1$
        GM_LineString.class, Color.red, Color.red, 1f, 4);
    LineSymbolizer symbolizer4 = (LineSymbolizer) layer4.getSymbolizer();
    GraphicFill graphicFill2 = new GraphicFill();
    Graphic graphicCircles = new Graphic();
    graphicCircles.setSize(10);
    ExternalGraphic externalGraphic = new ExternalGraphic();
    //tree.setHref("http://recherche.ign.fr/labos/cogit/img/LOGO_COGIT.gif"); //$NON-NLS-1$
    //tree.setFormat("gif"); //$NON-NLS-1$
    URL urlCircles = SLDDemoApplication.class
        .getResource("/images/circles.png"); //$NON-NLS-1$
    externalGraphic.setHref(urlCircles.toString());
    externalGraphic.setFormat("png"); //$NON-NLS-1$
    graphicCircles.getExternalGraphics().add(externalGraphic);
    graphicFill2.getGraphics().add(graphicCircles);
    /*
     * Mark mark = new Mark(); mark.setWellKnownName("circle"); //$NON-NLS-1$
     * graphicCircle.setSize(10f); Fill fill = new Fill(); fill.setColor(new
     * Color(0.8f,0.2f,0.4f)); mark.setFill(fill);
     * graphicCircle.getMarks().add(mark);
     * graphicFill2.getGraphics().add(graphicCircle);
     */
    symbolizer4.getStroke().setGraphicType(graphicFill2);

    Population<DefaultFeature> pop4 = new Population<DefaultFeature>("Chemin"); //$NON-NLS-1$
    pop4.add(new DefaultFeature(new GM_LineString(new DirectPositionList(new DirectPosition(0, 110), new DirectPosition(100, 110),
            new DirectPosition(100, 200)))));
    DataSet.getInstance().addPopulation(pop4);
    projectFrame.addLayer(layer4);

    try {
      projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    application.getFrame().setVisible(true);
    splashScreen.setVisible(false);
    splashScreen.dispose();
  }
}
