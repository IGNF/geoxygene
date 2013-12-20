/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util;

/**
 * @author JeT
 *         Class containing two objects
 */
public class Pair<U, V> {

    private U u = null;
    private V v = null;

    /**
     * Default Constructor
     */
    public Pair() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Quick constructor
     * 
     * @param u
     * @param v
     */
    public Pair(U u, V v) {
        super();
        this.u = u;
        this.v = v;
    }

    /**
     * @return the u
     */
    public U getU() {
        return this.u;
    }

    /**
     * @param u
     *            the u to set
     */
    public void setU(U u) {
        this.u = u;
    }

    /**
     * @return the v
     */
    public V getV() {
        return this.v;
    }

    /**
     * @param v
     *            the v to set
     */
    public void setV(V v) {
        this.v = v;
    }

}
