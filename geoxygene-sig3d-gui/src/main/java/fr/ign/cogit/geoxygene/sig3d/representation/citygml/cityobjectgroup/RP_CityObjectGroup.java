package fr.ign.cogit.geoxygene.sig3d.representation.citygml.cityobjectgroup;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.cityobjectgroup.CG_CityObjectGroup;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_CityObjectGroup extends RP_CityObject {

  public RP_CityObjectGroup(CG_CityObjectGroup cO,
      List<CG_AbstractSurfaceData> lCGA) {
    super();

    List<CG_AbstractSurfaceData> lCGAIni = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGA != null) {
      lCGAIni.addAll(lCGA);
    }

    int nbAppProperty = cO.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cO.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (cO.getGeometry() != null) {
      this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
          cO.getGeometry(), lCGA));

    }

    cO.setRepresentation(this);

  }

}
