package fr.ign.cogit.geoxygene.sig3d.geometry.topology;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 *
 * Classe de tétraèdre du modèle topologique issu du calcul de tétraèdrisation
 * 
 * 
 * Tetrahedron class of the topologic model implemented during tethraedrization
 * calculation
 * 
 */
public class Tetraedre extends GM_Solid {

  private List<Triangle> lTri;

  private List<Tetraedre> lNeighbour = new ArrayList<Tetraedre>(4);

  /**
   * 
   * @return les triangles composant le tétraèdre
   */
  public List<Triangle> getlTri() {
    return this.lTri;
  }

  /**
   * Permet de créer un tétraèdre à l'aide d'une liste de triangles
   * 
   * @param lTri la liste de triangles créant le tétraèdre
   */
  public Tetraedre(List<Triangle> lTri) {
    super(Tetraedre.prepareTriangle(lTri));
    this.lTri = lTri;
  }

  private static List<IOrientableSurface> prepareTriangle(List<Triangle> lTri) {
    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < 4; i++) {

      lOS.add(lTri.get(i));
    }

    return (lOS);
  }

  /**
   * Calcule le volume d'un tétraèdre
   */
  @Override
  public double volume() {

    IDirectPositionList dpl = this.coord();

    IDirectPositionList dplUnique = new DirectPositionList();

    int nbPointsIni = dpl.size();

    bouclei: for (int i = 0; i < nbPointsIni; i++) {
      IDirectPosition dpTemp = dpl.get(0);

      int nbUnique = dplUnique.size();

      if (nbUnique == 0) {
        dplUnique.add(dpTemp);
        continue;
      }

      for (int j = 0; j < nbUnique; j++) {

        IDirectPosition dpTempUnique = dpl.get(j);
        if (dpTempUnique.equals(dpTemp)) {

          continue bouclei;
        }

      }
      dplUnique.add(dpTemp);
      if (dplUnique.size() == 4) {
        break;
      }

    }

    IDirectPosition dp1 = dplUnique.get(0);
    IDirectPosition dp2 = dplUnique.get(1);
    IDirectPosition dp3 = dplUnique.get(2);
    IDirectPosition dp4 = dplUnique.get(3);

    Matrix matVol = new Matrix(3, 3);
    matVol.set(0, 0, dp1.getX() - dp2.getX());
    matVol.set(1, 0, dp2.getX() - dp3.getX());
    matVol.set(2, 0, dp3.getX() - dp4.getX());
    matVol.set(0, 1, dp1.getY() - dp2.getY());
    matVol.set(1, 1, dp2.getY() - dp3.getY());
    matVol.set(2, 1, dp3.getY() - dp4.getY());
    matVol.set(0, 2, dp1.getZ() - dp2.getZ());
    matVol.set(1, 2, dp2.getZ() - dp3.getZ());
    matVol.set(2, 2, dp3.getZ() - dp4.getZ());

    return Math.abs(matVol.det()) / 6;

  }

  /**
   * 
   * @return Renvoie les tétraèdres voisins d'un tétraèdre
   */
  public List<Tetraedre> getlNeighbour() {
    return this.lNeighbour;
  }

}
