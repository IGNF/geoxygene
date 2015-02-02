package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.impl.citygml.building.BuildingImpl;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.core.CityObject;

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
  public CityObject export() {
    Building build  = new BuildingImpl();
    this.complete(build);
    
    
    
    return build;
  }

}
