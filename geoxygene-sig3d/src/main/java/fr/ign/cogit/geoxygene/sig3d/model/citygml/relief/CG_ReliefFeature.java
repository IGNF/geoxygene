package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.relief.ReliefFeature;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;

public class CG_ReliefFeature extends CG_CityObject {

  public CG_ReliefFeature(ReliefFeature rF) {
    super(rF);
    if (rF.isSetLod()) {
      this.setLod(rF.getLod());
    }

    if (rF.isSetReliefComponent()) {

      int nbComponent = rF.getReliefComponent().size();

      for (int i = 0; i < nbComponent; i++) {

        this.getReliefComponent().add(
            CG_AbstractReliefComponent.generateReliefComponentType(rF
                .getReliefComponent().get(i).getReliefComponent()));
      }
    }
  }

  protected int lod;
  protected List<CG_AbstractReliefComponent> reliefComponent;

  /**
   * Gets the value of the lod property.
   * 
   */
  public int getLod() {
    return this.lod;
  }

  /**
   * Sets the value of the lod property.
   * 
   */
  public void setLod(int value) {
    this.lod = value;
  }

  public boolean isSetLod() {
    return true;
  }

  public List<CG_AbstractReliefComponent> getReliefComponent() {
    if (this.reliefComponent == null) {
      this.reliefComponent = new ArrayList<CG_AbstractReliefComponent>();
    }
    return this.reliefComponent;
  }

  public boolean isSetReliefComponent() {
    return ((this.reliefComponent != null) && (!this.reliefComponent.isEmpty()));
  }

  public void unsetReliefComponent() {
    this.reliefComponent = null;
  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }

}
