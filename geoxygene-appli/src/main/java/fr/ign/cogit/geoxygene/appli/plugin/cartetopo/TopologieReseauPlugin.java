package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.data.ParamDoingTopologicalStructure;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui.DialogTopoStructurePanel;
import fr.ign.cogit.geoxygene.appli.plugin.cartetopo.gui.TestPanel;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamFilenamePopulationEdgesNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultCarteTopoStatElement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultCarteTopoStatElementInterface;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * 
 * 
 *
 */
public class TopologieReseauPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {

  /** Classic logger. */
  private static Logger LOGGER = Logger.getLogger(TopologieReseauPlugin.class
      .getName());

  /** GeOxygeneApplication. */
  private GeOxygeneApplication application;

  private ParamDoingTopologicalStructure paramDoingTopologicalStructure;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {

    this.application = application;

    // Check if the DataMatching menu exists. If not we create it.
    JMenu menu = null;
    String menuName = I18N.getString("CarteTopoPlugin.CarteTopoPlugin"); //$NON-NLS-1$
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null
            && aMenu.getText().equalsIgnoreCase(menuName)) {
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu(menuName);
    }

    // Add network data matching menu item to the menu.
    JMenuItem menuItem = new JMenuItem(
        I18N.getString("CarteTopoPlugin.DoingTopologicalStructure")); //$NON-NLS-1$
    menuItem.addActionListener(this);
    menu.add(menuItem);

    // Refresh menu of the application
    application
        .getMainFrame()
        .getMenuBar()
        .add(menu,
            application.getMainFrame().getMenuBar().getComponentCount() - 2);

  }

  /**
     * 
     */
  @Override
  public void actionPerformed(final ActionEvent e) {

    // String filename =
    // "D:\\Data\\Appariement\\MesTests\\T3\\bdcarto_route.shp";
    // String filename =
    // "D:\\Data\\Appariement\\Haiti\\tout\\osm_extraitglobal_p.shp";
    // String filename =
    // "D:\\Data\\Appariement\\Haiti\\tout\\sertit_extraitglobal.shp";
    // String filename =
    // "D:\\Data\\Appariement\\Haiti\\routes_extrait2\\osm_extrait2_p.shp";
    // String filename =
    // "D:\\Data\\Appariement\\MesTests\\TestUnitaire\\DeuxRebroussementsConsecutifs.shp";
    String filename = "D:\\Data\\Appariement\\MesTests\\TestUnitaire\\Rebroussement.shp";

    ParamFilenamePopulationEdgesNetwork paramFilename = new ParamFilenamePopulationEdgesNetwork();
    paramFilename.addFilename(filename);

    // Launch parameter network data matching panel.
    DialogTopoStructurePanel dialogTopoStructurePanel = new DialogTopoStructurePanel(
        paramFilename, this);

    // Instanciation de la topologie
    if (dialogTopoStructurePanel.getAction().equals("LAUNCH")) {

      // ------------------------------------------------------------------------------------------
      // Show the result
      Dimension desktopSize = this.application.getMainFrame().getSize();
      int widthProjectFrame = desktopSize.width / 2;
      int heightProjectFrame = desktopSize.height;

      LOGGER.debug("Tolerance = " + paramDoingTopologicalStructure.tolerance);

      ResultCarteTopoStatElement resultStatArc = new ResultCarteTopoStatElement();
      ResultCarteTopoStatElement resultStatNoeud = new ResultCarteTopoStatElement();

      // Réseau final muni d'une topologie réseau
      ReseauApp reseau = new ReseauApp("Réseau muni d'une topologie réseau");
      IPopulation<? extends IFeature> popArc = reseau.getPopArcs();

      // ------------------------------------------------------------------------------------------
      // On ajoute récupère les réseaux à transformer
      List<String> fileList = paramDoingTopologicalStructure.paramDataset
          .getListNomFichiersPopArcs();
      List<IFeatureCollection<? extends IFeature>> populationsArcs = new ArrayList<IFeatureCollection<? extends IFeature>>();
      for (int i = 0; i < fileList.size(); i++) {
        String fileNetwork = fileList.get(i);
        populationsArcs.add(ShapefileReader.read(fileNetwork));
      }

      // 0. Import des populations d'arcs
      LOGGER.info("Import des populations d'arcs.");
      Iterator<IFeatureCollection<? extends IFeature>> itPopArcs = populationsArcs
          .iterator();
      while (itPopArcs.hasNext()) {
        IFeatureCollection<? extends IFeature> popGeo = itPopArcs.next();

        /*
         * CarteTopo networkMap = new CarteTopo("Network Map"); String
         * orientationAttribute = ""; Map<Object, Integer> orientationMap =
         * null; String filterAttribute = ""; Map<Object, Boolean> filterMap =
         * null; String groundPositionAttribute = ""; double tolerance;
         * Chargeur.importAsEdges(popGeo, networkMap, orientationAttribute,
         * orientationMap, filterAttribute, filterMap, groundPositionAttribute,
         * tolerance);
         * 
         * popArc.add(networkMap.getListeArcs());
         */

        for (IFeature element : popGeo) {
          ArcApp arc = (ArcApp) popArc.nouvelElement();
          ILineString ligne = new GM_LineString((IDirectPositionList) element
              .getGeom().coord().clone());
          arc.setGeometrie(ligne);
          arc.setOrientation(2);
          arc.addCorrespondant(element);
        }

      }

      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs bruts des réseaux = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_BRUTS, reseau.getPopArcs()
              .size());
      LOGGER.info("Nombre de noeuds bruts des réseaux = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_BRUTS, reseau.getPopNoeuds()
              .size());
      LOGGER.info("==================================");

      // Instanciation de la topologie
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Topology instanciation");
      }
      reseau.creeNoeudsManquants(paramDoingTopologicalStructure.tolerance);
      reseau.filtreDoublons(paramDoingTopologicalStructure.tolerance);
      reseau.creeTopologieArcsNoeuds(paramDoingTopologicalStructure.tolerance);
      reseau.filtreArcsDoublons();

      // => on enregistre pour l'affichage
      IPopulation<Arc> arcsBruts = reseau.getPopArcs();
      IPopulation<Noeud> noeudsBruts = reseau.getPopNoeuds();
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après instanciation de la topologie = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_IMPORT, reseau.getPopArcs()
              .size());
      LOGGER.info("Nombre de noeuds après instanciation de la topologie = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_IMPORT, reseau.getPopNoeuds()
              .size());
      LOGGER.info("==================================");

      // ---------------------------------------------------------------------------------
      // Frame n°1
      //
      ProjectFrame p1 = this.application.getMainFrame().newProjectFrame();
      p1.setTitle("Réseaux initiaux");
      Layer l1 = p1.addUserLayer(arcsBruts, "Arcs bruts", null);
      l1.getSymbolizer().getStroke().setColor(new Color(67, 144, 193));
      l1.getSymbolizer().getStroke().setStrokeWidth(6);
      Layer l2 = p1.addUserLayer(noeudsBruts, "Noeuds bruts", null);

      p1.setSize(widthProjectFrame, heightProjectFrame / 3 * 2);
      p1.setLocation(0, 0);
      Viewport viewport = p1.getLayerViewPanel().getViewport();

      // ------------------------------------------------------------------------------------------
      // 1- création de la topologie arcs-noeuds, rendu du graphe planaire
      if (paramDoingTopologicalStructure.doRenduPlanaire) {
        // cas où on veut une topologie planaire
        if (LOGGER.isEnabledFor(Level.INFO)) {
          LOGGER
              .info("Making the graph planar and instantiation of node-edge topology");
        }
        reseau.rendPlanaire(paramDoingTopologicalStructure.tolerance);
        reseau.filtreDoublons(paramDoingTopologicalStructure.tolerance);
      }
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après planaire = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_PLANAIRE, reseau.getPopArcs()
              .size());
      LOGGER.info("Nombre de noeuds après planaire = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_PLANAIRE, reseau
              .getPopNoeuds().size());
      LOGGER.info("==================================");

      // ------------------------------------------------------------------------------------------
      // 2- On fusionne les noeuds proches
      if (paramDoingTopologicalStructure.doFusionNoeudProche) {
        if (LOGGER.isEnabledFor(Level.INFO)) {
          LOGGER.info("Nodes Fusion");
        }
        reseau.fusionNoeuds(paramDoingTopologicalStructure.seuilFusion);
      }
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après fusion des noeuds proches = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_NOEUDS_PROCHES, reseau
              .getPopArcs().size());
      LOGGER.info("Nombre de noeuds après fusion des noeuds proches = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_NOEUDS_PROCHES, reseau
              .getPopNoeuds().size());
      LOGGER.info("==================================");

      // ------------------------------------------------------------------------------------------
      // 3- On enlève les noeuds isolés
      if (paramDoingTopologicalStructure.doSuppNoeudIsole) {
        if (LOGGER.isEnabledFor(Level.INFO)) {
          LOGGER.info("Isolated nodes filtering");
        }
        reseau.filtreNoeudsIsoles();
      }
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après fusion des noeuds proches = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_NOEUDS_ISOLES, reseau
              .getPopArcs().size());
      LOGGER.info("Nombre de noeuds après fusion des noeuds proches = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_NOEUDS_ISOLES, reseau
              .getPopNoeuds().size());
      LOGGER.info("==================================");

      // ------------------------------------------------------------------------------------------
      // 4- On filtre les noeuds simples (avec 2 arcs incidents)
      if (paramDoingTopologicalStructure.doFiltreNoeudSimple) {
        if (LOGGER.isEnabledFor(Level.INFO)) {
          LOGGER.info("    Filtering of nodes with only 2 incoming edges");
        }
        reseau.filtreNoeudsSimples();
      }
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après fusion des noeuds proches = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_FILTRE_ARCS_INCIDENTS, reseau
              .getPopArcs().size());
      LOGGER.info("Nombre de noeuds après fusion des noeuds proches = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_FILTRE_ARCS_INCIDENTS, reseau
              .getPopNoeuds().size());
      LOGGER.info("==================================");

      // ---------------------------------------------------------------------------------
      // 5- On fusionne des arcs en double
      if (paramDoingTopologicalStructure.doFusionArcDouble) {
        if (LOGGER.isEnabledFor(Level.INFO)) {
          LOGGER.info("Double edges filtering");
        }
        reseau.filtreArcsDoublons();
      }
      LOGGER.info("==================================");
      LOGGER.info("Nombre d'arcs après fusion des noeuds proches = "
          + reseau.getPopArcs().size());
      resultStatArc.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_INDEXATION, reseau
              .getPopArcs().size());
      LOGGER.info("Nombre de noeuds après fusion des noeuds proches = "
          + reseau.getPopNoeuds().size());
      resultStatNoeud.addNbElementForType(
          ResultCarteTopoStatElementInterface.NB_INDEXATION, reseau
              .getPopNoeuds().size());
      LOGGER.info("==================================");

      // 6 - On crée la topologie de faces
      /*
       * if (!ref && paramApp.varianteChercheRondsPoints) { if
       * (LOGGER.isDebugEnabled()) { LOGGER.debug("    Face topology creation");
       * } reseau.creeTopologieFaces(); }
       */

      // ---------------------------------------------------------------------------------
      // Frame n°2
      //
      ProjectFrame p2 = this.application.getMainFrame().newProjectFrame();
      p2.getLayerViewPanel().setViewport(viewport);
      viewport.getLayerViewPanels().add(p2.getLayerViewPanel());
      p2.setTitle("Carte topologie des réseaux");
      // Viewport viewport = p2.getLayerViewPanel().getViewport();

      Layer l3 = p2.addUserLayer(reseau.getPopArcs(), "Arcs", null);
      l3.getSymbolizer().getStroke().setColor(new Color(67, 144, 193));
      l3.getSymbolizer().getStroke().setStrokeWidth(3);
      Layer l4 = p2.addUserLayer(reseau.getPopNoeuds(), "Noeuds", null);

      p2.setSize(widthProjectFrame, heightProjectFrame);
      p2.setLocation(widthProjectFrame, 0);

      TestPanel testPanel = new TestPanel(p2, resultStatArc, resultStatNoeud);

      // On enregistre les résultats
      // ShapefileWriter.write(reseau.getPopArcs(),
      // "D:\\Data\\Appariement\\Haiti\\nettoyage\\ArcsToutSertit.shp");

    }

    LOGGER.debug("The end.");
  }

  public void setParamDoingTopologicalStructure(ParamDoingTopologicalStructure p) {
    paramDoingTopologicalStructure = p;
  }

}
