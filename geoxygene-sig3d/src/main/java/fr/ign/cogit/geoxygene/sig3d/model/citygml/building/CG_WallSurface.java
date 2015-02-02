package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.model.citygml.building.WallSurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_WallSurface extends CG_AbstractBoundarySurface {
  
  public CG_WallSurface(){
    super();
  }

  public CG_WallSurface(WallSurface w) {
    super(w);
  }
}
