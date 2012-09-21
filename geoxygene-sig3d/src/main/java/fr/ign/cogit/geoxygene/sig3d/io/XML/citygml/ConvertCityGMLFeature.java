package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml;

import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityfurniture.CityFurniture;
import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.relief.ReliefComponent;
import org.citygml4j.model.citygml.relief.ReliefFeature;
import org.citygml4j.model.citygml.transportation.TransportationObject;
import org.citygml4j.model.citygml.vegetation.VegetationObject;
import org.citygml4j.model.citygml.waterbody.WaterBody;
import org.citygml4j.model.citygml.waterbody.WaterBoundarySurface;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertBuilding;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertCityFurniture;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertGenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertLandUse;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertRelief;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertTransportationObject;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertVegetation;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.ConvertWaterBody;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature.Util;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * Classe permettant de convertir un objet citygml en feature geoxygene Class to
 * convert a citygml object into a Geoxygene Feature
 * 
 */
public class ConvertCityGMLFeature {

  private ConvertCityGMLFeature() {
    super();
  }

  /**
   * Convertit une entité CityGML en entité GeOxygene :
   * 
   * @param cityObj l'objet que l'on cherche à convertir
   * @return une collection correspondant à l'objet
   * @throws Exception problème dans le LOD ou lors de la conversion
   */
  public static FT_FeatureCollection<IFeature> convertCityObject(
      CityObject cityObj) throws Exception {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    if (cityObj instanceof AbstractBuilding) {

      featCollGeox.addAll(ConvertBuilding
          .convertBuilding((AbstractBuilding) cityObj));

    } else if (cityObj instanceof CityFurniture) {

      CityFurniture cf = (CityFurniture) cityObj;
      featCollGeox.addAll(ConvertCityFurniture.convertCityFurniture(cf));

    } else if (cityObj instanceof GenericCityObject) {

      GenericCityObject gCO = (GenericCityObject) cityObj;
      featCollGeox.addAll(ConvertGenericCityObject
          .convertCityCityGenericObject(gCO));

    } else if (cityObj instanceof ReliefFeature) {

      ReliefFeature rf = (ReliefFeature) cityObj;
      featCollGeox.addAll(ConvertRelief.convertReliefFeature(rf));

    } else if (cityObj instanceof ReliefComponent) {

      ReliefComponent rC = (ReliefComponent) cityObj;
      featCollGeox.addAll(ConvertRelief.convertReliefComponent(rC));

    } else if (cityObj instanceof LandUse) {

      featCollGeox.addAll(ConvertLandUse.convertLandUse((LandUse) cityObj));

    } else if (cityObj instanceof TransportationObject) {

      featCollGeox.addAll(ConvertTransportationObject
          .convertTransportationObject((TransportationObject) cityObj));

    } else if (cityObj instanceof VegetationObject) {

      featCollGeox.addAll(ConvertVegetation
          .convertVegetationObject((VegetationObject) cityObj));

    } else if (cityObj instanceof WaterBody) {

      featCollGeox.addAll(ConvertWaterBody
          .convertWaterBody((WaterBody) cityObj));

    } else if (cityObj instanceof WaterBoundarySurface) {

      featCollGeox.addAll(ConvertWaterBody
          .convertWaterBoundarySurface((WaterBoundarySurface) cityObj));
    }

    if (!ParserCityGMLV2.LOADGEOXGEOM) {
      Util.transformGeomToPoint(featCollGeox);
    }
    return featCollGeox;
  }

}
