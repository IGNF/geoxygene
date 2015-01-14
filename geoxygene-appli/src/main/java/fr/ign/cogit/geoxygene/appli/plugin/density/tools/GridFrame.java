package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;


/**
 * Fenetre graphique qui parametre le calcul de la grille de densité
 * Valeur par défaut:
 * <ul>
 *  <li>lignes: 10</li>
 *  <li>colonnes: 10</li>
 *  <li>classes: 7</li>
 * </ul>
 * @see Grid
 * @author Simon
 *
 */
public class GridFrame extends JFrame implements ActionListener, FocusListener, ChangeListener{

  private static final long serialVersionUID = 1L;

  private static int DEFAULT_ROW = 10;
  private static int DEFAULT_COL = 10;
  private static int DEFAULT_CLA =  7;


  private JTextField  colonne_grille;
  private JTextField    ligne_grille;
  private JTextField colonne_terrain;
  private JTextField   ligne_terrain;

  private JSpinner spin;

  private JCheckBox checkGrille;
  private JCheckBox checkTerrain;

  private JButton plus_col_gri;
  private JButton moin_col_gri;
  private JButton plus_lig_gri;
  private JButton moin_lig_gri;
  private JButton plus_col_ter;
  private JButton moin_col_ter;
  private JButton plus_lig_ter;
  private JButton moin_lig_ter;

  private JButton save;

  private JPanel grillePanel;
  private JPanel terrainPanel;
  private JPanel classifPanel;

  private ProjectFrame projectFrame;

  private Vector<IPopulation<DefaultFeature>> popGrid = null;
  private IPopulation<DefaultFeature> savePop;

  private Grid grid;


  public GridFrame() {
    super();
    setAlwaysOnTop(true);
    initialize();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setVisible(true);
    setLocationRelativeTo(null);
  }

  /**
   * Construit la fenetre graphique avec ses 3 panneaux:
   * <ul>
   *    <li>grillePanel</li>
   *    <li>terrainPanel</li>
   *    <li>classifPanel</li>
   * </ul>
   */
  private void initialize() {
    getContentPane().setLayout(new GridBagLayout());
    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.gridx = gbc2.gridy = 0;
    gbc2.insets.left = 20;
    getContentPane().add(getGrillePanel(), gbc2);
    gbc2.gridy++;
    getContentPane().add(getTerrainPanel(), gbc2);
    gbc2.gridy++;
    getContentPane().add(getClassifPanel(), gbc2);
    gbc2.gridy++;
    save = new JButton("Save as");
    save.addActionListener(this);
    getContentPane().add(save, gbc2);
    this.pack();
  }

  /**
   * Construit la partie d'IHM dédiée a la modification des lignes et colonnes
   * @return le panneaux construit
   */
  private JPanel getGrillePanel(){
    if(grillePanel==null){
      grillePanel = new JPanel();
      grillePanel.setBorder(BorderFactory.createTitledBorder("Grille"));
      grillePanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = gbc.gridy = 0;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.insets.left = 10;


      grillePanel.add(new JLabel("Colonne:"), gbc);
      gbc.gridy++;
      grillePanel.add(new JLabel("Ligne:"), gbc);
      gbc.gridy++;
      grillePanel.add(new JLabel("Utiliser Grille"), gbc);
      gbc.gridy++;

      gbc.gridx++;
      gbc.gridy = 0;
      gbc.ipadx = 150;

      colonne_grille = new JTextField(DEFAULT_COL+"");
      colonne_grille.addFocusListener(this);
      grillePanel.add(colonne_grille, gbc);
      gbc.gridy++;

      ligne_grille = new JTextField(DEFAULT_ROW+"");
      ligne_grille.addFocusListener(this);
      grillePanel.add(ligne_grille, gbc);
      gbc.gridy++;

      gbc.ipadx = 2;

      checkGrille = new JCheckBox();
      checkGrille.setSelected(true);
      checkGrille.addActionListener(this);
      grillePanel.add(checkGrille, gbc);

      gbc.gridx++;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.NONE;

      plus_col_gri = new MiniBouton("+");
      plus_col_gri.addActionListener(this);
      plus_col_gri.setPreferredSize(new Dimension(15, 15));
      grillePanel.add(plus_col_gri, gbc);
      gbc.gridy++;


      plus_lig_gri = new MiniBouton("+");
      plus_lig_gri.addActionListener(this);
      plus_lig_gri.setPreferredSize(new Dimension(15, 15));
      grillePanel.add(plus_lig_gri, gbc);

      gbc.gridx++;
      gbc.gridy = 0;

      moin_col_gri = new MiniBouton("-");
      moin_col_gri.addActionListener(this);
      moin_col_gri.setPreferredSize(new Dimension(15, 15));
      grillePanel.add(moin_col_gri, gbc);
      gbc.gridy++;

      moin_lig_gri = new MiniBouton("-");
      moin_lig_gri.addActionListener(this);
      moin_lig_gri.setPreferredSize(new Dimension(15, 15));
      grillePanel.add(moin_lig_gri, gbc);
    }
    return grillePanel;

  }

  /**
   * Construit la partie d'IHM dédiée a la modification des emprises
   * @return le panneaux construit
   */
  private JPanel getTerrainPanel(){
    if(terrainPanel==null){
      terrainPanel = new JPanel();
      terrainPanel.setBorder(BorderFactory.createTitledBorder("Terrain"));
      terrainPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = gbc.gridy = 0;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.insets.left = 10;


      terrainPanel.add(new JLabel("Colonne:"), gbc);
      gbc.gridy++;
      terrainPanel.add(new JLabel("Ligne:"), gbc);
      gbc.gridy++;
      terrainPanel.add(new JLabel("Utiliser Grille"), gbc);
      gbc.gridy++;

      gbc.gridx++;
      gbc.gridy = 0;
      gbc.ipadx = 150;

      colonne_terrain = new JTextField();
      colonne_terrain.addFocusListener(this);
      terrainPanel.add(colonne_terrain, gbc);
      gbc.gridy++;

      ligne_terrain = new JTextField();
      ligne_terrain.addFocusListener(this);
      terrainPanel.add(ligne_terrain, gbc);
      gbc.gridy++;

      gbc.ipadx = 2;

      checkTerrain = new JCheckBox();
      checkTerrain.setSelected(false);
      checkTerrain.addActionListener(this);
      terrainPanel.add(checkTerrain, gbc);

      gbc.gridx++;
      gbc.gridy = 0;


      terrainPanel.add(new JLabel("m"), gbc);
      gbc.gridy++;
      terrainPanel.add(new JLabel("m"), gbc);
      gbc.gridy++;

      gbc.gridx++;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.NONE;

      plus_col_ter = new MiniBouton("+");
      plus_col_ter.addActionListener(this);
      plus_col_ter.setPreferredSize(new Dimension(15, 15));
      terrainPanel.add(plus_col_ter, gbc);
      gbc.gridy++;


      plus_lig_ter = new MiniBouton("+");
      plus_lig_ter.addActionListener(this);
      plus_lig_ter.setPreferredSize(new Dimension(15, 15));
      terrainPanel.add(plus_lig_ter, gbc);

      gbc.gridx++;
      gbc.gridy = 0;

      moin_col_ter = new MiniBouton("-");
      moin_col_ter.addActionListener(this);
      moin_col_ter.setPreferredSize(new Dimension(15, 15));
      terrainPanel.add(moin_col_ter, gbc);
      gbc.gridy++;

      moin_lig_ter = new MiniBouton("-");
      moin_lig_ter.addActionListener(this);
      moin_lig_ter.setPreferredSize(new Dimension(15, 15));
      terrainPanel.add(moin_lig_ter, gbc);



      plus_col_ter.setEnabled(false);
      moin_col_ter.setEnabled(false);
      plus_lig_ter.setEnabled(false);
      moin_lig_ter.setEnabled(false);

    }
    return terrainPanel;

  }

  /**
   * Construit la partie d'IHM dédiée a la modification de la classification
   * @return le panneaux construit
   */
  private JPanel getClassifPanel(){
    if(classifPanel==null){
      classifPanel = new JPanel();
      classifPanel.setBorder(BorderFactory.createTitledBorder("Classificaion"));
      classifPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = gbc.gridy = 0;
      spin = new JSpinner(new SpinnerNumberModel(DEFAULT_CLA, 1, 100, 1));
      spin.addChangeListener(this);
      classifPanel.add(spin, gbc);
    }
    return classifPanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource()==plus_col_gri){
      grid.setNumCols(grid.getNumCols()+1);
    } else if(e.getSource()==moin_col_gri){
      grid.setNumCols(grid.getNumCols()-1);
    } else if(e.getSource()==plus_lig_gri){
      grid.setNumRows(grid.getNumRows()+1);
    } else if(e.getSource()==moin_lig_gri){
      grid.setNumRows(grid.getNumRows()-1);

    } else if(e.getSource()==plus_col_ter){
      grid.setStepX(grid.getStepX()+1);
    } else if(e.getSource()==moin_col_ter){
      grid.setStepX(grid.getStepX()-1);
    } else if(e.getSource()==plus_lig_ter){
      grid.setStepY(grid.getStepY()+1);
    } else if(e.getSource()==moin_lig_ter){
      grid.setStepY(grid.getStepY()-1);
    } else if(e.getSource()==checkGrille || e.getSource()==checkTerrain){
      if(e.getSource()==checkGrille)
        checkTerrain.setSelected(!checkTerrain.isSelected());
      else
        checkGrille.setSelected(!checkGrille.isSelected());
      toggleButtons();
    } else if(e.getSource()==save){
      ShapefileWriter.chooseAndWriteShapefile(savePop);
    }
    updateAll();
  }

  /**
   * Permute l'activation des boutons plus et moins
   */
  private void toggleButtons(){
    plus_col_gri.setEnabled(!plus_col_gri.isEnabled());
    moin_col_gri.setEnabled(!moin_col_gri.isEnabled());
    plus_lig_gri.setEnabled(!plus_lig_gri.isEnabled());
    moin_lig_gri.setEnabled(!moin_lig_gri.isEnabled());
    plus_col_ter.setEnabled(!plus_col_ter.isEnabled());
    moin_col_ter.setEnabled(!moin_col_ter.isEnabled());
    plus_lig_ter.setEnabled(!plus_lig_ter.isEnabled());
    moin_lig_ter.setEnabled(!moin_lig_ter.isEnabled());
  }
  
  /**
   * Modifie le ProjectFrame de la fenetre
   * @param projectFrame - la nouvelle instance du ProjectFrame
   */
  public void setProjectFrame(ProjectFrame projectFrame) {
    this.projectFrame = projectFrame;
  }

  /**
   * Modifie la population de la fenetre
   * @param pop - la nouvelle instance de population
   */
  public void setPop(IPopulation<DefaultFeature> pop) {
    IDirectPosition lc = pop.getEnvelope().getLowerCorner();
    IDirectPosition uc = pop.getEnvelope().getUpperCorner();
    grid = new Grid(uc, lc, Integer.parseInt(ligne_grille.getText()), Integer.parseInt(colonne_grille.getText()));
    for (IFeature iFeature : pop.getElements()) {
      IDirectPosition p = new DirectPosition(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      grid.addPoint(p);
    }
    try {
      projectFrame.getLayerViewPanel().getViewport().update();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Mets a jour les champs ainsi que l'affichage graphique
   */
  private void updateAll(){
    if( !(colonne_grille.getText().equals(grid.getNumCols()+"")    && 
            ligne_grille.getText().equals(grid.getNumRows()+"")    && 
           colonne_terrain.getText().equals(grid.getStepX()+"")    && 
             ligne_terrain.getText().equals(grid.getStepY()+""))   ){
    updateFields();
    updateAff();
    }
  }
  
  /**
   * Mets a jour l'affichage graphique
   */
  @SuppressWarnings("unchecked")
  private void updateAff() {
    popGrid = new Vector<IPopulation<DefaultFeature>>();
    List<IPopulation<? extends IFeature>> l = projectFrame.getDataSet().getPopulations();
    Vector<String> v = new Vector<String>();
    
    for (IPopulation<? extends IFeature> iPopulation : l){
      if(iPopulation.getNom().contains("Grid"))
        v.add(iPopulation.getNom());
    }
    
    int n = (int) spin.getModel().getValue();
    System.out.println("n couches: "+n);

    if(v.size()==n){
      for (int i = 0; i < n; i++) {
        IPopulation<DefaultFeature> plop = (IPopulation<DefaultFeature>) projectFrame.getDataSet().getPopulation("Grid "+i);
        popGrid.add(plop);
        plop.clear();
      }



    } else {
      for (String name : v)
        if(name.contains("Grid")){
          Layer pipo = projectFrame.getSld().getLayer(name);
          projectFrame.getSld().remove(pipo);
          projectFrame.getDataSet().removePopulation(projectFrame.getDataSet().getPopulation(name));
        }




      if(popGrid.size()==0){
        for (int i = 0; i < n; i++) {
          Population<DefaultFeature> plop = new Population<DefaultFeature>("Grid "+i);
          popGrid.add(plop);
          projectFrame.getDataSet().addPopulation(plop);
          Color c = new Color(255+i*(232-255)/(n-1), 250+i*(136-250)/(n-1), 133+i*(12-133)/(n-1));
          Layer layer = projectFrame.getSld().createLayer("Grid "+i,GM_Polygon.class, c, c, 1f, 1);
          layer.getSymbolizer().setUnitOfMeasurePixel();
          projectFrame.getSld().add(layer);
        }

        projectFrame.getSld().moveLayer(0,n);
      }
    }
    
    savePop = new Population<DefaultFeature>();
    
    FeatureType newFeatureType= new FeatureType();
    AttributeType type = new AttributeType();
    String nomField = "nPoints";
    String memberName = "nPoints";
    String valueType = "int";
    type.setNomField(nomField);
    type.setMemberName(memberName);
    type.setValueType(valueType);
    newFeatureType.addFeatureAttribute(type);

    newFeatureType.setGeometryType(GM_Polygon.class);


    // Création d'un schéma associé au featureType
    SchemaDefaultFeature schema = new SchemaDefaultFeature();
    schema.setFeatureType(newFeatureType);

    newFeatureType.setSchema(schema);

    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
    attLookup.put(new Integer(0), new String[] { nomField, memberName });
    schema.setAttLookup(attLookup);



    for (IPopulation<DefaultFeature> iPopulation : popGrid) {
      iPopulation.setFeatureType(newFeatureType);
    }

    savePop.setFeatureType(newFeatureType);
    
    for (int r = 0; r < grid.getNumRows(); r++) {
      for (int c = 0; c < grid.getNumCols(); c++) {

        IDirectPosition tl = new DirectPosition(grid.getLowerCorner().getX()+(c+1)*grid.getStepX(), grid.getUpperCorner().getY()-(r+1)*grid.getStepY());
        IDirectPosition br = new DirectPosition(grid.getLowerCorner().getX()+(c)*grid.getStepX()  , grid.getUpperCorner().getY()-(r)*grid.getStepY());

        List<IDirectPosition> list1 = new ArrayList<IDirectPosition>();


        list1.add(new DirectPosition(br.getX(), tl.getY()));
        list1.add(tl);
        list1.add(new DirectPosition(tl.getX(), br.getY()));
        list1.add(br);

        GM_LineString lineString = new GM_LineString(list1);
        GM_Polygon sqr = new GM_Polygon(lineString);
        DefaultFeature df = new DefaultFeature(sqr);

        df.setFeatureType(newFeatureType);
        df.setSchema(schema);


        int r2 = grid.getNumRows()-r-1;
        int nb = new Integer(grid.getMaille(r2, c).size());
        Object[] tab = {nb};
        df.setAttributes(tab);
        popGrid.get(grid.getClasse(nb, (int) spin.getModel().getValue())).add(df);
        savePop.add(df);
      }
    }

    try {
      projectFrame.getLayerViewPanel().getViewport().update();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

  }


/**
 * Mets à jour l'affichage des champs de la fenetre
 */
  private void updateFields(){
    colonne_grille.setText(grid.getNumCols()+"");
    ligne_grille.setText(grid.getNumRows()+"");
    colonne_terrain.setText(grid.getStepX()+"");
    ligne_terrain.setText(grid.getStepY()+"");
  }


  @Override
  public void focusGained(FocusEvent e) {
    if(e.getSource()==colonne_grille || e.getSource()==ligne_grille){
      checkTerrain.setSelected(false);
      checkGrille.setSelected(true);
    } else {
      checkTerrain.setSelected(true);
      checkGrille.setSelected(false);
    }
  }


  @Override
  public void focusLost(FocusEvent e) {
    if(e.getSource()==colonne_grille){
      try{
        int col = Integer.parseInt(colonne_grille.getText());
        if(col<=0)
          throw new NumberFormatException("Non 0 or negative number");
        colonne_grille.setForeground(Color.BLACK);
        if(col!=grid.getNumCols())
          grid.setNumCols(col);
        updateAll();
      }catch(NumberFormatException nfe){
        colonne_grille.setForeground(Color.RED);
        Toolkit.getDefaultToolkit().beep(); 
      }
    } else if(e.getSource()==ligne_grille){
      try{
        int row = Integer.parseInt(ligne_grille.getText());
        if(row<=0)
          throw new NumberFormatException("Non 0 or negative number");
        ligne_grille.setForeground(Color.BLACK);
        if(row!=grid.getNumRows())
          grid.setNumRows(row);
        updateAll();
      }catch(NumberFormatException nfe){
        ligne_grille.setForeground(Color.RED);
        Toolkit.getDefaultToolkit().beep(); 
      }
    } else if(e.getSource()==colonne_terrain){
      try{
        double stepX = Double.parseDouble(colonne_terrain.getText());
        if(stepX<=0)
          throw new NumberFormatException("Non 0 or negative number");
        colonne_terrain.setForeground(Color.BLACK);
        if(stepX!=grid.getStepX())
          grid.setStepX(stepX);
        updateAll();
      }catch(NumberFormatException nfe){
        colonne_terrain.setForeground(Color.RED);
        Toolkit.getDefaultToolkit().beep(); 
      }
    } else if(e.getSource()==ligne_terrain){
      try{
        double stepY = Double.parseDouble(ligne_terrain.getText());
        if(stepY<=0)
          throw new NumberFormatException("Non 0 or negative number");
        ligne_terrain.setForeground(Color.BLACK);
        if(stepY!=grid.getStepY())
          grid.setStepY(stepY);
        updateAll();
      }catch(NumberFormatException nfe){
        ligne_terrain.setForeground(Color.RED);
        Toolkit.getDefaultToolkit().beep(); 
      }
    }
  }


  @Override
  public void stateChanged(ChangeEvent e) {
    updateAff();
  }
}

/**
 * Surcharge de la classe JButton pour modifier l'apparence des boutons
 * @author Simon
 *
 */
class MiniBouton extends JButton {

  private static final long serialVersionUID = 1L;

  public MiniBouton(String s){
    super(s);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Dimension center = this.getSize();
    Graphics2D g2d = (Graphics2D) g;
    if(isEnabled())
      g2d.setColor(new Color(200, 200, 200));
    else
      g2d.setColor(new Color(100, 100, 100));
    g2d.fillRect(0, 0, center.width, center.height);

    center.width  /= 2;
    center.height /=2;

    g2d.setColor(Color.BLACK);
    g2d.drawLine( center.width-3, center.height, center.width+3, center.height);
    if(getText().equals("+"))
      g2d.drawLine( center.width, center.height-3,  center.width, center.height+3);
  }

}

