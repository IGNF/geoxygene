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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Complexe contenant un et un seul GM_Point. Cette classe ne sert pas a grand chose mais elle a été mise pour homogénéiser
 * avec les GM_Composite des autres types de primitives.
 * Hérite de GM_Point, mais le lien n'apparaît pas explicitement (problème de double héritage en java). Les méthodes et attributs ont été reportés.
 * <P> Utilisation : un GM_CompositePoint se construit exclusivement à partir d'un GM_Point.
 *
 * <P> ATTENTION : normalement, il faudrait remplir le set "element" (contrainte : toutes les primitives du generateur
 * sont dans le complexe). Ceci n'est pas implémenté pour le moment.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class GM_CompositePoint extends GM_Composite {

	/////////////////////////////////////////////////////////////////////////////////////////
	// Attribut et m�thodes propres � GM_CompositePoint /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/** Le GM_Point constituant self. C�est une r�f�rence (un nouvel objet n'est pas construit).*/
	protected GM_Point generator;

	/** Renvoie le GM_Point constituant self. */
	public GM_Point getGenerator () {return this.generator;}

	/** Affecte le GM_Point constituant self. */
	public void setGenerator (GM_Point value) {
		this.generator = value;
		this.position = new DirectPosition(value.getPosition().getCoordinate());
	}

	/** Renvoie 1 si un GM_Point a été affecté, 0 sinon. */
	public int sizeGenerator () {
		if ( this.generator == null ) return 0;
		return 1;
	}




	/////////////////////////////////////////////////////////////////////////////////////////
	// Attribut et méthodes héritées de GM_Point (héritage simulé) //////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/** DirectPosition du point (DirectPosition étant la classe stockant les coordonnées). */
	protected DirectPosition position;

	/** Renvoie le DirectPosition du point. */
	public DirectPosition getPosition () {
		this.position = this.generator.getPosition();
		return this.position;
	}

	// la methode setPosition n'existe pas, pour obliger a passer par le generator pour affecter un point.

	// Dans la norme, on passe un GM_Position en parametre. Je prefere faire 2 methodes pour simplifier,
	// l'une avec un GM_Point en parametre, l'autre avec un DirectPosition.
	/** NON IMPLEMENTE (renvoie null).
	 * Direction entre self et le GM_Point passé en paramètre, en suivant une courbe qui dépend du système de coordonnées (courbe géodésique par exemple).
	 * Le bearing retourné est un vecteur.
	 */
	/*public Bearing bearing(GM_Point toPoint) {
        return null;
    }*/

	/** NON IMPLEMENTE (renvoie null).
	 * Direction entre self et le DirectPosition passé en paramètre, en suivant une courbe qui dépend du système de coordonnées (courbe géodésique par exemple).
	 * Le bearing retourné est un vecteur.
	 */
	/*public Bearing bearing(DirectPosition toPoint) {
        return null;
    }*/

	/////////////////////////////////////////////////////////////////////////////////////////
	// Constructeurs ////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/** Constructeur par défaut. */
	public GM_CompositePoint() {
		this.position = new DirectPosition();
	}

	/** Constructeur à partir d'un GM_Point. */
	public GM_CompositePoint(GM_Point thePoint) {
		this.generator = thePoint;
		this.position = new DirectPosition(thePoint.getPosition().getCoordinate());
	}

}
