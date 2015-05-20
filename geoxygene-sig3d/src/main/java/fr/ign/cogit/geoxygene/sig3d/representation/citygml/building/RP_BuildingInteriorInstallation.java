package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_IntBuildingInstallation;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_BuildingInteriorInstallation extends RP_CityObject

{

  public RP_BuildingInteriorInstallation(CG_IntBuildingInstallation cgIBI) {

    this(cgIBI, null);

  }

  public RP_BuildingInteriorInstallation(CG_IntBuildingInstallation cgIBI,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = cgIBI.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cgIBI.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 4) {
      this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
          cgIBI.getLod4Geometry(), lCGA));

    }

    cgIBI.setRepresentation(this);
  }
}
