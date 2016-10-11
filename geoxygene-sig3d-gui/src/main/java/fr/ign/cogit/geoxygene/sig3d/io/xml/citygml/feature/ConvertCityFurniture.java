package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature;

import java.awt.Color;

import org.citygml4j.model.citygml.cityfurniture.CityFurniture;
import org.citygml4j.model.gml.GeometryProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ConvertCityGMLAppearance;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ParserCityGMLV2;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.sample.ObjectCartoon;

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
 *  @version 0.1
 * 
 * Permet de convertir des équipements urbains en features geoxygene Convert
 * building furniture into geoxygene features
 */
public class ConvertCityFurniture {

  /**
   * Convertit le CityFurniture cf
   * 
   * @param cf objet CityFurniture en paramètre
   * @return une liste d'entités correspondant à la transformation
   */
  public static FT_FeatureCollection<IFeature> convertCityFurniture(
      CityFurniture cf) {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // Fonctionne pour LOD 1 2 3 4
    GeometryProperty gP = null;
    switch (ParserCityGMLV2.LOD) {
      case 1:

        gP = cf.getLod1Geometry();

        break;

      case 2:
        gP = cf.getLod2Geometry();
        break;

      case 3:

        gP = cf.getLod3Geometry();
        break;
      case 4:

        gP = cf.getLod4Geometry();
        break;

      default:

    }

    if (gP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(gP.getGeometry(), cf));
    }
    ConvertCityFurniture.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;
  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl une liste d'objets à laquelle sera appliquée un style
   */
  public static void assignDefaultRepresentation(
      FT_FeatureCollection<IFeature> ftFeatColl) {
    int nbEl = ftFeatColl.size();

    for (int i = 0; i < nbEl; i++) {
      IFeature feat = ftFeatColl.get(i);

      if (feat.getRepresentation() != null) {
        Representation rep = feat.getRepresentation();

        if (rep instanceof ConvertCityGMLAppearance) {
          if (!((ConvertCityGMLAppearance) rep).isRepresentationSet()) {

            feat.setRepresentation(ConvertCityFurniture
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertCityFurniture
            .defaultBuildingApperance(feat));
      }

    }

  }

  /**
   * La couleur par défaut appliquée aux objets de ce type
   */
  public static Color DefaultColor = new Color(184, 115, 51);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat une entité à laquelle sera appliquée un style par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    return new ObjectCartoon(feat, ConvertCityFurniture.DefaultColor);
  }

}
