package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature;

import java.awt.Color;
import java.util.List;

import org.citygml4j.model.citygml.waterbody.BoundedByWaterSurfaceProperty;
import org.citygml4j.model.citygml.waterbody.WaterBody;
import org.citygml4j.model.citygml.waterbody.WaterBoundarySurface;
import org.citygml4j.model.gml.MultiCurveProperty;
import org.citygml4j.model.gml.MultiSurfaceProperty;
import org.citygml4j.model.gml.SolidProperty;
import org.citygml4j.model.gml.SurfaceProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.ConvertCityGMLAppearance;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.ParserCityGMLV2;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object0d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object1d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object2d;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object3d;

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
 * Convertisseur pour les classes du schéma WaterBody de CityGML Converter for
 * classes of WaterBody of CityGML Schéma TODO : Conversion des
 * attributs/attribute conversion
 * 
 */
public class ConvertWaterBody {

  private ConvertWaterBody() {
    super();
  }

  /**
   * Convertit des WaterBody en feature GeOxygene Si le WaterBody est composé de
   * WaterSurfaces, les objets liés sont convertis
   * 
   * @param wB l'Objet WaterBody que l'on souhaite convertir
   * @return une liste d'entité issue de la conversion de l'objet paramètre
   */
  public static IFeatureCollection<IFeature> convertWaterBody(WaterBody wB) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du LOD choisi

    // Cas ou la géométrie liée est un solide
    SolidProperty sP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        sP = wB.getLod1Solid();
        break;

      case 2:
        sP = wB.getLod2Solid();
        break;

      case 3:

        sP = wB.getLod3Solid();
        break;

      case 4:

        sP = wB.getLod4Solid();
        break;
      default:

    }

    if (sP != null) {
      // On traite la géométrie et attache une représentation à l'objet
      featCollGeox.addAll(Util.processAbstractGeometries(sP.getSolid(), wB));
    }

    // Cas ou la géométrie est une MultiSurface
    MultiSurfaceProperty mSProperty = null;

    switch (ParserCityGMLV2.LOD) {
      case 0:
        mSProperty = wB.getLod0MultiSurface();
        break;

      case 1:

        mSProperty = wB.getLod1MultiSurface();
        break;

      default:

    }

    if (mSProperty != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(
          mSProperty.getMultiSurface(), wB));
    }

    // Cas ou la géométrie est une multiCurve
    MultiCurveProperty mCProperty = null;

    switch (ParserCityGMLV2.LOD) {
      case 0:
        mCProperty = wB.getLod0MultiCurve();
        break;

      case 1:

        mCProperty = wB.getLod1MultiCurve();
        break;

      default:

    }

    if (mCProperty != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(
          mCProperty.getMultiCurve(), wB));
    }

    // Cas ou l'objet est composé d'une liste de WaterBoundaries
    List<BoundedByWaterSurfaceProperty> lWBS = wB.getBoundedBySurface();

    int nbLWBS = lWBS.size();

    for (int i = 0; i < nbLWBS; i++) {

      featCollGeox.addAll(ConvertWaterBody.convertWaterBoundarySurface(lWBS
          .get(i).getWaterBoundarySurface()));
    }
    ConvertWaterBody.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Convertit des WaterBoundarySurface (et classes filles) en FT_Feature
   * 
   * @param wBS l'objet WaterBoundarySurface que l'on souhaite convertir
   * @return une liste d'entités issue de la conversion de l'objet paramètre
   */
  public static IFeatureCollection<IFeature> convertWaterBoundarySurface(
      WaterBoundarySurface wBS) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On traite la géométrie en fonction de la dimension de l'objet

    SurfaceProperty sP = null;

    switch (ParserCityGMLV2.LOD) {

      case 2:
        sP = wBS.getLod2Surface();
        break;

      case 3:

        sP = wBS.getLod3Surface();
        break;

      case 4:

        sP = wBS.getLod4Surface();
        break;
      default:

    }

    if (sP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(sP.getSurface(), wBS));
    }
    ConvertWaterBody.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl la collection à laquelle est appliquée un style issu de
   *          CityGML
   */
  public static void assignDefaultRepresentation(
      IFeatureCollection<IFeature> ftFeatColl) {
    int nbEl = ftFeatColl.size();

    for (int i = 0; i < nbEl; i++) {
      IFeature feat = ftFeatColl.get(i);

      if (feat.getRepresentation() != null) {
        Representation rep = feat.getRepresentation();

        if (rep instanceof ConvertCityGMLAppearance) {
          if (!((ConvertCityGMLAppearance) rep).isRepresentationSet()) {

            feat.setRepresentation(ConvertWaterBody
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertWaterBody.defaultBuildingApperance(feat));
      }

    }

  }

  public static Color DefaultColor = new Color(90, 128, 151);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat l'entité qui se verra appliqué un style par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    switch (dim) {
      case 0:

        return new Object0d(feat, true, ConvertWaterBody.DefaultColor, 1, true);

      case 1:
        return new Object1d(feat, true, ConvertWaterBody.DefaultColor, 1, true);
      case 2:
        return new Object2d(feat, true, ConvertWaterBody.DefaultColor, 1, true);
      case 3:
        return new Object3d(feat, true, ConvertWaterBody.DefaultColor, 1, true);
    }

    return null;
  }
}
