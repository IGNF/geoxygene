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
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.core.GeneralisationLegend;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.urban.ITown;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.ProgressFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayerFactory;

public class TownMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private JMenuItem mVilleCreer = new JMenuItem(new CreateTownAction());
  private JMenuItem mTownLinks = new JMenuItem(new TownLinksAction());
  public JCheckBoxMenuItem mIdVilleVoir = new JCheckBoxMenuItem("See id");
  public JCheckBoxMenuItem mVoirAireVille = new JCheckBoxMenuItem(
      "See town area");
  private GeOxygeneApplication application;

  public TownMenu(String title, GeOxygeneApplication application) {
    super(title);

    this.application = application;
    this.add(this.mVilleCreer);
    this.add(this.mTownLinks);

    this.addSeparator();

    this.add(this.mIdVilleVoir);
    this.add(this.mVoirAireVille);

    this.addSeparator();

    this.add(new JMenuItem(new IsTownCentreAction()));
  }

  public GeOxygeneApplication getApplication() {
    return application;
  }

  public void setApplication(GeOxygeneApplication application) {
    this.application = application;
  }

  private class CreateTownAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          CartAGenDataSet dataset = CartAGenDoc.getInstance()
              .getCurrentDataset();
          ProgressFrame progressFrame = new ProgressFrame(
              "Enrichement in progress...", true);
          progressFrame.setVisible(true);
          progressFrame.setTextAndValue("Urban enrichment in progress", 0);
          UrbanEnrichment.buildTowns(dataset, false,
              dataset.getCartAGenDB().getGeneObjImpl().getCreationFactory());
          progressFrame.setTextAndValue("Urban enrichment in progress", 100);
          progressFrame.setVisible(false);
          progressFrame = null;

          ProjectFrame frame = application.getMainFrame()
              .getSelectedProjectFrame();
          FeatureType ft = new FeatureType();
          ft.setNomClasse(CartAGenDataSet.BLOCKS_POP);
          ft.setGeometryType(IPolygon.class);
          dataset.getBlocks().setFeatureType(ft);
          FeatureType ft2 = new FeatureType();
          ft2.setNomClasse(CartAGenDataSet.TOWNS_POP);
          ft2.setGeometryType(IPolygon.class);
          dataset.getTowns().setFeatureType(ft2);

          NamedLayerFactory factory = new NamedLayerFactory();
          factory.setModel(frame.getSld());
          factory.setName(CartAGenDataSet.BLOCKS_POP);

          factory.setGeometryType(IPolygon.class);
          Layer blockLayer = factory.createLayer();
          factory.setName(CartAGenDataSet.TOWNS_POP);
          Layer townLayer = factory.createLayer();
          frame.getSld().add(blockLayer);
          frame.getSld().add(townLayer);
        }
      });
      th.start();
    }

    public CreateTownAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create town agent with buffers around buildings (Boffet, 2001)");
      this.putValue(Action.NAME, "Create town with buildings");
    }
  }

  private class TownLinksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // first build the town/block link
      for (ITown town : CartAGenDoc.getInstance().getCurrentDataset()
          .getTowns()) {
        IPolygon geom = town.getGeom();
        Collection<IUrbanBlock> blocks = CartAGenDoc.getInstance()
            .getCurrentDataset().getBlocks().select(geom);
        town.setTownBlocks(new FT_FeatureCollection<IUrbanBlock>(blocks));
      }
    }

    public TownLinksAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          I18N.getString("TownMenu.ttipTownLinks"));
      this.putValue(Action.NAME, I18N.getString("TownMenu.lblTownLinks"));
    }
  }

  private class IsTownCentreAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(CartAGenPlugin.getInstance().getApplication().getMainFrame()
          .getSelectedProjectFrame().getSld());
      for (IFeature sel : SelectionUtil
          .getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
        if (!(sel instanceof IUrbanBlock)) {
          continue;
        }
        IUrbanBlock block = (IUrbanBlock) sel;
        ITown town = block.getTown();
        if (town == null) {
          System.out.println("null");
          continue;
        }
        boolean townCentre = town.isTownCentre(block);
        if (townCentre) {
          System.out.println(block.toString() + " is town centre");
          pool.addFeatureToGeometryPool(block.getGeom(),
              GeneralisationLegend.ILOTS_GRISES_COULEUR, 4);
        } else {
          System.out.println(block.toString() + " is not town centre");
        }
      }
    }

    public IsTownCentreAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger the multicriteria decision technique to check if a block is part of town centre");
      this.putValue(Action.NAME, "Is block a town centre?");
    }
  }

}
