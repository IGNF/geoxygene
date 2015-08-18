/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.continuous.BasicMorphing;
import fr.ign.cogit.cartagen.continuous.optcor.OptCorMorphing;
import fr.ign.cogit.cartagen.genealgorithms.polygon.VisvalingamWhyatt;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;

/**
 * Extra menu that contains utility functions of CartAGen.
 * 
 * @author GTouya
 * 
 */
public class ContinuousGUIComponent extends JMenu {

    private GeOxygeneApplication appli;
    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    public ContinuousGUIComponent(GeOxygeneApplication appli, String title) {
        super(title);
        this.appli = appli;
        JMenu morphingMenu = new JMenu("Morphing");
        this.add(morphingMenu);
        morphingMenu.add(new JMenuItem(new BasicMorphingAction()));
        morphingMenu.add(new JMenuItem(new OptCorMorphingAction()));
    }

    /**
     * Test the basic morphing on the selected object and a simplified version
     * of the object. Morphed and simplified geometries are displayed in the
     * geometry pool.
     * 
     * @author GTouya
     * 
     */
    class BasicMorphingAction extends AbstractAction {

        /**
     * 
     */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            CartAGenDataSet dataset = CartAGenDoc.getInstance()
                    .getCurrentDataset();
            GeometryPool pool = dataset.getGeometryPool();
            pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

            IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
            VisvalingamWhyatt algo = new VisvalingamWhyatt(1000.0);
            if (feat.getGeom() instanceof IPolygon) {
                IPolygon simplified = algo.simplify((IPolygon) feat.getGeom());
                pool.addFeatureToGeometryPool(simplified.exteriorLineString(),
                        Color.BLUE, 3);
                BasicMorphing morphing = new BasicMorphing(
                        ((IPolygon) feat.getGeom()).exteriorLineString(),
                        simplified.exteriorLineString());
                ILineString morph1 = (ILineString) morphing
                        .continuousGeneralisation(0.25);
                ILineString morph2 = (ILineString) morphing
                        .continuousGeneralisation(0.5);
                ILineString morph3 = (ILineString) morphing
                        .continuousGeneralisation(0.75);
                pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
                pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
                pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
            } else if (feat.getGeom() instanceof ILineString) {
                ILineString simplified = algo.simplify((ILineString) feat
                        .getGeom());
                pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);
                BasicMorphing morphing = new BasicMorphing(
                        ((ILineString) feat.getGeom()), simplified);
                ILineString morph1 = (ILineString) morphing
                        .continuousGeneralisation(0.25);

                ILineString morph2 = (ILineString) morphing
                        .continuousGeneralisation(0.5);
                ILineString morph3 = (ILineString) morphing
                        .continuousGeneralisation(0.75);

                pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
                pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
                pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
            }
        }

        public BasicMorphingAction() {
            putValue(Action.NAME, "Basic morphing on selection");
        }
    }

    /**
     * Test the {@link OptCorMorphing} on the selected object and a simplified
     * version of the object. Morphed and simplified geometries are displayed in
     * the geometry pool.
     * 
     * @author GTouya
     * 
     */
    class OptCorMorphingAction extends AbstractAction {

        /**
     * 
     */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            CartAGenDataSet dataset = CartAGenDoc.getInstance()
                    .getCurrentDataset();
            GeometryPool pool = dataset.getGeometryPool();
            pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

            IFeature feat = SelectionUtil.getFirstSelectedObject(appli);
            VisvalingamWhyatt algo = new VisvalingamWhyatt(1000.0);
            if (feat.getGeom() instanceof IPolygon) {
                IPolygon simplified = algo.simplify((IPolygon) feat.getGeom());
                pool.addFeatureToGeometryPool(simplified.exteriorLineString(),
                        Color.BLUE, 3);
                OptCorMorphing morphing = new OptCorMorphing(
                        ((IPolygon) feat.getGeom()).exteriorLineString(),
                        simplified.exteriorLineString());
                ILineString morph1 = (ILineString) morphing
                        .continuousGeneralisation(0.25);
                ILineString morph2 = (ILineString) morphing
                        .continuousGeneralisation(0.5);
                ILineString morph3 = (ILineString) morphing
                        .continuousGeneralisation(0.75);
                pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
                pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
                pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
            } else if (feat.getGeom() instanceof ILineString) {
                ILineString simplified = algo.simplify((ILineString) feat
                        .getGeom());
                pool.addFeatureToGeometryPool(simplified, Color.BLUE, 3);
                OptCorMorphing morphing = new OptCorMorphing(
                        ((ILineString) feat.getGeom()), simplified);
                ILineString morph1 = (ILineString) morphing
                        .continuousGeneralisation(0.25);

                ILineString morph2 = (ILineString) morphing
                        .continuousGeneralisation(0.5);
                ILineString morph3 = (ILineString) morphing
                        .continuousGeneralisation(0.75);

                pool.addFeatureToGeometryPool(morph1, Color.YELLOW, 3);
                pool.addFeatureToGeometryPool(morph2, Color.ORANGE, 3);
                pool.addFeatureToGeometryPool(morph3, Color.RED, 3);
            }
        }

        public OptCorMorphingAction() {
            putValue(Action.NAME, "OPTCOR morphing on selection");
        }
    }

}
