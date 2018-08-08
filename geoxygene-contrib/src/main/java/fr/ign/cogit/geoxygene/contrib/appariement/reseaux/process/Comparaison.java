/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe supportant les méthodes de comparaison globale de réseaux.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */

public class Comparaison {

  /**
   * Approximation de l'écart moyen entre deux réseaux. Cette approximation
   * grossière évalue pour tout point de réseau1 son écart à réseau2 (point le
   * plus proche quelconque), sans réaliser d'appariement de réseau.
   * 
   * @param reseau1 Un réseau, typiquement un réseau peu précis dont on veut
   *          estimer la qualité par rapport à reseau2.
   * @param reseau2 Un autre réseau, typiquement un réseau de bonne qualité qui
   *          sert de référence.
   * @param distanceMax Sert à éliminer les aberrations dans le calcul de la
   *          moyenne: les arcs de reseau1 situés au moins en un point à plus de
   *          distanceMax de reseau2 ne sont pas pris en compte dans le calcul.
   * @return L'écart moyen. Il est calculé comme la moyenne des distances entre
   *         les points des arcs de reseau1 et reseau2. Cette moyenne est
   *         pondérée par la longueur des segments entourant les points en
   *         question.
   */
  public static double approximationEcartPlaniMoyen(CarteTopo reseau1,
      CarteTopo reseau2, double distanceMax) {

    double dtot = 0, ltot = 0, dmax = 0;
    double ltotArc, dtotArc, dmaxArc, l1, l2, l;
    int n = 0;
    Iterator<?> itArcsRef, itArcsComp;
    Collection<Arc> arcsCompProches;
    IFeatureCollection<Arc> arcsRef = reseau1.getPopArcs();
    IFeatureCollection<Arc> arcsComp = reseau2.getPopArcs();
    itArcsRef = arcsRef.getElements().iterator();
    while (itArcsRef.hasNext()) { // pour chaque arc de this
      IFeature objetRef = (IFeature) itArcsRef.next();
      ILineString geomRef = (ILineString) objetRef.getGeom();
      IDirectPositionList dp = geomRef.coord();
      arcsCompProches = arcsComp.select(geomRef, distanceMax);

      dtotArc = 0;
      ltotArc = 0;
      dmaxArc = 0;
      for (int i = 0; i < dp.size(); i++) { // pour chaque point d'un arc
        IDirectPosition ptRef = dp.get(i);
        if (i == 0) {
          l1 = 0;
        } else {
          l1 = dp.get(i).distance(dp.get(i - 1));
        }
        if (i == dp.size() - 1) {
          l2 = 0;
        } else {
          l2 = dp.get(i).distance(dp.get(i + 1));
        }
        l = l1 + l2;
        double dmin = Double.MAX_VALUE;
        itArcsComp = arcsCompProches.iterator();
        while (itArcsComp.hasNext()) {
          IFeature objetComp = (IFeature) itArcsComp.next();
          ILineString geomComp = (ILineString) objetComp.getGeom();
          IDirectPosition ptProjete = Operateurs.projection(ptRef, geomComp);
          double d = ptRef.distance(ptProjete);
          if (d < dmin) {
            dmin = d;
          }
        }
        if (dmin > dmaxArc) {
          dmaxArc = dmin;
        }
        dtotArc = dtotArc + dmin * l;
        ltotArc = ltotArc + l;
      }
      if (dmaxArc > distanceMax) {
        continue;
      }
      if (dmaxArc > dmax) {
        dmax = dmaxArc;
      }
      dtot = dtot + dtotArc;
      ltot = ltot + ltotArc;
      n++;
    }

    System.out.println(I18N
        .getString("Comparaison.AverageDistanceBetweenEdges") + dtot / ltot); //$NON-NLS-1$
    System.out
        .println(I18N.getString("Comparaison.MaxDistanceBetweenEdges") + dmax); //$NON-NLS-1$
    System.out.println(I18N.getString("Comparaison.NumberOfEdgesUsed") + n); //$NON-NLS-1$
    System.out
        .println(I18N.getString("Comparaison.NumberOfEdgesNotUsed") + (arcsRef.size() - n)); //$NON-NLS-1$
    return dtot / ltot;
  }

  /**
   * Ensemble d'indicateurs évaluant globalement l'écart de position entre le
   * réseau à étudier réseau11 et un réseau de référence réseau2. Cette
   * évaluation se fait en s'appuyant sur le calcul des écarts entre chaque
   * point de reseau1 et sa projection au plus près sur le réseau reseau2 (et
   * non sur un réel appariement d'arcs). Les moyennes sont pondérées par la
   * longueur des segments entourant les points, pour gommer les effets dus aux
   * pas de découpage variables des lignes.
   * 
   * @param reseau1 réseau étudié.
   * @param reseau2 réseau servant de référence.
   * @param affichage Si TRUE alors les résultats sont affichés.
   * @param distanceMax Sert à éliminer les aberrations dans les calculs. - Les
   *          arcs de reseau1 situés en au moins un point à plus de distanceMax
   *          de reseau2 ne sont pas pris en compte dans le calcul des
   *          indicateurs sur les arcs. - Les noeuds de reseau1 situés à plus de
   *          distanceMax d'un noeud de reseau2 ne sont pas pris en compte dans
   *          le calcul des indicateurs sur les noeuds
   * @return Liste (de 'double') contenant un ensemble d'indicateurs sur l'écart
   *         entre les réseaux :
   *         <p>
   *         ESTIMATEURS SUR LES ARCS liste(0): longueur des arcs du réseau
   *         "this" total liste(1): longueur des arcs du réseau "this" pris en
   *         compte dans les calculs d'évaluation de l'écart liste(2): longueur
   *         des arcs du réseau "reseau" liste(3): nombre d'arcs du réseau
   *         "this" total liste(4): nombre d'arcs du réseau "this" pris en
   *         compte dans les calculs d'évaluation de l'écart liste(5): nombre
   *         d'arcs du réseau "reseau" liste(6): estimation du biais
   *         systématique en X sur les arcs (valeur en X de la moyenne des
   *         vecteurs d'écart entre un point de "this" et son projeté sur
   *         "reseau") liste(7): estimation du biais systématique en Y sur les
   *         arcs (valeur en Y de la moyenne des vecteurs d'écart entre un point
   *         de "this" et son projeté sur "reseau") liste(8): estimation de
   *         l'écart moyen sur les arcs (moyenne des longueurs des vecteurs
   *         d'écart entre un point de "this" et son projeté sur "reseau")
   *         liste(9): estimation de l'écart moyen quadratique sur les arcs
   *         (moyenne quadratique des longueurs des vecteurs d'écart entre un
   *         point de "this" et son projeté sur "reseau") liste(10): estimation
   *         de l'écart type sur les arcs, i.e. précision une fois le biais
   *         corrigé ( racine(ecart moyen quadratique^2 - biais^2) liste(11):
   *         histogramme de répartition des écarts sur tous les points (en nb de
   *         points intermédiaires sur les arcs).
   *         <p>
   *         ESTIMATEURS SUR LES NOEUDS (si ils existent) liste(12): nombre de
   *         noeuds du réseau "this" total liste(13): nombre de noeuds du réseau
   *         "this" pris en compte dans les calculs d'évaluation de l'écart
   *         liste(14): nombre de noeuds du réseau "reseau" liste(15):
   *         estimation du biais systématique en X sur les noeuds (valeur en X
   *         de la moyenne des vecteurs d'écart entre un noeud de "this" et le
   *         noeud le plus proche de "reseau") liste(16): estimation du biais
   *         systématique en Y sur les noeuds (valeur en Y de la moyenne des
   *         vecteurs d'écart entre un noeud de "this" et le noeud le plus
   *         proche de "reseau") liste(17): estimation de l'écart moyen sur les
   *         noeuds (moyenne des longueurs des vecteurs d'écart entre un noeud
   *         de "this" et le noeud le plus proche de "reseau") liste(18):
   *         estimation de l'écart moyen quadratique sur les arcs (moyenne
   *         quadratique des longueurs des vecteurs d'écart entre un noeud de
   *         "this" et le noeud le plus proche de "reseau") liste(19):
   *         estimation de l'écart type sur les noeuds, i.e. précision une fois
   *         le biais corrigé ( racine(ecart moyen quadratique^2 - biais^2)
   *         liste(20): histogramme de répartition des écarts sur tous les
   *         noeuds (en nb de noeuds)
   */
  public static List<?> evaluationEcartPosition(CarteTopo reseau1,
      CarteTopo reseau2, double distanceMax, boolean affichage) {

    List<Double> resultats = new ArrayList<Double>();
    IFeatureCollection<Arc> arcs1 = reseau1.getPopArcs();
    IFeatureCollection<Arc> arcs2 = reseau2.getPopArcs();
    Iterator<?> itArcs1, itArcs2;
    Arc arc1, arc2;
    ILineString geom1, geom2;
    Vecteur v12, vmin, vPourUnArc1, vTotal;
    IDirectPosition pt1, projete;
    Collection<Arc> arcs2proches;
    double longArc1, poids, d12, ecartQuadratiquePourUnArc1, l1, l2, poidsPourUnArc1, ecartPourUnArc1, ecartMaxArc1, poidsTotal;
    // indicateurs finaux
    double longTotal1, longPrisEnCompte1, longTotal2;
    double ecartTotal, ecartQuadratiqueTotal, ecartTypeArcs;
    int nbArcsTotal1, nbArcsPrisEnCompte1, nbArcsTotal2;
    // /////////////// EVALUATION SUR LES ARCS ////////////////////
    // indexation des arcs du réseau 2
    if (!reseau2.getPopArcs().hasSpatialIndex()) {
      reseau2.getPopArcs().initSpatialIndex(Tiling.class, false, 20);
    }
    // parcours du réseau 2 juste pour calculer sa longueur
    itArcs2 = arcs2.getElements().iterator();
    longTotal2 = 0;
    while (itArcs2.hasNext()) {
      arc2 = (Arc) itArcs2.next();
      longTotal2 = longTotal2 + ((ILineString) arc2.getGeom()).length();
    }
    nbArcsTotal2 = arcs2.getElements().size();
    // parcours du réseau 1 pour évaluer les écarts sur les arcs
    nbArcsTotal1 = arcs1.getElements().size();
    nbArcsPrisEnCompte1 = 0;
    poidsTotal = 0;
    ecartTotal = 0;
    ecartQuadratiqueTotal = 0;
    vTotal = new Vecteur(new DirectPosition(0, 0));
    longTotal1 = 0;
    longPrisEnCompte1 = 0;
    itArcs1 = arcs1.getElements().iterator();
    while (itArcs1.hasNext()) { // pour chaque arc du réseau 1
      arc1 = (Arc) itArcs1.next();
      geom1 = (ILineString) arc1.getGeom();
      longArc1 = geom1.length();
      longTotal1 = longTotal1 + geom1.length();

      arcs2proches = arcs2.select(geom1, distanceMax);
      if (arcs2proches.size() == 0) {
        continue;
      }
      poidsPourUnArc1 = 0;
      ecartPourUnArc1 = 0;
      ecartQuadratiquePourUnArc1 = 0;
      vmin = new Vecteur();
      vPourUnArc1 = new Vecteur(new DirectPosition(0, 0));
      ecartMaxArc1 = 0;
      IDirectPositionList dp = geom1.coord();
      for (int i = 0; i < dp.size(); i++) { // pour chaque point de arc1
        pt1 = dp.get(i);
        // calcul du poids de ce point
        if (i == 0) {
          l1 = 0;
        } else {
          l1 = dp.get(i).distance(dp.get(i - 1));
        }
        if (i == dp.size() - 1) {
          l2 = 0;
        } else {
          l2 = dp.get(i).distance(dp.get(i + 1));
        }
        poids = l1 + l2;
        // projection du point sur le réseau2
        double dmin = Double.MAX_VALUE;
        itArcs2 = arcs2proches.iterator();
        while (itArcs2.hasNext()) { // pour chaque arc du réseau 2
          arc2 = (Arc) itArcs2.next();
          geom2 = (ILineString) arc2.getGeom();
          projete = Operateurs.projection(pt1, geom2);
          v12 = new Vecteur(pt1, projete);
          d12 = pt1.distance(projete);
          if (d12 < dmin) {
            dmin = d12;
            vmin = v12;
          }
        }
        // calcul des indicateurs
        if (dmin > ecartMaxArc1) {
          ecartMaxArc1 = dmin;
        }
        ecartPourUnArc1 = ecartPourUnArc1 + dmin * poids;
        ecartQuadratiquePourUnArc1 = ecartPourUnArc1 + poids
            * Math.pow(dmin, 2);
        vPourUnArc1 = vPourUnArc1.ajoute(vmin.multConstante(poids));
        poidsPourUnArc1 = poidsPourUnArc1 + poids;
      }
      if (ecartMaxArc1 > distanceMax) {
        continue; // on ne prend pas l'arc en compte
      }
      longPrisEnCompte1 = longPrisEnCompte1 + longArc1;
      nbArcsPrisEnCompte1++;
      poidsTotal = poidsTotal + poidsPourUnArc1;
      ecartTotal = ecartTotal + ecartPourUnArc1;
      ecartQuadratiqueTotal = ecartQuadratiqueTotal
          + ecartQuadratiquePourUnArc1;
      vTotal = vTotal.ajoute(vPourUnArc1);
    }
    vTotal = vTotal.multConstante(1 / poidsTotal);
    ecartTotal = ecartTotal / poidsTotal;
    ecartTypeArcs = Math.sqrt((ecartQuadratiqueTotal / poidsTotal)
        - (Math.pow(vTotal.getX(), 2) + Math.pow(vTotal.getY(), 2)));
    ecartQuadratiqueTotal = Math.sqrt(ecartQuadratiqueTotal / poidsTotal);
    // resultats sur les arcs
    if (affichage) {
      System.out.println(I18N.getString("Comparaison.EdgeAssessment")); //$NON-NLS-1$
    }
    if (affichage) {
      System.out.println(I18N.getString("Comparaison.GlobalNetworkComparison")); //$NON-NLS-1$
    }
    resultats.add(longTotal1);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.TotalEdgeLengthNetwork1") + Math.round(longTotal1 / 1000)); //$NON-NLS-1$
    }
    resultats.add(longPrisEnCompte1);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.TotalEdgeLengthNetwork1Used") + Math.round(longPrisEnCompte1 / 1000)); //$NON-NLS-1$
    }
    resultats.add(longTotal2);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.TotalEdgeLengthNetwork2") + Math.round(longTotal2 / 1000)); //$NON-NLS-1$
    }
    resultats.add((double) nbArcsTotal1);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.NumberOfEdgesNetwork1") + nbArcsTotal1); //$NON-NLS-1$
    }
    resultats.add((double) nbArcsPrisEnCompte1);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.NumberOfEdgesNetwork1Used") + nbArcsPrisEnCompte1); //$NON-NLS-1$
    }
    resultats.add((double) nbArcsTotal2);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.NumberOfEdgesNetwork2") + nbArcsTotal2); //$NON-NLS-1$
    }

    if (affichage) {
      System.out.println(""); //$NON-NLS-1$
    }
    if (affichage) {
      System.out.println(I18N.getString("Comparaison.DeviationEstimation")); //$NON-NLS-1$
    }
    resultats.add(vTotal.getX());
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.SystematicXBias") + vTotal.getX()); //$NON-NLS-1$
    }
    resultats.add(vTotal.getY());
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.SystematicYBias") + vTotal.getY()); //$NON-NLS-1$
    }
    resultats.add(ecartTotal);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.AverageDeviation") + ecartTotal); //$NON-NLS-1$
    }
    resultats.add(ecartQuadratiqueTotal);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.QuadraticAverageDeviation") + ecartQuadratiqueTotal); //$NON-NLS-1$
    }
    resultats.add(ecartTypeArcs);
    if (affichage) {
      System.out
          .println(I18N.getString("Comparaison.StandardDeviation") + ecartTypeArcs); //$NON-NLS-1$
    }
    if (affichage) {
      System.out
          .println("*********************************************************"); //$NON-NLS-1$
    }
    return resultats;
  }

}
