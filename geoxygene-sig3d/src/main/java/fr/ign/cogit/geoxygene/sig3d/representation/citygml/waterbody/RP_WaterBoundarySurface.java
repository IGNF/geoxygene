package fr.ign.cogit.geoxygene.sig3d.representation.citygml.waterbody;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_WaterBoundarySurface;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_WaterBoundarySurface extends RP_CityObject {

  public RP_WaterBoundarySurface(CG_WaterBoundarySurface wB,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = wB.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(wB.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 2) {

      if (wB.isSetLod2Surface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod2Surface(), lCGA));
      }
    }

    if (Context.LOD_REP == 3) {

      if (wB.isSetLod3Surface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod3Surface(), lCGA));
      }
    }

    if (Context.LOD_REP == 4) {

      if (wB.isSetLod4Surface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod4Surface(), lCGA));
      }
    }

    wB.setRepresentation(this);

  }
}
