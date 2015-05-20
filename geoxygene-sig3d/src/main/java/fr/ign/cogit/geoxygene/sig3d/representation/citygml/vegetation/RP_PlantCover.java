package fr.ign.cogit.geoxygene.sig3d.representation.citygml.vegetation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation.CG_PlantCover;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_PlantCover extends RP_CityObject {

  public RP_PlantCover(CG_PlantCover cPC, List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = cPC.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cPC.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 0) {

    }

    if (Context.LOD_REP == 1) {

      if (cPC.isSetLod1MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod1MultiSurface(), lCGA));
      }

      if (cPC.isSetLod1MultiSolid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod1MultiSolid(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {

      if (cPC.isSetLod2MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod2MultiSurface(), lCGA));
      }

      if (cPC.isSetLod2MultiSolid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod2MultiSolid(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (cPC.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod3MultiSurface(), lCGA));
      }

      if (cPC.isSetLod3MultiSolid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod3MultiSolid(), lCGA));
      }
    }

    if (Context.LOD_REP == 4) {

      if (cPC.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cPC.getLod4MultiSurface(), lCGAIni));
      }

    }

    cPC.setRepresentation(this);

  }
}
