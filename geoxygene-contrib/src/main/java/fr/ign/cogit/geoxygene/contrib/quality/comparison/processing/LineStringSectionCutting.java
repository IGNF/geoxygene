package fr.ign.cogit.geoxygene.contrib.quality.comparison.processing;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
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
 *            Methods used to perform linestrings section cutting
 * 
 * @author JFGirres
 */
public class LineStringSectionCutting {

    /**
     * Découpe une linestring en plusieurs troncons de taille identique
     * @param lsEnEntree
     * @param pas
     * @return
     */

    public static List<ILineString> Tronconnage(ILineString lsEnEntree, double pas) {

        IDirectPositionList dplLsEnEntreeTemp = lsEnEntree.getControlPoint();
        ;
        IDirectPositionList dplTroncon = new DirectPositionList();
        List<ILineString> listLsTroncon = new ArrayList<ILineString>();
        double distanceManquante = pas;

        for (int i = 0; i < dplLsEnEntreeTemp.size(); i++) {
            DirectPositionList dplSegment = new DirectPositionList();
            dplSegment.add(dplLsEnEntreeTemp.get(i));
            dplSegment.add(dplLsEnEntreeTemp.get(i + 1));

            double longueurSegment = Distances.distance(dplSegment.get(i), dplSegment.get(i + 1));

            // Si le segment est plus long que la distance manquante, il faut le
            // couper
            if (longueurSegment > distanceManquante) {
                dplTroncon.add(dplLsEnEntreeTemp.get(i));
                IDirectPosition dpNewPoint = pointSurSegment(dplSegment.get(i), dplSegment.get(i + 1),
                        distanceManquante);
                dplTroncon.add(dpNewPoint);
                ILineString lsTroncon = new GM_LineString(dplTroncon);
                listLsTroncon.add(lsTroncon);
                dplTroncon.removeAll(dplTroncon);
                dplLsEnEntreeTemp.remove(i);
                dplLsEnEntreeTemp.add(0, dpNewPoint);
                distanceManquante = pas;
                i = -1;
            }

            // Si le segment est de longueur identique, on récupère les deux
            // DirectPosition
            else if (longueurSegment == distanceManquante) {
                dplTroncon.add(dplLsEnEntreeTemp.get(i));
                dplTroncon.add(dplLsEnEntreeTemp.get(i + 1));
                GM_LineString lsRefTroncon = new GM_LineString(dplTroncon);
                listLsTroncon.add(lsRefTroncon);
                dplTroncon.removeAll(dplTroncon);
                dplTroncon.add(dplLsEnEntreeTemp.get(i + 1));
                if (dplLsEnEntreeTemp.size() == 2) {
                    dplLsEnEntreeTemp.remove(i + 1);
                    dplLsEnEntreeTemp.remove(i);
                } else {
                    dplLsEnEntreeTemp.remove(i);
                    distanceManquante = pas;
                    i = -1;
                }
            }

            // Si le segment est plus court que la distance manquante, il faut
            // aller
            // couper sur les segments suivants
            else if (longueurSegment < distanceManquante) {
                dplTroncon.add(dplLsEnEntreeTemp.get(i));
                dplTroncon.add(dplLsEnEntreeTemp.get(i + 1));
                if (dplLsEnEntreeTemp.size() == 2) {
                    dplLsEnEntreeTemp.remove(i + 1);
                    dplLsEnEntreeTemp.remove(i);
                } else {
                    dplLsEnEntreeTemp.remove(i);
                    distanceManquante = distanceManquante - longueurSegment;
                    i = -1;
                }
            }
        }
        return listLsTroncon;
    }

    /**
     * Calcule les coordonnées d'un point sur un segment à une certaine distance
     * du premier point. Utilise pour cela l'intersection entre le segment et le
     * cercle de centre p1 et de rayon dist. (merci Guillaume)
     * @param p1 le premier point du segment
     * @param p2 le deuxième point du segment
     * @param dist la distance du point cherché à p1
     * @return
     */
    public static IDirectPosition pointSurSegment(IDirectPosition p1, IDirectPosition p2, double dist) {
        // on définit dx et dy les différences des coordonnées du segment
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        if (dist > Math.sqrt(dx * dx + dy * dy))
            return null;
        // on transforme le calcul de l'intersection en équation du second degré
        // A*t^2 + B*t + C = 0 avec t compris entre 0 et 1
        double A = dx * dx + dy * dy;
        double B = 0.0;// car le centre du cercle est p1
        double C = -dist * dist;
        // on résout l'équation: det = B² - 4AC
        double det = -4.0 * A * C;
        double t = -Math.sqrt(det) / (2.0 * A);
        if (t < 0.0 || t > 1.0)
            t = Math.sqrt(det) / (2.0 * A);
        // on récupère x et y à partir de t et de l'équation du segment
        double x = p1.getX() + t * dx;
        double y = p1.getY() + t * dy;
        return new DirectPosition(x, y);
    }
}
