package fr.ign.cogit.geoxygene.appli.plugin.matching.dst.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.plugin.matching.dst.EvidencePlugin;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;

/**
 * 
 * @author Marie-Dominique Van Damme
 * 
 */
public class CriterePanel extends JPanel implements ActionListener {

  /** Default serial ID. */
  private static final long serialVersionUID = 1L;

  private EvidencePlugin evidencePlugin;

  private Collection<Source<IFeature, GeomHypothesis>> criteria;

  private FormLayout layout;
  private CellConstraints cc;

  private JButton executeQuery = null;
  private JButton addCritere = null;

  private JTextField distanceSelectionField = null;
  private double distanceSelection;

  private ButtonGroup groupDecoupage;
  private JRadioButton radioCredibility;
  private JRadioButton radioPlausibility;
  private JRadioButton radioPignistic;
  
  private JButton matriceConfusionButton;
  private JButton configLayerButton;
  private JButton razButton;

  private List<JButton> editListCriteria;

  private JComboBox<String> comboListeJeu1;
  private JComboBox<String> comboListeJeu2;
  
  private List<String> listAttr1;
  private List<String> listAttr2;
  
  private boolean affTopo = false;

  /**
   * 
   * @param pFrame
   * @param c
   */
  public CriterePanel(EvidencePlugin evidencePlugin,
      Collection<Source<IFeature, GeomHypothesis>> c, double d) {

    this.evidencePlugin = evidencePlugin;
    this.criteria = c;
    this.distanceSelection = d;

    editListCriteria = new ArrayList<JButton>();

    String height = "10dlu, pref, 2dlu, pref, 2dlu, pref, 5dlu, pref, 5dlu, pref, pref, pref, 5dlu, pref, pref, 5dlu, pref, 5dlu, ";
    for (int i = 0; i < criteria.size(); i++) {
      height = height + "pref, pref, 5dlu, ";
    }

    layout = new FormLayout("10dlu, pref, 10dlu, pref, pref, 10dlu", 
        height + "10dlu, pref, 2dlu, pref, 10dlu");
    setLayout(layout);
    cc = new CellConstraints();

    listAttr1 = new ArrayList<String>();
    listAttr1.add("toponyme");
    listAttr1.add("nature");
    
    listAttr2 = new ArrayList<String>();
    listAttr2.add("NOM");
    listAttr2.add("NATURE");
    
    initPanel();

  }

  /**
   * 
   */
  private void initPanel() {

    // Liste des layers
    List<Layer> layersDispo = evidencePlugin.getApplication().getMainFrame()
        .getSelectedProjectFrame().getLayers();

    // Sélection du jeu 1
    add(new JLabel("Jeu n°1 : "), cc.xy(2, 2));
    comboListeJeu1 = new JComboBox<String>();
    comboListeJeu1.addItem("--");
    for (int l = 0; l < layersDispo.size(); l++) {
      comboListeJeu1.addItem(layersDispo.get(l).getName());
    }
    comboListeJeu1.addActionListener(this);
    add(comboListeJeu1, cc.xy(4, 2));

    // Sélection du jeu 2
    add(new JLabel("Jeu n°2 : "), cc.xy(2, 4));
    comboListeJeu2 = new JComboBox<String>();
    comboListeJeu2.addItem("--");
    for (int l = 0; l < layersDispo.size(); l++) {
      comboListeJeu2.addItem(layersDispo.get(l).getName());
    }
    comboListeJeu2.addActionListener(this);
    add(comboListeJeu2, cc.xy(4, 4));

    // ---------------------------------------------------------------
    //   Attributs
    add(new JLabel("Attributs : "), cc.xy(2, 6));
    
    // Jd1
    JPanel attrJeu = new JPanel();
    attrJeu.setBackground(Color.white);
    FormLayout layoutAttrPanel = new FormLayout(
        "4dlu, pref, 10dlu, pref, 4dlu", 
        "4dlu, pref, pref, 4dlu");
    attrJeu.setLayout(layoutAttrPanel);
    CellConstraints ccAP = new CellConstraints();
    
    attrJeu.add(new JLabel("toponyme"), ccAP.xy(2, 2));
    attrJeu.add(new JLabel("nature"), ccAP.xy(2, 3));
    attrJeu.add(new JLabel("NOM"), ccAP.xy(4, 2));
    attrJeu.add(new JLabel("NATURE"), ccAP.xy(4, 3));
    add(attrJeu, cc.xy(4, 6));
    
    // Sélection des candidats
    add(new JLabel("Sélection des candidats : "), cc.xy(2, 8));
    distanceSelectionField = new JTextField(4);
    distanceSelectionField.setText(Double.toString(distanceSelection));
    JPanel pStock = new JPanel(new FlowLayout(FlowLayout.LEFT));
    pStock.setBackground(Color.WHITE);
    pStock.add(distanceSelectionField);
    pStock.add(new JLabel(" m"));

    add(pStock, cc.xy(4, 8));

    // Méthode de décision
    JLabel titreDecisionType = new JLabel("Méthode de décision : ");
    add(titreDecisionType,
        cc.xywh(2, 10, 1, 3, CellConstraints.LEFT, CellConstraints.TOP));
    groupDecoupage = new ButtonGroup();
    radioCredibility = new JRadioButton("Credibility");
    radioCredibility.setBackground(Color.WHITE);
    groupDecoupage.add(radioCredibility);
    add(radioCredibility, cc.xy(4, 10));
    radioPlausibility = new JRadioButton("Plausibility");
    radioPlausibility.setBackground(Color.WHITE);
    groupDecoupage.add(radioPlausibility);
    add(radioPlausibility, cc.xy(4, 11));
    radioPignistic = new JRadioButton("Pignistic");
    radioPignistic.setBackground(Color.WHITE);
    radioPignistic.setSelected(true);
    groupDecoupage.add(radioPignistic);
    add(radioPignistic, cc.xy(4, 12));

    // Monde clos
    add(new JLabel("Monde clos : "), cc.xy(2, 14));
    ButtonGroup groupDecoupage2 = new ButtonGroup();
    JRadioButton ouiMC = new JRadioButton("Oui");
    ouiMC.setBackground(Color.WHITE);
    groupDecoupage2.add(ouiMC);
    add(ouiMC, cc.xy(4, 14));
    JRadioButton nonMC = new JRadioButton("Non");
    nonMC.setBackground(Color.WHITE);
    nonMC.setSelected(true);
    groupDecoupage2.add(nonMC);
    add(nonMC, cc.xy(4, 15));

    // Critères
    JPanel titreCriterePanel = new JPanel();
    titreCriterePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    titreCriterePanel.setBackground(Color.WHITE);

    JTextField titre = new JTextField("Critères (sources) - distances");
    titre.setFont(new java.awt.Font("Default", java.awt.Font.BOLD, 14));
    titre.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    // titre.setBackground(UIManager.getColor("Panel.background"));
    titreCriterePanel.add(titre);

    // bouton new critere
    ImageIcon iconAdd = new ImageIcon(CriterePanel.class.getResource(
        "/images/icons/add.png").getPath());
    addCritere = new JButton("", iconAdd);
    addCritere.setToolTipText("Ajouter un critère");
    addCritere.addActionListener(this);
    JPanel pAC = new JPanel();
    pAC.setBackground(Color.WHITE);
    pAC.add(addCritere);
    titreCriterePanel.add(pAC);

    add(titreCriterePanel, cc.xyw(2, 17, 5));

    // Liste des critères
    int cpt = 0;
    for (Source<IFeature, GeomHypothesis> source : criteria) {

      // Barre
      JPanel funcTitlePanel = new JPanel();

      FormLayout layoutTitlePanel = new FormLayout(
          "10dlu, pref, 10dlu, pref, pref, 10dlu", "2dlu, pref, 2dlu");
      funcTitlePanel.setLayout(layoutTitlePanel);
      CellConstraints ccTP = new CellConstraints();

      funcTitlePanel.add(new JLabel(source.getName()), ccTP.xy(2, 2));
      // functionGraphPanel.add(new JLabel("Nature & NATURE"),
      // BorderLayout.CENTER);
      ImageIcon icon = new ImageIcon(CriterePanel.class.getResource(
          "/images/icons/pencil.png").getPath());
      JButton editCritere = new JButton("", icon);
      editCritere.setMaximumSize(new Dimension(40, 40));
      editCritere.setToolTipText("Modifier les paramètres du critère");
      editCritere.addActionListener(this);
      funcTitlePanel.add(editCritere, ccTP.xy(4, 2));
      editListCriteria.add(editCritere);

      icon = new ImageIcon(CriterePanel.class.getResource(
          "/images/icons/cross.png").getPath());
      JButton deleteCritere = new JButton("", icon);
      deleteCritere.setMaximumSize(new Dimension(20, 20));
      deleteCritere.setToolTipText("Supprimer le critère");
      deleteCritere.addActionListener(this);
      funcTitlePanel.add(deleteCritere, ccTP.xy(5, 2));

      add(funcTitlePanel, cc.xyw(2, 19 + 3 * cpt, 5));

      // Graphes
      JPanel functionGraphPanel = new JPanel();
      // functionGraphPanel.setPreferredSize(new java.awt.Dimension(330, 150));
      functionGraphPanel.setLayout(new GridLayout(1, 0));

      // F1
      FunPanel f1 = new FunPanel(((GeoSource) source).getMasseAppCi());
      functionGraphPanel.add(f1, BorderLayout.CENTER);

      // F2
      FunPanel f2 = new FunPanel(((GeoSource) source).getMasseAppPasCi());
      functionGraphPanel.add(f2, BorderLayout.CENTER);

      // F2
      FunPanel f3 = new FunPanel(((GeoSource) source).getMasseIgnorance());
      functionGraphPanel.add(f3, BorderLayout.CENTER);

      //
      add(functionGraphPanel, cc.xyw(2, 20 + 3 * cpt, 5));

      // Compteur
      cpt++;
    }

    // ----------------------------------------------------------------
    // Panel 3 : boutons
    JPanel boutonPanel = new JPanel();
    boutonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    boutonPanel.setBackground(Color.WHITE);

    // Button to execute
    ImageIcon icon = new ImageIcon(CriterePanel.class.getResource(
        "/images/icons/cog.png").getPath());
    executeQuery = new JButton("", icon);
    executeQuery
        .setToolTipText("Lancer l'appariement utilisant la théorie de D-S");
    executeQuery.addActionListener(this);
    boutonPanel.add(executeQuery);

    razButton = new JButton();
    razButton.setIcon(new ImageIcon(EvidencePlugin.class
        .getResource("/images/icons/arrow_refresh_small.png")));
    razButton.setToolTipText("Remise à zéro");
    razButton.setSelected(false);
    razButton.addActionListener(this);
    boutonPanel.add(razButton);

    add(boutonPanel, cc.xyw(2, 20 + 3 * criteria.size(), 4));
    
    
    // ----------------------------------------------------------------
    //    Resultat boutons panel
    JPanel resultatPanel = new JPanel();
    resultatPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    resultatPanel.setBackground(Color.WHITE);
    
    configLayerButton = new JButton();
    configLayerButton.setIcon(new ImageIcon(EvidencePlugin.class
        .getResource("/images/icons/layers.png")));
    configLayerButton.setToolTipText("Afficher du texte.");
    configLayerButton.setSelected(false);
    configLayerButton.setEnabled(false);
    configLayerButton.addActionListener(this);
    resultatPanel.add(configLayerButton);
    
    matriceConfusionButton = new JButton();
    matriceConfusionButton.setIcon(new ImageIcon(EvidencePlugin.class
        .getResource("/images/icons/table.png")));
    matriceConfusionButton.setToolTipText("Afficher la matrice de confusion");
    matriceConfusionButton.setSelected(false);
    matriceConfusionButton.setEnabled(false);
    matriceConfusionButton.addActionListener(this);
    resultatPanel.add(matriceConfusionButton);
    
    JButton jeuMasseButton = new JButton();
    jeuMasseButton.setIcon(new ImageIcon(EvidencePlugin.class
        .getResource("/images/icons/chart_bar.png")));
    jeuMasseButton
        .setToolTipText("Afficher le jeu de masse après fusion des candidats");
    jeuMasseButton.setSelected(false);
    jeuMasseButton.setEnabled(false);
    jeuMasseButton.addActionListener(this);
    resultatPanel.add(jeuMasseButton);
    
    add(resultatPanel, cc.xyw(2, 22 + 3 * criteria.size(), 4));

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == executeQuery) {
      evidencePlugin.runEvidenceMatching();
    } else if (source == addCritere) {
      EvidenceParamPanel dialogEvidenceParamPanel = new EvidenceParamPanel(null);
      if (dialogEvidenceParamPanel.getAction().equals("LAUNCH")) {
        System.out.println("!!!!");
      }
    } else if (source == matriceConfusionButton) {
      MatriceConfusionPanel matriceConfusionPanel = new MatriceConfusionPanel(this.evidencePlugin.getPopLien(), 
          this.evidencePlugin.getFeatureTypeLien(), "nature1", "NATURE2");
      matriceConfusionPanel.setLocation(new Point (100, 100));
    } else if (source == configLayerButton) {
      configLayer();
    } else if (source == razButton) {
      remiseAZero();
    }

    // Critere Button
    for (int i = 0; i < editListCriteria.size(); i++) {
      if (source == editListCriteria.get(i)) {
        // System.out.println("i = " + i);
        @SuppressWarnings("unchecked")
        EvidenceParamPanel dialogEvidenceParamPanel = new EvidenceParamPanel(
            (Source<IFeature, GeomHypothesis>) this.criteria.toArray()[i]);
        if (dialogEvidenceParamPanel.getAction().equals("LAUNCH")) {
          System.out.println("!!!!");
        }
      }
    }
  }
  
  
  public void remiseAZero() {
    
    // supprime le layer lien
    this.evidencePlugin.getApplication().getMainFrame().getSelectedProjectFrame().removeLayers(
        this.evidencePlugin.getListLayer());
    
    // on rafraichit le tableau des liens
    Population<DefaultFeature>popLien = new Population<DefaultFeature>(false, "Liens", DefaultFeature.class, true);
    popLien.setFeatureType(this.evidencePlugin.getFeatureTypeLien());
    this.evidencePlugin.setPopLien(popLien);
    this.evidencePlugin.initLien();
    
    // On rafraichit le debugPanel
    this.evidencePlugin.getDebugPanel().setPopLien(popLien);
    this.evidencePlugin.getDebugPanel().refreshLightDebug();
    
    // desactive
    setEnableMatriceConfusionButton(false);
    setEnableConfigLayerButton(false);
  }
  
  /**
   * Option : affiche ou non les toponymes.
   */
  private void configLayer() {
    
    Layer layerRef = this.evidencePlugin.getApplication().getMainFrame().getSelectedProjectFrame().getLayer(getLayerNameDataset1());
    Layer layerPoint = this.evidencePlugin.getApplication().getMainFrame().getSelectedProjectFrame().getLayer(getLayerNameDataset2());
    
    if (!affTopo) {
      
      affTopo = true;
      TextSymbolizer txtSymbolizer = new TextSymbolizer();
      txtSymbolizer.setUnitOfMeasurePixel();
      fr.ign.cogit.geoxygene.style.Font sldFont = new fr.ign.cogit.geoxygene.style.Font(new Font("Verdana", Font.PLAIN, 10));
      txtSymbolizer.setFont(sldFont);
      // build the symbolizer fill
      Fill txtFill = new Fill();
      
      txtSymbolizer.setFill(txtFill);
      Stroke txtStroke = new Stroke();
      txtStroke.setColor(Color.BLACK);
      txtStroke.setStrokeWidth(1.5f);
      txtSymbolizer.setStroke(txtStroke);
      txtSymbolizer.setLabel("NOM");
      txtFill.setColor(new Color(0, 90, 50));
      LabelPlacement placement = new LabelPlacement();
      PointPlacement ptPlacement = new PointPlacement();
      
      placement.setPlacement(ptPlacement);
      ptPlacement.setRotation(0.0f);
      txtSymbolizer.setLabelPlacement(placement);
    
      FeatureTypeStyle ftStyle2 = new FeatureTypeStyle();
      Rule rule2 = new Rule();
      List<Symbolizer> listSymbolizer = new ArrayList<Symbolizer>();
      listSymbolizer.add(txtSymbolizer);
      rule2.setSymbolizers(listSymbolizer);
      List<Rule> listRule2 = new ArrayList<Rule>();
      listRule2.add(rule2);
      ftStyle2.setRules(listRule2);
      
      layerPoint.getStyles().get(0).getFeatureTypeStyles().add(ftStyle2);
      
      txtSymbolizer = new TextSymbolizer();
      txtSymbolizer.setUnitOfMeasurePixel();
      txtSymbolizer.setFont(sldFont);
      txtFill = new Fill();
      txtFill.setColor(new Color(156, 1, 75));
      txtSymbolizer.setFill(txtFill);
      txtStroke = new Stroke();
      txtStroke.setColor(Color.BLACK);
      txtStroke.setStrokeWidth(1.5f);
      txtSymbolizer.setStroke(txtStroke);
      txtSymbolizer.setLabel("toponyme");
    
      placement = new LabelPlacement();
      ptPlacement = new PointPlacement();
      placement.setPlacement(ptPlacement);
      ptPlacement.setRotation(0.0f);
      txtSymbolizer.setLabelPlacement(placement);
    
      ftStyle2 = new FeatureTypeStyle();
      rule2 = new Rule();
      listSymbolizer = new ArrayList<Symbolizer>();
      listSymbolizer.add(txtSymbolizer);
      rule2.setSymbolizers(listSymbolizer);
      listRule2 = new ArrayList<Rule>();
      listRule2.add(rule2);
      ftStyle2.setRules(listRule2);
      layerRef.getStyles().get(0).getFeatureTypeStyles().add(ftStyle2);
      
      this.evidencePlugin.getApplication().getMainFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
      
    } else {
      affTopo = false;
      layerPoint.getStyles().get(0).getFeatureTypeStyles().remove(1);
      layerRef.getStyles().get(0).getFeatureTypeStyles().remove(1);
      this.evidencePlugin.getApplication().getMainFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
    }
  }

  
  public double getDistanceSelection() {
    return Double.parseDouble(distanceSelectionField.getText());
  }
  
  public String getLayerNameDataset1() {
    return this.comboListeJeu1.getSelectedItem().toString();
  }
  
  public String getLayerNameDataset2() {
    return this.comboListeJeu2.getSelectedItem().toString();
  }

  @SuppressWarnings("unchecked")
  public IPopulation<IFeature> getJeu1() {
    if (this.comboListeJeu1.getSelectedItem().toString().equals("--")) {
      return null;
    } else {
      Layer layerJeu1 = this.evidencePlugin.getApplication().getMainFrame()
          .getSelectedProjectFrame()
          .getLayer(this.comboListeJeu1.getSelectedItem().toString());
      return (IPopulation<IFeature>) layerJeu1.getFeatureCollection();
    }
  }

  @SuppressWarnings("unchecked")
  public IPopulation<IFeature> getJeu2() {
    if (this.comboListeJeu2.getSelectedItem().toString().equals("--")) {
      return null;
    } else {
      Layer layerJeu2 = this.evidencePlugin.getApplication().getMainFrame()
          .getSelectedProjectFrame()
          .getLayer(this.comboListeJeu2.getSelectedItem().toString());
      return (IPopulation<IFeature>) layerJeu2.getFeatureCollection();
    }
  }
  
  public List<String> getListAttr1() {
    return listAttr1;
  }
  
  public List<String> getListAttr2() {
    return listAttr2;
  }
  
  public void setEnableMatriceConfusionButton(boolean b) {
    matriceConfusionButton.setEnabled(b);
  }
  
  public void setEnableConfigLayerButton(boolean b) {
    configLayerButton.setEnabled(b);
  }
}
