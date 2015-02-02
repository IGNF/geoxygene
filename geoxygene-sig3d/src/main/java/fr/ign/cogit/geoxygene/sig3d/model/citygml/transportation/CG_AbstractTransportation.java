package fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation;

import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.transportation.TrafficArea;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.citygml.transportation.TransportationObject;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractTransportation extends CG_CityObject {

  
  public CG_AbstractTransportation() {
    super();
  }
  
  public CG_AbstractTransportation(CityObject cO) {
    super(cO);
  }

  public static CG_AbstractTransportation generateAbstractTransportationObject(
      TransportationObject tO) {

    if (tO instanceof TrafficArea) {

      return new CG_TrafficArea((TrafficArea) tO);
    } else if (tO instanceof TransportationComplex) {
      return CG_TransportationComplex
          .generateTransportationComplex((TransportationComplex) tO);

    }

    System.out.println("Classe non gérée  " + tO.getCityGMLClass());
    return null;
  }

}
