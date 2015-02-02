package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.Random;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;


/**
 *         This software is released under the licence CeCILL
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
 * @version 1.7
 */
public class SensibilitePosition {

  private IFeatureCollection<IFeature> featJDD;
  private int nbPointsCouronnes;
  private double rayon;

  public IFeatureCollection<IFeature> getFeatJDD() {
    return featJDD;
  }

  public void setFeatJDD(IFeatureCollection<IFeature> featJDD) {
    this.featJDD = featJDD;
  }

  public int getPas() {
    return nbPointsCouronnes;
  }

  public void setPas(int pas) {
    this.nbPointsCouronnes = pas;
  }

  public double getRayon() {
    return rayon;
  }

  public void setRayon(double rayon) {
    this.rayon = rayon;
  }

  public SensibilitePosition(IFeatureCollection<IFeature> featJDD, int pas,
      double rayon) {
    super();
    this.featJDD = featJDD;
    this.nbPointsCouronnes = pas;
    this.rayon = rayon;
  }

  public IFeatureCollection<IFeature> process(IDirectPosition centre,
      int nbEchantillons, double ecartTypeP, double ecartTypeA) {
    int type = RayCasting.TYPE_FIRST_POINT_AND_SPHERE;

    IFeatureCollection<IFeature> featOut = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < nbEchantillons; i++) {

      IDirectPosition posTemp = generateCoordinate(centre, ecartTypeP,
          ecartTypeA);

      RayCasting ray = new RayCasting(posTemp, featJDD, nbPointsCouronnes,
          rayon, type, false);
      ray.cast();
      IndicatorVisu indV = new IndicatorVisu(ray);

      double ouverture = indV.getRatioSphere();

      IFeature feat = new DefaultFeature(new GM_Point(posTemp));
      AttributeManager.addAttribute(feat, "Ouverture", ouverture, "Double");

    }

    return featOut;

  }

  public static IDirectPosition generateCoordinate(IDirectPosition centre,
      double ecartTypeP, double ecartTypeA) {
    double xMoy = centre.getX();
    double yMoy = centre.getY();
    double zMoy = centre.getZ();

    Random generator;
    generator = new Random();
    double num1 = generator.nextGaussian();
    double num2 = generator.nextGaussian();

    double teta = Math.random() * 2 * Math.PI;
    double phi = Math.random() * Math.PI;

    double xFin = xMoy + num1 *  ecartTypeP * Math.cos(teta)  * Math.sin(phi);
    double yFin = yMoy +  num1 *  ecartTypeP * Math.sin(teta) * Math.sin(phi) ;
    double zFin = zMoy + num2 * ecartTypeA * Math.cos(phi) ;

    return new DirectPosition(xFin, yFin, zFin);

  }

}
