package fr.ign.cogit.geoxygene.contrib.quality.comparison.processing;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 *            Methods used to perform linestrings extremity cutting
 * 
 * @author JFGirres
 * 
 */
public class LineStringExtremityCutting {

    /**
     * Compute the position of the projected point in the linestring
     * @param lineStringADecouper
     * @param dpPointAPositionner
     * @return
     */
    public static double computeProjectedPointPosition(ILineString lineStringADecouper,
            IDirectPosition dpPointAPositionner) {

        IDirectPositionList listePoints = lineStringADecouper.coord();
        IDirectPosition point;
        double distMin = Double.MAX_VALUE;
        int positionMin = listePoints.size() - 1;
        int positionMin2 = 0;
        double indicePosition = 0;

        for (int j = 0; j < listePoints.size(); j++) {
            point = listePoints.get(j);

            // cas où un point projetté est parfaitement positionné sur un point
            // existant
            if (dpPointAPositionner == point) {
                return j;
            } else {
                double dist = Math
                        .sqrt(((dpPointAPositionner.getX() - point.getX()) * (dpPointAPositionner.getX() - point.getX()))
                                + ((dpPointAPositionner.getY() - point.getY()) * (dpPointAPositionner.getY() - point
                                        .getY())));
                if (distMin > dist) {
                    distMin = dist;
                    positionMin = j;
                }
            }
        }

        if (positionMin > 0 && positionMin < listePoints.size() - 1) {
            // Calcul la distance entre le point projetté et le sommet suivant
            // le
            // point le plus proche
            double distPlus1 = Math
                    .sqrt(((dpPointAPositionner.getX() - listePoints.get(positionMin + 1).getX()) * (dpPointAPositionner
                            .getX() - listePoints.get(positionMin + 1).getX()))
                            + ((dpPointAPositionner.getY() - listePoints.get(positionMin + 1).getY()) * (dpPointAPositionner
                                    .getY() - listePoints.get(positionMin + 1).getY())));
            // Calcul la distance entre le point projetté et le sommet précédent
            // le
            // point le plus proche
            double distMoins1 = Math
                    .sqrt(((dpPointAPositionner.getX() - listePoints.get(positionMin - 1).getX()) * (dpPointAPositionner
                            .getX() - listePoints.get(positionMin - 1).getX()))
                            + ((dpPointAPositionner.getY() - listePoints.get(positionMin - 1).getY()) * (dpPointAPositionner
                                    .getY() - listePoints.get(positionMin - 1).getY())));
            // Calcul la distance entre le sommet le plus proche du point
            // projetté et
            // le sommet précédent
            double distMinPlus1 = Math.sqrt(((listePoints.get(positionMin).getX() - listePoints.get(positionMin + 1)
                    .getX()) * (listePoints.get(positionMin).getX() - listePoints.get(positionMin + 1).getX()))
                    + ((listePoints.get(positionMin).getY() - listePoints.get(positionMin + 1).getY()) * (listePoints
                            .get(positionMin).getY() - listePoints.get(positionMin + 1).getY())));
            // Calcul la distance entre le sommet le plus proche du point
            // projetté et
            // le sommet suivant
            double distMinMoins1 = Math.sqrt(((listePoints.get(positionMin).getX() - listePoints.get(positionMin - 1)
                    .getX()) * (listePoints.get(positionMin).getX() - listePoints.get(positionMin - 1).getX()))
                    + ((listePoints.get(positionMin).getY() - listePoints.get(positionMin - 1).getY()) * (listePoints
                            .get(positionMin).getY() - listePoints.get(positionMin - 1).getY())));

            positionMin2 = positionMin + 1;

            // On part du principe que le point est positionné entre le sommet
            // le plus
            // proche (distMin) et le sommet suivant (distPlus1) ou précdent
            // (distMoins1) le plus proche...
            if (distPlus1 > distMoins1) {
                positionMin2 = positionMin - 1;
            }
            // ... Cependant, il peut arriver que le point projetté soit plus
            // proche
            // du sommet suivant (distPlus1) que du sommet précédent
            // (distMoins1)
            // alors qu'il est positionné entre distMin et distMoins1, donc on
            // teste...
            if (distPlus1 > distMinPlus1) {
                positionMin2 = positionMin - 1;
            }
            if (distMoins1 > distMinMoins1) {
                positionMin2 = positionMin + 1;
            }
            indicePosition = (double) (positionMin + positionMin2) / 2;
        }
        if (positionMin == 0) {
            indicePosition = 0;
        }
        if (positionMin == listePoints.size() - 1) {
            indicePosition = positionMin;
        }
        return indicePosition;
    }

    /**
     * Create a new cutted lineString using two points (start point and end
     * point) and their position in the input linestring.
     * @param dpNewStart
     * @param positionNewStart
     * @param dpNewEnd
     * @param positionNewEnd
     * @param lineStringToCut
     * @return
     */
    public static ILineString createCuttedLineString(IDirectPosition dpNewStart, double positionNewStart,
            IDirectPosition dpNewEnd, double positionNewEnd, ILineString lineStringToCut) {

        // Initialisation des variables
        // On considère que les points de départ et d'arrivée ont été attribués
        // dans
        // l'ordre...
        double positionDepart = positionNewStart;
        double positionArrivee = positionNewEnd;
        IDirectPosition dpDepart = dpNewStart;
        IDirectPosition dpArrivee = dpNewEnd;
        // ... mais il est possible que les points d'arrivée et de départ soient
        // inversés, donc on inverse.
        if (positionNewStart > positionNewEnd) {
            positionDepart = positionNewEnd;
            positionArrivee = positionNewStart;
            dpDepart = dpNewEnd;
            dpArrivee = dpNewStart;
        }

        GM_LineString lineStringCopie = new GM_LineString(lineStringToCut.getControlPoint());
        IDirectPositionList listePointsCopie = lineStringCopie.coord();

        // On boucle par la fin, en supprimant un à un le dernier point jusqu'à
        // la
        // valeur du point d'arrivée
        for (int i = listePointsCopie.size() - 1; i >= (double) positionArrivee; i--) {
            lineStringCopie.removeControlPoint(lineStringCopie.endPoint());
        }
        // ...et on intègre le point d'arrivée
        lineStringCopie.addControlPoint(listePointsCopie.size(), dpArrivee);
        // On boucle ensuite par le début, en supprimant à chaque itération le
        // point
        // de départ, jusqu'à la valeur du point de départ
        for (int i = 0; i <= (double) positionDepart; i++) {
            lineStringCopie.removeControlPoint(lineStringCopie.startPoint());
        }
        // Et on intègre le point de départ
        lineStringCopie.addControlPoint(0, dpDepart);
        return lineStringCopie;
    }
}
