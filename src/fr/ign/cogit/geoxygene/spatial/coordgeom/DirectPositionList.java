/**
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Liste de DirectPosition.
 * On reprend les methodes standards de <tt> java.util.List </tt> en les typant.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 * 19.02.2007 : ajout de la methode removeAll
 *
 */

///////// Dans la norme ISO, cette classe s'appelle GM_PointArray


public class DirectPositionList implements Collection<DirectPosition> {

	/** La liste des DirectPosition. */
	protected List<DirectPosition> list = new ArrayList<DirectPosition>();

	/** Affecte une liste à this. Attention, ne verifie pas que la liste
	 * passee en parametre ne contient que des DirectPosition. Ne clone pas la liste
	 * passee en parametre mais fait une reference. */
	public void setList(List <DirectPosition>theList) {this.list = theList; }

	/** Renvoie la liste de DirectPosition. */
	public List<DirectPosition> getList () {return this.list;}

	/** Renvoie l'element de rang i */
	public DirectPosition get (int i) {return this.list.get(i);}

	/** Affecte un element au i-eme rang de la liste */
	public void set (int i, DirectPosition value) {this.list.set(i, value);}

	/** Ajoute un element en fin de liste */
	@Override
	public boolean add (DirectPosition value) {return this.list.add(value);}

	/** Ajoute un element au i-eme rang de la liste */
	public void add (int i, DirectPosition value) {this.list.add(i, value);}

	/** Ajoute une liste deDirectPosititon en fin de liste */
	public boolean addAll (DirectPositionList theList) {return this.list.addAll(theList.getList());}

	/** Efface de la liste le (ou les) elements passe en parametre */
	public void remove (DirectPosition value)  {this.list.remove(value);}

	/** Efface le i-eme element de la liste */
	public void remove (int i)  {this.list.remove(i);}

	/** Retire une liste de DirectPosititon */
	public void removeAll (DirectPositionList theList) {this.list.removeAll(theList.getList());}

	/** Efface toute la liste */
	@Override
	public void clear() {this.list.clear();}

	/** Renvoie le nombre de elements */
	@Override
	public int size () {return this.list.size();}

	/** Constructeur par defaut. */
	public DirectPositionList(){}

	/** Constructeur à partir d'une liste de DirectPosition.  Attention, ne verifie pas que la liste
	 * passee en parametre ne contient que des DirectPosition. Ne clone pas la liste
	 * passee en parametre mais fait une reference. */
	public DirectPositionList(List<DirectPosition> theList) {setList(theList);}

	/** Clone this */
	@Override
	public Object clone() {
		DirectPositionList dpl = new DirectPositionList();
		for (DirectPosition p : this.list) dpl.add( (DirectPosition)p.clone() );
		return dpl;
	}

	/** Renvoie un tableau de double de la forme [X Y X Y ... X Y] */
	public double[] toArray2D() {
		double[] array = new double[this.list.size()*2];
		int i = 0;
		for (DirectPosition p : this) {
			array[i++] = p.getX();
			array[i++] = p.getY();
		}
		return array;
	}

	/** Renvoie un tableau de double de la forme [X Y Z X Y Z ... X Y Z] */
	public double[] toArray3D() {
		double[] array = new double[this.list.size()*3];
		int i = 0;
		for (DirectPosition p : this) {
			array[i++] = p.getX();
			array[i++] = p.getY();
			array[i++] = p.getZ();
		}
		return array;
	}

	/** Renvoie un tableau de double contenant tous les X des DirectPosition de la liste. */
	public double[] toArrayX() {
		double[] array = new double[this.list.size()];
		int i = 0;
		for (DirectPosition p : this) array[i++] = p.getX();
		return array;
	}

	/** Renvoie un tableau de double contenant tous les Y des DirectPosition de la liste. */
	public double[] toArrayY() {
		double[] array = new double[this.list.size()];
		int i = 0;
		for (DirectPosition p : this) array[i++] = p.getY();
		return array;
	}

	/** Renvoie un tableau de double contenant tous les Z des DirectPosition de la liste. */
	public double[] toArrayZ() {
		double[] array = new double[this.list.size()];
		int i = 0;
		for (DirectPosition p : this) array[i++] = p.getZ();
		return array;
	}


	/** Affiche les coordonnees des point (2D et 3D). */
	@Override
	public String toString () {
		String result = new String();
		if (size() == 0) {
			result = "DirectPositionList : liste vide"; //$NON-NLS-1$
			return result;
		}
		for (int i=0; i<size(); i++)
			result = result+get(i).toString()+"\n"; //$NON-NLS-1$
		return result.substring(0,result.length()-1);       // on enleve le dernier "\n"
	}

	@Override
	public Iterator<DirectPosition> iterator() {return this.list.iterator();}


	@Override
	public boolean addAll(Collection<? extends DirectPosition> c) {return this.addAll((DirectPositionList)c);}


	@Override
	public boolean contains(Object o) {return this.list.contains(o);}


	@Override
	public boolean containsAll(Collection<?> c) {return this.list.containsAll(c);}


	@Override
	public boolean isEmpty() {return this.list.isEmpty();}


	@Override
	public boolean remove(Object o) {return this.list.remove(o);}


	@Override
	public boolean removeAll(Collection<?> c) {return this.list.removeAll(c);}


	@Override
	public boolean retainAll(Collection<?> c) {return this.list.retainAll(c);}


	@Override
	public Object[] toArray() {return this.list.toArray();}


	@Override
	public <T> T[] toArray(T[] a) {return this.list.toArray(a);}

	/**
	 * permuter les elements i et j
	 * @param i
	 * @param j
	 */
	public void permuter(int i, int j){
		if(i==j) return;
		if( i >= size() || j >= size() ) {
			System.out.println("Erreur dans permutation: index "+i+" ou "+j+" plus grand que "+size());  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		}
		DirectPosition dpi = get(i),  dpj = get(j);
		remove(i); add(i, dpj);
		remove(j); add(j, dpi);
	}

	/**
	 * inverse l'ordre des directposition de la liste
	 */
	public void inverseOrdre(){
		int nb = size();
		for(int i=0; i<nb/2; i++) permuter(i, nb-1-i);
		
	}

}
