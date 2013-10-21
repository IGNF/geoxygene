/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.geompool;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.GeomPoolFrame;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayer;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * Extra menu that contains utility functions of CartAGen.
 * @author GTouya
 * 
 */
public class GeometryPoolMenu extends JMenu {

  static Logger logger = Logger.getLogger(GeometryPoolMenu.class.getName());

  /**
   * The Geometry Pool layer
   */
  private NamedLayer geomPoolLayer;
  private Color defaultColor = Color.RED;

  // Geometries pool
  /**
     */
  public JCheckBoxMenuItem mGeomPoolVisible = new JCheckBoxMenuItem(
      new GeomPoolVisibleAction());
  /**
   */
  public JMenuItem mGeomPoolManagement = new JMenuItem(
      new GeomPoolManagementAction());
  /**
     */
  private JMenuItem mGeomPoolEmpty = new JMenuItem(new GeomPoolEmptyAction());
  /**
     */
  private JMenuItem mGeomPoolAddObjects = new JMenuItem(
      new GeomPoolAddObjectsAction());
  /**
     */
  public JCheckBoxMenuItem mGeomPoolDrawSegments = new JCheckBoxMenuItem(
      "Draw segments");
  public IDirectPositionList mGeomPoolDrawSegmentsCoords = new DirectPositionList();

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public GeometryPoolMenu(String title) {
    super(title);
    // geometries pool menu
    this.setToolTipText("geometries pool: a set of geometries usefull to display");
    this.geomPoolLayer = new NamedLayer();
    this.geomPoolLayer.setName(CartAGenDataSet.GEOM_POOL);
    this.geomPoolLayer.getStyles().add(new UserStyle());

    this.mGeomPoolVisible.setSelected(false);
    this.add(this.mGeomPoolVisible);
    this.add(this.mGeomPoolManagement);

    this.add(this.mGeomPoolEmpty);
    this.add(this.mGeomPoolAddObjects);

    this.mGeomPoolDrawSegments.setSelected(false);
    this.add(this.mGeomPoolDrawSegments);
  }

  /**
   * Action that enables the anti-aliasing in the CartAGen display.
   * @author GTouya
   * 
   */
  class GeomPoolManagementAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      GeomPoolFrame.getInstance().setVisible(true);
    }

    public GeomPoolManagementAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Show the Geometry Pool management frame");
      putValue(Action.NAME, "Management");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class GeomPoolAddObjectsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      for (IFeature feat : CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
          .getSelectedFeatures()) {
        if (!(feat instanceof IGeneObj)) {
          continue;
        }
        CartAGenDoc.getInstance().getCurrentDataset().getGeometryPool()
            .addFeatureToGeometryPool(feat, defaultColor);
      }
      CartAGenPlugin.getInstance().getApplication().getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel().validate();
    }

    public GeomPoolAddObjectsAction() {
      putValue(Action.NAME, "Add selected objects geometry");
    }
  }

  /**
   * Empty the geometry pool.
   * @author GTouya
   * 
   */
  class GeomPoolEmptyAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDoc.getInstance().getCurrentDataset().getGeometryPoolPop()
          .clear();
    }

    public GeomPoolEmptyAction() {
      putValue(Action.SHORT_DESCRIPTION, "Empty the Geometry Pool");
      putValue(Action.NAME, "Empty");
    }
  }

  /**
   * Make the Geometry Pool layer visible or not.
   * @author GTouya
   * 
   */
  class GeomPoolVisibleAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      geomPoolLayer.setSld(CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame().getSld());
      if (geomPoolLayer != null) {
        geomPoolLayer.setVisible(mGeomPoolVisible.isSelected());
        ProjectFrame frame = CartAGenPlugin.getInstance().getApplication()
            .getMainFrame().getSelectedProjectFrame();
        if (mGeomPoolVisible.isSelected()) {
          frame.addLayer(geomPoolLayer);
        } else {
          List<Layer> toRemove = new ArrayList<Layer>();
          toRemove.add(geomPoolLayer);
          frame.removeLayers(toRemove);
        }
      }
    }

    public GeomPoolVisibleAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Make the Geometry Pool visible or not");
      putValue(Action.NAME, "Visible");
    }
  }

}
