package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.citygml4j.model.citygml.building.AbstractBoundarySurface;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.building.BuildingFurniture;
import org.citygml4j.model.citygml.building.BuildingInstallation;
import org.citygml4j.model.citygml.building.BuildingPart;
import org.citygml4j.model.citygml.building.Door;
import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.building.Room;
import org.citygml4j.model.citygml.building.Window;
import org.citygml4j.model.citygml.cityfurniture.CityFurniture;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.relief.AbstractReliefComponent;
import org.citygml4j.model.citygml.relief.ReliefFeature;
import org.citygml4j.model.citygml.transportation.AbstractTransportationObject;
import org.citygml4j.model.citygml.vegetation.AbstractVegetationObject;
import org.citygml4j.model.citygml.waterbody.AbstractWaterBoundarySurface;
import org.citygml4j.model.citygml.waterbody.AbstractWaterObject;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AppearanceProperty;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractBoundarySurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Building;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_BuildingFurniture;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_BuildingInstallation;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_BuildingPart;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Door;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_IntBuildingInstallation;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Room;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_Window;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.cityfurniture.CG_CityFurniture;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.cityobjectgroup.CG_CityObjectGroup;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.generics.CG_GenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.landuse.CG_LandUse;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_AbstractReliefComponent;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_ReliefFeature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_AbstractTransportation;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation.CG_AbstractVegetationObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_AbstractWaterObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_WaterBoundarySurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_CityObject extends DefaultFeature {

	private List<CG_AppearanceProperty> appearanceProperty = null;

	public static int ID_COUNT = 0;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CG_CityObject() {
		super();
		id = ID_COUNT++;
	}

	public List<CG_AppearanceProperty> getAppearanceProperty() {
		if (this.appearanceProperty == null) {
			this.appearanceProperty = new ArrayList<CG_AppearanceProperty>();
		}
		return this.appearanceProperty;
	}

	public abstract AbstractCityObject export();

	public CG_CityObject(AbstractCityObject cO) {
		super();
		this.creationDate = cO.getCreationDate();
		this.terminationDate = cO.getTerminationDate();

		if (cO.isSetAppearance()) {
			int nbAp = cO.getAppearance().size();

			for (int i = 0; i < nbAp; i++) {
				this.getAppearanceProperty().add(new CG_AppearanceProperty(cO.getAppearance().get(i)));

			}

		}

		int nbExtRef = cO.getExternalReference().size();

		for (int i = 0; i < nbExtRef; i++) {

			this.getExternalReference().add(new CG_ExternalReference(cO.getExternalReference().get(i)));
		}

		int nbGRT = cO.getGeneralizesTo().size();
		for (int i = 0; i < nbGRT; i++) {

			this.getGeneralizesTo().add(new CG_GeneralizationRelation(cO.getGeneralizesTo().get(i)));
		}

	}

	public static CG_CityObject generateCityObject(AbstractCityObject cO) {

		CG_CityObject cgCO = null;

		if (cO instanceof Room) {
			cgCO = new CG_Room((Room) cO);
		} else if (cO instanceof IntBuildingInstallation) {

			cgCO = new CG_IntBuildingInstallation((IntBuildingInstallation) cO);

		} else if (cO instanceof Window) {
			cgCO = new CG_Window((Window) cO);

		} else if (cO instanceof Door) {
			cgCO = new CG_Door((Door) cO);

		} else if (cO instanceof BuildingFurniture) {
			cgCO = new CG_BuildingFurniture((BuildingFurniture) cO);

		} else if (cO instanceof CityFurniture) {
			cgCO = new CG_CityFurniture((CityFurniture) cO);

		} else if (cO instanceof BuildingInstallation) {
			cgCO = new CG_BuildingInstallation((BuildingInstallation) cO);

		} else if (cO instanceof AbstractBoundarySurface) {
			cgCO = CG_AbstractBoundarySurface.generateBoundarySurface((AbstractBoundarySurface) cO);

		} else if (cO instanceof CityObjectGroup) {
			cgCO = new CG_CityObjectGroup((CityObjectGroup) cO);

		} else if (cO instanceof Building) {
			cgCO = new CG_Building((Building) cO);

		} else if (cO instanceof BuildingPart) {
			cgCO = new CG_BuildingPart((BuildingPart) cO);

		} else if (cO instanceof IntBuildingInstallation) {
			cgCO = new CG_IntBuildingInstallation((IntBuildingInstallation) cO);

		} else if (cO instanceof GenericCityObject) {
			cgCO = new CG_GenericCityObject((GenericCityObject) cO);

		} else if (cO instanceof LandUse) {
			cgCO = new CG_LandUse((LandUse) cO);

		} else if (cO instanceof ReliefFeature) {

			cgCO = new CG_ReliefFeature((ReliefFeature) cO);

		} else if (cO instanceof AbstractReliefComponent) {

			cgCO = CG_AbstractReliefComponent.generateReliefComponentType((AbstractReliefComponent) cO);

		} else if (cO instanceof AbstractTransportationObject) {

			cgCO = CG_AbstractTransportation.generateAbstractTransportationObject((AbstractTransportationObject) cO);

		} else if (cO instanceof AbstractVegetationObject) {

			cgCO = CG_AbstractVegetationObject.generateAbstractVegetationObject((AbstractVegetationObject) cO);

		} else if (cO instanceof AbstractWaterObject) {

			cgCO = CG_AbstractWaterObject.generateAbstractWaterObject((AbstractWaterObject) cO);

		} else if (cO instanceof AbstractWaterBoundarySurface) {

			cgCO = CG_WaterBoundarySurface.generateAbstractWaterBoundarySurface((AbstractWaterBoundarySurface) cO);

		} else {

			System.out.println("Non géré" + cO.getClass().toString());
		}

		return cgCO;

	}

	protected GregorianCalendar creationDate;
	protected GregorianCalendar terminationDate;
	protected List<CG_ExternalReference> externalReference;
	protected List<CG_GeneralizationRelation> generalizesTo;

	public GregorianCalendar getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(GregorianCalendar value) {
		this.creationDate = value;
	}

	public boolean isSetCreationDate() {
		return (this.creationDate != null);
	}

	public GregorianCalendar getTerminationDate() {
		return this.terminationDate;
	}

	public void setTerminationDate(GregorianCalendar value) {
		this.terminationDate = value;
	}

	public boolean isSetTerminationDate() {
		return (this.terminationDate != null);
	}

	public List<CG_ExternalReference> getExternalReference() {
		if (this.externalReference == null) {
			this.externalReference = new ArrayList<CG_ExternalReference>();
		}
		return this.externalReference;
	}

	public boolean isSetExternalReference() {
		return ((this.externalReference != null) && (!this.externalReference.isEmpty()));
	}

	public void unsetExternalReference() {
		this.externalReference = null;
	}

	public List<CG_GeneralizationRelation> getGeneralizesTo() {
		if (this.generalizesTo == null) {
			this.generalizesTo = new ArrayList<CG_GeneralizationRelation>();
		}
		return this.generalizesTo;
	}

	public boolean isSetGeneralizesTo() {
		return ((this.generalizesTo != null) && (!this.generalizesTo.isEmpty()));
	}

	public void unsetGeneralizesTo() {
		this.generalizesTo = null;
	}

}
