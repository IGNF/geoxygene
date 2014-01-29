package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Dimension;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.road.ICycleWay;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.importexport.OsmDataset;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.PathNotCrossingLake;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.AdjustLakeOutlineToPaths;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class AdjustLakePanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;

  private IFeatureCollection<IGeneObj> paths, lakes;
  private JSpinner spinBuffer, spinShore, spinCushion, spinTol, spinAngle;
  private JCheckBox chkBridges;

  public AdjustLakePanel() {
    super();
    this.paths = new FT_FeatureCollection<IGeneObj>();
    this.lakes = new FT_FeatureCollection<IGeneObj>();

    chkBridges = new JCheckBox(I18N.getString("AdjustLakePanel.bridges"));
    chkBridges.setSelected(true);
    SpinnerModel tolModel = new SpinnerNumberModel(0.9, 0.0, 1.0, 0.05);
    spinTol = new JSpinner(tolModel);
    spinTol.setPreferredSize(new Dimension(50, 20));
    spinTol.setMaximumSize(new Dimension(50, 20));
    spinTol.setMinimumSize(new Dimension(50, 20));
    SpinnerModel angleModel = new SpinnerNumberModel(60.0, 0.0, 180.0, 1.0);
    spinAngle = new JSpinner(angleModel);
    spinAngle.setPreferredSize(new Dimension(60, 20));
    spinAngle.setMaximumSize(new Dimension(60, 20));
    spinAngle.setMinimumSize(new Dimension(60, 20));
    detectionPanel.add(chkBridges);
    detectionPanel.add(Box.createHorizontalGlue());
    detectionPanel.add(new JLabel(I18N.getString("AdjustLakePanel.bridgeTol")));
    detectionPanel.add(spinTol);
    detectionPanel.add(Box.createHorizontalGlue());
    detectionPanel
        .add(new JLabel(I18N.getString("AdjustLakePanel.bridgeAngle")));
    detectionPanel.add(spinAngle);

    SpinnerModel bufferModel = new SpinnerNumberModel(4.0, 0.5, 20.0, 0.5);
    spinBuffer = new JSpinner(bufferModel);
    spinBuffer.setPreferredSize(new Dimension(60, 20));
    spinBuffer.setMaximumSize(new Dimension(60, 20));
    spinBuffer.setMinimumSize(new Dimension(60, 20));
    SpinnerModel shoreModel = new SpinnerNumberModel(20.0, 1.0, 50.0, 1.0);
    spinShore = new JSpinner(shoreModel);
    spinShore.setPreferredSize(new Dimension(60, 20));
    spinShore.setMaximumSize(new Dimension(60, 20));
    spinShore.setMinimumSize(new Dimension(60, 20));
    SpinnerModel cushionModel = new SpinnerNumberModel(5.0, 0.5, 20.0, 0.5);
    spinCushion = new JSpinner(cushionModel);
    spinCushion.setPreferredSize(new Dimension(60, 20));
    spinCushion.setMaximumSize(new Dimension(60, 20));
    spinCushion.setMinimumSize(new Dimension(60, 20));
    harmPanel.add(new JLabel(I18N.getString("AdjustLakePanel.bufferThresh")));
    harmPanel.add(spinShore);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(new JLabel(I18N.getString("AdjustLakePanel.shoreThresh")));
    harmPanel.add(spinBuffer);
    harmPanel.add(Box.createHorizontalGlue());
    harmPanel.add(new JLabel(I18N.getString("AdjustLakePanel.cushionThresh")));
    harmPanel.add(spinCushion);

  }

  @Override
  public String getTabName() {
    return I18N.getString("AdjustLakePanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs);
    // identify inconsistencies
    PathNotCrossingLake detectionProcess = new PathNotCrossingLake(lakes,
        paths, lodSlider.getValue());
    Set<LoDSpatialRelation> inconsistencies = detectionProcess.findInstances();

    // then, trigger the harmonisation process
    AdjustLakeOutlineToPaths process = new AdjustLakeOutlineToPaths(
        inconsistencies, (Double) spinBuffer.getValue(),
        (Double) spinShore.getValue(), (Double) spinCushion.getValue(),
        (Double) spinTol.getValue(), (Double) spinAngle.getValue() * Math.PI
            / 180);
    process.setSearchBridges(chkBridges.isSelected());
    return process.harmonise();
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof IWaterArea)
          lakes.add(obj);
        if (obj instanceof IPathLine) {
          paths.add(obj);
        }
        if (obj instanceof ICycleWay) {
          paths.add(obj);
        }
      }
    } else {
      lakes.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getWaterAreas());
      paths.addAll(CartAGenDoc.getInstance().getCurrentDataset().getPaths());
      paths.addAll(((OsmDataset) CartAGenDoc.getInstance().getCurrentDataset())
          .getCycleWays());
    }
  }
}
