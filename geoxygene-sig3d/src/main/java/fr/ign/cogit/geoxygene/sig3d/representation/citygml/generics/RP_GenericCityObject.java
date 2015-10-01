package fr.ign.cogit.geoxygene.sig3d.representation.citygml.generics;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.generics.CG_GenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_GenericCityObject extends RP_CityObject {

  public RP_GenericCityObject(CG_GenericCityObject cGO) {
    this(cGO, null);
  }

  public RP_GenericCityObject(CG_GenericCityObject cGO,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = cGO.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cGO.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 0) {

      if (cGO.isSetLod0Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod0Geometry(), lCGA));
      }

      if (cGO.isSetLod0TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod0TerrainIntersection(), lCGA));
      }

    }

    if (Context.LOD_REP == 1) {

      if (cGO.isSetLod1Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod1Geometry(), lCGA));
      }

      if (cGO.isSetLod1TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod1TerrainIntersection(), lCGA));
      }

    }

    if (Context.LOD_REP == 2) {

      if (cGO.isSetLod2Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod2Geometry(), lCGA));
      }

      if (cGO.isSetLod2TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod2TerrainIntersection(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (cGO.isSetLod3Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod3Geometry(), lCGA));
      }

      if (cGO.isSetLod3TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod3TerrainIntersection(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {

      if (cGO.isSetLod4Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod4Geometry(), lCGA));
      }

      if (cGO.isSetLod4TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cGO.getLod4TerrainIntersection(), lCGA));
      }

    }

    cGO.setRepresentation(this);

  }

}
