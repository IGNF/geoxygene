package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature;

import java.awt.Color;
import java.util.List;

import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.citygml.appearance.AppearanceMember;
import org.citygml4j.model.citygml.appearance.AppearanceProperty;
import org.citygml4j.model.citygml.building.AbstractBuilding;
import org.citygml4j.model.citygml.building.BoundarySurface;
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.BuildingInstallation;
import org.citygml4j.model.citygml.building.BuildingInstallationProperty;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.citygml.building.IntBuildingInstallation;
import org.citygml4j.model.citygml.building.IntBuildingInstallationProperty;
import org.citygml4j.model.citygml.building.OpeningProperty;
import org.citygml4j.model.gml.MultiSurfaceProperty;
import org.citygml4j.model.gml.SolidProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.ConvertCityGMLAppearance;
import fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.ParserCityGMLV2;
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
 * Cette classe permet de convertir un objet bâtiment en entités GeOxygene TODO
 * : Attributs et relations ne sont pas renseignés Ne récupère pas
 * l'intersection du bâtiment avec le terrain Ne s'occupe pas de l'intérieur de
 * la maison (classe room et furniture) Convert CityGML building into GeOxygene
 * entites Only get representation and geometries
 */
public class ConvertBuilding {

  /**
   * Le convertisseur prenant un bâtiment b Convertit la géométrie et affecte
   * une représentation pour être visualisé dans le viewer 3D
   * 
   * @param b un bâtiment CityGML
   * @return collections d'entités issues de l'objet initial
   */
  public static IFeatureCollection<IFeature> convertBuilding(AbstractBuilding b) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    IFeatureCollection<IFeature> featTempColl = new FT_FeatureCollection<IFeature>();

    // Cas ou la géométrie liée est un solide
    SolidProperty sP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        sP = b.getLod1Solid();
        break;

      case 2:
        sP = b.getLod2Solid();
        break;

      case 3:

        sP = b.getLod3Solid();
        break;

      case 4:

        sP = b.getLod4Solid();
        break;
      default:

    }

    if (sP != null) {
      // On traite la géométrie et attache une représentation à l'objet
      featTempColl.addAll(Util.processAbstractGeometries(sP.getSolid(), b));
    }

    if (featTempColl.size() > 0) {
      ConvertBuilding.assignDefaultRepresentation(featTempColl,
          b.getCityGMLClass());
      featCollGeox.addAll(featTempColl);
      featTempColl.clear();
    }

    // Cas ou la géométrie liée est un solide
    MultiSurfaceProperty mSP = null;

    switch (ParserCityGMLV2.LOD) {
      case 1:

        mSP = b.getLod1MultiSurface();
        break;

      case 2:
        mSP = b.getLod2MultiSurface();
        break;

      case 3:

        mSP = b.getLod3MultiSurface();
        break;

      case 4:

        mSP = b.getLod4MultiSurface();
        break;
      default:

    }

    if (mSP != null) {
      // On traite la géométrie et attache une représentation à l'objet
      featTempColl.addAll(Util.processAbstractGeometries(mSP.getMultiSurface(),
          b));
    }

    if (featTempColl.size() > 0) {
      ConvertBuilding.assignDefaultRepresentation(featTempColl,
          b.getCityGMLClass());
      featCollGeox.addAll(featTempColl);
      featTempColl.clear();
    }

    // On subdivise le batiment en parties
    // Ce sont ces parties qui seront représentées
    List<BuildingPartProperty> lBP = b.getConsistsOfBuildingPart();

    if (lBP == null) {

      return featCollGeox;
    }

    if (lBP.size() != 0) {
      int nbPart = lBP.size();

      for (int i = 0; i < nbPart; i++) {

        featCollGeox.addAll(ConvertBuilding.convertBuilding(lBP.get(i)
            .getBuildingPart()));

      }
      return featCollGeox;
    }

    // On récupère les parties bordants le batiments
    List<BoundarySurfaceProperty> lb = b.getBoundedBySurface();
    int nbBoundary = lb.size();

    // Géométries portées par les bordures
    for (int i = 0; i < nbBoundary; i++) {

      BoundarySurface bs = lb.get(i).getBoundarySurface();

      // Fonctionne pour LOD 2 3 4

      switch (ParserCityGMLV2.LOD) {

        case 2:

          if (bs.getAppearance() == null || bs.getAppearance().size() == 0) {

            featTempColl = Util.processSurfaceProperties(bs
                .getLod2MultiSurface().getMultiSurface().getSurfaceMember(), b);
          } else {

            featTempColl = Util
                .processSurfaceProperties(bs.getLod2MultiSurface()
                    .getMultiSurface().getSurfaceMember(), bs);
          }

          break;

        case 3:

          if (bs.getAppearance() == null || bs.getAppearance().size() == 0) {

              if(bs.getLod3MultiSurface() != null && bs.getLod3MultiSurface().getMultiSurface() != null)
              {
          
                  featTempColl = Util.processSurfaceProperties(bs
                          .getLod3MultiSurface().getMultiSurface().getSurfaceMember(), b);
              }else{
                  System.out.println();
              }
            
          } else {

            
   
            
            featTempColl = Util
                .processSurfaceProperties(bs.getLod3MultiSurface()
                    .getMultiSurface().getSurfaceMember(), bs);
          }
          break;
        case 4:

          if (bs.getAppearance() == null || bs.getAppearance().size() == 0) {

            featTempColl = Util.processSurfaceProperties(bs
                .getLod4MultiSurface().getMultiSurface().getSurfaceMember(), b);

          } else {

            featTempColl = Util
                .processSurfaceProperties(bs.getLod4MultiSurface()
                    .getMultiSurface().getSurfaceMember(), bs);

          }
          break;

        default:

      }
      if (featTempColl != null && featTempColl.size() > 0) {
        ConvertBuilding.assignDefaultRepresentation(featTempColl,
            bs.getCityGMLClass());
        featCollGeox.addAll(featTempColl);
        featTempColl.clear();
      }

      // Opening
      List<OpeningProperty> lO = bs.getOpening();

      if (lO != null) {
        int nbOpening = lO.size();

        for (int o = 0; o < nbOpening; o++) {

          if (lO.get(o).getObject().getAppearance() != null) {

            int nbApparence = lO.get(o).getObject().getAppearance().size();

            for (int j = 0; j < nbApparence; j++) {
              AppearanceProperty appP = lO.get(o).getObject().getAppearance()
                  .get(j);
              if (appP instanceof AppearanceMember) {

                ParserCityGMLV2.LIST_APP_GEN.add((AppearanceMember) appP);

              }

            }

          }

          switch (ParserCityGMLV2.LOD) {

            case 3:

              featTempColl = Util.processSurfaceProperties(lO.get(o)
                  .getObject().getLod3MultiSurface().getMultiSurface()
                  .getSurfaceMember(), bs);

              break;
            case 4:

              featTempColl = Util.processSurfaceProperties(lO.get(o)
                  .getObject().getLod4MultiSurface().getMultiSurface()
                  .getSurfaceMember(), bs);
              break;

            default:

          }

          if (featTempColl != null && featTempColl.size() > 0) {
            ConvertBuilding.assignDefaultRepresentation(featTempColl, lO.get(o)
                .getObject().getCityGMLClass());
            featCollGeox.addAll(featTempColl);
            featTempColl.clear();
          }

        }

      }

    }

    // Equipement extérieur

    List<BuildingInstallationProperty> lBIN = b.getOuterBuildingInstallation();

    int nbInstall = lBIN.size();

    for (int i = 0; i < nbInstall; i++) {

      BuildingInstallation bi = lBIN.get(i).getBuildingInstallation();

      switch (ParserCityGMLV2.LOD) {

        case 2:

          if (bi.getLod2Geometry() != null) {

            featTempColl = Util.processAbstractGeometries(bi.getLod2Geometry()
                .getGeometry(), bi);

          }

          break;
        case 3:
          if (bi.getLod3Geometry() != null) {

            featTempColl = Util.processAbstractGeometries(bi.getLod3Geometry()
                .getGeometry(), bi);

          }

          break;
        case 4:
          if (bi.getLod4Geometry() != null) {

            featTempColl = Util.processAbstractGeometries(bi.getLod4Geometry()
                .getGeometry(), bi);

          }
          break;

        default:

      }

      if (featTempColl != null && featTempColl.size() > 0) {
        ConvertBuilding.assignDefaultRepresentation(featTempColl,
            bi.getCityGMLClass());
        featCollGeox.addAll(featTempColl);
        featTempColl.clear();
      }

    }

    // Equipement intérieur
    List<IntBuildingInstallationProperty> lBINInterior = b
        .getInteriorBuildingInstallation();
    nbInstall = lBINInterior.size();

    for (int i = 0; i < nbInstall; i++) {

      IntBuildingInstallation bi = lBINInterior.get(i)
          .getIntBuildingInstallation();

      switch (ParserCityGMLV2.LOD) {

        case 4:
          if (bi.getLod4Geometry() != null) {

            featTempColl = Util.processAbstractGeometries(bi.getLod4Geometry()
                .getGeometry(), b);

          }
          break;

        default:

      }

      if (featTempColl != null && featTempColl.size() > 0) {
        ConvertBuilding.assignDefaultRepresentation(featTempColl,
            bi.getCityGMLClass());
        featCollGeox.addAll(featTempColl);
        featTempColl.clear();
      }

    }

    return featCollGeox;
  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl les entités qui se verront un style appliqué
   * @param c la classe CityGML correspondant à l'entité
   */
  public static void assignDefaultRepresentation(
      IFeatureCollection<IFeature> ftFeatColl, CityGMLClass c) {
    int nbEl = ftFeatColl.size();

    for (int i = 0; i < nbEl; i++) {
      IFeature feat = ftFeatColl.get(i);

      if (feat.getRepresentation() != null) {
        Representation rep = feat.getRepresentation();

        if (rep instanceof ConvertCityGMLAppearance) {
          if (!((ConvertCityGMLAppearance) rep).isRepresentationSet()) {

            feat.setRepresentation(ConvertBuilding.defaultBuildingApperance(
                feat, c));
          }
        }

      } else {
        feat.setRepresentation(ConvertBuilding
            .defaultBuildingApperance(feat, c));
      }

    }

  }

  /**
   * La couleur par défaut appliquée aux objets de ce type
   */
  public static Color DefaultColor = Color.white;

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat assigne une apparence par défaut aux objets dont l'apparence
   *          n'est pas définie dans le fichier cityGML
   * @param c la classe CityGML de l'objet
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat,
      CityGMLClass c) {

    if (c.equals(CityGMLClass.ROOFSURFACE)) {

      return new ObjectCartoon(feat, new Color(139, 101, 8));

    } else if (c.equals(CityGMLClass.WALLSURFACE)) {

      return new ObjectCartoon(feat, ConvertBuilding.DefaultColor);

    } else if (c.equals(CityGMLClass.GROUNDSURFACE)) {

      return new ObjectCartoon(feat, new Color(238, 207, 161));

    } else if (c.equals(CityGMLClass.WINDOW)) {

      return new ObjectCartoon(feat, new Color(205, 133, 63));

    } else if (c.equals(CityGMLClass.DOOR)) {

      return new ObjectCartoon(feat, Color.red);
    }

    return new ObjectCartoon(feat, ConvertBuilding.DefaultColor);
  }

}
