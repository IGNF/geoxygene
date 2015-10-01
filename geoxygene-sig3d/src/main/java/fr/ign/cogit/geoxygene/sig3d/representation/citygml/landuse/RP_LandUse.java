package fr.ign.cogit.geoxygene.sig3d.representation.citygml.landuse;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.landuse.CG_LandUse;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_LandUse extends RP_CityObject {

  public RP_LandUse(CG_LandUse lU) {
    this(lU, null);
  }

  public RP_LandUse(CG_LandUse lU, List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = lU.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(lU.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 0) {

      if (lU.isSetLod0MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod0MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 1) {

      if (lU.isSetLod1MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod1MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {

      if (lU.isSetLod2MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod2MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (lU.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod3MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {

      if (lU.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod4MultiSurface(), lCGAIni));
      }

    }

    lU.setRepresentation(this);

  }

}
