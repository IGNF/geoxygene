/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.menus.themes;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

@Deprecated
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
  }

  private class SelectAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CartagenApplication.getInstance().getFrame()
          .getVisuPanel().selectedObjects = new FT_FeatureCollection<IFeature>();
      for (IBuilding obj : CartAGenDocOld.getInstance().getCurrentDataset()
          .getBuildings()) {
        CartagenApplication.getInstance().getFrame()
            .getVisuPanel().selectedObjects.add(obj);
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
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(
              CartagenApplication.getInstance().getFrame(),
              "Coefficient de dilatation", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double coef = 1.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            double area = ab.getGeom().area();
            if (area < GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT) {
              ab.eliminate();
            } else {
              double aireMini = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
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
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
              CartagenApplication.getInstance().getFrame(),
              "Angle de rotation (en degres, sens trigo)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double angle = 0.0;
          if (s != null && !s.isEmpty()) {
            angle = Double.parseDouble(s);
          }
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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
          String s = JOptionPane.showInputDialog(
              CartagenApplication.getInstance().getFrame(), "Seuil", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double coef = 10.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : CartagenApplication.getInstance().getFrame()
              .getVisuPanel().selectedObjects) {
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

}
