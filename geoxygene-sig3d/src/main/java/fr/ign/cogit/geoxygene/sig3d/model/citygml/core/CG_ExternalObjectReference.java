package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import org.citygml4j.model.citygml.core.ExternalObject;

public class CG_ExternalObjectReference {

  public CG_ExternalObjectReference(ExternalObject exO) {
    this.setName(exO.getName());
    this.setUri(exO.getUri());

  }

  protected String name;

  protected String uri;

  public String getName() {
    return this.name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public boolean isSetName() {
    return (this.name != null);
  }

  public String getUri() {
    return this.uri;
  }

  public void setUri(String value) {
    this.uri = value;
  }

  public boolean isSetUri() {
    return (this.uri != null);
  }

}
