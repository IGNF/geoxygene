package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature;

import java.awt.Color;

import org.citygml4j.model.citygml.core.ImplicitRepresentationProperty;
import org.citygml4j.model.citygml.generics.GenericCityObject;
import org.citygml4j.model.gml.GeometryProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
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
 *  @version 0.1
 * 
 * Permet de convertir des generic city object en features geoxygene Convert
 * generic city object into geoxygene features
 * 
 */
public class ConvertGenericCityObject {

  private ConvertGenericCityObject() {
    super();
  }

  /**
   * Convertit le generic object gco
   * 
   * @param gCO l'objet que l'on souhaite convertir
   * @return une collection d'entité correspondant à l'objet CityGML
   */
  public static FT_FeatureCollection<IFeature> convertCityCityGenericObject(
      GenericCityObject gCO) {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // Fonctionne pour LOD 1 2 3 4
    GeometryProperty gP = null;
    switch (ParserCityGMLV2.LOD) {
      case 1:

        gP = gCO.getLod1Geometry();

        break;

      case 2:
        gP = gCO.getLod2Geometry();
        break;

      case 3:

        gP = gCO.getLod3Geometry();
        break;
      case 4:

        gP = gCO.getLod4Geometry();
        break;

      default:

    }

    ImplicitRepresentationProperty iRP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        iRP = gCO.getLod1ImplicitRepresentation();
        break;

      case 2:
        iRP = gCO.getLod2ImplicitRepresentation();
        break;

      case 3:

        iRP = gCO.getLod3ImplicitRepresentation();
        break;
      case 4:

        iRP = gCO.getLod4ImplicitRepresentation();
        break;

      default:

    }

    if (iRP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(iRP
          .getImplicitGeometry().getRelativeGMLGeometry().getGeometry(), gCO));
    }

    if (gP != null) {

      featCollGeox
          .addAll(Util.processAbstractGeometries(gP.getGeometry(), gCO));
    }
    ConvertGenericCityObject.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;
  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl une collection à laquelle on applique le style
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

            feat.setRepresentation(ConvertGenericCityObject
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertGenericCityObject
            .defaultBuildingApperance(feat));
      }

    }

  }

  public static Color DefaultColor = new Color(184, 115, 51);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat une collection qui se voit assignée le style par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    switch (dim) {
      case 0:

        return new Object0d(feat, true, ConvertGenericCityObject.DefaultColor,
            1, true);

      case 1:
        return new Object1d(feat, true, ConvertGenericCityObject.DefaultColor,
            1, true);
      case 2:
        return new Object2d(feat, true, ConvertGenericCityObject.DefaultColor,
            1, true);
      case 3:
        return new Object3d(feat, true, ConvertGenericCityObject.DefaultColor,
            1, true);
    }

    return null;
  }

}
