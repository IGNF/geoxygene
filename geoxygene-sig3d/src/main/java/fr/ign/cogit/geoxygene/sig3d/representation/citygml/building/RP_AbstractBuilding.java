package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractBuilding;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_AbstractSite;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_AbstractBuilding extends RP_AbstractSite {

  public RP_AbstractBuilding(CG_AbstractBuilding cAB) {

    this(cAB, null);

  }

  public RP_AbstractBuilding(CG_AbstractBuilding cAB,
      List<CG_AbstractSurfaceData> lCGAIni) {

    super(cAB);
    // On récupère les géométries concernant l'objet
    List<IGeometry> lGeom = new ArrayList<IGeometry>();

    if (Context.LOD_REP == 1) {

      if (cAB.isSetLod1Solid()) {

        lGeom.add(cAB.getLod1Solid());
      }

      if (cAB.isSetLod1MultiSurface()) {
        lGeom.add(cAB.getLod1MultiSurface());
      }

      if (cAB.isSetLod1TerrainIntersection()) {
        lGeom.add(cAB.getLod1TerrainIntersection());

      }

    }

    if (Context.LOD_REP == 2) {

      if (cAB.isSetLod2Solid()) {

        lGeom.add(cAB.getLod2Solid());
      }

      if (cAB.isSetLod2MultiSurface()) {
        lGeom.add(cAB.getLod2MultiSurface());
      }

      if (cAB.isSetLod2TerrainIntersection()) {
        lGeom.add(cAB.getLod2TerrainIntersection());

      }

      if (cAB.isSetLod2MultiCurve()) {

        lGeom.add(cAB.getLod2MultiCurve());

      }

    }

    if (Context.LOD_REP == 3) {

      if (cAB.isSetLod3Solid()) {

        lGeom.add(cAB.getLod3Solid());
      }

      if (cAB.isSetLod3MultiSurface()) {
        lGeom.add(cAB.getLod3MultiSurface());
      }

      if (cAB.isSetLod3TerrainIntersection()) {
        lGeom.add(cAB.getLod3TerrainIntersection());

      }

      if (cAB.isSetLod3MultiCurve()) {

        lGeom.add(cAB.getLod3MultiCurve());

      }

    }

    if (Context.LOD_REP == 4) {

      if (cAB.isSetLod4Solid()) {

        lGeom.add(cAB.getLod4Solid());
      }

      if (cAB.isSetLod4MultiSurface()) {
        lGeom.add(cAB.getLod4MultiSurface());
      }

      if (cAB.isSetLod4TerrainIntersection()) {
        lGeom.add(cAB.getLod4TerrainIntersection());

      }

      if (cAB.isSetLod4MultiCurve()) {

        lGeom.add(cAB.getLod4MultiCurve());

      }

    }

    // On récupère les éléments de représentations
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = cAB.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(cAB.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    int nbGeom = lGeom.size();

    for (int i = 0; i < nbGeom; i++) {
      // Pour chaque géométrie on affecte la style
      this.bGRep.addChild(CG_StylePreparator.generateRepresentation(
          lGeom.get(i), lCGA));

    }

    // On s'occupe des bounded surface
    if (cAB.getBoundedBySurfaces() != null) {
      int nbBoundingSurface = cAB.getBoundedBySurfaces().size();

      for (int i = 0; i < nbBoundingSurface; i++) {

        RP_BoundarySurface bS = new RP_BoundarySurface(cAB
            .getBoundedBySurfaces().get(i), lCGA);

        this.bGRep.addChild(bS.getBGRep());

      }

    }

    if (cAB.getOuterBuildingInstallation() != null) {

      int nbOuterBuildingInstallation = cAB.getOuterBuildingInstallation()
          .size();

      if (nbOuterBuildingInstallation > 0) {

        for (int i = 0; i < nbOuterBuildingInstallation; i++) {

          RP_BuildingInstallation bI = new RP_BuildingInstallation(cAB
              .getOuterBuildingInstallation().get(i), lCGA);
          this.bGRep.addChild(bI.getBGRep());

        }

      }
    }

    if (cAB.getInteriorBuildingInstallation() != null) {

      int nbIntBuildingInstallation = cAB.getInteriorBuildingInstallation()
          .size();

      for (int i = 0; i < nbIntBuildingInstallation; i++) {

        RP_BuildingInteriorInstallation bI = new RP_BuildingInteriorInstallation(
            cAB.getInteriorBuildingInstallation().get(i), lCGA);
        this.bGRep.addChild(bI.getBGRep());

      }

    }

    if (cAB.getInteriorRoom() != null) {

      int nbRooms = cAB.getInteriorRoom().size();

      for (int i = 0; i < nbRooms; i++) {

        RP_Room room = new RP_Room(cAB.getInteriorRoom().get(i), lCGA);
        this.bGRep.addChild(room.getBGRep());

      }
    }

    if (cAB.getConsistsOfBuildingPart() != null) {
      int nbPart = cAB.getConsistsOfBuildingPart().size();

      for (int i = 0; i < nbPart; i++) {

        RP_AbstractBuilding rAB = new RP_AbstractBuilding(cAB
            .getConsistsOfBuildingPart().get(i), lCGA);

        this.bGRep.addChild(rAB.getBGRep());

      }
    }

    if (cAB.getAddress() != null) {

      // Représentation des adresses non gérées
      // Ca vous manque vraiment ?
    }

    cAB.setRepresentation(this);

  }

}
