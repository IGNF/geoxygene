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

/**
 * NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait hériter GM_SurfacePatch de GM_Surface.
 *
 * <P> Définition de la norme : les classes GM_Surface et GM_SurfacePatch représentent toutes deux des géométries à deux dimensions, 
 * et partagent donc plusieurs signatures d'opération. Celles-ci sont définies dans l'interface GM_GenericSurface.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

interface GM_GenericSurface {

    /** Vecteur normal à self, au point passé en paramètre. */
//     Vecteur upNormal(DirectPosition point);

    /**Périmètre. */
     //NORME : le résultat est de type Length.
     double perimeter ();

    /** Aire. */
     // NORME : le résultat est de type Area.
     double area();
}
