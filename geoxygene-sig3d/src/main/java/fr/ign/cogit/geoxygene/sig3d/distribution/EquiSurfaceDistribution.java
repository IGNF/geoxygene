package fr.ign.cogit.geoxygene.sig3d.distribution;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.Tetraedrisation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.6
 * 
 * @author MBrasebin
 * 
 *         Classe permettant de tirer aléatoirement des points répartis
 *         équitablement sur un objet de dimension 2 ou 3 Le principe est basé
 *         sur Shape Distributions Osada and al[2002] Shape Distributions ROBERT
 *         OSADA, THOMAS FUNKHOUSER, BERNARD CHAZELLE, and DAVID DOBKIN
 *         Princeton University Class wich calculate the dissimilarity measure
 *         between 2 solids
 * 
 * 
 *         Class to generate random points distrubition on an object with a
 *         dimension superior than 2. The principle is based on : Shape
 *         Distributions Osada and al[2002] Shape Distributions ROBERT OSADA,
 *         THOMAS FUNKHOUSER, BERNARD CHAZELLE, and DAVID DOBKIN Princeton
 *         University Class wich calculate the dissimilarity measure between 2
 *         solids
 * 
 */
public class EquiSurfaceDistribution {

  private List<IOrientableSurface> lTriangles = new ArrayList<IOrientableSurface>();

  // Il s'agit des aires cumulés des différents triangles
  // airesCumulees[i] = somme des ieme premiers triangles
  // airesCumulees[n-1] = aire de la surface enblobant le corps
  private double[] accumulatedArea;

  public EquiSurfaceDistribution(IGeometry geom) {

    lTriangles = FromGeomToSurface.convertGeom(geom);

    initTri();
    initSamp();
  }

  // Initialisation avant le calcul de la fonction de caractérisation
  private void initTri() {

    boolean b = Util.containOnlyTriangleFaces(lTriangles);

    if (b) {

    } else {

      try {
        Tetraedrisation tet = new Tetraedrisation(new GM_Solid(lTriangles));

        tet.tetraedriseWithConstraint(true);

        lTriangles = tet.getTriangles();
      } catch (Exception e) {

        e.printStackTrace();
      }
    }

  }

  private void initSamp() {

    int nbTriangles = this.lTriangles.size();

    // calcul des aires cumulées qui permettront de choisir les points
    // aléatoires
    this.accumulatedArea = new double[nbTriangles];

    for (int i = 0; i < this.lTriangles.size(); i++) {
      IDirectPositionList lDP = this.lTriangles.get(i).coord();

      Vecteur v1 = new Vecteur(lDP.get(0), lDP.get(1));
      Vecteur v2 = new Vecteur(lDP.get(0), lDP.get(2));

      Vecteur v3 = v1.prodVectoriel(v2);

      double aire = v3.norme() * 0.5;

      // Nous avons 2 points à tester, nous pouvons calculer la longeur
      if (i != 0) {
        this.accumulatedArea[i] = aire + this.accumulatedArea[i - 1];

        continue;
      }

      this.accumulatedArea[i] = aire;

    }
  }

  /**
   * Fonction permettant de tirer aléatoirement un triangle sur une surface
   * triangulée en prenant compte de l'aire
   * 
   * @return
   */

  public IOrientableSurface randomTriangle(double rand) {

    // Tout d'abord on choisit aléatoirement un triangle
    // Sur la surface du volume (aléatoirement pondéré par l'aire des
    // triangles)
    int nbEleme = this.accumulatedArea.length - 1;
    double max = this.accumulatedArea[nbEleme];

    double alea = max * rand;

    int i = 0;
    // On choisit l'indice correspondant
    while (this.accumulatedArea[i] < alea) {
      i++;

    }

    double valArInf = i == 0 ? 0 :  accumulatedArea[i-1]; 
    
    corrected_rand = (alea - valArInf) / (this.accumulatedArea[i]-valArInf);
    
    // On retourne la valeur correspondante à cet indice
    IOrientableSurface surf = this.lTriangles.get(i);

    return surf;

  }
  
  private double corrected_rand;

  /**
   * Fonction permettant de retourner un point tiré aléatoirement sur la surface
   * d'un triangle
   * 
   * @param triangle
   * @return un point tiré aléatoirement sur le triangle
   */
  private DirectPosition randomPointOnTriangles(IOrientableSurface triangle,
      double aleaX, double aleaY) {
    IDirectPositionList pTriangle = triangle.coord();

    IDirectPosition p1 = pTriangle.get(0);
    IDirectPosition p2 = pTriangle.get(1);
    IDirectPosition p3 = pTriangle.get(2);

    // Cette méthode est conseillé dans l'article dont est issue la mesure
    double sqrtAleaX = Math.sqrt(aleaX);

    DirectPosition pointFinal = new DirectPosition(p1.getX() * (1 - sqrtAleaX)
        + p2.getX() * sqrtAleaX * (1 - aleaY) + p3.getX() * aleaY * sqrtAleaX,
        p1.getY() * (1 - sqrtAleaX) + p2.getY() * sqrtAleaX * (1 - aleaY)
            + p3.getY() * aleaY * sqrtAleaX, p1.getZ() * (1 - sqrtAleaX)
            + p2.getZ() * sqrtAleaX * (1 - aleaY) + p3.getZ() * aleaY
            * sqrtAleaX);

    return pointFinal;

  }

  public IDirectPosition sample() {
    return sample(Math.random(), Math.random());
  }

  public IDirectPosition sample(double rand1, double rand2) {
    IOrientableSurface sur = randomTriangle(rand1);
    return randomPointOnTriangles(sur, corrected_rand, rand2);
  }


}
