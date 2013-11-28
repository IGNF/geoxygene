/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class Compactness {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private IPolygon geom;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public Compactness(IPolygon geom) {
    this.geom = geom;
  }

  // Getters and setters //
  public void setGeom(IPolygon geom) {
    this.geom = geom;
  }

  public IPolygon getGeom() {
    return this.geom;
  }

  // Other public methods //
  public double getMillerIndex() {
    return 4.0 * Math.PI * this.geom.area()
        / (this.geom.perimeter() * this.geom.perimeter());
  }

  // *****************************************************
  //
  // MESURES DE COMPACITE DE MACEACHREN
  //
  // *****************************************************

  /**
   * Mesure de compacité issue de (McEachren 85). Cette compacité est basée sur
   * l'aire et le périmètre, maximisée par un cercle et est comprise entre 0 et
   * 1.
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * 
   * @return double
   */
  public double getMcEachrenIndex1() {
    return Math.sqrt(geom.area()) / (geom.length() * 0.282);
  }

  /**
   * Mesure de compacité issue de (McEachren 85). Cette compacité est basée sur
   * l'aire et le périmètre, maximisée par un cercle et est comprise entre 0 et
   * 1. Même chose que compaciteMcEachren1 mais au carré.
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * 
   * @return double
   */
  public double getMcEachrenIndex2() {
    return geom.area() / ((geom.length() * 0.282) * (geom.length() * 0.282));
  }

  /**
   * Mesure de compacité issue des travaux Ehrenberg. Cette compacité est basée
   * sur l'aire du cercle circonscrit du polygone calculé à partir du plus long
   * diamètre. Maximisée par un cercle et comprise entre 0 et 1.
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * 
   * @return double
   */
  public double getEhrenbergIndex() {
    // on récupère le plus long diamètre
    double diametre = CommonAlgorithmsFromCartAGen.getPolygonDiameter(geom)
        .length();
    return geom.area() / (Math.PI * (diametre * 0.5) * (diametre * 0.5));
  }

  /**
   * Mesure de compacité issue de Schumm. Cette compacité est basée sur le plus
   * long diamètre du polygone et son aire. Maximisée par un cercle et comprise
   * entre 0 et 1.
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * 
   * @return double
   */
  public double getSchummIndex() {
    // on r�cup�re le plus long diam�tre
    double diametre = CommonAlgorithmsFromCartAGen.getPolygonDiameter(geom)
        .length();
    return 2 * Math.sqrt(geom.area() / Math.PI) / diametre;
  }

  /**
   * Mesure de compacité basée sur une comparaison avec un cercle centré au
   * centroïde et de même surface que le polygone. Issu de Lee et Salle (1970).
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * 
   * @return double
   */
  public double compaciteSalleLee() {
    // on récupère le centroïde de la surface
    IDirectPosition centroid = geom.centroid();
    // on détermine le rayon du cercle à partir de son aire
    double rayon = Math.sqrt(geom.area() / Math.PI);
    // on construit la géométrie du cercle à partir du centre et du rayon
    IPolygon cercle = GeometryFactory.buildCircle(centroid, rayon, 30);
    return Distances.distanceSurfacique(geom, cercle);
  }

  /**
   * Mesure de compacité de Boyce-Clark. Cette mesure est basée sur le calcul
   * des rayons entre le centro�de et le bord du polygone. Cette mesure a un
   * paramètre n qui est le nombre de rayon que l'on calcule.
   * 
   * @param geom : la géométrie surfacique dont on cherche la compacité.
   * @param n : nombre de rayon à calculer (1 tous les 360/n°).
   * 
   * @return double
   */
  public double compaciteBoyceClark(int n) {
    // on récupère le centroïde de la surface
    IDirectPosition centroid = geom.centroid();
    // initialisation
    double sommeTotale = 0.0;
    double sommeRayons = 0.0;
    double angleCourant = 0.0;
    ArrayList<Double> listeRayons = new ArrayList<Double>();
    // on calcule le pas d'angle entre les rayons
    double pas = 2 * Math.PI / n;
    // on fait une boucle sur le nombre de rayons
    for (int i = 1; i <= n; i++) {
      // on calcule le i�me rayon
      // pour cela on fait une rotation de geom de -angleCourant
      IPolygon geomCourante = new GM_Polygon(new GM_LineString(geom.coord()));
      geomCourante = CommonAlgorithms.rotation(geomCourante, centroid,
          -angleCourant);
      // on récupère le x max de la géométrie rotatée
      double xMax = ((IDirectPosition) CommonAlgorithmsFromCartAGen
          .getPtMaxXFromPolygon(geomCourante).get(0)).getX();
      double rayon = xMax - centroid.getX();
      // on ajoute rayon à la liste
      listeRayons.add(new Double(rayon));
      // on met à jour la somme
      sommeRayons += rayon;
      // on incrémente l'angleCourant par le pas
      angleCourant += pas;
    }
    // on calcule le terme principal de la formule de compacité
    for (int i = 0; i < n; i++) {
      sommeTotale += Math.abs(100.0 * listeRayons.get(i).doubleValue()
          / sommeRayons - 100.0 / n);
    }

    // calcul final de la compacit�
    return 1 - sommeTotale / 200.0;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
