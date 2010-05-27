/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * En vrac un ensemble de méthodes statiques qui manipulent des géométries:
 * projections, abscisse curviligne, décalage, orientation...
 * CONTIENT des méthodes de :
 * Projections d'un point
 * Manipulation de l'abscisse curviligne d'une ligne
 * Mesures sur un polygone
 * Offset d'une ligne (décalage)
 * Echantillonage d'une ligne
 * Regression linéaire
 * et beaucoup d'autres choses très diverses
 * ATTENTION: certaines méthodes n'ont pas été conçues ni testées pour des
 * coordonnées 3D
 * English: Very very diverse set of methods on geometries
 * @author Mustière / Bonin / Rousseaux / Grosso / Lafragueta
 * @version 1.0
 */

public abstract class Operateurs {

    // ////////////////////////////////////////////////////////////////////
    // Projections d'un point
    // ////////////////////////////////////////////////////////////////////

    /**
     * Projection de M sur le segment [A,B]
     * English: Projects M on a [A,B]
     * author Mustière
     */
    public static DirectPosition projection(DirectPosition M, DirectPosition A,
            DirectPosition B) {
        if (Distances.distance(A, B) == 0) return A; // cas ou A et B sont
                                                     // confondus
        Vecteur uAB = new Vecteur(A, B).vectNorme();
        Vecteur AM = new Vecteur(A, M);
        double lambda = AM.prodScalaire(uAB);
        if (lambda <= 0) return A; // Cas ou M se projete en A sur le segment
                                   // [AB]
        if (lambda >= Distances.distance(A, B)) return B; // Cas ou M se projete
                                                          // en B sur le segment
                                                          // [AB]
        return translate(A, uAB.multConstante(lambda)); // Cas ou M se projete
                                                        // entre A et B
    }

    /**
     * Projection du point sur la polyligne.
     * En théorie, il peut y avoir plusieurs points projetés, mais dans ce cas
     * cette méthode n'en renvoie qu'un seul (le premier dans le sens de
     * parcours
     * de la ligne).
     * English: Projects M on the lineString
     * author Mustière
     */
    public static DirectPosition projection(DirectPosition dp, GM_LineString LS) {
        DirectPositionList listePoints = LS.coord();
        double d, dmin;
        DirectPosition pt, ptmin;
        if (listePoints.size() <= 1) return listePoints.get(0);
        ptmin = projection(dp, listePoints.get(0), listePoints.get(1));
        dmin = Distances.distance(dp, ptmin);
        for (int i = 0; i < listePoints.size() - 1; i++) {
            pt = projection(dp, listePoints.get(i), listePoints.get(i + 1));
            d = Distances.distance(dp, pt);
            if (d < dmin) {
                ptmin = pt;
                dmin = d;
            }
        }
        return ptmin;
    }

    /**
     * Projection du point sur la polyligne et insertion du point projeté dans
     * la ligne
     * English: Projects M on the lineString and return the line with the
     * projected point inserted
     * author Mustière
     */
    public static GM_LineString projectionEtInsertion(DirectPosition point,
            GM_LineString line) {
        DirectPositionList points = line.coord();
        projectAndInsert(point, points);
        GM_LineString newLine = new GM_LineString(points);
        return newLine;
    }

    /**
     * Projection du point sur la polyligne et insertion du point projeté dans
     * la ligne
     * English: Projects M on the lineString and return the line with the
     * projected point inserted
     * author Mustière
     */
    public static void projectAndInsert(DirectPosition point,
            DirectPositionList points) {
        if (points.size() < 2) { return; }
        DirectPosition ptmin = projection(point, points.get(0), points.get(1));
        double dmin = Distances.distance(point, ptmin);
        int imin = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            DirectPosition pt = projection(point, points.get(i), points.get(i + 1));
            double d = Distances.distance(point, pt);
            if (d < dmin) {
                ptmin = pt;
                dmin = d;
                imin = i;
            }
        }
        points.add(imin + 1, ptmin);
    }

    /**
     * Projection du point sur l'aggregat;
     * ATTENTION: ne fonctionne que si l'aggregat ne contient que des GM_Point
     * et GM_LineString.
     * En théorie, il peut y avoir plusieurs points projetés, mais dans ce cas
     * cette méthode n'en renvoie qu'un seul.
     * English: Projects M on the agregate
     * author Mustière
     */
    public static DirectPosition projection(DirectPosition dp,
            GM_Aggregate<GM_Object> aggr) {
        Iterator<GM_Object> itComposants = aggr.getList().iterator();
        double d = 0, dmin = Double.MAX_VALUE;
        DirectPosition pt = null, ptmin = null;
        boolean geomOK;

        while (itComposants.hasNext()) {
            GM_Object composant = itComposants.next();
            geomOK = false;
            if (composant instanceof GM_Point) {
                pt = ((GM_Point) composant).getPosition();
                d = Distances.distance(pt, dp);
                geomOK = true;
            }
            if (composant instanceof GM_LineString) {
                pt = projection(dp, (GM_LineString) composant);
                d = Distances.distance(pt, dp);
                geomOK = true;
            }
            if (!geomOK) {
                System.out.println("Projection - Type de géométrie non géré: "
                        + composant.getClass());
                continue;
            }
            if (d < dmin) {
                ptmin = pt;
                dmin = d;
            }
        }
        return ptmin;
    }

    // ////////////////////////////////////////////////////////////////////
    // Manipulation de l'abscisse curviligne d'une ligne
    // ////////////////////////////////////////////////////////////////////

    /**
     * coordonnées du point situé sur la ligne à l'abscisse curviligne passée en
     * paramètre.
     * Renvoie Null si l'abscisse est négative ou plus grande que la longueur de
     * la ligne.
     * English: Point located at the curvilinear abscisse
     * author Mustière
     */
    public static DirectPosition pointEnAbscisseCurviligne(GM_LineString ls,
            double abscisse) {
        int i;
        double l = 0;
        double d;
        DirectPosition pt1, pt2;
        Vecteur v1;

        if (abscisse > ls.length() || abscisse < 0) return null;
        pt1 = ls.coord().get(0);
        for (i = 1; i < ls.coord().size(); i++) {
            pt2 = ls.coord().get(i);
            d = Distances.distance(pt1, pt2);
            if (d != 0) {
                if (l + d > abscisse) {
                    v1 = new Vecteur(pt1, pt2);
                    v1 = v1.multConstante((abscisse - l) / d);
                    return translate(pt1, v1);
                }
                l = l + d;
                pt1 = pt2;
            }
        }
        return ls.coord().get(ls.coord().size() - 1);
    }

    /**
     * Abscisse curviligne du ieme point de la ligne ls.
     * English: curvilinear abscisse of the ith point
     * author Mustière
     */
    public static double abscisseCurviligne(GM_LineString ls, int i) {
        double abs = 0;
        for (int j = 0; j < i; j++) {
            abs = abs
                    + Distances.distance(ls.getControlPoint(j), ls
                            .getControlPoint(j + 1));
        }
        return abs;
    }

    /**
     * coordonnées du point situé sur au milieu de la ligne.
     * English: Point in the middle of the line
     * author Mustière
     */
    public static DirectPosition milieu(GM_LineString ls) {
        return pointEnAbscisseCurviligne(ls, ls.length() / 2);
    }

    /**
     * renvoie le milieu de [A,B].
     * English: Point in the middle of [A,B]
     * author Mustière
     */
    public static DirectPosition milieu(DirectPosition A, DirectPosition B) {
        DirectPosition M;
        if (!Double.isNaN(A.getZ()) && !Double.isNaN(B.getZ())) {
            M = new DirectPosition((A.getX() + B.getX()) / 2, (A.getY() + B
                    .getY()) / 2, (A.getZ() + B.getZ()) / 2);
        } else {
            M = new DirectPosition((A.getX() + B.getX()) / 2, (A.getY() + B
                    .getY()) / 2, Double.NaN);
        }
        return M;
    }

    /**
     * Premiers points intermédiaires de la ligne ls, situés à moins
     * de la longueur curviligne passée en paramètre du point initial.
     * Renvoie null si la longueur est négative.
     * Renvoie le premier point si et seulement si la longueur est 0.
     * Renvoie tous les points si la longueur est supérieure à la longueur de la
     * ligne
     * NB: les points sont renvoyés dans l'ordre en partant du premier point.
     * English: First points of the line.
     * author Mustière
     */
    public static DirectPositionList premiersPoints(GM_LineString ls,
            double longueur) {
        int i;
        double l = 0;
        DirectPositionList listePts = new DirectPositionList();

        if (longueur < 0) return null;
        listePts.add(ls.getControlPoint(0));
        for (i = 1; i < ls.coord().size(); i++) {
            l = l
                    + Distances.distance(ls.getControlPoint(i - 1), ls
                            .getControlPoint(i));
            if (l > longueur) break;
            listePts.add(ls.getControlPoint(i));
        }
        return listePts;
    }

    /**
     * Derniers points intermédiaires de la ligne ls, situés à moins
     * de la longueur curviligne passée en paramètre du point final.
     * Renvoie null si la longueur est négative.
     * Renvoie le dernier point seulement si la longueur est 0.
     * Renvoie tous les points si la longueur est supérieure à la longueur de la
     * ligne.
     * NB: les points sont renvoyés dans l'ordre en partant du dernier point
     * (ordre inverse par rapport à la géométrie initiale).
     * English: Last points of the line.
     * author Mustière
     */
    public static DirectPositionList derniersPoints(GM_LineString ls,
            double longueur) {
        int i;
        double l = 0;
        DirectPositionList listePts = new DirectPositionList();
        int nbPts = ls.coord().size();

        if (longueur < 0) return null;
        listePts.add(ls.getControlPoint(nbPts - 1));
        for (i = nbPts - 2; i >= 0; i--) {
            l = l
                    + Distances.distance(ls.getControlPoint(i), ls
                            .getControlPoint(i + 1));
            if (l > longueur) break;
            listePts.add(ls.getControlPoint(i));
        }
        return listePts;
    }

    // ////////////////////////////////////////////////////////////////////
    // Mesures sur un polygone
    // ////////////////////////////////////////////////////////////////////
    /**
     * Barycentre 2D (approximatif).
     * Il est défini comme le barycentre des points intermédiaires du contour,
     * ce qui est très approximatif
     * English: Center of the points of the polygon.
     * author Mustière
     */
    public static DirectPosition barycentre2D(GM_Polygon poly) {
        DirectPositionList listePoints = poly.coord();
        DirectPosition barycentre;
        double moyenneX = 0;
        double moyenneY = 0;
        double sommeX = 0;
        double sommeY = 0;

        for (int i = 0; i < listePoints.size() - 1; i++) {
            sommeX = sommeX + listePoints.get(i).getX();
            sommeY = sommeY + listePoints.get(i).getY();
        }
        moyenneX = sommeX / (listePoints.size() - 1);
        moyenneY = sommeY / (listePoints.size() - 1);

        barycentre = new DirectPosition(moyenneX, moyenneY);
        return (barycentre);
    }

    // ////////////////////////////////////////////////////////////////////
    // Offset d'une ligne (décalage)
    // ////////////////////////////////////////////////////////////////////
    /**
     * Calcul d'un offset direct (demi-buffer d'une ligne, ou décalage à
     * gauche).
     * Le paramètre offset est la taille du décalage.
     * English: shift of a line on the left
     * author Bonin, Rousseaux.
     */
    public static GM_LineString directOffset(GM_LineString ls, double offset) {
        DirectPositionList listePoints = ls.coord();
        DirectPosition point, pointPrec, pointSuiv, pointRes;
        Vecteur u, v, n;
        GM_LineString ligneResultat = new GM_LineString();
        u = new Vecteur(listePoints.get(0), listePoints.get(1));
        u.setZ(0.0);
        pointRes = new DirectPosition(listePoints.get(0).getX() + offset
                * u.vectNorme().getY(), listePoints.get(0).getY() - offset
                * u.vectNorme().getX(), listePoints.get(0).getZ());
        ligneResultat.addControlPoint(pointRes);
        for (int j = 1; j < listePoints.size() - 1; j++) {
            pointPrec = listePoints.get(j - 1);
            point = listePoints.get(j);
            pointSuiv = listePoints.get(j + 1);
            u = new Vecteur(pointPrec, point);
            u.setZ(0);
            v = new Vecteur(point, pointSuiv);
            v.setZ(0);
            n = u.vectNorme().soustrait(v.vectNorme());
            if (u.prodVectoriel(v).getZ() > 0) {
                pointRes = new DirectPosition(point.getX() + offset
                        * n.vectNorme().getX(), point.getY() + offset
                        * n.vectNorme().getY(), point.getZ());
                ligneResultat.addControlPoint(pointRes);
            } else {
                pointRes = new DirectPosition(point.getX() - offset
                        * n.vectNorme().getX(), point.getY() - offset
                        * n.vectNorme().getY(), point.getZ());
                ligneResultat.addControlPoint(pointRes);
            }
        }
        u = new Vecteur(listePoints.get(listePoints.size() - 2), listePoints
                .get(listePoints.size() - 1));
        u.setZ(0.0);
        pointRes = new DirectPosition(listePoints.get(listePoints.size() - 1)
                .getX()
                + offset * u.vectNorme().getY(), listePoints.get(
                listePoints.size() - 1).getY()
                - offset * u.vectNorme().getX(), listePoints.get(
                listePoints.size() - 1).getZ());
        ligneResultat.addControlPoint(pointRes);
        return ligneResultat;
    }

    /**
     * Calcul d'un offset indirect (demi-buffer d'une ligne, ou décalage à
     * droite).
     * Le paramètre offset est la taille du décalage.
     * English: shift of a line on the right
     * author Bonin, Rousseaux.
     */
    public static GM_LineString indirectOffset(GM_LineString ls, double offset) {
        DirectPositionList listePoints = ls.coord();
        DirectPosition point, pointPrec, pointSuiv, pointRes;
        Vecteur u, v, n;
        GM_LineString ligneResultat = new GM_LineString();
        u = new Vecteur(listePoints.get(0), listePoints.get(1));
        u.setZ(0);
        pointRes = new DirectPosition(listePoints.get(0).getX() - offset
                * u.vectNorme().getY(), listePoints.get(0).getY() + offset
                * u.vectNorme().getX(), listePoints.get(0).getZ());
        ligneResultat.addControlPoint(pointRes);
        for (int j = 1; j < listePoints.size() - 1; j++) {
            pointPrec = listePoints.get(j - 1);
            point = listePoints.get(j);
            pointSuiv = listePoints.get(j + 1);
            u = new Vecteur(pointPrec, point);
            u.setZ(0);
            v = new Vecteur(point, pointSuiv);
            v.setZ(0);
            n = u.vectNorme().soustrait(v.vectNorme());
            if (u.prodVectoriel(v).getZ() < 0) {
                pointRes = new DirectPosition(point.getX() + offset
                        * n.vectNorme().getX(), point.getY() + offset
                        * n.vectNorme().getY(), point.getZ());
                ligneResultat.addControlPoint(pointRes);
            } else {
                pointRes = new DirectPosition(point.getX() - offset
                        * n.vectNorme().getX(), point.getY() - offset
                        * n.vectNorme().getY(), point.getZ());
                ligneResultat.addControlPoint(pointRes);
            }
        }
        u = new Vecteur(listePoints.get(listePoints.size() - 2), listePoints
                .get(listePoints.size() - 1));
        u.setZ(0);
        pointRes = new DirectPosition(listePoints.get(listePoints.size() - 1)
                .getX()
                - offset * u.vectNorme().getY(), listePoints.get(
                listePoints.size() - 1).getY()
                + offset * u.vectNorme().getX(), listePoints.get(
                listePoints.size() - 1).getZ());
        ligneResultat.addControlPoint(pointRes);
        return ligneResultat;
    }

    // ////////////////////////////////////////////////////////////////////
    // Echantillonage
    // ////////////////////////////////////////////////////////////////////
    /**
     * méthode pour suréchantillonner une GM_LineString.
     * Des points intermédiaires écartés du pas sont ajoutés sur chaque segment
     * de la ligne ls, à partir du premier point de chaque segment.
     * (voir aussi echantillonePasVariable pour une autre méthode )
     * English: Resampling of a line
     * author Bonin, Rousseaux.
     */
    public static GM_LineString echantillone(GM_LineString ls, double pas) {
        DirectPosition point1, point2, Xins;
        Vecteur u1;
        int nseg, i;
        double longTronc;
        Double fseg;
        GM_LineString routeSurech = (GM_LineString) ls.clone();
        DirectPositionList listePoints = routeSurech.coord();
        DirectPositionList listePointsEchant = new DirectPositionList();
        for (int j = 1; j < listePoints.size(); j++) {
            point1 = listePoints.get(j - 1);
            listePointsEchant.add(point1);
            point2 = listePoints.get(j);
            longTronc = Distances.distance(point1, point2);
            fseg = new Double(longTronc / pas);
            nseg = fseg.intValue();
            u1 = new Vecteur(point1, point2);
            for (i = 0; i < nseg - 1; i++) {
                Xins = new DirectPosition(point1.getX() + (i + 1) * pas
                        * u1.vectNorme().getX(), point1.getY() + (i + 1) * pas
                        * u1.vectNorme().getY(), point1.getZ() + (i + 1) * pas
                        * u1.vectNorme().getZ());
                listePointsEchant.add(Xins);
            }
        }
        listePointsEchant.add(listePoints.get(listePoints.size() - 1));
        routeSurech = new GM_LineString(listePointsEchant);
        return routeSurech;
    }

    /**
     * méthode pour suréchantillonner une GM_LineString.
     * A l'inverse de la méthode "echantillone", le pas d'echantillonage
     * diffère sur chaque segment de manière à ce que l'on échantillone chaque
     * segment en différents mini-segments tous de même longueur.
     * Le pas en entrée est le pas maximum autorisé.
     * English : Resampling of a line
     * author Grosso.
     */
    public static GM_LineString echantillonePasVariable(GM_LineString ls,
            double pas) {
        DirectPosition point1, point2, Xins;
        Vecteur u1;
        int nseg, i;
        double longTronc;
        Double fseg;
        GM_LineString routeSurech = (GM_LineString) ls.clone();
        DirectPositionList listePoints = routeSurech.coord();
        DirectPositionList listePointsEchant = new DirectPositionList();
        for (int j = 1; j < listePoints.size(); j++) {
            point1 = listePoints.get(j - 1);
            listePointsEchant.add(point1);
            point2 = listePoints.get(j);
            longTronc = Distances.distance(point1, point2);
            fseg = new Double(longTronc / pas);
            nseg = fseg.intValue();
            double epsilonPas = 0;
            if (nseg != 0) {
                epsilonPas = longTronc / nseg - pas;
            }
            double nouveauPas = pas - epsilonPas;
            u1 = new Vecteur(point1, point2);
            for (i = 0; i < nseg - 1; i++) {
                Xins = new DirectPosition(point1.getX() + (i + 1) * nouveauPas
                        * u1.vectNorme().getX(), point1.getY() + (i + 1)
                        * nouveauPas * u1.vectNorme().getY(), point1.getZ()
                        + (i + 1) * nouveauPas * u1.vectNorme().getZ());
                listePointsEchant.add(Xins);
            }
        }
        listePointsEchant.add(listePoints.get(listePoints.size() - 1));
        routeSurech = new GM_LineString(listePointsEchant);
        return routeSurech;
    }

    /**
     * Renvoie le point translaté de P avec le vecteur V;
     * Contrairement au "move" de DirectPosition, on ne deplace pas le point P
     * English : Shift of a point
     */
    public static DirectPosition translate(DirectPosition P, Vecteur V) {
        if (!Double.isNaN(P.getZ()) && !Double.isNaN(V.getZ())) { return new DirectPosition(
                P.getX() + V.getX(), P.getY() + V.getY(), P.getZ() + V.getZ()); }
        return new DirectPosition(P.getX() + V.getX(), P.getY() + V.getY(),
                Double.NaN);
    }

    // ////////////////////////////////////////////////////////////////////
    // (très) Divers
    // ////////////////////////////////////////////////////////////////////

    /**
     * Mise bout à bout de plusieurs GM_LineString pour constituer une nouvelle
     * GM_LineString
     * La liste en entrée contient des GM_LineString.
     * La polyligne créée commence sur l'extrémité libre de la Première
     * polyligne de la liste.
     * English: Combination of lines
     * author: Mustière
     */
    public static GM_LineString compileArcs(List<GM_LineString> geometries) {
        DirectPositionList pointsFinaux = new DirectPositionList();
        DirectPosition pointCourant;
        GM_LineString LSCourante, LSSuivante, LSCopie;
        if (geometries.size() == 0) {
            System.out
                    .println("ATTENTION. Erreur à la compilation de lignes : aucune ligne en entrée");
            return null;
        }

        LSCourante = geometries.get(0);

        if (geometries.size() == 1) { return LSCourante; }

        LSSuivante = geometries.get(1);

        if (Distances.proche(LSCourante.startPoint(), LSSuivante.startPoint(),
                0)
                || Distances.proche(LSCourante.startPoint(), LSSuivante
                        .endPoint(), 0)) {
            // premier point = point finale de la premiere ligne
            pointsFinaux.addAll(((GM_LineString) LSCourante.reverse())
                    .getControlPoint());
            pointCourant = LSCourante.startPoint();
        } else if (Distances.proche(LSCourante.endPoint(), LSSuivante
                .startPoint(), 0)
                || Distances.proche(LSCourante.endPoint(), LSSuivante
                        .endPoint(), 0)) {
            // premier point = point initial de la premiere ligne
            pointsFinaux.addAll(LSCourante.getControlPoint());
            pointCourant = LSCourante.endPoint();
        } else {
            System.out
                    .println("ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
            return null;
        }

        for (int i = 1; i < geometries.size(); i++) {
            LSSuivante = geometries.get(i);
            LSCopie = new GM_LineString(LSSuivante.getControlPoint());
            if (Distances.proche(pointCourant, LSSuivante.startPoint(), 0)) {
                // LSSuivante dans le bon sens
                LSCopie.removeControlPoint(LSCopie.startPoint());
                pointsFinaux.addAll(LSCopie.getControlPoint());

                // quel intérêt à cette ligne???
                pointCourant = LSCopie.endPoint();
            } else if (Distances.proche(pointCourant, LSSuivante.endPoint(), 0)) {
                // LSSuivante dans le bon sens
                LSCopie.removeControlPoint(LSCopie.endPoint());
                pointsFinaux.addAll(((GM_LineString) LSCopie.reverse())
                        .getControlPoint());

                // quel intérêt à cette ligne???
                pointCourant = LSCopie.startPoint();
            } else {
                System.out
                        .println("ATTENTION. Erreur à la compilation de lignes (Operateurs) : les lignes ne se touchent pas");
                return null;
            }
        }

        return new GM_LineString(pointsFinaux);
    }

    /**
     * Version plus robuste mais aussi potentiellement faussée de
     * l'intersection.
     * Si JTS plante au calcul d'intersection, on filtre les surfaces avec
     * Douglas et Peucker,
     * progressivement avec 10 seuils entre min et max.
     * English: Robust intersection of objects (to bypass JTS bugs)
     * author: Mustière
     */
    public static GM_Object intersectionRobuste(GM_Object A, GM_Object B,
            double min, double max) {
        GM_Object intersection, Amodif, Bmodif;
        double seuilDouglas;
        intersection = A.intersection(B);

        if (intersection != null) return intersection;
        for (int i = 0; i < 10; i++) {
            seuilDouglas = min + i * (max - min) / 10;
            Amodif = Filtering.DouglasPeucker(A, seuilDouglas);
            Bmodif = Filtering.DouglasPeucker(B, seuilDouglas);
            intersection = Amodif.intersection(Bmodif);
            if (intersection != null) {
                System.out
                        .println("Calcul d'intersection fait après filtrage avec Douglas Peucker à "
                                + seuilDouglas
                                + "m, pour cause de plantage de JTS");
                return intersection;
            }
        }
        System.out
                .println("ATTENTION : Plantage du calcul d'intersection, même après nettoyage de la géométrie avec Douglas Peucker");
        return null;
    }

    /**
     * Version plus robuste mais aussi potentiellement faussée de l'union.
     * Si JTS plante au calcul d'union, on filtre les surfaces avec Douglas et
     * Peucker,
     * progressivement avec 10 seuils entre min et max.
     * English: Robust union of objects (to bypass JTS bugs)
     * author: Mustière
     */
    public static GM_Object unionRobuste(GM_Object A, GM_Object B, double min,
            double max) {
        GM_Object union, Amodif, Bmodif;
        double seuilDouglas;

        union = A.union(B);

        if (union != null) return union;
        for (int i = 0; i < 10; i++) {
            seuilDouglas = min + i * (max - min) / 10;
            Amodif = Filtering.DouglasPeucker(A, seuilDouglas);
            Bmodif = Filtering.DouglasPeucker(B, seuilDouglas);
            union = Amodif.union(Bmodif);
            if (union != null) {
                System.out
                        .println("Calcul d'union fait après filtrage avec Douglas Peucker à "
                                + seuilDouglas
                                + "m, pour cause de plantage de JTS");
                return union;
            }
        }
        System.out
                .println("ATTENTION : Plantage du calcul d'union, même après nettoyage de la géométrie avec Douglas Peucker");
        return null;
    }

    // ////////////////////////////////////////////////////////////////////
    // Regression linéaire
    // ////////////////////////////////////////////////////////////////////
    /**
     * Methode qui donne l'angle (radians) par rapport à l'axe des x de la
     * droite passant
     * au mieux au milieu d'un nuage de points (regression par moindres carrés).
     * Cet angle (défini à pi près) est entre 0 et pi.
     * English: Linear approximation
     * author: grosso
     */
    public static Angle directionPrincipale(DirectPositionList listePts) {
        Angle ang = new Angle();
        double angle;
        double x0, y0, x, y, a;
        double moyenneX = 0, moyenneY = 0;
        Matrix Atrans, A, B, X;
        int i;

        // cas où la ligne n'a que 2 pts
        if ((listePts.size() == 2)) {
            ang = new Angle(listePts.get(0), listePts.get(1));
            angle = ang.getValeur();
            if (angle >= Math.PI) return new Angle(angle - Math.PI);
            return new Angle(angle);
        }

        // initialisation des matrices
        // On stocke les coordonnées, en se ramenant dans un repère local sur
        // (x0,y0)
        A = new Matrix(listePts.size(), 1); // X des points de la ligne
        B = new Matrix(listePts.size(), 1); // Y des points de la ligne
        x0 = listePts.get(0).getX();
        y0 = listePts.get(0).getY();
        for (i = 0; i < listePts.size(); i++) {
            x = listePts.get(i).getX() - x0;
            moyenneX = moyenneX + x;
            A.set(i, 0, x);
            y = listePts.get(i).getY() - y0;
            moyenneY = moyenneY + y;
            B.set(i, 0, y);
        }
        moyenneX = moyenneX / listePts.size();
        moyenneY = moyenneY / listePts.size();

        // cas où l'angle est vertical
        if (moyenneX == 0) return new Angle(Math.PI / 2);

        // cas général : on cherche l'angle par régression linéaire
        Atrans = A.transpose();
        X = (Atrans.times(A)).inverse().times(Atrans.times(B));
        a = X.get(0, 0);
        angle = Math.atan(a);
        // on obtient un angle entre -pi/2 et pi/2 ouvert
        // on replace cet angle dans 0 et pi
        if (angle < 0) return new Angle(angle + Math.PI);
        return new Angle(angle);
    }

    /**
     * Methode qui donne l'angle dans [0,2*pi[ par rapport à l'axe des x,
     * de la droite orientée passant au mieux au milieu d'un nuage de points
     * ordonnés
     * (regression par moindres carrés).
     * L'ordre des points en entrée est important, c'est lui qui permet de
     * donner
     * l'angle à 2.pi près.
     * Exemple: la liste des points peut correspondre à n points d'un arc,
     * l'angle
     * représente alors l'orientation générale de ces points, en prenant le
     * premier
     * pour point de départ.
     * English: Linear approximation
     * author: grosso
     */
    public static Angle directionPrincipaleOrientee(DirectPositionList listePts) {
        double angle;
        double x0, y0, x, y, a;
        double moyenneX = 0, moyenneY = 0;
        Matrix Atrans, A, B, X;
        int i;

        // cas où la ligne n'a que 2 pts
        if ((listePts.size() == 2)) { return new Angle(listePts.get(0),
                listePts.get(1)); }

        // initialisation des matrices
        // On stocke les coordonnées, en se ramenant dans un repère local sur
        // (x0,y0)
        A = new Matrix(listePts.size(), 1); // X des points de la ligne
        B = new Matrix(listePts.size(), 1); // Y des points de la ligne
        x0 = listePts.get(0).getX();
        y0 = listePts.get(0).getY();
        for (i = 0; i < listePts.size(); i++) {
            x = listePts.get(i).getX() - x0;
            moyenneX = moyenneX + x;
            A.set(i, 0, x);
            y = listePts.get(i).getY() - y0;
            moyenneY = moyenneY + y;
            B.set(i, 0, y);
        }
        moyenneX = moyenneX / listePts.size();
        moyenneY = moyenneY / listePts.size();

        // cas où l'angle est vertical
        if (moyenneX == 0) {
            if (moyenneY < 0) return new Angle(3 * Math.PI / 2);
            if (moyenneY > 0) return new Angle(Math.PI / 2);
        }

        // cas général : on cherche l'angle par régression linéaire
        Atrans = A.transpose();
        X = (Atrans.times(A)).inverse().times(Atrans.times(B));
        a = X.get(0, 0);
        // on obtient un angle entre -pi/2 et pi/2 ouvert
        angle = Math.atan(a);
        // on replace cet angle dans 0 et 2pi
        if (moyenneY < 0) {
            if (angle >= 0) return new Angle(angle + Math.PI);
            return new Angle(angle + 2 * Math.PI);
        }
        if (angle < 0) return new Angle(angle + Math.PI);
        return new Angle(angle);
    }

    // ////////////////////////////////////////////////////////////////////
    // Divers
    // ////////////////////////////////////////////////////////////////////
    /**
     * Teste si 2 <code>DirectPosition</code>s ont les mêmes coordonnées.
     * Le test est effectué en 3D :
     * <ul>
     * <li>si le premier point n'a pas de Z, le second ne doit pas en avoir pour
     * être égal.
     * <li>si le premier point possède un Z, sa valeur est comparée au Z du
     * second.
     * </ul>
     * English: Tests the equality of geometries in 3D
     * @param pt1 une position
     * @param pt2 une position
     * @return vrai si les deux <code>DirectPosition</code>s ont les mêmes
     *         coordonnées.
     */
    public static boolean superposes(DirectPosition pt1, DirectPosition pt2) {
        return (pt1.getX() == pt2.getX())
                && (pt1.getY() == pt2.getY())
                && (Double.isNaN(pt1.getZ()) ? Double.isNaN(pt2.getZ()) : (pt1
                        .getZ() == pt2.getZ()));
    }

    /**
     * Teste si 2 <code>DirectPosition</code>s ont les mêmes coordonnées.
     * Le test est effectué en 2D : aucun Z n'est considéré.
     * English: Tests the equality of geometries in 2D
     * @param pt1 une position
     * @param pt2 une position
     * @return vrai si les deux <code>DirectPosition</code>s ont les mêmes
     *         coordonnées en 2D.
     */
    public static boolean superposes2D(DirectPosition pt1, DirectPosition pt2) {
        return (pt1.getX() == pt2.getX()) && (pt1.getY() == pt2.getY());
    }

    /**
     * Teste si 2 <code>GM_Point</code>s ont les mêmes coordonnées.
     * English: Tests the equality of geometries
     */
    public static boolean superposes(GM_Point pt1, GM_Point pt2) {
        return superposes(pt1.getPosition(), pt2.getPosition());
    }

    /**
     * Teste la présence d'un DirectPosition (égalité 2D) dans une
     * DirectPositionList.
     * Renvoie -1 si le directPosition n'est pas dans la liste
     * English: tests if the line contains the point (in 2D)
     */
    public static int indice2D(DirectPositionList dpl, DirectPosition dp) {
        int i;
        DirectPosition dp1;
        for (i = 0; i < dpl.size(); i++) {
            dp1 = dpl.get(i);
            if ((dp1.getX() == dp.getX()) && (dp1.getY() == dp.getY()))
                return i;
        }
        return -1;
    }

    /**
     * Teste la présence d'un DirectPosition (égalité 3D) dans une
     * DirectPositionList.
     * Renvoie -1 si le directPosition n'est pas dans la liste
     * English: tests if the line contains the point (in 3D)
     */
    public static int indice3D(DirectPositionList dpl, DirectPosition dp) {
        int i;
        DirectPosition dp1;
        for (i = 0; i < dpl.size(); i++) {
            dp1 = dpl.get(i);
            if ((dp1.getX() == dp.getX()) && (dp1.getY() == dp.getY())
                    && (dp1.getZ() == dp.getZ())) return i;
        }
        return -1;
    }

    /**
     * Attribue par interpolation un Z aux points d'une ligne en connaissant le
     * Z
     * des extrémités.
     * English: Z interpolation
     * author : Arnaud Lafragueta
     */
    public static GM_LineString calculeZ(GM_LineString ligne) {

        DirectPosition pointIni = ligne.startPoint();
        DirectPosition pointFin = ligne.endPoint();
        double z_ini = pointIni.getZ();
        double z_fin = pointFin.getZ();
        DirectPositionList listePoints = ligne.coord();
        double longueur = 0.0;
        double zCalc;
        DirectPosition pointRoute, pointRoute1;
        for (int j = 1; j < listePoints.size() - 1; j++) {
            pointRoute = listePoints.get(j);
            pointRoute1 = listePoints.get(j - 1);
            longueur = longueur + Distances.distance(pointRoute, pointRoute1);
            zCalc = z_ini + (z_fin - z_ini) * longueur / ligne.length();
            pointRoute.setZ(zCalc);
            ligne.setControlPoint(j, pointRoute);
        }

        return ligne;
    }

    /**
     * Fusionne les surfaces adjacentes d'une population.
     * NB: quand X objets sont fusionés, un des objets (au hasard) est gardé
     * avec ses attributs et sa géométrie est remplacée par celle fusionée.
     * English: aggregation of surfaces
     */
    @SuppressWarnings("unchecked")
    public static void fusionneSurfaces(Population popSurf) {

        Iterator itSurf = popSurf.getElements().iterator();
        Iterator itSurfAdjacentes;
        List aEnlever = new ArrayList();
        GM_Object surfaceAfusionner, surfFusionnee;
        FT_Feature objSurf, objAfusionner, objetAEnlever;

        if (!popSurf.hasSpatialIndex())
            popSurf.initSpatialIndex(Tiling.class, true);

        while (itSurf.hasNext()) {
            objSurf = (FT_Feature) itSurf.next();
            if (aEnlever.contains(objSurf)) continue;
            Collection<FT_Feature> surfAdjacentes = popSurf.select(objSurf.getGeom());
            surfAdjacentes.remove(objSurf);
            if (surfAdjacentes.size() == 0) continue;
            aEnlever.addAll(surfAdjacentes);
            itSurfAdjacentes = surfAdjacentes.iterator();
            // ATTENTION: bidouille ci-dessous pour pallier l'absence de "copie"
            // générique de géométrie
            surfFusionnee = new GM_Polygon(((GM_Polygon) objSurf.getGeom())
                    .boundary());
            while (itSurfAdjacentes.hasNext()) {
                objAfusionner = (FT_Feature) itSurfAdjacentes.next();
                surfaceAfusionner = objAfusionner.getGeom();
                surfFusionnee = surfFusionnee.union(surfaceAfusionner);
            }
            objSurf.setGeom(surfFusionnee);
        }
        Iterator itAEnlever = aEnlever.iterator();
        while (itAEnlever.hasNext()) {
            objetAEnlever = (FT_Feature) itAEnlever.next();
            popSurf.enleveElement(objetAEnlever);
        }

    }

    /**
     * Dilate les surfaces de la population.
     * English: dilates surfaces
     */
    public static void bufferSurfaces(Population<FT_Feature> popSurf,
            double tailleBuffer) {

        FT_Feature objSurf;
        Iterator<FT_Feature> itSurf = popSurf.getElements().iterator();

        while (itSurf.hasNext()) {
            objSurf = itSurf.next();
            objSurf.setGeom(objSurf.getGeom().buffer(tailleBuffer));
        }
    }

    /**
     * Surface d'un polygone (trous non gérés).
     * Utile pour pallier aux déficiences de JTS qui n'accèpte pas les
     * géométries dégénérées.
     * Le calcul est effectué dans un repère local centré sur le premier point
     * de la surface, ce qui est utile pour minimiser les erreurs de calcul
     * si on manipule de grandes coordonnées).
     * English: surface of a polygon
     */
    public static double surface(GM_Polygon poly) {
        DirectPositionList pts = poly.exteriorCoord();
        DirectPosition pt1, pt2;
        double ymin;
        double surf = 0;

        pt1 = pts.get(0);
        ymin = pt1.getY();
        for (int i = 1; i < pts.size(); i++) {
            pt2 = pts.get(i);
            surf = surf
                    + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin))
                    / 2;
            pt1 = pt2;
        }
        return Math.abs(surf);
    }

    /**
     * Surface d'un polygone (liste de points supposée fermée).
     * English: surface of a polygon
     */
    public static double surface(DirectPositionList pts) {
        DirectPosition pt1, pt2;
        double ymin;
        double surf = 0;

        pt1 = pts.get(0);
        ymin = pt1.getY();
        for (int i = 1; i < pts.size(); i++) {
            pt2 = pts.get(i);
            surf = surf
                    + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin))
                    / 2;
            pt1 = pt2;
        }
        return Math.abs(surf);
    }

    /**
     * détermine si une liste de points tourne dans le sens direct ou non.
     * NB : La liste de points est supposée fermée (premier point = dernier
     * point).
     * NB : renvoie true pour une surface dégénérée.
     * English : orientation of a polygon (direct rotation?)
     */
    public static boolean sensDirect(DirectPositionList pts) {
        Iterator<DirectPosition> itPts = pts.getList().iterator();
        DirectPosition pt1, pt2;
        double ymin;
        double surf = 0;
        pt1 = itPts.next();
        ymin = pt1.getY();
        while (itPts.hasNext()) {
            pt2 = itPts.next();
            surf = surf
                    + ((pt2.getX() - pt1.getX()) * (pt2.getY() + pt1.getY() - 2 * ymin));
            pt1 = pt2;
        }
        return (surf <= 0);
    }
	/**
	 * Compute the surface defined by 2 lineStrings.
	 * @param lineString1 line1
	 * @param lineString2 line2
	 * @return a polygon
	 */
	public static GM_Polygon surfaceFromLineStrings(
			GM_LineString lineString1,
			GM_LineString lineString2) {
		//fabrication de la surface delimitée par les lignes
		GM_LineString perimetre = new GM_LineString();
		//Initialisation de Distance1 = Somme des côtés à créer
		double sommeDistance1 = Distances.distance(lineString1.startPoint(), lineString2.startPoint()) + 
					Distances.distance(lineString1.endPoint(), lineString2.endPoint());
		
		//Initialisation de Distance2 = Somme des diagonales à créer
		double sommeDistance2 = Distances.distance(lineString1.startPoint(), lineString2.endPoint()) + 
					Distances.distance(lineString1.endPoint(), lineString2.startPoint());
		
		//Construction du périmètre sur le JDD de référence
		Iterator<DirectPosition> itPoints=lineString1.coord().getList().iterator();
		while (itPoints.hasNext()) {
			DirectPosition pt = itPoints.next();
			perimetre.addControlPoint(pt);
		}
		
		//Construction du périmètre sur le JDD à comparer en passant par les côtés
		//On considère que la somme des côtés est inférieure à la somme des diagonales
		itPoints=lineString2.coord().getList().iterator();
		while (itPoints.hasNext()) {
			DirectPosition pt = itPoints.next();
			if (sommeDistance1<sommeDistance2){
				perimetre.addControlPoint(0,pt);
			}
			else{
				perimetre.addControlPoint(pt);
			}
		}
		
		//Bouclage du périmètre
		perimetre.addControlPoint(perimetre.startPoint());		
		//Création d'un polygone à partir du périmètre
		GM_Polygon polygone = new GM_Polygon(perimetre);
		return polygone;
	}
	/**
	 * Fusionne bout à bout un ensemble de LineStrings en une seule.
	 * <p>
	 * Pour que l'algorithme fonctionne, il faut que, dans la liste, toutes les
	 * linestrings soient connexes. L'algorithme comment par remettre les
	 * linestrings dans l'ordre et dans le même sens.
	 *
	 * @param linestringList
	 *            ensemble de LineStrings
	 * @return fusion de LineStrings
	 */
	public static GM_LineString union(
			List<GM_LineString> linestringList) {
		// Si il n'y a pas de polylignes dans la liste, arrêt de la procédure
		if (linestringList.isEmpty()) {
			return null;
		}
		GM_LineString lineStringCourante = null;
		// Si il n'y a qu'une seule polyligne dans la liste, alors on retourne
		// uniquement cette polyligne
		if (linestringList.size() == 1) {
			lineStringCourante = linestringList.get(0);
			return lineStringCourante;
		}
		// Sinon, il faud comparer toutes les polylignes de la liste (toutes les
		// combinaisons point de départ, point d'arrivée) et les fusionner au
		// fur et à mesure en les orientant.
		// Initialisation de la polyligne de référence.
		lineStringCourante = linestringList.remove(0);
		for (int i = 0; i < linestringList.size(); i++) {
			GM_LineString lineStringSuivante = linestringList.get(i);
			GM_LineString lineStringCopie = new GM_LineString(lineStringSuivante
					.getControlPoint());
			DirectPositionList pointsLiaison = new DirectPositionList();
			// Si le point de départ de la polyligne courante = point de départ
			// de la polyligne suivante
			if (lineStringCourante.startPoint().equals2D(
					lineStringSuivante.startPoint(), 0)) {
				pointsLiaison.addAll(((GM_LineString) lineStringCourante
						.reverse()).getControlPoint());
				lineStringCopie
						.removeControlPoint(lineStringCopie.startPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point d'arriv�e de la polyligne courante = point d'arriv�e
			// de la polyligne suivante
			else if (lineStringCourante.endPoint().equals2D(
					lineStringSuivante.endPoint(), 0)) {
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCopie.removeControlPoint(lineStringCopie.endPoint());
				pointsLiaison
						.addAll(((GM_LineString) lineStringCopie.reverse())
								.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point d'arriv�e de la polyligne courante = point de d�part
			// de la polyligne suivante
			else if (lineStringCourante.endPoint().equals2D(
					lineStringSuivante.startPoint(), 0)) {
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCopie
						.removeControlPoint(lineStringCopie.startPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}

			// Si le point de départ de la polyligne courant = point d'arrivée
			// de la polyligne suivante
			else if (lineStringCourante.startPoint().equals2D(
					lineStringSuivante.endPoint(), 0)) {
				lineStringCopie.removeControlPoint(lineStringCopie.endPoint());
				pointsLiaison.addAll(lineStringCopie.getControlPoint());
				pointsLiaison.addAll(lineStringCourante.getControlPoint());
				lineStringCourante = new GM_LineString(pointsLiaison);
				pointsLiaison.removeAll(pointsLiaison);
				linestringList.remove(i);
				i = -1;
			}
		}
		return lineStringCourante;
	}
}
