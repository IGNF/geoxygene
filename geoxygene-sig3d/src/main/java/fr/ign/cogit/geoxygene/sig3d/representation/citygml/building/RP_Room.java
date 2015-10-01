package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Room;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_Room extends RP_CityObject {

  public RP_Room(CG_Room r) {
    this(r, null);
  }

  public RP_Room(CG_Room cgR, List<CG_AbstractSurfaceData> lCGAIni) {
    super();
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = cgR.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cgR.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    if (Context.LOD_REP == 4) {

      if (cgR.isSetLod4MultiSurface()) {
        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cgR.getLod4MultiSurface(), lCGA));

      }

      if (cgR.isSetLod4Solid()) {

        this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
            cgR.getLod4Solid(), lCGA));
      }

    }
    // On s'occupe des bounded surface
    if (cgR.getBoundedBySurfaces() != null) {
      int nbBoundingSurface = cgR.getBoundedBySurfaces().size();

      for (int i = 0; i < nbBoundingSurface; i++) {

        RP_BoundarySurface bS = new RP_BoundarySurface(cgR
            .getBoundedBySurfaces().get(i), lCGA);

        this.bGRep.addChild(bS.getBGRep());

      }

    }

    if (cgR.getInteriorFurniture() != null) {
      int nbFurniture = cgR.getInteriorFurniture().size();

      for (int i = 0; i < nbFurniture; i++) {

        RP_BuildingFurniture bf = new RP_BuildingFurniture(cgR
            .getInteriorFurniture().get(i), lCGA);

        this.bGRep.addChild(bf.getBGRep());

      }

    }

    cgR.setRepresentation(this);
  }

}
