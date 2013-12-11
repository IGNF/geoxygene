package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Dimension;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.importexport.OsmDataset;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.TreeAlongRoad;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.AlignTreesAlongRoads;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class AlignTreeAlongRoadPanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;

  private IFeatureCollection<IGeneObj> trees, roads;
  private JSpinner spinDist, spinTreeWidth, spinOffset;

  public AlignTreeAlongRoadPanel() {
    super();
    this.trees = new FT_FeatureCollection<IGeneObj>();
    this.roads = new FT_FeatureCollection<IGeneObj>();

    // fill the detection panel
    SpinnerModel distModel = new SpinnerNumberModel(0.9, 0.1, 3.0, 0.1);
    spinDist = new JSpinner(distModel);
    spinDist.setPreferredSize(new Dimension(60, 20));
    spinDist.setMaximumSize(new Dimension(60, 20));
    spinDist.setMinimumSize(new Dimension(60, 20));
    SpinnerModel treeModel = new SpinnerNumberModel(0.05, 0.05, 1.0, 0.05);
    spinTreeWidth = new JSpinner(treeModel);
    spinTreeWidth.setPreferredSize(new Dimension(50, 20));
    spinTreeWidth.setMaximumSize(new Dimension(50, 20));
    spinTreeWidth.setMinimumSize(new Dimension(50, 20));
    detectionPanel.add(new JLabel(I18N
        .getString("AlignTreeAlongRoadPanel.minDistThresh")));
    detectionPanel.add(spinDist);
    detectionPanel.add(Box.createHorizontalGlue());
    detectionPanel.add(new JLabel(I18N
        .getString("AlignTreeAlongRoadPanel.treeWidth")));
    detectionPanel.add(spinTreeWidth);

    // fill the harmonisation parameters panel
    SpinnerModel offsetModel = new SpinnerNumberModel(0.1, 0.05, 1.0, 0.05);
    spinOffset = new JSpinner(offsetModel);
    spinOffset.setPreferredSize(new Dimension(50, 20));
    spinOffset.setMaximumSize(new Dimension(50, 20));
    spinOffset.setMinimumSize(new Dimension(50, 20));
    harmPanel.add(new JLabel(I18N.getString("AdjustLakePanel.offsetThresh")));
    harmPanel.add(spinOffset);
  }

  @Override
  public String getTabName() {
    return I18N.getString("AlignTreeAlongRoadPanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs);
    // convert the parameters in meters
    double minDist = (Double) spinDist.getValue()
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double treeWidth = (Double) spinTreeWidth.getValue()
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    double offset = (Double) spinOffset.getValue()
        * Legend.getSYMBOLISATI0N_SCALE() / 1000;
    TreeAlongRoad detectionProc = new TreeAlongRoad(roads, trees,
        lodSlider.getValue(), minDist, treeWidth);
    detectionProc.findInstances();

    System.out.println("left instances");
    for (LoDSpatialRelation i : detectionProc.getLeftInstances())
      System.out.println(i);
    System.out.println("right instances");
    for (LoDSpatialRelation i : detectionProc.getRightInstances())
      System.out.println(i);

    // harmonise the inconsistencies
    AlignTreesAlongRoads process = new AlignTreesAlongRoads(
        detectionProc.getLeftInstances(), detectionProc.getRightInstances(),
        offset);
    return process.harmonise();
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof ITreePoint)
          trees.add(obj);
        if (obj instanceof IRoadLine) {
          if (((IRoadLine) obj).getImportance() == 0)
            continue;
          roads.add(obj);
        }
      }
    } else {
      trees.addAll(((OsmDataset) CartAGenDoc.getInstance().getCurrentDataset())
          .getTreePoints());
      for (IRoadLine road : CartAGenDoc.getInstance().getCurrentDataset()
          .getRoads()) {
        if (road.getImportance() == 0)
          continue;
        roads.add(road);
      }
    }
  }
}
