package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature;

import java.awt.Color;
import java.util.List;

import org.citygml4j.model.citygml.transportation.AuxiliaryTrafficArea;
import org.citygml4j.model.citygml.transportation.AuxiliaryTrafficAreaProperty;
import org.citygml4j.model.citygml.transportation.TrafficArea;
import org.citygml4j.model.citygml.transportation.TrafficAreaProperty;
import org.citygml4j.model.citygml.transportation.TransportationComplex;
import org.citygml4j.model.citygml.transportation.TransportationObject;
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
 * Convertit les objets liés aux transports du schéma CityGML Convert Objects
 * related to transportation in CityGML Schémas TODO : Attribute management
 * 
 */
public class ConvertTransportationObject {

  private ConvertTransportationObject() {
    super();
  }

  /**
   * Transforme TransportationObject en features GeOxygene
   * 
   * @param atc TransportationObject que l'on souhaite convertir
   * @return une collection d'objets résultant de la convertion de l'objet en
   *         paramètre
   */
  public static IFeatureCollection<IFeature> convertTransportationObject(
      TransportationObject atc) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    if (atc instanceof TrafficArea) {

      featCollGeox.addAll(ConvertTransportationObject
          .convertTrafficArea((TrafficArea) atc));

    } else if (atc instanceof TransportationComplex) {

      featCollGeox.addAll(ConvertTransportationObject
          .convertTransportationComplex((TransportationComplex) atc));

    } else if (atc instanceof AuxiliaryTrafficArea) {

      featCollGeox.addAll(ConvertTransportationObject
          .convertAuxiliaryTrafficArea((AuxiliaryTrafficArea) atc));
    }

    return featCollGeox;

  }

  /**
   * Transformation des objets de type TransportationComplex
   * 
   * @param tc l'objet TransportationComplex que l'on souhaite conervtir
   * @return une collection issue de la conversion de l'objet initial
   */
  public static IFeatureCollection<IFeature> convertTransportationComplex(
      TransportationComplex tc) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du type de l'objet
    /*
     * if (tc instanceof Road) { } else if (tc instanceof Track) { } else if (tc
     * instanceof Railway) { } else if (tc instanceof Square) { }
     */

    MultiSurfaceProperty mSP = null;

    switch (ParserCityGMLV2.LOD) {

      case 1:

        mSP = tc.getLod1MultiSurface();

        break;

      case 2:
        mSP = tc.getLod2MultiSurface();
        break;

      case 3:

        mSP = tc.getLod3MultiSurface();
        break;
      case 4:

        mSP = tc.getLod4MultiSurface();
        break;

      default:

    }

    if (mSP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(mSP.getMultiSurface(),
          tc));
    }

    // Objets TrafficArea

    List<TrafficAreaProperty> lTA = tc.getTrafficArea();

    if (lTA != null) {
      int nbLTA = lTA.size();

      for (int i = 0; i < nbLTA; i++) {

        featCollGeox.addAll(ConvertTransportationObject.convertTrafficArea(lTA
            .get(i).getTrafficArea()));
      }

    }

    // Objets AuxiliaryTrafficArea

    List<AuxiliaryTrafficAreaProperty> lATA = tc.getAuxiliaryTrafficArea();

    if (lATA != null) {
      int nbLATA = lATA.size();

      for (int i = 0; i < nbLATA; i++) {

        featCollGeox
            .addAll(ConvertTransportationObject
                .convertAuxiliaryTrafficArea(lATA.get(i)
                    .getAuxiliaryTrafficArea()));
      }

    }
    ConvertTransportationObject.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Convertit un traffic Area en entité GeOxygene
   * 
   * @param ta l'objet TrafficArea que l'on souhaite convertir
   * @return une collection GeOxygene issue de la conversion de l'objet en
   *         entrée
   */
  public static IFeatureCollection<IFeature> convertTrafficArea(TrafficArea ta) {

    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du type de l'objet

    MultiSurfaceProperty mSP = null;

    switch (ParserCityGMLV2.LOD) {

      case 2:
        mSP = ta.getLod2MultiSurface();
        break;

      case 3:

        mSP = ta.getLod3MultiSurface();
        break;
      case 4:

        mSP = ta.getLod4MultiSurface();
        break;

      default:

    }

    if (mSP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(mSP.getMultiSurface(),
          ta));
    }
    ConvertTransportationObject.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Convertit des objets CityGML de type AuxiliaryTrafficArea en entités
   * GeOxygene
   * 
   * @param ata l'objet initial que l'on souhaite convertir
   * @return les entités GeOxygene issue de la conversion de l'objet paramètre
   */
  public static IFeatureCollection<IFeature> convertAuxiliaryTrafficArea(
      AuxiliaryTrafficArea ata) {
    IFeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    // On peut faire un tri en fonction du type de l'objet

    MultiSurfaceProperty mSP = null;

    switch (ParserCityGMLV2.LOD) {

      case 2:
        mSP = ata.getLod2MultiSurface();
        break;

      case 3:

        mSP = ata.getLod3MultiSurface();
        break;
      case 4:

        mSP = ata.getLod4MultiSurface();
        break;

      default:

    }

    if (mSP != null) {

      featCollGeox.addAll(Util.processAbstractGeometries(mSP.getMultiSurface(),
          ata));
    }
    ConvertTransportationObject.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;

  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl une collection d'entités qui se verra appliquée un style
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

            feat.setRepresentation(ConvertTransportationObject
                .defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertTransportationObject
            .defaultBuildingApperance(feat));
      }

    }

  }

  public static Color defaultColor = new Color(94, 94, 94);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat l'entité qui aura un style par défaut appliquée
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    switch (dim) {
      case 0:

        return new Object0d(feat, true,
            ConvertTransportationObject.defaultColor, 1, true);

      case 1:

        return new Object1d(feat, true,
            ConvertTransportationObject.defaultColor, 1, true);

      case 2:
        return new Object2d(feat, true,
            ConvertTransportationObject.defaultColor, 1, true);
      case 3:
        return new Object3d(feat, true,
            ConvertTransportationObject.defaultColor, 1, true);
    }

    return null;
  }

}
