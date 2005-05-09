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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** 
 *  Liste de DirectPosition.
 * On reprend les méthodes standards de <tt> java.util.List </tt> en les typant.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

///////// Dans la norme ISO, cette classe s'appelle GM_PointArray


public class DirectPositionList {
        
    /** La liste des DirectPosition. */
    protected List list = new ArrayList();
    
    /** Itérateur sur la liste des DirectPosition. */
    private Iterator iList;
    
    /** Affecte une liste à this. Attention, ne vérifie pas que la liste
     * passée en paramètre ne contient que des DirectPosition. Ne clone pas la liste 
     * passée en paramètre mais fait une référence. */
    public void setList(List theList) {list = theList; }
    
    /** Renvoie la liste de DirectPosition. */
    public List getList () {return this.list;}
    
    /** Renvoie l'élément de rang i */
    public DirectPosition get (int i) {return (DirectPosition)this.list.get(i);}
    
    /** Affecte un élément au i-ème rang de la liste */
    public void set (int i, DirectPosition value) {
        this.list.set(i, value);
    }
    
    /** Ajoute un élément en fin de liste */
    public void add (DirectPosition value) {
        this.list.add(value);
    }
    
    /** Ajoute un élément au i-ème rang de la liste */
    public void add (int i, DirectPosition value) {
        this.list.add(i, value);
    }
    
    /** Ajoute une liste deDirectPosititon en fin de liste */
    public void addAll (DirectPositionList theList) {
        this.list.addAll(theList.getList());
    }
            
    /** Efface de la liste le (ou les) éléments passé en paramètre */
    public void remove (DirectPosition value)  {
        this.list.remove(value);
    }    
    
    /** Efface le i-ème élément de la liste */
    public void remove (int i)  {
        this.list.remove(i);
    }
    
    /** Efface toute la liste */
    public void clear() {
        this.list.clear();
    }
    
    /** Initialise l'itérateur de la liste. */
    public void initIterator() {
        iList = this.list.iterator();
    }
    
    /** Renvoie true s'il reste des éléments avec l'itérateur, false sinon. */
    public boolean hasNext() {
        if (iList.hasNext()) return true;
        else return false;
    }
    
    /** Renvoie le prochain element avec l'iterateur. */
    public DirectPosition next() {
        return (DirectPosition)iList.next();
    }
    
    /** Renvoie le nombre de éléments */
    public int size () {return this.list.size();}
    
    /** Constructeur par défaut. */
    public DirectPositionList(){
    }
    
    /** Constructeur à partir d'une liste de DirectPosition.  Attention, ne vérifie pas que la liste
     * passée en paramètre ne contient que des DirectPosition. Ne clone pas la liste 
     * passée en paramètre mais fait une référence. */
    public DirectPositionList(List theList) {
        setList(theList);
    }
    
    /** Clone this, atester. */
    public Object clone() {
        DirectPositionList result = new DirectPositionList();
        this.initIterator();
        while (this.hasNext()) {
            DirectPosition p = this.next();
            DirectPosition pp = (DirectPosition)p.clone();
            result.add(pp);
        }
        return result;
    }

    /** Renvoie un tableau de double de la forme [X Y X Y ... X Y] */
    public double[] toArray2D() {
        double[] array = new double[list.size()*2];
        initIterator();
        int i = 0;
        while (hasNext()) {
            DirectPosition p = next();
            array[i] = p.getX();
            i++;
            array[i] = p.getY();
            i++;
        }
        return array;
    }
    
    /** Renvoie un tableau de double de la forme [X Y Z X Y Z ... X Y Z] */
    public double[] toArray3D() {
        double[] array = new double[list.size()*3];
        initIterator();
        int i = 0;
        while (hasNext()) {
            DirectPosition p = next();
            array[i] = p.getX();
            i++;
            array[i] = p.getY();
            i++;
            array[i] = p.getZ();
            i++;
        }
        return array;
    }
    
    /** Renvoie un tableau de double contenant tous les X des DirectPosition de la liste. */
    public double[] toArrayX() {
        double[] array = new double[list.size()];
        initIterator();
        int i = 0;
        while (hasNext()) {
            DirectPosition p = next();
            array[i] = p.getX();
            i++;
        }
        return array;        
    }    
            
    /** Renvoie un tableau de double contenant tous les Y des DirectPosition de la liste. */
    public double[] toArrayY() {
        double[] array = new double[list.size()];
        initIterator();
        int i = 0;
        while (hasNext()) {
            DirectPosition p = next();
            array[i] = p.getY();
            i++;
        }
        return array;        
    }   
    
    /** Renvoie un tableau de double contenant tous les Z des DirectPosition de la liste. */
    public double[] toArrayZ() {
        double[] array = new double[list.size()];
        initIterator();
        int i = 0;
        while (hasNext()) {
            DirectPosition p = next();
            array[i] = p.getZ();
            i++;
        }
        return array;        
    } 

      
    /** Affiche les coordonnées des point (2D et 3D). */
    public String toString () {
        String result = new String();
        if (size() == 0) {
            result = "DirectPositionList : liste vide";
            return result;
        } 
        for (int i=0; i<size(); i++)
            result = result+get(i).toString()+"\n";
        return result.substring(0,result.length()-1);       // on enleve le dernier "\n"
    }    
    
}
