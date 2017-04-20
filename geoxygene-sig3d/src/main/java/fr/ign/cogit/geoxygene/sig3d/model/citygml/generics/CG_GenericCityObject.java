package fr.ign.cogit.geoxygene.sig3d.model.citygml.generics;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.gml.basicTypes.Code;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_RepresentationProperty;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_GenericCityObject extends CG_CityObject {

	public CG_GenericCityObject(GenericCityObject gCO) {

		super(gCO);

		this.clazz = gCO.getClazz();
		this.function = gCO.getFunction();
		this.usage = gCO.getUsage();

		if (gCO.isSetLod1TerrainIntersection()) {
			this.lod1TerrainIntersection = ConvertyCityGMLGeometry
					.convertGMLMultiCurve(gCO.getLod1TerrainIntersection());
		}

		if (gCO.isSetLod2TerrainIntersection()) {
			this.lod2TerrainIntersection = ConvertyCityGMLGeometry
					.convertGMLMultiCurve(gCO.getLod2TerrainIntersection());
		}

		if (gCO.isSetLod3TerrainIntersection()) {
			this.lod3TerrainIntersection = ConvertyCityGMLGeometry
					.convertGMLMultiCurve(gCO.getLod3TerrainIntersection());
		}

		if (gCO.isSetLod4TerrainIntersection()) {
			this.lod4TerrainIntersection = ConvertyCityGMLGeometry
					.convertGMLMultiCurve(gCO.getLod4TerrainIntersection());
		}

		if (gCO.isSetLod0Geometry()) {

			this.lod0Geometry = ConvertyCityGMLGeometry.convertGMLGeometry(gCO.getLod0Geometry());
		}

		if (gCO.isSetLod1Geometry()) {

			this.lod1Geometry = ConvertyCityGMLGeometry.convertGMLGeometry(gCO.getLod1Geometry());
		}

		if (gCO.isSetLod2Geometry()) {

			this.lod2Geometry = ConvertyCityGMLGeometry.convertGMLGeometry(gCO.getLod2Geometry());
		}

		if (gCO.isSetLod3Geometry()) {

			this.lod3Geometry = ConvertyCityGMLGeometry.convertGMLGeometry(gCO.getLod3Geometry());
		}

		if (gCO.isSetLod4Geometry()) {

			this.lod4Geometry = ConvertyCityGMLGeometry.convertGMLGeometry(gCO.getLod4Geometry());
		}

		if (gCO.isSetLod0ImplicitRepresentation()) {
			System.out.println("Non gestion des géométries implicites : CG_GenericCityObject");
		}

		if (gCO.isSetLod1ImplicitRepresentation()) {
			System.out.println("Non gestion des géométries implicites : CG_GenericCityObject");
		}

		if (gCO.isSetLod2ImplicitRepresentation()) {
			System.out.println("Non gestion des géométries implicites : CG_GenericCityObject");
		}

		if (gCO.isSetLod3ImplicitRepresentation()) {
			System.out.println("Non gestion des géométries implicites: CG_GenericCityObject");
		}

		if (gCO.isSetLod4ImplicitRepresentation()) {
			System.out.println("Non gestion des géométries implicites : CG_GenericCityObject");
		}
	}

	protected Code clazz;
	protected List<Code> function;
	protected List<Code> usage;
	protected IGeometry lod0Geometry;
	protected IGeometry lod1Geometry;
	protected IGeometry lod2Geometry;
	protected IGeometry lod3Geometry;
	protected IGeometry lod4Geometry;
	protected IMultiCurve<IOrientableCurve> lod0TerrainIntersection;
	protected IMultiCurve<IOrientableCurve> lod1TerrainIntersection;
	protected IMultiCurve<IOrientableCurve> lod2TerrainIntersection;
	protected IMultiCurve<IOrientableCurve> lod3TerrainIntersection;
	protected IMultiCurve<IOrientableCurve> lod4TerrainIntersection;
	protected CG_RepresentationProperty lod0ImplicitRepresentation;
	protected CG_RepresentationProperty lod1ImplicitRepresentation;
	protected CG_RepresentationProperty lod2ImplicitRepresentation;
	protected CG_RepresentationProperty lod3ImplicitRepresentation;
	protected CG_RepresentationProperty lod4ImplicitRepresentation;

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
	 * Gets the value of the usage property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the usage property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getUsage().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
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

	/**
	 * Gets the value of the lod0Geometry property.
	 * 
	 * @return possible object is {@link IGeometry }
	 * 
	 */
	public IGeometry getLod0Geometry() {
		return this.lod0Geometry;
	}

	/**
	 * Sets the value of the lod0Geometry property.
	 * 
	 * @param value
	 *            allowed object is {@link IGeometry }
	 * 
	 */
	public void setLod0Geometry(IGeometry value) {
		this.lod0Geometry = value;
	}

	public boolean isSetLod0Geometry() {
		return (this.lod0Geometry != null);
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
	 * Gets the value of the lod0TerrainIntersection property.
	 * 
	 * @return possible object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public IMultiCurve<IOrientableCurve> getLod0TerrainIntersection() {
		return this.lod0TerrainIntersection;
	}

	/**
	 * Sets the value of the lod0TerrainIntersection property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public void setLod0TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
		this.lod0TerrainIntersection = value;
	}

	public boolean isSetLod0TerrainIntersection() {
		return (this.lod0TerrainIntersection != null);
	}

	/**
	 * Gets the value of the lod1TerrainIntersection property.
	 * 
	 * @return possible object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public IMultiCurve<IOrientableCurve> getLod1TerrainIntersection() {
		return this.lod1TerrainIntersection;
	}

	/**
	 * Sets the value of the lod1TerrainIntersection property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public void setLod1TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
		this.lod1TerrainIntersection = value;
	}

	public boolean isSetLod1TerrainIntersection() {
		return (this.lod1TerrainIntersection != null);
	}

	/**
	 * Gets the value of the lod2TerrainIntersection property.
	 * 
	 * @return possible object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public IMultiCurve<IOrientableCurve> getLod2TerrainIntersection() {
		return this.lod2TerrainIntersection;
	}

	/**
	 * Sets the value of the lod2TerrainIntersection property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public void setLod2TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
		this.lod2TerrainIntersection = value;
	}

	public boolean isSetLod2TerrainIntersection() {
		return (this.lod2TerrainIntersection != null);
	}

	/**
	 * Gets the value of the lod3TerrainIntersection property.
	 * 
	 * @return possible object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public IMultiCurve<IOrientableCurve> getLod3TerrainIntersection() {
		return this.lod3TerrainIntersection;
	}

	/**
	 * Sets the value of the lod3TerrainIntersection property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public void setLod3TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
		this.lod3TerrainIntersection = value;
	}

	public boolean isSetLod3TerrainIntersection() {
		return (this.lod3TerrainIntersection != null);
	}

	/**
	 * Gets the value of the lod4TerrainIntersection property.
	 * 
	 * @return possible object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public IMultiCurve<IOrientableCurve> getLod4TerrainIntersection() {
		return this.lod4TerrainIntersection;
	}

	/**
	 * Sets the value of the lod4TerrainIntersection property.
	 * 
	 * @param value
	 *            allowed object is {@link IMultiCurve<IOrientableCurve> }
	 * 
	 */
	public void setLod4TerrainIntersection(IMultiCurve<IOrientableCurve> value) {
		this.lod4TerrainIntersection = value;
	}

	public boolean isSetLod4TerrainIntersection() {
		return (this.lod4TerrainIntersection != null);
	}

	/**
	 * Gets the value of the lod0ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public CG_RepresentationProperty getLod0ImplicitRepresentation() {
		return this.lod0ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod0ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod0ImplicitRepresentation(CG_RepresentationProperty value) {
		this.lod0ImplicitRepresentation = value;
	}

	public boolean isSetLod0ImplicitRepresentation() {
		return (this.lod0ImplicitRepresentation != null);
	}

	/**
	 * Gets the value of the lod1ImplicitRepresentation property.
	 * 
	 * @return possible object is {@link CG_RepresentationProperty }
	 * 
	 */
	public CG_RepresentationProperty getLod1ImplicitRepresentation() {
		return this.lod1ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod1ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod1ImplicitRepresentation(CG_RepresentationProperty value) {
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
	public CG_RepresentationProperty getLod2ImplicitRepresentation() {
		return this.lod2ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod2ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod2ImplicitRepresentation(CG_RepresentationProperty value) {
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
	public CG_RepresentationProperty getLod3ImplicitRepresentation() {
		return this.lod3ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod3ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod3ImplicitRepresentation(CG_RepresentationProperty value) {
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
	public CG_RepresentationProperty getLod4ImplicitRepresentation() {
		return this.lod4ImplicitRepresentation;
	}

	/**
	 * Sets the value of the lod4ImplicitRepresentation property.
	 * 
	 * @param value
	 *            allowed object is {@link CG_RepresentationProperty }
	 * 
	 */
	public void setLod4ImplicitRepresentation(CG_RepresentationProperty value) {
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
