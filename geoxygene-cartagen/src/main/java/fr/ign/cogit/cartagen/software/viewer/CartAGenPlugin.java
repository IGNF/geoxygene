/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.GeneralisationMenuComplement;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.ConfigMenuComponent;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.GeomPoolFrame;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Triangulation plugin.
 * @author Julien Perret
 */
public class CartAGenPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(Triangulation.class.getName());

  private GeOxygeneApplication application = null;
  // config
  /**
     */
  public ConfigMenuComponent menuConfig = new ConfigMenuComponent(
      "CartAGen-Config");

  // Geometries pool
  /**
     */
  private JMenu menuGeomPool = new JMenu("Geometries pool");
  /**
     */
  public JCheckBoxMenuItem mGeomPoolVisible = new JCheckBoxMenuItem("Visible");
  /**
   */
  public JMenuItem mGeomPoolManagement = new JMenuItem("Management");
  /**
     */
  private JMenuItem mGeomPoolEmpty = new JMenuItem("Empty");
  /**
     */
  private JMenuItem mGeomPoolAddObjects = new JMenuItem(
      "Add selected objects geometry");
  /**
     */
  public JCheckBoxMenuItem mGeomPoolDrawSegments = new JCheckBoxMenuItem(
      "Draw segments");
  public IDirectPositionList mGeomPoolDrawSegmentsCoords = new DirectPositionList();

  /**
   * Initialize the plugin.
   * @param application the application
   */
  @Override
  public final void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenuBar menuBar = application.getFrame().getJMenuBar();
    // geometries pool menu
    this.menuGeomPool
        .setToolTipText("geometries pool: a set of geometries usefull to display");

    this.mGeomPoolVisible.setSelected(true);
    this.menuGeomPool.add(this.mGeomPoolVisible);

    this.mGeomPoolManagement.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeomPoolFrame.getInstance().setVisible(true);
      }
    });
    this.menuGeomPool.add(this.mGeomPoolManagement);

    this.mGeomPoolEmpty.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // empty the geometries pool layer
        for (CartAGenProjectFrame frame : ((CartAGenFrame) application
            .getFrame()).getAllCartProjectFrames()) {
          frame.emptyGeometriesPool();
        }
      }
    });
    this.menuGeomPool.add(this.mGeomPoolEmpty);

    this.mGeomPoolAddObjects.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (IFeature feat : CartagenApplication.getInstance().getFrame()
            .getVisuPanel().selectedObjects) {
          if (!(feat instanceof IGeneObj)) {
            continue;
          }
          CartagenApplication.getInstance().getFrame().getLayerManager()
              .addToGeometriesPool(((IGeneObj) feat).getGeom());
        }
      }
    });
    this.menuGeomPool.add(this.mGeomPoolAddObjects);

    this.mGeomPoolDrawSegments.setSelected(false);
    this.menuGeomPool.add(this.mGeomPoolDrawSegments);

    this.menuConfig.setFont(menuBar.getFont());
    menuBar.add(this.menuConfig, menuBar.getMenuCount() - 1);
    this.menuGeomPool.setFont(menuBar.getFont());
    menuBar.add(this.menuGeomPool, menuBar.getMenuCount() - 1);

    // ajout contenu generalisation au menu
    GeneralisationMenuComplement.getInstance().add(menuBar);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

  }
}
