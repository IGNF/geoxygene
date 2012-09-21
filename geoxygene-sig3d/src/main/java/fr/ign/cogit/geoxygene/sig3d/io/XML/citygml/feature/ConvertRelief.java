package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml.feature;

import java.awt.Color;
import java.util.List;

import org.citygml4j.model.citygml.relief.BreaklineRelief;
import org.citygml4j.model.citygml.relief.MassPointRelief;
import org.citygml4j.model.citygml.relief.RasterRelief;
import org.citygml4j.model.citygml.relief.ReliefComponent;
import org.citygml4j.model.citygml.relief.ReliefComponentProperty;
import org.citygml4j.model.citygml.relief.ReliefFeature;
import org.citygml4j.model.citygml.relief.TINRelief;

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
 * Classe permettant de convertir les features des classes de relief CityGML
 * Class used to convert relief CityGML objects into GeOxygene features
 * 
 */
public class ConvertRelief {

  private ConvertRelief() {
    super();
  }

  /**
   * Convertit un relief Feature qui est composé d'une liste de
   * ReliefComponenent
   * 
   * @param rF le ReliefFeature que l'on souhaite convertir
   * @return une collection de features GeOxygene issues de l'objet paramètre
   */
  public static FT_FeatureCollection<IFeature> convertReliefFeature(
      ReliefFeature rF) {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    if (rF.getLod() == ParserCityGMLV2.LOD) {

      List<ReliefComponentProperty> lRC = rF.getReliefComponent();
      int nbLRC = lRC.size();
      for (int i = 0; i < nbLRC; i++) {

        ReliefComponentProperty rcp = lRC.get(i);
        featCollGeox.addAll(ConvertRelief.convertReliefComponent(rcp
            .getReliefComponent()));

      }

    }

    return featCollGeox;

  }

  /**
   * Convertit un relief component TODO les types MassPointRelief,
   * BreaklineRelief et RasterRelief ne sont pas encore gérés
   * 
   * @param rC l'objet relief que l'on souhaite convertir
   * @return une collection d'entités géoxygène correspondant à l'entité
   *         paramètre
   */
  public static FT_FeatureCollection<IFeature> convertReliefComponent(
      ReliefComponent rC) {

    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();
    // Cas ou le LOD ne coïncide pas
    if (rC.getLod() != ParserCityGMLV2.LOD) {

      return featCollGeox;

    }
    // On traite les différents types de reliefs
    if (rC instanceof TINRelief) {

      featCollGeox.addAll(ConvertRelief.convertTINRelief((TINRelief) rC));

    } else if (rC instanceof MassPointRelief) {
      System.out.println(rC.getCityGMLClass() + "non traité");
    } else if (rC instanceof BreaklineRelief) {
      System.out.println(rC.getCityGMLClass() + "non traité");
    } else if (rC instanceof RasterRelief) {
      System.out.println(rC.getCityGMLClass() + "non traité");
    }

    return featCollGeox;

  }

  /**
   * Convertit un Tin en entités GeOxygene
   * 
   * @param tin le tin que l'on souhaite convertir
   * @return une collection d'entités correspondant au Tin initial
   */
  public static FT_FeatureCollection<IFeature> convertTINRelief(TINRelief tin) {
    FT_FeatureCollection<IFeature> featCollGeox = new FT_FeatureCollection<IFeature>();

    featCollGeox.addAll(Util.processAbstractGeometries(tin.getTin()
        .getTriangulatedSurface(), tin));
    ConvertRelief.assignDefaultRepresentation(featCollGeox);
    return featCollGeox;
  }

  /**
   * Assigne une représentation par défault aux objets n'ayant pas de
   * représentation
   * 
   * @param ftFeatColl la collection à laquelle sera appliquée la représentation
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

            feat.setRepresentation(ConvertRelief.defaultBuildingApperance(feat));
          }
        }

      } else {
        feat.setRepresentation(ConvertRelief.defaultBuildingApperance(feat));
      }

    }

  }

  public static Color DefaultColor = new Color(244, 164, 96);

  /**
   * La représentation par défault assignée aux entités n'ayant pas de
   * représentation
   * 
   * @param feat une entité à laquelle on applique une représentation par défaut
   */
  public static I3DRepresentation defaultBuildingApperance(IFeature feat) {

    int dim = feat.getGeom().dimension();

    switch (dim) {
      case 0:

        return new Object0d(feat, true, ConvertRelief.DefaultColor, 1, true);

      case 1:
        return new Object1d(feat, true, ConvertRelief.DefaultColor, 1, true);
      case 2:
        return new Object2d(feat, true, ConvertRelief.DefaultColor, 1, true);
      case 3:
        return new Object3d(feat, true, ConvertRelief.DefaultColor, 1, true);
    }

    return null;
  }

}
