package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BranchGroup;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2.Context;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractBoundarySurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractOpening;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation.CG_StylePreparator;

public class RP_BoundarySurface extends RP_CityObject {

  public RP_BoundarySurface(CG_AbstractBoundarySurface bS) {

    this(bS, null);

  }

  public RP_BoundarySurface(CG_AbstractBoundarySurface bS,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    // On récupère les géométries

    List<IGeometry> lGeom = new ArrayList<IGeometry>();

    if (bS.isSetLod2MultiSurface()) {

      if (Context.LOD_REP == 2) {

        lGeom.add(bS.getLod2MultiSurface());
      }

    }

    if (bS.isSetLod3MultiSurface()) {

      if (Context.LOD_REP == 3) {

        
        System.out.println(bS.getClass().toString());
        
        lGeom.add(bS.getLod3MultiSurface());
      }

    }

    if (bS.isSetLod4MultiSurface()) {

      if (Context.LOD_REP == 4) {

        lGeom.add(bS.getLod4MultiSurface());
      }

    }

    // On récupère la liste des styles
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = bS.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(bS.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    int nbGeom = lGeom.size();

    // On attache au noeud les représentations
    for (int i = 0; i < nbGeom; i++) {

      BranchGroup bg = CG_StylePreparator.generateRepresentation(lGeom.get(i),
          lCGA);




      
      this.bGRep.addChild(bg);

    }

    if (bS.isSetOpening()) {
      List<CG_AbstractOpening> oP = bS.getOpening();
      for (int i = 0; i < oP.size(); i++) {
        CG_AbstractOpening aO = oP.get(i);

        RP_Opening rPO = new RP_Opening(aO, lCGA);

        this.bGRep.addChild(rPO.getBGRep());

      }

    }

    bS.setRepresentation(this);

  }

}
