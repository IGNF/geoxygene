package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.algorithms.SwingingArmNonConvexHull;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;


/**
 * Frame de configuration pour le calcul des alpha hull
 * 
 * @author Simon
 *
 */
public class FrameAHull extends JFrame implements ActionListener{

  private static final long serialVersionUID = 1L;
  
  private JTextField jtfStart;
  private JTextField jtfEnd;
  private JTextField jtfStep;
  
  private JPanel pSaisie;
  
  private ListAHull model;
  
  private JButton submit;
  private ProjectFrame projectFrame;
  private IPopulation<? extends IFeature> pop;

  private JMenuBar menuBar;

  private JMenuItem export;
  
  private Action enterAction = new EnterAction();

  public FrameAHull() {
    super();
    setAlwaysOnTop(true);
    initialize();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(500, 300);
    setVisible(true);
    setLocationRelativeTo(null);
  }
  
  
  private void initialize() {
    
    menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    
    pSaisie = new JPanel();
    pSaisie.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets.left = 20;
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    
    gbc.anchor = GridBagConstraints.WEST;
    
    gbc.gridy++;
    JLabel jl2 = new JLabel("Valeur Initiale:");
    pSaisie.add(jl2, gbc);
    
    gbc.gridy++;
    pSaisie.add(new JLabel("Valeur Finale:"), gbc);
    
    gbc.gridy++;
    pSaisie.add(new JLabel("Step:"), gbc);
    
    gbc.gridy = 0;
    gbc.gridx++;
    gbc.ipadx = 120;
    
    jtfStart = new JTextField();
    jtfStart.setText("10000");
    jtfEnd = new JTextField("1000");
    jtfStep = new JTextField("1000");
    
    pSaisie.add(new JLabel("Saisir Paramètres"), gbc);
    gbc.gridy++;
    pSaisie.add(jtfStart, gbc);
    gbc.gridy++;
    pSaisie.add(jtfEnd, gbc);
    gbc.gridy++;
    pSaisie.add(jtfStep, gbc);
    
    submit = new JButton("Calcul");
    submit.addActionListener(this);
    
    gbc.gridy++;
    pSaisie.add(submit, gbc);


    jtfStart.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ),"doEnterAction" );
    jtfEnd.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ),"doEnterAction" );
    jtfStep.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ),"doEnterAction" );

    jtfStart.getActionMap().put( "doEnterAction", enterAction );
    jtfEnd.getActionMap().put( "doEnterAction", enterAction );
    jtfStep.getActionMap().put( "doEnterAction", enterAction );


    getContentPane().add(pSaisie);



  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource()==submit){
      this.getContentPane().removeAll();
      this.getContentPane().repaint();
      
      int start = Integer.parseInt(jtfStart.getText());
      int end   = Integer.parseInt(jtfEnd.getText());
      int step  = Integer.parseInt(jtfStep.getText());
      
      model = new ListAHull(start, end, step);
      
      ArrayList<IDirectPosition> listIDP = new ArrayList<IDirectPosition>();
      
      GM_MultiPoint agg = new GM_MultiPoint();
      
      for (IFeature iFeature : pop.getElements()) {
        DirectPosition dp = new DirectPosition(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
        listIDP.add(dp);
        agg.add(new GM_Point(dp));
      }
      
      double surfConvexHull = agg.convexHull().area();
      
      
      for (ValueTableAHull value : model.getV()) {
        SwingingArmNonConvexHull sanch = new SwingingArmNonConvexHull(listIDP, value.getAlpha());
        value.setGeometry(sanch.compute());
        
        int n = 0;
        for (IDirectPosition idp : listIDP)
          if(!value.getGeometry().contains(idp.toGM_Point()))
            n++;
        value.setnPtsHors(n);
        value.setSurfConvexHull(surfConvexHull);
        value.setNbPointsTotal(listIDP.size());
      }
      
      
      this.getContentPane().removeAll();
      this.getContentPane().setLayout(new BorderLayout());
      JTable tableau = new JTable(model);
      
      getContentPane().add(new JScrollPane(tableau), BorderLayout.CENTER);
      
      this.revalidate();
      
      
      
      for(int i=model.getV().size()-1; i>0; i--){
        ValueTableAHull value = model.getV().get(i);
        
        String name = "AlphaHull - "+value.getAlpha();
        Population<DefaultFeature> popAlpha = new Population<DefaultFeature>(name);
        projectFrame.getDataSet().addPopulation(popAlpha);
        
        int b = (value.getAlpha()-start)*(254-38)/(end-start)+38;
            
        Layer layerC = projectFrame.getSld().createLayer(name,GM_Polygon.class, Color.BLACK, new Color(b, 108, 201), 1f, 4);
        
        projectFrame.getSld().add(layerC);
        
        popAlpha.add(new DefaultFeature(value.getGeometry()));
       
      }
      
      projectFrame.getSld().moveLayer(0,projectFrame.getSld().layersCount()-1);
      
      export = new JMenuItem("Export");
      export.addActionListener(this);
      menuBar.add(export);
      this.revalidate();

    } else if(e.getSource()==export){
      JFileChooser jfc = new JFileChooser("C:\\Users\\SIMON\\Dropbox\\Ecole\\ProjetRech");
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int returnVal = jfc.showOpenDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String src = jfc.getSelectedFile().getAbsolutePath()+"\\resultat.xls";
        try {
          model.write(src);
        } catch (IOException e1) {
          JOptionPane.showMessageDialog(this, "Probleme avec l'ecriture du fichier", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
  
  
  class EnterAction extends AbstractAction{

    private static final long serialVersionUID = 1L;

    public void actionPerformed( ActionEvent tf ){
      submit.doClick();
    }
  }
  
  /**
   * Modifie le projectFrame du plugin
   * @param projectFrame - la nouvelle instance du projectFrame 
   */
  public void setProjectFrame(ProjectFrame projectFrame) {
    this.projectFrame = projectFrame;
  }
  
  /**
   * Modifie la population du plugin
   * @param pop - la nouvelle instance de la population
   */
  public void setPop(IPopulation<? extends IFeature> pop) {
    this.pop = pop;
  }

}
