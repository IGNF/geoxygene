package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.CoastlineCrossingLand;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.lodharmonisation.process.RemoveLandFromSea;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class CoastlineLandusePanel extends HarmonisationPanel {

  /****/
  private static final long serialVersionUID = 1L;

  private IFeatureCollection<IGeneObj> coastlines, landuse;

  public CoastlineLandusePanel() {
    super();
    this.coastlines = new FT_FeatureCollection<IGeneObj>();
    this.landuse = new FT_FeatureCollection<IGeneObj>();

    // no parameter for this method
    this.remove(detectionPanel);
    this.remove(harmPanel);

  }

  @Override
  public String getTabName() {
    return I18N.getString("CoastlineLandusePanel.tabName");
  }

  @Override
  public Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs) {
    fillCollections(window, windowObjs);
    // identify inconsistencies
    CoastlineCrossingLand detectionProcess = new CoastlineCrossingLand(
        coastlines, landuse, lodSlider.getValue());
    Set<LoDSpatialRelation> inconsistencies = detectionProcess.findInstances();

    // then, trigger the harmonisation process
    RemoveLandFromSea process = new RemoveLandFromSea(inconsistencies);
    return process.harmonise();
  }

  private void fillCollections(boolean window, Set<IGeneObj> windowObjs) {
    if (window) {
      for (IGeneObj obj : windowObjs) {
        if (obj instanceof ICoastLine)
          coastlines.add(obj);
        if (obj instanceof ISimpleLandUseArea) {
          landuse.add(obj);
        }
      }
    } else {
      coastlines.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getCoastlines());
      landuse.addAll(CartAGenDoc.getInstance().getCurrentDataset()
          .getLandUseAreas());
    }
  }
}
