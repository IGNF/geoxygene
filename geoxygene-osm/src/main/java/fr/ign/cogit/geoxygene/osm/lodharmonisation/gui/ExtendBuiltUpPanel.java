package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Dimension;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.BuildingInBuiltUp;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.ExtendBuiltUpAreas;
import fr.ign.cogit.geoxygene.osm.schema.landuse.OsmLandUseTypology;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class ExtendBuiltUpPanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;
  private JSpinner spinRadius, spinDoug, spinDist;
  private JSlider slidIter;
  private IFeatureCollection<IGeneObj> buildings, builtUps;

  public ExtendBuiltUpPanel() {
    super();
    this.buildings = new FT_FeatureCollection<IGeneObj>();
    this.builtUps = new FT_FeatureCollection<IGeneObj>();

    // fill the detection panel
    SpinnerModel distModel = new SpinnerNumberModel(12.5, 1.0, 100.0, 0.5);
    spinDist = new JSpinner(distModel);
    spinDist.setPreferredSize(new Dimension(60, 20));
    spinDist.setMaximumSize(new Dimension(60, 20));
    spinDist.setMinimumSize(new Dimension(60, 20));
    detectionPanel.add(new JLabel(I18N
        .getString("ExtendBuiltUpPanel.distanceThresh")));
    detectionPanel.add(spinDist);

    // fill the harmonisation panel
    SpinnerModel radiusModel = new SpinnerNumberModel(15.0, 1.0, 200.0, 1.0);
    SpinnerModel dougModel = new SpinnerNumberModel(3.0, 0.5, 50.0, 0.5);
    spinRadius = new JSpinner(radiusModel);
    spinRadius.setPreferredSize(new Dimension(60, 20));
    spinRadius.setMaximumSize(new Dimension(60, 20));
    spinRadius.setMinimumSize(new Dimension(60, 20));
    spinDoug = new JSpinner(dougModel);
    spinDoug.setPreferredSize(new Dimension(60, 20));
    spinDoug.setMaximumSize(new Dimension(60, 20));
    spinDoug.setMinimumSize(new Dimension(60, 20));
    slidIter = new JSlider(1, 5, 1);
    slidIter.setPaintTicks(true);
    slidIter.setMajorTickSpacing(1);
    harmPanel
        .add(new JLabel(I18N.getString("ExtendBuiltUpPanel.radiusThresh")));
    harmPanel.add(spinRadius);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(new JLabel(I18N.getString("ExtendBuiltUpPanel.dougThresh")));
    harmPanel.add(spinDoug);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(new JLabel(I18N.getString("ExtendBuiltUpPanel.iterNb")));
    harmPanel.add(slidIter);
  }

  @Override
  public String getTabName() {
    return I18N.getString("ExtendBuiltUpPanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs);
    // identify inconsistencies
    BuildingInBuiltUp detectionProcess = new BuildingInBuiltUp(buildings,
        builtUps, lodSlider.getValue(), (Double) spinDist.getValue());
    Set<LoDSpatialRelation> inconsistencies = detectionProcess.findInstances();

    // then, trigger the harmonisation process
    ExtendBuiltUpAreas process = new ExtendBuiltUpAreas(inconsistencies,
        (Double) spinRadius.getValue(), (Double) spinDoug.getValue());
    process.setIterations(slidIter.getValue());
    return process.harmonise();
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof IBuilding)
          buildings.add(obj);
        if (obj instanceof ISimpleLandUseArea) {
          if (((ISimpleLandUseArea) obj).getType() == OsmLandUseTypology.RESIDENTIAL
              .ordinal())
            builtUps.add(obj);
        }
      }
    } else {
      buildings.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings());
      for (ISimpleLandUseArea landuse : CartAGenDoc.getInstance()
          .getCurrentDataset().getLandUseAreas()) {
        if (landuse.getType() == OsmLandUseTypology.RESIDENTIAL.ordinal())
          builtUps.add(landuse);
      }
    }
  }
}
