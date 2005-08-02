/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for 
 * the development and deployment of geographic (GIS) applications. It is a open source 
 * contribution of the COGIT laboratory at the Institut Géographique National (the French 
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net 
 *  
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library (see file LICENSE if present); if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SurfaceBoundary;

/**
  * Polygone : morceau de surface plan (les arêtes constituant la frontière sont coplanaires).
  * L'attribut interpolation vaut "planar" par défaut.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class GM_Polygon extends GM_SurfacePatch {
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    //// modele original ISO abandonne pour simplification ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Stocke la frontière constituant le polygone. */
    //protected GM_SurfaceBoundary boundary;
    
    /** Renvoie la frontière. */
    //public GM_SurfaceBoundary getBoundary () {return this.boundary;}

    /** Optionnel. */
    //protected GM_Surface spanningSurface;
    
    /** Renvoie la spanning surface. */
    //public GM_Surface getSpanningSurface () {return this.spanningSurface;}


    
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    //// frontiere du polygone ///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    
     /** Anneau extérieur. */
    protected GM_Ring exterior;
    
    /** Renvoie l'anneau extérieur */
    public GM_Ring getExterior () { return this.exterior; }
    
    /** Affecte une valeur à l'anneau extérieur */ 
    protected void setExterior (GM_Ring value) { this.exterior = value; }
    
    /** Renvoie 1 si l'anneau extérieur est affecté, 0 sinon. 
     * Il paraît qu'il existe des cas où on peut avoir une surface avec que des frontières intérieures. */
    public int sizeExterior () {
        if ( this.exterior == null ) return 0;
        else return 1;
    }

    /** Anneau(x) intérieur(s) en cas de trou(s) : liste de GM_Ring */
    protected List interior = new ArrayList();
    
    /** Renvoie la liste des anneaux intérieurs */
    public List getInterior () {return this.interior;}
    
    /** Renvoie l'anneau intérieur de rang i */
    public GM_Ring getInterior (int i) {return (GM_Ring)this.interior.get(i);}
    
    /** Affecte un GM_Ring au rang i */
    public void setInterior (int i, GM_Ring value) {this.interior.set(i, value);}
    
    /** Ajoute un GM_Ring en fin de liste */
    public void addInterior (GM_Ring value) {this.interior.add(value);}
    
    /** Ajoute un GM_ring au rang i */
    public void addInterior (int i, GM_Ring value) {this.interior.add(i, value);}
    
    /** Efface le (ou les) GM_Ring passé en paramètre */
    public void removeInterior (GM_Ring value)  {this.interior.remove(value);}
    
    /** Efface le GM_Ring de rang i */
    public void removeInterior (int i)  {this.interior.remove(i);}
    
    /** Nombre d'anneaux intérieurs */
    public int sizeInterior () {return this.interior.size();}
    
    
       
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// Constructeurs /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Constructeur par défaut */
    public GM_Polygon() {
        super();
        this.patch.add(this);
        interpolation = "planar";
    }
    
    
    /** NON IMPLEMENTE.
      * Constructeur à partir d'une frontière et d'une surface. */
    public GM_Polygon(GM_SurfaceBoundary boundary, GM_Surface spanSurf) {
        super();
        this.patch.add(this);
        interpolation = "planar";
    }

    
    /** Constructeur à partir d'une frontière. */
    public GM_Polygon(GM_SurfaceBoundary bdy) {
        super();
        this.patch.add(this);
        interpolation = "planar";
        this.exterior = bdy.getExterior();
        this.interior = bdy.getInterior();
    }
    
    
    /** Constructeur à partir d'une GM_LineString fermée sans vérifier la fermeture.
     * ATTENTION : ne vérifie pas la fermeture. */
    public GM_Polygon(GM_LineString ls) {
        super();
        this.patch.add(this);
        interpolation = "planar";
        GM_Ring ring = new GM_Ring(ls);
        this.exterior = ring;
    }
    
    
    /** Constructeur à partir d'une GM_LineString fermée en vérifiant la fermeture.
     * Vérifie la fermeture (d'où le paramètre tolérance), sinon exception. */
    public GM_Polygon(GM_LineString ls, double tolerance) throws Exception {
        super();
        this.patch.add(this);
        interpolation = "planar";
        try {
            GM_Ring ring = new GM_Ring(ls,tolerance);
            this.exterior = ring;
        } catch (Exception e) {
                throw new Exception("Tentative de créer un polygone avec une LineString non fermée.");
        }
    }
    
    
    /** Constructeur à partir d'une GM_Curve fermée sans vérifier la fermeture.
     * ATTENTION : ne vérifie pas la fermeture. */
    public GM_Polygon(GM_Curve curve) {
        super();
        this.patch.add(this);
        interpolation = "planar";
        GM_Ring ring = new GM_Ring(curve);
        this.exterior = ring;
    }
    
    
    /** Constructeur à partir d'une GM_Curve fermée en vérifiant la fermeture.
     * Vérifie la fermeture (d'où le paramètre tolérance), sinon exception. */
    public GM_Polygon(GM_Curve curve, double tolerance) throws Exception {
        super();
        this.patch.add(this);
        interpolation = "planar";
        try {
            GM_Ring ring = new GM_Ring(curve,tolerance);
            this.exterior = ring;
        } catch (Exception e) {
                throw new Exception("Tentative de créer un polygone avec une GM_Curve non fermée.");
        }
    }
    
    
    /** Constructeur à partir d'un GM_Ring. */
    public GM_Polygon (GM_Ring ring) {
        super();
        this.patch.add(this);
        interpolation = "planar";
        this.exterior = ring;
    }
    

    /** Constructeur à partir d'une enveloppe (2D). */
    public GM_Polygon (GM_Envelope env) {
        super();
        this.patch.add(this);
        interpolation = "planar";
        GM_LineString ls = new GM_LineString();
        boolean flag3D = true;
        DirectPosition dp;
        Double D = new Double (env.getLowerCorner().getZ());
        if (D.isNaN()) flag3D = false;
        ls.getControlPoint().add(env.getLowerCorner());        
        if (flag3D) dp = new DirectPosition(env.getUpperCorner().getX(),env.getLowerCorner().getY(), 0.0);
        else dp = new DirectPosition(env.getUpperCorner().getX(),env.getLowerCorner().getY());
        ls.getControlPoint().add(dp);
        ls.getControlPoint().add(env.getUpperCorner());
        if (flag3D) dp = new DirectPosition(env.getLowerCorner().getX(),env.getUpperCorner().getY(), 0.0);
        else dp = new DirectPosition(env.getLowerCorner().getX(),env.getUpperCorner().getY());
        ls.getControlPoint().add(dp);
        ls.getControlPoint().add(env.getLowerCorner());
        GM_Ring ring = new GM_Ring(ls);
        this.exterior = ring;
    }

    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// Methode implementant une methode de GM_SurfacePatch ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Renvoie un GM_Polygon en "retournant" la frontière (inverse l'ordre du trace des points).*/
    // Implémentation d'une méthode abstraite de GM_SurfacePatch.
    public GM_SurfacePatch reverse() {
        GM_Ring oldRing = this.getExterior();
        GM_Ring newRing = new GM_Ring();
        int n = oldRing.sizeGenerator();
        for (int i=0; i<n; i++) {
            GM_OrientableCurve oriCurve = oldRing.getGenerator(n-i-1);
            if (oriCurve.getOrientation() == +1) newRing.addGenerator((GM_OrientableCurve)oriCurve.getNegative());
            else if (oriCurve.getOrientation() == -1) newRing.addGenerator((GM_OrientableCurve)oriCurve.getPositive());
        }
        GM_SurfaceBoundary newBdy = new GM_SurfaceBoundary(newRing);
        int m = this.sizeInterior();
        if (m > 0)
            for (int j=0; j<m; j++) {
                oldRing = this.getInterior(j);
                n = oldRing.sizeGenerator();
                newRing = new GM_Ring();
                for (int i=0; i<n; i++) {
                    GM_OrientableCurve oriCurve = oldRing.getGenerator(n-i-1);
                    if (oriCurve.getOrientation() == +1) newRing.addGenerator((GM_OrientableCurve)oriCurve.getNegative());
                    else if (oriCurve.getOrientation() == -1) newRing.addGenerator((GM_OrientableCurve)oriCurve.getPositive());
                }
                newBdy.addInterior(newRing);
            }
        GM_Polygon result = new GM_Polygon(newBdy);
        return result;
    }

}
