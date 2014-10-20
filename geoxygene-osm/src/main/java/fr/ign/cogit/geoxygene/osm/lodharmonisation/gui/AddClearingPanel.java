package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Dimension;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.defaultschema.urban.UrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.BufferClustering;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.HouseGroupInForest;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.AddClearingInForest;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.AddClearingInForest.ClearingShape;
import fr.ign.cogit.geoxygene.osm.schema.landuse.OsmLandUseTypology;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class AddClearingPanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;
  private JSpinner spinErosion, spinOverlap, spinRadius, spinBigBuilding;
  private JComboBox shapeCombo;
  private JCheckBox networkCheck;
  private IFeatureCollection<IGeneObj> buildings, blocks, forests;

  public AddClearingPanel() {
    super();
    this.buildings = new FT_FeatureCollection<IGeneObj>();
    this.blocks = new FT_FeatureCollection<IGeneObj>();
    this.forests = new FT_FeatureCollection<IGeneObj>();

    // fill the detection panel
    SpinnerModel distModel = new SpinnerNumberModel(0.8, 0.0, 1.0, 0.05);
    spinOverlap = new JSpinner(distModel);
    spinOverlap.setPreferredSize(new Dimension(60, 20));
    spinOverlap.setMaximumSize(new Dimension(60, 20));
    spinOverlap.setMinimumSize(new Dimension(60, 20));
    SpinnerModel radiusModel = new SpinnerNumberModel(20.0, 1.0, 100.0, 1.0);
    spinRadius = new JSpinner(radiusModel);
    spinRadius.setPreferredSize(new Dimension(60, 20));
    spinRadius.setMaximumSize(new Dimension(60, 20));
    spinRadius.setMinimumSize(new Dimension(60, 20));
    SpinnerModel bigBuildModel = new SpinnerNumberModel(1500.0, 10.0, 10000.0,
        10.0);
    spinBigBuilding = new JSpinner(bigBuildModel);
    spinBigBuilding.setPreferredSize(new Dimension(60, 20));
    spinBigBuilding.setMaximumSize(new Dimension(60, 20));
    spinBigBuilding.setMinimumSize(new Dimension(60, 20));
    detectionPanel.add(new JLabel(I18N
        .getString("AddClearingPanel.overlapThresh")));
    detectionPanel.add(spinOverlap);
    detectionPanel.add(new JLabel(I18N
        .getString("AddClearingPanel.radiusThresh")));
    detectionPanel.add(spinRadius);
    detectionPanel.add(new JLabel(I18N
        .getString("AddClearingPanel.bigBuildThresh")));
    detectionPanel.add(spinBigBuilding);

    // fill the harmonisation panel
    networkCheck = new JCheckBox(I18N.getString("AddClearingPanel.cutNetwork"));
    SpinnerModel erosionModel = new SpinnerNumberModel(10.0, 0.0, 50.0, 0.5);
    spinErosion = new JSpinner(erosionModel);
    spinErosion.setPreferredSize(new Dimension(60, 20));
    spinErosion.setMaximumSize(new Dimension(60, 20));
    spinErosion.setMinimumSize(new Dimension(60, 20));
    shapeCombo = new JComboBox(new String[] { "BUFFER", "CONVEX", "RECTANGLE" });
    shapeCombo.setPreferredSize(new Dimension(80, 20));
    shapeCombo.setMaximumSize(new Dimension(80, 20));
    shapeCombo.setMinimumSize(new Dimension(80, 20));
    harmPanel.add(new JLabel(I18N.getString("AddClearingPanel.clearingShape")));
    harmPanel.add(shapeCombo);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(networkCheck);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(new JLabel(I18N.getString("AddClearingPanel.erosionThresh")));
    harmPanel.add(spinErosion);
  }

  @Override
  public String getTabName() {
    return I18N.getString("AddClearingPanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs);
    // identify inconsistencies
    HouseGroupInForest detectionProcess = new HouseGroupInForest(blocks,
        forests, lodSlider.getValue(), (Double) spinOverlap.getValue());
    Set<LoDSpatialRelation> inconsistencies = detectionProcess.findInstances();

    // then, trigger the harmonisation process
    AddClearingInForest process = new AddClearingInForest(inconsistencies,
        ClearingShape.valueOf((String) shapeCombo.getSelectedItem()),
        networkCheck.isSelected(), (Double) spinErosion.getValue());
    return process.harmonise();
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof IBuilding)
          buildings.add(obj);
        if (obj instanceof ISimpleLandUseArea) {
          if (((ISimpleLandUseArea) obj).getType() == OsmLandUseTypology.FOREST
              .ordinal())
            forests.add(obj);
        }
      }
    } else {
      buildings.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings());
      for (ISimpleLandUseArea landuse : CartAGenDoc.getInstance()
          .getCurrentDataset().getLandUseAreas()) {
        if (landuse.getType() == OsmLandUseTypology.FOREST.ordinal())
          forests.add(landuse);
      }
    }
    // then, compute the building groups from the buildings
    BufferClustering clusters = new BufferClustering(buildings,
        (Double) spinRadius.getValue());
    clusters.setDebug(false);
    for (Set<IGeneObj> cluster : clusters.getClusters().keySet()) {
      if (cluster.size() < 4) {
        double totalArea = 0.0;
        for (IGeneObj obj : cluster)
          totalArea += obj.getGeom().area();
        if (totalArea < (Double) spinBigBuilding.getValue())
          continue;
      }
      Collection<IUrbanElement> buildingGroup = new HashSet<IUrbanElement>();
      for (IGeneObj obj : cluster) {
        buildingGroup.add((IUrbanElement) obj);
      }
      blocks.add(new UrbanBlock(clusters.getClusters().get(cluster), null,
          null, buildingGroup, null));
    }
  }
}
