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

package fr.ign.cogit.geoxygene.util.index;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Interface pour un index spatial.
 * Les selections se font au sens large : tout objet intersectant la zone d'extraction est renvoye.
 * 
 * @author Thierry Badard, Arnaud Braun & Sébastien Mustière
 * @version 1.0
 */

public interface SpatialIndex<Feature extends FT_Feature> {

	/** Renvoie les paramètres de l'index.
	 * Ce que contient exactement cette liste peut être différent pour chaque type d'index.
	 * 
	 * Pour un dallage: renvoie une ArrayList de 4 éléments
	 * - 1er  élément : Class égal à Dallage.class
	 * - 2ème élément : Boolean indiquant si l'index est en mode MAJ automatique ou non
	 * - 3ème élément : GM_Envelope décrivant les limites de la zone couverte
	 * - 4ème élément : Integer exprimant le nombre de cases en X et Y.
	 * 
	 */
	public List<Object> getParametres();

	/** Indique si l'on a demande une mise a jour automatique. */
	public boolean hasAutomaticUpdate() ;

	/** Demande une mise a jour automatique.
	 * NB: Cette méthode ne fait pas les éventuelles MAJ qui
	 * auriant ete faites alors que le mode MAJ automatique n'était
	 * pas activé.
	 */
	public void setAutomaticUpdate(boolean auto) ;

	/** Met a jour l'index avec le FT_Feature passé en paramètre.
	 * <p>
	 * <b>ATTENTION : si le nouveau feature est en dehors des dalles existantes, il ne sera jamais insûré dans l'index !</b>
	 * @param value FT_Feature provocant la mise à jour de l'index
	 * @param cas type de modification de l'index :
	 * <ul>
	 * <li> +1 : on ajoute le feature.
	 * <li> -1 : on enleve le feature.
	 * <li> 0 : on modifie le feature.
	 * </ul>
	 */
	public void update (Feature value, int cas);

	/** Selection dans le carre dont P est le centre, de cote D.
	 * NB: D peut être nul. */
	public Collection<Feature> select (DirectPosition P, double D) ;

	/** Selection a l'aide d'un rectangle. */
	public Collection<Feature> select (GM_Envelope env) ;

	/** Selection des objets qui intersectent un objet geometrique quelconque. */
	public Collection<Feature> select (GM_Object geometry) ;

	/** Selection des objets qui croisent ou intersectent un objet geometrique quelconque.
	 * 
	 * @param strictlyCrosses
	 * Si c'est TRUE : ne retient que les  objets qui croisent (CROSS au sens JTS)
	 * Si c'est FALSE : ne retient que les  objets qui intersectent (INTERSECT au sens JTS)
	 * Exemple : si 1 ligne touche "geometry" juste sur une extrémité,
	 * alors avec TRUE cela ne renvoie pas la ligne, avec FALSE cela la renvoie
	 */
	public Collection<Feature> select(GM_Object geometry, boolean strictlyCrosses) ;


	/** Selection a l'aide d'un objet geometrique quelconque et d'une distance.
	 * NB: D peut être nul*/
	public Collection<Feature> select (GM_Object geometry, double distance) ;

}
