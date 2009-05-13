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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_Complex;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Classe mère abstraite pour les primitives géométriques (point, ligne, surface, solide).
 * Son but est définir l'opération de base "boundary()" qui lie les primitives de différentes dimensions entre elles.
 * Cette opération est redéfinie dans les sous-classes concrètes pour assurer un bon typage.
 * Une primitive géométrique ne peut pas être décomposée en autres primitives,
 * même si elle découpée en morceaux de courbes (curve segment) ou en morceaux de surface (surface patch) :
 * un curve segment et un surface patch ne peuvent pas exister en dehors du contexte d'une primitive.
 * GM_Complex et GM_Primitive partagent les mêmes propriétés, sauf qu'un complexe est fermé par l'opération "boundary".
 * Par exemple pour une CompositeCurve, GM_Primitive::contains(endPoint) retourne FALSE,
 * alors que GM_Complex::contains(endPoint) retourne TRUE.
 * En tant que GM_Object ces 2 objets seront égaux.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


abstract public class GM_Primitive extends GM_Object {


	// The "Interior to" association is not implemented

	// Le constructeur a partir d'une GM_Envelope est defini dans GM_Poygon


	/** Association avec les GM_Complex, instanciée du coté du complexe. */
	public Set<GM_Complex> complex = new HashSet<GM_Complex>();

	/** Renvoie le set des complexes auxquels appartient this */
	public Set<GM_Complex> getComplex() {return complex;}

	/** Nombre de complexes auxquels appartient this  */
	public int sizeComplex () {return this.complex.size();}

}
