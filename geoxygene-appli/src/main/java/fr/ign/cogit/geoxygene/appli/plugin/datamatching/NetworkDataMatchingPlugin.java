package fr.ign.cogit.geoxygene.appli.plugin.datamatching;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.network.DisplayToolBarNetworkDataMatching;
import fr.ign.cogit.geoxygene.appli.plugin.datamatching.network.EditParamPanel;


import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultatAppariement;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Data matching plugin.
 * Display 2 types of results :
 *   - links + stats + param 
 *   - corrected network + comparison network
 *   
 * @since 1.6 manage new Result structure
 * @author Julien Perret
 */
public class NetworkDataMatchingPlugin implements GeOxygeneApplicationPlugin,
  ActionListener {
  
private static Logger LOGGER = Logger.getLogger(NetworkDataMatchingPlugin.class.getName());
  
  private GeOxygeneApplication application;

  /** Reference Shape Filename. */
  private String refShapeFilename = null;
  /** Comparative Shape Filename. */
  private String compShapeFilename = null;
  /** Parameters. */
  // private String paramFilename = null;
  private ParamNetworkDataMatching param = null;

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    
    // Check if the DataMatching menu exists. If not we create it.
    JMenu menu = null;
    String menuName = I18N.getString("DataMatchingPlugin.DataMatching"); //$NON-NLS-1$
    for (Component c : application.getFrame().getJMenuBar().getComponents()) {
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
        I18N.getString("DataMatchingPlugin.NDM.OpenDataMatchingEnvironment" //$NON-NLS-1$
        ));
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    // Refresh menu of the application
    application
        .getFrame()
        .getJMenuBar()
        .add(menu, application.getFrame().getJMenuBar().getComponentCount() - 1);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    EditParamPanel dialogParamDataMatchingNetwork = new EditParamPanel(this);
    if (refShapeFilename == null || compShapeFilename == null) { // || paramFilename == null) {
      return;
    }
    
    if (LOGGER.isEnabledFor(Level.INFO)) {
      LOGGER.info("Fichier de référence : " + refShapeFilename);
      LOGGER.info("Fichier de comparaison : " + compShapeFilename);
      // LOGGER.info("Fichier paramètres : " + paramFilename);
    }

    /*
     * IPopulation<IFeature> popRef = ShapefileReader.chooseAndReadShapefile();
     * popRef.setNom("popRef"); IPopulation<IFeature> popComp =
     * ShapefileReader.chooseAndReadShapefile(); popComp.setNom("popComp");
     */
    IPopulation<IFeature> popRef = ShapefileReader.read(refShapeFilename);
    popRef.setNom("popRef");
    IPopulation<IFeature> popComp = ShapefileReader.read(compShapeFilename);
    popComp.setNom("popComp");

    // On charge
    //ParametresAppData paramAppData = null;
    //paramAppData = ParametresAppData.unmarshall(paramFilename);
    //if (LOGGER.isEnabledFor(Level.INFO)) {
    //  LOGGER.info("Paramètres chargés");
    //}

    ParametresApp paramOld = new ParametresApp();
    
//    paramOld = param.ParamNDMToParamApp();
    
    // Population
    paramOld.populationsArcs1.clear();
    paramOld.populationsArcs1.add(popRef);
    paramOld.populationsArcs2.clear();
    paramOld.populationsArcs2.add(popComp);
    
    // Ecarts de distance autorisés
    paramOld.distanceArcsMax = 25; // paramAppData.getNoeudsMax();
    paramOld.distanceArcsMin = 10; // paramAppData.getArcsMin();
    paramOld.distanceNoeudsMax = 10; // paramAppData.getArcsMax();
    paramOld.distanceNoeudsImpassesMax = -1; //paramAppData.getNoeudsImpassesMax();
    // 
    paramOld.topologieFusionArcsDoubles1 = false;
    paramOld.topologieFusionArcsDoubles2 = false;
    paramOld.topologieGraphePlanaire1 = false;
    paramOld.topologieGraphePlanaire2 = false;
    paramOld.topologieSeuilFusionNoeuds2 = 0.1;
    paramOld.varianteFiltrageImpassesParasites = false;
    paramOld.projeteNoeuds1SurReseau2 = false;
    paramOld.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    paramOld.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    paramOld.projeteNoeuds2SurReseau1 = false;
    paramOld.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    paramOld.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    paramOld.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    paramOld.varianteForceAppariementSimple = true;
    paramOld.varianteRedecoupageArcsNonApparies = true;
    paramOld.debugTirets = true;
    paramOld.debugBilanSurObjetsGeo = false;
    paramOld.varianteRedecoupageArcsNonApparies = true;
    paramOld.debugAffichageCommentaires = 2;
    paramOld.debugBuffer = true;
    paramOld.debugPasTirets = 10;
    
    
    System.out.println("attributOrientation1 = " + paramOld.attributOrientation1);
    // System.out.println("orientationMap1 = " + paramOld.orientationMap1.toString());

    // EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, reseaux);
    ResultatAppariement resultatAppariement = NetworkDataMatching.networkDataMatching(paramOld);
    EnsembleDeLiens liens = resultatAppariement.getLinkDataSet();
    ResultNetwork resultNetwork = resultatAppariement.getResultStat();
    
    /*LOGGER.info("Paramétrage = " + liens.getParametrage());
    LOGGER.info("Evaluation interne = " + liens.getEvaluationInterne());
    LOGGER.info("Evaluation globale = " + liens.getEvaluationGlobale());
    for (Lien feature : liens) {
      Lien lien = feature;
      LOGGER.info("Lien = " + lien); //$NON-NLS-1$
      LOGGER.info("Ref = " + lien.getObjetsRef().toString()); //$NON-NLS-1$
      LOGGER.info("Comp = " + lien.getObjetsComp()); //$NON-NLS-1$
      LOGGER.info("Evaluation = " + lien.getEvaluation()); //$NON-NLS-1$
    }*/

    CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseauRef(), resultatAppariement.getReseauComp(),
        liens);
    IPopulation<Arc> arcs = reseauRecale.getPopArcs();
    LOGGER.info(arcs.getNom());

    //Qu'est-ce que ca fait ??
    for (Lien lien : liens) {
      IGeometry geom = lien.getGeom();
      if (geom instanceof GM_Aggregate<?>) {
        GM_MultiCurve<GM_LineString> multiCurve = new GM_MultiCurve<GM_LineString>();
        for (IGeometry lineGeom : ((GM_Aggregate<?>) geom).getList()) {
          if (lineGeom instanceof GM_LineString) {
            multiCurve.add((GM_LineString) lineGeom);
          } else {
            if (lineGeom instanceof GM_MultiCurve<?>) {
              multiCurve.addAll(((GM_MultiCurve<GM_LineString>) lineGeom).getList());
            }
          }
        }
        lien.setGeom(multiCurve);
      } else {
        LOGGER.info(geom.getClass().getSimpleName());
      }
    }
    LOGGER.info(arcs.getNom());

    LOGGER.trace("----------------------------------------------------------");
    LOGGER.trace("Taille popRef = " + popRef.size());
    LOGGER.trace("Taille popComp = " + popComp.size());
    LOGGER.trace("Nom    popRef = " + popRef.getNom());
    LOGGER.trace("Nom    popComp = " + popComp.getNom());
    LOGGER.trace("----------------------------------------------------------");

    // StockageLiens.stockageDesLiens(liens, 1, 2, 3);
    
    this.application.getFrame().getDesktopPane().removeAll();
    
    Dimension desktopSize = this.application.getFrame().getDesktopPane().getSize();
    int widthProjectFrame = desktopSize.width / 2;
    int heightProjectFrame = desktopSize.height / 2;
    
    // SLD
    StyledLayerDescriptor sld = StyledLayerDescriptor
        .unmarshall("./src/main/resources/sld/appariementSLD.xml");
    System.out.println("SLD, nombre de layers = " + sld.getLayers().size());

    // Frame n°1
    ProjectFrame p1 = this.application.getFrame().newProjectFrame();
    p1.setTitle("Reference Pop + Comparaison Pop");
    // Layer popRef
    // Layer l = sld.getLayer("popRef");
    // p1.setSld(sld);
    p1.addUserLayer(popRef, "1 - Utilisateur", null);
    p1.addUserLayer(popComp, "2 - BDUni", null);
    p1.addUserLayer(resultatAppariement.getReseauRef().getPopArcs(), "Arcs utilisateur 1", null);
    p1.addUserLayer(resultatAppariement.getReseauRef().getPopNoeuds(), "Noeuds utilisateur 1", null);
    p1.addUserLayer(resultatAppariement.getReseauComp().getPopArcs(), "Arcs BDUni 2", null);
    p1.addUserLayer(resultatAppariement.getReseauComp().getPopNoeuds(), "Noeuds BDUni 2", null);
    p1.setSize(widthProjectFrame, heightProjectFrame);
    p1.setLocation(0, 0);
    Viewport viewport = p1.getLayerViewPanel().getViewport();

    ProjectFrame p3 = this.application.getFrame().newProjectFrame();
    p3.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p3.getLayerViewPanel());
    p3.setTitle("Corrected Pop après recalage"); //$NON-NLS-1$
    p3.addUserLayer(arcs, "Utilisateur recale", null);
    p3.addUserLayer(popRef, "Utilisateur brut", null);
    p3.addUserLayer(popComp, "2 - BDUni", null);
    p3.setSize(widthProjectFrame, heightProjectFrame);
    p3.setLocation(0, heightProjectFrame); 
    
    ProjectFrame p4 = this.application.getFrame().newProjectFrame();
    p4.getLayerViewPanel().setViewport(viewport);
    viewport.getLayerViewPanels().add(p4.getLayerViewPanel());
    p4.setTitle("Links"); //$NON-NLS-1$
    p4.addUserLayer(popRef, "1 - Utilisateur", null);
    p4.addUserLayer(popComp, "2 - BDUni", null);
    Layer layer = p4.addUserLayer(liens, "Liens", null);
    // p4.addUserLayer(((LienApp)liens.getElements())., "2 - BDUni", null);
    p4.setSize(widthProjectFrame, heightProjectFrame * 2);
    p4.setLocation(widthProjectFrame, 0);
    
//     JButton launchButton = new JButton("Test");
    DisplayToolBarNetworkDataMatching resultToolBar = new DisplayToolBarNetworkDataMatching(p4, resultNetwork, paramOld);
    JMenuBar menuBar = new JMenuBar();
    p4.setJMenuBar(menuBar);   
    p4.getJMenuBar().add(resultToolBar, 0);
    
    layer.getSymbolizer().getStroke().setStrokeWidth(2);
    LOGGER.info("Finished"); //$NON-NLS-1$
    
  }

  /**
   * @param f The reference Shape Filename to set
   */
  public void setRefShapeFilename(String f) {
    refShapeFilename = f;
  }

  /**
   * @param f The comparative Shape Filename to set
   */
  public void setCompShapeFilename(String f) {
    compShapeFilename = f;
  }

  /**
   * @param f The parameters XML Filename to set
   */
  // public void setParamFilename(String f) {
  //  paramFilename = f;
  // }

  public void setParam(ParamNetworkDataMatching p) {
    param = p;
  }
}
