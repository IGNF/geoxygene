package fr.ign.cogit.geoxygene.sig3d.representation.citygml.building;

import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Building;

public class RP_Building extends RP_AbstractBuilding {

  public RP_Building(CG_Building cO) {
    super(cO);

  }

  public RP_Building(CG_Building cO, List<CG_AbstractSurfaceData> lCGA) {
    super(cO, lCGA);
  }

}
