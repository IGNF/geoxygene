package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 * @author Aurélien Velten
 * 
 * @version 0.1
 * 
 * Classe permettant d'obtenir la trace de murs au sol
 * 
 * Class to cast wall on the ground
 * 
 * 
 */
public class GroundCasting {

  private double[] coord1 = null;
  private double[] coord2 = null;
  private double[] coord = null;
  private double[] coord3 = null;

  private IDirectPosition pt;

  private double Z;

  /**
   * Permet d'obtenir les projeté au sol ) parfitr d'une face
   * 
   * @param face
   */
  public GroundCasting(IOrientableSurface face) {

    // récupèration des deux points de la face ayant les coordonnées Z
    // minimales
    for (int i = 0; i < face.coord().size(); i++) {
      this.pt = face.coord().get(i);

      this.coord = this.pt.getCoordinate();

      if (i == 0) {
        this.coord1 = this.coord;
      }

      if (i == 1) {
        if (this.coord[2] >= this.coord1[2]) {
          this.coord2 = this.coord;
        } else {
          this.coord2 = this.coord1;
          this.coord1 = this.coord;
        }
      }

      if (i == 2) {
        if (this.coord[2] <= this.coord1[2]) {
          this.coord3 = this.coord2;
          this.coord2 = this.coord1;
          this.coord1 = this.coord;
        }
        if (this.coord[2] > this.coord1[2] && this.coord[2] <= this.coord2[2]) {
          this.coord3 = this.coord2;
          this.coord2 = this.coord;
        }
        if (this.coord[2] > this.coord2[2]) {
          this.coord3 = this.coord;
        }
      }

      if (i > 2) {
        if (this.coord[2] <= this.coord1[2]) {
          this.coord3 = this.coord2;
          this.coord2 = this.coord1;
          this.coord1 = this.coord;
        }
        if (this.coord[2] > this.coord1[2] && this.coord[2] <= this.coord2[2]) {
          this.coord3 = this.coord2;
          this.coord2 = this.coord;
        }
        if (this.coord[2] > this.coord2[2] && this.coord[2] < this.coord3[2]) {
          this.coord3 = this.coord;
        }
      }
    }

    if (this.coord1 == this.coord2) {
      this.coord2 = this.coord3;
    }

    this.Z = this.coord1[2];
  }

  /*
   * // constructeur public Proj_sol(Facette facette) { // récupèration des deux
   * points de la face ayant les coordonnées Z // minimales for (int i = 0; i <
   * facette.getListePoints().size(); i++) { pt =
   * facette.getListePoints().get(i); DirectPosition dp = pt.getCoord(); coord =
   * dp.getCoordinate();
   * 
   * if (i == 0) { coord1 = coord; Z = coord[2]; continue; }
   * 
   * if(Z>coord[2]){ Z= coord[2]; }
   * 
   * if (i == 1) { if (coord[2] >= coord1[2]) { coord2 = coord; } else { coord2
   * = coord1; coord1 = coord; } continue; }
   * 
   * 
   * 
   * if (i == 2) {
   * 
   * 
   * if((coord[0]!= coord2[0])&& (coord[1]!= coord2[1])){ coord2[2] =
   * Math.min(coord2[2], coord[2]); coord3 = coord; continue; }
   * 
   * if((coord[0]!= coord1[0])&& (coord[1]!= coord1[1])){ coord1[2] =
   * Math.min(coord1[2], coord[2]);
   * 
   * coord3 = coord; continue;
   * 
   * }
   * 
   * 
   * if (coord[2] <= coord1[2]) { coord3 = coord2; coord2 = coord1; coord1 =
   * coord; continue; } if (coord[2] > coord1[2] && coord[2] <= coord2[2]) {
   * coord3 = coord2; coord2 = coord; continue; } if ((coord[2] > coord2[2])){
   * coord3 = coord; continue; } }
   * 
   * 
   * if((coord[0]!= coord2[0])&& (coord[1]!= coord2[1])){ coord2[2] =
   * Math.min(coord2[2], coord[2]); continue; }
   * 
   * if((coord[0]!= coord1[0])&& (coord[1]!= coord1[1])){ coord1[2] =
   * Math.min(coord1[2], coord[2]);
   * 
   * continue; }
   * 
   * 
   * if (i > 2) {
   * 
   * 
   * 
   * 
   * if (coord[2] <= coord1[2]) { coord3 = coord2; coord2 = coord1; coord1 =
   * coord; continue; } if (coord[2] > coord1[2] && coord[2] <= coord2[2]) {
   * coord3 = coord2; coord2 = coord; continue; } if (coord[2] > coord2[2] &&
   * coord[2] < coord3[2]&&(coord[0]!= coord2[0])&& (coord[1]!=
   * coord2[1])&&(coord[0]!= coord1[0])&& (coord[1]!= coord1[1])) { coord3 =
   * coord; continue; } } }
   * 
   * if (coord1[0] == coord2[0]&&coord1[1] == coord2[1]) { coord2 = coord3; }
   * 
   * }
   */

  /**
   * Renvoie le segment projeté au sol sous forme de LineString
   */
  public GM_LineString getGroundCasting() {

    DirectPositionList dpLis = new DirectPositionList();

    dpLis.add(new DirectPosition(this.coord1[0], this.coord1[1], this.Z));
    dpLis.add(new DirectPosition(this.coord2[0], this.coord2[1], this.Z));

    return new GM_LineString(dpLis);
  }

  /**
   * 
   * @return retourne l'altitude Z du segment au sol
   */
  public double getZ() {
    return this.Z;
  }

}
