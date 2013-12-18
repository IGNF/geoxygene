package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.importexport.OsmDataset;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.FunctionalSiteComponents;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.AdjustFunctionalSitesBounds;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class AdjustFunctionalSitePanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;
  private JSpinner spinBelongSch, spinBelongHosp, spinExclSch, spinExclHosp;
  private JCheckBox chkSchools, chkHospitals;
  private IFeatureCollection<IGeneObj> buildings, schools, hospitals,
      sportsFields, roads;

  public AdjustFunctionalSitePanel() {
    super();
    this.buildings = new FT_FeatureCollection<IGeneObj>();
    this.schools = new FT_FeatureCollection<IGeneObj>();
    this.hospitals = new FT_FeatureCollection<IGeneObj>();
    this.sportsFields = new FT_FeatureCollection<IGeneObj>();
    this.roads = new FT_FeatureCollection<IGeneObj>();

    // fill the detection panel
    SpinnerModel belongSchModel = new SpinnerNumberModel(0, -10, 20, 1);
    spinBelongSch = new JSpinner(belongSchModel);
    spinBelongSch.setPreferredSize(new Dimension(60, 20));
    spinBelongSch.setMaximumSize(new Dimension(60, 20));
    spinBelongSch.setMinimumSize(new Dimension(60, 20));
    SpinnerModel belongHospModel = new SpinnerNumberModel(0, -10, 20, 1);
    spinBelongHosp = new JSpinner(belongHospModel);
    spinBelongHosp.setPreferredSize(new Dimension(60, 20));
    spinBelongHosp.setMaximumSize(new Dimension(60, 20));
    spinBelongSch.setMinimumSize(new Dimension(60, 20));
    SpinnerModel exclSchModel = new SpinnerNumberModel(0, -10, 20, 1);
    spinExclSch = new JSpinner(exclSchModel);
    spinExclSch.setPreferredSize(new Dimension(60, 20));
    spinExclSch.setMaximumSize(new Dimension(60, 20));
    spinExclSch.setMinimumSize(new Dimension(60, 20));
    SpinnerModel exclHospModel = new SpinnerNumberModel(0, -10, 20, 1);
    spinExclHosp = new JSpinner(exclHospModel);
    spinExclHosp.setPreferredSize(new Dimension(60, 20));
    spinExclHosp.setMaximumSize(new Dimension(60, 20));
    spinExclHosp.setMinimumSize(new Dimension(60, 20));
    chkSchools = new JCheckBox(
        I18N.getString("AdjustFunctionalSitePanel.schools"));
    chkSchools.setSelected(true);
    chkHospitals = new JCheckBox(
        I18N.getString("AdjustFunctionalSitePanel.hospitals"));
    chkHospitals.setSelected(true);
    detectionPanel.add(chkSchools);
    detectionPanel.add(new JLabel(I18N
        .getString("AdjustFunctionalSitePanel.belongThresh")));
    detectionPanel.add(spinBelongSch);
    detectionPanel.add(new JLabel(I18N
        .getString("AdjustFunctionalSitePanel.excludeThresh")));
    detectionPanel.add(spinExclSch);
    detectionPanel.add(chkHospitals);
    detectionPanel.add(new JLabel(I18N
        .getString("AdjustFunctionalSitePanel.belongThresh")));
    detectionPanel.add(spinBelongHosp);
    detectionPanel.add(new JLabel(I18N
        .getString("AdjustFunctionalSitePanel.excludeThresh")));
    detectionPanel.add(spinExclHosp);

    // no harmonisation panel
    this.remove(harmPanel);
  }

  @Override
  public String getTabName() {
    return I18N.getString("AdjustFunctionalSitePanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs, chkSchools.isSelected(),
        chkHospitals.isSelected());

    Set<IGeneObj> harmonisedFeats = new HashSet<IGeneObj>();

    if (chkSchools.isSelected()) {
      IFeatureCollection<IGeneObj> components = new FT_FeatureCollection<IGeneObj>();
      components.addAll(buildings);
      components.addAll(sportsFields);
      // identify inconsistencies
      FunctionalSiteComponents detectionProcess = new FunctionalSiteComponents(
          schools, components, lodSlider.getValue(),
          (Integer) spinBelongSch.getValue(), (Integer) spinExclSch.getValue());
      detectionProcess.findInstances();
      Set<LoDSpatialRelation> toInclude = detectionProcess
          .getIncludeInstances();
      Set<LoDSpatialRelation> toExclude = detectionProcess
          .getExcludeInstances();

      // then, trigger the harmonisation process
      AdjustFunctionalSitesBounds process = new AdjustFunctionalSitesBounds(
          toInclude, toExclude);

      harmonisedFeats.addAll(process.harmonise());
    }

    if (chkHospitals.isSelected()) {
      IFeatureCollection<IGeneObj> components = new FT_FeatureCollection<IGeneObj>();
      components.addAll(buildings);
      components.addAll(roads);
      // identify inconsistencies
      FunctionalSiteComponents detectionProcess = new FunctionalSiteComponents(
          hospitals, components, lodSlider.getValue(),
          (Integer) spinBelongHosp.getValue(),
          (Integer) spinExclHosp.getValue());
      detectionProcess.findInstances();
      Set<LoDSpatialRelation> toInclude = detectionProcess
          .getIncludeInstances();
      Set<LoDSpatialRelation> toExclude = detectionProcess
          .getExcludeInstances();

      // then, trigger the harmonisation process
      AdjustFunctionalSitesBounds process = new AdjustFunctionalSitesBounds(
          toInclude, toExclude);

      harmonisedFeats.addAll(process.harmonise());
    }
    return harmonisedFeats;
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs,
      boolean school, boolean hospital) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof IBuilding)
          buildings.add(obj);
        if (obj instanceof IRoadLine && hospital)
          roads.add(obj);
      }
    } else {
      buildings.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings());
      if (hospital) {
        roads.addAll(CartAGenDoc.getInstance().getCurrentDataset().getRoads());
        hospitals.addAll(((OsmDataset) CartAGenDoc.getInstance()
            .getCurrentDataset()).getHospitals());
      }
      if (school) {
        sportsFields.addAll(CartAGenDoc.getInstance().getCurrentDataset()
            .getSportsFields());
        schools.addAll(((OsmDataset) CartAGenDoc.getInstance()
            .getCurrentDataset()).getSchools());
      }
    }
  }
}
