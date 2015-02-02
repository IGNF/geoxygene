package fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation;

import org.citygml4j.model.citygml.vegetation.PlantCover;
import org.citygml4j.model.citygml.vegetation.SolitaryVegetationObject;
import org.citygml4j.model.citygml.vegetation.VegetationObject;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractVegetationObject extends CG_CityObject {

  public CG_AbstractVegetationObject(VegetationObject aVO) {
    super(aVO);
  }

  public static CG_AbstractVegetationObject generateAbstractVegetationObject(
      VegetationObject vO) {

    if (vO instanceof SolitaryVegetationObject) {

      return new CG_SolitaryVegetationObject((SolitaryVegetationObject) vO);

    } else if (vO instanceof PlantCover) {
      return new CG_PlantCover((PlantCover) vO);
    }

    System.out.println("Class non reconnue : " + vO.getCityGMLClass());
    return null;
  }

}
