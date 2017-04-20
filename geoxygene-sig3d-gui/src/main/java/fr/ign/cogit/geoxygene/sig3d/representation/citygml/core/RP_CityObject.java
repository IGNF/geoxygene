package fr.ign.cogit.geoxygene.sig3d.representation.citygml.core;

import java.util.List;

import org.citygml4j.model.citygml.building.Room;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
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
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.generics.CG_GenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.landuse.CG_LandUse;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_ReliefFeature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_TINRelief;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_AuxiliaryTrafficArea;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_TrafficArea;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.transportation.CG_TransportationComplex;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation.CG_PlantCover;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.vegetation.CG_SolitaryVegetationObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_WaterBody;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.waterbody.CG_WaterBoundarySurface;
import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_AbstractBuilding;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_BoundarySurface;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_Building;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_BuildingFurniture;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_BuildingInstallation;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_BuildingInteriorInstallation;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_Opening;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.building.RP_Room;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.cityfurniture.RP_CityFurniture;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.cityobjectgroup.RP_CityObjectGroup;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.generics.RP_GenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.landuse.RP_LandUse;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.relief.RP_ReliefFeature;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.relief.RP_TINRelief;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.transportation.RP_AuxiliaryTrafficArea;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.transportation.RP_TrafficArea;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.transportation.RP_TransportationComplex;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.vegetation.RP_PlantCover;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.vegetation.RP_SolitaryVegetation;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.waterbody.RP_WaterBody;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.waterbody.RP_WaterBoundarySurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public class RP_CityObject extends Default3DRep {

	public static void generateCityObjectRepresentation(CG_CityObject cO) {

		RP_CityObject.generateCityObjectRepresentation(cO, null);
	}

	public static void generateCityObjectRepresentation(CG_CityObject cO, List<CG_AbstractSurfaceData> lCGA) {

		if (cO instanceof CG_Building) {
			new RP_Building((CG_Building) cO, lCGA);

			return;

		} else if (cO instanceof CG_Room) {

			new RP_Room((CG_Room) cO, lCGA);
		} else if (cO instanceof CG_IntBuildingInstallation) {

			new RP_BuildingInteriorInstallation((CG_IntBuildingInstallation) cO, lCGA);

		} else if (cO instanceof CG_Window) {

			new RP_Opening((CG_Window) cO, lCGA);

		} else if (cO instanceof CG_Door) {

			new RP_Opening((CG_Door) cO, lCGA);

		} else if (cO instanceof CG_BuildingFurniture) {

			new RP_BuildingFurniture((CG_BuildingFurniture) cO, lCGA);

		} else if (cO instanceof CG_BuildingInstallation) {
			new RP_BuildingInstallation((CG_BuildingInstallation) cO, lCGA);

		} else if (cO instanceof CG_AbstractBoundarySurface) {
			new RP_BoundarySurface((CG_AbstractBoundarySurface) cO, lCGA);

		} else if (cO instanceof CG_BuildingPart) {
			new RP_AbstractBuilding((CG_BuildingPart) cO, lCGA);

		} else if (cO instanceof CG_CityObjectGroup) {
			new RP_CityObjectGroup((CG_CityObjectGroup) cO, lCGA);

		} else if (cO instanceof CG_LandUse) {

			new RP_LandUse((CG_LandUse) cO, lCGA);
		} else if (cO instanceof CG_AuxiliaryTrafficArea) {

			new RP_AuxiliaryTrafficArea((CG_AuxiliaryTrafficArea) cO, lCGA);
		} else if (cO instanceof CG_TrafficArea) {

			new RP_TrafficArea((CG_TrafficArea) cO, lCGA);
		} else if (cO instanceof CG_TransportationComplex) {

			new RP_TransportationComplex((CG_TransportationComplex) cO, lCGA);

		} else if (cO instanceof CG_CityFurniture) {
			new RP_CityFurniture((CG_CityFurniture) cO, lCGA);

		} else if (cO instanceof CG_SolitaryVegetationObject) {
			new RP_SolitaryVegetation((CG_SolitaryVegetationObject) cO, lCGA);

		} else if (cO instanceof CG_PlantCover) {
			new RP_PlantCover((CG_PlantCover) cO, lCGA);

		} else if (cO instanceof CG_GenericCityObject) {
			new RP_GenericCityObject((CG_GenericCityObject) cO, lCGA);

		} else if (cO instanceof CG_WaterBody) {

			new RP_WaterBody((CG_WaterBody) cO, lCGA);

		} else if (cO instanceof CG_WaterBoundarySurface) {

			new RP_WaterBoundarySurface((CG_WaterBoundarySurface) cO, lCGA);

		} else if (cO instanceof CG_TINRelief) {

			new RP_TINRelief((CG_TINRelief) cO, lCGA);
		} else if (cO instanceof CG_ReliefFeature) {
			new RP_ReliefFeature((CG_ReliefFeature) cO, lCGA);

		}

		return;
	}

	/*
	 * 
	 * } else if (cO instanceof GenericCityObject) { cgCO = CG_GenericCityObject
	 * .generateCityObject(((GenericCityObject) cO));
	 * 
	 * 
	 * 
	 * } else if (cO instanceof ReliefFeature) {
	 * 
	 * cgCO = new CG_ReliefFeature((ReliefFeature) cO);
	 * 
	 * } else if (cO instanceof ReliefComponent) {
	 * 
	 * cgCO = CG_AbstractReliefComponent
	 * .generateReliefComponentType((ReliefComponent) cO);
	 * 
	 * 
	 * 
	 * 
	 * } else if (cO instanceof WaterObject) {
	 * 
	 * cgCO = CG_AbstractWaterObject .generateAbstractWaterObject((WaterObject)
	 * cO);
	 * 
	 * } else if (cO instanceof WaterBoundarySurface) {
	 * 
	 * cgCO = CG_WaterBoundarySurface
	 * .generateAbstractWaterBoundarySurface((WaterBoundarySurface) cO);
	 * 
	 * } else {
	 * 
	 * System.out.println("Non géré" + cO.getClass().toString()); }
	 */

}
