package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import org.citygml4j.model.citygml.building.Door;
import org.citygml4j.model.citygml.building.Window;
import org.citygml4j.model.citygml.core.AbstractCityObject;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Window extends CG_AbstractOpening {

	public CG_Window(Window w) {
		super(w);
	}

	@Override
	public AbstractCityObject export() {
		Door d = new Door();

		if (this.isSetLod3MultiSurface()) {

			d.setLod3MultiSurface(ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod3MultiSurface()));

		}

		if (this.isSetLod4MultiSurface()) {

			d.setLod4MultiSurface(ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod4MultiSurface()));

		}

		// TODO Auto-generated method stub
		return d;
	}

}
