/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.algorithms.polygon.PolygonAggregation;
import fr.ign.cogit.cartagen.algorithms.polygon.PolygonSquaring;
import fr.ign.cogit.cartagen.algorithms.polygon.SquarePolygonLS;
import fr.ign.cogit.cartagen.core.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.core.Legend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

public class BuildingMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(BuildingMenu.class.getName());

  private JMenuItem mBatimentSelectionnerTous = new JMenuItem(
      new SelectAction());

  public JCheckBoxMenuItem mBuildingDeplacement = new JCheckBoxMenuItem(
      "Displace buildings");

  public JCheckBoxMenuItem mIdBatiVoir = new JCheckBoxMenuItem("Display id");

  public JCheckBoxMenuItem mVoirTauxSuperposition = new JCheckBoxMenuItem(
      "Display overlapping rate");

  public JCheckBoxMenuItem mVoirAire = new JCheckBoxMenuItem("Voir aire");
  public JCheckBoxMenuItem mVoirAireBut = new JCheckBoxMenuItem(
      "Voir aire but");

  public JCheckBoxMenuItem mVoirAltitude = new JCheckBoxMenuItem(
      "Voir altitude");

  public JCheckBoxMenuItem mVoirOrientationGenerale = new JCheckBoxMenuItem(
      "Voir orientation generale");
  public JCheckBoxMenuItem mVoirOrientationMurs = new JCheckBoxMenuItem(
      "Voir orientation murs");
  public JCheckBoxMenuItem mVoirRosaceOrientationMurs = new JCheckBoxMenuItem(
      "Voir rosace orientation murs");

  public JCheckBoxMenuItem mVoirRosaceEncombrement = new JCheckBoxMenuItem(
      "Voir rosace encombrement");

  public JCheckBoxMenuItem mVoirElongation = new JCheckBoxMenuItem(
      "Voir elongation");

  public JCheckBoxMenuItem mVoirConvexite = new JCheckBoxMenuItem(
      "Voir convexite");

  public JCheckBoxMenuItem mVoirLgPlusPetitCote = new JCheckBoxMenuItem(
      "Voir lg plus petit cote");

  private JMenuItem mParametrisedDilatation = new JMenuItem(
      new ParametrisedDilatationAction());
  private JMenuItem mDilatation = new JMenuItem(new DilatationAction());
  private JMenuItem mPPRE = new JMenuItem(new EnlargeToRectangleAction());
  private JMenuItem mPPREAireConstante = new JMenuItem(new ToRectangleAction());
  private JMenuItem mRotation = new JMenuItem(new RotateAction());
  private JMenuItem mEnveloppeConvexe = new JMenuItem(new ToConvexHullAction());
  private JMenuItem mEnveloppeRectAxe = new JMenuItem(
      new ToRectAxisHullAction());
  private JMenuItem mSimplification = new JMenuItem(new SimplifyAction());
  private JMenuItem mSimpleSquaring = new JMenuItem(new SimpleSquaringAction());
  private JMenuItem mLSSquaring = new JMenuItem(new LSSquaringAction());
  private JMenuItem mAmalgamation = new JMenuItem(new AmalgamationAction());

  /**
   * Constructor a of the menu from a title.
   * @param title
   */
  public BuildingMenu(String title) {
    super(title);

    this.add(this.mBatimentSelectionnerTous);
    this.addSeparator();

    this.add(this.mBuildingDeplacement);
    this.addSeparator();

    this.add(this.mIdBatiVoir);
    this.addSeparator();

    this.add(this.mVoirTauxSuperposition);
    this.addSeparator();
    this.add(this.mVoirAire);
    this.add(this.mVoirAireBut);
    this.addSeparator();
    this.add(this.mVoirAltitude);
    this.addSeparator();
    this.add(this.mVoirOrientationGenerale);
    this.add(this.mVoirOrientationMurs);
    this.add(this.mVoirRosaceOrientationMurs);
    this.addSeparator();
    this.add(this.mVoirRosaceEncombrement);
    this.addSeparator();
    this.add(this.mVoirElongation);
    this.add(this.mVoirConvexite);
    this.add(this.mVoirLgPlusPetitCote);

    this.addSeparator();

    this.add(this.mParametrisedDilatation);
    this.add(this.mDilatation);
    this.add(this.mPPRE);
    this.add(this.mPPREAireConstante);
    this.add(this.mRotation);
    this.add(this.mEnveloppeConvexe);
    this.add(this.mEnveloppeRectAxe);
    this.add(this.mSimplification);
    JMenu mSquaring = new JMenu("Squaring algorithms");
    mSquaring.add(this.mSimpleSquaring);
    mSquaring.add(this.mLSSquaring);
    this.add(mSquaring);
    this.add(this.mAmalgamation);
  }

  private class SelectAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings()) {
        SelectionUtil.addFeatureToSelection(
            CartAGenPlugin.getInstance().getApplication(), obj);
      }
    }

    public SelectAction() {
      this.putValue(Action.NAME, "Select all buildings");
    }
  }

  private class ParametrisedDilatationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(appli.getMainFrame().getGui(),
              "Coefficient de dilatation", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double coef = 1.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Dilatation du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            ab.setGeom(CommonAlgorithms.homothetie(ab.getGeom(), coef));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ParametrisedDilatationAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger parametrised dilatation algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger parametrised dilatation");
    }
  }

  private class DilatationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            double area = ab.getGeom().area();
            if (area < GeneralisationSpecifications.BUILDING_ELIMINATION_AREA_THRESHOLD) {
              ab.eliminate();
            } else {
              double aireMini = GeneralisationSpecifications.BUILDING_MIN_AREA
                  * Legend.getSYMBOLISATI0N_SCALE()
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000000.0;
              if (area < aireMini) {
                GM_Object geom = (GM_Object) CommonAlgorithms
                    .homothetie(ab.getGeom(), Math.sqrt(aireMini / area));
                ab.setGeom(geom);
              }
            }
          }
        }
      });
      th.start();
    }

    public DilatationAction() {
      super();
      this.putValue(Action.NAME, "Self dilatation of selected buildings");
    }

  }

  private class EnlargeToRectangleAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger
                .info("Transformation en PPRE du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            ab.setGeom(
                SmallestSurroundingRectangleComputation.getSSR(ab.getGeom()));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public EnlargeToRectangleAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Enlarge to rectangle algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Enlarge to rectangle");
    }
  }

  private class ToRectangleAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info(
                "Transformation en PPRE avec aire constante du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Aire initiale: " + ab.getGeom().area());
            }
            ab.setGeom(SmallestSurroundingRectangleComputation
                .getSSRPreservedArea(ab.getGeom()));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Aire finale: " + ab.getGeom().area());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToRectangleAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to rectangle algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to rectangle");
    }
  }

  private class RotateAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Angle de rotation (en degres, sens trigo)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double angle = 0.0;
          if (s != null && !s.isEmpty()) {
            angle = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Rotation du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            ab.setGeom(CommonAlgorithms.rotation(ab.getGeom(),
                angle * Math.PI / 180.0));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public RotateAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Rotate algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Rotate");
    }
  }

  private class ToConvexHullAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger
                .info("Transformation en enveloppe convexe du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            ab.setGeom(ab.getGeom().convexHull());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToConvexHullAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to convex hull algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to convex hull");
    }
  }

  private class ToRectAxisHullAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info(
                "Transformation en enveloppe rectangulaire parallèle aux axes du batiment "
                    + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie initiale: " + ab.getGeom());
            }
            ab.setGeom(ab.getGeom().envelope().getGeom());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger
                  .config("Geometrie finale: " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToRectAxisHullAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to rect. axis hull algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to rect. axis hull");
    }
  }

  private class SimplifyAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane
              .showInputDialog(
                  CartAGenPlugin.getInstance().getApplication().getMainFrame()
                      .getGui(),
                  "Seuil", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double coef = 10.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Simplification de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            ab.setGeom(
                SimplificationAlgorithm.simplification(ab.getGeom(), coef));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public SimplifyAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Simplify algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Simplify");
    }
  }

  private class SimpleSquaringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Angles to square (radians)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double angTol = 8 * Math.PI / 180;
          if (s != null && !s.isEmpty()) {
            angTol = Double.parseDouble(s);
          }
          String s2 = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "tolerance (radians)", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double correctTol = 0.6 * Math.PI / 180;
          if (s2 != null && !s2.isEmpty()) {
            correctTol = Double.parseDouble(s2);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Equarrissage de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            PolygonSquaring squaring = new PolygonSquaring(ab.getGeom(), angTol,
                correctTol);
            ab.setGeom(squaring.simpleSquaring());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public SimpleSquaringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Simple Squaring algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Simple Squaring");
    }
  }

  private class LSSquaringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "Right angles tol (°)", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double rightTol = 8.0;
          if (s != null && !s.isEmpty()) {
            rightTol = Double.parseDouble(s);
          }
          String s3 = JOptionPane.showInputDialog(
              CartAGenPlugin.getInstance().getApplication().getMainFrame()
                  .getGui(),
              "45° angles tol (°)", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double midTol = 8.0;
          if (s3 != null && !s3.isEmpty()) {
            midTol = Double.parseDouble(s3);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(
              CartAGenPlugin.getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Equarrissage de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            SquarePolygonLS squaring = new SquarePolygonLS(rightTol, 0.1,
                midTol);
            squaring.setPolygon(ab.getGeom());
            ab.setGeom(squaring.square());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public LSSquaringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Least Squares Squaring algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger LS Squaring");
    }
  }

  private class AmalgamationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      List<IFeature> selected = SelectionUtil.getListOfSelectedObjects(
          CartAGenPlugin.getInstance().getApplication());
      IFeature feat1 = selected.get(0);
      IFeature feat2 = selected.get(1);
      PolygonAggregation algo = new PolygonAggregation(
          (IPolygon) feat1.getGeom(), (IPolygon) feat2.getGeom());
      IPolygon amalgamated = algo.regnauldAmalgamation(1.0);
      System.out.println(amalgamated);
      feat1.setGeom(amalgamated);
      ((IGeneObj) feat2).eliminate();
    }

    public AmalgamationAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Amalgamation of selected two buildings from Regnauld 98");
      this.putValue(Action.NAME, "Amalgamation of selected two buildings");
    }
  }
}
