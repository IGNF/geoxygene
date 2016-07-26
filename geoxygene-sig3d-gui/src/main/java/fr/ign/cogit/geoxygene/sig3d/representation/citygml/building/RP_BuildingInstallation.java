package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_BuildingInstallation;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_BuildingInstallation extends RP_CityObject {

  public RP_BuildingInstallation(CG_BuildingInstallation bI) {
    this(bI, null);
  }

  public RP_BuildingInstallation(CG_BuildingInstallation bI,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();
    List<CG_AbstractSurfaceData> lCGAI = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGAI.addAll(lCGAIni);
    }

    int nbAppProperty = bI.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {
      lCGAI.addAll(bI.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 3) {
      this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
          bI.getLod3Geometry(), lCGAI));

    }

    if (Context.LOD_REP == 4) {
      this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
          bI.getLod4Geometry(), lCGAI));

    }

    bI.setRepresentation(this);

  }

}
