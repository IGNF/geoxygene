package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.impl.citygml.building.BuildingPartImpl;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.core.CityObject;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_BuildingPart extends CG_AbstractBuilding {

  public CG_BuildingPart(BuildingPart bP) {

    super(bP);

  }
  
  
  
  public CG_BuildingPart(){
    super();
  }
  
  

  @Override
  public CityObject export() {
    BuildingPart build = new BuildingPartImpl();
    this.complete(build);

    return build;
  }
}
