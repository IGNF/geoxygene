package fr.ign.cogit.geoxygene.example.appariement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

// import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Mon premier test sur un appariement d'un reseau topo et d'un reseau carto d'un canton du 68
 * @author MDVan-Damme
 *
 */
public class TestAppariement extends GeOxygeneApplication {
    
    /** Log. */
    private static final Logger logger = Logger.getLogger(TestAppariement.class);
    
    /**  */
    // private ProjectFrame frame;
    
    public TestAppariement() {
        
        // Nom de l'interface
        getFrame().setTitle("Tests sur l'appariement de réseaux");

        // Menu
        JMenu menuTest = new JMenu("Tests topo-carto");
        
        // On charge le reseau de référence
        JMenuItem menuLoadReseauReference = new JMenuItem("Chargement du reseau de référence");
        
        menuLoadReseauReference.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             
             logger.info("On charge le réseau de référence (topo)");
             
             ProjectFrame p1 = getFrame().newProjectFrame();
             p1.setTitle("Réseau de référence"); 
             charger(fr.ign.cogit.geoxygene.example.appariement.data.BDTopoRoutier.class, "Topo route", GM_LineString.class, p1);
             Viewport viewport = p1.getLayerViewPanel().getViewport();
             
             ProjectFrame p2 = getFrame().newProjectFrame();
             p2.setTitle("Réseau de comparaison"); 
             charger(fr.ign.cogit.geoxygene.example.appariement.data.BDCartoRoutier.class, "Carto route", GM_LineString.class, p2);
             viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
             
             // Paramètres par défaut
             /* ParametresApp param = new ParametresApp();
             
             param.populationsArcs1.add(popRef);
             param.populationsArcs2.add(popComp);
             param.topologieFusionArcsDoubles1 = true;
             param.topologieFusionArcsDoubles2 = true;
             param.topologieGraphePlanaire1 = true;
             param.topologieGraphePlanaire2 = true;
             param.topologieSeuilFusionNoeuds2 = 1;
             param.varianteFiltrageImpassesParasites = false;
             param.projeteNoeuds1SurReseau2 = true;
             param.projeteNoeuds1SurReseau2DistanceNoeudArc = 8; // 25
             param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 20; // 50
             param.projeteNoeuds2SurReseau1 = true;
             param.projeteNoeuds2SurReseau1DistanceNoeudArc = 8; // 25
             param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 20; // 50
             param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
             param.varianteForceAppariementSimple = true;
             param.distanceArcsMax = 20; // 50
             param.distanceArcsMin = 8; // 30
             param.distanceNoeudsMax = 20; // 50
             param.varianteRedecoupageArcsNonApparies = true;
             param.debugTirets = false;
             param.debugBilanSurObjetsGeo = false;
             param.varianteRedecoupageArcsNonApparies = true;
             param.debugAffichageCommentaires = 2;
             
             // Appariement
             List<ReseauApp> reseaux = new ArrayList<ReseauApp>();
             EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, reseaux); */
           
         }
         
        });
        menuTest.add(menuLoadReseauReference);
        
        // 
        JMenuBar barreMenu = TestAppariement.this.getFrame().getJMenuBar();
        barreMenu.add(menuTest, barreMenu.getComponentCount() - 1);

        // Adding a new Project Frame
        ProjectFrame projectFrame = new ProjectFrame(getFrame(), getIcon());
        projectFrame.setSize(this.getFrame().getDesktopPane().getSize());
        projectFrame.setVisible(true);
        this.getFrame().getDesktopPane().add(projectFrame, JLayeredPane.DEFAULT_LAYER);
        this.getFrame().getDesktopPane().setSelectedFrame(projectFrame);
      
        // Initialisation attribut frame
        // this.frame = (ProjectFrame)TestAppariement.this.getFrame().getDesktopPane().getSelectedFrame();
    }
    
    /**
     * Generic Method to load data from a table of a PostGres DB.
     * @param <T> FeatureType, the Java Class representing the PostGres Data
     * @param javaClass Java Class representing the postGres Data Table
     * @param popName Name of the futur layer on the viewer
     * @param geometryClass GeometryType ({@link GM_Point},{@link GM_LineString},{@link GM_Polygon}...)
     */
    public static <T extends FT_Feature> void charger(
            Class<T> javaClass,
            String popName,
            Class<? extends GM_Object> geometryClass,
            ProjectFrame cartoframe) {
        
        Geodatabase geoDB = GeodatabaseOjbFactory.newInstance();
        
        // Loading Data from PostGres DataBase
        IFeatureCollection<T> collec
                = geoDB.loadAllFeatures(javaClass);
        FeatureType ft = new FeatureType();
        ft.setGeometryType(geometryClass);
        Population<T> pop = new Population<T>(popName);
        pop.addCollection(collec);
        pop.setFeatureType(ft);
        cartoframe.getDataSet().addPopulation(pop);
        cartoframe.addFeatureCollection(pop, pop.getNom(), null);

    }

    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
      try {
          TestAppariement testApplication = new TestAppariement();
          testApplication.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      } catch (Exception e) {
      }
    }
}
