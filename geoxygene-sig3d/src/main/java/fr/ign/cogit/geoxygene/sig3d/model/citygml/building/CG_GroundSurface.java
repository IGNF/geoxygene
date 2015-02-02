package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.model.citygml.building.GroundSurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_GroundSurface extends CG_AbstractBoundarySurface {
  
  public CG_GroundSurface(){
    super();
  }

  public CG_GroundSurface(GroundSurface gS) {
    super(gS);
  }

}
