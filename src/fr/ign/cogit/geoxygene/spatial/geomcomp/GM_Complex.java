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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** NON TESTE.
 * Un complexe est un set de primitives géométriques dont les intérieurs sont disjoints.
 * De plus, si une primitive est dans un complexe, alors il existe un set de primitives dans le complexe dont l'union en forme la frontière.
 * <P> ATTENTION : pour le moment, la contrainte qui impose que si un élément est dans un complexe, sa frontière est dans
 * un complexe, n'est pas implémentée.
 * <P> A FAIRE AUSSI : mettre des itérateurs sur les listes
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

public class GM_Complex extends GM_Object {


	/** Set de primitives constituant self. */
	protected Set<GM_Object> element = new HashSet<GM_Object>();

	/** Renvoie le set des primitives */
	public Set<GM_Object> getElement() {return this.element;}

	/** Ajoute une primitive (ajoute aussi un complexe à la primitive)  */
	public void addElement (GM_Primitive value) {
		this.element.add(value);
		value.complex.add(this);
	}

	/** Efface la primitive passée en paramètre (efface aussi le complexe de la primitive)  */
	public void removeElement (GM_Primitive value) {
		this.element.remove(value);
		value.complex.remove(this);
	}

	/** Nombre de primitives constituant self. */
	public int sizeElement () {return this.element.size();}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attribut "subComplex" et méthodes pour le traiter ///////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Met a jour automatiquement : le super-complexe, et la liste des elements.

	/** Les sous-complexes constituant self. */
	protected Set<GM_Complex> subComplex = new HashSet<GM_Complex>();

	/** Renvoie la liste des sous-complexes */
	public Set<GM_Complex> getSubComplex() {return this.subComplex;}

	/** Ajoute un sous-complexe en fin de liste.  */
	public void addSubComplex (GM_Complex value) {
		this.subComplex.add(value);
		value.superComplex.add(this);
		this.element.add(value);
		value.element.add(this);
	}

	/** Efface le (ou les) sous-complexes passé en paramètre. */
	public void removeSubComplex (GM_Complex value) {
		this.subComplex.remove(value);
		value.superComplex.remove(this);
		this.element.remove(value);
		value.element.remove(this);

	}

	/** Nombre de sous-complexes constituant self. */
	public int sizeSubComplex () {return this.subComplex.size();}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attribut "superComplex" et méthodes pour le traiter /////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** Les super-complexes constituant self. */
	protected Set<GM_Complex> superComplex = new HashSet<GM_Complex>();

	/** Renvoie la liste des super-complexes */
	public Set<GM_Complex> getSuperComplex() {return this.superComplex;}

	/** Ajoute un super-complexe en fin de liste.  */
	public void addSuperComplex (GM_Complex value) {this.superComplex.add(value);}

	/** Efface le (ou les) super-complexes passé en paramètre. */
	public void removeSuperComplex (GM_Complex value) {this.superComplex.remove(value);}

	/** Nombre de super-complexes constituant self. */
	public int sizeSuperComplex () {return this.superComplex.size();}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Méthodes ////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////

	/** Un complexe est maximal s'il n'est le subcomplexe de personne. */
	public boolean isMaximal()  {
		if (this.sizeSuperComplex() == 0) return true;
		return false;
	}

	/** NON IMPLEMENTE POUR UN COMPLEXE QUELCONQUE.
	 * Collection de GM_Object représentant la frontière de self.
	 * Cette collection d'objets a une structure de GM_ComplexBoundary,
	 * qui est un GM_Complex de dimension inférieure à 1 à celle de l'objet initial.
	 */
	//   public GM_ComplexBoundary boundary() {
	//       return null;
	//   }



	/** Marche pas. Renvoie null.  */
	@Override
	public DirectPositionList coord() {
		System.out.println("coord() : marche pas pour un complexe. Renvoie null"); //$NON-NLS-1$
		return null;
	}

}
