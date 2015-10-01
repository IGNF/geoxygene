package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BranchGroup;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractOpening;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_Opening extends RP_CityObject {

  public RP_Opening(CG_AbstractOpening aO) {

    this(aO, null);

  }

  public RP_Opening(CG_AbstractOpening aO, List<CG_AbstractSurfaceData> lCGAIni) {

    super();
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = aO.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(aO.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    BranchGroup bg = null;

    if (Context.LOD_REP == 3) {

      if (aO.isSetLod3MultiSurface()) {

        bg = CG_StylePreparator.generateRepresentation(
            aO.getLod3MultiSurface(), lCGA);

      }

    }

    if (Context.LOD_REP == 4) {

      if (aO.isSetLod4MultiSurface()) {

        bg = CG_StylePreparator.generateRepresentation(
            aO.getLod4MultiSurface(), lCGA);

      }

    }

    if (bg != null) {
      this.bGRep.addChild(bg);
    }

    aO.setRepresentation(this);

  }

}
