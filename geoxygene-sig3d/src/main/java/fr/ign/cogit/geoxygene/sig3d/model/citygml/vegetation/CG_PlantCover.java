package fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.vegetation.PlantCover;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import net.opengis.gml.LengthType;

public class CG_PlantCover extends CG_AbstractVegetationObject {

	/**
	 * 
	 * @param pC
	 */
	public CG_PlantCover(PlantCover pC) {
		super(pC);

		if (pC.isSetClazz()) {
			this.setClazz(pC.getClazz());
		}

		if (pC.isSetFunction()) {
			this.getFunction().addAll(pC.getFunction());
		}

		if (pC.isSetAverageHeight()) {
			this.setAverageHeight(pC.getAverageHeight().getValue());
		}

		if (pC.isSetLod1MultiSurface()) {
			this.setLod1MultiSurface(ConvertCityGMLtoGeometry.convertGMLMultiSurface(pC.getLod1MultiSurface()));
		}

		if (pC.isSetLod2MultiSurface()) {
			this.setLod2MultiSurface(ConvertCityGMLtoGeometry.convertGMLMultiSurface(pC.getLod2MultiSurface()));
		}

		if (pC.isSetLod3MultiSurface()) {
			this.setLod3MultiSurface(ConvertCityGMLtoGeometry.convertGMLMultiSurface(pC.getLod3MultiSurface()));
		}

		if (pC.isSetLod4MultiSurface()) {
			this.setLod4MultiSurface(ConvertCityGMLtoGeometry.convertGMLMultiSurface(pC.getLod4MultiSurface()));
		}

		if (pC.isSetLod1MultiSolid()) {
			this.setLod1MultiSolid(
					ConvertCityGMLtoGeometry.convertGMLMultiSolid(pC.getLod1MultiSolid().getMultiSolid()));
		}

		if (pC.isSetLod2MultiSolid()) {
			this.setLod2MultiSolid(
					ConvertCityGMLtoGeometry.convertGMLMultiSolid(pC.getLod2MultiSolid().getMultiSolid()));
		}

		if (pC.isSetLod3MultiSolid()) {
			this.setLod3MultiSolid(
					ConvertCityGMLtoGeometry.convertGMLMultiSolid(pC.getLod3MultiSolid().getMultiSolid()));
		}

	}

	protected Code clazz;
	protected List<Code> function;
	protected Double averageHeight;
	protected IMultiSurface<IOrientableSurface> lod1MultiSurface;
	protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
	protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
	protected IMultiSurface<IOrientableSurface> lod4MultiSurface;
	protected IMultiSolid<ISolid> lod1MultiSolid;
	protected IMultiSolid<ISolid> lod2MultiSolid;
	protected IMultiSolid<ISolid> lod3MultiSolid;

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
	 * Gets the value of the averageHeight property.
	 * 
	 * @return possible object is {@link LengthType }
	 * 
	 */
	public Double getAverageHeight() {
		return this.averageHeight;
	}

	/**
	 * Sets the value of the averageHeight property.
	 * 
	 * @param value
	 *            allowed object is {@link LengthType }
	 * 
	 */
	public void setAverageHeight(double value) {
		this.averageHeight = value;
	}

	public boolean isSetAverageHeight() {
		return (this.averageHeight != null);
	}

	/**
	 * Gets the value of the lod1MultiSurface property.
	 * 
	 * @return possible object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public IMultiSurface<IOrientableSurface> getLod1MultiSurface() {
		return this.lod1MultiSurface;
	}

	/**
	 * Sets the value of the lod1MultiSurface property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public void setLod1MultiSurface(IMultiSurface<IOrientableSurface> value) {
		this.lod1MultiSurface = value;
	}

	public boolean isSetLod1MultiSurface() {
		return (this.lod1MultiSurface != null);
	}

	/**
	 * Gets the value of the lod2MultiSurface property.
	 * 
	 * @return possible object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public IMultiSurface<IOrientableSurface> getLod2MultiSurface() {
		return this.lod2MultiSurface;
	}

	/**
	 * Sets the value of the lod2MultiSurface property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public void setLod2MultiSurface(IMultiSurface<IOrientableSurface> value) {
		this.lod2MultiSurface = value;
	}

	public boolean isSetLod2MultiSurface() {
		return (this.lod2MultiSurface != null);
	}

	/**
	 * Gets the value of the lod3MultiSurface property.
	 * 
	 * @return possible object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public IMultiSurface<IOrientableSurface> getLod3MultiSurface() {
		return this.lod3MultiSurface;
	}

	/**
	 * Sets the value of the lod3MultiSurface property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public void setLod3MultiSurface(IMultiSurface<IOrientableSurface> value) {
		this.lod3MultiSurface = value;
	}

	public boolean isSetLod3MultiSurface() {
		return (this.lod3MultiSurface != null);
	}

	/**
	 * Gets the value of the lod4MultiSurface property.
	 * 
	 * @return possible object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public IMultiSurface<IOrientableSurface> getLod4MultiSurface() {
		return this.lod4MultiSurface;
	}

	/**
	 * Sets the value of the lod4MultiSurface property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSurface<IOrientableSurface> }
	 * 
	 */
	public void setLod4MultiSurface(IMultiSurface<IOrientableSurface> value) {
		this.lod4MultiSurface = value;
	}

	public boolean isSetLod4MultiSurface() {
		return (this.lod4MultiSurface != null);
	}

	/**
	 * Gets the value of the lod1MultiSolid property.
	 * 
	 * @return possible object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public IMultiSolid<ISolid> getLod1MultiSolid() {
		return this.lod1MultiSolid;
	}

	/**
	 * Sets the value of the lod1MultiSolid property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public void setLod1MultiSolid(IMultiSolid<ISolid> value) {
		this.lod1MultiSolid = value;
	}

	public boolean isSetLod1MultiSolid() {
		return (this.lod1MultiSolid != null);
	}

	/**
	 * Gets the value of the lod2MultiSolid property.
	 * 
	 * @return possible object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public IMultiSolid<ISolid> getLod2MultiSolid() {
		return this.lod2MultiSolid;
	}

	/**
	 * Sets the value of the lod2MultiSolid property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public void setLod2MultiSolid(IMultiSolid<ISolid> value) {
		this.lod2MultiSolid = value;
	}

	public boolean isSetLod2MultiSolid() {
		return (this.lod2MultiSolid != null);
	}

	/**
	 * Gets the value of the lod3MultiSolid property.
	 * 
	 * @return possible object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public IMultiSolid<ISolid> getLod3MultiSolid() {
		return this.lod3MultiSolid;
	}

	/**
	 * Sets the value of the lod3MultiSolid property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiSolid<ISolid> }
	 * 
	 */
	public void setLod3MultiSolid(IMultiSolid<ISolid> value) {
		this.lod3MultiSolid = value;
	}

	public boolean isSetLod3MultiSolid() {
		return (this.lod3MultiSolid != null);
	}

	@Override
	public AbstractCityObject export() {
		// TODO Auto-generated method stub
		return null;
	}

}
