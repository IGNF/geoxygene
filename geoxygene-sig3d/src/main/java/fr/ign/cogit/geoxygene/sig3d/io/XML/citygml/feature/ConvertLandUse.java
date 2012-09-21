package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature;

import java.awt.Color;

import org.citygml4j.model.citygml.landuse.LandUse;
import org.citygml4j.model.gml.MultiSurfaceProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
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
 *  @version 0.1
 * 
 * Convertit un objet CityGML LandUse en features Convert a CityGML LandUse
 * object into features
 * 
 */
public class ConvertLandUse {

  private ConvertLandUse() {
    super();
  }

  /**
   * Convert LandUse to FT_Features
   * 
   * @param lU l'objet LandUse que l'on souhaite convertir
   * @return Une collection de features correspondant au LandUse initial
   */
  public static FT_FeatureCollection<IFeature> convertLandUse(LandUse lU) {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // Fonctionne pour LOD 1 2 3 4
    MultiSurfaceProperty mSP = null;
    switch (ParserCityGMLV2.LOD) {
      case 0:

        mSP = lU.getLod0MultiSurface();

        break;
      case 1:

        mSP = lU.getLod1MultiSurface();

        break;

      case 2:
        mSP = lU.getLod2MultiSurface();
        break;

      case 3:

        mSP = lU.getLod3MultiSurface();
        break;
      case 4:

        mSP = lU.getLod4MultiSurface();
        break;

      default:

    }

    if (mSP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(mSP.getMultiSurface(),
          lU));
    }
    ConvertLandUse.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl une collection à laquelle est appliquée le nouveau style
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

            feat.setRepresentation(ConvertLandUse
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertLandUse.defaultBuildingApperance(feat));
      }

    }

  }

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat une entité à laquelle on applique le style par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    Color c = new Color(111, 66, 66);

    switch (dim) {
      case 0:

        return new Object0d(feat, true, c, 1, true);

      case 1:
        return new Object1d(feat, true, c, 1, true);
      case 2:
        return new Object2d(feat, true, c, 1, true);
      case 3:
        return new Object3d(feat, true, c, 1, true);
    }

    return null;
  }
}
