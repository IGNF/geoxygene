/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.block.BuildingDeletionOverlap;
import fr.ign.cogit.cartagen.genealgorithms.block.BuildingDisplacementRandom;
import fr.ign.cogit.cartagen.genealgorithms.polygon.RaposoSimplification;
import fr.ign.cogit.cartagen.genealgorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * A menu for generic algorithms, i.e. algorithms that apply to standard
 * features (i.e. not IGeneObj instances).
 * @author GTouya
 *
 */
public class AlgorithmsMenu extends JMenu {

  /****/
  private static final long serialVersionUID = 1L;

  public AlgorithmsMenu() {
    super("Algorithms");

    JMenu mLineSimplif = new JMenu("Line simplification");
    JMenuItem mDouglas = new JMenuItem("Douglas & Peucker");
    mDouglas.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeOxygeneApplication appli = CartAGenPlugin.getInstance()
            .getApplication();
        double seuil = Double.valueOf(
            JOptionPane.showInputDialog("Douglas & Peucker threshold"));
        for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {
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
        GeOxygeneApplication appli = CartAGenPlugin.getInstance()
            .getApplication();
        double seuil = Double.valueOf(
            JOptionPane.showInputDialog("Visvalingam-Whyatt threshold (m²)"));
        for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {
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
        GeOxygeneApplication appli = CartAGenPlugin.getInstance()
            .getApplication();
        double initialScale = Double.valueOf(JOptionPane.showInputDialog(
            "initial scale of simplified data (1000.0 for 1:1k scale)"));
        for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {
          IGeometry geom = feat.getGeom();
          RaposoSimplification algo = new RaposoSimplification(true, false,
              initialScale);
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

    // displacement menu
    JMenu mDisplacement = new JMenu("Displacement");
    JMenuItem mRandom = new JMenuItem(
        "Random displacement of overlapping polygon features");
    mRandom.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeOxygeneApplication appli = CartAGenPlugin.getInstance()
            .getApplication();
        List<IFeature> polygons = new ArrayList<>();
        Set<IFeature> lines = new HashSet<>();
        for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {
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
    JMenuItem mOverlaps = new JMenuItem(
        "Contextual deletion based on overlapping polygon features");
    mOverlaps.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeOxygeneApplication appli = CartAGenPlugin.getInstance()
            .getApplication();
        List<IFeature> polygons = new ArrayList<>();
        Set<IFeature> lines = new HashSet<>();
        for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {
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
          Layer layer = CartAGenPlugin.getInstance().getApplication()
              .getMainFrame().getSelectedProjectFrame()
              .getLayerFromFeature(feat);
          layer.getFeatureCollection().remove(feat);
        }
      }
    });
    mElimination.add(mOverlaps);

    this.add(mLineSimplif);
    this.add(mElimination);
    this.add(mDisplacement);
  }

}
