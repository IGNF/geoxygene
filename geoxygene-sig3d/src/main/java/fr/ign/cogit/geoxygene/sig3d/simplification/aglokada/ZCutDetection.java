package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;

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
 * Classe permettant de déterminer les différentes coupes en Z d'un batiment
 * 
 */
public class ZCutDetection {

  private List<List<IOrientableSurface>> listeGroupesFaces;

  /**
   * Initialise les différentes attribues
   */
  public ZCutDetection() {
    this.listeGroupesFaces = new ArrayList<List<IOrientableSurface>>();
  }

  private List<Double> lZtop = new ArrayList<Double>();

  /**
   * @return Renvoie les sommets des différentes coupes
   */
  public List<Double> getlZtop() {
    return this.lZtop;

  }

  /**
   * Méthode qui regroupe les faces en fonction des altitudes
   * 
   * @param LS liste des faces dont on souhaite regrouper les altitudes
   * @param seuil seuil en Z de regroupement
   * @return une liste de liste de faces triées par altitude
   */
  public List<List<IOrientableSurface>> groupingFaces2(
      List<IOrientableSurface> LS, double seuil) {

    int nbElem = LS.size();

    List<Box3D> lb = new ArrayList<Box3D>();
    double zmin = Double.POSITIVE_INFINITY;
    double zmax = Double.NEGATIVE_INFINITY;
    // On détecte le zmin
    for (int i = 0; i < nbElem; i++) {

      Box3D b = new Box3D(LS.get(i));

      zmax = Math.max(zmax, b.getURDP().getZ());
      zmin = Math.min(zmin, b.getLLDP().getZ());

      lb.add(b);

    }

    // On calcul zTop
    int nbSeuil = (int) ((zmax - zmin) / seuil) + 1;
    // System.out.println(nbSeuil);

    for (int i = 0; i < nbSeuil; i++) {

      this.lZtop.add(i * seuil + zmin);
      this.listeGroupesFaces.add(new ArrayList<IOrientableSurface>());
    }

    for (int i = 0; i < nbElem; i++) {

      Box3D b = new Box3D(LS.get(i));

      double zBMin = b.getLLDP().getZ();
      double zBMax = b.getURDP().getZ();

      int nbContrib = (int) ((zBMax - zBMin) / seuil) + 1;
      int zIni = (int) ((zBMin - zmin) / seuil);

      // System.out.println(nbContrib);

      for (int idContrib = zIni; idContrib < nbContrib; idContrib++) {
        this.listeGroupesFaces.get(idContrib).add(LS.get(i));

      }

    }

    return this.listeGroupesFaces;
  }

  /**
   * Autre méthode qui regroupe les faces en fonction des altitudes
   * @param LS liste des faces dont on souhaite regrouper les altitudes
   * @param seuil seuil en Z de regroupement
   * @return une liste de liste de faces triées par altitude
   */
  public List<List<IOrientableSurface>> groupingFaces(
      List<IOrientableSurface> LS, double seuil) {

    int nbSegment = LS.size();

    double[] ListeZ;

    ListeZ = new double[nbSegment];

    // Ajout MKL
    double[] ListeZmin = new double[nbSegment];

    // pour chaque face on récupère le Z minimal des 2 points ayant les Z
    // max
    // pour chaque face on récupère le Z minimal de la face
    for (int i = 0; i < nbSegment; i++) {

      IOrientableSurface face = LS.get(i);

      double[] coord = new double[3];
      double[] coord1 = new double[3];
      double[] coord2 = new double[3];
      double[] coord3 = new double[3];
      double Z;

      for (int j = 0; j < face.coord().size(); j++) {
        IDirectPosition dp = face.coord().get(j);

        coord = dp.getCoordinate();

        if (j == 0) {
          coord1 = coord;
        }
        if (j == 1) {
          if (coord[2] <= coord1[2]) {
            coord2 = coord;
          } else {
            coord2 = coord1;
            coord1 = coord;
          }
        }
        if (j == 2) {
          if (coord[2] >= coord1[2]) {
            coord3 = coord2;
            coord2 = coord1;
            coord1 = coord;
          }
          if (coord[2] < coord1[2] && coord[2] >= coord2[2]) {
            coord3 = coord2;
            coord2 = coord;
          }
          if (coord[2] < coord2[2]) {
            coord3 = coord;
          }
        }
        if (j > 2) {
          if (coord[2] >= coord1[2]) {
            coord3 = coord2;
            coord2 = coord1;
            coord1 = coord;
          }
          if (coord[2] < coord1[2] && coord[2] >= coord2[2]) {
            coord3 = coord2;
            coord2 = coord;
          }
          if (coord[2] < coord2[2] && coord[2] > coord3[2]) {
            coord3 = coord;
          }
        }
      }
      if (coord1.equals(coord2)) {
        coord2 = coord3;
      }

      Z = coord2[2];
      ListeZ[i] = Z;

      // Ajout MKL
      ListeZmin[i] = Calculation3D.pointMin(face).getZ();
    }

    // méthode pour x cycles
    boolean cont = true;
    ArrayList<IOrientableSurface> GroupeFaces = new ArrayList<IOrientableSurface>();

    List<IOrientableSurface> LSnew = LS;

    boolean[] used = new boolean[nbSegment];
    for (int i = 0; i < nbSegment; i++) {
      used[i] = false;
    }

    // if(modif){
    while (cont == true) {

      // On prend le premier non utilisé
      int indini = -1;
      for (int i = 0; i < nbSegment; i++) {
        if (used[i]) {
          continue;
        }

        indini = i;

        break;
      }

      if (indini == -1) {

        break;
      }
      // /On initialize les hateurs
      // on indique que l'on a utilisé ce segment
      double ZiniMax = ListeZ[indini];

      // Ztop est le plus grand des z entre Zinimax et Zinimax + seuil
      double zTsommet = ZiniMax;

      used[indini] = true;

      GroupeFaces.add(LSnew.get(indini));

      for (int k = 0; k < LSnew.size(); k++) {

        if (k == indini) {
          continue;

        }

        double D = ZiniMax - ListeZ[k];

        // Cas initial du max de la hauteur différent seulement de la
        // coupe
        if (D < seuil && D > -seuil) {
          GroupeFaces.add(LSnew.get(k));

          if (zTsommet < ListeZ[k]) {
            zTsommet = ListeZ[k];

          }

          used[k] = true;
          continue;
        }

        // cas d'un pan dépasse de plus de Zseuil
        /*
         * I I I I I I I <---- Zseuil
         */

        if (ListeZ[k] > ZiniMax && ListeZmin[k] < ZiniMax - seuil) {

          GroupeFaces.add(LSnew.get(k));
        }

      }// Boucle for
      int nbface = GroupeFaces.size();

      boolean ajoute = false;
      // Nous ne prenons pas les groupes formés de 1 ou 2 éléments
      if (nbface > 2) {

        // Il faut les ordonner

        int nbgroup = this.listeGroupesFaces.size();

        for (int i = 0; i < nbgroup; i++) {

          Double topTemp = this.lZtop.get(i);

          if (topTemp.doubleValue() < zTsommet) {

            continue;
          }

          this.listeGroupesFaces.add(i, GroupeFaces);
          this.lZtop.add(i, new Double(zTsommet));
          ajoute = true;
          break;
        }

        if (!ajoute) {

          this.listeGroupesFaces.add(GroupeFaces);
          this.lZtop.add(new Double(zTsommet));

        }

      }

      GroupeFaces = new ArrayList<IOrientableSurface>();

    }

    return this.listeGroupesFaces;
  }

}
