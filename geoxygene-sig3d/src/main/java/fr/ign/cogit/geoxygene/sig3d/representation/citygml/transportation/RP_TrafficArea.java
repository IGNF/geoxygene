package fr.ign.cogit.geoxygene.sig3d.representation.citygml.transportation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_TrafficArea;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_TrafficArea extends RP_CityObject {

  public RP_TrafficArea(CG_TrafficArea tA, List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = tA.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(tA.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 2) {

      if (tA.isSetLod2MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tA.getLod2MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (tA.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tA.getLod3MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {

      if (tA.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tA.getLod4MultiSurface(), lCGAIni));
      }

    }

    tA.setRepresentation(this);

  }

}
