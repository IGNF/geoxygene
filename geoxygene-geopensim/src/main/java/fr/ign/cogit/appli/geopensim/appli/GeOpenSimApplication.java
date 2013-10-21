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

package fr.ign.cogit.appli.geopensim.appli;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographique;
import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.agent.macro.EtatGlobal;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentGroupeBatiments;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentUniteBatie;
import fr.ign.cogit.appli.geopensim.agent.meso.AgentZoneElementaireBatie;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentBatiment;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconChemin;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconCoursEau;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconRoute;
import fr.ign.cogit.appli.geopensim.agent.micro.AgentTronconVoieFerree;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ChangeValeurObjectif;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ChoixEtatComparaison;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ChooseDateDialog;
import fr.ign.cogit.appli.geopensim.appli.comparaison.ListeParamComparaisonFrame;
import fr.ign.cogit.appli.geopensim.appli.peuplement.ChoixParamSimul;
import fr.ign.cogit.appli.geopensim.appli.peuplement.LienPeuplementTypeFonctionnel;
import fr.ign.cogit.appli.geopensim.appli.peuplement.ListeMethodesPeuplementFrame;
import fr.ign.cogit.appli.geopensim.appli.rules.EditRulesFrame;
import fr.ign.cogit.appli.geopensim.appli.rules.EditRulesFramev2;
import fr.ign.cogit.appli.geopensim.appli.tracking.FrameInfoAgent;
import fr.ign.cogit.appli.geopensim.appli.tracking.SimulationTreeFrame;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleBase;
import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconVoieFerree;
import fr.ign.cogit.appli.geopensim.scheduler.Scheduler;
import fr.ign.cogit.appli.geopensim.scheduler.SchedulerEvent;
import fr.ign.cogit.appli.geopensim.scheduler.SchedulerEventListener;
import fr.ign.cogit.appli.geopensim.util.MetadataLoader;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Base class for GeOxygene applications.
 * 
 * @author Julien Perret
 * 
 */
public class GeOpenSimApplication extends GeOxygeneApplication implements
    SchedulerEventListener, ActionListener {
  static Logger logger = Logger.getLogger(GeOpenSimApplication.class.getName());

  private final AgentGeographiqueCollection collection = AgentGeographiqueCollection
      .getInstance();
  private List<Population<? extends AgentGeographique>> populations = null;
  private List<EtatGlobal> listeEtats = new ArrayList<EtatGlobal>();

  private final ImageIcon start = new ImageIcon(
      MainFrameToolBar.class.getResource("/images/icons/16x16/startSimulation.png"));
  private final ImageIcon stop = new ImageIcon(
      MainFrameToolBar.class.getResource("/images/icons/16x16/stopSimulation.png"));
  private final JButton simulationButton = new JButton(start);
  private final JButton editRulesButton = new JButton("Editer règles");
  private final JButton applyRulesButton = new JButton("Appliquer règles");
  private final JButton chooseDate = new JButton("Changer la date");
  private final JButton simulationTreeButton = new JButton("arbre des états");
  private final JButton changerValObj = new JButton("changer valeurs objectifs");
  private JComboBox comboEtatInitial;
  private JLabel dateLabel;
  private final JMenu comparaison, simulation, paramSimul;
  private final JMenuItem paramCompar, lancementCompar, methodesPeuplement,
      lienTypeObjPeuplement, lancementSimul, reglesEvolution;
  private final String textDate = "Date simulée : ";
  private List<AgentGeographique> coll = new ArrayList<AgentGeographique>();
  SimulationTreeFrame simulFrame = null;
  JFrame methodesPeuplementFrame = null;
  JFrame reglesEvolutionFrame = null;
  LienPeuplementTypeFonctionnel lien = null;
  private EtatGlobal etatCourant = null;
  String[] listeEtatsString = new String[listeEtats.size()];
  List<EvolutionRule> listeReglesSelect = new ArrayList<EvolutionRule>();

  /**
   * Constructor.
   */
  public GeOpenSimApplication(String title, ImageIcon theApplicationIcon) {
    super(title, theApplicationIcon);
    simulationButton.addActionListener(this);
    this.getMainFrame().getMode().getToolBar().add(simulationButton);
    editRulesButton.addActionListener(this);
    this.getMainFrame().getMode().getToolBar().add(editRulesButton);
    applyRulesButton.addActionListener(this);
    this.getMainFrame().getMode().getToolBar().add(applyRulesButton);
    simulationTreeButton.addActionListener(this);
    this.getMainFrame().getMode().getToolBar().add(simulationTreeButton);
    changerValObj.addActionListener(this);
    this.getMainFrame().getMode().getToolBar().add(changerValObj);

    JButton treeButton = new JButton(new ImageIcon(
        MainFrameToolBar.class.getResource("/images/icons/16x16/tree.png")));
    treeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Set<IFeature> selectedFeatures = getMainFrame()
            .getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures();
        List<AgentGeographique> agents = new ArrayList<AgentGeographique>();
        for (IFeature feature : selectedFeatures) {
          if (ElementRepresentation.class.isAssignableFrom(feature.getClass())) {
            ElementRepresentation representation = (ElementRepresentation) feature;
            agents.add(representation.getAgentGeographique());
          } else if (AgentGeographique.class.isAssignableFrom(feature
              .getClass())) {
            agents.add((AgentGeographique) feature);
          }
        }
        for (AgentGeographique agent : agents) {
          FrameInfoAgent frameInfo = new FrameInfoAgent(getMainFrame(), agent);
          frameInfo.setVisible(true);
        }
      }
    });
    this.getMainFrame().getMode().getToolBar().add(treeButton);

    // Menu de la barre d'outils
    comparaison = new JMenu("Comparaison");
    paramCompar = new JMenuItem("Paramétres comparaison");
    lancementCompar = new JMenuItem("Lancer comparaison");
    comparaison.add(paramCompar);
    paramCompar.addActionListener(this);
    comparaison.add(lancementCompar);
    lancementCompar.addActionListener(this);
    simulation = new JMenu("Simulation");
    paramSimul = new JMenu("Paramètres simulation");
    simulation.add(paramSimul);
    methodesPeuplement = new JMenuItem("Ajout types peuplement");
    methodesPeuplement.addActionListener(this);
    paramSimul.add(methodesPeuplement);
    lienTypeObjPeuplement = new JMenuItem("Lien types Objectif et peuplement");
    lienTypeObjPeuplement.addActionListener(this);
    paramSimul.add(lienTypeObjPeuplement);
    reglesEvolution = new JMenuItem("Ajout regles Evolution");
    reglesEvolution.addActionListener(this);
    paramSimul.add(reglesEvolution);
    lancementSimul = new JMenuItem("Lancer simulation");
    lancementSimul.addActionListener(this);
    simulation.add(lancementSimul);
    this.getMainFrame()
        .getMenuBar()
        .add(Box.createHorizontalStrut(30),
            this.getMainFrame().getMenuBar().getComponentCount() - 1);
    this.getMainFrame()
        .getMenuBar()
        .add(simulation,
            this.getMainFrame().getMenuBar().getComponentCount() - 1);
    this.getMainFrame()
        .getMenuBar()
        .add(comparaison,
            this.getMainFrame().getMenuBar().getComponentCount() - 1);

    // Initilisation des règles d'évolution
    EvolutionRuleBase configuration = new EvolutionRuleBase();
    configuration = EvolutionRuleBase.getInstance();
    List<EvolutionRule> listeRegles = configuration.getRules();
    List<EvolutionRule> listeReglesD = new ArrayList<EvolutionRule>();
    List<EvolutionRule> listeReglesT = new ArrayList<EvolutionRule>();
    for (EvolutionRule regle : listeRegles) {
      if (regle.getPropertyName().equals("densiteBut"))
        listeReglesD.add(regle);
      else if (regle.getPropertyName().equals("classificationFonctionnelleBut"))
        listeReglesT.add(regle);
    }
    listeReglesSelect.add(listeReglesD.get(0));
    listeReglesSelect.add(listeReglesT.get(0));

    this.initializeDatePanels();

    Scheduler.getInstance().addListener(this);

    ProjectFrame project = this.getMainFrame().newProjectFrame();
    synchronized (project.getSld()) {
      project.getSld().setDataSet(DataSet.getInstance());
    }
    String db = MetadataLoader.getConnection();
    this.openDataBase(db);
    this.initializeLayers();
  }

  /**
   * 
   */
  private void initializeDatePanels() {
    JPanel datePanel = new JPanel();
    datePanel.setLayout(new BorderLayout(10, 10));
    datePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    comboEtatInitial = new JComboBox();
    comboEtatInitial.setMaximumSize(comboEtatInitial.getPreferredSize());
    comboEtatInitial.addActionListener(this);
    Box hBoxEtatInitial = Box.createHorizontalBox();
    hBoxEtatInitial.add(Box.createHorizontalStrut(10));
    hBoxEtatInitial.add(new JLabel("Etat initial : "));
    hBoxEtatInitial.add(comboEtatInitial);
    hBoxEtatInitial.add(Box.createHorizontalStrut(20));

    // La date simulée
    Box hBoxDateSimulee = Box.createHorizontalBox();
    dateLabel = new JLabel(textDate + "????");
    hBoxDateSimulee.add(dateLabel);
    hBoxDateSimulee.add(Box.createHorizontalStrut(10));
    chooseDate.addActionListener(this);
    hBoxDateSimulee.add(chooseDate);
    hBoxDateSimulee.add(Box.createHorizontalGlue());

    Box hBoxdate = Box.createHorizontalBox();
    hBoxdate.add(hBoxEtatInitial);
    hBoxdate.add(hBoxDateSimulee);
    datePanel.add(hBoxdate);

    this.getMainFrame().add(datePanel, BorderLayout.PAGE_END);
  }

  /**
   * 
   */
  private void initializeLayers() {
    ProjectFrame frame = this.getMainFrame().getSelectedProjectFrame();

    logger.info("Adding the layers");
    // initialise les layers
    StyledLayerDescriptor sld = null;
    try {
      sld = StyledLayerDescriptor
          .unmarshall("./src/resources/sld/geopensimSLD.xml");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    Layer layerZoneUrbaine = sld.getLayer("ZoneElementaireBatie");
    frame.addLayer(layerZoneUrbaine);
    Layer layerGroupeBatiments = sld.getLayer("GroupeBatiments");
    frame.addLayer(layerGroupeBatiments);
    // Layer layerAlignement = sld.getLayer("Alignement");
    // frame.addLayer(layerAlignement);
    Layer layerTronconCoursEau = sld.getLayer("TronconCoursEau");
    frame.addLayer(layerTronconCoursEau);
    Layer layerTronconVoieFerree = sld.getLayer("TronconVoieFerree");
    frame.addLayer(layerTronconVoieFerree);
    Layer layerTronconRoute = sld.getLayer("TronconRoute");
    frame.addLayer(layerTronconRoute);
    Layer layerTronconChemin = sld.getLayer("TronconChemin");
    frame.addLayer(layerTronconChemin);
    Layer layerBatiment = sld.getLayer("Batiment");
    frame.addLayer(layerBatiment);
    Layer layerTriangulation = sld.getLayer("Triangulation");
    frame.addLayer(layerTriangulation);
    Layer layerMedialAxis = sld.getLayer("MedialAxis");
    frame.addLayer(layerMedialAxis);
    // try {
    // frame.getLayerViewPanel().getViewport().zoomToFullExtent();
    // } catch (NoninvertibleTransformException e) {
    // e.printStackTrace();
    // }
    logger.info("Initialization finished");
  }

  /**
   * @param jcdAlias name of the database to open
   */
  private void openDataBase(String jcdAlias) {
    // Chargement des données GeOpenSim Stockées dans la BDD OJB
    logger.info("Opening database " + jcdAlias);
    DataSet.db = GeodatabaseOjbFactory.newInstance(jcdAlias);
    // Récupération des agents
    logger.info("Loading agents");
    long time = System.currentTimeMillis();
    collection.chargerPopulations();
    // Récupération des dates auxquelle on possède des données
    List<Integer> dates = new ArrayList<Integer>(collection.getDates());
    logger.info("Loading agents took " + (System.currentTimeMillis() - time)
        + " ms");

    // Pour chacune des dates initiales création d'un état global
    listeEtats = new ArrayList<EtatGlobal>(dates.size());
    for (int date : collection.getDates()) {
      logger.debug("Création des population pour la date : " + date);
      EtatGlobal nouvelEtat = new EtatGlobal();
      nouvelEtat.setDate(date);
      nouvelEtat.setSimule(false);
      creationPopulationsv2(date);
      nouvelEtat.setCollection(coll);
      String debutNom = "";
      try {
        debutNom = DataSet.db.getConnection().getCatalog();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      String finNom = (nouvelEtat.isSimule()) ? "simulé" : "initial";
      nouvelEtat.setNom(debutNom + "_" + date + "_" + finNom);
      listeEtats.add(nouvelEtat);
    }

    // Choix a priori des dates de début de simulation et de fin de simulation
    int dateDebutSimulation = dates.get(dates.size() - 1);
    collection.setDateDebutSimulation(dateDebutSimulation);
    collection.calculDureePasSimulation();
    int dateSimulee = dateDebutSimulation + collection.getDureePasSimulation();
    logger.info("La date simulée est " + dateSimulee);
    collection.setDateSimulee(dateSimulee);

    // On charge la population correspondant à l'état initial le plus récent
    etatCourant = listeEtats.get(0);
    for (EtatGlobal etatG : listeEtats) {
      if ((etatG.getDate() == dateDebutSimulation) && (!etatG.isSimule())) {
        etatCourant = etatG;
      }
    }
    chargePopulations(etatCourant);
    int date = dateDebutSimulation;
    for (Population<? extends AgentGeographique> pop : populations) {
      logger.info("Population " + pop.getNom());
      for (AgentGeographique agent : pop) {
        ElementRepresentation rep = agent.getRepresentation(date);
        if (rep == null) {
          agent.setGeom(null);
          agent.setSupprime(true);
        } else {
          agent.prendreAttributsRepresentation(rep);
        }
      }
    }
    logger.info("Géométries affectées");
    // L'état initial
    int index = 0;
    comboEtatInitial.removeAllItems();
    listeEtatsString = new String[listeEtats.size()];
    for (int i = 0; i < listeEtats.size(); i++) {
      listeEtatsString[i] = listeEtats.get(i).getNom();
      if (listeEtats.get(i) == etatCourant) {
        index = i;
      }
      comboEtatInitial.addItem(listeEtatsString[i]);
    }
    comboEtatInitial.setSelectedIndex(index);

    dateLabel.setText(textDate + collection.getDateSimulee());

    try {
      this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
          .getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

    for (IPopulation<?> pop : DataSet.getInstance().getPopulations()) {
      logger.info("Population in DataSet " + pop.getNom());
    }
  }

  protected void chargePopulations(EtatGlobal etat) {
    logger.info("Chargement de la population à la date : " + etat.getDate());
    long time = System.currentTimeMillis();
    populations = new ArrayList<Population<? extends AgentGeographique>>(0);
    // On recherche les populations correspondant à la date de début de
    // simulation
    coll = etat.getCollection();
    // Unités urbaines
    while (!collection.getUnitesBaties().isEmpty()) {
      collection.getUnitesBaties().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentUniteBatie) {
        collection.getUnitesBaties().add((AgentUniteBatie) agent);
      }
    }
    collection.getUnitesBaties().initSpatialIndex(Tiling.class, false);
    populations.add(collection.getUnitesBaties());
    // Zones élémentaires urbaines
    while (!collection.getZonesElementairesBaties().isEmpty()) {
      collection.getZonesElementairesBaties().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentZoneElementaireBatie) {
        collection.getZonesElementairesBaties().add(
            (AgentZoneElementaireBatie) agent);
      }
    }
    collection.getZonesElementairesBaties().initSpatialIndex(Tiling.class,
        false);
    populations.add(collection.getZonesElementairesBaties());
    // Groupes bâtiments
    while (!collection.getGroupesBatiments().isEmpty()) {
      collection.getGroupesBatiments().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentGroupeBatiments) {
        collection.getGroupesBatiments().add((AgentGroupeBatiments) agent);
      }
    }
    collection.getGroupesBatiments().initSpatialIndex(Tiling.class, true);
    populations.add(collection.getGroupesBatiments());
    // Bâtiments
    while (!collection.getBatiments().isEmpty()) {
      collection.getBatiments().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentBatiment) {
        collection.getBatiments().add((AgentBatiment) agent);
      }
    }
    collection.getBatiments().initSpatialIndex(Tiling.class, true);
    populations.add(collection.getBatiments());
    // Troncons cours d'eau
    while (!collection.getTronconsCoursEau().isEmpty()) {
      collection.getTronconsCoursEau().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentTronconCoursEau) {
        collection.getTronconsCoursEau().add((AgentTronconCoursEau) agent);
      }
    }
    collection.getTronconsCoursEau().initSpatialIndex(Tiling.class, false);
    populations.add(collection.getTronconsCoursEau());
    // Troncons chemins
    while (!collection.getTronconsChemin().isEmpty()) {
      collection.getTronconsChemin().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentTronconChemin) {
        collection.getTronconsChemin().add((AgentTronconChemin) agent);
      }
    }
    collection.getTronconsChemin().initSpatialIndex(Tiling.class, false);
    populations.add(collection.getTronconsChemin());
    // Troncons routes
    while (!collection.getTronconsRoute().isEmpty()) {
      collection.getTronconsRoute().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentTronconRoute) {
        collection.getTronconsRoute().add((AgentTronconRoute) agent);
      }
    }
    collection.getTronconsRoute().initSpatialIndex(Tiling.class, false);
    populations.add(collection.getTronconsRoute());
    // Troncons voies ferrées
    while (!collection.getTronconsVoieFerree().isEmpty()) {
      collection.getTronconsVoieFerree().remove(0);
    }
    for (AgentGeographique agent : coll) {
      if (agent instanceof AgentTronconVoieFerree) {
        collection.getTronconsVoieFerree().add((AgentTronconVoieFerree) agent);
      }
    }
    collection.getTronconsVoieFerree().initSpatialIndex(Tiling.class, false);
    populations.add(collection.getTronconsVoieFerree());

    for (Population<? extends AgentGeographique> pop : populations) {
      DataSet.getInstance().addPopulation(pop);
      if (logger.isDebugEnabled()) {
        logger.debug("Population " + pop.getNom() + " ajoutée au dataset avec "
            + pop.size() + " objets");
        logger.debug("Centre = " + pop.getCenter());
      }
    }
    Population<Arc> triangulation = new Population<Arc>(false, "Triangulation", //$NON-NLS-1$
        Arc.class, true);
    DataSet.getInstance().addPopulation(triangulation);
    Population<DefaultFeature> medialAxis = new Population<DefaultFeature>(
        false, "MedialAxis", //$NON-NLS-1$
        DefaultFeature.class, true);
    DataSet.getInstance().addPopulation(medialAxis);
    logger.info("Le chargement de la population a pris "
        + (System.currentTimeMillis() - time) + " ms");
  }

  protected void creationPopulationsv2(int date) {

    coll = new ArrayList<AgentGeographique>(0);
    // Unités urbaines
    List<AgentGeographique> agents = collection.getElementsGeo(
        UniteUrbaine.class, date);
    for (AgentGeographique agent : agents) {
      coll.add(agent);
    }
    // Zones élémentaires urbaines
    agents = collection.getElementsGeo(ZoneElementaireUrbaine.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Groupes bâtiments
    agents = collection.getElementsGeo(GroupeBatiments.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Bâtiments
    agents = collection.getElementsGeo(Batiment.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Troncons routes
    agents = collection.getElementsGeo(TronconRoute.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Troncons chemins
    agents = collection.getElementsGeo(TronconChemin.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Troncons cours d'eau
    agents = collection.getElementsGeo(TronconCoursEau.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
    // Troncons voies ferrées
    agents = collection.getElementsGeo(TronconVoieFerree.class, date);
    for (AgentGeographique agent : agents)
      coll.add(agent);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    GeOpenSimApplication application = new GeOpenSimApplication(
        "GeOpenSimulator", new ImageIcon(
            GeOpenSimApplication.class
                .getResource("/images/geopensim-icon.gif")));
    application.getMainFrame().getGui().setVisible(true);
  }

  @Override
  public void changed(SchedulerEvent event) {
    if (event.getType() == SchedulerEvent.Type.STARTED) {
      simulationButton.setIcon(stop);
    } else if (event.getType() == SchedulerEvent.Type.FINISHED) {
      simulationButton.setEnabled(false);
      // long time = System.currentTimeMillis();
      // EtatGlobal nouvelEtat = new EtatGlobal();
      // logger.info("création d'un nouvel état : "+nouvelEtat.getId());
      // nouvelEtat.setDate(collection.getDateSimulee());
      // nouvelEtat.setSimule(true);
      // //sauvegarde de la collection simulée
      // List<AgentGeographique> listeAgents = new
      // ArrayList<AgentGeographique>(0);
      // for (Population<? extends FT_Feature> pop :
      // DataSet.getInstance().getPopulations()){
      // for (FT_Feature feat:pop.getElements()){
      // if(feat instanceof AgentGeographique){
      // if(!listeAgents.contains((AgentGeographique)feat)){
      // listeAgents.add((AgentGeographique)feat);
      // }
      // }
      // }
      // }
      // for (AgentGeographique agent : listeAgents) {
      // if ((agent.getGeom()!=null)&&(!agent.isSupprime())){
      // agent.setDateSimulee(collection.getDateSimulee());
      // agent.setIdSimul(nouvelEtat.getId());
      // if (agent instanceof AgentUniteBatie){
      // AgentUniteBatie unite = (AgentUniteBatie)agent;
      // ElementRepresentation repUn = unite.construireRepresentationCourante();
      // unite.add(repUn);
      // collection.getUnitesBaties().add(unite);
      // }else if (agent instanceof AgentZoneElementaireBatie){
      // AgentZoneElementaireBatie zone = (AgentZoneElementaireBatie)agent;
      // ElementRepresentation repZE = zone.construireRepresentationCourante();
      // zone.add(repZE);
      // collection.getZonesElementairesBaties().add(zone);
      // }else if (agent instanceof AgentBatiment){
      // AgentBatiment bati = (AgentBatiment)agent;
      // ElementRepresentation repBat = bati.construireRepresentationCourante();
      // bati.add(repBat);
      // collection.getBatiments().add(bati);
      // }else if (agent instanceof AgentGroupeBatiments){
      // AgentGroupeBatiments groupe = (AgentGroupeBatiments)agent;
      // ElementRepresentation repGB =
      // groupe.construireRepresentationCourante();
      // groupe.add(repGB);
      // collection.getGroupesBatiments().add(groupe);
      // }else if(agent instanceof AgentTroncon){
      // AgentTroncon troncon = (AgentTroncon)agent;
      // ElementRepresentation repTronc =
      // troncon.construireRepresentationCourante();
      // troncon.add(repTronc);
      // if(troncon instanceof AgentTronconRoute){
      // collection.getTronconsRoute().add((AgentTronconRoute)troncon);
      // }else if(troncon instanceof AgentTronconChemin){
      // collection.getTronconsChemin().add((AgentTronconChemin)troncon);
      // }else if(troncon instanceof AgentTronconCoursEau){
      // collection.getTronconsCoursEau().add((AgentTronconCoursEau)troncon);
      // }else if(troncon instanceof AgentTronconVoieFerree){
      // collection.getTronconsVoieFerree().add((AgentTronconVoieFerree)troncon);
      // }
      // }
      // }
      // }
      //
      // creationPopulationsv2(collection.getDateSimulee());
      //
      // nouvelEtat.setCollection(coll);
      // int nbbati = 0;
      // int nbZE = 0;
      // for (AgentGeographique ag:coll){
      // if (ag instanceof AgentBatiment)nbbati ++;
      // else if (ag instanceof AgentZoneElementaireBatie)nbZE ++;
      // }
      //
      // // Création du nom
      // String debutNom = "";
      // try {debutNom = DataSet.db.getConnection().getCatalog();}
      // catch (SQLException e) {e.printStackTrace();}
      // String finNom = (nouvelEtat.isSimule()) ? "simulé" : "initial";
      // String nomEntier = debutNom+"_"+collection.getDateSimulee()+"_"+finNom;
      // // On vérifie que ce nom n'existe pas déjà
      // int numero = 0;
      // for (EtatGlobal etat : listeEtats){
      // if (etat.getNom().contains(nomEntier)){numero++;}
      // }
      // nomEntier = (numero>0) ? nomEntier+numero : nomEntier;
      // nouvelEtat.setNom(nomEntier);
      // // Assignation de l'état parent
      // nouvelEtat.setEtatPrecedent(etatCourant);
      // listeEtats.add(nouvelEtat);
      // comboEtatInitial.addItem(nouvelEtat.getNom());
      // comboEtatInitial.setSelectedItem(nouvelEtat.getNom());
      // time = System.currentTimeMillis() - time;
      // logger.info("création d'un nouvel état : "+nouvelEtat.getId() +
      // " terminées en " + time + " ms");
      // logger.info("i.e. " + TimeUtil.toTimeLength(time));
      //
      simulationButton.setIcon(start);
      simulationButton.setEnabled(true);
    } else {
      if (event.getType() == SchedulerEvent.Type.AGENT_FINISHED) {
        logger.info("agent " + event.getSource() + " finished");
        if (!this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
            .isRecording()) {
          this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
              .setRecord(true);
          if (this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
              .getRenderingManager() != null) {
            this.getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
                .getRenderingManager().renderAll();
          }
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.simulationButton) {
      if (Scheduler.getInstance().isRunning()) {
        logger.info("stopping the simulation");
        Scheduler.getInstance().desactiver();
      } else {
        Set<IFeature> selectedFeatures = getMainFrame()
            .getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures();
        List<AgentGeographique> agents = new ArrayList<AgentGeographique>(0);
        if (selectedFeatures.isEmpty()) {
          logger
              .info("No selected features, using all building blocks instead");
          for (AgentZoneElementaireBatie agent : collection
              .getZonesElementairesBaties()) {
            if (!agent.isDeleted()) {
              agents.add(agent);
            }
          }
          logger.info(agents.size() + " features taken");
        } else {
          for (IFeature feature : selectedFeatures) {
            if (ElementRepresentation.class
                .isAssignableFrom(feature.getClass())) {
              ElementRepresentation representation = (ElementRepresentation) feature;
              agents.add(representation.getAgentGeographique());
            } else if (AgentGeographique.class.isAssignableFrom(feature
                .getClass())) {
              agents.add((AgentGeographique) feature);
            }
          }
        }
        Scheduler.setAgentsAtraiter(agents);
        Scheduler.charger();
        if (logger.isDebugEnabled())
          logger.debug("instanciation des contraintes");
        collection.instancierContraintes();
        if (logger.isDebugEnabled())
          logger.debug("Lancement de la simulation");
        Scheduler.getInstance().activer();
      }
      if (logger.isInfoEnabled()) {
        logger.info("Changement de la date affichée terminé");
      }
    } else if (e.getSource() == editRulesButton) {
      EditRulesFrame editFrame = new EditRulesFrame(getMainFrame(), collection);
      editFrame.setVisible(true);
    } else if (e.getSource() == applyRulesButton) {
      this.appliquerRegles(listeReglesSelect);
    } else if (e.getSource() == simulationTreeButton) {// la fenêtre des états
                                                       // de simulation
      simulFrame = new SimulationTreeFrame(this.getMainFrame().getGui(),
          listeEtats, etatCourant);
      simulFrame.setVisible(true);
      simulFrame.setAlwaysOnTop(true);
    } else if (e.getSource() == comboEtatInitial) {
      String nomEtatInitial = this.comboEtatInitial.getSelectedItem()
          .toString();
      EtatGlobal etat = null;
      for (EtatGlobal et : listeEtats) {
        if (et.getNom().equals(nomEtatInitial))
          etat = et;
      }
      // on met à jour la carte
      this.affichageCarte(etat);
      // On met à jour l'arbre des états (si nécessaire)
      if (simulFrame != null) {
        simulFrame.affichageArbre(etatCourant.getNom());
      }
    } else if (e.getSource() == chooseDate) {
      ChooseDateDialog choixDate = new ChooseDateDialog(this.getMainFrame(),
          collection.getDateSimulee());
      choixDate.setVisible(true);
      int dateSimulee = choixDate.getDateSimulee();
      /*
       * if(dateSimulee!=0){ logger.info(dateSimulee);
       * collection.setDateSimulee(dateSimulee);
       * collection.setDureePasSimulation
       * (dateSimulee-collection.getDateDebutSimulation());
       * dateLabel.setText(textDate+collection.getDateSimulee()); }
       */
      this.setDateSimul(dateSimulee);
    } else if (e.getSource() == paramCompar) {
      // ChoixParametresComparaison choixParam = new
      // ChoixParametresComparaison();
      ListeParamComparaisonFrame choixParam = new ListeParamComparaisonFrame();
      choixParam.setVisible(true);
      choixParam.setAlwaysOnTop(true);
    } else if (e.getSource() == lancementCompar) {
      ChoixEtatComparaison choixEtatCompar = new ChoixEtatComparaison(
          (JFrame) this.getMainFrame(), listeEtats, etatCourant);
      choixEtatCompar.setVisible(true);
      choixEtatCompar.setAlwaysOnTop(true);
    } else if (e.getSource() == methodesPeuplement) {
      String nomFich = new File("").getAbsolutePath()
          + "\\ConfigurationMethodesPeuplement.xml";
      methodesPeuplementFrame = new ListeMethodesPeuplementFrame(nomFich, this
          .getMainFrame().getIconImage());
      methodesPeuplementFrame.setVisible(true);
      methodesPeuplementFrame.setAlwaysOnTop(true);
    } else if (e.getSource() == reglesEvolution) {
      reglesEvolutionFrame = new EditRulesFramev2();
      reglesEvolutionFrame.setVisible(true);
      reglesEvolutionFrame.setAlwaysOnTop(true);
    } else if (e.getSource() == lienTypeObjPeuplement) {
      lien = new LienPeuplementTypeFonctionnel();
      lien.setVisible(true);
      lien.setAlwaysOnTop(true);
    } else if (e.getSource() == changerValObj) {
      ChangeValeurObjectif choixValObj = new ChangeValeurObjectif(
          (JFrame) this.getMainFrame());
      choixValObj.setVisible(true);
      choixValObj.setAlwaysOnTop(true);
    } else if (e.getSource() == lancementSimul) {
      ChoixParamSimul choixParamSim = new ChoixParamSimul(
          (JFrame) this.getMainFrame(), listeEtats, etatCourant,
          collection.getDateSimulee());
      choixParamSim.setVisible(true);
      choixParamSim.setAlwaysOnTop(true);
      List<EvolutionRule> listeReglesS = choixParamSim.getRegles();
      if (listeReglesS != null) {
        listeReglesSelect = listeReglesS;
        this.appliquerRegles(listeReglesSelect);
      }
    }
  }

  public void appliquerRegles(List<EvolutionRule> listeR) {
    collection.applyEvolutionRules(listeR);
    ProjectFrame frame = this.getMainFrame().getSelectedProjectFrame();
    frame.getLayerViewPanel().repaint();
  }

  public void setDateSimul(int dat) {
    if (dat != 0) {
      logger.info(dat);
      collection.setDateSimulee(dat);
      collection.setDureePasSimulation(dat
          - collection.getDateDebutSimulation());
      dateLabel.setText(textDate + collection.getDateSimulee());
    }
  }

  public void affichageCarte(EtatGlobal etat) {
    if (etatCourant != etat) {
      etatCourant = etat;
      if (etatCourant != null) {
        int dateDeb = etatCourant.getDate();
        int dateSim = collection.getDateSimulee();
        collection.setDateDebutSimulation(dateDeb);
        collection.setDureePasSimulation(dateSim - dateDeb);
        logger.info(dateDeb);
        // On met à jour le menu des états
        int index = 0;
        for (int i = 0; i < comboEtatInitial.getItemCount(); i++) {
          if (comboEtatInitial.getItemAt(i) == etatCourant.getNom()) {
            index = i;
          }
        }
        comboEtatInitial.setSelectedIndex(index);
        // On déselectionne tout
        getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
            .getSelectedFeatures().clear();
        // On met à jour les populations et la carte
        for (Population<? extends AgentGeographique> pop : populations) {
          DataSet.getInstance().removePopulation(pop);
        }
        this.chargePopulations(etatCourant);
        for (Population<? extends AgentGeographique> pop : populations) {
          for (AgentGeographique agent : pop) {
            ElementRepresentation rep = null;
            if (!etatCourant.isSimule()) {
              rep = agent.getRepresentation(dateDeb);
            } else {
              rep = agent.getRepresentation(dateDeb, etatCourant.getId());
            }
            if (rep == null) {
              agent.setGeom(null);
              agent.setSupprime(true);
            } else {
              agent.prendreAttributsRepresentation(rep);
            }
          }
        }
        ProjectFrame frame = this.getMainFrame().getSelectedProjectFrame();
        frame.getLayerViewPanel().repaint();
        this.getMainFrame().getGui().repaint();
      }
    }
  }

  public void suppressionEtatComboBox(EtatGlobal etatASupprimer) {
    // On met à jour le menu des états
    int index = 0;
    for (int i = 0; i < comboEtatInitial.getItemCount(); i++) {
      if (comboEtatInitial.getItemAt(i) == etatASupprimer.getNom()) {
        index = i;
      }
    }
    comboEtatInitial.removeItemAt(index);
  }
}
