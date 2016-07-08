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
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.genealgorithms.network.RiverNetworkSelection;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayerFactory;

public class HydroNetworkMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final Logger logger = Logger
      .getLogger(HydroNetworkMenu.class.getName());

  private final JMenuItem mResHydroEnrich = new JMenuItem(
      new EnrichHydroNetAction());
  private final JMenuItem mResHydroSelect = new JMenuItem(
      new SelectSectionsAction());
  public JCheckBoxMenuItem mVoirTauxSuperpositionRoutier = new JCheckBoxMenuItem(
      "Voir taux superposition routier");
  public JCheckBoxMenuItem mIdHydroVoir = new JCheckBoxMenuItem("Display id");
  private final JMenuItem mNetworkSelection = new JMenuItem(
      new NetworkSelectionAction());

  public HydroNetworkMenu(String title) {
    super(title);

    this.add(this.mResHydroEnrich);
    this.add(this.mResHydroSelect);

    this.addSeparator();
    this.add(this.mNetworkSelection);
    this.addSeparator();

    this.add(this.mIdHydroVoir);
    this.add(this.mVoirTauxSuperpositionRoutier);

  }

  private class EnrichHydroNetAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      HydroNetworkMenu.this.logger.info("Enrichment of "
          + CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork());
      INetwork net = CartAGenDoc.getInstance().getCurrentDataset()
          .getHydroNetwork();
      if (net.getSections().size() == 0) {
        IFeatureCollection<INetworkSection> sections = new FT_FeatureCollection<INetworkSection>();
        for (IWaterLine w : CartAGenDoc.getInstance().getCurrentDataset()
            .getWaterLines()) {
          sections.add(w);
        }
        net.setSections(sections);
      }
      NetworkEnrichment.buildTopology(
          CartAGenDoc.getInstance().getCurrentDataset(), net, false);
    }

    public EnrichHydroNetAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Enrichment of the hydro network");
      this.putValue(Action.NAME, "Enrichment");
    }
  }

  private class SelectSectionsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      HydroNetworkMenu.this.logger.info("Selection of "
          + CartAGenDoc.getInstance().getCurrentDataset().getHydroNetwork());
      for (IWaterLine section : CartAGenDoc.getInstance().getCurrentDataset()
          .getWaterLines()) {
        SelectionUtil.addFeatureToSelection(
            CartAGenPlugin.getInstance().getApplication(), section);
      }
    }

    public SelectSectionsAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Select all the sections of the hydro network");
      this.putValue(Action.NAME, "Select all sections");
    }
  }

  private class NetworkSelectionAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {

      RiverNetworkSelection selection = new RiverNetworkSelection(3, 5000.0,
          50000.0, true);
      selection.selection();
      // add the strokes as a new layer
      IPopulation<Stroke> pop = new Population<Stroke>();
      pop.setNom("riverStrokes");
      pop.addAll(selection.getNet().getStrokes());
      CartAGenDoc.getInstance().getCurrentDataset().addPopulation(pop);
      FeatureType ftGeom = new FeatureType();
      ftGeom.setGeometryType(ILineString.class);
      CartAGenDoc.getInstance().getCurrentDataset()
          .getPopulation("riverStrokes").setFeatureType(ftGeom);
      ProjectFrame frame = CartAGenPlugin.getInstance().getApplication()
          .getMainFrame().getSelectedProjectFrame();
      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName("riverStrokes");
      factory.setGeometryType(ILineString.class);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public NetworkSelectionAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "hydro network selection based on strokes and Horton ordering");
      this.putValue(Action.NAME, "hydro network selection");
    }
  }
}
