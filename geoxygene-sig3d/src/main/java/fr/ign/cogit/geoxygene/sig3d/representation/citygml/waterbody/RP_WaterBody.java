package fr.ign.cogit.geoxygene.sig3d.representation.citygml.waterbody;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_WaterBody;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_WaterBody extends RP_CityObject {

  public RP_WaterBody(CG_WaterBody wB, List<CG_AbstractSurfaceData> lCGAIni) {
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

    if (Context.LOD_REP == 0) {

      if (wB.isSetLod0MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod0MultiSurface(), lCGA));
      }

      if (wB.isSetLod0MultiCurve()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod0MultiCurve(), lCGA));
      }

    }

    if (Context.LOD_REP == 1) {

      if (wB.isSetLod1MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod1MultiSurface(), lCGA));
      }

      if (wB.isSetLod1MultiCurve()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod1MultiCurve(), lCGA));
      }

      if (wB.isSetLod1Solid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod1Solid(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {
      if (wB.isSetLod2Solid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod2Solid(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {
      if (wB.isSetLod3Solid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod3Solid(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {
      if (wB.isSetLod4Solid()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            wB.getLod4Solid(), lCGA));
      }

    }

    if (wB.isSetBoundedBySurfaces()) {

      int nbBS = wB.getBoundedBySurfaces().size();

      for (int i = 0; i < nbBS; i++) {
        RP_WaterBoundarySurface wBS = new RP_WaterBoundarySurface(wB
            .getBoundedBySurfaces().get(i), lCGA);
        this.getBGRep().addChild(wBS.getBGRep());
      }

    }

    wB.setRepresentation(this);
  }

}
