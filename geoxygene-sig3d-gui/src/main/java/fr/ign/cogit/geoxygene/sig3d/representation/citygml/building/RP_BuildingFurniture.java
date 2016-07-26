package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_BuildingFurniture;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_BuildingFurniture extends RP_CityObject {

  public RP_BuildingFurniture(CG_BuildingFurniture bF) {
    this(bF, null);
  }

  public RP_BuildingFurniture(CG_BuildingFurniture bF,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = bF.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(bF.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 4) {

      if (bF.isSetLod4Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod4Geometry(), lCGA));
      }

    }

    bF.setRepresentation(this);

  }

}
