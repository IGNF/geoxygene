package fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody;

import org.citygml4j.model.citygml.waterbody.WaterBody;
import org.citygml4j.model.citygml.waterbody.WaterBoundarySurface;
import org.citygml4j.model.citygml.waterbody.WaterObject;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;

public abstract class CG_AbstractWaterObject extends CG_CityObject {

  public CG_AbstractWaterObject(WaterObject aWO) {
    super(aWO);
  }

  public static CG_CityObject generateAbstractWaterObject(WaterObject aWO) {

    if (aWO instanceof WaterBody) {

      return new CG_WaterBody((WaterBody) aWO);

    } else if (aWO instanceof WaterBoundarySurface) {

      WaterBoundarySurface wBS = (WaterBoundarySurface) aWO;

      return CG_WaterBoundarySurface.generateAbstractWaterBoundarySurface(wBS);

    }

    return null;
  }

}
