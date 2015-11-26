package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.geoxygene.GeoxConverter;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.RtreeMultiLevelIndex;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.PostgresExtractor;
import fr.ign.cogit.mapping.util.ScaleInfoUtil;

/*
 * @author Dr Dieudonné Tsatcha
 */

public class TilingPlugin extends Thread implements GeOxygeneApplicationPlugin, ActionListener, Runnable {

    /** Logger. */
    private final static Logger LOGGER = Logger.getLogger(TilingPlugin.class
            .getName());

    /** GeOxygeneApplication */
    private GeOxygeneApplication application = null;

    public void actionPerformed(ActionEvent e) {
        // String tableName = "ref_patte_d_oie";

        ProjectFrame projectFrame = application.getMainFrame()
                .newProjectFrame();
        PostgresExtractor myextractor = new PostgresExtractor();
        // Set<Layer> selectedLayers =
        // projectFrame.getLayerLegendPanel().getSelectedLayers();
        // if (selectedLayers.size() != 1) {
        // javax.swing.JOptionPane.showMessageDialog(null,
        // "You need to select one (and only one) layer.");
        // TilingPlugin.LOGGER.error("You need to select one (and only one) layer.");
        // return;
        // }
        // Layer layer = selectedLayers.iterator().next();

        // ProjectFrame project =
        // this.application.getMainFrame().getSelectedProjectFrame();
        // Set<fr.ign.cogit.geoxygene.style.Layer> selectedLayers =
        // project.getLayerLegendPanel().getSelectedLayers();

        // extraire toutes les tables de la base de données
        List<String> autList = new ArrayList<String>();
        Map<Integer, Map<String, String>> test = myextractor.readAllTables();
        //
        for (Integer table : test.keySet()) {
            //
            Map<String, String> entry = test.get(table);

            autList.add(entry.get("table"));
        }
        //
        // Map<Integer, String>
        // mcontent=myextractor.readTableGeometry(entry.get("table"),entry.get("geometry_colum"));
        //
        // for(Integer key : mcontent.keySet()) {
        // System.out.println(mcontent.get(key));
        // }
        // }

        autList = new ArrayList<String>();
        autList.add("comp_courbe_de_niveau");
        autList.add("ref_estran");
        autList.add("batiment_fonctionnel_25");
        autList.add("route_numerotee_ou_nommee");
        autList.add("troncon_route_25");
        autList.add("troncon_route_nommee_50");
       autList.add("ref_troncon_de_cours_d_eau");
        autList.add("troncon_cours_d_eau_25");
        autList.add("batiment_religieux_ponct_25");
        autList.add("route_numerotee_ou_nommee");
        autList.add("terrain_de_sport_25");
        autList.add("surface_de_route");
        autList.add("ref_zone_marine");
        autList.add("troncon_route_nommee_50");
        autList.add("chef_lieu");
        autList.add("cimetiere_25");
        autList.add("comp_equipement_administratif_ou_militaire");
        // manager.deleteAllLevel();
        
        int width= projectFrame.getLayerViewPanel().getWidth(); 
        int hight= projectFrame.getLayerViewPanel().getHeight(); 
        Converter geox = new GeoxConverter(width,hight);
        // Controller control = new Controller(manager, myextractor);
        // manager.deleteAllLevel();
        // for( Integer table : test.keySet()){
        // Map<String, String> entry = test.get(table);
        //
        /****
         * Affichage des contenus de la table
         */
        // for (String table : autList) {
        // // obtain se charge de fabriquer une population perisisante...
        // ScaleInfo scale = ScaleInfoUtil.generateScale(table);
        // geox.setTable(table);
        // geox.setSignature("geometrie");
        // // geox=new GeoxConverter(table, "geometrie");
        // // Population<DefaultFeature> pop = control.obtainTable(table,
        // // "geometrie", scale);
        // // ecrire une methode pour charger les differentes tables
        // // sans necessaire les envoyer au client...
        // // Population<DefaultFeature> pop = ((GeoxConverter) geox)
        // // .tableToPopulation();
        //
        // // if (pop != null) {
        // // projectFrame.getDataSet().addPopulation(pop);
        // // projectFrame.addFeatureCollection(pop, pop.getNom(), null);
        // // // System.out.println("population size" + pop.size());
        // // } else {
        // // TilingPlugin.LOGGER.error("Aucune population" + table
        // // + " n'a été chargé");
        // // }
        //
        // // }
        // }
        //
        /*
         * Decoupage en sous forme de tuilage ...;
         */
        // chargement des données
//        for (String table : autList) {
//            // // obtain se charge de fabriquer une population perisisante...
//            ScaleInfo scale = ScaleInfoUtil.generateScale(table);
//            geox.setTable(table);
//            geox.setSignature("geometrie");
//            // fabrique l'indexation rtree associée à cette table..
//            if (!((GeoxConverter) geox).BuildtableIndex()) {
//                TilingPlugin.LOGGER.error("Aucune population" + geox.getTable()
//                        + " n'a été chargé");
//            } else {
//                TilingPlugin.LOGGER.info("La population" + geox.getTable()
//                        + "a été chargée");
//            }
//
//        }
        //
        // affichage des contenus...
        
//        for (String table : autList) {
////          // // obtain se charge de fabriquer une population perisisante...
//         ScaleInfo scale = ScaleInfoUtil.generateScale(table);

        
    
         ScaleInfo scale = new ScaleInfo(15,25);
         
      

        List<Population<DefaultFeature>> containt = ((GeoxConverter) geox)
                .vueContaint(scale);

        //
        if (containt != null && containt.size() > 0) {

            for (Population pop : containt) {

                if (pop != null && pop.size() > 0) {
                    System.out.println("population size" + pop.size());
                    projectFrame.getDataSet().addPopulation(pop);
                    projectFrame.addFeatureCollection(pop, pop.getNom(), null);

                } else {
                    TilingPlugin.LOGGER
                            .error("Aucune population correspondant "
                                    + "à l'echelle " + scale.toString()
                                    + " n'a été chargé");
                }
            }
        } else {
            TilingPlugin.LOGGER.error("le cadre " + containt.size()
                    + "à l'echelle " + scale.toString() + " n'a été chargé");
        }
    
//        //
//        try {
//            Thread.sleep(100000);
//            ((GeoxConverter) geox).applyUpdateDaemon(true);
//        } catch (InterruptedException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//         
    }

    public void initialize(GeOxygeneApplication application) {
        // TODO Auto-generated method stub
        this.application = application;

        JMenu menuExample = null;
        String menuName = "Mapping";
        for (Component c : application.getMainFrame().getMenuBar()
                .getComponents()) {
            if (c instanceof JMenu) {
                JMenu aMenu = (JMenu) c;
                if (aMenu.getText() != null
                        && aMenu.getText().equalsIgnoreCase(menuName)) {
                    menuExample = aMenu;
                }
            }
        }
        if (menuExample == null) {
            menuExample = new JMenu(menuName);
        }

        JMenuItem menuItem = new JMenuItem("Charger");
        menuItem.addActionListener(this);
        menuExample.add(menuItem);

        int menuComponentCount = application.getMainFrame().getMenuBar()
                .getComponentCount();
        application.getMainFrame().getMenuBar()
                .add(menuExample, menuComponentCount - 1);
    }

    // ...

}