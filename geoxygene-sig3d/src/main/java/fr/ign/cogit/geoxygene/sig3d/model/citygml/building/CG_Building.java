package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.core.AbstractCityObject;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Building extends CG_AbstractBuilding {

  public CG_Building(){
    super();
  }
  
  
  public CG_Building(Building build) {
    super(build);
  }

  @Override
  public AbstractCityObject export() {
    Building build  = new Building();
    this.complete(build);
    
    
    
    return build;
  }

}
