package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.WorldToTexture;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_WorldToTexture {

  protected List<Double> value;
  protected String srsName;
  protected Integer srsDimension;
  protected List<String> axisLabels;
  protected List<String> uomLabels;

  public CG_WorldToTexture(WorldToTexture wTT) {

    if (wTT.isSetUomLabels()) {
      this.getUomLabels().addAll(wTT.getUomLabels());
    }

    if (wTT.isSetAxisLabels()) {
      this.getAxisLabels().addAll(wTT.getAxisLabels());
    }

    if (wTT.isSetSrsDimension()) {
      this.srsDimension = wTT.getSrsDimension();
    }

    if (wTT.isSetSrsName()) {
      this.srsName = wTT.getSrsName();
    }

  }

  public List<Double> getValue() {
    if (this.value == null) {
      this.value = new ArrayList<Double>();
    }
    return this.value;
  }

  public boolean isSetValue() {
    return ((this.value != null) && (!this.value.isEmpty()));
  }

  public void unsetValue() {
    this.value = null;
  }

  /**
   * Gets the value of the srsName property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getSrsName() {
    return this.srsName;
  }

  /**
   * Sets the value of the srsName property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setSrsName(String value) {
    this.srsName = value;
  }

  public boolean isSetSrsName() {
    return (this.srsName != null);
  }

  /**
   * Gets the value of the srsDimension property.
   * 
   * @return possible object is {@link BigInteger }
   * 
   */
  public Integer getSrsDimension() {
    return this.srsDimension;
  }

  /**
   * Sets the value of the srsDimension property.
   * 
   * @param value allowed object is {@link BigInteger }
   * 
   */
  public void setSrsDimension(Integer value) {
    this.srsDimension = value;
  }

  public boolean isSetSrsDimension() {
    return (this.srsDimension != null);
  }

  public List<String> getAxisLabels() {
    if (this.axisLabels == null) {
      this.axisLabels = new ArrayList<String>();
    }
    return this.axisLabels;
  }

  public boolean isSetAxisLabels() {
    return ((this.axisLabels != null) && (!this.axisLabels.isEmpty()));
  }

  public void unsetAxisLabels() {
    this.axisLabels = null;
  }

  public List<String> getUomLabels() {
    if (this.uomLabels == null) {
      this.uomLabels = new ArrayList<String>();
    }
    return this.uomLabels;
  }

  public boolean isSetUomLabels() {
    return ((this.uomLabels != null) && (!this.uomLabels.isEmpty()));
  }

  public void unsetUomLabels() {
    this.uomLabels = null;
  }

}
