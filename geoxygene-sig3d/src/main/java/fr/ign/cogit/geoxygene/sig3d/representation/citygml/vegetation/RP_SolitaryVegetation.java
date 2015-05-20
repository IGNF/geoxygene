package fr.ign.cogit.geoxygene.sig3d.representation.citygml.vegetation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation.CG_SolitaryVegetationObject;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_SolitaryVegetation extends RP_CityObject {

  public RP_SolitaryVegetation(CG_SolitaryVegetationObject lU,
      List<CG_AbstractSurfaceData> lCGAIni) {
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

    if (Context.LOD_REP == 1) {

      if (lU.isSetLod1Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod1Geometry(), lCGA));
      }

      if (lU.isSetLod1ImplicitRepresentation()) {
        System.out
            .println("CG_SolitaryVegetation : implicit geometry not handled");
        // this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
        // lU.getLod1ImplicitRepresentation(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {

      if (lU.isSetLod2Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod2Geometry(), lCGA));
      }

      if (lU.isSetLod2ImplicitRepresentation()) {
        System.out
            .println("CG_SolitaryVegetation : implicit geometry not handled");
      }

    }

    if (Context.LOD_REP == 3) {

      if (lU.isSetLod3Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod3Geometry(), lCGA));
      }

      if (lU.isSetLod3ImplicitRepresentation()) {
        System.out
            .println("CG_SolitaryVegetation : implicit geometry not handled");
      }

    }

    if (Context.LOD_REP == 4) {

      if (lU.isSetLod3Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            lU.getLod4Geometry(), lCGAIni));
      }

      if (lU.isSetLod4ImplicitRepresentation()) {
        System.out
            .println("CG_SolitaryVegetation : implicit geometry not handled");
      }

    }

    lU.setRepresentation(this);

  }

}
