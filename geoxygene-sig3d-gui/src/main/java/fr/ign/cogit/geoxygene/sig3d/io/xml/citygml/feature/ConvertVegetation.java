package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature;

import java.awt.Color;

import org.citygml4j.model.citygml.core.ImplicitRepresentationProperty;
import org.citygml4j.model.citygml.vegetation.PlantCover;
import org.citygml4j.model.citygml.vegetation.SolitaryVegetationObject;
import org.citygml4j.model.citygml.vegetation.VegetationObject;
import org.citygml4j.model.gml.GeometryProperty;
import org.citygml4j.model.gml.MultiSolidProperty;
import org.citygml4j.model.gml.MultiSurfaceProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ConvertCityGMLAppearance;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ParserCityGMLV2;
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
 * Classe permettant de convertir les objets du package Végétation de CityGML en
 * entité GeOxygene Class to Load CityGML Vegetation objects TODO : to add
 * attributes information
 * 
 */
public class ConvertVegetation {

  private ConvertVegetation() {
    super();
  }

  /**
   * Convertit un VegetationObject en entités géoxygene. L'objet est traité
   * différemment suivant la classe qu'il implémente
   * 
   * @param vO l'objet VegetationObject que l'on souhaite convertir
   * @return une collection d'entités issue de la conversion de l'objet
   *         paramètre
   */
  public static IFeatureCollection<IFeature> convertVegetationObject(
      VegetationObject vO) {
    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    if (vO instanceof SolitaryVegetationObject) {

      featCollGeox.addAll(ConvertVegetation
          .convertSolitaryVegetationObject((SolitaryVegetationObject) vO));

    } else if (vO instanceof PlantCover) {

      featCollGeox.addAll(ConvertVegetation
          .convertSolitaryPlantCover((PlantCover) vO));
    }

    return featCollGeox;
  }

  /**
   * Convertit un objet de type SolitaryPlantCover en entité GeOxygene
   * 
   * @param pC l'objet SolitaryPlantCover que l'on souhaite convertir
   * @return une liste d'entités GeOxygene issue de la conversion de l'objet
   *         paramètre
   */
  public static IFeatureCollection<IFeature> convertSolitaryPlantCover(
      PlantCover pC) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du type de l'objet

    MultiSolidProperty mSP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        mSP = pC.getLod1MultiSolid();
        break;

      case 2:
        mSP = pC.getLod2MultiSolid();
        break;

      case 3:

        mSP = pC.getLod3MultiSolid();
        break;

      default:

    }

    if (mSP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(mSP.getMultiSolid(),
          pC));
    }

    MultiSurfaceProperty mSProperty = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        mSProperty = pC.getLod1MultiSurface();
        break;

      case 2:
        mSProperty = pC.getLod2MultiSurface();
        break;

      case 3:

        mSProperty = pC.getLod3MultiSurface();
        break;
      case 4:

        mSProperty = pC.getLod4MultiSurface();
        break;

      default:

    }

    if (mSProperty != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(
          mSProperty.getMultiSurface(), pC));
    }
    ConvertVegetation.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Convertit un SolitaryVegetationObject en Feature (en prenant compte
   * géométrie et représentation)
   * 
   * @param sVO l'objet SolitaryVegetationObject que l'on souhaite convertir
   * @return une liste d'entités GeOxygene issue de la conversion de l'objet
   *         paramètre
   */
  public static IFeatureCollection<IFeature> convertSolitaryVegetationObject(
      SolitaryVegetationObject sVO) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du type de l'objet

    GeometryProperty aGP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        aGP = sVO.getLod1Geometry();
        break;

      case 2:
        aGP = sVO.getLod2Geometry();
        break;

      case 3:

        aGP = sVO.getLod3Geometry();
        break;
      case 4:

        aGP = sVO.getLod4Geometry();
        break;

      default:

    }

    if (aGP != null) {

      featCollGeox
          .addAll(Util.processAbstractGeometries(aGP.getGeometry(), sVO));
    }

    ImplicitRepresentationProperty iRP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        iRP = sVO.getLod1ImplicitRepresentation();
        break;

      case 2:
        iRP = sVO.getLod2ImplicitRepresentation();
        break;

      case 3:

        iRP = sVO.getLod3ImplicitRepresentation();
        break;
      case 4:

        iRP = sVO.getLod4ImplicitRepresentation();
        break;

      default:

    }

    if (iRP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(iRP
          .getImplicitGeometry().getRelativeGMLGeometry().getGeometry(), sVO));
    }
    ConvertVegetation.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl une collection d'entité à laquelle on applique un style
   *          défini en CityGML
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

            feat.setRepresentation(ConvertVegetation
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertVegetation.defaultBuildingApperance(feat));
      }

    }

  }

  public static Color DefaultColor = new Color(162, 205, 90);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat une entité à laquelle est appliqué un style par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    switch (dim) {
      case 0:

        return new Object0d(feat, true, ConvertVegetation.DefaultColor, 1, true);

      case 1:
        return new Object1d(feat, true, ConvertVegetation.DefaultColor, 1, true);
      case 2:
        return new Object2d(feat, true, ConvertVegetation.DefaultColor, 1, true);
      case 3:
        return new Object3d(feat, true, ConvertVegetation.DefaultColor, 1, true);
    }

    return null;
  }
}
