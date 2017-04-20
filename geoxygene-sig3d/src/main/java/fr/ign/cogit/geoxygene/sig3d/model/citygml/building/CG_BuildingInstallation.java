package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.building.BuildingInstallation;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_BuildingInstallation extends CG_CityObject {

	protected Code clazz;
	protected List<Code> function;
	protected List<Code> usage;
	protected IGeometry lod2Geometry;
	protected IGeometry lod3Geometry;
	protected IGeometry lod4Geometry;

	public CG_BuildingInstallation(BuildingInstallation bI) {

		super(bI);

		if (bI.isSetLod2Geometry()) {
			this.setLod2Geometry(ConvertyCityGMLGeometry.convertGMLGeometry(bI.getLod2Geometry()));
		}
		if (bI.isSetLod3Geometry()) {
			this.setLod3Geometry(ConvertyCityGMLGeometry.convertGMLGeometry(bI.getLod3Geometry()));
		}

		if (bI.isSetLod4Geometry()) {
			this.setLod4Geometry(ConvertyCityGMLGeometry.convertGMLGeometry(bI.getLod4Geometry()));
		}

		if (bI.isSetClazz()) {
			this.setClazz(bI.getClazz());
		}

		if (bI.isSetUsage()) {
			this.getUsage().addAll(bI.getUsage());
		}

		if (bI.isSetFunction()) {
			this.getFunction().addAll(bI.getFunction());
		}

	}

	@Override
	public AbstractCityObject export() {

		BuildingInstallation bIOut = new BuildingInstallation();

		if (this.isSetLod2Geometry()) {

			bIOut.setLod2Geometry(ConvertToCityGMLGeometry.convertGeometryProperty(this.getLod2Geometry()));

		}

		if (this.isSetLod3Geometry()) {

			bIOut.setLod3Geometry(ConvertToCityGMLGeometry.convertGeometryProperty(this.getLod3Geometry()));

		}

		if (this.isSetLod4Geometry()) {

			bIOut.setLod4Geometry(ConvertToCityGMLGeometry.convertGeometryProperty(this.getLod4Geometry()));

		}

		if (this.isSetClazz()) {
			bIOut.setClazz(this.getClazz());
		}

		if (this.isSetUsage()) {
			bIOut.setUsage(this.getUsage());
		}

		if (this.isSetFunction()) {
			bIOut.setFunction(this.getFunction());
		}

		return bIOut;
	}

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

	public List<Code> getUsage() {
		if (this.usage == null) {
			this.usage = new ArrayList<Code>();
		}
		return this.usage;
	}

	public boolean isSetUsage() {
		return ((this.usage != null) && (!this.usage.isEmpty()));
	}

	public void unsetUsage() {
		this.usage = null;
	}

	public IGeometry getLod2Geometry() {
		return this.lod2Geometry;
	}

	public void setLod2Geometry(IGeometry value) {
		this.lod2Geometry = value;
	}

	public boolean isSetLod2Geometry() {
		return (this.lod2Geometry != null);
	}

	public IGeometry getLod3Geometry() {
		return this.lod3Geometry;
	}

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

	public void setLod4Geometry(IGeometry value) {
		this.lod4Geometry = value;
	}

	public boolean isSetLod4Geometry() {
		return (this.lod4Geometry != null);
	}

}
