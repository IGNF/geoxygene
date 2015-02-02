package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import org.citygml4j.model.citygml.core.ExternalReference;

public class CG_ExternalReference {

  public CG_ExternalReference(ExternalReference extRef) {

    this.setInformationSystem(extRef.getInformationSystem());
    this.setExternalObject(new CG_ExternalObjectReference(extRef
        .getExternalObject()));

  }

  protected String informationSystem;

  protected CG_ExternalObjectReference externalObject;

  public String getInformationSystem() {
    return this.informationSystem;
  }

  public void setInformationSystem(String value) {
    this.informationSystem = value;
  }

  public boolean isSetInformationSystem() {
    return (this.informationSystem != null);
  }

  public CG_ExternalObjectReference getExternalObject() {
    return this.externalObject;
  }

  public void setExternalObject(CG_ExternalObjectReference value) {
    this.externalObject = value;
  }

  public boolean isSetExternalObject() {
    return (this.externalObject != null);
  }

}
