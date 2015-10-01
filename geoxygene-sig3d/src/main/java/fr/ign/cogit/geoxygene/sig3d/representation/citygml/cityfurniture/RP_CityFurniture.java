package fr.ign.cogit.geoxygene.sig3d.representation.citygml.cityfurniture;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.cityfurniture.CG_CityFurniture;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_CityFurniture extends RP_CityObject {

  public RP_CityFurniture(CG_CityFurniture bF,
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

    if (Context.LOD_REP == 1) {

      if (bF.isSetLod1TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod1TerrainIntersection(), lCGA));

      }

      if (bF.isSetLod1Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod1Geometry(), lCGA));

      }

    }

    if (Context.LOD_REP == 2) {

      if (bF.isSetLod2TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod2TerrainIntersection(), lCGA));

      }

      if (bF.isSetLod2Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod2Geometry(), lCGA));
      }

    }

    if (Context.LOD_REP == 3) {

      if (bF.isSetLod3TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod3TerrainIntersection(), lCGA));

      }
      if (bF.isSetLod3Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod3Geometry(), lCGA));
      }

    }

    if (Context.LOD_REP == 4) {

      if (bF.isSetLod4TerrainIntersection()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod4TerrainIntersection(), lCGA));

      }

      if (bF.isSetLod4Geometry()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            bF.getLod4Geometry(), lCGA));
      }

    }

    bF.setRepresentation(this);

  }

}
