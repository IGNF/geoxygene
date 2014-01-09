/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.datamatching;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrameMenuBar;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamFilenamePopulationEdgesNetwork;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamPluginNetworkDataMatching;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui.EditParamPanel;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Data matching plugin. Display 2 types of results : - links + stats + param -
 * corrected network + comparison network
 * 
 * @since 1.6 manage new Result structure
 * @author Julien Perret
 */
public class NetworkDataMatchingPlugin implements GeOxygeneApplicationPlugin, ActionListener {

    /** Classic logger. */
    private static Logger LOGGER = Logger.getLogger(NetworkDataMatchingPlugin.class.getName());

    /** GeOxygeneApplication. */
    protected GeOxygeneApplication application;

    /** Parameters and datasets. */
    protected ParamPluginNetworkDataMatching paramPlugin = new ParamPluginNetworkDataMatching();
    private DataSet datasetNetwork1;
    private DataSet datasetNetwork2;

    /** Displayed colors. */
    private final static Color nodeColor = new Color(255, 216, 0);

    private final static Color linkColorOk = new Color(77, 146, 33);
    private final static Color linkColorNull = new Color(239, 59, 44);
    private final static Color linkColorDoubtfull = new Color(240, 157, 18);

    private final static Color network1Color = new Color(11, 73, 157);

    private final static Color network2Color = new Color(145, 100, 10);
    private final static Color matchedNetworkColor = new Color(136, 64, 153);
    private final static int LINE_WIDTH = 3;
    private final static int LINE_WIDTH_LINK = 1;

    /**
     * Initialize the plugin.
     * @param application the application
     */
    @Override
    public void initialize(final GeOxygeneApplication application) {

        this.application = application;

        // Check if the DataMatching menu exists. If not we create it.
        JMenu menu = null;
        String menuName = I18N.getString("DataMatchingPlugin.DataMatching"); //$NON-NLS-1$
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

        // Add network data matching menu item to the menu.
        JMenuItem menuItem = new JMenuItem(I18N.getString("DataMatchingPlugin.NDM.OpenDataMatchingEnvironment" //$NON-NLS-1$
                ));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        // initialize Param
        initializeParam();

        // Refresh menu of the application
        int menuComponentCount = application.getMainFrame().getMenuBar().getComponentCount();
        application.getMainFrame().getMenuBar().add(menu, menuComponentCount - 2);
    }

    /**
     * Initialize default parameters from an XML files.
     */
    private void initializeParam() {
        
        // String filename1 = "D:\\Data\\Appariement\\MesTests\\T3\\bdcarto_route.shp";
        // String filename2 = "D:\\Data\\Appariement\\MesTests\\T3\\bdtopo_route.shp";
        // String filename1 = "D:\\DATA\\Appariement\\ESPON_DB\\Test03\\Reseau1.shp";
        // String filename2 = "D:\\DATA\\Appariement\\ESPON_DB\\Test03\\Reseau2.shp";
        String filename1 = "E:\\Workspace\\GeOxygene\\data\\geoxygene-data\\sample-dataset\\rondpoint\\bdcarto.shp";
        String filename2 = "E:\\Workspace\\GeOxygene\\data\\geoxygene-data\\sample-dataset\\rondpoint\\bdtopo.shp";
        
        ParamFilenamePopulationEdgesNetwork paramFilename1 = new ParamFilenamePopulationEdgesNetwork();
        paramFilename1.addFilename(filename1);
        paramPlugin.setParamFilenameNetwork1(paramFilename1);

        ParamFilenamePopulationEdgesNetwork paramFilename2 = new ParamFilenamePopulationEdgesNetwork();
        paramFilename2.addFilename(filename2);
        paramPlugin.setParamFilenameNetwork2(paramFilename2);

        // -----------------------------------------------------------------------------------------
        // Param
        ParametresApp param = new ParametresApp();

        // Direction
        param.populationsArcsAvecOrientationDouble2 = false;
        param.attributOrientation2 = "DIR_TRAVEL";
        Map<Object, Integer> orientationMap2 = new HashMap<Object, Integer>(); 
        orientationMap2.put("T", OrientationInterface.SENS_DIRECT);
        orientationMap2.put("F", OrientationInterface.SENS_INVERSE);
        orientationMap2.put("B", OrientationInterface.DOUBLE_SENS);
        param.orientationMap2 = orientationMap2;
        
        // Distance
        float distanceNoeudsMax = 50;
        param.distanceNoeudsMax = distanceNoeudsMax;
        param.distanceArcsMax = 2 * distanceNoeudsMax;
        param.distanceArcsMin = 1;

        // Topologie
        param.topologieGraphePlanaire1 = true;
        param.topologieFusionArcsDoubles1 = true;
        param.topologieSeuilFusionNoeuds1 = 0.2;

        param.topologieGraphePlanaire2 = true;
        param.topologieFusionArcsDoubles2 = true;
        param.topologieSeuilFusionNoeuds2 = 0.2;
        
        // Projection
        param.projeteNoeuds1SurReseau2 = true;
        param.projeteNoeuds1SurReseau2DistanceNoeudArc = distanceNoeudsMax;
        param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 2 * distanceNoeudsMax;

        param.projeteNoeuds2SurReseau1 = true;
        param.projeteNoeuds2SurReseau1DistanceNoeudArc = distanceNoeudsMax;
        param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 2 * distanceNoeudsMax;
        param.projeteNoeuds2SurReseau1ImpassesSeulement = true;
        
        // Variante
        param.varianteChercheRondsPoints = true;
        
        //
        paramPlugin.setParamNetworkDataMatching(param);

        // -----------------------------------------------------------------------------------------
        // Actions
        paramPlugin.setDoRecalage(false);
        paramPlugin.setDoLinkExport(false);

    }

    /**
     * 
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        // Launch parameter network data matching panel.
        EditParamPanel dialogParamDataMatchingNetwork = new EditParamPanel(this);
        if (dialogParamDataMatchingNetwork.getAction().equals("LAUNCH")) {
            doNetworkDataMatching();
        }
    }

    /**
     * @param f The parameters XML Filename to set
     */
    public void setParamPlugin(ParamPluginNetworkDataMatching p) {
        paramPlugin = p;
    }

    public ParamPluginNetworkDataMatching getParamPlugin() {
        return paramPlugin;
    }

    protected void doNetworkDataMatching() {
        // -------------------------------------------------------------------------------
        // Dataset
        String lastFile = "";
        for (int i = 0; i < paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().size(); i++) {
            String filename = paramPlugin.getParamFilenameNetwork1().getListNomFichiersPopArcs().get(i);
            lastFile = filename;
            IPopulation<IFeature> reseau = ShapefileReader.read(filename);
            reseau.setNom("Edge");
            datasetNetwork1 = new DataSet();
            datasetNetwork1.addPopulation(reseau);
            IPopulation<IFeature> popNode = new Population<IFeature>("Node");
            datasetNetwork1.addPopulation(popNode);
        }
        for (int i = 0; i < paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().size(); i++) {
            String filename = paramPlugin.getParamFilenameNetwork2().getListNomFichiersPopArcs().get(i);
            lastFile = filename;
            IPopulation<IFeature> reseau = ShapefileReader.read(filename);
            reseau.setNom("Edge");
            datasetNetwork2 = new DataSet();
            datasetNetwork2.addPopulation(reseau);
            IPopulation<IFeature> popNode = new Population<IFeature>("Node");
            datasetNetwork1.addPopulation(popNode);
        }
        
        if (datasetNetwork1.getPopulation("Edge").size() < 1) {
          javax.swing.JOptionPane.showMessageDialog(null, "You need to select one layer for network 1.");
        } else if (datasetNetwork2.getPopulation("Edge").size() < 1) {
          javax.swing.JOptionPane.showMessageDialog(null, "You need to select one layer for network 2.");
        } else {

        // Set last Opened file
        application.getProperties().setLastOpenedFile(lastFile);
        MainFrameMenuBar.fc.setPreviousDirectory(new File(lastFile));
        application.getProperties().marshall(application.getPropertiesFile().getFile());

        NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(
                paramPlugin.getParamNetworkDataMatching(), datasetNetwork1, datasetNetwork2);
        networkDataMatchingProcess.setActions(paramPlugin.getDoRecalage(), paramPlugin.getDoLinkExport());
        ResultNetworkDataMatching resultatAppariement = networkDataMatchingProcess.networkDataMatching();

        // Logs
        LOGGER.info("Nb arcs du réseau 1 calculés : "
                + resultatAppariement.getReseau1().getListeArcs().size() + " >= "
                + datasetNetwork1.getPopulation("Edge").size());
        LOGGER.info("Nb arcs du réseau 2 calculés : "
                + resultatAppariement.getReseau2().getListeArcs().size() + " >= "
                + datasetNetwork2.getPopulation("Edge").size());

        EnsembleDeLiens liens = resultatAppariement.getLiens();

        // Split les multi des liens
        Population<DefaultFeature> collection = new Population<DefaultFeature>();
        fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
        newFeatureType.setTypeName("aaa");
        newFeatureType.setGeometryType(GM_Polygon.class);
        SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
        schemaDefaultFeature.setNom("aaa");
        schemaDefaultFeature.setNomSchema("aaa");
        schemaDefaultFeature.setFeatureType(newFeatureType);
        collection.setFeatureType(newFeatureType);

        for (Lien lien : liens) {
            IGeometry geom = lien.getGeom();
            if (geom instanceof GM_Aggregate<?>) {
                GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
                for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
                    if (lineGeom instanceof GM_LineString) {
                        multiCurve.add((GM_LineString) lineGeom);
                    } else if (lineGeom instanceof GM_MultiCurve<?>) {
                        multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
                    } else if (lineGeom instanceof GM_Polygon) {
                        DefaultFeature defaultFeature = new DefaultFeature();

                        defaultFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
                        defaultFeature.setSchema(schemaDefaultFeature);
                        defaultFeature.setGeom((GM_Polygon) lineGeom);
                        collection.add(defaultFeature);
                    }
                }
                lien.setGeom(multiCurve);
            }
        }

        this.application.getMainFrame().createNewDesktop("Appariement réseau.");

        Dimension desktopSize = this.application.getMainFrame().getSize();
        int widthProjectFrame = desktopSize.width / 2;
        int heightProjectFrame = desktopSize.height / 2;

        // ---------------------------------------------------------------------------------
        // Frame n°1
        //
        ProjectFrame p1 = this.application.getMainFrame().newProjectFrame();
        p1.setTitle("Réseau 1 et réseau 2");
        Layer l1 = p1.addUserLayer(datasetNetwork1.getPopulation("Edge"), "Réseau 1", null);
        l1.getSymbolizer().getStroke().setColor(network1Color);
        l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
        Layer l2 = p1.addUserLayer(datasetNetwork2.getPopulation("Edge"), "Réseau 2", null);
        l2.getSymbolizer().getStroke().setColor(network2Color);
        l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);
        if (paramPlugin.getDoRecalage()) {
            p1.setSize(widthProjectFrame, heightProjectFrame);
        } else {
            p1.setSize(widthProjectFrame, heightProjectFrame * 2);
        }
        p1.setLocation(0, 0);
        Viewport viewport = p1.getLayerViewPanel().getViewport();

        // ---------------------------------------------------------------------------------
        // Frame n°2 : recalage
        //
        if (paramPlugin.getDoRecalage()) {
            //
            CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseau1(),
                    resultatAppariement.getReseau2(), liens);
            IPopulation<Arc> arcs = reseauRecale.getPopArcs();

            ProjectFrame p2 = this.application.getMainFrame().newProjectFrame();
            p2.getLayerViewPanel().setViewport(viewport);
            viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
            p2.setTitle("Réseau 1 après recalage");

            List<String> valeursClassement = new ArrayList<String>();
            valeursClassement.add(I18N.getString("Appariement.Matched"));
            valeursClassement.add(I18N.getString("Appariement.Uncertain"));
            valeursClassement.add(I18N.getString("Appariement.Unmatched"));

            LOGGER.info("----");
            LOGGER.info("Nombre d'arcs de la carte topo n°1 = "
                    + resultatAppariement.getReseau1().getListeArcs().size());
            LOGGER.info("Nombre de noeuds de la carte topo n°1 = "
                    + resultatAppariement.getReseau2().getListeNoeuds().size());

            l1 = p2.addUserLayer(datasetNetwork1.getPopulation("Edge"), "Réseau 1", null);
            l1.getSymbolizer().getStroke().setColor(network1Color);
            l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);

            l2 = p2.addUserLayer(datasetNetwork2.getPopulation("Edge"), "Réseau 2", null);
            l2.getSymbolizer().getStroke().setColor(network2Color);
            l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);

            Layer l1bis = p2.addUserLayer(arcs, "Réseau 1 recale", null);
            l1bis.getSymbolizer().getStroke().setColor(matchedNetworkColor);
            l1bis.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);

            p2.setSize(widthProjectFrame, heightProjectFrame);
            p2.setLocation(0, heightProjectFrame);
        }

        // ---------------------------------------------------------------------------------
        // Frame n°3
        //
        ProjectFrame p3 = this.application.getMainFrame().newProjectFrame();
        p3.getLayerViewPanel().setViewport(viewport);
        viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
        p3.setTitle("Liens d'appariement");

        l1 = p3.addUserLayer(resultatAppariement.getReseau1().getPopArcs(), "CT 1 - Arcs", null);
        l1.getSymbolizer().getStroke().setColor(network1Color);
        l1.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);

        p3.addUserLayer(resultatAppariement.getReseau1().getPopNoeuds(), "CT 1 - Noeuds", null);
        p3.addUserLayer(resultatAppariement.getReseau2().getPopNoeuds(), "CT 2 - Noeuds", null);

        l2 = p3.addUserLayer(resultatAppariement.getReseau2().getPopArcs(), "CT 2 - Arcs", null);
        l2.getSymbolizer().getStroke().setColor(network2Color);
        l2.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH);

        Layer l4 = p3.addUserLayer(collection, "Liens noeuds", null);
        l4.getSymbolizer().getStroke().setColor(nodeColor);
        l4.getSymbolizer().getStroke().setStrokeOpacity(new Float(0.4));

        List<Double> valeursClassementL = new ArrayList<Double>();
        valeursClassementL.add(new Double(0.5));
        valeursClassementL.add(new Double(1));

        List<EnsembleDeLiens> liensClasses = liens.classeSelonSeuilEvaluation(valeursClassementL);
        EnsembleDeLiens liensNuls = liensClasses.get(0);
        EnsembleDeLiens liensIncertains = liensClasses.get(1);
        EnsembleDeLiens liensSurs = liensClasses.get(2);

        Layer link_ok_layer = p3.addUserLayer(liensSurs, "Liens sûrs", null);
        link_ok_layer.getSymbolizer().getStroke().setColor(linkColorOk);
        link_ok_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);

        Layer link_null_layer = p3.addUserLayer(liensNuls, "Liens nulls", null);
        link_null_layer.getSymbolizer().getStroke().setColor(linkColorNull);
        link_null_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);

        Layer link_doubtfull_layer = p3.addUserLayer(liensIncertains, "Liens incertains", null);
        link_doubtfull_layer.getSymbolizer().getStroke().setColor(linkColorDoubtfull);
        link_doubtfull_layer.getSymbolizer().getStroke().setStrokeWidth(LINE_WIDTH_LINK);

        p3.setSize(widthProjectFrame, heightProjectFrame * 2);
        p3.setLocation(widthProjectFrame, 0);

        //
        LOGGER.info("Finished");
        }
    }

}
