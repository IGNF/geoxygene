package fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation;

import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.transportation.Road;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Road extends CG_TransportationComplex {
  
  
  public CG_Road(){
    super();
  }

  public CG_Road(Road tO) {
    super(tO);
  }

  @Override
  public CityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
