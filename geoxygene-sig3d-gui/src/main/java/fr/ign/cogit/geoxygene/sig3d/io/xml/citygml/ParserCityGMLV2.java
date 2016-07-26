package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.appearance.AppearanceMember;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.cityfurniture.CityFurniture;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.citygml.relief.ReliefComponent;
import org.citygml4j.model.citygml.relief.ReliefFeature;
import org.citygml4j.model.citygml.transportation.TransportationObject;
import org.citygml4j.model.citygml.vegetation.VegetationObject;
import org.citygml4j.model.citygml.waterbody.WaterBody;
import org.citygml4j.model.citygml.waterbody.WaterBoundarySurface;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertBuilding;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertCityFurniture;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertGenericCityObject;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertLandUse;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertRelief;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertTransportationObject;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertVegetation;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.ConvertWaterBody;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature.Util;

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
 * Chargeur CityGML utilisant la librairie CityGML4j. Actuellement elle permet
 * de : - Charger les géométries des entités qui en possèdent - Appliquer la
 * représentation si elle est définie ou en appliquer une par défaut - Séparer
 * les entités par couche CityGML loader using CityGML4j librarie. It loads
 * geometries, apply representation and split in several layer the entities TODO
 * : gérer les relations entre entités et la sémantique
 */
public class ParserCityGMLV2 {

  public static int LOD = 3;
  /**
   * Indique si l'on charge ou non la géométrie GeOxygene (dans le but de
   * réaliser des économies de mémoire) Si c'est false, seul coordonnées de
   * points seront affectés aux géométries
   */
  public static boolean LOADGEOXGEOM = true;

  public static List<AppearanceMember> LIST_APP_GEN = null;

  public static String PATH = "";

  public static String[] NOM_COUCHE = {
      Messages.getString("ParserCityGMLV2.Building"),
      Messages.getString("ParserCityGMLV2.CityFurniture"),
      Messages.getString("ParserCityGMLV2.GenericCityObjects"),
      Messages.getString("ParserCityGMLV2.Relief"),
      Messages.getString("ParserCityGMLV2.LandUse"),
      Messages.getString("ParserCityGMLV2.Transportation"),
      Messages.getString("ParserCityGMLV2.Vegetation"),
      Messages.getString("ParserCityGMLV2.Water") };

  public static FT_FeatureCollection<IFeature> COLLECTION_ABSTRACTBUILDING = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_CITYFURNITURE = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_GENERICCITYOBJET = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_RELIEF = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_LANDUSE = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_TRANSPORTATIONOBJECT = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_VEGETATION = new FT_FeatureCollection<IFeature>();
  public static FT_FeatureCollection<IFeature> COLLECTION_WATERBODY = new FT_FeatureCollection<IFeature>();

  public static int NB_LOADING = 0;

  /**
   * @param f le fichier que l'on souhaite charger
   * @param lod son niveau de détail
   * @param isTranslated indique si l'on translate en 0,0 les données ou si l'on
   *          conserve les coordonnées actuelles
   * @return renvoie une liste de FeatureCollection chacune contenant les
   *         données chargées. Le tableau NOM_COUCHE assure la correspondance
   *         entre les collections et leurs noms
   * @throws Exception erreur de chargement peut être due aux textures ou au
   *           niveau de détail
   */
  public static List<FT_FeatureCollection<IFeature>> readCityGMLFile(File f,
      int lod, boolean isTranslated) throws Exception {
    ParserCityGMLV2.LOD = lod;

    if (!isTranslated) {
      ConvertyCityGMLGeometry.coordXIni = 0;
      ConvertyCityGMLGeometry.coordYIni = 0;
      ConvertyCityGMLGeometry.coordZIni = 0;
    }

    CityGMLReader reader = ParserCityGMLV2.getCityGMLInputFactory()
        .createCityGMLReader(f);

    ParserCityGMLV2.PATH = f.getParentFile().getAbsolutePath() + "/";

    ParserCityGMLV2.NB_LOADING++;

    ParserCityGMLV2.COLLECTION_ABSTRACTBUILDING.clear();
    ParserCityGMLV2.COLLECTION_CITYFURNITURE.clear();
    ParserCityGMLV2.COLLECTION_GENERICCITYOBJET.clear();
    ParserCityGMLV2.COLLECTION_RELIEF.clear();
    ParserCityGMLV2.COLLECTION_LANDUSE.clear();
    ParserCityGMLV2.COLLECTION_TRANSPORTATIONOBJECT.clear();
    ParserCityGMLV2.COLLECTION_VEGETATION.clear();
    ParserCityGMLV2.COLLECTION_WATERBODY.clear();

    while (reader.hasNextFeature()) {
      CityGML citygml = reader.nextFeature();

      if (citygml.getCityGMLClass() == CityGMLClass.CITYMODEL) {
        CityModel cityModel = (CityModel) citygml;

        ParserCityGMLV2.LIST_APP_GEN = cityModel.getAppearanceMember();

        IFeatureCollection<IFeature> ftColl = null;

        for (CityObjectMember cityObjectMember : cityModel
            .getCityObjectMember()) {
          CityObject cityObj = cityObjectMember.getCityObject();

          try {

            if (cityObj instanceof AbstractBuilding) {

              ftColl = ConvertBuilding
                  .convertBuilding((AbstractBuilding) cityObj);

              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_ABSTRACTBUILDING.addAll(ftColl);

            } else if (cityObj instanceof CityFurniture) {

              CityFurniture cf = (CityFurniture) cityObj;
              ftColl = ConvertCityFurniture.convertCityFurniture(cf);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_CITYFURNITURE.addAll(ftColl);

            } else if (cityObj instanceof GenericCityObject) {

              GenericCityObject gCO = (GenericCityObject) cityObj;

              ftColl = ConvertGenericCityObject
                  .convertCityCityGenericObject(gCO);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_GENERICCITYOBJET.addAll(ftColl);

            } else if (cityObj instanceof ReliefFeature) {

              ReliefFeature rf = (ReliefFeature) cityObj;
              ftColl = ConvertRelief.convertReliefFeature(rf);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_RELIEF.addAll(ftColl);

            } else if (cityObj instanceof ReliefComponent) {

              ReliefComponent rC = (ReliefComponent) cityObj;

              ftColl = ConvertRelief.convertReliefComponent(rC);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_RELIEF.addAll(ftColl);

            } else if (cityObj instanceof LandUse) {

              ftColl = ConvertLandUse.convertLandUse((LandUse) cityObj);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_LANDUSE.addAll(ftColl);

            } else if (cityObj instanceof TransportationObject) {
              ftColl = ConvertTransportationObject
                  .convertTransportationObject((TransportationObject) cityObj);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_TRANSPORTATIONOBJECT.addAll(ftColl);

            } else if (cityObj instanceof VegetationObject) {
              ftColl = ConvertVegetation
                  .convertVegetationObject((VegetationObject) cityObj);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_VEGETATION.addAll(ftColl);

            } else if (cityObj instanceof WaterBody) {
              ftColl = ConvertWaterBody.convertWaterBody((WaterBody) cityObj);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_WATERBODY.addAll(ftColl);

            } else if (cityObj instanceof WaterBoundarySurface) {
              ftColl = ConvertWaterBody
                  .convertWaterBoundarySurface((WaterBoundarySurface) cityObj);
              if (!ParserCityGMLV2.LOADGEOXGEOM) {

                Util.transformGeomToPoint(ftColl);
              }
              ParserCityGMLV2.COLLECTION_WATERBODY.addAll(ftColl);
            }
            if (ftColl != null) {
              ftColl.clear();
            }

          } catch (Exception e) {
            e.printStackTrace();

          }

        }
      }

    }

    reader.close();

    List<FT_FeatureCollection<IFeature>> lFeatColl = new ArrayList<FT_FeatureCollection<IFeature>>();
    lFeatColl.add(ParserCityGMLV2.COLLECTION_ABSTRACTBUILDING);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_CITYFURNITURE);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_GENERICCITYOBJET);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_RELIEF);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_LANDUSE);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_TRANSPORTATIONOBJECT);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_VEGETATION);
    lFeatColl.add(ParserCityGMLV2.COLLECTION_WATERBODY);

    return lFeatColl;
  }

  private static CityGMLInputFactory in = null;

  private static CityGMLInputFactory getCityGMLInputFactory()
      throws JAXBException, CityGMLReadException {

    if (ParserCityGMLV2.in == null) {

      CityGMLContext ctx = new CityGMLContext();
      JAXBBuilder builder = ctx.createJAXBBuilder();

      ParserCityGMLV2.in = builder.createCityGMLInputFactory();

    }
    return ParserCityGMLV2.in;

  }

  public static int getLOD() {
    return ParserCityGMLV2.LOD;
  }

  public static String getPATH() {
    return ParserCityGMLV2.PATH;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_ABSTRACTBUILDING() {
    return ParserCityGMLV2.COLLECTION_ABSTRACTBUILDING;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_CITYFURNITURE() {
    return ParserCityGMLV2.COLLECTION_CITYFURNITURE;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_GENERICCITYOBJET() {
    return ParserCityGMLV2.COLLECTION_GENERICCITYOBJET;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_RELIEF() {
    return ParserCityGMLV2.COLLECTION_RELIEF;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_LANDUSE() {
    return ParserCityGMLV2.COLLECTION_LANDUSE;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_TRANSPORTATIONOBJECT() {
    return ParserCityGMLV2.COLLECTION_TRANSPORTATIONOBJECT;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_VEGETATION() {
    return ParserCityGMLV2.COLLECTION_VEGETATION;
  }

  public FT_FeatureCollection<IFeature> getCOLLECTION_WATERBODY() {
    return ParserCityGMLV2.COLLECTION_WATERBODY;
  }

}
