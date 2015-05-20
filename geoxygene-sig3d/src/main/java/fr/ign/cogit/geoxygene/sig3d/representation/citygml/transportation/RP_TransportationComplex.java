package fr.ign.cogit.geoxygene.sig3d.representation.citygml.transportation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_TransportationComplex;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_TransportationComplex extends RP_CityObject {

  public RP_TransportationComplex(CG_TransportationComplex tc) {
    this(tc, null);

  }

  public RP_TransportationComplex(CG_TransportationComplex tC,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = tC.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(tC.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 1) {

      if (tC.isSetLod1MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tC.getLod1MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {

      if (tC.isSetLod2MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tC.getLod2MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (tC.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tC.getLod3MultiSurface(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {

      if (tC.isSetLod3MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            tC.getLod4MultiSurface(), lCGAIni));
      }

    }

    if (tC.isSetTrafficArea()) {
      int nbGeom = tC.getTrafficArea().size();

      for (int i = 0; i < nbGeom; i++) {

        RP_TrafficArea tA = new RP_TrafficArea(tC.getTrafficArea().get(i), lCGA);
        this.getBGRep().addChild(tA.getBGRep());

      }

    }

    if (tC.isSetAuxiliaryTrafficArea()) {
      int nbGeom = tC.getAuxiliaryTrafficArea().size();

      for (int i = 0; i < nbGeom; i++) {
        RP_AuxiliaryTrafficArea tA = new RP_AuxiliaryTrafficArea(tC
            .getAuxiliaryTrafficArea().get(i), lCGA);
        this.getBGRep().addChild(tA.getBGRep());

      }

    }

    tC.setRepresentation(this);

  }

}
