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
 * @version 1.0
 **/
package fr.ign.cogit.geoxygene.sig3d.indicator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;


/**
 * 
 * @author MBrasebin
 *
 */
public class Saliency {

  public static double nbNeighBourMin = 10;
  private static String HEIGHT_ATT_NAME = "Height";
  private static String DEVIATION_ATT_NAME = "Deviation";
  private static String FACADE_AREA_ATT_NAME = "FArea";
  private static String GEOMETRY_COMPLEXITY_ATT_NAME = "GCompl";
  private static String SHAPE_FACTOR_ATT_NAME = "ShapeFac";

  private static String HEIGHT_CONTRIB = "HC";
  private static String DEVIATION_CONTRIB = "DC";
  private static String FACADE_CONTRIB = "FC";
  private static String GC_CONTRIB = "GC";
  private static String SF_CONTRIB = "SF";

  private double value = 0;

  public Saliency(IFeature feat, IFeatureCollection<IFeature> iFeatColl) {

    value = 0;
    if (iFeatColl == null || iFeatColl.size() < nbNeighBourMin) {
      AttributeManager.addAttribute(feat, HEIGHT_CONTRIB, 0, "Integer");
      AttributeManager.addAttribute(feat, DEVIATION_CONTRIB, 0, "Integer");
      AttributeManager.addAttribute(feat, FACADE_CONTRIB, 0, "Integer");
      AttributeManager.addAttribute(feat, GC_CONTRIB, 0, "Integer");
      AttributeManager.addAttribute(feat, SF_CONTRIB, 0, "Integer");
      return;
    }

    IDirectPosition rangeHM = determineRange(iFeatColl, HEIGHT_ATT_NAME);
    IDirectPosition rangeDA = determineRange(iFeatColl, DEVIATION_ATT_NAME);
    IDirectPosition rangeFA = determineRange(iFeatColl, FACADE_AREA_ATT_NAME);
    IDirectPosition rangeGC = determineRange(iFeatColl,
        GEOMETRY_COMPLEXITY_ATT_NAME);
    IDirectPosition rangeSF = determineRange(iFeatColl, SHAPE_FACTOR_ATT_NAME);

    double indHM = 0;

    if (rangeHM.getX() != rangeHM.getY()) {

      indHM = (Double
          .parseDouble(feat.getAttribute(HEIGHT_ATT_NAME).toString()) - rangeHM
          .getX())
          / (rangeHM.getY() - rangeHM.getX());

    }

    double indDA = 0;
    if (rangeDA.getX() != rangeDA.getY()) {
      indDA = (Double.parseDouble(feat.getAttribute(DEVIATION_ATT_NAME)
          .toString()) - rangeDA.getX()) / (rangeDA.getY() - rangeDA.getX());
    }

    double indFA = 0;
    if (rangeFA.getX() != rangeDA.getY()) {
      indFA = (Double.parseDouble(feat.getAttribute(FACADE_AREA_ATT_NAME)
          .toString()) - rangeFA.getX()) / (rangeFA.getY() - rangeFA.getX());
    }

    double indGC = 0;
    if (rangeGC.getX() != rangeGC.getY()) {
      indGC = (Double.parseDouble(feat.getAttribute(
          GEOMETRY_COMPLEXITY_ATT_NAME).toString()) - rangeGC.getX())
          / (rangeGC.getY() - rangeGC.getX());
    }

    double indSF = 0;
    if (rangeSF.getX() != rangeSF.getY()) {
      indSF = (Double.parseDouble(feat.getAttribute(SHAPE_FACTOR_ATT_NAME)
          .toString()) - rangeSF.getX()) / (rangeSF.getY() - rangeSF.getX());
    }

    if (indHM > 0.9) {
      value++;

    }

    if (indDA < 0.1) {
      value++;

    }
    if (indFA > 0.9) {
      value++;

    }

    if (indGC > 0.9) {
      value++;

    }
    if (indSF > 0.9) {
      value++;

    }

    value = value / 5;

  }

  /**
   * 
   * @param iFeatColl
   * @param attName
   * @return X pour min et Y pour max, j'ai honte, mais j'ai la flemme de faire
   *         ça proprement on devrait pouvoir renvoyer plusieurs objets d'un
   *         coup
   */
  private static IDirectPosition determineRange(
      IFeatureCollection<IFeature> iFeatColl, String attName) {

    int nbElem = iFeatColl.size();

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < nbElem; i++) {

      double value = Double.parseDouble(iFeatColl.get(i).getAttribute(attName)
          .toString());

      min = Math.min(min, value);
      max = Math.max(max, value);

    }
    return new DirectPosition(min, max);

  }

  public static void processInitialisation(IFeature b) {
	    double zMin = HauteurCalculation.calculateZBasPBB(b);
	    double zMax = HauteurCalculation.calculateZHautPHF(b);

    AttributeManager.addAttribute(b, HEIGHT_ATT_NAME, zMax - zMin, "Double");

    FacadeArea fA = new FacadeArea(b);
    AttributeManager.addAttribute(b, FACADE_AREA_ATT_NAME, fA.getValue(),
        "Double");

    ShapeDeviation sB = new ShapeDeviation(b);
    AttributeManager.addAttribute(b, DEVIATION_ATT_NAME, sB.getValue(),
        "Double");

    int complexity = FromPolygonToTriangle.convertAndTriangle(FromGeomToSurface.convertGeom(b.getGeom())).size();
    AttributeManager.addAttribute(b, GEOMETRY_COMPLEXITY_ATT_NAME, complexity,
        "Integer");

    ShapeFactor sF = new ShapeFactor(b);
    AttributeManager.addAttribute(b, SHAPE_FACTOR_ATT_NAME, sF.getValue(),
        "Double");

  }

}
