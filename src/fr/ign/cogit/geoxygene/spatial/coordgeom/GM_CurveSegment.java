/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;

/**
  * Segment homogène d'une GM_Curve. Classe mère abstraite.
  * <P> Modification de la norme : cette classe hérite de GM_Curve. Du coup on a fait sauter le lien d'implémentation de GM_GenericCurve. 
  * Un GM_CurveSegment sera une GM_Curve composée d'un et d'un seul segment qui sera lui-même. 
  * Les méthodes addSegment, removeSegment, etc... seront interdites.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

abstract public class GM_CurveSegment   extends GM_Curve
                                        /*implements GM_GenericCurve*/ {
    

    //////////////////////////////////////////////////////////////////////////////////
    // Attributs /////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /** Mécanisme d'interpolation, selon une liste de codes. Vaut "linear" par défaut.
      * <P> La liste de codes est la suivante : 
      * {linear, geodesic, circularArc3Points, circularArc2PointsWithBulge, elliptical, clothoid, 
      * conic, polynomialSpline, cubicSpline, rationalSpline}.  */
    protected String interpolation = "linear";
    
    /** Renvoie l'attribut interpolation. */
    public String getInterpolation () {return this.interpolation;}

    
    /** Type de continuité entre un segment et son prédecesseur (ignoré pour le premier segment). 
      * Pour des polylignes on aura une continuité C0.  */
    protected int numDerivativesAtStart = 0;
    
    /** Renvoie l'attribut numDerivativesAtStart. */
    public int getNumDerivativesAtStart () {return this.numDerivativesAtStart;}
    
    /** Type de continuité entre un segment et son successeur (ignoré pour le dernier segment). 
      * Pour des polylignes on aura une continuité C0.  */
    protected int numDerivativeAtEnd = 0;
    
    /** Renvoie l'attribut numDerivativeAtEnd. */
    public int getNumDerivativeAtEnd () {return this.numDerivativeAtEnd;}

    
    /** Type de continuité garantie à l'intérieur de la courbe. 
     * Pour des polylignes on aura une continuité C0. */
    protected int numDerivativeInterior = 0;
    
    /** Renvoie l'attribut numDerivativeInterior. */
    public int getNumDerivativeInterior () {return this.numDerivativeInterior;}

    
        
    
    //////////////////////////////////////////////////////////////////////////////////
    // Méthodes (abstaites, implémentée dans les sous-classes)////////////////////////
    //////////////////////////////////////////////////////////////////////////////////    
    /** Renvoie un GM_CurveSegment de sens opposé. Méthode abstraite implémentée dans les sous-classes. */
    abstract public GM_CurveSegment reverse() ;

}
