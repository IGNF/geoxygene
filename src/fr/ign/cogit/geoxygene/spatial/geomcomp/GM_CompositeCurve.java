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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_CurveBoundary;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

/**
 * Complexe ayant toutes les propriétés géométriques d'une courbe.
 * C'est une liste de courbes orientées (GM_OrientableCurve)
 * de telle manière que le noeud final d'une courbe correspond au noeud initial
 * de la courbe suivante dans la liste.
 * Hérite de GM_OrientableCurve, mais le lien n'apparaît pas explicitement
 * (problème de double héritage en java). Les méthodes et attributs ont été reportés.
 *
 * <P> ATTENTION : normalement, il faudrait remplir le set "element"
 * (contrainte : toutes les primitives du generateur
 * sont dans le complexe). Ceci n'est pas implémenté pour le moment.
 * <P> A FAIRE AUSSI : iterateur sur "generator"
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */
public class GM_CompositeCurve extends GM_Composite {
    ////////////////////////////////////////////////////////////////////////
    // Attribut "generator" et méthodes pour le traiter ////////////////////
    ////////////////////////////////////////////////////////////////////////
    /** Les GM_OrientableCurve constituant self. */
    protected List<GM_OrientableCurve> generator;

    /** Renvoie la liste des GM_OrientableCurve. */
    public List<GM_OrientableCurve> getGenerator() { return this.generator; }

    /** Renvoie la GM_OrientableCurve de rang i. */
    public GM_OrientableCurve getGenerator (int i) {
        return this.generator.get(i);
    }

    /**
     * Affecte une GM_OrientableCurve au rang i.
     * Attention : aucun contrôle de continuité n'est effectué.
     */
    public void setGenerator (int i, GM_OrientableCurve value) {
        this.generator.set(i, value);
    }

    /**
     * Ajoute une GM_OrientableCurve en fin de liste.
     * Attention : aucun contrôle de continuité n'est effectué.
     */
    public void addGenerator (GM_OrientableCurve value) {
        this.generator.add(value);
    }

    /**
     * Ajoute une GM_OrientableCurve en fin de liste avec un contrôle de
     * continuité avec la tolérance passée en paramètre.
     * Envoie une exception en cas de problème.
     */
    public void addGenerator (GM_OrientableCurve value, double tolerance)
    throws Exception {
        DirectPosition pt1;
        DirectPosition pt2;
        if (this.generator.size() > 0) {
            GM_OrientableCurve laDerniereCourbe =
                this.getGenerator(this.generator.size() - 1);
            pt1 = laDerniereCourbe.boundary().getEndPoint().getPosition();
            pt2 = value.boundary().getStartPoint().getPosition();
            if (pt1.equals(pt2, tolerance)) {
                this.generator.add(value);
            } else {
                throw new Exception(
                "Rupture de chaînage avec la courbe passée en paramètre."); //$NON-NLS-1$
            }
        } else {
            this.generator.add(value);
        }
    }

    /**
     * Ajoute une GM_OrientableCurve en fin de liste avec un contrôle de
     * continuité avec la tolérance passée en paramètre.
     * Eventuellement change le sens d'orientation de la courbe pour assurer
     * la continuite.
     * Envoie une exception en cas de problème.
     */
    public void addGeneratorTry (GM_OrientableCurve value, double tolerance)
    throws Exception {
        try {
            this.addGenerator(value, tolerance);
        } catch (Exception e1) {
            try {
                this.addGenerator(value.getNegative(), tolerance);
            } catch (Exception e2) {
                throw new Exception(
                "Rupture de chaînage avec la courbe passée en paramètre(après avoir essayé les 2 orientations)"); //$NON-NLS-1$
            }
        }
    }

    /**
     * Ajoute une GM_OrientableCurve au rang i.
     * Attention : aucun contrôle de continuité n'est effectué.
     */
    public void addGenerator (int i, GM_OrientableCurve value) {
        this.generator.add(i, value);
    }

    /**
     * Efface la (ou les) GM_OrientableCurve passé en paramètre.
     * Attention : aucun contrôle de continuité n'est effectué.
     */
    public void removeGenerator (GM_OrientableCurve value) throws Exception {
        if (this.generator.size() == 1) {
            throw new Exception (
            "Il n'y a qu'un objet dans l'association."); //$NON-NLS-1$
        }
        this.generator.remove(value);
    }

    /**
     * Efface la GM_OrientableCurve de rang i.
     * Attention : aucun contrôle de continuité n'est effectué.
     */
    public void removeGenerator (int i) throws Exception {
        if (this.generator.size() == 1) {
            throw new Exception (
            "Il n'y a qu'un objet dans l'association."); //$NON-NLS-1$
        }
        this.generator.remove(i);
    }

    /**
     * Nombre de GM_OrientableCurve constituant self.
     */
    public int sizeGenerator () { return this.generator.size(); }

    ////////////////////////////////////////////////////////////////////////
    // Constructeurs ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // les constructeurs sont calques sur ceux de GM_Curve
    /**
     * Constructeur par défaut.
     */
    public GM_CompositeCurve() {
        this.generator = new ArrayList<GM_OrientableCurve>();
        this.primitive = new GM_Curve();
        this.proxy[0] = this.primitive;
        GM_OrientableCurve proxy1 = new GM_OrientableCurve();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this.primitive;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = new GM_Curve(this.primitive);
        this.proxy[1] = proxy1;
    }

    /**
     * Constructeur à partir d'une et d'une seule GM_OrientableCurve.
     *  L'orientation vaut +1.
     */
    public GM_CompositeCurve(GM_OrientableCurve oCurve) {
        this.generator = new ArrayList<GM_OrientableCurve>();
        this.generator.add(oCurve);
        this.primitive = new GM_Curve();
        this.simplifyPrimitive();
        this.proxy[0] = this.primitive;
        GM_OrientableCurve proxy1 = new GM_OrientableCurve();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this.primitive;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = new GM_Curve(this.primitive);
        this.proxy[1] = proxy1;
    }

    ////////////////////////////////////////////////////////////////////////
    // Attributs et méthodes héritées de GM_OrientableCurve ////////////////
    ////////////////////////////////////////////////////////////////////////
    // On simule l'heritage du modele en reportant les attributs et methodes
    // de GM_OrientableCurve
    // On n'a pas repris l'attribut "orientation" qui ne sert a rien ici.

    /**
     * Primitive. Elle doit etre recalculée à chaque modification de self :
     * fait dans getPrimitive().
     */
    protected GM_Curve primitive;

    /**
     * Renvoie la primitive de self.
     * Le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
     */
    public GM_Curve getPrimitive ()  {
        this.simplifyPrimitive();
        return this.primitive;
    }

    /**
     * Attribut stockant les primitives orientées de cette primitive.
     * Proxy[0] est celle orientée positivement.
     * Proxy[1] est celle orientée négativement.
     * On accède aux primitives orientées par getPositive() et getNegative().
     */
    protected GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];

    /**
     * Renvoie la primitive orientée positivement.
     */
    public GM_OrientableCurve getPositive() {
        this.simplifyPrimitive();
        return this.primitive;       // equivaut a return this.proxy[0]
    }

    /**
     * Renvoie la primitive orientée négativement.
     */
    public GM_OrientableCurve getNegative()  {
        this.simplifyPrimitive();
        return this.primitive.getNegative();
    }

    /**
     * Redéfinition de l'opérateur "boundary" sur GM_OrientableCurve.
     * Renvoie une GM_CurveBoundary, c'est-à-dire deux GM_Point.
     */
    public GM_CurveBoundary boundary()  {
        this.simplifyPrimitive();
        return this.primitive.boundary();
    }

    ////////////////////////////////////////////////////////////////////////
    // Méthodes "validate" /////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // cette méthode n'est pas dans la norme.
    /**
     * Vérifie le chaînage des composants. Renvoie TRUE s'ils sont chaînés,
     * FALSE sinon.
     */
    public boolean validate(double tolerance) {
        for (int i = 0; i<this.generator.size() - 1; i++) {
            GM_OrientableCurve oCurve1 = this.generator.get(i);
            GM_Curve prim1 = oCurve1.getPrimitive();
            GM_OrientableCurve oCurve2 = this.generator.get(i + 1);
            GM_Curve prim2 = oCurve2.getPrimitive();
            DirectPosition pt1 = prim1.endPoint();
            DirectPosition pt2 = prim2.startPoint();
            if (!pt1.equals(pt2, tolerance)) { return false; }
        }
        return true;
    }

    /**
     * Renvoie les coordonnees de la primitive.
     */
    @Override
    public DirectPositionList coord() { return getPrimitive().coord(); }

    ////////////////////////////////////////////////////////////////////////
    // Méthodes privées pour usage interne /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /**
     * Calcule la primitive se self.
     */
    private void simplifyPrimitive()  {
        int n = this.generator.size();
        if (n > 0) {
            // vidage de la primitive
            synchronized (this.primitive.getSegment()) {
                this.primitive.clearSegments();
                for (GM_OrientableCurve oCurve : this.generator) {
                    GM_Curve thePrimitive = oCurve.getPrimitive();
                    synchronized (thePrimitive.getSegment()) {
                        for (GM_CurveSegment theSegment :
                            thePrimitive.getSegment()) {
                            this.primitive.addSegment(theSegment);
                        }
                    }
                }
            }
        }
    }
}
