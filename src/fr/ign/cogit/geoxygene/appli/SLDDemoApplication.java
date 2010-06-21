/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.geom.NoninvertibleTransformException;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

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
        SplashScreen splashScreen = new SplashScreen(splashImage(),
        "GeOxygene"); //$NON-NLS-1$
        splashScreen.setVisible(true);
        SLDDemoApplication application = new SLDDemoApplication();
        ProjectFrame projectFrame = application.getFrame().newProjectFrame();
        Layer layer = projectFrame.getSld().createLayer("Vegetation", //$NON-NLS-1$
                GM_Polygon.class, new Color(0.5f, 1.f, 0.5f), Color.green,
                1f, 4);
        PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();
        GraphicFill graphicFill = new GraphicFill();
        Graphic graphicCircle = new Graphic();
        Mark mark = new Mark();
        mark.setWellKnownName("circle"); //$NON-NLS-1$
        graphicCircle.setSize(20f);
        Fill fill = new Fill();
        fill.setColor(new Color(0.4f,1.f,0.4f));
        mark.setFill(fill);
        graphicCircle.getMarks().add(mark);
        //graphicFill.getGraphics().add(graphicCircle);
        Graphic graphicStar = new Graphic();
        Mark markStar = new Mark();
        markStar.setWellKnownName("star"); //$NON-NLS-1$
        graphicStar.setSize(10f);
        Fill fillStar = new Fill();
        fillStar.setColor(new Color(1.f,0.4f,0.4f));
        markStar.setFill(fillStar);
        graphicStar.getMarks().add(markStar);
        //graphicFill.getGraphics().add(graphicStar);
        Graphic graphicTree = new Graphic();
        graphicTree.setSize(30);
        ExternalGraphic tree = new ExternalGraphic();
        tree.setHref("http://terrapreta.bioenergylists.org/files/images/tree.gif"); //$NON-NLS-1$
        tree.setFormat("gif");
        graphicTree.getExternalGraphics().add(tree);
        graphicFill.getGraphics().add(graphicTree);
        symbolizer.getFill().setGraphicFill(graphicFill);
        Population<DefaultFeature> pop = new Population<DefaultFeature>("Vegetation"); //$NON-NLS-1$
        pop.add(new DefaultFeature(new GM_Polygon(new GM_Envelope(0, 100, 0, 100))));
        DataSet.getInstance().addPopulation(pop);
        projectFrame.addLayer(layer);
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
