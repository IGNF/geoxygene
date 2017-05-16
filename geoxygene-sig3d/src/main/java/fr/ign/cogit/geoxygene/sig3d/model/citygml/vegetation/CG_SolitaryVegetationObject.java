package fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.vegetation.SolitaryVegetationObject;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_RepresentationProperty;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_SolitaryVegetationObject extends CG_AbstractVegetationObject {

	public CG_SolitaryVegetationObject(SolitaryVegetationObject sV) {
		super(sV);

		if (sV.isSetClazz()) {
			this.setClazz(sV.getClazz());
		}

		if (sV.isSetFunction()) {
			this.getFunction().addAll(sV.getFunction());
		}

		if (sV.isSetSpecies()) {
			this.setSpecies(sV.getSpecies());
		}

		if (sV.isSetHeight()) {
			this.setHeight(sV.getHeight().getValue());
		}

		if (sV.isSetTrunkDiameter()) {
			this.setTrunkDiameter(sV.getTrunkDiameter().getValue());
		}

		if (sV.isSetCrownDiameter()) {
			this.setCrownDiameter(sV.getCrownDiameter().getValue());
		}

		if (sV.isSetLod1Geometry()) {
			this.setLod1Geometry(ConvertCityGMLtoGeometry.convertGMLGeometry(sV.getLod1Geometry()));
		}

		if (sV.isSetLod2Geometry()) {
			this.setLod2Geometry(ConvertCityGMLtoGeometry.convertGMLGeometry(sV.getLod2Geometry()));
		}

		if (sV.isSetLod3Geometry()) {
			this.setLod3Geometry(ConvertCityGMLtoGeometry.convertGMLGeometry(sV.getLod3Geometry()));
		}

		if (sV.isSetLod4Geometry()) {
			this.setLod4Geometry(ConvertCityGMLtoGeometry.convertGMLGeometry(sV.getLod4Geometry()));
		}

		if (sV.isSetLod1ImplicitRepresentation()) {

			this.setLod1ImplicitRepresentation(ConvertCityGMLtoGeometry.convertGMLGeometry(
					sV.getLod1ImplicitRepresentation().getImplicitGeometry().getRelativeGMLGeometry().getGeometry()));

		}

		if (sV.isSetLod2ImplicitRepresentation()) {
			this.setLod2ImplicitRepresentation(ConvertCityGMLtoGeometry.convertGMLGeometry(
					sV.getLod2ImplicitRepresentation().getImplicitGeometry().getRelativeGMLGeometry().getGeometry()));

		}

		if (sV.isSetLod3ImplicitRepresentation()) {
			this.setLod3ImplicitRepresentation(ConvertCityGMLtoGeometry.convertGMLGeometry(
					sV.getLod3ImplicitRepresentation().getImplicitGeometry().getRelativeGMLGeometry().getGeometry()));

		}

		if (sV.isSetLod4ImplicitRepresentation()) {
			this.setLod4ImplicitRepresentation(ConvertCityGMLtoGeometry.convertGMLGeometry(
					sV.getLod4ImplicitRepresentation().getImplicitGeometry().getRelativeGMLGeometry().getGeometry()));

		}

	}

	protected Code clazz;
	protected List<Code> function;
	protected Code species;
	protected Double height;
	protected Double trunkDiameter;
	protected Double crownDiameter;
	protected IGeometry lod1Geometry;
	protected IGeometry lod2Geometry;
	protected IGeometry lod3Geometry;
	protected IGeometry lod4Geometry;
	protected IGeometry lod1ImplicitRepresentation;
	protected IGeometry lod2ImplicitRepresentation;
	protected IGeometry lod3ImplicitRepresentation;
	protected IGeometry lod4ImplicitRepresentation;

	/**
	 * Gets the value of the clazz property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public Code getClazz() {
		return this.clazz;
	}

	/**
	 * Sets the value of the clazz property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setClazz(Code value) {
		this.clazz = value;
	}

	public boolean isSetClazz() {
		return (this.clazz != null);
	}

	/**
	 * Gets the value of the function property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the function property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFunction().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<Code> getFunction() {
		if (this.function == null) {
			this.function = new ArrayList<Code>();
		}
		return this.function;
	}

	public boolean isSetFunction() {
		return ((this.function != null) && (!this.function.isEmpty()));
	}

	public void unsetFunction() {
		this.function = null;
	}

	/**
	 * Gets the value of the species property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public Code getSpecies() {
		return this.species;
	}

	/**
	 * Sets the value of the species property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpecies(Code value) {
		this.species = value;
	}

	public boolean isSetSpecies() {
		return (this.species != null);
	}

	/**
	 * Gets the value of the height property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getHeight() {
		return this.height;
	}

	/**
	 * Sets the value of the height property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setHeight(Double value) {
		this.height = value;
	}

	public boolean isSetHeight() {
		return (this.height != null);
	}

	/**
	 * Gets the value of the trunkDiameter property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getTrunkDiameter() {
		return this.trunkDiameter;
	}

	/**
	 * Sets the value of the trunkDiameter property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setTrunkDiameter(Double value) {
		this.trunkDiameter = value;
	}

	public boolean isSetTrunkDiameter() {
		return (this.trunkDiameter != null);
	}

	/**
	 * Gets the value of the crownDiameter property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Double getCrownDiameter() {
		return this.crownDiameter;
	}

	/**
	 * Sets the value of the crownDiameter property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setCrownDiameter(Double value) {
		this.crownDiameter = value;
	}

	public boolean isSetCrownDiameter() {
		return (this.crownDiameter != null);
	}

	/**
	 * Gets the value of the lod1Geometry property.
	 * 
	 * @return possible object is {@link IGeometry }
	 * 
	 */
	public IGeometry getLod1Geometry() {
		return this.lod1Geometry;
	}

	/**
	 * Sets the value of the lod1Geometry property.
	 * 
	 * @param value
	 *            allowed object is {@link IGeometry }
	 * 
	 */
	public void setLod1Geometry(IGeometry value) {
		this.lod1Geometry = value;
	}

	public boolean isSetLod1Geometry() {
		return (this.lod1Geometry != null);
	}

	/**
	 * Gets the value of the lod2Geometry property.
	 * 
	 * @return possible object is {@link IGeometry }
	 * 
	 */
	public IGeometry getLod2Geometry() {
		return this.lod2Geometry;
	}

	/**
	 * Sets the value of the lod2Geometry property.
	 * 
	 * @param value
	 *            allowed object is {@link IGeometry }
	 * 
	 */
	public void setLod2Geometry(IGeometry value) {
		this.lod2Geometry = value;
	}

	public boolean isSetLod2Geometry() {
		return (this.lod2Geometry != null);
	}

	/**
	 * Gets the value of the lod3Geometry property.
	 * 
	 * @return possible object is {@link IGeometry }
	 * 
	 */
	public IGeometry getLod3Geometry() {
		return this.lod3Geometry;
	}

	/**
	 * Sets the value of the lod3Geometry property.
	 * 
	 * @param value
	 *            allowed object is {@link IGeometry }
	 * 
	 */
	public void setLod3Geometry(IGeometry value) {
		this.lod3Geometry = value;
	}

	public boolean isSetLod3Geometry() {
		return (this.lod3Geometry != null);
	}

	/**
	 * Gets the value of the lod4Geometry property.
	 * 
	 * @return possible object is {@link IGeometry }
	 * 
	 */
	public IGeometry getLod4Geometry() {
		return this.lod4Geometry;
	}

	/**
	 * Sets the value of the lod4Geometry property.
	 * 
	 * @param value
	 *            allowed object is {@link IGeometry }
	 * 
	 */
	public void setLod4Geometry(IGeometry value) {
		this.lod4Geometry = value;
	}

	public boolean isSetLod4Geometry() {
		return (this.lod4Geometry != null);
	}

	/**
	 * Gets the value of the lod1ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public IGeometry getLod1ImplicitRepresentation() {
		return this.lod1ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod1ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod1ImplicitRepresentation(IGeometry value) {
		this.lod1ImplicitRepresentation = value;
	}

	public boolean isSetLod1ImplicitRepresentation() {
		return (this.lod1ImplicitRepresentation != null);
	}

	/**
	 * Gets the value of the lod2ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public IGeometry getLod2ImplicitRepresentation() {
		return this.lod2ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod2ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod2ImplicitRepresentation(IGeometry value) {
		this.lod2ImplicitRepresentation = value;
	}

	public boolean isSetLod2ImplicitRepresentation() {
		return (this.lod2ImplicitRepresentation != null);
	}

	/**
	 * Gets the value of the lod3ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public IGeometry getLod3ImplicitRepresentation() {
		return this.lod3ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod3ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod3ImplicitRepresentation(IGeometry value) {
		this.lod3ImplicitRepresentation = value;
	}

	public boolean isSetLod3ImplicitRepresentation() {
		return (this.lod3ImplicitRepresentation != null);
	}

	/**
	 * Gets the value of the lod4ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public IGeometry getLod4ImplicitRepresentation() {
		return this.lod4ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod4ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod4ImplicitRepresentation(IGeometry value) {
		this.lod4ImplicitRepresentation = value;
	}

	public boolean isSetLod4ImplicitRepresentation() {
		return (this.lod4ImplicitRepresentation != null);
	}

	@Override
	public AbstractCityObject export() {
		// TODO Auto-generated method stub
		return null;
	}

}
