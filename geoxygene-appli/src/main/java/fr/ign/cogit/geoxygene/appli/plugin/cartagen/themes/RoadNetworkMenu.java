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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IBranchingCrossroad;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoundAbout;
import fr.ign.cogit.cartagen.genealgorithms.section.CollapseRoundabout;
import fr.ign.cogit.cartagen.genealgorithms.section.LineCurvatureSmoothing;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.SectionSymbol;
import fr.ign.cogit.cartagen.spatialanalysis.network.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

public class RoadNetworkMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final Logger logger = Logger.getLogger(RoadNetworkMenu.class
      .getName());

  private final JMenuItem mResRoutierEnrich = new JMenuItem(
      new EnrichRoadNetAction());
  private final JMenuItem mNetworkFaces = new JMenuItem(
      new NetworkFacesAction());
  private final JMenuItem mResRoutierSelect = new JMenuItem(

  new SelectSectionsAction());
  public JCheckBoxMenuItem mNoeudsResRoutierVoir = new JCheckBoxMenuItem(
      "Display nodes");
  public JCheckBoxMenuItem mDegreNoeudsResRoutierVoir = new JCheckBoxMenuItem(
      "Display nodes degree");
  public JCheckBoxMenuItem mRoutierVoirEmpatementTroncons = new JCheckBoxMenuItem(
      "Display sections coalescence value");
  public JCheckBoxMenuItem mRoutierVoirSinuositeTroncons = new JCheckBoxMenuItem(
      "Display sections sinuosity value");
  public JCheckBoxMenuItem mRoutierVoirRouteDecalee = new JCheckBoxMenuItem(
      "Display offset road");
  private final JMenuItem mRoutierSupprimerImpasses = new JMenuItem(
      new DeleteDeadEndsAction());
  private final JMenuItem mRoutierDensifier = new JMenuItem(new DensifyAction());
  private final JMenuItem mRoutierAgregerTronconsAdjacentsAnalogues = new JMenuItem(
      new AggregationAction());
  private final JMenuItem mRoutierDetectBranchings = new JMenuItem(
      new DetectBranchingsAction());
  private final JMenuItem mRoutierCollapseRoundabouts = new JMenuItem(
      new CollapseRoundaboutsAction());
  private final JMenuItem mRoutierLissageGaussien = new JMenuItem(
      new GaussianSmoothingAction());
  private final JMenuItem mRoutierFiltrageCourbe = new JMenuItem(
      new CurvatureFilteringAction());

  public RoadNetworkMenu(String title) {
    super(title);

    this.add(this.mResRoutierEnrich);
    this.add(this.mNetworkFaces);
    this.add(this.mResRoutierSelect);

    this.addSeparator();

    this.add(this.mNoeudsResRoutierVoir);
    this.add(this.mDegreNoeudsResRoutierVoir);

    this.addSeparator();

    this.add(this.mRoutierVoirEmpatementTroncons);
    this.add(this.mRoutierVoirSinuositeTroncons);

    this.addSeparator();

    this.add(this.mRoutierVoirRouteDecalee);

    this.addSeparator();

    this.add(this.mRoutierSupprimerImpasses);
    this.add(this.mRoutierDensifier);
    this.add(this.mRoutierAgregerTronconsAdjacentsAnalogues);

    this.addSeparator();

    this.add(this.mRoutierDetectBranchings);
    this.add(this.mRoutierCollapseRoundabouts);

    this.addSeparator();

    this.add(this.mRoutierLissageGaussien);
    this.add(this.mRoutierFiltrageCourbe);

  }

  private class EnrichRoadNetAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      RoadNetworkMenu.this.logger.info("Enrichment of "
          + CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork());
      NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
          .getCurrentDataset(), CartAGenDoc.getInstance().getCurrentDataset()
          .getRoadNetwork());
    }

    public EnrichRoadNetAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Enrichment of the road network");
      this.putValue(Action.NAME, "Enrichment");
    }
  }

  private class NetworkFacesAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      NetworkEnrichment.buildNetworkFaces(CartAGenDoc.getInstance()
          .getCurrentDataset());
    }

    public NetworkFacesAction() {
      this.putValue(Action.NAME, "Create network faces");
    }
  }

  private class SelectSectionsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      RoadNetworkMenu.this.logger.info("Selection of "
          + CartAGenDoc.getInstance().getCurrentDataset().getRoadNetwork());
      for (IRoadLine section : CartAGenDoc.getInstance().getCurrentDataset()
          .getRoads()) {
        SelectionUtil.addFeatureToSelection(CartAGenPlugin.getInstance()
            .getApplication(), section);
      }
    }

    public SelectSectionsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Select all the sections of the road network");
      this.putValue(Action.NAME, "Select all sections");
    }
  }

  private class DeleteDeadEndsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {

          String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(),
              "Longueur seuil des impasses:", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double lg = 1.0;
          if (s != null && !s.isEmpty()) {
            lg = Double.parseDouble(s);
          }

          RoadNetworkMenu.this.logger
              .info("Suppression des impasses du reseau routier");
          NetworkEnrichment.supprimerImpasses(CartAGenDoc.getInstance()
              .getCurrentDataset().getRoadNetwork(), lg);

          if (RoadNetworkMenu.this.logger.isLoggable(Level.FINEST)) {
            RoadNetworkMenu.this.logger
                .finest("calcul importance des noeuds de " + this);
          }

          RoadNetworkMenu.this.logger
              .info("Fin de suppression des impasses du reseau routier");
        }
      });
      th.start();
    }

    public DeleteDeadEndsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Delete the dead ends enrichments");
      this.putValue(Action.NAME, "Delete dead ends");
    }
  }

  private class DensifyAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IRoadLine tr : CartAGenDoc.getInstance().getCurrentDataset()
              .getRoads()) {
            if (tr.isDeleted()) {
              continue;
            }
            RoadNetworkMenu.this.logger
                .info("Densification du troncon routier " + tr);
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie initiale: "
                  + tr.getGeom());
            }
            tr.setGeom(LineDensification.densification(tr.getGeom(), 1.0));
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie finale: "
                  + tr.getGeom());
            }
            RoadNetworkMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public DensifyAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger point densification on all network sections");
      this.putValue(Action.NAME, "Densify all sections");
    }
  }

  private class AggregationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          RoadNetworkMenu.this.logger
              .info("Aggregation of analog adjacent road sections");
          if (RoadNetworkMenu.this.logger.isLoggable(Level.FINEST)) {
            RoadNetworkMenu.this.logger.finest("   initial: nbTroncons="
                + CartAGenDoc.getInstance().getCurrentDataset().getRoads()
                    .size()
                + "  nbNoeuds="
                + CartAGenDoc.getInstance().getCurrentDataset().getRoadNodes()
                    .size());
          }
          NetworkEnrichment.aggregateAnalogAdjacentSections(CartAGenDoc
              .getInstance().getCurrentDataset(), CartAGenDoc.getInstance()
              .getCurrentDataset().getRoadNetwork());
          if (RoadNetworkMenu.this.logger.isLoggable(Level.FINEST)) {
            RoadNetworkMenu.this.logger.finest("   final: nbTroncons="
                + CartAGenDoc.getInstance().getCurrentDataset().getRoads()
                    .size()
                + "  nbNoeuds="
                + CartAGenDoc.getInstance().getCurrentDataset().getRoadNodes()
                    .size());
          }
          if (RoadNetworkMenu.this.logger.isLoggable(Level.FINEST)) {
            RoadNetworkMenu.this.logger
                .finest("Node importance computation for " + this);
          }
          RoadNetworkMenu.this.logger.info("End of the aggregation");
        }
      });
      th.start();
    }

    public AggregationAction() {
      this.putValue(Action.NAME, "Aggregation of analog adjacent sections");
    }
  }

  private class DetectBranchingsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      CrossRoadDetection detect = new CrossRoadDetection();
      detect.detectRoundaboutsAndBranchingCartagen(dataset);
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(CartAGenPlugin.getInstance().getApplication().getMainFrame()
          .getSelectedProjectFrame().getSld());
      for (IRoundAbout round : dataset.getRoundabouts()) {
        pool.addFeatureToGeometryPool(round.getGeom(), Color.RED, 2);
      }
      for (IBranchingCrossroad branch : dataset.getBranchings()) {
        pool.addFeatureToGeometryPool(branch.getGeom(), Color.GREEN, 2);
      }
    }

    public DetectBranchingsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Detects roundabouts and branching crossroads");
      this.putValue(Action.NAME, "Detect roundabouts and branchings");
    }
  }

  private class CollapseRoundaboutsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      for (IRoundAbout roundAbout : dataset.getRoundabouts()) {
        CollapseRoundabout collapseRoundabout = new CollapseRoundabout(150,
            roundAbout);
        collapseRoundabout.collapseToPoint();
      }
    }

    public CollapseRoundaboutsAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Collapse rounadbouts");
      this.putValue(Action.NAME, "Collapse roundabouts");
    }
  }

  private class GaussianSmoothingAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof INetworkSection)) {
              continue;
            }

            RoadNetworkMenu.this.logger
                .info("Application du lissage gaussien au troncon routier "
                    + sel);
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie initiale: "
                  + ((INetworkSection) sel).getGeom());
            }

            INetworkSection road = (INetworkSection) sel;
            double symbolWidth = SectionSymbol.getUsedSymbolWidth(road) / 2;
            double sigma = 75.0 * symbolWidth;

            ILineString filteredGeom = GaussianFilter.gaussianFilter(
                road.getGeom(), sigma,
                GeneralisationSpecifications.getRESOLUTION());
            road.setGeom(filteredGeom);

            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie finale: "
                  + ((INetworkSection) sel).getGeom());
            }
            RoadNetworkMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public GaussianSmoothingAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Gaussian smoothing algorithm on selected roads for tests");
      this.putValue(Action.NAME, "Trigger Gaussian smoothing");
    }
  }

  private class CurvatureFilteringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof INetworkSection)) {
              continue;
            }

            RoadNetworkMenu.this.logger
                .info("Application de lissage de courbure au troncon routier "
                    + sel);
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie initiale: "
                  + ((INetworkSection) sel).getGeom());
            }
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("longueur initiale: "
                  + ((INetworkSection) sel).getGeom().length());
            }

            LineCurvatureSmoothing algo = new LineCurvatureSmoothing(
                (INetworkSection) sel);
            algo.compute(true);

            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("Geometrie finale: "
                  + ((INetworkSection) sel).getGeom());
            }
            if (RoadNetworkMenu.this.logger.isLoggable(Level.CONFIG)) {
              RoadNetworkMenu.this.logger.config("longueur finale: "
                  + ((INetworkSection) sel).getGeom().length());
            }
            RoadNetworkMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public CurvatureFilteringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Curvature filtering algorithm on selected roads for tests");
      this.putValue(Action.NAME, "Trigger Curvature filtering");
    }
  }

}
