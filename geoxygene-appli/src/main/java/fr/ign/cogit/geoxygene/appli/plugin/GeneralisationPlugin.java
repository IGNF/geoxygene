/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingDeletionOverlap;
import fr.ign.cogit.cartagen.algorithms.block.displacement.BuildingDisplacementRandom;
import fr.ign.cogit.cartagen.algorithms.polygon.LiOpenshawSimplification;
import fr.ign.cogit.cartagen.algorithms.polygon.RaposoSimplification;
import fr.ign.cogit.cartagen.algorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.algorithms.polygon.Spinalize;
import fr.ign.cogit.cartagen.algorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.algorithms.section.BendSeriesAlgorithm;
import fr.ign.cogit.cartagen.algorithms.section.BendSeriesContinuousAlgorithm;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.graph.TreeGraph;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.Bend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.section.BendSeries;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.SelectionUtil;

public class GeneralisationPlugin implements GeOxygeneApplicationPlugin {

    private GeOxygeneApplication application = null;

    public GeneralisationPlugin() {
    }

    @Override
    public void initialize(GeOxygeneApplication application) {
        this.application = application;
        JMenu menu = new JMenu("Generalisation");
        JMenu mLineSimplif = new JMenu("Line simplification");
        JMenuItem mDouglas = new JMenuItem("Douglas & Peucker");
        mDouglas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double seuil = Double.valueOf(JOptionPane.showInputDialog("Douglas & Peucker threshold"));
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    IGeometry generalised = Filtering.DouglasPeucker(geom, seuil);
                    if (generalised != null)
                        feat.setGeom(generalised);
                }
            }
        });
        mLineSimplif.add(mDouglas);
        JMenuItem mVisva = new JMenuItem("Visvalingam-Whyatt");
        mVisva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double seuil = Double.valueOf(JOptionPane.showInputDialog("Visvalingam-Whyatt threshold (m²)"));
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    VisvalingamWhyatt algo = new VisvalingamWhyatt(seuil);
                    if (geom instanceof ILineString) {
                        ILineString generalised = algo.simplify((ILineString) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    } else if (geom instanceof IPolygon) {
                        IPolygon generalised = algo.simplify((IPolygon) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    }
                }
            }
        });

        mLineSimplif.add(mVisva);
        JMenuItem mRaposo = new JMenuItem("Raposo simplification");
        mRaposo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double initialScale = Double.valueOf(
                        JOptionPane.showInputDialog("initial scale of simplified data (1000.0 for 1:1k scale)"));
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    RaposoSimplification algo = new RaposoSimplification(true, false, initialScale);
                    if (geom instanceof ILineString) {
                        ILineString generalised = algo.simplify((ILineString) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    } else if (geom instanceof IPolygon) {
                        IPolygon generalised = algo.simplify((IPolygon) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    }
                }
            }
        });
        mLineSimplif.add(mRaposo);
        JMenuItem mLiOpenshaw = new JMenuItem("Li-Openshaw simplification");
        mLiOpenshaw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double cellSize = Double.valueOf(JOptionPane
                        .showInputDialog("size (in meters) of the cell (only one vertex is retained per cell)"));
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    LiOpenshawSimplification algo = new LiOpenshawSimplification(true, cellSize);
                    if (geom instanceof ILineString) {
                        ILineString generalised = algo.simplify((ILineString) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    } else if (geom instanceof IPolygon) {
                        IPolygon generalised = algo.simplify((IPolygon) geom);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    }
                }
            }
        });
        mLineSimplif.add(mLiOpenshaw);
        JMenuItem mSmoothing = new JMenuItem("Gaussian Smoothing");
        mSmoothing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                double seuil = Double.valueOf(JOptionPane.showInputDialog("Gaussian sigma threshold"));
                double step = Double.valueOf(JOptionPane.showInputDialog("line subsampling step"));
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    if (geom instanceof ILineString) {
                        IGeometry generalised = GaussianFilter.gaussianFilter((ILineString) geom, seuil, step);
                        if (generalised != null)
                            feat.setGeom(generalised);
                    } else if (geom instanceof IPolygon) {
                        ILineString generalised = GaussianFilter.gaussianFilter(((IPolygon) geom).exteriorLineString(),
                                seuil, step);
                        if (generalised != null)
                            feat.setGeom(GeometryEngine.getFactory().createIPolygon(generalised));
                    }
                }
            }
        });
        mLineSimplif.add(mSmoothing);

        // displacement menu
        JMenu mDisplacement = new JMenu("Displacement");
        JMenuItem mRandom = new JMenuItem("Random displacement of overlapping polygon features");
        mRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                List<IFeature> polygons = new ArrayList<>();
                Set<IFeature> lines = new HashSet<>();
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    if (geom instanceof IPolygon)
                        polygons.add(feat);
                    else if (geom instanceof ILineString)
                        lines.add(feat);
                }
                BuildingDisplacementRandom.computeFeats(polygons, lines);
            }
        });
        mDisplacement.add(mRandom);

        // elimination menu
        JMenu mElimination = new JMenu("Contextual Deletion");
        JMenuItem mOverlaps = new JMenuItem("Contextual deletion based on overlapping polygon features");
        mOverlaps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                List<IFeature> polygons = new ArrayList<>();
                Set<IFeature> lines = new HashSet<>();
                for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                    IGeometry geom = feat.getGeom();
                    if (geom instanceof IPolygon)
                        polygons.add(feat);
                    else if (geom instanceof ILineString)
                        lines.add(feat);
                }
                BuildingDeletionOverlap algo = new BuildingDeletionOverlap(0.3);
                List<IFeature> toDelete = algo.compute(polygons);
                for (IFeature feat : toDelete) {
                    feat.setDeleted(true);
                    if (feat instanceof IGeneObj)
                        ((IGeneObj) feat).eliminate();
                    Layer layer = application.getMainFrame().getSelectedProjectFrame().getLayerFromFeature(feat);
                    layer.getFeatureCollection().remove(feat);
                }
            }
        });
        mElimination.add(mOverlaps);

        // Collapse menu
        JMenu mCollapse = new JMenu("Polygon Collapse");
        JMenuItem mStraightSke = new JMenuItem(new StraightSkeAction());
        JMenuItem mTinSke = new JMenuItem(new TinSkeAction());
        JMenuItem mSpinalize = new JMenuItem(new SpinalizeAction());
        mCollapse.add(mStraightSke);
        mCollapse.add(mTinSke);
        mCollapse.add(mSpinalize);

        // Bend generalization
        JMenu mBendSeries = new JMenu("Bend Series");
        JMenuItem mContinuousAccordion = new JMenuItem(new ContinuousAccordionAction());
        mBendSeries.add(mContinuousAccordion);
        JMenuItem mAccordion = new JMenuItem(new AccordionAction());
        mBendSeries.add(mAccordion);
        JMenuItem mContinuousMaxBreak = new JMenuItem(new ContinuousMaxBreakAction());
        mBendSeries.add(mContinuousMaxBreak);

        menu.add(mLineSimplif);
        menu.add(mElimination);
        menu.add(mDisplacement);
        menu.add(mCollapse);
        menu.add(mBendSeries);
    }

    class StraightSkeAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public StraightSkeAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger the straight skeleton collapse algorithm on selected polygons");
            this.putValue(Action.NAME, "Straight Skeleton");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof IPolygon))
                    continue;
                Set<ILineSegment> segments = Skeletonize.skeletonizeStraightSkeleton((IPolygon) geom);
                Set<ILineString> skeleton = Skeletonize.connectSkeletonToPolygon(segments, (IPolygon) geom);
                for (ILineString generalised : skeleton) {
                    if (generalised != null) {
                        fc.add(new DefaultFeature(generalised));
                    }
                }
            }
            // display the output in a new layer
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "skeleton", null);
        }
    }

    class TinSkeAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public TinSkeAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger the medial axis skeleton collapse algorithm on selected polygons");
            this.putValue(Action.NAME, "TIN based Skeleton");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            double densStep = Double.valueOf(JOptionPane.showInputDialog("add a vertex every x meters"));
            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof IPolygon))
                    continue;
                TreeGraph skeleton = Skeletonize.skeletonizeTINGraph((IPolygon) geom, densStep);

                // display the output in the geometry pool
                for (IEdge edge : skeleton.getEdges())
                    fc.add(new DefaultFeature(edge.getGeom()));
            }
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "skeleton_medial_axis", null);
        }
    }

    class SpinalizeAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public SpinalizeAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger the straight skeleton collapse algorithm on selected polygons");
            this.putValue(Action.NAME, "Straight Skeleton");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<IPolygon> listPoly = new ArrayList<>();
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            double overSample = Double.valueOf(JOptionPane.showInputDialog("add a vertex every x meters"));
            double lengthMin = Double
                    .valueOf(JOptionPane.showInputDialog("minimum length (in m) to keep a segment in the spine"));
            int answer = JOptionPane.showConfirmDialog(null, "Do you want to remove holes from the polygons?");
            boolean removeHoles = answer == JOptionPane.YES_OPTION;

            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof IPolygon))
                    continue;
                listPoly.add((IPolygon) geom);
            }
            List<ILineString> segments = Spinalize.spinalize(listPoly, lengthMin, overSample, removeHoles);

            for (ILineString generalised : segments) {
                if (generalised != null) {
                    // display the output in the geometry pool
                    fc.add(new DefaultFeature(generalised));
                }
            }
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "spine", null);

        }

    }

    class ContinuousAccordionAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ContinuousAccordionAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger a continuous version of Accordion algorithm on a selected line with bend series");
            this.putValue(Action.NAME, "Continuous Accordion");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof ILineString))
                    continue;
                BendSeries bendSeries = new BendSeries((ILineString) geom);
                BendSeriesContinuousAlgorithm algo = new BendSeriesContinuousAlgorithm(bendSeries, 20.0, 60.0);
                List<ILineString> generalized = algo.accordion(5);

                // display the output in a new layer
                for (ILineString line : generalized)
                    fc.add(new DefaultFeature(line));
            }
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "generalized_accordion", null);
        }
    }

    class ContinuousMaxBreakAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public ContinuousMaxBreakAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger a continuous version of max break algorithm on all the bends of a selected line with bend series");
            this.putValue(Action.NAME, "Continuous Max Break");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof ILineString))
                    continue;
                BendSeries bendSeries = new BendSeries((ILineString) geom);
                BendSeriesContinuousAlgorithm algo = new BendSeriesContinuousAlgorithm(bendSeries, 20.0, 60.0);
                for (Bend bend : bendSeries.getBends()) {
                    List<ILineString> generalized = algo.continuousMaxBreak(5, bend);

                    // display the output in the geometry pool
                    for (ILineString line : generalized)
                        fc.add(new DefaultFeature(line));
                }
            }
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "generalized_max_break", null);
        }
    }

    class AccordionAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public AccordionAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Trigger the Accordion algorithm on a selected line with bend series");
            this.putValue(Action.NAME, "Accordion");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
            for (IFeature feat : SelectionUtil.getSelectedObjects(application)) {
                IGeometry geom = feat.getGeom();
                if (!(geom instanceof ILineString))
                    continue;
                BendSeries bendSeries = new BendSeries((ILineString) geom);
                BendSeriesAlgorithm algo = new BendSeriesAlgorithm(bendSeries, 0.0, 60.0);
                ILineString generalized = algo.accordion();

                // display the output in a new layer
                fc.add(new DefaultFeature(generalized));
            }
            ProjectFrame project = application.getMainFrame().getSelectedProjectFrame();
            project.addUserLayer(fc, "generalized_accordion", null);
        }
    }

}
