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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.algorithms.block.deletion.BuildingsDeletionProximityMultiCriterion;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.geompool.GeometryPool;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.spatialanalysis.urban.UrbanEnrichment;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.graphe.IEdge;
import fr.ign.cogit.geoxygene.contrib.graphe.IGraphLinkableFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.NamedLayerFactory;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class BlockMenu extends JMenu {

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private Logger logger = Logger.getLogger(BlockMenu.class.getName());

  private JMenuItem mCreateBlocksArea = new JMenuItem(
      new CreateBlocksInAreaAction());
  private JMenuItem mIlotSelectionnerTous = new JMenuItem(new SelectAction());
  private JMenuItem mBlockLinks = new JMenuItem(new RestoreBlockLinksAction());

  public JCheckBoxMenuItem mIdIlotVoir = new JCheckBoxMenuItem("Display id");

  public JCheckBoxMenuItem mVoirCoutSuppressionBatiments = new JCheckBoxMenuItem(
      "Voir cout suppression batiments");

  public JCheckBoxMenuItem mVoirDensiteInitiale = new JCheckBoxMenuItem(
      "Voir densite initiale");
  public JCheckBoxMenuItem mVoirDensiteSimulee = new JCheckBoxMenuItem(
      "Voir densite simulee");
  public JCheckBoxMenuItem mVoirSatisfactionDensite = new JCheckBoxMenuItem(
      "Voir satisfaction densite");
  public JCheckBoxMenuItem mVoirTauxSuperpositionBatiments = new JCheckBoxMenuItem(
      "Voir moyenne taux superposition batiments");
  public JCheckBoxMenuItem mVoirSatisfactionProximite = new JCheckBoxMenuItem(
      "Voir satisfaction proximite");

  public BlockMenu(String title) {
    super(title);

    this.add(this.mCreateBlocksArea);
    this.add(this.mIlotSelectionnerTous);
    this.add(this.mBlockLinks);

    this.addSeparator();

    this.add(this.mIdIlotVoir);
    this.add(new JMenuItem(new ShowProximityInPoolAction()));

    this.addSeparator();

    this.add(this.mVoirCoutSuppressionBatiments);
    this.add(new JMenuItem(new EliminationRankAction()));

    this.addSeparator();

    this.add(this.mVoirDensiteInitiale);
    this.add(this.mVoirDensiteSimulee);

    this.addSeparator();

    this.add(this.mVoirTauxSuperpositionBatiments);
    this.add(this.mVoirSatisfactionProximite);

  }

  private class SelectAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      for (IUrbanBlock ai : CartAGenDoc.getInstance().getCurrentDataset()
          .getBlocks()) {
        SelectionUtil.addFeatureToSelection(
            CartAGenPlugin.getInstance().getApplication(), ai);
      }
    }

    public SelectAction() {
      this.putValue(Action.NAME, "Select all blocks");
    }
  }

  private class CreateBlocksInAreaAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      for (IFeature feat : SelectionUtil.getSelectedObjects(appli)) {

        if (feat.getGeom() instanceof IPolygon) {
          IPolygon area = (IPolygon) feat.getGeom();

          // create the topological map
          // remplit carte topo avec les troncons
          CarteTopo carteTopo = new CarteTopo("cartetopo");
          for (INetwork res : UrbanEnrichment.getStructuringNetworks(dataset)) {
            if (res.getSections().size() > 0) {
              IFeatureCollection<IFeature> sections = new FT_FeatureCollection<>();
              sections.addAll(res.getSections().select(area.getEnvelope()));
              carteTopo.importClasseGeo(sections, true);
            }
          }

          // remplit carte topo avec limites de villes
          IFeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
          DefaultFeature contour = new DefaultFeature();
          contour.setGeom(area.exteriorLineString());
          contours.add(contour);
          carteTopo.importClasseGeo(contours, true);
          // Set infinite face to true for face creation, because of a bug if
          // not
          // set.
          // Intended to be removed when the bug is corrected
          carteTopo.setBuildInfiniteFace(true);
          carteTopo.creeNoeudsManquants(1.0);
          carteTopo.fusionNoeuds(1.0);
          carteTopo.filtreArcsDoublons();
          carteTopo.rendPlanaire(1.0);
          carteTopo.fusionNoeuds(1.0);
          carteTopo.filtreArcsDoublons();
          carteTopo.creeTopologieFaces();
          carteTopo.getPopFaces().initSpatialIndex(Tiling.class, false);

          UrbanEnrichment.buildBlocksInArea(area, dataset, carteTopo, false);

        }
      }
      FeatureType ft = new FeatureType();
      ft.setNomClasse(CartAGenDataSet.BLOCKS_POP);
      ft.setGeometryType(IPolygon.class);
      dataset.getBlocks().setFeatureType(ft);

      ProjectFrame frame = appli.getMainFrame().getSelectedProjectFrame();
      NamedLayerFactory factory = new NamedLayerFactory();
      factory.setModel(frame.getSld());
      factory.setName(CartAGenDataSet.BLOCKS_POP);

      factory.setGeometryType(IPolygon.class);
      Layer layer = factory.createLayer();
      frame.getSld().add(layer);
    }

    public CreateBlocksInAreaAction() {
      this.putValue(Action.NAME, "Create blocks in the selected area");
    }
  }

  private class EliminationRankAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      for (IFeature obj : SelectionUtil.getSelectedObjects(
          CartAGenPlugin.getInstance().getApplication(),
          CartAGenDataSet.BLOCKS_POP)) {
        List<IUrbanElement> rank = BuildingsDeletionProximityMultiCriterion
            .compute((IUrbanBlock) obj);
        for (int j = 1; j <= rank.size(); j++)
          System.out.println(j + ". " + rank.get(j));
      }
    }

    public EliminationRankAction() {
      this.putValue(Action.NAME, "Show building elimination rank");
    }
  }

  private class ShowProximityInPoolAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      for (IFeature obj : SelectionUtil.getSelectedObjects(
          CartAGenPlugin.getInstance().getApplication(),
          CartAGenDataSet.BLOCKS_POP)) {
        for (IUrbanElement ue : ((IUrbanBlock) obj).getUrbanElements()) {
          for (IEdge edge : ((IGraphLinkableFeature) ue)
              .getProximitySegments()) {
            pool.addFeatureToGeometryPool(edge.getGeom(), Color.PINK, 2);
          }
        }
      }
    }

    public ShowProximityInPoolAction() {
      this.putValue(Action.NAME,
          "Show proximity triangulation in geometry pool");
    }
  }

  private class RestoreBlockLinksAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      for (IFeature obj : SelectionUtil.getSelectedObjects(
          CartAGenPlugin.getInstance().getApplication(),
          CartAGenDataSet.BLOCKS_POP)) {
        // first, the link to urban elements components
        Collection<IBuilding> components = CartAGenDoc.getInstance()
            .getCurrentDataset().getBuildings().select(obj.getGeom());
        for (IBuilding urbanElement : components) {
          // batiment totallement inclu dans ilot
          if (obj.getGeom().contains(urbanElement.getGeom())) {
            ((IUrbanBlock) obj).addUrbanElement(urbanElement);
            continue;
          }

          // le batiment n'est pas totalement dans l'ilot. calcul de la part du
          // batiment dans l'ilot
          double taux = urbanElement.getGeom().intersection(obj.getGeom())
              .area() / (urbanElement.getGeom().area());
          System.out.println(taux);
          // si ce taux est suffisament grand, le batiment est considere comme
          // appartenant a l'ilot
          if (taux > 0.6) {
            ((IUrbanBlock) obj).addUrbanElement(urbanElement);
            continue;
          }
        }

        // then, the link with surrounding roads
        Collection<INetworkSection> surrounding = new HashSet<>();
        surrounding.addAll(CartAGenDoc.getInstance().getCurrentDataset()
            .getRoads().select(obj.getGeom()));
        surrounding.addAll(CartAGenDoc.getInstance().getCurrentDataset()
            .getWaterLines().select(obj.getGeom()));
        surrounding.addAll(CartAGenDoc.getInstance().getCurrentDataset()
            .getRailwayLines().select(obj.getGeom()));
        for (INetworkSection section : surrounding) {
          // c'est un troncon
          if (obj.getGeom().contains(section.getGeom())) {
            ((IUrbanBlock) obj).getSurroundingNetwork().add(section);
            ((Ilot) ((IUrbanBlock) obj).getGeoxObj()).getArcsReseaux()
                .add((ArcReseau) section.getGeoxObj());
          }
          if (JTSAlgorithms.coversPredicate(obj.getGeom(), section.getGeom())) {
            ((IUrbanBlock) obj).getSurroundingNetwork().add(section);
            ((Ilot) ((IUrbanBlock) obj).getGeoxObj()).getArcsReseaux()
                .add((ArcReseau) section.getGeoxObj());
          }
        }
      }
    }

    public RestoreBlockLinksAction() {
      this.putValue(Action.NAME, "Restore link between blocks and components");
    }
  }
}
