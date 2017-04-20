package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;

import org.citygml4j.model.citygml.relief.AbstractReliefComponent;
import org.citygml4j.model.citygml.relief.BreaklineRelief;
import org.citygml4j.model.citygml.relief.MassPointRelief;
import org.citygml4j.model.citygml.relief.RasterRelief;
import org.citygml4j.model.citygml.relief.TINRelief;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractReliefComponent extends CG_CityObject {

	protected int lod;
	protected IEnvelope extent;

	public CG_AbstractReliefComponent(AbstractReliefComponent rC) {
		super(rC);
		this.setLod(rC.getLod());

		if (rC.isSetExtent()) {
			GM_Polygon pol = ConvertCityGMLtoGeometry.convertGMLPolygon(rC.getExtent().getPolygon());
			if(pol != null){
				this.setExtent(pol.envelope());
			}
		
		}

	}

	public static CG_AbstractReliefComponent generateReliefComponentType(AbstractReliefComponent rC) {

		if (rC instanceof TINRelief) {

			return new CG_TINRelief((TINRelief) rC);

		} else if (rC instanceof MassPointRelief) {

			return new CG_MassPointRelief((MassPointRelief) rC);

		} else if (rC instanceof BreaklineRelief) {

			return new CG_BreaklineRelief((BreaklineRelief) rC);

		} else if (rC instanceof RasterRelief) {

			return new CG_RasterRelief((RasterRelief) rC);

		} else {
			System.out.println("Classe non gérée :" + rC.getClass().getCanonicalName());
		}

		return null;

	}

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

	/**
	 * Gets the value of the extent property.
	 * 
	 * @return possible object is {@link GM_Envelope }
	 * 
	 */
	public IEnvelope getExtent() {
		return this.extent;
	}

	/**
	 * Sets the value of the extent property.
	 * 
	 * @param value
	 *            allowed object is {@link GM_Envelope }
	 * 
	 */
	public void setExtent(IEnvelope value) {
		this.extent = value;
	}

	public boolean isSetExtent() {
		return (this.extent != null);
	}

}
