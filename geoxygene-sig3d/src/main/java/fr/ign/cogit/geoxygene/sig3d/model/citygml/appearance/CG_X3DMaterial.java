package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.HashSet;

import javax.vecmath.Color3f;

import org.citygml4j.model.citygml.appearance.X3DMaterial;

public class CG_X3DMaterial extends CG_AbstractSurfaceData {

	protected Double ambientIntensity = 0.2;

	protected Color3f diffuseColor = new Color3f(0.8f, 0.8f, 0.8f);

	protected Color3f emissiveColor = new Color3f(0.0f, 0.0f, 0.0f);

	protected Color3f specularColor;

	protected Double shininess = 0.2;

	protected Double transparency = 0.0;

	protected Boolean isSmooth;

	protected HashSet<String> target;

	public CG_X3DMaterial() {
		super();
	}

	public CG_X3DMaterial(X3DMaterial sD) {
		super(sD);

		if (sD.isSetAmbientIntensity()) {
			this.setAmbientIntensity(sD.getAmbientIntensity());
		}

		if (sD.isSetDiffuseColor()) {
			this.diffuseColor = new Color3f(sD.getDiffuseColor().getRed().floatValue(),
					sD.getDiffuseColor().getGreen().floatValue(), sD.getDiffuseColor().getGreen().floatValue());
		}

		if (sD.isSetEmissiveColor()) {
			this.emissiveColor = new Color3f(sD.getEmissiveColor().getRed().floatValue(),
					sD.getDiffuseColor().getGreen().floatValue(), sD.getDiffuseColor().getGreen().floatValue());
		}

		if (sD.isSetSpecularColor()) {
			this.specularColor = new Color3f(sD.getSpecularColor().getRed().floatValue(),
					sD.getDiffuseColor().getGreen().floatValue(), sD.getDiffuseColor().getGreen().floatValue());
		}

		if (sD.isSetShininess()) {
			this.shininess = sD.getShininess();
		}

		if (sD.isSetTransparency()) {
			this.transparency = sD.getTransparency();
		}

		if (sD.isSetIsSmooth()) {
			this.isSmooth = sD.isSetIsSmooth();
		}

		if (sD.isSetTarget()) {
			this.getTarget().addAll(sD.getTarget());
		}

	}

	/**
	 * Gets the value of the ambientIntensity property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getAmbientIntensity() {
		return this.ambientIntensity;
	}

	/**
	 * Sets the value of the ambientIntensity property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setAmbientIntensity(Double value) {
		this.ambientIntensity = value;
	}

	public boolean isSetAmbientIntensity() {
		return (this.ambientIntensity != null);
	}

	/**
	 * Gets the value of the diffuseColor property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the diffuseColor property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDiffuseColor().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Double }
	 * 
	 * 
	 */
	public Color3f getDiffuseColor() {
		if (this.diffuseColor == null) {
			this.diffuseColor = new Color3f();
		}
		return this.diffuseColor;
	}

	public boolean isSetDiffuseColor() {
		return (this.diffuseColor != null);
	}

	public void unsetDiffuseColor() {
		this.diffuseColor = null;
	}

	/**
	 * Gets the value of the emissiveColor property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the emissiveColor property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getEmissiveColor().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Double }
	 * 
	 * 
	 */
	public Color3f getEmissiveColor() {
		if (this.emissiveColor == null) {
			this.emissiveColor = new Color3f();
		}
		return this.emissiveColor;
	}

	public boolean isSetEmissiveColor() {
		return (this.emissiveColor != null);
	}

	public void unsetEmissiveColor() {
		this.emissiveColor = null;
	}

	/**
	 * Gets the value of the specularColor property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the specularColor property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSpecularColor().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Double }
	 * 
	 * 
	 */
	public Color3f getSpecularColor() {
		if (this.specularColor == null) {
			this.specularColor = new Color3f();
		}
		return this.specularColor;
	}

	public boolean isSetSpecularColor() {
		return (this.specularColor != null);
	}

	public void unsetSpecularColor() {
		this.specularColor = null;
	}

	/**
	 * Gets the value of the shininess property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getShininess() {
		return this.shininess;
	}

	/**
	 * Sets the value of the shininess property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setShininess(Double value) {
		this.shininess = value;
	}

	public boolean isSetShininess() {
		return (this.shininess != null);
	}

	/**
	 * Gets the value of the transparency property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getTransparency() {
		return this.transparency;
	}

	/**
	 * Sets the value of the transparency property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setTransparency(Double value) {
		this.transparency = value;
	}

	public boolean isSetTransparency() {
		return (this.transparency != null);
	}

	/**
	 * Gets the value of the isSmooth property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public Boolean isIsSmooth() {
		return this.isSmooth;
	}

	/**
	 * Sets the value of the isSmooth property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setIsSmooth(Boolean value) {
		this.isSmooth = value;
	}

	public boolean isSetIsSmooth() {
		return (this.isSmooth != null);
	}

	/**
	 * Gets the value of the target property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the target property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTarget().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public HashSet<String> getTarget() {
		if (this.target == null) {
			this.target = new HashSet<String>();
		}
		return this.target;
	}

	public boolean isSetTarget() {
		return ((this.target != null) && (!this.target.isEmpty()));
	}

	public void unsetTarget() {
		this.target = null;
	}

}
